var db = "cardDb.xml";

function getCard(cardid){

    var cardFile = read(db);
    var card = cardFile.infocard.(id == cardid);
    return card;
}

function readCardStore() {
 var cardFile = read(db);
 return cardFile;
}

function storeCard(card){
debug("storeCard");
debug(card);
    var cardFile = read(db);
    cardFile.infocard += card;
    save(db,cardFile.toString());
}

function removeCard(selectedCardId) {
    var cardFile = read(db);
    var count = 0;
    for each (c in cardFile.infocard) {
        var latestId = c.id;
        if (latestId == selectedCardId) delete cardFile.infocard[count];
        count++;
    }
    save(db,cardFile.toString());
}

// make a copy of the db replacing one card
function updateCard(card) {
    var cardFile = read(db);
    var nDB = newDB();
    
    for each (c in cardFile.infocard) {
        var latestId = c.id;
        if (latestId == card.id) {
         nDB.infocard += card;
        } else {
         nDB.infocard += c;
        }
    }
    save(db,nDB.toString());
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

function newDB(){

    var dbFile = new XML("<infocards/>");
    dbFile.version = "1";
    debug ( "New DB: " + dbFile.toString());
    return dbFile;

}

function debug(msg) {
  var debug = Components.classes["@mozilla.org/consoleservice;1"].getService(Components.interfaces.nsIConsoleService);
  debug.logStringMessage("cardstore: " + msg);
}

