
function isClaimChecked(elementId, uri) {
	 var checkbox = document.getElementById(elementId);
	 if (!(checkbox === undefined)) {
icDebug("isClaimChecked: found " + elementId);
		 if (!(checkbox.checked === undefined)) {
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

function setOptionalClaimsSelf(policy) {
	    var optionalClaims = "";
    if (!(policy.optionalClaims === undefined)) {
     optionalClaims = policy.optionalClaims;
     if (optionalClaims !== null) {
      icDebug("setOptionalClaimsSelf optionalClaims: " + optionalClaims);
      var checkedClaims = null;
      var claims = optionalClaims.split(/\s+/);
      icDebug("setOptionalClaimsSelf claims: " + claims);
      var i;
      for (i in claims) {
       var claim = claims[i];
       icDebug("setOptionalClaimsSelf claim: " + claim);
       if (claim.indexOf("givenname") != -1) {
        if (isClaimChecked("_givenname") !== null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname";
         if (checkedClaims === null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("emailaddress") != -1) {
        if (isClaimChecked("_email") !== null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress";
         if (checkedClaims === null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("surname") != -1) {
        if (isClaimChecked("_surname") !== null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname";
         if (checkedClaims === null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("streetaddress") != -1) {
        if (isClaimChecked("_streetAddress") !== null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/streetaddress";
         if (checkedClaims === null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("stateorprovince") != -1) {
        if (isClaimChecked("_stateOrProvince") !== null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/stateorprovince";
         if (checkedClaims === null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("postalcode") != -1) {
        if (isClaimChecked("_postalCode") !== null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/postalcode";
         if (checkedClaims === null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("country") != -1) {
        if (isClaimChecked("_country") !== null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/country";
         if (checkedClaims === null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("homephone") != -1) {
        if (isClaimChecked("_primaryPhone") !== null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/homephone";
         if (checkedClaims === null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("otherphone") != -1) {
        if (isClaimChecked("_otherPhone") !== null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/otherphone";
         if (checkedClaims === null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("mobilephone") != -1) {
        if (isClaimChecked("_mobilePhone") !== null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/mobilephone";
         if (checkedClaims === null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("dateofbirth") != -1) {
        if (isClaimChecked("_dateOfBirth") !== null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/dateofbirth";
         if (checkedClaims === null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("gender") != -1) {
        if (isClaimChecked("_gender") !== null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/gender";
         if (checkedClaims === null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("locality") != -1) {
        if (isClaimChecked("_locality") !== null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality";
         if (checkedClaims === null) {
          checkedClaims = uri;
         } else {
          checkedClaims = checkedClaims + " " + uri;
         }
        }
        continue;
       } else if(claim.indexOf("privatepersonalidentifier") != -1) {
        if (isClaimChecked("privatepersonalidentifier") !== null) {
         var uri = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier";
         if (checkedClaims === null) {
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
	  policy.optionalClaims = checkedClaims;
     }
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
	 var requiredClaims = policy.requiredClaims;
	 if (requiredClaims === undefined) { return; }
	 if (requiredClaims === null) { return; }
	
	 var optionalClaims = policy.optionalClaims;
	 
	 //requiredClaims = requiredClaims.toLowerCase();
	 //if (optionalClaims !== null) {
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

function setCardSelf(selectedCard, policy) {
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

   	 	if (policy !== null) {
   	 		indicateRequiredClaims(policy);
   	 	}
   	 	
        var grid = document.getElementById("editgrid");
        grid.setAttribute("hidden", "false");


        var grid1 = document.getElementById("editgrid1");
        grid1.setAttribute("hidden", "false");

		var stringsBundle = document.getElementById("string-bundle");
		var selfassertedcard = stringsBundle.getString('selfassertedcard');
        var label = document.getElementById("notify");
        if (label !== null) {
        	label.setAttribute("value", selfassertedcard);
        }
}