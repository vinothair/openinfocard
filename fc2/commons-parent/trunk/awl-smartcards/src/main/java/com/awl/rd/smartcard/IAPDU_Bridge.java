package com.awl.rd.smartcard;

import com.awl.rd.smartcard.exception.SmartCard_Exception_NO_CARD;
import com.awl.rd.smartcard.exception.SmartCard_Exception_UNKNOW;

public interface IAPDU_Bridge {
	public void Initialize(Object [] l_lstObjectNeeded) throws SmartCard_Exception_NO_CARD;
	public void selectApplication(String l_strAID);
	public boolean selectApplication(byte [] l_bytAID);
	public byte [] SendAPDU(int l_bCLA,
							int l_bINS,
							int l_bP1,
							int l_bP2,
							int l_bLC,
							byte [] l_tabBuffer,
							int l_bLengthBuffer,
							int l_bOffeset,
							int l_bLe);
	public byte [] getResData();
	public byte [] getResStatus();
	public byte [] transmitControlCommand(int code, byte [] control) throws SmartCard_Exception_UNKNOW;
	
	public String sendAPDUString(String l_strADPU) throws Exception;
	public byte [] getATR();
	public int getState();
	public final static int SW_CARD_NOT_PRESENT =1;
	public final static int SW_CARD_PROTO_MISMATCH =2;
	public final static int SW_CARD_UNKOWN_ERROR= 3;
	public final static int SW_CARD_READY = 4;
	
	
}
