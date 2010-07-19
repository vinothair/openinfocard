package com.awl.fc2.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.io.FileUtils;

import com.awl.fc2.plugin.authenticationHandler.IPlugInAuthenticationHandler;
import com.awl.fc2.plugin.connector.IPluginConnector;
import com.awl.fc2.plugin.preprocessing.IPlugInPreProcess;
import com.awl.fc2.plugin.session.IPlugInSession;
import com.awl.fc2.plugin.store.IPlugInStore;
import com.awl.fc2.plugin.ui.IPluginUI;
import com.awl.fc2.plugin.ui.console.PlugInUI_Console;
import com.awl.fc2.selector.connector.IConnector;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.storage.ICardStoreV2;
import com.awl.fc2.selector.userinterface.ISelectorUI;
import com.awl.logger.Logger;

public class PlugInLauncher {
	
	static Logger log = new Logger(PlugInLauncher.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
	HashMap<String, IPlugInSession> mapSessionPlugin = new HashMap<String, IPlugInSession>();
	HashMap<String, IPlugInAuthenticationHandler> mapAuthHPlugin = new HashMap<String, IPlugInAuthenticationHandler>();
	HashMap<String, IPluginConnector> mapConnectorPlugin = new HashMap<String, IPluginConnector>();
	HashMap<String, IPlugInPreProcess> mapPreProcessingPlugin = new HashMap<String, IPlugInPreProcess>();
	ICardStoreV2 cardStore = null;
	int icardStore_priority = -1;
	ISelectorUI ui = null;
	int iui_priority = -1;
	
	public void addPlugin(IJSSPlugin plugin){
		if(plugin.getType().contains(IJSSPlugin.PLG_SESSION_ELEMENT)){
			mapSessionPlugin.put(plugin.getName(), (IPlugInSession)plugin);
		}
		if(plugin.getType().contains(IJSSPlugin.PLG_CARDSTORE)){
			if(icardStore_priority < plugin.getPriority()){
				cardStore = ((IPlugInStore) plugin).getCardStore();
				icardStore_priority = plugin.getPriority();
			}else{
				plugin.uninstall();
			}
			
		}
		if(plugin.getType().contains(IJSSPlugin.PLG_AUTHENTICATION_HANDLER)){
			trace("Adding AuthHandler : " + plugin.getName());
			mapAuthHPlugin.put(plugin.getName(), (IPlugInAuthenticationHandler)plugin);
		}
		if(plugin.getType().contains(IJSSPlugin.PLG_USER_INTERFACE)){
			//if(!plugin.getName().equalsIgnoreCase("PulgInUI_Console") || ui == null){
			if(iui_priority < plugin.getPriority()){
				iui_priority = plugin.getPriority();
				trace("Installing : " + plugin.getName());
				ui = ((IPluginUI) (plugin)).getInterface();
			}else{
				plugin.uninstall();
			}
				
			//}
			
		}
		if(plugin.getType().contains(IJSSPlugin.PLG_CONNECTOR)){
			trace("Adding Connector : " + plugin.getName());
			mapConnectorPlugin.put(plugin.getName(), (IPluginConnector)plugin);
		}
		if(plugin.getType().contains(IJSSPlugin.PLG_PREPROCESS)){
			trace("Adding Connector : " + plugin.getName());
			mapPreProcessingPlugin.put(plugin.getName(), (IPlugInPreProcess)plugin);
		}
		
	}
	
	public void loadDefault(Config cnf){
//		{
//			IJSSPlugin plg =new PlugInSession_TrayIcon();
//			plg.install(cnf);
//			addPlugin(plg);
////		}
		//UI - Console
		{
			IJSSPlugin plg =new PlugInUI_Console();
			plg.install(cnf);
			addPlugin(plg);
		}
//		{//UI - Flex
//			IJSSPlugin plg =new PlugInUI_Flex();
//			plg.install(cnf);
//			addPlugin(plg);
//		}
//		{// Wallet Store
//			IJSSPlugin plg = new PlugInCardStore_Wallet();
//			plg.install(cnf);
//			addPlugin(plg);
//		}
//		{// Local Store
//			IJSSPlugin plg = new PlugInCardStore_Local();
//			plg.install(cnf);
//			addPlugin(plg);
//		}
//		{//HBX Connector
//			IJSSPlugin plg = new PlugInConnector_HBX();
//			plg.install(cnf);
//			addPlugin(plg);
//		}
		//Authentication handler
//		{
//			IJSSPlugin plg = new PlugInAuthenticationHandler_MAP();
//			plg.install(cnf);
//			addPlugin(plg);
//		}
//		{
//			IJSSPlugin plg = new PlugInAuthenticationHandler_SELMAC();
//			plg.install(cnf);
//			addPlugin(plg);
//		}
//		{
//			IJSSPlugin plg = new PlugInAuthenticationHandler_UserName();
//			plg.install(cnf);
//			addPlugin(plg);
//		}
//		{
//			IJSSPlugin plg = new PlugInAuthenticationHandler_X509();
//			plg.install(cnf);
//			addPlugin(plg);
//		}
//		
//		
//		{// Preprocess - addcard
//			IJSSPlugin plg = new PlugInPreProcess_AddCard();
//			plg.install(cnf);
//			addPlugin(plg);
//		}
		
	}
	synchronized public void launch(Config cnf){
		trace("Launch plugin launcher");
		loadDefault(cnf);
		try {
			lookForPlugins(cnf);
		} catch (Exception e) {
			trace("problem loading plugin");
		}
	}
	
	public Vector<IPlugInAuthenticationHandler> getAuthenticationHandlerPlugin(){
		Vector<IPlugInAuthenticationHandler> res = new Vector<IPlugInAuthenticationHandler>();
		for(IPlugInAuthenticationHandler plg : mapAuthHPlugin.values()){
			res.add(plg);
		}
		return res;
	}
	
	public Vector<IPlugInSession> getSessionElementPlugin(){
		Vector<IPlugInSession> res = new Vector<IPlugInSession>();
		for(IPlugInSession plg : mapSessionPlugin.values()){
			res.add(plg);
		}
		return res;
	}
	
	public Vector<IConnector> getConnectors(){
		Vector<IConnector> res = new Vector<IConnector>();
		for(IPluginConnector plg : mapConnectorPlugin.values()){
			res.add(plg.getConnector());
		}
		return res;
	}
	
	public Vector<IPlugInPreProcess> getPreProcessingPlugins(){
		Vector<IPlugInPreProcess> res = new Vector<IPlugInPreProcess>();
		for(IPlugInPreProcess plg : mapPreProcessingPlugin.values()){
			res.add(plg);
		}
		return res;
	}
	public ICardStoreV2 getCardStore(){
		return cardStore;
	}
	public ISelectorUI getUI(){
		return ui;
	}
	public void lookForPlugins(Config cnf) throws Exception{
		String pack=IJSSPlugin.class.getPackage().getName();
		//addClasses("D:/Cauchie stephane/progs Tmp/WrksSpaces/Web/MAP_Solution/awl.smartcards/target/classes/",null);
//		trace("Look into : " + pack);
//		Class [] tabClasses = getClasses(pack);
//		for(Class cls :tabClasses){
//			trace("Found : " + cls);
//		}
//		
		trace("Look into : " + pack);
		
		Collection<Class> collClasses = getClassesForPackage(pack);
		Vector<Class> tobeLoad = new Vector<Class>();
		for(Class cls :collClasses){
			//trace("Check : " +cls);
			boolean found = false;
			for(Class parent : cls.getInterfaces()){
				
//				if(parent.equals(IJSSPlugin.class)){
//					trace("Found : " + cls);
//					break;
//				}
				if(parent.equals(IPlugInAuthenticationHandler.class)){
					found=true;
					break;
				}
				if(parent.equals(IPluginConnector.class)){
					found=true;
					break;
				}
				if(parent.equals(IPlugInSession.class)){
					found=true;
					
					break;
				}
				if(parent.equals(IPlugInStore.class)){
					found=true;
					break;
				}
				if(parent.equals(IPluginUI.class)){
					found=true;
					break;
				}
				if(parent.equals(IPlugInPreProcess.class)){
					found=true;
					break;
				}
			}
			if(found){
				trace("Found plugin  : "+ cls);
				tobeLoad.add(cls);
				
			}
			
			
		}
		for(Class toLoad : tobeLoad){
			try {
				IJSSPlugin plg = (IJSSPlugin) toLoad.newInstance();
				plg.install(cnf);
				addPlugin(plg);
			} catch (Exception e) {
				trace("not a good plugin  : " + toLoad);
			}
		}
	}
	public static void main(String arg[]) throws Exception{
		String pack=IJSSPlugin.class.getPackage().getName();
		//addClasses("D:/Cauchie stephane/progs Tmp/WrksSpaces/Web/MAP_Solution/awl.smartcards/target/classes/",null);
//		trace("Look into : " + pack);
//		Class [] tabClasses = getClasses(pack);
//		for(Class cls :tabClasses){
//			trace("Found : " + cls);
//		}
//		
		trace("Look into : " + pack);
		Collection<Class> collClasses = getClassesForPackage(pack);
		for(Class cls :collClasses){
			//trace("Check : " +cls);
			
			for(Class parent : cls.getInterfaces()){
				
				if(parent.equals(IJSSPlugin.class)){
					trace("Found : " + cls);
					break;
				}
				if(parent.equals(IPlugInAuthenticationHandler.class)){
					trace("Found : " + cls);
					break;
				}
				if(parent.equals(IPluginConnector.class)){
					trace("Found : " + cls);
					break;
				}
				if(parent.equals(IPlugInSession.class)){
					trace("Found : " + cls);
					break;
				}
				if(parent.equals(IPlugInStore.class)){
					trace("Found : " + cls);
					break;
				}
				if(parent.equals(IPluginUI.class)){
					trace("Found : " + cls);
					break;
				}
			}
			
			
		}
	}
	
	/**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName;//.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
        	trace(">> " + directory);
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
        	trace("files : " + file);
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
    static public Collection<Class> getClassesForPackage(String packageName) throws Exception {
    	  String packagePath = packageName.replace(".", "/");
    	  ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    	  Set<URL> jarUrls = new HashSet<URL>();
    	  Set<Class> classes = new HashSet<Class>();
    	  while (classLoader != null) {
    	    if (classLoader instanceof URLClassLoader)
    	      for (URL url : ((URLClassLoader) classLoader).getURLs()){
    	    	 // trace("URL : " + url);
    	    	  if(url.getFile().endsWith("/")){
    	    		  String path = url.getFile().replaceFirst("/", "").replaceAll("%20"," ")+packagePath;
    	    		  addClasses(path,packageName, classes);
    	    	  }
    	    	  if (url.getFile().endsWith(".jar"))  // may want better way to detect jar files
        	          jarUrls.add(url);
    	      }
    	        

    	    classLoader = classLoader.getParent();
    	  }

    	  

    	  for (URL url : jarUrls) {
    	    JarInputStream stream = new JarInputStream(url.openStream()); // may want better way to open url connections
    	    JarEntry entry = stream.getNextJarEntry();

    	    while (entry != null) {
    	      String name = entry.getName();
    	   //   trace("NAME : " + name);
//    	      int i = name.lastIndexOf("/");
//
//    	      if (i > 0 && name.endsWith(".class") && name.substring(0, i).equals(packagePath)) {
//    	    	  trace(">> found " + name);
//    	    	  classes.add(Class.forName(name.substring(0, name.length() - 6).replace("/", ".")));  
//    	      }
    	      if(name.startsWith(packagePath) && name.contains(".class")){
    	    	  trace(">> found " + name);
    	    	  classes.add(Class.forName(name.replace("/", ".").replace(".class", "")));
    	      }
    	       

    	      entry = stream.getNextJarEntry();
    	    }

    	    stream.close();
    	  }

    	  return classes;
    	}


    public static void addClasses(String path,String pack,Collection<Class> classes){
    	//trace("Looking in " + path);
    	 File root = new File(path);
    	 if(!root.exists()){
    		// trace("No Directory ");
    		 return;
    	 }
         try {
             String[] extensions = {"class"};
             boolean recursive = true;

             //
             // Finds files within a root directory and optionally its
             // subdirectories which match an array of extensions. When the
             // extensions is null all files will be returned.
             //
             // This method will returns matched file as java.io.File
             //
             Collection files = FileUtils.listFiles(root, extensions, recursive);

             for (Iterator iterator = files.iterator(); iterator.hasNext();) {
                 File file = (File) iterator.next();
                 path = path.replace("/", ".").replace("\\", ".");;
                 
                 String strurl = file.getAbsolutePath().replace("/", ".").replace("\\", ".").replaceFirst(path, "").replaceAll(".class","");
                 if(strurl.contains("$")) continue;
                 
                 Class cls = Class.forName(pack+strurl);
                 classes.add(cls);
                // System.out.println("File = " + strurl);
             }
         } catch (Exception e) {
             e.printStackTrace();
         }

    }

}
