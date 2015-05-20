
package cmabreu.sagitarii.action;

import java.util.HashSet;
import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.UserTableEntity;
import cmabreu.sagitarii.persistence.entity.Consumption;
import cmabreu.sagitarii.persistence.entity.Pipeline;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.services.ConsumptionService;
import cmabreu.sagitarii.persistence.services.PipelineService;
import cmabreu.sagitarii.persistence.services.RelationService;

@Action (value = "viewPipeline", results = { @Result (location = "viewPipeline.jsp", name = "ok"),
		@Result (type="redirect", location = "${destiny}", name = "error")
}, interceptorRefs= { @InterceptorRef("seguranca")} ) 

@ParentPackage("default")
public class ViewPipelineAction extends BasicActionClass {
	private Integer idPipeline;
	private Pipeline pipeline;
	private String destiny;
	private String tableName;
	private int idExperiment;
	private Set<UserTableEntity> consumptions;
	private Set<UserTableEntity> products;
	
	public String execute () {
		destiny = "viewTableData?idExperiment=" + idExperiment + "&tableName=" + tableName;

		
		try {
			
			PipelineService ps = new PipelineService();
			ConsumptionService cs = new ConsumptionService();
			RelationService rs = new RelationService();

			pipeline = ps.getPipeline( idPipeline );
			products = rs.getGeneratedData(tableName, idPipeline, idExperiment);

			// we must pass a valid not null Consumption list to getConsumptionsData
			try {
				pipeline.setConsumptions( cs.getList(idPipeline) );
			} catch ( NotFoundException ignored) {	
				pipeline.setConsumptions( new HashSet<Consumption>() );
			}
			consumptions = rs.getConsumptionsData( pipeline.getConsumptions(), idExperiment );
			
		} catch ( NotFoundException  e) {
			setMessageText("Pipeline not found.");
			return "error";
		} catch (Exception e) {
			e.printStackTrace();
			setMessageText("Error: " + e.getMessage() );
		} 
		
		return "ok";
	}


	public Pipeline getPipeline() {
		return pipeline;
	}

	public void setIdPipeline(Integer idPipeline) {
		this.idPipeline = idPipeline;
	}

	public String getDestiny() {
		return destiny;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setIdExperiment(int idExperiment) {
		this.idExperiment = idExperiment;
	}


	public String getTableName() {
		return tableName;
	}


	public int getIdExperiment() {
		return idExperiment;
	}

	public Set<UserTableEntity> getProducts() {
		return products;
	}
	
	public Set<UserTableEntity> getConsumptions() {
		return consumptions;
	}
	
}
