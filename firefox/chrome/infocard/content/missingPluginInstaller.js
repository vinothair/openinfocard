// store the original
missingPluginInstaller.prototype.__newMissingPlugin = missingPluginInstaller.prototype.newMissingPlugin;

missingPluginInstaller.prototype.newMissingPlugin = function(aEvent){
	var pluginInfo = getPluginInfo(aEvent.target);
	if ("application/x-informationcard" == pluginInfo.mimetype) {
		var doc = aEvent.target.ownerDocument;
		var objElem = aEvent.target;
		// send event
		var event = doc.createEvent( "Event");
        event.initEvent( "ICObjectLoaded", true, true);
        objElem.dispatchEvent( event);
		return;
	}
	missingPluginInstaller.prototype.__newMissingPlugin(aEvent);
}