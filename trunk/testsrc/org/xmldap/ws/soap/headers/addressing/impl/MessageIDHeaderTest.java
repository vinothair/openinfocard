package org.xmldap.ws.soap.headers.addressing.impl;

import junit.framework.TestCase;

/**
 * MessageIDHeader Tester.
 *
 * @author <Authors name>
 * @since <pre>03/16/2006</pre>
 * @version 1.0
 */
public class MessageIDHeaderTest extends TestCase {



    public void setUp() throws Exception {
        super.setUp();
    }


    public void testSetGetMessageId() throws Exception {
        MessageIDHeader header = new MessageIDHeader("aaaa-bbbb-cccc-dddd");
        assertEquals("aaaa-bbbb-cccc-dddd", header.getMessageId());
    }

    public void testToXML() throws Exception {
        MessageIDHeader header = new MessageIDHeader("aaaa-bbbb-cccc-dddd");
        assertEquals("<wsa:MessageID xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">aaaa-bbbb-cccc-dddd</wsa:MessageID>", header.toXML());
        header = new MessageIDHeader();
        assertEquals("<wsa:MessageID xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">" + header.getMessageId() + "</wsa:MessageID>", header.toXML());

    }

}
