package org.xmldap.sts.db;


public class DbSupportedClaim {
	public String uri;
	public String columnName;
	public String columnType;
	public DbDisplayTag[] displayTags = null;
	
	public DbSupportedClaim(String uri, String columnName, String columnType,  DbDisplayTag[] displayTags) {
		this.uri = uri;
		this.columnName = columnName;
		this.columnType = columnType;
		this.displayTags = displayTags;
	}
}
