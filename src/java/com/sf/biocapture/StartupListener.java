package com.sf.biocapture;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import nw.orm.core.service.Nworm;

import com.sf.biocapture.app.BsClazz;

@Startup
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class StartupListener extends BsClazz{	

    @PostConstruct
    public void start(){
        logger.debug("Waking up Biocapture Services");
        Properties props = new Properties();
        props.setProperty("config.name", "hibernate.cfg.xml");
        Nworm dbService = Nworm.getInstance();
        dbService.enableJTA();
        dbService.enableSessionByContext();
        logger.debug("Biocapture Services is awake");

    }

    @PreDestroy
    public void stop() throws InterruptedException{
        Nworm.getInstance().closeFactory();
        logger.debug("Goodbye Bio Services");
            
    }
    
    public static void main(String[] args) {
            System.out.println(Boolean.valueOf("true"));
    }

}
