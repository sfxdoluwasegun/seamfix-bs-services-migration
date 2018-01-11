package com.sf.biocapture.app;

import com.sf.biocapture.ilog.ILog;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import com.sf.biocapture.entity.EnrollmentRef;
import com.sf.biocapture.entity.audit.AccessLog;
import com.sf.biocapture.entity.security.KMUser;
import com.sf.biocapture.sim.churn.ChurnMessage;

@Stateless
public class JmsSender extends BsClazz{

	@Resource
//        (lookup = "java:comp/DefaultJMSConnectionFactory")
	private ConnectionFactory connFactory;
	
	@Inject
	protected JMSContext ctx;

	@Resource(lookup = "java:/bio/queue/HeartBeat")
	private Queue beatQueue;

	@Resource(lookup = "java:/bio/queue/HelpMessage")
	private Queue helpQueue;

	@Resource(lookup = "java:/bio/queue/KycNodes")
	private Queue nodeQueue;

	@Resource(lookup = "java:/bio/queue/ChurnQueue")
	private Queue churnQueue;
	
	@Resource(lookup = "java:/bio/queue/SimSwap")
	private Queue swapQueue;
	
	@Resource(lookup = "java:/bio/queue/LoginQueue")
	private Queue loginQueue;
        
	@Resource(lookup = "java:/bio/queue/AccessLogQueue")
	private Queue accessLogQueue;
        
	@Resource(lookup = "java:/bio/queue/IntegrationLogQueue")
	private Queue integrationLogQueue;
	
	private Integer beatRate = 10;

	@PostConstruct
	protected void init(){
		logger.info("Initializing Application Session");
		beatRate = appProps.getInt("heartbeat.rate", 10);
	}
	
	public String queueUserLogin(KMUser user){
		Message msg = ctx.createObjectMessage(user);
		ctx.createProducer()
			.setDeliveryMode(DeliveryMode.PERSISTENT)
			.send(loginQueue, msg);
		return "User object queued successfully!!!";
	}
        
	public String queueLoginLog(AccessLog log){
		Message msg = ctx.createObjectMessage(log);
		ctx.createProducer()
			.setDeliveryMode(DeliveryMode.PERSISTENT)
			.send(accessLogQueue, msg);
		return "Login Log object queued successfully!!! --- " + log.getUsername();
	}

	public boolean queueKit(EnrollmentRef ref){
		Message msg = ctx.createObjectMessage(ref);
		ctx.createProducer()
			.setDeliveryMode(DeliveryMode.PERSISTENT)
			.send(nodeQueue, msg);
		return true;
	}

	public Integer queueBeat(Serializable beat){
		Message msg = ctx.createObjectMessage(beat);
		ctx.createProducer()
			.setDeliveryMode(DeliveryMode.PERSISTENT)
			.send(beatQueue, msg);
		return beatRate;
	}

	public String queueHelpMessage(String hmsg){
		Message msg = ctx.createObjectMessage(hmsg);
		ctx.createProducer()
			.setDeliveryMode(DeliveryMode.PERSISTENT)
			.send(helpQueue, msg);
		return "true";
	}
	
	public String queueIntegrationLog(ILog log){
		Message msg = ctx.createObjectMessage(log);
		ctx.createProducer()
			.setDeliveryMode(DeliveryMode.PERSISTENT)
			.send(integrationLogQueue, msg);
		return "Integration log queued successfully!!!";
	}

	public String queueChurn(ChurnMessage cmsg){
		ObjectMessage msg = ctx.createObjectMessage(cmsg);
		ctx.createProducer()
			.setDeliveryMode(DeliveryMode.PERSISTENT)
			.send(churnQueue, msg);
		return "true";
	}
	
	public String queueMsisdn(String msisdn){
		Message msg = ctx.createObjectMessage(msisdn);
		ctx.createProducer().setDeliveryMode(DeliveryMode.PERSISTENT).send(swapQueue, msg);
		return "Msisdn queued successfully";
	}

	@PreDestroy
	protected void clean(){
		
	}

	public static void main(String[] args) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy h:mm a");
		System.out.println(sdf.parse("04-08-2014 1:06 PM"));

	}

}
