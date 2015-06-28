package cmabreu.sagitarii.persistence.entity;

import java.io.ByteArrayInputStream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import cmabreu.sagitarii.core.ClustersManager;
import cmabreu.sagitarii.misc.ProgressAwareInputStream;
import cmabreu.sagitarii.misc.ProgressListener;

@Entity
@Table(name="files", indexes = {
        @Index(columnList = "id_file", name = "file_id_hndx"),
        @Index(columnList = "id_experiment", name = "file_experiment_hndx"),
        @Index(columnList = "filename", name = "file_name_hndx")
})
public class File {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_file")
	private int idFile;
	
	@ManyToOne
	@JoinColumn(name="id_activity", foreignKey = @ForeignKey(name = "fk_file_activity"))
	@Fetch(FetchMode.JOIN)
	private Activity activity;
	
	@ManyToOne
	@JoinColumn(name="id_instance", foreignKey = @ForeignKey(name = "fk_file_instance"))
	@Fetch(FetchMode.JOIN)
	private Instance instance;
	
	@ManyToOne
	@JoinColumn(name="id_experiment", foreignKey = @ForeignKey(name = "fk_file_exp"))
	@Fetch(FetchMode.JOIN)
	private Experiment experiment;
	
	@Column(length=250)
	private String fileName;

	@Lob
	private byte[] file;
	
	public int getIdFile() {
		return idFile;
	}
	
	@Transient
	public ProgressAwareInputStream getDownloadStream( String macAddress ) {
		if ( file == null ) {
			return null;
		}
        ProgressAwareInputStream pais = new ProgressAwareInputStream( new ByteArrayInputStream( file ), 
        		file.length, fileName );
        ProgressListener pl = new ProgressListener( );
        pais.setOnProgressListener( pl );
        ClustersManager.getInstance().addProgressListener( macAddress, pl );
        return pais; 
	}

	public void setIdFile(int idFile) {
		this.idFile = idFile;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}


}
