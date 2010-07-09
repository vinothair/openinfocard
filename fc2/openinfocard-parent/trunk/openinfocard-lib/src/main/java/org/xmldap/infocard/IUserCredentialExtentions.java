package org.xmldap.infocard;

import nu.xom.Element;

import org.xmldap.exceptions.ParsingException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.extentions.IOpenInfoCardPlugin;
import org.xmldap.infocard.UserCredential;



public interface IUserCredentialExtentions extends IOpenInfoCardPlugin{

	public void fromUserCredential(UserCredential userCredential) throws ParsingException;
	public String getAuthenticationType();
	public void setHint(String hint);
	Element serialize() throws SerializationException;
}
