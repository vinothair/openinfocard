package org.xmldap.extentions;



public interface IOpenInfoCardPlugin {
	public static String PLG_INFOCARD_USERCREDENTIAL_EXTENTIONS = "PLG_INFOCARD_USERCREDENTIAL_EXTENTIONS";

	
	
	public String getType();
	public String getName();
	public void install();
	
}
