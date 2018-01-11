package com.sf.biocapture.ws.tags;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.javacodegeeks.kannel.api.SMSManager;
import com.sf.biocapture.analyzer.IntrusionAnalyzer;
import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.app.JmsSender;
import com.sf.biocapture.app.KannelSMS;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.DeviceStatus;
import com.sf.biocapture.entity.DeviceType;
import com.sf.biocapture.entity.DuplicateNode;
import com.sf.biocapture.entity.EnrollmentRef;
import com.sf.biocapture.entity.KycDealer;
import com.sf.biocapture.entity.License;
import com.sf.biocapture.entity.Node;
import com.sf.biocapture.entity.NodeAssignment;
import com.sf.biocapture.entity.Setting;
import com.sf.biocapture.entity.TagHistory;
import com.sf.biocapture.entity.audit.VersionLog;
import com.sf.biocapture.entity.enums.KycPrivilege;
import com.sf.biocapture.entity.enums.SettingsEnum;
import com.sf.biocapture.entity.enums.VersionType;
import com.sf.biocapture.entity.security.KMUser;
import com.sf.biocapture.ws.HeaderIdentifier;
import javax.ws.rs.core.HttpHeaders;

import nw.commons.StopWatch;
import nw.orm.core.exception.NwormQueryException;
import org.hibernate.HibernateException;

@Stateless
public class TagsDS extends DataService {

	public final String NO_LICENCE = "NO_LICENCE";
	public final String INCOMPLETE_LICENCE = "INCOMPLETE_LICENCE";
	public final String FULL_LICENCE = "FULL_LICENCE";
        private final String KYC_SMART_CLIENT = "KYC Smart Client";
        
        //
        private static final String NAME_QUERY_STRING = "name";

	@Inject
	private KannelSMS ksms;

	@Inject
	private JmsSender appSession;

	@Inject
	private AccessDS accessDS;

	@Inject
	private BioCache cache;
        
        @Inject
        private IntrusionAnalyzer analyzer;

	private String licenseAdminMobile = appProps.getProperty("kyc-license-admin", "08128008218");

	/**
	 * Tags a kit
	 * @param cr
         * @param headers
	 * @return
	 */
    public ClientRefResponse tag(ClientRefRequest cr, HttpHeaders headers) {
        ClientRefResponse cresp = new ClientRefResponse();
        HeaderIdentifier headerIdentifier = analyzer.getIdentifier(headers);

        logger.debug("MAC ADDRESS: " + cr.getMac() + "; Tag: " + cr.getRef() + " ; admin email : [" + cr.getAdminEmail() + " ; deviceId :" + headerIdentifier.getRefDeviceId() + "]");
        try {
            String clientTaggingAllowed = accessDS.getSettingValue(SettingsEnum.ALLOW_CLIENT_TAGGING);
            if (!Boolean.parseBoolean(clientTaggingAllowed)) {
                cresp.setStatus(-2);
                cresp.setMessage("Tagging not allowed. Upgrade to the latest version.");
                return cresp;
            }
        } catch (IllegalArgumentException e) {
            logger.error("Unable able to convert setting value to a boolean.", e);
        }

        try {
            KMUser user = getUser(cr.getAdminEmail());
            if ((cr.getAdminEmail() == null) || (cr.getAdminEmail().isEmpty()) || (user == null) || !user.isActive() || !user.hasPrivilege(KycPrivilege.TAGGING)) {
                cresp.setStatus(-2);
                cresp.setMessage("A valid user email is required for tagging a kit. Please provide one and try again.");
                return cresp;
            }

            if (isEmpty(cr.getMac()) || isEmpty(cr.getRef())) {
                // invalid request received
                cresp.setStatus(-2);
                cresp.setMessage("Unspecified Machine ID. MAC Address not valid");
            } else {
                //check if kit is blacklisted
                if (kitIsBlacklisted(cr.getRef(), cr.getMac(), headerIdentifier.getRefDeviceId())) {
                    cresp.setStatus(-2);
                    cresp.setMessage("Kit is blacklisted. Please contact support");
                    return cresp;
                }

                //check if client time is incorrect
                if (cr.getClientTime() != null) {
                    if (!clientTimeIsCorrect(String.valueOf(cr.getClientTime().getTime()))) {
                        cresp.setStatus(-2);
                        cresp.setMessage("Please correct your system time. Server time is " + new SimpleDateFormat("dd MMM yyyy hh:mm:ss a").format(new Date()));
                        return cresp;
                    }
                }
                List<EnrollmentRef> refs = getKitByTagOrMac(cr.getMac(), cr.getRef()); // check for ref existence
                if (refs.isEmpty()) {
                    // ref does not exist
                    tagRetagKit(cr, null, false);
                    cresp.setStatus(0);
                    cresp.setMessage("Kit has been tagged successfully");
                } else if (refs.size() == 1) {
                    // ref exists
                    EnrollmentRef ref = refs.get(0);
                    cresp = retagKit(cr, ref, false);

                } else if (refs.size() == 2) {
                    cresp.setStatus(-2);
                    cresp.setMessage("Kit tag is already in use by another system");
                } else {
                    cresp.setStatus(-2);
                    cresp.setMessage("Multiple Entries found, please contact support");
                }

            }
        } catch (HibernateException | NwormQueryException e) {
            logger.error("Error while tagging kit.", e);
        }

        return cresp;
    }

	private boolean kitIsBlacklisted(String tag, String mac, String deviceId){
		return accessDS.checkBlacklistStatus(tag, mac,deviceId).equalsIgnoreCase("Y");
	}

	/**
	 * Compares the time in client with time in server
	 * @param clientTime
	 * @return
	 */
	public boolean clientTimeIsCorrect(String clientTime){
		boolean timeCorrect = false;
                if (clientTime == null || clientTime.isEmpty()) {
                    return timeCorrect;
                }
		try{
			Long cTime = clientTime != null ? Long.valueOf(clientTime) : 0L;
			if(cTime != 0L){
				int allowableDiff = Integer.valueOf(getSetting("ALLOWABLE_TIME_DIFF", "120")); //in secs
				long diff = Math.abs(new Timestamp(System.currentTimeMillis()).getTime() - cTime);
				logger.debug("Time difference: " + diff + "; Allowable time difference: " + (allowableDiff * 1000));
				if(diff < (allowableDiff * 1000)){
					timeCorrect = true;
				}
			}
		}catch(NumberFormatException e){
			logger.error("Error in retrieving client time:", e);
		}

		return timeCorrect;
	}

	private String getSetting(String settingName, String defaultValue){
		String settingValue = cache.getItem(settingName, String.class);
		if(settingValue == null){
			//check db settings
			Setting setting = dbService.getByCriteria(Setting.class, Restrictions.eq(NAME_QUERY_STRING, settingName).ignoreCase());
			if(setting != null && setting.getValue() != null){
				settingValue = setting.getValue().trim();
			}else{
				settingValue = defaultValue;
			}
			cache.setItem(settingName, settingValue, 60 * 60); //1 hr
		}

		return settingValue;
	}

	private String decodeLicense(String lic){
		if(!isEmpty(lic)){
			return "A";
		}
		return "";
	}

	public boolean isValidUser(KMUser kUser){
		return  (kUser != null) && (kUser.isActive());
	}

	public ClientRefResponse handleDoubleKitEntry(ClientRefRequest cr, List<EnrollmentRef> refs){
		ClientRefResponse crx = new ClientRefResponse();
		logger.debug("Scenario #3 :: Tag and MacAddress already exists in different entries " + cr.getRef() + " with id " + cr.getMac());
		EnrollmentRef ref = null;
		EnrollmentRef oldRef = null;
		for(EnrollmentRef eref : refs){
			logger.debug("Ref located : " + eref.getCode() + " ::: " + eref.getMacAddress());
			if(eref.getMacAddress() == null || "".equals(eref.getMacAddress())){
				ref = eref;
			}else{
				oldRef = eref;
			}
		}

		if(ref != null && oldRef != null){
			// Clear Mac Address information on old tag
			oldRef.setDeviceId(null);
			oldRef.setMacAddress(null);
			oldRef.setNetworkCardName("");
			oldRef.setDescription(null);
			dbService.update(oldRef);

			// Update Mac Address information on unclaimed tag
			/** Replace in new location **/
			ref.setDateInstalled(new Timestamp(new Date().getTime()));
			ref.setInstalledBy(cr.getAdminEmail());
			ref.setMacAddress(cr.getMac());
			ref.setNetworkCardName(cr.getNetCardName());
			ref.setDescription(KYC_SMART_CLIENT);
			ref.setCustom1(cr.getPatchVersion() == null ? "" : cr.getPatchVersion() + "");
			ref.setCustom3(cr.getKitType());
			dbService.update(ref);
//			appSession.queueKit(ref);
			
			tagRetagKit(cr, ref, true);
			
			// Create a license request entry if none exists, else update
			//licenseKit(cr);
			crx.setStatus(-2);
			crx.setMessage("Kit retagged successfully");
		}
		return crx;
	}

	public List<EnrollmentRef> getKitByTagOrMac(String mac, String tag){
            if (mac != null) {
                mac = mac.toUpperCase();
            }
		Disjunction or = Restrictions.or(Restrictions.eq(NAME_QUERY_STRING, tag), Restrictions.eq("code", tag), Restrictions.eq("macAddress", mac));
		List<EnrollmentRef> refs = dbService.getListByCriteria(EnrollmentRef.class, or);
		logger.debug("Ref list retrieved: " + refs.size());
		return refs;
	}

	public ClientRefResponse retagKit(ClientRefRequest cr, EnrollmentRef ref, boolean tag){
		ClientRefResponse crx = new ClientRefResponse();
		logger.debug("Scenario #2 :: Tag or MacAddress already exists " + cr.getRef() + " with id " + cr.getMac());
		// #2a. Null Mac Address
		if(ref.getMacAddress() == null){
			logger.debug("Retag operation using a ref which was previously used by another kit");
			ref.setDescription(KYC_SMART_CLIENT);
			ref.setDateInstalled(new Timestamp(new Date().getTime()));
			ref.setInstalledBy(cr.getAdminEmail());
			ref.setMacAddress(cr.getMac());
			ref.setNetworkCardName(cr.getNetCardName());
			ref.setCustom1(cr.getPatchVersion() == null ? "" : String.valueOf(cr.getPatchVersion()));
			ref.setCustom2(cr.getAdminMobile());
			ref.setCustom3(cr.getKitType());
			ref.setCorporate(Boolean.FALSE);
			update(ref);
//			appSession.queueKit(ref);
			
			tagRetagKit(cr, ref, true);
			
			crx.setStatus(0);
			crx.setMessage("Kit tag updated successfully");
//			licenseKit(cr);
			return crx;
		}

		// #2b. Code and Mac Address Match
		if(ref.getCode().equalsIgnoreCase(cr.getRef()) && cr.getMac().equalsIgnoreCase(ref.getMacAddress())){
			crx.setStatus(0);
			crx.setMessage("Kit tag unchanged. No update required");
			return crx;
		}

		// #2c. Only Code Match
		if(!cr.getMac().equalsIgnoreCase(ref.getMacAddress()) && tag){
			crx.setStatus(-2);
			crx.setMessage("Kit tag unavailable for use. Tag is already in use");
			return crx;
		}

		// #2d. Only Mac Address Match {RE TAG}
		if(!ref.getCode().equalsIgnoreCase(cr.getRef()) && !tag){
			try{
					tagRetagKit(cr, ref, true);
			}catch(NwormQueryException e){
				logger.error("Error in updating ref: ", e);
				crx.setStatus(-2);
				crx.setMessage("Server error");
				return crx;
			}

//			appSession.queueKit(createRef(cr));

			crx.setStatus(0);
			crx.setMessage("Kit tag updated successfully");
//			licenseKit(cr);
			return crx;
		}
		if(tag){
			crx.setStatus(-2);
			crx.setMessage("Kit had previously been tagged with " + ref.getCode());
			return crx;
		}
		return crx;
	}

	/**
	 * 
	 * @param enrollmentRef
	 * @param newTag
	 * @return TagHistory
	 */
	private TagHistory createTagHistory (String oldTag, ClientRefRequest cr, boolean retag) throws NwormQueryException{
		//log tag history to keep track of changes on existing EnrollmentRef
		TagHistory tagHistory = new TagHistory();
		tagHistory.setAdminEmail(cr.getAdminEmail());
		tagHistory.setMacAddress(cr.getMac());
		tagHistory.setNewTag(cr.getRef());
		if (retag) {
			tagHistory.setOldTag(oldTag);
		}
		if(dbService.create(tagHistory) != null){
			logger.debug("New Tag History created for " + cr.getRef());
		}
		return tagHistory;
	}
	
	private EnrollmentRef createRef(ClientRefRequest cr){
		// Create a new Enrollment Ref
		EnrollmentRef nref = new EnrollmentRef();
		nref.setCode(cr.getRef());
		nref.setName(cr.getRef());
		nref.setDescription(KYC_SMART_CLIENT);
		nref.setMacAddress(cr.getMac());
		nref.setDateInstalled(new Timestamp(new Date().getTime()));
		nref.setInstalledBy(cr.getAdminEmail());
		nref.setNetworkCardName(cr.getNetCardName());
		nref.setCustom1(cr.getPatchVersion() == null ? "" : String.valueOf(cr.getPatchVersion()));
		nref.setCustom2(cr.getAdminMobile());
		nref.setCustom3(cr.getKitType());
		nref.setCorporate(Boolean.FALSE);
		if(dbService.create(nref) != null){
			logger.debug("New Ref Tag created " + cr.getRef());
		}

		return nref;
	}
	
	private Node createNode(EnrollmentRef eRef, DeviceStatus device){
		Node node = new Node();
		node.setInstallationTimestamp(eRef.getDateInstalled());
		node.setInstalledBy(eRef.getInstalledBy());
		node.setMacAddress(eRef.getMacAddress());
		node.setNetworkCardName(eRef.getNetworkCardName());
		node.setDeviceStatus(device);
		node.setLastInstalledUpdate(eRef.getCustom1() == null ? null : Float.valueOf(eRef.getCustom1()));
                node.setEnrollmentRef(eRef);
		if(dbService.create(node) != null){
			logger.debug("Node successfully created for " + eRef.getCode());
		}
		return node;
	}
	
	private VersionLog createVersionLog(ClientRefRequest cr, Node node){
		VersionLog versionLog = new VersionLog();
        versionLog.setType(VersionType.KIT_VERSION);
        versionLog.setVersion(cr.getPatchVersion() == null ? "" : cr.getPatchVersion() + "");
        versionLog.setNode(node);
        if(dbService.create(versionLog) != null){
        	logger.debug("Kit version logged for " + cr.getRef());
        }
        return versionLog;
	}
	
	/**
	 * Assigns the created node to a dealer
	 * @param node
	 * @return
	 */
	private NodeAssignment doNodeSraaAssignment(String kitTag, Node node, boolean retag){
		//extract dealer code from kit tag
		String dealerCode = null;
		try{
			dealerCode = kitTag.split("-")[1];
		}catch(ArrayIndexOutOfBoundsException ex){
			logger.error("Cannot extract dealer code from " + kitTag);
			return null;
		}
		
		//get the SRAA
		logger.debug("Dealer code from " + kitTag + " is " + dealerCode);
		KycDealer dealer = dbService.getByCriteria(KycDealer.class, Restrictions.eq("dealCode", dealerCode), Restrictions.eq("active", true));
		if(dealer == null){
                    logger.debug("No dealer found with code: " + dealerCode);
			return null;
		}
		
                logger.debug("Found dealer with code: " + dealerCode);
		NodeAssignment assignment = null;
		if(retag){
			List<NodeAssignment> nodeAssignments = dbService.getListByCriteria(NodeAssignment.class, Restrictions.eq("targetNode.id", node.getId())); //caters for duplicates created by the DBAs
			if(nodeAssignments != null && !nodeAssignments.isEmpty()){
				assignment = nodeAssignments.get(0);
				logger.debug("Derived node assignment: " + assignment.getPk());
			}
		}
		if(assignment == null){
			assignment = new NodeAssignment();
		}
		
		assignment.setTargetNode(node);
		assignment.setAssignedDealer(dealer);
		if(dbService.createOrUpdate(assignment)){
			logger.debug("Successfully assigned/reassigned " + kitTag + " to dealer " + dealer.getName());
		}
		return assignment;
	}

	private void tagRetagKit(ClientRefRequest cr, EnrollmentRef ref, boolean retag) throws NwormQueryException {
		StopWatch sw = new StopWatch();
		EnrollmentRef eRef = null;
		Node node = getNodeByMac(cr.getMac());
		if(!retag){
			//new devices
			eRef = createRef(cr);
			if(node == null){
				node = createNode(eRef, getDeviceStatus("UNASSIGNED"));
			}else{
				//should not happen, but update node with client info if it does
				node.setLastInstalledUpdate(cr.getPatchVersion());
				node.setInstalledBy(cr.getAdminEmail());
				if(cr.getInstallDate() != null){
					node.setInstallationTimestamp(new Timestamp(cr.getInstallDate().getTime()));
				}
				node.setNetworkCardName(cr.getNetCardName());
				node.setLastUpdated(new Date());
				update(node);
			}
		}else{
			//existing devices
//			eRef = ref.getMacAddress() == null ? createRef(cr) : ref; //mac address is null for retag; not null for double kit entry cases
                        //update enrollment ref with new tag
                        ref.setCode(cr.getRef());
                        update(ref);
                        eRef = ref;
			node = getNodeByMac(cr.getMac());
		}
		
		createVersionLog(cr, node);		
		String kitTag = retag ? cr.getRef() : eRef.getCode();
		String oldTag = retag ? ref.getCode() : null;
		
		if(Boolean.valueOf(appProps.getProperty("assign-node-sraa", "true"))){
			doNodeSraaAssignment(kitTag, node, retag);
		}
		
		createTagHistory(oldTag, cr, retag);
//		licenseKit(cr); // request for a license
		logger.debug("Time taken to tag/retag kit [" + sw.currentElapsedTime() + " ms]");
		sw.stop();
	}

	/**
	 * Creates a new license request if none exists for this kit
	 */
	private void licenseKit(ClientRefRequest cr){
		// prepare licenses coming from the client
		String fingerLicense = decodeLicense(cr.getFingerLicense());
		String wsqLicense = decodeLicense(cr.getWsqLicesne());
		License license = getLicenseByMac(cr.getMac());
		createOrUpdateLicenceRequest(license, cr.getMac(), fingerLicense, wsqLicense);
	}

	public License getLicenseByMac(String mac){
		return dbService.getByCriteria(License.class, Restrictions.eq("macAddress", mac));
	}

	public Node getNodeByTagName(String tagName){
		return dbService.getByCriteria(Node.class, Restrictions.eq("dataListTagName", tagName));
	}

	public DeviceStatus getDeviceStatus(String status){
		return dbService.getByCriteria(DeviceStatus.class, Restrictions.eq(NAME_QUERY_STRING, status));
	}

	public DuplicateNode getDuplicateNodeByMac(String macAddress){
		return dbService.getByCriteria(DuplicateNode.class, Restrictions.eq("macAddress", macAddress));
	}

	public DeviceType getDeviceTypeByName(String name){
		return dbService.getByCriteria(DeviceType.class, Restrictions.ilike(NAME_QUERY_STRING, name, MatchMode.EXACT));
	}

	/**
	 * @param macAddress
	 * @param finger
	 * @param wsq
	 * @return Created licence request object
	 */
	private License createLicenceRequest(String macAddress, String finger, String wsq){
		License req = new License();
		try{
			logger.debug("In createLicenceRequest()...");
			req = new License();
			req.setFingerLicense(finger);
			req.setWsqLicense(wsq);
			req.setReceiptTimestamp(new Timestamp(new Date().getTime()));
			req.setMacAddress(macAddress);
			req.setRequestStatus(getRequestStatus(finger, wsq));
			req.setUpdateDescription("Licence request created. Licence from client: " + 
					(!isEmpty(wsq)? "[WSQ] " : "") + (!isEmpty(finger)? "[Fingerprint]" : "") );
			req.setLastUpdated(new Timestamp(new Date().getTime()));
			dbService.create(req);
		}catch(NwormQueryException e){
			logger.error(e.getMessage());
		}
		return req;
	}

	/**
	 * @param req
	 * @param finger
	 * @param wsq
	 */
	private void updateLicenceRequest(License req, String finger, String wsq){
		try{
			logger.debug("In updateLicenceRequest()...");
			boolean wsqFromClientExists = !isEmpty(wsq);
			boolean fingerFromClientExists = !isEmpty(finger);
			boolean wsqExistsInDB = !isEmpty(req.getWsqLicense());
			boolean fingerExistsInDB = !isEmpty(req.getFingerLicense());
			boolean update = false;

			logger.debug("wsqFromClientExists="+wsqFromClientExists+"; fingerFromClientExists" + fingerFromClientExists+
					"; wsqExistsInDB" + wsqExistsInDB+"; fingerExistsInDB" + fingerExistsInDB);
			String wsqDesc = "";
			String fingerDesc = "";

			if(!wsqExistsInDB && wsqFromClientExists){
				update = true;
				req.setWsqLicense(wsq);
				wsqDesc = "[WSQ]";
			}

			if(!fingerExistsInDB && fingerFromClientExists){
				update = true;
				req.setFingerLicense(finger);
				fingerDesc = "[Fingerprint]";
			}

			logger.debug("Update condition met: " + update);
			if(update){
				req.setRequestStatus(getRequestStatus(finger, wsq));
				req.setUpdateDescription("Licence request updated. Licence from client: " 
						+ wsqDesc + " " + fingerDesc );
				req.setLastUpdated(new Timestamp(new Date().getTime()));
				dbService.update(req);
				logger.debug("Licence request updated for machine with ID: " + req.getMacAddress());
			}

			//TODO Send notification to support


		}catch(NwormQueryException e){
			logger.error(e.getMessage());
		}
	}

	public void createOrUpdateLicenceRequest(License req, String mac, String finger, String wsq){
		try{
			logger.debug("In create or update lic request...");
			if(req == null){
				createLicenceRequest(mac, finger, wsq);
				String licenceRequestAlert = "A licence request has been made by client with ID: " + mac + " at " + new Date().toString();
				List<String> recvs = Arrays.asList(licenseAdminMobile.split(","));
				ksms.sendSMS(recvs, licenceRequestAlert, SMSManager.MESSAGE_PRIORITY_0);
			}else{
				updateLicenceRequest(req, finger, wsq);
				String licenceRequestAlert = "A licence request has been made by client with ID: " + mac + " at " + new Date().toString();
				List<String> recvs = Arrays.asList(licenseAdminMobile.split(","));
				ksms.sendSMS(recvs, licenceRequestAlert, SMSManager.MESSAGE_PRIORITY_0);
			}
		}catch(NwormQueryException e){
			logger.error(e.getMessage());
		}
	}

	private  String getRequestStatus(String finger, String wsq){
		String requestStatus = INCOMPLETE_LICENCE;

		if(!isEmpty(wsq) && !isEmpty(finger)){
			requestStatus = FULL_LICENCE;
		}else if (isEmpty(wsq) && isEmpty(finger)){
			requestStatus = NO_LICENCE;
		}
		return requestStatus;
	}
}
