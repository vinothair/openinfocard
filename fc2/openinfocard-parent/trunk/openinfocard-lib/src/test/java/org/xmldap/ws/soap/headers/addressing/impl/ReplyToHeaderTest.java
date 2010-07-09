package org.xmldap.ws.soap.headers.addressing.impl;

import junit.framework.TestCase;

/**
 * ReplyToHeader Tester.
 *
 * @author <Authors name>
 * @since <pre>03/16/2006</pre>
 * @version 1.0
 */
public class ReplyToHeaderTest extends TestCase {

    ReplyToHeader header;

    public void setUp() throws Exception {
        super.setUp();
        header = new ReplyToHeader("http://ReplyTo");
    }


    public void testSetGetReplyTo() throws Exception {
        assertEquals("http://ReplyTo", header.getReplyTo());
    }

    public void testToXML() throws Exception {

        assertEquals("<wsa:ReplyTo xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">http://ReplyTo</wsa:ReplyTo>", header.toXML());

    }
}
