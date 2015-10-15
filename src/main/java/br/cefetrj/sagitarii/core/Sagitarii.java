package br.cefetrj.sagitarii.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.core.delivery.DeliveryUnit;
import br.cefetrj.sagitarii.core.delivery.InstanceDeliveryControl;
import br.cefetrj.sagitarii.core.filetransfer.FileImporter;
import br.cefetrj.sagitarii.core.filetransfer.FileReceiverManager;
import br.cefetrj.sagitarii.core.filetransfer.ReceivedFile;
import br.cefetrj.sagitarii.core.mail.MailService;
import br.cefetrj.sagitarii.core.processor.Activation;
import br.cefetrj.sagitarii.core.types.ExperimentStatus;
import br.cefetrj.sagitarii.core.types.FragmentStatus;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.entity.Fragment;
import br.cefetrj.sagitarii.persistence.entity.Instance;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.services.ExperimentService;
import br.cefetrj.sagitarii.persistence.services.FragmentService;
import br.cefetrj.sagitarii.persistence.services.InstanceService;
import br.cefetrj.sagitarii.persistence.services.RelationService;


public class Sagitarii {
	private Logger logger = LogManager.getLogger( this.getClass().getName() );
	private static Sagitarii sagitarii;
	private List<Experiment> runningExperiments;
	private boolean stopped = false;
	private InstanceBuffer instanceBuffer;
	private double systemSpeedUp = 0;
	private double systemEfficiency = 0;
	
	public void removeExperiment( Experiment exp ) {
		for ( Experiment experiment : runningExperiments ) {
			if ( experiment.getTagExec().equals( exp.getTagExec() ) ) {
				logger.debug("removing experiment " + exp.getTagExec() + " from execution queue.");
				runningExperiments.remove( experiment );
				break;
			}
		}
	}
	
	public boolean experimentIsStillQueued( Experiment exp ) {
		return instanceBuffer.experimentIsStillQueued(exp);
	}

	
	public synchronized void finishInstance( Instance instance ) {
		logger.debug("instance " + instance.getSerial() + " is finished by " + instance.getExecutedBy() +
				". execution time: " + instance.getElapsedTime() );

		try {
			// Set as finished (database)
			InstanceService instanceService = new InstanceService();
			instanceService.finishInstance( instance );
			
			// Remove from output buffer if any
			instanceBuffer.removeFromOutputBuffer( instance );
			
		} catch ( Exception e ) {
			logger.error( e.getMessage() );
			
			e.printStackTrace();
			
		}
	}
	
	public void updateSystemMetrics() {
		try {
			RelationService rs = new RelationService();
			systemSpeedUp = rs.getSystemSpeedUp();
			rs.newTransaction();
			systemEfficiency = rs.getSystemEfficiency();
		} catch ( Exception e ) {
			
		}
	}
	
	/**
	 * Percorre a lista de experimentos em execução e para cada experimento verifica se seus fragmentos já 
	 * foram todos executados ( todas as instancias do fragmento encerraram).
	 * 
	 * Em caso positivo, verifica se o próximo fragmento na ordem de execução pode ser
	 * executado (atividades iniciais já estão desbloqueadas). Se puder, gera os instances
	 * do próximo fragmento.
	 * 
	 * Se não há mais fragmentos no experimento ou os que restam ainda estiverem bloqueados
	 * (nesse caso, houve algum erro na execução dos fragmentos anteriores, pois se os desbloqueados já terminaram 
	 * então todos os dados dos próximos já deveriam estar presentes) então encerra o experimento.
	 * 
	 * As condições para um fragmento ser executado são: a) Todos os fragmentos com ordem de execução menor que ele precisam
	 * ter terminado; b) Todas as tabelas de entrada da atividade de entrada do fragmento precisam possuir dados.
	 * 
	 * experimentIsStillQueued é verdadeiro quando não há mais instances do fragmento enfileirados no banco, porém
	 * ainda existem alguns no buffer da saída aguardando serem processados pelos nós.
	 * 
	 * Um fragmento é gerado com status READY. Quando for possível gerar instances para o fragmento, ele se torna 
	 * PIPELINED (os instances foram gerados mas nenhum foi pego ainda para o buffer). Quando os instances deste 
	 * fragmento forem pegos do banco para o buffer de saída, então o fragmento está RUNNING. Então, quando não houverem
	 * mais instances deste fragmento NO BANCO, então ele estará FINISHED. 
	 * 
	 * Mais detalhes em updateFragments()
	 * 
	 */
	private synchronized void checkFinished() {
		logger.debug("checking finished experiments...");
		for ( Experiment exp : runningExperiments ) {
			boolean allFinished = true;
			boolean haveReady = false;
			for ( Fragment frag : exp.getFragments() ) {
				// allFinished é verdadeiro quando não há mais fragmentos RUNNING (somente READY e FINISHED).
				// Os READY vão se tornar PIPELINED quando forem gerados novos instances. E se tornarão RUNNING
				// quando esta rotina for executada novamente.
				if ( frag.getStatus() != FragmentStatus.READY ) {
					allFinished = ( allFinished && ( frag.getStatus() == FragmentStatus.FINISHED ) );
				} else {
					haveReady = true;
					logger.debug(" > experiment " + exp.getTagExec() + " fragment " + frag.getSerial() +" : " + frag.getStatus() );
				}
			}
			
			// Se todos os fragmentos que estavam RUNNING já estiverem terminado e não houverem mais
			// instances a serem processados, então é hora de gerar os instances dos fragmentos 
			// que dependiam destes e iniciar sua execução. 
			// Condição: Não existem instances de nenhum fragmento deste 
			// experimento no banco e nem nos buffers.
			// pois se não houver uma recuperação eficiente ele ficará tempo demais no buffer de saída, impedindo
			// o prosseguimento da execução do experimento.
			// Deve haver ao menos um fragmento READY ( provavelmente é um BLOQUEANTE que 
			// dependia dos que estavam rodando ).
			if ( allFinished && haveReady && !experimentIsStillQueued( exp ) ) {
				logger.debug("generating instances for next fragment in experiment " + exp.getTagExec() );
				try {
					FragmentInstancer fp = new FragmentInstancer( exp );
					fp.generate();
					int pips = fp.getInstances().size();
					if ( pips == 0) {
						logger.error("experiment " + exp.getTagExec() + " generate empty instance list. Will finish it" );
						haveReady = false;
					} 
				} catch (Exception e) {
					logger.error("cannot generate instances for experiment " + exp.getTagExec() + ": " + e.getMessage() );
					haveReady = false;
				}
				logger.debug("done generating instances (" + exp.getTagExec() + ")");
			}

			// Se todos os instances deste experimento foram concluídos ( allFinished ) e 
			// não há nenhum por processar ( not haveReady ) e 
			// nenhum instance está no buffer ( not experimentIsStillQueued ) então o experimento 
			// está totalmente concluído!! 
			if ( allFinished && !haveReady && !experimentIsStillQueued( exp ) ) {
				
				// Verify if we still receiving data from Teapot for this experiment
				try {
					if ( !FileReceiverManager.getInstance().workingOnExperiment( exp.getTagExec() ) ) { 
						exp.setStatus( ExperimentStatus.FINISHED );
						exp.setFinishDateTime( Calendar.getInstance().getTime() );
						
							ExperimentService experimentService = new ExperimentService();
							experimentService.updateExperiment(exp);
							runningExperiments.remove( exp );
							logger.debug("experiment " + exp.getTagExec() + " is finished.");
							
							MailService ms = new MailService();
							try {
								ms.notifyExperimentFinished( exp );
							} catch ( Exception ex ) {
								logger.error("Cannot send notification mail: " + ex.getMessage() );
							}
							
							return;
					} else {
						logger.debug("cannot finish experiment " + exp.getTagExec() + ": found open sessions receiving files");
					}
					
					
					
				} catch ( Exception e ) {
					logger.error("cannot check experiment status: " + e.getMessage() );
					e.printStackTrace();
				}
				
			}
			
		}
		logger.debug("done checking finished experiments.");
	}
	
	
	/**
	 * Verifica se existe algum fragmento que já tenha processado
	 * todas as suas instancias.
	 * Caso encontre, tenta gerar novas instancias.
	 * Caso não gere mais nenhum instancia, marca o experimento como 
	 * encerrado ( Status = FINISHED ). 
	 */
	private synchronized void updateFragments() throws Exception {
		logger.debug("updating fragments...");
		for ( Experiment exp : runningExperiments ) {
			for ( Fragment frag : exp.getFragments() ) {
				
				FragmentStatus oldStatus = frag.getStatus();
				
				int count = frag.getRemainingInstances();

				logger.debug("Frag: " + frag.getSerial() + " Status: " + frag.getStatus() + " Instances: " + count );
				
				// Case 1 --------------------------------------------------------------------
				if ( (frag.getStatus() == FragmentStatus.PIPELINED) && (count > 0) ) {
					logger.debug(" > " + count + " instances found. Changing fragment " + frag.getSerial() + " status from PIPELINED to RUNNING");
					frag.setStatus( FragmentStatus.RUNNING );
				}

				// Case 2 --------------------------------------------------------------------
				if ( frag.getStatus() == FragmentStatus.RUNNING ) {
					logger.debug(" > updating instance count");
					
					// TODO: Check buffers BEFORE check databbase.
					// If we still have instances in buffers, no need to hit database, right?
					
					try {
						InstanceService instanceService = new InstanceService(); 
						count = instanceService.getPipelinedList( frag.getIdFragment() ).size();
						logger.debug(" > found " + count + " instances in database");
					} catch ( NotFoundException e) {
						logger.debug(" > this fragment have no instances in database");
						count = 0;
					} 
					
					frag.setRemainingInstances( count );
					if ( count == 0 ) {	
						logger.debug(" > no instances found: can I set fragment status to finished?");

						// TODO: Check this BEFORE hit database... In memory check is less costly...
						logger.debug("Instances in Delivery Control:");
						for( DeliveryUnit du : InstanceDeliveryControl.getInstance().getUnits()  ) {
							logger.debug( " > Instance: " + du.getInstance().getSerial() + " FragID: " + du.getInstance().getIdFragment() );
							logger.debug(" > Tasks: ");
							for( Activation act : du.getActivations() ) {
								logger.debug("    > " + act.getActivitySerial()	);
							}
						}

						logger.debug("current importers");
						for( FileImporter importer :  FileReceiverManager.getInstance().getImporters() ) {
							try {
								logger.debug(" > " + importer.getName() );
								for( ReceivedFile file : importer.getReceivedFiles() ) {
									logger.debug("     > " + file.getActivity() + " " + file.getFileName() );
								}
							} catch ( Exception e ) {}
						}
						
						
						if ( experimentIsStillQueued( frag.getExperiment() )  ) {
							logger.debug(" > WAIT! this fragment still have instances queued!");
						} else {
							logger.debug(" > Yeap! setting fragment " + frag.getSerial() + " to finished.");
							frag.setStatus( FragmentStatus.FINISHED );
						}
						// ===============================================================================
					}
					
					if ( oldStatus != frag.getStatus() ) {
						logger.debug(" > fragment " + frag.getSerial() + " status is now '" + frag.getStatus()  + "' in database");
						FragmentService fragmentService = new FragmentService();
						fragmentService.updateFragment(frag);
						fragmentService = null;
					} else {
						logger.debug(" > fragment " + frag.getSerial() + " status still as '" + frag.getStatus()+ "'" );
					}
					
				}
				
			}
		}
		logger.debug("done updating fragments.");
	}
	
	
	public synchronized Instance getNextInstance(String macAddress) {
		return instanceBuffer.getNextInstance( runningExperiments, macAddress );
	}

	
	public synchronized void returnToBuffer( Instance instance ) {
		instanceBuffer.returnToBuffer(instance);
	}
	
	
	public synchronized Instance getNextJoinInstance( String macAddress) {
		return instanceBuffer.getNextJoinInstance( macAddress);
	}


	public synchronized void reloadAfterCrash() {
		instanceBuffer.reloadAfterCrash( runningExperiments );
	}
	
	
	/**
	 * Chamado pelo Orchestrator de tempos em tempos.
	 * é o coração do sistema.
	 * O arquivo config.xml possui uma tag poolIntervalSeconds
	 * que configura o tempo em segundos que este método é chamado.
	 * 
	 * Um tempo muito curto o sistema fica mais rápido, mas pode ocasionar
	 * mais lidas no banco de dados sem necessidade (para verificar se existem instances
	 * produzidos).
	 * 
	 * Um tempo muito longo faz com que menos acessos ao banco sejam feitos, mas
	 * o sistema pode demorar a encher o buffer de saída, fazendo com que os nós 
	 * fiquem desocupados.
	 * 
	 * Este método faz um acesso ao banco para carregar instances ao buffer quando este atinge
	 * menos de 1/3 de sua capacidade (comum) ou 1/5 da capacidade (SQL). No caso no buffer de JOIN, 
	 * este acesso é quase constante, pois é um tipo de instance escasso e de processamento rápido, 
	 * o que faz com que o buffer fique quase sempre vazio (por isso o limite de 1/5 e não de 1/3).
	 * 
	 * Este método também gira a roleta de experimentos, escalonando os experimentos em ordem numa
	 * fila para terem seus instances processados de forma homogênea.
	 * 
	 * Se necessitar aumentar a velocidade, coloque um buffer pequeno, assim ele esvazia rápido
	 * o suficiente para a próxima verificação justificar o acesso ao banco.
	 * Claro que o tamanho do buffer é influenciado pela quantidade de nós de processamento e sua
	 * velocidade de trabalho. Um buffer muito pequeno com muitos nós fará com que eles fiquem desocupados
	 * aguardando esta rotina recarregar o buffer, mas terá melhor distribuição entre os experimentos
	 * ( a roleta de experimentos irá girar mais depressa ). 
	 * 
	 * Um buffer grande com poucos nós fará com que ele fique sempre cheio. Esta rotina fará acessos
	 * injustificados ao banco e fará a roleta de experimentos girar devagar, carregando uma quantidade muito
	 * grande de instances do mesmo experimento no buffer e causando inanição nos demais.
	 * 
	 * Procure um balanço ideal entre "poolIntervalSeconds", "maxInputBufferCapacity" e a quantidade
	 * de nós de processamento em sua rede.
	 * 
	 */
	public synchronized void loadInputBuffer() {

		if ( stopped || ( runningExperiments.size() == 0 ) ) {
			return;
		}

		if ( ClustersManager.getInstance().hasClusters() ) {
			
			try {
				instanceBuffer.loadBuffers();
			} catch ( Exception e ) {
				logger.error( "load buffers error: " + e.getMessage() );
			}
			
			try {
				updateFragments();
			} catch (Exception e) {
				logger.error( "update fragments error: " + e.getMessage() );
			}
		
			checkFinished();

		} else {
			// logger.debug("will not work until have nodes to process.");
		}
			
	}
	
	public boolean isRunning() {
		return ( runningExperiments.size() > 0 );
	}
	
	public synchronized static Sagitarii getInstance() {
		if ( sagitarii == null ) {
			sagitarii = new Sagitarii();
		}
		return sagitarii;
	}

	
	public void stopProcessing() {
		logger.warn("Sagitarii was stopped.");
		stopped = true;
	}

	public void resumeProcessing() {
		logger.warn("Sagitarii was resumed.");
		stopped = false;
	}

	public boolean isStopped() {
		return stopped;
	}
	
	private Sagitarii() {
		instanceBuffer = new InstanceBuffer();
		runningExperiments = new ArrayList<Experiment>();
		updateSystemMetrics();
	}
	
	/**
	 * Pausa um experimento.
	 * Não retirar da lista de experimentos em execução, pois só assim ele poderá ser 
	 * colocado em execução novamente. Ver "resume()".
	 * 
	 */
	public void pause( int idExperiment ) throws Exception {
		ExperimentService experimentService = new ExperimentService();
		for( Experiment exp : runningExperiments ) {
			if ( (exp.getIdExperiment() == idExperiment) && ( exp.getStatus() == ExperimentStatus.RUNNING ) ) {
				exp.setStatus( ExperimentStatus.PAUSED );
				experimentService.updateExperiment(exp);
				return;
			}
		}
	}

	/**
	 * Coloca um experimento pausado para executar novamente.
	 * Não busca no banco, e sim na lista se experimentos em execução
	 * para poupar acesso ao banco de dados. Somente o seu status é altarado no banco.
	 */
	public void resume( int idExperiment ) throws Exception {
		logger.debug("resuming experiment " + idExperiment);
		ExperimentService experimentService = new ExperimentService();
		for( Experiment exp : runningExperiments ) {
			if ( (exp.getIdExperiment() == idExperiment) && ( exp.getStatus() == ExperimentStatus.PAUSED ) ) {
				logger.debug("found "+ exp.getTagExec() + ". resuming...");
				exp.setStatus( ExperimentStatus.RUNNING );
				experimentService.updateExperiment(exp);
				logger.debug("done");
				return;
			}
		}
		logger.error("cannot resume. Experiment " + idExperiment + " not found on buffer.");
	}
	
	public synchronized List<Experiment> getRunningExperiments() {
		return new ArrayList<Experiment>( runningExperiments );
	}

	public void setRunningExperiments(List<Experiment> runningExperiments) {
		this.runningExperiments = runningExperiments;
	}
	
	public synchronized void addRunningExperiment( Experiment experiment ) throws Exception {
		boolean found = false;
		for ( Experiment exp : runningExperiments ) {
			if ( exp.getTagExec().equalsIgnoreCase( experiment.getTagExec() ) ) {
				found = true;
			}
		}
		if ( !found && ( experiment.getStatus() == ExperimentStatus.RUNNING ) ) {
			runningExperiments.add( experiment );
			updateFragments();
		}
	}
	
	public Queue<Instance> getInstanceInputBuffer() {
		return instanceBuffer.getInstanceInputBuffer();
	}

	public Queue<Instance> getInstanceOutputBuffer() {
		return instanceBuffer.getInstanceOutputBuffer();
	}

	public void setMaxInputBufferCapacity(int maxInputBufferCapacity) {
		instanceBuffer.setBufferSize( maxInputBufferCapacity );
	}
	
	public int getMaxInputBufferCapacity() {
		return instanceBuffer.getBufferSize();
	}

	public Queue<Instance> getInstanceJoinInputBuffer() {
		return instanceBuffer.getInstanceJoinInputBuffer();
	}

	public double getSystemEfficiency() {
		return systemEfficiency;
	}
	
	public double getSystemSpeedUp() {
		return systemSpeedUp;
	}

}
