package eleicao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EleicaoSplit {
	private static String inputFile;		// args[0]
	private static String workFolder;		// args[1]

	public static void processHeader( String[] columns ) {
		// do nothing with input csv header.
	}

	public static void processLine( String[] lineData ) {
		String fileName = lineData[1].replace("\"", "");
		try {
			File source = new File( workFolder + "/inbox/" + fileName );
			File dest = new File( workFolder + "/sagi_output.txt" );
			copyFile( source, dest );
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) throws Exception {
		
		if ( args.length < 2 ) {
			System.out.println("Missing parameters.");
			System.exit(1);
		}
		
		inputFile = args[0];		// CSV input data file
		workFolder = args[1];		// Working folder 

		List<String> inputData = readFile( inputFile );
		if( inputData.size() > 0 ) {
			for( int x=1; x<inputData.size(); x++ ) {
				String[] lineData = inputData.get(x).split(",");
				processLine( lineData );
			}
		}
	}

	public static List<String> readFile(String file) throws Exception {
		Scanner s = new Scanner( new File(file), "UTF-8" );
		ArrayList<String> list = new ArrayList<String>();
		while (s.hasNext()){
			list.add(s.next());
		}
		s.close();
		return list;
	}

	private static void copyFile(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}
}
