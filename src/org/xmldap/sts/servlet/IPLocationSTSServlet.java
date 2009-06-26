package org.xmldap.sts.servlet;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Locale;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.XPathContext;

import org.xmldap.exceptions.CryptoException;
import org.xmldap.exceptions.KeyStoreException;
import org.xmldap.exceptions.ParsingException;
import org.xmldap.exceptions.TokenIssuanceException;
import org.xmldap.sts.db.CardStorage;
import org.xmldap.sts.db.ManagedCard;
import org.xmldap.sts.db.SupportedClaims;
import org.xmldap.sts.db.impl.CardStorageEmbeddedDBImpl;
import org.xmldap.util.Bag;
import org.xmldap.util.KeystoreUtil;
import org.xmldap.util.PropertiesManager;

import com.telekom.developer.Environment;
import com.telekom.developer.iplocation.clientsdk.IPLocationClient;
import com.telekom.developer.iplocation.model.City;
import com.telekom.developer.iplocation.model.GeoCoordinates;
import com.telekom.developer.iplocation.model.IPAddress;
import com.telekom.developer.iplocation.model.IPAddressLocation;
import com.telekom.developer.iplocation.model.IPAddressTypeEnum;
import com.telekom.developer.iplocation.model.LocateIPResponse;
import com.telekom.developer.iplocation.model.Region;

public class IPLocationSTSServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger log = Logger.getLogger("org.xmldap.sts.servlet.IPLocationSTSServlet");

    RSAPrivateKey key = null;
    X509Certificate cert = null;
    String domain = null;
    String issuerName = null;
    private String servletPath = null;

    CardStorage storage = null;
    SupportedClaims supportedClaimsImpl = null;

    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        try {
//        	 System.setProperty("javax.xml.soap.MessageFactory",
//							"com.sun.xml.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl");
//			System.setProperty("javax.xml.soap.SOAPFactory",
//							"com.sun.xml.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl");
//			System.setProperty("javax.xml.soap.SOAPConnectionFactory",
//							"com.sun.xml.messaging.saaj.client.p2p.HttpSOAPConnectionFactory");
//			System.setProperty("javax.xml.soap.MetaFactory",
//					"com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl"); 

//        	System.setProperty("http.proxyHost", "www-ab.dienste.telekom.de");
//        	System.setProperty("http.proxyPort", "8080");

        } catch (Exception e) {
        	throw new ServletException("error setting SAAJ system properies", e);
        }
        
        try {

            PropertiesManager properties = new PropertiesManager(PropertiesManager.SECURITY_TOKEN_SERVICE, config.getServletContext());
            String keystorePath = properties.getProperty("keystore");
            if (keystorePath == null) {
            	throw new ServletException("keystorePath is null");
            }
            String keystorePassword = properties.getProperty("keystore.password");
            if (keystorePassword == null) {
               	throw new ServletException("keystore.password is null");
            }
            String keyname = properties.getProperty("key.name");
            if (keyname == null) {
            	throw new ServletException("key.name is null");
            }
            String keypassword = properties.getProperty("key.password");
            if (keypassword == null) {
            	throw new ServletException("key.password is null");
            }
            domain = properties.getProperty("domain");
            if (domain == null) {
            	throw new ServletException("domainname is null");
            }
            servletPath = properties.getProperty("servletPath");
            if (servletPath == null) {
            	throw new ServletException("servletPath is null");
            }
            issuerName = properties.getProperty("issuerName");
            if (issuerName == null) {
            	issuerName = "https://" + domain + servletPath;
            }

            String supportedClaimsClass = properties.getProperty("supportedClaimsClass");
            if (supportedClaimsClass == null) {
            	throw new ServletException("supportedClaimsClass is null");
            }
            supportedClaimsImpl = SupportedClaims.getInstance(supportedClaimsClass);
            if (supportedClaimsImpl == null) {
            	throw new ServletException("supportedClaimsImpl is null");
            }
            storage = new CardStorageEmbeddedDBImpl(supportedClaimsImpl);
            if (supportedClaimsClass == null) {
            	throw new ServletException("storage is null");
            }
            
            KeystoreUtil keystore = new KeystoreUtil(keystorePath, keystorePassword);
            cert = keystore.getCertificate(keyname);

            key = (RSAPrivateKey) keystore.getPrivateKey(keyname,keypassword);


        } catch (IOException e) {
            throw new ServletException(e);
        } catch (KeyStoreException e) {
            throw new ServletException(e);
        } catch (InstantiationException e) {
        	throw new ServletException(e);
		} catch (IllegalAccessException e) {
			throw new ServletException(e);
		} catch (ClassNotFoundException e) {
			throw new ServletException(e);
    }

        storage.startup();

    }

    // test only. Remove in production
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String remoteAddress = null;
        {
        	remoteAddress = request.getHeader("X-FORWARDED-FOR");
        	if (remoteAddress == null  || remoteAddress.trim().length() < 7) {
            	remoteAddress = request.getRemoteAddr();
        	}
        }
        if (remoteAddress == null  || remoteAddress.trim().length() < 7) {
        	response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, 
        			"failed to determine remote address");
        	return;
    	}
        
        remoteAddress = "84.191.68.83"; // TODO FIXME
        System.out.println("IPLocation doGet for: " + remoteAddress); System.out.flush();
        log.fine("IPLocation doGet for: " + remoteAddress);
        
        try {
        	IPLocationClient client = new IPLocationClient(
        		       "ignisvulpis","dvlprkm45l",Environment.PRODUCTION);
        	LocateIPResponse result =
                client.locateIP(new IPAddress(IPAddressTypeEnum.IP_V4,
                		remoteAddress));
        	if ("0000".equals(result.getStatusCode())) {
        		IPAddressLocation[] locations = result.getIPAdressLocation();
        	      if (locations.length == 0) {
        	    	  return;
        	      }
        	      IPAddressLocation location = locations[0];
        	      PrintWriter out = response.getWriter();
        	      out.println("=== Location [0] ===");
        	      out.println("\tstatus code = " + location.getStatusCode());
        	      out.println("\tstatus message = " + location.getStatusMessage());
        	      out.println("\tIP-Type = " + location.getIpType());
        	      out.println("\tIP-Address  = " + location.getAddress());
        	      out.println("\tradius = " + location.getRadius());

        	      if (location.getIsInCity() == null)
        	      {
        	        System.out.println("No city information avaiable.");
        	      }
        	      else
        	      {
        	        City city = location.getIsInCity();
        	        out.println("City : ");
        	        out.println("\tcity = " + city.getCity());
        	        out.println("\tcity code = " + city.getCityCode());
        	        out.println("\tcountry code = " + city.getCountryCode());
//        	        card.setClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:country", city.getCountryCode());
//        	        card.setClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:A3", city.getCity());
        	      }

        	      if (location.getIsInGeo() == null)
        	      {
        	        out.println("No geo coordinates avaiable.");
        	      }
        	      else
        	      {
        	        GeoCoordinates geo = location.getIsInGeo();
        	        out.println("Geo coordinates : ");
        	        out.println("\tLatitude = " + geo.getGeoLatitude());
        	        out.println("\tLongitude = " + geo.getGeoLongitude());
//        	        card.setClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:LOC", 
//        	        		"Latitude="+geo.getGeoLatitude()+" tLongitude="+geo.getGeoLongitude());       	      
        	       }

        	      if (location.getIsInRegion() == null)
        	      {
        	        out.println("No Region information avaiable.");
        	      }
        	      else
        	      {
        	        Region region = location.getIsInRegion();
        	        out.println("Region information : ");
        	        out.println("\tcountry code = " + region.getCountryCode());
        	        out.println("\tregion code = " + region.getRegionCode());
        	        out.println("\tregion name = " + region.getRegionName());
//        	        card.setClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:country", region.getCountryCode());
//        	        card.setClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:A2", region.getRegionName());
        	      }

        	} else {
        		response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, 
    				result.getStatusMessage() + " Address: " + remoteAddress);
        		return;
        	}
		} catch (ServiceException e1) {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, 
    				e1.getMessage() + "\nAddress: " + remoteAddress);
        		return;
		}
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	HttpSession session = request.getSession();
    	session.getServletContext().log("STS Servlet got a request");
    	System.out.println("Boink"); System.out.flush();
    	
    	log.finest("STS got a request");
        int contentLen = request.getContentLength();

        String requestXML = null;
        if (contentLen > 0) {

            DataInputStream inStream = new DataInputStream(request.getInputStream());
            byte[] buf = new byte[contentLen];
            inStream.readFully(buf);
            requestXML = new String(buf);

            System.out.println("STS Request:\n" + requestXML);

        }

        //let's make a doc
        Builder parser = new Builder();
        Document req = null;
        try {
            req = parser.build(requestXML, "");
        } catch (nu.xom.ParsingException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        XPathContext context = Utils.buildRSTcontext();

        log.finest("We have a doc");

        boolean isUser = false;
		try {
			isUser = Utils.authenticate(storage, req, context);
		} catch (ParsingException e) {
			log.fine("authentication failed");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "authentication failed\n\n" + e.getMessage());
			return;
		}
        
        //TODO = SOAPFaulr
        if (!isUser) {
        	response.sendError(HttpServletResponse .SC_UNAUTHORIZED);
        	return;
        }

        String requestURL = request.getRequestURL().toString();
        String servletPath = request.getServletPath();
        int i = requestURL.indexOf(servletPath);
        String prefix = requestURL.substring(0, i);
        Bag requestElements = null;
        try {
            Nodes rsts = req.query("//wst:RequestSecurityToken",context);
            Element rst = (Element) rsts.get(0);
            log.finest("RST: " + rst.toXML());
            requestElements = Utils.parseRequest(rst, prefix);
        } catch (ParsingException e) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            e.printStackTrace();
            //TODO - SOAP Fault
            return;
        }

        String relyingPartyURL = null;
        String relyingPartyCertB64 = null;
        try {
			Bag appliesToBag = Utils.parseAppliesTo(req, context);
			relyingPartyURL = (String) appliesToBag.get("relyingPartyURL");
			relyingPartyCertB64 = (String) appliesToBag.get("relyingPartyCertB64");
		} catch (ParsingException e) {
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            e.printStackTrace();
            //TODO - SOAP Fault
            return;
		}
		
        ManagedCard card = storage.getCard((String)requestElements.get("cardId"));
        if (card == null ) throw new IOException("Unable to read card: " + (String)requestElements.get("cardId"));

        String remoteAddress = null;
        {
        	remoteAddress = request.getHeader("X-FORWARDED-FOR");
        	if (remoteAddress == null  || remoteAddress.trim().length() < 7) {
            	remoteAddress = request.getRemoteAddr();
        	}
        }
        if (remoteAddress == null  || remoteAddress.trim().length() < 7) {
        	response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, 
        			"failed to determine remote address");
        	return;
    	}
        
        System.out.println("IPLocation doPost for: " + remoteAddress); System.out.flush();
        log.fine("IPLocation doPost for: " + remoteAddress);

        try {
        	remoteAddress = "84.191.68.83"; // TODO FIXME
        	IPLocationClient client = new IPLocationClient(
        		       "ignisvulpis","dvlprkm45l",Environment.PRODUCTION);
        	LocateIPResponse result =
                client.locateIP(new IPAddress(IPAddressTypeEnum.IP_V4,
                		remoteAddress));
        	if ("0000".equals(result.getStatusCode())) {
        		IPAddressLocation[] locations = result.getIPAdressLocation();
        	      if (locations.length == 0) {
        	    	  return;
        	      }
        	      IPAddressLocation location = locations[0];
        	      System.out.println("=== Location [0] ===");
        	      System.out.println("\tstatus code = " + location.getStatusCode());
        	      System.out.println("\tstatus message = " + location.getStatusMessage());
        	      System.out.println("\tIP-Type = " + location.getIpType());
        	      System.out.println("\tIP-Address  = " + location.getAddress());
        	      System.out.println("\tradius = " + location.getRadius());

        	      if (location.getIsInCity() == null)
        	      {
        	        System.out.println("No city information avaiable.");
        	      }
        	      else
        	      {
        	        City city = location.getIsInCity();
        	        System.out.println("City : ");
        	        System.out.println("\tcity = " + city.getCity());
        	        System.out.println("\tcity code = " + city.getCityCode());
        	        System.out.println("\tcountry code = " + city.getCountryCode());
        	        card.setClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:country", city.getCountryCode());
        	        card.setClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:A3", city.getCity());
        	      }

        	      if (location.getIsInGeo() == null)
        	      {
        	        System.out.println("No geo coordinates avaiable.");
        	      }
        	      else
        	      {
        	        GeoCoordinates geo = location.getIsInGeo();
        	        System.out.println("Geo coordinates : ");
        	        System.out.println("\tLatitude = " + geo.getGeoLatitude());
        	        System.out.println("\tLongitude = " + geo.getGeoLongitude());
        	        card.setClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:LOC", 
        	        		"Latitude="+geo.getGeoLatitude()+" tLongitude="+geo.getGeoLongitude());       	      
        	       }

        	      if (location.getIsInRegion() == null)
        	      {
        	        System.out.println("No Region information avaiable.");
        	      }
        	      else
        	      {
        	        Region region = location.getIsInRegion();
        	        System.out.println("Region information : ");
        	        System.out.println("\tcountry code = " + region.getCountryCode());
        	        System.out.println("\tregion code = " + region.getRegionCode());
        	        System.out.println("\tregion name = " + region.getRegionName());
        	        card.setClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:country", region.getCountryCode());
        	        card.setClaim("urn:ietf:params:xml:ns:pidf:geopriv10:civicLoc:A2", region.getRegionName());
        	      }

        	} else {
        		response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, 
    				result.getStatusMessage() + "\nADDRESS: " + remoteAddress);
        		return;
        	}
		} catch (ServiceException e1) {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, 
    				e1.getMessage() + " ADDRESS: " + remoteAddress);
        		return;
		}
        
        System.out.println("STS Issuing Managed Card " + (String)requestElements.get("cardId") + " for " + card.getClaim(org.xmldap.infocard.Constants.IC_NS_EMAILADDRESS));

        Locale clientLocale = request.getLocale();
        String stsResponse = "";
		try {
			stsResponse = Utils.issue(
					card, requestElements, clientLocale, cert, key, 
					issuerName, supportedClaimsImpl, relyingPartyURL, relyingPartyCertB64);
		} catch (CryptoException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            e.printStackTrace();
            //TODO - SOAP Fault
            return;
		} catch (TokenIssuanceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        response.setContentType("application/soap+xml; charset=\"utf-8\"");
        response.setContentLength(stsResponse.length());
        log.finest("STS Response:\n " + stsResponse);
        PrintWriter out = response.getWriter();
        out.println(stsResponse);
        out.flush();
        out.close();

    }

}
