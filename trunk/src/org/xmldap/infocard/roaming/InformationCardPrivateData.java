package org.xmldap.infocard.roaming;

import nu.xom.Element;

public abstract class InformationCardPrivateData {
  String masterKey = null;
  
	abstract Element serialize();
	public String getMasterKey() { return masterKey; }
}
