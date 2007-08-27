package org.xmldap.xmldsig;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.List;
import java.util.Vector;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.ParsingException;
import nu.xom.XPathContext;
import nu.xom.XPathException;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.exceptions.SigningException;

public class EnvelopedSignature extends ValidatingBaseEnvelopedSignature {

    protected EnvelopedSignature(KeyInfo keyInfo, PrivateKey privateKey) {
    	super(keyInfo, privateKey);
    }

    public Element sign(Element xml, String xpath, XPathContext context) throws SigningException {

        Element signThisOne = (Element) xml.copy();
        return signXML(signThisOne, xpath, context);

    }


    public Document sign(Document xml) throws SigningException {

        return sign(xml, "/*", null);

    }

    public String sign(String xml) throws SigningException {

        return sign(xml, "/*", null);

    }

    public Document sign(Document xml, String xpath, XPathContext context) throws SigningException {

        Document signThisDoc = (Document) xml.copy();
        signXML(signThisDoc.getRootElement(), xpath, context);
        return signThisDoc;

    }


    public String sign(String xml, String xpath, XPathContext context) throws SigningException {

        Document doc;
        try {
        	doc = org.xmldap.xml.XmlUtils.parse(xml);
//            doc = parse(xml);
        } catch (IOException e) {
			throw new SigningException(e);
		} catch (ParsingException e) {
			throw new SigningException(e);
		}
        Document signedDoc = sign(doc, xpath, context);
        return signedDoc.toXML();

    }


//    private Document parse(String doc) throws org.xmldap.exceptions.ParsingException {
//
//        Builder parser = new Builder();
//        Document parsedDoc;
//
//        try {
//
//            parsedDoc = parser.build(doc, "");
//
//
//        } catch (nu.xom.ParsingException e) {
//            throw new org.xmldap.exceptions.ParsingException("Unable to parse XML", e);
//        } catch (IOException e) {
//            throw new org.xmldap.exceptions.ParsingException("Unable to parse XML", e);
//        }
//
//        return parsedDoc;
//
//    }


    private Element signXML(Element xml, String xpath, XPathContext context) throws SigningException {

        //Get Reference
        Nodes nodesToReference = getNodesByXPath(xml, xpath, context);

        signNodes(xml, nodesToReference);

        return xml;

    }

	/**
	 * @param xml
	 * @param xpath
	 * @param context
	 * @return
	 * @throws SigningException
	 */
	private Nodes getNodesByXPath(Element xml, String xpath, XPathContext context) throws SigningException {
		Nodes nodesToReference = new Nodes();


        if (xpath.equals("/*")) {

            //Just put in the base document - skip the xpath
            nodesToReference.append(xml);

        } else {

            try {

                if (context != null) {
                    nodesToReference = xml.query(xpath, context);
                } else {
                    nodesToReference = xml.query(xpath);
                }

            } catch (XPathException e) {

                throw new SigningException("Error in signing XPath: " + xpath, e);

            }

        }

        if (nodesToReference.size() == 0) throw new SigningException("XPath returned no results");
		return nodesToReference;
	}

}
