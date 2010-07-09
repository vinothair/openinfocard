package com.utils;

//import com.ibm.util.Util;
//import com.pki.Constants;

public class SUtil {
	static Class<?> s_ClasseConstants = null;
	
	
	//FOR ETICKETING
	static public void convertShort2Bytes(short l_toConvert, byte[]bytes){
		int hexBase = 0xff; // A byte of all ones
		bytes[0] = (byte) (hexBase & l_toConvert);
		bytes[1] = (byte) (((hexBase << 8) & l_toConvert) >> 8);
		/*bytes[2] = ((hexBase << 16) & myInt) >> 16;
		bytes[3] = ((hexBase << 24) & myInt) >> 24;*/
		
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
	public static void setConstantClass(Class<?> l_cl){
		s_ClasseConstants = l_cl;
	}
	static void printOneByteToHew(byte l_buf){
		String toDisplay =Integer.toHexString(l_buf);
		if(toDisplay.length() == 1)
			toDisplay = "0" + toDisplay;
		if(toDisplay.length() >3){
			toDisplay = toDisplay.substring(toDisplay.length()-2, toDisplay.length());
		}
		System.out.print("0x"+toDisplay);
	}
	public static void printBufferBytes(byte [] buffer){
	//	System.out.println(buffer.length);
		for (int i=0;i<buffer.length-1;i++){
			printOneByteToHew(buffer[i]);
			System.out.print(",");
		}
		printOneByteToHew(buffer[buffer.length-1]);
		System.out.println("");
		//System.out.println(Integer.toHexString(buffer[buffer.length-1]));
	}
	public static void printBufferBytes(byte [] buffer, int l_iPos){
		for (int i=l_iPos;i<buffer.length-1;i++){
			printOneByteToHew(buffer[i]);
			System.out.print(",");
		}
		printOneByteToHew(buffer[buffer.length-1]);
		System.out.println("");
	}
	static public int convertBytes2Int(byte [] b,int offset,int length){
		int read = 0;
		for (int i = 0; i < length; i++) {
            read += (int) ((short) b[i+offset] & 0xff)*256;
            
            //System.out.println("Read: " + read);
        }
		if(length == 2)
			read= b[0]*256+b[1];
		return read;
	}
	static public void main(String [] args){
	/*	byte [] b={(byte)0x90,0x00};
		System.out.println(convertBytes2Int(b, 0, b.length));*/
	}
	static public byte[] convertString2Bytes(String l_strAPDU){
		if(l_strAPDU.length()%2 != 0){
			System.err.println("SUtils::convertString2Bytes : Malformed APDU String");
			return null;
		}
		byte [] l_tabByte = new byte[l_strAPDU.length()/2];
		int pos=0;
		for(int i=0;i<l_tabByte.length;i++,pos+=2){
			String l_strTmp = l_strAPDU.substring(pos, pos+2);
			
			
			l_tabByte[i] = (byte)Integer.valueOf(l_strTmp,16).byteValue(); 
		}
		printBufferBytes(l_tabByte);
		return l_tabByte;
	}
	  /**
	   * Convert a String to byte array
	   * @param cmd
	   * @return
	   */
	  static public byte[] string2Bytes(String cmd) {
	    
	    byte data[] = new byte[cmd.length() / 2];
	    for(int i = 0; i < data.length; i++) {
	      int j = i * 2;
	      data[i] = Integer.valueOf(cmd.substring(j, j + 2), 16).byteValue();
	    }
	 
	    return data;
	  }
	  
	 
	static public void displayError(byte [] l_bytStatus){
		//short l_tmp = l_bytStatus[1];
		//l_tmp += ((short)l_bytStatus[0])*256;
		//short l_tmp = 	(short) (Array.getShort(l_bytStatus, 1) + 256* Array.getShort(l_bytStatus, 0)); 
		short l_tmp = (short) convertBytes2Int(l_bytStatus, 0, l_bytStatus.length);//Util.shortMSBF(l_bytStatus, 0);
		//short l_tmp = (short) Util.shortMSBF(l_bytStatus, 0);
		try{
			{
				Class<?> l_Classe = s_ClasseConstants;
				if(l_Classe != null){
					java.lang.reflect.Field [] l_field = l_Classe.getDeclaredFields();
					for(int i = 0;i<l_field.length;i++){
						/*System.out.print("Name : " + l_field[i].getName() +
								         "\t Type : " + l_field[i].getType().toString()+
								         "\t Value:" + l_field[i].getShort(null)+
										 "\n");*/
						
						if(l_field[i].getType().toString().equals("short")){
						//	System.out.println("Error Code = " + l_field[i].getName() + " = " +l_field[i].getShort(null) + " || " + l_tmp);	
							if(l_tmp == l_field[i].getShort(null))
								System.out.println("===>Error Code = " + l_field[i].getName());
							
							
						}
						
					}
				}
				
			}
			
			{
				Class<?> l_Classe = MIMIC_ISO7816.class;
				java.lang.reflect.Field [] l_field = l_Classe.getDeclaredFields();
				for(int i = 0;i<l_field.length;i++){
					/*System.out.print("Name : " + l_field[i].getName() +
							         "\t Type : " + l_field[i].getType().toString()+
							         "\t Value:" + l_field[i].getShort(null)+
									 "\n");*/
					
					if(l_field[i].getType().toString().equals("short")){
					//	System.out.println("Error Code = " + l_field[i].getName() + " = " +l_field[i].getShort(null) + " || " + l_tmp);	
						if(l_tmp == l_field[i].getShort(null))
							System.out.println("===>Error Code = " + l_field[i].getName());
						
						
					}
					
				}
			}
			
			
			SUtil.printBufferBytes(l_bytStatus);
			//if(l_bytStatus[0] != 0x90 && l_bytStatus[1] != 0x00)
				//System.exit(-1);
		
		}catch(Exception e){
			
		}
	}
}



 
 class MIMIC_ISO7816 {

	/**
	 * Response status : Incorrect parameters (P1,P2) = 0x6A86
	 */
	public static final short SW_INCORRECT_P1P2 = (short) 0x6a86;

	/**
	 * Response status : Not enough memory space in the file = 0x6A84
	 */
	public static final short SW_FILE_FULL = (short) 0x6a84;

	/**
	 * Response status : Card does not support secure messaging =       
	 * 0x6882
	 */
	public static final short SW_SECURE_MESSAGING_NOT_SUPPORTED = (short) 0x6882;

	/**
	 * Response status : Command not allowed (no current EF) = 0x6986
	 */
	public static final short SW_COMMAND_NOT_ALLOWED = (short) 0x6986;

	/**
	 * APDU command INS : SELECT = 0xA4
	 */
	public static final byte INS_SELECT = (byte) 0xffa4;

	/**
	 * APDU command INS : EXTERNAL AUTHENTICATE = 0x82
	 */
	public static final byte INS_EXTERNAL_AUTHENTICATE = (byte) 0xff82;

	/**
	 * Response status : Security condition not satisfied = 0x6982
	 */
	public static final short SW_SECURITY_STATUS_NOT_SATISFIED = (short) 0x6982;

	/**
	 * Response status : File invalid = 0x6983
	 */
	public static final short SW_FILE_INVALID = (short) 0x6983;

	/**
	 * Response status : CLA value not supported = 0x6E00
	 */
	public static final short SW_CLA_NOT_SUPPORTED = (short) 0x6e00;

	/**
	 * Response status : Warning, card state unchanged = 0x6200
	 */
	public static final short SW_WARNING_STATE_UNCHANGED = (short) 0x6200;

	/**
	 * Response status : Applet selection failed = 0x6999;
	 */
	public static final short SW_APPLET_SELECT_FAILED = (short) 0x6999;

	/**
	 * Response status : Function not supported = 0x6A81
	 */
	public static final short SW_FUNC_NOT_SUPPORTED = (short) 0x6a81;

	/**
	 * Response status : Incorrect parameters (P1,P2) = 0x6B00
	 */
	public static final short SW_WRONG_P1P2 = (short) 0x6b00;

	/**
	 * APDU header offset : INS = 1
	 */
	public static final byte OFFSET_INS = (byte) 0x0001;

	/**
	 * Response status : Record not found = 0x6A83
	 */
	public static final short SW_RECORD_NOT_FOUND = (short) 0x6a83;

	/**
	 * APDU header offset : CLA = 0
	 */
	public static final byte OFFSET_CLA = (byte) 0x0000;

	/**
	 * APDU command data offset : CDATA = 5
	 */
	public static final byte OFFSET_CDATA = (byte) 0x0005;

	/**
	 * Response status : File not found = 0x6A82
	 */
	public static final short SW_FILE_NOT_FOUND = (short) 0x6a82;

	/**
	 * Response status : Data invalid = 0x6984
	 */
	public static final short SW_DATA_INVALID = (short) 0x6984;

	/**
	 * Response status : No precise diagnosis = 0x6F00
	 */
	public static final short SW_UNKNOWN = (short) 0x6f00;

	/**
	 * Response status : Card does not support logical channels =       
	 * 0x6881
	 */
	public static final short SW_LOGICAL_CHANNEL_NOT_SUPPORTED = (short) 0x6881;

	/**
	 * Response status : No Error = (short)0x9000
	 */
	public static final short SW_NO_ERROR = (short) 0x9000;

	/**
	 * Response status : Conditions of use not satisfied = 0x6985
	 */
	public static final short SW_CONDITIONS_NOT_SATISFIED = (short) 0x6985;

	/**
	 * APDU header offset : P1 = 2
	 */
	public static final byte OFFSET_P1 = (byte) 0x0002;

	/**
	 * APDU command CLA : ISO 7816 = 0x00
	 */
	public static final byte CLA_ISO7816 = (byte) 0x0000;

	/**
	 * APDU header offset : LC = 4
	 */
	public static final byte OFFSET_LC = (byte) 0x0004;

	/**
	 * Response status : INS value not supported = 0x6D00
	 */
	public static final short SW_INS_NOT_SUPPORTED = (short) 0x6d00;

	/**
	 * Response status : Wrong length = 0x6700
	 */
	public static final short SW_WRONG_LENGTH = (short) 0x6700;

	/**
	 * APDU header offset : P2 = 3
	 */
	public static final byte OFFSET_P2 = (byte) 0x0003;

	/**
	 * Response status : Correct Expected Length (Le) = 0x6C00
	 */
	public static final short SW_CORRECT_LENGTH_00 = (short) 0x6c00;

	/**
	 * Response status : Wrong data = 0x6A80
	 */
	public static final short SW_WRONG_DATA = (short) 0x6a80;

	/**
	 * Response status : Response bytes remaining = 0x6100
	 */
	public static final short SW_BYTES_REMAINING_00 = (short) 0x6100;

}
