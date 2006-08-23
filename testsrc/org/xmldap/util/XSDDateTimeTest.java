package org.xmldap.util;

import junit.framework.TestCase;

/**
 * XSDDateTime Tester.
 *
 * @author <Authors name>
 * @since <pre>03/18/2006</pre>
 * @version 1.0
 */
public class XSDDateTimeTest extends TestCase {

    XSDDateTime dateTime;

    public void setUp() throws Exception {
        super.setUp();
        dateTime = new XSDDateTime();
    }

    public void testPad() throws Exception {

        assertEquals("01", dateTime.pad(1));
        assertEquals("12", dateTime.pad(12));

    }

}
