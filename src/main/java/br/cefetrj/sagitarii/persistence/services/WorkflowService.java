package br.cefetrj.sagitarii.persistence.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.entity.Fragment;
import br.cefetrj.sagitarii.persistence.entity.Relation;
import br.cefetrj.sagitarii.persistence.entity.Workflow;
import br.cefetrj.sagitarii.persistence.exceptions.DatabaseConnectException;
import br.cefetrj.sagitarii.persistence.exceptions.DeleteException;
import br.cefetrj.sagitarii.persistence.exceptions.InsertException;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.exceptions.UpdateException;
import br.cefetrj.sagitarii.persistence.repository.WorkflowRepository;

public class WorkflowService { 
	private WorkflowRepository rep;
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	public WorkflowService() throws DatabaseConnectException {
		this.rep = new WorkflowRepository();
	}
	

	public void newTransaction() {
		rep.newTransaction();
	}
	
	/**
	 * Inclui um novo workflow no banco de dados.
	 * 
	 */
	public void insertWorkflow(Workflow workflow) throws InsertException {
		logger.debug("inserting workflow " + workflow.getTag() );
		rep.insertWorkflow( workflow );
	}
	
	
	/**
	 * Atualiza um workflow no banco de dados.
	 */
	public void updateWorkflow(Workflow workflow) throws UpdateException {
		Workflow oldWorkflow;
		try {
			oldWorkflow = rep.getWorkflow( workflow.getIdWorkflow() );
		} catch (NotFoundException e) {
			logger.debug( e.getMessage() );
			throw new UpdateException( e.getMessage() );
		}
		
		oldWorkflow.setDescription( workflow.getDescription() );
		oldWorkflow.setTag( workflow.getTag() );

		rep.newTransaction();
		rep.updateWorkflow(oldWorkflow);
	}	

	
	/**
	 * Atualiza a definicao de fluxo de um workflow no banco de dados.
	 * 
	 */
	public void updateWorkflowActivities(Workflow workflow) throws UpdateException {
		Workflow oldWorkflow;
		try {
			oldWorkflow = rep.getWorkflow( workflow.getIdWorkflow() );
		} catch (NotFoundException e) {
			logger.debug( e.getMessage() );
			throw new UpdateException( e.getMessage() );
		}
		
		oldWorkflow.setActivitiesSpecs( workflow.getActivitiesSpecs() );
		oldWorkflow.setImagePreviewData( workflow.getImagePreviewData() );

		rep.newTransaction();
		rep.updateWorkflow(oldWorkflow);
	}	

	
	/**
	 * Retorna um objeto workflow do banco de dados usando o seu ID como critério de busca.
	 */
	public Workflow getWorkflow(int idWorkflow) throws NotFoundException{
		return rep.getWorkflow(idWorkflow);
	}


	public String getXML(int idWorkflow) throws Exception {
		Workflow wf = rep.getWorkflow(idWorkflow);
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<workflow tag=\""+wf.getTag()+"\">\n");
		
		sb.append("<specification>\n");
		//sb.append( wf.getActivitiesSpecs() );
		sb.append("</specification>\n");
		
		for( Experiment exp : wf.getExperiments() ) {
			exp = new ExperimentService().previewExperiment( exp.getIdExperiment() );
			sb.append("<experiment tag=\""+exp.getTagExec()+"\">\n");
			
			sb.append("<fragments>\n");
			for( Fragment frag : exp.getFragments() ) {
				sb.append("<fragment executionOrder=\""+ frag.getIndexOrder() +"\">\n");
				for ( Activity act : frag.getActivities() ) {
					sb.append("<activity type=\""+act.getType()+"\" executor=\""+act.getExecutorAlias()+"\" name=\""+act.getTag()+"\">\n");
					
					sb.append("<inputTables>\n");
					for( Relation rel : act.getInputRelations() ){
						sb.append("<table name=\""+ rel.getName() +"\"/>\n");
					}
					sb.append("</inputTables>\n");
					
					sb.append("<outputTable name=\""+  act.getOutputRelation().getName() +"\" />\n");
					
					sb.append("<nextActivities>");
					for( Activity next : act.getNextActivities() ) {
						sb.append("<activity type=\""+next.getType()+"\" executor=\""+next.getExecutorAlias()+"\" name=\""+next.getTag()+"\" />\n");
					}
					sb.append("</nextActivities>");

					sb.append("<previousActivities>");
					for( Activity previous : act.getPreviousActivities() ) {
						sb.append("<activity type=\""+previous.getType()+"\" executor=\""+previous.getExecutorAlias()+"\" name=\""+previous.getTag()+"\" />\n");
					}
					sb.append("</previousActivities>");
					
					sb.append("</activity>\n");
				}
				sb.append("</fragment>\n");
			}
			sb.append("</fragments>\n");
			
			sb.append("<tables>\n");
			for( Relation rel : exp.getUsedTables() ){
				sb.append("<table name=\""+ rel.getName() +"\">\n");
				sb.append("<schema>\n");
				sb.append( rel.getSchema() );
				sb.append("</schema>\n");
				sb.append("</table>\n");
			}
			sb.append("</tables>\n");
			
			sb.append("</experiment>\n");
		}
		
		
		
		
		sb.append("</workflow>\n\n");
		return sb.toString();
	}

	
	/**
	 * Retorna um objeto workflow do banco de dados usando a sua TAG como critério de busca.
	 * 
	 */
	public Workflow getWorkflow(String tag) throws NotFoundException{
		return  rep.getWorkflow(tag);
	}

	
	
	/**
	 * Exclui um workflow do banco de dados.
	 */
	public Workflow deleteWorkflow( int idWorkflow ) throws DeleteException {
		Workflow workflow = null;
		try {
			workflow = rep.getWorkflow(idWorkflow);
			
			if ( workflow.getExperiments().size() > 0 ) {
				throw new DeleteException("This workflow still have experiments");
			}
			
			rep.newTransaction();
			rep.deleteWorkflow(workflow);
		} catch (NotFoundException e) {
			logger.error( e.getMessage() );
			throw new DeleteException( e.getMessage() );
		}
		return workflow;
	}

	
	public Workflow getPendent() throws NotFoundException {
		return rep.getPendent();
	}
	
	
	/**
	 * Retorna uma lista de workflows.
	 * 
	 */
	public List<Workflow> getList() throws NotFoundException {
		logger.debug( "retrieving workflow list..." );  
		List<Workflow> preList = rep.getList();
		logger.debug( "done." );  
		return preList;	
	}
	
}