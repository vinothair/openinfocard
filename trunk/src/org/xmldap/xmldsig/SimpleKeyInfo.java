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

package org.xmldap.xmldsig;

import nu.xom.Element;
import org.xmldap.exceptions.SerializationException;
import org.xmldap.util.Base64;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;


public class SimpleKeyInfo implements KeyInfo {

    X509Certificate cert = null;

    public SimpleKeyInfo(X509Certificate cert) {
        this.cert = cert;
    }

    private Element getKeyInfo() throws SerializationException {

        //TODO - extract constants!
        Element keyInfo = new Element("dsig:KeyInfo", "http://www.w3.org/2000/09/xmldsig#");

        Element x509Data = new Element("dsig:X509Data", "http://www.w3.org/2000/09/xmldsig#");
        Element x509Certificate = new Element("dsig:X509Certificate", "http://www.w3.org/2000/09/xmldsig#");
        Element x509Certificate2 = new Element("dsig:X509Certificate", "http://www.w3.org/2000/09/xmldsig#");
        Element x509Certificate3 = new Element("dsig:X509Certificate", "http://www.w3.org/2000/09/xmldsig#");

        try {
            x509Certificate.appendChild(Base64.encodeBytesNoBreaks(cert.getEncoded()));
        } catch (CertificateEncodingException e) {
            throw new SerializationException("Error getting Cert for keyinfo", e);
        }

        x509Certificate2.appendChild("MIIEQTCCA6qgAwIBAgICAQQwDQYJKoZIhvcNAQEFBQAwgbsxJDAiBgNVBAcTG1ZhbGlDZXJ0IFZhbGlkYXRpb24gTmV0d29yazEXMBUGA1UEChMOVmFsaUNlcnQsIEluYy4xNTAzBgNVBAsTLFZhbGlDZXJ0IENsYXNzIDIgUG9saWN5IFZhbGlkYXRpb24gQXV0aG9yaXR5MSEwHwYDVQQDExhodHRwOi8vd3d3LnZhbGljZXJ0LmNvbS8xIDAeBgkqhkiG9w0BCQEWEWluZm9AdmFsaWNlcnQuY29tMB4XDTA0MDExNDIxMDUyMVoXDTI0MDEwOTIxMDUyMVowgewxCzAJBgNVBAYTAlVTMRAwDgYDVQQIEwdBcml6b25hMRMwEQYDVQQHEwpTY290dHNkYWxlMSUwIwYDVQQKExxTdGFyZmllbGQgVGVjaG5vbG9naWVzLCBJbmMuMTAwLgYDVQQLEydodHRwOi8vd3d3LnN0YXJmaWVsZHRlY2guY29tL3JlcG9zaXRvcnkxMTAvBgNVBAMTKFN0YXJmaWVsZCBTZWN1cmUgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkxKjAoBgkqhkiG9w0BCQEWG3ByYWN0aWNlc0BzdGFyZmllbGR0ZWNoLmNvbTCBnTANBgkqhkiG9w0BAQEFAAOBiwAwgYcCgYEA2xFDa9zRaXhZSehudBQIdBFsfrcqqCLYQjx6z59QskaupmcaIyK+D7M0+6yskKpbKMJw9raKgCrgm5xS4JGocqAW4cROfREJs5651POyUMRtSAi9vCqXDG2jimo8ms9KNNwe3upaJsChooKpSvuGIhKQOrKC1JKRn6lFn8Ok2/sCAQOjggEhMIIBHTAMBgNVHRMEBTADAQH/MAsGA1UdDwQEAwIBBjBKBgNVHR8EQzBBMD+gPaA7hjlodHRwOi8vY2VydGlmaWNhdGVzLnN0YXJmaWVsZHRlY2guY29tL3JlcG9zaXRvcnkvcm9vdC5jcmwwTwYDVR0gBEgwRjBEBgtghkgBhvhFAQcXAzA1MDMGCCsGAQUFBwIBFidodHRwOi8vd3d3LnN0YXJmaWVsZHRlY2guY29tL3JlcG9zaXRvcnkwOQYIKwYBBQUHAQEELTArMCkGCCsGAQUFBzABhh1odHRwOi8vb2NzcC5zdGFyZmllbGR0ZWNoLmNvbTAdBgNVHQ4EFgQUrFXet+oT6/yYaOJTYB7xJT6M7ucwCQYDVR0jBAIwADANBgkqhkiG9w0BAQUFAAOBgQB+HJi+rQONJYXufJCIIiv+J/RCsux/tfxyaAWkfZHvKNF9IDk7eQg3aBhS1Y8D0olPHhHR6aV0S/xfZ2WEcYR4WbfWydfXkzXmE6uUPI6TQImMwNfy5wdS0XCPmIzroG3RNlOQoI8WMB7ew79/RqWVKvnI3jvbd/TyMrEzYaIwNQ==");
        x509Certificate3.appendChild("MIIC5zCCAlACAQEwDQYJKoZIhvcNAQEFBQAwgbsxJDAiBgNVBAcTG1ZhbGlDZXJ0IFZhbGlkYXRpb24gTmV0d29yazEXMBUGA1UEChMOVmFsaUNlcnQsIEluYy4xNTAzBgNVBAsTLFZhbGlDZXJ0IENsYXNzIDIgUG9saWN5IFZhbGlkYXRpb24gQXV0aG9yaXR5MSEwHwYDVQQDExhodHRwOi8vd3d3LnZhbGljZXJ0LmNvbS8xIDAeBgkqhkiG9w0BCQEWEWluZm9AdmFsaWNlcnQuY29tMB4XDTk5MDYyNjAwMTk1NFoXDTE5MDYyNjAwMTk1NFowgbsxJDAiBgNVBAcTG1ZhbGlDZXJ0IFZhbGlkYXRpb24gTmV0d29yazEXMBUGA1UEChMOVmFsaUNlcnQsIEluYy4xNTAzBgNVBAsTLFZhbGlDZXJ0IENsYXNzIDIgUG9saWN5IFZhbGlkYXRpb24gQXV0aG9yaXR5MSEwHwYDVQQDExhodHRwOi8vd3d3LnZhbGljZXJ0LmNvbS8xIDAeBgkqhkiG9w0BCQEWEWluZm9AdmFsaWNlcnQuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDOOnHK5avIWZJV16vYdA757tn2VUdZZUcOBVXc65g2PFxTXdMwzzjsvUGJ7SVCCSRrCl6zfN1SLUzm1NZ9WlmpZdRJEy0kTRxQb7XBhVQ7/nHk01xC+YDgkRoKWzk2Z/M/VXwbP7RfZHM047QSv4dk+NoS/zcnwbNDu+97bi5p9wIDAQABMA0GCSqGSIb3DQEBBQUAA4GBADt/UG9vUJSZSWI4OB9L+KXIPqeCgfYrx+jFzug6EILLGACOTb2oWH+heQC1u+mNr0HZDzTuIYEZoDJJKPTEjlbVUjP9UNV+mWwD5MlM/Mtsq2azSiGM5bUMMj4QssxsodyamEwCW/POuZ6lcg5Ktz885hZo+L7tdEy8W9ViH0Pd");

        x509Data.appendChild(x509Certificate);
        x509Data.appendChild(x509Certificate2);
        x509Data.appendChild(x509Certificate3);
        keyInfo.appendChild(x509Data);
        return keyInfo;


    }


    public String toXML() throws SerializationException {
        Element keyInfo = serialize();
        return keyInfo.toXML();
    }

    public Element serialize() throws SerializationException {
        return getKeyInfo();
    }
}
