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

icDebug("getMex: mex request: " + mex);
icDebug("managedCard.carddata.managed.mex: " + mexAddress);

    var req = new XMLHttpRequest();
    req.open('POST', mexAddress, false);
    icDebug('mex xmlhttprequest open');
    req.setRequestHeader("Content-type", "application/soap+xml; charset=utf-8");
    req.setRequestHeader("Cache-Control", "no-cache");
    req.setRequestHeader("accept-language", "en-us");
    req.setRequestHeader("User-Agent", "xmldap infocard stack");
    icDebug('mex xmlhttprequest send');
    try {
	    req.send(mex);
    }
    catch (e) {
    	icDebug(e);
    	alert("posting the MEX request failed." + e);
    	return null;
    }
icDebug("getMex: mex POST request status="+req.status);
    if(req.status == 200) {
icDebug("getMex: mex POST request status 200");

        mexResponse = req.responseText;
icDebug("getMex POST 200: " + mexResponse);
        return mexResponse;
    } else {
icDebug("getMex POST " + req.status + ": " + req.responseText);
	    req.open('GET', mexAddress, false);
	    icDebug('mex xmlhttprequest open');
	    req.setRequestHeader("Content-type", "application/soap+xml; charset=utf-8");
	    req.setRequestHeader("Cache-Control", "no-cache");
	    req.setRequestHeader("accept-language", "en-us");
	    req.setRequestHeader("User-Agent", "xmldap infocard stack");
	    icDebug('mex GET xmlhttprequest send');
	    try {
		    req.send(null);
	    }
	    catch (e) {
	    	icDebug(e);
    		alert("getting the MEX request failed." + e);
    		return null;
	    }
icDebug("getMex: mex GET request status="+req.status);
    if(req.status == 200) {
icDebug("getMex: mex GET request status 200");
        mexResponse = req.responseText;
icDebug("getMex: " + mexResponse);
        return mexResponse;
    } else {
    	icDebug("getMex GET " + req.status + ": " + req.responseText);
    }
	return null;
}
}

function getMexForCard(managedCard) {
	var to = xmlreplace(managedCard.carddata.managed.mex);
	var mexAddress = managedCard.carddata.managed.mex;
	var mexResponse = getMex1(to, mexAddress);
	return mexResponse;
}

function processManagedCard(
		managedCard, requiredClaims, optionalClaims, tokenType, clientPseudonym, 
		relyingPartyURL, relyingPartyCertB64, issuerPolicy) {

		if (issuerPolicy != null) {
			var to = xmlreplace(managedCard.carddata.managed.issuer);
			var mexAddress = issuerPolicy;
			var issuerMex = getMex1(to, mexAddress);
			icDebug("issuerMex=" + issuerMex);
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


	            icDebug(address);

	            var ic = new Namespace("ic", "http://schemas.xmlsoap.org/ws/2005/05/identity");

				var usercredential = managedCard.carddata.managed.ic::UserCredential;
	icDebug("processManagedCard::usercredential>>>" + usercredential);

	            var rst = "<s:Envelope " + 
	    			"xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" " + 
	    			"xmlns:a=\"http://www.w3.org/2005/08/addressing\" " +
	    			"xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">" + 
	    			"<s:Header>" + 
	              	 "<a:Action u:Id=\"_1\">http://schemas.xmlsoap.org/ws/2005/02/trust/RST/Issue</a:Action>" +
	    			 "<o:Security s:mustUnderstand=\"1\" xmlns:o=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"; 

				if (!(usercredential.ic::UsernamePasswordCredential == undefined)) {
		            var hint = usercredential.ic::DisplayCredentialHint;
		            icDebug("hint:" + hint);
//		            var prompts = Components.classes["@mozilla.org/embedcomp/prompt-service;1"].getService(Components.interfaces.nsIPromptService);
		            var prompts = Components.classes["@mozilla.org/network/default-auth-prompt;1"].getService(Components.interfaces.nsIAuthPrompt);
		            username = {value:usercredential.ic::UsernamePasswordCredential.ic::Username};
		            password = {value:""};
//		            var check = {value: false};
		            hint = hint + "("+ username.value + ")";
//		            okorcancel = prompts.promptUsernameAndPassword(window, 'Card Authentication', hint, username, password, null, check);
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
		            icDebug("hint:" + hint);
		            var usercredential = usercredential.ic::SelfIssuedCredential.ic::PrivatePersonalIdentifier;
		            icDebug("usercredential:" + usercredential);
		            icDebug("stsCert:" + managedCard.carddata.managed.stsCert);
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
	            		"<a:Address>" + xmlreplace(relyingPartyURL) + "</a:Address>";
	            	if (relyingPartyCertB64 != null) {
	            		appliesTo = appliesTo + 
	                    "<i:Identity xmlns:i=\"http://schemas.xmlsoap.org/ws/2006/02/addressingidentity\">" + 
	                    "<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:X509Data>" +
						"<ds:X509Certificate>" + relyingPartyCertB64 + "</ds:X509Certificate>" +
						"</ds:X509Data></ds:KeyInfo></i:Identity>";
	            	}
	            	appliesTo = appliesTo + "</a:EndpointReference></p:AppliesTo>";
	icDebug("requireAppliesTo" + appliesTo);
				    rst = rst + appliesTo;
	            }
	            
	            rst = rst + "<wst:RequestType>http://schemas.xmlsoap.org/ws/2005/02/trust/Issue</wst:RequestType>" +
	            "<wsid:InformationCardReference xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\">" +
	            "<wsid:CardId>";
	icDebug("cardid:"+ managedCard.id);
	icDebug("cardid xmlreplaced:"+ xmlreplace(managedCard.id));
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
					   icDebug("dynamic claim: " + claim);
					   var prefix = claim.substr(0,i);
					   var foundit = false;
	                   for (var index = 0; (index<claimsArray.length) && (foundit == false); index++) {
	                    var requestedUri = claimsArray[index];
	                    if (requestedUri.indexOf(prefix) == 0) {
					     icDebug("dynamic claim match: " + requestedUri);
					     requestedClaims = requestedClaims + "<wsid:ClaimType Uri=\"" + xmlreplace(requestedUri) + "\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"/>";
					     foundit = true;
	                    }
	                   }
	                   if (foundit == false) {
	                   	icDebug("dynamic claim: " + claim + " not found in " + claimsArray);
	                   }
					  } else {
					  	icDebug("static claim: " + claim);
					  	requestedClaims = requestedClaims + "<wsid:ClaimType Uri=\"" + xmlreplace(uri) + "\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"/>";
					  }
					  count++;
					 } else {
					 	icDebug("claim is null: " + uri);
					 }
	               }
	               if (count == 0) {
	                icDebug("no claims were requested!");
	               }
	               rst = rst + "<wst:Claims>" + requestedClaims + "</wst:Claims>";
	            }
	            
	            rst = rst + "<wst:KeyType>http://schemas.xmlsoap.org/ws/2005/05/identity/NoProofKey</wst:KeyType>";
	            
//	            if (managedCard.carddata.managed.requireAppliesTo == undefined) {
	            // if a ppid is requested, then provide some selector entropy (clientPseudonym). The STS uses this to generate a RP depended ppid
	            // even if the STS does not know the RP
	            if (requiredClaims.indexOf("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier") >= 0) {
		            rst = rst + "<ClientPseudonym xmlns=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><PPID>" + xmlreplace(clientPseudonym) + "</PPID></ClientPseudonym>";
				}
//	          }
	            // tokenType is optional. http://docs.oasis-open.org/ws-sx/ws-trust/200512/ws-trust-1.3-os.html
	            if (tokenType != null) {
		            rst = rst + "<wst:TokenType>" + xmlreplace(tokenType) + "</wst:TokenType>";
		        }
	            rst = rst + "<wsid:RequestDisplayToken xml:lang=\"en\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"/>" +
	            "</wst:RequestSecurityToken></s:Body></s:Envelope>";

	icDebug("processManagedCard: request: " + rst);
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
					icDebug(e);
					alert("posting the request to get the security tokens failed. " + e);
					return null;            	
	            }
	            if(rstReq.status == 200) {
	icDebug("processManagedCard: request status 200");

	                rstr = rstReq.responseText;
	icDebug("processManagedCard: RSTR:" + rstr);

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
	icDebug("prefix=" + prefix);
	    var rest = rstReq.responseText.substring(j);
	icDebug("rest=" + rest);
	    var l = rest.indexOf(">");
	    rest = rest.substring(l+1);
	icDebug("Rest=" + rest);
	    var k = rest.indexOf("</" + prefix + "RequestedSecurityToken");
	    var tokenToReturn = rest.substring(0,k);
	    
	icDebug("RSTR: " + tokenToReturn);
	            } else {
		            icDebug("token request (" + address + ") failed. (" + rstReq.status +")\n" + rstReq.responseText);
		            alert("token request (" + address + ") failed. (" + rstReq.status +")\n" + rstReq.responseText);
//	            	var responseXml = new XML(rstReq.responseText);
//	            	var soap = new Namespace("soap", "http://www.w3.org/2003/05/soap-envelope");
//	            	var text = responseXml..soap::Text;
//	            	if (text == undefined) {
//		            	alert("token request (" + address + ") failed. (" + rstReq.status +")\n" + rstReq.responseText);
//		            } else {
//		            	alert("token request (" + address + ") failed. (" + rstReq.status +")\n" + text);
//		            }	
	            }

	        }

	    } else {
	    	alert("mex request (" + managedCard.carddata.managed.mex + ") failed. ");
	    }

	    return tokenToReturn;

	}

function setCardManaged(requiredClaims, optionalClaims) {
	icDebug("setCardManaged requiredClaims: " + requiredClaims);
	icDebug("setCardManaged optionalClaims: " + optionalClaims);
	
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
		icDebug("number of supported claims: " + list.length());
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
icDebug("optional claim:" + uri);
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
 		   	icDebug("thisClaim: " + thisClaim);
 		   }
		   var qi = thisClaim.indexOf('?');
icDebug("qi claim:" + thisClaim + " " + qi);
		   if (qi >= 0) {
				value = thisClaim.substr(qi);
				icDebug("variable claim value: " + value);	
		   }
		  }
		 }
		 if (requiredClaims != null) {
		  var ui = requiredClaims.indexOf(uri);
		  if (ui != -1) {
 		   checkbox.setAttribute("checked", "true");
 		   checkbox.setAttribute("disabled", "true");
icDebug("requiredClaim claim:" + uri);
 		   var thisClaim = requiredClaims.substr(ui);
icDebug("thisClaim=" + thisClaim);
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
 		   	icDebug("thisClaim: " + thisClaim);
 		   }
		   var qi = thisClaim.indexOf('?');
icDebug("qi Claim:" + thisClaim + " " + qi);
		   if (qi >= 0) {
				value = thisClaim.substr(qi);
				icDebug("variable Claim value: " + value);	
		   }
		  }
		 }
		 try {
		 	  // DisplayTag should be changed to Description when description is supported
			 checkbox.setAttribute("tooltiptext", supportedClaim.ic::DisplayTag); // this is not cropped
		 }
		 catch (err) {
		  // tooltiptext barfs on "invalid character" while value does not... Axel
		  icDebug(err + "(" + supportedClaim.ic::DisplayTag + ")");
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
icDebug("optional claim:" + uri);
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
 		   	icDebug("thisClaim: " + thisClaim);
 		   }
		   var qi = thisClaim.indexOf('?');
icDebug("qi claim:" + thisClaim + " " + qi);
		   if (qi >= 0) {
				value = thisClaim.substr(qi);
				icDebug("variable claim value: " + value);	
		   }
		  }
		 }
		 if (requiredClaims != null) {
		  var ui = requiredClaims.indexOf(uri);
		  if (ui != -1) {
 		   checkbox.setAttribute("checked", "true");
 		   checkbox.setAttribute("disabled", "true");
icDebug("requiredClaim claim:" + uri);
 		   var thisClaim = requiredClaims.substr(ui);
icDebug("thisClaim=" + thisClaim);
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
 		   	icDebug("thisClaim: " + thisClaim);
 		   }
		   var qi = thisClaim.indexOf('?');
icDebug("qi Claim:" + thisClaim + " " + qi);
		   if (qi >= 0) {
				value = thisClaim.substr(qi);
				icDebug("variable Claim value: " + value);	
		   }
		  }
		 }
		 try {
		 	  // DisplayTag should be changed to Description when description is supported
			 checkbox.setAttribute("tooltiptext", supportedClaim.ic::DisplayTag); // this is not cropped
		 }
		 catch (err) {
		  // tooltiptext barfs on "invalid character" while value does not... Axel
		  icDebug(err + "(" + supportedClaim.ic::DisplayTag + ")");
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
        if (label != null) {
        	label.setAttribute("value", managedcardfromissuer );
        }
}

