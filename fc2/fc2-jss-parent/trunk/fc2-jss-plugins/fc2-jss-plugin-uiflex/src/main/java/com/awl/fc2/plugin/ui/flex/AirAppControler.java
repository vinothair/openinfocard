/*
 * Copyright (c) 2010, Atos Worldline - http://www.atosworldline.com/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names xmldap, xmldap.org, xmldap.com nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.awl.fc2.plugin.ui.flex;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JOptionPane;

import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.launcher.Config;
import com.awl.logger.Logger;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;



/**
 * This class is the link between an external program handeling the user interface (In our case a AIR application). Nevertheless, if other applications implements the same behaviours new user interface can be done.
 * The AirAppControler also use the singleton pattern, please use, {@link AirAppControler#getInstance()} to get the instance.<br/>
 * Here is describred how its work : <br/>
 * AirAppControl opens a serversocket and wait for the UI to connect.<br/>
 * Then the AirAPPconectrol can send different command and wait for the answer<br/>
 * 
 * @author Cauchie st�phane
 *
 */
public class AirAppControler extends Thread{
	public static  String CEVENT_SETVISIBLE = "setVisible";
	
	public static String CEVENT_READER_CHECKING = "CheckingReader";
	public static String CEVENT_READER_NOT_AVIABLE = "NoReader";
	public static String CEVENT_NOCARD = "NoCard";
	public static String CEVENT_READER_AVIABLE = "ReaderOk";
	public static String CEVENT_CARD_OK = "CardOk";
	public static String CEVENT_ENTER_PIN ="PIN_ASKED";
	public static String CEVENT_SECURE_CODE_EXTRACTION ="SC_Extraction";
	public static String SECURE_CODE_VERIFICATION ="SC_Verif";
	public static String CEVENT_AUTHENTIFICATION_SUCCEED ="AUTH_SUCCESS";
	public static String CEVENT_AUTHENTIFICATION_FAILED = "AUTH_FAILED";
	
	public static String CEVENT_CLOSE = "Close";
	public static String CEVENT_SELECTCARD = "SELECT_CARD";
	public static String CEVENT_SELECTSETOFCARDS= "SELECT_SETOF_CARDS";
	public static String CEVENT_AGREEONCLAIMS = "AGREE_ONCLAIMS";
	public static String CEVENT_QUESTION = "QUESTION";
	public static String CEVENT_NOTIFICATION = "NOTIFICATION";
	public static String CEVENT_CHOOSEAUTHENTICATION_METHOD = "CHOOSEMETHOD";
	public static String CEVENT_OPEN = "OPEN";
	public static String CEVENT_MODALBOX = "MODALBOX";
	
	public static String CEVENT_ADDCONSOLEMSG = "AddingConsoleMessage";
	public static String CEVENT_CLEARCONSOLE = "ClearConsoleMessage";
	
	public static String CEVENT_DISPLAY_REQUESTED_CLAIMS = "DISPLAY_REQUESTED_CLAIMS";
	
	//BEID SPECIFIC EVENT
	public static String CEVENT_SET_IDDATA ="SETIDATA";
	String response ="";
	
	ServerSocket socket;
	Socket incoming;
	BufferedReader readerIn;
	PrintStream printOut;
	
	byte b[] = new byte[1024];
	static AirAppControler s_this;
	boolean allreadyinit = false;
	static Logger log = new Logger(AirAppControler.class);
	static public void trace(Object msg){
		log.trace(msg);
	}
	/**
	 * Empty constructor
	 */
	protected AirAppControler() {}
	/**
	 * 
	 * @return the unique instance of the AirAppControler
	 */
	static public AirAppControler getInstance(){
		if(s_this==null)
			s_this = new AirAppControler();
		return s_this;
	}
	Process p=null;
	public void killFlex(){
		if(p!=null){
			p.destroy();
		}
	}
	
	public void sendDisplayToken(){
		sendEvent(CEVENT_DISPLAY_REQUESTED_CLAIMS, "");
	}
	
	/**
	 * Initaliaze the UI external program. <br/>
	 * The program to launch is describe in the Config_Selecteur.xml file at {@code <FLEXUI>} balise
	 * 
	 */
	public void initControler(){
		if(!allreadyinit)
		{
			if(true){
				try {
				String exe = XMLParser.getFirstValue(Config.getInstance().getXML(),"FLEXUI");
				trace("initContoller with " + exe);
				p = Runtime.getRuntime().exec(exe);
				} catch (IOException e) {				
					e.printStackTrace();
				} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
					e.printStackTrace();
				} catch (Config_Exception_NotDone e) {
					e.printStackTrace();
				}
			}
			
			init();
			allreadyinit = true;
		}
	}
	

	/**
	 * Just for test
	 */
	public void testEvent(){
		sendClose();		
		input();
		
		sendOpen();
		input();
		
		sendModalNotification("modal notif");
		
		/////
		String xmlCards =  "<?xml version=\"1.0\"?>"+
		"<methods>"+
		"<item>"+
			"<url>https://rentacar.atosworldline.bancaire.test.fc2consortium.org/stef/icard_icons/permis.PNG</url>"+
			"<info> Permis de conduire</info>"+
			"<title> Permis </title>"+
			"<sendBack>Choose:0</sendBack>"+
		"</item>"+												
		"<item>"+
			"<url>https://rentacar.atosworldline.bancaire.test.fc2consortium.org/stef/icard_icons/CNIE.PNG</url>"+
			"<title> Carte d identit�</title>"+
			"<info> CNIE</info>"+
			"<sendBack>Choose:1</sendBack>"+
		"</item>"+																										
	"</methods>";
		sendSelectCard(xmlCards);
		input();
		
		String xmlClaims = "<claims>"+
        "<claim>"+
        "<ATT_NAME>Nom</ATT_NAME>"+
        "<ATT_VALUE>Cauchie</ATT_VALUE>"+                        
    "</claim>"+
    "<claim>"+
        "<ATT_NAME>prenom</ATT_NAME>"+
        "<ATT_VALUE>Stephane</ATT_VALUE>"+                        
    "</claim>"+
 "</claims>";
		sendClaims(xmlClaims);
		input();
		
		
		
		traceConsole("TRACE CONSOLE");
		/*sendNotification("Nous allons vous authentifier");
		input();*/
		sendChooseMethod("NOTHING");
		
		sendQuestion("Votre mot de passe ?");
		input();
		sendClose();
		input();
	}
	
	/**
	 * Just for test
	 */
	public void input(){
		System.out.print("press a key : ");
		Scanner in = new Scanner(System.in);
		in.nextLine();
	}
	
	/**
	 * Ask to the UI to display a modal notification
	 * @param notif the notification to be send
	 */
	public void sendModalNotification(String notif){
		if(notif.length() > 200)
			notif = notif.substring(0, 200);
		String data ="<root><Notification>"+
		notif+
        "</Notification></root>";
		sendEvent(CEVENT_MODALBOX, data);
	}
	
	/**
	 * Ask the UI to open (setVisible = true)
	 */
	public void sendOpen(){
		sendEvent(CEVENT_OPEN, "");
	}
	
	/**
	 * Ask the UI to select a card From it. The xml to be send must look like :<br/>
	* {@code <?xml version=\"1.0\"?> }<br/>
	* {@code <methods> }<br/>
	* {@code <item> }<br/>
	* {@code <url>https://rentacar.atosworldline.bancaire.test.fc2consortium.org/map/CAP_CONNECT.PNG</url> }<br/>
	* {@code <info> CAP en mode connect�</info> }<br/>
	* {@code <title> CAP CONNECTED</title> }<br/>
	* {@code <sendBack>Choose:0</sendBack> }<br/>
	* {@code </item> }<br/>
	* {@code </methods> }<br/>
	 * @param xmlMethods
	 * @return the corresponding sendback value of the selected card
	 */
	public String sendChooseMethod(String xmlMethods){
		
		sendEvent(CEVENT_CHOOSEAUTHENTICATION_METHOD, xmlMethods);
		System.out.println("RESPONSE FROM FLEX : " + response);
		return response;
	}
	
	/**
	 * Ask to the UI to dipslay an non modal notification
	 * @param notification
	 */
	public void sendNotification(String notification){
		if(notification.length() > 200)
			notification = notification.substring(0, 200);
		String data ="<root><Notification>"+
			        notification+
			        "</Notification></root>";
		sendEvent(CEVENT_NOTIFICATION, data);
	}
	
	/**
	 * Ask to the UI to display a question, and wait until a response is entered
	 * @param question
	 * @return response
	 */
	public String sendQuestion(String question){
		String data ="<root><Question>"+
			        question+
			        "</Question></root>";
		sendEvent(CEVENT_QUESTION, data);
		System.out.println("RESPONSE FROM FLEX : " + response);
		return response;
	}
	
	/**
	 * Send the claims to be display for selection. If the value are present its for the display token step.<br/>
	 * The xml Must Look like : 
	* {@code <claims> }<br/>
	* {@code <claim> }<br/>
	* {@code <ATT_NAME>Nom</ATT_NAME> }<br/>
	* {@code <ATT_VALUE>Cauchie</ATT_VALUE> }<br/>
	* {@code </claim> }<br/>
	* {@code <claim> }<br/>
	* {@code <ATT_NAME>prenom</ATT_NAME> }<br/>
	* {@code <ATT_VALUE>Stephane</ATT_VALUE> }<br/>
	* {@code </claim> }<br/>
	* {@code </claims> }<br/>
	 * @param xmlClaims the claims (optionaly the values) to be displayed
	 */
	public void sendClaims(String xmlClaims){
		sendEvent(CEVENT_AGREEONCLAIMS,xmlClaims);
	}
	/**
	 * Ask the UI to select a card From it. The xml to be send must look like :<br/>
	* {@code <?xml version=\"1.0\"?> }<br/>
	* {@code <methods> }<br/>
	* {@code <item> }<br/>
	* {@code <url>https://rentacar.atosworldline.bancaire.test.fc2consortium.org/stef/icard_icons/permis.PNG</url> }<br/>
	* {@code <info> Permis de conduire</info> }<br/>
	* {@code <title> Permis </title> }<br/>
	* {@code <sendBack>Choose:0</sendBack> }<br/>
	* {@code </item> }<br/>
	* {@code <item> }<br/>
	* {@code <url>https://rentacar.atosworldline.bancaire.test.fc2consortium.org/stef/icard_icons/CNIE.PNG</url> }<br/>
	* {@code <title> Carte d identit�</title> }<br/>
	* {@code <info> CNIE</info> }<br/>
	* {@code <sendBack>Choose:1</sendBack> }<br/>
	* {@code </item>  }<br/>
	* {@code </methods> }<br/>
	 * @param xmlCards
	 * @return the corresponding sendback value of the selected card
	 */
	public String sendSelectCard(String xmlCards){
		sendEvent(CEVENT_SELECTCARD, xmlCards);
		System.out.println("RESPONSE FROM FLEX : " + response);
		return response;
	}
	
	
	/**
	 * Ask the UI to select a card From it. The xml to be send must look like :<br/>
	* {@code <?xml version=\"1.0\"?> }<br/>
	* {@code <methods> }<br/>
	* {@code <item> }<br/>
	* {@code <url>https://rentacar.atosworldline.bancaire.test.fc2consortium.org/stef/icard_icons/permis.PNG</url> }<br/>
	* {@code <info> Permis de conduire</info> }<br/>
	* {@code <title> Permis </title> }<br/>
	* {@code <sendBack>Choose:0</sendBack> }<br/>
	* {@code </item> }<br/>
	* {@code <item> }<br/>
	* {@code <url>https://rentacar.atosworldline.bancaire.test.fc2consortium.org/stef/icard_icons/CNIE.PNG</url> }<br/>
	* {@code <title> Carte d identit�</title> }<br/>
	* {@code <info> CNIE</info> }<br/>
	* {@code <sendBack>Choose:1</sendBack> }<br/>
	* {@code </item>  }<br/>
	* {@code </methods> }<br/>
	 * @param xmlCards
	 * @return the corresponding sendback value of the selected card
	 */
	public String sendSelectSetOfCards(String xmlCards){
		sendEvent(CEVENT_SELECTSETOFCARDS, xmlCards);
		System.out.println("RESPONSE FROM FLEX : " + response);
		return response;
	}
	
	/**
	 * Ask to the UI to close (setVisible = false)
	 */
	public void sendClose(){
		sendEvent(CEVENT_CLOSE, "");
	}
	
	public void clearConsole(){
		sendEvent(CEVENT_CLEARCONSOLE, "");
	}
	public void traceConsole(String msg){
		sendEvent(CEVENT_ADDCONSOLEMSG, msg);
	}
	
	
	
	/**
	 * send an event with corresponding data. The packet to be send must be on the form : <br/>
	 * {@code <EVENT><TYPE>event</TYPE><DATA>data</DATA></EVENT>}
	 * @param event
	 * @param data
	 */
	private void sendEvent(String event,String data){
		
		//JOptionPane.showConfirmDialog(null, "send");
		
	/*	try {
			System.in.read(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		String toBeSend = "<EVENT><TYPE>"+event+"</TYPE><DATA>"+data+"</DATA></EVENT>";
		System.out.println("Sending : " + toBeSend);
		printOut.println(toBeSend);
		printOut.flush();		
		waitAck();
		//Date date = new Date();
		//while(new Date().)
	}
	
	/**
	 * Initialize the socket and wait for a connection.
	 */
	public void init(){
		trace("initialize");
		int port =3244;// Integer.parseInt(args[0]);
		try {
			socket = new ServerSocket(port);
			//for (;;) {
				trace("Wait for AIR app connection");
				incoming = socket.accept();
				readerIn = new BufferedReader(new InputStreamReader(incoming
						.getInputStream()));
				printOut = new PrintStream(incoming.getOutputStream());
				trace("Connection ok");
			//}
		} catch (Exception e) {
			JOptionPane.showConfirmDialog(null, "Flex UI is allready present, please kill testSelecteur2.exe");
		}
	}
	
	/**
	 * Wait that the UI external program send a StartMessage
	 * 
	 */
	public void waitStartMessage(){
		trace("Wait for Starting authentication message");
		try {
			//System.in.read();
			System.out.println("Wait some data...");
			String received;
			do{
				received = readerIn.readLine();
				if(received==null) {
					
				}
				System.out.println(received);
				
			}while(!received.equalsIgnoreCase("AUTHENTICATION"));
			
			//System.out.println(received);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Not used
	 */
	public void waitPINMessage(){
		trace("Wait for Starting authentication message");
		try {
			//System.in.read();
			System.out.println("Wait some data...");
			String received;
			do{
				received = readerIn.readLine();
				if(received==null) {
					
				}
				System.out.println(received);
			}while(!received.startsWith("PIN"));
			
			//System.out.println(received);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Wait for acknoldge (no expected response)
	 */
	public void waitAck(){
		trace("Wait for acknowledge message");
		try {
			//System.in.read();
			System.out.println("Wait Acknoledge...");
			String received = readerIn.readLine();
			if(received==null) {
				
			}
			response = received;
			System.out.println(received);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Not used
	 */
	synchronized public void go(){
		notify();
	}
	/**
	 * Not used
	 */
	synchronized public void run(){
		init();
		do{
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			run_();
		}while(true);
		
	}
	/**
	 * Not used
	 */
	synchronized public void run_(){
		
		//try {
			//wait(1000);
		
		/*System.out.println("_run de SELMAC");
		if(m_strTypeApplication.equalsIgnoreCase(APP_BEID) )
			runBEID();
		if(m_strTypeApplication.equalsIgnoreCase(APP_CAP))
			runCAP();
		if(m_strTypeApplication.equalsIgnoreCase(APP_ORANGE))
			runOrange();
		if(m_strTypeApplication.equalsIgnoreCase(APP_PERMIS))
			runPermis();
		/*if(Proxy.theThreadToWakeUp != null){
			Proxy.theThreadToWakeUp.wakeup();
		}		*/
		
	/*	} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	public static void main(String arg[]) throws Exception{
		System.out.println("Without Proxy tries");
		Config.getInstance("c:/tempp/cards/Config_Selecteur.xml",true);
		AirAppControler app = new AirAppControler();
		// No thread here
		app.initControler();	
		//app.init();
		//app.run_();
		System.out.println("PRESS a key");
		System.in.read();
		app.testEvent();
	}
}
