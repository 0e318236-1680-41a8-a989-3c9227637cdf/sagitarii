package cmabreu.sagitarii.core;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import cmabreu.sagitarii.core.config.Configurator;
import cmabreu.sagitarii.core.processor.MainCluster;
import cmabreu.sagitarii.core.sockets.FileReceiverManager;
import cmabreu.sagitarii.core.types.ExecutorType;
import cmabreu.sagitarii.core.types.UserType;
import cmabreu.sagitarii.metrics.Chronos;
import cmabreu.sagitarii.misc.PathFinder;
import cmabreu.sagitarii.persistence.entity.ActivationExecutor;
import cmabreu.sagitarii.persistence.entity.Domain;
import cmabreu.sagitarii.persistence.entity.Experiment;
import cmabreu.sagitarii.persistence.entity.User;
import cmabreu.sagitarii.persistence.exceptions.DatabaseConnectException;
import cmabreu.sagitarii.persistence.exceptions.InsertException;
import cmabreu.sagitarii.persistence.exceptions.NotFoundException;
import cmabreu.sagitarii.persistence.repository.RelationRepository;
import cmabreu.sagitarii.persistence.services.ExecutorService;
import cmabreu.sagitarii.persistence.services.ExperimentService;
import cmabreu.sagitarii.persistence.services.UserService;

@WebListener
public class Orchestrator implements ServletContextListener {
    private ScheduledExecutorService scheduler;
    
    private void loggerDebug( String log ) {
    	System.out.println( log );
    }
    
    private void loggerError( String log ){
    	System.out.println( log );
    }

	@Override
    public void contextInitialized(ServletContextEvent event) {
    	loggerDebug("system init");
    	
    	ServletContext context = event.getServletContext();
    	System.setProperty("rootPath", context.getRealPath("/") );
    	
    	System.out.println( context.getRealPath("/")  );
    	try {
    		System.out.println( PathFinder.getInstance().getPath() );
    	} catch ( Exception e ) {
    		
    	}
    	
    	UserService us;
    	
    	try {
			us = new UserService();
			
			try {
	    		// Chamar getList().size() apenas para ver se existem usuários cadastrados.
				// Um NotFoundException significa que precisamos cadastrar o Admin
				// pois o banco está vazio. Caso contrário já temos ao menos o Admin.
				us.getList().size();
			} catch (NotFoundException ignored ) {
				// Nada encontrado. Precisamos de um Admin!
				User usr = new User();
				usr.setFullName("System Administrator");
				usr.setLoginName("admin");
				usr.setType( UserType.ADMIN );
				usr.setPassword("admin");
				usr.setUserMail("no.mail@localhost");
				us.newTransaction();
				us.insertUser(usr);
				loggerDebug("System Administrator created");
				
				// Add the default RRUNNER wrapper
				ExecutorService es = new ExecutorService();
				ActivationExecutor ex = new ActivationExecutor();
				ex.setActivationWrapper("r-wrapper.jar");
				ex.setExecutorAlias("executor_r");
				ex.setType( ExecutorType.RRUNNER );
				es.insertExecutor( ex );
				
			}
			
		} catch (DatabaseConnectException | InsertException e) {
			loggerError("init error : " + e.getMessage() );
			e.printStackTrace();
		}
			
		loggerDebug("check for interrupted work");	
		try {
			ExperimentService ws = new ExperimentService();
			List<Experiment> running = ws.getRunning();
			Sagitarii.getInstance().setRunningExperiments( running );
			Sagitarii.getInstance().reloadAfterCrash();
			loggerDebug("found " + Sagitarii.getInstance().getRunningExperiments().size() + " running experiments");	
		} catch ( NotFoundException e ) {
			loggerDebug("no running experiments found");	
		} catch (Exception e) {
			loggerError( e.getMessage() );	
		} 
		loggerDebug("done.");
		
        int interval = 5;
        int pseudoInterval = 5;
        int pseudoMaxTasks = 4;
        int mainNodesQuant = 1;
        int maxInputBufferCapacity = 500;
        int fileReceiverPort = 3333;
        int chunkBuffer = 100;
        
        try {
			Configurator config = Configurator.getInstance("config.xml");
			
			interval = config.getPoolIntervalSeconds();
			pseudoInterval = config.getPseudoClusterIntervalSeconds();
			pseudoMaxTasks = config.getPseudoMaxTasks();
			pseudoMaxTasks = config.getPseudoMaxTasks();
			maxInputBufferCapacity = config.getMaxInputBufferCapacity();
			mainNodesQuant = config.getMainNodesQuant();
			fileReceiverPort = config.getFileReceiverPort();
			chunkBuffer = config.getFileReceiverChunkBufferSize();
			
			if ( mainNodesQuant < 1 ) {
				mainNodesQuant = 1;
			}
			if ( mainNodesQuant > 9 ) {
				mainNodesQuant = 9;
			}
			
			Sagitarii.getInstance().setMaxInputBufferCapacity(maxInputBufferCapacity);
			
			loggerDebug("Sagitarii Scheduler: check every " + interval + " seconds");
			loggerDebug("Main Cluster: check every " + pseudoInterval + " seconds");

			try {
				RelationRepository rr = new RelationRepository();
				List<Domain> domains = rr.getDomains();
				DomainStorage.getInstance().setDomains( domains );
			} catch ( NotFoundException ignored ) { 
				
			}

			loggerDebug("Start File Receiver Manager on port " + fileReceiverPort);
			loggerDebug("Cache directory:");
			loggerDebug(" > " + PathFinder.getInstance().getPath() + "/cache");
			FileReceiverManager.getInstance().startServer( fileReceiverPort, chunkBuffer );

			scheduler = Executors.newSingleThreadScheduledExecutor();
			
			ActivationScheduler as = new ActivationScheduler();
	        scheduler.scheduleAtFixedRate(as, 0, interval , TimeUnit.SECONDS);

	        Chronos chronos = new Chronos();
	        scheduler.scheduleAtFixedRate( chronos , 0, 1, TimeUnit.SECONDS);
	        
	        for ( int x = 1; x <= mainNodesQuant; x++ ) {
	        	scheduler.scheduleAtFixedRate( new MainCluster( pseudoMaxTasks, "S0-A0-G0-I0-T0-A0-RI-0" + x ), 0, pseudoInterval , TimeUnit.SECONDS);
	        }
			
			
		} catch (Exception e) { 
			loggerError( e.getMessage() );
			e.printStackTrace(); 
		}
        
        
	}
	
	@Override
    public void contextDestroyed(ServletContextEvent event) {
		loggerDebug("shutdown");
        scheduler.shutdownNow();
    }
}
