package com.awl.fc2.plugin.store.local;

import java.util.Vector;

import javax.swing.JOptionPane;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.infocard.InfoCard;

import com.awl.fc2.selector.exceptions.CardStore_Execption_FailedRetrieving;
import com.awl.fc2.selector.exceptions.CredentialStore_Execption_FailedRetrieving;
import com.awl.fc2.selector.infocard.InfocardHistory;
import com.awl.fc2.selector.infocard.InfocardUseEvent;
import com.awl.fc2.selector.query.ClaimsQuery;
import com.awl.fc2.selector.query.CompatibleInfoCards;
import com.awl.fc2.selector.session.SessionSelector;
import com.awl.fc2.selector.storage.ICardStoreV2;
import com.awl.fc2.selector.storage.ICredentialsStore;
import com.awl.logger.Logger;
import com.utils.Base64;

public class LocalCardStore implements ICardStoreV2 {
	static Logger log = new Logger(LocalCardStore.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
	LocalCardStore_OldVersion store = new LocalCardStore_OldVersion();
	public void shouldThrowException(String reason){
		JOptionPane.showConfirmDialog(null, reason);
	}
	public void notImplemented(){
		JOptionPane.showConfirmDialog(null, "NOT IMPLEMENTED");
	}
	
	@Override
	public void addInfoCard(InfoCard cardToAdd, boolean onlyMemory) {
		if(onlyMemory){
			store.addInfoCard(cardToAdd);
		}else{
			notImplemented();
			store.addInfoCard(cardToAdd);
			String xml;
			try {
				xml = cardToAdd.toXML();
				store.commit(cardToAdd.getCardId(), Base64.encode(xml.getBytes()));
			} catch (SerializationException e) {
				trace("Impossible to transform the infocard into a String XML Version");
			}			
			
		}

	}

	@Override
	public void editNameManagedInfoCard(String cardId, String name) {
		notImplemented();

	}

	@Override
	public void editPersonalInfoCard(String cardId, InfoCard cardToUpdate) {
		notImplemented();

	}

	@Override
	public String exportCrds(Vector<String> veccardIDs) {
		notImplemented();
		return "CRDS....";
	}

	@Override
	public Vector<InfoCard> getAllInfoCards() {
		return store.listAllCards(0);
	}

	@Override
	public Vector<CompatibleInfoCards> getCompatibleInfoCards(ClaimsQuery query) {
		
		Vector<InfoCard> lstCards = store.getCompatibleCards(query);
		if(lstCards.size()== 0){
			trace("No single card found, try to find a set");
			Vector<ClaimsQuery> vecQuery = new Vector<ClaimsQuery>();
			vecQuery.add(query);
			Vector<CompatibleInfoCards> set = store.getSetCompatibleCards(vecQuery);
			trace("find : " + set);
			return set;
//			if(set.size()>0){
//				return set;
//			}else{
//				return resQuery;
//			}
		}else{
			Vector<CompatibleInfoCards> resQuery = new Vector<CompatibleInfoCards>();
			for(InfoCard card : lstCards){
				CompatibleInfoCards resp = new CompatibleInfoCards();
				resp.addInfoCard(card);
				resQuery.add(resp);
			}
			return resQuery;
			
		}
	}

	@Override
	public InfoCard getInfoCard(String cardId) {
		Vector<InfoCard> lstCards = store.listAllCards(0);
		for(InfoCard card:lstCards){
			if(card.getCardId().equalsIgnoreCase(cardId)){
				return card;
			}
		}
		shouldThrowException("getInfoCard(): No card found");
		return null;
	}

	@Override
	public InfocardHistory getInfoCardHistory(String cardId) {
		notImplemented();
		return null;
	}

	@Override
	public Vector<InfoCard> getInfoCardsList() {
		// TODO Auto-generated method stub
		return store.listAllCards(0);
	}

	@Override
	public void importCrds(String crds) {
		notImplemented();

	}

	@Override
	public void removeInfoCard(String cardId) {
		store.removeInfoCard(cardId);

	}

	@Override
	public void updateInfoCardHistory(String cardId, InfocardUseEvent event) {
		notImplemented();

	}

	@Override
	public void configure(SessionSelector theSession) {
		store.configure(theSession);

	}

	@Override
	public void destroy() {
		store.destroy();

	}

	@Override
	public void reset() throws CardStore_Execption_FailedRetrieving,
			CredentialStore_Execption_FailedRetrieving {
		store.reset();

	}
	CredentialStore credStore = null;
	@Override
	public ICredentialsStore getCredentialStore() {
		if(credStore == null) credStore = new CredentialStore();
		return credStore;
	}

}
