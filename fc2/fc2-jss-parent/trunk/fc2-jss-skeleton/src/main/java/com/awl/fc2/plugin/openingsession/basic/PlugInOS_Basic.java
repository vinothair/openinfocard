package com.awl.fc2.plugin.openingsession.basic;

import com.awl.fc2.plugin.IJSSPlugin;
import com.awl.fc2.plugin.openingsession.IPlugInOpeningSession;
import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.userinterface.lang.Lang;

public class PlugInOS_Basic implements IPlugInOpeningSession {

	@Override
	public String getPassword() {
		String pwd;
		try {
			pwd = Selector.getInstance().getUI().getBasicInterface().sendQuestion(Lang.get(Lang.NEW_SESSION), "-" + Lang.get(Lang.ASKPWD),true);
			return pwd;
		} catch (Config_Exeception_UnableToReadConfigFile e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Config_Exeception_MalFormedConfigFile e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Config_Exception_NotDone e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getUsername() {
		String username;
		try {
			username = Selector.getInstance().getUI().getBasicInterface().sendQuestion(Lang.get(Lang.NEW_SESSION), "-" + Lang.get(Lang.ASK_USERNAME),false);
			return username;
		} catch (Config_Exeception_UnableToReadConfigFile e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Config_Exeception_MalFormedConfigFile e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Config_Exception_NotDone e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void retrieveUserCredentials() {
		// TODO Auto-generated method stub
		//return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return PlugInOS_Basic.class.toString();
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return IJSSPlugin.PLG_OPENING_SESSION;
	}

	@Override
	public void install(Config cnf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void uninstall() {
		// TODO Auto-generated method stub
		
	}

}
