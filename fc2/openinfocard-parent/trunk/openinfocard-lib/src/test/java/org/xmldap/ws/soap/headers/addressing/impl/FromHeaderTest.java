package org.xmldap.ws.soap.headers.addressing.impl;

import junit.framework.TestCase;

/**
 * FromHeader Tester.
 *
 * @author <Authors name>
 * @since <pre>03/16/2006</pre>
 * @version 1.0
 */
public class FromHeaderTest extends TestCase {

    FromHeader header;

    public void setUp() throws Exception {
        super.setUp();
        header = new FromHeader("http://from");
    }


    public void testSetGetFrom() throws Exception {
        assertEquals("http://from", header.getFrom());
    }

    public void testToXML() throws Exception {

        assertEquals("<wsa:From xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">http://from</wsa:From>", header.toXML());

    }

}
