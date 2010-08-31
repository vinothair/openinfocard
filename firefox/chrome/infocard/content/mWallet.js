
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
      delete policy.timeoutId;
    }

    try {
//    TokenIssuer.beginCardSelection();
      try {
        // remove certs for now FIXME
        if (policy.hasOwnProperty("cert")) {
          delete policy.cert;
        }
        if (policy.hasOwnProperty("chainLength")) {
          for (var i=0; i<policy.chainLength;i++) {
            delete policy["certChain"+i];
          }
          delete policy.chainLength;
        }
      } catch(ee) {
        // ignore missing certs
      }
      // FIXME
      // http://schemas.t-labs.de/ws/2010/10/identity/claims/shopname?v=T-Labs+Computershop
      if (policy.hasOwnProperty("optionalClaims")) {
        var optionalClaims = policy.optionalClaims;
        var i = optionalClaims.indexOf("http://schemas.t-labs.de/ws/2010/10/identity/claims/shopname?v=");
        if (i>=0) {
          var shopname = optionalClaims.substring(i+ ("http://schemas.t-labs.de/ws/2010/10/identity/claims/shopname?v=".length));
          policy.shopname = unescape(shopname);
        }
      }
      
      // http://schemas.t-labs.de/ws/2010/10/identity/claims/price?v=500+Euro
      if (policy.hasOwnProperty("optionalClaims")) {
        var optionalClaims = policy.optionalClaims;
        var i = optionalClaims.indexOf("http://schemas.t-labs.de/ws/2010/10/identity/claims/price?v=");
        if (i>=0) {
          var price = optionalClaims.substring(i + ("http://schemas.t-labs.de/ws/2010/10/identity/claims/price?v=".length));
          policy.price = unescape(price);
        }
      }
      policy.timestamp = (new Date()).toLocaleString();
      var serializedPolicy = JSON.stringify(policy);
      TokenIssuer.startCardSelection(serializedPolicy);
    } catch (e) {
      Components.utils.reportError("TokenIssuer.startCardSelection " + e);
      return;
    }
    policy["timeoutId"] = window.setInterval(function(){pollForSelectedCardCallback(policy);}, 1000, true);
  }
}

function _card2token(policy, card) {
  mwDebug("_card2token: card=" + card);
//  var claimValueList = "";
//  var cardname;
//  var givenname;
//  var surname;
//  var emailaddress;
//  var modulus;
//  var cardXml;
//  var domain;
//  var icon;
//  var number;
//  try {
//    mwDebug("_card2token: typeof(card)=" + typeof(card));
//    var aCard = "" + card;
//    mwDebug("_card2token: typeof(aCard)=" + typeof(aCard));
////    var cardXml = new XML(Components.classes['@mozilla.org/xmlextras/xmlserializer;1'].createInstance(Components.interfaces.nsIDOMSerializer).serializeToString(card));
//    cardXml = new XML(aCard);
//  } catch (e) {
//    Components.utils.reportError("_card2token: xml error: " + e + "\n" + card);
//    return null;
//  }
//  if (cardXml) {
//    try {
//      mwDebug("_card2token: typeof(cardXml)=" + typeof(cardXml));
//      mwDebug("_card2token: cardXml=" + cardXml.toXMLString());
//      mwDebug("_card2token: cardXml.Attribute.length()=" + cardXml.Attribute.length());
//      for (var i = 0; i < cardXml.Attribute.length(); i++) {
//        var attr = cardXml.Attribute[i];
//        mwDebug(attr.toString());
//        var name = attr.@name.toString();
//        var value = attr.child(0);
//        mwDebug("_card2token: name=" + name + " value=" + value);
//        if (name === "email") {
//          emailaddress = value;
//          claimValueList += "<ic:ClaimValue Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress\">" +
//          "<ic:Value>" + value + "</ic:Value>" + 
//          "</ic:ClaimValue>";
//        } else if (name === "name") {
//          cardname = value;
//          var x = cardname.split(' ');
//          givenname = x[0];
//          "<ic:ClaimValue Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname\">" +
//          "<ic:Value>" + givenname + "</ic:Value>" + 
//          "</ic:ClaimValue>";
//          surname = x[1];
//          claimValueList += "<ic:ClaimValue Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname\">" +
//          "<ic:Value>" + surname + "</ic:Value>" + 
//          "</ic:ClaimValue>";
//        } else if (name === "surname") {
//          surname = value;
//          claimValueList += "<ic:ClaimValue Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname\">" +
//          "<ic:Value>" + value + "</ic:Value>" + 
//          "</ic:ClaimValue>";
//        } else if (name === "givenname") {
//          givenname = value;
//          claimValueList += "<ic:ClaimValue Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname\">" +
//          "<ic:Value>" + value + "</ic:Value>" + 
//          "</ic:ClaimValue>";
//        } else if (name === "Modulus") {
//          modulus = value;
//        } else if (name === "number") {
//          number = value;
//          claimValueList += "<ic:ClaimValue Uri=\"http://schemas.t-labs.de/ws/2010/10/identity/claims/cardnumber\">" +
//          "<ic:Value>" + value + "</ic:Value>" + 
//          "</ic:ClaimValue>";
//        } else {
//          mwDebug("_card2token: unsupported Attributename name=" + name + " value=" + value);
//          claimValueList += "<ic:ClaimValue Uri=\"http://schemas.t-labs.de/ws/2010/10/identity/claims/" + name + "\">" +
//          "<ic:Value>" + value + "</ic:Value>" + 
//          "</ic:ClaimValue>";
//        }
//      }
//    } catch (ee) {
//      mwDebug("_card2token: Exception: " + ee);
//      Components.utils.reportError("_card2token: Exception: " + ee);
//    }
//  } else {
//    Components.utils.reportError("_card2token: cardXml is undefined" + cardXml);
//  }
//  
//  domain = cardXml.@domain;
//  if (domain) {
//    domain = domain.toString();
//    claimValueList += "<ic:ClaimValue Uri=\"http://schemas.t-labs.de/ws/2010/10/identity/claims/carddomain\">" +
//    "<ic:Value>" + domain + "</ic:Value>" + 
//    "</ic:ClaimValue>";
//  }
//  
//  icon = cardXml.@icon;
//  if (icon) {
//    icon = icon.toString();
//    claimValueList += "<ic:ClaimValue Uri=\"http://schemas.t-labs.de/ws/2010/10/identity/claims/imageref\">" +
//    "<ic:Value>" + icon + "</ic:Value>" + 
//    "</ic:ClaimValue>";
//  }
//  
//  cardname = cardXml.@name;
//  if (cardname) {
//    cardname = cardname.toString();
//  }
//  
//  if (domain === "Identity") {
//  } else if (domain === "Payment") {
//    claimValueList += "<ic:ClaimValue Uri=\"http://schemas.t-labs.de/ws/2010/10/identity/claims/cardname\">" +
//    "<ic:Value>" + cardname + "</ic:Value>" + 
//    "</ic:ClaimValue>";
//  } else {
//    mwDebug("_card2token: unsupported domain=" + domain);
//  }
//  
////  infocard: choosenCard=<infocard>
////  <name>sechs</name>
////  <type>selfAsserted</type>
////  <version>1</version>
////  <id>19064</id>
////  <privatepersonalidentifier>446e7f7a527d75e24329ecd2982769311c36dff8</privatepersonalidentifier>
////  <ic:InformationCardPrivateData xmlns:ic="http://schemas.xmlsoap.org/ws/2005/05/identity">
////    <ic:MasterKey>446e7f7a527d75e24329ecd2982769311c36dff8</ic:MasterKey>
////    <ic:ClaimValueList>
////      <ic:ClaimValue Uri="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname">
////        <ic:Value>Axel</ic:Value>
////      </ic:ClaimValue>
////      <ic:ClaimValue Uri="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname">
////        <ic:Value>Nennker</ic:Value>
////      </ic:ClaimValue>
////      <ic:ClaimValue Uri="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress">
////        <ic:Value>axel@nennker.de</ic:Value>
////      </ic:ClaimValue>
////    </ic:ClaimValueList>
////  </ic:InformationCardPrivateData>
////  <ic:SupportedClaimTypeList xmlns:ic="http://schemas.xmlsoap.org/ws/2005/05/identity">
////    <ic:SupportedClaimType Uri="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname">
////      <ic:DisplayTag>Vorname:</ic:DisplayTag>
////    </ic:SupportedClaimType>
////    <ic:SupportedClaimType Uri="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname">
////      <ic:DisplayTag>Nachname:</ic:DisplayTag>
////    </ic:SupportedClaimType>
////    <ic:SupportedClaimType Uri="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress">
////      <ic:DisplayTag>Email:</ic:DisplayTag>
////    </ic:SupportedClaimType>
////    <ic:SupportedClaimType Uri="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier"/>
////  </ic:SupportedClaimTypeList>
////  <rpIds>ef328e98c0fd49dab2629400914924156616f2e2</rpIds>
////</infocard>
//
//  var masterKey;
//  if (modulus) {
//    masterKey = modulus;
//  } else {
//    masterKey = "446e7f7a527d75e24329ecd2982769311c36dff8"; // FIXME
//  }
//
//  var infocard = "<infocard>" +
//  "<id>" + cardXml.@name.toString() + "</id>" +
//  "<privatepersonalidentifier>" + cardXml.@name.toString() + "</privatepersonalidentifier>" +
//  "<ic:InformationCardPrivateData xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\">" + 
//    "<ic:MasterKey>" + masterKey + "</ic:MasterKey>" +
//    "<ic:ClaimValueList>" + claimValueList + "</ic:ClaimValueList>" +
//  "</ic:InformationCardPrivateData>" + 
//  "</infocard>";
//  mwDebug("_card2token: infocard=" + infocard);
//  
//  try {
//    policy.type = "selfAsserted";
//    policy.card = infocard;
//    
//    var serializedPolicy = JSON.stringify(policy);
//    var sp = TokenIssuer.getToken(serializedPolicy);
//    var newPolicy = JSON.parse(sp);
//    if (newPolicy.hasOwnProperty("tokenToReturn")) {
//      var tokenToReturn = newPolicy("tokenToReturn"));
//      return tokenToReturn;
//    }
//    mwDebug("_card2token: no tokenToReturn" + sp);
//    return null;
//  } catch (getTokenException) {
//    mwDebug("_card2token: getTokenException=" + getTokenException);
//    return null;
//  }
}

function pollForSelectedCardCallback(policy){
  try {
    mwDebug("pollForSelectedCardCallback: ");
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
  } catch (eee) {
    Components.utils.reportError("mWallet.pollForSelectedCardCallback: exception=" + e);
  }
}

function mWalletLoad(policyParam){
  try {
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
  } catch (e) {
    mwDebug("mWalletLoad exception: " + e );
  }
}

