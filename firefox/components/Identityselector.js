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

//Implements Kevin's contract:
//IIdentitySelector.GetBrowserToken(data.issuer , data.recipientURL, data.requiredClaims ,data.optionalClaims , data.tokenType ,data.privacyPolicy, data.privacyPolicyVersion ,sslStatus.serverCert );


const nsISupports = Components.interfaces.nsISupports;
const IIdentitySelector = Components.interfaces.IIdentitySelector;

const CLASS_ID = Components.ID("72e894fd-0d6c-484d-abe8-5903b5f8bf3b");
const CLASS_NAME = "The xmldap.org identity selector";
const CONTRACT_ID = "@xmldap.org/identityselector;1";

const nsIX509Cert = Components.interfaces.nsIX509Cert;

function Xmldapidentityselector() {}

Xmldapidentityselector.prototype = {

    GetBrowserToken: function (issuer , recipientURL, requiredClaims, optionalClaims , tokenType, privacyPolicy, privacyPolicyVersion, serverCert ) {

        debug('issuer: ' + issuer);
        debug('recipientURL: ' + recipientURL);
        debug('requiredClaims: ' + requiredClaims);
        debug('optionalClaims: ' + optionalClaims);
        debug('tokenType: ' + tokenType);
        debug('privacyPolicy: ' + privacyPolicy);
        debug('privacyPolicyVersion: ' + privacyPolicyVersion);
        debug('serverCert: ' + serverCert);


        var callback;

        var policy = [];
        policy["tokenType"] = tokenType;
        policy["issuer"] = issuer;
        policy["requiredClaims"] = requiredClaims;
        policy["optionalClaims"] = optionalClaims;

        //get a handle on a window
        var wm = Components.classes["@mozilla.org/appshell/window-mediator;1"].getService(Components.interfaces.nsIWindowMediator);
        var win = wm.getMostRecentWindow("navigator:browser");

        policy["cert"] = getDer(serverCert,win);
        policy["cn"] = serverCert.commonName;
        // win.document.URL is undefined
        // win.document.location.href is chrome://.../browser.xul
		policy["url"] = recipientURL; 

	    var chain = serverCert.getChain();
		debug('chain: ' + chain);
		debug('chainLength: ' + chain.length);
		debug('chain[0]: ' + chain.queryElementAt(0, nsIX509Cert));
		
		policy["chainLength"] = ""+chain.length;
		for (var i = 0; i < chain.length; ++i) {
		  var currCert = chain.queryElementAt(i, nsIX509Cert);
		  policy["certChain"+i] = getDer(currCert,win);
		}

        var cardManager = win.openDialog("chrome://infocard/content/cardManager.xul","InfoCard Selector", "modal,chrome", policy, function (callbackData) { callback = callbackData;});

        var doc = win.document;
        var event = doc.createEvent("Events");
        event.initEvent("CloseIdentitySelector", true, true);
        win.dispatchEvent(event);

        debug('Token: ' + callback);

        return callback;

    },

    QueryInterface: function(aIID) {

        if ( (!aIID.equals(nsISupports))  && (!aIID.equals(IIdentitySelector)))
            throw Components.results.NS_ERROR_NO_INTERFACE;
        return this;

    }

}


function getDer(cert,win){

    var length = {};
    var derArray = cert.getRawDER(length);
    var certBytes = '';
    for (var i = 0; i < derArray.length; i++) {
        certBytes = certBytes + String.fromCharCode(derArray[i]);
    }
    return win.btoa(certBytes);

}


//=================================================
// Note: You probably don't want to edit anything
// below this unless you know what you're doing.
//
// Factory
var XmldapidentityselectorFactory = {
  createInstance: function (aOuter, aIID)
  {
    if (aOuter != null)
      throw Components.results.NS_ERROR_NO_AGGREGATION;
    return (new Xmldapidentityselector()).QueryInterface(aIID);
  }
};

// Module
var XmldapidentityselectorModule = {
  registerSelf: function(aCompMgr, aFileSpec, aLocation, aType)
  {
    aCompMgr = aCompMgr.QueryInterface(Components.interfaces.nsIComponentRegistrar);
    aCompMgr.registerFactoryLocation(CLASS_ID, CLASS_NAME, CONTRACT_ID, aFileSpec, aLocation, aType);
  },

  unregisterSelf: function(aCompMgr, aLocation, aType)
  {
    aCompMgr = aCompMgr.QueryInterface(Components.interfaces.nsIComponentRegistrar);
    aCompMgr.unregisterFactoryLocation(CLASS_ID, aLocation);
  },

  getClassObject: function(aCompMgr, aCID, aIID)
  {
    if (!aIID.equals(Components.interfaces.nsIFactory))
      throw Components.results.NS_ERROR_NOT_IMPLEMENTED;

    if (aCID.equals(CLASS_ID))
      return XmldapidentityselectorFactory;

    throw Components.results.NS_ERROR_NO_INTERFACE;
  },

  canUnload: function(aCompMgr) { return true; }
};

//module initialization
function NSGetModule(aCompMgr, aFileSpec) { return XmldapidentityselectorModule; }




function debug(msg) {
  var debug = Components.classes["@mozilla.org/consoleservice;1"].getService(Components.interfaces.nsIConsoleService);
  debug.logStringMessage("XMLDAP: " + msg);
}