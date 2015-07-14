package cmabreu.sagitarii.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import cmabreu.sagitarii.core.types.ActivityType;
import cmabreu.sagitarii.core.types.InstanceStatus;

@SuppressWarnings("serial")
@Entity
@Table(name="instances", indexes = {
        @Index(columnList = "id_instance", name = "instance_id_hndx"),
        @Index(columnList = "serial", name = "instance_serial_hndx")
}) 
public class Instance implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_instance")
	private int idInstance;
	
	@Column(name="start_date_time")
	@Type(type="timestamp")
	private Date startDateTime;

	@Column(name="finish_date_time")
	@Type(type="timestamp")
	private Date finishDateTime;

	@Column(name="id_fragment")
	private int idFragment;
	
	@Column(length=15)
	private String serial;

	/*
	@Column(length=250)
	private String executorAlias;
	*/
	
    @OneToMany(orphanRemoval=true,  mappedBy="instance", fetch = FetchType.LAZY)
    @OrderBy("id_table, id_row ASC")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<Consumption> consumptions = new HashSet<Consumption>();
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@Column(name="qtd_activations")
	private int qtdActivations;

	@Column(length=50)
	@Enumerated(EnumType.STRING)
	private InstanceStatus status;	
	
	@Column(length=50)
	@Enumerated(EnumType.STRING)
	private ActivityType type;	
	
	@Transient
	String finishedActivities;
	
	public void decreaseQtdActivations() {
		if ( qtdActivations > 0 ) {
			qtdActivations--;
		}
		if ( qtdActivations == 0 ) {
			status = InstanceStatus.FINISHED;
		}
	}

	public Instance() {
        UUID uuid = UUID.randomUUID();
        serial = uuid.toString().toUpperCase().substring(0,15);
        status = InstanceStatus.PIPELINED;
	}
	
	public void addConsumption( Consumption consumption) {
		consumption.setInstance( this );
		consumptions.add( consumption );
	}
	
	public void setQtdActivations(int qtdActivations) {
		this.qtdActivations = qtdActivations;
	}
	
	public int getQtdActivations() {
		return qtdActivations;
	}

	public int getIdInstance() {
		return idInstance;
	}

	public void setIdInstance(int idInstance) {
		this.idInstance = idInstance;
	}

	public int getIdFragment() {
		return idFragment;
	}

	public void setIdFragment(int idFragment) {
		this.idFragment = idFragment;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ActivityType getType() {
		return type;
	}

	public void setType(ActivityType type) {
		this.type = type;
	}

	public String getFinishedActivities() {
		return finishedActivities;
	}

	public void setFinishedActivities(String finishedActivities) {
		this.finishedActivities = finishedActivities;
	}

	public InstanceStatus getStatus() {
		return status;
	}

	public void setStatus(InstanceStatus status) {
		this.status = status;
	}

	/*
	public String getExecutorAlias--XX() {
		return executorAlias;
	}

	public void setExecutorAlias--XX(String executorAlias) {
		this.executorAlias = executorAlias;
	}
	*/

	public Set<Consumption> getConsumptions() {
		return consumptions;
	}

	public void setConsumptions(Set<Consumption> consumptions) {
		for ( Consumption con : consumptions ) {
			con.setInstance( this );
		}
		this.consumptions = consumptions;
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Date getFinishDateTime() {
		return finishDateTime;
	}

	public void setFinishDateTime(Date finishDateTime) {
		this.finishDateTime = finishDateTime;
	}

}
