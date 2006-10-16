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
 *     * Neither the name of the University of California, Berkeley nor the
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
    //TODO - I'm not enforcing policy - do so.
    var policy = window.arguments[0];
    policy["card"] = selectedCard.toString();

    //TRUE of FALSE on the second param enabled debug
    var tokenToReturn = processCard(policy,false);
    window.arguments[1](tokenToReturn);
    window.close();

}


function cancel(){
    window.arguments[1](null);
    window.close();
}

function getCard(cardid){

    var cardFile = read(db);
    var card = cardFile.infocard.(id == cardid);
    return card;


}


function load(){
    var stringsBundle = document.getElementById("string-bundle");

    var cardFile = read(db);
    var cardArea = document.getElementById("listarea");
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

    selectedCard = card;
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

    indicateRequiredClaims();

    var grid = document.getElementById("editgrid");
    grid.setAttribute("hidden", "false");
    var label = document.getElementById("notify");
    label.setAttribute("value", "Selected Card");




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
    var imgurl = c.carddata.selfasserted.imgurl;
     //var picture = document.createElement("html:img");
    var picturebox = document.createElement("hbox");
    picturebox.setAttribute("flex", "0");
    picturebox.setAttribute("align", "center");
     var picture = document.createElement("image");
debug(c.name + " " + imgurl);
    if (imgurl != undefined) {
debug(c.name + ":" + imgurl);
     //picture.setAttribute("src", "chrome://infocard/content/xmldap.png");
     picture.setAttribute("src", imgurl);
     //picture.setAttribute("width", "32");
     //picture.setAttribute("height", "32");
     picture.setAttribute("cardid", c.id);
     //vbox.appendChild(picture);
     picturebox.appendChild(picture);
    }
    vbox.appendChild(picturebox);
    vbox.appendChild(labelName);
    vbox.appendChild(labelVersion);
    hbox.appendChild(vbox);
    hbox.addEventListener("click", handleCardChoice, false);
    return hbox;

}


function saveCard(card){

    var cardFile = read(db);
    cardFile.infocard += card;
    save(db,cardFile.toString());
    var cardArea = document.getElementById("listarea");
    cardArea.appendChild(createItem(card));
    setCard(card);
    return true;

}


function newCard(){

    var callback;
    var cardWiz = window.openDialog("chrome://infocard/content/cardWizard.xul","Card Wizard", "modal,chrome,resizable=yes,width=640,height=480",
                                    null, function (callbackData) { callback = callbackData;});

    var cardName = callback.cardname;
    var type = callback.type;

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


function deleteCard(){


    var cardFile = read(db);
    var selectedCardId = selectedCard.id;

    var count = 0;
    for each (c in cardFile.infocard) {
        var latestCard = createItem(c);
        var latestId = latestCard.id;
        if (latestId == selectedCardId) cardFile.infocard[count] = null;
        count++;
    }

}







function processCard(policy, enableDebug){

    if (enableDebug) {
        var jvm = Components.classes["@mozilla.org/oji/jvm-mgr;1"].getService(Components.interfaces.nsIJVMManager);
        jvm.showJavaConsole();
    }

    var token;
    if ( policy.issuer !== undefined ) { // if it is defined then it has to be "self"
     if ( policy.issuer == "http://schemas.microsoft.com/ws/2005/05/identity/issuer/self") {
        var serializedPolicy = JSON.stringify(policy);
        token = TokenIssuer.getToken(serializedPolicy);
     } else {
      debug("Unsupported issuer: " + policy.issuer);
     }
    } else { // if it is undefined, just use the cards I have
        var serializedPolicy = JSON.stringify(policy);
        token = TokenIssuer.getToken(serializedPolicy);
    }

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
	/* Open flags
	#define PR_RDONLY       0x01
	#define PR_WRONLY       0x02
	#define PR_RDWR         0x04
	#define PR_CREATE_FILE  0x08
	#define PR_APPEND      0x10
	#define PR_TRUNCATE     0x20
	#define PR_SYNC         0x40
	#define PR_EXCL         0x80
	*/
	/*
	** File modes ....
	**
	** CAVEAT: 'mode' is currently only applicable on UNIX platforms.
	** The 'mode' argument may be ignored by PR_Open on other platforms.
	**
	**   00400   Read by owner.
	**   00200   Write by owner.
	**   00100   Execute (search if a directory) by owner.
	**   00040   Read by group.
	**   00020   Write by group.
	**   00010   Execute by group.
	**   00004   Read by others.
	**   00002   Write by others
	**   00001   Execute by others.
	**
	*/
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
