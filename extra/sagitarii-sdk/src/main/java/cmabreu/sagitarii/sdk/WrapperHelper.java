package cmabreu.sagitarii.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class WrapperHelper {
	private String wrapperAlias;
	private String wrapperFolder;
	private String workFolder;
	
	public WrapperHelper( String wrapperAlias, String wrapperFolder, String workFolder ) {
		this.wrapperAlias = wrapperAlias;
		this.wrapperFolder = wrapperFolder;
		this.workFolder = workFolder;
	}

	public String getInboxFolder() {
		return workFolder + "/inbox/";
	}

	public String getOutboxFolder() {
		return workFolder + "/outbox/";
	}

	public String getWorkFolder() {
		return workFolder + "/";
	}
	
	public String getWrapperFolder() {
		return wrapperFolder;
	}
	
	public List<String> readFromLibraryFolder( String file ) throws Exception {
		System.out.println( "[" + wrapperAlias + "] Read library: " + file );
		ArrayList<String> list = new ArrayList<String>();
		try {
			String line = "";
			file = wrapperFolder + "/" + file;
			BufferedReader br = new BufferedReader( new FileReader( file ) );
			while ( (line = br.readLine() ) != null ) {
			    list.add( line );
			}
			if (br != null) {
				br.close();
			}		
			System.out.println( "[" + wrapperAlias + "] Read library: " + list.size() + " lines" );
		} catch ( Exception e ) {
			System.out.println( "[" + wrapperAlias + "] Error reading library: " + file );
			throw e;
		}
		return list;
	}
	

	public void runExternal( String application ) throws Exception {
		Process process = null;
		System.out.println( "[" + wrapperAlias + "] start external application" );
        try {
        	//process = Runtime.getRuntime().exec( application );
        	List<String> args = new ArrayList<String>();
        	args.add("/bin/sh");
        	args.add("-c");
        	args.add( application );
        	
        	process = new ProcessBuilder( args ).start();
        	
        	BufferedReader reader = new BufferedReader( new InputStreamReader(process.getInputStream() ) );
        	BufferedReader readerErr = new BufferedReader( new InputStreamReader(process.getErrorStream() ) );
            String line="";
            while ((line = reader.readLine()) != null) {
        		System.out.println( "[" + wrapperAlias + ":EXTERNAL] : " + line );
            }
            
        	line="";
            while ((line = readerErr.readLine()) != null) {
        		System.out.println( "[" + wrapperAlias + ":EXTERNAL] : " + line );
            }
           
            process.waitFor();
    		System.out.println( "[" + wrapperAlias + "] Done " );
        } catch ( Exception e ) {
    		System.out.println( "[" + wrapperAlias + "] Error runnig external application at " );
    		System.out.println( application );
    		System.out.println( e.getCause() );
    		for ( StackTraceElement ste : e.getStackTrace() ) {
    			System.out.println( ste.getClassName() );
    		}
			throw e;
        }
    }
	
	public void moveFile(String source, String dest) throws Exception {
		System.out.println( "[" + wrapperAlias + "] Move file " );
		System.out.println( "[" + wrapperAlias + "]  > from " + source);
		System.out.println( "[" + wrapperAlias + "]  > to   " + dest );
		try {
			File src = new File(source);
			File trgt = new File(dest);
			if ( src.exists() ) {
			    Files.copy(src.toPath(), trgt.toPath());
			    src.delete();
				System.out.println( "[" + wrapperAlias + "] Moved");
			} else {
				System.out.println( "[" + wrapperAlias + "] Source file not found");
			}
		} catch ( Exception e ) {
			System.out.println( "[" + wrapperAlias + "] Error when moving file");
			throw e;
		}
	}
	
	public List<String> scanFolder( String folder ) {
		System.out.println( "[" + wrapperAlias + "] Scanning folder " + folder + "..." );
		List<String> folderContent = new ArrayList<String>();
		File file = new File( folder );
	    for ( final File fileEntry : file.listFiles() ) {
	        if ( !fileEntry.isDirectory()) {
	            folderContent.add( fileEntry.getName() );
	        }
	    }
		System.out.println( "[" + wrapperAlias + "] Found " + folderContent.size() + " files. ");
	    return folderContent;
	}
	
	public void copyFile(String source, String dest) throws Exception {
		System.out.println( "[" + wrapperAlias + "] Copy file " );
		System.out.println( "[" + wrapperAlias + "]  > from " + source);
		System.out.println( "[" + wrapperAlias + "]  > to   " + dest );
		try {
			File src = new File(source);
			File trgt = new File(dest);
			if ( src.exists() ) {
			    Files.copy(src.toPath(), trgt.toPath());
			    src.delete();
				System.out.println( "[" + wrapperAlias + "] Copied");
			} else {
				System.out.println( "[" + wrapperAlias + "] Source file not found");
			}
		} catch ( Exception e ) {
			System.out.println( "[" + wrapperAlias + "] Error when copying file");
			throw e;
		}
	}		
	
	
}
