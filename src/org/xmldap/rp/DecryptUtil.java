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

package org.xmldap.rp;

import net.sourceforge.lightcrypto.SafeObject;
import nu.xom.*;
import org.xmldap.crypto.CryptoUtils;
import org.xmldap.ws.WSConstants;
import org.xmldap.exceptions.CryptoException;

import java.io.IOException;
import java.security.PrivateKey;


public class DecryptUtil {

    public String decryptToken(String encryptedXML, PrivateKey key) throws CryptoException {

        Builder parser = new Builder();
        Document xml = null;
        try {
            xml = parser.build(encryptedXML, "");
        } catch (ParsingException e) {
            throw new CryptoException("Error buidling a XOM Document out of encrypted token", e);
        } catch (IOException e) {
            throw new CryptoException("Error buidling a XOM Document out of encrypted token", e);
        }

        XPathContext context = new XPathContext();
        context.addNamespace("enc", WSConstants.ENC_NAMESPACE);
        context.addNamespace("dsig", WSConstants.DSIG_NAMESPACE);
        context.addNamespace("wsse", WSConstants.WSSE_NAMESPACE_OASIS_10);

        Nodes keys = xml.query("/enc:EncryptedData/dsig:KeyInfo/enc:EncryptedKey/enc:CipherData/enc:CipherValue", context);
        Element cipherValue = (Element) keys.get(0);
        String keyCipherText = cipherValue.getValue();


        Nodes dataNodes = xml.query("/enc:EncryptedData/enc:CipherData/enc:CipherValue", context);
        Element dataCipherValue = (Element) dataNodes.get(0);
        String dataCipherText = dataCipherValue.getValue();


        byte[] clearTextKey = null;
        try {
            clearTextKey = CryptoUtils.decryptRSAOAEP(keyCipherText, key);
        } catch (org.xmldap.exceptions.CryptoException e) {
            throw new CryptoException("Error using RSA to decrypt the AES Encryption Key", e);
        }

        SafeObject keyBytes = new SafeObject();
        try {
            keyBytes.setText(clearTextKey);
        } catch (Exception e) {
            throw new CryptoException("Error Generating SafeObject for AES decryption of token", e);
        }
        StringBuffer clearTextBuffer = new StringBuffer(dataCipherText);

        StringBuffer clearText = null;
        try {
            clearText = CryptoUtils.decryptAESCBC(clearTextBuffer, keyBytes);
        } catch (org.xmldap.exceptions.CryptoException e) {
            throw new CryptoException("Error performing AES decryption of token", e);
        }

        return clearText.toString();

    }


}
