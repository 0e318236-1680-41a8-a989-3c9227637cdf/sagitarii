package br.cefetrj.sagitarii.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import br.cefetrj.sagitarii.core.types.LogType;

@Entity
@Table(name="logs", indexes = {
        @Index(columnList = "node_mac", name = "log_node_hndx")
})
public class LogEntry {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_log")
	private int idLog;
	
	@Column(length=20, name="node_mac")
	private String node;

	@Column(length=8, name="activity_serial")
	private String activitySerial;
	
	@Column(name="date_time")
	@Type(type="timestamp")
	private Date dateTime;	
	
	@Column(columnDefinition = "TEXT", name="log")
	private String log;
	
	@Column(length=50)
	@Enumerated(EnumType.STRING)
	private LogType type;
	
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public int getIdLog() {
		return idLog;
	}

	public void setIdLog(int idLog) {
		this.idLog = idLog;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public Date getDateTime() {
		return dateTime;
	}
	
	public LogType getType() {
		return type;
	}
	
	public void setType(LogType type) {
		this.type = type;
	}

	public void setActivitySerial(String activitySerial) {
		this.activitySerial = activitySerial;
	}
	
	public String getActivitySerial() {
		return activitySerial;
	}
	
}
