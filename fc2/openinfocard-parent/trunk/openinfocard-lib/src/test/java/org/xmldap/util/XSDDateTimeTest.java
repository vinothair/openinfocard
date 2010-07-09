package org.xmldap.util;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

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

    public void testParse() throws Exception {
    	Calendar cal = XSDDateTime.parse("2006-09-27T12:58:26Z");
        assertEquals(2006, cal.get(Calendar.YEAR));
        assertEquals(8, cal.get(Calendar.MONTH));
        assertEquals(27, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(58, cal.get(Calendar.MINUTE));
        assertEquals(26, cal.get(Calendar.SECOND));

    	TimeZone tz = TimeZone.getTimeZone("GMT+00:00");
        Calendar exp = Calendar.getInstance(tz, Locale.US);
        exp.set(2006,Calendar.SEPTEMBER,27,12,58,26);
        assertEquals(exp.get(Calendar.YEAR), cal.get(Calendar.YEAR));
        assertEquals(exp.get(Calendar.MONTH), cal.get(Calendar.MONTH));
        assertEquals(exp.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(exp.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(exp.get(Calendar.MINUTE), cal.get(Calendar.MINUTE));
        assertEquals(exp.get(Calendar.SECOND), cal.get(Calendar.SECOND));
    }

    public void testGetDateTime() throws Exception {
    	Calendar cal = XSDDateTime.parse("2006-09-27T12:58:26Z");
    	String datetime = XSDDateTime.getDateTime(cal);
    	assertEquals("2006-09-27T12:58:26Z", datetime);
    }

    public void testFormat() throws Exception {
//    	2006-09-27T12:58:26Z
//    	2006-09-27T13:16:09Z
    	String time = dateTime.getDateTime();
    	assertEquals(20, time.length());
    	assertEquals(19, time.indexOf("Z"));
    	assertEquals(13, time.indexOf(":"));
    	assertEquals(10, time.indexOf("T"));
    	assertEquals(4, time.indexOf("-"));
    }

    public void testPad() throws Exception {

        assertEquals("01", dateTime.pad(1));
        assertEquals("12", dateTime.pad(12));

    }

}
