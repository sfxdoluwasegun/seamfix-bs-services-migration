package com.sf.biocapture.ilog.listener;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ds.AccessDS;
import com.sf.biocapture.entity.audit.AgilityIntegrationLog;
import com.sf.biocapture.ilog.ILog;
import com.sf.biocapture.ilog.backup.LogRunner;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.PostConstruct;
import nw.orm.core.exception.NwormQueryException;

/**
 *
 * @author Nnanna
 * @since 7 Jun 2017, 14:16:57
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/bio/queue/IntegrationLogQueue")})
public class IntegrationLogListener extends BsClazz implements MessageListener {
    
    @Inject
    protected AccessDS accessDS;
    
    private String backupPathSettingName;
    
    @Override
    public void onMessage(Message msg) {
        try {
            ObjectMessage om = (ObjectMessage) msg;
            ILog log = (ILog) om.getObject();
            backupLog(log);
        } catch (JMSException e) {
            logger.error("Exception ", e);
        }
    }
    
    private void backupLog(ILog log) {
        String backupPath = accessDS.getSettingValue(backupPathSettingName, "ilog");
        String year = "", month = "", day = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String delimetered[] = sdf.format(new Date()).split("-");
        year = delimetered[0];
        month = delimetered[1];
        day = delimetered[2];
        
        String filePath = backupPath + File.separator + log.getRequestType() + File.separator + year + File.separator + month + File.separator + day;
        String transactionId = "";
        File backupFile = null;
        if (log.getEntity() instanceof AgilityIntegrationLog) {
            AgilityIntegrationLog agilityIntegrationLog = (AgilityIntegrationLog) log.getEntity();
            if (agilityIntegrationLog.getMsisdn() != null && !agilityIntegrationLog.getMsisdn().isEmpty()) {
                transactionId = agilityIntegrationLog.getMsisdn();
            } else if (agilityIntegrationLog.getSimSerial() != null && !agilityIntegrationLog.getSimSerial().isEmpty()) {
                transactionId = agilityIntegrationLog.getSimSerial();
            } else {
                transactionId = "";
            }
            if (!transactionId.isEmpty()) {
                transactionId += "-";
            }
            transactionId += new Date().getTime();
            filePath += File.separator + transactionId;
            agilityIntegrationLog.setTransactionId(transactionId);
            backupFile = newBackupFile(filePath);
            if (backupFile != null) {
                agilityIntegrationLog.setBackupPath(backupFile.getAbsolutePath());
            }
            saveLog(agilityIntegrationLog);
        } else {
            saveLog(log.getEntity());
        }

        //write payloads to file
        new Thread(new LogRunner(log, backupFile)).start();
    }
    
    private File newBackupFile(String path) {
        File backupPathFile = new File(path);
        if (!backupPathFile.exists()) {
            backupPathFile.mkdirs();
        }
        return backupPathFile;
    }
    
    private void saveLog(Object entity) {
        if (appProps.getBool("create-agility-integration-log", Boolean.TRUE)) {
            try {
                accessDS.getDbService().create(entity);
            } catch (NwormQueryException e) {
                logger.error("", e);
            }
        } else {
            logger.debug("Integration was not saved to the database. Action was disabled");
        }
    }
    
    @PostConstruct
    private void init() {
        backupPathSettingName = appProps.getProperty("INTEGRATION_LOG_BACKUP_PATH", "INTEGRATION_LOG_BACKUP_PATH");
    }
}
