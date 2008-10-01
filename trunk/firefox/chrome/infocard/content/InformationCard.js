//------------------------------------------------------------------------------
// Desc:
// Tabs: 3
//
// Copyright (c) 2007 Novell, Inc. All Rights Reserved.
//
// This program and the accompanying materials are made available 
// under the terms of the Eclipse Public License v1.0 which
// accompanies this distribution, and is available at 
// http://www.eclipse.org/legal/epl-v10.html
//
// To contact Novell about this file by physical or electronic mail, 
// you may find current contact information at www.novell.com.
//
// $Id$
//
// Author: Andrew Hodgkinson <ahodgkinson@novell.com>
// Contributor: Axel Nennker http://ignisvulpis.blogspot.com/
//
//------------------------------------------------------------------------------

// **************************************************************************
// Desc: Globals
// **************************************************************************

var gPrefService = Components.classes[ 
		"@mozilla.org/preferences-service;1"].
			getService( Components.interfaces.nsIPrefBranch);

var gNavPlat = navigator.platform;
var gbIsWin = ((gNavPlat.indexOf( "Win") > -1) ? true : false);
var gbIsMac = ((gNavPlat.indexOf( "Mac") > -1) ? true : false);
var gbIsLinux = ((gNavPlat.indexOf( "Linux") > -1) ? true : false);

/****************************************************************************
Desc:
****************************************************************************/

function nsResolver(prefix) {
	  var ns = {
	    'xrds' : 'xri://$xrds',
	    'xrd': 'xri://$XRD*($v*2.0)'
	  };
	  return ns[prefix] || null;
	}

var listObserver = {
		onDragOver: function (event, flavour, session) {
			IdentitySelector.logMessage("onDragOver: " + flavour.contentType);
		},

		onDrop : function (evt, transferData, session) {
			IdentitySelector.logMessage("onDrop: " + transferData.data);
//			event.target.setAttribute("value",transferData.data); 
		},
		getSupportedFlavours : function () { 
			var flavours = new FlavourSet(); 
//			flavours.appendFlavour("text/unicode"); 
			flavours.appendFlavour("application/x-informationcard+id"); 
			return flavours; 
		} 
}

/****************************************************************************
Desc:
****************************************************************************/

function xrdsListener(doc, hrefStr)  
{
		this.doc = doc;
		this.hrefStr = hrefStr;
		
		this.onError =  function(error) {
			IdentitySelector.logMessage("xrdsListener:onError", "error=" + error);
		}
		
		this.onReady = function(xrds) {
			try {
				var elts = xrds.getElementsByTagName("Service");
				for (var i=0; i<elts.length; i++) {
					var type = "" + elts[i].getElementsByTagName("Type")[0].firstChild.nodeValue + "";
					if (type.indexOf("http://infocardfoundation.org/policy/1.0/login") == 0) {
						var uri = "" + elts[i].getElementsByTagName("URI")[0].firstChild.nodeValue ;
						doc.__identityselector__.icLoginPolicy = uri;
						IdentitySelector.logMessage("xrdsListener:onReady", "IC Login Service Policy: " + doc.__identityselector__.icLoginPolicy);
						IdentitySelector.retrieveIcLoginServicePolicy(doc, doc.__identityselector__.icLoginPolicy);
					} else {
						if (type.indexOf("http://infocardfoundation.org/service/1.0/login") == 0) {
							var uri = "" + elts[i].getElementsByTagName("URI")[0].firstChild.nodeValue ;
							doc.__identityselector__.icLoginService = uri;
							IdentitySelector.logMessage("xrdsListener:onReady", "IC Login Service: " + doc.__identityselector__.icLoginService);
						} else {
							IdentitySelector.logMessage("xrdsListener:onReady", "Service: type=" + type + ":" + typeof(type) + " URI=" + elts[i].getElementsByTagName("URI")[0].firstChild.nodeValue);
						}
					}
				}
//				for (var i in xrds) {
//					IdentitySelector.logMessage("xrdsListener:onReady", "i=" + i + " type=" + typeof(i));
//				}
				var response = new XML (Components.classes['@mozilla.org/xmlextras/xmlserializer;1'].createInstance (Components.interfaces.nsIDOMSerializer).serializeToString(xrds.documentElement));
				doc.__identityselector__.xrds = response;
				IdentitySelector.logMessage("xrdsListener:onReady", "response=" + response);
//				var elts = xrds.evalutate('Service', xrds, nsResolver, XPathResult.UNORDERED_NODE_ITERATOR_TYPE, null);
			} catch(e) {
				IdentitySelector.logMessage("xrdsListener:onReady", "Error: " + e);
			}
		}

}

/****************************************************************************
Desc:
****************************************************************************/

function icLoginServiceListener(doc, hrefStr)  
{
		this.doc = doc;
		this.hrefStr = hrefStr;
		
		this.onError =  function(error) {
			IdentitySelector.logMessage("icLoginServiceListener:onError", "error=" + error);
		}
		
		this.onReady = function(xrds) {
			try {
//				var elts = xrds.getElementsByTagName("Service");
//				for (var i=0; i<elts.length; i++) {
//					var type = "" + elts[i].getElementsByTagName("Type")[0].firstChild.nodeValue + "";
//					if (type.indexOf("http://infocardfoundation.org/policy/1.0/login") == 0) {
//						var uri = "" + elts[i].getElementsByTagName("URI")[0].firstChild.nodeValue ;
//						doc.__identityselector__.icLoginPolicy = uri;
//						IdentitySelector.logMessage("xrdsListener:onReady", "IC Login Service Policy: " + doc.__identityselector__.icLoginPolicy);
//						IdentitySelector.retrieveIcLoginServicePolicy(doc, doc.__identityselector__.icLoginPolicy);
//					} else {
//						if (type.indexOf("http://infocardfoundation.org/service/1.0/login") == 0) {
//							var uri = "" + elts[i].getElementsByTagName("URI")[0].firstChild.nodeValue ;
//							doc.__identityselector__.icLoginService = uri;
//							IdentitySelector.logMessage("icLoginServiceListener:onReady", "IC Login Service: " + doc.__identityselector__.icLoginService);
//						} else {
//							IdentitySelector.logMessage("icLoginServiceListener:onReady", "Service: type=" + type + ":" + typeof(type) + " URI=" + elts[i].getElementsByTagName("URI")[0].firstChild.nodeValue);
//						}
//					}
//				}
//				for (var i in xrds) {
//					IdentitySelector.logMessage("xrdsListener:onReady", "i=" + i + " type=" + typeof(i));
//				}
				var response = new XML (Components.classes['@mozilla.org/xmlextras/xmlserializer;1'].createInstance (Components.interfaces.nsIDOMSerializer).serializeToString(xrds.documentElement));
				doc.__identityselector__.icLoginService = response;
				IdentitySelector.logMessage("icLoginServiceListener:onReady", "response=" + response);
//				var elts = xrds.evalutate('Service', xrds, nsResolver, XPathResult.UNORDERED_NODE_ITERATOR_TYPE, null);
			} catch(e) {
				IdentitySelector.logMessage("icLoginServiceListener:onReady", "Error: " + e);
			}
		}

}

// **************************************************************************
// Desc:
// **************************************************************************

var ICProgressListener =
{
	QueryInterface : function( aIID)
	{
		if( aIID.equals( Components.interfaces.nsIWebProgressListener) ||
			 aIID.equals(Components.interfaces.nsISupportsWeakReference) ||
			 aIID.equals(Components.interfaces.nsISupports))
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
					
					IdentitySelector.processICardItems( 
						aProgress.DOMWindow.document, true);
					IdentitySelector.processHtmlLinkElements( aProgress.DOMWindow.document, true); // process "LINK rel" too
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
		// where we add a global submit intercept.
		
		try
		{
			IdentitySelector.runInterceptScript( aProgress.DOMWindow.document);
		}
		catch( e)
		{
			IdentitySelector.reportError( "onLocationChange", e);
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
	
	onSecurityChange : function(aProgress, aRequest, aState)
	{
		IdentitySelector.logMessage( "onSecurityChange", "state=" + aState);
		return IdentitySelector.onSecurityChange(aProgress, aRequest, aState);
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
	disabled : false,
	
	// Mode strings used to control CSS display
	IDENTITY_MODE_IDENTIFIED       : "verifiedIdentity", // High-quality identity information
	IDENTITY_MODE_DOMAIN_VERIFIED  : "verifiedDomain",   // Minimal SSL CA-signed domain verification
	IDENTITY_MODE_UNKNOWN          : "unknownIdentity",  // No trusted identity information
	
	setMode : function(doc, newMode) {
		IdentitySelector.runInterceptScript(doc);
		if( doc.wrappedJSObject)
		{
			doc = doc.wrappedJSObject;
		}

		doc.__identityselector__.mode = newMode;
	},
	
	getMode : function(doc) {
		IdentitySelector.runInterceptScript(doc);
		if( doc.wrappedJSObject)
		{
			doc = doc.wrappedJSObject;
		}
		return doc.__identityselector__.mode;
	},
	
	onSecurityChange : function(aProgress, aRequest, aState)
	{
		var doc = aProgress.DOMWindow.document;
	    if (aState & Components.interfaces.nsIWebProgressListener.STATE_IDENTITY_EV_TOPLEVEL) {
	        this.setMode(doc, this.IDENTITY_MODE_IDENTIFIED);
			IdentitySelector.logMessage( "IdentitySelector", " onSecurityChange state=STATE_IDENTITY_EV_TOPLEVEL location=" + doc.location.href);
	    } else if (aState & Components.interfaces.nsIWebProgressListener.STATE_SECURE_HIGH) {
	        this.setMode(doc, this.IDENTITY_MODE_DOMAIN_VERIFIED);
			IdentitySelector.logMessage( "IdentitySelector", " onSecurityChange state=STATE_SECURE_HIGH location=" + doc.location.href);
	    }  else {
	        this.setMode(doc, this.IDENTITY_MODE_UNKNOWN);
			IdentitySelector.logMessage( "IdentitySelector", " onSecurityChange state=IDENTITY_MODE_UNKNOWN location=" + doc.location.href);
	    }

		return( 0);
	},
	
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
		var consoleService = Components.classes[ "@mozilla.org/consoleservice;1"].
			getService( Components.interfaces.nsIConsoleService);
			
		consoleService.logStringMessage( "IdentitySelector:" + 
			location + ": " + message);
	},
	
	// ***********************************************************************
	// Method: dumpConsoleToLogFile
	// ***********************************************************************
	
	dumpConsoleLogToFile : function( filePath)
	{
		var consoleService = Components.classes[ 
			"@mozilla.org/consoleservice;1"].getService( 
			Components.interfaces.nsIConsoleService);
		var messageArray = {};
		var messageCount = {};
		var logBuffer = "";
		
		try
		{
			consoleService.getMessageArray( messageArray, messageCount);
			
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

	
	sameSchemeAndDomain : function(ownerDocument, htmlDoc) 
	{
		IdentitySelector.logMessage( "sameSchemeAndDomain", "ownerDocument.location.href=" + ownerDocument.location.href);
		topScheme = ownerDocument.location.protocol;
		topDomain = ownerDocument.location.host;
		// TODO this should go up to the top. Currently this code supports only only level deep nesting.

		IdentitySelector.logMessage( "sameSchemeAndDomain", " topURL:" + ownerDocument.location.href);

		var subWindowScheme = "";
		if (htmlDoc.location == undefined) { // htmlDoc is a string
			IdentitySelector.logMessage( "sameSchemeAndDomain", " subWindowURL:" + htmlDoc);
			// it is a string not a doc
			var i = htmlDoc.indexOf(':');
			if (i != -1) {
				subWindowScheme = htmlDoc.substring(0,i+1); // include the colon
			}
		} else {
			IdentitySelector.logMessage( "sameSchemeAndDomain", " subWindowURL:" + htmlDoc.location.href);
			subWindowScheme = htmlDoc.location.protocol;
		}
		IdentitySelector.logMessage( "sameSchemeAndDomain", " subWindowDomain:" + subWindowDomain);
		if (subWindowScheme == topScheme) {
			IdentitySelector.logMessage( "sameSchemeAndDomain", " topDomain:" + topDomain);
			var subWindowDomain = "";
			if (htmlDoc.location == undefined) {
				// it is a string not a doc
				var i = htmlDoc.indexOf("//");
				if (i != -1) {
					var rest = htmlDoc.substr(i+2);
					i = rest.indexOf("/");
					if (i != -1) {
						subWindowDomain = rest.substring(0, i);
					} else {
						subWindowDomain = rest;
					}
				}
			} else {
				subWindowDomain = htmlDoc.location.host;
			}
			IdentitySelector.logMessage( "sameSchemeAndDomain", " subWindowDomain:" + subWindowDomain);
			if (subWindowDomain == topDomain) {
				return true;
			} else {
				IdentitySelector.logMessage( "sameSchemeAndDomain", "domains do not match. " + subWindowDomain + "!=" + topDomain);
			}
		} else {
			IdentitySelector.logMessage( "sameSchemeAndDomain", "schemes do not match. " + subWindowScheme + "!=" + topScheme);
		}
		return false;
	},
	
	// ***********************************************************************
	// Method: onInstall
	// ***********************************************************************
	
	onInstall : function( event)
	{
		IdentitySelector.logMessage("onInstall", "start");

		var handlerAlreadyInstalled = false;
		
		{
			//var prefService = Components.classes[ 
			//	"@mozilla.org/preferences-service;1"].
			//	getService( Components.interfaces.nsIPrefBranch);
			this.disabled = gPrefService.getBoolPref( "identityselector.disabled");
			if (this.disabled == true) {
				IdentitySelector.logMessage("onInstall", " ID selector is disabled. Exiting");
				return;
			}
		}
		
		var htmlDoc;
		
//		IdentitySelector.logMessage("onInstall", "document.contentType=" + document.contentType);
//		IdentitySelector.logMessage("onInstall", "window.document.contentType=" + window.document.contentType);
//		IdentitySelector.logMessage("onInstall", "window.document.contentType=" + window.document.contentType);
		if (event.originalTarget instanceof HTMLDocument) {
			htmlDoc = event.originalTarget;
			IdentitySelector.logMessage("onInstall", "HTML type:" + event.originalTarget.contentType + 
					"\nlocation=" + htmlDoc.location.href);
		} else {
			IdentitySelector.logMessage("onInstall", "this no HTML. Exiting onInstall.");
			window.removeEventListener( "load", 
					IdentitySelector.onInstall, true);
			return;
		}
		try
		{
			// Remove the load event listener
			
			window.removeEventListener( "load", 
				IdentitySelector.onInstall, true);
				
			// Determine if a plug-in has registered to handle
			// information cards
			if( navigator.mimeTypes && navigator.mimeTypes.length)
			{
				var mimeHandler = navigator.mimeTypes[ 
											"application/x-informationcard"];
				if( mimeHandler && mimeHandler.enabledPlugin)
				{
					handlerAlreadyInstalled = true;
				}
			}

			if (!handlerAlreadyInstalled) {
				var doc = htmlDoc;
				IdentitySelector.runInterceptScript(doc);
				if( doc.wrappedJSObject)
				{
					doc = doc.wrappedJSObject;
				}
				if (!(doc.__identityselector__.IdentitySelectorAvailable == undefined)) {
					handlerAlreadyInstalled = (doc.__identityselector__.IdentitySelectorAvailable == true);
				} else {
					doc.__identityselector__.IdentitySelectorAvailable = true;
				}
			}

//			// Determine if another extension has registered to handle
//			// information cards
//			if( !handlerAlreadyInstalled)
//			{
//				var event = document.createEvent( "Event");
//
//				event.initEvent( "IdentitySelectorAvailable", true, true);
//				window.dispatchEvent( event);
//				
//				if( window.IdentitySelectorAvailable == true)
//				{
//					handlerAlreadyInstalled = true;
//				}
//			}
			
			if( !handlerAlreadyInstalled)
			{
				var secureFrameHandling = false;
				{
					var htmlDocWindow = null;
					if (!(htmlDoc.defaultView == undefined)) {
						htmlDocWindow = htmlDoc.defaultView;
						IdentitySelector.logMessage( "onInstall", " htmlDoc.defaultView.location.href=" + htmlDoc.defaultView.location.href);
						var parent = htmlDocWindow.frameElement; 
						if (parent != null) {
							if (parent.wrappedJSObject) {
								parent = parent.wrappedJSObject;
							}
							if (parent instanceof HTMLIFrameElement) {
								var ownerDocument = parent.ownerDocument;
								secureFrameHandling = IdentitySelector.sameSchemeAndDomain(ownerDocument, htmlDoc);
								IdentitySelector.logMessage( "onInstall", " secureFrameHandling=" + secureFrameHandling + 
										"\nlocation=" + htmlDoc.location.href + " topLocation=" + ownerDocument.location.href);
							} else {
								IdentitySelector.logMessage( "onInstall", "parent=" + parent);
							}
						} else {
							secureFrameHandling = true;
							IdentitySelector.logMessage( "onInstall", " secureFrameHandling=" + secureFrameHandling + 
									"\nlocation=" + htmlDoc.location.href + " no parent");
						}
					} else {
						IdentitySelector.logMessage( "onInstall", " document.defaultView is not defined" );
					}
					
				}
				
				{
					var doc = htmlDoc;
					IdentitySelector.runInterceptScript(doc);
					if( doc.wrappedJSObject)
					{
						doc = doc.wrappedJSObject;
					}
					doc.__identityselector__.secureFrameHandling = secureFrameHandling;
				}
				
				// Add event handlers.  The optional fourth parameter to
				// addEventListener indicates that the listener is willing to
				// accept untrusted events (such as those generated by web
				// content).
				
				window.addEventListener( "IdentitySelectorAvailable",
					IdentitySelector.onIdentitySelectorAvailable, false, true);
		
				window.addEventListener( "CallIdentitySelector",
					IdentitySelector.onCallIdentitySelector, false, true);
			
				window.addEventListener( "DisableIdentitySelector",
					IdentitySelector.onDisableIdentitySelector, false, false);
			
				window.addEventListener( "EnableIdentitySelector",
					IdentitySelector.onEnableIdentitySelector, false, false);
			
				window.addEventListener( "ICHideNotificationBox",
					IdentitySelector.onHideNotificationBox, false, false);
			
				window.addEventListener( "ICObjectLoaded",
					IdentitySelector.onICardObjectLoaded, false, false);
		
				window.addEventListener( "ICElementLoaded",
					IdentitySelector.onICardElementLoaded, false, false);
					
				window.addEventListener( "ICFormSubmit",
					IdentitySelector.onFormSubmit, false, true);
					
				window.addEventListener( "ICGetTokenValue",
					IdentitySelector.onGetTokenValue, false, true);
					
				window.addEventListener( "DOMContentLoaded", 
					IdentitySelector.onContentLoaded, false, false);
					
//				document.documentElement.addEventListener( "DOMSubtreeModified", 
//					IdentitySelector.onSomethingChanged, false, false);
//					
//				document.documentElement.addEventListener( "DOMNodeInsertedIntoDocument", 
//					IdentitySelector.onSomethingChanged, false, false);
//					
//				document.documentElement.addEventListener( "DOMAttrModified", 
//					IdentitySelector.onSomethingChanged, false, false);
//					
//				document.documentElement.addEventListener( "DOMNodeInserted", 
//					IdentitySelector.onSomethingChanged, false, false);
					
				// Add a progress listener
					
				window.getBrowser().addProgressListener( ICProgressListener, 
					Components.interfaces.nsIWebProgress.NOTIFY_ALL);
			}
			else
			{
				IdentitySelector.logMessage( "onInstall", 
					"Another identity selector is already installed.\nlocation=" + htmlDoc.location.href);
			}
		}
		catch( e)
		{
			IdentitySelector.throwError( "onInstall", e);
		}
	},
	
	// ***********************************************************************
	// Method: onEnableIdentitySelector
	// ***********************************************************************
	
	onEnableIdentitySelector : function( event)
	{
		IdentitySelector.logMessage("onEnableIdentitySelector", " received");
		this.disabled = false;
		//onInstall(event);
	},
	
	// ***********************************************************************
	// Method: onDisableIdentitySelector
	// ***********************************************************************
	
	onDisableIdentitySelector : function( event)
	{
		IdentitySelector.logMessage("onDisableIdentitySelector", " received");
		this.disabled = true;
		//onUninstall(event);
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
				
			window.removeEventListener( "DOMContentLoaded", 
				IdentitySelector.onContentLoaded, false);
				
//			document.removeEventListener( "DOMNodeInserted", 
//					IdentitySelector.onSomethingChanged, false);
//					
//			document.removeEventListener( "DOMSubtreeModified", 
//					IdentitySelector.onSomethingChanged, false);
//					
//			document.removeEventListener( "DOMNodeInsertedIntoDocument", 
//					IdentitySelector.onSomethingChanged, false);
//					
//			document.removeEventListener( "DOMAttrModified", 
//					IdentitySelector.onSomethingChanged, false);

			// Remove progress listener
			var browser = window.getBrowser(); 
			if (browser != undefined) {
				browser.removeProgressListener( ICProgressListener);
			} 
		}
		catch( e)
		{
			IdentitySelector.throwError( "onUninstall", e);
		}
	},
	
	// ***********************************************************************
	// Method: form submit interceptor
	// ***********************************************************************
	
	interceptor : function(e)
	{
		IdentitySelector.logMessage("IdentitySelector.interceptor", " start");
		var frm = e ? e.target : this;
		var dcmt = frm.ownerDocument;
		var evnt = dcmnt.createEvent( 'Event');
	    evnt.initEvent( 'ICFormSubmit', true, true);
	    this.dispatchEvent( evnt);
	    dcmnt.__identityselector__.chainSubmit.apply( this);
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
		
	onSomethingChanged : function(event)
	{
		if (this.disabled == true) {
			IdentitySelector.logMessage("onSomethingChanged", " ID selector is disabled. Exiting");
			return;
		}

	    if (event) {
		 var target = event.target;
		
		 if( target.wrappedJSObject)
		 {
			target = target.wrappedJSObject;
		 }
		
		 if (target instanceof HTMLObjectElement) {
		 	this.disabled = gPrefService.getBoolPref( "identityselector.disabled");
			if (this.disabled == true) {
				IdentitySelector.logMessage("onSomethingChanged", " Id selector is disabled. Exiting");
				return;
			}
		 	
     		 var doc;

		     doc = target.ownerDocument;
    		
		     IdentitySelector.logMessage( "onSomethingChanged", "event: " + 
			    event.type + " location: " + doc.location);
    		 
    		 delete target.__processed;
    		 
     		runInterceptScript(doc); // make sure that __identityselector is defined
     		IdentitySelector.processICardItems( doc, true);
		 }
		 
		 if (target instanceof HTMLLinkElement) {
    		runInterceptScript(doc); // make sure that __identityselector is defined
    		IdentitySelector.logMessage( "onSomethingChanged", "HTMLLinkElement " + target.tagName);
		  	IdentitySelector.processHtmlLinkElements( doc, true); // process "LINK rel" too
		 }

	    } else {
	     IdentitySelector.logMessage( "onSomethingChanged", "event " + event);
	    }
	},

	// ***********************************************************************
	// Method: onContentLoaded
	// Notes : This method is called after all of the DOM content has been
	//         loaded, but before any 'onload' events have fired.
	// ***********************************************************************
	
	onContentLoaded : function( event)
	{
		var target = event ? event.target : this;
		var doc = null;
		
		if( target.wrappedJSObject)
		{
			target = target.wrappedJSObject;
		}
		
		doc = target;
		
		doc.addEventListener( "DOMSubtreeModified", 
					IdentitySelector.onSomethingChanged, false, false);
					
		doc.addEventListener( "DOMNodeInsertedIntoDocument", 
					IdentitySelector.onSomethingChanged, false, false);
					
		doc.addEventListener( "DOMAttrModified", 
					IdentitySelector.onSomethingChanged, false, false);
					
		doc.addEventListener( "DOMNodeInserted", 
					IdentitySelector.onSomethingChanged, false, false);
					
		// Make sure the intercept script has been executed in the context
		// of this document
		
		IdentitySelector.runInterceptScript( doc);
		
		// Sanity checks

		try
		{
			// If the intercept script was executed correctly, 
			// __identityselector__ should be defined.
			
			if( doc.__identityselector__ === undefined)
			{
				// Since there are information card items on the page, warn
				// the user that the identity selector was unable to insert
				// itself properly.  This is most likely due to JavaScript being
				// disabled.
				
				var jsEnabled = gPrefService.getBoolPref( "javascript.enabled");
		
				// Warn if JavaScript is disabled
				
				if( !jsEnabled)
				{
					IdentitySelector.reportError( "onContentLoaded", 
						"This page contains information card objects, but " +
						"JavaScript is disabled.  The information card " +
						"selector may not run properly.  To enable, go to " +
						"Preferences->Content->Enable JavaScript.");
				}
				else
				{
					IdentitySelector.reportError( "onContentLoaded", 
						"This page contains information card objects, but " +
						"the information card selector was unable to fully " +
						"process the page.");
				}
				
				// Hide the notification box
			
				IdentitySelector.onHideNotificationBox();
				
				// Done
				
				return;
			}
			
			if (doc.__identityselector__.icLoginService != undefined) {
				var s = "" + doc.__identityselector__.icLoginService;
				var i = s.indexOf(" id=\"");
				if (i != -1) {
					i += 5;
					s = s.substring(i);
					i = s.indexOf("\"");
					if (i != -1){
						s = s.substring(0,i); // s has now value the first id attribute
						var node = doc.getElementById(s);
						if (node) {
							doc.replaceNode(node, doc.__identityselector__.icLoginService);
						} else {
							IdentitySelector.logMessage("onContentLoaded", "Could not find node with id=" + s);
						}
					} else {
						IdentitySelector.logMessage("onContentLoaded", "Could not find closing \" for ' id=' in\n" + doc.__identityselector__.icLoginService);
					}
				} else {
					IdentitySelector.logMessage("onContentLoaded", "Could not find ' id=' in\n" + doc.__identityselector__.icLoginService);
				}
			}
			
			// Process all of the information card objects and elements 
			// in the document
		
			doc.__identityselector__.contentLoaded = true;
			
			if( !doc.__identityselector__.submitIntercepted)
			{
				IdentitySelector.processICardItems( doc, true);
				IdentitySelector.processHtmlLinkElements( doc, true); // process "LINK rel" too
			}					
		}
		catch( e)
		{
			IdentitySelector.reportError( "onContentLoaded", e);
		}
	},
	
	// ***********************************************************************
	// Method: processHtmlLinkElements
	// ***********************************************************************
	
	processHtmlLinkElements : function( doc, dispatchEvents)
	{
		if( doc.wrappedJSObject)
		{
			doc = doc.wrappedJSObject;
		}
		if (this.disabled == true) {
			IdentitySelector.logMessage("processHtmlLinkElements", " ID selector is disabled. Exiting");
			return;
		}
		if( doc.__identityselector__ === undefined) {
			IdentitySelector.runInterceptScript(doc);
		}
		var linkElems = doc.getElementsByTagName( "LINK");
		for( var i = 0; i < linkElems.length; i++) 
		{
			var linkElem = linkElems[ i];
			var relStr = linkElem.getAttribute( "REL");
			if( (relStr != null) && (relStr == "xrds.metadata")) {
				var hrefStr = linkElem.getAttribute( "HREF");
				if (hrefStr == null) {
					continue;
				} else {
					IdentitySelector.logMessage("processHtmlLinkElements: href=", hrefStr);
					if( doc.__identityselector__.xrds === undefined) {
						var data = doc.__identityselector__.data;
						data.xrds_metadata_href = hrefStr;
						
						IdentitySelector.retrieveXrds(doc, hrefStr); // async
					} else {
						IdentitySelector.logMessage("processHtmlLinkElements: already loaded: href=", hrefStr);
					}
					return;
				}
			} else {
				continue;
			}
		}
	},
	
	retrieveXrds : function(doc, hrefStr) {
		IdentitySelector.retrieveX(doc, hrefStr, xrdsListener);
	},
	
	retrieveIcLoginServicePolicy : function(doc, hrefStr) {
		IdentitySelector.retrieveX(doc, hrefStr, icLoginServiceListener);
	},
	
	retrieveX : function(doc, hrefStr, listenerO) {
		try {
			if (typeof(hrefStr) == 'string') {
				var i = hrefStr.indexOf("://");
				if (i == -1) { // it is not an URL. Try to build an URL from the baseURI of the document.
					var baseUri = doc.baseURI;
					if ((baseUri != null) && (baseUri.length > 0)) {
						if ((baseUri.length - 1) == baseUri.lastIndexOf('/')) { // ends with /
							hrefStr = baseUri + hrefStr;
						} else {
							hrefStr = baseUri + '/' + hrefStr;
						}
						IdentitySelector.logMessage("retrieveXrds: href=", hrefStr);
					} // else no baseUri
				} // else its an URL. Go ahead.
			} // else not string but document
			
			var sameSchemeAndDomain = IdentitySelector.sameSchemeAndDomain(doc, hrefStr);
			if (sameSchemeAndDomain == true) {
				var req = Components.classes["@mozilla.org/xmlextras/xmlhttprequest;1"].createInstance();
				req.open('GET', hrefStr, true);
				req.setRequestHeader ('Content-Type', 'text/xml');
				req.overrideMimeType ('text/xml');
				var listener = new listenerO(doc, hrefStr);
				
				req.onreadystatechange = function (aEvent) {
		            if (req.readyState == 4) {
		                if (!req.responseXML) {
		                	listener.onError (req.responseText);
		                    return;
		                }
	
		                if (req.status != 200) {
		                	listener.onError (req.statusText);
		                    return;
		                }
		                listener.onReady(req.responseXML);
		            }
				};
				
				req.send(null);
			}
		} catch(e) {
			IdentitySelector.logMessage("retrieveXrds: ", e);
		}
	},
	
	// ***********************************************************************
	// Method: processICardItems
	// ***********************************************************************
	
	processICardItems : function( doc, dispatchEvents)
	{
		if (this.disabled == true) {
			IdentitySelector.logMessage("processICardItems", " ID selector is disabled. Exiting");
			return;
		}
		
		var itemCount = 0;
		
		// Process all of the information card objects in the document
				
		var objElems = doc.getElementsByTagName( "OBJECT");
		var icardObjectCount = 0;
		
		for( var i = 0; i < objElems.length; i++) 
		{
			var objElem = objElems[ i];
			var objTypeStr = objElem.getAttribute( "TYPE");
			
			if( objTypeStr == null || 
				 objTypeStr.toLowerCase() !== 
					"application/x-informationcard")
			{
				continue;
			}
			
			if( dispatchEvents)
			{
				var evnt = doc.createEvent( "Event");
				evnt.initEvent( "ICObjectLoaded", true, true);
				objElem.dispatchEvent( evnt);
			}
			
			icardObjectCount++;
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
				
				var evnt = doc.createEvent( "Event");
				evnt.initEvent( "ICElementLoaded", true, true);
				icardElem.dispatchEvent( evnt);
			}
		}
		
		var frames = doc.defaultView.frames;
		for (var i = 0; i < frames.length; i++) { 
			var frame = frames[i];
			IdentitySelector.logMessage( "processICardItems", "frame.document.location.href=" + frame.document.location.href);
			IdentitySelector.processICardItems(frame.document, dispatchEvents);
		 }

		IdentitySelector.logMessage( "processICardItems", "Found " + 
			icardElementCount + " ICard element(s) on " + doc.location);
		

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
			
			if( objElem.tokenType != undefined && objElem.tokenType != null && objElem.tokenType != "")
			{
				data.tokenType = objElem.tokenType;
			}
			else
			{
				delete data[ "tokenType"];
			}
			
			// optionalClaims
			
			if( objElem.optionalClaims != undefined && 
				objElem.optionalClaims != null &&
				objElem.optionalClaims != "")
			{
				data.optionalClaims = objElem.optionalClaims;
			}
			else
			{
				delete data[ "optionalClaims"];
			}
			
			// requiredClaims
			
			if( objElem.requiredClaims != undefined && 
				objElem.requiredClaims != null &&
				objElem.requiredClaims != "")
			{
				data.requiredClaims = objElem.requiredClaims;
			}
			else
			{
				delete data[ "requiredClaims"];
			}
			
			// issuer
			
			if( objElem.issuer != undefined && objElem.issuer != null && objElem.issuer != "")
			{
				data.issuer = objElem.issuer;
			}
			else
			{
				delete data[ "issuer"];
			}
			
			// issuerPolicy
			
			if( objElem.issuerPolicy != undefined && objElem.issuerPolicy != null && objElem.issuerPolicy != "")
			{
				data.issuerPolicy = objElem.issuerPolicy;
			}
			else
			{
				delete data[ "issuerPolicy"];
			}
			
			// privacyUrl
			
			if( objElem.privacyUrl != undefined && 
				objElem.privacyUrl != null && 
				objElem.privacyUrl != "")
			{
				data.privacyUrl = objElem.privacyUrl;
			}
			else
			{
				delete data[ "privacyUrl"];
			}
			
			// privacyVersion
			
			if( objElem.privacyVersion != undefined && 
				objElem.privacyVersion != null && 
				objElem.privacyVersion != "")
			{
				data.privacyVersion = objElem.privacyVersion;
			}
			else
			{
				delete data[ "privacyVersion"];
			}

			// icDropTargetId
			
			if( objElem.icDropTargetId != undefined && 
				objElem.icDropTargetId != null && 
				objElem.icDropTargetId != "")
			{
				data.icDropTargetId = objElem.icDropTargetId;
			}
			else
			{
				delete data[ "icDropTargetId"];
			}
		}
		catch( e)
		{
			IdentitySelector.reportError( "setParamsFromElem", e);
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
			var form = null;
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
					var evnt = doc.createEvent( "Event");
					evnt.initEvent( "ICObjectLoaded", true, true);
					objElem.dispatchEvent( evnt);
				}
				
//				var invokeSelector = ( objElem.token == undefined);
				var invokeSelector = ( objElem.token == undefined);

				// If the embedded ICard object doesn't have a token attached to
				// it, invoke the selector
				
				if( objElem.token == undefined)
				{
					IdentitySelector.logMessage( "onFormSubmit", 
						"Submit encountered in-line");
					
					var evnt = doc.createEvent( "Event");
					evnt.initEvent( "CallIdentitySelector", true, true);
					doc.__identityselector__.targetElem = objElem;
					doc.dispatchEvent( evnt);
					
					// If a token was retrieved, add it as a hidden field of
					// the form
					
					if( objElem.token != undefined)
					{
						IdentitySelector.logMessage( "onFormSubmit", 
						"adding input with name=" + objElem.getAttribute( "name") + " to form. value=" + objElem.token);
						var input = doc.createElement( "INPUT");
					
						input.setAttribute( "name", 
							objElem.getAttribute( "name"));
						input.setAttribute( "type", "hidden");
						input.value = objElem.token;
						form.appendChild( input);
						delete objElem[ "token"];
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
					var evnt = doc.createEvent( "Event");
					evnt.initEvent( "ICElementLoaded", true, true);
					icardElem.dispatchEvent( evnt);
				}
				
				// If the embedded ICard element doesn't have a token attached to
				// it, invoke the selector
				
				if( icardElem.token == undefined)
				{
					IdentitySelector.logMessage( "onFormSubmit", 
						"Submit encountered in-line");
					
					var evnt = doc.createEvent( "Event");
					evnt.initEvent( "CallIdentitySelector", true, true);
					doc.__identityselector__.targetElem = icardElem;
					doc.dispatchEvent( evnt);
					
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
			IdentitySelector.reportError( "onFormSubmit", e);
		}
		
		if (form instanceof HTMLFormElement) {
			IdentitySelector.logMessage( "onFormSubmit", 
					"submitting form");
////			form.submit(); // this call cancels the previous submit
//			var evnt = doc.createEvent( "Event");
//			evnt.initEvent( "submit", true, true);
//			form.dispatchEvent( evnt);
		} else {
			IdentitySelector.logMessage( "onFormSubmit", 
			"form is no instanceof HTMLFormElement but : " + form);
		}
		var cancelSubmit = true;
		return cancelSubmit;
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
			
			if( targetElem.__value == undefined)
			{
				var evnt = doc.createEvent( "Event");
				evnt.initEvent( "CallIdentitySelector", true, true);
				doc.__identityselector__.targetElem = targetElem;
				doc.dispatchEvent( evnt);
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
	
	extractParameter : function( sourceNode, destNode)
	{
		switch( sourceNode.name.toLowerCase())
		{
			case "tokentype":
			{
				IdentitySelector.logMessage( "extractParameter", 
					"tokenType = " + sourceNode.value);
				destNode.tokenType = sourceNode.value;
				break;
			}
		  
			case "optionalclaims":
			{
				IdentitySelector.logMessage( "extractParameter", 
					"optionalClaims = " + sourceNode.value);
				destNode.optionalClaims = sourceNode.value;
				break;
			}
	  
			case "requiredclaims":
			{
				IdentitySelector.logMessage( "extractParameter", 
					"requiredClaims = " + sourceNode.value);
				destNode.requiredClaims = sourceNode.value;
				break;
			}
	  
			case "issuer":
			{
				IdentitySelector.logMessage( "extractParameter", 
					"issuer = " + sourceNode.value);
				destNode.issuer = sourceNode.value;
				break;
			}
	  
			case "issuerpolicy":
			{
				IdentitySelector.logMessage( "extractParameter", 
					"issuerPolicy = " + sourceNode.value);
				destNode.issuerPolicy = sourceNode.value;
				break;
			}

			case "privacyurl":
			{
				IdentitySelector.logMessage( "extractParameter", 
					"privacyUrl = " + sourceNode.value);
				destNode.privacyUrl = sourceNode.value;
				break;
			}

			case "privacyversion":
			{
				IdentitySelector.logMessage( "extractParameter", 
					"privacyVersion = " + sourceNode.value);
				destNode.privacyVersion = sourceNode.value;
				break;
			}

			case "icdroptargetid":
			{
				IdentitySelector.logMessage( "extractParameter", 
					"icDropTargetId = " + sourceNode.value);
				destNode.icDropTargetId = sourceNode.value;
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
	// Method: onDrop
	// ***********************************************************************
	
	onDrop : function(event)
	{
		IdentitySelector.logMessage( "IdentitySelector.onDrop", event.target.nodeName);
		nsDragAndDrop.drop(event, listObserver);
		return false;
	},
	
	// ***********************************************************************
	// Method: onDrop
	// ***********************************************************************
	
	onDragOver : function(event)
	{
		IdentitySelector.logMessage( "IdentitySelector.onDragOver", event.target.nodeName);
		nsDragAndDrop.dragOver(event, listObserver);
		return false;
	},

	// ***********************************************************************
	// Method: findRelatedObject
	// loop through the objects in the doc to find the one that has targetId
	// as the value of icDropTargetId
	// ***********************************************************************
	
	findRelatedObject : function(doc, targetId)
	{
		
		var itemCount = 0;
		
		// Process all of the information card objects in the document
				
		var objElems = doc.getElementsByTagName( "OBJECT");
		
		for( var i = 0; i < objElems.length; i++) 
		{
			var objElem = objElems[ i];
			var objTypeStr = objElem.getAttribute( "TYPE");
			
			if( objTypeStr == null || 
				 objTypeStr.toLowerCase() !== 
					"application/x-informationcard")
			{
				continue;
			}
			if (objElem.icDropTargetId != undefined) {
				IdentitySelector.logMessage( "IdentitySelector.findRelatedObject", "dropTarget for object " + ((objElem.name != undefined) ? objElem.name : "") + " is " + objElem.icDropTargetId);
				if (targetId == objElem.icDropTargetId) {
					return objElem;
				}
			} else {
				IdentitySelector.logMessage( "IdentitySelector.findRelatedObject", "no dropTarget specified for object " + object.name);
			}
			
		}
		return null;
	},

	// ***********************************************************************
	// Method: onWindowDragDrop
	// ***********************************************************************
	
	onWindowDragDrop : function(event)
	{
		IdentitySelector.logMessage( "IdentitySelector.onWindowDragDrop",   "target.nodeName=", event.target.nodeName +
					"\noriginalTarget.nodeName=" + event.originalTarget.nodeName + 
					"\ncurrentTarget.nodeName=" + event.currentTarget.nodeName);
		if (this.disabled == true) {
			IdentitySelector.logMessage("IdentitySelector.onWindowDragDrop", " ID selector is disabled. Exiting");
			return;
		}
		var target = event.target;
		if( target.wrappedJSObject)
		{
			target = target.wrappedJSObject;
		}
		var targetId = target.id;
		if (targetId != null) {
			
			var doc = target.ownerDocument;
			var object = IdentitySelector.findRelatedObject(doc, targetId);
			if (object != null) {
				var dragService = Cc["@mozilla.org/widget/dragservice;1"].getService(Ci.nsIDragService);
			    var dragSession = dragService.getCurrentSession();
			    var sourceNode = dragSession.sourceNode;

			    // Setup a transfer item to retrieve the file data
			    var trans = Cc["@mozilla.org/widget/transferable;1"].createInstance(Ci.nsITransferable);
			    trans.addDataFlavor("application/x-informationcard+id");

			    var cardId = null;
			    for (var i=0; i<dragSession.numDropItems; i++) {
			      var uri = null;

			      dragSession.getData(trans, i);
			      var flavor = {}, data = {}, length = {};
			      trans.getAnyTransferData(flavor, data, length);
			      if (data) {
			    	var str = null;
			        try {
			          str = data.value.QueryInterface(Ci.nsISupportsString);
			        }
			        catch(ex) {
			        }
			        if (str != null) {
			        	IdentitySelector.logMessage("IdentitySelector.onWindowDragDrop", "data=" + str);
			        	cardId = str;
			        	break; // only one
			        } else {
			        	IdentitySelector.logMessage("IdentitySelector.onWindowDragDrop", "data=null");
			        }
			      }
			    }
			    if (cardId != null) {
			    	// launch IdentitySelector with cardId
		        	IdentitySelector.logMessage("IdentitySelector.onWindowDragDrop", "launching IdentitySelector for card: " + cardId);
					doc.__identityselector__.targetElem = object;
					doc.__identityselector__.cardId = cardId;

					var form = target;
					var fired = false;
					while( form != null) 
					{
						if( form.tagName != undefined && form.tagName == "FORM")
						{
							// the droptarget is inside a form -> submit it
				        	var trgt = form;
							var evnt = doc.createEvent( "Event");
							evnt.initEvent( "submit", true, true);
							trgt.dispatchEvent( evnt);
							fired = true;

							break;
						}
						
						form = form.parentNode;
					}

					if (!fired) {
						alert("The drop target is not inside a form\nDon't know how to submit token.");
					}
//					if (!fired) {
//			        	var trgt = doc;
//						var evnt = doc.createEvent( "Event");
//						evnt.initEvent( "CallIdentitySelector", true, true);
//						trgt.dispatchEvent( evnt);
//					}
			    }
				event.preventDefault();
				event.stopPropagation();
			} else {
				IdentitySelector.logMessage("IdentitySelector.onWindowDragDrop",   "no object found for targetId=" + targetId + " in document " + doc.location.href);
			}
		}
		return false;
	},

//	// ***********************************************************************
//	// Method: valueGetter
//	// ***********************************************************************
//	
//	valueGetter : function()
//	{
//		IdentitySelector.logMessage("IdentitySelector.valueGetter",   "object =" + this + "location=" + this.ownerDocument.location.href );
//		return "value";
//	},

	// ***********************************************************************
	// Method: onICardObjectLoaded
	// ***********************************************************************
	
	onICardObjectLoaded : function( event)
	{
		// From http://www.mail-archive.com/public-webapi@w3.org/msg02179.html
		//
		// Since scripts sometimes depend on style information being up
		// to date by the time they're run, Gecko (even Gecko 1.9) won't
		// run that script until the stylesheet has finished loading. And 
		// since scripts block the parser (due to document.write), 
		// DOMContentLoaded automatically fires after scripts have all run.
		
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
			
			if (objElem.tagName != 'OBJECT') {
				IdentitySelector.logMessage( "onICardObjectLoaded", "object tagName != OBJECT. Exiting.");
				return;
			}
			
			if( objElem.__processed != undefined)
			{
				IdentitySelector.logMessage( "onICardObjectLoaded", 
					"ICard object has already been processed.");
			}
			else
			{
				if( doc.__identityselector__ === undefined) {
					IdentitySelector.logMessage( "onICardElementLoaded", 
					"running intercept script");
					IdentitySelector.runInterceptScript(doc);
				}
				
				{
					if( objElem.wrappedJSObject)
					{
						objElem = objElem.wrappedJSObject;
						IdentitySelector.logMessage( "onICardElementLoaded", 
								"objElem wrapped: " + objElem);
					}

					IdentitySelector.logMessage( "onICardElementLoaded", "defining Getter for " + objElem);
				delete objElem[ "value"];
//				objElem.__defineGetter__( "value", 
//					doc.__identityselector__.valueGetter);
//					objElem.__defineGetter__( "value", 
//						IdentitySelector.valueGetter);
				}
				
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
				
				// Process the parameter values
				
				for( each in objElem.childNodes)
				{
					var childNode = objElem.childNodes[ each];
				
					if( childNode.tagName != undefined &&
						 childNode.tagName == "PARAM") 
					{
						IdentitySelector.extractParameter( childNode, objElem);
					}
				}
				
				if (objElem.icDropTargetId != undefined) {
					var dropTargetElement = doc.getElementById(objElem.icDropTargetId);
					if (dropTargetElement != null) {
						IdentitySelector.logMessage( "onICardObjectLoaded", 
							"icDropTargetId=" + objElem.icDropTargetId);
						dropTargetElement.setAttribute("ondragdrop", "IdentitySelector.onDrop(); return false;");
						dropTargetElement.setAttribute("ondragover", "IdentitySelector.onDragOver(); return false;");
//						dropTargetElement.setAttribute("ondragdrop", "IdentitySelector.onDrop(); return false;");
						if ((doc.defaultView != undefined) && (doc.defaultView)) {
							var docWindow = doc.defaultView;
							docWindow.addEventListener("dragdrop", IdentitySelector.onWindowDragDrop, false);
						}
					} else {
						IdentitySelector.logMessage( "onICardObjectLoaded", 
							"icDropTargetId == null");
					}
				}
				
				IdentitySelector.logMessage( "onICardObjectLoaded", 
					"Processed ICard object.");
					
				objElem.__processed = true;

				if( !("notificationBoxHidden" in doc.__identityselector__))
				{
					IdentitySelector.onHideNotificationBox();
					doc.__identityselector__.notificationBoxHidden = true;
				}

			}
		}
		catch( e)
		{
			IdentitySelector.reportError( "onICardObjectLoaded", e);
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
			var optionalClaims = null;
			var requiredClaims = null;
			
			if( icardElem.__processed != undefined)
			{
				IdentitySelector.logMessage( "onICardElementLoaded", 
					"ICard element has already been processed.");
			}
			else
			{
				if( doc.__identityselector__ === undefined) {
					IdentitySelector.logMessage( "onICardElementLoaded", 
					"running intercept script");
					IdentitySelector.runInterceptScript(doc);
				}

				delete icardElem[ "value"];
//				icardElem.__defineGetter__( "value", 
//					doc.__identityselector__.valueGetter);
				
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
				
				// Process attributes
				
				for( var i = 0; i < icardElem.attributes.length; i++)
				{
					IdentitySelector.extractParameter( 
						icardElem.attributes[ i], icardElem);
				}
				
				// Process the child nodes
				
				var addElems = icardElem.getElementsByTagName( "IC:ADD");
				
				for( var i = 0; i < addElems.length; i++) 
				{
					var addElem = addElems[ i];
					var claimTypeAttr = addElem.attributes[ "claimType"];
					var optionalAttr = addElem.attributes[ "optional"];
					var isOptional;
					
					if( claimTypeAttr != null) 
					{
						if( optionalAttr != null)
						{
							if( optionalAttr.value == "true")
							{
								isOptional = true;
							}
							else
							{
								isOptional = false;
							}
						}
						else
						{
							isOptional = false;
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
						
						IdentitySelector.logMessage( "onICardElementLoaded", 
							"claimType = " + claimTypeAttr.value);
					}
				}
				
				if( optionalClaims)
				{
					icardElem.optionalClaims = optionalClaims;
					IdentitySelector.logMessage( "onICardElementLoaded", 
						"optionalClaims = " + optionalClaims);
				}
				
				if( requiredClaims)
				{
					icardElem.requiredClaims = requiredClaims;
					IdentitySelector.logMessage( "onICardElementLoaded", 
						"requiredClaims = " + requiredClaims);
				}
				
				IdentitySelector.logMessage( "onICardElementLoaded", 
					"Processed ICard element.");
					
				icardElem.__processed = true;
			}
		}
		catch( e)
		{
			IdentitySelector.reportError( "onICardElementLoaded", e);
		}
	},
	
	// ***********************************************************************
	// Method: getSecurityToken
	// ***********************************************************************
	
	getSecurityToken : function( data) 
	{
		var exec = Components.classes[ "@mozilla.org/file/local;1"].
						createInstance( Components.interfaces.nsILocalFile);
		var pr = Components.classes[ "@mozilla.org/process/util;1"].
						createInstance( Components.interfaces.nsIProcess);
		var browser = document.getElementById( "content");
		var secureUi = browser.securityUI;
		var sslStatusProvider = null;
		var sslStatus = null;
		var args = new Array();
		var identitySelectorExePath = IdentitySelector.findIdentitySelectorExe();
		var exePath = identitySelectorExePath;
		var iLoop;
		var tokenFilePath = null;
		var certFilePath = null;
		var privFilePath = null;
		var token = null;
		var savedException = null;
		
		try
		{
			sslStatusProvider = secureUi.QueryInterface(
							Components.interfaces.nsISSLStatusProvider);
	
			if( sslStatusProvider != null)
			{
				try
				{
					sslStatus = sslStatusProvider.SSLStatus.QueryInterface(
								Components.interfaces.nsISSLStatus);
				}
				catch( e)
				{
					sslStatus = null;
				}
			}
	
			tokenFilePath = IdentitySelector.generateTmpFilePath();
			
			args.push( "--gettoken");
			
			if( data.issuer)
			{
				args.push( "--issuers=\"" + data.issuer + "\"");
			}
			
			if( data.recipient)
			{
				args.push( "--recipient=\"" + data.recipient + "\"");
			}
			
			if( data.requiredClaims)
			{
				args.push( "--reqclaims=\"" + data.requiredClaims + "\"");
			}
			
			if( data.optionalClaims)
			{
				args.push( "--optclaims=\"" + data.optionalClaims + "\"");
			}
			
			if( data.tokenType)
			{
				args.push( "--tokentype=\"" + data.tokenType + "\"");
			}
			
			if( data.privacyUrl)
			{
				privFilePath = IdentitySelector.generateTmpFilePath();
				IdentitySelector.writeFile( privFilePath, data.privacyUrl);
				args.push( "--privfile=\"" + privFilePath + "\"");
			}
			
			if( sslStatus != null && sslStatus.serverCert != undefined)
			{
				var certChain = sslStatus.serverCert.getChain();
				var chainBytes = "";
				
				for( iLoop = 0; iLoop < certChain.length; iLoop++)
				{
					var cert = certChain.queryElementAt( iLoop, 
										Components.interfaces.nsIX509Cert);
					var length = {};
					var rawDer = cert.getRawDER( length);
					var derBytes = "";
					
					for( var iSubLoop = 0; iSubLoop < rawDer.length; iSubLoop++)
					{
						derBytes = derBytes + String.fromCharCode( rawDer[ iSubLoop]);
					}
					
					chainBytes = chainBytes + derBytes;
				}
				
				certFilePath = IdentitySelector.generateTmpFilePath();
				sslStatus.serverCert.getChain();
				IdentitySelector.writeFile( certFilePath, chainBytes);
				args.push( "--certfile=\"" + certFilePath + "\"");
			}
			
			args.push( "--tokenfile=\"" + tokenFilePath + "\"");
			
			// Log the arguments
			
			for( iLoop = 0; iLoop < args.length; iLoop++)
			{
				IdentitySelector.logMessage( "getSecurityToken", 
						"arg[" + (iLoop + 1) + "] = " + args[ iLoop] + "\n");
			}
			
			// Run the selector
			
			exec.initWithPath( exePath);
			pr.init( exec);
			pr.run( true, args, args.length);
			
			// Log the exit code
			
			IdentitySelector.logMessage( "getSecurityToken", 
					"pr.exitValue == " + pr.exitValue);
					
			// Attach the returned token (if any) to the target element
			
			if( pr.exitValue === 0)
			{
				token = IdentitySelector.readFile( tokenFilePath);
			}
		}
		catch( e)
		{
			savedException = e;
		}
		
		// Clean up temporary files
		
		try
		{
			var file = Components.classes[
							"@mozilla.org/file/local;1"].createInstance(
								Components.interfaces.nsILocalFile);
								
			if( tokenFilePath !== null)
			{
				file.initWithPath( tokenFilePath);
				file.remove( false);
			}
			
			if( certFilePath !== null)
			{
				file.initWithPath( certFilePath);
				file.remove( false);
			}
			
			if( privFilePath !== null)
			{
				file.initWithPath( privFilePath);
				file.remove( false);
			}
		}
		catch( e)
		{
			IdentitySelector.throwError( "getSecurityToken", e);
		}
		
		// Throw any saved exceptions
		
		if( savedException != null)
		{
			IdentitySelector.throwError( "getSecurityToken", savedException);
		}
		
		// Done

		return( token);
	},
	
	// ***********************************************************************
	// Method: onCallIdentitySelector
	// ***********************************************************************
	
	onCallIdentitySelector : function( event) 
	{
	 	this.disabled = gPrefService.getBoolPref( "identityselector.disabled");
		if (this.disabled == true) {
			IdentitySelector.logMessage("onCallIdentitySelector", " Id selector is disabled. Exiting");
			return;
		}

		var target = event ? event.target : this;
		var doc;
		var data;
		var result;
		var identObject;
		
		IdentitySelector.logMessage( "onCallIdentitySelector", 
			"Identity selector invoked.");
		
		if( target.wrappedJSObject)
		{
			target = target.wrappedJSObject;
		}
		
		IdentitySelector.logMessage( "onCallIdentitySelector", 
				"target=" + target + "\noriginalTarget=" + event.originalTarget);
		
		if (target instanceof HTMLObjectElement) {
			doc = target.ownerDocument; // event was fired in onDragDrop
		} else {
			doc = target;
		}
		
		identObject = doc.__identityselector__;
		data = identObject.data;

		if (doc.__identityselector__.secureFrameHandling == false) {
			IdentitySelector.logMessage( "onCallIdentitySelector", " secureFrameHandling == false. " + window.document.URL);
			return;
		}

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
			
			// Launch the card selector
		    var sslCert = null;
			{
			    var browser = document.getElementById( "content");
			    var secureUi = browser.securityUI;
			    var sslStatusProvider = null;

			    sslStatusProvider = secureUi.QueryInterface(
							    Components.interfaces.nsISSLStatusProvider);

			    if( sslStatusProvider != null)
			    {
				    try
				    {
					    sslStatus = sslStatusProvider.SSLStatus.QueryInterface(
								    Components.interfaces.nsISSLStatus);
					    if( sslStatus != null && sslStatus.serverCert != undefined)
					    {
						    sslCert = sslStatus.serverCert
					    }
				    }
				    catch( e)
				    {
					    sslStatus = null;
				    }
			    }
            }

            var cid = null;
            {
   			    // lookup class id from config.
			    var prefs = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
			    var pbi = prefs.QueryInterface(Components.interfaces.nsIPrefBranch);

			    cid = pbi.getCharPref("identityselector.contractid");
            }

            if (cid == "digitalme") {
    			identObject.targetElem.token = 
	    			IdentitySelector.getSecurityToken( data);
	    	} else {
	    	    var obj = null;
			    try {
				    var cidClass = Components.classes[cid];
				    if (cidClass != undefined) { 
					    obj = cidClass.createInstance();
					    obj = obj.QueryInterface(Components.interfaces.IIdentitySelector);
				    } else {
					    IdentitySelector.reportError("onCallIdentitySelector", "the class " + cid + " is not installed");
					    return;
				    }
			    }
			    catch (e) {
				    IdentitySelector.throwError( "onCallIdentitySelector:", e);
			    }
			    
			    IdentitySelector.logMessage( "onCallIdentitySelector", "ssl security mode=" + IdentitySelector.getMode(doc));
			    
			    /* Make the call to the selector */
			    identObject.targetElem.token = obj.GetBrowserToken(
			     data.issuer , 
			     data.recipient, 
			     data.requiredClaims,
			     data.optionalClaims, 
			     data.tokenType,
			     data.privacyUrl, 
			     data.privacyVersion, 
			     sslCert, 
                 data.issuerPolicy,
                 data.icDropTargetId,
                 IdentitySelector.getMode(doc));

			    IdentitySelector.logMessage( "onCallIdentitySelector", 
					    "returned token == " + identObject.targetElem.token);
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
			getBrowser().getNotificationBox().notificationsHidden = true;
		}
		catch( e)
		{
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
			
			if( objTypeStr == null || 
				 objTypeStr.toLowerCase() !== "application/x-informationcard")
			{
				continue;
			}
			
			return( objElem);
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
	// Method: findIdentitySelectorExe
	// ***********************************************************************
	
	findIdentitySelectorExe : function()
	{
		var exePath = null;
		var bFound = false;
		var bSetExePref = false;
		
		try
		{
			var file = Components.classes[
							"@mozilla.org/file/local;1"].createInstance(
								Components.interfaces.nsILocalFile);
			var userEnvironment = Components.classes[
							"@mozilla.org/process/environment;1"].getService(
								Components.interfaces.nsIEnvironment);
			var pr = Components.classes[
							"@mozilla.org/process/util;1"].
								createInstance( Components.interfaces.nsIProcess);
			var dirService = Components.classes[
							"@mozilla.org/file/directory_service;1"].getService(
								Components.interfaces.nsIProperties);
			var exeName = null;
			var path = null;
			var userHome = null;
			var iLoop;
			
			// Log information about the browser and platform
			
			IdentitySelector.logPlatformInfo();
			
			// See if a preference exists for the path
			
			if( (exePath = IdentitySelector.getStringPref( "exe.path")) != null)
			{
				try
				{
					file.initWithPath( exePath);
					if( file.exists()) 
					{
						bFound = true;
					}
				}
				catch( e)
				{
					bSetExePref = true;
				}
			}
			else
			{
				bSetExePref = true;
			}
			
			if( !bFound)
			{
				// User's home directory
					
				userHome = Components.classes[ 
					"@mozilla.org/file/directory_service;1"].getService( 
						Components.interfaces.nsIProperties).get( "Home", 
							Components.interfaces.nsIFile).path;
				
				IdentitySelector.logMessage( "findIdentitySelectorExe", 
					"User's home directory: " + userHome);
					
				// Executable name
					
				if( gbIsMac)
				{
					exeName = "DigitalMe";
				}
				else if( gbIsWin)
				{
					exeName = "digitalme.exe";
				}
				else
				{
					exeName = "digitalme";
				}
						
				IdentitySelector.logMessage( "findIdentitySelectorExe", 
					"Executable name: " + exeName);
					
				for( ;;)
				{
					// Search the path
					
					try 
					{
						if( gbIsWin)
						{
							path = userEnvironment.get( "PATH").split( ";");
						}
						else
						{
							path = userEnvironment.get( "PATH").split( ":");
						}
						
						for( iLoop = 0; iLoop < path.length; iLoop++) 
						{
							IdentitySelector.logMessage( "findIdentitySelectorExe", 
								"Looking for " + exeName + " in " + 
								path[ iLoop] + " ...");
							
							file.initWithPath( path[ iLoop]);
							file.appendRelativePath( exeName);
							
							if( file.exists()) 
							{
								bFound = true;
								exePath = file.path;
								break;
							}
						}
					} 
					catch( e1)
					{
						IdentitySelector.logMessage( "findIdentitySelectorExe", e1);
					}
					
					if( bFound)
					{
						break;
					}
					
					// Look in other "standard" locations
					
					if( gbIsMac)
					{
						path = [
							userHome + "/Desktop/DigitalMe.app/Contents/MacOS",
							"/Applications/DigitalMe.app/Contents/MacOS",
							"/Applications/Utilities/DigitalMe.app/Contents/MacOS"];
					}
					else if( !gbIsWin)
					{
						path = [
							"/usr/local/lib/digitalme/bin",
							"/usr/lib/digitalme/bin",
							userHome + "/digitalme/bin",
							userHome + "/Desktop/digitalme/bin"];
					}
					
					for( iLoop = 0; iLoop < path.length; iLoop++) 
					{
						IdentitySelector.logMessage( "findIdentitySelectorExe", 
							"Looking for " + exeName + " in " + path[ iLoop] + " ...");
	
						file.initWithPath( path[ iLoop]);
						file.appendRelativePath( exeName);
						
						if( file.exists()) 
						{
							bFound = true;
							exePath = file.path;
							break;
						}
					}
					
					break;
				}
			}
		}
		catch( e2) 
		{
			IdentitySelector.reportError( "findIdentitySelectorExe", e2);
		}
	
		if( !bFound) 
		{
			if( gbIsMac)
			{
				IdentitySelector.throwError( "findIdentitySelectorExe",
					"Unable to locate an identity selector.  " + 
					"Please make sure one is installed " +
					"on your desktop, in the Applications folder, or in the " +
					"Utilities folder.");
			}
			else
			{
				IdentitySelector.throwError( "findIdentitySelectorExe",
					"Unable to locate an identity selector.  " +
					"Please make sure one is installed.");
			}
		}
		else
		{
			IdentitySelector.logMessage( "findIdentitySelectorExe", 
				"IdentitySelector executable found: " + exePath);
				
			if( bSetExePref)
			{
				IdentitySelector.setStringPref( "exe.path", exePath);
			}
		}

		return( exePath);
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
			var file = Components.classes[
				"@mozilla.org/file/local;1"].createInstance(
					Components.interfaces.nsILocalFile);
			var fileOStream = Components.classes[
				"@mozilla.org/network/file-output-stream;1"].createInstance(
					Components.interfaces.nsIFileOutputStream);
					
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
	// Method: getStringPref
	// ***********************************************************************
	
	getStringPref : function( prefId)
	{
		var fullPrefId = "extensions.digitalme." + prefId;
		
		if( gPrefService.getPrefType( fullPrefId) == gPrefService.PREF_STRING)
		{
			return( gPrefService.getCharPref( fullPrefId));
		}
		
		return( null);
	},
	
	// ***********************************************************************
	// Method: setStringPref
	// ***********************************************************************
	
	setStringPref : function( prefId, prefValue)
	{
		gPrefService.setCharPref( "extensions.digitalme." + prefId, prefValue);
	},
	
	// ***********************************************************************
	// Method: getStringPref
	// ***********************************************************************
	
	getBooleanPref : function( prefId)
	{
		var fullPrefId = "extensions.digitalme." + prefId;
		
		if( gPrefService.getPrefType( fullPrefId) == gPrefService.PREF_BOOL)
		{
			return( gPrefService.getBoolPref( fullPrefId));
		}
		
		return( false);
	},
	
	// ***********************************************************************
	// Method: setStringPref
	// ***********************************************************************
	
	setBooleanPref : function( prefId, prefValue)
	{
		gPrefService.setBoolPref( "extensions.digitalme." + prefId, prefValue);
	}
};


/****************************************************************************
Desc:
****************************************************************************/

var httpRequestObserver =
{
  observe: function(subject, topic, data)
  {
    if (topic == "http-on-modify-request") {
      var httpChannel = subject.QueryInterface(Components.interfaces.nsIHttpChannel);
      httpChannel.setRequestHeader("X-ID-Selector", "openinfocard", false);
    }
  },

  get observerService() {
    return Components.classes["@mozilla.org/observer-service;1"]
                     .getService(Components.interfaces.nsIObserverService);
  },

  register: function()
  {
    this.observerService.addObserver(this, "http-on-modify-request", false);
  },

  unregister: function()
  {
    this.observerService.removeObserver(this, "http-on-modify-request");
  }
};

/****************************************************************************
Desc:
****************************************************************************/
try {
	httpRequestObserver.register();
} catch (e) {
 	 IdentitySelector.logMessage("httpRequestObserver.register() failed: ", e);
}

///****************************************************************************
//Desc:
//****************************************************************************/
//
//var formSubmitObserver =
//{
//  QueryInterface : function(aIID)
//  {
//	  if ( !aIID.equals(Components.interfaces.nsISupports) &&
//	       !aIID.equals(Components.interfaces.nsIObserver) &&
//	       !aIID.equals(Components.interfaces.nsIFormSubmitObserver) &&
//	       !aIID.equals(Components.interfaces.nsISupportsWeakReference) )
//		  {
//		    throw Components.results.NS_ERROR_NO_INTERFACE;
//		  }
//		  return this;
//  },
//  
//  notify: function(formElement, aWindow, actionURI)
//  {
//	var doc = formElement.ownerDocument;
//	var evnt = doc.createEvent( "Event");
//	evnt.initEvent( "ICFormSubmit", true, true);
//	formElement.dispatchEvent( evnt);
//	var cancelSubmit = true;
//	return cancelSubmit; // cancel submit
//  },
//
//  get observerService() {
//    return Components.classes["@mozilla.org/observer-service;1"]
//                     .getService(Components.interfaces.nsIObserverService)
//  },
//
//  register: function()
//  {
//    this.observerService.addObserver(this, "earlyformsubmit", false);
//  },
//
//  unregister: function()
//  {
//    this.observerService.removeObserver(this, "earlyformsubmit");
//  }
//};
//
///****************************************************************************
//Desc:
//****************************************************************************/
//
//try {
//	formSubmitObserver.register();
//} catch(e) {
//  	 IdentitySelector.logMessage("formSubmitObserver.register() failed: ", e);
//}

/****************************************************************************
Desc:
****************************************************************************/
var prefObserver =
{
  prefs: null,
	
  observe: function(subject, topic, data)
   {
   	 IdentitySelector.logMessage("prefObserver:observe", "subject=" + subject + " topic=" + topic + " data=" + data);
     if (topic != "nsPref:changed")
     {
       return;
     }
 
     switch(data)
     {
       case "disabled":
         var disabled = this.prefs.getBoolPref( "disabled" );
         if (disabled == true) {
         	IdentitySelector.onUninstall();
         } else {
         	IdentitySelector.onInstall();
         }
         break;
     }
   },

  register: function()
  {
  	this.prefs = Components.classes["@mozilla.org/preferences-service;1"]
         .getService(Components.interfaces.nsIPrefService)
         .getBranch("identityselector.");
     this.prefs.QueryInterface(Components.interfaces.nsIPrefBranch2);
     this.prefs.addObserver("", this, false);
  },

  unregister: function()
  {
    this.prefs.removeObserver("", this);
  }
};

/****************************************************************************
Desc:
****************************************************************************/

try
{
	prefObserver.register();
}
catch( e)
{
	IdentitySelector.reportError( "prefObserver.register() failed: ", e);
}

// **************************************************************************
// Desc:
// **************************************************************************
try
{
	window.addEventListener("load", function () { gBrowser.addEventListener("load", IdentitySelector.onInstall, true); }, false);
	window.addEventListener("unload", function () { gBrowser.addEventListener("load", IdentitySelector.onUninstall, true); }, false);
//	window.addEventListener( "load",
//		IdentitySelector.onInstall, false);
//		
//	window.addEventListener( "unload", 
//		IdentitySelector.onUninstall, false);
}
catch( e)
{
	IdentitySelector.reportError( "window.addEventListener failed: ", e);
}
