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
 *     * Neither the name of the University of California, Berkeley nor the
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

package org.xmldap.rp.util;

import nu.xom.*;
import org.xmldap.exceptions.CryptoException;
import org.xmldap.ws.WSConstants;

import java.io.IOException;
import java.util.HashMap;

public class ClaimParserUtil {



    public HashMap parseClaims(String toValidate) throws CryptoException {

        Builder parser = new Builder();
        Document assertion = null;
        try {
            assertion = parser.build(toValidate, "");
            return parseClaims(assertion);
        } catch (ParsingException e) {
            throw new CryptoException(e);
        } catch (IOException e) {
            throw new CryptoException(e);
        }

    }


    public HashMap parseClaims(Document assertion){

        HashMap map = new HashMap();
        XPathContext context = new XPathContext();
        context.addNamespace("saml", WSConstants.SAML11_NAMESPACE);
        Nodes claims = assertion.query("/saml:Assertion/saml:AttributeStatement/saml:Attribute", context);
        for (int i = 0; i < claims.size(); i++) {

            Element claim = (Element) claims.get(i);
            Attribute nameAttr = claim.getAttribute("AttributeName");
            String name = nameAttr.getValue();
            Element valueElm = claim.getFirstChildElement("AttributeValue", WSConstants.SAML11_NAMESPACE);
            String value = valueElm.getValue();

            map.put(name,value);

        }
        return map;

    }
}
