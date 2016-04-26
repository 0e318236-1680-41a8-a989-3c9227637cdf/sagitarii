package br.cefetrj.sagitarii.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import br.cefetrj.sagitarii.core.statistics.Accumulator;

@Entity
@Table(name="timecontrol", indexes = {
        @Index(columnList = "id_tc", name = "id_tc_hndx"),
        @Index(columnList = "hash", name = "hash_hndx")
})
public class TimeControl {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_tc")
	private int idTimeControl;

	@Column(name="average")
	private long averageMilis = 0;
	
	@Column(name="calculated")
	private int calculatedCount = 0;
	
	@Column(length=250)
	private String hash;
	
	@Column(length=250)
	private String content;

	@Column(name="total")
	private long totalAgeMilis = 0;

	@Column(name="minAge")
	private long minAgeMilis = 0;
	
	@Column(name="maxAge")
	private long maxAgeMilis = 0;
	
	public TimeControl() {
		// Keep default constructor or Hibernate will raise an Exception
	}
	
	public TimeControl( Accumulator ac ) {
		this.idTimeControl = ac.getIdTimeControl();
		this.averageMilis = ac.getAverageMillis();
		this.calculatedCount = ac.getCalculatedCount();
		this.hash = ac.getHash();
		this.totalAgeMilis = ac.getTotalAgeMillis();
		this.content = ac.getContent();
		this.minAgeMilis = ac.getMinAgeMillis();
		this.maxAgeMilis = ac.getMaxAgeMillis();
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getIdTimeControl() {
		return idTimeControl;
	}

	public void setIdTimeControl(int idTimeControl) {
		this.idTimeControl = idTimeControl;
	}

	public long getAverageMilis() {
		return averageMilis;
	}
	
	public void setAverageMilis(long averageMilis) {
		this.averageMilis = averageMilis;
	}
	
	public int getCalculatedCount() {
		return calculatedCount;
	}
	
	public void setCalculatedCount(int calculatedCount) {
		this.calculatedCount = calculatedCount;
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public long getTotalAgeMilis() {
		return totalAgeMilis;
	}
	
	public void setTotalAgeMilis(long totalAgeMilis) {
		this.totalAgeMilis = totalAgeMilis;
	}

	public long getMinAgeMilis() {
		return minAgeMilis;
	}

	public void setMinAgeMilis(long minAgeMilis) {
		this.minAgeMilis = minAgeMilis;
	}

	public long getMaxAgeMilis() {
		return maxAgeMilis;
	}

	public void setMaxAgeMilis(long maxAgeMilis) {
		this.maxAgeMilis = maxAgeMilis;
	}

	

}
