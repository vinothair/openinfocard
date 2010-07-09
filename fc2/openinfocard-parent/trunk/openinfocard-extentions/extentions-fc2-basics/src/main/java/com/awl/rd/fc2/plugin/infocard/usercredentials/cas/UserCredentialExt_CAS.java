package com.awl.rd.fc2.plugin.infocard.usercredentials.cas;

import nu.xom.Element;
import nu.xom.Elements;

import org.xmldap.exceptions.ParsingException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.IUserCredentialExtentions;
import org.xmldap.infocard.UserCredential;
import org.xmldap.ws.WSConstants;

import com.awl.logger.Logger;


public class UserCredentialExt_CAS implements IUserCredentialExtentions {
	public static final String CAS = "CASAuthenticate";
	static public Logger log = new Logger(UserCredentialExt_CAS.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
	String associatedProtocol = "";
	String hint ="";
	String username;

	public String getUsername(){
		return username;
	}
	public UserCredentialExt_CAS() {
	
	}
	
	public UserCredentialExt_CAS(String associatedProtocol) {
		this.associatedProtocol = associatedProtocol;
		
	}
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getAssociatedProtocol(){
		return associatedProtocol;
	}
	
	
	
	@Override
	public String getAuthenticationType() {
		// TODO Auto-generated method stub
		return CAS;
	}

	@Override
	public Element serialize() throws SerializationException {
		Element userCredential = new Element(WSConstants.INFOCARD_PREFIX
				+ ":UserCredential", WSConstants.INFOCARD_NAMESPACE);
		
//		<SmartCardCredential>
//        <Username>philippe-sc</Username>
//        <Protocol>ECHO</Protocol>
//      </SmartCardCredential>
		Element displayCredentialHint = new Element(
				WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint",
				WSConstants.INFOCARD_NAMESPACE);
		if (hint != null) {
			displayCredentialHint.appendChild(hint);
		} else {
			displayCredentialHint
					.appendChild("Please enter your username and password.");
		}
		userCredential.appendChild(displayCredentialHint);

		//Element credential = new Element("any" + ":MAPCredential");

		Element credential = new Element(WSConstants.INFOCARD_PREFIX
				+ ":SmartCardCredential", WSConstants.INFOCARD_NAMESPACE);
		
		Element username = new Element(WSConstants.INFOCARD_PREFIX
				+ ":Username", WSConstants.INFOCARD_NAMESPACE);
		
		Element Protocol = new Element(WSConstants.INFOCARD_PREFIX
				+ ":Protocol", WSConstants.INFOCARD_NAMESPACE);
		
		username.appendChild(username);
		Protocol.appendChild(associatedProtocol);
		credential.appendChild(username);
		credential.appendChild(Protocol);
		userCredential.appendChild(credential);
		return userCredential;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.getClass().toString();
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return PLG_INFOCARD_USERCREDENTIAL_EXTENTIONS;
	}

	@Override
	public void install() {
		// TODO Auto-generated method stub

	}



	@Override
	public void setHint(String hint) {
		this.hint = hint;
		
	}
	@Override
	public void fromUserCredential(UserCredential userCredential)
			throws ParsingException {
		if(!userCredential.getAuthType().equalsIgnoreCase(UserCredential.UNKNOWN) &&
				   !userCredential.getAuthType().equalsIgnoreCase(getAuthenticationType()))
				{
					throw new ParsingException("NOT A CAS UserCredential");
				}
		Element elt = userCredential.getUnknownCredential();
		Elements elts = elt.getChildElements("SmartCardCredential",WSConstants.INFOCARD_NAMESPACE );// WSConstants.INFOCARD_NAMESPACE);	
		if (elts.size() == 1) {
			trace("We have a CAS card");
			//authType = CAS;
			Element usernamePasswordCredential = elts.get(0);
			Element usernameElt = usernamePasswordCredential
					.getFirstChildElement("Username",WSConstants.INFOCARD_NAMESPACE );// WSConstants.INFOCARD_NAMESPACE);WSConstants.INFOCARD_NAMESPACE);
			if (usernameElt != null) {
				username = usernameElt.getValue();
			} else {
				throw new ParsingException("Expected Username");
			}
			
			Element protocolElt = usernamePasswordCredential
			.getFirstChildElement("Protocol",WSConstants.INFOCARD_NAMESPACE );// WSConstants.INFOCARD_NAMESPACE);WSConstants.INFOCARD_NAMESPACE);
			if (protocolElt != null) {
				associatedProtocol = protocolElt.getValue();
			} else {
				throw new ParsingException("Expected Username");
			}
			userCredential.setAuthenticationType(getAuthenticationType());
			
		}else{
			throw new ParsingException("NOT A CAS UserCredential");
		}
		
	}

}
