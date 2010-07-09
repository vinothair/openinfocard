package com.awl.fc2.plugin.connector;

import com.awl.fc2.plugin.IJSSPlugin;
import com.awl.fc2.selector.connector.IConnector;

public interface IPluginConnector extends IJSSPlugin {

	IConnector getConnector();
}
