package com.sf.biocapture.ws.heartbeat;

import java.sql.Timestamp;
import java.util.Date;

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
import com.sf.biocapture.common.GenericException;
import com.sf.biocapture.entity.HeartBeat;
import com.sf.biocapture.ws.heartbeatstatus.HBStatusHelper;

import nw.orm.core.exception.NwormQueryException;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/bio/queue/HeartBeat")})
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BeatMessageListener extends BsClazz implements MessageListener {

    @Inject
    private BeatDS bsm;
    
    @Inject
    private HBStatusHelper hbStatusHelper;

    private String delimiter = "#s#x#";

    @Override
    public void onMessage(Message msg) {
        try {
            ObjectMessage om = (ObjectMessage) msg;
            if (om.getObject() instanceof String) {
                String sp = (String) om.getObject();
                try {
                    process(sp);
                    logger.info("Heartbeat processed successfully : " + sp);
                } catch (GenericException e) {
                    logger.debug("Heartbeat processing failed: " + sp);
                    logger.error("Exception ", e);
                }
            } else if (om.getObject() instanceof ClientBeat) {
                ClientBeat beat = (ClientBeat) om.getObject();
                processBeat(beat);
                logger.info("Heartbeat processed successfully: " + beat.getTag());
            }

        } catch (JMSException e) {
            logger.error("Exception ", e);
        }
    }

    public void processBeat(ClientBeat beat) {
        try {
            HeartBeat h = new HeartBeat();
            h.setTag(beat.getTag());
            h.setAgentMobile(beat.getAgentMobile());
            h.setAgentName(beat.getAgent());
            h.setMacAddress(beat.getMacAddress());
            h.setRealTimeDeviceId(beat.getRealTimeDeviceId());
            h.setRefDeviceId(beat.getRefDeviceId());
            h.setDeployState(beat.getDeployState());
            h.setModemModel(beat.getModemModel());
            h.setModemSerial(beat.getModemSerial());
            h.setModemSignalLevel(String.valueOf(beat.getModemSignalLevel()));
            h.setCameraStatus(beat.isCamConnected() ? "CONNECTED" : "NOT_CONNECTED");
            h.setScannerStatus(beat.isScannerConnected() ? "CONNECTED" : "NOT_CONNECTED");
            h.setClientTimeStatus(beat.isLocalTimeCorrect() ? "CORRECT" : "NOT_CORRECT");
            h.setClientUptime(beat.getClientUptime());
            h.setLoginUptime(beat.getLoginUptime());

            h.setLongitude(beat.getLongitude());
            h.setLatitude(beat.getLatitude());

            h.setTotalRegistered(beat.getTotalRegistration());
            h.setTotalSent(beat.getTotalSynchronized());
            h.setTotalConfirmed(beat.getTotalConfirmed());
            h.setDailyRegistered(beat.getDailyRegistration());
            h.setDailySent(beat.getDailySynchronized());
            h.setDailyConfirmed(beat.getDailyConfirmed());

            h.setAppVersion(beat.getAppVersion());

            h.setReceiptTimestamp(new Timestamp(new Date().getTime()));
            h.setThresholdVersion(beat.getThresholdVersion());
            
            h.setNetworkStrength(beat.getNetworkStrength());
            h.setNetworkConnectionType(beat.getNetworkConnectionType());
            h.setRooted(beat.getRooted());
            h.setOsName(beat.getOsName());
            h.setOsVersion(beat.getOsVersion());
            h.setLocationInformationSource(beat.getLocationInformationSource());
            h.setLocationAccuracy(beat.getLocationAccuracy());
            h.setProcessorSpeed(beat.getProcessorSpeed());
            h.setUsedStorage(beat.getUsedStorage());
            h.setAvailableStorage(beat.getAvailableStorage());
            h.setTotalStorage(beat.getTotalStorage());
            h.setRamSize(beat.getRamSize());
            h.setNetworkType(beat.getNetworkType());
            h.setMockedCoordinate(beat.getMockedCoordinate());
            bsm.getDbService().create(h);
            hbStatusHelper.createOrUpdateHeartStatus(h);
        } catch (NwormQueryException e) {
            logger.error("Exception : ", e);
        }
    }

    @Deprecated
    public void process(String beat) throws GenericException {
        try {
            HeartBeat h = new HeartBeat();
            String[] beats = beat.split(delimiter);
            if (beats.length < 21) {
                throw new GenericException("Invalid heartbeat mesage received");
            }
            String tag = beats[0];
            String agentName = beats[1];
            String agentMobile = beats[2];
            String macAddress = beats[3];
            String deployState = beats[4];
            String modemSerial = beats[5];
            String modemModel = beats[6];
            String modemSignal = beats[7];
            String camStatus = beats[8];
            String scannerStatus = beats[9];
            String clientTimeStatus = beats[10];
            String uptime = beats[11];
            String loginUptime = beats[12];
            String longitude = beats[13];
            String latitude = beats[14];
            String totalReg;
            String totalSent;
            String totalConfirmed;
            String subs;
            String sent;
            String confirmed;
            try {
                totalReg = beats[15];
                totalSent = beats[16];
                totalConfirmed = beats[17];
                subs = beats[18];
                sent = beats[19];
                confirmed = beats[20];

                h.setTotalRegistered(Long.valueOf(totalReg.split(":")[1]));
                h.setTotalSent(Long.valueOf(totalSent.split(":")[1]));
                h.setTotalConfirmed(Long.valueOf(totalConfirmed.split(":")[1]));
                h.setDailyRegistered(Long.valueOf(subs.split(":")[1]));
                h.setDailySent(Long.valueOf(sent.split(":")[1]));
                h.setDailyConfirmed(Long.valueOf(confirmed.split(":")[1]));

            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                logger.error("", e);
            }
            String[] ams = agentMobile.split(":");
            if (ams.length > 1) {
                h.setAgentMobile(agentMobile.split(":")[1]);
            } else {
                h.setAgentMobile("NA");
            }
            h.setTag(tag.split(":")[1]);
            h.setAgentName(agentName.split(":")[1]);
            h.setMacAddress(macAddress.split(":")[1]);
            h.setDeployState(deployState.split(":")[1]);
            h.setModemSerial(modemSerial.split(":")[1]);
            h.setModemModel(modemModel.split(":")[1]);
            h.setModemSignalLevel(modemSignal.split(":")[1]);
            h.setCameraStatus(camStatus.split(":")[1]);
            h.setScannerStatus(scannerStatus.split(":")[1]);
            h.setClientTimeStatus(clientTimeStatus.split(":")[1]);
            h.setClientUptime(Double.valueOf(uptime.split(":")[1]).longValue());
            h.setLoginUptime(Double.valueOf(loginUptime.split(":")[1]).longValue());
            h.setLongitude(getDouble(longitude.split(":")[1]));
            h.setLatitude(getDouble(latitude.split(":")[1]));
            h.setReceiptTimestamp(new Timestamp(new Date().getTime()));
            bsm.getDbService().create(h);
            hbStatusHelper.createOrUpdateHeartStatus(h);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            logger.error("Exception : ", e);
        }
    }

    private Double getDouble(String dbl) {
        if (dbl.equals("")) {
            return 0.0;
        }

        try {
            return Double.valueOf(dbl);
        } catch (NumberFormatException e) {
            logger.error("Exception while gettin double value ", e);
        }
        return 0.0;
    }

}
