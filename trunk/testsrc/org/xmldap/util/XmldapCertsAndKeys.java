/*
 * XmldapCertsAndKeys.java
 *
 * Created on 6. September 2006, 11:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.xmldap.util;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;

/**
 * 
 * @author Axel Nennker
 */
public class XmldapCertsAndKeys {

	/** Creates a new instance of XmldapCertsAndKeys */
	private XmldapCertsAndKeys() {
	}

	public static X509Certificate getXmldapCert() throws CertificateException {
		String certB64 = "MIIDXTCCAkUCBEQd+4EwDQYJKoZIhvcNAQEEBQAwczELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNh"
				+ "bGlmb3JuaWExFjAUBgNVBAcTDVNhbiBGcmFuY2lzY28xDzANBgNVBAoTBnhtbGRhcDERMA8GA1UE"
				+ "CxMIaW5mb2NhcmQxEzARBgNVBAMTCnhtbGRhcC5vcmcwHhcNMDYwMzIwMDA0NjU3WhcNMDYwNjE4"
				+ "MDA0NjU3WjBzMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2Fu"
				+ "IEZyYW5jaXNjbzEPMA0GA1UEChMGeG1sZGFwMREwDwYDVQQLEwhpbmZvY2FyZDETMBEGA1UEAxMK"
				+ "eG1sZGFwLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANMnkVA4xfpG0bLos9FO"
				+ "pNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEIbiT+IbYPZF1vCcBrcFa8"
				+ "Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+MrKV1SGTqaIJ2l+7Rd05WH"
				+ "scwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7ZlJAUndGJ/kfZw8sKuL9Q"
				+ "Srh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYFgm9J+vYm4tzRequLy/nj"
				+ "teRIkcfAdcAtt6PCYjUCAwEAATANBgkqhkiG9w0BAQQFAAOCAQEAURtxiA7qDSq/WlUpWpfWiZ7H"
				+ "vveQrwTaTwV/Fk3l/I9e9WIRN51uFLuiLtZMMwR02BX7Yva1KQ/Gl999cm/0b5hptJ+TU29rVPZI"
				+ "lI32c5vjcuSVoEda8+BRj547jlC0rNokyWm+YtBcDOwfHSPFFwVPPVxyQsVEebsiB6KazFq6iZ8A"
				+ "0F2HLEnpsdFnGrSwBBbH3I3PH65ofrTTgj1Mjk5kA6EVaeefDCtlkX2ogIFMlcS6ruihX2mlCLUS"
				+ "rlPs9TH+M4j/R/LV5QWJ93/X9gsxFrxVFGg3b75EKQP8MZ111/jaeKd80mUOAiTO06EtfjXZPrjP"
				+ "N4e2l05i2EGDUA==";
		byte[] certBytes = Base64.decode(certB64);
		CertificateFactory cf = CertificateFactory.getInstance("X509");
		ByteArrayInputStream inStream = new ByteArrayInputStream(certBytes);
		return (X509Certificate) cf.generateCertificate(inStream);
	}

	public static RSAPrivateKey getXmldapPrivateKey()
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		String exponentB64 = "AKh/FZVHiKxcIPA8g2mN8TUdMXuX58I7z4jS+57vYta387MG3DGZtQ/XXfHdPx9WjdoW0KWE2Pl5"
				+ "SbOZW7tVcwigF88FrSJ5i6XDwUktmXjFwJM/TvUZlxWAKUdoOX8MC3DrAYZxeT3kC1mzAiBMPdC4"
				+ "W4zNe7Zo0YgbsMzQZowVxZTP4GWa/L8o3adXTvdobP1nKW5buPj9vkgaGCTxE0vQzbuiGj1HRJe9"
				+ "MRtvcU/I2shiIVE0F35wk8gw0FATtkvMpTpR12YVeo1JGZsHFQoD7gTD/n/NmC9Rjk2baYGj97hV"
				+ "9EpDRcPNsMll2pVRy4Z45j2+t/yl8WjaqK5lhkE=";
		String modulusB64 = "ANMnkVA4xfpG0bLos9FOpNBjHAdFahy2cJ7FUwuXd/IShnG+5qF/z1SdPWzRxTtpFFyodtXlBUEI"
				+ "biT+IbYPZF1vCcBrcFa8Kz/4rBjrpPZgllgA/WSVKjnJvw8q4/tO6CQZSlRlj/ebNK9VyT1kN+Mr"
				+ "KV1SGTqaIJ2l+7Rd05WHscwZMPdVWBbRrg76YTfy6H/NlQIArNLZanPvE0Vd5QfD4ZyG2hTh3y7Z"
				+ "lJAUndGJ/kfZw8sKuL9QSrh4eOTc280NQUmPGz6LP5MXNmu0RxEcomod1+ToKll90yEKFAUKuPYF"
				+ "gm9J+vYm4tzRequLy/njteRIkcfAdcAtt6PCYjU=";
		byte[] exponentBytes = Base64.decode(exponentB64);
		byte[] modulusBytes = Base64.decode(modulusB64);
		BigInteger exponent = new BigInteger(1, exponentBytes);
		BigInteger modulus = new BigInteger(1, modulusBytes);
		RSAPrivateKeySpec ks = new RSAPrivateKeySpec(modulus, exponent);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return (RSAPrivateKey) kf.generatePrivate(ks);
	}


}
