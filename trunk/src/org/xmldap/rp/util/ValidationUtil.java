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

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.XPathContext;
import org.xmldap.util.XSDDateTime;
import org.xmldap.util.XmlFileUtil;
import org.xmldap.ws.WSConstants;
import org.xmldap.xmldsig.EnvelopedSignature;

import java.io.ByteArrayInputStream;
import java.security.cert.*;
import java.text.DateFormat;
import java.util.Calendar;

public class ValidationUtil {

	/**
	 * 
	 * @param assertion
	 * 
	 * @return String (NotBefore<now) && (now<NotOnOrAfter) if the notbefore
	 *         and the notOnOrAfter dates fit to the current date or !(NotBefore<now) &&
	 *         (now<NotOnOrAfter) or (NotBefore<now) && !(now<NotOnOrAfter)
	 * 
	 * 
	 */
	public static String validateConditions(Document assertion) {
		// <saml:Conditions NotBefore="2006-09-27T13:26:59Z"
		// NotOnOrAfter="2006-09-27T13:46:59Z" />
		XPathContext thisContext = new XPathContext();
		thisContext.addNamespace("saml", WSConstants.SAML11_NAMESPACE);
		Nodes nodes = assertion.query("//saml:Conditions", thisContext);
		Element element = (Element) nodes.get(0);
		String notBefore = (String) element.getAttribute("NotBefore")
				.getValue();
		String notOnOrAfter = (String) element.getAttribute("NotOnOrAfter")
				.getValue();
		Calendar now = XSDDateTime.parse(new XSDDateTime().getDateTime());
		Calendar nb = XSDDateTime.parse(notBefore);
		Calendar na = XSDDateTime.parse(notOnOrAfter);
		// TODO take care of "on"
		StringBuffer res = null;
		DateFormat df = DateFormat.getTimeInstance();
		if (now.after(nb)) {
			res = new StringBuffer("(" + df.format(nb.getTime()) + "<"
					+ df.format(now.getTime()) + ")");
		} else {
			res = new StringBuffer("!(" + df.format(nb.getTime()) + "<"
					+ df.format(now.getTime()) + ")");
		}
		if (now.before(na)) {
			res = res.append(" && (" + df.format(now.getTime()) + "<"
					+ df.format(na.getTime()) + ")");
		} else {
			res = res.append(" && !(" + df.format(now.getTime()) + "<"
					+ df.format(na.getTime()) + ")");
		}
		return res.toString();
	}

	public static X509Certificate getCertificate(Document assertion)
			throws CertificateException {
		XPathContext thisContext = new XPathContext();
		thisContext.addNamespace("saml", WSConstants.SAML11_NAMESPACE);
		thisContext.addNamespace("dsig", WSConstants.DSIG_NAMESPACE);
		Nodes nodes = assertion.query("//dsig:X509Data/dsig:X509Certificate",
				thisContext);
		if ((nodes != null) && (nodes.size() > 0)) {
			String element = nodes.get(0).getValue();
			StringBuffer sb = new StringBuffer("-----BEGIN CERTIFICATE-----\n");
			sb.append(element);
			sb.append("\n-----END CERTIFICATE-----\n");

			ByteArrayInputStream bis = new ByteArrayInputStream(sb.toString()
					.getBytes());
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			X509Certificate cert = (X509Certificate) cf
					.generateCertificate(bis);
			return cert;
		} else {
			return null;
		}
	}

	public static String validateCertificate(Document assertion)
			throws CertificateException {
		try {
			X509Certificate cert = getCertificate(assertion);
			if (cert != null) {
				cert.checkValidity();
				return "is valid";
			} else {
				return "is missing"; // if it is not there then it is invalid
			}
		} catch (CertificateExpiredException e) {
			return "has expired";
		} catch (CertificateNotYetValidException e) {
			return "is not yet valid";
		}
	}



	/**
	 * test ValidationUtil by validating a digest and signature in a SAML
	 * assertion contained in a file.
	 * 
	 * @param args
	 *            an array of Strings, in which arg[0] is a filename of an input
	 *            file
	 */
	public static void main(String[] args) throws Exception {
		String fn = args[0];
		Document assertion = XmlFileUtil.readXmlFile(fn);

		boolean verified = EnvelopedSignature.validate(assertion);
		if (!verified) {
			System.err.println("FAIL");
			System.exit(1);
		}
		System.exit(0);
	}
}
