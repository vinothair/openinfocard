package com.awl.fc2.plugin.connector.hbxsocket;

import com.awl.fc2.plugin.connector.IPluginConnector;
import com.awl.fc2.selector.connector.IConnector;
import com.awl.fc2.selector.launcher.Config;

public class PlugInConnector_HBX implements IPluginConnector {

	HBXSocketConnector connector;
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "PlugInConnector_HBX";
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return PLG_CONNECTOR;
	}

	@Override
	public void install(Config cnf) {
		if(connector == null){
			connector = new HBXSocketConnector();
		}

	}

	@Override
	public IConnector getConnector() {
		
		return connector;
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
