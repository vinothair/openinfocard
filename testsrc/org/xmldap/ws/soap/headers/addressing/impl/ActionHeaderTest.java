package org.xmldap.ws.soap.headers.addressing.impl;

import junit.framework.TestCase;

public class ActionHeaderTest extends TestCase {

    ActionHeader header;

    public void setUp() throws Exception {
        super.setUp();
        header = new ActionHeader("urn:header/test");
    }


    public void testSetGetAction() throws Exception {
        assertEquals("urn:header/test", header.getAction());
    }

    public void testToXML() throws Exception {

        assertEquals("<wsa:Action xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">urn:header/test</wsa:Action>", header.toXML());

    }


}
