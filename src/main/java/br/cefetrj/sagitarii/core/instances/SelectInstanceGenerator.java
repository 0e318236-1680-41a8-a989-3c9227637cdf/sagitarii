package br.cefetrj.sagitarii.core.instances;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.Fragment;
import br.cefetrj.sagitarii.persistence.entity.Instance;

public class SelectInstanceGenerator implements IInstanceGenerator {
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	@Override
	public List<Instance> generateInstances(Activity activity, Fragment frag) throws Exception {
		logger.debug( "Activity '" + activity.getTag() + "' allowed to run." );
		logger.debug("generating instances...");		
		logger.debug("'SELECT' type detected: single instance will be created. No need to fetch data.");

		InstanceCreator pc = new InstanceCreator();
		List<Instance> pipes = new ArrayList<Instance>();

		Instance pipe = pc.createInstance( activity, frag, null );
		pipes.add(pipe);
		
		return pipes;
	}

}
