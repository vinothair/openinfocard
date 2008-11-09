//------------------------------------------------------------------------------
// Desc:
// Tabs: 3
//
// Copyright (c) 2007-2008 Novell, Inc. All Rights Reserved.
//
// This program and the accompanying materials are made available
// under, alternatively, the terms of:  a) the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html; or, b) the Apache License,
// Version 2.0 which accompanies this distribution as is available at
// www.opensource.org/licenses/apache2.0.php.
//
// To contact Novell about this file by physical or electronic mail,
// you may find current contact information at www.novell.com.
//
// Author: Andrew Hodgkinson <ahodgkinson@novell.com>
//------------------------------------------------------------------------------

// **************************************************************************
// Desc: Constants
// **************************************************************************

const nsIX509Cert = Components.interfaces.nsIX509Cert;

const nsIX509CertDB = Components.interfaces.nsIX509CertDB;
const nsX509CertDB = "@mozilla.org/security/x509certdb;1";

const nsINSSCertCache = Components.interfaces.nsINSSCertCache;
const nsNSSCertCache = "@mozilla.org/security/nsscertcache;1";

const nsICertTree = Components.interfaces.nsICertTree;
const nsCertTree = "@mozilla.org/security/nsCertTree;1";

const nsILocalFile = Components.interfaces.nsILocalFile;
const nsLocalFile = "@mozilla.org/file/local;1";

const nsIFileOutputStream = Components.interfaces.nsIFileOutputStream;
const nsFileOutputStream = "@mozilla.org/network/file-output-stream;1";

const nsICardObjTypeStr = "application/x-informationcard";
               
// **************************************************************************
// Desc: Global services
// **************************************************************************

var gPrefService = Components.classes[
                "@mozilla.org/preferences-service;1"].
                        getService( Components.interfaces.nsIPrefBranch);
var gConsoleService = Components.classes[ "@mozilla.org/consoleservice;1"].
                        getService( Components.interfaces.nsIConsoleService);
var gbLoggingEnabled = true;
var gLastFailedGetTokenDate = null;
var gDisableStartTime = null;
var gDebugMode = true;

// **************************************************************************
// Desc: Other global variables
// **************************************************************************

var gNavPlat = navigator.platform;
var gbIsWin = ((gNavPlat.indexOf( "Win") > -1) ? true : false);
var gbIsMac = ((gNavPlat.indexOf( "Mac") > -1) ? true : false);
var gbIsLinux = ((gNavPlat.indexOf( "Linux") > -1) ? true : false);
                                       
// **************************************************************************
// Desc:
// **************************************************************************

var ICProgressListener =
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
       
        onStateChange : function( aProgress, aRequest, aFlag, aStatus)
        {
                var progListIFace = Components.interfaces.nsIWebProgressListener;
               
                // Log the flags
               
                IdentitySelector.logMessage( "onStateChange", "flags = " + aFlag);
               
                if( aFlag & progListIFace.STATE_IS_DOCUMENT)
                {
                        IdentitySelector.logMessage( "onStateChange", "flag & document");
                }
                       
                if( aFlag & progListIFace.STATE_IS_WINDOW)
                {
                        IdentitySelector.logMessage( "onStateChange", "flag & window");
                }
               
                if( aFlag & progListIFace.STATE_START)
                {
                        IdentitySelector.logMessage( "onStateChange", "flag & start");
                }
               
                if( aFlag & progListIFace.STATE_STOP)
                {
                        IdentitySelector.logMessage( "onStateChange", "flag & stop");
                }
               
                // Process the document.  The 'STOP' state isn't reached until after
                // the page is fully loaded and all onload events have completed.
                // We need to re-process the page in case an onload event added
                // information card elements or objects to the page.  An example of
                // a page that does this is login.live.com.
               
                if( aFlag & progListIFace.STATE_STOP)
                {
                        if( aFlag & progListIFace.STATE_IS_WINDOW)
                        {
                                IdentitySelector.logMessage( "onStateChange",
                                        "stop status code = " + aStatus);
                                       
                                if( aStatus == 0)
                                {
                                        // Process any information card items
                                       
                                        try
                                        {
                                                IdentitySelector.processICardItems(
                                                        aProgress.DOMWindow.document, true);
                                        }
                                        catch( e)
                                        {
                                                alert( e);
                                        }
                                }
                        }
                }
               
                return( 0);
        },
       
        onLocationChange : function( aProgress, aRequest, aURI)
        {
                // This fires when a load event has been confirmed or when the
                // user switches tabs.  At this point, Firefox has created a skeletal
                // document into which the source document will be loaded.  This is
                // where we run our global intercept script.
               
                try
                {
                        IdentitySelector.runInterceptScript( aProgress.DOMWindow.document);
                }
                catch( e)
                {
                        if( gDebugMode)
                        {
                                IdentitySelector.reportError( "onLocationChange", e);
                        }
                }
               
                return( 0);
        },
       
        onProgressChange : function()
        {
                return( 0);
        },
       
        onStatusChange : function()
        {
                return( 0);
        },
       
        onSecurityChange : function()
        {
                return( 0);
        },
       
        onLinkIconAvailable : function()
        {
                return( 0);
        }
};

// **************************************************************************
// Desc:
// **************************************************************************

var IdentitySelector =
{
        // ***********************************************************************
        // Method: reportError
        // ***********************************************************************
       
        reportError : function( location, description)
        {
                alert( "IdentitySelector Error:" + location + ": " + description);
                IdentitySelector.logMessage( location, "Error:" + description);
        },

        // ***********************************************************************
        // Method: throwError
        // ***********************************************************************
       
        throwError : function( location, description)
        {
                IdentitySelector.reportError( location, description);
                throw( "IdentitySelector Exception:" + location + ": " + description);
        },
       
        // ***********************************************************************
        // Method: logMessage
        // ***********************************************************************
       
        logMessage : function( location, message)
        {
                if( gbLoggingEnabled)
                {
                        gConsoleService.logStringMessage( "IdentitySelector:" +
                                location + ": " + message);
                }
        },
       
        // ***********************************************************************
        // Method: dumpConsoleToLogFile
        // ***********************************************************************
       
        dumpConsoleLogToFile : function( filePath)
        {
                var messageArray = {};
                var messageCount = {};
                var logBuffer = "";
               
                try
                {
                        gConsoleService.getMessageArray( messageArray, messageCount);
                       
                        for( var i = 0; i < messageCount.value; i++)
                        {
                                var messageStr = messageArray.value[ i].message;
                               
                                if( messageStr.indexOf( "IdentitySelector:") > -1)
                                {
                                        logBuffer += messageStr + "\n";
                                }
                        }
                       
                        if( gbIsMac || gbIsLinux)
                        {
                                IdentitySelector.writeFile( "/tmp/icardxpi.log", logBuffer);
                        }
                }
                catch( e)
                {
                        IdentitySelector.reportError( "dumpConsoleLogToFile", e);
                }
        },

        // ***********************************************************************
        // Method: logPlatformInfo
        // ***********************************************************************
       
        logPlatformInfo : function()
        {
                try
                {
                        IdentitySelector.logMessage( "logPlatformInfo",
                                "platform = " + navigator.platform);
                               
                        IdentitySelector.logMessage( "logPlatformInfo",
                                "appName = " + navigator.appName);
                               
                        IdentitySelector.logMessage( "logPlatformInfo",
                                "appVersion = " + navigator.appVersion);
                               
                        IdentitySelector.logMessage( "logPlatformInfo",
                                "product = " + navigator.product);
                               
                        IdentitySelector.logMessage( "logPlatformInfo",
                                "productSub = " + navigator.productSub);
                               
                        IdentitySelector.logMessage( "logPlatformInfo",
                                "userAgent = " + navigator.userAgent);
                               
                        IdentitySelector.logMessage( "logPlatformInfo",
                                "oscpu = " + navigator.oscpu);
                }
                catch( e)
                {
                }
        },
                       
        // ***********************************************************************
        // Method: onInstall
        // ***********************************************************************
       
        onInstall : function( event)
        {
        	IdentitySelector.logMessage( "IdentitySelector", "onInstall start");

                var handlerAlreadyInstalled = false;
               
                try
                {
                        // Remove the load event listener
                       
                        window.removeEventListener( "load",
                                IdentitySelector.onInstall, false);
                               
                        // Determine if another add-on or plug-in has registered to handle
                        // information cards
                       
                        if( navigator.mimeTypes && navigator.mimeTypes.length)
                        {
                                var mimeHandler = navigator.mimeTypes[ nsICardObjTypeStr];
                               
                                if( mimeHandler && mimeHandler.enabledPlugin)
                                {
                                        handlerAlreadyInstalled = true;
                                }
                        }
                       
                        if( !handlerAlreadyInstalled)
                        {
                                var event = document.createEvent( "Event");

                                event.initEvent( "IdentitySelectorAvailable", true, true);
                                top.dispatchEvent( event);
                               
                                if( top.IdentitySelectorAvailable == true)
                                {
                                        handlerAlreadyInstalled = true;
                                }
                        }
                       
                        if( !handlerAlreadyInstalled)
                        {
                                // Add event handlers.  The optional fourth parameter to
                                // addEventListener indicates that the listener is willing to
                                // accept untrusted events (such as those generated by web
                                // content).
                               
                                window.addEventListener(
                                        "IdentitySelectorAvailable",
                                        IdentitySelector.onIdentitySelectorAvailable, false, true);
               
                                window.addEventListener(
                                        "CallIdentitySelector",
                                        IdentitySelector.onCallIdentitySelector, false, true);
                       
                                window.addEventListener(
                                        "ICHideNotificationBox",
                                        IdentitySelector.onHideNotificationBox, false, true);
                       
                                window.addEventListener(
                                        "ICObjectLoaded",
                                        IdentitySelector.onICardObjectLoaded, false, true);
               
                                window.addEventListener(
                                        "ICElementLoaded",
                                        IdentitySelector.onICardElementLoaded, false, true);
                                       
                                window.addEventListener(
                                        "ICFormSubmit",
                                        IdentitySelector.onFormSubmit, false, true);
                                       
                                window.addEventListener(
                                        "ICGetTokenValue",
                                        IdentitySelector.onGetTokenValue, false, true);
                                       
                                window.addEventListener(
                                        "ICProcessItems",
                                        IdentitySelector.onProcessItems, false, true);
                                       
                                window.addEventListener(
                                        "ICDOMChanged",
                                        IdentitySelector.onDOMChanged, false, true);
                                       
                                // Add a progress listener
                                       
                                window.getBrowser().addProgressListener( ICProgressListener,
                                        Components.interfaces.nsIWebProgress.NOTIFY_ALL);
                        }
                        else
                        {
                                IdentitySelector.logMessage( "onInstall",
                                        "Another identity selector is already installed.");
                        }
                }
                catch( e)
                {
                        IdentitySelector.throwError( "onInstall", e);
                }
        },
       
        // ***********************************************************************
        // Method: onUninstall
        // ***********************************************************************
       
        onUninstall : function( event)
        {
                try
                {
                        // Remove the event listeners
                       
                        window.removeEventListener( "load",
                                IdentitySelector.onInstall, false);
                               
                        window.removeEventListener( "unload",
                                IdentitySelector.onUninstall, false);
                               
                        window.removeEventListener( "CallIdentitySelector",
                                IdentitySelector.onCallIdentitySelector, false);
               
                        window.removeEventListener( "IdentitySelectorAvailable",
                                IdentitySelector.onIdentitySelectorAvailable, false);
       
                        window.removeEventListener( "ICHideNotificationBox",
                                IdentitySelector.onHideNotificationBox, false);
               
                        window.removeEventListener( "ICObjectLoaded",
                                IdentitySelector.onICardObjectLoaded, false);
       
                        window.removeEventListener( "ICElementLoaded",
                                IdentitySelector.onICardElementLoaded, false);
                               
                        window.removeEventListener( "ICFormSubmit",
                                IdentitySelector.onFormSubmit, false);
                               
                        window.removeEventListener( "ICGetTokenValue",
                                IdentitySelector.onGetTokenValue, false);
                               
                        window.removeEventListener( "ICProcessItems",
                                IdentitySelector.onProcessItems, false);
                                       
                        window.removeEventListener( "ICDOMChanged",
                                IdentitySelector.onDOMChanged, false);
                               
                        // Remove progress listener
                                       
                        window.getBrowser().removeProgressListener( ICProgressListener);
                }
                catch( e)
                {
                        IdentitySelector.throwError( "onUninstall", e);
                }
        },
       
        // ***********************************************************************
        // Method: runInterceptScript
        // ***********************************************************************
       
        runInterceptScript : function( doc)
        {
                if( doc.wrappedJSObject)
                {
                        doc = doc.wrappedJSObject;
                }
               
                if( doc.__identityselector__ == undefined)
                {
                        // Load and execute the script
                       
                        Components.classes[
                                "@mozilla.org/moz/jssubscript-loader;1"].getService(
                                Components.interfaces.mozIJSSubScriptLoader).loadSubScript(
                                "chrome://infocard/content/Intercept.js", doc)
                       
                        IdentitySelector.logMessage( "runInterceptScript",
                                "Executed script on " + doc.location);
                }
        },
       
        // ***********************************************************************
        // Method: onDOMChanged
        // ***********************************************************************
        onDOMChanged : function( event)
        {
                var target = event ? event.target : this;
                var doc;

                if( target.wrappedJSObject)
                {
                        target = target.wrappedJSObject;
                }

                try
                {
                        if( (doc = target.ownerDocument) == undefined)
                        {
                                return;
                        }
       
                        var event = doc.createEvent( "Event");
                        event.initEvent( "ICDOMChanged", true, true);
                        target.dispatchEvent( event);
                }
                catch( e)
                {
                        if( gDebugMode)
                        {
                                IdentitySelector.reportError( "onDOMChanged", e);
                        }
                }
        },
       
        // ***********************************************************************
        // Method: onProcessItems
        // ***********************************************************************
       
        onProcessItems : function( event)
        {
                var target = event ? event.target : this;
                var doc;
               
                if( target.wrappedJSObject)
                {
                        target = target.wrappedJSObject;
                }
               
                doc = target;
               
                // Sanity checks

                try
                {
                        // If the intercept script was executed correctly,
                        // __identityselector__ should be defined.
                       
                        if( doc.__identityselector__ == undefined)
                        {
                                // Since there are information card items on the page, warn
                                // the user that the identity selector was unable to insert
                                // itself properly.  This is most likely due to JavaScript being
                                // disabled.
                               
                                var jsEnabled = gPrefService.getBoolPref( "javascript.enabled");
               
                                // Warn if JavaScript is disabled
                               
                                if( !jsEnabled)
                                {
                                        IdentitySelector.reportError( "onProcessItems",
                                                "This page contains information card objects, but " +
                                                "JavaScript is disabled.  The information card " +
                                                "selector may not run properly.  To enable, go to " +
                                                "Preferences->Content->Enable JavaScript.");
                                }
                                else
                                {
                                        IdentitySelector.reportError( "onProcessItems",
                                                "This page contains information card objects, but " +
                                                "the information card selector was unable to fully " +
                                                "process the page.");
                                }
                               
                                // Hide the notification box
                               
                                IdentitySelector.onHideNotificationBox();
                               
                                // Done
                               
                                return;
                        }
                       
                        // Process all of the information card objects and elements
                        // in the document
               
                        doc.__identityselector__.contentLoaded = true;
                       
                        if( !doc.__identityselector__.submitIntercepted)
                        {
                                IdentitySelector.processICardItems( doc, true);
                        }
                }
                catch( e)
                {
                        if( gDebugMode)
                        {
                                IdentitySelector.reportError( "onProcessItems", e);
                        }
                }
        },
       
        // ***********************************************************************
        // Method: processICardItems
        // ***********************************************************************
       
        processICardItems : function( doc, dispatchEvents)
        {
                try
                {
                        var itemCount = 0;
                       
                        // Process all of the information card objects in the document
                                       
                        var objElems = doc.getElementsByTagName( "OBJECT");
                        var icardObjectCount = 0;
                       
                        IdentitySelector.logMessage( "processICardItems", "Found " +
                                objElems.length + " object(s) on " + doc.location);
                               
                        for( var i = 0; i < objElems.length; i++)
                        {
                                var objElem = objElems[ i];
                                var objTypeStr = objElem.getAttribute( "TYPE");
                               
                                if( (objTypeStr != null &&
                                                objTypeStr.toLowerCase() == nsICardObjTypeStr) ||
                                        objElem._type == nsICardObjTypeStr)
                                {
                                        if( dispatchEvents)
                                        {
                                                var event = doc.createEvent( "Event");
                                                event.initEvent( "ICObjectLoaded", true, true);
                                                objElem.dispatchEvent( event);
                                        }
                                       
                                        icardObjectCount++;
                                }
                        }
                       
                        IdentitySelector.logMessage( "processICardItems", "Found " +
                                icardObjectCount + " ICard object(s) on " + doc.location);
                               
                        // Process all of the information card elements in the document
                       
                        var icardElems = doc.getElementsByTagName( "IC:INFORMATIONCARD");
                        var icardElementCount = icardElems.length;
                       
                        if( dispatchEvents)
                        {
                                for( var i = 0; i < icardElems.length; i++)
                                {
                                        var icardElem = icardElems[ i];
                                       
                                        var event = doc.createEvent( "Event");
                                        event.initEvent( "ICElementLoaded", true, true);
                                        icardElem.dispatchEvent( event);
                                }
                        }
                       
                        IdentitySelector.logMessage( "processICardItems", "Found " +
                                icardElementCount + " ICard element(s) on " + doc.location);
                               
                        return( icardObjectCount + icardElementCount);
                }
                catch( e)
                {
                        if( gDebugMode)
                        {
                                IdentitySelector.reportError( "processICardItems", e);
                        }
                }
        },
       
        // ***********************************************************************
        // Method: setParamsFromElem
        // ***********************************************************************
       
        setParamsFromElem : function( doc, objElem)
        {
                try
                {
                        var data = doc.__identityselector__.data;
                       
                        // recipient
                       
                        if( doc.location.href != undefined &&
                                doc.location.href != null && doc.location.href != "")
                        {
                                data.recipient = doc.location.href;
                        }
                        else
                        {
                                delete data[ "recipient"];
                        }

                        // tokenType
                       
                        if( objElem.tokenType != undefined && objElem.tokenType != null)
                        {
                                data.tokenType = objElem.tokenType;
                        }
                        else
                        {
                                delete data[ "tokenType"];
                        }
                       
                        // optionalClaims
                       
                        if( objElem.optionalClaims != undefined &&
                                objElem.optionalClaims != null)
                        {
                                data.optionalClaims = objElem.optionalClaims;
                        }
                        else
                        {
                                delete data[ "optionalClaims"];
                        }
                       
                        // requiredClaims
                       
                        if( objElem.requiredClaims != undefined &&
                                objElem.requiredClaims != null)
                        {
                                data.requiredClaims = objElem.requiredClaims;
                        }
                        else
                        {
                                delete data[ "requiredClaims"];
                        }
                       
                        // issuer
                       
                        if( objElem.issuer != undefined && objElem.issuer != null)
                        {
                                data.issuer = objElem.issuer;
                        }
                        else
                        {
                                delete data[ "issuer"];
                        }
                       
                        // issuerPolicy
                       
                        if( objElem.issuerPolicy != undefined && objElem.issuerPolicy != null)
                        {
                                data.issuerPolicy = objElem.issuerPolicy;
                        }
                        else
                        {
                                delete data[ "issuerPolicy"];
                        }
                       
                        // privacyUrl
                       
                        if( objElem.privacyUrl != undefined &&
                                objElem.privacyUrl != null)
                        {
                                data.privacyUrl = objElem.privacyUrl;
                        }
                        else
                        {
                                delete data[ "privacyUrl"];
                        }
                       
                        // privacyVersion
       
                        if( objElem.privacyVersion != undefined &&
                                objElem.privacyVersion != null)
                        {
                                data.privacyVersion = objElem.privacyVersion;
                        }
                        else
                        {
                                delete data[ "privacyVersion"];
                        }
                }
                catch( e)
                {
                        if( gDebugMode)
                        {
                                IdentitySelector.reportError( "setParamsFromElem", e);
                        }
                }
        },
       
        // ***********************************************************************
        // Method: onFormSubmit
        // ***********************************************************************

        onFormSubmit : function( event)
        {
                try
                {
                        var target = event ? event.target : this;
                        var doc;
                        var form;
                        var objElem;
                        var icardElem;
                       
                        if( target.wrappedJSObject)
                        {
                                target = target.wrappedJSObject;
                        }
                       
                        form = target;
                        doc = target.ownerDocument;
                       
                        // Determine if the form has an embedded information card object.
                       
                        if( (objElem = IdentitySelector.getEmbeddedICardObject( form)) != null)
                        {
                                IdentitySelector.logMessage( "onFormSubmit",
                                        "Intercepted submit of form with embedded ICard object");
                                       
                                // Process the embedded object
                               
                                if( !doc.__identityselector__.contentLoaded)
                                {
                                        var event = doc.createEvent( "Event");
                                        event.initEvent( "ICObjectLoaded", true, true);
                                        objElem.dispatchEvent( event);
                                }
                               
                                // If the embedded ICard object doesn't have a token attached to
                                // it, invoke the selector
                               
                                if( objElem.token == undefined)
                                {
                                        IdentitySelector.logMessage( "onFormSubmit",
                                                "Submit encountered in-line");
                                       
                                        var event = doc.createEvent( "Event");
                                        event.initEvent( "CallIdentitySelector", true, true);
                                        doc.__identityselector__.targetElem = objElem;
                                        doc.dispatchEvent( event);
                                       
                                        // If a token was retrieved, add it as a hidden field of
                                        // the form
                                       
                                        if( objElem.token != undefined)
                                        {
                                                var input = doc.createElement( "INPUT");
                                       
                                                input.setAttribute( "name",
                                                        objElem.getAttribute( "name"));
                                                input.setAttribute( "type", "hidden");
                                                input.value = objElem.token;
                                                form.appendChild( input);
                                        }
                                }
                        }
                        else if( (icardElem =
                                IdentitySelector.getEmbeddedICardElement( form)) != null)
                        {
                                IdentitySelector.logMessage( "onFormSubmit",
                                        "Intercepted submit of form with embedded ICard element");
                               
                                // Process the embedded element
                               
                                if( !doc.__identityselector__.contentLoaded)
                                {
                                        var event = doc.createEvent( "Event");
                                        event.initEvent( "ICElementLoaded", true, true);
                                        icardElem.dispatchEvent( event);
                                }
                               
                                // If the embedded ICard element doesn't have a token attached to
                                // it, invoke the selector
                               
                                if( icardElem.token == undefined)
                                {
                                        IdentitySelector.logMessage( "onFormSubmit",
                                                "Submit encountered in-line");
                                       
                                        var event = doc.createEvent( "Event");
                                        event.initEvent( "CallIdentitySelector", true, true);
                                        doc.__identityselector__.targetElem = icardElem;
                                        doc.dispatchEvent( event);
                                       
                                        // If a token was retrieved, add it as a hidden field of
                                        // the form
                                       
                                        if( icardElem.token != undefined)
                                        {
                                                var input = doc.createElement( "INPUT");
                                                       
                                                input.setAttribute( "name",
                                                        icardElem.getAttribute( "name"));
                                                input.setAttribute( "type", "hidden");
                                                input.value = icardElem.token;
                                                form.appendChild( input);
                                        }
                                }
                        }
                        else
                        {
                                IdentitySelector.logMessage( "onFormSubmit",
                                        "Intercepted submit of standard form");
                        }
                       
                        doc.__identityselector__.submitIntercepted = true;
                }
                catch( e)
                {
                        if( gDebugMode)
                        {
                                IdentitySelector.reportError( "onFormSubmit", e);
                        }
                }
        },

        // ***********************************************************************
        // Method: onGetTokenValue
        // ***********************************************************************
       
        onGetTokenValue : function( event)
        {
                try
                {
                        var target = event ? event.target : this;
                        var doc;
                        var targetElem;
                       
                        if( target.wrappedJSObject)
                        {
                                target = target.wrappedJSObject;
                        }
                       
                        targetElem = target;
                        doc = target.ownerDocument;
                       
                        IdentitySelector.logMessage( "onGetTokenValue",
                                "Token value requested");
                               
                        if( targetElem.__value == undefined)
                        {
                                var event = doc.createEvent( "Event");
                                event.initEvent( "CallIdentitySelector", true, true);
                                doc.__identityselector__.targetElem = targetElem;
                                doc.dispatchEvent( event);
                                targetElem.__value = targetElem.token;
                        }
                }
                catch( e)
                {
                        IdentitySelector.throwError( "onGetTokenValue", e);
                }
        },
       
        // ***********************************************************************
        // Method: extractParameter
        // ***********************************************************************
       
        extractParameter : function( sourceNode, dataObj)
        {
                switch( sourceNode.name.toLowerCase())
                {
                        case "tokentype":
                        {
                                IdentitySelector.logMessage( "extractParameter",
                                        "tokenType = " + sourceNode.value);
                                dataObj.tokenType = sourceNode.value;
                                break;
                        }
                 
                        case "optionalclaims":
                        {
                                IdentitySelector.logMessage( "extractParameter",
                                        "optionalClaims = " + sourceNode.value);
                                dataObj.optionalClaims = sourceNode.value;
                                break;
                        }
         
                        case "requiredclaims":
                        {
                                IdentitySelector.logMessage( "extractParameter",
                                        "requiredClaims = " + sourceNode.value);
                                dataObj.requiredClaims = sourceNode.value;
                                break;
                        }
         
                        case "issuer":
                        {
                                IdentitySelector.logMessage( "extractParameter",
                                        "issuer = " + sourceNode.value);
                                dataObj.issuer = sourceNode.value;
                                break;
                        }
         
                        case "issuerpolicy":
                        {
                                IdentitySelector.logMessage( "extractParameter",
                                        "issuerPolicy = " + sourceNode.value);
                                dataObj.issuerPolicy = sourceNode.value;
                                break;
                        }

                        case "privacyurl":
                        {
                                IdentitySelector.logMessage( "extractParameter",
                                        "privacyUrl = " + sourceNode.value);
                                dataObj.privacyUrl = sourceNode.value;
                                break;
                        }

                        case "privacyversion":
                        {
                                IdentitySelector.logMessage( "extractParameter",
                                        "privacyVersion = " + sourceNode.value);
                                dataObj.privacyVersion = sourceNode.value;
                                break;
                        }

                        default:
                        {
                                IdentitySelector.logMessage( "extractParameter",
                                        "unknown parameter: " + sourceNode.name +
                                        " = " + sourceNode.value);
                                break;
                        }
                }
        },

        // ***********************************************************************
        // Method: onDOMChanged
        // ***********************************************************************
       
        onDOMChanged : function( event)
        {
                var target = event ? event.target : this;
                var doc;

                if( target.wrappedJSObject)
                {
                        target = target.wrappedJSObject;
                }

                try
                {
                        if( (doc = target.ownerDocument) == undefined)
                        {
                                return;
                        }
                       
                        if( target instanceof HTMLObjectElement)
                        {
                                var objTypeStr = target.getAttribute( "TYPE");

                                IdentitySelector.logMessage( "onDOMChanged",
                                        "OBJECT changed, type = " + objTypeStr);

                                if( (objTypeStr != null && objTypeStr.toLowerCase() ==
                                                "application/x-informationcard") ||
                                        target._type == "application/x-informationcard")
                                {
                                        delete target.__processed;
                                       
                                        var event = doc.createEvent( "Event");
                                        event.initEvent( "ICObjectLoaded", true, true);
                                        target.dispatchEvent( event);
                                }
                        }
                }
                catch( e)
                {
                        if( gDebugMode)
                        {
                                IdentitySelector.reportError( "onDOMChanged", e);
                        }
                }
        },
                               
        // ***********************************************************************
        // Method: onICardObjectLoaded
        // ***********************************************************************
       
        onICardObjectLoaded : function( event)
        {
                var target = event ? event.target : this;
                var doc;
               
                if( target.wrappedJSObject)
                {
                        target = target.wrappedJSObject;
                }
               
                doc = target.ownerDocument;
               
                try
                {
                        var objElem = target;
                        var form = objElem;
                        var browser = getBrowser();
                       
                        if( objElem.__processed == true)
                        {
                                IdentitySelector.logMessage( "onICardObjectLoaded",
                                        "ICard object has already been processed.");
                        }
                        else
                        {
                                // Make sure the intercept script has been run on this document.
                                // In the case of an iframe, it may not have been run.
                               
                                if( doc.__identityselector__ == undefined)
                                {
                                        IdentitySelector.runInterceptScript( doc);
                                }
                               
                                // Override the value getter
                               
                                delete objElem[ "value"];
                               
                                objElem.__defineGetter__( "value",
                                        doc.__identityselector__.valueGetter);
                                       
                                // Clear the type attribute if this version of the browser
                                // doesn't support hiding the notification box
                               
                                if( typeof( browser.getNotificationBox) != "function")
                                {
                                        if( (objElem._type = objElem.getAttribute(
                                                "type")) != undefined)
                                        {
                                                objElem._type = objElem._type.toLowerCase();
                                                objElem.removeAttribute( "type");
                                        }
                                }
                                       
                                // Intercept all submit actions if the object is embedded within
                                // a parent form
                               
                                while( form != null)
                                {
                                        if( form.tagName != undefined && form.tagName == "FORM")
                                        {
                                                // The form submit method has been hooked.  We still need
                                                // to register for the submit event, however.
                                               
                                                form.addEventListener( "submit",
                                                        IdentitySelector.onFormSubmit, false);
                                                break;
                                        }
                                       
                                        form = form.parentNode;
                                }
                               
                                // Set this as the default information card target element
                               
                                doc.__identityselector__.targetElem = objElem;
                               
                                // Log the event
                               
                                IdentitySelector.logMessage( "onICardObjectLoaded",
                                        "Processed ICard object.");
                                       
                                // Mark the object as 'processed'
                                       
                                objElem.__processed = true;
                        }
                }
                catch( e)
                {
                        if( gDebugMode)
                        {
                                IdentitySelector.reportError( "onICardObjectLoaded", e);
                        }
                }
               
                if( !("notificationBoxHidden" in doc.__identityselector__))
                {
                        IdentitySelector.onHideNotificationBox();
                        doc.__identityselector__.notificationBoxHidden = true;
                }
        },
       
        // ***********************************************************************
        // Method: onICardElementLoaded
        // ***********************************************************************
       
        onICardElementLoaded : function( event)
        {
                var target = event ? event.target : this;
                var doc;
               
                if( target.wrappedJSObject)
                {
                        target = target.wrappedJSObject;
                }
               
                doc = target.ownerDocument;
               
                try
                {
                        var icardElem = target;
                        var form = icardElem;
                       
                        if( icardElem.__processed == true)
                        {
                                IdentitySelector.logMessage( "onICardElementLoaded",
                                        "ICard element has already been processed.");
                        }
                        else
                        {
                                // Override the value getter
                               
                                delete icardElem[ "value"];
                               
                                icardElem.__defineGetter__( "value",
                                        doc.__identityselector__.valueGetter);
                               
                                // Intercept all submit actions if the element is embedded within
                                // a parent form
                               
                                while( form != null)
                                {
                                        if( form.tagName != undefined && form.tagName == "FORM")
                                        {
                                                // The form submit method has been hooked.  We still need
                                                // to register for the submit event, however.
                                               
                                                form.addEventListener( "submit",
                                                        IdentitySelector.onFormSubmit, false);
                                                break;
                                        }
                                       
                                        form = form.parentNode;
                                }
                               
                                // Set this as the default information card element
                               
                                doc.__identityselector__.targetElem = icardElem;
                               
                                // Log the event
                               
                                IdentitySelector.logMessage( "onICardElementLoaded",
                                        "Processed ICard element.");
                                       
                                // Mark the element as 'processed'
                               
                                icardElem.__processed = true;
                        }
                }
                catch( e)
                {
                        if( gDebugMode)
                        {
                                IdentitySelector.reportError( "onICardElementLoaded", e);
                        }
                }
        },
       
        // ***********************************************************************
        // Method: onCallIdentitySelector
        // ***********************************************************************
       
        onCallIdentitySelector : function( event)
        {
                var target = event ? event.target : this;
                var doc;
                var dataObj;
                var result;
                var identObject;
                var selectorClass;
                var optionalClaims = null;
                var requiredClaims = null;
                var currentTime = new Date();
                var iDOSThreshold = 15;
               
                // Log the operation
               
                IdentitySelector.logMessage( "onCallIdentitySelector",
                        "Identity selector invoked.");
                       
                // Configure the target
               
                if( target.wrappedJSObject)
                {
                        target = target.wrappedJSObject;
                }
               
                doc = target;
                identObject = doc.__identityselector__;
                dataObj = identObject.data;
               
                // Log information about the browser and platform
               
                IdentitySelector.logPlatformInfo();
               
                // Request a token from the selector
                       
                try
                {
                        if( identObject.targetElem != null)
                        {
                                IdentitySelector.setParamsFromElem( doc, identObject.targetElem);
                                delete identObject.targetElem[ "token"];
                        }
                        else
                        {
                                IdentitySelector.throwError( "onCallIdentitySelector",
                                        "Invalid target element.");
                        }
                       
                        // Process parameters
                       
                        if( identObject.targetElem.tagName == "OBJECT")
                        {
                                var objElem = identObject.targetElem;
                               
                                // Process parameters
                               
                                if( objElem.childNodes.length)
                                {
                                        IdentitySelector.logMessage( "onCallIdentitySelector",
                                                "Found " + objElem.childNodes.length +
                                                " object child elements");
                                               
                                        // Process the parameter values
                                       
                                        for( each in objElem.childNodes)
                                        {
                                                var childNode = objElem.childNodes[ each];
                                       
                                                if( childNode.tagName != undefined)
                                                {
                                                        IdentitySelector.logMessage( "onCallIdentitySelector",
                                                                "Processing object child element = " +
                                                                childNode.tagName);
       
                                                        if( childNode.tagName == "PARAM")
                                                        {
                                                                IdentitySelector.extractParameter( childNode, dataObj);
                                                        }
                                                }
                                        }
                                }
                        }
                        else
                        {
                                var icardElem = identObject.targetElem;
                               
                                for( var i = 0; i < icardElem.attributes.length; i++)
                                {
                                        IdentitySelector.extractParameter(
                                                icardElem.attributes[ i], dataObj);
                                }
                               
                                // Process the child nodes
                               
                                var addElems = icardElem.getElementsByTagName( "IC:ADD");
                               
                                for( var i = 0; i < addElems.length; i++)
                                {
                                        var addElem = addElems[ i];
                                        var claimTypeAttr = addElem.attributes[ "claimType"];
                                        var optionalAttr = addElem.attributes[ "optional"];
                                        var isOptional = false;
                                       
                                        if( claimTypeAttr != null)
                                        {
                                                if( optionalAttr != null)
                                                {
                                                        if( optionalAttr.value == "true")
                                                        {
                                                                isOptional = true;
                                                        }
                                                }
                                       
                                                if( isOptional)
                                                {
                                                        if( optionalClaims)
                                                        {
                                                                optionalClaims += " " + claimTypeAttr.value;
                                                        }
                                                        else
                                                        {
                                                                optionalClaims = claimTypeAttr.value;
                                                        }
                                                }
                                                else
                                                {
                                                        if( requiredClaims)
                                                        {
                                                                requiredClaims += " " + claimTypeAttr.value;
                                                        }
                                                        else
                                                        {
                                                                requiredClaims = claimTypeAttr.value;
                                                        }
                                                }
                                               
                                                IdentitySelector.logMessage( "onCallIdentitySelector",
                                                        "claimType = " + claimTypeAttr.value);
                                        }
                                }
                               
                                if( optionalClaims)
                                {
                                        dataObj.optionalClaims = optionalClaims;
                                        IdentitySelector.logMessage( "onCallIdentitySelector",
                                                "optionalClaims = " + optionalClaims);
                                }
                               
                                if( requiredClaims)
                                {
                                        dataObj.requiredClaims = requiredClaims;
                                        IdentitySelector.logMessage( "onCallIdentitySelector",
                                                "requiredClaims = " + requiredClaims);
                                }
                        }
                       
                        // Get the selector class
                       
                        if( (selectorClass = IdentitySelector.getStringPref(
                                "identityselector", "selector_class")) == null)
                        {
                                selectorClass = "NoIdentitySelector";
                        }
                       
                        getSecurityToken = eval( selectorClass).getSecurityToken;
                       
                        if( typeof getSecurityToken != "function")
                        {
                                selectorClass = "NoIdentitySelector";
                                getSecurityToken = eval( selectorClass).getSecurityToken;
                        }
                       
                        // Set the token to null
                       
                        identObject.targetElem.token = null;
                       
                        // Check for a denial-of-service attack
                       
                        if( gDisableStartTime != null)
                        {
                                // If there haven't been any token requests in the last 15 seconds,
                                // the selector can be re-enabled
                               
                                if( gDisableStartTime.getTime() +
                                         (iDOSThreshold * 1000) < currentTime.getTime())
                                {
                                        gDisableStartTime = null;
                                        gLastFailedGetTokenDate = null;
                                       
                                        IdentitySelector.logMessage( "onCallIdentitySelector",
                                                "Selector re-enabled.");
                                }
                                else
                                {
                                        // Re-start the "disabled" timer
                                       
                                        gDisableStartTime = new Date();
                                }       
                        }
                        else
                        {
                                // Verify that it has been at least 10 seconds since the last
                                // time a token was requested
                               
                                if( gLastFailedGetTokenDate != null)
                                {
                                        if( currentTime.getTime() <
                                                gLastFailedGetTokenDate.getTime() + (iDOSThreshold * 1000))
                                        {
                                                if( !window.confirm( "The identity selector was launched " +
                                                        "less than " + iDOSThreshold + " seconds ago.  This " +
                                                        "can sometimes be caused by a site attempting a " +
                                                        "denial-of-service attack.  Select 'Cancel' to " +
                                                        "temporarily disable the identity selector or 'OK' " +
                                                        "to allow the selector to launch."))
                                                {
                                                        currentTime = gDisableStartTime = new Date();
                                                }
                                        }
                                }
                        }
                               
                        // Invoke the selector
                       
                        if( gDisableStartTime != null)
                        {
                                IdentitySelector.logMessage( "onCallIdentitySelector",
                                        "Launching the selector has been disabled");
                                       
                                doc.getElementsByTagName( "HTML")[ 0].innerHTML =
                                        "<body>" +
                                        "<h2>The identity selector has been disabled.</h2>" +
                                        "<blockquote>The selector was disabled to prevent a " +
                                        "denial-of-service attack.  It will be re-enabled " +
                                        "automatically in " +
                                                ((((gDisableStartTime.getTime() + (iDOSThreshold * 1000)) -
                                                currentTime.getTime()) / 1000) | 0) +
                                         " second(s).</blockquote>" +
                                         "</body>";
                        }
                        else
                        {
                                gLastFailedGetTokenDate = null;
                               
                                if( (identObject.targetElem.token =
                                        getSecurityToken( dataObj)) == null)
                                {
                                        gLastFailedGetTokenDate = new Date();
                                }
                        }
                }
                catch( e)
                {
                        IdentitySelector.throwError( "onCallIdentitySelector", e);
                }
        },
       
        // ***********************************************************************
        // Method: onIdentitySelectorAvailable
        // ***********************************************************************
       
        onIdentitySelectorAvailable : function( event)
        {
                var target = event ? event.target : this;
               
                if( target.wrappedJSObject)
                {
                        target = target.wrappedJSObject;
                }

                target.IdentitySelectorAvailable = true;
        },
       
        // ***********************************************************************
        // Method: onHideNotificationBox
        // ***********************************************************************
       
        onHideNotificationBox : function( event)
        {
                try
                {
                        var browser = getBrowser();
                       
                        if( typeof( browser.getNotificationBox) == "function")
                        {
                                browser.getNotificationBox().notificationsHidden = true;
                        }
                }
                catch( e)
                {
                        alert( e);
                }
        },
       
        // ***********************************************************************
        // Method: getEmbeddedICardObject
        // ***********************************************************************
       
        getEmbeddedICardObject : function( targetElem)
        {
                var objElems = targetElem.getElementsByTagName( "OBJECT");
               
                for( var i = 0; i < objElems.length; i++)
                {
                        var objElem = objElems[ i];
                        var objTypeStr = objElem.getAttribute( "TYPE");
                       
                        if( (objTypeStr != null &&
                                        objTypeStr.toLowerCase() == nsICardObjTypeStr) ||
                                objElem._type == nsICardObjTypeStr)
                        {
                                return( objElem);
                        }
                }
               
                return( null);
        },
       
        // ***********************************************************************
        // Method: getEmbeddedICardElement
        // ***********************************************************************
       
        getEmbeddedICardElement : function( targetElem)
        {
                var icardElems = targetElem.getElementsByTagName( "IC:INFORMATIONCARD");
               
                if( icardElems.length)
                {
                        return( icardElems[ 0]);
                }
               
                return( null);
        },
       
        // ***********************************************************************
        // Method: getTrustedCertList
        // ***********************************************************************
       
        getTrustedCertList : function()
        {
                var certListBuf = "";
               
                try
                {
                        var certDb = Components.classes[ nsX509CertDB].
                                                                getService( nsIX509CertDB);
                        var certCache = Components.classes[ nsNSSCertCache].
                                                                createInstance( nsINSSCertCache);
                        var caTreeView;
                       
                        // Load all of the certificates
                 
                        certCache.cacheAllCerts();
                       
                        // Create a tree view that includes all of the trusted CA roots
                       
                        caTreeView = Components.classes[ nsCertTree].
                                                                createInstance( nsICertTree);
                        caTreeView.loadCertsFromCache( certCache,
                                                                nsIX509Cert.CA_CERT | nsIX509Cert.SERVER_CERT);
                       
                        // Iterate through the items in the tree view
                       
                        for( var iLoop = 0; iLoop < caTreeView.rowCount; iLoop++)
                        {
                                var cert = caTreeView.getCert( iLoop);
                               
                                if( cert == null)
                                {
                                        // Not all rows in the tree view are certificates.  Some are
                                        // display tags, placeholders, etc.
                                       
                                        continue;
                                }
                               
                                // Get the raw DER encoding of the certificate
                               
                                var derLen = {};
                                var rawDer = cert.getRawDER( derLen);
                               
                                // Convert the raw DER to a hex string
                               
                                var derBytes = "";
                                       
                                for( var iSubLoop = 0; iSubLoop < rawDer.length; iSubLoop++)
                                {
                                        derBytes = derBytes + String.fromCharCode( rawDer[ iSubLoop]);
                                }
                               
                                certListBuf += derBytes;
                        }
                }
                catch( e)
                {
                        alert( e);
                }
               
                return( certListBuf);
        },

        // ***********************************************************************
        // Method: arrayToHexStr
        // ***********************************************************************
       
        arrayToHexStr : function( data)
        {
                var newStr = "";
               
                for( var i in data)
                {
                        newStr += ( "0" + data.charCodeAt( i).toString( 16)).slice( -2);
                }
               
                return( newStr);
        },

        // ***********************************************************************
        // Method: generateTmpFilePath
        // ***********************************************************************
       
        generateTmpFilePath : function()
        {
                var file = Components.classes[
                                "@mozilla.org/file/directory_service;1"].
                                getService( Components.interfaces.nsIProperties).
                                get( "TmpD", Components.interfaces.nsIFile);
                var ch = Components.classes[
                                "@mozilla.org/security/hash;1"].
                                createInstance( Components.interfaces.nsICryptoHash);
                var converter = Components.classes[
                                "@mozilla.org/intl/scriptableunicodeconverter"].
                                createInstance( Components.interfaces.nsIScriptableUnicodeConverter);
                var result = {};
                var data;
                var now = new Date();
                var datestr = (Date.UTC( now.getFullYear(),
                                now.getMonth(), now.getDate(), now.getHours(),
                                now.getMinutes(), now.getSeconds())).toString( 10);
                var hash;
               
                converter.charset = "UTF-8";
                data = converter.convertToByteArray( datestr, result);
               
                ch.init( ch.MD5);
                ch.update( data, data.length);
                hash = ch.finish( false);
       
                file.append( IdentitySelector.arrayToHexStr( hash));
                file.createUnique(
                        Components.interfaces.nsIFile.NORMAL_FILE_TYPE, 0x01B6);
       
                return( file.path);
        },
       
        // ***********************************************************************
        // Method: readFile
        // ***********************************************************************
       
        readFile : function( filename)
        {
                try
                {
                        var file = Components.classes[
                                "@mozilla.org/file/local;1"].createInstance(
                                        Components.interfaces.nsILocalFile);
                                       
                        file.initWithPath( filename);
                       
                        if( file.exists() && file.isReadable())
                        {
                                var fileIStream = Components.classes[
                                        "@mozilla.org/network/file-input-stream;1"].createInstance(
                                                Components.interfaces.nsIFileInputStream);
                                var sis = Components.classes[
                                        "@mozilla.org/scriptableinputstream;1"].createInstance(
                                                Components.interfaces.nsIScriptableInputStream);
                                var fileData = null;
                                               
                                fileIStream.init( file, 0x01, 0x04, 0);
                                sis.init( fileIStream);
                                fileData = sis.read( sis.available());
                                sis.close();
                               
                                return( fileData);
                        }
                        else 
                        {
                                IdentitySelector.reportError( "readFile", "Unable to open file.");
                                return( null);
                        }
                }
                catch( e)
                {
                        IdentitySelector.reportError( "readFile", "Unable to read file.");
                }
               
                return( null);
        },
       
        // ***********************************************************************
        // Method: writeFile
        // ***********************************************************************

        writeFile : function( filename, data)
        {
                try
                {
                        var file = Components.classes[ nsLocalFile].
                                                                createInstance( nsILocalFile);
                        var fileOStream = Components.classes[ nsFileOutputStream].
                                                                createInstance( nsIFileOutputStream);
                                       
                        file.initWithPath( filename);
                        fileOStream.init( file, 0x02 | 0x08 | 0x20, 0x01B6, 0);
                        fileOStream.write( data, data.length);
                        fileOStream.close();
                }               
                catch( e)
                {
                        IdentitySelector.throwError( "writeFile", "Unable to write file.");
                }
        },
       
        // ***********************************************************************
        // Method: hasPref
        // ***********************************************************************
       
        hasPref : function( componentId, prefId)
        {
                var fullPrefId = "extensions." + componentId + "." + prefId;
               
                if( gPrefService.getPrefType( fullPrefId) != gPrefService.PREF_INVALID)
                {
                        return( true);
                }
               
                return( false);
        },
       
        // ***********************************************************************
        // Method: getStringPref
        // ***********************************************************************
       
        getStringPref : function( componentId, prefId)
        {
                var fullPrefId = "extensions." + componentId + "." + prefId;
               
                if( gPrefService.getPrefType( fullPrefId) == gPrefService.PREF_STRING)
                {
                        return( gPrefService.getCharPref( fullPrefId));
                }
               
                return( null);
        },
       
        // ***********************************************************************
        // Method: setStringPref
        // ***********************************************************************
       
        setStringPref : function( componentId, prefId, prefValue)
        {
                var fullPrefId = "extensions." + componentId + "." + prefId;
                gPrefService.setCharPref( fullPrefId, prefValue);
        },
       
        // ***********************************************************************
        // Method: getBooleanPref
        // ***********************************************************************
       
        getBooleanPref : function( componentId, prefId)
        {
                var fullPrefId = "extensions." + componentId + "." + prefId;
               
                if( gPrefService.getPrefType( fullPrefId) == gPrefService.PREF_BOOL)
                {
                        return( gPrefService.getBoolPref( fullPrefId));
                }
               
                return( false);
        },
       
        // ***********************************************************************
        // Method: setBooleanPref
        // ***********************************************************************
       
        setBooleanPref : function( componentId, prefId, prefValue)
        {
                var fullPrefId = "extensions." + componentId + "." + prefId;
                gPrefService.setBoolPref( fullPrefId, prefValue);
        },
       
        // ***********************************************************************
        // Method: isWinPlatform
        // ***********************************************************************
       
        isWinPlatform : function()
        {
                return( gbIsWin);
        },
       
        // ***********************************************************************
        // Method: isMacPlatform
        // ***********************************************************************
       
        isMacPlatform : function()
        {
                return( gbIsMac);
        },
       
        // ***********************************************************************
        // Method: isLinuxPlatform
        // ***********************************************************************
       
        isLinuxPlatform : function()
        {
                return( gbIsLinux);
        },
       
        // ***********************************************************************
        // Method: forceRefresh
        // ***********************************************************************
       
        forceRefresh : function()
        {
                try
                {
                        window.open( "javascript:setTimeout( 'window.close();', 100);", "",
                                "toolbar=0,scrollbars=0,location=0,statusbar=0," +
                                "menubar=0,resizable=0,width=1,height=1,modal=yes");
                }
                catch( e)
                {
                }
        }
};

// **************************************************************************
// Desc:
// **************************************************************************

var NoIdentitySelector =
{
        // ***********************************************************************
        // Method: getSecurityToken
        // ***********************************************************************
       
        getSecurityToken : function( data)
        {
                IdentitySelector.reportError( "getSecurityToken",
                        "Unable to locate an identity selector.  " +
                        "Please make sure one is installed.");
               
                return( null);
        }
};

// **************************************************************************
// Desc: Startup and shutdown
// **************************************************************************

try
{
	IdentitySelector.logMessage( "IdentitySelector", "start");
        window.addEventListener( "load",
                IdentitySelector.onInstall, false);
               
        window.addEventListener( "unload",
                IdentitySelector.onUninstall, false);
}
catch( e)
{
        IdentitySelector.reportError( "window.addEventListener", e);
}