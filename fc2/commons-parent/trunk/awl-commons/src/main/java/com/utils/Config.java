package com.utils;


import com.utils.execeptions.Utils_Exception_Unable_ToRead_File;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;

public class Config {

	public static String getDBAPPCore_ConnectorType() throws XMLParser_Exception_NO_ATTRIBUTE, Utils_Exception_Unable_ToRead_File{
		return getDB_T_ConnectorType(Constant_XML.XML_BALISE_DB_APPLICATION_CORE);						
	}
	
	
	public static String getDBAPPCore_ConnectorParam() throws XMLParser_Exception_NO_ATTRIBUTE, Utils_Exception_Unable_ToRead_File{
		return getDB_T_ConnectorParam(Constant_XML.XML_BALISE_DB_APPLICATION_CORE);					
	}
	
	public static String getDBSite_ConnectorType() throws XMLParser_Exception_NO_ATTRIBUTE, Utils_Exception_Unable_ToRead_File{
		return getDB_T_ConnectorType(Constant_XML.XML_BALISE_DB_SITE);						
	}
	
	
	public static String getDBSite_ConnectorParam() throws XMLParser_Exception_NO_ATTRIBUTE, Utils_Exception_Unable_ToRead_File{
		return getDB_T_ConnectorParam(Constant_XML.XML_BALISE_DB_SITE);					
	}
	
	public static String getDBUser_ConnectorType() throws XMLParser_Exception_NO_ATTRIBUTE, Utils_Exception_Unable_ToRead_File{
		return getDB_T_ConnectorType(Constant_XML.XML_BALISE_DB_USER);						
	}
	
	
	public static String getDBUser_ConnectorParam() throws XMLParser_Exception_NO_ATTRIBUTE, Utils_Exception_Unable_ToRead_File{
		return getDB_T_ConnectorParam(Constant_XML.XML_BALISE_DB_USER);					
	}
	
	public static String getDBAuthenticator_ConnectorParam() throws XMLParser_Exception_NO_ATTRIBUTE, Utils_Exception_Unable_ToRead_File{
		return getDB_T_ConnectorParam(Constant_XML.XML_BALISE_DB_AUTHENTICATORS);
	}
	
	public static String getDB_T_ConnectorType(String dbName) throws XMLParser_Exception_NO_ATTRIBUTE, Utils_Exception_Unable_ToRead_File{
		String query = Constant_XML.XML_BALISE_DB +"/"+
					   dbName+"/"+		
					   Constant_XML.XML_BALISE_DB_T_CONNECTOR +"/"+
		               Constant_XML.XML_BALISE_DB_T_CONNECTOR_TYPE;
		return XMLParser.getFirstValue(Utils.getConfigXML(),query);						
	}
	
	
	public static String getDB_T_ConnectorParam(String dbName) throws XMLParser_Exception_NO_ATTRIBUTE, Utils_Exception_Unable_ToRead_File{
		String query = Constant_XML.XML_BALISE_DB +"/"+
		               dbName+"/"+					   	
					   Constant_XML.XML_BALISE_DB_T_CONNECTOR +"/"+
		               Constant_XML.XML_BALISE_DB_T_CONNECTOR_PARAM;
		return XMLParser.getFirstValue(Utils.getConfigXML(),query);						
	}
	
	public static String getKeyStore() throws XMLParser_Exception_NO_ATTRIBUTE, 
											  Utils_Exception_Unable_ToRead_File{
		String query = Constant_XML.XML_BALISE_KEYSTORE+"/"+Constant_XML.XML_BALISE_KEYSTORE_LOCATION;
		return XMLParser.getFirstValue(Utils.getConfigXML(),query);		
	}	
	public static String getKeyStoreMdp() throws XMLParser_Exception_NO_ATTRIBUTE, 
											  Utils_Exception_Unable_ToRead_File{
		String query = Constant_XML.XML_BALISE_KEYSTORE+"/"+Constant_XML.XML_BALISE_KEYSTORE_MDP;
		return XMLParser.getFirstValue(Utils.getConfigXML(),query);		
	}
	public static String getKeyStoreAlias(String constantAlias) throws XMLParser_Exception_NO_ATTRIBUTE, 
												 Utils_Exception_Unable_ToRead_File{
		String query = Constant_XML.XML_BALISE_KEYSTORE+"/"+constantAlias;
		return XMLParser.getFirstValue(Utils.getConfigXML(),query);		
	}	

	
	
	/**
	 * 
	 * @param args
	 * @throws Utils_Exception_Unable_ToRead_File 
	 * @throws XMLParser_Exception_NO_ATTRIBUTE 
	 */
	public static void main(String[] args) throws XMLParser_Exception_NO_ATTRIBUTE, Utils_Exception_Unable_ToRead_File {
		// TODO Auto-generated method stub
		System.out.println(getDBUser_ConnectorType());
		System.out.println(getDBUser_ConnectorParam());
		System.out.println(getDBSite_ConnectorType());
		System.out.println(getDBSite_ConnectorParam());
		System.out.println(getDBAPPCore_ConnectorType());
		System.out.println(getDBAPPCore_ConnectorParam());
	}

}
