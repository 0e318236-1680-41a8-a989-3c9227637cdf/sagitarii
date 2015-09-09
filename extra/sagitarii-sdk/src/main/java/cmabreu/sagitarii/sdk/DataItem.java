package br.cefetrj.sagitarii.sdk;

/**
 *	This class will hold the column name, data value and index number of
 *	a CSV item of a line. 
 * 
 * @author Carlos Magno O. Abreu : magno.mabreu@gmail.com
 */
public class DataItem {
	public String columnName;
	public String data;
	public Integer columnIndex;
	
	public DataItem( String columnName, String data, Integer columnIndex ) {
		this.columnName = columnName;
		this.data = data;
		this.columnIndex = columnIndex;
	}
	
	
}
