package cmabreu.sagitarii.core.instances;

import java.util.List;

import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Fragment;
import cmabreu.sagitarii.persistence.entity.Instance;

public interface IInstanceGenerator {
	List<Instance> generateInstances( Activity activity, Fragment frag) throws Exception;
}
