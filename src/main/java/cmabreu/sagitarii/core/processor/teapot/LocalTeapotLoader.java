package cmabreu.sagitarii.core.processor.teapot;

public class LocalTeapotLoader {
	private static LocalTeapotLoader instance;
	private boolean running = false;
	
	private LocalTeapotLoader() {
		
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void notifyFinish() {
		 running = false;
	}
	
	public static LocalTeapotLoader getInstance() {
		if ( instance == null ) {
			instance = new LocalTeapotLoader();
		}
		return instance;
	}
	
	public void execute() {
		if( running ) {
			return;
		}
		running = true;
		
		LocalTeapot lt = new LocalTeapot( this );
		lt.start();
		
	}
	
	
	
}
