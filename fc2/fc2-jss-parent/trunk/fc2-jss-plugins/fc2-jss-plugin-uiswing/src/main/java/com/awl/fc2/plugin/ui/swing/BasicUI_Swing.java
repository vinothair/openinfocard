package com.awl.fc2.plugin.ui.swing;

import com.awl.fc2.selector.userinterface.swing.Dialog;
import com.awl.fc2.selector.userinterface.swing.Dialog_ModalNotif;
import com.awl.fc2.selector.userinterface.swing.MainWindow;
import com.awl.logger.Logger;
import com.awl.rd.protocols.messagehandler.IUI_BasicInterface;
import com.utils.XMLParser;
import com.utils.execeptions.XMLParser_Exception_NO_ATTRIBUTE;
import com.utils.execeptions.XMLParser_Exception_NoNextValue;

public class BasicUI_Swing implements IUI_BasicInterface {
	static Logger log = new Logger(BasicUI_Swing.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
	
	@Override
	public int sendChooseMethod(String xmlAuth) {
		XMLParser parser = new XMLParser(xmlAuth);
		
		try {
			parser.query("URI_CONFIG");
		} catch (XMLParser_Exception_NO_ATTRIBUTE e) {
			trace("Failed parsing xmlAuth return 0");
			return 0;
		}
		trace("Quelle methode ? ");
		int cpt=0;
		while(parser.hasNext()){
			try {
				trace(cpt++ + ") : " + parser.getNextValue());
			} catch (XMLParser_Exception_NoNextValue e) {
				trace("Failed getting method return 0");
				return 0;
			}
		}
		//Scanner in = new Scanner(System.in);
		//String response = in.nextLine();
		String response = "0";
		int idx = Integer.valueOf(response).intValue();
		return idx;
	}

	@Override
	public void sendNotification(String Title, String caption, boolean modal) {
		
		if("Please insert your pin".equalsIgnoreCase(caption)){
			caption = "Saisissez votre code confidentiel";
		}		
		trace("inform("+Title+", "+caption+", "+modal+")");
		if(modal){
			final Dialog_ModalNotif inform = new Dialog_ModalNotif(MainWindow.getInstance());
			inform.settings(Title, caption);
		}else{
			MainWindow.getInstance().traceConsole(caption);
		}
	}

	@Override
	public String sendQuestion(String title, String question,boolean dspAsPwd) {
		
		trace("["+title+"]"+question);
		MainWindow.getInstance().wakeupProc();
		final Dialog jd = new Dialog();
		jd.settings(title,question,dspAsPwd);
		String response = jd.getResponse();
		
		return response;
	}

}
