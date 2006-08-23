package org.xmldap.ws.soap.headers.addressing.impl;

import junit.framework.TestCase;

/**
 * ToHeader Tester.
 *
 * @author <Authors name>
 * @since <pre>03/16/2006</pre>
 * @version 1.0
 */
public class ToHeaderTest extends TestCase {

    ToHeader header;

    public void setUp() throws Exception {
        super.setUp();
        header = new ToHeader("http://To");
    }


    public void testSetGetTo() throws Exception {
        assertEquals("http://To", header.getTo());
    }

    public void testToXML() throws Exception {

        assertEquals("<wsa:To xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">http://To</wsa:To>", header.toXML());

    }
}
