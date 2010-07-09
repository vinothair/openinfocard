package com.awl.rd.smartcard;


public class FactoryReaders {
	final static public String READER_JCOP="client.Readers.CReaders_JCOP";
	final static public String READER_WL="client.Readers.CReaders_WL";
	final static public String READER_JRE_EMBEDDED="com.awl.rd.smartcard.CReaders_JRE_Embedded";
	
	//Applet Reader
	final static public String READER_JRE_EMBEDDED_FORAPPLET = "com.awl.rd.smartcard.CReaders_ForApplet";
	
	
	final static public String READER_KNOWN_JCOP_EMULATOR = "Emulateur JCOP";
	static public IReaders createReaders(String l_strTypeReader){
		IReaders l_reader = null;
		
		try {
			try {
				l_reader = (IReaders) Class.forName(l_strTypeReader).newInstance();
				/*
				if(l_strTypeReader.equals(READER_JRE_EMBEDDED_FORAPPLET)){
					IReaders l_inner = (IReaders) Class.forName(READER_JRE_EMBEDDED).newInstance();
					((CReaders_ForApplet)l_reader).setInnerReader(l_inner);
				}
				*/
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return l_reader;
		
	}
}
