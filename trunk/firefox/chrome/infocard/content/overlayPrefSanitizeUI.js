var InformationCardPrefSanitizeUI = {
	sanitizeUI : function() {
		var prefs = document.getElementsByTagName('preferences')[0];
		if (prefs) {
			var pref = document.createElement('preference');
			pref.setAttribute('id', 'privacy.item.infocard');
			pref.setAttribute('name', 'privacy.item.infocard');
			pref.setAttribute('type', 'bool');
			prefs.appendChild(pref);
		}
		
		var cb = document.getElementsByTagName('checkbox')[0];
		if (cb)
		{
			var parent = cb.parentNode;
			
			var check = document.createElement('checkbox');
			var stringsBundle = document.getElementById("string-bundle");
			var labelValue = 'openinfocard history';
			if (stringsBundle) {
				labelValue = stringsBundle.getString('openinfocard history');
			}
			check.setAttribute('label', labelValue);
			check.setAttribute('preference', 'privacy.item.infocard');
	 		check.setAttribute('onsyncfrompreference', 'if (gSanitizePromptDialog) {return gSanitizePromptDialog.onReadGeneric();} else {return false;}');
			parent.appendChild(check);
		}
	}
};

try {
	InformationCardPrefSanitizeUI.sanitizeUI();
} catch (e) {
	try { Components.utils.reportError(ex); } catch(ex) {}
}

