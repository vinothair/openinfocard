package com.awl.fc2.plugin.ui.console;

import com.awl.fc2.plugin.ui.IPluginUI;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.userinterface.ISelectorUI;

public class PlugInUI_Console implements IPluginUI {
	SelectorUI_Console ui;
	
	@Override
	public ISelectorUI getInterface() {
		// TODO Auto-generated method stub
		return ui;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "PulgInUI_Console";
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return PLG_USER_INTERFACE;
	}

	@Override
	public void install(Config cnf) {
		ui = new SelectorUI_Console();

	}
	@Override
	public int getPriority() {
		
		return 1;
	}

	@Override
	public void uninstall() {
		// TODO Auto-generated method stub
		
	}

}
