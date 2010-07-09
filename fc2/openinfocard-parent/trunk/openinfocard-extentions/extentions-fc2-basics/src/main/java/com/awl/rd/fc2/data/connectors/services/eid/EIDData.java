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
package com.awl.rd.fc2.data.connectors.services.eid;

import com.awl.rd.fc2.data.connectors.services.dbutils.DataAccess;

public class EIDData extends DataAccess{

	public String country;
	public String cnieissuingauthority;
	public String cnieexpirydate;
	public String cnieissuingdate;
	public String height;
	public String nationality;
	public String cnienumber;
	public String locality;
	public String civility;
	public String gender;
	public String surname;
	public String givenname;
	public String dateofbirth;
	public String placeofbirth;
	public String departmentofbirth;
	public String streetaddress;
	public String postalcode;
	
	public final static String USER_STEF = "scauchie";
	public final static String USER_FJ = "fjritaine";
	public final static String USER_ROBERT = "robert";
	public static EIDData getDefaultUser(String name){
		EIDData toRet = new EIDData();
		{
//			toRet.country="";
//			toRet.cnieissuingauthority="";
//			toRet.cnieexpirydate="";
//			toRet.cnieissuingdate="";
//			toRet.height="";
//			toRet.nationality="";
//			toRet.cnienumber="";
//			toRet.locality="";
//			toRet.civility="";
//			toRet.gender="";
//			toRet.surname="";
//			toRet.givename="";
//			toRet.dateofbirth="";
//			toRet.placeofbirth="";
//			toRet.departmentofbirth="";
//			toRet.streetaddress="";
//			toRet.postalcode="";
			
		}
		if(USER_STEF.equalsIgnoreCase(name)){
			toRet.country="FR";
			toRet.cnieissuingauthority="VeryStefGov";
			toRet.cnieexpirydate="10/10/2100";
			toRet.cnieissuingdate="10/10/1542";
			toRet.height="2.12";
			toRet.nationality="FR";
			toRet.cnienumber="IDFRA000012345";
			toRet.locality="FR_FR";
			toRet.civility="M.";
			toRet.gender="M";
			toRet.surname="Cauchie";
			toRet.givenname="St�phane";
			toRet.dateofbirth="24/04/1981";
			toRet.placeofbirth="Maubeuge";
			toRet.departmentofbirth="59";
			toRet.streetaddress="dtc";
			toRet.postalcode="37380";
		}
		if(USER_FJ.equalsIgnoreCase(name)){
			toRet.country="FR";
			toRet.cnieissuingauthority="VeryStefGov";
			toRet.cnieexpirydate="10/10/2100";
			toRet.cnieissuingdate="10/10/1542";
			toRet.height="1.85";
			toRet.nationality="FR";
			toRet.cnienumber="IDFRA000012346";
			toRet.locality="FR_FR";
			toRet.civility="CHEPO";
			toRet.gender="M";
			toRet.surname="Ritaine";
			toRet.givenname="FJ";
			toRet.dateofbirth="26/12/1985";
			toRet.placeofbirth="LOMME";
			toRet.departmentofbirth="59";
			toRet.streetaddress="LILLE";
			toRet.postalcode="59000";
		}
		if(USER_ROBERT.equalsIgnoreCase(name)){
			toRet.country="BEL";
			toRet.cnieissuingauthority="CertiPost";
			toRet.cnieexpirydate="24/04/2012";
			toRet.cnieissuingdate="24/04/2007";
			toRet.height="1.60";
			toRet.nationality="BEL";
			toRet.cnienumber="IDBEL000000332";
			toRet.locality="BE";
			toRet.civility="M.";
			toRet.gender="M";
			toRet.surname="Robert";
			toRet.givenname="Vandenbergh";
			toRet.dateofbirth="21/01/1971";
			toRet.placeofbirth="Ottignies Louvain-la-neuve";
			toRet.departmentofbirth="13";
			toRet.streetaddress="42 Avenue Théodore Schwann";
			toRet.postalcode="1348";
		}
		return toRet;
	}
	
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCnieissuingauthority() {
		return cnieissuingauthority;
	}

	public void setCnieissuingauthority(String cnieissuingauthority) {
		this.cnieissuingauthority = cnieissuingauthority;
	}

	public String getCnieexpirydate() {
		return cnieexpirydate;
	}

	public void setCnieexpirydate(String cnieexpirydate) {
		this.cnieexpirydate = cnieexpirydate;
	}

	public String getCnieissuingdate() {
		return cnieissuingdate;
	}

	public void setCnieissuingdate(String cnieissuingdate) {
		this.cnieissuingdate = cnieissuingdate;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getCnienumber() {
		return cnienumber;
	}

	public void setCnienumber(String cnienumber) {
		this.cnienumber = cnienumber;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
