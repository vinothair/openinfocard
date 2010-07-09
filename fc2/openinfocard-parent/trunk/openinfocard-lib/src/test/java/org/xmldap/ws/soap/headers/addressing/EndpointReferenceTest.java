package org.xmldap.ws.soap.headers.addressing;

import java.io.IOException;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

import org.xmldap.exceptions.SerializationException;
import org.xmldap.xml.XmlUtils;

import junit.framework.TestCase;

public class EndpointReferenceTest  extends TestCase {
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testToXml() throws SerializationException {
    	EndpointReference epr = new EndpointReference("sts", "mex");
    	String xml = epr.toXML();
    	String expected = "<wsa:EndpointReference xmlns:wsa=\"http://www.w3.org/2005/08/addressing\"><wsa:Address>sts</wsa:Address><wsa:Metadata><mex:Metadata xmlns:mex=\"http://schemas.xmlsoap.org/ws/2004/09/mex\"><mex:MetadataSection><mex:MetadataReference><wsa:Address>mex</wsa:Address></mex:MetadataReference></mex:MetadataSection></mex:Metadata></wsa:Metadata></wsa:EndpointReference>";
    	assertEquals(expected, xml);
    }
    
    public void testToXmlAndBack() throws SerializationException, IOException, ParsingException, org.xmldap.exceptions.ParsingException {
    	EndpointReference epr = new EndpointReference("sts", "mex");
    	String xml = epr.toXML();
    	Document doc = XmlUtils.parse(xml);
    	Element root = doc.getRootElement();
    	EndpointReference parsedEpr = new EndpointReference(root);
    	String xml2 = parsedEpr.toXML();
    	assertEquals(xml, xml2);
    	assertEquals("mex", parsedEpr.mex);
    	assertEquals("sts", parsedEpr.sts);
    }
}
