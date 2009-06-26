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

        mexResponse = req.responseText; // bug 270553
        mexResponse = mexResponse.replace(/^<\?xml\s+version\s*=\s*(["'])[^\1]+\1[^?]*\?>/, ""); // bug 336551
        
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
        mexResponse = req.responseText; // bug 270553
        mexResponse = mexResponse.replace(/^<\?xml\s+version\s*=\s*(["'])[^\1]+\1[^?]*\?>/, ""); // bug 336551
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

function sendRST(sendRstParameter) {

	var tsEndpointAddressStr = sendRstParameter.tsEndpointAddressStr;
	var usercredential = sendRstParameter.icUserCredential;
	var managedCard = sendRstParameter.managedCard;
	var requiredClaims = sendRstParameter.requiredClaims;
	var optionalClaims = sendRstParameter.optionalClaims;
	var tokenType = sendRstParameter.tokenType;
	var clientPseudonym = sendRstParameter.clientPseudonym;
	var relyingPartyURL = sendRstParameter.relyingPartyURL;
	var relyingPartyCertB64 = sendRstParameter.relyingPartyCertB64;
	var issuerPolicy = sendRstParameter.issuerPolicy;

	//		if (issuerPolicy != null) {
	//			var to = xmlreplace(managedCard.carddata.managed.issuer);
	//			var mexAddress = issuerPolicy;
	//			var issuerMex = getMex1(to, mexAddress);
	//			icDebug("issuerMex=" + issuerMex);
	//		}
			
	//	    var mexResponse = getMexForCard(managedCard);

    icDebug("sendRST::usercredential:" + usercredential);
    if (usercredential === undefined) {
    	Components.utils.reportError("sendRST::usercredential === undefined");
    	return null;
    }
    if (usercredential === null) {
    	Components.utils.reportError("sendRST::usercredential === null");
    	return null;
    }
    if (tsEndpointAddressStr === undefined) {
    	Components.utils.reportError("sendRST::tsEndpointAddressStr === undefined");
    	return null;
    }
    if (tsEndpointAddressStr === null) {
    	Components.utils.reportError("sendRST::tsEndpointAddressStr === null");
    	return null;
    }
    icDebug("sendRST::tsEndpointAddressStr:" + tsEndpointAddressStr);
    
    var rst = "<s:Envelope " + 
		"xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" " + 
		"xmlns:a=\"http://www.w3.org/2005/08/addressing\" " +
		"xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">" + 
		"<s:Header>" + 
      	 "<a:Action u:Id=\"_1\">http://schemas.xmlsoap.org/ws/2005/02/trust/RST/Issue</a:Action>" +
      	 "<a:To s:mustUnderstand=\"1\">" + xmlreplace(tsEndpointAddressStr) + "</a:To>" +
		 "<o:Security s:mustUnderstand=\"1\" xmlns:o=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"; 

    var ic = new Namespace("ic", "http://schemas.xmlsoap.org/ws/2005/05/identity");
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
        okorcancel = prompts.promptUsernameAndPassword('Card Authentication', hint, tsEndpointAddressStr, prompts.SAVE_PASSWORD_PERMANENTLY, username, password);
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
		Components.utils.reportError("undefined user credential type: " + usercredential);
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
    
    if (requiredClaims.indexOf("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier") >= 0) {
        rst = rst + "<ClientPseudonym xmlns=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><PPID>" + xmlreplace(clientPseudonym) + "</PPID></ClientPseudonym>";
	}
    if (tokenType != null) {
        rst = rst + "<wst:TokenType>" + xmlreplace(tokenType) + "</wst:TokenType>";
    }
    rst = rst + "<wsid:RequestDisplayToken xml:lang=\"en\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"/>" +
    "</wst:RequestSecurityToken></s:Body></s:Envelope>";

    icDebug("sendRST: request: " + rst);
    var rstr;
    var rstReq = new XMLHttpRequest();
    rstReq.open('POST', tsEndpointAddressStr, false);
    rstReq.setRequestHeader("Content-type", "application/soap+xml; charset=utf-8");
    rstReq.setRequestHeader("Cache-Control", "no-cache");
    rstReq.setRequestHeader("accept-language", "en-us");
    rstReq.setRequestHeader("User-Agent", "xmldap infocard stack");
    try {
        rstReq.send(rst);
    } 
    catch (e) {
		icDebug("rstReq.send(rst) failed: " + e);
		alert("posting the request to get the security tokens failed. " + e);
			return null;            	
    }
    if(rstReq.status == 200) {
//		  // should you replace the string voodoo below through something 
//    	  // more elaborate like E4X-handling then think of these two bugs
//        rstResponse = req.responseText; // bug 270553
//        rstResponse = mexResponse.replace(/^<\?xml\s+version\s*=\s*(["'])[^\1]+\1[^?]*\?>/, ""); // bug 336551

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
        icDebug("token request (" + tsEndpointAddressStr + ") failed. (" + rstReq.status +")\n" + rstReq.responseText);
        alert("token request (" + tsEndpointAddressStr + ") failed. (" + rstReq.status +")\n" + rstReq.responseText);
    }
    return tokenToReturn;
}

function processManagedCard(
		managedCard, requiredClaims, optionalClaims, tokenType, clientPseudonym, 
		relyingPartyURL, relyingPartyCertB64, issuerPolicy) {

    var tokenToReturn = null;

    var ic = new Namespace("ic", "http://schemas.xmlsoap.org/ws/2005/05/identity");
    var wsa = new Namespace("wsa", "http://www.w3.org/2005/08/addressing");
    
    var tokenServiceList = managedCard.carddata.managed.ic::TokenServiceList;
    icDebug("processManagedCard::tokenServiceList>>>" + tokenServiceList);
    if (tokenServiceList === null || tokenServiceList.length() == 0) {
    	alert("This is probably a managed card that is stored in an old and now unsupported internal format.\n" +
    			"Please delete the card. Sorry for the inconvenience.");
    	return null;
    }
    var usercredential = null;
    var mexResponse = null;
    
    var mexes = {}; // mex response cache
    
    var tokenServices = tokenServiceList.ic::TokenService;
	for each (var ts in tokenServices) {
		icDebug("processManagedCard::tokenService>>>" + ts);
		
		var icUserCredential = ts.ic::UserCredential;
		icDebug("processManagedCard::icUserCredential>>>" + icUserCredential);
		var icUserCredentialChild;
		if (icUserCredential.*.length() == 1) {
			icUserCredentialChild = icUserCredential.child(0);
		} else {
			icUserCredentialChild = icUserCredential.child(1);
		}
		icDebug("processManagedCard::isUserCredentialChild>>>" + icUserCredentialChild);
		var localName = icUserCredentialChild.name().localName;
		icDebug("processManagedCard::isUserCredentialChild.name().localname>>>" + localName);
		if (localName === "UsernamePasswordCredential") { // currently only UsernamePasswordCredential is supported
			var tsEndpointAddress = ts.wsa::EndpointReference.wsa::Address; 
			icDebug("processManagedCard::tsEndpointAddress>>>" + tsEndpointAddress);
			
			var tsWsaMetadata = ts.wsa::EndpointReference.wsa::Metadata;
			icDebug("processManagedCard::tsMetadata>>>" + tsWsaMetadata);
			var wsx = new Namespace("wsx", "http://schemas.xmlsoap.org/ws/2004/09/mex");
			var tsWsxMetadata = tsWsaMetadata.wsx::Metadata;
			icDebug("processManagedCard::tsWsxMetadata>>>" + tsWsxMetadata);
			var tsMetadataSection = tsWsxMetadata.wsx::MetadataSection; 
			icDebug("processManagedCard::tsMetadataSection>>>" + tsMetadataSection);
			var tsMetadataReference = tsMetadataSection.wsx::MetadataReference;
			icDebug("processManagedCard::tsMetadataReference>>>" + tsMetadataReference);
			var tsMexAddress = tsMetadataReference.wsa::Address;
			if ((tsMexAddress === null) || (tsMexAddress.length() == 0)) {
				icDebug("processManagedCard::wsa:tsMexAddress not found in >>>" + tsMetadataReference.toXMLString());
				return null;
			}
			icDebug("processManagedCard::tsMexAddress>>>" + tsMexAddress);
			var tsMexAddressStr = tsMexAddress.toString();
			if (mexes[tsMexAddressStr] !== undefined) {
				mexResponse = mexes[tsMexAddressStr];
			} else {
				mexResponse = getMex1(xmlreplace(tsMexAddress), tsMexAddress);
			}
			if (mexResponse !== null) {
				icDebug("processManagedCard::mexResponse>>>" + mexResponse);
				mexes[tsMexAddressStr] = mexResponse;
				
				var mexXml = new XML(mexResponse);
				var wsdl = new Namespace("http://schemas.xmlsoap.org/wsdl/");
				var wsa10 = new Namespace("http://www.w3.org/2005/08/addressing");
				var wsp = new Namespace("http://schemas.xmlsoap.org/ws/2004/09/policy");
				var wsu = new Namespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
				
				var addresses = mexXml..wsdl::port.wsa10::EndpointReference.wsa10::Address;
				var tsEndpointAddressStr = tsEndpointAddress.toString();

				for each (var anAddress in addresses) {
					var theAddress = anAddress.child(0);
//					icDebug("processManagedCard::typeof("+theAddress+")>>>" + typeof(theAddress));
//					icDebug("processManagedCard::typeof("+tsEndpointAddress+")>>>" + typeof(tsEndpointAddress));
					if (theAddress.toString() === tsEndpointAddressStr) {
						icDebug("processManagedCard::address>>>" + theAddress);
						var addressParent = anAddress.parent(); // EndpointReference
						icDebug("processManagedCard::parent >>>" + addressParent);
						var endpointReferenceParent = addressParent.parent(); // wsdl::port
						icDebug("processManagedCard::port   >>>" + endpointReferenceParent);
						var binding = endpointReferenceParent.@binding;
						icDebug("processManagedCard::port.@binding>>>" + binding);
						var colonIndex = binding.indexOf(":");
						binding = binding.substring(colonIndex+1); // works even if colonIndex == -1
						var bindingStr = binding.toString();
						
						var bindings = mexXml..wsdl::binding;
						for each (var aBinding in bindings) {
							var bindingNameAttrValueStr = aBinding.@name.toString();
							if (bindingNameAttrValueStr === bindingStr) {
								icDebug("processManagedCard::bindingNameAttrValueStr>>>" + bindingNameAttrValueStr);
								var wspPolicyReference = aBinding.wsp::PolicyReference;
								if (wspPolicyReference !== null) {
									icDebug("processManagedCard::wspPolicyReference>>>" + wspPolicyReference.toXMLString());
									var wspPolicyReferenceURIStr = wspPolicyReference.@URI.toString();
									icDebug("processManagedCard::wspPolicyReferenceURI>>>" + wspPolicyReferenceURIStr);
									var hashmarkIndex = wspPolicyReferenceURIStr.indexOf("#");
									if (hashmarkIndex == 0) {
										var wspPolicyReferenceURIStr = wspPolicyReferenceURIStr.substring(1);
										icDebug("processManagedCard::wspPolicyReferenceURIStr>>>" + wspPolicyReferenceURIStr);
										var wsdlPolicies = mexXml..wsdl::definitions.wsp::Policy;
										icDebug("processManagedCard::wsdlPolicies.length()>>>" + wsdlPolicies.length());
										for each (var aPolicy in wsdlPolicies) {
											//icDebug("processManagedCard::aPolicy>>>" + aPolicy.toXMLString());
											var wsuId = aPolicy.@wsu::Id;
											var wsuIdStr = wsuId.toString();
											if (wsuIdStr === wspPolicyReferenceURIStr) {
												icDebug("processManagedCard::wsuIdStr>>>" + wsuIdStr);
												// try both security policies
												var sp2005 = new Namespace("http://schemas.xmlsoap.org/ws/2005/07/securitypolicy");
												var sp2007 = new Namespace("http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702");
												var spTransportBinding = aPolicy..sp2005::TransportBinding;
												if (spTransportBinding === null || spTransportBinding.length() == 0) {
													icDebug("processManagedCard::spTransportBinding (2005) null in sPolicy>>>" + aPolicy.toString());
//													var spTransportBinding = aPolicy..sp2007::TransportBinding;
//													if (spTransportBinding === null || spTransportBinding.length() == 0) {
//														icDebug("processManagedCard::spTransportBinding (2007) null too>>>");
//														continue;
//													}
													continue;
												}
												icDebug("processManagedCard::before sendRST: tsEndpointAddressStr=" + tsEndpointAddressStr);
											    icDebug("processManagedCard::typeof(tsEndpointAddressStr):" + typeof(tsEndpointAddressStr));
												icDebug("processManagedCard::before sendRST: icUserCredential=" + icUserCredential);
											    icDebug("processManagedCard::typeof(icUserCredential):" + typeof(icUserCredential));
												try {
													var sendRstParameter = {};
													sendRstParameter.tsEndpointAddressStr = tsEndpointAddressStr;
													sendRstParameter.icUserCredential = icUserCredential;
													sendRstParameter.managedCard = managedCard;
													sendRstParameter.requiredClaims = requiredClaims;
													sendRstParameter.optionalClaims = optionalClaims;
													sendRstParameter.tokenType = tokenType;
													sendRstParameter.clientPseudonym = clientPseudonym;
													sendRstParameter.relyingPartyURL = relyingPartyURL;
													sendRstParameter.relyingPartyCertB64 = relyingPartyCertB64;
													sendRstParameter.issuerPolicy = issuerPolicy;
													var aToken = sendRST(sendRstParameter);
													if ((aToken !== undefined) && (aToken !== null)) {
														return aToken;
													} else {
														icDebug("processManagedCard: sendRST returned null or undefined for: " + tsEndpointAddressStr);
													}
												} catch (sendRstException) {
													icDebug("processManagedCard: sendRST threw: " + sendRstException);
												}
											} else {
												//icDebug("processManagedCard::WSUID   >>>" + wsuIdStr);
											}
										}
									} else {
										icDebug("processManagedCard::hasmarkIndex>>>" + hashmarkIndex);
									}
								} else {
									Components.utils.reportError("processManagedCard::wspPolicyReference===null! aBinding=" + aBinding);
									return null;
								}
							}
						}
					} else {
						// icDebug("processManagedCard::ADDRESS>>>" + theAddress);		
					}
				}
			} else {
				icDebug("processManagedCard::mexResponse is null for " + tsMexAddress);
			}
		} else {
			icDebug("processManagedCard::authenticationmethod not supported>>>" + localname);
		}
	}
    return null;
}

function createCheckbox(optionalClaims, requiredClaims, displayTag, uri) {
	 var checkbox = document.createElement("checkbox");
	 var label;
//  		 if (displayTag.length > 10) {
//  		  label = displayTag.substring(0,9);
//  		 } else {
//  		  label = displayTag;
//  		 }
	 label = displayTag;
	 icDebug("setCardManaged: label=" + label);
	 label = xmlreplace(label);
	 checkbox.setAttribute("label", label);
	 checkbox.setAttribute("id", "label_"+uri);
	 checkbox.setAttribute("class", "claimLabel");
	 checkbox.setAttribute("crop", "end");
     checkbox.setAttribute("checked", "false");
	 checkbox.setAttribute("disabled", "true");
	 
	 if (optionalClaims != null) {
  		  var ui = optionalClaims.indexOf(uri);
  		  if (ui != -1) {
  		   checkbox.setAttribute("checked", "false");
  		   checkbox.setAttribute("disabled", "false");
  		  }
	 }
	 if (requiredClaims != null) {
  		  ui = requiredClaims.indexOf(uri);
  		  if (ui != -1) {
  		   checkbox.setAttribute("checked", "true");
  		   checkbox.setAttribute("disabled", "true");
  		  }
	 }
	 try {
	 	  // DisplayTag should be changed to Description when description is supported
		 checkbox.setAttribute("tooltiptext", displayTag); // this is not cropped
	 }
	 catch (err) {
	  // tooltiptext barfs on "invalid character" while value does not... Axel
	  icDebug(err + "(" + displayTag + ")");
	 }
	 return checkbox;
}

function getVariableClaimValue() {
   var value = "";
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
   return value;
}

function setCardManaged(requiredClaims, optionalClaims, list, row1Id, row2Id, claimValues) {
	try {
		var ic = new Namespace("ic", "http://schemas.xmlsoap.org/ws/2005/05/identity");

		icDebug("setCardManaged requiredClaims: " + requiredClaims);
		icDebug("setCardManaged optionalClaims: " + optionalClaims);
		
			var managedRows = document.getElementById(row1Id);
			
			// remove child rows before appending new ones
			while (managedRows.hasChildNodes()) { 
	  		 managedRows.removeChild(managedRows.childNodes[0]);
			}
			
			icDebug("setCardManaged: number of supported claims: " + list.length());
			
			for (var index=0; index<list.length(); index++) {
			 var supportedClaim = list[index];
			 var uri = supportedClaim.@Uri.toXMLString();
			 icDebug("setCardManaged: uri=" + uri);
			 
			 if (uri == "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier") {
				 continue;
			 }
			 
	  		 var row = document.createElement("row");
	  		 row.setAttribute("class", "rowClass");

	  		 var displayTag = "" + supportedClaim.ic::DisplayTag;
	  		 var checkbox = createCheckbox(optionalClaims, requiredClaims, displayTag, uri);
	  		 var value = "";
	  		 if (optionalClaims != null) {
	  		  var ui = optionalClaims.indexOf(uri);
	  		  if (ui != -1) {
	  			  icDebug("optional claim:" + uri);
	  			  var thisClaim = optionalClaims.substr(ui);
	  			  value = getVariableClaimValue(thisClaim);
	  		  }
	  		 }
	  		 if (requiredClaims != null) {
	  		  var ui = requiredClaims.indexOf(uri);
	  		  if (ui != -1) {
	  			  icDebug("requiredClaim claim:" + uri);
	  	 		  var thisClaim = requiredClaims.substr(ui);
	  	 		  value = getVariableClaimValue(thisClaim);
	  		  }
	  		 }
	  		 
	  		 if ((claimValues !== undefined) && (claimValues !== null)) {
				icDebug("setCardManaged: number of claimValues: " + claimValues.length());
				for (var ci=0; ci<claimValues.length(); ci++) {
					var claimValue = claimValues[ci];
					icDebug("setCardManaged: claimValue=" + claimValue.toXMLString());
					var claimUri = claimValue.@Uri.toXMLString();
					if (claimUri === uri) {
						value = claimValue.ic::Value.text();
					} else {
						icDebug("setCardManaged: claimUri=" + claimUri + " uri=" + uri);
					}
				}
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
			
			var stringsBundle = document.getElementById("string-bundle");
	        var label = document.getElementById("notify");
	        if (label != null) {
				if (selectedCard.type == "managedCard" ) {
					var managedcardfromissuer = stringsBundle.getFormattedString('managedcardfromissuer', [selectedCard.carddata.managed.issuer]);
		        	label.setAttribute("value", managedcardfromissuer );
				} else {
					if (selectedCard.type == "selfAsserted" )  {
						var selfassertedcard = stringsBundle.getString('selfassertedcard');
			        	label.setAttribute("value", selfassertedcard);
					}
				}
	        }
	} catch (e) {
		Components.utils.reportError("setCardManaged: threw: " + e);
	}
}

