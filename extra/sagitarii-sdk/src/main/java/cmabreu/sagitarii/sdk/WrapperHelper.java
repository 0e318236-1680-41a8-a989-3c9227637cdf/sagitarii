package cmabreu.sagitarii.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * This class will helps you with some useful tools.  
 * Will be passed to the processor {@link IWrapperProcessor#processLine( LineData lineData, WrapperHelper helper )} 
 * when processing each CSV line
 *  
 * @author Carlos Magno O. Abreu : magno.mabreu@gmail.com
 */
public class WrapperHelper {
	private String wrapperAlias;
	private String wrapperFolder;
	private String workFolder;
	
	public WrapperHelper( String wrapperAlias, String wrapperFolder, String workFolder ) {
		this.wrapperAlias = wrapperAlias;
		this.wrapperFolder = wrapperFolder;
		this.workFolder = workFolder;
	}

	/**
	 *	Returns the complete path to the activity instance inbox under exclusive work folder used by Teapot.  
	 */
	public String getInboxFolder() {
		return workFolder + "/inbox/";
	}

	/**
	 *	Returns the complete path to the activity instance outbox under exclusive work folder used by Teapot.  
	 */
	public String getOutboxFolder() {
		return workFolder + "/outbox/";
	}

	/**
	 *	Returns the complete path to the activity instance exclusive work folder used by Teapot.  
	 */
	public String getWorkFolder() {
		return workFolder + "/";
	}
	
	/**
	 *	Returns the complete path to the wrappers folder used by Teapot.  
	 */
	public String getWrapperFolder() {
		return wrapperFolder + "/";
	}
	
	/**
	 *	Read a text file from wrappers folder.  
	 */
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
	
	/**
	 *	Execute an external application  
	 */
	public void runExternal( String application ) throws Exception {
		Process process = null;
		System.out.println( "[" + wrapperAlias + "] Start " + application );
        try {
        	process = Runtime.getRuntime().exec( application );
        	BufferedReader reader = new BufferedReader( new InputStreamReader(process.getInputStream() ) );
            String line="";
            while ((line = reader.readLine()) != null) {
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
	
	/**
	 * If you need to move files
	 */
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
	
	/**
	 * If you need to copy files
	 */
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
