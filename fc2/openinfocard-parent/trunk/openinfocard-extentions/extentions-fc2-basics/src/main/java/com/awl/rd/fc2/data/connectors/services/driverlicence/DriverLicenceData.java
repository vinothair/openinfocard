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
package com.awl.rd.fc2.data.connectors.services.driverlicence;

import com.awl.rd.fc2.data.connectors.services.dbutils.DataAccess;

public class DriverLicenceData extends DataAccess{
	public String drivinglicencesstatutes;
	public String drivinglicenceslist;
	public String drivinglicencenumber;
	public String drivinglicenceissuingplace;
	public String drivinglicenceissuingauthority;
	public String drivinglicenceissuingdate;
	public String civility;
	public String gender;
	public String surname;
	public String givenname;
	public String dateofbirth;
	public String placeofbirth;
	public String departmentofbirth;
	public String streetaddress;
	public String postalcode;
	public String locality;

	
	public final static String USER_STEF = "scauchie";
	public final static String USER_FJ = "fjritaine";
	public final static String USER_ROBERT = "robert";
	
	public static DriverLicenceData getDefaultUser(String name){
		DriverLicenceData toRet = new DriverLicenceData();
		
		if(USER_STEF.equalsIgnoreCase(name)) {
			toRet.civility="CHEPO";
			toRet.gender="M";
			toRet.surname="Cauchie";
			toRet.givenname="Stéphane";
			toRet.dateofbirth="24/04/1981";
			toRet.placeofbirth="Maubeuge";
			toRet.departmentofbirth="59";
			toRet.streetaddress="dtc";
			toRet.postalcode="37380";
			toRet.locality="FR_FR";
			toRet.drivinglicencesstatutes = "valide";
			toRet.drivinglicenceslist = "A,B";
			toRet.drivinglicencenumber = "730741102550";
			toRet.drivinglicenceissuingplace = "dtc";
			toRet.drivinglicenceissuingauthority = "Préfet";
			toRet.drivinglicenceissuingdate = "19/02/2000";
		}
		if(USER_FJ.equalsIgnoreCase(name)) {
			toRet.civility="CHEPO";
			toRet.gender="M";
			toRet.surname="Ritaine";
			toRet.givenname="FJ";
			toRet.dateofbirth="26/12/1985";
			toRet.placeofbirth="DANSLE62";
			toRet.departmentofbirth="62";
			toRet.streetaddress="LILLE";
			toRet.postalcode="59000";
			toRet.locality="FR_FR";
			toRet.drivinglicencesstatutes = "valide";
			toRet.drivinglicenceslist = "B";
			toRet.drivinglicencenumber = "730244752458";
			toRet.drivinglicenceissuingplace = "Saint-Omer";
			toRet.drivinglicenceissuingauthority = "Préfet";
			toRet.drivinglicenceissuingdate = "25/09/2004";
		}
		if(USER_ROBERT.equalsIgnoreCase(name)) {
			toRet.civility="M.";
			toRet.gender="M";
			toRet.surname="Robert";
			toRet.givenname="Vandenbergh";
			toRet.dateofbirth="21/01/1971";
			toRet.placeofbirth="Ottignies Louvain-la-neuve";
			toRet.departmentofbirth="13";
			toRet.streetaddress="42 Avenue Théodore Schwann";
			toRet.postalcode="1348";
			toRet.locality="BE";
			toRet.drivinglicencesstatutes = "valide";
			toRet.drivinglicenceslist = "B";
			toRet.drivinglicencenumber = "730242123456";
			toRet.drivinglicenceissuingplace = "Namur";
			toRet.drivinglicenceissuingauthority = "Préfet";
			toRet.drivinglicenceissuingdate = "25/05/1992";
		}
		return toRet;
	}
	
	public String getDrivinglicencesstatutes() {
		return drivinglicencesstatutes;
	}
	public void setDrivinglicencesstatutes(String drivinglicencesstatutes) {
		this.drivinglicencesstatutes = drivinglicencesstatutes;
	}
	public String getDrivinglicenceslist() {
		return drivinglicenceslist;
	}
	public void setDrivinglicenceslist(String drivinglicenceslist) {
		this.drivinglicenceslist = drivinglicenceslist;
	}
	public String getDrivinglicencenumber() {
		return drivinglicencenumber;
	}
	public void setDrivinglicencenumber(String drivinglicencenumber) {
		this.drivinglicencenumber = drivinglicencenumber;
	}
	public String getDrivinglicenceissuingplace() {
		return drivinglicenceissuingplace;
	}
	public void setDrivinglicenceissuingplace(String drivinglicenceissuingplace) {
		this.drivinglicenceissuingplace = drivinglicenceissuingplace;
	}
	public String getDrivinglicenceissuingauthority() {
		return drivinglicenceissuingauthority;
	}
	public void setDrivinglicenceissuingauthority(
			String drivinglicenceissuingauthority) {
		this.drivinglicenceissuingauthority = drivinglicenceissuingauthority;
	}
	public String getDrivinglicenceissuingdate() {
		return drivinglicenceissuingdate;
	}
	public void setDrivinglicenceissuingdate(String drivinglicenceissuingdate) {
		this.drivinglicenceissuingdate = drivinglicenceissuingdate;
	}
	public String getCivility() {
		return civility;
	}
	public void setCivility(String civility) {
		this.civility = civility;
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
	public void setGivenname(String givename) {
		this.givenname = givename;
	}
	public String getDateofbirth() {
		return dateofbirth;
	}
	public void setDateofbirth(String dateofbirth) {
		this.dateofbirth = dateofbirth;
	}
	public String getPlaceofbirth() {
		return placeofbirth;
	}
	public void setPlaceofbirth(String placeofbirth) {
		this.placeofbirth = placeofbirth;
	}
	public String getDepartmentofbirth() {
		return departmentofbirth;
	}
	public void setDepartmentofbirth(String departmentofbirth) {
		this.departmentofbirth = departmentofbirth;
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
