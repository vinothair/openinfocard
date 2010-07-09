package com.awl.fc2.plugin.ui.flex;

import com.awl.fc2.plugin.ui.IPluginUI;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.userinterface.ISelectorUI;

public class PlugInUI_Flex implements IPluginUI {
	SelectorUI_Flex ui;
	
	@Override
	public ISelectorUI getInterface() {
		// TODO Auto-generated method stub
		return ui;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "PulgInUI_Flex";
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return PLG_USER_INTERFACE;
	}

	@Override
	public void install(Config cnf) {
		ui = new SelectorUI_Flex();

	}

	@Override
	public int getPriority() {
		
		return 2;
	}

	@Override
	public void uninstall() {
		ui.kill();
		
	}

}
