
var InformationCardHelper = {
	isSslCertEV : function(doc) {
		var browser = doc.defaultView.getBrowser();
		if (browser !== null) {
			if (browser.securityUI !== undefined) {
			   var secureUi = browser.securityUI;
			   return (secureUi.state & Components.interfaces.nsIWebProgressListener.STATE_IDENTITY_EV_TOPLEVEL);
			}
		}
		return false;
	}
	, getSSLCertFromDocument : function(doc) {
	   var sslCert = null;
	   var browser = null;
	   var aContent = document.getElementById( "content");
	   if (aContent !== null) {
		   browser = aContent.mCurrentBrowser;
	   }
	   
	   if (browser === null) {
		   var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"]
		                               .getService(Components.interfaces.nsIWindowMediator);
		   var enumerator = wm.getEnumerator(null);
		   while(enumerator.hasMoreElements()) {
			  var win = enumerator.getNext();
			  var winContent = win.document.getElementById( "content");
			  if (winContent !== null) {
				  var curBrowser = winContent.mCurrentBrowser;
				  var contentDocument = curBrowser.contentDocument;
				  if (contentDocument.location.href === doc.location.href) {
					  browser = curBrowser;
				  }
			  }
		   }
	   }

	   if (browser !== null) {
		   if (browser.securityUI !== undefined) {
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
		   } else {
			   IdentitySelectorDiag.logMessage("InformationCardHelper.getSSLCertFromDocument", "browser.securityUI === undefined");
			   IdentitySelectorDiag.logMessage("InformationCardHelper.getSSLCertFromDocument", "doc.location.href="+doc.location.href);
		   }
	   } else {
		   IdentitySelectorDiag.reportError("InformationCardHelper.getSSLCertFromDocument", "content not found");
	   }
	   return sslCert;
	}

    , parseRpPolicy: function(icLoginPolicy) {
        // IdentitySelectorDiag.logMessage("parseRpPolicy",
        // "typeof(icLoginPolicy)=" +
        // typeof(icLoginPolicy)); // xml
        IdentitySelectorDiag.logMessage("parseRpPolicy", "icLoginPolicy=" + icLoginPolicy.toString());
        var data = new Object();
        // if( icLoginPolicy.wrappedJSObject)
        // {
        // IdentitySelectorDiag.logMessage("parseRpPolicy", "icLoginPolicy =" +
        // icLoginPolicy.wrappedJSObject.toString());
        // icLoginPolicy = icLoginPolicy.wrappedJSObject;
        // }
        // var xmlPolicy = new XML(icLoginPolicy.toString());
        // IdentitySelectorDiag.logMessage("parseRpPolicy",
        // "xmlPolicy.child(1).@name=" +
        // xmlPolicy.child(1).@name.toString());
        var params = icLoginPolicy.toString().split("<param");
        IdentitySelectorDiag.logMessage("parseRpPolicy", "params.length=" + params.length);
        for (var i = 0; i < params.length; i++) {
            var name = null;
            var value = null;
            var param = params[i];
            IdentitySelectorDiag.logMessage("parseRpPolicy", "param i=" + i + " param=" + param);
            var j = param.indexOf('name="');
            if (j != -1) {
                var s1 = param.substring(j + 6);
                j = s1.indexOf('"');
                if (j != -1) {
                    name = s1.substring(0, j);
                    s1 = param.substring(j);
                    j = s1.indexOf("value=\"");
                    if (j != -1) {
                        s1 = s1.substring(j + 7);
                        j = s1.indexOf('"');
                        if (j != -1) {
                            value = s1.substring(0, j);
                        }
                        else {
                            IdentitySelectorDiag.logMessage("parseRpPolicy", "no closing \" in value= in " + param);
                        }
                    }
                    else {
                        IdentitySelectorDiag.logMessage("parseRpPolicy", "no value=\" in " + param);
                    }
                }
                else {
                    IdentitySelectorDiag.logMessage("parseRpPolicy", "no closing \" in name= in " + param);
                }
            }
            else {
                IdentitySelectorDiag.logMessage("parseRpPolicy", "no name= in " + param);
            }
            if ((name != null) && (name != "") && (value != null)) {
                data[name] = value;
                IdentitySelectorDiag.logMessage("parseRpPolicy", "data[" + name + "] =" + value);
            }
            else {
                IdentitySelectorDiag.logMessage("parseRpPolicy", "no name or no value in " + param);
            }
        }
        return data;
    },

    getCidFromPrefs: function() {
        var cid = null;
        // lookup class id from config.
        var prefs = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
        var pbi = prefs.QueryInterface(Components.interfaces.nsIPrefBranch);
        cid = pbi.getCharPref("identityselector.contractid");
        return cid;
    },
    getObjectForClassId: function(cid) {
        var obj = null;
        try {
            var cidClass = Components.classes[cid];
            if (cidClass != undefined) {
                obj = cidClass.createInstance();
                obj = obj.QueryInterface(Components.interfaces.IIdentitySelector);
            }
            else {
                IdentitySelectorDiag.reportError("getObjectForClassId", "the class " + cid + " is not installed");
            }
        }
        catch(e) {
            IdentitySelectorDiag.throwError("getObjectForClassId:", e);
        }
        return obj;
    },

    callIdentitySelector: function(doc) {
        IdentitySelectorDiag.logMessage("IdentitySelector.callIdentitySelector", "doc.location.href=" + doc.location.href);

        var selectorClass;
        // Get the selector class
        if ((selectorClass = IdentitySelectorPrefs.getStringPref("identityselector", "selector_class")) == null) {
            selectorClass = "NoIdentitySelector";
        }

        var data;
        
        var openidReturnToUri = doc.__identityselector__.openidReturnToUri;
        if (openidReturnToUri === undefined) {
	        var icLoginService = doc.__identityselector__.icLoginService;
	        if (icLoginService === undefined) {
	        	throw "internal error in callIdentitySelector: icLoginService === undefined\ndoc=" + doc.location.href;
	        }
	        var icLoginPolicy = doc.__identityselector__.icLoginPolicy;
	        if (icLoginPolicy === undefined) {
	        	throw "internal error in callIdentitySelector: icLoginPolicy === undefined\ndoc=" + doc.location.href;
	        }
	        var sameSchemeAndDomain = InformationCardHelper.sameSchemeAndDomain(doc, icLoginService);
	        if (sameSchemeAndDomain === false) {
	        	Components.utils.reportError("IdentitySelector.callIdentitySelector: Ignoring: sameSchemeAndDomain === false");
	        }
	        data = InformationCardHelper.parseRpPolicy(icLoginPolicy);
        } else {
        	data = {};
        	data.openidReturnToUri = openidReturnToUri;
        }
        
        if (doc.__identityselector__.cardId != undefined) {
            data.cardid = "" + doc.__identityselector__.cardId;
            IdentitySelectorDiag.logMessage("IdentitySelector.callIdentitySelector", "cardid=" + data.cardid);
        }
        data.recipient = doc.location.href;
        
        if (doc.__identityselector__.sslMode != undefined) {
            data.sslMode = "" + doc.__identityselector__.sslMode;
        }

        var getSecurityToken = eval(selectorClass).getSecurityToken;

        if (typeof getSecurityToken != "function") {
            selectorClass = "NoIdentitySelector";
            getSecurityToken = eval(selectorClass).getSecurityToken;
        }

        // call identity selector
        var token = getSecurityToken(data, doc);

        if (token != null) {
            IdentitySelectorDiag.logMessage("IdentitySelector.callIdentitySelector", "sending token " + token + " to " + icLoginService);
            var req = Components.classes["@mozilla.org/xmlextras/xmlhttprequest;1"].createInstance();
            req.open('POST', icLoginService, false);
            req.setRequestHeader("Content-Length", token.length);
            try {
                req.send(token);
            } catch(e) {
                icDebug(e);
                alert("posting the security token to " + icLoginService + " failed" + e);
                return;
            }
            IdentitySelectorDiag.logMessage("IdentitySelector.callIdentitySelector status=" + req.status);
            if (req.status == 200) {
                doc.location.href = icLoginService;
                return;
                // fine
            } else {
                alert("The service " + icLoginService + " returned an error:\n" + req.responseText);
                return;
            }
        } else {
            IdentitySelectorDiag.logMessage("IdentitySelector.callIdentitySelector", "token == null for doc.location.href=" + doc.location.href + "!=" + "icLoginService=" + icLoginService);
        }

    }

    // ***********************************************************************
    // Method: findRelatedObject
    // loop through the objects in the doc to find the one that has targetId
    // as the value of icDropTargetId
    // ***********************************************************************
    ,
    findRelatedObject: function(doc, targetId) {
        var itemCount = 0;
        // Process all of the information card objects in the document
        var objElems = doc.getElementsByTagName("OBJECT");
        for (var i = 0; i < objElems.length; i++) {
            var objElem = objElems[i];
            var objTypeStr = objElem.getAttribute("TYPE");
            if (objTypeStr == null || objTypeStr.toLowerCase() !== "application/x-informationcard") {
                continue;
            }
            if (objElem.icDropTargetId != undefined) {
                IdentitySelectorDiag.logMessage("IdentitySelector.findRelatedObject", "dropTarget for object " + ((objElem.name != undefined) ? objElem.name: "") + " is " + objElem.icDropTargetId);
                if (targetId == objElem.icDropTargetId) {
                    return objElem;
                }
            }
            else {
                IdentitySelectorDiag.logMessage("IdentitySelector.findRelatedObject", "no dropTarget specified for object " + objElem.name);
            }
        }
        // if there is exactly one object then return that
        if (objElems.length == 1) {
        	objElem = objElems[0];
        	var objTypeStr = objElem.getAttribute("TYPE");
        	if (objTypeStr == null || objTypeStr.toLowerCase() === "application/x-informationcard") {
        		return objElem;
        	}
        }
        
        return null;
    }

    ,
    sameSchemeAndDomain: function(ownerDocument, htmlDoc) {
        IdentitySelectorDiag.logMessage("sameSchemeAndDomain", "ownerDocument.location.href=" + ownerDocument.location.href);
        topScheme = ownerDocument.location.protocol;
        topDomain = ownerDocument.location.host;
        // TODO this should go up to the top. Currently this code supports only
        // only
        // level deep nesting.
        IdentitySelectorDiag.logMessage("sameSchemeAndDomain", " topURL:" + ownerDocument.location.href);
        var subWindowScheme = "";
        if (htmlDoc === undefined) {
        	throw "htmlDoc === undefined. caller=" + InformationCardHelper.sameSchemeAndDomain.caller;
        }
        if (htmlDoc.location == undefined) {
            // htmlDoc is a string
            IdentitySelectorDiag.logMessage("sameSchemeAndDomain", " subWindowURL:" + htmlDoc);
            // it is a string not a doc
            var i = htmlDoc.indexOf(':');
            if (i != -1) {
                subWindowScheme = htmlDoc.substring(0, i + 1);
                // include the colon
            }
        }
        else {
            IdentitySelectorDiag.logMessage("sameSchemeAndDomain", " subWindowURL:" + htmlDoc.location.href);
            subWindowScheme = htmlDoc.location.protocol;
        }
        IdentitySelectorDiag.logMessage("sameSchemeAndDomain", " subWindowDomain:" + subWindowDomain);
        if ((subWindowScheme == topScheme) || ((topScheme === "https" ) && (subWindowScheme === "http"))) {
            IdentitySelectorDiag.logMessage("sameSchemeAndDomain", " topDomain:" + topDomain);
            var subWindowDomain = "";
            if (htmlDoc.location == undefined) {
                // it is a string not a doc
                var i = htmlDoc.indexOf("//");
                if (i != -1) {
                    var rest = htmlDoc.substr(i + 2);
                    i = rest.indexOf("/");
                    if (i != -1) {
                        subWindowDomain = rest.substring(0, i);
                    }
                    else {
                        subWindowDomain = rest;
                    }
                }
            }
            else {
                subWindowDomain = htmlDoc.location.host;
            }
            IdentitySelectorDiag.logMessage("sameSchemeAndDomain", " subWindowDomain:" + subWindowDomain);
            if (subWindowDomain == topDomain) {
                return true;
            }
            else {
                IdentitySelectorDiag.logMessage("sameSchemeAndDomain", "domains do not match. " + subWindowDomain + "!=" + topDomain);
            }
        }
        else {
            IdentitySelectorDiag.logMessage("sameSchemeAndDomain", "schemes do not match. " + subWindowScheme + "!=" + topScheme);
        }
        return false;
    }

};