package org.xmldap.infocard;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;

import org.xmldap.ws.WSConstants;

public class SelfIssuedClaims implements Comparable<SelfIssuedClaims> {
//  <ic:InformationCardPrivateData> ?
//  <ic:MasterKey> xs:base64Binary </ic:MasterKey>
//  <ic:ClaimValueList> ?
//    <ic:ClaimValue Uri="xs:anyURI" ...> +
//      <ic:Value> xs:string </ic:Value>
//    </ic:ClaimValue>
//  </ic:ClaimValueList>
//</ic:InformationCardPrivateData>

	Hashtable<String, String>claimValueList = new Hashtable<String, String>();
	
	public Collection<String> getKeySet() {
		return Collections.unmodifiableCollection(claimValueList.keySet());
	}

	public void setGivenName(String givenName) {
		claimValueList.put(Constants.IC_NS_GIVENNAME, givenName);
	}

	public String getGivenName() {
		return claimValueList.get(Constants.IC_NS_GIVENNAME);
	}

	public void setSurname(String surname) {
		claimValueList.put(Constants.IC_NS_SURNAME, surname);
	}

	public String getSurname() {
		return claimValueList.get(Constants.IC_NS_SURNAME);
	}

	public void setEmailAddress(String emailAddress) {
		claimValueList.put(Constants.IC_NS_EMAILADDRESS, emailAddress);
	}

	public String getEmailAddress() {
		return claimValueList.get(Constants.IC_NS_EMAILADDRESS);
	}

	public void setStreetAddress(String streetAddress) {
		claimValueList.put(Constants.IC_NS_STREETADDRESS, streetAddress);
	}

	public String getStreetAddress() {
		return claimValueList.get(Constants.IC_NS_STREETADDRESS);
	}

	public void setLocality(String locality) {
		claimValueList.put(Constants.IC_NS_LOCALITY, locality);
	}

	public String getLocality() {
		return claimValueList.get(Constants.IC_NS_LOCALITY);
	}

	public void setWebPage(String webpage) {
		claimValueList.put(Constants.IC_NS_WEBPAGE, webpage);
	}
	
	public String getWebPage() {
		return claimValueList.get(Constants.IC_NS_WEBPAGE);
	}
	
	public void setStateOrProvince(String stateOrProvince) {
		claimValueList.put(Constants.IC_NS_STATEORPROVINCE, stateOrProvince);
	}

	public String getStateOrProvince() {
		return claimValueList.get(Constants.IC_NS_STATEORPROVINCE);
	}

	public void setPostalCode(String postalCode) {
		claimValueList.put(Constants.IC_NS_POSTALCODE, postalCode);
	}

	public String getPostalCode() {
		return claimValueList.get(Constants.IC_NS_POSTALCODE);
	}

	public void setCountry(String country) {
		claimValueList.put(Constants.IC_NS_COUNTRY, country);
	}

	public String getCountry() {
		return claimValueList.get(Constants.IC_NS_COUNTRY);
	}

	public void setPrimaryPhone(String primaryPhone) {
		claimValueList.put(Constants.IC_NS_HOMEPHONE, primaryPhone);
	}

	public String getPrimaryPhone() {
		return claimValueList.get(Constants.IC_NS_HOMEPHONE);
	}

	public void setOtherPhone(String otherPhone) {
		claimValueList.put(Constants.IC_NS_OTHERPHONE, otherPhone);
	}

	public String getOtherPhone() {
		return claimValueList.get(Constants.IC_NS_OTHERPHONE);
	}

	public void setMobilePhone(String mobilePhone) {
		claimValueList.put(Constants.IC_NS_MOBILEPHONE, mobilePhone);
	}

	public String getMobilePhone() {
		return claimValueList.get(Constants.IC_NS_MOBILEPHONE);
	}

	public void setDateOfBirth(String dateOfBirth) {
		claimValueList.put(Constants.IC_NS_DATEOFBIRTH, dateOfBirth);
	}

	public String getDateOfBirth() {
		return claimValueList.get(Constants.IC_NS_DATEOFBIRTH);
	}

	public void setPrivatePersonalIdentifier(String privatePersonalIdentifier) {
		claimValueList.put(Constants.IC_NS_PRIVATEPERSONALIDENTIFIER, privatePersonalIdentifier);
	}

	public String getPrivatePersonalIdentifier() {
		return claimValueList.get(Constants.IC_NS_PRIVATEPERSONALIDENTIFIER);
	}

	public void setGender(String gender) {
		claimValueList.put(Constants.IC_NS_GENDER, gender);
	}

	public String getGender() {
		return claimValueList.get(Constants.IC_NS_GENDER);
	}

	public void setClaim(String uri, String value) {
		System.out.println("Adding claim : " + uri +"="+value);
		claimValueList.put(uri, value);
	}

	public String getClaim(String uri) {
		return claimValueList.get(uri);
	}

//  <ic:ClaimValueList> ?
//  <ic:ClaimValue Uri="xs:anyURI" ...> +
//    <ic:Value> xs:string </ic:Value>
//  </ic:ClaimValue>
//</ic:ClaimValueList>

	public String toXML() {
		StringBuffer sb = new StringBuffer();;
		for (String key : claimValueList.keySet()) {
			String value = claimValueList.get(key);
			if (value != null && !"".equals(value)) {
				sb.append("<ic:ClaimValue Uri=" + key + ">");
				sb.append(" <ic:Value>" + value + "</ic:Value>");
				sb.append("<ic:ClaimValue>");
			}
		}
		if (sb.length() > 0) {
			return "<ic:ClaimValueList xmlns:ic=\"" + WSConstants.INFOCARD_NAMESPACE + "\">"+ sb.toString() + "</ic:ClaimValueList>";
		}
		return "";
	}
	
	@Override
	public int compareTo(SelfIssuedClaims o) {
		int comparison;
		
		if (getSurname() != null) {
			comparison = getSurname().compareTo(o.getSurname());
			if (comparison != 0) return comparison;
		} else {
			if (o.getSurname() != null) return -1;
		}
		
		if (getGivenName() != null) {
			comparison = getGivenName().compareTo(o.getGivenName());
			if (comparison != 0) return comparison;
		} else {
			if (o.getGivenName() != null) return -1;
		}

		if (getCountry() != null) {
			comparison = getCountry().compareTo(o.getCountry());
			if (comparison != 0) return comparison;
		} else {
			if (o.getCountry() != null) return -1;
		}

		if (getStateOrProvince() != null) {
			comparison = getStateOrProvince().compareTo(o.getStateOrProvince());
			if (comparison != 0) return comparison;
		} else {
			if (o.getStateOrProvince() != null) return -1;
		}

		if (getLocality() != null) {
			comparison = getLocality().compareTo(o.getLocality());
			if (comparison != 0) return comparison;
		} else {
			if (o.getLocality() != null) return -1;
		}

		if (getPostalCode() != null) {
			comparison = getPostalCode().compareTo(o.getPostalCode());
			if (comparison != 0) return comparison;
		} else {
			if (o.getPostalCode() != null) return -1;
		}

		if (getStreetAddress() != null) {
			comparison = getStreetAddress().compareTo(o.getStreetAddress());
			if (comparison != 0) return comparison;
		} else {
			if (o.getStreetAddress() != null) return -1;
		}

		if (getEmailAddress() != null) {
			comparison = getEmailAddress().compareTo(o.getEmailAddress());
			if (comparison != 0) return comparison;
		} else {
			if (o.getEmailAddress() != null) return -1;
		}

		if (getPrimaryPhone() != null) {
			comparison = getPrimaryPhone().compareTo(o.getPrimaryPhone());
			if (comparison != 0) return comparison;
		} else {
			if (o.getPrimaryPhone() != null) return -1;
		}

		if (getOtherPhone() != null) {
			comparison = getOtherPhone().compareTo(o.getOtherPhone());
			if (comparison != 0) return comparison;
		} else {
			if (o.getOtherPhone() != null) return -1;
		}
		
		if (getMobilePhone() != null) {
			comparison = getMobilePhone().compareTo(o.getMobilePhone());
			if (comparison != 0) return comparison;
		} else {
			if (o.getMobilePhone() != null) return -1;
		}
		
		if (getDateOfBirth() != null) {
			comparison = getDateOfBirth().compareTo(o.getDateOfBirth());
			if (comparison != 0) return comparison;
		} else {
			if (o.getDateOfBirth() != null) return -1;
		}
		
		if (getGender() != null) {
			comparison = getGender().compareTo(o.getGender());
			if (comparison != 0) return comparison;
		} else {
			if (o.getGender() != null) return -1;
		}
		
		if (getWebPage() != null) {
			comparison = getWebPage().compareTo(o.getWebPage());
			if (comparison != 0) return comparison;
		} else {
			if (o.getWebPage() != null) return -1;
		}

		if (getPrivatePersonalIdentifier() != null) {
			comparison = getPrivatePersonalIdentifier().compareTo(o.getPrivatePersonalIdentifier());
			if (comparison != 0) return comparison;
		} else {
			if (o.getPrivatePersonalIdentifier() != null) return -1;
		}
		// TODO ??? Axel: extra claims beyond the standard ones ???
		return 0;
	}

}
