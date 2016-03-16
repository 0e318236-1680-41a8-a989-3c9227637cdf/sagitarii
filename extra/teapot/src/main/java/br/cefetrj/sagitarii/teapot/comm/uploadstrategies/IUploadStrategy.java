package br.cefetrj.sagitarii.teapot.comm.uploadstrategies;

import java.util.List;

public interface IUploadStrategy {
	long uploadFile( List<String> fileNames, String targetTable, String experimentSerial, 
			String sessionSerial, String sourcePath ) throws Exception;
}
