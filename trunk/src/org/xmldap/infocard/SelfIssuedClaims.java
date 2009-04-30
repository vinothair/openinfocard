package org.xmldap.infocard;

public class SelfIssuedClaims implements Comparable<SelfIssuedClaims> {
	private String givenName;

	private String surname;

	private String emailAddress;

	private String streetAddress;

	private String locality;
	
	private String webpage;

	private String stateOrProvince;

	private String postalCode;

	private String country;

	private String primaryPhone;

	private String otherPhone;

	private String mobilePhone;

	private String dateOfBirth;

	private String privatePersonalIdentifier;

	private String gender;

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getSurname() {
		return surname;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public void setWebPage(String webpage) {
		this.webpage = webpage;
	}
	
	public String getWebPage() {
		return webpage;
	}
	
	public String getLocality() {
		return locality;
	}

	public void setStateOrProvince(String stateOrProvince) {
		this.stateOrProvince = stateOrProvince;
	}

	public String getStateOrProvince() {
		return stateOrProvince;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountry() {
		return country;
	}

	public void setPrimaryPhone(String primaryPhone) {
		this.primaryPhone = primaryPhone;
	}

	public String getPrimaryPhone() {
		return primaryPhone;
	}

	public void setOtherPhone(String otherPhone) {
		this.otherPhone = otherPhone;
	}

	public String getOtherPhone() {
		return otherPhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setPrivatePersonalIdentifier(String privatePersonalIdentifier) {
		this.privatePersonalIdentifier = privatePersonalIdentifier;
	}

	public String getPrivatePersonalIdentifier() {
		return privatePersonalIdentifier;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGender() {
		return gender;
	}

	@Override
	public int compareTo(SelfIssuedClaims o) {
		int comparison;
		
		if (surname != null) {
			comparison = surname.compareTo(o.surname);
			if (comparison != 0) return comparison;
		} else {
			if (o.surname != null) return -1;
		}
		
		if (givenName != null) {
			comparison = givenName.compareTo(o.givenName);
			if (comparison != 0) return comparison;
		} else {
			if (o.givenName != null) return -1;
		}

		if (country != null) {
			comparison = country.compareTo(o.country);
			if (comparison != 0) return comparison;
		} else {
			if (o.country != null) return -1;
		}

		if (stateOrProvince != null) {
			comparison = stateOrProvince.compareTo(o.stateOrProvince);
			if (comparison != 0) return comparison;
		} else {
			if (o.stateOrProvince != null) return -1;
		}

		if (locality != null) {
			comparison = locality.compareTo(o.locality);
			if (comparison != 0) return comparison;
		} else {
			if (o.locality != null) return -1;
		}

		if (postalCode != null) {
			comparison = postalCode.compareTo(o.postalCode);
			if (comparison != 0) return comparison;
		} else {
			if (o.postalCode != null) return -1;
		}

		if (streetAddress != null) {
			comparison = streetAddress.compareTo(o.streetAddress);
			if (comparison != 0) return comparison;
		} else {
			if (o.streetAddress != null) return -1;
		}

		if (emailAddress != null) {
			comparison = emailAddress.compareTo(o.emailAddress);
			if (comparison != 0) return comparison;
		} else {
			if (o.emailAddress != null) return -1;
		}

		if (primaryPhone != null) {
			comparison = primaryPhone.compareTo(o.primaryPhone);
			if (comparison != 0) return comparison;
		} else {
			if (o.primaryPhone != null) return -1;
		}

		if (otherPhone != null) {
			comparison = otherPhone.compareTo(o.otherPhone);
			if (comparison != 0) return comparison;
		} else {
			if (o.otherPhone != null) return -1;
		}
		
		if (mobilePhone != null) {
			comparison = mobilePhone.compareTo(o.mobilePhone);
			if (comparison != 0) return comparison;
		} else {
			if (o.mobilePhone != null) return -1;
		}
		
		if (dateOfBirth != null) {
			comparison = dateOfBirth.compareTo(o.dateOfBirth);
			if (comparison != 0) return comparison;
		} else {
			if (o.dateOfBirth != null) return -1;
		}
		
		if (gender != null) {
			comparison = gender.compareTo(o.gender);
			if (comparison != 0) return comparison;
		} else {
			if (o.gender != null) return -1;
		}
		
		if (webpage != null) {
			comparison = webpage.compareTo(o.webpage);
			if (comparison != 0) return comparison;
		} else {
			if (o.webpage != null) return -1;
		}

		if (privatePersonalIdentifier != null) {
			comparison = privatePersonalIdentifier.compareTo(o.privatePersonalIdentifier);
			if (comparison != 0) return comparison;
		} else {
			if (o.privatePersonalIdentifier != null) return -1;
		}
		
		return 0;
	}

}
