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

var db = "cardDb.xml";
var selectedCard;

function ok(){

    var tokenToReturn;
    var policy = window.arguments[0];


    if (selectedCard.type == "selfAsserted") {
        policy["type"] = "selfAsserted";
        policy["card"] = selectedCard.toString();
        //TRUE or FALSE on the second param enabled debug
        tokenToReturn = processCard(policy,false);
        finish(tokenToReturn);

    } else if (selectedCard.type == "managedCard"){

        var assertion = processManagedCard(selectedCard);
        debug(assertion);

        policy["type"] = "managedCard";
        policy["assertion"] = assertion;
        //TRUE or FALSE on the second param enabled debug
        tokenToReturn = processCard(policy,false);
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



function processManagedCard(managedCard) {

    var tokenToReturn = null;
    var mexResponse;

    var messageIdInt = Math.floor(Math.random()*100000+1);
    var messageId = "urn:uuid:" + messageIdInt;
    var mex = "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:a=\"http://www.w3.org/2005/08/addressing\"><s:Header><a:Action s:mustUnderstand=\"1\">http://schemas.xmlsoap.org/ws/2004/09/transfer/Get</a:Action><a:MessageID>" +   messageId  +  "</a:MessageID><a:ReplyTo><a:Address>http://www.w3.org/2005/08/addressing/anonymous</a:Address></a:ReplyTo><a:To s:mustUnderstand=\"1\">" + managedCard.carddata.managed.issuer + "</a:To></s:Header><s:Body/></s:Envelope>";


    var req = new XMLHttpRequest();
    req.open('POST', managedCard.carddata.managed.mex, false);
    req.setRequestHeader("Content-type", "application/soap+xml; charset=utf-8");
    req.setRequestHeader("Cache-Control", "no-cache");
    req.setRequestHeader("accept-language", "en-us");
    req.setRequestHeader("User-Agent", "xmldap infocard stack");
    req.send(mex);
    if(req.status == 200) {

        mexResponse = req.responseText;

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

            var prompts = Components.classes["@mozilla.org/embedcomp/prompt-service;1"].getService(Components.interfaces.nsIPromptService);
            username = {value:managedCard.carddata.managed.username};
            password = {value:""};
            var check = {value: false};
            okorcancel = prompts.promptUsernameAndPassword(window, 'Card Authentication', managedCard.carddata.managed.hint, username, password, null, check);
            var uid =  username.value;
            var pw =  password.value;


            var rst = "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\"><s:Header><o:Security s:mustUnderstand=\"1\" xmlns:o=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\"><o:UsernameToken u:Id=\"";

            var messageIdInt1 = Math.floor(Math.random()*100000+1);
            var messageId1 = "urn:uuid:" + messageIdInt;

            rst = rst + messageId1 + "\"><o:Username>";

            rst = rst + uid;

            rst = rst + "</o:Username><o:Password o:Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">";

            rst = rst + pw;

            rst = rst + "</o:Password></o:UsernameToken></o:Security></s:Header><s:Body><wst:RequestSecurityToken Context=\"ProcessRequestSecurityToken\" xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\"><wsid:InformationCardReference xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><wsid:CardId>";

            rst = rst + managedCard.id;

            rst = rst + "</wsid:CardId><wsid:CardVersion>1</wsid:CardVersion></wsid:InformationCardReference><wst:Claims><wsid:ClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/privatepersonalidentifier\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"/><wsid:ClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"/><wsid:ClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"/><wsid:ClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"/></wst:Claims><wst:KeyType>http://schemas.xmlsoap.org/ws/2005/05/identity/NoProofKey</wst:KeyType><ClientPseudonym xmlns=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><PPID>TBD_WHAT_TO_DO</PPID></ClientPseudonym><wst:TokenType>urn:oasis:names:tc:SAML:1.0:assertion</wst:TokenType><wsid:RequestDisplayToken xml:lang=\"en\" xmlns:wsid=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"/></wst:RequestSecurityToken></s:Body></s:Envelope>";
            var rstr;
            var rstReq = new XMLHttpRequest();
            rstReq.open('POST', address, false);
            rstReq.setRequestHeader("Content-type", "application/soap+xml; charset=utf-8");
            rstReq.setRequestHeader("Cache-Control", "no-cache");
            rstReq.setRequestHeader("accept-language", "en-us");
            rstReq.setRequestHeader("User-Agent", "xmldap infocard stack");
            rstReq.send(rst);
            if(rstReq.status == 200) {

                rstr = rstReq.responseText;

                var rstIndex = rstr.indexOf("RequestedSecurityToken>");
                rstIndex += 23;
                var assertionStart = rstr.substring(rstIndex);

                var assertionEndIndex = assertionStart.indexOf("Assertion>");
                assertionEndIndex += 10;
                var assertion = assertionStart.substring(0,assertionEndIndex);

                tokenToReturn = assertion;

            }

        }

    }

    return tokenToReturn;

}



function cancel(){

    stopServer();
    window.arguments[1](null);
    window.close();
}

function getCard(cardid){

    var cardFile = read(db);
    var card = cardFile.infocard.(id == cardid);
    return card;


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

    var cardFile = read(db);
    var cardArea = document.getElementById("cardselection");
    var latestCard;
    var selectMe;
    var count = 0;
    for each (c in cardFile.infocard) {

        latestCard = createItem(c);
        selectMe = c;
        cardArea.appendChild(latestCard);
        count++;

    }

    if ( count != 0) {
        var policy = window.arguments[0];
        var label = document.getElementById("notify");
		var site = policy["cn"];
        var please = stringsBundle.getFormattedString('pleaseselectacard', [site]);
        label.setAttribute("value", please);
    } else {
        var label = document.getElementById("notify");
		var button = stringsBundle.getString('newcard');
        var youdont = stringsBundle.getFormattedString('youdonthaveanycards', [button]);
        label.setAttribute("value", youdont);
    }
	{
		var policy = window.arguments[0];
		var cert = policy["cert"];
		//var issuerLogoURL = TokenIssuer.getIssuerLogoURL(cert);
		//if (issuerLogoURL != undefined) {
		//	var issuerlogo = document.getElementById("issuerlogo");
		//	issuerlogo.src = issuerLogoURL;
		//	issuerlogo.hidden = false;
		//	var issuerlogo_label = document.getElementById("issuerlogo_label");
        //                issuerlogo_label.hidden = false;
		//}
	}
}

function indicateRequiredClaim(requiredClaims, claim){
 var name = "_" + claim;
 var element = document.getElementById(name);
 if (element == undefined) {
  debug( "Element " + name + " not found" );
  return;
 }
 if (requiredClaims.indexOf(claim.toLowerCase()) != -1) {
    debug("Claim " + claim + " found in " + requiredClaims);
    if (element.value.charAt(0) != '*') {
     element.value = "*" + element.value;
    }
 } else {
    debug("Claim " + claim + " not found in " + requiredClaims);
    if (element.value.charAt(0) == '*') {
     element.value = element.value.substr(1,element.value.length-1);
    }
 }
}

function indicateRequiredClaims(){
 var policy = window.arguments[0];
 var requiredClaims = policy["requiredClaims"];
 if (requiredClaims == undefined) return;

 requiredClaims = requiredClaims.toLowerCase();
debug("requiredClaims: " + requiredClaims);
 indicateRequiredClaim(requiredClaims, "givenname");
 indicateRequiredClaim(requiredClaims, "surname");
 indicateRequiredClaim(requiredClaims, "email");
 indicateRequiredClaim(requiredClaims, "streetAddress");
 indicateRequiredClaim(requiredClaims, "locality");
 indicateRequiredClaim(requiredClaims, "stateOrProvince");
 indicateRequiredClaim(requiredClaims, "postalCode");
 indicateRequiredClaim(requiredClaims, "country");
 indicateRequiredClaim(requiredClaims, "primaryPhone");
 indicateRequiredClaim(requiredClaims, "otherPhone");
 indicateRequiredClaim(requiredClaims, "mobilePhone");
 indicateRequiredClaim(requiredClaims, "dateOfBirth");
 indicateRequiredClaim(requiredClaims, "gender");
}

function setCard(card){


    var select = document.getElementById('selectcontrol');
    select.setAttribute('hidden', 'false');


    selectedCard = card;

    debug("TYPE: " + selectedCard.type);
    debug(selectedCard);

    if (selectedCard.type == "selfAsserted" )  {

        document.getElementById("cardname").value = selectedCard.name;
        document.getElementById("givenname").value = selectedCard.carddata.selfasserted.givenname;
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

        var label = document.getElementById("notify");
        label.setAttribute("value", "Self Asserted Card");

    }  else if (selectedCard.type == "managedCard" )   {


        document.getElementById("cardname").value = selectedCard.name;
        document.getElementById("givenname").visibility = 'hidden';
        document.getElementById("givenname").value = "";
        document.getElementById("surname").visibility = 'hidden';
        document.getElementById("surname").value = "";
        document.getElementById("email").visibility = 'hidden';
        document.getElementById("email").value = "";
        document.getElementById("streetAddress").visibility = 'hidden';
        document.getElementById("streetAddress").value = "";
        document.getElementById("locality").visibility = 'hidden';
        document.getElementById("locality").value = "";
        document.getElementById("stateOrProvince").visibility = 'hidden';
        document.getElementById("stateOrProvince").value = "";
        document.getElementById("postalCode").visibility = 'hidden';
        document.getElementById("postalCode").value = "";
        document.getElementById("country").visibility = 'hidden';
        document.getElementById("country").value = "";
        document.getElementById("primaryPhone").visibility = 'hidden';
        document.getElementById("primaryPhone").value = "";
        document.getElementById("otherPhone").visibility = 'hidden';
        document.getElementById("otherPhone").value = "";
        document.getElementById("mobilePhone").visibility = 'hidden';
        document.getElementById("mobilePhone").value = "";
        document.getElementById("dateOfBirth").visibility = 'hidden';
        document.getElementById("dateOfBirth").value = "";
        document.getElementById("gender").visibility = 'hidden';
        document.getElementById("gender").value = "";
        document.getElementById("imgurl").visibility = 'hidden';
        document.getElementById("imgurl").value = "";


        indicateRequiredClaims();

        var grid = document.getElementById("editgrid");
        grid.setAttribute("hidden", "false");


        var grid1 = document.getElementById("editgrid1");
        grid1.setAttribute("hidden", "false");

        var label = document.getElementById("notify");
        label.setAttribute("value", "Managed Card from " + selectedCard.carddata.managed.issuer);



    } else if (selectedCard.type == "openid" )  {


        var label = document.getElementById("notify");
        label.setAttribute("value", "Use OpenID with Identity URL: " + selectedCard.id);


    }





}

function handleCardChoice(event){

    var choice = event.originalTarget;
    var selectedCardId = choice.getAttribute("cardid");
    var choosenCard = getCard(selectedCardId);
    setCard(choosenCard);

}



function createItem(c){


    var hbox = document.createElement("hbox");
    hbox.setAttribute("class","contact");
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
    debug ("Setting cardid " + hbox.getAttribute("cardid"));
    return hbox;

}


function saveCard(card){

    var cardFile = read(db);
    cardFile.infocard += card;
    save(db,cardFile.toString());
    var cardArea = document.getElementById("cardselection");
    cardArea.appendChild(createItem(card));
    setCard(card);
    return true;

}


function newCard(){

    var callback;
    var cardWiz = window.openDialog("chrome://infocard/content/cardWizard.xul","Card Wizard", "modal,chrome,resizable=yes,width=640,height=480",
                                    null, function (callbackData) { callback = callbackData;});

    var cardName = callback.cardName;
    var type = callback.type;


    if ( type == "selfAsserted") {

        var card = new XML("<infocard/>");
        card.name = cardName;
        card.type = type;
        var version = "1";
        card.version = version;
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

        var card = new XML("<infocard/>");
        card.name = "" + cardName + "";
        card.type = type;
        var version = "1";
        card.version = version;
        card.id = "" + callback.cardId + "";

        var data = new XML("<managed/>");
        data.issuer = "" + callback.issuer + "";
        data.mex = "" + callback.mex + "";
        data.username = "" + callback.uid + "";
        data.hint = "" + callback.hint + "";
        data.image = "data:image/png;base64," + callback.cardImage + "";

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


function deleteCard(){


    var cardFile = read(db);
    var selectedCardId = selectedCard.id;

    debug("Delete Card : " + selectedCardId);

    var count = 0;
    for each (c in cardFile.infocard) {
        var latestId = c.id;
        if (latestId == selectedCardId) delete cardFile.infocard[count];
        count++;
    }

    save(db,cardFile.toString());

    //debug(cardFile.toString());

    var cardArea = document.getElementById("cardselection");
    while (cardArea.hasChildNodes())
	{
	  cardArea.removeChild(cardArea.firstChild);
	}


    var grid = document.getElementById("editgrid");
    grid.setAttribute("hidden", "true");

    var grid1 = document.getElementById("editgrid1");
    grid1.setAttribute("hidden", "true");

    var label = document.getElementById("notify");
    label.setAttribute("value", "Please select another card");

    var select = document.getElementById('selectcontrol');
    select.setAttribute('hidden', 'true');

    document.getElementById("cardname").value = "";

    selectedCard = null;

    load();





    //var cardToRemove = document.getElementById(selectedCardId);

    //debug("Removing " + cardToRemove);
    //cardArea.removeChild(cardToRemove);






}







function processCard(policy, enableDebug){

    if (enableDebug) {
        var jvm = Components.classes["@mozilla.org/oji/jvm-mgr;1"].getService(Components.interfaces.nsIJVMManager);
        jvm.showJavaConsole();
    }

    var token;
    /* it's not clear what this code does...Axel?  I'm commenting out for now.
    if ( policy.issuer != undefined ) { // if it is defined then it has to be "self"
        if (( policy.issuer == "http://schemas.microsoft.com/ws/2005/05/identity/issuer/self") ||  (policy.issuer == "http://schemas.xmlsoap.org/ws/2005/05/identity/issuer/self"))  {
            var serializedPolicy = JSON.stringify(policy);
            token = TokenIssuer.getToken(serializedPolicy);
        } else {
            debug("Unsupported issuer: " + policy.issuer);
        }
    } else { // if it is undefined, just use the cards I have
        var serializedPolicy = JSON.stringify(policy);
        token = TokenIssuer.getToken(serializedPolicy);
    }
    */
    
    var serializedPolicy = JSON.stringify(policy);
    token = TokenIssuer.getToken(serializedPolicy);

    return token;

}


function getDir(){
    var path;

    try {
        netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
    } catch (e) {
        alert("Permission to save file was denied.");
    }
    // get the path to the user's home (profile) directory
    const DIR_SERVICE = new Components.Constructor("@mozilla.org/file/directory_service;1","nsIProperties");
    try {
        path=(new DIR_SERVICE()).get("ProfD", Components.interfaces.nsIFile).path;
    } catch (e) {
        alert("error");
    }
    // determine the file-separator
    if (path.search(/\\/) != -1) {
        path = path + "\\";
    } else {
        path = path + "/";
    }

    return path;

}



function save(fileName, fileContents) {

    fileName = getDir() + fileName;

    try {
		netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
	} catch (e) {
		debug("Unable to manage db");
	}
	var file = Components.classes["@mozilla.org/file/local;1"].createInstance(Components.interfaces.nsILocalFile);
	file.initWithPath( fileName );
	if ( file.exists() == false ) {
		file.create( Components.interfaces.nsIFile.NORMAL_FILE_TYPE, 420 );
	}
	var outputStream = Components.classes["@mozilla.org/network/file-output-stream;1"].createInstance( Components.interfaces.nsIFileOutputStream );
	outputStream.init( file, 0x04 | 0x08 | 0x20, 420, 0 );
    var result = outputStream.write( fileContents, fileContents.length );
    outputStream.close();

}



function read(fileName) {

    fileName = getDir() + fileName;

    try {
		netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
	} catch (e) {
		debug("Permission to read file was denied.");
	}

	var file = Components.classes["@mozilla.org/file/local;1"].createInstance(Components.interfaces.nsILocalFile);
	file.initWithPath( fileName );

    if ( file.exists() == false ) {

        return newDB();

	} else {

        var is = Components.classes["@mozilla.org/network/file-input-stream;1"].createInstance( Components.interfaces.nsIFileInputStream );
		is.init( file,0x01, 00004, null);
		var sis = Components.classes["@mozilla.org/scriptableinputstream;1"].createInstance( Components.interfaces.nsIScriptableInputStream );
		sis.init( is );
		var output = sis.read( sis.available() );
        if (!output) output = newDB();
        var dbFile = new XML(output);
        return dbFile;

	}

}


function newDB(){

    var dbFile = new XML("<infocards/>");
    dbFile.version = "1";
    debug ( "New DB: " + dbFile.toString());
    return dbFile;

}



function debug(msg) {
  var debug = Components.classes["@mozilla.org/consoleservice;1"].getService(Components.interfaces.nsIConsoleService);
  debug.logStringMessage("infocard: " + msg);
}
