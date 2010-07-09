package com.awl.rd.applications.map.enroller;

import com.awl.rd.applications.common.message.IMessage;
import com.awl.rd.applications.map.enroller.exceptions.APP_Enroller_Exception_InternalError;

public interface IAPP_Enroller_ExportedFunctions {

	public abstract String stopSession(String sessionId);
	public abstract String startSession();
	public abstract String isCallCompleted(String sessionId,String xml);
	public abstract String destroy(String sessionId);
	public abstract String processMessage(String sessionId,IMessage msg);
	public abstract String selectUser(String sessionId,String id);
	public abstract String createUser(String sessionId,String Id);
	public abstract String commitUser(String sessionId);
	/**
	 * <PERSONALDATA>
	 * <data><NAME></NAME><VALUE></VALUE></data>
	 * </PERSONALDATA>
	 * 
	 * @param personalData
	 * @throws APP_Enroller_Exception_InternalError 
	 */
	public abstract String setPersonalData(String sessionId,String xmlPersonalData);

	/*
	 *<ENROLLER>
	 *<URI_AUTHENT>theURI</URI_AUTHENT>
	 * </ENROLLER>
	 * 
	 */
	public abstract String initEnrollmentMethod(String sessionId,String xmlInit);
	public abstract String getNeededPersonalInformation(String sessionId);
	public abstract String isProcessMessageNeeded(String sessionId);

}