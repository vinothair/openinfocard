/*
 * Copyright (c) 2006, Chuck Mortimore - xmldap.org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names xmldap, xmldap.org, xmldap.com nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.xmldap.util;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class XSDDateTime {

    private int moreMinutes;

    public XSDDateTime() {
        moreMinutes = 0;
    }

    public XSDDateTime(int moreMinutes) {
        this.moreMinutes = moreMinutes;
    }

     public static Calendar parse(String dt) {
    	String[] dateTime = dt.split("T");
    	String date = dateTime[0];
    	String time = dateTime[1];
    	String[] ymd = date.split("-");
    	int year = Integer.parseInt(ymd[0]);
    	int month = Integer.parseInt(ymd[1])-1;
    	int day = Integer.parseInt(ymd[2]);
    	String[] hms = time.split(":");
    	int hour = Integer.parseInt(hms[0]);
    	int minutes = Integer.parseInt(hms[1]);
    	int seconds = Integer.parseInt(hms[2].substring(0, 2));
        TimeZone tz = TimeZone.getTimeZone("GMT+00:00");
        Calendar cal = Calendar.getInstance(tz, Locale.US);
        cal.set(year, month, day, hour, minutes, seconds);
        return cal;
     }

     public static String getDateTime(Calendar cal) {
    	if (!cal.getTimeZone().equals(TimeZone.getTimeZone("GMT+00:00"))) {
    		throw new InvalidParameterException();
    	}
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		month++;
		String monthString = pad(month);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String dayString = pad(day);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		String hourString = pad(hour);
		int minutes = cal.get(Calendar.MINUTE);
		String minutesString = pad(minutes);
		int seconds = cal.get(Calendar.SECOND);
		String secondsString = pad(seconds);

		return year + "-" + monthString + "-" + dayString + "T" + hourString
				+ ":" + minutesString + ":" + secondsString + "Z";
	}

	public String getDateTime() {
		TimeZone tz = TimeZone.getTimeZone("GMT+00:00");
		Calendar cal = Calendar.getInstance(tz, Locale.US);
		cal.add(Calendar.MINUTE, moreMinutes);
		return getDateTime(cal);
	}

    protected static String pad(int value) {

        Integer valueInt = new Integer(value);
        String valueString = valueInt.toString();
        if (valueString.length() == 1) valueString = "0" + valueString;
        return valueString;

    }


}
