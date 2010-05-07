
/*
 * Copyright (c) 2006, Chuck Mortimore - charliemortimore at gmail.com
 * xmldap.org
 * All rights reserved.
 *
 * Based upon work by: David Fran�ois Huynh  <dfhuynh at csail.mit.edu>
 * http://simile.mit.edu/java-firefox-extension/
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names xmldap, xmldap.org, xmldap.com nor the
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

var TokenIssuer = new Object();

TokenIssuer.initialize = function() {
	TokenIssuer._trace("TokenIssuer.initialize");
	this.tokenIssuer = this.getTokenIssuer();
};

//TokenIssuer.initialize = function() {
//    try {
//
//        var tokenIssuer = this.getTokenIssuer();
//        
//        if (java == undefined) {
//        	TokenIssuer._trace("TokenIssuer.initialize: java is undefined: " + win.document.location.href);
////        	alert("TokenIssuer.initialize: java is undefined");
//        	return false;
//        }
//        
//        {
//        	TokenIssuer._trace( "java.verion=" + java.lang.System.getProperty("java.version") );
//        }
//        
//        /*
//         *  Initialize it. The trick is to get past its IDL interface
//         *  and right into its Javascript implementation, so that we
//         *  can pass it the LiveConnect "java" object, which it will
//         *  then use to load its JARs. Note that XPCOM Javascript code
//         *  is not given LiveConnect by default.
//         */
//        if (!tokenIssuer.wrappedJSObject.initialize(java, true)) {
//            alert(tokenIssuer.wrappedJSObject.error);
//            return false;
//        }
//    } catch (e) {
//        this._fail(e);
//        return false;
//    }
//    return true;
//};

TokenIssuer.getAllCards = function(dirName, password) {
    try {
    	var tokenIssuer = this.getTokenIssuer();
        
        var issuer = tokenIssuer.wrappedJSObject.getTokenIssuer();
        var result = issuer.getAllCards(dirName, password);
        return result;
    } catch (e) {
        this._fail(e);
    }
    return null;
};

TokenIssuer.getCard = function(dirName, password, card) {
    try {
        var tokenIssuer = this.getTokenIssuer();
        
        var issuer = tokenIssuer.wrappedJSObject.getTokenIssuer();
        if (issuer != null) {
	        var result = issuer.getCard(dirName, password, card);
	        return result;
        } else {
        	TokenIssuer._trace("TokenIssuer.getCard: issuer == null");
        }
    } catch (e) {
        this._fail(e);
    }
    return null;
};

TokenIssuer.newCard = function(dirName, password, card) {
    try {
        var tokenIssuer = this.getTokenIssuer();
        
        var issuer = tokenIssuer.wrappedJSObject.getTokenIssuer();
        var result = issuer.newCard(dirName, password, card);
        return result;
    } catch (e) {
        this._fail(e);
    }
    return null;
};

TokenIssuer.newCardStore = function() {
    try {
        var tokenIssuer = this.getTokenIssuer();
        
        var issuer = tokenIssuer.wrappedJSObject.getTokenIssuer();
        var result = issuer.newCardStore();
        return result;
    } catch (e) {
        this._fail(e);
    }
    return null;
};

TokenIssuer.deleteCard = function(cardId) {
    try {
        var tokenIssuer = this.getTokenIssuer();
        
        var issuer = tokenIssuer.wrappedJSObject.getTokenIssuer();
        var result = issuer.deleteCard(cardId);
        return result;
    } catch (e) {
        this._fail(e);
    }
    return null;
};


TokenIssuer.getIssuerLogoURL = function(cert) {
    try {
        var tokenIssuer = this.getTokenIssuer();

        var issuer = tokenIssuer.wrappedJSObject.getTokenIssuer();
        var result = issuer.getIssuerLogoURL(cert);
        return result;

    } catch (e) {
        this._fail(e);
    }
    return null;
};

TokenIssuer.getToken = function(policy) {
    try {
        var tokenIssuer = this.getTokenIssuer();
        
        var issuer = tokenIssuer.wrappedJSObject.getTokenIssuer();
        if (issuer != null) {
	        var result = issuer.getToken(policy);
	        return result;
        } else {
        	TokenIssuer._trace("TokenIssuer.getToken: issuer == null");
        	alert("Please make sure that java is installed and enabled");
        }
    } catch (e) {
        this._fail(e);
    }
    return null;
};

TokenIssuer.importManagedCard = function(importedCardJSONStr, cardFileJSONStr) {
    try {
        var tokenIssuer = this.getTokenIssuer();
        
        var issuer = tokenIssuer.wrappedJSObject.getTokenIssuer();
        var result = issuer.importManagedCard(importedCardJSONStr, cardFileJSONStr);
        return result;

    } catch (e) {
        this._fail(e);
    }
    return null;
};

/*
 *  Get the file path to the installation directory of this 
 *  extension.
 */
TokenIssuer._getExtensionPath = function(extensionName) {
    var chromeRegistry =
        Components.classes["@mozilla.org/chrome/chrome-registry;1"]
            .getService(Components.interfaces.nsIChromeRegistry);
            
    var uri =
        Components.classes["@mozilla.org/network/standard-url;1"]
            .createInstance(Components.interfaces.nsIURI);
    
    uri.spec = "chrome://" + extensionName + "/content/";
    
    var path = chromeRegistry.convertChromeURL(uri);
    if (typeof(path) == "object") {
        path = path.spec;
    }
    
    path = path.substring(0, path.indexOf("/chrome/") + 1);
    
    return path;
};

//TokenIssuer.getTokenIssuer = function() {
//    return Components.classes["@xmldap.org/token-issuer;1"]
//        .getService(Components.interfaces.nsIHelloWorld);
//}

TokenIssuer.getTokenIssuer = function() {
	this._trace("getTokenIssuer {");
    	
	try {
		if (this.tokenIssuer)
	    	return this.tokenIssuer;
		} else {
			var extensionPath = this._getExtensionPath("infocard");
			var libPath = extensionPath + "components/lib/";
			var xmldapUrl = new java.net.URL(libPath+"xmldap.jar");
			var cl = new java.net.URLClassLoader( [ xmldapUrl ]  );
			if (cl === null) {
				this._trace("class loader is null");
				return null;
			}
			// xmldapPolicy gives us read/write permission on all files that are named cardDB.xml
			var xmldapPolicyClass = java.lang.Class.forName("org.xmldap.firefox.XmldapPolicy", true, cl);
			if (xmldapPolicyClass === null) {
				this._trace("xmldapPolicyClass is null");
				return null;
			}
			var policyConstructor = xmldapPolicyClass.getConstructor([java.lang.Class.forName("java.lang.String")]);
			if (policyConstructor === null) {
				this._trace("policyConstructor is null");
				return null;
			}
			var xmldapPolicy = policyConstructor.newInstance( [extensionPath] );
			if (xmldapPolicy === null) {
				this._trace("xmldapPolicy is null");
				return null;
			}
			java.security.Policy.setPolicy(xmldapPolicy);
			
			var tiClass = java.lang.Class.forName("org.xmldap.firefox.TokenIssuer", true, cl);
			if (tiClass === null) {
				this._trace("tiClass is null");
				return null;
			}
			var constructor = tiClass.getConstructor([java.lang.Class.forName("java.lang.String")]);
			if (constructor === null) {
				this._trace("constructor is null");
				return null;
			}
			this.tokenIssuer = constructor.newInstance( [extensionPath] );
			return this.tokenIssuer;
		}
	} catch (e) {
		this._trace("getTokenIssuer threw: " + e);
		return null;
	} finally {
		this._trace("getTokenIssuer }");
	}
};

TokenIssuer._trace = function (msg) {
    Components.classes["@mozilla.org/consoleservice;1"]
        .getService(Components.interfaces.nsIConsoleService)
            .logStringMessage(msg);
};

TokenIssuer._fail = function(e) {
    var msg;
    if (e.getMessage) {
        msg = e + ": " + e.getMessage() + "\n";
        while (e.getCause() != null) {
            e = e.getCause();
            msg += "caused by " + e + ": " + e.getMessage() + "\n";
        }
    } else {
        msg = e;
    }
    alert(msg);
};
