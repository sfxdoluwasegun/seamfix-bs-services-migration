package com.sf.biocapture.app;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import nw.commons.AppProperties;
import nw.commons.NeemClazz;
import nw.commons.cache.PropertiesCache;

public abstract class BsClazz extends NeemClazz {
	private AppProperties nationalityProps;
        private AppProperties stateProps;
        
	public BsClazz() {
		setTargetPropertyFilename("bs.props");
		appProps = PropertiesCache.getPropertyFile(getTargetPropertyFilename());
                nationalityProps = PropertiesCache.getPropertyFile("nationality_country_map.properties");
                stateProps = PropertiesCache.getPropertyFile("state_map.properties");
	}

    public AppProperties getNationalityProps() {
        return nationalityProps;
    }

    public void setNationalityProps(AppProperties nationalityProps) {
        this.nationalityProps = nationalityProps;
    }

    public AppProperties getStateProps() {
        return stateProps;
    }

    public void setStateProps(AppProperties stateProps) {
        this.stateProps = stateProps;
    }
    
    @SuppressWarnings("rawtypes")
    public <T> String marshalObject(Class clazz, T obj){
    	try {
    		JAXBContext context = JAXBContext.newInstance(clazz);
    		Marshaller m = context.createMarshaller();
    		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

    		//write response to output stream
    		OutputStream os = new ByteArrayOutputStream();
    		m.marshal(obj, os);                        
    		return os.toString();
    	} catch (JAXBException e) {
    		logger.error("Exception thrown in marshalling object to string:", e);
    	}
    	return null;
    }
}