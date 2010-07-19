package com.awl.fc2.plugin.ui.swing;

import com.awl.fc2.plugin.ui.IPluginUI;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.userinterface.ISelectorUI;

public class PlugInUI_Swing implements IPluginUI {
	SelectorUI_Swing ui;
	
	@Override
	public ISelectorUI getInterface() {
		// TODO Auto-generated method stub
		return ui;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "PulgInUI_Swing";
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return PLG_USER_INTERFACE;
	}

	@Override
	public void install(Config cnf) {
		ui = new SelectorUI_Swing();

	}
	@Override
	public int getPriority() {
		
		return 2;
	}

	@Override
	public void uninstall() {
		// TODO Auto-generated method stub
		
	}

}
