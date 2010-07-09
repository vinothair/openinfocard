/*
 * Copyright (c) 2010, Atos Worldline - http://www.atosworldline.com/
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

import java.util.Vector;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmldap.exceptions.ParsingException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.ws.WSConstants;



import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

public class UserCredential {
	static Logger log = Logger.getLogger(UserCredential.class);
	public static void trace(Object message){
		log.info(message);
	}
	
	public static final String USERNAME = "UserNamePasswordAuthenticate";
	public static final String SELF_ISSUED = "SelfIssuedAuthenticate";
	public static final String X509 = "X509V3Authenticate";
	public static final String KERB = "KerberosV5Authenticate";
	public static final String UNKNOWN = "unknown";
//	public static final String MAP = "MapAuthenticatice";
//	public static final String CAS = "CASAuthenticate";

	Element unknownCredential = null;
	
	private String authType = USERNAME;

	
	private String hint = null;

	private String userName = null;
	private String ppi = null;
	private String x509Hash = null;
	private String kerberosServicePrincipalName = null;
//	private String mapServicePrincipalName = null;
//	private String CAS_AssociatedProtocol = null;
//	public void setCAS_AssociatedProtocol(String protocol){
//		CAS_AssociatedProtocol = protocol;
//	}
	
	public Element getUnknownCredential(){
		return unknownCredential;
	}

	public UserCredential(Element elt) throws ParsingException {
		String name = elt.getLocalName();
		System.out.println("UserCredential : " +name);
		
		if ("UserCredential".equals(name)) {
			
			Elements elts = elt.getChildElements("DisplayCredentialHint",
					WSConstants.INFOCARD_NAMESPACE);
			if (elts.size() == 1) {
				Element displayCredentialHintElt = elts.get(0);
				hint = displayCredentialHintElt.getValue();
			} else {
				if (elts.size() > 1) {
					throw new ParsingException(
							"Expected zero or one UserCredential but found"
									+ elts.size());
				}
				// else size == 0. OK
				hint = null;
			}
//			elts = elt.getChildElements("SmartCardCredential",WSConstants.INFOCARD_NAMESPACE );// WSConstants.INFOCARD_NAMESPACE);	
//			if (elts.size() == 1) {
//				trace("We have a CAS card");
//				authType = CAS;
//				Element usernamePasswordCredential = elts.get(0);
//				Element usernameElt = usernamePasswordCredential
//						.getFirstChildElement("Username",WSConstants.INFOCARD_NAMESPACE );// WSConstants.INFOCARD_NAMESPACE);WSConstants.INFOCARD_NAMESPACE);
//				if (usernameElt != null) {
//					userName = usernameElt.getValue();
//				} else {
//					throw new ParsingException("Expected Username");
//				}
//				
//				Element protocolElt = usernamePasswordCredential
//				.getFirstChildElement("Protocol",WSConstants.INFOCARD_NAMESPACE );// WSConstants.INFOCARD_NAMESPACE);WSConstants.INFOCARD_NAMESPACE);
//				if (protocolElt != null) {
//					CAS_AssociatedProtocol = usernameElt.getValue();
//				} else {
//					throw new ParsingException("Expected Username");
//				}
//			}else {
//				elts = elt.getChildElements("MAPCredential",WSConstants.INFOCARD_NAMESPACE );// WSConstants.INFOCARD_NAMESPACE);
//				if (elts.size() == 1) {
//					authType = MAP;
//					Element usernamePasswordCredential = elts.get(0);
//					Element usernameElt = usernamePasswordCredential
//							.getFirstChildElement("Username",WSConstants.INFOCARD_NAMESPACE );// WSConstants.INFOCARD_NAMESPACE);WSConstants.INFOCARD_NAMESPACE);
//					if (usernameElt != null) {
//						userName = usernameElt.getValue();
//					} else {
//						throw new ParsingException("Expected Username");
//					}
//				} else {
					elts = elt.getChildElements("UsernamePasswordCredential",
							WSConstants.INFOCARD_NAMESPACE);
					if (elts.size() == 1) {
						authType = USERNAME;
						Element usernamePasswordCredential = elts.get(0);
						Element usernameElt = usernamePasswordCredential
								.getFirstChildElement("Username",
										WSConstants.INFOCARD_NAMESPACE);
						if (usernameElt != null) {
							userName = usernameElt.getValue();
						} else {
							throw new ParsingException("Expected Username");
						}
					} else {
						elts = elt.getChildElements("KerberosV5Credential",
								WSConstants.INFOCARD_NAMESPACE);
						if (elts.size() == 1) {
							authType = KERB;
						} else {
							elts = elt.getChildElements("X509V3Credential",
									WSConstants.INFOCARD_NAMESPACE);
							if (elts.size() == 1) {
								authType = X509;
								Element x509V3Credential = elts.get(0);
								elts = x509V3Credential.getChildElements(
										"X509Data", WSConstants.DSIG_NAMESPACE);
								if (elts.size() == 1) {
									Element x509Data = elts.get(0);
									elts = x509Data.getChildElements(
											"KeyIdentifier",
											WSConstants.WSSE_NAMESPACE_OASIS_10);
									if (elts.size() == 1) {
										Element keyIdentifier = elts.get(0);
										// TODO check ValueType and EncodingType
										x509Hash = keyIdentifier.getValue();
									} else {
										throw new ParsingException(
												"Expected one KeyIdentifier but found: "
														+ elts.size());
									}
								} else {
									throw new ParsingException(
											"Expected one X509Data but found: "
													+ elts.size());
								}
							} else {
								elts = elt.getChildElements("SelfIssuedCredential",
										WSConstants.INFOCARD_NAMESPACE);
								if (elts.size() == 1) {
									authType = SELF_ISSUED;
									Element selfIssuedCredential = elts.get(0);
									elts = selfIssuedCredential.getChildElements(
											"PrivatePersonalIdentifier",
											WSConstants.INFOCARD_NAMESPACE);
									if (elts.size() == 1) {
										Element privatePersonalIdentifier = elts
												.get(0);
										this.ppi = privatePersonalIdentifier
												.getValue();
									} else {
										throw new ParsingException(
												"Expected one PrivatePersonalIdentifier but found: "
														+ elts.size());
									}
								} else {
									unknownCredential = elt;
									authType = UNKNOWN;
//									throw new ParsingException(
//											"Expected one of UsernamePasswordCredential, KerberosV5Credential, X509V3Credential or SelfIssuedCredential");
								}
							}
						}
					}
				//}//
			//}
			
		} else {
			throw new ParsingException("Expected UserCredential but found"
					+ name);
		}
	}

	public UserCredential(String authType, String value) {
		setAuthType(authType, value);
	}
	
	IUserCredentialExtentions userCredentialExtentions;
	public UserCredential(IUserCredentialExtentions extentions) {
		authType = UNKNOWN;
		userCredentialExtentions = extentions;
	}
	

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getAuthType() {
		return authType;
	}

	public String getPPI() {
		return ppi;
	}

	// use public void setAuthType(TokenServiceReference.SELF_ISSUED, String
	// value)
	private void setPPI(String ppi) {
		this.ppi = ppi;
	}

	public String getKerberosServicePrincipalName() {
		return kerberosServicePrincipalName;
	}

	// use public void setAuthType(TokenServiceReference.KERB, String value)
	private void setKerberosServicePrincipalName(
			String kerberosServicePrincipalName) {
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
	// private void setUserName(String userName) {
	public void setUserName(String userName) {
		this.userName = userName;
	}

	// public void setCert(X509Certificate cert) {
	// this.cert = cert;
	// }

	public void setAuthenticationType(String authType){
		this.authType = authType;
	}
	protected void setAuthType(String authType, String value) {
		if (UserCredential.USERNAME.equals(authType)) {
			setUserName(value);
		} else if (UserCredential.KERB.equals(authType)) {
			setKerberosServicePrincipalName(value);
		} else if (UserCredential.SELF_ISSUED.equals(authType)) {
			setPPI(value);
		} else if (UserCredential.X509.equals(authType)) {
			setX509Hash(value);
		} /*else if (UserCredential.MAP.equals(authType)) {
			mapServicePrincipalName = value;
		} else if (UserCredential.CAS.equals(authType)) {
			CAS_AssociatedProtocol = value;
		} */else {
			throw new IllegalArgumentException(
					"undefined authentication type (" + authType + ")");
		}

		this.authType = authType;
	}

	public Element serialize() throws SerializationException {
		Element userCredential = new Element(WSConstants.INFOCARD_PREFIX
				+ ":UserCredential", WSConstants.INFOCARD_NAMESPACE);
		/*if (CAS.equals(authType)) {
			// userCredential = new Element(WSConstants.INFOCARD_PREFIX +
			// ":MAPCredential", WSConstants.INFOCARD_NAMESPACE);
			
//			<SmartCardCredential>
//            <Username>philippe-sc</Username>
//            <Protocol>ECHO</Protocol>
//          </SmartCardCredential>
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
			
			username.appendChild(userName);
			Protocol.appendChild(CAS_AssociatedProtocol);
			credential.appendChild(username);
			credential.appendChild(Protocol);
			userCredential.appendChild(credential);
		} else */if (USERNAME.equals(authType)) {
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

			Element credential = new Element(WSConstants.INFOCARD_PREFIX
					+ ":UsernamePasswordCredential",
					WSConstants.INFOCARD_NAMESPACE);
			Element username = new Element(WSConstants.INFOCARD_PREFIX
					+ ":Username", WSConstants.INFOCARD_NAMESPACE);
			username.appendChild(userName);
			credential.appendChild(username);
			userCredential.appendChild(credential);
		}/*else if (MAP.equals(authType)) {
			// userCredential = new Element(WSConstants.INFOCARD_PREFIX +
			// ":MAPCredential", WSConstants.INFOCARD_NAMESPACE);
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
			username.appendChild(userName);
			credential.appendChild(username);
			userCredential.appendChild(credential);
		} else if (USERNAME.equals(authType)) {
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

			Element credential = new Element(WSConstants.INFOCARD_PREFIX
					+ ":UsernamePasswordCredential",
					WSConstants.INFOCARD_NAMESPACE);
			Element username = new Element(WSConstants.INFOCARD_PREFIX
					+ ":Username", WSConstants.INFOCARD_NAMESPACE);
			username.appendChild(userName);
			credential.appendChild(username);
			userCredential.appendChild(credential);
		} else if (KERB.equals(authType)) {
			Element displayCredentialHint = new Element(
					WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint",
					WSConstants.INFOCARD_NAMESPACE);
			if (hint != null) {
				displayCredentialHint.appendChild(hint);
			} else {
				displayCredentialHint
						.appendChild("Enter your kerberos credentials");
			}
			userCredential.appendChild(displayCredentialHint);
			// <ic:KerberosV5Credential />
			Element credential = new Element(WSConstants.INFOCARD_PREFIX
					+ ":KerberosV5Credential", WSConstants.INFOCARD_NAMESPACE);
			userCredential.appendChild(credential);
			/*
			 * To enable the service requester to obtain a Kerberos v5 service
			 * ticket for the IP/STS, the endpoint reference of the IP/STS in
			 * the information card or in the metadata retrieved from it must
			 * include a 'service principal name' identity claim under the
			 * wsid:Identity tag as defined in [Addressing-Ext].
			 * http://www.w3.org/TR/2005/CR-ws-addr-core-20050817/
			 */
		//} else 
		else if (SELF_ISSUED.equals(authType)) {
			Element displayCredentialHint = new Element(
					WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint",
					WSConstants.INFOCARD_NAMESPACE);
			if (hint != null) {
				displayCredentialHint.appendChild(hint);
			} else {
				displayCredentialHint
						.appendChild("Choose a self-asserted card");
			}
			userCredential.appendChild(displayCredentialHint);
			/*
			 * <ic:SelfIssuedCredential> <ic:PrivatePersonalIdentifier>
			 * xs:base64Binary </ic:PrivatePersonalIdentifier>
			 * </ic:SelfIssuedCredential>
			 */
			Element credential = new Element(WSConstants.INFOCARD_PREFIX
					+ ":SelfIssuedCredential", WSConstants.INFOCARD_NAMESPACE);
			Element credentialValue = new Element(WSConstants.INFOCARD_PREFIX
					+ ":PrivatePersonalIdentifier",
					WSConstants.INFOCARD_NAMESPACE);
			credentialValue.appendChild(ppi);
			credential.appendChild(credentialValue);
			userCredential.appendChild(credential);
			// System.out.println(userCredential.toXML());
		} else if (X509.equals(authType)) {
			/*
			 * <ic:DisplayCredentialHint> xs:string </ic:DisplayCredentialHint>
			 * <ic:X509V3Credential> <ds:X509Data> <wsse:KeyIdentifier
			 * ValueType=
			 * "http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soap-message-security-1.1#ThumbprintSHA1"
			 * EncodingType=
			 * "http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soap-message-security-1.1#Base64Binary"
			 * > xs:base64binary </wsse:KeyIdentifier> </ds:X509Data>
			 * </ic:X509V3Credential>
			 */
			Element displayCredentialHint = new Element(
					WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint",
					WSConstants.INFOCARD_NAMESPACE);
			if (hint != null) {
				displayCredentialHint.appendChild(hint);
			} else {
				displayCredentialHint.appendChild("Choose a certificate");
			}
			userCredential.appendChild(displayCredentialHint);

			Element credential = new Element(WSConstants.INFOCARD_PREFIX
					+ ":X509V3Credential", WSConstants.INFOCARD_NAMESPACE);
			Element x509Data = new Element(WSConstants.DSIG_PREFIX
					+ ":X509Data", WSConstants.DSIG_NAMESPACE);
			Element keyIdentifier = new Element(WSConstants.WSSE_PREFIX
					+ ":KeyIdentifier", WSConstants.WSSE_NAMESPACE_OASIS_10);
			Attribute valueType = new Attribute("ValueType",
					WSConstants.WSSE_OASIS_XX_THUMBPRINTSHA1);
			Attribute encodingType = new Attribute("EncodingType",
					WSConstants.WSSE_OASIS_XX_BASE64BINARY);
			keyIdentifier.addAttribute(valueType);
			keyIdentifier.addAttribute(encodingType);
			keyIdentifier.appendChild(x509Hash);
			x509Data.appendChild(keyIdentifier);
			credential.appendChild(x509Data);
			userCredential.appendChild(credential);
		} else {
			throw new SerializationException("unsupported authentication type:"
					+ authType);
		}
		if(authType.equalsIgnoreCase(UNKNOWN)){
			trace("Calling the extentions");
			userCredential = userCredentialExtentions.serialize();
		}
		return userCredential;
	}
	 public JSONObject toJSON() throws SerializationException {
	      try {
	        JSONObject json = new JSONObject();
	        json.put("Type", authType);
	        if (USERNAME.equals(authType)) {
	          json.put("Username", userName);
	        } else if (KERB.equals(authType)) {
	          // TODO
	          throw new SerializationException("Unsupported Authentication Type: " + authType);
	        } else if (SELF_ISSUED.equals(authType)) {
	          json.put("PPID", ppi);
	        } else if (X509.equals(authType)) {
	          json.put("X509Hash", x509Hash);
	        } else {
	          throw new SerializationException("Unsupported Authentication Type: " + authType);
	        }
	        if (hint != null) {
	          json.put("Hint", hint);
	        }
	        return json;
	      } catch (JSONException e) {
	        throw new SerializationException(e);
	      }
	
	    }

}



//package org.xmldap.infocard;
//
//import org.xmldap.exceptions.ParsingException;
//import org.xmldap.exceptions.SerializationException;
//import org.xmldap.ws.WSConstants;
//
//import nu.xom.Attribute;
//import nu.xom.Element;
//import nu.xom.Elements;
//
//public class UserCredential {
//    public static final String USERNAME = "UserNamePasswordAuthenticate";
//    public static final String SELF_ISSUED = "SelfIssuedAuthenticate";
//    public static final String X509 = "X509V3Authenticate";
//    public static final String KERB = "KerberosV5Authenticate";
//    public static final String MAP = "MapAuthenticatice";
//
//    private String authType = USERNAME;
//    
//    private String hint = null;
//    
//	private String userName = null;
//    private String ppi = null;
//    private String x509Hash = null;
//    private String kerberosServicePrincipalName = null;
//    private String mapServicePrincipalName = null;
//	public UserCredential(Element elt) throws ParsingException {
//		String name = elt.getLocalName();
//		if ("UserCredential".equals(name)) {
//			Elements elts = elt.getChildElements("DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
//			if (elts.size() == 1) {
//				Element displayCredentialHintElt = elts.get(0);
//				hint = displayCredentialHintElt.getValue();
//			} else {
//				if (elts.size() > 1) {
//					throw new ParsingException("Expected zero or one UserCredential but found" + elts.size());
//				}
//				// else size == 0. OK
//				hint = null;
//			}
//			
//			//elts = elt.getChildElements("MAPCredential","https://rentacar.atosworldline.bancaire.test.fc2consortium.org/identity.xsd");
//			elts = elt.getChildElements("MAPCredential",WSConstants.INFOCARD_NAMESPACE);
//			if(elts.size() ==1 ){
//				authType = MAP;
//				Element usernamePasswordCredential = elts.get(0);
//				Element usernameElt = usernamePasswordCredential.getFirstChildElement("Username", WSConstants.INFOCARD_NAMESPACE);//WSConstants.INFOCARD_NAMESPACE);WSConstants.INFOCARD_NAMESPACE);
//				if (usernameElt != null) {
//					userName = usernameElt.getValue();
//				}/* else {
//					throw new ParsingException("Expected Username");
//				}*/
//			}else{
//				elts = elt.getChildElements("UsernamePasswordCredential", WSConstants.INFOCARD_NAMESPACE);
//				if (elts.size() == 1) {
//					authType = USERNAME;
//					Element usernamePasswordCredential = elts.get(0);
//					Element usernameElt = usernamePasswordCredential.getFirstChildElement("Username", WSConstants.INFOCARD_NAMESPACE);
//					if (usernameElt != null) {
//						userName = usernameElt.getValue();
//					} else {
//						throw new ParsingException("Expected Username");
//					}
//				} else {
//					elts = elt.getChildElements("KerberosV5Credential", WSConstants.INFOCARD_NAMESPACE);
//					if (elts.size() == 1) {
//						authType = KERB;
//					} else {
//						elts = elt.getChildElements("X509V3Credential", WSConstants.INFOCARD_NAMESPACE);
//						if (elts.size() == 1) {
//							authType = X509;
//							Element x509V3Credential = elts.get(0);
//							elts = x509V3Credential.getChildElements("X509Data", WSConstants.DSIG_NAMESPACE);
//							if (elts.size() == 1) {
//								Element x509Data = elts.get(0);
//								elts = x509Data.getChildElements("KeyIdentifier", WSConstants.WSSE_NAMESPACE_OASIS_10);
//								if (elts.size() == 1) {
//									Element keyIdentifier = elts.get(0);
//									// TODO check ValueType and EncodingType
//									x509Hash = keyIdentifier.getValue();
//								} else {
//									throw new ParsingException("Expected one KeyIdentifier but found: " + elts.size());
//								}
//							} else {
//								throw new ParsingException("Expected one X509Data but found: " + elts.size());
//							}
//						} else {
//							elts = elt.getChildElements("SelfIssuedCredential", WSConstants.INFOCARD_NAMESPACE);
//							if (elts.size() == 1) {
//								authType = SELF_ISSUED;
//								Element selfIssuedCredential = elts.get(0);
//								elts = selfIssuedCredential.getChildElements("PrivatePersonalIdentifier", WSConstants.INFOCARD_NAMESPACE);
//								if (elts.size() == 1) {
//									Element privatePersonalIdentifier = elts.get(0);
//									this.ppi = privatePersonalIdentifier.getValue();
//								} else {
//									throw new ParsingException("Expected one PrivatePersonalIdentifier but found: " + elts.size());
//								}
//							} else {
//								throw new ParsingException("Expected one of UsernamePasswordCredential, KerberosV5Credential, X509V3Credential or SelfIssuedCredential");
//							}
//						}
//						
//					}
//					
//				}
//			}
//			
//			
//
//		} else {
//			throw new ParsingException("Expected UserCredential but found" + name);
//		}
//	}
//	
//	public UserCredential(String authType, String value) {
//		setAuthType(authType, value);
//	}
//	
//    public String getHint() {
//		return hint;
//	}
//
//	public void setHint(String hint) {
//		this.hint = hint;
//	}
//
//    public String getAuthType() {
//        return authType;
//    }
//
//    public String getPPI() {
//        return ppi;
//    }
//
//    // use public void setAuthType(TokenServiceReference.SELF_ISSUED, String value)
//    private void setPPI(String ppi) {
//        this.ppi = ppi;
//    }
//
//    public String getKerberosServicePrincipalName() {
//        return kerberosServicePrincipalName;
//    }
//
//    // use public void setAuthType(TokenServiceReference.KERB, String value)
//    private void setKerberosServicePrincipalName(String kerberosServicePrincipalName) {
//        this.kerberosServicePrincipalName = kerberosServicePrincipalName;
//    }
//
//    public String getX509Hash() {
//        return x509Hash;
//    }
//
//    // use public void setAuthType(TokenServiceReference.X509, String value)
//    private void setX509Hash(String x509Hash) {
//        this.x509Hash = x509Hash;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    // use public void setAuthType(TokenServiceReference.USERNAME, String value)
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
////    public void setCert(X509Certificate cert) {
////        this.cert = cert;
////    }
//
//    protected void setAuthType(String authType, String value) {
//    	if (UserCredential.USERNAME.equals(authType)) {
//    		setUserName(value);
//    	} else if (UserCredential.KERB.equals(authType)) {
//    		setKerberosServicePrincipalName(value);
//    	} else if (UserCredential.SELF_ISSUED.equals(authType)) {
//    		setPPI(value);
//    	} else if (UserCredential.X509.equals(authType)) {
//    		setX509Hash(value);
//    	}else if (UserCredential.MAP.equals(authType)){
//    		mapServicePrincipalName= value;
//    	}
//    	else {
//    		throw new IllegalArgumentException("undefined authentication type (" + authType + ")");
//    	}
//    	
//		this.authType = authType;
//    }
//
//    public Element serialize() throws SerializationException {
//        Element userCredential = new Element(WSConstants.INFOCARD_PREFIX + ":UserCredential", WSConstants.INFOCARD_NAMESPACE);
//        if (MAP.equals(authType)) {
//        	//userCredential = new Element(WSConstants.INFOCARD_PREFIX + ":MAPCredential", WSConstants.INFOCARD_NAMESPACE);
//	        Element displayCredentialHint = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
//	        if (hint != null) {
//	        	displayCredentialHint.appendChild(hint);
//	        } else {
//	        	displayCredentialHint.appendChild("Please enter your username and password.");
//	        }
//	        userCredential.appendChild(displayCredentialHint);
//	
//	       // Element credential = new Element(WSConstants.INFOCARD_PREFIX + ":MAPCredential","other");// WSConstants.INFOCARD_NAMESPACE);
//	        Element credential = new Element("b:MAPCredential","https://rentacar.atosworldline.bancaire.test.fc2consortium.org/identity.xsd");// WSConstants.INFOCARD_NAMESPACE);
//	        Element username = new Element(WSConstants.INFOCARD_PREFIX + ":Username", WSConstants.INFOCARD_NAMESPACE);
//	        username.appendChild(userName);
//	        credential.appendChild(username);
//	        userCredential.appendChild(credential);
//        }else if (USERNAME.equals(authType)) {
//	        Element displayCredentialHint = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
//	        if (hint != null) {
//	        	displayCredentialHint.appendChild(hint);
//	        } else {
//	        	displayCredentialHint.appendChild("Please enter your username and password.");
//	        }
//	        userCredential.appendChild(displayCredentialHint);
//	
//	        Element credential = new Element(WSConstants.INFOCARD_PREFIX + ":UsernamePasswordCredential", WSConstants.INFOCARD_NAMESPACE);
//	        Element username = new Element(WSConstants.INFOCARD_PREFIX + ":Username", WSConstants.INFOCARD_NAMESPACE);
//	        username.appendChild(userName);
//	        credential.appendChild(username);
//	        userCredential.appendChild(credential);
//        } else if (KERB.equals(authType)) {
//	        Element displayCredentialHint = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
//	        if (hint != null) {
//	        	displayCredentialHint.appendChild(hint);
//	        } else {
//		        displayCredentialHint.appendChild("Enter your kerberos credentials");
//	        }
//	        userCredential.appendChild(displayCredentialHint);
//	        // <ic:KerberosV5Credential />
//	        Element credential = new Element(WSConstants.INFOCARD_PREFIX + ":KerberosV5Credential", WSConstants.INFOCARD_NAMESPACE);
//	        userCredential.appendChild(credential);
//	        /* To enable the service requester to obtain a Kerberos v5 service ticket for the IP/STS, the endpoint reference of the IP/STS 
//	         * in the information card or in the metadata retrieved from it must include a 'service principal name' identity claim under 
//	         * the wsid:Identity tag as defined in [Addressing-Ext]. http://www.w3.org/TR/2005/CR-ws-addr-core-20050817/
//	         */
//        } else if (SELF_ISSUED.equals(authType)) {
//	        Element displayCredentialHint = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
//	        if (hint != null) {
//	        	displayCredentialHint.appendChild(hint);
//	        } else {
//		        displayCredentialHint.appendChild("Choose a self-asserted card");
//	        }
//	        userCredential.appendChild(displayCredentialHint);
//        	/*
//	        	  <ic:SelfIssuedCredential>
//	        	    <ic:PrivatePersonalIdentifier>
//	        	      xs:base64Binary 
//	        	    </ic:PrivatePersonalIdentifier>
//	        	  </ic:SelfIssuedCredential>
//        	 */
//	        Element credential = new Element(WSConstants.INFOCARD_PREFIX + ":SelfIssuedCredential", WSConstants.INFOCARD_NAMESPACE);
//	        Element credentialValue = new Element(WSConstants.INFOCARD_PREFIX + ":PrivatePersonalIdentifier", WSConstants.INFOCARD_NAMESPACE);
//	        credentialValue.appendChild(ppi);
//	        credential.appendChild(credentialValue);
//	        userCredential.appendChild(credential);
//	        //System.out.println(userCredential.toXML());
//        } else if (X509.equals(authType)) {
//        	/*
//  				  <ic:DisplayCredentialHint> xs:string </ic:DisplayCredentialHint>
//  				  <ic:X509V3Credential>
//				    <ds:X509Data>
//				      <wsse:KeyIdentifier
//				        ValueType="http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soap-message-security-1.1#ThumbprintSHA1"
//				        EncodingType="http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soap-message-security-1.1#Base64Binary">
//				        xs:base64binary
//				      </wsse:KeyIdentifier>
//				    </ds:X509Data>
//				  </ic:X509V3Credential>
//        	 */
//	        Element displayCredentialHint = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
//	        if (hint != null) {
//	        	displayCredentialHint.appendChild(hint);
//	        } else {
//		        displayCredentialHint.appendChild("Choose a certificate");
//	        }
//	        userCredential.appendChild(displayCredentialHint);
//	
//	        Element credential = new Element(WSConstants.INFOCARD_PREFIX + ":X509V3Credential", WSConstants.INFOCARD_NAMESPACE);
//	        Element x509Data = new Element(WSConstants.DSIG_PREFIX + ":X509Data", WSConstants.DSIG_NAMESPACE);
//	        Element keyIdentifier = new Element(WSConstants.WSSE_PREFIX + ":KeyIdentifier", WSConstants.WSSE_NAMESPACE_OASIS_10);
//	        Attribute valueType = new Attribute("ValueType", WSConstants.WSSE_OASIS_XX_THUMBPRINTSHA1);
//	        Attribute encodingType = new Attribute("EncodingType", WSConstants.WSSE_OASIS_XX_BASE64BINARY);
//	        keyIdentifier.addAttribute(valueType);
//	        keyIdentifier.addAttribute(encodingType);
//	        keyIdentifier.appendChild(x509Hash);
//	        x509Data.appendChild(keyIdentifier);
//	        credential.appendChild(x509Data);
//	        userCredential.appendChild(credential);
//        } else {
//        	throw new SerializationException("unsupported authentication type:" + authType);
//        }
//        return userCredential;
//    }
//
//}

//package org.xmldap.infocard;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.xmldap.exceptions.ParsingException;
//import org.xmldap.exceptions.SerializationException;
//import org.xmldap.ws.WSConstants;
//
//import nu.xom.Attribute;
//import nu.xom.Element;
//import nu.xom.Elements;
//
//public class UserCredential {
//    public static final String USERNAME = "UserNamePasswordAuthenticate";
//    public static final String SELF_ISSUED = "SelfIssuedAuthenticate";
//    public static final String X509 = "X509V3Authenticate";
//    public static final String KERB = "KerberosV5Authenticate";
//
//
//    private String authType = USERNAME;
//    
//    private String hint = null;
//    
//	private String userName = null;
//    private String ppi = null;
//    private String x509Hash = null;
//    private String kerberosServicePrincipalName = null;
//    
//	public UserCredential(Element elt) throws ParsingException {
//		String name = elt.getLocalName();
//		if ("UserCredential".equals(name)) {
//			Elements elts = elt.getChildElements("DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
//			if (elts.size() == 1) {
//				Element displayCredentialHintElt = elts.get(0);
//				hint = displayCredentialHintElt.getValue();
//			} else {
//				if (elts.size() > 1) {
//					throw new ParsingException("Expected zero or one UserCredential but found" + elts.size());
//				}
//				// else size == 0. OK
//				hint = null;
//			}
//			
//			elts = elt.getChildElements("UsernamePasswordCredential", WSConstants.INFOCARD_NAMESPACE);
//			if (elts.size() == 1) {
//				authType = USERNAME;
//				Element usernamePasswordCredential = elts.get(0);
//				Element usernameElt = usernamePasswordCredential.getFirstChildElement("Username", WSConstants.INFOCARD_NAMESPACE);
//				if (usernameElt != null) {
//					userName = usernameElt.getValue();
//				} else {
//					throw new ParsingException("Expected Username");
//				}
//			} else {
//				elts = elt.getChildElements("KerberosV5Credential", WSConstants.INFOCARD_NAMESPACE);
//				if (elts.size() == 1) {
//					authType = KERB;
//				} else {
//					elts = elt.getChildElements("X509V3Credential", WSConstants.INFOCARD_NAMESPACE);
//					if (elts.size() == 1) {
//						authType = X509;
//						Element x509V3Credential = elts.get(0);
//						elts = x509V3Credential.getChildElements("X509Data", WSConstants.DSIG_NAMESPACE);
//						if (elts.size() == 1) {
//							Element x509Data = elts.get(0);
//							elts = x509Data.getChildElements("KeyIdentifier", WSConstants.WSSE_NAMESPACE_OASIS_10);
//							if (elts.size() == 1) {
//								Element keyIdentifier = elts.get(0);
//								// TODO check ValueType and EncodingType
//								x509Hash = keyIdentifier.getValue();
//							} else {
//								throw new ParsingException("Expected one KeyIdentifier but found: " + elts.size());
//							}
//						} else {
//							throw new ParsingException("Expected one X509Data but found: " + elts.size());
//						}
//					} else {
//						elts = elt.getChildElements("SelfIssuedCredential", WSConstants.INFOCARD_NAMESPACE);
//						if (elts.size() == 1) {
//							authType = SELF_ISSUED;
//							Element selfIssuedCredential = elts.get(0);
//							elts = selfIssuedCredential.getChildElements("PrivatePersonalIdentifier", WSConstants.INFOCARD_NAMESPACE);
//							if (elts.size() == 1) {
//								Element privatePersonalIdentifier = elts.get(0);
//								this.ppi = privatePersonalIdentifier.getValue();
//							} else {
//								throw new ParsingException("Expected one PrivatePersonalIdentifier but found: " + elts.size());
//							}
//						} else {
//							throw new ParsingException("Expected one of UsernamePasswordCredential, KerberosV5Credential, X509V3Credential or SelfIssuedCredential");
//						}
//					}
//					
//				}
//				
//			}
//			
//
//		} else {
//			throw new ParsingException("Expected UserCredential but found" + name);
//		}
//	}
//	
//	public UserCredential(String authType, String value) {
//		setAuthType(authType, value);
//	}
//	
//    public String getHint() {
//		return hint;
//	}
//
//	public void setHint(String hint) {
//		this.hint = hint;
//	}
//
//    public String getAuthType() {
//        return authType;
//    }
//
//    public String getPPI() {
//        return ppi;
//    }
//
//    // use public void setAuthType(TokenServiceReference.SELF_ISSUED, String value)
//    private void setPPI(String ppi) {
//        this.ppi = ppi;
//    }
//
//    public String getKerberosServicePrincipalName() {
//        return kerberosServicePrincipalName;
//    }
//
//    // use public void setAuthType(TokenServiceReference.KERB, String value)
//    private void setKerberosServicePrincipalName(String kerberosServicePrincipalName) {
//        this.kerberosServicePrincipalName = kerberosServicePrincipalName;
//    }
//
//    public String getX509Hash() {
//        return x509Hash;
//    }
//
//    // use public void setAuthType(TokenServiceReference.X509, String value)
//    private void setX509Hash(String x509Hash) {
//        this.x509Hash = x509Hash;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    // use public void setAuthType(TokenServiceReference.USERNAME, String value)
//    private void setUserName(String userName) {
//        this.userName = userName;
//    }
//
////    public void setCert(X509Certificate cert) {
////        this.cert = cert;
////    }
//
//    protected void setAuthType(String authType, String value) {
//    	if (UserCredential.USERNAME.equals(authType)) {
//    		setUserName(value);
//    	} else if (UserCredential.KERB.equals(authType)) {
//    		setKerberosServicePrincipalName(value);
//    	} else if (UserCredential.SELF_ISSUED.equals(authType)) {
//    		setPPI(value);
//    	} else if (UserCredential.X509.equals(authType)) {
//    		setX509Hash(value);
//    	} else {
//    		throw new IllegalArgumentException("undefined authentication type (" + authType + ")");
//    	}
//		this.authType = authType;
//    }
//
//    public JSONObject toJSON() throws SerializationException {
//      try {
//        JSONObject json = new JSONObject();
//        json.put("Type", authType);
//        if (USERNAME.equals(authType)) {
//          json.put("Username", userName);
//        } else if (KERB.equals(authType)) {
//          // TODO
//          throw new SerializationException("Unsupported Authentication Type: " + authType);
//        } else if (SELF_ISSUED.equals(authType)) {
//          json.put("PPID", ppi);
//        } else if (X509.equals(authType)) {
//          json.put("X509Hash", x509Hash);
//        } else {
//          throw new SerializationException("Unsupported Authentication Type: " + authType);
//        }
//        if (hint != null) {
//          json.put("Hint", hint);
//        }
//        return json;
//      } catch (JSONException e) {
//        throw new SerializationException(e);
//      }
//
//    }
//
//    public Element serialize() throws SerializationException {
//        Element userCredential = new Element(WSConstants.INFOCARD_PREFIX + ":UserCredential", WSConstants.INFOCARD_NAMESPACE);
//        if (USERNAME.equals(authType)) {
//	        Element displayCredentialHint = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
//	        if (hint != null) {
//	        	displayCredentialHint.appendChild(hint);
//	        } else {
//	        	displayCredentialHint.appendChild("Please enter your username and password.");
//	        }
//	        userCredential.appendChild(displayCredentialHint);
//	
//	        Element credential = new Element(WSConstants.INFOCARD_PREFIX + ":UsernamePasswordCredential", WSConstants.INFOCARD_NAMESPACE);
//	        Element username = new Element(WSConstants.INFOCARD_PREFIX + ":Username", WSConstants.INFOCARD_NAMESPACE);
//	        username.appendChild(userName);
//	        credential.appendChild(username);
//	        userCredential.appendChild(credential);
//        } else if (KERB.equals(authType)) {
//	        Element displayCredentialHint = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
//	        if (hint != null) {
//	        	displayCredentialHint.appendChild(hint);
//	        } else {
//		        displayCredentialHint.appendChild("Enter your kerberos credentials");
//	        }
//	        userCredential.appendChild(displayCredentialHint);
//	        // <ic:KerberosV5Credential />
//	        Element credential = new Element(WSConstants.INFOCARD_PREFIX + ":KerberosV5Credential", WSConstants.INFOCARD_NAMESPACE);
//	        userCredential.appendChild(credential);
//	        /* To enable the service requester to obtain a Kerberos v5 service ticket for the IP/STS, the endpoint reference of the IP/STS 
//	         * in the information card or in the metadata retrieved from it must include a 'service principal name' identity claim under 
//	         * the wsid:Identity tag as defined in [Addressing-Ext]. http://www.w3.org/TR/2005/CR-ws-addr-core-20050817/
//	         */
//        } else if (SELF_ISSUED.equals(authType)) {
//	        Element displayCredentialHint = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
//	        if (hint != null) {
//	        	displayCredentialHint.appendChild(hint);
//	        } else {
//		        displayCredentialHint.appendChild("Choose a self-asserted card");
//	        }
//	        userCredential.appendChild(displayCredentialHint);
//        	/*
//	        	  <ic:SelfIssuedCredential>
//	        	    <ic:PrivatePersonalIdentifier>
//	        	      xs:base64Binary 
//	        	    </ic:PrivatePersonalIdentifier>
//	        	  </ic:SelfIssuedCredential>
//        	 */
//	        Element credential = new Element(WSConstants.INFOCARD_PREFIX + ":SelfIssuedCredential", WSConstants.INFOCARD_NAMESPACE);
//	        Element credentialValue = new Element(WSConstants.INFOCARD_PREFIX + ":PrivatePersonalIdentifier", WSConstants.INFOCARD_NAMESPACE);
//	        credentialValue.appendChild(ppi);
//	        credential.appendChild(credentialValue);
//	        userCredential.appendChild(credential);
//	        //System.out.println(userCredential.toXML());
//        } else if (X509.equals(authType)) {
//        	/*
//  				  <ic:DisplayCredentialHint> xs:string </ic:DisplayCredentialHint>
//  				  <ic:X509V3Credential>
//				    <ds:X509Data>
//				      <wsse:KeyIdentifier
//				        ValueType="http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soap-message-security-1.1#ThumbprintSHA1"
//				        EncodingType="http://docs.oasis-open.org/wss/2004/xx/oasis-2004xx-wss-soap-message-security-1.1#Base64Binary">
//				        xs:base64binary
//				      </wsse:KeyIdentifier>
//				    </ds:X509Data>
//				  </ic:X509V3Credential>
//        	 */
//	        Element displayCredentialHint = new Element(WSConstants.INFOCARD_PREFIX + ":DisplayCredentialHint", WSConstants.INFOCARD_NAMESPACE);
//	        if (hint != null) {
//	        	displayCredentialHint.appendChild(hint);
//	        } else {
//		        displayCredentialHint.appendChild("Choose a certificate");
//	        }
//	        userCredential.appendChild(displayCredentialHint);
//	
//	        Element credential = new Element(WSConstants.INFOCARD_PREFIX + ":X509V3Credential", WSConstants.INFOCARD_NAMESPACE);
//	        Element x509Data = new Element(WSConstants.DSIG_PREFIX + ":X509Data", WSConstants.DSIG_NAMESPACE);
//	        Element keyIdentifier = new Element(WSConstants.WSSE_PREFIX + ":KeyIdentifier", WSConstants.WSSE_NAMESPACE_OASIS_10);
//	        Attribute valueType = new Attribute("ValueType", WSConstants.WSSE_OASIS_XX_THUMBPRINTSHA1);
//	        Attribute encodingType = new Attribute("EncodingType", WSConstants.WSSE_OASIS_XX_BASE64BINARY);
//	        keyIdentifier.addAttribute(valueType);
//	        keyIdentifier.addAttribute(encodingType);
//	        keyIdentifier.appendChild(x509Hash);
//	        x509Data.appendChild(keyIdentifier);
//	        credential.appendChild(x509Data);
//	        userCredential.appendChild(credential);
//        } else {
//        	throw new SerializationException("unsupported authentication type:" + authType);
//        }
//        return userCredential;
//    }
//
//}
