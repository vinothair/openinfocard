package com.awl.fc2.plugin.preprocessing.cardclaims;

import java.util.Vector;

import org.xmldap.infocard.InfoCard;

import com.awl.fc2.plugin.preprocessing.IPlugInPreProcess;
import com.awl.fc2.selector.Selector;
import com.awl.fc2.selector.exceptions.Config_Exception_NotDone;
import com.awl.fc2.selector.exceptions.Config_Exeception_MalFormedConfigFile;
import com.awl.fc2.selector.exceptions.Config_Exeception_UnableToReadConfigFile;
import com.awl.fc2.selector.launcher.Config;
import com.awl.fc2.selector.session.SessionSelector;
import com.awl.fc2.selector.storage.utils.Utils;
import com.awl.logger.Logger;
import com.awl.rd.fc2.claims.CardsSupportedClaims;

public class PlugInPreProcess_AddCard implements IPlugInPreProcess {

	static Logger log = new Logger(PlugInPreProcess_AddCard.class);
	public static void trace(Object msg){
		log.trace(msg);
	}
	@Override
	public String getCertificateB64() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<String> getOptionalClaims() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<String> getRequiredClaims() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURLRequestor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequestModified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean process(Vector<String> lstRequiredClaims,
			Vector<String> lstOptionalClaims, String urlRequestor,
			String certifRequestorB64) {
		SessionSelector session;
		
		try {
			session = Selector.getInstance().session;
			String cardId = null;
			String pwd = null;
			for(String claim:lstRequiredClaims){
				trace("RP try to put a card");
				int dyn = claim.indexOf("?");				
				String dynamicPart = claim.substring(dyn+1);
				if(claim.contains(CardsSupportedClaims.pwdCRDO.uri)){
					String tab[] = dynamicPart.split("--");
					if(tab.length==2){
						cardId = tab[0];
						pwd = tab[1];
					}						
				}
				if(claim.contains(CardsSupportedClaims.listCardIdO.uri)){
					InfoCard cardToAdd = Utils.CRDB64ToInfocar(dynamicPart);
					if(cardToAdd != null){
						//SINCE V2
						session.getCardStore().addInfoCard(cardToAdd,true);
						//session.getCardStore().addInfoCard(cardToAdd);
					}
					//cardId = cardToAdd.getCardId();						
				}
				
			}
			if(cardId != null && pwd != null){
				trace("RP will send the pwd to the credential store");
				trace("JSS will put be configure with it");
				trace("CARD ID = "+  cardId);
				trace("PWD     = "+ pwd);
				session.getCredentialStore().addPwdForCardId(cardId, pwd);
			}
		} catch (Config_Exeception_UnableToReadConfigFile e) {

		} catch (Config_Exeception_MalFormedConfigFile e) {

		} catch (Config_Exception_NotDone e) {

		}
		
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	@Override
	public String getType() {
		return PLG_PREPROCESS;
	}

	@Override
	public void install(Config cnf) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public int getPriority() {
		
		return 0;
	}
	@Override
	public void uninstall() {
		// TODO Auto-generated method stub
		
	}

}
