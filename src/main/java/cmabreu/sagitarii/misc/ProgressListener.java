package cmabreu.sagitarii.misc;

import cmabreu.sagitarii.misc.ProgressAwareInputStream.OnProgressListener;

public class ProgressListener implements OnProgressListener {
	private int percentage = 0;
	private String fileName;
	
	@Override
	public void onProgress(int percentage, Object tag) {
		this.percentage = percentage;
		this.fileName = (String)tag;
		
		System.out.println( (String)tag + " " + percentage + "%" );
	}

	public int getPercentage() {
		return percentage;
	}
	
	public String getFileName() {
		return fileName;
	}
	
}
