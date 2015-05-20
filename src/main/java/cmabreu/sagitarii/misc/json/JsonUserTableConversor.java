package cmabreu.sagitarii.misc.json;

import java.util.Set;

import cmabreu.sagitarii.core.UserTableEntity;

public class JsonUserTableConversor {
	
	public String asJson( Set<UserTableEntity> userData, int totalRecords, int draw ) {
		StringBuilder builder = new StringBuilder();
		String totalDisplayRecords = String.valueOf( userData.size() );
		String sufix = ",";
		int count = 0;
		builder.append("{\"draw\":\"" + draw + "\",\"iTotalDisplayRecords\":\""+totalRecords+"\",\"iTotalRecords\":\""+totalRecords+"\",\"aaData\":[");
		for ( UserTableEntity ue : userData ) {
			count++;
			builder.append("{");
			String prefix = "";
			for ( String columnName : ue.getColumnNames() ) {
				String value = ue.getData( columnName );
				builder.append( prefix + "\"" + columnName + "\"" + ":" +  "\"" + value + "\"" );
				prefix = ",";
			}
			if ( count == userData.size() ) {
				sufix = "";
			}
			builder.append("}" + sufix);
		}
		
		builder.append("]}");
		
		//System.out.println( builder.toString() );
		
		return builder.toString();
	}
	
	

}
