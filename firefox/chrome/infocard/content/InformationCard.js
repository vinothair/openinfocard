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
//------------------------------------------------------------------------------

/****************************************************************************
Desc:
****************************************************************************/
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
	},
	
	onLocationChange : function( aProgress, aRequest, aURI)
	{
		// This fires when a load event has been confirmed or when the
		// user switches tabs.  At this point, Firefox allows content to be
		// added into the document.  This is where we add a global submit
		// intercept.
		
		try
		{
			var doc = aProgress.DOMWindow.content.document;
			var headElm = doc.getElementsByTagName( "HEAD")[ 0];
			
			if( headElm != null)
			{
				if( doc.getElementById( "__identityselector__") == null)
				{
					var scriptNode = doc.createElement( "SCRIPT");
					
					scriptNode.appendChild( doc.createTextNode( "try"));
					scriptNode.appendChild( doc.createTextNode( "{"));
					scriptNode.appendChild( doc.createTextNode( "   document.__identityselector__ = new Object();"));
					scriptNode.appendChild( doc.createTextNode( "   document.__identityselector__.data = new Object();"));
					
					scriptNode.appendChild( doc.createTextNode( "   document.__identityselector__.chainSubmit = HTMLFormElement.prototype.submit;"));
					scriptNode.appendChild( doc.createTextNode( "   HTMLFormElement.prototype.submit = function()"));
					scriptNode.appendChild( doc.createTextNode( "   {"));
					scriptNode.appendChild( doc.createTextNode( "      var event = document.createEvent( 'Event');"));
					scriptNode.appendChild( doc.createTextNode( "      event.initEvent( 'ICFormSubmit', true, true);"));
					scriptNode.appendChild( doc.createTextNode( "      this.dispatchEvent( event);"));
					scriptNode.appendChild( doc.createTextNode( "      document.__identityselector__.chainSubmit.apply( this);"));
					scriptNode.appendChild( doc.createTextNode( "   };"));
					
					scriptNode.appendChild( doc.createTextNode( "   document.__identityselector__.valueGetter = function()"));
					scriptNode.appendChild( doc.createTextNode( "   {"));
					scriptNode.appendChild( doc.createTextNode( "      var event = document.createEvent( 'Event');"));
					scriptNode.appendChild( doc.createTextNode( "      event.initEvent( 'ICGetTokenValue', true, true);"));
					scriptNode.appendChild( doc.createTextNode( "      this.dispatchEvent( event);"));
					scriptNode.appendChild( doc.createTextNode( "      return( this.__value);"));
					scriptNode.appendChild( doc.createTextNode( "   };"));
					scriptNode.appendChild( doc.createTextNode( "}"));
					scriptNode.appendChild( doc.createTextNode( "catch( e)"));
					scriptNode.appendChild( doc.createTextNode( "{"));
					scriptNode.appendChild( doc.createTextNode( "   alert( e);"));
					scriptNode.appendChild( doc.createTextNode( "}"));
					scriptNode.setAttribute( "id", "__identityselector__");
					
					headElm.appendChild( scriptNode);
				}
			}
		}
		catch( e)
		{
			IdentitySelector.reportError( "onLocationChange", e);
		}
	},
	
	onProgressChange : function()
	{
	},
	
	onStatusChange : function()
	{
	},
	
	onSecurityChange : function()
	{
	},
	
	onLinkIconAvailable : function()
	{
	}
};

/****************************************************************************
Desc:
****************************************************************************/
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
		var consoleService = Components.classes[ "@mozilla.org/consoleservice;1"].
			getService( Components.interfaces.nsIConsoleService);
			
		consoleService.logStringMessage( "IdentitySelector:" + 
			location + ": " + message);
	},

	// ***********************************************************************
	// Method: onInstall
	// ***********************************************************************
	
	onInstall : function( event)
	{
		// Remove the event listener
		
		window.removeEventListener( "load", 
			IdentitySelector.onInstall, true);
			
		var target = event ? event.target : this;
		var doc;
		
		if( target.wrappedJSObject)
		{
			target = target.wrappedJSObject;
		}
		
		doc = target;
		try
		{
			if( doc.__identityselector__ == undefined) {
				// Add event handlers
				
				addEventListener( "CallIdentitySelector",
					IdentitySelector.onCallIdentitySelector, false, true);
			
				addEventListener( "IdentitySelectorAvailable",
					IdentitySelector.onIdentitySelectorAvailable, false, true);
		
				addEventListener( "ICHideNotificationBox",
					IdentitySelector.onHideNotificationBox, false, true);
			
				addEventListener( "ICObjectLoaded",
					IdentitySelector.onICardObjectLoaded, false, true);
		
				addEventListener( "ICElementLoaded",
					IdentitySelector.onICardElementLoaded, false, true);
					
				addEventListener( "ICFormSubmit",
					IdentitySelector.onFormSubmit, false, true);
					
				addEventListener( "ICGetTokenValue",
					IdentitySelector.onGetTokenValue, false, true);
					
				window.addEventListener( "DOMContentLoaded", 
					IdentitySelector.onContentLoaded, false);
					
				// Add a progress listener
					
				window.getBrowser().addProgressListener( ICProgressListener, 
					Components.interfaces.nsIWebProgress.NOTIFY_ALL);
			} else {
				IdentitySelector.reportError("onInstall", "Another identityselector is already handling informationcards. Backing out.")
			}
		} catch(e) {
			IdentitySelector.throwError("onInstall", e);
		}
	},
	
	// ***********************************************************************
	// Method: onContentLoaded
	// ***********************************************************************
	
	onContentLoaded : function( event)
	{
		var target = event ? event.target : this;
		var doc;
		
		if( target.wrappedJSObject)
		{
			target = target.wrappedJSObject;
		}
		
		doc = target;
		try
		{
			if( doc.__identityselector__ !== undefined)
			{
				doc.__identityselector__.contentLoaded = true;
				
				if( !doc.__identityselector__.submitIntercepted)
				{
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
						
						var evnt = doc.createEvent( "Event");
						evnt.initEvent( "ICObjectLoaded", true, true);
						objElem.dispatchEvent( evnt);
						
						icardObjectCount++;
					}
					
					IdentitySelector.logMessage( "onContentLoaded", "Found " + 
						icardObjectCount + " ICard object(s) on " + doc.location);
						
					// Process all of the information card elements in the document
					
					var icardElems = doc.getElementsByTagName( "IC:INFORMATIONCARD");
					var icardElementCount = 0;
					
					for( var i = 0; i < icardElems.length; i++) 
					{
						var icardElem = icardElems[ i];
						
						var evnt = doc.createEvent( "Event");
						evnt.initEvent( "ICElementLoaded", true, true);
						icardElem.dispatchEvent( evnt);
						
						icardElementCount++;
					}
					
					IdentitySelector.logMessage( "onContentLoaded", "Found " + 
						icardElementCount + " ICard element(s) on " + doc.location);
				}
			}
		}
		catch( e)
		{
			IdentitySelector.reportError( "onContentLoaded", e);
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
					var evnt = doc.createEvent( "Event");
					evnt.initEvent( "ICObjectLoaded", true, true);
					objElem.dispatchEvent( evnt);
				}
				
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

			case "privacypolicy":
			case "privacyurl":
			{
				IdentitySelector.logMessage( "extractParameter", 
					"privacyUrl = " + sourceNode.value);
				destNode.privacyUrl = sourceNode.value;
				break;
			}

			case "privacypolicyversion":
			case "privacyversion":
			{
				IdentitySelector.logMessage( "extractParameter", 
					"privacyVersion = " + sourceNode.value);
				destNode.privacyVersion = sourceNode.value;
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
			
			if( objElem.__processed != undefined)
			{
				IdentitySelector.reportError( "onICardObjectLoaded", 
					"ICard object has already been processed!");
			}
			else
			{
				delete objElem[ "value"];
				objElem.__defineGetter__( 'value', 
					doc.__identityselector__.valueGetter);
				
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
				
				IdentitySelector.logMessage( "onICardObjectLoaded", 
					"Processed ICard object.");
					
				objElem.__processed = true;
			}
		}
		catch( e)
		{
			IdentitySelector.reportError( "onICardObjectLoaded", e);
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
			var optionalClaims = null;
			var requiredClaims = null;
			
			if( icardElem.__processed != undefined)
			{
				IdentitySelector.reportError( "onICardElementLoaded", 
					"ICard element has already been processed!");
			}
			else
			{
				delete icardElem[ "value"];
				icardElem.__defineGetter__( "value", 
					doc.__identityselector__.valueGetter);
				
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
	// Method: onCallIdentitySelector
	// ***********************************************************************
	
	onCallIdentitySelector : function( event) 
	{
		var target = event ? event.target : this;
		var doc;
		var data;
		var result;
		var identObject;
		var tokenFilePath = null;
		
		IdentitySelector.logMessage( "onCallIdentitySelector", 
			"Identity selector invoked.");
		
		if( target.wrappedJSObject)
		{
			target = target.wrappedJSObject;
		}
		
		doc = target;
		identObject = doc.__identityselector__;
		data = identObject.data;

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

			var browser = document.getElementById( "content");
			var secureUi = browser.securityUI;
			var sslStatusProvider = null;
			var sslCert = null;

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
/**********
 * Collect arguments that are required by the selectors
 * Used when testing initial interface - here for reference now.

 			var args = {};
			args["issuer"] = data.issuer;
			args["recipient"] = data.recipient;
			args["requiredClaims"] = data.requiredClaims;
			args["optionalClaims"] = data.optionalClaims;
			args["tokenType"] = data.tokenType;
			args["privacyUrl"] = data.privacyUrl;
			args["privacyVersion"] = data.privacyVersion;
			args["issuerPolicy"] = data.issuerPolicy;
			args["recipientURL"] = document.location;
 *********/
 			
			if( sslStatus != null && sslStatus.serverCert != undefined)
			{
				sslCert = sslStatus.serverCert
			}

/**********
 * This is where the callout to the specific selector would occur.
 * Not sure how a lookup to get access to the specific selector interface happens
 * Currently just using the function that was implemented in processInfocardObjects
 * Maybe better to use the getBrowserToken function signature from the perpetual motion plugin.
 *********/

			// var token = invokeSelector(doc, sslCert, args);
			var token = null;

			var obj = null;

			// lookup class id from config.
			var prefs = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
			var pbi = prefs.QueryInterface(Components.interfaces.nsIPrefBranch);

			var cid = pbi.getCharPref("identityselector.contractid");

			//var cid = "@xmldap.org/identityselector;1";
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
				IdentitySelector.throwError( "onCallIdentitySelector", e);
			}
			
/* Make the call to the selector */
			token = obj.GetBrowserToken(
			 data.issuer , 
			 data.recipient, 
			 data.requiredClaims,
			 data.optionalClaims, 
			 data.tokenType,
			 data.privacyUrl, 
			 data.privacyVersion, 
			 sslCert, 
             data.issuerPolicy );

			IdentitySelector.logMessage( "onCallIdentitySelector", 
					"returned token == " + token);
			
			if( token != null)
			{
				if( identObject.targetElem)
				{
					identObject.targetElem.token = token;
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
			getBrowser().getNotificationBox().notificationsHidden = true;
		}
		catch( e)
		{
			IdentitySelector.reportError( "onHideNotificationBox", e);
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
			var bFound = false;
			var exeName = null;
			var path = null;
			var userHome = null;
			var navplat = navigator.platform;
			var bIsWin = ((navplat.indexOf( 'Win') > -1) ? true : false);
			var bIsMac = ((navplat.indexOf( 'Mac') > -1) ? true : false);
			var bIsLinux = ((navplat.indexOf( 'Linux') > -1) ? true : false);
			var iLoop;
			
			userHome = Components.classes["@mozilla.org/file/directory_service;1"].
							getService(Components.interfaces.nsIProperties).
							get("Home", Components.interfaces.nsIFile).path;
			
			for( ;;)
			{
				// Search the path
				
				try 
				{
					if( bIsWin)
					{
						path = userEnvironment.get( "PATH").split( ";");
					}
					else
					{
						path = userEnvironment.get( "PATH").split( ":");
					}
					
					if( bIsMac)
					{
						exeName = "DigitalMe";
					}
					else if( bIsWin)
					{
						exeName = "digitalme.exe";
					}
					else
					{
						exeName = "digitalme";
					}
					
					for( iLoop = 0; iLoop < path.length; iLoop++) 
					{
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
				
				if( bIsMac)
				{
					path = [
						userHome + "/Desktop/DigitalMe.app/Contents/MacOS",
						"/Applications/DigitalMe.app/Contents/MacOS",
						"/Applications/Utilities/DigitalMe.app/Contents/MacOS"];
				}
				else if( !bIsWin)
				{
					path = [
						"/usr/local/lib/digitalme/bin",
						"/usr/lib/digitalme/bin",
						userHome + "/digitalme/bin"];
				}
				
				for( iLoop = 0; iLoop < path.length; iLoop++) 
				{
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
						
			if( !bFound) 
			{
				if( bIsMac)
				{
					IdentitySelector.reportError( "findIdentitySelectorExe",
						"Unable to locate an identity selector.  " + 
						"Please make sure one is installed " +
						"on your desktop, in the Applications folder, or in the " +
						"Utilities folder.");
				}
				else
				{
					IdentitySelector.reportError( "findIdentitySelectorExe",
						"Unable to locate an identity selector.  " +
						"Please make sure one is installed.");
				}
			}
		}
		catch( e2) 
		{
			IdentitySelector.reportError( "findIdentitySelectorExe",
				"Unable to locate an identity selector.  " + 
				"Please make sure one is installed.");
		}
	
		IdentitySelector.logMessage( "findIdentitySelectorExe", 
			"IdentitySelector EXE = " + exePath);
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
	}
};

/****************************************************************************
Desc:
****************************************************************************/
window.addEventListener( "load",
	IdentitySelector.onInstall, true);
