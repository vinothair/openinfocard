package com.awl.fc2.plugin.store;

import com.awl.fc2.plugin.session.IPlugInSession;
import com.awl.fc2.selector.storage.ICardStoreV2;

public interface IPlugInStore extends IPlugInSession {
	public ICardStoreV2 getCardStore();
}
