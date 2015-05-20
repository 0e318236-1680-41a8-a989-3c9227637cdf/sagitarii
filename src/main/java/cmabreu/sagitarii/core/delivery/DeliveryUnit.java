package cmabreu.sagitarii.core.delivery;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cmabreu.sagitarii.core.processor.Activation;
import cmabreu.sagitarii.core.processor.XMLParser;
import cmabreu.sagitarii.misc.DateLibrary;
import cmabreu.sagitarii.persistence.entity.Pipeline;

public class DeliveryUnit {
	private Pipeline pipeline;
	private List<Activation> activations;
	private String macAddress;
	private Date deliverTime;
	private Date receiveTime;
	
	public List<Activation> getActivations() {
		return activations;
	}
	
	public Pipeline getPipeline() {
		return pipeline;
	}

	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
		try {
			this.activations = new XMLParser().parseActivations( pipeline.getContent() );
		} catch (Exception e) { }
	}


	public String getMacAddress() {
		return macAddress;
	}
	
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public long getAgeMillis() {
		Date endTime = Calendar.getInstance().getTime();
		if( receiveTime != null ) {
			endTime = receiveTime;
		} 
		DateLibrary.getInstance().setTo( deliverTime );
		Calendar data = Calendar.getInstance();
		data.setTime(endTime);
		long milis = DateLibrary.getInstance().getDiferencaMilisAte( data );
		return milis;  
	}

	public String getAgeTime() {
		long millis = getAgeMillis();
		String retorno = String.format("%02d:%02d:%02d", 
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), 
				TimeUnit.MILLISECONDS.toSeconds(millis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))); 	
		return retorno;
	}
	
	public Date getDeliverTime() {
		return deliverTime;
	}

	public void setDeliverTime(Date deliverTime) {
		this.deliverTime = deliverTime;
	}

	public Date getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	
	
}
