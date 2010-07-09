package com.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.security.SecureRandom;

import org.apache.log4j.Logger;

import com.utils.execeptions.Utils_Exception_Unable_ToRead_File;



public class Utils {
	static Logger log = Logger.getLogger(Utils.class);
	static public void trace(Object msg){
		log.info(msg);
	}
	public static String generateRandomString(int nbBytes){
		byte [] tmp = new SecureRandom().generateSeed(nbBytes);
		return Base64.encode(tmp);
		
	}
	public static String generateNounceBase10(int nbBytes){
		byte [] tmp = new SecureRandom().generateSeed(nbBytes);
		return new BigInteger(tmp).mod(BigInteger.valueOf(10).pow(nbBytes)).toString();
			
	}
	static final public int OS_WINDOWS = 0;
	static final public int OS_UNIX =1;
	static public String xmlConfig=null;
	public static int getOS(){
		/*String [] props = {"os.name", "os.arch", "os.version"};
        for (String prop : props) {
            System.out.println(prop + "=" + System.getProperty(prop));

        }*/
		if(System.getProperty("os.name").toLowerCase().contains("windows")){
			return OS_WINDOWS;
		}
        return OS_UNIX;
	}
	public static String getConfigXML() throws Utils_Exception_Unable_ToRead_File{
		if(xmlConfig!=null){
			return xmlConfig;
		}
		String configFile = "configMAP_{OS}.xml";
		if(getOS()==OS_WINDOWS)
		{
			configFile = configFile.replace("{OS}", "WINDOWS");		
		}else{
			configFile = configFile.replace("{OS}", "UNIX");
		}
		
		//File file = new File();
		trace("getConfigXML() : Loading " + configFile);
		URL url = Utils.class.getClassLoader().getResource(configFile);
		InputStream in;
		try {
			in = url.openStream();
			//FileReader fin = new FileReader(file);
			StringBuffer buf = new StringBuffer();
			byte[] buffer = new byte[50];
			int read = 1;
			while(read!= -1){
				try {
					read = in.read(buffer);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(read != -1){
					String tmp = new String(buffer,0,read);
					//buf.append(buffer,0,read);
					buf.append(tmp);
				}
				
			}
			String toRet = buf.toString();
			return toRet;
		} catch (IOException e1) {
			throw(new Utils_Exception_Unable_ToRead_File(configFile));
		}
		
		
		
		//throw(new Utils_Exception_Unable_ToRead_File(configFile));
	}
	public static void main(String arg[]){
		try {
			System.out.println(getConfigXML());
			System.out.println(generateNounceBase10(10));
		} catch (Utils_Exception_Unable_ToRead_File e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 static final byte[] HEX_CHAR_TABLE = {
		    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
		    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
		    (byte)'8', (byte)'9', (byte)'a', (byte)'b',
		    (byte)'c', (byte)'d', (byte)'e', (byte)'f'
		  };    

	 public static byte[] getBytesFromHexString(String encoded){
		 byte [] res = new byte[encoded.length()/2];
		 int cpt=0;
		 for(int i=0;i<encoded.length();i+=2,cpt++)
		 {
			 String extract = encoded.substring(i, i+2);
	//		 System.out.println(i+"  " +extract);
			 res[cpt] = Integer.valueOf(extract, 16).byteValue();
		 }
		 return res;
	 }
	  public static String getHexString(byte[] raw) 
	    throws UnsupportedEncodingException 
	  {
		  if(raw==null || raw.length==0) return "";
	    byte[] hex = new byte[2 * raw.length];
	    int index = 0;

	    for (byte b : raw) {
	      int v = b & 0xFF;
	      hex[index++] = HEX_CHAR_TABLE[v >>> 4];
	      hex[index++] = HEX_CHAR_TABLE[v & 0xF];
	    }
	    return new String(hex, "ASCII");
	  }
	  
	  
	  

}
