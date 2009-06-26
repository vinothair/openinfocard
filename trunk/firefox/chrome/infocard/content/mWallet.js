
function mwDebug(msg) {
	  var debug = Components.classes["@mozilla.org/consoleservice;1"].getService(Components.interfaces.nsIConsoleService);
	  debug.logStringMessage("mWallet: " + msg);
}

function cancel(){
	mwDebug("cancel");
    var doc = window.document;
    var event = doc.createEvent("Events");
    event.initEvent("CancelIdentitySelector", true, true);
    window.dispatchEvent(event);

   	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
   		try {
   			window.arguments[1](null);
   		} catch (e) {
   			Components.utils.reportError("mWallet.cancel: " + e);
   		}
	}
    window.close();
}

function getPolicy(){
	var policy = null;
	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
	    policy = window.arguments[0];
	}
	return policy;
}

function mWalletUnload(){
	mwDebug("mWalletUnload start. href=" + window.document.location.href );
	
	try {
		TokenIssuer.endCardSelection();
	} catch (e) {
		Components.utils.reportError("mWalletUnload:endCardSelection " + e);
	}
	try {
		TokenIssuer.phoneFini();
	} catch (e) {
		Components.utils.reportError("mWalletUnload:phoneFini " + e);
	}
	try {
		TokenIssuer.finalize();
	} catch (e) {
		Components.utils.reportError("mWalletUnload:finalize " + e);
	}
}

function pollForCardsCallback(policy){
	var card = null;
	var serializedPolicy = JSON.stringify(policy);
	card = TokenIssuer.phoneSelectCard(serializedPolicy);
	if (card !== null) {
		try {
			mwDebug("pollForCardsCallback: " + card);
			if (policy.hasOwnProperty("timeoutId")) {
				window.clearInterval(policy["timeoutId"]);
			}
			var givenname;
			var surname;
			var emailaddress;
			try {
				var cardXml = new XML(card);
				for (var i = 0; i < cardXml.Attribute.length(); i++) {
					var attr = cardXml.Attribute[i];
					mwDebug(attr.toString());
					var name = attr.@name.toString();
					var value = attr.child(0);
					mwDebug("pollForCardsCallback: name=" + name + " value=" + value);
					if (name === "email") {
						emailaddress = value;
					} else if (name === "name") {
						var x = value.split(' ');
						givenname = x[0];
						surname = x[1];
					} else if (name === "surname") {
						surname = value;
					} else if (name === "givenname") {
						givenname = value;
					} else {
						Components.utils.reportError("mWallet: unsupported Attributename name=" + name + " value=" + value);
					}
				 }
			} catch (e) {
				mwDebug("pollForCardsCallback: xml error: " + e);
				return;
			}
			var infocard = "<infocard>" +
			"<id>" + cardXml.name + "</id>" +
			"<privatepersonalidentifier>" + cardXml.name + "</privatepersonalidentifier>" +
			"<carddata><selfasserted>" + 
			"<givenname>" + givenname + "</givenname>" + 
			"<surname>" + surname + "</surname>" +
			"<emailaddress>" + emailaddress + "</emailaddress>" + 
			"</selfasserted></carddata>" + 
			"</infocard>";
	        policy["type"] = "selfAsserted";
	        policy["card"] = infocard;
	        
	        var serializedPolicy = JSON.stringify(policy);
	        var tokenToReturn = TokenIssuer.getToken(serializedPolicy);
	
		   	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
		   		try {
		   			window.arguments[1](tokenToReturn);
		   		} catch (e) {
		   			Components.utils.reportError("mWallet.mWalletLoad: " + e);
		   		}
			}
		} finally {
			window.close();
		}
	}
}

function pollForPhoneAvailableCallback(policy){
	var card = null;
//	var serializedPolicy = JSON.stringify(policy);
	var label = document.getElementById("notify");
    if (label != null) {
    	var isPhoneAvailable = TokenIssuer.isPhoneAvailable();
    	var availableTxt;
    	if (isPhoneAvailable === false) {
    		availableTxt = "Please put your NFC-phone on the NFC-reader!";
    	} else {
    		if (isPhoneAvailable === true) {
    			availableTxt = "Thank you";
    			if (policy.hasOwnProperty("timeoutId")) {
    				window.clearInterval(policy["timeoutId"]);
    			}
    			TokenIssuer.beginCardSelection();
    			policy["timeoutId"] = window.setInterval(function(){pollForSelectedCardCallback(policy);}, 1000, true);
    		} else {
    			availableTxt = "boink: " + isPhoneAvailable + " typeof(isPhoneAvailable)=" + typeof(isPhoneAvailable);
    		}
    	}
    	mwDebug("pollForPhoneAvailableCallback: " + availableTxt);
    	label.setAttribute("value", availableTxt );
    }
}

function _card2token(policy, card) {
	var givenname;
	var surname;
	var emailaddress;
	try {
		mwDebug("_card2token: typeof(card)=" + typeof(card));
		var aCard = "" + card;
		mwDebug("_card2token: typeof(aCard)=" + typeof(aCard));
//		var cardXml = new XML(Components.classes['@mozilla.org/xmlextras/xmlserializer;1'].createInstance(Components.interfaces.nsIDOMSerializer).serializeToString(card));
		var cardXml = new XML(aCard);
	} catch (e) {
		Components.utils.reportError("_card2token: xml error: " + e + "\n" + card);
		return null;
	}
	for (var i = 0; i < cardXml.CardPortfolio.Attribute.length(); i++) {
		var attr = cardXml.CardPortfolio.Attribute[i];
		mwDebug(attr.toString());
		var name = attr.@name.toString();
		var value = attr.child(0);
		mwDebug("_card2token: name=" + name + " value=" + value);
		if (name === "email") {
			emailaddress = value;
		} else if (name === "name") {
			var x = value.split(' ');
			givenname = x[0];
			surname = x[1];
		} else if (name === "surname") {
			surname = value;
		} else if (name === "givenname") {
			givenname = value;
		} else {
			Components.utils.reportError("_card2token: unsupported Attributename name=" + name + " value=" + value);
		}
	}
	var infocard = "<infocard>" +
	"<id>" + cardXml.name + "</id>" +
	"<privatepersonalidentifier>" + cardXml.name + "</privatepersonalidentifier>" +
	"<carddata><selfasserted>" + 
	"<givenname>" + givenname + "</givenname>" + 
	"<surname>" + surname + "</surname>" +
	"<emailaddress>" + emailaddress + "</emailaddress>" + 
	"</selfasserted></carddata>" + 
	"</infocard>";
    policy.type = "selfAsserted";
    policy.card = infocard;
    
    var serializedPolicy = JSON.stringify(policy);
    var tokenToReturn = TokenIssuer.getToken(serializedPolicy);
    return tokenToReturn;
}

function pollForSelectedCardCallback(policy){
	var card = TokenIssuer.getSelectedCard();
	if (card !== null) {
		try {
			mwDebug("pollForSelectedCardCallback: " + card);
			if (policy.hasOwnProperty("timeoutId")) {
				window.clearInterval(policy["timeoutId"]);
			}
			
			var token = _card2token(policy, card);
			
		   	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
		   		try {
		   			window.arguments[1](token);
		   		} catch (e) {
		   			Components.utils.reportError("mWallet.pollForSelectedCardCallback: " + e);
		   		}
			}
		} finally {
			window.close();
		}
	}
}

function mWalletLoad(policyParam){
	mwDebug("mWalletLoad start. href=" + window.document.location.href );
	var tokenIssuerInitialized = TokenIssuer.initialize();
	if (tokenIssuerInitialized == false) {
		mwDebug("mWalletUnload could not initialize the tokenissuer" );
		return;
	}
	
    var policy;
    if (policyParam == undefined) {
    	policy = getPolicy();
    } else {
    	policy = policyParam;
    }

	mwDebug("cardManagerLoad weiter gehts" );
	var serializedPolicy = JSON.stringify(policy);
	
	var isPhoneAvailable = TokenIssuer.isPhoneAvailable();
	if (isPhoneAvailable) {
		if (false) {
			var card = TokenIssuer.phoneSelectCard(serializedPolicy);
			if (card !== null) {
				return card;
			}
			
			policy["timeoutId"] = window.setInterval(function(){pollForCardsCallback(policy);}, 1000, true);
		} else {
			TokenIssuer.beginCardSelection();
			policy["timeoutId"] = window.setInterval(function(){pollForSelectedCardCallback(policy);}, 1000, true);
		}
	} else {
		policy["timeoutId"] = window.setInterval(function(){pollForPhoneAvailableCallback(policy);}, 1000, true);
	}
}

