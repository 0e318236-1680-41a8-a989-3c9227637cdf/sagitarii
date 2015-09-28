package cmabreu.sagitarii.sdk;

import java.util.ArrayList;
import java.util.List;
/**
 * This is a entire line of data from your CSV data
 * and holds a list of {@link DataItem}
 * 
 * 
 * @author Carlos Magno O. Abreu : magno.mabreu@gmail.com
 */
public class LineData {
	private List<DataItem> line;

	public LineData() {
		line = new ArrayList<DataItem>();
	}
	
	/**
	 * Adds a new column and its value to the line represented by this object.
	 * If you're holding a list of LineData to represent all CSV file, then you 
	 * need to create this same column and provide a data value for ALL LineData
	 * objects of your list. If you don't, the resulting CSV will be corrupted 
	 * ( one line differs from others )
	 *    
	 * @param columnName
	 * 		The name of the new column you're adding to the CSV.
	 * 
	 * @param value
	 * 		The data for this column
	 */
	public void addValue( String columnName, String value ) {
		addDataItem( new DataItem(columnName, value, line.size() ) );
	}

	/**
	 * Same as {@link #addValue(String columnName, String value)}, but you can provide a column index on
	 * the new {@link DataItem}
	 *  
	 * @param item
	 * 		A {@link DataItem} object representing the column, data and column index.
	 */
	public void addDataItem( DataItem item ) {
		line.add( item );
		for ( int x = 0; x < line.size(); x++ ) {
			line.get(x).columnIndex = x;
		}
	}
	
	/**
	 * Return the internal list of {@link DataItem} objects representing the CSV line. 
	 */
	public List<DataItem> getLine() {
		return line;
	}
	
	/**
	 *	Return a CSV formated string representing the header ( list of columns )
	 *	of the line represented by this object
	 */
	public String getCsvHeader() {
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		String header = "";
		for ( DataItem data : line ) {
			header = header + prefix + data.columnName;
			prefix = ",";
		}
		sb.append( header );
		return sb.toString();
	}

	
	/**
	 *	Return a CSV formated string representing the line ( list of values )
	 *	of the line represented by this object
	 */
	public String getCsvLine() {
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		String csvLine = "";
		
		for ( DataItem data : line ) {
			csvLine = csvLine + prefix + data.data;
			prefix = ",";
		}
		sb.append( csvLine );
		return sb.toString();
	}
	
	
	/**
	 *	Given a column name, returns its index or -1 if not found.
	 */
	public Integer getIndex( String columnName ) {
		for ( DataItem data : line ) {
			if ( data.columnName.equals( columnName ) ) {
				return data.columnIndex;
			}
		}
		return -1;
	}
	
	
	/**
	 *	Given a column name, return the correspondent data
	 *	of the line represented by this object
	 */
	public String getData( String columnName ) {
		for ( DataItem data : line ) {
			if ( data.columnName.equals( columnName ) ) {
				return data.data;
			}
		}
		return "";
	}
	
	/**
	 *	Given an index number, returns the column name.
	 */
	public String getColumn( Integer index ) {
		for ( DataItem data : line ) {
			if ( data.columnIndex == index ) {
				return data.columnName;
			}
		}
		return "";
	}

	/**
	 *	Given an index number, returns the data value.
	 */
	public String getData( Integer index ) {
		for ( DataItem data : line ) {
			if ( data.columnIndex == index ) {
				return data.data;
			}
		}
		return "";
	}
	
}
