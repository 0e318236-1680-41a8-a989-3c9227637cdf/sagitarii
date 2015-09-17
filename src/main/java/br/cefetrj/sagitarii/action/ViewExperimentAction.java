
package br.cefetrj.sagitarii.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.filetransfer.FileImporter;
import br.cefetrj.sagitarii.core.filetransfer.FileSaver;
import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.CustomQuery;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.entity.Fragment;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.CustomQueryService;
import br.cefetrj.sagitarii.persistence.services.ExperimentService;

@Action (value = "viewExperiment", 
	results = { 
		@Result ( location = "viewExperiment.jsp", name = "ok"),
		@Result ( location="wfmanager", type="redirect", name="erro") 
	}, interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class ViewExperimentAction extends BasicActionClass {
	private int idExperiment;
	private Experiment experiment;
	private Set<CustomQuery> queries;
	private List<Activity> activities = new ArrayList<Activity>();
	private List<FileSaver> savers;
	private List<FileImporter> importers;
	
	public String execute () {
		
		try {
			experiment = new ExperimentService().previewExperiment( idExperiment );

			for ( Fragment frag : experiment.getFragments()  ) {
				activities.addAll( frag.getActivities() );
			}
			
			try {
				CustomQueryService cqs = new CustomQueryService();
				queries = cqs.getList( idExperiment );
			} catch ( NotFoundException ignored ) {
				//
			} 
			
		} catch (DatabaseConnectException e) {
			return "erro";
		} catch (NotFoundException e) {
			setMessageText("Cannot find this Experiment.");
			return "erro";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "ok";
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setIdExperiment(int idExperiment) {
		this.idExperiment = idExperiment;
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public Set<CustomQuery> getQueries() {
		return queries;
	}
	
	public List<FileSaver> getSavers() {
		return savers;
	}
	
	public List<FileImporter> getImporters() {
		return importers;
	}
	
}
