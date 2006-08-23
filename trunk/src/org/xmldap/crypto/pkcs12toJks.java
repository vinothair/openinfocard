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

package org.xmldap.crypto;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;


/**
 * pkcs12toJks converts a PKCS12 keystore to a jkskeystore.   At the moment its hardcoded, as I only needed it once.
 */
public class pkcs12toJks {


    public static void main(String[] args) throws Exception {

        KeyStore kspkcs12 = KeyStore.getInstance("PKCS12");
        kspkcs12.load(new FileInputStream("./conf/export.pkcs12"), "password".toCharArray());

        KeyStore ksjks = KeyStore.getInstance("JKS");
        ksjks.load(new FileInputStream("./conf/xmldap.jks"), "storepassword".toCharArray());

        Certificate c[] = kspkcs12.getCertificateChain("Server-Cert");
        Key key = kspkcs12.getKey("Server-Cert", "password".toCharArray());

        ksjks.setKeyEntry("Server-Cert", key, "keypassword".toCharArray(), c);
        ksjks.store(new FileOutputStream("./conf/xmldap.org.jks"), "storepassword".toCharArray());


    }
}
