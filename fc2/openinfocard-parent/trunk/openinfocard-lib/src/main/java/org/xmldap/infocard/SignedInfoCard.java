/*
 * Copyright (c) 2008, Axel Nennker - http://axel.nennker.de/
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
 *     * The names of the contributors may NOT be used to endorse or promote products
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
 * 
 */

package org.xmldap.infocard;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ValidityException;

import org.xmldap.exceptions.CryptoException;
import org.xmldap.exceptions.ParsingException;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.util.XmlFileUtil;
import org.xmldap.ws.WSConstants;
import org.xmldap.xmldsig.ValidatingEnvelopedSignature;

/**
 * @author Nennker.Axel
 *
 */
public class SignedInfoCard extends InfoCard {

	String xml;
	public String toXML() throws SerializationException{
		return xml;
	}
	public SignedInfoCard(String xml)  throws ParsingException {
		try {
			Element root;
			root = XmlFileUtil.readXml(new ByteArrayInputStream(xml.getBytes())).getRootElement();
			constructFromInfoCardElement(root);
			this.xml= xml;
			//SignedInfoCard card = new SignedInfoCard(root);
			//return card;

		} catch (ValidityException e) {
			throw new ParsingException("Signature is not valid");
		} catch (IOException e) {
			throw new ParsingException("Signature is not valid");
		}  catch (nu.xom.ParsingException e) {
			throw new ParsingException("Signature is not valid");
		} 
	}
	void constructFromInfoCardElement(Element infoCardElement) throws ParsingException{
		ValidatingEnvelopedSignature signature = new ValidatingEnvelopedSignature(infoCardElement);
    	try {
			Element validatedInfoCardElement = signature.validate();
			Elements elts = validatedInfoCardElement.getChildElements("InformationCard", WSConstants.INFOCARD_NAMESPACE);
			if(elts.size()==1){
				if (validatedInfoCardElement != null) {
					createFromElement(elts.get(0));
				} else {
					throw new ParsingException("Signature is not valid");
				}
			}
			
		} catch (CryptoException e) {
			throw new ParsingException(e);
		}
	}
    public SignedInfoCard(Element infoCardElement) throws ParsingException {
    	constructFromInfoCardElement(infoCardElement);
    }

}


///*
// * Copyright (c) 2008, Axel Nennker - http://axel.nennker.de/
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// *
// *     * Redistributions of source code must retain the above copyright
// *       notice, this list of conditions and the following disclaimer.
// *     * Redistributions in binary form must reproduce the above copyright
// *       notice, this list of conditions and the following disclaimer in the
// *       documentation and/or other materials provided with the distribution.
// *     * The names of the contributors may NOT be used to endorse or promote products
// *       derived from this software without specific prior written permission.
// *
// * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
// * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
// * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// * 
// */
//
//package org.xmldap.infocard;
//
//import nu.xom.Element;
//
//import org.xmldap.exceptions.CryptoException;
//import org.xmldap.exceptions.ParsingException;
//import org.xmldap.xmldsig.ValidatingEnvelopedSignature;
//
///**
// * @author Nennker.Axel
// *
// */
//public class SignedInfoCard extends InfoCard {
//
//    public SignedInfoCard(Element infoCardElement) throws ParsingException {
//    	ValidatingEnvelopedSignature signature = new ValidatingEnvelopedSignature(infoCardElement);
//    	try {
//			Element validatedInfoCardElement = signature.validate();
//			if (validatedInfoCardElement != null) {
//				createFromElement(validatedInfoCardElement);
//			} else {
//				throw new ParsingException("Signature is not valid");
//			}
//		} catch (CryptoException e) {
//			throw new ParsingException(e);
//		}
//    }
//
//}
