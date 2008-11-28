var InformationCardSanitizeUI = {
	sanitizeUI : function() {
		var cb = document.getElementsByTagName('checkbox')[0];
		if (cb)
		{
			var parent = cb.parentNode;
			
			var check = document.createElement('checkbox');
			check.setAttribute('id', 'openinfocardHistoryCheckbox');
			var stringsBundle = document.getElementById("string-bundle");
			var labelValue = 'openinfocard history';
			if (stringsBundle) {
				labelValue = stringsBundle.getString('openinfocard history');
			}
			check.setAttribute('label', labelValue);
			check.setAttribute('preference', 'privacy.item.infocard');
			parent.appendChild(check);
			if (gSanitizePromptDialog)
			{
		 		check.setAttribute('onsyncfrompreference', 'return gSanitizePromptDialog.onReadGeneric();');
			}
		}
	}
};

try {
	InformationCardSanitizeUI.sanitizeUI();
} catch (e) {
	try { Components.utils.reportError(ex); } catch(ex) {}
}

Sanitizer.prototype.items['infocard'] = {
	clear : function() {
		try	{
			// clear the information card history here
			clearOpeninfocardHistory();
		} catch (ex) {
			try { Components.utils.reportError(ex); } catch(ex) {}
		}
	},
	get canClear() {
		return true;
	}
};
