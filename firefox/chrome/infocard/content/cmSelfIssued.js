
function setOptionalClaimsSelf(policy) {
  var optionalClaims = null;
  if (policy.optionalClaims === undefined) {
    icDebug("setOptionalClaimsSelf: policy.optionalClaims === undefined");
    return;
  } 
  optionalClaims = policy.optionalClaims;

  if (optionalClaims === null) {
    icDebug("setOptionalClaimsSelf: optionalClaims === null");
    return;
  }

  icDebug("setOptionalClaimsSelf optionalClaims: " + optionalClaims);
  var checkedClaims = null;

  var claims = optionalClaims.split(/\s+/);
  icDebug("setOptionalClaimsSelf claims: " + claims);
  for (var i=0; i<claims.length; i++) {
     var claim = claims[i];
     icDebug("setOptionalClaimsSelf claim: " + claim);
     
     var checkedClaim = isClaimChecked("label_"+claim, claim);
     if (checkedClaim !== null) {
       checkedClaims = checkedClaims + " " + checkedClaim;
     }
  }
  
  icDebug("setOptionalClaimsSelf checkedClaims: " + checkedClaims);
  policy.optionalClaims = checkedClaims;

}

function indicateRequiredClaim(requiredClaims, optionalClaims, claim){
 var name = "_" + claim;
 var element = document.getElementById(name);
 if (element === undefined) {
  icDebug( "Element " + name + " not found" );
  return;
 }
 if (requiredClaims.indexOf(claim.toLowerCase()) != -1) {
    //debug("required claim " + claim + " found in " + requiredClaims);
    element.checked = true;
    element.disabled = true;
    return;
 } 

 if (optionalClaims !== null) {
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

function findSelfIssuedCardByPPID(ppid, serializedPolicy) {
  var cardFile;
  try {
    var cardstoreManagerSvc = Components.classes["@openinfocard.org/CardstoreManager/service;1"].
                                   getService( Components.interfaces.nsIHelloWorld);

    cardFile = CardstoreToolkit.readCardStore();
    icDebug("findSelfIssuedCardByPPID cardFile = " + cardFile);
  } catch (eee) {
    icDebug("findSelfIssuedCardByPPID = exception" + eee);
    throw eee;
  }
  
  var cardXmllist = cardFile.infocard;
  for (var i=0; i<cardXmllist.length(); i++) {
    var c = cardXmllist[i];
    var type = c.type;
    icDebug("findSelfIssuedCardByPPID c = " + c);
    if (c.type.toString() !== "selfAsserted") {
      continue;
    }
    var cardPpid = TokenIssuer.generateRPPPID(serializedPolicy);
    icDebug("findSelfIssuedCardByPPID ppid=" + ppid + " c.id=" + c.id);
    if (cardPpid === ppid) {
      icDebug("findSelfIssuedCardByPPID = matching card found for ppid" + ppid);
      return c;
    }
  }
  icDebug("findSelfIssuedCardByPPID = no matching card found for ppid" + ppid);
  return null;

}

