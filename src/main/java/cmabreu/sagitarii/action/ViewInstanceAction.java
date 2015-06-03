
package cmabreu.sagitarii.action;

import java.util.HashSet;
import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import cmabreu.sagitarii.core.UserTableEntity;
import cmabreu.sagitarii.persistence.entity.Consumption;
import cmabreu.sagitarii.persistence.entity.Instance;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.services.ConsumptionService;
import cmabreu.sagitarii.persistence.services.InstanceService;
import cmabreu.sagitarii.persistence.services.RelationService;

@Action (value = "viewInstance", results = { @Result (location = "viewInstance.jsp", name = "ok"),
		@Result (type="redirect", location = "${destiny}", name = "error")
}, interceptorRefs= { @InterceptorRef("seguranca")} ) 

@ParentPackage("default")
public class ViewInstanceAction extends BasicActionClass {
	private Integer idInstance;
	private Instance instance;
	private String destiny;
	private String tableName;
	private int idExperiment;
	private Set<UserTableEntity> consumptions;
	private Set<UserTableEntity> products;
	
	public String execute () {
		destiny = "viewTableData?idExperiment=" + idExperiment + "&tableName=" + tableName;

		
		try {
			
			InstanceService ps = new InstanceService();
			ConsumptionService cs = new ConsumptionService();
			RelationService rs = new RelationService();

			instance = ps.getInstance( idInstance );
			products = rs.getGeneratedData(tableName, idInstance, idExperiment);

			// we must pass a valid not null Consumption list to getConsumptionsData
			try {
				instance.setConsumptions( cs.getList(idInstance) );
			} catch ( NotFoundException ignored) {	
				instance.setConsumptions( new HashSet<Consumption>() );
			}
			consumptions = rs.getConsumptionsData( instance.getConsumptions(), idExperiment );
			
		} catch ( NotFoundException  e) {
			setMessageText("Instance not found.");
			return "error";
		} catch (Exception e) {
			e.printStackTrace();
			setMessageText("Error: " + e.getMessage() );
		} 
		
		return "ok";
	}


	public Instance getInstance() {
		return instance;
	}

	public void setIdInstance(Integer idInstance) {
		this.idInstance = idInstance;
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
