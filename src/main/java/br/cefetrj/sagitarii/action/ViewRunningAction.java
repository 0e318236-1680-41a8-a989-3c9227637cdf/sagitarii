
package br.cefetrj.sagitarii.action;

import java.util.Queue;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import br.cefetrj.sagitarii.core.Sagitarii;
import br.cefetrj.sagitarii.persistence.entity.Instance;

@Action (value = "viewRunning", 
	results = { 
		@Result ( location = "viewRunning.jsp", name = "ok")
	}, interceptorRefs= { @InterceptorRef("seguranca")	 }) 

@ParentPackage("default")
public class ViewRunningAction extends BasicActionClass {
	private Queue<Instance> instanceInputBuffer;
	private Queue<Instance> instanceJoinInputBuffer;
	private Queue<Instance> instanceOutputBuffer;	
	private Queue<Instance> instanceTempOutputBuffer;	
	
	public String execute () {
		Sagitarii sagi = Sagitarii.getInstance();
		instanceInputBuffer = sagi.getInstanceInputBuffer();
		instanceJoinInputBuffer = sagi.getInstanceJoinInputBuffer();
		instanceOutputBuffer = sagi.getInstanceOutputBuffer();
		return "ok";
	}
	
	
	public Queue<Instance> getInstanceInputBuffer() {
		return instanceInputBuffer;
	}

	public Queue<Instance> getInstanceJoinInputBuffer() {
		return instanceJoinInputBuffer;
	}

	public Queue<Instance> getInstanceOutputBuffer() {
		return instanceOutputBuffer;
	}
	
	public Queue<Instance> getInstanceTempOutputBuffer() {
		return instanceTempOutputBuffer;
	}
	
}
