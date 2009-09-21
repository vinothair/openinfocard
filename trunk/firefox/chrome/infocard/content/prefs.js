function prefsDebug(msg) {
	var debug = Components.classes['@mozilla.org/consoleservice;1'].getService(Components.interfaces.nsIConsoleService);
	debug.logStringMessage("openinfocard prefs: " + msg);
}

function prefsCallback() {
	var mgmtWindow = window.openDialog("chrome://infocard/content/cardManager.xul","InfoCard Selector", "modal,chrome,resizable,width=800,height=640,centerscreen");
}

function filePicker() {

	netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
	var nsIFilePicker = Components.interfaces.nsIFilePicker;
	var fp = Components.classes["@mozilla.org/filepicker;1"].createInstance(nsIFilePicker);
	fp.init(window, "Select a File", nsIFilePicker.modeOpen);
	fp.appendFilters( nsIFilePicker.filterAll );
	var res = fp.show();
	if (res == nsIFilePicker.returnOK) {
		var textbox = document.getElementById("cardStoreLocalFilePath");
		textbox.value = fp.file.path;
		var pref = document.getElementById("pref_cardStoreLocalFilePath");
		pref.value = fp.file.path;
	}

}

function populateMenu() {
	var cid = null;

	// lookup class id from config.
	var prefs = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
	var pbi = prefs.QueryInterface(Components.interfaces.nsIPrefBranch);

	cid = pbi.getCharPref("extensions.identityselector.selector_class");

	prefsDebug("selected selector " + cid);
	var menuList = document.getElementById("selector");
	var menuPopup = document.getElementById("menupopup");

	var CATMAN_CONTRACTID = "@mozilla.org/categorymanager;1";
	var nsICategoryManager = Components.interfaces.nsICategoryManager;
	var catman = Components.classes[CATMAN_CONTRACTID].getService(nsICategoryManager);
	var IIDENTITYSELECTOR_IID_STR = "ddd9bc02-c964-4bd5-b5bc-943e483c6c57";

	var selectors = catman.enumerateCategory ( IIDENTITYSELECTOR_IID_STR );
	for (;selectors.hasMoreElements(); ) {
		var clasz = selectors.getNext().QueryInterface(Components.interfaces.nsISupportsCString).data;
		prefsDebug("clasz=" + clasz);

		var categoryEntry = catman.getCategoryEntry(IIDENTITYSELECTOR_IID_STR, clasz);
		var j = categoryEntry.indexOf(':');
		var selectorClass = categoryEntry.substring(0,j);
		var contractid = categoryEntry.substring(j+1);
		prefsDebug("contractid=" + contractid);
		prefsDebug("selectorClass=" + selectorClass);

		try {
			var obj = Components.classes[contractid];
			if (obj !== undefined) {
				var menuitem = document.createElement("menuitem");
				menuitem.setAttribute("label", clasz);
				menuitem.setAttribute("value", selectorClass);
				menuPopup.appendChild(menuitem);
				if ((selectorClass !== null) && (cid !== null) && (selectorClass === cid)) {
					prefsDebug("selecting " + clasz);
					menuList.selectedItem = menuitem;
				}
			} else {
				prefsDebug("the class " + clasz + " class is unknown");
			}
		}
		catch (e) { prefsDebug("selector " + clasz + " is not installed. " + e); }

	}


	var aMenuitem = document.createElement("menuitem");
	aMenuitem.setAttribute("label", "digitalme");
	aMenuitem.setAttribute("value", "digitalme");
	menuPopup.appendChild(aMenuitem);
	if (aMenuitem.selectedItem === null) {
		menuList.selectedItem = aMenuitem;
	}

}
