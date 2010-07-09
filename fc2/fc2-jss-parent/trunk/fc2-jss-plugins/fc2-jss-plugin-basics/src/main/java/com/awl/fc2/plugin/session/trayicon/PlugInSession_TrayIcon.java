package com.awl.fc2.plugin.session.trayicon;

import com.awl.fc2.plugin.session.IPlugInSession;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.session.ISessionElement;

public class PlugInSession_TrayIcon implements IPlugInSession {

	FC2TrayIcon trayIcon = null;
	
	@Override
	public ISessionElement getSessionElement() {
		// TODO Auto-generated method stub
		return trayIcon;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "TrayIcon";
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return PLG_SESSION_ELEMENT;
	}

	@Override
	public void install(Config cnf) {
		trayIcon = new FC2TrayIcon();
		
	}
	@Override
	public int getPriority() {
		
		return 0;
	}
	@Override
	public void uninstall() {
		// TODO Auto-generated method stub
		
	}

}
