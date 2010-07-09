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
package com.awl.rd.fc2.data.connectors.services.rac;

import com.awl.rd.fc2.data.connectors.services.dbutils.DataAccess;

public class RACData extends DataAccess {
	public String dateofbirth;
	public String emailaddress;
	public String password;
	public String drivinglicencenumber;
	public String drivinglicenceissuingdate;
	public String locality;
	public String country;
	public String homephone;
	public String fax;
	public String RACUserID;
	public String city;
	public String civility;
	public String surname;
	public String givenname;
	public String streetaddress;
	public String postalcode;

	public String getDateofbirth() {
		return dateofbirth;
	}

	public void setDateofbirth(String dateofbirth) {
		this.dateofbirth = dateofbirth;
	}

	public String getEmailaddress() {
		return emailaddress;
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDrivinglicencenumber() {
		return drivinglicencenumber;
	}

	public void setDrivinglicencenumber(String drivinglicencenumber) {
		this.drivinglicencenumber = drivinglicencenumber;
	}

	public String getDrivinglicenceissuingdate() {
		return drivinglicenceissuingdate;
	}

	public void setDrivinglicenceissuingdate(String drivinglicenceissuingdate) {
		this.drivinglicenceissuingdate = drivinglicenceissuingdate;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHomephone() {
		return homephone;
	}

	public void setHomephone(String homephone) {
		this.homephone = homephone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getRACUserID() {
		return RACUserID;
	}

	public void setRACUserID(String rACUserID) {
		RACUserID = rACUserID;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCivility() {
		return civility;
	}

	public void setCivility(String civility) {
		this.civility = civility;
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

	public RACData(String dateofbirth, String emailaddress, String password,
			String drivinglicencenumber, String drivinglicenceissuingdate,
			String locality, String country, String homephone, String fax,
			String rACUserID, String city, String civility, String surname,
			String givenname, String streetaddress, String postalcode) {
		super();
		this.dateofbirth = dateofbirth;
		this.emailaddress = emailaddress;
		this.password = password;
		this.drivinglicencenumber = drivinglicencenumber;
		this.drivinglicenceissuingdate = drivinglicenceissuingdate;
		this.locality = locality;
		this.country = country;
		this.homephone = homephone;
		this.fax = fax;
		RACUserID = rACUserID;
		this.city = city;
		this.civility = civility;
		this.surname = surname;
		this.givenname = givenname;
		this.streetaddress = streetaddress;
		this.postalcode = postalcode;
	}

	public RACData(String _city,String _civility,String _surname,String _givenname,
				   String _streetaddress,String _postalcode) {
		city = _city;
		civility =_civility;
		surname = _surname;
		givenname = _givenname;
		postalcode = _postalcode;
	}
}
