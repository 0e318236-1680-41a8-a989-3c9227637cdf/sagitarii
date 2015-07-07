package cmabreu.sagitarii.r;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

public class TextConsole implements RMainLoopCallbacks {
	private boolean canShowLog = false;
	
    public void rWriteConsole(Rengine re, String text, int oType) {
    	if ( canShowLog ) {
    		System.out.print( text );
    	}
    }
    
    public void enableLog( ) {
    	canShowLog = true;
    }
    
    public void disableLog( ) {
    	canShowLog = false;
    }

    public void rBusy(Rengine re, int which) {
    }
    
    public String rReadConsole(Rengine re, String prompt, int addToHistory) {
        return null;
       
    }
    
    public void rShowMessage(Rengine re, String message) {
    	System.out.println( message );
    }
	
    public String rChooseFile(Rengine re, int newFile) {
    	return null;
    }
    
    public void   rFlushConsole (Rengine re) {
    }
	
    public void   rLoadHistory  (Rengine re, String filename) {
    }			
    
    public void   rSaveHistory  (Rengine re, String filename) {
    }
    
}
