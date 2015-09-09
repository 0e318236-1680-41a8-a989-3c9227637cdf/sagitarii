package br.cefetrj.sagitarii.executors;

import java.util.List;

import br.cefetrj.sagitarii.teapot.Activation;

public interface IExecutor {
	int execute( Activation activation );
	List<String> getConsole();
}
