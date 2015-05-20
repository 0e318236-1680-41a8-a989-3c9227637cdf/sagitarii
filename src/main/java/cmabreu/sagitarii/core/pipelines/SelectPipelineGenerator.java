package cmabreu.sagitarii.core.pipelines;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Fragment;
import cmabreu.sagitarii.persistence.entity.Pipeline;

public class SelectPipelineGenerator implements IPipelineGenerator {
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	
	@Override
	public List<Pipeline> generatePipelines(Activity activity, Fragment frag) throws Exception {
		logger.debug( "Activity '" + activity.getTag() + "' allowed to run." );
		logger.debug("generating pipelines...");		
		logger.debug("'SELECT' type detected: single pipeline will be created. No need to fetch data.");

		PipelineCreator pc = new PipelineCreator();
		List<Pipeline> pipes = new ArrayList<Pipeline>();

		Pipeline pipe = pc.createPipeline( activity, frag, null );
		pipes.add(pipe);
		
		return pipes;
	}

}
