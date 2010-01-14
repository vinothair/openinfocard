
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

function isPhoneAvailable(){
  var msg;
  var label = document.getElementById("notify");
  var stringsBundle = document.getElementById("string-bundle");

  var phoneAvailable = TokenIssuer.isPhoneAvailable();
  if (phoneAvailable === true) {
    TokenIssuer.resetWalletException();
    if (label) {
      msg = "Thank you";
      if (stringsBundle) {
        var text = stringsBundle.getString('thankyou');
        if (text) {
          msg = text;
        }
      }
      label.setAttribute("value", msg );
    }
    return true;
  }
  var exception = TokenIssuer.getWalletException();
  if (exception) {
    Components.utils.reportError("mWallet.isPhoneAvailable: " + exception);
    if (label) {
      var str = "" + exception + "";
      if (str.indexOf("WalletIsLockedException") >= 0) {
        var msg = "Please unlock the wallet on your phone";
        if (stringsBundle) {
          var text = stringsBundle.getString('walletislocked');
          if (text) {
            msg = text;
          }
          label.setAttribute("value", msg );
        }
      }
      
    }
  } else {
    if (label) {
      msg = "Please put your NFC-phone on the NFC-reader!";
      if (stringsBundle) {
        var text = stringsBundle.getString('pleaseputyournfcphoneonthereader');
        if (text) {
          msg = text;
        }
      }
      label.setAttribute("value", msg );
    }
  }
  return false;
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

function pollForPhoneAvailableCallback(policy){
	var card = null;

	var phoneAvailable = isPhoneAvailable();

	if (phoneAvailable === true) {
		if (policy.hasOwnProperty("timeoutId")) {
			window.clearInterval(policy["timeoutId"]);
		}
		TokenIssuer.beginCardSelection();
		policy["timeoutId"] = window.setInterval(function(){pollForSelectedCardCallback(policy);}, 1000, true);
	}
}

function _card2token(policy, card) {
	var givenname;
	var surname;
	var emailaddress;
	var modulus;
	var cardXml;
	try {
		mwDebug("_card2token: typeof(card)=" + typeof(card));
		var aCard = "" + card;
		mwDebug("_card2token: typeof(aCard)=" + typeof(aCard));
//		var cardXml = new XML(Components.classes['@mozilla.org/xmlextras/xmlserializer;1'].createInstance(Components.interfaces.nsIDOMSerializer).serializeToString(card));
		cardXml = new XML(aCard);
	} catch (e) {
		Components.utils.reportError("_card2token: xml error: " + e + "\n" + card);
		return null;
	}
	if (cardXml) {
	  try {
      mwDebug("_card2token: typeof(cardXml)=" + typeof(cardXml));
      mwDebug("_card2token: cardXml=" + cardXml.toXMLString());
      mwDebug("_card2token: cardXml.Attribute.length()=" + cardXml.Attribute.length());
    	for (var i = 0; i < cardXml.Attribute.length(); i++) {
    		var attr = cardXml.Attribute[i];
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
    		} else if (name === "Modulus") {
          modulus = value;
    		} else {
    		  mwDebug("_card2token: unsupported Attributename name=" + name + " value=" + value);
    		}
    	}
	  } catch (ee) {
      mwDebug("_card2token: Exception: " + ee);
      Components.utils.reportError("_card2token: Exception: " + ee);
	  }
	} else {
    Components.utils.reportError("_card2token: cardXml is undefined" + cardXml);
	}
	
	
//	infocard: choosenCard=<infocard>
//  <name>sechs</name>
//  <type>selfAsserted</type>
//  <version>1</version>
//  <id>19064</id>
//  <privatepersonalidentifier>446e7f7a527d75e24329ecd2982769311c36dff8</privatepersonalidentifier>
//  <ic:InformationCardPrivateData xmlns:ic="http://schemas.xmlsoap.org/ws/2005/05/identity">
//    <ic:MasterKey>446e7f7a527d75e24329ecd2982769311c36dff8</ic:MasterKey>
//    <ic:ClaimValueList>
//      <ic:ClaimValue Uri="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname">
//        <ic:Value>Axel</ic:Value>
//      </ic:ClaimValue>
//      <ic:ClaimValue Uri="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname">
//        <ic:Value>Nennker</ic:Value>
//      </ic:ClaimValue>
//      <ic:ClaimValue Uri="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress">
//        <ic:Value>axel@nennker.de</ic:Value>
//      </ic:ClaimValue>
//    </ic:ClaimValueList>
//  </ic:InformationCardPrivateData>
//  <ic:SupportedClaimTypeList xmlns:ic="http://schemas.xmlsoap.org/ws/2005/05/identity">
//    <ic:SupportedClaimType Uri="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname">
//      <ic:DisplayTag>Vorname:</ic:DisplayTag>
//    </ic:SupportedClaimType>
//    <ic:SupportedClaimType Uri="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname">
//      <ic:DisplayTag>Nachname:</ic:DisplayTag>
//    </ic:SupportedClaimType>
//    <ic:SupportedClaimType Uri="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress">
//      <ic:DisplayTag>Email:</ic:DisplayTag>
//    </ic:SupportedClaimType>
//    <ic:SupportedClaimType Uri="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier"/>
//  </ic:SupportedClaimTypeList>
//  <rpIds>ef328e98c0fd49dab2629400914924156616f2e2</rpIds>
//</infocard>

	var masterKey;
	if (modulus) {
	  masterKey = modulus;
	} else {
	  masterKey = "446e7f7a527d75e24329ecd2982769311c36dff8"; // FIXME
	}

	var infocard = "<infocard>" +
	"<id>" + cardXml.@name.toString() + "</id>" +
	"<privatepersonalidentifier>" + cardXml.@name.toString() + "</privatepersonalidentifier>" +
	"<ic:InformationCardPrivateData xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\">" + 
	  "<ic:MasterKey>" + masterKey + "</ic:MasterKey>" +
  	"<ic:ClaimValueList>" +
    	"<ic:ClaimValue Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname\">" +
    	"<ic:Value>" + givenname + "</ic:Value>" + 
    	"</ic:ClaimValue>" +
    	"<ic:ClaimValue Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname\">" +
      "<ic:Value>" + surname + "</ic:Value>" + 
      "</ic:ClaimValue>" +
    	"<ic:ClaimValue Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress\">" +
      "<ic:Value>" + emailaddress + "</ic:Value>" + 
      "</ic:ClaimValue>" + 
  	"</ic:ClaimValueList>" +
	"</ic:InformationCardPrivateData>" + 
	"</infocard>";
	mwDebug("_card2token: infocard=" + infocard);
	
    policy.type = "selfAsserted";
    policy.card = infocard;
    
    var serializedPolicy = JSON.stringify(policy);
    var tokenToReturn = TokenIssuer.getToken(serializedPolicy);
    return tokenToReturn;
}

function pollForSelectedCardCallback(policy){
	var card = TokenIssuer.getSelectedCard();
	if (card) {
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
	} else {
	  var label = document.getElementById("notify");
    if (label != null) {
      var labelText = "Select a card on your phone";
      var stringsBundle = document.getElementById("string-bundle");
      if (stringsBundle) {
        var text = stringsBundle.getString('selectacardonyourphone');
        if (text) {
          labelText = text;
        }
      }
      label.setAttribute("value", labelText );
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
	
  var cancelselector = document.getElementById('cancelselector');
  if (cancelselector) {
    cancelselector.addEventListener("click", cancel, false);
  }
  
	var phoneAvailable = isPhoneAvailable();
	if (phoneAvailable) {
		TokenIssuer.beginCardSelection();
		policy["timeoutId"] = window.setInterval(function(){pollForSelectedCardCallback(policy);}, 1000, true);
	} else {
		policy["timeoutId"] = window.setInterval(function(){pollForPhoneAvailableCallback(policy);}, 1000, true);
	}
}

