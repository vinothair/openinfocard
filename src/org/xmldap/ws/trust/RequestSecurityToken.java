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

package org.xmldap.ws.trust;

import nu.xom.Element;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Serializable;

public class RequestSecurityToken implements Serializable {


    String requestType = null;
    String tokenType = null;
    Element securityTokenReference = null;


    String claims = null;
    String entropy = null;
    String lifetime = null;
    String allowPostdating = null;
    String renewing = null;
    String onBehalfOf = null;
    String issuer = null;
    String authenticationType = null;
    String keyType = null;
    String keySize = null;
    String signatureAlgorithm = null;
    String encryption = null;
    String encryptionAlgorithm = null;
    String canonicalizationAlgorithm = null;
    String proofEncryption = null;
    UseKey useKey = null;
    String signWith = null;
    String encryptWith = null;
    String delegateTo = null;
    String forwardable = null;
    String delegatable = null;

    Element appliesTo = null;
    String policy = null;
    String policyReference = null;


    private RequestSecurityToken() {}

    public RequestSecurityToken(String requestType) {
        this.requestType = requestType;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Element getSecurityTokenReference() {
        return securityTokenReference;
    }

    public void setSecurityTokenReference(Element securityTokenReference) {
        this.securityTokenReference = securityTokenReference;
    }

    public Element getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(Element appliesTo) {
        this.appliesTo = appliesTo;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public void setUseKey(UseKey useKey) {
    	this.useKey = useKey;
    }
    
    public String toXML() throws SerializationException {

        Element rst = serialize();
        return rst.toXML();
    }

    /*
     * <wst:RequestSecurityToken>
     *  <wst:TokenType>
     *   urn:oasis:names:tc:SAML:1.0:assertion
     *  </wst:TokenType>
     *  <wst:RequestType>
     *   http://schemas.xmlsoap.org/ws/2005/02/trust/Issue
     *  </wst:RequestType>
     *  <wst:KeyType>
     *   http://schemas.xmlsoap.org/ws/2005/02/trust/PublicKey
     *  </wst:KeyType>
     *  <ic:InformationCardReference>
     *   <ic:CardId> http://contoso.com/id/d795621fa01d454285f9</ic:CardId>
     *  </ic:InformationCardReference>
     *  <wst:Claims
     *   wst:Dialect=”http://schemas.xmlsoap.org/ws/2005/05/identity”>
     *   <ic:ClaimType Uri=”http://.../identity/claims/givenname”/>
     *   <ic:ClaimType Uri=”http://.../identity/claims/surname”/>
     *  </wst:Claims>
     *  <ic:ClientPseudonym>
     *   <ic:PPID>NHbuoB4KVKuvUx7b8szaux+bM8Rr0rPTPOXQlQTEBAo=</ic:PPID>
     *  </ic:ClientPseudonym>
     *  <wst:UseKey Sig="#_46">
     *   <ds:KeyInfo>
     *    <ds:KeyValue>
     *     <ds:RSAKeyValue>
     *      <ds:Modulus>...</ds:Modulus>
     *      <ds:Exponent>...</ds:Exponent>
     *     </ds:RSAKeyValue>
     *    </ds:KeyValue>
     *   </ds:KeyInfo>
     *  </wst:UseKey>
     *  <ic:RequestDisplayToken xml:lang="en-us" />
     * </wst:RequestSecurityToken>
     */
    public Element serialize() throws SerializationException {

        Element rst = new Element(WSConstants.TRUST_PREFIX + ":RequestSecurityToken", WSConstants.TRUST_NAMESPACE_04_04);

        Element requestTypeElm = new Element(WSConstants.TRUST_PREFIX + ":RequestType", WSConstants.TRUST_NAMESPACE_04_04);
        requestTypeElm.appendChild(requestType);
        rst.appendChild(requestTypeElm);

        if (tokenType != null) {
            Element element = new Element(WSConstants.TRUST_PREFIX + ":TokenType", WSConstants.TRUST_NAMESPACE_04_04);
            element.appendChild(tokenType);
            rst.appendChild(element);
        }

        if (appliesTo != null) {
            Element element = new Element(WSConstants.POLICY_PREFIX + ":AppliesTo", WSConstants.POLICY_NAMESPACE_02_12);
            element.appendChild(appliesTo);
            rst.appendChild(element);
        }

        if (keyType != null) {
        	Element element = new Element(WSConstants.TRUST_PREFIX + ":KeyType", WSConstants.TRUST_NAMESPACE_04_04);
        	element.appendChild(keyType);
        	rst.appendChild(element);
        }
        
        if (useKey != null) {
        	rst.appendChild(useKey.serialize());
        }
        rst.appendChild(getSecurityTokenReference());

        return rst;
    }

}
