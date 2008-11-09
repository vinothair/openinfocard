
const CONTRACT_ID = "@xmldap.org/identityselector;1";
const SELECTOR_CLASS_NAME = "OpeninfocardSelector";

function debugObject(prefix, object, indent) {
	var msg = "";
	var count = 0;
	//if (indent > 3) return;
	var pre = "";
	for (var j=0; j<indent; j++) { pre += '\t'; }
	for (var i in object) {
		var value = object[i];
		if (typeof(value) == 'object') {
			//debugObject(prefix, value, indent+1);
			msg += pre + i + ' type=' + typeof(value) + ':' + value + '\n';
//			debug(prefix + pre + i + ' type=' + typeof(value) + ':' + value);
		} else if ((typeof(value) == 'string') || ((typeof(value) == 'boolean')) || ((typeof(value) == 'number'))) {
			msg += pre + ':' + i + '=' + value + '\n';
//			debug(prefix + pre + ':' + i + '=' + value);
		} else {
			msg += pre + i + ' type=' + typeof(value) + '\n'
//			debug(prefix + pre + i + ' type=' + typeof(value));
		}
	}
	IdentitySelector.logMessage(msg);
}

// **************************************************************************
// Desc:
// **************************************************************************

var OpeninfocardSelector = {
	// ***********************************************************************
	// Method: getSecurityToken
	// ***********************************************************************

	getSecurityToken : function(data) {
		var token = null;
		
		this.disabled = gPrefService.getBoolPref("identityselector.disabled");
		if (this.disabled == true) {
			IdentitySelector.logMessage(SELECTOR_CLASS_NAME+".getSecurityToken",
					" Id selector is disabled. Exiting");
			return null;
		}
		
		var doc = document;
		
		IdentitySelector.logMessage(SELECTOR_CLASS_NAME+".getSecurityToken",
				"Identity selector invoked.");

		try {
			// Launch the card selector
			var sslCert = OpeninfocardSelector.getSSLCertFromDocument(document);
			var cid = CONTRACT_ID;
			// class id of selector
			{
				var obj = null;
				try {
					var cidClass = Components.classes[cid];
					if (cidClass != undefined) {
						obj = cidClass.createInstance();
						obj = obj
								.QueryInterface(Components.interfaces.IIdentitySelector);
					} else {
						IdentitySelector.reportError(SELECTOR_CLASS_NAME+".getSecurityToken",
								"the class " + cid + " is not installed");
						return;
					}
				} catch (e) {
					IdentitySelector.throwError("OpeninfocardSelector.getSecurityToken:", e);
				}
//				IdentitySelector.logMessage(SELECTOR_CLASS_NAME+".getSecurityToken",
//						"ssl security mode=" + IdentitySelector.getMode(doc));
				
				var extraParams = {};
				for (var i in data) {
					extraParams[i] = data[i];
				}
				
				if (extraParams.issuer !== undefined) {
					delete extraParams.issuer;
				}
				if (extraParams.recipient !== undefined) {
					delete extraParams.recipient;
				}
				if (extraParams.requiredClaims !== undefined) {
					delete extraParams.requiredClaims;
				}
				if (extraParams.optionalClaims !== undefined) {
					delete extraParams.optionalClaims;
				}
				if (extraParams.tokenType !== undefined) {
					delete extraParams.tokenType;
				}
				if (extraParams.privacyUrl !== undefined) {
					delete extraParams.privacyUrl;
				}
				if (extraParams.privacyVersion !== undefined) {
					delete extraParams.privacyVersion;
				}
				if (extraParams.issuerPolicy !== undefined) {
					delete extraParams.issuerPolicy;
				}
				debugObject("OpeninfocardSelector", data, 0);
				/* Make the call to the selector */
				token = obj.GetBrowserToken(data.issuer,
						data.recipient, data.requiredClaims,
						data.optionalClaims, data.tokenType, data.privacyUrl,
						data.privacyVersion, sslCert, data.issuerPolicy,
						extraParams);
			}
		} catch (e) {
			IdentitySelector.throwError(SELECTOR_CLASS_NAME+".getSecurityToken", e);
		}
		return token;
	}

	, getSSLCertFromDocument : function(doc) {
		   var sslCert = null;
		   var browser = doc.getElementById( "content");
		   var secureUi = browser.securityUI;
		   var sslStatusProvider = null;
		   sslStatusProvider = secureUi.QueryInterface(Components.interfaces.nsISSLStatusProvider);
		   if( sslStatusProvider != null) {
		      try {
		         sslStatus = sslStatusProvider.SSLStatus.QueryInterface(Components.interfaces.nsISSLStatus);
		         if( sslStatus != null && sslStatus.serverCert != undefined) {
		            sslCert = sslStatus.serverCert;
		         }
		      }
		      catch( e) {
		         IdentitySelector.logMessage("getSSLCertFromDocument: " + e);
		      }
		   }
		   return sslCert;
	}
};
