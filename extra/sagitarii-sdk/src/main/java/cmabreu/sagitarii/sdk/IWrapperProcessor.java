package br.cefetrj.sagitarii.sdk;

import java.util.List;

/**
 * Implement this interface to create a wrapper processor that will
 * be passed to the Wrapper engine.
 * 
 * The Wrapper engine encapsulates all complex Sagitarii-related stuffs
 * so you can concentrate in you own job.
 *
 * @author Carlos Magno O. Abreu : magno.mabreu@gmail.com
 */
public interface IWrapperProcessor {
	/**
	 * This method will be called for each line found in the CSV input file.
	 * @param helper {@link WrapperHelper}: 
	 * 		Contains some methods to help you on copy files, reading text files
	 * 		from library folder and so on.
	 * 
	 * @param lineData {@link LineData}:
	 * 		Contains a list of {@link DataItem} ( a CSV line ) witch holds a data item 
	 * 		of the line ( column name, the data for that column at that line and the column index).
	 * 
	 */
	void processLine( LineData lineData, WrapperHelper helper ) throws Exception;
	/**
	 * This event will be fired when all CSV file was read and processed. 
	 */
	void onProcessFinish();
	/**
	 * this event will be fired just before the CSV starting to process.
	 * @param headerData
	 * 		Contains the header of the CSV file.
	 */
	void onProcessBeforeStart( LineData headerData );
	/**
	 * This event will be fired when you call Wrapper.save().
	 * You must provide a list of strings representing your CSV output data file.
	 */
	List<String> onNeedOutputData();
}
