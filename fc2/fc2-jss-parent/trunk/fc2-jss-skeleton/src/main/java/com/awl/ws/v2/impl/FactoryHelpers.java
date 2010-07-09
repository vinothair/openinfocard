package com.awl.ws.v2.impl;

import org.xmldap.crypto.CryptoUtils;
import org.xmldap.exceptions.CryptoException;

import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.utils.Hexify;
import com.awl.ws.messages.IRequestSecurityToken;
import com.awl.ws.messages.impl.RequestSecurityToken_1_2;
import com.awl.ws.messages.impl.RequestSecurityToken_1_3;

public class FactoryHelpers {
	static public IRequestSecurityToken createEmptyRST(String rstVersion){
		if(IRequestSecurityToken.REQUEST_TYPE_ISSUING_1_2.equalsIgnoreCase(rstVersion)){
			return new RequestSecurityToken_1_2("wst", IRequestSecurityToken.REQUEST_TYPE_ISSUING_1_2);
		}
		if(IRequestSecurityToken.REQUEST_TYPE_ISSUING_1_3.equalsIgnoreCase(rstVersion)){
			return  new RequestSecurityToken_1_3("wst", IRequestSecurityToken.REQUEST_TYPE_ISSUING_1_3);
		}
	
		return null;
		
	}
	
	/**
	 * compute the PPID of for the actual RP.
	 * @param url the RP Url
	 * @param CardId the CardID of the choosen card
	 * @return the String representation of the PPID
	 */
	public static String computePPID(String url,String CardId){
		String res="";
		try {
			byte [] dig = CryptoUtils.byteDigest((url+CardId).getBytes(),Config.getDigestMethod());
			res =Hexify.encode(dig);
			return res;
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  null;
	}
}
