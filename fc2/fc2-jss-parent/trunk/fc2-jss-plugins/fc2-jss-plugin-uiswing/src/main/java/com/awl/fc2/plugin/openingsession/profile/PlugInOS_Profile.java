package com.awl.fc2.plugin.openingsession.profile;

import com.awl.fc2.plugin.IJSSPlugin;
import com.awl.fc2.plugin.openingsession.IPlugInOpeningSession;
import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.userinterface.lang.Lang;
import com.awl.fc2.selector.userinterface.swing.Dialog_Profile;

public class PlugInOS_Profile implements IPlugInOpeningSession {

	String username, password;
	boolean profileUse = false;
	
	@Override
	public String getPassword() {
		//String password;
		if(profileUse) return password;
		else {
		try {
			password = Selector.getInstance().getUI().getBasicInterface().sendQuestion(Lang.get(Lang.NEW_SESSION), "-" + Lang.get(Lang.ASKPWD),true);
			return password;
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
		//return "robert";
		}
	}

	@Override
	public String getUsername() {
		//String username;
//		try {
//			username = Selector.getInstance().getUI().getBasicInterface().sendQuestion(Lang.get(Lang.NEW_SESSION), "-" + Lang.get(Lang.ASK_USERNAME),false);
			return username;
//		} catch (Config_Exeception_UnableToReadConfigFile e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Config_Exeception_MalFormedConfigFile e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Config_Exception_NotDone e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
	}

	@Override
	public void retrieveUserCredentials() {
		// TODO Auto-generated method stub
		Dialog_Profile pd = new Dialog_Profile();
		pd.settings("opening a session");
		profileUse = pd.getProfileUse();
		username = pd.getUsername();
		if(profileUse) password = pd.getPassword();
	}

	@Override
	public String getName() {
		return PlugInOS_Profile.class.toString();
	}

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public String getType() {
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
