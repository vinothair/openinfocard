package org.xmldap.sts.db;

import java.util.Locale;


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
	
	public String getDisplayTag(Locale clientLocale) {
		for (DbDisplayTag displayTag : displayTags) {
			Locale locale;
			String[] LC = displayTag.language.split("_");
			String language = LC[0];
			if (LC.length > 1) {
				String country = LC[1];
				locale = new Locale(language, country);
			} else {
				locale = new Locale(language);
			}
			if (locale.equals(clientLocale)) {
				return displayTag.displayTag;
			}
		}
		return displayTags[0].displayTag;
	}
	
	public Locale getLocale(Locale clientLocale) {
		// do we support clientLocale? Then return clientLocale
		for (DbDisplayTag displayTag : displayTags) {
			Locale locale;
			String[] LC = displayTag.language.split("_");
			String language = LC[0];
			if (LC.length > 1) {
				String country = LC[1];
				locale = new Locale(language, country);
			} else {
				locale = new Locale(language);
			}
			if (locale.equals(clientLocale)) {
				return clientLocale;
			}
		}
		// clientLocale is not supported
		// return locale of first displayTag
		Locale locale;
		String[] LC = displayTags[0].language.split("_");
		String language = LC[0];
		if (LC.length > 1) {
			String country = LC[1];
			locale = new Locale(language, country);
		} else {
			locale = new Locale(language);
		}
		return locale;
	}
}
