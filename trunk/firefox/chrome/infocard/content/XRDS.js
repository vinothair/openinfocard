
//var xrdsHelper = new IdentitySelectorUtils("IdentitySelectorXrds");


var listObserver = {
   onDragOver : function (event, flavour, session) {
      IdentitySelector.logMessage("onDragOver: " + flavour.contentType);
      }
   , onDrop : function (evt, transferData, session) {
      IdentitySelector.logMessage("onDrop: " + transferData.data);
      // event.target.setAttribute("value",transferData.data);
      }
   , getSupportedFlavours : function () {
      var flavours = new FlavourSet();
      // flavours.appendFlavour("text/unicode");
      flavours.appendFlavour("application/x-informationcard+id");
      return flavours;
      }
};

/***************************************************************************
 * Desc:
 **************************************************************************/
function nsResolver(prefix) {
   var ns = {
      'xrds' : 'xri://$xrds', 'xrd': 'xri://$XRD*($v*2.0)'};
   return ns[prefix] || null;
}

/***************************************************************************
 * Desc:
 **************************************************************************/
function xrdsListener(doc, hrefStr) {
   this.doc = doc;
   this.hrefStr = hrefStr;
   this.onError = function(error) {
      IdentitySelector.logMessage("xrdsListener:onError", "error=" + error);
      };
   this.onReady = function(xrds) {
      try {
         var elts = xrds.getElementsByTagName("Service");
         for (var i = 0; i < elts.length; i++) {
            var type = "" + elts[i].getElementsByTagName("Type")[0].firstChild.nodeValue + "";
            if (type.indexOf("http://infocardfoundation.org/policy/1.0/login") == 0) {
               var uri = "" + elts[i].getElementsByTagName("URI")[0].firstChild.nodeValue ;
               doc.__identityselector__.icLoginPolicyUri = uri;
               IdentitySelector.logMessage("xrdsListener:onReady", "IC Login Service Policy: " + doc.__identityselector__.icLoginPolicy);
               InformationCardXrds.retrieveIcLoginServicePolicy(doc, doc.__identityselector__.icLoginPolicyUri);
               }
            else {
               if (type.indexOf("http://infocardfoundation.org/service/1.0/login") == 0) {
                  var uri = "" + elts[i].getElementsByTagName("URI")[0].firstChild.nodeValue ;
                  doc.__identityselector__.icLoginService = uri;
                  IdentitySelector.logMessage("xrdsListener:onReady", "IC Login Service: " + doc.__identityselector__.icLoginService);
                  }
               else {
                  IdentitySelector.logMessage("xrdsListener:onReady", "Service: type=" + type + ":" + typeof(type) + " URI=" + elts[i].getElementsByTagName("URI")[0].firstChild.nodeValue);
                  }
               }
            }
         // for (var i in xrds) {
         // IdentitySelector.logMessage("xrdsListener:onReady", "i=" + i
			// + " type=" +
         // typeof(i));
         // }
         var response = new XML (Components.classes['@mozilla.org/xmlextras/xmlserializer;1'].createInstance (Components.interfaces.nsIDOMSerializer).serializeToString(xrds.documentElement));
         doc.__identityselector__.xrds = response;
         IdentitySelector.logMessage("xrdsListener:onReady", "response=" + response);
         // var elts = xrds.evalutate('Service', xrds, nsResolver,
         // XPathResult.UNORDERED_NODE_ITERATOR_TYPE, null);
         }
      catch(e) {
         IdentitySelector.logMessage("xrdsListener:onReady", "Error: " + e);
         }
      };
}

/***************************************************************************
 * Desc:
 **************************************************************************/
function icLoginServiceListener(doc, hrefStr) {
   this.doc = doc;
   this.hrefStr = hrefStr;
   this.onError = function(error) {
      IdentitySelector.logMessage("icLoginServiceListener:onError", "error=" + error);
      };
   this.onReady = function(xrds) {
      try {
         var response = new XML (Components.classes['@mozilla.org/xmlextras/xmlserializer;1'].createInstance (Components.interfaces.nsIDOMSerializer).serializeToString(xrds.documentElement));
         doc.__identityselector__.icLoginPolicy = response;
         IdentitySelector.logMessage("icLoginServiceListener:onReady", "response=" + response);
         // var elts = xrds.evalutate('Service', xrds, nsResolver,
         // XPathResult.UNORDERED_NODE_ITERATOR_TYPE, null);
         if ((doc.defaultView != undefined) && (doc.defaultView)) {
             var docWindow = doc.defaultView;
             docWindow.addEventListener("dragdrop", InformationCardDragAndDrop.onWindowDragDrop, false);
            InformationCardStatusbar.showStatusbarIcon(document, true);
            }
         }
      catch(e) {
         IdentitySelector.logMessage("icLoginServiceListener:onReady", "Error: " + e);
         }
      };
}		

var InformationCardXrds = {
		
	retrieveXrds : function(doc, hrefStr) {
      IdentitySelector.logMessage("retrieveXrds: doc=" + doc.location.href + " href=", hrefStr);
      InformationCardXrds.retrieveX(doc, hrefStr, xrdsListener);
      }
	, retrieveIcLoginServicePolicy : function(doc, hrefStr) {
		InformationCardXrds.retrieveX(doc, hrefStr, icLoginServiceListener);
      }
	, retrieveX : function(doc, hrefStr, listenerO) {
      try {
         if (typeof(hrefStr) == 'string') {
            var i = hrefStr.indexOf("://");
            if (i == - 1) {
               // it is not an URL. Try to build an URL from the baseURI of the
               // document.
               var baseUri = doc.baseURI;
               if ((baseUri != null) && (baseUri.length > 0)) {
                  if ((baseUri.length - 1) == baseUri.lastIndexOf('/')) {
                     // ends with /
                     hrefStr = baseUri + hrefStr;
                     }
                  else {
                     hrefStr = baseUri + '/' + hrefStr;
                     }
                  IdentitySelector.logMessage("retrieveXrds: href=", hrefStr);
                  }
               // else no baseUri
               }
            // else its an URL. Go ahead.
            }
         // else not string but document
         var sameSchemeAndDomain = InformationCardHelper.sameSchemeAndDomain(doc, hrefStr);
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
         }
      catch(e) {
         IdentitySelector.logMessage("retrieveXrds: ", e);
         }
      }

	  // ***********************************************************************
	  // Method: processHtmlLinkElements
	  // ***********************************************************************
	  , processHtmlLinkElements : function(event) {
//		  var doc = event.originalTarget;
		var browser = gBrowser.selectedTab.linkedBrowser;
			// browser is the XUL element of the browser that's just been selected
			var doc = gBrowser.selectedBrowser.contentDocument;

		  var dispatchEvents = true;
		  
		  IdentitySelector.logMessage("processHtmlLinkElements", " start: " + doc.location.href);
	      if( doc.wrappedJSObject != undefined) {
	         doc = doc.wrappedJSObject;
	         }
	      if (IdentitySelector.disabled == true) {
	         IdentitySelector.logMessage("processHtmlLinkElements", " ID selector is disabled. Exiting");
	         return;
	         }
	      
	      if (!(doc instanceof HTMLDocument)) {
		      IdentitySelector.logMessage("processHtmlLinkElements", " no html document. Exiting");
	    	  return;
	      }
	      
	      if( doc.__identityselector__ === undefined) {
	         IdentitySelector.runInterceptScript(doc);
	         }
	      var linkElems = doc.getElementsByTagName( "LINK");
	      for( var i = 0; i < linkElems.length; i++) {
	         var linkElem = linkElems[ i];
	         var relStr = linkElem.getAttribute( "REL");
	         if( (relStr != null) && (relStr == "xrds.metadata")) {
	            var hrefStr = linkElem.getAttribute( "HREF");
	            if (hrefStr == null) {
	               continue;
	               }
	            else {
	               IdentitySelector.logMessage("processHtmlLinkElements: href=", hrefStr);
	               if( doc.__identityselector__.xrds === undefined) {
	                  var data = doc.__identityselector__.data;
	                  data.xrds_metadata_href = hrefStr;
	                  InformationCardXrds.retrieveXrds(doc, hrefStr);
	                  // async
	                  }
	               else {
	                  IdentitySelector.logMessage("processHtmlLinkElements: already loaded: href=", hrefStr);
	                  }
	               return;
	               }
	            }
	         else {
	            continue;
	            }
	         }
	      }
	
};

try {
	IdentitySelector.logMessage( "InformationCardXrds", "start");
	
//	window.addEventListener("load", 
//			function(event) {InformationCardXrds.processHtmlLinkElements(event);}, false );
	window.addEventListener("DOMContentLoaded", 
			function(event) {InformationCardXrds.processHtmlLinkElements(event);}, false );

	window.addEventListener("unload", function(event) {InformationCardXrds.processHtmlLinkElements(event);}, false);
}
catch( e) {
	IdentitySelector.reportError( "InformationCardXrds window.addEventListener failed: ", e);
}
