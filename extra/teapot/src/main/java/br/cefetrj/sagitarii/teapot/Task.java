package br.cefetrj.sagitarii.teapot;

/**
 * Copyright 2015 Carlos Magno Abreu
 * magno.mabreu@gmail.com 
 *
 * Licensed under the Apache  License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required  by  applicable law or agreed to in  writing,  software
 * distributed   under the  License is  distributed  on  an  "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the  specific language  governing  permissions  and
 * limitations under the License.
 * 
 */

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Task {
	private List<String> sourceData;
	private List<String> console;
	private List<String> execLog;
	private TaskStatus status;
	private int exitCode;
	private Activation activation;
	private Date realStartTime;
	private Date realFinishTime;
	private Logger logger = LogManager.getLogger( this.getClass().getName() ); 
	private int PID;
	private Double cpuAcc = 0.0;
	private int cpuCount = 0; 
	private Double cpuCost = 0.0;
	
	public Double getCpuCost() {
		return cpuCost;
	}
	
	public void addCpuLevel( Double cpu ) {
		cpuCount++;
		try {
			cpuAcc = cpuAcc + cpu;
			cpuCost = cpuAcc / cpuCount;
		} catch ( Exception e ) {	}
		if ( cpuCost == 0 ) {
			cpuCost = cpu;
		}
	}

	public int getPID() {
		return PID;
	}

	public Date getRealFinishTime() {
		return realFinishTime;
	}

	public Date getRealStartTime() {
		return realStartTime;
	}

	public void setRealFinishTime(Date realFinishTime) {
		this.realFinishTime = realFinishTime;
	}

	public void setRealStartTime(Date realStartTime) {
		this.realStartTime = realStartTime;
	}

	private void error( String s ) {
		execLog.add( s );
		logger.error( s );
	}

	private void debug( String s ) {
		execLog.add( s );
		logger.debug( s );
	}

	public Activation getActivation() {
		return activation;
	}

	public List<String> getSourceData() {
		return sourceData;
	}

	public List<String> getConsole() {
		return console;
	}

	public List<String> getExecLog() {
		return execLog;
	}

	public void setSourceData(List<String> sourceData) {
		this.sourceData = sourceData;
	}

	public TaskStatus getTaskStatus() {
		return this.status;
	}

	public String getApplicationName() {
		return activation.getCommand();
	}

	public String getTaskId() {
		return this.activation.getTaskId();
	}	

	public Task( Activation activation, List<String> execLog ) {
		this.activation = activation;
		status = TaskStatus.STOPPED;
		this.activation = activation;
		this.console = new ArrayList<String>();
		this.execLog = execLog;
	}

	/**
	 * BLOCKING
	 * Will execute a external program (wrapper)
	 * WIll block until task is finished
	 * 
	 */
	public void run( Configurator conf ) {
		Process process = null;
		status = TaskStatus.RUNNING;
		try {
			debug("running external wrapper " + activation.getCommand() );
			//process = Runtime.getRuntime().exec( activation.getCommand() );
			
        	List<String> args = new ArrayList<String>();
        	args.add("/bin/sh");
        	args.add("-c");
        	args.add( activation.getCommand() );			
        	process = new ProcessBuilder( args ).start();
        	
			InputStream in = process.getInputStream(); 
			BufferedReader br = new BufferedReader( new InputStreamReader(in) );
			String line = null;

			PID = 0;
			if( process.getClass().getName().equals("java.lang.UNIXProcess") ) {
				try {
					Field f = process.getClass().getDeclaredField("pid");
					f.setAccessible(true);
					PID = f.getInt( process );
				} catch (Throwable e) {

				}
			}

			InputStream es = process.getErrorStream();
			BufferedReader errorReader = new BufferedReader(  new InputStreamReader(es) );
			while ( (line = errorReader.readLine() ) != null) {
				console.add( line );
				logger.error( line );
			}	
			errorReader.close();

			while( ( line=br.readLine() )!=null ) {
				console.add( line );
				logger.debug( "[" + activation.getActivitySerial() + "] " + activation.getExecutor() + " > " + line );
			}  
			br.close();

			exitCode = process.waitFor();


		} catch ( Exception ex ){
			error( ex.getMessage() );
			for ( StackTraceElement ste : ex.getStackTrace() ) {
				error( ste.toString() );
			}
		}
		status = TaskStatus.FINISHED;
		debug("external wrapper finished.");
	}

	public int getExitCode() {
		return this.exitCode;
	}

	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

}