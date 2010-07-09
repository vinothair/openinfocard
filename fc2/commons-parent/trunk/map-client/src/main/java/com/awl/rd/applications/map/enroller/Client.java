package com.awl.rd.applications.map.enroller;

import java.util.Scanner;
import java.util.Vector;

import org.apache.log4j.Logger;

//import com.awl.fc2.selector.authentication.map.ClientPost_Enroller;
import com.awl.rd.applications.common.exceptions.APP_Exception_InternalError;
import com.awl.rd.applications.map.orchestror.exceptions.APP_Orchestror_Exception_InternalError;
import com.utils.XMLParser;

public class Client {

	static Logger logger = Logger.getLogger(Client.class);
	static public void trace(Object obj){		
		logger.info(obj);
	}
	static public void warning(Object msg){
		System.out.println(msg);
		logger.warn(msg);
	}
	static public void err(Object msg){
		System.err.println(msg);
		logger.error(msg);
	}
	IAPP_Enroller_ExportedFunctions enroller = null;
	
	public void setEnroller(IAPP_Enroller_ExportedFunctions enroller){
		this.enroller = enroller;
	}
	public void run(String userID, String uriAuthenticationMethod) throws APP_Exception_InternalError{
		trace("running the enrollment of "+userID+" with the method : "+uriAuthenticationMethod);
		String response = "";
		String sessionId;
		response = enroller.startSession();
		sessionId = XMLParser.getSessionIDFromResponse(response);
		
		
		
		
		response = enroller.initEnrollmentMethod(sessionId,"<ENROLLER><URI_AUTHENT>ALG_AUTH_CAP</URI_AUTHENT></ENROLLER>");
		if(!XMLParser.isCallCompleted(response)){
			return;
		}
		
		response = enroller.getNeededPersonalInformation(sessionId);
		if(!XMLParser.isCallCompleted(response)){
			return;
		}
		String value = XMLParser.getValueFromResponseValue(response);
		String tabData[] = value.replace("[", "").replace("]", "").split(",");
		
		Vector<String> vecURIData = new Vector<String>();
		for(int i=0;i<tabData.length;i++){
			vecURIData.add(tabData[i]);
		}
		trace("We need the following data to be set : "+ vecURIData);
		//String id = question("Pour quel utilisateur (Id)");
		
		
		response = enroller.createUser(sessionId,userID);
		if(!XMLParser.isCallCompleted(response)){
			response = enroller.selectUser(sessionId,userID);
			if(!XMLParser.isCallCompleted(response)){
				return;
			}
		}
				
		
		
		
		String request = "<PERSONALDATA>";
		String tmplData = "<data><NAME>URI</NAME><VALUE>THEVALUE</VALUE></data>";
		
		for(String uri : vecURIData){
			String toAdd = tmplData.replace("URI", uri);
			String valueD = question("->"+uri+" :");
			request += toAdd.replace("THEVALUE", valueD);
			
			
		}
		request += "</PERSONALDATA>";
		
		

		response = enroller.setPersonalData(sessionId,request);
		if(!XMLParser.isCallCompleted(response)){
			return;
		}
		response = enroller.commitUser(sessionId);
		if(!XMLParser.isCallCompleted(response)){
			return;
		}
		
	}
	public static String question(String msg){
		System.out.println(msg);
		Scanner in = new Scanner(System.in);
		String response = in.nextLine();		
		return response;
	}
	/**
	 * @param args
	 * @throws APP_Orchestror_Exception_InternalError 
	 * @throws APP_Exception_InternalError 
	 */
	public static void main(String[] args) throws APP_Orchestror_Exception_InternalError, APP_Exception_InternalError {
//		IAPP_Enroller_ExportedFunctions enroller = new ClientPost_Enroller();
//		
//		Client client = new Client();
//		client.setEnroller(enroller);
//		String userId = question("UserID to enroll");
//		String uriMethod = question("URI_AUTH ? "); //ALG_AUTH_CAP
//		client.run(userId,uriMethod);
		
	}

}
