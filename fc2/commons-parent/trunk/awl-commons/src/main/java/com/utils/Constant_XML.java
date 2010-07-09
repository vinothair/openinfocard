package com.utils;

public class Constant_XML {
	//IDENTIFICATION SERVICE
	
	public static final String XML_BALISE_COMPLETE ="COMPLETE";
	public static final String XML_VALUE_COMPLETE_OK ="OK";
	public static final String XML_VALUE_COMPLETE_NOK ="NOT_OK";
	public static final String XML_BALISE_SESSIONID = "SESSIONID";
	public static final String XML_BALISE_METHOD = "CALL";
	public static final String XML_BALISE_ERROR = "ERROR";
	public static final String XML_BALISE_RETURN_VALUE = "VALUE";
	
	
	//SELECTOR SERVICE
	
	public static final String XML_BALISE_USER ="USER";
	public static final String XML_BALISE_ID = "ID";
	
	//CONFIG
	public static final String XML_BALISE_MAP = "MAP";
	public static final String XML_BALISE_ENTITY_SELECTOR="ENTITY_SELECTOR";
	public static final String XML_BALISE_ENTITY_IDENTITY="ENTITY_IDENTIFICATION";
	public static final String XML_BALISE_ENTITY_AUTHENTICATION="ENTITY_AUTHENTICATION";
	public static final String XML_BALISE_ENTITY_TICKETING="ENTITY_TICKETING";
	public static final String XML_BALISE_ENTITY_ORCHESTROR="ENTITY_ORCHESTROR";
	public static final String XML_BALISE_ENTITY_ENROLLER="ENTITY_ENROLLER";
	
	public static final String XML_BALISE_COMMUNICATION = "COMMUNICATION";
	public static final String XML_BALISE_MODE = "MODE";
	
	public static final String XML_BALISE_KEYSTORE = "KEYSTORE";
	public static final String XML_BALISE_KEYSTORE_LOCATION = "LOCATION";
	public static final String XML_BALISE_KEYSTORE_MDP = "MDP";
	public static final String XML_BALISE_KEYSTORE_ALIAS_SAML2 = "ALIAS_SAML2";
	
	public static final String XML_BALISE_SUPPORTED_MESSAGES ="MESSAGES_UNDERSTANDING";
	public static final String XML_BALISE_MESSAGE_TYPE = "MESSAGE_TYPE";
	public static final String XML_BALISE_URI = "URI";
	public static final String XML_BALISE_INITIALIZATION_CONTEXT = "INITIALIZATION-CONTEXT";
	public static final String XML_BALISE_URI_AUTHENTICATION="URI_AUTHENT";
	public static final String XML_BALISE_URI_CONFIG ="URI_CONFIG";
	
	
	public static final String XML_BALISE_PROTOCOL_STATUS_IN_PROGRESS = "STATUS_IN_PROGRESS";
	public static final String XML_BALISE_PROTOCOL_STATUS_FAILED = "STATUS_FAILED";
	public static final String XML_BALISE_PROTOCOL_STATUS_SUCCEED = "STATUS_SUCCEED";
	
	//CNFG-DB
	/*
	 * <DB>
		<URN>USER</URN>
		<CONNECTOR>
		<TYPE>DB_XSTREAM</TYPE>
		<PARAM>d:/csa/db/Users/</PARAM>
		</CONNECTOR>
		</DB>
	 */
	public static final String XML_BALISE_DB ="DB";
	public static final String XML_BALISE_DB_USER ="USER";
	public static final String XML_BALISE_DB_AUTHENTICATORS ="AUTHENTICATORS";
	public static final String XML_BALISE_DB_SITE ="SITE";
	public static final String XML_BALISE_DB_APPLICATION_CORE = "APPLICATION_CORE";
	public static final String XML_BALISE_DB_T_URN ="URN";
	public static final String XML_BALISE_DB_T_CONNECTOR = "CONNECTOR";
	public static final String XML_BALISE_DB_T_CONNECTOR_TYPE = "TYPE";
	public static final String XML_BALISE_DB_T_CONNECTOR_PARAM ="PARAM";
	
	
	public static String getXMLQuery_CommunicationE2E(String entityA,String entityB){
		//"MAP/ENTITY_SELECTOR/COMMUNICATION/ENTITY_IDENTIFICATION/MODE"
		String toRet=XML_BALISE_MAP;
		toRet += "/" + entityA +"/" + XML_BALISE_COMMUNICATION +"/" + entityB + "/"+XML_BALISE_MODE;
			
		return toRet;
	}
	
}
