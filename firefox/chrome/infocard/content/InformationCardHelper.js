function getSSLCertFromDocument(doc) {
    var sslCert = null;
    var browser = doc.getElementById("content");
    var secureUi = browser.securityUI;
    var sslStatusProvider = null;
    sslStatusProvider = secureUi.QueryInterface(Components.interfaces.nsISSLStatusProvider);
    if (sslStatusProvider != null) {
        try {
            sslStatus = sslStatusProvider.SSLStatus.QueryInterface(Components.interfaces.nsISSLStatus);
            if (sslStatus != null && sslStatus.serverCert != undefined) {
                sslCert = sslStatus.serverCert
            }
        }
        catch(e) {
            IdentitySelectorDiag.logMessage("getSSLCertFromDocument: " + e);
        }
    }
    return sslCert;
}

var InformationCardHelper = {
    parseRpPolicy: function(icLoginPolicy) {
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
            IdentitySelector.throwError("getObjectForClassId:", e);
        }
        return obj;
    },

    callIdentitySelector: function(doc) {
        IdentitySelectorDiag.logMessage("IdentitySelector.callIdentitySelector", "doc.location.href=" + doc.location.href);

        var icLoginService = doc.__identityselector__.icLoginService;
        var icLoginPolicy = doc.__identityselector__.icLoginPolicy;
        // post token value to service
        var sameSchemeAndDomain = InformationCardHelper.sameSchemeAndDomain(doc, icLoginService);
        if (sameSchemeAndDomain == false) {
            IdentitySelectorDiag.logMessage("IdentitySelector.callIdentitySelector", "sameSchemeAndDomain == false");
            return;
        }
        // Get the selector class
        if ((selectorClass = IdentitySelectorPrefs.getStringPref("identityselector", "selector_class")) == null) {
            selectorClass = "NoIdentitySelector";
        }

        getSecurityToken = eval(selectorClass).getSecurityToken;

        if (typeof getSecurityToken != "function") {
            selectorClass = "NoIdentitySelector";
            getSecurityToken = eval(selectorClass).getSecurityToken;
        }

        var data = InformationCardHelper.parseRpPolicy(icLoginPolicy);
        
        if (doc.__identityselector__.cardId != undefined) {
            data.cardid = "" + doc.__identityselector__.cardId;
        }
        data.recipient = doc.location.href;
        
        if (doc.__identityselector__.sslMode != undefined) {
            data.sslMode = "" + doc.__identityselector__.sslMode;
        }

        // call identity selector
        var token = getSecurityToken(data);

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
                IdentitySelectorDiag.logMessage("IdentitySelector.findRelatedObject", "no dropTarget specified for object " + object.name);
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
        if (subWindowScheme == topScheme) {
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