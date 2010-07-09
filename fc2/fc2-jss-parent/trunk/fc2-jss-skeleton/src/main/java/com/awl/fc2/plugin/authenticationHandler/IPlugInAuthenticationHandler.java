package com.awl.fc2.plugin.authenticationHandler;

import org.xmldap.infocard.InfoCard;

import com.awl.fc2.plugin.IJSSPlugin;
import com.awl.fc2.selector.authentication.AuthenticationConfig;
import com.awl.fc2.selector.exceptions.FC2Authentication_Exeception_AuthenticationFailed;
import com.awl.ws.messages.IRequestSecurityTokenResponse;
import com.awl.ws.messages.authentication.IToken;

public interface IPlugInAuthenticationHandler extends IJSSPlugin {
	public String getAuthenticationURI();
	public String getTokenType();
	//public IToken createToken();
	
	//** Authentication handling
	public void configureAuthentication(AuthenticationConfig config,String cardid);
	public void configureRSTFactory(String strSOAPProcotolInUse, String RST_Version);
	public void handleProtocol(String stsURL,String username) throws FC2Authentication_Exeception_AuthenticationFailed;
	public IRequestSecurityTokenResponse getFinalRSTR();
	
	public boolean isCardCompatible(InfoCard card);
	public boolean isStorableToken();
	public IToken getToken();
	//--
}
