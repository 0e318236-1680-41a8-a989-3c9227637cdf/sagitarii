
package br.cefetrj.sagitarii.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.delivery.DeliveryUnit;
import br.cefetrj.sagitarii.core.delivery.InstanceDeliveryControl;
import br.cefetrj.sagitarii.core.statistics.Accumulator;
import br.cefetrj.sagitarii.core.statistics.AgeCalculator;

@Action (value = "viewDeliveryControl", results = { @Result (location = "viewDC.jsp", name = "ok") 
}, interceptorRefs= { @InterceptorRef("seguranca")	 } ) 

@ParentPackage("default")
public class ViewDeliveryControlAction extends BasicActionClass {
	private List<DeliveryUnit> units;
	private List<Accumulator> ageStatistics;
	private String firstDelayLimitSeconds;
	
	public String execute () {
		firstDelayLimitSeconds = InstanceDeliveryControl.getInstance().getFirstDelayLimitSeconds();
		units = InstanceDeliveryControl.getInstance().getUnits();
		ageStatistics = AgeCalculator.getInstance().getList();
		return "ok";
	}

	public List<DeliveryUnit> getUnits() {
		return units;
	}

	public List<Accumulator> getAgeStatistics() {
		return ageStatistics;
	}
	
	public String getFirstDelayLimitSeconds() {
		return firstDelayLimitSeconds;
	}

}
