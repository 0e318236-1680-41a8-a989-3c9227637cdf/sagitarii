package br.cefetrj.sagitarii.core;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import br.cefetrj.sagitarii.core.config.Configurator;
import br.cefetrj.sagitarii.core.filetransfer.FileReceiverManager;
import br.cefetrj.sagitarii.core.processor.MainCluster;
import br.cefetrj.sagitarii.core.statistics.AgeCalculator;
import br.cefetrj.sagitarii.core.types.UserType;
import br.cefetrj.sagitarii.metrics.Chronos;
import br.cefetrj.sagitarii.misc.PathFinder;
import br.cefetrj.sagitarii.misc.json.GSONThreadLocalImmolater;
import br.cefetrj.sagitarii.persistence.entity.Domain;
import br.cefetrj.sagitarii.persistence.entity.Experiment;
import br.cefetrj.sagitarii.persistence.entity.User;
import br.cefetrj.sagitarii.persistence.exceptions.NotFoundException;
import br.cefetrj.sagitarii.persistence.infra.ConnFactory;
import br.cefetrj.sagitarii.persistence.repository.RelationRepository;
import br.cefetrj.sagitarii.persistence.services.ExperimentService;
import br.cefetrj.sagitarii.persistence.services.UserService;

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
    	loggerDebug("--------------------------------------");
    	loggerDebug("Sagitarii Workflow Data Science System");
    	loggerDebug("CEFET-RJ                          2015");
    	loggerDebug("Carlos M. Abreu magno.mabreu@gmail.com");
    	loggerDebug("--------------------------------------");
    	
    	ServletContext context = event.getServletContext();
    	System.setProperty("rootPath", context.getRealPath("/") );

    	UserService us;
    	try {

	        int interval = 5;
	        int pseudoInterval = 5;
	        int pseudoMaxTasks = 4;
	        int mainNodesQuant = 1;
	        int maxInputBufferCapacity = 500;
	        int fileReceiverPort = 3333;
	        int chunkBuffer = 100;
	        
			Configurator config = Configurator.getInstance("config.xml");
			
			interval = config.getPoolIntervalSeconds();
			pseudoInterval = config.getPseudoClusterIntervalSeconds();
			pseudoMaxTasks = config.getPseudoMaxTasks();
			pseudoMaxTasks = config.getPseudoMaxTasks();
			maxInputBufferCapacity = config.getMaxInputBufferCapacity();
			mainNodesQuant = config.getMainNodesQuant();
			fileReceiverPort = config.getFileReceiverPort();
			chunkBuffer = config.getFileReceiverChunkBufferSize();
			String user = config.getUserName();
			String passwd = config.getPassword();
			String database = config.getDatabaseName();
    		
    		ConnFactory.setCredentials(user, passwd, database);

    		try {
        		AgeCalculator.getInstance().retrieveList();
        	} catch ( NotFoundException e ) {
        		//
        	} catch ( Exception e ) {
        		loggerError("Critical database initialization error: " + e.getMessage() );
        		return;
        	}
    		
			us = new UserService();
			try {
				us.getList().size();
			} catch (NotFoundException ignored ) {
				// No users found. We need an Admin!
				User usr = new User();
				usr.setFullName("System Administrator");
				usr.setLoginName("admin");
				usr.setType( UserType.ADMIN );
				usr.setPassword("admin");
				usr.setUserMail("no.mail@localhost");
				us.newTransaction();
				us.insertUser(usr);
				loggerDebug("System Administrator created");
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
			} 
			loggerDebug("done.");
			
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

			loggerDebug("File Receiver Manager started.");

			scheduler = Executors.newSingleThreadScheduledExecutor();
			
			MainHeartBeat as = new MainHeartBeat();
	        scheduler.scheduleAtFixedRate(as, 0, interval , TimeUnit.SECONDS);

	        Chronos chronos = new Chronos();
	        scheduler.scheduleAtFixedRate( chronos , 0, 1, TimeUnit.SECONDS);
	        
	        for ( int x = 1; x <= mainNodesQuant; x++ ) {
	        	scheduler.scheduleAtFixedRate( new MainCluster( pseudoMaxTasks, "S0-A0-G0-I0-T0-A0-RI-0" + x ), 0, pseudoInterval , TimeUnit.SECONDS);
	        }
			
		} catch (Exception e) { 
			System.out.println( e.getMessage() );
			loggerError( e.getMessage() );
			e.printStackTrace(); 
		}
        
        
	}
	
	@Override
    public void contextDestroyed(ServletContextEvent event) {
		loggerDebug("shutdown");
        scheduler.shutdownNow();
		loggerDebug("immolating GSON threads...");
		int result = 0;
		try {
			result = GSONThreadLocalImmolater.immolate();
		} catch ( Exception e ) {
			loggerError( e.getMessage() );
		}
		loggerDebug("done: " + result + " threads killed.");
		try {
			FileReceiverManager.getInstance().stopServer();
		} catch (Exception e) {
			//
		}
        
    }
}
