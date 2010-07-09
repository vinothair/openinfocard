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
 *     * The names of the contributors may NOT be used to endorse or promote products
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
 * 
 */
package com.awl.rd.fc2.data.connectors.services.telcos;

import com.awl.rd.fc2.data.connectors.services.dbutils.DataAccess;

public class TelcosData extends DataAccess{
	
	public String numberofsms;
	public String detailsoflastemails;
	public String numberofunreademails;
	public String numberofemails;
	public String facade;
	public String workemailaddress;
	public String workmobilephone;
	public String workfax;
	public String workcountry;
	public String worklocality;
	public String workpostalcode;
	public String homefax;
	public String language;
	public String pseudo;
	public String privatepersonalidentifier;
	public String otherphone;
	public String emailaddress;
	public String mobilephone;
	public String homephone;
	public String country;
	public String gender;
	public String surname;
	public String givenname;
	public String dateofbirth;
	public String streetaddress;
	public String postalcode;
	public String locality;

	
	public final static String USER_STEF = "scauchie";
	public final static String USER_FJ = "fjritaine";
	public final static String USER_ROBERT = "robert";
	
	public static TelcosData getDefaultUser(String name) {
		TelcosData toRet = new TelcosData();

		if (USER_STEF.equalsIgnoreCase(name)) {
			toRet.numberofsms = "";
			toRet.detailsoflastemails = "";
			toRet.numberofunreademails = "";
			toRet.numberofemails = "";
			toRet.facade = "";
			toRet.workemailaddress = "s.cauchie@atos.fr";
			toRet.workmobilephone = "0611445566";
			toRet.workfax = "";
			toRet.workcountry = "France";
			toRet.worklocality = "Blois";
			toRet.workpostalcode = "41000";
			toRet.homefax = "";
			toRet.language = "FR";
			toRet.pseudo = "stef";
			toRet.privatepersonalidentifier = "";
			toRet.otherphone = "";
			toRet.emailaddress = "stef@stef.fr";
			toRet.mobilephone = "0606112233";
			toRet.homephone = "0247112233";
			toRet.country="FR";
			toRet.gender="M";
			toRet.surname="Cauchie";
			toRet.givenname="St�phane";
			toRet.dateofbirth="24/04/1981";
		}
		if (USER_FJ.equalsIgnoreCase(name)) {
			toRet.numberofsms = "";
			toRet.detailsoflastemails = "";
			toRet.numberofunreademails = "";
			toRet.numberofemails = "";
			toRet.facade = "";
			toRet.workemailaddress = "fj.ritaine@atos.fr";
			toRet.workmobilephone = "0699887766";
			toRet.workfax = "";
			toRet.workcountry = "France";
			toRet.worklocality = "Seclin";
			toRet.workpostalcode = "59113";
			toRet.homefax = "";
			toRet.language = "FR";
			toRet.pseudo = "fjritaine";
			toRet.privatepersonalidentifier = "";
			toRet.otherphone = "";
			toRet.emailaddress = "fj@fj.com";
			toRet.mobilephone = "0618778899";
			toRet.homephone = "0321778899";
			toRet.country="FR";
			toRet.gender="M";
			toRet.surname="Ritaine";
			toRet.givenname="FJ";
			toRet.dateofbirth="26/12/1985";
		}
		if (USER_ROBERT.equalsIgnoreCase(name)) {
			toRet.numberofsms = "";
			toRet.detailsoflastemails = "";
			toRet.numberofunreademails = "";
			toRet.numberofemails = "";
			toRet.facade = "";
			toRet.workemailaddress = "";
			toRet.workmobilephone = "0698754312";
			toRet.workfax = "";
			toRet.workcountry = "Bel";
			toRet.worklocality = "Koekelare";
			toRet.workpostalcode = "8680";
			toRet.homefax = "";
			toRet.language = "BE";
			toRet.pseudo = "rob";
			toRet.privatepersonalidentifier = "";
			toRet.otherphone = "";
			toRet.emailaddress = "robert.vandenbergh@test.fr";
			toRet.mobilephone = "0612398756";
			toRet.homephone = "0412398745";
			toRet.country="BEL";
			toRet.gender="M";
			toRet.surname="Robert";
			toRet.givenname="Vandenbergh";
			toRet.dateofbirth="21/01/1971";
		}
		return toRet;

	}
	
	public String getNumberofsms() {
		return numberofsms;
	}
	public void setNumberofsms(String numberofsms) {
		this.numberofsms = numberofsms;
	}
	public String getDetailsoflastemails() {
		return detailsoflastemails;
	}
	public void setDetailsoflastemails(String detailsoflastemails) {
		this.detailsoflastemails = detailsoflastemails;
	}
	public String getNumberofunreademails() {
		return numberofunreademails;
	}
	public void setNumberofunreademails(String numberofunreademails) {
		this.numberofunreademails = numberofunreademails;
	}
	public String getNumberofemails() {
		return numberofemails;
	}
	public void setNumberofemails(String numberofemails) {
		this.numberofemails = numberofemails;
	}
	public String getFacade() {
		return facade;
	}
	public void setFacade(String facade) {
		this.facade = facade;
	}
	public String getWorkemailaddress() {
		return workemailaddress;
	}
	public void setWorkemailaddress(String workemailaddress) {
		this.workemailaddress = workemailaddress;
	}
	public String getWorkmobilephone() {
		return workmobilephone;
	}
	public void setWorkmobilephone(String workmobilephone) {
		this.workmobilephone = workmobilephone;
	}
	public String getWorkfax() {
		return workfax;
	}
	public void setWorkfax(String workfax) {
		this.workfax = workfax;
	}
	public String getWorkcountry() {
		return workcountry;
	}
	public void setWorkcountry(String workcountry) {
		this.workcountry = workcountry;
	}
	public String getWorklocality() {
		return worklocality;
	}
	public void setWorklocality(String worklocality) {
		this.worklocality = worklocality;
	}
	public String getWorkpostalcode() {
		return workpostalcode;
	}
	public void setWorkpostalcode(String workpostalcode) {
		this.workpostalcode = workpostalcode;
	}
	public String getHomefax() {
		return homefax;
	}
	public void setHomefax(String homefax) {
		this.homefax = homefax;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public String getPrivatepersonalidentifier() {
		return privatepersonalidentifier;
	}
	public void setPrivatepersonalidentifier(String privatepersonalidentifier) {
		this.privatepersonalidentifier = privatepersonalidentifier;
	}
	public String getOtherphone() {
		return otherphone;
	}
	public void setOtherphone(String otherphone) {
		this.otherphone = otherphone;
	}
	public String getEmailaddress() {
		return emailaddress;
	}
	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}
	public String getMobilephone() {
		return mobilephone;
	}
	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}
	public String getHomephone() {
		return homephone;
	}
	public void setHomephone(String homephone) {
		this.homephone = homephone;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getGivenname() {
		return givenname;
	}
	public void setGivenname(String givenname) {
		this.givenname = givenname;
	}
	public String getDateofbirth() {
		return dateofbirth;
	}
	public void setDateofbirth(String dateofbirth) {
		this.dateofbirth = dateofbirth;
	}
	public String getStreetaddress() {
		return streetaddress;
	}
	public void setStreetaddress(String streetaddress) {
		this.streetaddress = streetaddress;
	}
	public String getPostalcode() {
		return postalcode;
	}
	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	
}
