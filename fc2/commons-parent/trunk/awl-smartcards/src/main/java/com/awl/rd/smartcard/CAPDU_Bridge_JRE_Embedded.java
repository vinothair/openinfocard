package com.awl.rd.smartcard;

//import com.atosworldline.rd.tpay.readerapplet.ReaderApplet;

//import com.SUtil.SUtil;


import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardNotPresentException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.ResponseAPDU;
import org.apache.log4j.Logger;

import com.awl.rd.smartcard.exception.SmartCard_Exception_NO_CARD;
import com.awl.rd.smartcard.exception.SmartCard_Exception_UNKNOW;
import com.utils.SUtil;
import com.utils.Utils;

public class CAPDU_Bridge_JRE_Embedded implements IAPDU_Bridge {
	static Logger logger = Logger.getLogger(CAPDU_Bridge_JRE_Embedded.class);
	static public void trace(Object obj){
		System.out.println("CAPDU_Bridge_JRE_Embedded : " + obj);
		logger.trace(obj);
	}
	static public void warning(Object msg){
		System.out.println(msg);
		logger.warn(msg);
	}
	static public void err(Object msg){
		System.err.println(msg);
		logger.error(msg);
	}
	public static String GET_RESPONSE_CMD       = "00C00000";
	CardTerminal m_Terminal = null;
	CardChannel m_channel = null;
	Card m_card;
	byte m_bytStatus[] = new byte[2];
	byte m_bytData[] = null;
	int state=0;

	public void internalInit(String proto) throws SmartCard_Exception_NO_CARD{
		try{
			//m_Terminal.open();
			m_card= m_Terminal.connect(proto);
			/*if(!wait4Card(m_Terminal, 3000)){
			      System.out.println("Card not found");
			      return;
			    }*/
		}catch(CardNotPresentException e){
			throw(new SmartCard_Exception_NO_CARD(e.getMessage()));
		
			
		}
		catch(CardException e){
			e.printStackTrace();
			state = SW_CARD_UNKOWN_ERROR;
			return;
		}
		 //  open channel
	    System.out.print("Openning slot channel");
	  //  System.out.println(m_Terminal.isSlotChannelAvailable(0));
	    try{
	    	m_channel = (CardChannel)m_card.getBasicChannel();
	    	m_card.getATR();
	     // System.out.println(m_Terminal.isSlotChannelAvailable(0));
	    }catch(Exception e){
	      e.printStackTrace();
	      System.out.println("Openning did not succeed");
	      return;
	    }
	    state = SW_CARD_READY;
	    System.out.println("Openning succeed");
	}
	public void Initialize(Object[] objectNeeded) throws SmartCard_Exception_NO_CARD {
		trace("Initialize");
		
		m_Terminal = (javax.smartcardio.CardTerminal)((IReaders)objectNeeded[0]).getSelectedTerminal();
		System.out.println("Waiting for card on " + m_Terminal.getName() +"...");
		internalInit("*");
		/*try {
			internalInit("T=0");
		} catch (Exception e) {
			try {
				internalInit("T=1");
			} catch (Exception e2) {
				e2.printStackTrace();
				System.exit(666);
			}
			
		}*/
		
	}

	public byte[] SendAPDU(int l_bcla, int l_bins, int l_bp1, int l_bp2,
			int l_blc, byte[] buffer, int lengthBuffer, int offeset, int le) {
		// TODO Auto-generated method stub
		
		byte l_tmpAPDU[] = new byte[5+lengthBuffer];
		if(le!=-1)  l_tmpAPDU = new byte[5+lengthBuffer+1];
		String response;
		l_tmpAPDU[0] = (byte) l_bcla;
		l_tmpAPDU[1] = (byte) l_bins;
		l_tmpAPDU[2] = (byte) l_bp1;
		l_tmpAPDU[3] = (byte) l_bp2;
		l_tmpAPDU[4] = (byte) l_blc;
		
		if(lengthBuffer !=0){
			System.arraycopy(buffer, offeset, l_tmpAPDU, 5, lengthBuffer);
			
		}
		
		 
		
		System.out.println(bytes2String(l_tmpAPDU));
		if(le!=-1)l_tmpAPDU[l_tmpAPDU.length-1] = (byte)le;
		try {
			
			ResponseAPDU rApdu = m_channel.transmit(new CommandAPDU(l_tmpAPDU));
			m_bytStatus[0] = (byte) rApdu.getSW1();
			m_bytStatus[1] = (byte) rApdu.getSW2();
			SUtil.displayError(m_bytStatus);
			if((byte)rApdu.getSW1() == (byte)0x61) {   
			     if((byte)rApdu.getSW2()==0){
			       return null;
			     }
			     String getResponseCmd = GET_RESPONSE_CMD + bytes2String(new byte[] {(byte) rApdu.getSW2()});
			     rApdu = m_channel.transmit(new CommandAPDU(string2Bytes(getResponseCmd)));
			    }
			    
			    // T=1
			    if((byte)rApdu.getSW1() == (byte)0x90 && (byte)rApdu.getSW2() == 0){
			    	if(rApdu.getData()!=null){
			    		m_bytData = new byte[rApdu.getData().length];
			    		System.arraycopy(rApdu.getData(), 0, m_bytData, 0, m_bytData.length);
			    	}else{
			    		m_bytData = null;
			    	}
			      response = bytes2String(rApdu.getData());
			      System.out.println(response);
			      if(response.startsWith("4100")){
			        response = response.substring(4);
			      }
			      
			      return response.getBytes();
			    }else {
			    /*  throw new Exception(
			          bytes2String(new byte[] {rApdu.sw1(), rApdu.sw2()}));*/
			    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("CAPDU_Bridge_JRE_Embedded::sendAPDU() --");
			e.printStackTrace();
		}
		
		
		
		return l_tmpAPDU;
	}

	public byte[] getResData() {
		// TODO Auto-generated method stub
		return m_bytData;
	}

	public byte[] getResStatus() {
		// TODO Auto-generated method stub
		SUtil.displayError(m_bytStatus);
		return m_bytStatus;
	}

	public void selectApplication(String l_straid) {
		// TODO Auto-generated method stub
		
		String selectResponse="Not done";
		try {
			String cmd = "00A40400";
			if(l_straid.length()<16) cmd+="0";
			cmd += Integer.toHexString(l_straid.getBytes().length).toUpperCase();
			cmd += bytes2String(l_straid.getBytes());
			System.out.println("Selecting application : " + cmd);
			selectResponse = sendAPDUString( cmd);
			System.out.println(selectResponse);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println(selectResponse);
		

	}
//	 private boolean wait4Card(CardTerminal terminal, int timer) throws Exception{ 
//	  //  throws CardTerminalException{
//	    
//	  /*  int counter = 0;
//	    boolean timerRaised = false;
//	    boolean stop = false;
//	    while(!m_Terminal.isCardPresent(0)){
//	      try{Thread.sleep(2000);}catch(Exception e){;}
//	      if(!stop && timer>0 && counter == timer*5){
//	        timerRaised = true;
//	        break;
//	      }
//	      counter++;
//	    }
//	    
//	    if(timerRaised){return false;}  
//	    return true;*/
//		 System.out.println("CAPDU_Bridge_JRE_Embedded::wait4card Not Yet Implemented");
//		 return true;
//
//	  }
	 /**
	   * Convert a String to byte array
	   * @param cmd
	   * @return
	   */
	  public static byte[] string2Bytes(String cmd) {
	    
	    byte data[] = new byte[cmd.length() / 2];
	    for(int i = 0; i < data.length; i++) {
	      int j = i * 2;
	      data[i] = Integer.valueOf(cmd.substring(j, j + 2), 16).byteValue();
	    }
	 
	    return data;
	  }
	  /**
	   * Convert a byte array to string
	   * @param array
	   * @return
	   */
	  public static String bytes2String(byte array[]) {
	    
	    StringBuffer sb = new StringBuffer();
	    try {
	      for(int i = 0; i < array.length; i++) {
	       String v = Integer.toHexString(array[i]).toUpperCase();
	       if(v.length() < 2){ v = "0" + v; }
	       sb.append(v.substring(v.length() - 2));
	      }
	    } catch(Exception ex_6) {
	      sb = new StringBuffer("");
	    }
	    return sb.toString();
	  }

	  public String sendAPDUString(String l_strADPU) throws Exception {
		// TODO Auto-generated method stub
		  trace("sendADPDUString("+l_strADPU+")");
		  byte [] l_bytAPDU = string2Bytes(l_strADPU);
		  SUtil.printBufferBytes(l_bytAPDU);
		  CommandAPDU l_cmd = new CommandAPDU(l_bytAPDU);
		  ResponseAPDU rApdu = m_channel.transmit(l_cmd);
		  m_bytStatus[0] =(byte) rApdu.getSW1();
		  m_bytStatus[1] =(byte) rApdu.getSW2();
		  m_bytData = rApdu.getData();
		  SUtil.displayError(m_bytStatus);
		// ResponseAPDU rApdu = m_channel.sendAPDU(
		//	        new CommandAPDU(string2Bytes(l_strADPU)));
		  return Utils.getHexString(m_bytStatus);
			       
			    // T=0
//			    if(rApdu.getSW1() == (byte)0x61) {   
//			     if(rApdu.getSW2()==0){
//			       return "";
//			     }
//			     String getResponseCmd = GET_RESPONSE_CMD + bytes2String(new byte[] {(byte) rApdu.getSW2()});
//			     rApdu = m_channel.transmit(new CommandAPDU(string2Bytes(getResponseCmd)));
//			     m_bytData = rApdu.getData();
//			    }
//			    
//			    // T=1
//			    if((byte)rApdu.getSW1() == (byte)0x90 && (byte)rApdu.getSW2() == 0){         
//			      String response = bytes2String(rApdu.getData());
//			      m_bytData = rApdu.getData();
//			      if(response.startsWith("4100")){
//			        response = response.substring(4);
//			      }
//			      return response;
//			    }else {
//			      throw new Exception(
//			          bytes2String(new byte[] {(byte) rApdu.getSW1(), (byte) rApdu.getSW2()}));
//			    }
			   
	}

	public boolean selectApplication(byte[] l_bytaid) {
		// TODO Auto-generated method stub
		trace("selectApplication");
		SendAPDU(0, 0xA4, 0x04, 0x00, l_bytaid.length, l_bytaid, l_bytaid.length, 0, -1);
		if(m_bytStatus[0] == (byte) 0x90 &&
				m_bytStatus[1] == (byte) 0x00)
		{
			return true;
		}else
			return false;
	}

	//@Override
	public byte[] getATR() {
		// TODO Auto-generated method stub
		return m_card.getATR().getBytes();
	}
	public int getState() {
		// TODO Auto-generated method stub
		return state;
	}
	@Override
	public byte[] transmitControlCommand(int code, byte[] control) throws SmartCard_Exception_UNKNOW {
		trace("transmitControlCommand("+code+","+SUtil.bytes2String(control)+")");
		try {
			return m_card.transmitControlCommand(code, control);
		} catch (CardException e) {
			throw(new SmartCard_Exception_UNKNOW(e.getMessage()));
		}
	}
	
}
