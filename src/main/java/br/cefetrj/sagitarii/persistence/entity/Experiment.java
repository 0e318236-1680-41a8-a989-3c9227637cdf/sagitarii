package br.cefetrj.sagitarii.persistence.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import br.cefetrj.sagitarii.core.UserTableEntity;
import br.cefetrj.sagitarii.core.types.ExperimentStatus;
import br.cefetrj.sagitarii.misc.DateLibrary;
import br.cefetrj.sagitarii.misc.ZipUtil;
import br.cefetrj.sagitarii.persistence.services.RelationService;

@Entity
@Table(name="experiments", indexes = {
        @Index(columnList = "id_experiment", name = "exp_id_hndx"),
        @Index(columnList = "tagexec", name = "exp_tag_hndx")
}) 
public class Experiment {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_experiment")
	private int idExperiment;

	@Column(length=150, name="tagexec")
	private String tagExec;
	
	@Column(length=50)
	@Enumerated(EnumType.STRING)
	private ExperimentStatus status;

	@Column(name="activities_specs")
	private byte[] activitiesSpecs;	

	@Column(columnDefinition = "TEXT", name="image_data")
	private String imagePreviewData;	
	
	@Column
	private String description;	
	
	@ManyToOne
	@JoinColumn(name="id_workflow", foreignKey = @ForeignKey(name = "fk_exp_wf"))
	@Fetch(FetchMode.JOIN)
	private Workflow workflow;    

	@ManyToOne
	@JoinColumn(name="id_user", foreignKey = @ForeignKey(name = "fk_exp_user"))
	@Fetch(FetchMode.JOIN)
	private User owner;    
	
	@Column
	@Type(type="timestamp")
	private Date creationDate;
	
	@Column
	@Type(type="timestamp")
	private Date lastExecutionDate;

	@Column(length=15, name="elapsed_time")
	private String elapsedTime;

	@Column(length=15, name="serial_time")
	private String serialTime;
	
	@Column
	@Type(type="timestamp")
	private Date finishDateTime;
	
	@Column
	private Boolean availability = false;
	
	@Column
	@Type(type="timestamp")
	private Date alterationDate;

	@Column
	private Double speedUp = 0.0;
	
	@Column
	private Double parallelEfficiency = 0.0;

	@Column
	private int coresWorking;
	
    public Experiment() {
        UUID uuid = UUID.randomUUID();
        tagExec = uuid.toString().toUpperCase().substring(0,17);
		status = ExperimentStatus.STOPPED;
		setCreationDate( Calendar.getInstance().getTime() );
	}
    
    @Transient
    private List<Fragment> fragments = new ArrayList<Fragment>();
    
    @Transient
    public List<Relation> getUsedTables() {
    	List<Relation> tables = new ArrayList<Relation>();
    	for ( Fragment frag : fragments ) {
    		for ( Activity act : frag.getActivities() ) {
    			for( Relation rel : act.getInputRelations()  ) {
    				if ( !tables.contains( rel ) ) {
    					tables.add( rel );
    				}
    			}
    			if ( act.getOutputRelation() != null ) {
    				if ( !tables.contains(act.getOutputRelation() ) ) {
    					tables.add( act.getOutputRelation() );
    				}
    			}
    		}
    		
    	}
    	return tables;
    }
    
    
	public int getIdExperiment() {
		return idExperiment;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public void setIdExperiment(int idExperiment) {
		this.idExperiment = idExperiment;
	}

	public String getTagExec() {
		return tagExec;
	}

	public ExperimentStatus getStatus() {
		return status;
	}

	public void setStatus(ExperimentStatus status) {
		this.status = status;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastExecutionDate() {
		return lastExecutionDate;
	}

	public void setLastExecutionDate(Date lastExecutionDate) {
		this.lastExecutionDate = lastExecutionDate;
	}

	public Date getAlterationDate() {
		return alterationDate;
	}

	public void setAlterationDate(Date alterationDate) {
		this.alterationDate = alterationDate;
	}

	public String getActivitiesSpecs() {
		if ( activitiesSpecs != null ) {
			return ZipUtil.decompress( activitiesSpecs );
		} else {
			return "";
		}
	}

	public void setActivitiesSpecs(String activitiesSpecs) {
		this.activitiesSpecs = ZipUtil.compress( activitiesSpecs );
	}

	public String getImagePreviewData() {
		return imagePreviewData;
	}

	public void setImagePreviewData(String imagePreviewData) {
		this.imagePreviewData = imagePreviewData;
	}

	public List<Fragment> getFragments() {
		return fragments;
	}

	public void setFragments(List<Fragment> fragments) {
		for( Fragment frag : fragments ) {
			frag.setExperiment(this);
			frag.setIdExperiment( idExperiment );
		}
		this.fragments = fragments;
	}

	public Boolean getAvailability() {
		return availability;
	}

	public void setAvailability(Boolean availability) {
		this.availability = availability;
	}

	public Date getFinishDateTime() {
		return finishDateTime;
	}

	public void setFinishDateTime(Date finishDateTime) {
		this.finishDateTime = finishDateTime;
		elapsedTime = getStringVersionOfTime( getElapsedMillis() );
	}

	
	public long getElapsedMillis() {
		DateLibrary dl = DateLibrary.getInstance();
		dl.setTo( lastExecutionDate );
		Calendar cl = Calendar.getInstance();
		
		if ( finishDateTime != null ) {
			cl.setTime( finishDateTime );
		} else {
			cl.setTime( Calendar.getInstance().getTime() );
		}
		
		long millis = dl.getDiffMillisTo( cl ) ;

		if ( status == ExperimentStatus.STOPPED ) {
			millis = 0;
		}
		return millis;
	}
	
	private String getStringVersionOfTime( long millis ) {
		String time = String.format("%03d %02d:%02d:%02d", 
				TimeUnit.MILLISECONDS.toDays( millis ),
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), 
				TimeUnit.MILLISECONDS.toSeconds(millis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		
		return time;
	}
	
	public String getElapsedTime() {
		elapsedTime = getStringVersionOfTime( getElapsedMillis() );
		return elapsedTime;
	}
	
	public void updateMetrics() {
		getSerialTime();
		getParallelEfficiency();
	}
	
	public String getSerialTime() {
		serialTime = getStringVersionOfTime( getSerialTimeMillis() ); 
		return serialTime;
	}

	public double getParallelEfficiency() {
		if ( coresWorking == 0 ) return 0.0;
		getSpeedUp();
		if ( speedUp == 0.0 ) {
			parallelEfficiency = speedUp;
		} else {
			try {
				parallelEfficiency = speedUp / coresWorking;
			} catch ( Exception e ) { e.printStackTrace(); }
		}
		if ( parallelEfficiency.isNaN() ) parallelEfficiency = 0.0;
		return parallelEfficiency;
	}
	
	public double getSpeedUp() {
		speedUp = 0.0;
		try {
			long parallelTime = getElapsedMillis();
			long sequencialTime = getSerialTimeMillis();
			speedUp = (double)sequencialTime / (double)parallelTime;
		} catch ( Exception  e) {	}
		if ( speedUp.isNaN() ) speedUp = 0.0;
		return speedUp;
	}
	
	private long getSerialTimeMillis() {
		int qtd = 0;
		try {
			RelationService rs = new RelationService();
			Set<UserTableEntity> result = rs.genericFetchList("select sum(elapsed_millis) as sum from "
					+ "instances where id_fragment in ( select id_fragment from fragments where id_experiment = "+idExperiment+" )");
			
			List<UserTableEntity> res = new ArrayList<UserTableEntity> ( result );
			if ( res.size() > 0 ) {
				UserTableEntity ute = res.get(0);
				String sQtd = ute.getData("sum");
				qtd = Integer.valueOf( sQtd );
			}
		} catch ( Exception e ) {
			//
		}
		return qtd;
	}
	
	
	public void setCoresWorking(int coresWorking) {
		this.coresWorking = coresWorking;
	}
	
	public int getCoresWorking() {
		return coresWorking;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
}
