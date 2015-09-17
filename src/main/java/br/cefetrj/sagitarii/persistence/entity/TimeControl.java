package br.cefetrj.sagitarii.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

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

	public TimeControl() {
		
	}
	
	public TimeControl(int idTimeControl, long averageMilis, int calculatedCount,	String hash, long totalAgeMilis, String content) {
		this.idTimeControl = idTimeControl;
		this.averageMilis = averageMilis;
		this.calculatedCount = calculatedCount;
		this.hash = hash;
		this.totalAgeMilis = totalAgeMilis;
		this.content = content;
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

	

}
