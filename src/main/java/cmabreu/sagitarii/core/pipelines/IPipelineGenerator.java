package cmabreu.sagitarii.core.pipelines;

import java.util.List;

import cmabreu.sagitarii.persistence.entity.Activity;
import cmabreu.sagitarii.persistence.entity.Fragment;
import cmabreu.sagitarii.persistence.entity.Pipeline;

public interface IPipelineGenerator {
	List<Pipeline> generatePipelines( Activity activity, Fragment frag) throws Exception;
}
