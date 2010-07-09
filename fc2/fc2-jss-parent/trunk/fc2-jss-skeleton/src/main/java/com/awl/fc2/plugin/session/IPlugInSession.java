package com.awl.fc2.plugin.session;

import com.awl.fc2.plugin.IJSSPlugin;
import com.awl.fc2.selector.session.ISessionElement;

public interface IPlugInSession extends IJSSPlugin {
	public ISessionElement getSessionElement();
}
