package com.awl.rd.applications.map.orchestror;

import javax.jws.WebService;

import com.awl.rd.applications.common.IAPPExportedFunctions;

@WebService
public interface IAPP_Orchestror_ExportedFunctions extends IAPPExportedFunctions{

	public abstract String stopSession(String sessionId);

	/* (non-Javadoc)
	 * @see com.awl.rd.applications.map.identification.IAPP_Identification_Exported_Functions#startSession()
	 */
	/* (non-Javadoc)
	 * @see com.awl.rd.applications.authentication.IAPP_Authentication_ExportedFunctions#startSession()
	 */
	public abstract String startSession();

	public abstract String initConnections(String sessionId);

	public abstract String initTransaction(String sessionId, String xmlContext);

	public abstract String getAuthenticationMethods(String sessionId);

	public abstract String initAuthentication(String sessionId,
			String initializationContext);

	public abstract String isComplete(String sessionId);

	public abstract String processMessage(String sessionId, String xmlmsg);

	public abstract String getAuthenticatorResult(String sessionId);

	public abstract String getTicket(String sessionId, String URIToken);

}