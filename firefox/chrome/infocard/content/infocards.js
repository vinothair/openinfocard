/*
 * Copyright (c) 2006, Chuck Mortimore - xmldap.org
 * All rights reserved.
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

var selectedCard;
var selectorDebugging = true;
var tokenIssuerInitialized = false;

function xmlreplace(text) {
 var str;
 if (typeof(text) == 'string') {
  str = text;
 } else {
  str = "" + text + "";
 }
 var result = str.replace(/&/g, "&amp;");
 result = result.replace(/</g, "&lt;");
 result = result.replace(/>/g, "&gt;");
 result = result.replace(/\?/g, "%3F");
 return(result);
}

// update the list of RPs to where a card has been sent
// this function is called from "ok" -> selectedCard is set
function updateRPs() {
	var policy = getPolicy();
   	if (policy != null) {
	    if (policy.hasOwnProperty("cert")) {
			var relyingPartyCertB64 = policy["cert"];
		    var rpIdentifier = computeRpIdentifier(relyingPartyCertB64);
		    var count = 0;
		    for each (rpId in selectedCard.rpIds) {
		     count++;
	icDebug("updateRPs:" + selectedCard.name + " rpId:" + rpId + " rpIdentifier:" + rpIdentifier);
		     if (rpId == rpIdentifier) {
		      // this RP is already in list of RPs
		      return;
		     }
		    }
		    selectedCard.rpIds[count] = rpIdentifier;
		    updateCard(selectedCard); // save to disk
	    }
	} // else nothing
}

function computeClientPseudonymPre20080829(policy){
	var url = policy["url"]; // RP url
	return hex_sha1(url + selectedCard.id);
}

function getRandomBytes(howMany) {
	  var i;
	  var bytes = new Array();
	  for (i=0; i<howMany; i++)
	    bytes[i] = Math.round(Math.random()*255);
	  return bytes;
}

//return binary value of hashsalt
function getHashSalt(card) {
	var hashSalt;
	if (card.hashsalt != undefined) {
		hashSalt = window.atob(card.hashsalt);
	} else {
		hashSalt = "" + getRandomBytes(256);
		card.hashsalt = window.btoa(hashsalt);
		icDebug("card.hashsalt: " + card.hashsalt);
		storeCard(card);
		hashSalt = window.atob(card.hashsalt);
	}
	return hashSalt;
}

//return binary value of masterkey
function getMasterKey(card) {
	var masterkey;
	if (card.masterkey != undefined) {
		masterkey = window.atob(card.masterkey);
	} else {
		masterkey = "" + getRandomBytes(256);
		card.masterkey = window.btoa(masterkey);
		icDebug("card.masterkey: " + card.masterkey);
		storeCard(card);
		masterkey = window.atob(card.masterkey);
	}
	return masterkey;
}

function certFromB64(certB64) {
	
}

function getCaCertForServerCert(cert)
{
   var i=1;
   var nextCertInChain;
   nextCertInChain = cert;
   var lastSubjectName="";
   while(true)
   {
     if(nextCertInChain == null)
     {
        return null;
     }
     if((nextCertInChain.type == nsIX509Cert.CA_CERT) ||
                                 (nextCertInChain.subjectName = lastSubjectName))
     {
        break;
     }

     lastSubjectName = nextCertInChain.subjectName;
     nextCertInChain = nextCertInChain.issuer;
   }

   return nextCertInChain;
}

function computeRpPpidSeed(policy) {
	var relyingPartyCertB64 = null;
	if (policy.hasOwnProperty("cert")) {
		relyingPartyCertB64 = policy["cert"];
	}
    if (relyingPartyCertB64 != null) {
		var cert = certFromB64(relyingPartyCertB64);
    	if (isEV(relyingPartyCertB64)) {
    	} else {
    	}
    } else {
    }
}

// returns the base64 encoded value of ClientPseudonym
function computeClientPseudonymPost20080829(policy){
	var url = policy["url"]; // RP url
	var relyingPartyCertB64 = null;
	if (policy.hasOwnProperty("cert")) {
		relyingPartyCertB64 = policy["cert"];
	}

	var rpPpidSeed = computeRpPpidSeed(policy);
	var hashSalt = getHashSalt(selectedCard);
	var masterkey = getMasterKey(selectedCard);
	var clientPseudonymPpidBytes = sha256(masterkey + rpPpidSeed + hashsalt);
	return window.btoa(clientPseudonymPpidBytes);
}

function ok(){

    var tokenToReturn;
    var policy = getPolicy();
    if (policy == null) {
    	icDebug("policy == null");
    	return;
    }

    if (selectedCard.type == "selfAsserted") {
        policy["type"] = "selfAsserted";
        policy["card"] = selectedCard.toString();
        //TRUE or FALSE on the second param enabled debug
        setOptionalClaimsSelf(policy);
        tokenToReturn = processCard(policy,selectorDebugging);
        finish(tokenToReturn);
		updateRPs();
    } else if (selectedCard.type == "managedCard"){
		var requiredClaims = policy["requiredClaims"];
		var optionalClaims = policy["optionalClaims"];
		var tokenType = null;
		if (policy.hasOwnProperty("tokenType")) {
			tokenType = policy["tokenType"];
		}
		var url = policy["url"]; // RP url
		var relyingPartyCertB64 = null;
		if (policy.hasOwnProperty("cert")) {
			relyingPartyCertB64 = policy["cert"];
		}
		var issuerPolicy = null;
		if (policy.hasOwnProperty("issuerPolicy")) {
			issuerPolicy = policy["issuerPolicy"];
		}
		var clientPseudonym = hex_sha1(url + selectedCard.id);
        var assertion = processManagedCard(
        	selectedCard, requiredClaims, optionalClaims, tokenType, 
        	clientPseudonym, url, relyingPartyCertB64, issuerPolicy);
        icDebug("assertion:" + assertion);
        if (assertion == null) {
         return;
        }
		updateRPs();
        

        if (!(selectedCard.carddata.managed.requireAppliesTo == undefined)) {
        	// STS is in auditing mode -> just return the unencrypted assertion
        	tokenToReturn = assertion;
	    } else {
	        policy["type"] = "managedCard";
    	    policy["assertion"] = assertion;
	    	// STS is NOT in auditing mode -> encrypt the assertion
	        //TRUE or FALSE on the second param enabled debug
	        tokenToReturn = processCard(policy,selectorDebugging);
	    }
	    
        finish(tokenToReturn);


    } else if (selectedCard.type == "openid"){
        openid(selectedCard.id);
    }

}


function finalizeOpenId() {


    icDebug('1');


    var tokenToReturn;
    var policy = getPolicy();


    policy["type"] = "selfAsserted";
    selectedCard.privatepersonalidentifier = hex_sha1(selectedCard.cardName + selectedCard.version + selectedCard.id);

    var count = 0;
    var data = new XML("<selfasserted/>");

    selectedCard.supportedclaim[count] = "givenname";
    data.givenname = openid_nickname;
    count++;

    selectedCard.supportedclaim[count] = "surname";
    data.surname = openid_fullname;
    count++;

    selectedCard.supportedclaim[count] = "emailaddress";
    data.emailaddress = openid_email;
    count++;

    selectedCard.carddata.data = data;
    policy["card"] = selectedCard.toString();


    tokenToReturn = processCard(policy,false);

    icDebug('2');


    finish(tokenToReturn);

}


function finish(tokenToReturn) {

    stopServer();

    if (tokenToReturn != null) {

        icDebug("Token: " + tokenToReturn);
        window.arguments[1](tokenToReturn);
        window.close();

    }

}


function isClaimChecked(elementId, uri) {
	 var checkbox = document.getElementById(elementId);
	 if (!(checkbox == undefined)) {
icDebug("isClaimChecked: found " + elementId);
		 if (!(checkbox.checked == undefined)) {
icDebug("isClaimChecked: is a checkbox ");
		  if (checkbox.checked) {
icDebug("isClaimChecked: is checked ");
		   if ( uri === undefined ) {
		     return "";
		   } else {
			 return uri;
		   }
		  } else {
icDebug("isClaimChecked: is not checked ");
		  }
		 } else {
		  icDebug( "expected type checkbox, but found: " + typeof(checkbox));
		 } 
	 } else {
	  icDebug("checkbox not defined for uri: " + uri );
	 }
	 return null;
}

function showPrivacyStatement() {
	var policy = getPolicy();
	if (policy != null) {
	    var privacyUrl = policy["privacyUrl"];
	    if (privacyUrl == null) {
	     alert("relying party did not specify a privacy statement URL");
	     return;
	    }
	    // prevent file:// and chrome:// etc
	    if (privacyUrl.indexOf("http") != 0) {
	     alert("The relying party's privacy statement URL does not start with 'http'\n" + privacyUrl);
	     return;
	    }
	    
	    window.open(privacyUrl, "privacyStatement");
	}
}

function disable(){
	var prefService = Components.classes[ 
		"@mozilla.org/preferences-service;1"].
			getService( Components.interfaces.nsIPrefBranch);
	var disabled = prefService.getBoolPref( "identityselector.disabled");
	icDebug("disabled=" + disabled);
	prefService.setBoolPref( "identityselector.disabled", true);
	disabled = prefService.getBoolPref( "identityselector.disabled");
	icDebug("disabled=" + disabled);

    var doc = window.document;
    var event = doc.createEvent("Events");
    event.initEvent("DisableIdentitySelector", true, true);
    window.dispatchEvent(event);

	cancel();
}

function cancel(){
	icDebug("cancel");
    var doc = window.document;
    var event = doc.createEvent("Events");
    event.initEvent("CancelIdentitySelector", true, true);
    window.dispatchEvent(event);

    stopServer();
   	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
	    window.arguments[1](null);
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

function getCardId(extraParams){
	try {
		for (var i=0; i<extraParams.length; i++) {
			var val = JSON.parse(extraParams[i]);
			if ((val !== null) && val.hasOwnProperty("cardid")) {
				var cardId = val.cardid;
				icDebug("getCardId: cardId = " + cardId);
				return cardId;
			} else {
				icDebug("getCardId: extraParams[" + i + "] = " + extraParams[i]);
			}
		}
	} catch (e) {
		icDebug("getCardId threw exception: " + e);
	}
//	return undefined;
}

function cardManagerUnload(){
	icDebug("cardManagerUnload start. href=" + window.document.location.href );
	TokenIssuer.finalize();
}

function cardManagerLoad(policyParam){
	icDebug("cardManagerLoad start. href=" + window.document.location.href );
	
	var controlarea = document.getElementById('selectcontrol');
	if (controlarea) {
	    var select = document.getElementById('selectcontrol');
	    select.addEventListener("click", ok, false);
	
	
	    var disableElm = document.getElementById('disableSelector');
	    disableElm.addEventListener("click", disable, false);
	
	    var newCardElm = document.getElementById('newCard');
	    newCardElm.addEventListener("click", newCard, false);
	
	    var deleteCardElm = document.getElementById('deleteCard');
	    deleteCardElm.addEventListener("click", deleteCard, false);
	
	    var cancelselector = document.getElementById('cancelselector');
	    cancelselector.addEventListener("click", cancel, false);
	}
	
    var stringsBundle = document.getElementById("string-bundle");

    var policy;
    if (policyParam == undefined) {
    	policy = getPolicy();
    } else {
    	policy = policyParam;
    }
    	
    var rpIdentifier = null;

    if (policy != null && policy.hasOwnProperty("cert")) {
		var relyingPartyCertB64 = policy["cert"];
	    rpIdentifier = computeRpIdentifier(relyingPartyCertB64);
    }

    var extraParams = null;
    var extraParamsCardId;
    if ((policy !== null) && policy.hasOwnProperty("extraParams")) {
    	extraParams = policy.extraParams;
    	extraParamsCardId = getCardId(extraParams);
    	icDebug("extraParams length = " + extraParams.length);
    	icDebug("extraParams cardId = " + extraParamsCardId);
    }

    var cardFile = readCardStore();
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
	
	if (extraParamsCardId !== undefined) {
		var choosenCard = getCard(extraParamsCardId);
		setCard(choosenCard);
	} else {
		 icDebug("extraParamsCardId === undefined");
	}

}

function indicateRequiredClaim(requiredClaims, optionalClaims, claim){
 var name = "_" + claim;
 var element = document.getElementById(name);
 if (element == undefined) {
  icDebug( "Element " + name + " not found" );
  return;
 }
 if (requiredClaims.indexOf(claim.toLowerCase()) != -1) {
    //debug("required claim " + claim + " found in " + requiredClaims);
    element.checked = true;
    element.disabled = true;
    return;
 } 

 if (optionalClaims != null) {
  if (optionalClaims.indexOf(claim.toLowerCase()) != -1) {
    //icDebug("optional claim " + claim + " found in " + optionalClaims);
    element.checked = false;
    element.disabled = false;
    return;
  }
 } 
  
 //icDebug("claim " + claim + " not found");
 element.checked = false;
 element.disabled = true;
}

function indicateRequiredClaims(policy){
	 var requiredClaims = policy["requiredClaims"];
	 if (requiredClaims == undefined) return;
	 if (requiredClaims == null) return;
	
	 var optionalClaims = policy["optionalClaims"];
	 
	 //requiredClaims = requiredClaims.toLowerCase();
	 //if (optionalClaims != null) {
	 	 //optionalClaims = optionalClaims.toLowerCase();
	 //}
	
	icDebug("requiredClaims: " + requiredClaims);
	 indicateRequiredClaim(requiredClaims, optionalClaims, "givenname");
	 indicateRequiredClaim(requiredClaims, optionalClaims, "surname");
	 indicateRequiredClaim(requiredClaims, optionalClaims, "email");
	 indicateRequiredClaim(requiredClaims, optionalClaims, "streetAddress");
	 indicateRequiredClaim(requiredClaims, optionalClaims, "locality");
	 indicateRequiredClaim(requiredClaims, optionalClaims, "stateOrProvince");
	 indicateRequiredClaim(requiredClaims, optionalClaims, "postalCode");
	 indicateRequiredClaim(requiredClaims, optionalClaims, "country");
	 indicateRequiredClaim(requiredClaims, optionalClaims, "primaryPhone");
	 indicateRequiredClaim(requiredClaims, optionalClaims, "otherPhone");
	 indicateRequiredClaim(requiredClaims, optionalClaims, "mobilePhone");
	 indicateRequiredClaim(requiredClaims, optionalClaims, "dateOfBirth");
	 indicateRequiredClaim(requiredClaims, optionalClaims, "gender");
}

function setCardSelf() {
	//icDebug("setCardSelf card= " + selectedCard);
	icDebug("setCardSelf givenname: " + selectedCard.carddata.selfasserted.givenname);
        document.getElementById("givenname").value = selectedCard.carddata.selfasserted.givenname;
	icDebug("setCardSelf: " + document.getElementById("givenname").value);
        document.getElementById("surname").value = selectedCard.carddata.selfasserted.surname;
        document.getElementById("email").value = selectedCard.carddata.selfasserted.emailaddress;
        document.getElementById("streetAddress").value = selectedCard.carddata.selfasserted.streetaddress;
        document.getElementById("locality").value = selectedCard.carddata.selfasserted.locality;
        document.getElementById("stateOrProvince").value = selectedCard.carddata.selfasserted.stateorprovince;
        document.getElementById("postalCode").value = selectedCard.carddata.selfasserted.postalcode;
        document.getElementById("country").value = selectedCard.carddata.selfasserted.country;
        document.getElementById("primaryPhone").value = selectedCard.carddata.selfasserted.primaryphone;
        document.getElementById("otherPhone").value = selectedCard.carddata.selfasserted.otherphone;
        document.getElementById("mobilePhone").value = selectedCard.carddata.selfasserted.mobilephone;
        document.getElementById("dateOfBirth").value = selectedCard.carddata.selfasserted.dateofbirth;
        document.getElementById("gender").value = selectedCard.carddata.selfasserted.gender;
        document.getElementById("imgurl").value = selectedCard.carddata.selfasserted.imgurl;



        document.getElementById("cardname").visibility = 'visible';
        document.getElementById("givenname").visibility = 'visible';
        document.getElementById("surname").visibility = 'visible';
        document.getElementById("email").visibility = 'visible';
        document.getElementById("streetAddress").visibility = 'visible';
        document.getElementById("locality").visibility = 'visible';
        document.getElementById("stateOrProvince").visibility = 'visible';
        document.getElementById("postalCode").visibility = 'visible';
        document.getElementById("country").visibility = 'visible';
        document.getElementById("primaryPhone").visibility = 'visible';
        document.getElementById("otherPhone").visibility = 'visible';
        document.getElementById("mobilePhone").visibility = 'visible';
        document.getElementById("dateOfBirth").visibility = 'visible';
        document.getElementById("gender").visibility = 'visible';
        document.getElementById("imgurl").visibility = 'visible';

   	 	var policy = getPolicy();
   	 	if (policy != null) {
   	 		indicateRequiredClaims(policy);
   	 	}
   	 	
        var grid = document.getElementById("editgrid");
        grid.setAttribute("hidden", "false");


        var grid1 = document.getElementById("editgrid1");
        grid1.setAttribute("hidden", "false");

		var stringsBundle = document.getElementById("string-bundle");
		var selfassertedcard = stringsBundle.getString('selfassertedcard');
        var label = document.getElementById("notify");
        if (label != null) {
        	label.setAttribute("value", selfassertedcard);
        }
}

function setCard(card){

	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
	    var select = document.getElementById('selectcontrol');
    	select.setAttribute('hidden', 'false');
	}
	
    var showPrivacyStatementElm = document.getElementById('privacy_label');
    if (showPrivacyStatementElm != null) {
    	showPrivacyStatementElm.hidden = true;
    }
    
	var issuerlogo = document.getElementById("issuerlogo");
	if (issuerlogo != null) {
		issuerlogo.src = "";
		issuerlogo.hidden = true;
		var issuerlogo_label = document.getElementById("issuerlogo_label");
	    issuerlogo_label.hidden = true;
		var issuer_hbox = document.getElementById("issuer_hbox");
	    issuer_hbox.hidden = true;
	}
	
    selectedCard = card;

    icDebug("TYPE: " + selectedCard.type);
//    icDebug(selectedCard);

    var selfassertedClaims = document.getElementById('selfassertedClaims');
    var managedClaims = document.getElementById('managedClaims');

    if (selectedCard.type == "selfAsserted" )  {
    	if (selfassertedClaims != null) {
    		selfassertedClaims.setAttribute("hidden", "false");
    	}
    	if (managedClaims != null) {
    		managedClaims.setAttribute("hidden", "true");
    	}
		var cardname = document.getElementById("cardname");
		if (cardname != null) {
			cardname.value = selectedCard.name;
			cardname.hidden = false;
		}
		setCardSelf();
    }  else if (selectedCard.type == "managedCard" )   {
	    var requiredClaims = null;
	    var optionalClaims = null;
	    var policy = getPolicy();
 	    if (policy != null) {
		  requiredClaims = policy["requiredClaims"];
   	      optionalClaims = policy["optionalClaims"];
	    }	
    	if (selfassertedClaims != null) {
    		selfassertedClaims.setAttribute("hidden", "true");
    	}
    	if (managedClaims != null) {
    		managedClaims.setAttribute("hidden", "false");
    	}

		var cardname = document.getElementById("cardname");
		if (cardname != null) {
	        cardname.value = selectedCard.name;
	        cardname.hidden = false;
		}
		setCardManaged(requiredClaims, optionalClaims);
    } else if (selectedCard.type == "openid" )  {


        var label = document.getElementById("notify");
        if (label != null) {
        	label.setAttribute("value", "Use OpenID with Identity URL: " + selectedCard.id);
        }
        
		var cardname = document.getElementById("cardname");
		if (cardname != null) {
	        cardname.value = "";
	        cardname.hidden = true;
		}
		
    	if (selfassertedClaims != null) {
    		selfassertedClaims.setAttribute("hidden", "true");
    	}
    	if (managedClaims != null) {
    		managedClaims.setAttribute("hidden", "true");
    	}

    } else  {
    	alert("unsupported card type\n" + selectedCard.type);
    	return;
    }
}

function dblclick(event) {
	handleCardChoice(event);
	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
		icDebug("dblclick: ok");
		ok();
	} else {
		// else cardManager.xul called from preferences
		icDebug("dblclick: not calling ok");
	}
}

function handleCardChoice(event){

    var choice = event.originalTarget;
    var selectedCardId = choice.getAttribute("cardid");
    var choosenCard = getCard(selectedCardId);
    setCard(choosenCard);

}



function createItem(c, classStr){


    var hbox = document.createElement("hbox");
    hbox.setAttribute("class",classStr);
    hbox.setAttribute("cardid",c.id);
    hbox.setAttribute("id",c.id);
    hbox.setAttribute("draggable","true");
    hbox.setAttribute("ondraggesture", 
    	"nsDragAndDrop.startDrag(event, listObserver);");
    hbox.setAttribute("ondragexit", 
    	"nsDragAndDrop.dragExit(event, listObserver);");
    hbox.setAttribute("ondragdrop", 
		"nsDragAndDrop.drop(event, listObserver);");
    var vbox = document.createElement("vbox");
    vbox.setAttribute("class","databox");
    vbox.setAttribute("flex","1");
    var labelName = document.createElement("label");
    labelName.setAttribute("class","lblname");
    labelName.setAttribute("value",c.name);
    labelName.setAttribute("cardid",c.id);
    var labelVersion = document.createElement("label");
    labelVersion.setAttribute("class","lblmail");
    labelVersion.setAttribute("value", "Version " + c.version);
    labelVersion.setAttribute("cardid",c.id);

    var imgurl = "";
    if ( c.type == "selfAsserted") {
        imgurl = c.carddata.selfasserted.imgurl;
    } else if ( c.type == "managedCard") {
        imgurl = c.carddata.managed.image;
    }
     //var picture = document.createElement("html:img");
    var picturebox = document.createElement("hbox");
    picturebox.setAttribute("flex", "0");
    picturebox.setAttribute("align", "center");
    var picture = document.createElement("image");
    if ( (imgurl == "") || (imgurl == undefined)) {

        if (c.type == "selfAsserted") {
            picture.setAttribute("src", "chrome://infocard/content/img/card.png");
        } else if (c.type == "openid") {
            picture.setAttribute("src", "chrome://infocard/content/img/openid.png");
        }
    } else {
        picture.setAttribute("src", imgurl);
    }

    picture.setAttribute("cardid", c.id);
    picture.setAttribute("class", "cardClass");
    picturebox.appendChild(picture);
    vbox.appendChild(picturebox);
    vbox.appendChild(labelName);
    vbox.appendChild(labelVersion);
    hbox.appendChild(vbox);
    hbox.addEventListener("click", handleCardChoice, false);
    hbox.addEventListener("dblclick", dblclick, false);
    icDebug ("Setting cardid " + hbox.getAttribute("cardid"));
    return hbox;

}

var listObserver = {
		  onDragStart: function (event, transferData, action) {
			if (event.target.nodeName == 'image') {
				var data = event.target.getAttribute("cardid");
			  	icDebug("onDragStart: DATA=" + data);
			} else {
				icDebug("onDragStart: target.nodeName=" + event.target.nodeName);
			    var data = event.target.getAttribute("id");
			    if ("" + data != "") {
				  	icDebug("onDragStart: data=" + data);
			    } else {
			      	icDebug("onDragStart: Data" + "boink");
			    }
			}
		    transferData.data = new TransferData();
//		    transferData.data.addDataForFlavour("text/unicode", "" + data);
		    transferData.data.addDataForFlavour("application/x-informationcard+id", "" + data);
		  },
		  
		  onDragExit : function (event, session) {
		  	icDebug("onDragExit: event.target.nodeName=" + event.target.nodeName);
//		    var targetId = event.target.getAttribute("id");
//		  	icDebug("onDragExit: targetId=" + targetId);
//		  	var doc = event.target.ownerDocument;
//		  	if (doc.__identityselector__ == undefined) {
//		  		icDebug("onDragExit: doc.__identityselector__ == undefined");
//		  	} else {
//		  		icDebug("onDragExit: doc.__identityselector__ != undefined");
//		  	}
//		  	for (var i in session) {
//		  		
//		  		icDebug("onDragExit: session." + i + "=" + eval("session." + i));
//		  	}
//		  	icDebug("onDragExit: session.sourceNode=" + session.sourceNode.nodeName);
//		  	if (session.sourceNode.nodeName == 'image') {
//				var data = event.target.getAttribute("cardid");
//			  	icDebug("onDragExit: DATA=" + data);
//		  	} else {
//			    var data = event.target.getAttribute("id");
//			    if ("" + data != "") {
//				  	icDebug("onDragExit: data=" + data);
//			    } else {
//			      	icDebug("onDragExit: data" + "boink");
//			    }
//		  	}
//		    transferData.data = new TransferData();
//		    transferData.data.addDataForFlavour("text/unicode", data);
  		  },
  		  
  		onDrop : function (evt, transferData, session) {
  			  icDebug("onDrop: " + transferData.data);
//  			event.target.setAttribute("value",transferData.data); 
  		}
}

// returns true of card tokentype and RP's policy tokentype match
// if something unexcpected happens then return false
function computeMatching(card, policy) {
 var matchingTokenType = false;
 var tokenType = null;
 tokenType = null;
 if (policy.hasOwnProperty("tokenType")) {
  tokenType = policy["tokenType"];
 }

 if (tokenType != null) {
  icDebug("tokenType: " + tokenType + " card:" + card.name);
  if (card.type == "managedCard") {
	  var ic = new Namespace("ic", "http://schemas.xmlsoap.org/ws/2005/05/identity");
	  var trust = new Namespace("trust", "http://schemas.xmlsoap.org/ws/2005/02/trust");
	  var list = card.carddata.managed.ic::SupportedTokenTypeList.trust::TokenType;
	  
	  for (var index = 0; index<list.length(); index++) {
	   var cardTokenType = list[index];
	   if (tokenType == cardTokenType) {
	    matchingTokenType = true;
	    icDebug("matchingTokenType:" + tokenType);
	    var relyingPartyUrl = policy["url"];
	    if (card.carddata.managed.requireStrongRecipientIdentity != undefined) {
	    	if (card.carddata.managed.requireStrongRecipientIdentity == true) {
	    		if (relyingPartyUrl.indexOf("https") == 0) {
	    		    icDebug("matchingRequireStrongRecipientIdentity");
	    			return true;
	    		} else {
	    			return false;
	    		}
	    	}
	    } // else do not care about requireStrongRecipientIdentity
	    return matchingTokenType;
	   } else {
	    icDebug("notMatchingTokenType:" + cardTokenType);
	   }
	  }
  } else if (card.type == "selfAsserted") {
   if (tokenType == "urn:oasis:names:tc:SAML:1.0:assertion") {
    matchingTokenType = true;
   } else if (tokenType ==  "http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV1.1") {
    matchingTokenType = true;
   } else {
    icDebug("tokenType does not match tokenTypes for self-issued cards:" + tokenType);
   }
  } else {
   icDebug("unsupported card type");
   matchingTokenType = false;
  }
 } else { // RP does not require a special tokentype
  matchingTokenType = true;
 }
 return matchingTokenType;
}

function computeRpIdentifier(cert) {
	return hex_sha1(cert);
}

function computeHasBeenSend(card, policy) {
	if (!policy.hasOwnProperty("cert")) {
		return false;
	}
	var relyingPartyCertB64 = policy["cert"];
    var rpIdentifier = computeRpIdentifier(relyingPartyCertB64);
    var beenThere = false;
    for each (rpId in card.rpIds) {
     icDebug("computeHasBeenSend: " + card.name + " RpId:" + rpId + " RpIdentifier:" + rpIdentifier);
     if (rpId == rpIdentifier) {
      icDebug(card.name + " has been sent to: " + policy["cn"]);
      beenThere = true;
      break;
     }
    }
    return beenThere;
}

// this function is intended to compute the visualization class
// for this card. It returns one of contactGreen, contactYellow 
// or contactRed
// or contact of we are managing cards that is: there is no RP
// green - 	card is matching the requirements of the RP
// yellow - card is matching the requirements of the RP but was never sent to RP
// red -	card is not matching
// 
function computeCardClass(card, policy) {
 var cardClass;
 if ((window.java == undefined) && (card.type == "selfAsserted")) {
	 // if there is a java problem then self-issued cards do not work
	 cardClass = "contactRed";
 } else {
	 if (policy != null) {
	  var matching = computeMatching(card, policy);
	  if (matching == true) {
	   var hasBeenSent = computeHasBeenSend(card, policy);
	   if (hasBeenSent == true) {
	    cardClass = "contactGreen";
	   } else {
	    cardClass = "contactYellow";
	   }
	  } else {
	   cardClass = "contactRed";
	  }
	 } else {
		 cardClass = "contact";
	 }
 }
 icDebug("cardClass " + cardClass + " " + card.name);
 return cardClass;
}

function saveCard(card){
    storeCard(card);
    var cardArea = document.getElementById("cardselection");
    if (cardArea) {
    	var policy = getPolicy();
	    var cardClass = computeCardClass(card, policy);
	    cardArea.appendChild(createItem(card, cardClass));
	    setCard(card);
    } // else called from cardWizard and not from cardManager!
    return true;

}


function newCard(){

    var callback;
    var cardWiz = window.openDialog("chrome://infocard/content/cardWizard.xul","Card Wizard", "modal,chrome,resizable=yes,width=640,height=480",
                                    null, function (callbackData) { callback = callbackData;});
	digestNewCard(callback);
}

function validateSignature(callback) {
	var cardFile = readCardStore();
	
	var importedCardJSONStr = null;
	try {
		importedCardJSONStr = JSON.stringify(callback);
	} catch (e) {
		icDebug("error JSON.stringifying(callback) " + e);
	}
	icDebug2("importedCardJSONStr="+importedCardJSONStr, 120);
	var cardFileStr = null;
	try {
		cardFileStr = "" + cardFile + "";
	} catch (e) {
		icDebug("error JSON.stringifying(cardFile) " + e);
	}
	if (tokenIssuerInitialized = TokenIssuer.initialize() == false) {
		icDebug("digestNewCard: could not initialize TokenIssuer. Signature will not be validated");
		icDebug("processCard: window.java = " + window.java + "window.document.location.href=" + window.document.location.href);
//		alert("Could not initialize java. The card's signature will not be validated!");
	} else {
		var currentRoamingStore = readRoamingStore();
		icDebug2("currentRoamingStore = " + currentRoamingStore, 120);
		var importedCardStr = null;
		try {
			var clasz = Components.classes["@mozilla.org/oji/jvm-mgr;1"];
			if ((clasz !== null) && (Components.interfaces.nsIJVMManager !== null)) {
				var jvm = clasz.getService(Components.interfaces.nsIJVMManager);
				jvm.showJavaConsole();
			}
		} catch (e) {
			icDebug("Components.classes['@mozilla.org/oji/jvm-mgr;1'].getService(Components.interfaces.nsIJVMManager); threw exception " + e);
		}
		try {
			importedCardStr = TokenIssuer.importManagedCard(importedCardJSONStr, currentRoamingStore);
		} catch (e1) {
			icDebug("TokenIssuer.importManagedCard threw exception " + e1);
		}
    	if (importedCardStr != null) {
	    	icDebug2("importedCardStr = " + importedCardStr, 120);
	    	icDebug("importedCardStr type=" + typeof(importedCardStr));
	    	var importedCard = JSON.parse("" + importedCardStr + ""); // convert java.lang.String to javascript string
	    	if (importedCard == false) {
	    		// oops. Could not parse json
	    		icDebug2("Internal error: could not parse json=" + importedCardStr, 120);
	    		alert("Internal error: could not parse json=" + importedCardStr);
	    		return;
	    	}
	    	if (importedCard == null) {
	    		alert("The managed card is NOT imported");
	    		return;
	    	}
	    	
	    	if (importedCard.error != null) {
	       		alert("The card is NOT imported\n" + importedCard.error);
	    		return;
	    	} 
	    	var result = importedCard.result;
	    	icDebug2("RoamingStore: " + result, 160);
	    	
	    	var roamingstore = new XML(result);
	    	//icDebug("RoamingStoreXML: " + roamingstore);
	    	saveRoamingStore(roamingstore);
    	} else {
    		alert("Could not verify SIGNATURE of card: " + callback.cardName + "\nContinuing anyways with import.");
    	}
	}
}
function digestNewCard(callback) {
	icDebug("digestNewCard");
	if (callback == undefined) {
	 alert("no new card was imported");
	 return;
	}
	if (callback == null) {
	 alert("No new card was imported");
	 return;
	}
	
    var cardName = callback.cardName;
    var type = "" + callback.type;
    var cardId = "" + callback.cardId;

    if (isCardInStore(cardId)) {
    	alert("This card is already in the card store. Please delete it first.");
		return;
    }
	
    if ( type === "selfAsserted") {
    	
        var card = newSelfIssuedCard(callback);
        saveCard(card);
        return;
    }

    if ( type === "managedCard") {
    	validateSignature(callback);
    	card = newManagedCard(callback);
        saveCard(card);
    }


    if ( type == "openid") {
    	card = newOpenIdCard(callback);
        saveCard(card);

    }


}

function reload(policyParam) {
    var cardArea = document.getElementById("cardselection");
    while (cardArea.hasChildNodes())
	{
	  cardArea.removeChild(cardArea.firstChild);
	}


    var grid = document.getElementById("editgrid");
    if (grid != null) {
    	grid.setAttribute("hidden", "true");
    }
    grid = document.getElementById("editgrid1");
    if (grid != null) {
    	grid.setAttribute("hidden", "true");
    }

    grid = document.getElementById("editgrid2");
    if (grid != null) {
    	grid.setAttribute("hidden", "true");
    }

    grid = document.getElementById("editgrid3");
    if (grid != null) {
    	grid.setAttribute("hidden", "true");
    }

    var label = document.getElementById("notify");
    if (label != null) {
    	label.setAttribute("value", "Please select another card");
    }
    
    var select = document.getElementById('selectcontrol');
    if (select != null) {
    	select.setAttribute('hidden', 'true');
    }
    
	var cardname = document.getElementById("cardname");
	if (cardname != null) {
        cardname.value = "";
        cardname.hidden = false;
	}
	
    selectedCard = null;

    cardManagerLoad(policyParam);
}

function deleteCard(){

	if (selectedCard == undefined) return;
	if (selectedCard == null) return;
	
    
    var selectedCardId = selectedCard.id;
    icDebug("Delete Card : " + selectedCardId);
    removeCard(selectedCardId);
	reload();
}

function setOptionalClaimsSelf(policy) {
	    var optionalClaims = "";
    if (!(policy["optionalClaims"] == undefined)) {
     optionalClaims = policy["optionalClaims"];
     if (optionalClaims != null) {
      icDebug("setOptionalClaimsSelf optionalClaims: " + optionalClaims);
      var checkedClaims = null;
      var claims = optionalClaims.split(/\s+/);
      icDebug("setOptionalClaimsSelf claims: " + claims);
      var i;
      for (i in claims) {
       var claim = claims[i];
       icDebug("setOptionalClaimsSelf claim: " + claim);
       if (claim.indexOf("givenname") != -1) {
        if (isClaimChecked("_givenname") != null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname";
         if (checkedClaims == null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("emailaddress") != -1) {
        if (isClaimChecked("_email") != null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress";
         if (checkedClaims == null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("surname") != -1) {
        if (isClaimChecked("_surname") != null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname";
         if (checkedClaims == null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("streetaddress") != -1) {
        if (isClaimChecked("_streetAddress") != null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress";
         if (checkedClaims == null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("stateorprovince") != -1) {
        if (isClaimChecked("_stateOrProvince") != null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/stateorprovince";
         if (checkedClaims == null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("postalcode") != -1) {
        if (isClaimChecked("_postalCode") != null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode";
         if (checkedClaims == null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("country") != -1) {
        if (isClaimChecked("_country") != null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country";
         if (checkedClaims == null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("homephone") != -1) {
        if (isClaimChecked("_primaryPhone") != null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone";
         if (checkedClaims == null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("otherphone") != -1) {
        if (isClaimChecked("_otherPhone") != null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/otherphone";
         if (checkedClaims == null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("mobilephone") != -1) {
        if (isClaimChecked("_mobilePhone") != null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/mobilephone";
         if (checkedClaims == null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("dateofbirth") != -1) {
        if (isClaimChecked("_dateOfBirth") != null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth";
         if (checkedClaims == null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("gender") != -1) {
        if (isClaimChecked("_gender") != null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender";
         if (checkedClaims == null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("locality") != -1) {
        if (isClaimChecked("_locality") != null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality";
         if (checkedClaims == null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("privatepersonalidentifier") != -1) {
        if (isClaimChecked("privatepersonalidentifier") != null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier";
         if (checkedClaims == null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else {
        icDebug("processCard: claim not in list:" + claim);
       }
      }
      icDebug("setOptionalClaimsSelf checkedClaims: " + checkedClaims);
	  policy["optionalClaims"] = checkedClaims;
     }
    }
}
function processCard(policy, enableDebug){
	icDebug("processCard: start");
    if (enableDebug) {
    	try {
    		var clasz = Components.classes["@mozilla.org/oji/jvm-mgr;1"];
    		if ((clasz !== null) && (Components.interfaces.nsIJVMManager !== undefined)) {
		        var jvm = clasz.getService(Components.interfaces.nsIJVMManager);
		        jvm.showJavaConsole();
    		}
    	} catch (ee) {
    		icDebug("processCard: Components.classes['@mozilla.org/oji/jvm-mgr;1'].getService(Components.interfaces.nsIJVMManager) threw exception:" + ee);
    	}
    }
    
	if (tokenIssuerInitialized = TokenIssuer.initialize() == false) {
		icDebug("processCard: could not initialize TokenIssuer. window.document.location.href=" + window.document.location.href);
		icDebug("processCard: window.java=" + window.java);
		//		alert("Could not initialize the TokenIssuer. This is probably an java issuer");
		return null;
	}
	
    var token;
    
    var serializedPolicy = JSON.stringify(policy);
    token = TokenIssuer.getToken(serializedPolicy);

    return token;

}
