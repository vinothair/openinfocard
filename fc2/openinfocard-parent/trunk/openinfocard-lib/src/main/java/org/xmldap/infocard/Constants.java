/*
 * Copyright (c) 2007, Axel Nennker 
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
package org.xmldap.infocard;

public class Constants {
//	public static final String MS_NAMESPACE_PREFIX = "http://schemas.microsoft.com/ws/2005/05/identity/claims/";

	public static final String IC_NAMESPACE_PREFIX = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/";
	public static final String IC_NAMESPACE = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims";

    public static final String IC_GIVENNAME 						= "givenname";
    public static final String IC_SURNAME 							= "surname";
    public static final String IC_EMAILADDRESS 						= "emailaddress";
    public static final String IC_STREETADDRESS						= "streetaddress";
    public static final String IC_LOCALITY							= "locality";
    public static final String IC_STATEORPROVINCE					= "stateorprovince";
    public static final String IC_POSTALCODE						= "postalcode";
    public static final String IC_COUNTRY							= "country";
    public static final String IC_HOMEPHONE							= "homephone";
    public static final String IC_OTHERPHONE						= "otherphone";
    public static final String IC_MOBILEPHONE						= "mobilephone";
    public static final String IC_DATEOFBIRTH						= "dateofbirth";
    public static final String IC_GENDER							= "gender";
    public static final String IC_WEBPAGE							= "webpage";
    
//    public static final String IC_STREETADDRESS 						= "streetaddress";
//    public static final String IC_NS_STREETADDRESS 						= IC_NAMESPACE_PREFIX + IC_STREETADDRESS;

    public static final String IC_PRIVATEPERSONALIDENTIFIER 		= "privatepersonalidentifier";
    
    public static final String IC_NS_GIVENNAME 						= IC_NAMESPACE_PREFIX + IC_GIVENNAME;
    public static final String IC_NS_SURNAME 						= IC_NAMESPACE_PREFIX + IC_SURNAME;
    public static final String IC_NS_EMAILADDRESS 					= IC_NAMESPACE_PREFIX + IC_EMAILADDRESS;
    public static final String IC_NS_STREETADDRESS					= IC_NAMESPACE_PREFIX + IC_STREETADDRESS;
    public static final String IC_NS_LOCALITY						= IC_NAMESPACE_PREFIX + IC_LOCALITY;
    public static final String IC_NS_STATEORPROVINCE				= IC_NAMESPACE_PREFIX + IC_STATEORPROVINCE;
    public static final String IC_NS_POSTALCODE						= IC_NAMESPACE_PREFIX + IC_POSTALCODE;
    public static final String IC_NS_COUNTRY						= IC_NAMESPACE_PREFIX + IC_COUNTRY;
    public static final String IC_NS_HOMEPHONE						= IC_NAMESPACE_PREFIX + IC_HOMEPHONE;
    public static final String IC_NS_OTHERPHONE						= IC_NAMESPACE_PREFIX + IC_OTHERPHONE;
    public static final String IC_NS_MOBILEPHONE					= IC_NAMESPACE_PREFIX + IC_MOBILEPHONE;
    public static final String IC_NS_DATEOFBIRTH					= IC_NAMESPACE_PREFIX + IC_DATEOFBIRTH;
    public static final String IC_NS_GENDER							= IC_NAMESPACE_PREFIX + IC_GENDER;
    public static final String IC_NS_WEBPAGE							= IC_NAMESPACE_PREFIX + IC_WEBPAGE;

    public static final String IC_NS_PRIVATEPERSONALIDENTIFIER 		= IC_NAMESPACE_PREFIX + IC_PRIVATEPERSONALIDENTIFIER;
    
    public static final String ISSUER_MICROSOFT = "http://schemas.microsoft.com/ws/2005/05/identity/issuer/self";
    public static final String ISSUER_XMLSOAP = "http://schemas.xmlsoap.org/ws/2005/05/identity/issuer/self";
    public static final String ISSUER_DEFAULT = ISSUER_XMLSOAP;
    
    public static final String LOA1_URL = "http://idmanagement.gov/icam/2009/09/imi_1.0_profile#assurancelevel1";
    public static final String LOA2_URL = "http://idmanagement.gov/icam/2009/09/imi_1.0_profile#assurancelevel2";
    public static final String LOA3_URL = "http://idmanagement.gov/icam/2009/09/imi_1.0_profile#assurancelevel3";
    public static final String LOA4_URL = "http://idmanagement.gov/icam/2009/09/imi_1.0_profile#assurancelevel4";
}
