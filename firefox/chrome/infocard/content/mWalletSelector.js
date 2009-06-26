
function mwDebugObject(prefix, object, indent) {
	var msg = "";
	var count = 0;
	//if (indent > 3) return;
	var pre = "";
	for (var j=0; j<indent; j++) { pre += '\t'; }
	for (var i in object) {
		if (object.hasOwnProperty(i)) {
	
			var value = object[i];
			if (typeof(value) == 'object') {
				//mwDebugObject(prefix, value, indent+1);
				msg += pre + i + ' type=' + typeof(value) + ':' + value + '\n';
	//			debug(prefix + pre + i + ' type=' + typeof(value) + ':' + value);
			} else if ((typeof(value) == 'string') || ((typeof(value) == 'boolean')) || ((typeof(value) == 'number'))) {
				msg += pre + ':' + i + '=' + value + '\n';
	//			debug(prefix + pre + ':' + i + '=' + value);
			} else {
				msg += pre + i + ' type=' + typeof(value) + '\n';
	//			debug(prefix + pre + i + ' type=' + typeof(value));
			}
		}
	}
	IdentitySelectorDiag.logMessage(msg);
}

//**************************************************************************
//Desc:
//**************************************************************************

var mWalletSecurityStateChangeListener =
{
        QueryInterface : function( aIID)
        {
                if( aIID.equals( Components.interfaces.nsIWebProgressListener) ||
                         aIID.equals( Components.interfaces.nsISupportsWeakReference) ||
                         aIID.equals( Components.interfaces.nsISupports))
                {
                        return( this);
                }
               
                throw Components.results.NS_NOINTERFACE;
        },

        onProgressChange : function() { return( 0); },
       
        onStatusChange : function() { return( 0); },
       
        onLocationChange: function(aProgress, aRequest, aURI) {},
        
        onStateChange: function(aWebProgress, aRequest, aFlag, aStatus) {},

        onSecurityChange : function(aWebProgress, aRequest, aState)
        {
//    		IdentitySelectorDiag.logMessage( "mWalletSecurityStateChangeListener", "onSecurityChange aState=" + aState);
        	try {
	        	const wpl = Components.interfaces.nsIWebProgressListener;
	        	var doc = aWebProgress.DOMWindow.document;
	        	IdentitySelector.runInterceptScript(doc);
	        	if( doc.wrappedJSObject)
                {
                        doc = doc.wrappedJSObject;
                }
	            if( doc.__identityselector__ === undefined)
	            {
	        		IdentitySelectorDiag.logMessage( "mWalletSecurityStateChangeListener", "doc.__identityselector__ == undefined; " + doc.location.href);
	            	return;
	            }
	            if (aState & wpl.STATE_IDENTITY_EV_TOPLEVEL) {
	            	doc.__identityselector__.sslMode = "EV";
	            } else if (aState & wpl.STATE_SECURE_HIGH) {
	            	doc.__identityselector__.sslMode = "SSL";
	            } else {
	            	doc.__identityselector__.sslMode = "low";
	            }
        		//IdentitySelectorDiag.logMessage( "mWalletSecurityStateChangeListener", "doc.location.href=" + doc.location.href + " doc.__identityselector__.sslMode=" + doc.__identityselector__.sslMode);
	        } catch (e) {
        		IdentitySelectorDiag.reportError( "mWalletSecurityStateChangeListener", "doc.location.href=" + doc.location.href + " exception=" + e);
	        }
        }
};

// **************************************************************************
// Desc:
// **************************************************************************

var mWalletSelector = {
	CONTRACT_ID : "@laboratories/identityselector;1",
	SELECTOR_CLASS_NAME : "mWalletSelector",

	prefListenerCallback : function(branch, data) {
		var value;
		switch (data)
		{
		case ".selector_guid": 
		case "selector_guid": 
			value = IdentitySelectorPrefs.getStringPref("identityselector", "selector_guid");
			IdentitySelectorDiag.logMessage( "mWalletSelector.prefListenerCallback", "was called for: " + branch + "::" + data + " new value=" + value);
			break;
		case ".contractid":
		case "contractid":
			value = IdentitySelectorPrefs.getStringPref("identityselector", "contractid");
			IdentitySelectorDiag.logMessage( "mWalletSelector.prefListenerCallback", "was called for: " + branch + "::" + data + " new value=" + value);
			if (mWalletSelector.CONTRACT_ID === value) {
				IdentitySelectorPrefs.setStringPref("identityselector", "selector_guid", mWalletSelector.CONTRACT_ID);
			} else {
				IdentitySelectorDiag.logMessage( "mWalletSelector.prefListenerCallback", "contractid: " + mWalletSelector.CONTRACT_ID + "!==" + value);
			}
			break;
		case "selector_class":
		case ".selector_class":
			value = IdentitySelectorPrefs.getStringPref("identityselector", "selector_class");
			IdentitySelectorDiag.logMessage( "mWalletSelector.prefListenerCallback", "was called for: " + branch + "::" + data + " new value=" + value);
			if (mWalletSelector.SELECTOR_CLASS_NAME === value) {
				IdentitySelectorPrefs.setStringPref("identityselector", "selector_guid", mWalletSelector.CONTRACT_ID);
			} else {
				IdentitySelectorDiag.logMessage( "mWalletSelector.prefListenerCallback", "contractid: " + mWalletSelector.CONTRACT_ID + "!==" + value);
			}
			break;
		default:
			IdentitySelectorDiag.logMessage( "mWalletSelector.prefListenerCallback", "WAS called for: " + branch + "::" + data);			
		break;
		}
	},

	guid : function() {
		return mWalletSelector.CONTRACT_ID;
	},
	
	// ***********************************************************************
	// Method: getSecurityToken
	// ***********************************************************************

	getSecurityToken : function(data, doc) {
		var token = null;
		
		this.disabled = IdentitySelectorPrefs.getBooleanPref("identityselector", "disabled");
		if (this.disabled === true) {
			IdentitySelectorDiag.logMessage(mWalletSelector.SELECTOR_CLASS_NAME+".getSecurityToken",
					" Id selector is disabled. Exiting");
			return null;
		}
		
//		var doc = document;
//		try {
//			doc = doc.getElementById( "content");
////			doc = doc.contentDocument;
//		} catch (e) {
//			IdentitySelectorDiag.logMessage(mWalletSelector.SELECTOR_CLASS_NAME+".getSecurityToken",
//			" doc.contentDocument threw exception: " + e);
//		}
//		if( doc.wrappedJSObject)
//        {
//                doc = doc.wrappedJSObject;
//        }
		
		IdentitySelectorDiag.logMessage(mWalletSelector.SELECTOR_CLASS_NAME+".getSecurityToken",
				"Identity selector invoked.");

		try {
			// Launch the card selector
			var sslCert = InformationCardHelper.getSSLCertFromDocument(doc);
			var cid = mWalletSelector.CONTRACT_ID;
			// class id of selector
			var obj = null;
			try {
				var cidClass = Components.classes[cid];
				if (cidClass !== undefined) {
					obj = cidClass.createInstance();
					obj = obj
							.QueryInterface(Components.interfaces.IIdentitySelector);
				} else {
					IdentitySelectorDiag.reportError(mWalletSelector.SELECTOR_CLASS_NAME+".getSecurityToken",
							"the class " + cid + " is not installed");
					return;
				}
			} catch (e) {
				IdentitySelectorDiag.throwError("mWalletSelector.getSecurityToken:", e);
			}
//				IdentitySelectorDiag.logMessage(mWalletSelector.SELECTOR_CLASS_NAME+".getSecurityToken",
//						"ssl security mode=" + IdentitySelector.getMode(doc));
			
			var extraParams = function(){
				var extraParams = [];
				var len = 0;
				for (var i in data) {
					if (("issuer" !== ""+i) && ("recipient" !== ""+i) && ("requiredClaims" !== ""+i) && ("optionalClaims" !== ""+i) && ("tokenType" !== ""+i) && ("privacyUrl" !== ""+i) && ("privacyVersion" !== ""+i) && ("issuerPolicy" !== ""+i)) 
					{
						var obj = {};
						obj[i] = data[i];
						len = extraParams.length;
						extraParams[len] = JSON.stringify(obj);
						IdentitySelectorDiag.logMessage(mWalletSelector.SELECTOR_CLASS_NAME+".getSecurityToken",
								"extraParams[" + len + "] = " + extraParams[len]);
					} else {
						IdentitySelectorDiag.logMessage(mWalletSelector.SELECTOR_CLASS_NAME+".getSecurityToken",
								"i=" + i + "; value=" + data[i] + ";");
					}
				}
				return extraParams;
			}();
			
//			if (doc.__identityselector__ != undefined && doc.__identityselector__.sslMode != undefined) {
//				extraParams[extraParams.length] = "{" + "sslMode" + "," + doc.__identityselector__.sslMode + "}";
//			} else {
//				if (doc.__identityselector__ == undefined) {
//					IdentitySelectorDiag.logMessage(mWalletSelector.SELECTOR_CLASS_NAME+".getSecurityToken",
//							"doc.__identityselector__ == undefined doc.location.href=" + doc.location.href);
//				} else if (doc.__identityselector__.sslMode == undefined ) {
//					IdentitySelectorDiag.logMessage(mWalletSelector.SELECTOR_CLASS_NAME+".getSecurityToken",
//							"doc.__identityselector__.sslMode == undefined doc.location.href=" + doc.location.href);
//				} else {
//					IdentitySelectorDiag.logMessage(mWalletSelector.SELECTOR_CLASS_NAME+".getSecurityToken",
//							"??? doc.location.href=" + doc.location.href);
//				}
//			}
			
			IdentitySelectorDiag.logMessage(mWalletSelector.SELECTOR_CLASS_NAME+".getSecurityToken",
					"extraParams.length="+extraParams.length);
			
			mwDebugObject("mWalletSelector", data, 0);
			/* Make the call to the selector */
			token = obj.GetBrowserToken(data.issuer,
					data.recipient, data.requiredClaims,
					data.optionalClaims, data.tokenType, data.privacyUrl,
					data.privacyVersion, sslCert, data.issuerPolicy,
					extraParams.length, extraParams);
		} catch (e1) {
			IdentitySelectorDiag.throwError(mWalletSelector.SELECTOR_CLASS_NAME+".getSecurityToken", e1);
		}
		return token;
	},

	onLoad : function(event) {
		try {
			IdentitySelectorDiag.logMessage( "mWalletSelector", "onLoad: " + document.location.href);
			gBrowser.removeEventListener( "load", mWalletSelector.onLoad, false);
	        window.getBrowser().addProgressListener( mWalletSecurityStateChangeListener,
	                Components.interfaces.nsIWebProgress.NOTIFY_STATE_ALL &
	                Components.interfaces.nsIWebProgress.NOTIFY_SECURITY);
		} catch (e) {
			Components.utils.reportError("mWalletSelector::onLoad Exception=" +  e);
		}
	},
	
	onUnload : function(event) {
		try {
			IdentitySelectorDiag.logMessage( "mWalletSelector", "onUnload: " + document.location.href);
			gBrowser.removeEventListener( "load", mWalletSelector.onLoad, false);
		    window.removeEventListener( "load", function(event){gBrowser.addEventListener("load", mWalletSelector.onLoad(event), true);}, false);
	               
	        window.removeEventListener( "unload", mWalletSelector.onUnload, false);
	        window.getBrowser().removeProgressListener( mWalletSecurityStateChangeListener );
	        IdentitySelector.deregisterSelector(mWalletSelector);
		} catch (e) {}
	}
};

try
{
	IdentitySelectorDiag.logMessage( "mWalletSelector", "start");
    IdentitySelector.registerSelector(mWalletSelector);

    try {
    	mWalletSelector._identityselectorPrefListener = 
    		new IdentitySelectorPrefListener("extensions.identityselector", mWalletSelector.prefListenerCallback);
    	mWalletSelector._identityselectorPrefListener.register();
	} catch (e) {
		IdentitySelectorDiag.reportError( "OpeninfocardSelector IdentitySelectorPrefListener", e);
	}

    window.addEventListener( "load", function(event){gBrowser.addEventListener("load", mWalletSelector.onLoad, true);}, false);
    window.addEventListener( "unload",
    		mWalletSelector.onUnload, false);
}
catch( e)
{
        IdentitySelectorDiag.reportError( "window.addEventListener", e);
}