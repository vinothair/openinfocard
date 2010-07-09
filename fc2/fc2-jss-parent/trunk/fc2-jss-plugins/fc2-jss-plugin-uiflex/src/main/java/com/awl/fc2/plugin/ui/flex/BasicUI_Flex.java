package com.awl.fc2.plugin.ui.flex;


import com.awl.logger.Logger;
import com.awl.rd.protocols.messagehandler.IUI_BasicInterface;


public class BasicUI_Flex implements IUI_BasicInterface {
	static Logger log = new Logger(BasicUI_Flex.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
	
	@Override
	public int sendChooseMethod(String xmlAuth) {
		String res =  AirAppControler.getInstance().sendChooseMethod(xmlAuth);
		return Integer.valueOf(res).intValue();
	}

	@Override
	public void sendNotification(String Title,String caption,boolean modal) {
		if("Please insert your pin".equalsIgnoreCase(caption)){
			caption = "Saisissez votre code confidentiel";
		}		
		trace("inform("+Title+", "+caption+", "+modal+")");
		if(!modal){
			AirAppControler.getInstance().sendNotification(caption);
		}else{
			AirAppControler.getInstance().sendModalNotification(caption);
		}

	}

	@Override
	public String sendQuestion(String Title,String question,boolean dspAsPwd){
	trace("question("+Title+", "+question+")");
		if(dspAsPwd){
			question = "*"+question;
		}
		return AirAppControler.getInstance().sendQuestion(question);
		
	}

}
