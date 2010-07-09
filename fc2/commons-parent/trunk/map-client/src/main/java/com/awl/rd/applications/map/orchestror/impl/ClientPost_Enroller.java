package com.awl.rd.applications.map.orchestror.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Scanner;

import com.awl.rd.applications.common.exceptions.APP_Exception_InternalError;
import com.awl.rd.applications.common.message.IMessage;
import com.awl.rd.applications.map.enroller.Client;
import com.awl.rd.applications.map.enroller.IAPP_Enroller_ExportedFunctions;
import com.awl.rd.applications.map.orchestror.exceptions.APP_Orchestror_Exception_InternalError;

public class ClientPost_Enroller implements IAPP_Enroller_ExportedFunctions{
	public static final String S_FCTNAME = "fctName";
	public static final String S_NBPARAMS = "NBPARAM";
	public static final String S_PREFIX_PARAMS = "PARAM_";
	public static final String URL_MAP = "http://rd-srv-demo.priv.atos.fr:8080/map.ajax-0.0.1-SNAPSHOT/MapEnrollerAction";//"http://localhost:8080/map.ajax/MapEnrollerAction";
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
	        //URL url = new URL("http://rd-srv-demo.priv.atos.fr:8080/map.ajax-0.0.1-SNAPSHOT/MapAction");
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
	@Override
	public String commitUser(String sessionId) {
		return callMapFunction("commitUser", 1, new String[]{sessionId});
	}
	@Override
	public String createUser(String arg0, String arg1) {		
		return callMapFunction("createUser", 2, new String[]{arg0,arg1});
	}
	@Override
	public String destroy(String arg0) {
		return callMapFunction("destroy", 1, new String[]{arg0});
	}
	@Override
	public String getNeededPersonalInformation(String arg0) {
		return callMapFunction("getNeededPersonalInformation", 1, new String[]{arg0});
	}
	@Override
	public String initEnrollmentMethod(String arg0, String arg1) {
		return callMapFunction("initEnrollmentMethod", 2, new String[]{arg0,arg1});
	}
	@Override
	public String isCallCompleted(String arg0, String arg1) {
		return callMapFunction("isCallCompleted", 2, new String[]{arg0,arg1});
	}
	@Override
	public String isProcessMessageNeeded(String arg0) {
		return callMapFunction("isProcessMessageNeeded", 1, new String[]{arg0});
	}
	@Override
	public String processMessage(String arg0, IMessage arg1) {
		// TODO Auto-generated method stub
		return "NOT DONE";
	}
	@Override
	public String selectUser(String arg0, String arg1) {
		return callMapFunction("selectUser", 2, new String[]{arg0,arg1});
	}
	@Override
	public String setPersonalData(String arg0, String arg1) {
		return callMapFunction("setPersonalData", 2, new String[]{arg0,arg1});
	}
	@Override
	public String startSession() {
		return callMapFunction("startSession", 0, new String[]{});
	}
	@Override
	public String stopSession(String arg0) {
		return callMapFunction("stopSession", 1, new String[]{arg0});
	}
	public static String question(String msg){
		System.out.println(msg);
		Scanner in = new Scanner(System.in);
		String response = in.nextLine();		
		return response;
	}
	public static void main(String arg[]) throws APP_Orchestror_Exception_InternalError, APP_Exception_InternalError{
		IAPP_Enroller_ExportedFunctions enroller = new ClientPost_Enroller();
		
		Client client = new Client();
		client.setEnroller(enroller);
		String userId = question("UserID to enroll");
		String uriMethod = question("URI_AUTH ? ");
		client.run(userId,uriMethod);
	}
}
