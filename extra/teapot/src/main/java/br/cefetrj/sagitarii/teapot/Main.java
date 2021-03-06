package br.cefetrj.sagitarii.teapot;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;

import br.cefetrj.sagitarii.teapot.comm.Communicator;


public class Main {
	private static Logger logger = LogManager.getLogger( "br.cefetrj.sagitarii.teapot.Main" ); 
	private static long totalInstancesProcessed = 0;
	private static boolean paused = false;
	private static List<TaskRunner> runners = new ArrayList<TaskRunner>();
	private static boolean restarting = false;
	private static boolean reloading = false;
	private static boolean quiting = false;
	private static boolean cleaning = false;
	private static Communicator communicator;
	private static Configurator configurator;
	private static Watchdog watchdog;
	private static final int TASK_PREFETCH_VALUE = 2;
	private static ExecutorService executor;
	
	public static void pause() {
		paused = true;
	}
	
	public static long getTotalInstancesProcessed() {
		return totalInstancesProcessed;
	}
	
	public static Communicator getCommunicator() {
		return communicator;
	}
	
	public static Configurator getConfigurator() {
		return configurator;
	}

	public static void resume() {
		paused = false;
	}

	/**
	 * Remover o diretorio raiz do namespace
	 * Chamado antes de iniciar os trabalhos para sempre ter um namespace limpo.
	 */
	private static void cleanUp() {
		cleaning = true;
		if (  getRunners().size() > 0 ) {
			logger.warn("will not clean workspace. " + getRunners().size() + " tasks still runnig");
		} else {
			try {
				FileUtils.deleteDirectory( new File( "namespaces" ) ); 
			} catch ( IOException e ) {
				logger.error( e.getMessage() ); 
			}
			logger.warn("workspace cleaned");
			cleaning = false;
		}
	}

	
	private static List<String> decodeResponse( String encodedResponse ) {
		logger.debug("decoding response ...");
		String[] responses = encodedResponse.replace("[", "").replace("]", "").replace(" ", "").split(",");
		List<String> resp = new ArrayList<String>();
		logger.debug("response package contains " + resp.size() + " instances" );
		resp = new ArrayList<String>( Arrays.asList( responses ) ); 
		logger.debug("done");
		return resp;
	}

	
	/**
	 * TaskManager entry point
	 * 
	 * EX UNITATE VIRES !

	 */
	public static void main( String[] args ) {
		
		boolean wrappersDownloaded = false;
		try {
			System.out.println("");
	    	System.out.println("Sagitarii Teapot Node v1.0.125        23/04/2015");
	    	System.out.println("Carlos Magno Abreu        magno.mabreu@gmail.com");
			System.out.println("------------------------------------------------");
			System.out.println("");
			
			logger.debug("Loading Repository Manager ...");
			
			configurator = new Configurator("config.xml");
			configurator.loadMainConfig();
			RepositoryManager rm = new RepositoryManager( configurator );

			logger.debug("cleaning workspace...");
			cleanUp();
			
			if ( configurator.useProxy() ) {
				logger.debug("Proxy: " + configurator.getProxyInfo().getHost() );
			}
			if ( !configurator.getShowConsole() ) {
				logger.debug("No activations console.");
			}

			logger.debug("Searching for wrappers...");
			try {
				rm.downloadWrappers( );
				wrappersDownloaded = true;
			} catch ( ConnectException e ) {
				logger.error("Cannot download wrappers. Will interrupt startup until Sagitarii is up.");
			}

			logger.debug("Staring communicator...");
			communicator = new Communicator( configurator );
			
			if ( wrappersDownloaded ) {
				logger.debug("TaskManager started.");
			}
			
			watchdog = new Watchdog();
			
			// =============================================================
			// =============================================================
			
			executor = Executors.newFixedThreadPool( configurator.getActivationsMaxLimit() );
			
			while (true) {
				clearRunners();

				logger.debug( "init new cycle: " + runners.size() + " of " + configurator.getActivationsMaxLimit() + " tasks running:" );
				for ( TaskRunner tr : getRunners() ) {
					if ( tr.getCurrentTask() != null ) {
						String time = tr.getStartTime() + " (" + tr.getTime() + ")";
						logger.debug( " > " + tr.getCurrentTask().getTaskId() + " (" + tr.getCurrentActivation().getExecutor() + ") : " + time);
					}
				}
				
				SpeedEqualizer.equalize( configurator, runners.size() );
				
				if ( !wrappersDownloaded ) {
					try {
						logger.debug("Searching for wrappers...");
						rm.downloadWrappers();
						wrappersDownloaded = true;
						logger.debug("Done. TaskManager Started.");
					} catch ( ConnectException e ) {
						logger.error("Cannot download wrappers. Skipping.");
					}
				} else {
					if ( !paused ) {
						
						watchdog.protect( getRunners(), configurator.getSystemProperties().getCpuLoad() );
						
						String response = "NO_DATA";
						try {
							if ( runners.size() < ( configurator.getActivationsMaxLimit() + TASK_PREFETCH_VALUE ) ) {

								if ( !havePendentCommand() ) {
									logger.debug( "asking Sagitarii for tasks to process...");
									
									int packageSize = configurator.getActivationsMaxLimit() - runners.size();
									if ( packageSize < 1 ) {
										packageSize = 1;
									}
									
									response = getTasksFromSagitarii( packageSize + TASK_PREFETCH_VALUE );
									
									if ( response.length() > 0 ) {
										if ( response.equals("COMM_ERROR") ) {
											logger.error("Sagitarii is offline");
										} else {
											if ( !specialCommand( response ) ) {
												List<String> responses = decodeResponse( response );
												for ( String decodedResponse : responses ) {
													startTask( decodedResponse );
												}
											}
										}
									} else {
										logger.debug("nothing to do for now");
									}
									
								} else {
									logger.debug("cannot request new tasks: flushing buffers...");
								}
								
								// If this number is > 0 then atfer I started new tasks, 
								// some of old ones have finished, so I'm a bit slow 
								logger.debug("Lazy Rate: " + (configurator.getActivationsMaxLimit() - runners.size() ) );
								
							} else {
								//
							}

							DynamicLoadBalancer.equalize( configurator, runners.size() );

						} catch ( Exception e ) {
							logger.error( "process error: " + e.getMessage() );
							logger.error( " > " + response );
						}
					}
					sendRunners();
				}
				
				logger.debug("will sleep " + configurator.getPoolIntervalMilliSeconds() + "ms");
				try {
				    Thread.sleep( configurator.getPoolIntervalMilliSeconds() );
				} catch( InterruptedException ex ) {
				
				}
						
			}
			
			
		} catch (Exception e) {
			logger.debug("Critical error. Cannot start TaskManager Node.");
			logger.debug("Error details:");
			e.printStackTrace();
		}

	}

	private static String getTasksFromSagitarii(int packageSize) {
		String response;
		response = communicator.announceAndRequestTask( configurator.getSystemProperties().getCpuLoad(), 
				configurator.getSystemProperties().getFreeMemory(), configurator.getSystemProperties().getTotalMemory(),
				packageSize, configurator.getSystemProperties().getMemoryPercent(), configurator.getActivationsMaxLimit() );
		return response;
	}
	
	private static void startTask( String decodedResponse ) {
		logger.debug("starting new task");
		notifySagitarii("starting new task...");
		TaskRunner tr = new TaskRunner( decodedResponse, communicator, configurator);
		
		
		//FutureTask<Long> futureTaskHdfs = new FutureTask<Long>( tr );
		executor.execute( tr );
		
		
		runners.add(tr);
		//tr.start();
		totalInstancesProcessed++;
		logger.debug("new task started");
		notifySagitarii("new task started. Total: " + runners.size() );
	}

	private static String generateJsonPair(String paramName, String paramValue) {
		return "\"" + paramName + "\":\"" + paramValue + "\""; 
	}

	private static String addArray(String paramName, String arrayValue) {
		return "\"" + paramName + "\":" + arrayValue ; 
	}

	private static void sendRunners() {
		logger.debug("sending " + getRunners().size() + " Task Runners to Sagitarii ");
		StringBuilder sb = new StringBuilder();
		String dataPrefix = "";
		sb.append("[");
		for ( TaskRunner tr : getRunners() ) {
			if ( tr.getCurrentActivation() != null ) {
				logger.debug( " > " + tr.getCurrentActivation().getTaskId() + " sent" );
				sb.append( dataPrefix + "{");
				sb.append( generateJsonPair( "workflow" , tr.getCurrentActivation().getWorkflow() ) + "," );
				sb.append( generateJsonPair( "experiment" , tr.getCurrentActivation().getExperiment() ) + "," );
				sb.append( generateJsonPair( "taskId" , tr.getCurrentActivation().getTaskId() ) + "," );
				sb.append( generateJsonPair( "executor" , tr.getCurrentActivation().getExecutor() ) + "," ); 
				sb.append( generateJsonPair( "startTime" , tr.getStartTime() ) + "," );
				sb.append( generateJsonPair( "elapsedTime" , tr.getTime() ) );
				dataPrefix = ",";
				sb.append("}");
			} else {
				sb.append( dataPrefix + "{");
				sb.append( generateJsonPair( "workflow" , "UNKNOWN" ) + "," );
				sb.append( generateJsonPair( "experiment" , "UNKNOWN" ) + "," );
				sb.append( generateJsonPair( "taskId" , "UNKNOWN" ) + "," );
				sb.append( generateJsonPair( "executor" , "UNKNOWN" ) + "," ); 
				sb.append( generateJsonPair( "startTime" , "00:00:00" ) + "," );
				sb.append( generateJsonPair( "elapsedTime" , "00:00:00" ) );
				dataPrefix = ",";
				sb.append("}");
			}
		}
		sb.append("]");
		StringBuilder data = new StringBuilder();
		data.append("{");
		data.append( generateJsonPair( "nodeId" , configurator.getSystemProperties().getMacAddress() ) + "," );
		data.append( generateJsonPair( "cpuLoad" , String.valueOf( configurator.getSystemProperties().getCpuLoad() ) ) + "," );
		data.append( generateJsonPair( "freeMemory" , String.valueOf( configurator.getSystemProperties().getFreeMemory() ) ) + "," );
		data.append( generateJsonPair( "totalMemory" , String.valueOf( configurator.getSystemProperties().getTotalMemory() ) ) + "," );
		data.append( generateJsonPair( "memoryPercent" , String.valueOf( configurator.getSystemProperties().getMemoryPercent() ) ) + "," );
		data.append( generateJsonPair( "totalDiskSpace" , String.valueOf( configurator.getSystemProperties().getTotalDiskSpace() ) ) + "," );
		data.append( generateJsonPair( "freeDiskSpace" , String.valueOf( configurator.getSystemProperties().getFreeDiskSpace() ) ) + "," );
		data.append( generateJsonPair( "maximunLimit" , String.valueOf( configurator.getActivationsMaxLimit() ) ) + "," );
		data.append( addArray("data", sb.toString() ) ); 
		data.append("}");			
		
		logger.debug(" done sending Task Runners: " + data.toString() );
		
		communicator.doPost("receiveNodeTasks", "tasks", data.toString() );
		
	}
	
	/**
	 * Check if there is a command waiting for task buffer is flushed 
	 */
	private static boolean havePendentCommand() {
		if ( quiting ) {
			logger.debug("TaskManager is quiting... do not process tasks anymore");
			quit();
			return true;
		}
		if ( restarting ) {
			logger.debug("TaskManager is restarting... do not process tasks anymore");
			restart();
			return true;
		}
		if ( reloading ) {
			logger.debug("TaskManager is reloading wrappers... do not process tasks for now");
			reloadWrappers();
			return true;
		}
		if ( cleaning ) {
			logger.debug("TaskManager is cleaning workspace... do not process tasks for now");
			cleanUp();
			return true;
		}
		
		// No command is in process. Can free TaskManager now...
		return false;
	}
	
	/**
	 * Will check if Sagitarii sent a special command to this node
	 * Returning TRUE will deny to run tasks ( may be a command or in flushing process )
	 * 
	 * WARNING: By returning FALSE ensure this "response" is a valid XML instance
	 * 	or the XML parser will throw an error.
	 * 
	 */
	private static boolean specialCommand( String response ) {
		logger.debug("checking preprocess");
		
		// RESULT:
		// FALSE = A valid XML instance. Will run a new task.
		// TRUE  = A Sagitarii command or we don't want to run new tasks 
		// 			even "response" is a valid XML instance
		
		if ( ( !response.equals( "NO_ANSWER" ) ) && ( !response.equals( "COMM_ERROR" ) ) && ( !response.equals( "" ) ) ) {
			
			if ( response.equals( "COMM_RESTART" ) ) {
				logger.warn("get restart command from Sagitarii");
				restart();
				return true;
			} 
				
			if ( response.equals( "RELOAD_WRAPPERS" ) ) {
				logger.warn("get reload wrappers command from Sagitarii");
				reloadWrappers();
				return true;
			} 
				
			if ( response.equals( "COMM_QUIT" ) ) {
				logger.warn("get quit command from Sagitarii");
				quit();
				return true;
			} 
				
			if ( response.contains( "INFORM" ) ) {
				String[] data = response.split("#");
				logger.warn("Sagitarii is asking for Instance " + data[1] );
				inform( data[1] );
				// No need to stop TaskManager or flush buffers... just an information request
				// Avoid consider this response as a valid XML instance by returning "TRUE"
				return true;
			} 
				
			if ( response.equals( "COMM_CLEAN_WORKSPACE" ) ) {
				logger.warn("get clean workspace command from Sagitarii");
				cleanUp();
				return true;
			} 
			
		} else {
			logger.debug("invalid response from Sagitarii: " + response);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Will restart TaskManager
	 * It is a Sagitarii command
	 */
	public static void restartApplication() {
		try {
		  final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		  final File currentJar = new File ( TaskManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());
	
		  /* is it a jar file? */
		  if( !currentJar.getName().endsWith(".jar") ) {
		    return;
		  }
	
		  /* Build command: java -jar application.jar */
		  final ArrayList<String> command = new ArrayList<String>();
		  command.add( javaBin );
		  command.add( "-jar" );
		  command.add( currentJar.getPath() );
	
		  final ProcessBuilder builder = new ProcessBuilder(command);
		  builder.start();
		  System.exit(0);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Restart TaskManager
	 */
	private static void restart() {
		restarting = true;
		if ( getRunners().size() > 0 ) {
			logger.debug("cannot restart now. " + getRunners().size() + " tasks still runnig");
		} else {
			logger.debug("restart now.");
			restartApplication();
		}
	}
	
	public static void notifySagitarii( String message ) {
		// Will do nothing for now.. too much network noise !
		// Notifier.getInstance(communicator, configurator).notifySagitarii("MAIN", message, null);
		/*
		logger.debug( "notify Sagitarii: " + message );
		message = "[MAIN] " + message; 
		try {
			String parameters = "macAddress=" + configurator.getSystemProperties().getMacAddress() + "&errorLog=" + URLEncoder.encode( message, "UTF-8");
			communicator.send("receiveErrorLog", parameters);
		} catch ( Exception e ) {
			logger.error("cannot notify Sagitarii: " + e.getMessage() );
		}
		*/
	}	
	
	private static void inform( String instanceSerial ) {
		notifySagitarii("Sagitarii is asking for Instance " + instanceSerial );
		boolean found = false;
		for ( TaskRunner tr : getRunners() ) {
			if ( tr.getCurrentTask() != null ) {
				if ( tr.getCurrentActivation().getInstanceSerial().equals( instanceSerial ) ) {
					found = true;
					break;
				}
			}
		}
		String status = "";
		if ( found ) {
			status = "RUNNING";
			logger.debug("Instance "+instanceSerial+" is running");
			notifySagitarii("Instance "+instanceSerial+" is running");
		} else {
			status = "NOT_FOUND";
			logger.debug("Instance "+instanceSerial+" not found");
			notifySagitarii("Instance "+instanceSerial+" not found");
		}
		String parameters = "macAddress=" + configurator.getSystemProperties().getMacAddress() + 
				"&instance=" + instanceSerial + "&status=" + status;
		communicator.send("taskStatusReport", parameters);

	}
	
	/**
	 * Close TaskManager
	 */
	private static void quit() {
		quiting = true;
		if ( getRunners().size() > 0 ) {
			logger.debug("cannot quit now. " + getRunners().size() + " tasks still runnig");
		} else {
			logger.debug("quit now.");
			System.exit(0);
		}
	}
	
	/**
	 * Download all wrappers from Sagitarii again
	 */
	private static void reloadWrappers() {
		if( reloading ) {
			logger.debug("already reloading... will wait.");
			return;
		}
		reloading = true;
		if ( getRunners().size() > 0 ) {
			logger.debug("cannot reload wrappers now. " + getRunners().size() + " tasks still runnig");
			notifySagitarii("cannot reload wrappers now. " + getRunners().size() + " tasks still runnig");
		} else {
			logger.debug("reload all wrappers now.");
			try {
				RepositoryManager rm = new RepositoryManager( configurator );
				rm.downloadWrappers();
				logger.debug("all wrappers reloaded.");
				notifySagitarii("all wrappers reloaded.");
			} catch ( Exception e ) {
				notifySagitarii("cannot reload wrappers: " + e.getMessage());
				logger.error("cannot reload wrappers: " + e.getMessage() );
			}
			reloading = false;
		}
	}

	public static List<TaskRunner> getRunners() {
		return new ArrayList<TaskRunner>( runners );
	}
	
	/**
	 * Remove all finished task threads from buffer
	 *  
	 */
	private static void clearRunners() {
		logger.debug("cleaning task runners...");
		int total = 0;
		Iterator<TaskRunner> i = runners.iterator();
		while ( i.hasNext() ) {
			TaskRunner req = i.next(); 
			if ( !req.isActive() ) {
				try {
					logger.debug(" > killing task runner " + req.getCurrentActivation().getExecutor() + " " + req.getSerial() + " (" + req.getCurrentTask().getTaskId() + ")" );
				} catch ( Exception e ) { 
					logger.debug(" > killing null task runner");
				}
				i.remove();
				total++;
			}
		}		
		logger.debug( total + " task runners deleted" );
	}

}
