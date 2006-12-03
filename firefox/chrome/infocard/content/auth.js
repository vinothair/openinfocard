

function authenticate() {

    var pk11tokendb = Components.classes["@mozilla.org/security/pk11tokendb;1"].createInstance(Components.interfaces.nsIPK11TokenDB);
    var pk11token = pk11tokendb.getInternalKeyToken();
    if ( ! pk11token.isLoggedIn() ) {

        if (pk11token.checkPassword(""))  window.openDialog("chrome://mozapps/content/preferences/changemp.xul","","modal,chrome",null);

        try {
            pk11token.login(true);
        } catch (e) {
            return false;
        }

    }
    return pk11token.isLoggedIn();
}