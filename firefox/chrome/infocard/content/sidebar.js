
function sidebarLoad(){
	// infocard: sidebarLoad start. href=chrome://infocard/content/cardSidebar.xul
	icDebug("sidebarLoad start. href=" + window.document.location.href );
	
    var stringsBundle = document.getElementById("string-bundle");

    var policy = null;
    	
    var rpIdentifier = null;

    if (policy != null && policy.hasOwnProperty("cert")) {
		var relyingPartyCertB64 = policy["cert"];
	    rpIdentifier = computeRpIdentifier(relyingPartyCertB64);
    }

    var cardFile = CardstoreToolkit.readCardStore();
    var cardArea = document.getElementById("cardselection");
    var latestCard;
    var selectMe;
    var beenThere = false;
    var count = 0;
    var scrolledIntoView = false;
    for each (c in cardFile.infocard) {
	    var cardClass = "contact";
    	if (policy != null) {
			cardClass = computeCardClass(c, policy);
		}
        latestCard = createItem(c, cardClass);
        selectMe = c;
        cardArea.appendChild(latestCard);
        count++;

        if (!beenThere) {
         if (rpIdentifier != null) {
	      for each (rpId in c.rpIds) {
           //debug("RpId:" + rpId + " RpIdentifier:" + rpIdentifier);
	       if (rpId == rpIdentifier) {
	        //debug("been there at: " + policy["cn"]);
            beenThere = true;
            if (scrolledIntoView == false) {
            	var xpcomInterface = cardArea.scrollBoxObject.QueryInterface(Components.interfaces.nsIScrollBoxObject);
   				xpcomInterface.ensureElementIsVisible(latestCard);
   	        	scrolledIntoView = true;
            }
            break;
           }
          }
         }
        }
    }
    
    if ( count != 0) {
    	if (policy != null) {
	        var label = document.getElementById("notify");
	        if (label != null) {
				var site = "Unknown";
				if (policy.hasOwnProperty("cn")) {
					site = policy["cn"];
				} else if (policy.hasOwnProperty("url")) {
					site = policy["url"];
				}
		        var please = stringsBundle.getFormattedString('pleaseselectacard', [site]);
		        label.setAttribute("value", please);
	        }
	    } else {
	        var label = document.getElementById("notify");
	        if (label != null) {
	        	label.setAttribute("value", "card management");
	        }
	    }
	} else {
        var label = document.getElementById("notify");
        if (label != null) {
			var button = stringsBundle.getString('newcard');
	        var youdont = stringsBundle.getFormattedString('youdonthaveanycards', [button]);
	        label.setAttribute("value", youdont);
        }
    }
	if (policy != null) {
		var serializedPolicy = JSON.stringify(policy);
		if (TokenIssuer.initialize() == true) {
			var issuerLogoURL = TokenIssuer.getIssuerLogoURL(serializedPolicy);
			icDebug("issuerLogoURL=" + issuerLogoURL);
			if (issuerLogoURL != undefined) {
				var issuerlogo = document.getElementById("issuerlogo");
				issuerlogo.src = issuerLogoURL;
				issuerlogo.hidden = false;
				var issuerlogo_label = document.getElementById("issuerlogo_label");
	            issuerlogo_label.hidden = false;
				var issuer_hbox = document.getElementById("issuer_hbox");
	            issuer_hbox.hidden = false;
			}
		} else {
			icDebug("load: error initializing the TokenIssuer");
			icDebug("load: window.java=" + window.java + " window.document.location.href=" + window.document.location.href);
//			alert("Error: Initializing the TokenIssuer");
			// return;
		}
	}
	if (policy != null) {
	    var privacyUrl = policy["privacyUrl"];
        if (privacyUrl != null) {
	    icDebug("privacyUrl " + privacyUrl);
             var showPrivacyStatementElm = document.getElementById('privacy_label');
             showPrivacyStatementElm.addEventListener("click", showPrivacyStatement, false);
             showPrivacyStatementElm.hidden = false;
	    }
	}
	if (!beenThere) {
		if (policy != null) {
			 if (policy.hasOwnProperty("cn")) {
				 icDebug("never been here: " + policy["cn"]);
			 } else if (policy.hasOwnProperty("url")) {
			 	icDebug("never been here: " + policy["url"]);
			 } else {
			 	icDebug("never been here");
			 }
		}
		if (policy !== null) { // no card management
			var firstTimeVisit = document.getElementById('firstTimeVisit');
			if (firstTimeVisit != null) {
				var labelText;
				try {
					labelText = stringsBundle.getString('firsttimevisit');
				} catch (e) {
				 icDebug("firstTime: exception=" + e);
				 labelText = "This is your first visit to this site. Think!";
				}
				icDebug("firstTime: " + labelText);
				firstTimeVisit.setAttribute("value", labelText);
				var firstTimeVisitBox = document.getElementById('firstTimeVisitBox');
				firstTimeVisitBox.setAttribute("hidden", "false");
			}
		} else { // else card management
			var firstTimeVisitBox = document.getElementById('firstTimeVisitBox');
			if (firstTimeVisitBox !== null) {
				firstTimeVisitBox.setAttribute("hidden", "false");
			} // else sidebar
		}
	}
	
	if ((extraParamsCardId !== undefined) && (extraParamsCardId !== null)) {
		var choosenCard = getCard(extraParamsCardId);
		if ((choosenCard === undefined) || (choosenCard === null)) {
			Components.utils.reportError("sidebarLoad: extraParamsCardId=" + extraParamsCardId + " but card not found");
			alert("internal error: card not found in cardstore.");
			window.close();
			return;
		}
		setCard(choosenCard);
		var chossenCardElement = document.getElementById(extraParamsCardId);
		if (chossenCardElement !== null) {
			var xpcomInterface = cardArea.scrollBoxObject.QueryInterface(Components.interfaces.nsIScrollBoxObject);
			xpcomInterface.ensureElementIsVisible(chossenCardElement);
		}
	} else {
		 icDebug("extraParamsCardId === undefined");
	}

}

function sidebardOnLoad() {
	IdentitySelectorDiag.logMessage("sidebar", "onLoad event");
}

try {
	IdentitySelectorDiag.logMessage("sidebar", "start");
	
    window.addEventListener( "load", function(event){gBrowser.addEventListener("load", sidebardOnLoad, true);}, false);
           
    //window.addEventListener( "unload", IcXrdsStartHelper.onUnload, false);
} catch( e) {
	IdentitySelectorDiag.reportError( "sidebar: ", e);
}

