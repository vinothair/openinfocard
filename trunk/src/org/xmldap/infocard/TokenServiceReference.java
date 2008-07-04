/*
 * Copyright (c) 2006, Chuck Mortimore - xmldap.org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names xmldap, xmldap.org, xmldap.com nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.xmldap.infocard;

import nu.xom.Attribute;
import nu.xom.Element;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.ws.WSConstants;
import org.xmldap.ws.soap.headers.addressing.IdentityEnabledEndpointReference;
import org.xmldap.xml.Serializable;

import java.security.cert.X509Certificate;


public class TokenServiceReference implements Serializable {

    public static final String USERNAME = "UserNamePasswordAuthenticate";
    public static final String SELF_ISSUED = "SelfIssuedAuthenticate";
    public static final String X509 = "X509V3Authenticate";
    public static final String KERB = "KerberosV5Authenticate";


    private String authType = USERNAME;
    private String address;
    private String mexAddress;
    
    private String userName = null;
    private String ppi = null;
    private String x509Hash = null;
    private String kerberosServicePrincipalName = null;
    
    private X509Certificate cert;

    public TokenServiceReference() {
    }

    public TokenServiceReference(String address, String mexAddress) {
        this.address = address;
        this.mexAddress = mexAddress;
    }

    public TokenServiceReference(String address, String mexAddress, X509Certificate cert) {
        this.address = address;
        this.mexAddress = mexAddress;
        this.cert = cert;
    }

    public TokenServiceReference(Element tokenServiceReference) {
    	
    }
    
    public String getMexAddress() {
        return mexAddress;
    }

    public void setMexAddress(String mexAddress) {
        this.mexAddress = mexAddress;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType, String value) {
    	if (USERNAME.equals(authType)) {
    		setUserName(value);
    	} else if (KERB.equals(authType)) {
    		setKerberosServicePrincipalName(value);
    	} else if (SELF_ISSUED.equals(authType)) {
    		setPPI(value);
    	} else if (X509.equals(authType)) {
    		setX509Hash(value);
    	} else {
    		throw new IllegalArgumentException("undefined authentication type (" + authType + ")");
    	}
		this.authType = authType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPPI() {
        return ppi;
    }

    // use public void setAuthType(TokenServiceReference.SELF_ISSUED, String value)
    private void setPPI(String ppi) {
        this.ppi = ppi;
    }

    public String getKerberosServicePrincipalName() {
        return kerberosServicePrincipalName;
    }

    // use public void setAuthType(TokenServiceReference.KERB, String value)
    private void setKerberosServicePrincipalName(String kerberosServicePrincipalName) {
        this.kerberosServicePrincipalName = kerberosServicePrincipalName;
    }

    public String getX509Hash() {
        return x509Hash;
    }

    // use public void setAuthType(TokenServiceReference.X509, String value)
    private void setX509Hash(String x509Hash) {
        this.x509Hash = x509Hash;
    }

    public String getUserName() {
        return userName;
    }

    // use public void setAuthType(TokenServiceReference.USERNAME, String value)
    private void setUserName(String userName) {
        this.userName = userName;
    }

    public X509Certificate getCert() {
        return cert;
    }

//    public void setCert(X509Certificate cert) {
//        this.cert = cert;
//    }


    private Element getTokenServiceReference() throws SerializationException {

        //TODO - support all the reference types
        Element tokenService = new Element(WSConstants.INFOCARD_PREFIX + ":TokenService", WSConstants.INFOCARD_NAMESPACE);
        IdentityEnabledEndpointReference iepr = new IdentityEnabledEndpointReference(address, mexAddress, cert);
        tokenService.appendChild(iepr.serialize());

        Element userCredential = new Element(WSConstants.INFOCARD_PREFIX + ":UserCredential", WSConstants.INFOCARD_NAMESPACE);
        if (USERNAME.equals(authType)) {
	        Element displayCredentialHint = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
	        displayCredentialHint.appendChild("Enter your username and password");
	        userCredential.appendChild(displayCredentialHint);
	
	        Element credential = new Element(WSConstants.INFOCARD_PREFIX + ":UsernamePasswordCredential", WSConstants.INFOCARD_NAMESPACE);
	        Element username = new Element(WSConstants.INFOCARD_PREFIX + ":Username", WSConstants.INFOCARD_NAMESPACE);
	        username.appendChild(userName);
	        credential.appendChild(username);
	        userCredential.appendChild(credential);
        } else if (KERB.equals(authType)) {
	        Element displayCredentialHint = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
	        displayCredentialHint.appendChild("Enter your kerberos credentials");
	        userCredential.appendChild(displayCredentialHint);
	        // <ic:KerberosV5Credential />
	        Element credential = new Element(WSConstants.INFOCARD_PREFIX + ":KerberosV5Credential", WSConstants.INFOCARD_NAMESPACE);
	        userCredential.appendChild(credential);
	        /* To enable the service requester to obtain a Kerberos v5 service ticket for the IP/STS, the endpoint reference of the IP/STS 
	         * in the information card or in the metadata retrieved from it must include a 'service principal name' identity claim under 
	         * the wsid:Identity tag as defined in [Addressing-Ext]. http://www.w3.org/TR/2005/CR-ws-addr-core-20050817/
	         */
        } else if (SELF_ISSUED.equals(authType)) {
	        Element displayCredentialHint = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
	        displayCredentialHint.appendChild("Choose a self-asserted card");
	        userCredential.appendChild(displayCredentialHint);
        	/*
	        	  <ic:SelfIssuedCredential>
	        	    <ic:PrivatePersonalIdentifier>
	        	      xs:base64Binary 
	        	    </ic:PrivatePersonalIdentifier>
	        	  </ic:SelfIssuedCredential>
        	 */
	        Element credential = new Element(WSConstants.INFOCARD_PREFIX + ":SelfIssuedCredential", WSConstants.INFOCARD_NAMESPACE);
	        Element credentialValue = new Element(WSConstants.INFOCARD_PREFIX + ":PrivatePersonalIdentifier", WSConstants.INFOCARD_NAMESPACE);
	        credentialValue.appendChild(ppi);
	        credential.appendChild(credentialValue);
	        userCredential.appendChild(credential);
	        System.out.println(userCredential.toXML());
        } else if (X509.equals(authType)) {
        	/*
  				  <ic:DisplayCredentialHint> xs:string </ic:DisplayCredentialHint>
  				  <ic:X509V3Credential>
				    <ds:X509Data>
				      <wsse:KeyIdentifier
				        ValueType="http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soap-message-security-1.1#ThumbprintSHA1"
				        EncodingType="http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soap-message-security-1.1#Base64Binary">
				        xs:base64binary
				      </wsse:KeyIdentifier>
				    </ds:X509Data>
				  </ic:X509V3Credential>
        	 */
	        Element displayCredentialHint = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
	        displayCredentialHint.appendChild("Choose a certificate");
	        userCredential.appendChild(displayCredentialHint);
	
	        Element credential = new Element(WSConstants.INFOCARD_PREFIX + ":X509V3Credential", WSConstants.INFOCARD_NAMESPACE);
	        Element x509Data = new Element(WSConstants.DSIG_PREFIX + ":X509Data", WSConstants.DSIG_NAMESPACE);
	        Element keyIdentifier = new Element(WSConstants.WSSE_PREFIX + ":KeyIdentifier", WSConstants.WSSE_NAMESPACE_OASIS_10);
	        Attribute valueType = new Attribute("ValueType", WSConstants.WSSE_OASIS_XX_THUMBPRINTSHA1);
	        Attribute encodingType = new Attribute("EncodingType", WSConstants.WSSE_OASIS_XX_BASE64BINARY);
	        keyIdentifier.addAttribute(valueType);
	        keyIdentifier.addAttribute(encodingType);
	        keyIdentifier.appendChild(x509Hash);
	        x509Data.appendChild(keyIdentifier);
	        credential.appendChild(x509Data);
	        userCredential.appendChild(credential);
        } else {
        	throw new SerializationException("unsupported authentication type:" + authType);
        }
        tokenService.appendChild(userCredential);
        return tokenService;

    }


    public String toXML() throws SerializationException {

        Element tsr = getTokenServiceReference();
        return tsr.toXML();

    }

    public Element serialize() throws SerializationException {
        return getTokenServiceReference();
    }


}
