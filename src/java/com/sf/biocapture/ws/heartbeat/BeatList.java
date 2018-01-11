package com.sf.biocapture.ws.heartbeat;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.sf.biocapture.entity.HeartBeat;

@XmlRootElement(name = "BEATS")
@XmlAccessorType(XmlAccessType.FIELD)
public class BeatList {
	
	@XmlElement(name = "ITEM")
	private List<HeartBeat> beats;

	public List<HeartBeat> getBeats() {
		return beats;
	}

	public void setBeats(List<HeartBeat> beats) {
		this.beats = beats;
	}
	
	

}
