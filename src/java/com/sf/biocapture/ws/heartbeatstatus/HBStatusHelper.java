package com.sf.biocapture.ws.heartbeatstatus;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.sf.biocapture.entity.HeartBeat;
import com.sf.biocapture.entity.HeartbeatStatus;
import com.sf.biocapture.util.map.LocationUtil;

import nw.commons.NeemClazz;
import nw.orm.core.exception.NwormQueryException;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class HBStatusHelper extends NeemClazz {
	
	@Inject
	GeoFenceDS dataService;
	
	@Inject
	LocationUtil locationUtil;

	public void createOrUpdateHeartStatus(HeartBeat beat) {
		HeartbeatStatus hbStatus = null;
		try {
			hbStatus = dataService.getHeartBeatStatus(beat.getRefDeviceId(), beat.getMacAddress());
		} catch (NwormQueryException e) {
			return;
		}
		
		if(hbStatus == null) {
			hbStatus = createHeatBeatStatus(beat);
		} else {
			hbStatus = updateHeartbeatStatus(hbStatus, beat);
		}
		
		if(hbStatus != null) {
			locationUtil.checkForLocationDeviation(hbStatus.getLatitude(), hbStatus.getLongitude(), hbStatus.getCoordinateAddress(), 
					hbStatus.getMacAddress(), hbStatus.getRefDeviceId());
		}
	}

	private HeartbeatStatus createHeatBeatStatus(HeartBeat beat) {
		try {
			HeartbeatStatus hbStatus = new HeartbeatStatus();
			hbStatus.setActive(true);
                        hbStatus.setMockedCoordinate(beat.getMockedCoordinate());
			hbStatus.setAgentMobile(beat.getAgentMobile());
			hbStatus.setAgentName(beat.getAgentName());
			hbStatus.setAvailableStorage(beat.getAvailableStorage());
			hbStatus.setCameraStatus(beat.getCameraStatus());
			hbStatus.setClientTimeStatus(beat.getClientTimeStatus());
			hbStatus.setClientUptime(beat.getClientUptime());
			hbStatus.setCoordinateAddress(locationUtil.getAddress(beat.getLatitude(), beat.getLongitude(), null));
			hbStatus.setDeleted(false);
			hbStatus.setDeployState(beat.getDeployState());
			hbStatus.setLatitude(beat.getLatitude());
			hbStatus.setLongitude(beat.getLongitude());
			hbStatus.setLocationAccuracy(beat.getLocationAccuracy());
			hbStatus.setLocationInformationSource(beat.getLocationInformationSource());
			hbStatus.setMacAddress(beat.getMacAddress());
			hbStatus.setNetworkConnectionType(beat.getNetworkConnectionType());
			hbStatus.setNetworkStrength(beat.getNetworkStrength());
			hbStatus.setOsName(beat.getOsName());
			hbStatus.setOsVersion(beat.getOsVersion());
			hbStatus.setProcessorSpeed(beat.getProcessorSpeed());
			hbStatus.setRamSize(beat.getRamSize());
			hbStatus.setRealtimeDeviceId(beat.getRealTimeDeviceId());
			hbStatus.setReceiptDate(new Date());
			hbStatus.setRefDeviceId(beat.getRefDeviceId());
			hbStatus.setRooted(beat.getRooted());
			hbStatus.setScannerStatus(beat.getScannerStatus());
			hbStatus.setTag(beat.getTag());
			hbStatus.setTotalStorage(beat.getTotalStorage());
			hbStatus.setUsedStorage(beat.getUsedStorage());
			hbStatus.setNetworkType(beat.getNetworkType());
                        hbStatus.setAppVersion(beat.getAppVersion());
			dataService.getDbService().create(hbStatus);
			return hbStatus;
		} catch(NwormQueryException e) {
			logger.error("Error while creating heartbeatstatus : ", e);
		}
		return null;
	}
	
	private HeartbeatStatus updateHeartbeatStatus(HeartbeatStatus beatStatus, HeartBeat beat) {
		try {
			beatStatus.setAgentMobile(beat.getAgentMobile());
			beatStatus.setAgentName(beat.getAgentName());
			beatStatus.setAvailableStorage(beat.getAvailableStorage());
			beatStatus.setCameraStatus(beat.getCameraStatus());
			beatStatus.setClientTimeStatus(beat.getClientTimeStatus());
			beatStatus.setClientUptime(beat.getClientUptime());
                        beatStatus.setMockedCoordinate(beat.getMockedCoordinate());
			
			if(beat.getLongitude() != null && beat.getLongitude() != 0 && beat.getLatitude() != null && beat.getLatitude() != 0) {
				String address = locationUtil.getAddress(beat.getLatitude(), beat.getLongitude(), beatStatus);
				beatStatus.setCoordinateAddress(address);
				beatStatus.setLatitude(beat.getLatitude());
				beatStatus.setLongitude(beat.getLongitude());
				beatStatus.setLocationAccuracy(beat.getLocationAccuracy());
				beatStatus.setLocationInformationSource(beat.getLocationInformationSource());
			}			
			beatStatus.setDeployState(beat.getDeployState());
			beatStatus.setMacAddress(beat.getMacAddress());
			beatStatus.setNetworkConnectionType(beat.getNetworkConnectionType());
			beatStatus.setNetworkStrength(beat.getNetworkStrength());
			beatStatus.setOsName(beat.getOsName());
			beatStatus.setOsVersion(beat.getOsVersion());
			beatStatus.setProcessorSpeed(beat.getProcessorSpeed());
			beatStatus.setRamSize(beat.getRamSize());
			beatStatus.setRealtimeDeviceId(beat.getRealTimeDeviceId());
			beatStatus.setRefDeviceId(beat.getRefDeviceId());
			beatStatus.setReceiptDate(new Date());
			beatStatus.setRooted(beat.getRooted());
			beatStatus.setScannerStatus(beat.getScannerStatus());
			beatStatus.setTag(beat.getTag());
			beatStatus.setTotalStorage(beat.getTotalStorage());
			beatStatus.setUsedStorage(beat.getUsedStorage());
			beatStatus.setNetworkType(beat.getNetworkType());
                        beatStatus.setAppVersion(beat.getAppVersion());
			dataService.getDbService().update(beatStatus);
		} catch(NwormQueryException e) {
			logger.error("Error while editing heartbeatstatus : ", e);
		}
		
		return beatStatus;
	}
}
