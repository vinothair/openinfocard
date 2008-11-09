var InformationCardStatusbar = {

	/*******************************************************************************
	 * Desc:
	 ******************************************************************************/
	showStatusbarIcon : function(doc, show) {
		var statusBarImage = doc.getElementById("ic-status-bar-image");
		if (statusBarImage != null) {
			if (show == true) {
				statusBarImage.src = "chrome://infocard/content/img/infocard_23x16.png";
			} else {
				statusBarImage.src = "chrome://infocard/content/img/infocard_23x16-crossed.png";
			}
		} else {
			// the object is created before the status-bar is created
			IdentitySelector.logMessage("showStatusbarIcon",
					"Internal Warning: ic-status-bar-image not found");
		}
	},

	// ***********************************************************************
	// Method: tabSelected
	// ***********************************************************************
	tabSelected : function(event) {
		IdentitySelector.logMessage("tabSelected: ", "start");
		var browser = gBrowser.selectedTab.linkedBrowser;
		// browser is the XUL element of the browser that's just been selected
		var doc = gBrowser.selectedBrowser.contentDocument;
		if (doc instanceof HTMLDocument) {
			IdentitySelector.logMessage("tabSelected: ",
					"document is HTMLDocument: " + doc.location.href);
			if (doc.wrappedJSObject != undefined) {
				IdentitySelector.logMessage("tabSelected: ",
						"document was wrapped: " + doc.location.href);
				doc = doc.wrappedJSObject;
			}
			if (doc.__identityselector__ === undefined) {
				IdentitySelector.logMessage("tabSelected: ", "this false");
				InformationCardStatusbar.showStatusbarIcon(document, false);
			} else {
				if (((doc.__identityselector__.icLoginService != undefined) && (doc.__identityselector__.icLoginPolicy != undefined))
						|| (doc.__identityselector__.notificationBoxHidden != undefined)) {
					InformationCardStatusbar.showStatusbarIcon(document, true);
				} else {
					IdentitySelector.logMessage("tabSelected: ", "that false");
					InformationCardStatusbar.showStatusbarIcon(document, false);
				}
			}

			var sidebarElement = document.getElementById("sidebar");
			if (sidebarElement != null) {
				var sidebarWindow = sidebarElement.contentWindow;
				if (sidebarWindow
						&& sidebarWindow.location.href == "chrome://infocard/content/cardSidebar.xul") {
					sidebarWindow.reload();
				}
			}
		} else {
			IdentitySelector.logMessage("tabSelected: ",
					"document is no HTMLDocument: " + doc.location.href);
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
        if( doc.wrappedJSObject)
        {
        	doc = doc.wrappedJSObject;
        }
        IdentitySelector.logMessage("InformationCardStatusbar.onICardObjectLoaded",
				"document: " + doc.location.href);
        InformationCardStatusbar.showStatusbarIcon(document, true);
    },
    
    onLoad : function(event) {
        doc = event.target;
        if( doc.wrappedJSObject)
        {
        	doc = doc.wrappedJSObject;
        }

		var container = gBrowser.tabContainer; 
		container.addEventListener("TabSelect", InformationCardStatusbar.tabSelected, false);
		window.addEventListener("ICObjectLoaded",InformationCardStatusbar.onICardObjectLoaded, false);	
		window.addEventListener("DOMContentLoaded", 
				function(event) {InformationCardStatusbar.onDomContentLoaded(event);}, false );

		if (doc.__identityselector__ === undefined) {
			InformationCardStatusbar.showStatusbarIcon(document, false);
		} else {
			if (((doc.__identityselector__.icLoginService != undefined) && (doc.__identityselector__.icLoginPolicy != undefined))
					|| (doc.__identityselector__.notificationBoxHidden != undefined)) {
				InformationCardStatusbar.showStatusbarIcon(document, true);
			} else {
				InformationCardStatusbar.showStatusbarIcon(document, false);
			}
		}

	
    }
    
    , onDomContentLoaded : function(event) {
    	var doc = event.originalTarget;
        if (doc instanceof HTMLDocument) {
            if( doc.wrappedJSObject != undefined) {
               doc = doc.wrappedJSObject;
            }
    		if (doc.__identityselector__ === undefined) {
    			InformationCardStatusbar.showStatusbarIcon(document, false);
    		} else {
    			if (((doc.__identityselector__.icLoginService != undefined) && (doc.__identityselector__.icLoginPolicy != undefined))
    					|| (doc.__identityselector__.notificationBoxHidden != undefined)) {
    				InformationCardStatusbar.showStatusbarIcon(document, true);
    			} else {
    				InformationCardStatusbar.showStatusbarIcon(document, false);
    			}
    		}
        }
    }
    
    , statusbarClick : function() {
        var doc = gBrowser.selectedBrowser.contentDocument;
        IdentitySelector.logMessage( "statusbarClick", "clicked doc.location.href=" + doc.location.href);
        if (doc instanceof HTMLDocument) {
           if( doc.wrappedJSObject != undefined) {
              doc = doc.wrappedJSObject;
              }
           if( !(doc.__identityselector__ === undefined)) {
              if ((doc.__identityselector__.icLoginService != undefined) && (doc.__identityselector__.icLoginPolicy != undefined)) {
            	  InformationCardHelper.callIdentitySelector(doc);
                 }
              else {
                 if (doc.location != undefined) {
                    IdentitySelector.logMessage( "statusbarClick", "The site " + doc.location.href + " does not support Information Cards on this page.\n" + "To learn more about Information Cards please visit the Information Card Foundation at\n" + "https://informationcard.net/");
                    alert("The site " + doc.location.href + " does not support Information Cards.\n" + "To learn more about Information Cards please visit the Information Card Foundation at\n" + "https://informationcard.net/");
                    }
                 else {
                    IdentitySelector.logMessage( "statusbarClick", "Information Cards are not supported here.\n" + "To learn more about Information Cards please visit the Information Card Foundation at\n" + "https://informationcard.net/");
                    alert("Information Cards are not supported here.\n" + "To learn more about Information Cards please visit the Information Card Foundation at\n" + "https://informationcard.net/");
                    }
                 }
              }
           else {
              if (doc.location != undefined) {
                 IdentitySelector.logMessage( "statusbarClick", "The site " + doc.location.href + " does not support Information Cards on this page.\n" + "To learn more about Information Cards please visit the Information Card Foundation at\n" + "https://informationcard.net/");
                 alert("The site " + doc.location.href + " does not support Information Cards.\n" + "To learn more about Information Cards please visit the Information Card Foundation at\n" + "https://informationcard.net/");
                 }
              else {
                 IdentitySelector.logMessage( "statusbarClick", "Information Cards are not supported here.\n" + "To learn more about Information Cards please visit the Information Card Foundation at\n" + "https://informationcard.net/");
                 alert("Information Cards are not supported here.\n" + "To learn more about Information Cards please visit the Information Card Foundation at\n" + "https://informationcard.net/");
                 }
              }
           }
        }

};

try {
	IdentitySelector.logMessage( "Statusbar", "start");
	
	window.addEventListener("load", 
			function(event) {InformationCardStatusbar.onLoad(event);}, false );

//	window.addEventListener("load", 
//			function() {
//				gBrowser.addEventListener("load", InformationCardStatusbar.onLoad, true);
//			}, false );
	
//	   var tempFunction = function(event) = { alert("pfump"); };
//	   
//	   var statusbarObjectLoadedFunction = function(event) { InformationCardStatusbar.onICardObjectLoaded(event); };
//	   window.addEventListener("load",  alert
//	   , false);
//	   window.addEventListener("unload", function () {
//		      var container = gBrowser.tabContainer; 
//		      container.removeEventListener("TabSelect", tempFunction, false);
//		      container.removeEventListener("ICObjectLoaded",tempFunction, false);
//		   }
//	   , false);
	   }
catch( e) {
	IdentitySelector.reportError( "statusbar window.addEventListener failed: ", e);
}



