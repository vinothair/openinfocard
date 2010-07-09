import java.security.MessageDigest;

import com.utils.SUtil;
import com.utils.Utils;


public class test {
	public static void main(String arg[]) throws Exception
	{
//		CReaders_JRE_Embedded reader = new CReaders_JRE_Embedded();
//		Vector<String> lstTerminal = reader.getVectorOfAccessibleReaders();
//		System.out.println(lstTerminal);
//		
//		reader.selectTerminal(lstTerminal.get(0));
//		IAPDU_Bridge apdu = reader.createCompatibleAPDUBridge(true);
//		
//		String response = apdu.sendAPDUString("00A4040007A000000003800200");
//		System.out.println("Response =  " + response);
//		//
////		response = apdu.sendAPDUString("80A8000002830000");
////		System.out.println(response);
//		response = apdu.sendAPDUString("00B2011400");
//		System.out.println(response);
//		byte buffer [] = apdu.getResData();
//		SUtil.printBufferBytes(buffer);
		
		
		
		
		
		MessageDigest hash = MessageDigest.getInstance("SHA-1");
		int nbBytes = 64;
		String data = Utils.generateNounceBase10(nbBytes);
		System.out.println("DATA = " + data);
		byte[] Hdata = hash.digest(data.getBytes());
		
		SUtil.printBufferBytes(Hdata);
	}
}
