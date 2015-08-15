package br.cefetrj.sagitarii.core.instances;

import java.util.List;

import br.cefetrj.sagitarii.persistence.entity.Activity;
import br.cefetrj.sagitarii.persistence.entity.Fragment;
import br.cefetrj.sagitarii.persistence.entity.Instance;

public interface IInstanceGenerator {
	List<Instance> generateInstances( Activity activity, Fragment frag) throws Exception;
}
