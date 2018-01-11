package com.sf.biocapture.sim.churn;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.sf.biocapture.app.JmsSender;
import com.sf.biocapture.app.BioCache;
import com.sf.biocapture.app.BsClazz;

@Stateless
public class ChurnProcessor extends BsClazz{
	
	private File churnDumpLocation;
	private File churnProcessedDumpLocation;
	private String msisdnRegexPattern;
	
	@Inject
	private JmsSender jmsSender;
	
	@Inject
	private BioCache cache;
	
	@PostConstruct
	protected void init(){
		churnDumpLocation = new File(appProps.getProperty("churn-dump-location", "C:/fix/_bio/churn"));
		churnProcessedDumpLocation = new File(appProps.getProperty("churn-processed-dump-location", "C:/fix/_bio/processed_churn"));
		if(!churnProcessedDumpLocation.exists()){
			churnProcessedDumpLocation.mkdirs();
		}
		msisdnRegexPattern = appProps.getProperty("msisdn-regex-patter", "^(?!(\\d)\\1+\\b|1234567890)\\d{10,13}$");
		
		logger.debug("Initialized Churn Processor");
	}
	
	public void processChurnedLines(){
		if(!churnDumpLocation.exists() || !churnDumpLocation.canRead()){
			logger.debug("Churn Dump Location not found or not readable @ " + churnDumpLocation.getAbsolutePath());
			return;
		}
		selectFiles();
	}
	
	private void selectFiles(){
		File[] churnFiles = churnDumpLocation.listFiles();
		if(churnFiles.length > 0){
			for(File file: churnFiles){
				logger.debug("Churn File Selected: " + file);
				readLines(file);
				try {
					FileUtils.moveFile(file, new File(churnProcessedDumpLocation + "/" + file.getName()));
					logger.debug("Churn File was hopefully moved: " + file);
				} catch (IOException e) {
					logger.error("Exception ", e);
				}
			}
		}
	}
	
	private void readLines(File file){
		try {
			LineIterator li = FileUtils.lineIterator(file, "UTF-8");
			ChurnMessage cm = new ChurnMessage();
			cm.setRefNumber(file.getName());
			while(li.hasNext()){
				String line = li.nextLine();
				String msisdn = extractMsisdn(line, file.getName());
				if(msisdn != null && !cm.addIfNotFull(msisdn)){
					jmsSender.queueChurn(cm);
					cm = new ChurnMessage();
					cm.setRefNumber(file.getName());
				}
			}
			li.close();
		} catch (IOException e) {
			logger.error("Exception ", e);
		}
	}
	
	private String extractMsisdn(String line, String ref){
		String[] lineItems = line.split(",");
		for(String item: lineItems){
			if(item.matches(msisdnRegexPattern) && cache.getItem(ref + "_CHURN_" + item) == null){
				cache.setItem(ref + "_CHURN_" + item, "0", 86400);
				return item;
			}
		}
		return null;
	}

}
