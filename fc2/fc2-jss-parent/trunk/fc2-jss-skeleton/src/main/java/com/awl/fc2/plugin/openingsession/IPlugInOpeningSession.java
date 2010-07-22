package com.awl.fc2.plugin.openingsession;

import com.awl.fc2.plugin.IJSSPlugin;

public interface IPlugInOpeningSession extends IJSSPlugin {

		public void retrieveUserCredentials();
		public String getUsername();
		public String getPassword();
}
