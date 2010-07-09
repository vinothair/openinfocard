package com.awl.fc2.plugin.store.local;

import com.awl.fc2.plugin.store.IPlugInStore;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.session.ISessionElement;
import com.awl.fc2.selector.storage.ICardStoreV2;

public class PlugInCardStore_Local implements IPlugInStore {
	LocalCardStore wallet = null;
	
	@Override
	public ICardStoreV2 getCardStore() {
		// TODO Auto-generated method stub
		return wallet;
	}

	@Override
	public ISessionElement getSessionElement() {
		// TODO Auto-generated method stub
		return wallet;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "WalletCardStore";
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return PLG_SESSION_ELEMENT+","+PLG_CARDSTORE;
	}

	@Override
	public void install(Config cnf) {
		wallet = new LocalCardStore();		
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
