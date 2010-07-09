package com.awl.rd.fc2.plugin.infocard.usercredentials.map;

import nu.xom.Element;
import nu.xom.Elements;

import org.xmldap.exceptions.ParsingException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.IUserCredentialExtentions;
import org.xmldap.infocard.UserCredential;
import org.xmldap.ws.WSConstants;



public class UserCredentialExt_MAP implements IUserCredentialExtentions {
	public static final String MAP = "MapAuthenticatice";
	
	String trustedBroker = "";
	String hint ="";
	String username;
	
	public UserCredentialExt_MAP(){
		
	}
	
	public UserCredentialExt_MAP(String trustedBroker) {
		this.trustedBroker = trustedBroker;
		
	}
	public void setUsername(String username){
		this.username = username;
	}
	public String getUsername(){
		return username;
	}
	
	
	@Override
	public String getAuthenticationType() {
		// TODO Auto-generated method stub
		return MAP;
	}

	@Override
	public Element serialize() throws SerializationException {
		Element userCredential = new Element(WSConstants.INFOCARD_PREFIX
				+ ":UserCredential", WSConstants.INFOCARD_NAMESPACE);
		
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
				+ ":MAPCredential", WSConstants.INFOCARD_NAMESPACE);
		
		Element username = new Element(WSConstants.INFOCARD_PREFIX
				+ ":Username", WSConstants.INFOCARD_NAMESPACE);
		username.appendChild(username);
		credential.appendChild(username);
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


	public String getTrustedServer(){
		return "URLMAP";
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
			throw new ParsingException("NOT A MAP UserCredential");
		}
		Element elt = userCredential.getUnknownCredential();
		Elements elts = elt.getChildElements("MAPCredential",WSConstants.INFOCARD_NAMESPACE );// WSConstants.INFOCARD_NAMESPACE);
		if (elts.size() == 1) {
			
			Element usernamePasswordCredential = elts.get(0);
			Element usernameElt = usernamePasswordCredential
					.getFirstChildElement("Username",WSConstants.INFOCARD_NAMESPACE );// WSConstants.INFOCARD_NAMESPACE);WSConstants.INFOCARD_NAMESPACE);
			if (usernameElt != null) {
				username = usernameElt.getValue();
			} else {
				throw new ParsingException("Expected Username");
			}
			userCredential.setAuthenticationType(getAuthenticationType());
		}else{
			throw new ParsingException("NOT A MAP UserCredential");
		}
		
	}

}
