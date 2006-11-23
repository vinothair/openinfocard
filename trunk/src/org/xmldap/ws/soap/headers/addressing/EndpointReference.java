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

package org.xmldap.ws.soap.headers.addressing;

import nu.xom.Element;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.ws.WSConstants;
import org.xmldap.xml.Serializable;

public class EndpointReference implements Serializable {

    String sts = null;
    String mex = null;

    public EndpointReference(String sts, String mex) {
        this.sts = sts;
        this.mex = mex;
    }

    protected Element getEPR() throws SerializationException {

        Element epr = new Element("wsa:EndpointReference", WSConstants.WSA_NAMESPACE_05_08);

        Element addressElm = new Element("wsa:Address", WSConstants.WSA_NAMESPACE_05_08);
        addressElm.appendChild(sts);
        epr.appendChild(addressElm);

        Element metaDataElm = new Element("wsa:Metadata", WSConstants.WSA_NAMESPACE_05_08);


        Element mexMetaDataElm = new Element("mex:Metadata", WSConstants.MEX_04_09);
        //mexMetaDataElm.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        //mexMetaDataElm.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");

        Element mexMetaDataSectionElm = new Element("mex:MetadataSection", WSConstants.MEX_04_09);
        Element mexMetaDataReferenceElm = new Element("mex:MetadataReference", WSConstants.MEX_04_09);



        Element mexAddressElm = new Element("wsa:Address", WSConstants.WSA_NAMESPACE_05_08);
        mexAddressElm.appendChild(mex);
        mexMetaDataReferenceElm.appendChild(mexAddressElm);
        mexMetaDataSectionElm.appendChild(mexMetaDataReferenceElm);
        mexMetaDataElm.appendChild(mexMetaDataSectionElm);
        metaDataElm.appendChild(mexMetaDataElm);
        epr.appendChild(metaDataElm);
        return epr;

    }

    public String toXML() throws SerializationException {
        Element epr = serialize();
        return epr.toXML();
    }

    public Element serialize() throws SerializationException {

        return getEPR();

    }
}
