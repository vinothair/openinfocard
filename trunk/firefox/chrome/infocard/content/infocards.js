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

var selectorDebugging = false;

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
   	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
	    var policy = window.arguments[0];
	    if (policy.hasOwnProperty("cert")) {
			var relyingPartyCertB64 = policy["cert"];
		    var rpIdentifier = hex_sha1(relyingPartyCertB64);
		    var count = 0;
		    for each (rpId in selectedCard.rpIds) {
		     count++;
	debug(selectedCard.name + " rpId:" + rpId + " rpIdentifier:" + rpIdentifier);
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

function ok(){

    var tokenToReturn;
    var policy = window.arguments[0];

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
		var clientPseudonym = hex_sha1(url + selectedCard.id);
		var relyingPartyCertB64 = null;
		if (policy.hasOwnProperty("cert")) {
			relyingPartyCertB64 = policy["cert"];
		}
		var issuerPolicy = null;
		if (policy.hasOwnProperty("issuerPolicy")) {
			issuerPolicy = policy["issuerPolicy"];
		}
        var assertion = processManagedCard(
        	selectedCard, requiredClaims, optionalClaims, tokenType, 
        	clientPseudonym, url, relyingPartyCertB64, issuerPolicy);
        debug("assertion:" + assertion);
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


    debug('1');


    var tokenToReturn;
    var policy = window.arguments[0];


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

    debug('2');


    finish(tokenToReturn);

}


function finish(tokenToReturn) {

    stopServer();

    if (tokenToReturn != null) {

        debug("Token: " + tokenToReturn);
        window.arguments[1](tokenToReturn);
        window.close();

    }

}

function getMexForCard(managedCard) {
	var to = xmlreplace(managedCard.carddata.managed.mex);
	var mexAddress = managedCard.carddata.managed.mex;
	var mexResponse = getMex1(to, mexAddress);
	return mexResponse;
}

function getMex1(to, mexAddress) {
    var messageIdInt = Math.floor(Math.random()*100000+1);
    var messageId = "urn:uuid:" + messageIdInt;
    var mex = "<s:Envelope " + 
    	"xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" " + 
    	"xmlns:a=\"http://www.w3.org/2005/08/addressing\">" + 
    	"<s:Header>" + 
    	 "<a:Action s:mustUnderstand=\"1\">http://schemas.xmlsoap.org/ws/2004/09/transfer/Get</a:Action>" + 
    	 "<a:MessageID>" +   messageId  +  "</a:MessageID>" + 
    	 "<a:ReplyTo>" + 
    	  "<a:Address>http://www.w3.org/2005/08/addressing/anonymous</a:Address>" + 
    	 "</a:ReplyTo>" + 
    	 "<a:To s:mustUnderstand=\"1\">" + to + "</a:To>" + 
    	"</s:Header><s:Body/></s:Envelope>";

debug("getMex: mex request: " + mex);
debug("managedCard.carddata.managed.mex: " + mexAddress);

    var req = new XMLHttpRequest();
    req.open('POST', mexAddress, false);
    debug('mex xmlhttprequest open');
    req.setRequestHeader("Content-type", "application/soap+xml; charset=utf-8");
    req.setRequestHeader("Cache-Control", "no-cache");
    req.setRequestHeader("accept-language", "en-us");
    req.setRequestHeader("User-Agent", "xmldap infocard stack");
    debug('mex xmlhttprequest send');
    try {
	    req.send(mex);
    }
    catch (e) {
    	debug(e);
    	alert("posting the MEX request failed." + e);
    	return null;
    }
debug("getMex: mex POST request status="+req.status);
    if(req.status == 200) {
debug("getMex: mex POST request status 200");

        mexResponse = req.responseText;
debug("getMex POST 200: " + mexResponse);
        return mexResponse;
    } else {
debug("getMex POST " + req.status + ": " + req.responseText);
	    req.open('GET', mexAddress, false);
	    debug('mex xmlhttprequest open');
	    req.setRequestHeader("Content-type", "application/soap+xml; charset=utf-8");
	    req.setRequestHeader("Cache-Control", "no-cache");
	    req.setRequestHeader("accept-language", "en-us");
	    req.setRequestHeader("User-Agent", "xmldap infocard stack");
	    debug('mex GET xmlhttprequest send');
	    try {
		    req.send(null);
	    }
	    catch (e) {
	    	debug(e);
    		alert("getting the MEX request failed." + e);
    		return null;
	    }
debug("getMex: mex GET request status="+req.status);
    if(req.status == 200) {
debug("getMex: mex GET request status 200");
        mexResponse = req.responseText;
debug("getMex: " + mexResponse);
        return mexResponse;
    } else {
    	debug("getMex GET " + req.status + ": " + req.responseText);
    }
	return null;
}
}

function isClaimChecked(elementId, uri) {
	 var checkbox = document.getElementById(elementId);
	 if (!(checkbox == undefined)) {
debug("isClaimChecked: found " + elementId);
		 if (!(checkbox.checked == undefined)) {
debug("isClaimChecked: is a checkbox ");
		  if (checkbox.checked) {
debug("isClaimChecked: is checked ");
		   if ( uri === undefined ) {
		     return "";
		   } else {
			 return uri;
		   }
		  } else {
debug("isClaimChecked: is not checked ");
		  }
		 } else {
		  debug( "expected type checkbox, but found: " + typeof(checkbox));
		 } 
	 } else {
	  debug("checkbox not defined for uri: " + uri );
	 }
	 return null;
}

function processManagedCard(
	managedCard, requiredClaims, optionalClaims, tokenType, clientPseudonym, 
	relyingPartyURL, relyingPartyCertB64, issuerPolicy) {

	if (issuerPolicy != null) {
		var to = xmlreplace(managedCard.carddata.managed.issuer);
		var mexAddress = issuerPolicy;
		var issuerMex = getMex1(to, mexAddress);
		debug("issuerMex=" + issuerMex);
	}
	
    var tokenToReturn = null;
    var mexResponse = getMexForCard(managedCard);

    if(mexResponse != null) {
        //Start with TransportBinding support
        var tb = mexResponse.indexOf("TransportBinding");
        if (tb < 0) {
           alert("The Selector currently supports only the TransportBinding");
           return null;
        } else {

            var bodyIndex = mexResponse.indexOf("Body>");
            bodyIndex += 5;
            var body = mexResponse.substring(bodyIndex);

            var addrIndex = body.indexOf("Address>");
            addrIndex += 8;
            var subStr = body.substring(addrIndex);

            var endAddr = subStr.indexOf("</");
            var address = subStr.substring(0,endAddr);


            debug(address);

            var ic = new Namespace("ic", "http://schemas.xmlsoap.org/ws/2005/05/identity");

			var usercredential = managedCard.carddata.managed.ic::UserCredential;
debug("processManagedCard::usercredential>>>" + usercredential);

            var rst = "<s:Envelope " + 
    			"xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" " + 
    			"xmlns:a=\"http://www.w3.org/2005/08/addressing\" " +
    			"xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">" + 
    			"<s:Header>" + 
              	 "<a:Action u:Id=\"_1\">http://schemas.xmlsoap.org/ws/2005/02/trust/RST/Issue</a:Action>" +
    			 "<o:Security s:mustUnderstand=\"1\" xmlns:o=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"; 

			if (!(usercredential.ic::UsernamePasswordCredential == undefined)) {
	            var hint = usercredential.ic::DisplayCredentialHint;
	            debug("hint:" + hint);
//	            var prompts = Components.classes["@mozilla.org/embedcomp/prompt-service;1"].getService(Components.interfaces.nsIPromptService);
	            var prompts = Components.classes["@mozilla.org/network/default-auth-prompt;1"].getService(Components.interfaces.nsIAuthPrompt);
	            username = {value:usercredential.ic::UsernamePasswordCredential.ic::Username};
	            password = {value:""};
//	            var check = {value: false};
	            hint = hint + "("+ username.value + ")";
//	            okorcancel = prompts.promptUsernameAndPassword(window, 'Card Authentication', hint, username, password, null, check);
	            okorcancel = prompts.promptUsernameAndPassword('Card Authentication', hint, address, prompts.SAVE_PASSWORD_PERMANENTLY, username, password);
	            if (okorcancel == false) {
	            	return null;
	            }
	            var uid =  username.value;
	            var pw =  password.value;

	            var messageIdInt = Math.floor(Math.random()*100000+1);
	            var messageId = "urn:uuid:" + messageIdInt;
	
	
	            rst = rst + "<o:UsernameToken u:Id=\"" + messageId + "\"><o:Username>";
	
	            rst = rst + xmlreplace(uid);
	
	            rst = rst + "</o:Username><o:Password o:Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">";
	
	            rst = rst + xmlreplace(pw);
	
	            rst = rst + "</o:Password></o:UsernameToken>";
	        }  else if (!(usercredential.ic::KerberosV5Credential == undefined)) {
				alert("unimplemented user credential type: KerberosV5Credential");
				return null;
	        } else if (!(usercredential.ic::X509V3Credential == undefined)) {
	            var dsig = new Namespace("dsig", "http://www.w3.org/2000/09/xmldsig#");
	            var wsa = new Namespace("wsa", "http://www.w3.org/2005/08/addressing");
	            var mex = new Namespace("mex", "http://schemas.xmlsoap.org/ws/2004/09/mex");
	            var wss = new Namespace("wss", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
				alert("unimplemented user credential type: X509V3Credential");
				return null;
	        } else if (!(usercredential.ic::SelfIssuedCredential == undefined)) {
	            var hint = usercredential.ic::DisplayCredentialHint;
	            debug("hint:" + hint);
	            var usercredential = usercredential.ic::SelfIssuedCredential.ic::PrivatePersonalIdentifier;
	            debug("usercredential:" + usercredential);
	            debug("stsCert:" + managedCard.carddata.managed.stsCert);
				alert("unimplemented user credential type: SelfIssuedCredential");
				return null;
			} else {
				alert("undefined user credential type");
				return null;
			}	        
	            
	        rst = rst + "</o:Security></s:Header>" +
            "<s:Body><wst:RequestSecurityToken Context=\"ProcessRequestSecurityToken\" " +
            "xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\">";
            
            if (!(managedCard.carddata.managed.requireAppliesTo == undefined)) {
            	var appliesTo = "<p:AppliesTo xmlns:p=\"http://schemas.xmlsoap.org/ws/2004/09/policy\"><a:EndpointReference>" + 
            		"<a:Address>" + xmlreplace(relyingPartyURL) + "</a:Address>" + 
                    "<i:Identity xmlns:i=\"http://schemas.xmlsoap.org/ws/2006/02/addressingidentity\">" + 
                    "<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:X509Data>" +
					"<ds:X509Certificate>" + relyingPartyCertB64 + "</ds:X509Certificate>" +
					"</ds:X509Data></ds:KeyInfo></i:Identity>" +
                    "</a:EndpointReference></p:AppliesTo>";
debug("requireAppliesTo" + appliesTo);
			    rst = rst + appliesTo;
            }
            
            rst = rst + "<wst:RequestType>http://schemas.xmlsoap.org/ws/2005/02/trust/Issue</wst:RequestType>" +
            "<wsid:InformationCardReference xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\">" +
            "<wsid:CardId>";
debug("cardid:"+ managedCard.id);
debug("cardid xmlreplaced:"+ xmlreplace(managedCard.id));
            rst = rst + xmlreplace(managedCard.id);

            rst = rst + "</wsid:CardId>";
            
            rst = rst + "<wsid:CardVersion>" + xmlreplace(managedCard.version) + "</wsid:CardVersion>" + "</wsid:InformationCardReference>";
            
            {
               var claims = requiredClaims + " " + optionalClaims;
               var claimsArray = claims.split(/\s+/);
	           var ic = new Namespace("ic", "http://schemas.xmlsoap.org/ws/2005/05/identity");
			   var list = managedCard.carddata.managed.ic::SupportedClaimTypeList.ic::SupportedClaimType;
			   var count=0;
			   var requestedClaims = "";
			   for (var index = 0; index<list.length(); index++) {
				 var supportedClaim = list[index];
				 var uri = supportedClaim.@Uri;
				 var claim = isClaimChecked("label_"+uri, uri);
				 if (claim != null) {
				  var i = claim.indexOf("?");
				  if (i > 0) { // dynamic claim. Uris starting with ? are not allowed
				   debug("dynamic claim: " + claim);
				   var prefix = claim.substr(0,i);
				   var foundit = false;
                   for (var index = 0; (index<claimsArray.length) && (foundit == false); index++) {
                    var requestedUri = claimsArray[index];
                    if (requestedUri.indexOf(prefix) == 0) {
				     debug("dynamic claim match: " + requestedUri);
				     requestedClaims = requestedClaims + "<wsid:ClaimType Uri=\"" + xmlreplace(requestedUri) + "\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"/>";
				     foundit = true;
                    }
                   }
                   if (foundit == false) {
                   	debug("dynamic claim: " + claim + " not found in " + claimsArray);
                   }
				  } else {
				  	debug("static claim: " + claim);
				  	requestedClaims = requestedClaims + "<wsid:ClaimType Uri=\"" + xmlreplace(uri) + "\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"/>";
				  }
				  count++;
				 } else {
				 	debug("claim is null: " + uri);
				 }
               }
               if (count == 0) {
                debug("no claims were requested!");
               }
               rst = rst + "<wst:Claims>" + requestedClaims + "</wst:Claims>";
            }
            
            rst = rst + "<wst:KeyType>http://schemas.xmlsoap.org/ws/2005/05/identity/NoProofKey</wst:KeyType>";
            
            // if a ppid is requested, then provide some selector entropy (clientPseudonym). The STS uses this to generate a RP depended ppid
            // even if the STS does not know the RP
            if (requiredClaims.indexOf("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier") >= 0) {
	            rst = rst + "<ClientPseudonym xmlns=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><PPID>" + xmlreplace(clientPseudonym) + "</PPID></ClientPseudonym>";
			}
			
            // tokenType is optional. http://docs.oasis-open.org/ws-sx/ws-trust/200512/ws-trust-1.3-os.html
            if (tokenType != null) {
	            rst = rst + "<wst:TokenType>" + xmlreplace(tokenType) + "</wst:TokenType>";
	        }
            rst = rst + "<wsid:RequestDisplayToken xml:lang=\"en\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"/>" +
            "</wst:RequestSecurityToken></s:Body></s:Envelope>";

debug("processManagedCard: request: " + rst);
            var rstr;
            var rstReq = new XMLHttpRequest();
            rstReq.open('POST', address, false);
            rstReq.setRequestHeader("Content-type", "application/soap+xml; charset=utf-8");
            rstReq.setRequestHeader("Cache-Control", "no-cache");
            rstReq.setRequestHeader("accept-language", "en-us");
            rstReq.setRequestHeader("User-Agent", "xmldap infocard stack");
            try {
	            rstReq.send(rst);
            } 
            catch (e) {
				debug(e);
				alert("posting the request to get the security tokens failed. " + e);
				return null;            	
            }
            if(rstReq.status == 200) {
debug("processManagedCard: request status 200");

                rstr = rstReq.responseText;
debug("processManagedCard: RSTR:" + rstr);

    var j = rstReq.responseText.indexOf("RequestedSecurityToken");
    if (j<0) {
     alert("token server did not sent a RequestedSecurityToken.\n" + rstReq.responseText);
     return null;
    }
    var prefix;
    if (rstReq.responseText.charAt(j-1) == ':') {
	    var start = rstReq.responseText.substring(0,j-1);
	    var i = start.lastIndexOf("<");
	    if (i<0) {
	     alert("illegal XML\n" + start);
	     return null;
	    }
	    var prefix = start.substring(i+1) + ":";
	} else {
	 	prefix = "";
	}    
debug("prefix=" + prefix);
    var rest = rstReq.responseText.substring(j);
debug("rest=" + rest);
    var l = rest.indexOf(">");
    rest = rest.substring(l+1);
debug("Rest=" + rest);
    var k = rest.indexOf("</" + prefix + "RequestedSecurityToken");
    var tokenToReturn = rest.substring(0,k);
    
debug("RSTR: " + tokenToReturn);
            } else {
	            debug("token request (" + address + ") failed. (" + rstReq.status +")\n" + rstReq.responseText);
	            alert("token request (" + address + ") failed. (" + rstReq.status +")\n" + rstReq.responseText);
//            	var responseXml = new XML(rstReq.responseText);
//            	var soap = new Namespace("soap", "http://www.w3.org/2003/05/soap-envelope");
//            	var text = responseXml..soap::Text;
//            	if (text == undefined) {
//	            	alert("token request (" + address + ") failed. (" + rstReq.status +")\n" + rstReq.responseText);
//	            } else {
//	            	alert("token request (" + address + ") failed. (" + rstReq.status +")\n" + text);
//	            }	
            }

        }

    } else {
    	alert("mex request (" + managedCard.carddata.managed.mex + ") failed. ");
    }

    return tokenToReturn;

}


function showPrivacyStatement() {
	var policy = window.arguments[0];
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

function disable(){
	var prefService = Components.classes[ 
		"@mozilla.org/preferences-service;1"].
			getService( Components.interfaces.nsIPrefBranch);
	var disabled = prefService.getBoolPref( "identityselector.disabled");
	debug("disabled=" + disabled);
	prefService.setBoolPref( "identityselector.disabled", true);
	disabled = prefService.getBoolPref( "identityselector.disabled");
	debug("disabled=" + disabled);

    var doc = window.document;
    var event = doc.createEvent("Events");
    event.initEvent("DisableIdentitySelector", true, true);
    window.dispatchEvent(event);

	cancel();
}

function cancel(){
	debug("cancel");
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

function load(){

    var select = document.getElementById('selectcontrol');
    select.addEventListener("click", ok, false);


    var newCardElm = document.getElementById('newCard');
    newCardElm.addEventListener("click", newCard, false);

    var deleteCardElm = document.getElementById('deleteCard');
    deleteCardElm.addEventListener("click", deleteCard, false);

    var cancelselector = document.getElementById('cancelselector');
    cancelselector.addEventListener("click", cancel, false);

    var stringsBundle = document.getElementById("string-bundle");

    var rpIdentifier = null;
   	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
	    var policy = window.arguments[0];
	    if (policy.hasOwnProperty("cert")) {
			var relyingPartyCertB64 = policy["cert"];
		    rpIdentifier = hex_sha1(relyingPartyCertB64);
	    }
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
    	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
			cardClass = computeCardClass(c, window.arguments[0]);
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
    	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
	        var policy = window.arguments[0];
	        var label = document.getElementById("notify");
			var site = "Unknown";
			if (policy.hasOwnProperty("cn")) {
				site = policy["cn"];
			} else if (policy.hasOwnProperty("url")) {
				site = policy["url"];
			}
	        var please = stringsBundle.getFormattedString('pleaseselectacard', [site]);
	        label.setAttribute("value", please);
	    } else {
	        var label = document.getElementById("notify");
	        label.setAttribute("value", "card management");
	    }
	} else {
        var label = document.getElementById("notify");
		var button = stringsBundle.getString('newcard');
        var youdont = stringsBundle.getFormattedString('youdonthaveanycards', [button]);
        label.setAttribute("value", youdont);
    }
	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
		var policy = window.arguments[0];
		var serializedPolicy = JSON.stringify(policy);
		var issuerLogoURL = TokenIssuer.getIssuerLogoURL(serializedPolicy);
		debug("issuerLogoURL=" + issuerLogoURL);
		if (issuerLogoURL != undefined) {
			var issuerlogo = document.getElementById("issuerlogo");
			issuerlogo.src = issuerLogoURL;
			issuerlogo.hidden = false;
			var issuerlogo_label = document.getElementById("issuerlogo_label");
            issuerlogo_label.hidden = false;
			var issuer_hbox = document.getElementById("issuer_hbox");
            issuer_hbox.hidden = false;
		}
	}
	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
		var policy = window.arguments[0];
	    var privacyUrl = policy["privacyUrl"];
        if (privacyUrl != null) {
	    debug("privacyUrl " + privacyUrl);
             var showPrivacyStatementElm = document.getElementById('privacy_label');
             showPrivacyStatementElm.addEventListener("click", showPrivacyStatement, false);
             showPrivacyStatementElm.hidden = false;
	    }
	}
	if (!beenThere) {
	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
		 var policy = window.arguments[0];
		 if (policy.hasOwnProperty("cn")) {
			 debug("never been here: " + policy["cn"]);
		 } else if (policy.hasOwnProperty("url")) {
		 	debug("never been here: " + policy["url"]);
		 } else {
		 	debug("never been here");
		 }
	}
	 var firstTimeVisit = document.getElementById('firstTimeVisit');
	 var labelText;
	 try {
	  labelText = stringsBundle.getString('firstTimeVisit');
	 } catch (e) {
	  labelText = "This is your first visit to this site. Think!";
	 }
	 debug("firstTime: " + labelText);
	 firstTimeVisit.setAttribute("value", labelText);
	 var firstTimeVisitBox = document.getElementById('firstTimeVisitBox');
	 firstTimeVisitBox.setAttribute("hidden", "false");
	}
}

function indicateRequiredClaim(requiredClaims, optionalClaims, claim){
 var name = "_" + claim;
 var element = document.getElementById(name);
 if (element == undefined) {
  debug( "Element " + name + " not found" );
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
    //debug("optional claim " + claim + " found in " + optionalClaims);
    element.checked = false;
    element.disabled = false;
    return;
  }
 } 
  
 //debug("claim " + claim + " not found");
 element.checked = false;
 element.disabled = true;
}

function indicateRequiredClaims(){
 if (window.arguments == undefined) return;
 if (window.arguments.length > 0) {
	 var policy = window.arguments[0];
	 var requiredClaims = policy["requiredClaims"];
	 if (requiredClaims == undefined) return;
	 if (requiredClaims == null) return;
	
	 var optionalClaims = policy["optionalClaims"];
	 
	 //requiredClaims = requiredClaims.toLowerCase();
	 //if (optionalClaims != null) {
	 	 //optionalClaims = optionalClaims.toLowerCase();
	 //}
	
	debug("requiredClaims: " + requiredClaims);
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
}

function setCardSelf() {
	//debug("setCardSelf card= " + selectedCard);
	debug("setCardSelf givenname: " + selectedCard.carddata.selfasserted.givenname);
        document.getElementById("givenname").value = selectedCard.carddata.selfasserted.givenname;
	debug("setCardSelf: " + document.getElementById("givenname").value);
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

        indicateRequiredClaims();

        var grid = document.getElementById("editgrid");
        grid.setAttribute("hidden", "false");


        var grid1 = document.getElementById("editgrid1");
        grid1.setAttribute("hidden", "false");

		var stringsBundle = document.getElementById("string-bundle");
		var selfassertedcard = stringsBundle.getString('selfassertedcard');
        var label = document.getElementById("notify");
        label.setAttribute("value", selfassertedcard);
}

function setCardManaged(requiredClaims, optionalClaims) {
	debug("setCardManaged requiredClaims: " + requiredClaims);
	debug("setCardManaged optionalClaims: " + optionalClaims);
	
		var managedRows = document.getElementById("managedRows0");
		
		// remove child rows before appending new ones
		while (managedRows.hasChildNodes()) { 
  		 managedRows.removeChild(managedRows.childNodes[0]);
		}
		
		var ic = new Namespace("ic", "http://schemas.xmlsoap.org/ws/2005/05/identity");
		var list = selectedCard.carddata.managed.ic::SupportedClaimTypeList.ic::SupportedClaimType;
		//alert("root type:" + typeof(selectedCard));
		//alert("list type:" + typeof(list));
		//alert("list[0] type:" + typeof(list[0]));
		//alert(list[0]);
		//alert("length:" + list.length());
		//alert(list.toXMLString());
		debug("number of supported claims: " + list.length());
		var half = list.length() / 2;
		var index=0;
		for (; index<half; index++) {
		 var supportedClaim = list[index];
		 var uri = supportedClaim.@Uri;
  		 var row = document.createElement("row");
  		 row.setAttribute("class", "rowClass");
  		 var checkbox = document.createElement("checkbox");
  		 var label;
  		 var displayTag = "" + supportedClaim.ic::DisplayTag;
  		 if (displayTag.length > 10) {
  		  label = displayTag.substring(0,9);
  		 } else {
  		  label = displayTag;
  		 }
  		 label = xmlreplace(label);
		 checkbox.setAttribute("label", label);
		 checkbox.setAttribute("id", "label_"+uri);
 		 checkbox.setAttribute("class", "claimLabel");
 		 checkbox.setAttribute("crop", "end");
	     checkbox.setAttribute("checked", "false");
 		 checkbox.setAttribute("disabled", "true");
 		 var value = "";
		 if (optionalClaims != null) {
		  var ui = optionalClaims.indexOf(uri);
		  if (ui != -1) {
 		   checkbox.setAttribute("checked", "false");
 		   checkbox.setAttribute("disabled", "false");
debug("optional claim:" + uri);
 		   var thisClaim = optionalClaims.substr(ui);
 		   var ws = thisClaim.indexOf(' '); // space
 		   if (ws == -1) {
 		   	ws = thisClaim.indexOf('	'); // tab
 		   	if (ws == -1) {
 		   		ws = thisClaim.indexOf(String.fromCharCode(10));  // line-feed
 		   		if (ws == -1) {
 		   			ws = thisClaim.indexOf(String.fromCharCode(13)); // carriage return
 		   		}
 		   	}
 		   }
 		   if (ws != -1) {
 		   	thisClaim = thisClaim.substring(0,ws);
 		   	debug("thisClaim: " + thisClaim);
 		   }
		   var qi = thisClaim.indexOf('?');
debug("qi claim:" + thisClaim + " " + qi);
		   if (qi >= 0) {
				value = thisClaim.substr(qi);
				debug("variable claim value: " + value);	
		   }
		  }
		 }
		 if (requiredClaims != null) {
		  var ui = requiredClaims.indexOf(uri);
		  if (ui != -1) {
 		   checkbox.setAttribute("checked", "true");
 		   checkbox.setAttribute("disabled", "true");
debug("requiredClaim claim:" + uri);
 		   var thisClaim = requiredClaims.substr(ui);
debug("thisClaim=" + thisClaim);
 		   var ws = thisClaim.indexOf(' '); // space
 		   if (ws == -1) {
 		   	ws = thisClaim.indexOf('	'); // tab
 		   	if (ws == -1) {
 		   		ws = thisClaim.indexOf(String.fromCharCode(10));  // line-feed
 		   		if (ws == -1) {
 		   			ws = thisClaim.indexOf(String.fromCharCode(13)); // carriage return
 		   		}
 		   	}
 		   }
 		   if (ws != -1) {
 		   	thisClaim = thisClaim.substring(0,ws);
 		   	debug("thisClaim: " + thisClaim);
 		   }
		   var qi = thisClaim.indexOf('?');
debug("qi Claim:" + thisClaim + " " + qi);
		   if (qi >= 0) {
				value = thisClaim.substr(qi);
				debug("variable Claim value: " + value);	
		   }
		  }
		 }
		 try {
		 	  // DisplayTag should be changed to Description when description is supported
			 checkbox.setAttribute("tooltiptext", supportedClaim.ic::DisplayTag); // this is not cropped
		 }
		 catch (err) {
		  // tooltiptext barfs on "invalid character" while value does not... Axel
		  debug(err + "(" + supportedClaim.ic::DisplayTag + ")");
		 }
		 var textbox = document.createElement("textbox");
		 textbox.setAttribute("id", uri);
		 textbox.setAttribute("class", "claimText");
		 textbox.setAttribute("value", value);
		 textbox.setAttribute("readonly", "true");
		 row.appendChild(checkbox);
		 row.appendChild(textbox);
		 managedRows.appendChild(row);
		}
		if (managedRows.hasChildNodes()) {
	        var grid = document.getElementById("editgrid2");
	        grid.setAttribute("hidden", "false");
		}
		
		managedRows = document.getElementById("managedRows1");
		
		// remove child rows before appending new ones
		while (managedRows.hasChildNodes()) { 
  		 managedRows.removeChild(managedRows.childNodes[0]);
		}
		for (; index<list.length(); index++) {
		 var supportedClaim = list[index];
		 var uri = supportedClaim.@Uri;
  		 var row = document.createElement("row");
  		 row.setAttribute("class", "rowClass");
  		 var checkbox = document.createElement("checkbox");
  		 var label;
  		 var displayTag = "" + supportedClaim.ic::DisplayTag;
  		 if (displayTag.length > 10) {
  		  label = displayTag.substring(0,9);
  		 } else {
  		  label = displayTag;
  		 }
  		 label = xmlreplace(label);
		 checkbox.setAttribute("id", "label_"+uri);
		 checkbox.setAttribute("label", label);
 		 checkbox.setAttribute("class", "claimLabel");
 		 checkbox.setAttribute("crop", "end");
 		 checkbox.setAttribute("checked", "false");
 		 checkbox.setAttribute("disabled", "true");
		 if (optionalClaims != null) {
		  var ui = optionalClaims.indexOf(uri);
		  if (ui != -1) {
 		   checkbox.setAttribute("checked", "false");
 		   checkbox.setAttribute("disabled", "false");
debug("optional claim:" + uri);
 		   var thisClaim = optionalClaims.substr(ui);
 		   var ws = thisClaim.indexOf(' '); // space
 		   if (ws == -1) {
 		   	ws = thisClaim.indexOf('	'); // tab
 		   	if (ws == -1) {
 		   		ws = thisClaim.indexOf(String.fromCharCode(10));  // line-feed
 		   		if (ws == -1) {
 		   			ws = thisClaim.indexOf(String.fromCharCode(13)); // carriage return
 		   		}
 		   	}
 		   }
 		   if (ws != -1) {
 		   	thisClaim = thisClaim.substring(0,ws);
 		   	debug("thisClaim: " + thisClaim);
 		   }
		   var qi = thisClaim.indexOf('?');
debug("qi claim:" + thisClaim + " " + qi);
		   if (qi >= 0) {
				value = thisClaim.substr(qi);
				debug("variable claim value: " + value);	
		   }
		  }
		 }
		 if (requiredClaims != null) {
		  var ui = requiredClaims.indexOf(uri);
		  if (ui != -1) {
 		   checkbox.setAttribute("checked", "true");
 		   checkbox.setAttribute("disabled", "true");
debug("requiredClaim claim:" + uri);
 		   var thisClaim = requiredClaims.substr(ui);
debug("thisClaim=" + thisClaim);
 		   var ws = thisClaim.indexOf(' '); // space
 		   if (ws == -1) {
 		   	ws = thisClaim.indexOf('	'); // tab
 		   	if (ws == -1) {
 		   		ws = thisClaim.indexOf(String.fromCharCode(10));  // line-feed
 		   		if (ws == -1) {
 		   			ws = thisClaim.indexOf(String.fromCharCode(13)); // carriage return
 		   		}
 		   	}
 		   }
 		   if (ws != -1) {
 		   	thisClaim = thisClaim.substring(0,ws);
 		   	debug("thisClaim: " + thisClaim);
 		   }
		   var qi = thisClaim.indexOf('?');
debug("qi Claim:" + thisClaim + " " + qi);
		   if (qi >= 0) {
				value = thisClaim.substr(qi);
				debug("variable Claim value: " + value);	
		   }
		  }
		 }
		 try {
		 	  // DisplayTag should be changed to Description when description is supported
			 checkbox.setAttribute("tooltiptext", supportedClaim.ic::DisplayTag); // this is not cropped
		 }
		 catch (err) {
		  // tooltiptext barfs on "invalid character" while value does not... Axel
		  debug(err + "(" + supportedClaim.ic::DisplayTag + ")");
		 }
		 var textbox = document.createElement("textbox");
		 textbox.setAttribute("id", uri);
		 textbox.setAttribute("class", "claimText");
		 textbox.setAttribute("value", value);
		 textbox.setAttribute("readonly", "true");
		 row.appendChild(checkbox);
		 row.appendChild(textbox);
		 managedRows.appendChild(row);
		}
		if (managedRows.hasChildNodes()) {
	        var grid1 = document.getElementById("editgrid3");
    	    grid1.setAttribute("hidden", "false");
		}
		
		var stringsBundle = document.getElementById("string-bundle");
		var managedcardfromissuer = stringsBundle.getFormattedString('managedcardfromissuer', [selectedCard.carddata.managed.issuer]);
        var label = document.getElementById("notify");
        label.setAttribute("value", managedcardfromissuer );
}

function setCard(card){

	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
	    var select = document.getElementById('selectcontrol');
    	select.setAttribute('hidden', 'false');
	}
	
    var showPrivacyStatementElm = document.getElementById('privacy_label');
    showPrivacyStatementElm.hidden = true;
    
	var issuerlogo = document.getElementById("issuerlogo");
	issuerlogo.src = "";
	issuerlogo.hidden = true;
	var issuerlogo_label = document.getElementById("issuerlogo_label");
    issuerlogo_label.hidden = true;
	var issuer_hbox = document.getElementById("issuer_hbox");
    issuer_hbox.hidden = true;

    selectedCard = card;

    debug("TYPE: " + selectedCard.type);
//    debug(selectedCard);

    var selfassertedClaims = document.getElementById('selfassertedClaims');
    var managedClaims = document.getElementById('managedClaims');

    if (selectedCard.type == "selfAsserted" )  {
        selfassertedClaims.setAttribute("hidden", "false");
        managedClaims.setAttribute("hidden", "true");
		var cardname = document.getElementById("cardname");
        cardname.value = selectedCard.name;
        cardname.hidden = false;
		setCardSelf();
    }  else if (selectedCard.type == "managedCard" )   {
	    var requiredClaims = null;
	    var optionalClaims = null;
 	    if (!(window.arguments == undefined)) {
	     if (window.arguments.length > 0) {
		  var policy = window.arguments[0];
		  requiredClaims = policy["requiredClaims"];
   	      optionalClaims = policy["optionalClaims"];
	     }
	    }	
        selfassertedClaims.setAttribute("hidden", "true");
        managedClaims.setAttribute("hidden", "false");

		var cardname = document.getElementById("cardname");
        cardname.value = selectedCard.name;
        cardname.hidden = false;
		setCardManaged(requiredClaims, optionalClaims);
    } else if (selectedCard.type == "openid" )  {


        var label = document.getElementById("notify");
        label.setAttribute("value", "Use OpenID with Identity URL: " + selectedCard.id);

		var cardname = document.getElementById("cardname");
        cardname.value = "";
        cardname.hidden = true;

        selfassertedClaims.setAttribute("hidden", "true");
        managedClaims.setAttribute("hidden", "true");

    } else  {
    	alert("unsupported card type\n" + selectedCard.type);
    	return;
    }
}

function dblclick(event) {
	handleCardChoice(event);
	if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
		ok();
	} // else cardManager.xul called from preferences
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
    debug ("Setting cardid " + hbox.getAttribute("cardid"));
    return hbox;

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
  debug("tokenType: " + tokenType + " card:" + card.name);
  if (card.type == "managedCard") {
	  var ic = new Namespace("ic", "http://schemas.xmlsoap.org/ws/2005/05/identity");
	  var trust = new Namespace("trust", "http://schemas.xmlsoap.org/ws/2005/02/trust");
	  var list = card.carddata.managed.ic::SupportedTokenTypeList.trust::TokenType;
	  
	  for (var index = 0; index<list.length(); index++) {
	   var cardTokenType = list[index];
	   if (tokenType == cardTokenType) {
	    matchingTokenType = true;
	    debug("matchingTokenType:" + tokenType);
	    return matchingTokenType;
	   } else {
	    debug("notMatchingTokenType:" + cardTokenType);
	   }
	  }
  } else if (card.type == "selfAsserted") {
   if (tokenType == "urn:oasis:names:tc:SAML:1.0:assertion") {
    matchingTokenType = true;
   } else if (tokenType ==  "http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV1.1") {
    matchingTokenType = true;
   } else {
    debug("tokenType does not match tokenTypes for self-issued cards:" + tokenType);
   }
  } else {
   debug("unsupported card type");
   matchingTokenType = false;
  }
 } else { // RP does not require a special tokentype
  matchingTokenType = true;
 }
 return matchingTokenType;
}

function computeHasBeenSend(card, policy) {
	if (!policy.hasOwnProperty("cert")) {
		return false;
	}
	var relyingPartyCertB64 = policy["cert"];
    var rpIdentifier = hex_sha1(relyingPartyCertB64);
    var beenThere = false;
    for each (rpId in card.rpIds) {
     debug(card.name + " RpId:" + rpId + " RpIdentifier:" + rpIdentifier);
     if (rpId == rpIdentifier) {
      debug(card.name + " has been sent to: " + policy["cn"]);
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
function computeCardClass(card) {
 var cardClass;
 var policy = null;
 if (!(window.arguments == undefined)) {
  policy = window.arguments[0];
 }
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
 debug("cardClass " + cardClass + " " + card.name);
 return cardClass;
}

function saveCard(card){
    storeCard(card);
    var cardArea = document.getElementById("cardselection");
    if (cardArea) {
	    var cardClass = computeCardClass(card);
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

function digestNewCard(callback) {
	
	if (callback == undefined) {
	 alert("no new card was imported");
	 return;
	}
	if (callback == null) {
	 alert("No new card was imported");
	 return;
	}
	
    var cardName = callback.cardName;
    var type = callback.type;
    var cardId = "" + callback.cardId;

    var cardFile = readCardStore();
    for each (c in cardFile.infocard) {
    	debug("newCard: cardId=" + typeof(c.id));
    	if ("" + c.id == cardId) {
    		alert("This card is already in the card store. Please delete it first.");
    		return;
    	} else {
    		debug("newCard: cardId=" + cardId + "!=" + c.id);
    	}
	}
	
    if ( type == "selfAsserted") {

        var card = new XML("<infocard/>");
        card.name = cardName;
        card.type = type;
        if (callback["cardVersion"] == undefined) {
	        card.version = 1;
	    } else {
	    	card.version = callback["cardVersion"];
	    }
        var id = Math.floor(Math.random()*100000+1);
        card.id = id;
        card.privatepersonalidentifier = hex_sha1(cardName + version + id);

        var count = 0;
        var data = new XML("<selfasserted/>");
        if ( type == "selfAsserted") {

            var givenName = callback.givenname;
            if (givenName) {
                card.supportedclaim[count] = "givenname";
                data.givenname = givenName;
                count++;
            }
            var surname = callback.surname;
            if (surname) {
                card.supportedclaim[count] = "surname";
                data.surname = surname;
                count++;
            }
            var emailAddress = callback.email;
            if (emailAddress) {
                card.supportedclaim[count] = "emailaddress";
                data.emailaddress = emailAddress;
                count++;
            }
            var streetAddress = callback.streetAddress;
            if (streetAddress) {
                card.supportedclaim[count] = "streetaddress";
                data.streetaddress = streetAddress;
                count++;
            }
            var locality = callback.locality;
            if (locality) {
                card.supportedclaim[count] = "locality";
                data.locality = locality;
                count++;
            }
            var stateOrProvince = callback.stateOrProvince;
            if (stateOrProvince) {
                card.supportedclaim[count] = "stateorprovince";
                data.stateorprovince = stateOrProvince;
                count++;
            }
            var postalCode = callback.postalCode;
            if (postalCode) {
                card.supportedclaim[count] = "postalcode";
                data.postalcode = postalCode;
                count++;
            }
            var country = callback.country;
            if (country) {
                card.supportedclaim[count] = "country";
                data.country = country;
                count++;
            }
            var primaryPhone = callback.primaryPhone;
            if (primaryPhone) {
                card.supportedclaim[count] = "primaryphone";
                data.primaryphone = primaryPhone;
                count++;
            }
            var otherPhone = callback.otherPhone;
            if (otherPhone) {
                card.supportedclaim[count] = "otherphone";
                data.otherphone = otherPhone;
                count++;
            }
            var mobilePhone = callback.mobilePhone;
            if (mobilePhone) {
                card.supportedclaim[count] = "mobilephone";
                data.mobilephone = mobilePhone;
                count++;
            }
            var dateOfBirth = callback.dateOfBirth;
            if (dateOfBirth) {
                card.supportedclaim[count] = "dateofbirth";
                data.dateofbirth = dateOfBirth;
                count++;
            }
            var gender = callback.gender;
            if (gender) {
                card.supportedclaim[count] = "gender";
                data.gender = gender;
                count++;
            }
            var imgurl = callback.imgurl;
            if (imgurl) {
                card.supportedclaim[count] = "imgurl";
                data.imgurl = imgurl;
                count++;
            }


        }

        card.carddata.data = data;
        saveCard(card);

    }

    if ( type == "managedCard") {
debug(JSON.stringify(callback));
        var card = new XML("<infocard/>");
        card.name = "" + cardName + "";
        card.type = type;
        card.version = "" + callback.cardVersion + "";
        card.id = "" + callback.cardId + "";

        var data = new XML("<managed/>");
        data.issuer = "" + callback.issuer + "";
        data.mex = "" + callback.mex + "";
//        data.username = "" + callback.uid + "";
//        data.KeyIdentifier = "" + callback.KeyIdentifier + "";
//        data.hint = "" + callback.hint + "";
        data.image = "data:image/png;base64," + callback.cardImage + "";
        var supportedClaims = new XML(callback.supportedClaims);
        data.supportedClaims = supportedClaims;
debug("new card" + callback.usercredential);
		data.usercredential = new XML(callback.usercredential);
		data.stsCert = "" + callback.stsCert + "";
		data.requireAppliesTo = "" + callback.requireAppliesTo + "";
		data.supportedTokenTypeList = new XML(callback.supportedTokenTypeList);
		
        card.carddata.data = data;
        saveCard(card);



    }


    if ( type == "openid") {

        var card = new XML("<infocard/>");
        card.name = "" + cardName + "";
        card.type = type;
        var version = "1";
        card.version = version;
        card.id = "" + callback.cardId + "";
        saveCard(card);

    }


}

function reload() {
    var cardArea = document.getElementById("cardselection");
    while (cardArea.hasChildNodes())
	{
	  cardArea.removeChild(cardArea.firstChild);
	}


    var grid = document.getElementById("editgrid");
    grid.setAttribute("hidden", "true");

    var grid1 = document.getElementById("editgrid1");
    grid1.setAttribute("hidden", "true");

    var grid = document.getElementById("editgrid2");
    grid.setAttribute("hidden", "false");

    var grid1 = document.getElementById("editgrid3");
    grid1.setAttribute("hidden", "false");

    var label = document.getElementById("notify");
    label.setAttribute("value", "Please select another card");

    var select = document.getElementById('selectcontrol');
    select.setAttribute('hidden', 'true');

		var cardname = document.getElementById("cardname");
        cardname.value = "";
        cardname.hidden = false;

    selectedCard = null;

    load();
}

function deleteCard(){

    debug("Delete Card : " + selectedCardId);
    
    var selectedCardId = selectedCard.id;
    removeCard(selectedCardId);
	reload();
}

function setOptionalClaimsSelf(policy) {
	    var optionalClaims = "";
    if (!(policy["optionalClaims"] == undefined)) {
     optionalClaims = policy["optionalClaims"];
     if (optionalClaims != null) {
      debug("setOptionalClaimsSelf optionalClaims: " + optionalClaims);
      var checkedClaims = null;
      var claims = optionalClaims.split(/\s+/);
      debug("setOptionalClaimsSelf claims: " + claims);
      var i;
      for (i in claims) {
       var claim = claims[i];
       debug("setOptionalClaimsSelf claim: " + claim);
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
        debug("processCard: claim not in list:" + claim);
       }
      }
      debug("setOptionalClaimsSelf checkedClaims: " + checkedClaims);
	  policy["optionalClaims"] = checkedClaims;
     }
    }
}
function processCard(policy, enableDebug){

    if (enableDebug) {
        var jvm = Components.classes["@mozilla.org/oji/jvm-mgr;1"].getService(Components.interfaces.nsIJVMManager);
        jvm.showJavaConsole();
    }
    

    var token;
    
    var serializedPolicy = JSON.stringify(policy);
    token = TokenIssuer.getToken(serializedPolicy);

    return token;

}

function debug(msg) {
  var debug = Components.classes["@mozilla.org/consoleservice;1"].getService(Components.interfaces.nsIConsoleService);
  debug.logStringMessage("infocard: " + msg);
}
