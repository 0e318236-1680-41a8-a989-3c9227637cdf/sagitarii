
package cmabreu.sagitarii.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.sockets.FileImporter;
import cmabreu.sagitarii.core.sockets.FileReceiverManager;
import cmabreu.sagitarii.core.sockets.FileSaver;
import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.CustomQuery;
import cmabreu.sagitarii.persistence.entity.Experiment;
import cmabreu.sagitarii.persistence.entity.Fragment;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.services.CustomQueryService;
import cmabreu.sagitarii.persistence.services.ExperimentService;

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
			
			try {
				savers = FileReceiverManager.getInstance().getSaversByExperiment( experiment.getTagExec() );
				importers = FileReceiverManager.getInstance().getImportersBySession( experiment.getTagExec() );
			} catch( Exception ignored ) {
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
