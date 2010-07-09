package com.awl.rd.smartcard;




import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

import org.apache.log4j.Logger;

import com.awl.rd.smartcard.exception.SmartCard_Exception_NO_CARD;
import com.awl.rd.smartcard.exception.SmartCard_Exception_NO_READER;
import java.util.List;



public class CReaders_JRE_Embedded implements IReaders  {
	static Logger logger = Logger.getLogger(CReaders_JRE_Embedded.class);
	static public void trace(Object obj){
		System.out.println("CReaders_JRE_Embedded : " + obj);
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
	Hashtable<String,CardTerminal> m_tblTerminals = new Hashtable<String,CardTerminal>();
	javax.smartcardio.CardTerminal m_trmSelected = null;
	/* (non-Javadoc)
	 * @see client.Readers.IReaders#getSelectedTerminal()
	 */
	public javax.smartcardio.CardTerminal getSelectedTerminal(){
		return m_trmSelected;
		/*try {
			return TerminalFactory.getDefault().terminals().list().get(0);
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;*/
	}
	public CReaders_JRE_Embedded(){
		
	}
	/* (non-Javadoc)
	 * @see client.Readers.IReaders#getVectorOfAccessibleReadersForApplet()
	 */
	/*public Vector<String> getVectorOfAccessibleReadersForApplet(){
		try {
			return (Vector<String>) AccessController.doPrivileged(new getVectorOfAccessibleReaders_Protected(this));
		} catch (PrivilegedActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}*/
	/* (non-Javadoc)
	 * @see client.Readers.IReaders#getVectorOfAccessibleReaders()
	 */
	public Vector<String> getVectorOfAccessibleReaders() throws SmartCard_Exception_NO_READER{
		Vector<String> l_vecReaders = new Vector<String>();
		m_tblTerminals.clear();
		try{
			 //SmartCard.start();
	      //  int l_icpt=0;
	        List<CardTerminal> en = TerminalFactory.getDefault().terminals().list() ;
	        int size = en.size();
	        Iterator<CardTerminal> l_it = en.iterator();
	        for(int i=0;i<size;i++){
	        	CardTerminal term = l_it.next();
	        	m_tblTerminals.put(term.getName(), term);
			      System.out.println(term.getName());
			      l_vecReaders.add(term.getName());
			      
	        }
		    
		}catch(Exception e){
			System.err.println("In CReaders::getVectorOfAccessibleReaders");
			throw(new SmartCard_Exception_NO_READER(e.getMessage()));
		}
		//System.out.println("SmartCard Start");
	    
	   
	    return l_vecReaders;
	}
	
	/* (non-Javadoc)
	 * @see client.Readers.IReaders#selectTerminal(java.lang.String)
	 */
	public boolean selectTerminal(String l_strTerminalName){
		m_trmSelected = m_tblTerminals.get(l_strTerminalName);
		
		if(m_trmSelected == null){
			System.err.println("Did not Find the terminal");
			return false;
		}
		System.out.println("Terminal Selected");
		return true;
	}
	

//	/**
//	 * @author  A168594
//	 */
//	class getVectorOfAccessibleReaders_Protected implements PrivilegedExceptionAction{
//		/**
//		 * @uml.property  name="m_This"
//		 * @uml.associationEnd  
//		 */
//		private CReaders_JRE_Embedded m_This = null;
//		getVectorOfAccessibleReaders_Protected(CReaders_JRE_Embedded l_this){
//			m_This = l_this;
//		}
//		public Object run() throws Exception {
//			// TODO Auto-generated method stub
//			Vector<String> l_vecReaders = new Vector<String>();
//			try{
//				
//				  List<CardTerminal> en = TerminalFactory.getDefault().terminals().list() ;
//			        int size = en.size();
//			        Iterator<CardTerminal> l_it = en.iterator();
//			        for(int i=0;i<size;i++){
//			        	CardTerminal term = l_it.next();
//			        	m_tblTerminals.put(term.getName(), term);
//					      System.out.println(term.getName());
//					      l_vecReaders.add(term.getName());
//			        }							     		       
//			}catch(Exception e){
//				System.err.println("In CReaders::getVectorOfAccessibleReaders_Protected");
//				e.printStackTrace();
//			}
//			System.out.println("SmartCard Start");
//		    
//		   
//		    return l_vecReaders;
//		}
//		
//	}
//
//
	public void close() {
		// TODO Auto-generated method stub
		/*if(SmartCard.isStarted())
			try {
				SmartCard.shutdown();
			} catch (CardTerminalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
	}
	public IAPDU_Bridge createCompatibleAPDUBridge(boolean binitialize) throws SmartCard_Exception_NO_CARD {
		trace("createCompatibleAPDUBridge");
		CAPDU_Bridge_JRE_Embedded l_ret = new CAPDU_Bridge_JRE_Embedded();
		if(binitialize) l_ret.Initialize(new Object[]{this});
		return l_ret;
	}

}

