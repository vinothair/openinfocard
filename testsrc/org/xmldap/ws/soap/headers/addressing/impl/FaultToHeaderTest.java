package org.xmldap.ws.soap.headers.addressing.impl;

import junit.framework.TestCase;


public class FaultToHeaderTest extends TestCase {


    FaultToHeader header;

    public void setUp() throws Exception {
        super.setUp();
        header = new FaultToHeader("http://faultto");
    }


    public void testSetGetFaultTo() throws Exception {
        assertEquals("http://faultto", header.getFaultTo());
    }

    public void testToXML() throws Exception {

        assertEquals("<wsa:FaultTo xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">http://faultto</wsa:FaultTo>", header.toXML());

    }


}
