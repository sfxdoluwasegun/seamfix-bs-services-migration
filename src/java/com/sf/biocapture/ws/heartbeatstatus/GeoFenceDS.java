package com.sf.biocapture.ws.heartbeatstatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;

import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.HeartbeatStatus;
import com.sf.biocapture.entity.NodeAssignment;
import com.sf.biocapture.entity.Outlet;
import com.sf.biocapture.entity.audit.GeoFenceLog;

import nw.orm.core.exception.NwormQueryException;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class GeoFenceDS extends DataService {

    public HeartbeatStatus getHeartBeatStatus(String deviceId, String mac) throws NwormQueryException {
        if (StringUtils.isBlank(deviceId) && StringUtils.isBlank(mac)) {
            return null;
        }

        Session session = null;
        HeartbeatStatus heartbeatStatus = null;
        try {
            session = dbService.getSessionService().getManagedSession();
            if (StringUtils.isNotBlank(deviceId)) {
                Criteria criteriaId = session.createCriteria(HeartbeatStatus.class);
                criteriaId.add(Restrictions.eq("refDeviceId", deviceId));
                heartbeatStatus = (HeartbeatStatus) criteriaId.uniqueResult();
                if (heartbeatStatus != null) {
                    return heartbeatStatus;
                }
            }

            //try to retrieve by mac address for existing systems(i.e. devices that are still making use of mac address)
            if (StringUtils.isNotBlank(mac)) {
                Criteria criteriaMac = session.createCriteria(HeartbeatStatus.class);
                criteriaMac.add(Restrictions.eq("macAddress", mac));
                heartbeatStatus = (HeartbeatStatus) criteriaMac.uniqueResult();
                return heartbeatStatus;
            }
        } catch (HibernateException | NwormQueryException e) {
            logger.error("Something went wrong while getting heartbeatStatus By deviceId and/or macAdress", e);
            throw new NwormQueryException("for getting heartbeatstatus: ", e);
        } finally {
            dbService.getSessionService().closeSession(session);
        }
        return heartbeatStatus;
    }

    public List<GeoFenceLog> getGeoFenceLogsByRange(double lat, double lng, double range) {
        double minLat = lat - range, maxLat = lat + range, minLng = lng - range, maxLng = lng + range;
        Map<String, Object> map = new HashMap<String, Object>();
        String hql = "SELECT g FROM GeoFenceLog g WHERE g.latitude >= :minLat AND g.latitude <= :maxLat AND g.longitude >= :minLng AND g.longitude <= :maxLng";
        map.put("minLat", minLat);
        map.put("maxLat", maxLat);
        map.put("minLng", minLng);
        map.put("maxLng", maxLng);
        return dbService.getListByHQL(hql, map, GeoFenceLog.class);
    }

    public List<GeoFenceLog> getGeoFenceLogsByKitRange(double lat, double lng, double range, String mac, String deviceId) {
        double minLat = lat - range, maxLat = lat + range, minLng = lng - range, maxLng = lng + range;
        String hql = "SELECT g FROM GeoFenceLog g left join g.heartbeatStatus h WHERE (h.macAddress = :mac ";
        if (StringUtils.isNotBlank(deviceId)) {
            hql += " OR h.refDeviceId = :deviceId ";
        }
        hql += ") AND g.latitude >= :minLat AND g.latitude <= :maxLat AND g.longitude >= :minLng AND g.longitude <= :maxLng AND g.deleted = :deleted";

        Session session = null;
        try {
            session = dbService.getSessionService().getManagedSession();
            Query query = session.createQuery(hql);
            query.setParameter("minLat", minLat);
            query.setParameter("maxLat", maxLat);
            query.setParameter("minLng", minLng);
            query.setParameter("maxLng", maxLng);
            query.setParameter("mac", mac);
            query.setParameter("deleted", false);
            if (StringUtils.isNotBlank(deviceId)) {
                query.setParameter("deviceId", deviceId);
            }
            return (List<GeoFenceLog>) query.list();
        } catch (HibernateException | NwormQueryException e) {
            logger.error("Something went wrong while getting GeoFenceLog by kit: ", e);
        } finally {
            dbService.getSessionService().closeSession(session);
        }
        return null;
    }

    public NodeAssignment getNodeAssignment(String mac) {
        String hql = "SELECT a FROM NodeAssignment a left join a.targetNode.enrollmentRef e left join fetch a.outlet o left join fetch a.assignedDealer d "
                + "left join fetch d.assignedZone z left join fetch z.region r left join fetch a.fieldSupportAgent f WHERE a.targetNode.macAddress = :mac AND a.deleted = :deleted";
        Session session = null;
        try {
            session = dbService.getSessionService().getManagedSession();
            Query query = session.createQuery(hql);
            query.setParameter("mac", mac);
            query.setParameter("deleted", false);
            return (NodeAssignment) query.uniqueResult();
        } catch (HibernateException | NwormQueryException e) {
            logger.error("Something went wrong while getting NodeAssignment: ", e);
        } finally {
            dbService.getSessionService().closeSession(session);
        }
        return null;
    }

    public Outlet getOutlet(String mac, String deviceId) {
        String hql = "SELECT a FROM NodeAssignment a left join fetch a.outlet o left join a.targetNode.enrollmentRef e WHERE (e.macAddress = :mac";
        if (StringUtils.isNotBlank(deviceId)) {
            hql += " OR e.deviceId = :deviceId ";
        }
        hql += ") and a.deleted = :deleted order by a.createDate desc";

        Session session = null;
        try {
            session = dbService.getSessionService().getManagedSession();
            Query query = session.createQuery(hql);
            query.setParameter("mac", mac == null ? null : mac.toUpperCase());
            if (StringUtils.isNotBlank(deviceId)) {
                query.setParameter("deviceId", deviceId);
            }
            query.setParameter("deleted", false);
            List<NodeAssignment> nA = (List<NodeAssignment>) query.list();
            if (nA != null && !nA.isEmpty()) {
                return nA.get(0).getOutlet();
            }
        } catch (HibernateException | NwormQueryException e) {
            logger.error("Something went wrong while getting outlet: ", e);
        } finally {
            dbService.getSessionService().closeSession(session);
        }
        return null;
    }

    public List<UserEmailPojo> getUsersByRole(String[] code) {
        String sql = "Select ku.email_address as email from user_role ur left join km_user ku on ur.user_fk = ku.pk "
                + "left join km_role kr on ur.role_fk = kr.pk where kr.code in :code";
        Session session = null;
        try {
            session = dbService.getSessionService().getManagedSession();
            SQLQuery sqlQuery = session.createSQLQuery(sql);
            sqlQuery.setParameterList("code", code);
            //sqlQuery.setParameter("deleted", Boolean.valueOf(false));
            sqlQuery.addScalar("email", StringType.INSTANCE);
            return sqlQuery.setResultTransformer(Transformers.aliasToBean(UserEmailPojo.class)).list();
        } catch (HibernateException | NwormQueryException e) {
            logger.error("Something went wrong while getting User emails by role: ", e);
        } finally {
            dbService.getSessionService().closeSession(session);
        }
        return null;
    }

}
