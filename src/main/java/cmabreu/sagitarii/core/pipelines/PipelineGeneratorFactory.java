package cmabreu.sagitarii.core.pipelines;

import cmabreu.sagitarii.core.types.ActivityType;

public class PipelineGeneratorFactory {
	
	
	public static IPipelineGenerator getGenerator( ActivityType type ) {
		
		switch (type) {
		case MAP:
			return new MapPipelineGenerator();
		case SELECT:
			return new SelectPipelineGenerator();
		case REDUCE:
			return new ReducePipelineGenerator();
		case SPLIT_MAP:
			return new SplitPipelineGenerator();
		default:
			return null;
		}
	}

	
	
}
