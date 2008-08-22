
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
    try {

        var tokenIssuer = this.getTokenIssuer();
        
        /*
         *  Initialize it. The trick is to get past its IDL interface
         *  and right into its Javascript implementation, so that we
         *  can pass it the LiveConnect "java" object, which it will
         *  then use to load its JARs. Note that XPCOM Javascript code
         *  is not given LiveConnect by default.
         */
        if (!tokenIssuer.wrappedJSObject.initialize(java, false)) {
            alert(tokenIssuer.wrappedJSObject.error);
        }
    } catch (e) {
        this._fail(e);
    }
};

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
        var result = issuer.getCard(dirName, password, card);
        return result;
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
        var result = issuer.getToken(policy);
        return result;

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

TokenIssuer.getTokenIssuer = function() {
    return Components.classes["@xmldap.org/token-issuer;1"]
        .getService(Components.interfaces.nsIHelloWorld);
}

TokenIssuer._trace = function (msg) {
    Components.classes["@mozilla.org/consoleservice;1"]
        .getService(Components.interfaces.nsIConsoleService)
            .logStringMessage(msg);
}

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
