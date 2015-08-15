package br.cefetrj.sagitarii.core.instances;

import br.cefetrj.sagitarii.core.types.ActivityType;

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
