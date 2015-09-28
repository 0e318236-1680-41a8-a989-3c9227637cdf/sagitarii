package br.cefetrj.sagitarii.core;

public class TableAttribute {
	private String name;
	private AttributeType type ;
	private String tableName;
	
	public enum AttributeType {
		INTEGER, STRING, FLOAT, DATE, TIME, FILE, TEXT
	}
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public AttributeType getType() {
		return type;
	}
	public void setType(AttributeType type) {
		this.type = type;
	}

}
