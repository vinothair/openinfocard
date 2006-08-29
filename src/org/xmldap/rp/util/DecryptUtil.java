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

import net.sourceforge.lightcrypto.SafeObject;
import nu.xom.*;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.util.Base64;
import org.xmldap.ws.WSConstants;

import java.io.IOException;
import java.security.PrivateKey;


public class DecryptUtil {


    private Document parse(String xml) {

        Builder parser = new Builder();
        Document doc = null;
        try {
            doc = parser.build(xml, "");

            //TODO - improve error handling
        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;

    }


    public StringBuffer decryptXML(String encryptedXML, PrivateKey key) {

        Document xml = parse(encryptedXML);

        XPathContext context = new XPathContext();
        context.addNamespace("enc", WSConstants.ENC_NAMESPACE);
        context.addNamespace("dsig", WSConstants.DSIG_NAMESPACE);
        context.addNamespace("wsse", WSConstants.WSSE_NAMESPACE_OASIS_10);

        Nodes fingerprints = xml.query("//wsse:KeyIdentifier", context);
        Element fingerprintElm = (Element) fingerprints.get(0);
        String fingerprint = fingerprintElm.getValue();
        byte[] fingerPrintBytes = Base64.decode(fingerprint);


        Nodes keys = xml.query("/enc:EncryptedData/dsig:KeyInfo/enc:EncryptedKey/enc:CipherData/enc:CipherValue", context);
        Element cipherValue = (Element) keys.get(0);
        String keyCipherText = cipherValue.getValue();


        Nodes dataNodes = xml.query("/enc:EncryptedData/enc:CipherData/enc:CipherValue", context);
        Element dataCipherValue = (Element) dataNodes.get(0);
        String dataCipherText = dataCipherValue.getValue();
        //System.out.println("Data Cipher Text: " + dataCipherText);


        byte[] clearTextKey = null;
        try {
            clearTextKey = CryptoUtils.decryptRSAOAEP(keyCipherText, key);
        } catch (org.xmldap.exceptions.CryptoException e) {
            e.printStackTrace();
        }


        SafeObject keyBytes = new SafeObject();
        try {
            keyBytes.setText(clearTextKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer clearTextBuffer = new StringBuffer(dataCipherText);

        StringBuffer clearText = null;
        try {
            clearText = CryptoUtils.decryptAESCBC(clearTextBuffer, keyBytes);
        } catch (org.xmldap.exceptions.CryptoException e) {
            e.printStackTrace();
        }
        return clearText;

    }


}
