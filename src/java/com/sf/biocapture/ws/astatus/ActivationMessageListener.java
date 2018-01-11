/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.astatus;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.app.JmsSender;
import com.sf.biocapture.common.GenericException;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.entity.audit.AgilityIntegrationLog;
import com.sf.biocapture.entity.audit.BfpSyncLog;
import com.sf.biocapture.entity.enums.ActivationStatusEnum;
import com.sf.biocapture.entity.enums.BfpSyncStatusEnum;
import com.sf.biocapture.entity.enums.SettingsEnum;
import com.sf.biocapture.ilog.ILog;
import com.sf.biocapture.ilog.agl.AgilityLog;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import nw.orm.core.exception.NwormQueryException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

/**
 *
 * @author Marcel
 * @since May 26, 2017
 */
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ActivationMessageListener extends BsClazz implements MessageListener {

    private ActivationDS activationDS;

    private AccessDS accessDS;

    private JmsSender jmsSender;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    public ActivationMessageListener() {
    }

    public ActivationMessageListener(ActivationDS activationDS, AccessDS accessDS, JmsSender jmsSender) {
        this.activationDS = activationDS;
        this.accessDS = accessDS;
        this.jmsSender = jmsSender;
    }

    @Override
    public void onMessage(Message message) {
        logger.debug("incoming message");
        try {
            TextMessage om = (TextMessage) message;
            ActivationMessage am = unmarshal(ActivationMessage.class, om.getText());
            logger.debug("activation message pojo: " + am);
            saveMessage(am, om.getText());
        } catch (JMSException ex) {
            logger.error("", ex);
        } catch (ParseException ex) {
            logger.error("", ex);
        } catch (GenericException ex) {
            logger.error("", ex);
        }
        logger.debug("done processing message on arrival");
    }

    protected ILog logActivity(String requestXml, String responseXml, AgilityIntegrationLog log) {
        AgilityLog agilityLog = new AgilityLog();
        agilityLog.setRequestType(log.getRequestType());
        agilityLog.setAgilityIntegrationLog(log);
        agilityLog.setRequestPayload(requestXml);
        agilityLog.setResponsePayload(responseXml);
        return agilityLog;
    }

    public void saveMessage(ActivationMessage activationMessage, String queueItemMessage) throws ParseException, GenericException {
        try {
            String msisdn = activationMessage.getMsisdn();
            String simSerial = activationMessage.getSimSerial() != null ? activationMessage.getSimSerial().trim() : activationMessage.getSimSerial();
            AgilityIntegrationLog agilityIntegrationLog = new AgilityIntegrationLog();
            agilityIntegrationLog.setMsisdn(msisdn);
            agilityIntegrationLog.setRequestType(activationMessage.getActivityType()); //SIM_ACTIVATION_NOTIFICATION as returned by agility
            agilityIntegrationLog.setSimSerial(simSerial);
            agilityIntegrationLog.setTransactionId(activationMessage.getTransactionID());

            AgilityLog agilityLog = new AgilityLog();
            agilityLog.setRequestType(activationMessage.getActivityType());
            agilityLog.setAgilityIntegrationLog(agilityIntegrationLog);
            agilityLog.setRequestPayload("");
            agilityLog.setResponsePayload(queueItemMessage);
            jmsSender.queueIntegrationLog(agilityLog);

            BfpSyncLog bsl = activationDS.getBfpSyncLog(activationMessage.getTransactionID(), msisdn, simSerial);
            if (bsl == null) {
                /**
                 * this indicates that this registration was not carried out on
                 * our system thus honour now.
                 */
                logger.debug("Unrecognized registration: " + activationMessage);
                bsl = new BfpSyncLog();
                bsl.setBfpSyncStatusEnum(BfpSyncStatusEnum.SUCCESS);
                bsl.setUniqueId(activationMessage.getTransactionID());
                bsl.setMsisdn(msisdn);
                bsl.setSimSerial(simSerial);
                bsl.setActivationDate(sdf.parse(activationMessage.getActivationDate()));
                ActivationStatusEnum activationStatusEnum = translateStatus(activationMessage.getActivationStatus());
                if (activationStatusEnum == null) {
                    throw new GenericException("Unrecognized Activation Status");
                }
                bsl.setActivationStatusEnum(activationStatusEnum);
                activationDS.save(bsl);
                logger.debug("Created Unrecognized registration: " + activationMessage.getTransactionID());
            } else {
                /**
                 *
                 */
                bsl.setActivationDate(sdf.parse(activationMessage.getActivationDate()));
                ActivationStatusEnum activationStatusEnum = translateStatus(activationMessage.getActivationStatus());
                if (activationStatusEnum == null) {
                    throw new GenericException("Unrecognized Activation Status");
                }
                bsl.setActivationStatusEnum(activationStatusEnum);
                activationDS.update(bsl);
            }
        } catch (NwormQueryException e) {
            logger.error("", e);
        }
    }

    @SuppressWarnings("PMD")
    public String log(Object entity) {
        String result = "";
        if (entity == null) {
            return null;
        }
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            result = ow.writeValueAsString(entity);
        } catch (Exception e) {
            logger.error("", e);
        }
        return result;
    }

    public <T extends Object> T unmarshal(Class<T> clazz, String xml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            return clazz.cast(jaxbUnmarshaller.unmarshal(reader));
        } catch (JAXBException e) {
            logger.error("", e);
        }

        return null;
    }

    private ActivationStatusEnum translateStatus(String status) {
        String settingsValue = accessDS.getSettingValue(SettingsEnum.ACTIVATION_STATUS_MAPPER);
        if (settingsValue != null && !settingsValue.isEmpty()) {
            logger.debug("Retrieved status: " + status + ", activation status mapper: " + settingsValue);
            String mapper[] = settingsValue.split(",");
            for (String key : mapper) {
                String values[] = key.split(":");
                String enumName = values[0].trim();
                String agilityStatus = values[1].trim();
                if (agilityStatus.equalsIgnoreCase(status)) {
                    return ActivationStatusEnum.valueOf(enumName);
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("FAILED_ACTIVATION: " + ActivationStatusEnum.valueOf("FAILED_ACTIVATION"));
        System.out.println(new ActivationMessageListener().unmarshal(ActivationMessage.class, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<Notification xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:thir=\"http://www.example.org/ThirdParty/\">\n"
                + "          <ActivityType>SIM_ACTIVATION_NOTIFICATION</ActivityType>\n"
                + "          <ActivityMSISDN>8142050242</ActivityMSISDN>\n"
                + "          <ActivationDate>20170608 15:06:10</ActivationDate>\n"
                + "          <SimSerial>NA</SimSerial>\n"
                + "          <TransactionID>WIN-007-LAG-ETI-76211-1496930532964</TransactionID>\n"
                + "          <ActivationStatus>ACTIVATED</ActivationStatus>\n"
                + "        </Notification>"));
    }
}
