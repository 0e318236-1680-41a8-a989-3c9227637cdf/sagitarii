package br.cefetrj.sagitarii.teapot;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.cefetrj.sagitarii.teapot.comm.Uploader;


public class Main {
	private static Logger logger = LogManager.getLogger( "br.cefetrj.sagitarii.teapot.Main" ); 
	private static Configurator configurator;
	

	public static void main( String[] args ) {
		try {
			configurator = new Configurator("config.xml");
			configurator.loadMainConfig();

			if ( configurator.useProxy() ) {
				logger.debug("Proxy: " + configurator.getProxyInfo().getHost() );
			}

			if ( args.length == 1 ) {
				if ( args[0].toLowerCase().equals("-gui") ) {
					new SwingFileUploadHTTP( configurator ).runUi();
				} else showGreetings(); 
			} else 

			if ( args.length == 3 ) {
				if ( args[0].toLowerCase().equals("-experiments") ) {
					String userName = args[1];
					String password = args[2];
					
					System.out.println("Listing experiments for user " + userName + "...");
					SagitariiInterface si = new SagitariiInterface(configurator.getHostURL(), userName, password);
					List<Experiment> exps = si.getMyExperiments();
					for ( Experiment exp : exps ) {
						System.out.println( " > " + exp.getTagExec() + " " + exp.getWorkflow() + " " + exp.getElapsedTime() + " " + exp.getStatus() );
					}
				} else
				if ( args[0].toLowerCase().equals("-workflows") ) {
					String userName = args[1];
					String password = args[2];
					
					System.out.println("Listing all Workflows");
					SagitariiInterface si = new SagitariiInterface(configurator.getHostURL(), userName, password);
					List<Workflow> wfls = si.getWorkflows();
					for ( Workflow wfl : wfls ) {
						System.out.println( " > " + wfl.getAlias() + " " + wfl.getOwner() + " " + wfl.getOwnerMail() );
					}
				} else showGreetings();
			} else
				
			if ( args.length == 4 ) {
				if ( args[0].toLowerCase().equals("-create") ) {
					String workflowAlias = args[1];
					String userName = args[2];
					String password = args[3];
					
					System.out.println("Creating new experiment for Workflow " + workflowAlias + "...");
					SagitariiInterface si = new SagitariiInterface(configurator.getHostURL(), userName, password);
					String ret = si.createNewExperiment(workflowAlias);
					System.out.println( "Server response: " + ret );
				} else
				if ( args[0].toLowerCase().equals("-start") ) {
					String experimentSerial = args[1];
					String userName = args[2];
					String password = args[3];
					
					System.out.println("Starting experiment " + experimentSerial + "...");
					SagitariiInterface si = new SagitariiInterface(configurator.getHostURL(), userName, password);
					String ret = si.startExperiment(experimentSerial);
					System.out.println( "Server response: " + ret );
					
				} else {
					String fileName = args[0];
					String relationName = args[1];
					String experimentSerial = args[2];
					String folderName = args[3];
					System.out.println("Uploading data. Wait...");
					new Uploader(configurator).uploadCSV(fileName, relationName, experimentSerial, folderName, configurator.getSystemProperties() );
					System.out.println("Done.");
					System.exit(0);
				} 
			} else {
				showGreetings();
			}
		} catch (Exception e) {
			System.out.println( e.getMessage() );
		}

	}
	
	private static void showGreetings() {
		System.out.println("");
    	System.out.println("Sagitarii Upload Tool v1.1            15/09/2015");
    	System.out.println("Carlos Magno Abreu        magno.mabreu@gmail.com");
		System.out.println("------------------------------------------------------------------------------");
		System.out.println("");
		System.out.println("Use upload <file.csv> <target_table> <experiment_tag> <work_folder> ");
		System.out.println("          | -start <experiment_tag> <username> <password>");
		System.out.println("          | -create <workflow_alias> <username> <password>");
		System.out.println("          | -experiments <username> <password>");
		System.out.println("          | -workflows <username> <password>");
		System.out.println("          | -gui");
		System.out.println("");
		System.out.println("     file.csv       : Your data file (just the file name).");			
		System.out.println("");
		System.out.println("     target_table   : The table where 'file.csv' content will be stored.");
		System.out.println("");
		System.out.println("     experiment_tag : The Experiment serial number.");
		System.out.println("                      You must create an Experiment using Sagitarii");			
		System.out.println("                      and then copy the serial number ( TagExec) ");			
		System.out.println("                      from Sagitarii web interface.");			
		System.out.println("");
		System.out.println("     work_folder    : Full path where 'file.csv' is in.");
		System.out.println("");
		System.out.println("     -start         : Will start Experiment 'experiment_tag' under");
		System.out.println("                      'username' credentials. This user must");			
		System.out.println("                      be a valid user in Sagitarii.");			
		System.out.println("");
		System.out.println("     -create        : Will create a new Experiment for workflow");
		System.out.println("                      'workflow_alias'. This Workflow must exists.");			
		System.out.println("                      This user must be a valid user in Sagitarii.");			
		System.out.println("");
		System.out.println("     -experiments   : List all Experiments for user 'username'.");
		System.out.println("                      This user must be a valid user in Sagitarii.");			
		System.out.println("");
		System.out.println("     -workflows     : List all Workflows.");
		System.out.println("                      This user must be a valid user in Sagitarii.");			
		System.out.println("");
		System.out.println("     -gui           : Will start the Graphical User Interface");
		System.out.println("                      When using the GUI you can leave 'Work Folder' blank.");			
		System.out.println("");
		System.out.println("------------------------------------------------------------------------------");
		System.out.println("Note: You MUST put all files used by 'file.csv' in 'outbox' folder under");
		System.out.println("   this directory BEFORE call Upload Tool. Just create if you can't find it.");
		System.out.println("------------------------------------------------------------------------------");
	}

}
