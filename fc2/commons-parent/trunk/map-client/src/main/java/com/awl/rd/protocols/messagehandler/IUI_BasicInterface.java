package com.awl.rd.protocols.messagehandler;

public interface IUI_BasicInterface {
	public void sendNotification(String Title,String caption,boolean modal);
	public String sendQuestion(String Title,String question,boolean dspAsPwd);
	public int sendChooseMethod(String xmlAuth);
	
}