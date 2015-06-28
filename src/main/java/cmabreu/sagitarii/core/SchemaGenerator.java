package cmabreu.sagitarii.core;

import java.util.ArrayList;
import java.util.List;

public class SchemaGenerator {
	
	/**
	 * Gera uma lista de valores baseado em uma string CSV.
	 * 
	 */
	public static List<String> generateList( String csvList, String separator ) {
		List<String> list = new ArrayList<String>(  );
		String[] result = csvList.split( separator );
		for(String s : result){
			list.add(s);
		}
		return list;
	}
	
	
	
	/**
	 * Gera um esquema de criação de tabela (DDL).
	 * 
	 */
	public static String generateSchema( String tableName, List<TableAttribute> attributes ) {
		String retorno = "CREATE TABLE " + tableName + "( ";
		String primaryKey = " CONSTRAINT " + tableName + "_pkey PRIMARY KEY (index_id)";
		String foreignKeyEX = " CONSTRAINT " + tableName +  "_fkex FOREIGN KEY (id_experiment) REFERENCES experiments (id_experiment)";
		String foreignKeyAC = " CONSTRAINT " + tableName +  "_fkac FOREIGN KEY (id_activity) REFERENCES activities (id_activity)";
		String foreignKeyPI = " CONSTRAINT " + tableName +  "_fkpi FOREIGN KEY (id_instance) REFERENCES instances (id_instance)";
		String foreignKeyFiles = "";
		 
		String attributeDef = "index_id serial NOT NULL,id_experiment integer,id_activity integer,id_instance integer,";
		for( TableAttribute attr : attributes ) {
			switch ( attr.getType() ) {
				case FILE : 
					attributeDef = attributeDef + attr.getName() +  " integer,"; 
					foreignKeyFiles = foreignKeyFiles + 
							" CONSTRAINT " + attr.getName() +  "_fkfile FOREIGN KEY ("+attr.getName()+") REFERENCES files (id_file), ";
					
					break;
				case INTEGER : attributeDef = attributeDef + attr.getName() +  " integer,"; break;
				case STRING : attributeDef = attributeDef + attr.getName() +  " character varying(250),"; break;
				case FLOAT : attributeDef = attributeDef + attr.getName() +  " numeric,"; break;
				case TEXT : attributeDef = attributeDef + attr.getName() +  " text,"; break;
				case DATE : attributeDef = attributeDef + attr.getName() +  " date,"; break;
				case TIME : attributeDef = attributeDef + attr.getName() +  " time,"; break;
			}
		}
		if (attributeDef.length() > 0 ) {
			retorno = retorno + attributeDef + primaryKey + "," + foreignKeyEX + "," + foreignKeyFiles + foreignKeyAC + "," + foreignKeyPI + ")";
			return retorno;
		}
		return "";
	}

	
}
