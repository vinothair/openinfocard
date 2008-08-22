     function selfAssertedCallback() {

         var callbackdata = {};
         callbackdata["type"] = "selfAsserted";
         callbackdata["cardName"] = document.getElementById("cardname").value;
         callbackdata["givenname"] = document.getElementById("givenname").value;
         callbackdata["surname"] = document.getElementById("surname").value;
         callbackdata["email"] = document.getElementById("email").value;
         callbackdata["streetAddress"] = document.getElementById("streetAddress").value;
         callbackdata["locality"] = document.getElementById("locality").value;
         callbackdata["stateOrProvince"] = document.getElementById("stateOrProvince").value;
         callbackdata["postalCode"] = document.getElementById("postalCode").value;
         callbackdata["country"] = document.getElementById("country").value;
         callbackdata["primaryPhone"] = document.getElementById("primaryPhone").value;
         callbackdata["otherPhone"] = document.getElementById("otherPhone").value;
         callbackdata["mobilePhone"] = document.getElementById("mobilePhone").value;
         callbackdata["dateOfBirth"] = document.getElementById("dateOfBirth").value;
         callbackdata["gender"] = document.getElementById("gender").value;
         callbackdata["imgurl"] = document.getElementById("imgurl").value;

         window.arguments[1](callbackdata);

         document.getElementById('card-window').cancel();


     }

     
     function next() {
         var menu = document.getElementById('cardType');
         var selectedItem = menu.selectedItem;
         var value = selectedItem.getAttribute('value');

         var wizard = document.getElementById('card-window');
         wizard.goTo(value);
//         var typeObj = wizard.getPageById("type");
//         var typeObj = document.getElementById('type'); 
//         alert("boink: " + value);
//         typeObj.setAttribute('next', value);

         return false;
     }

     
      var theCard = "";
     var theFile;
     var theData;          
	
	function onProgress(e) {
		var loadingStatus = document.getElementById("loadingStatus");
		var percentComplete = 0;
		if (e.totalSize > 0) {
		  percentComplete = (e.position / e.totalSize)*100;
		  loadingStatus.value = "loaded: " + percentComplete +"%";
		} else {
			// blinking 
			if (loadingStatus.value.indexOf("loading") > 0) {
				loadingStatus.value = "";
			} else {
				loadingStatus.value = "loading";
			}
		}
	}

	function loadCardFromURL(url) {
		var req = new XMLHttpRequest();
		req.open('GET', url, true);
		req.onprogress = onProgress;
		req.onreadystatechange = function (aEvt) {
		  if (req.readyState == 4) {
			  var loadingStatus = document.getElementById("loadingStatus");
			  if(req.status == 200) {
			   loadingStatus.value = "loaded: 100%";
			   theData = req.responseText;
			  } else {
			   loadingStatus.value = "Error loading page: " + req.status;
			   debug(req.responseText);
			  }
		  }
		};
		req.send(null); 
	}
	
	function onPageShowManagedCard() {
		if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
		    var policy = window.arguments[0];
		    if ((policy != null) && (policy.hasOwnProperty("tokenType"))) {
				var tokenType = policy["tokenType"];
				if (tokenType == "urn:oasis:names:tc:IC:1.0:managedcard") {
					if (policy.hasOwnProperty("issuer")) {
						var textbox = document.getElementById("cardfile");
						textbox.value = policy["issuer"];
						textbox.setAttribute("readOnly", true);
						loadCardFromURL(textbox.value);
					}
				}
		    }
		}
	}
	
     function pickFile() {

      var textbox = document.getElementById("cardfile");
        var button = document.getElementById('cardbutton');
        textbox.thefile = null;

       netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
       var nsIFilePicker = Components.interfaces.nsIFilePicker;
       var fp = Components.classes["@mozilla.org/filepicker;1"].createInstance(nsIFilePicker);
       fp.init(window, "Select a File", nsIFilePicker.modeOpen);
       fp.appendFilters( nsIFilePicker.filterAll );
       var res = fp.show();
       if (res == nsIFilePicker.returnOK) {
         textbox.thefile = fp.file;
         theData = getFile(fp.file);
         theFile = fp.file;
         textbox.value = textbox.thefile.path;
       } else {
         textbox.thefile = null;
         textbox.value = "";
       }

      theCard = textbox.value;
     }


     function getFile(file) {


         if (file != null) {

             netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
             var is = Components.classes["@mozilla.org/network/file-input-stream;1"].createInstance( Components.interfaces.nsIFileInputStream );
             var sstream = Components.classes["@mozilla.org/scriptableinputstream;1"].createInstance(Components.interfaces.nsIScriptableInputStream);

             is.init( file, 0x01, 00004, null);
             sstream.init(is);
             var data = "";
             var count = 0;

             while (sstream.available() > 0) {
                   count = sstream.available();
                   if (count > 2048) {
                     count = 2048;
                   }
                   data += sstream.read(count);
             }

             var stop;
			 while ((stop = data.indexOf("?>")) > 0) {
			  debug("import managed card: removing processing instructions" + stop);
			  var newData = data.substring(stop+2);
			  data = newData;
			  debug("import managed card: " + data);
			 }
			 
			 // remove garbaged / BOM
			 if ((stop = data.indexOf('<')) > 0) {
				 var newData = data.substring(stop);
				 debug("removed garbage in front of first <");
				 data = newData;
			 }
			 
		     debug("Import managed card: " + data);
             return data;
         } else {
            return "";
         }

      }


      function parseCard(cardData) {
		     debug("parseCard: " + cardData);

         var cardxml;
         
         try {
        	 cardxml = new XML(cardData);
         } catch (e) {
        	 alert(e);
             window.arguments[1](null);

             document.getElementById('card-window').cancel();
        	 return;
         }
         var dsig = new Namespace("dsig", "http://www.w3.org/2000/09/xmldsig#");
         var ic = new Namespace("ic", "http://schemas.xmlsoap.org/ws/2005/05/identity");
         var wsid = new Namespace("wsid", "http://schemas.xmlsoap.org/ws/2006/02/addressingidentity");
         var wsa = new Namespace("wsa", "http://www.w3.org/2005/08/addressing");
         var mex = new Namespace("mex", "http://schemas.xmlsoap.org/ws/2004/09/mex");
         var wss = new Namespace("wss", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

//         default xml namespace = "http://www.w3.org/2000/09/xmldsig#";
      
//the following line does not work with the openidcards.sxip.com cards. It works down to the mexReference but failes to retrieve the address
//         var mexEP =    cardxml.dsig::Object.ic::InformationCard.ic::TokenServiceList.ic::TokenService.wsa::EndpointReference.wsa::Metadata.mex::Metadata.mex::MetadataSection.mex::MetadataReference.wsa::Address;

         var tokenservice =    cardxml.dsig::Object.ic::InformationCard.ic::TokenServiceList.ic::TokenService;
         //debug("tokenservice:" + tokenservice.toXMLString());
         var epr = tokenservice.wsa::EndpointReference;
         //debug("EndpointReference:" + epr.toXMLString());
         var mexOuter = epr.wsa::Metadata;
         //debug("outer Metadata:" + mexOuter.toXMLString());
         var mexInner = mexOuter.mex::Metadata;
         //debug("inner Metadata:" + mexInner.toXMLString());
         var mexSection = mexInner.mex::MetadataSection;
         //debug("MetadataSection:" + mexSection.toXMLString());
         var mexReference = mexSection.mex::MetadataReference;
         //debug("MetadataReference: " + mexReference.toXMLString());
         var mexEP = mexReference.wsa::Address; // this does not work with the sxip openidinfocards
         debug("mexAddress: " + mexEP.toXMLString());
         if (mexEP == undefined) {
            var address = "" + mexReference.toString() + "";
            var ia = address.indexOf("Address");
            if (ia > 0) {
            	var lt = String.fromCharCode(60); 
            	ia += "Address".length+1;
            	address = address.substring(ia);
            	var ib = address.indexOf(lt);
            	address = address.substring(0, ib);
            	debug("address:" + address);
            	mexEP = address;
            } else {
            	alert("Could not find Metadata Address in imported card");
	            window.arguments[1](null);
                document.getElementById('card-window').cancel();
            	return;
            }
         }

         var cardId = cardxml.dsig::Object.ic::InformationCard.ic::InformationCardReference.ic::CardId;
         var cardVersion = cardxml.dsig::Object.ic::InformationCard.ic::InformationCardReference.ic::CardVersion;
         var cardName = cardxml.dsig::Object.ic::InformationCard.ic::CardName;
         var cardImage = cardxml.dsig::Object.ic::InformationCard.ic::CardImage;
         var issuer = cardxml.dsig::Object.ic::InformationCard.ic::Issuer;
         
         var tokenServiceList = cardxml.dsig::Object.ic::InformationCard.ic::TokenServiceList.ic::TokenService;
//         alert("tokenServiceList=" + tokenServiceList);
         
         var stsCert;
         if (tokenServiceList.length > 1) {
        	 alert("multiple tokenservices are currently not supported. Using the first one only");
         }
         var tokenService = tokenServiceList[0];
//         alert("tokenService=" + tokenService);
         
         stsCert = tokenService.wsa::EndpointReference.wsid::Identity.dsig::KeyInfo.dsig::X509Data.dsig::X509Certificate;
// alert("wsa::EndpointReference" + cardxml.dsig::Object.ic::InformationCard.ic::TokenServiceList.ic::TokenService.wsa::EndpointReference);
         var identity = tokenService.wsa::EndpointReference.wsid::Identity;
//alert("wsid::Identity" + identity);
		 var keyInfo = identity.dsig::KeyInfo;
		 var identityStr = "" + identity;
		 if ((typeof(keyInfo) == 'xml') && (""+keyInfo == "") && ((keyInfoIndex = identityStr.indexOf("KeyInfo>")) != -1)) {
			 // I think this is a bug in Mozilla javascript XML implementation. 
			 // It does not handle default namespaces well...
			 var keyInfoStr = identityStr.substring(keyInfoIndex); 
			 var certIndex = keyInfoStr.indexOf("X509Certificate>") + "X509Certificate>".length; // start of value of X509Certificate element
			 if (certIndex == -1) {
				 alert("Could not find STS certificate");
				 return;
			 }
			 var certSubStr = keyInfoStr.substring(certIndex);
			 var certStopIndex = certSubStr.indexOf("<"); // < ist not in the Base64 alphabet
			 if (certStopIndex == -1) {
				 alert("Could not find STS certificate!");
				 return;
			 }
			 stsCert = certSubStr.substring(0,certStopIndex);
//			 alert("stsCert String="+stsCert);
			 debug("stsCert String="+stsCert);
		 } else {
			 debug("KeyInfo="+keyInfo);
		 }
		 
//alert("dsig::KeyInfo" + keyInfo + "type=" + typeof(keyInfo));
//alert("KeyInfo" + tokenService.wsa::EndpointReference.wsid::Identity.KeyInfo);
//		 alert("dsig::X509Data" + tokenService.wsa::EndpointReference.wsid::Identity.dsig::KeyInfo.dsig::X509Data);
//		 alert("dsig::X509Certificate" + tokenService.wsa::EndpointReference.wsid::Identity.dsig::KeyInfo.dsig::X509Data.dsig::X509Certificate);

		 var supportedTokenTypeList = cardxml.dsig::Object.ic::InformationCard.ic::SupportedTokenTypeList;

		 var cardUserCredential = cardxml.dsig::Object.ic::InformationCard.ic::TokenServiceList.ic::TokenService.ic::UserCredential.toXMLString();
//         var cardHint =    cardxml.dsig::Object.ic::InformationCard.ic::TokenServiceList.ic::TokenService.ic::UserCredential.ic::DisplayCredentialHint;
//         var cardUid =    cardxml.dsig::Object.ic::InformationCard.ic::TokenServiceList.ic::TokenService.ic::UserCredential.ic::UsernamePasswordCredential.ic::Username;
//         var cardKeyIdentifier = cardxml.dsig::Object.ic::InformationCard.ic::TokenServiceList.ic::TokenService.ic::UserCredential.ic::X509V3Credential.dsig::X509Data.wss::KeyIdentifier;
//         var cardPrivatePersonalIdentifier = cardxml.dsig::Object.ic::InformationCard.ic::TokenServiceList.ic::TokenService.ic::UserCredential.ic::SelfIssuedCredential.dsig::PrivatePersonalIdentifier;
		 var requireAppliesTo = cardxml.dsig::Object.ic::InformationCard.ic::RequireAppliesTo;
		 
          debug(cardId);
          debug(cardName);
          debug(issuer);
          debug(mexEP);
//          debug(cardHint);
//          debug(cardUid);
//		  debug(cardKeyIdentifier);
//		  debug(cardPrivatePersonalIdentifier);
		  debug(cardUserCredential);
		  debug("stsCert: " + stsCert);
		  debug("requireAppliesTo:" + requireAppliesTo);
		  debug("SupportedTokenTypeList:"+ supportedTokenTypeList);
		  
          var callbackdata = {};
          
          callbackdata["crdFileContent"] = cardData;
          
          callbackdata["type"] = "managedCard";
          callbackdata["cardId"] = cardId;
          callbackdata["cardVersion"] = cardVersion;
          callbackdata["cardName"] = cardName;
          callbackdata["cardImage"] = cardImage;
          callbackdata["issuer"] = issuer;
          callbackdata["mex"] = mexEP;
//          callbackdata["hint"] = cardHint;
//          callbackdata["uid"] = cardUid;
//          callbackdata["KeyIdentifier"] = cardKeyIdentifier;
//          callbackdata["PrivatePersonalIdentifier"] = cardPrivatePersonalIdentifier;
		  callbackdata["usercredential"] = cardUserCredential;
          callbackdata["stsCert"] = stsCert;
          callbackdata["supportedClaims"] = cardxml.dsig::Object.ic::InformationCard.ic::SupportedClaimTypeList.toXMLString();
          callbackdata["supportedTokenTypeList"] = supportedTokenTypeList.toXMLString();
          if (requireAppliesTo == null) {
              callbackdata["requireAppliesTo"] = requireAppliesTo;
          }
          
			if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
			    var policy = window.arguments[0];
			    if (policy != null && policy.hasOwnProperty("tokenType")) {
					var tokenType = policy["tokenType"];
					if (tokenType == "urn:oasis:names:tc:IC:1.0:managedcard") {
						digestNewCard(callbackdata);
					}
			    }
			}
		
			{ 
				var func = window.arguments[1];
				if (typeof(func) == 'function') {
					window.arguments[1](callbackdata);
				}
			}
			
          document.getElementById('card-window').cancel();


      }

      function openidCallback() {

          var callbackdata = {};
          callbackdata["type"] = "openid";
          callbackdata["openid_url"] = document.getElementById("openid_url").value;
          callbackdata["cardName"] = document.getElementById("openid_url").value;
          callbackdata["cardId"] = document.getElementById("openid_url").value;
          window.arguments[1](callbackdata);

          document.getElementById('card-window').cancel();

      }

      function loadCardWizard(){
    		if ((!(window.arguments == undefined)) && (window.arguments.length > null)) {
    		    var policy = window.arguments[0];
    		    if ((policy != null) && (policy.hasOwnProperty("tokenType"))) {
    				var tokenType = policy["tokenType"];
    				if (tokenType == "urn:oasis:names:tc:IC:1.0:managedcard") {
    					document.getElementById('card-window').goTo('managedCard');
    				}
    		    }
    		}
    		
    	}



