package com.sf.biocapture.ws.tags;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.entity.Device;
import com.sf.biocapture.entity.DeviceStatus;
import com.sf.biocapture.entity.DeviceType;
import com.sf.biocapture.entity.EnrollmentRef;
import com.sf.biocapture.entity.Node;
import com.sf.biocapture.entity.audit.VersionLog;
import com.sf.biocapture.entity.enums.VersionType;

@MessageDriven(activationConfig = {@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/bio/queue/KycNodes")})
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class NodeListener extends BsClazz implements MessageListener{
	
	@Inject
	private TagsDS tagsDS;

	@Override
	public void onMessage(Message msg) {
		try {
			ObjectMessage omsg = (ObjectMessage) msg;
			EnrollmentRef ref = (EnrollmentRef) omsg.getObject();
			
			Node node = tagsDS.getNodeByMac(ref.getMacAddress());
			DeviceStatus device = tagsDS.getDeviceStatus("UNASSIGNED");
			
			if (node != null) {
				logger.debug("Node "+ ref.getCode() +" with mac + " + ref.getMacAddress() + " already exists");
			} else {
				logger.debug(ref.getCode()+ " is a brand new Node");
				node = new Node();
				node.setInstallationTimestamp(ref.getDateInstalled());
				node.setInstalledBy(ref.getInstalledBy());
				node.setMacAddress(ref.getMacAddress());
				node.setNetworkCardName(ref.getNetworkCardName());
				node.setDeviceStatus(device);
				node.setLastInstalledUpdate(ref.getCustom1() == null ? null : Float.valueOf(ref.getCustom1()));
				tagsDS.getDbService().create(node);
				createNodeDevices(node, ref.getCode()); //for reconciliation?
                                
                                VersionLog versionLog = new VersionLog();
                                versionLog.setType(VersionType.KIT_VERSION);
                                versionLog.setVersion(ref.getCustom1());
                                versionLog.setNode(node);
                                tagsDS.getDbService().create(versionLog);
                                
				logger.debug("Node Created Successfully");
			}
			
		} catch (JMSException e) {
			logger.error("Exception in creating node ", e);
		}
		
	}
	
	private void createNodeDevices(Node node, String ref) {
		Device newDevice1 = new Device();
		DeviceType cam = tagsDS.getDeviceTypeByName("Camera");

		if (cam == null) {
			cam = createDeviceType("Camera");
		}
		
		newDevice1.setDeviceTag(ref);
		newDevice1.setNode(node);
		newDevice1.setDeviceType(cam);
		tagsDS.getDbService().create(newDevice1);

		Device newDevice2 = new Device();
		DeviceType fpScanner = null;
		fpScanner = tagsDS.getDeviceTypeByName("Fingerprint Scanner");//CustomService.getDeviceTypeByName(Constants.DEVICE_TYPE_FINGER_PRINT_SCANNER);
		
		if (fpScanner == null) {
			fpScanner = createDeviceType("Fingerprint Scanner");
		}
		newDevice2.setDeviceTag(ref);
		newDevice2.setNode(node);
		newDevice2.setDeviceType(fpScanner);
		tagsDS.getDbService().create(newDevice2);

		logger.debug("Node's Devices Created Successfully...");
	}

	private DeviceType createDeviceType(String device) {
		DeviceType deviceType = new DeviceType();
		deviceType.setName(device.replace("_", " "));
		tagsDS.getDbService().create(deviceType);
		return deviceType;
	}
}
