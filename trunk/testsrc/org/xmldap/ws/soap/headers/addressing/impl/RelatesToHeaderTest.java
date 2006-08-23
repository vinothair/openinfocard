package org.xmldap.ws.soap.headers.addressing.impl;

import junit.framework.TestCase;


public class RelatesToHeaderTest extends TestCase {

    RelatesToHeader header;

    public void setUp() throws Exception {
        super.setUp();
        header = new RelatesToHeader("http://relatesTo");
    }


    public void testSetGetRelatesTo() throws Exception {
        assertEquals("http://relatesTo", header.getRelatesTo());
    }

    public void testToXML() throws Exception {

        assertEquals("<wsa:RelatesTo xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">http://relatesTo</wsa:RelatesTo>", header.toXML());

    }
}
