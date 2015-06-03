package cmabreu.sagitarii.core.instances;

import cmabreu.sagitarii.core.types.ActivityType;

public class InstanceGeneratorFactory {
	
	
	public static IInstanceGenerator getGenerator( ActivityType type ) {
		
		switch (type) {
		case MAP:
			return new MapInstanceGenerator();
		case SELECT:
			return new SelectInstanceGenerator();
		case REDUCE:
			return new ReduceInstanceGenerator();
		case SPLIT_MAP:
			return new SplitInstanceGenerator();
		default:
			return null;
		}
	}

	
	
}
