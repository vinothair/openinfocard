package com.awl.rd.applications.map.orchestror.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.awl.rd.applications.common.exceptions.APP_Exception_InternalError;
import com.awl.rd.applications.map.orchestror.MAPRunner;
import com.awl.rd.applications.map.orchestror.IAPP_Orchestror_ExportedFunctions;
import com.awl.rd.protocols.messagehandler.impl.BasicUI_Console;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;
import com.utils.execeptions.XMLParser_Exception_NoNextValue;

public class Orchestror_ExportedFunctions_PostVersion implements IAPP_Orchestror_ExportedFunctions {
	public static final String S_FCTNAME = "fctName";
	public static final String S_NBPARAMS = "NBPARAM";
	public static final String S_PREFIX_PARAMS = "PARAM_";
	public static final String URL_MAP = "http://rd.atosworldline.com/MAP2/MapAction";//"http://rd-srv-demo.priv.atos.fr:8080/map.ajax-0.0.1-SNAPSHOT/MapAction";
	//public static final String URL_MAP = "http://localhost:8080/map.ajax/MapAction";//"http://rd-srv-demo.priv.atos.fr:8080/map.ajax-0.0.1-SNAPSHOT/MapAction";
	public String callMapFunction(String fctName,int nbParam,String [] theParams){
		try {
	        // Construct data
	        String data = URLEncoder.encode(S_FCTNAME, "UTF-8") + "=" + URLEncoder.encode(fctName, "UTF-8");
	        data += "&" + URLEncoder.encode(S_NBPARAMS, "UTF-8") + "=" + URLEncoder.encode(Integer.valueOf(nbParam).toString(), "UTF-8");
	        for(int i=0;i<nbParam;i++){
	        	String paramName = S_PREFIX_PARAMS + i;
	        	data += "&" + URLEncoder.encode(paramName, "UTF-8") + "=" + URLEncoder.encode(theParams[i], "UTF-8");
	        }
	        
	        // Send data
	       // URL url = new URL("http://rd-srv-demo.priv.atos.fr:8080/map.ajax-0.0.1-SNAPSHOT/MapAction");
	        URL url = new URL(URL_MAP);
	        URLConnection conn = url.openConnection();
	        conn.setDoOutput(true);
	        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	        wr.write(data);
	        wr.flush();
	    
	        // Get the response
	        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String line;
	        String response ="";
	        while ((line = rd.readLine()) != null) {
	            // Process line...
	        	response += line;
	        }
	        wr.close();
	        rd.close();
	        return response;
	    } catch (Exception e) {
	    }
		return "FAILED";
	    
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println(new ClientPost().startSession());
		IAPP_Orchestror_ExportedFunctions map = new Orchestror_ExportedFunctions_PostVersion();
		MAPRunner testClient = new MAPRunner(new BasicUI_Console());
		testClient.setOrchestror(map);
		String userID = "robert";
		try {
			testClient.run(userID);
		} catch (APP_Exception_InternalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLParser_Exception_NoNextValue e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public String getAuthenticationMethods(String sessionId) {
		return callMapFunction("getAuthenticationMethods", 1, new String[]{sessionId});
	}
	@Override
	public String getAuthenticatorResult(String sessionId) {
		return callMapFunction("getAuthenticatorResult", 1, new String[]{sessionId});
	}
	@Override
	public String getTicket(String sessionId, String URIToken) {
		return callMapFunction("getTicket", 2, new String[]{sessionId,URIToken});
	}
	@Override
	public String initAuthentication(String sessionId,
			String initializationContext) {
		return callMapFunction("initAuthentication", 2, new String[]{sessionId,initializationContext});
	}
	@Override
	public String initConnections(String sessionId) {
		return callMapFunction("initConnections", 1, new String[]{sessionId});
	}
	@Override
	public String initTransaction(String sessionId, String xmlContext) {
		return callMapFunction("initTransaction", 2, new String[]{sessionId,xmlContext});
	}
	@Override
	public String isComplete(String sessionId) {
		return callMapFunction("isComplete", 1, new String[]{sessionId});
	}
	@Override
	public String processMessage(String sessionId, String xmlmsg) {
		return callMapFunction("processMessage", 2, new String[]{sessionId,xmlmsg});
	}
	@Override
	public String startSession() {
		return callMapFunction("startSession", 0, null);
	}
	@Override
	public String stopSession(String sessionId) {
		return callMapFunction("stopSession", 1, new String[]{sessionId});
	}
	

}
