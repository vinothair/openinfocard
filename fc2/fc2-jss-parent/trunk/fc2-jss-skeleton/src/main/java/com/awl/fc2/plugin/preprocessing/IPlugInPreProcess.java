package com.awl.fc2.plugin.preprocessing;

import java.util.Vector;

import com.awl.fc2.plugin.IJSSPlugin;

public interface IPlugInPreProcess extends IJSSPlugin {
	boolean process(Vector<String> lstRequiredClaims,
					  Vector<String> lstOptionalClaims,
					  String urlRequestor,
					  String certifRequestorB64);
	boolean isRequestModified();
	
	public Vector<String> getRequiredClaims();
	public Vector<String> getOptionalClaims();
	public String getURLRequestor();
	public String getCertificateB64();
	
}
