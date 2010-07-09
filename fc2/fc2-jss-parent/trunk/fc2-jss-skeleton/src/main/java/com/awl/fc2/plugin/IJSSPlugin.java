package com.awl.fc2.plugin;

import com.awl.fc2.selector.launcher.Config;

public interface IJSSPlugin {
	public static String PLG_SESSION_ELEMENT = "plgSessionElement";
	public static String PLG_AUTHENTICATION_HANDLER ="plgAuthenticationHandler";
	public static String PLG_USER_INTERFACE ="plgUserInterface";
	public static String PLG_CARDSTORE ="plgCardStore";
	public static String PLG_CONNECTOR ="PLG_CONNECTOR";
	public static String PLG_PREPROCESS ="PLG_PREPROCESS";
	
	
	
	public String getType();
	public String getName();
	public void install(Config cnf);
	public void uninstall();
	
	public int getPriority();
	
}
