package com.awl.rd.smartcard;

import java.util.Vector;

import com.awl.rd.smartcard.exception.SmartCard_Exception_NO_CARD;
import com.awl.rd.smartcard.exception.SmartCard_Exception_NO_READER;

//import opencard.core.terminal.CardTerminal;

public interface IReaders {

	public abstract Object getSelectedTerminal();

	//public abstract Vector<String> getVectorOfAccessibleReadersForApplet();

	public abstract Vector<String> getVectorOfAccessibleReaders() throws SmartCard_Exception_NO_READER;

	public abstract boolean selectTerminal(String l_strTerminalName) ;
	
	public abstract void close();
	
	
	
	public IAPDU_Bridge createCompatibleAPDUBridge(boolean binitialize) throws SmartCard_Exception_NO_CARD;



}