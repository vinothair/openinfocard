
const CONTRACT_ID = "@xmldap.org/identityselector;1";
const SELECTOR_CLASS_NAME = "OpeninfocardSelector";

function debugObject(prefix, object, indent) {
	var msg = "";
	var count = 0;
	//if (indent > 3) return;
	var pre = "";
	for (var j=0; j<indent; j++) { pre += '\t'; }
	for (var i in object) {
		if (object.hasOwnProperty(i)) {
	
			var value = object[i];
			if (typeof(value) == 'object') {
				//debugObject(prefix, value, indent+1);
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

var OpeninfocardSecurityStateChangeListener =
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
    		IdentitySelectorDiag.logMessage( "OpeninfocardSecurityStateChangeListener", "onSecurityChange aState=" + aState);
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
	        		IdentitySelectorDiag.logMessage( "OpeninfocardSecurityStateChangeListener", "doc.__identityselector__ == undefined; " + doc.location.href);
	            	return;
	            }
	            if (aState & wpl.STATE_IDENTITY_EV_TOPLEVEL) {
	            	doc.__identityselector__.sslMode = "EV";
	            } else if (aState & wpl.STATE_SECURE_HIGH) {
	            	doc.__identityselector__.sslMode = "SSL";
	            } else {
	            	doc.__identityselector__.sslMode = "low";
	            }
        		IdentitySelectorDiag.logMessage( "OpeninfocardSecurityStateChangeListener", "doc.location.href=" + doc.location.href + " doc.__identityselector__.sslMode=" + doc.__identityselector__.sslMode);
	        } catch (e) {
        		IdentitySelectorDiag.reportError( "OpeninfocardSecurityStateChangeListener", "doc.location.href=" + doc.location.href + " exception=" + e);
	        }
        }
};

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
		if (this.disabled === true) {
			IdentitySelectorDiag.logMessage(SELECTOR_CLASS_NAME+".getSecurityToken",
					" Id selector is disabled. Exiting");
			return null;
		}
		
//		var doc = document;
//		try {
//			doc = doc.getElementById( "content");
////			doc = doc.contentDocument;
//		} catch (e) {
//			IdentitySelectorDiag.logMessage(SELECTOR_CLASS_NAME+".getSecurityToken",
//			" doc.contentDocument threw exception: " + e);
//		}
//		if( doc.wrappedJSObject)
//        {
//                doc = doc.wrappedJSObject;
//        }
		
		IdentitySelectorDiag.logMessage(SELECTOR_CLASS_NAME+".getSecurityToken",
				"Identity selector invoked.");

		try {
			// Launch the card selector
			var sslCert = OpeninfocardSelector.getSSLCertFromDocument(document);
			var cid = CONTRACT_ID;
			// class id of selector
			var obj = null;
			try {
				var cidClass = Components.classes[cid];
				if (cidClass !== undefined) {
					obj = cidClass.createInstance();
					obj = obj
							.QueryInterface(Components.interfaces.IIdentitySelector);
				} else {
					IdentitySelectorDiag.reportError(SELECTOR_CLASS_NAME+".getSecurityToken",
							"the class " + cid + " is not installed");
					return;
				}
			} catch (e) {
				IdentitySelectorDiag.throwError("OpeninfocardSelector.getSecurityToken:", e);
			}
//				IdentitySelectorDiag.logMessage(SELECTOR_CLASS_NAME+".getSecurityToken",
//						"ssl security mode=" + IdentitySelector.getMode(doc));
			
			var extraParams = function(){
				var extraParams = [];
				var len = 0;
				for (var i in data) {
					if (("issuer" !== ""+i) 
						&& ("recipient" !== ""+i)
						&& ("requiredClaims" !== ""+i)
						&& ("optionalClaims" !== ""+i)
						&& ("tokenType" !== ""+i)
						&& ("privacyUrl" !== ""+i)
						&& ("privacyVersion" !== ""+i)
						&& ("issuerPolicy" !== ""+i)) 
					{
						var obj = {};
						obj[i] = data[i];
						len = extraParams.length;
						extraParams[len] = JSON.stringify(obj);
						IdentitySelectorDiag.logMessage(SELECTOR_CLASS_NAME+".getSecurityToken",
								"extraParams[" + len + "] = " + extraParams[len]);
					} else {
						IdentitySelectorDiag.logMessage(SELECTOR_CLASS_NAME+".getSecurityToken",
								"i=" + i + "; value=" + data[i] + ";");
					}
				}
				return extraParams;
			}();
			
//			if (doc.__identityselector__ != undefined && doc.__identityselector__.sslMode != undefined) {
//				extraParams[extraParams.length] = "{" + "sslMode" + "," + doc.__identityselector__.sslMode + "}";
//			} else {
//				if (doc.__identityselector__ == undefined) {
//					IdentitySelectorDiag.logMessage(SELECTOR_CLASS_NAME+".getSecurityToken",
//							"doc.__identityselector__ == undefined doc.location.href=" + doc.location.href);
//				} else if (doc.__identityselector__.sslMode == undefined ) {
//					IdentitySelectorDiag.logMessage(SELECTOR_CLASS_NAME+".getSecurityToken",
//							"doc.__identityselector__.sslMode == undefined doc.location.href=" + doc.location.href);
//				} else {
//					IdentitySelectorDiag.logMessage(SELECTOR_CLASS_NAME+".getSecurityToken",
//							"??? doc.location.href=" + doc.location.href);
//				}
//			}
			
			IdentitySelectorDiag.logMessage(SELECTOR_CLASS_NAME+".getSecurityToken",
					"extraParams.length="+extraParams.length);
			
			debugObject("OpeninfocardSelector", data, 0);
			/* Make the call to the selector */
			token = obj.GetBrowserToken(data.issuer,
					data.recipient, data.requiredClaims,
					data.optionalClaims, data.tokenType, data.privacyUrl,
					data.privacyVersion, sslCert, data.issuerPolicy,
					extraParams.length, extraParams);
		} catch (e1) {
			IdentitySelectorDiag.throwError(SELECTOR_CLASS_NAME+".getSecurityToken", e1);
		}
		return token;
	}

	, getSSLCertFromDocument : function(doc) {
		   var sslCert = null;
		   var browser = doc.getElementById( "content");
		   var secureUi = browser.securityUI;
		   var sslStatusProvider = null;
		   sslStatusProvider = secureUi.QueryInterface(Components.interfaces.nsISSLStatusProvider);
		   if( sslStatusProvider !== null) {
		      try {
		         var sslStatus = sslStatusProvider.SSLStatus.QueryInterface(Components.interfaces.nsISSLStatus);
		         if( sslStatus !== null && sslStatus.serverCert !== undefined) {
		            sslCert = sslStatus.serverCert;
		         }
		      }
		      catch( e) {
		         IdentitySelectorDiag.logMessage("getSSLCertFromDocument: " + e);
		      }
		   }
		   return sslCert;
	}
	
	, onLoad : function(event) {
		IdentitySelectorDiag.logMessage( "OpeninfocardSelector", "onLoad: " + document.location.href);
		gBrowser.removeEventListener( "load", OpeninfocardSelector.onLoad, false);
        window.getBrowser().addProgressListener( OpeninfocardSecurityStateChangeListener,
                Components.interfaces.nsIWebProgress.NOTIFY_STATE_ALL &
                Components.interfaces.nsIWebProgress.NOTIFY_SECURITY);

	}
	
	, onUnload : function(event) {
		try {
			IdentitySelectorDiag.logMessage( "OpeninfocardSelector", "onUnload: " + document.location.href);
			gBrowser.removeEventListener( "load", OpeninfocardSelector.onLoad, false);
		    window.removeEventListener( "load", function(event){gBrowser.addEventListener("load", OpeninfocardSelector.onLoad(event), true);}, false);
	               
	        window.removeEventListener( "unload", OpeninfocardSelector.onUnload, false);
	        window.getBrowser().removeProgressListener( OpeninfocardSecurityStateChangeListener );
		} catch (e) {}
	}
};

try
{
	IdentitySelectorDiag.logMessage( "OpeninfocardSelector", "start");
    window.addEventListener( "load", function(event){gBrowser.addEventListener("load", OpeninfocardSelector.onLoad, true);}, false);
           
    window.addEventListener( "unload",
    		OpeninfocardSelector.onUnload, false);
}
catch( e)
{
        IdentitySelectorDiag.reportError( "window.addEventListener", e);
}