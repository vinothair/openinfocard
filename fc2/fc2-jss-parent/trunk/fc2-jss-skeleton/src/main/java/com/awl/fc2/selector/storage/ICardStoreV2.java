/*
 * Copyright (c) 2010, Atos Worldline - http://www.atosworldline.com/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names xmldap, xmldap.org, xmldap.com nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.awl.fc2.selector.storage;

import java.util.Vector;

import org.xmldap.infocard.InfoCard;

import com.awl.fc2.selector.infocard.InfocardHistory;
import com.awl.fc2.selector.infocard.InfocardUseEvent;
import com.awl.fc2.selector.query.ClaimsQuery;
import com.awl.fc2.selector.query.CompatibleInfoCards;
import com.awl.fc2.selector.session.ISessionElement;

public interface ICardStoreV2 extends ISessionElement{

	//récupérer les Infocards compatibles avec une requête émise par un RP
	public abstract Vector<CompatibleInfoCards> getCompatibleInfoCards(ClaimsQuery query);
						 //CompatibleInfoCards
	//supprimer une Infocard du Podeca
	public abstract void removeInfoCard(String cardId);

	//ajouter une Infocard au Podeca
	public abstract void addInfoCard(InfoCard cardToAdd, boolean onlyMemory);

	// récupérer la liste des Infocards présentes dans le Podeca
	public abstract Vector<InfoCard> getInfoCardsList();

	//récupérer la totalité des Infocards du Podeca
	public abstract Vector<InfoCard> getAllInfoCards();

	//éditer le contenu d’une Infocard personnelle
	public abstract void editPersonalInfoCard(String cardId, InfoCard cardToUpdate);

	//editer le nom d’une Infocard gérée
	public abstract void editNameManagedInfoCard(String cardId, String name);

	//récupérer le contenu d’une Infocard
	public abstract InfoCard getInfoCard(String cardId);

	//récupérer l’historique d’utilisation d’une Infocard
	public abstract InfocardHistory getInfoCardHistory(String cardId);

	//ajouter une entrée dans l’historique d’utilisation d’une Infocard
	public abstract void updateInfoCardHistory(String cardId, InfocardUseEvent event);

	//exporter les Infocards du Podeca au format crds
	public abstract String exportCrds(Vector<String> veccardIDs);

	//importer des Infocards au format crds et l’ajouter à l’existant au sein du Podeca
	public abstract void importCrds(String crds);

	public ICredentialsStore getCredentialStore();
	
}
