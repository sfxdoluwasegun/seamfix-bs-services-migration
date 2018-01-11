package com.sf.biocapture.ws.notification;

import com.sf.biocapture.analyzer.IntrusionAnalyzer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nw.orm.core.exception.NwormQueryException;
import nw.orm.core.query.QueryModifier;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.sf.biocapture.app.NotificationCache;
import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.KycBroadcast;
import com.sf.biocapture.entity.Node;
import com.sf.biocapture.entity.security.KMUser;
import com.sf.biocapture.ws.HeaderIdentifier;
import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.notification.querytransformer.KycBroadcast_;
import javax.ws.rs.core.HttpHeaders;
import nw.orm.core.query.QueryFetchMode;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

/**
 * 
 * @author Nnanna
 * @since 10/11/2016
 *
 */
@Stateless
public class NotificationDS extends DataService {
	
	@Inject
	NotificationCache cache;
        
        @Inject
        IntrusionAnalyzer analyzer;
	
	private static final String CACHE_PREFIX = "NOTN-";
        
        private static final String GLOBAL = "global";
        
        private static final String EXPIRED = "expired";
	
	public NotificationResponse getUserNotifications(NotificationRequest request, HttpHeaders headers){
		//check for missing email or mac address
                HeaderIdentifier headerIdentifier = analyzer.getIdentifier(headers);
		
		if(request.getEmail() == null || request.getMacAddress() == null){
			return new NotificationResponse(ResponseCodeEnum.INVALID_INPUT, "Email or mac address is missing");
		}
		
		String cacheKey = !StringUtils.isEmpty(headerIdentifier.getRefDeviceId()) ? CACHE_PREFIX + headerIdentifier.getRealTimeDeviceId() :CACHE_PREFIX +request.getMacAddress();
		NotificationResponse resp = cache.getItem(cacheKey, NotificationResponse.class);
		if(resp != null){
			logger.debug("CLIENT NOTIFICATION ALREADY CACHED: " + request.getEmail());
			return resp;
		}else{
			logger.debug("CLIENT NOTIFICATION NOT CACHED: " + request.getEmail());
			resp = new NotificationResponse();
		}
		
		KMUser user = doLightKMUserRetrieval(request.getEmail());
		if(user == null){
			return new NotificationResponse(ResponseCodeEnum.FAILED_AUTHENTICATION, "User with email, " + request.getEmail() + ", not found");
		}else{
			//check if user is active
			if(!user.isActive()){
				return new NotificationResponse(ResponseCodeEnum.INACTIVE_ACCOUNT, "User is blacklisted");
			}
		}
		
		try{
			//confirm that node exists
			Node node = doLightNodeRetrieval(request.getMacAddress());
			
			//load user notifications
			resp.setNotifications(formatNotifications_(doLightNotificationsRetrieval(user.getPk(), node.getId(), request.getLastMessageDate())));
			resp.setAgentEmail(request.getEmail());
			resp.setCode(ResponseCodeEnum.SUCCESS);
			resp.setDescription("Successfully retrieved user notifications");
		}catch(NwormQueryException ex){
			logger.error("Error in retrieving user notifications", ex);
			resp = new NotificationResponse(ResponseCodeEnum.ERROR, "Server Error");
		}
		
		cache.setItem(cacheKey, resp, appProps.getInt("notification-cache-time", 3600)); //cache notifications for 1hr
		
		return resp;
	}
	
	private List<NotificationData> formatNotifications(List<KycBroadcast> notifications){
		if(notifications == null){
			return null;
		}
		List<NotificationData> data = new ArrayList<NotificationData>();
		for(KycBroadcast broadcast : notifications){
                    logger.debug("TargetNode: " + broadcast.getTargetNode());
                    logger.debug("TargetUser: " + broadcast.getTargetUser());
			data.add(NotificationData.from(broadcast));
		}
		
		return data;
	}
        
	private List<NotificationData> formatNotifications_(List<KycBroadcast_> notifications){
		if(notifications == null){
			return null;
		}
		List<NotificationData> data = new ArrayList<NotificationData>();
		for(KycBroadcast_ broadcast : notifications){
			data.add(NotificationData.from(broadcast));
		}
		
		return data;
	}
	
        @Deprecated
	private List<KycBroadcast> getNotifications(Long userPk, Long nodeId, Date lastMessageDate){
		Disjunction or = Restrictions.disjunction();
		or.add(Restrictions.eq(GLOBAL, true));
		if(nodeId != null){
			or.add(Restrictions.eq("targetNode.id", nodeId));
		}
		if(userPk != null){
			or.add(Restrictions.eq("targetUser.pk", userPk));
		}
		
		Conjunction and = Restrictions.conjunction();
		and.add(or);
		and.add(Restrictions.eq("active", true));
		and.add(Restrictions.eq(EXPIRED, false));
		if(lastMessageDate != null){ //client already has messages
			logger.debug("Latest message on the client: " + lastMessageDate);
			and.add(Restrictions.gt("createDate", lastMessageDate));
		}
		
		QueryModifier qm = new QueryModifier(KycBroadcast.class);
		qm.addOrderBy(Order.desc("lastModified"));
                
                QueryFetchMode targetNode = new QueryFetchMode();
                targetNode.setAlias("targetNode");
                targetNode.setFetchMode(FetchMode.LAZY);
                qm.addFetchMode(targetNode);
                
                QueryFetchMode targetUser = new QueryFetchMode();
                targetUser.setAlias("targetUser");
                targetUser.setFetchMode(FetchMode.LAZY);
                qm.addFetchMode(targetUser);
		
		return dbService.getListByCriteria(KycBroadcast.class, qm, and);
	}
        
    private List<KycBroadcast_> doLightNotificationsRetrieval(Long userPk, Long nodeId, Date lastMessageDate) {
        final String LAST_MODIFIED = "lastModified";

        Session session = null;
        try {
            session = dbService.getSessionService().getManagedSession();
            Criteria criteria = session.createCriteria(KycBroadcast.class);
            Disjunction orr = Restrictions.disjunction();
            orr.add(Restrictions.eq(GLOBAL, true));
            if (nodeId != null) {
                orr.add(Restrictions.eq("targetNode.id", nodeId));
            }
            if (userPk != null) {
                orr.add(Restrictions.eq("targetUser.pk", userPk));
            }

            Conjunction and = Restrictions.conjunction();
            and.add(orr);
            and.add(Restrictions.eq("active", true));
            and.add(Restrictions.eq(EXPIRED, false));
            if (lastMessageDate != null) { //client already has messages
                logger.debug("Latest message on the client: " + lastMessageDate);
                and.add(Restrictions.gt("createDate", lastMessageDate));
            }
            criteria.add(and);
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.property("pk").as("pk"))
                    .add(Projections.property("message").as("message"))
                    .add(Projections.property(GLOBAL).as(GLOBAL))
                    .add(Projections.property(EXPIRED).as(EXPIRED))
                    .add(Projections.property("targetNode.id").as("nodeFk"))
                    .add(Projections.property("targetUser.pk").as("userFk"))
                    .add(Projections.property(LAST_MODIFIED).as(LAST_MODIFIED)));
            criteria.addOrder(Order.desc(LAST_MODIFIED));
            return criteria.setResultTransformer(Transformers.aliasToBean(KycBroadcast_.class)).list();
        } catch (NwormQueryException | HibernateException e) {
            logger.error("", e);
        } finally {
            dbService.getSessionService().closeSession(session);
        }
        return null;
    }
    
    private KMUser doLightKMUserRetrieval(String agentEmail) {
        Session session = null;
        KMUser user = null;
        try {
            session = dbService.getSessionService().getManagedSession();
            String query = "select u.pk as pk, u.active as active, u.surname as surname, u.first_name as firstName, u.email_address as emailAddress from km_user u where u.email_address = :emailAddress";
            SQLQuery sqlq = session.createSQLQuery(query);
            sqlq.setParameter("emailAddress", agentEmail.toLowerCase());
            sqlq.addScalar("pk", new LongType());
            sqlq.addScalar("active", new BooleanType());
            sqlq.addScalar("surname", new StringType());
            sqlq.addScalar("firstName", new StringType());
            sqlq.addScalar("emailAddress", new StringType());
            user = (KMUser) sqlq.setResultTransformer(Transformers.aliasToBean(KMUser.class)).uniqueResult();
        } catch (HibernateException | NwormQueryException e) {
            logger.error("", e);
        } finally {
            dbService.getSessionService().closeSession(session);
        }
        return user;
    }

    private Node doLightNodeRetrieval(String macAddress) {
        Session session = null;
        Node node = null;
        try {
            session = dbService.getSessionService().getManagedSession();
            String query = "select n.id as id from node n where n.mac_address =:macAddress";
            SQLQuery sqlq = session.createSQLQuery(query);
            sqlq.setParameter("macAddress", macAddress);
            sqlq.addScalar("id", new LongType());
            node = (Node) sqlq.setResultTransformer(Transformers.aliasToBean(Node.class)).uniqueResult();
        } catch (HibernateException | NwormQueryException e) {
            logger.error("", e);
        } finally {
            dbService.getSessionService().closeSession(session);
        }
        return node;
    }
}
