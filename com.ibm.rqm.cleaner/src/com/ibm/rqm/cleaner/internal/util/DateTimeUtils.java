/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2013, 2019. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.ISO_8601_DATE_PATTERN;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RFC_3339_DATE_PATTERN;

/**
 * <p>Date/time utilities.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public final class DateTimeUtils {

	private static SimpleDateFormat iso8601DateFormat = new SimpleDateFormat(ISO_8601_DATE_PATTERN, Locale.ENGLISH); 
	private static SimpleDateFormat rfc3339DateFormat = new SimpleDateFormat(RFC_3339_DATE_PATTERN, Locale.ENGLISH); 

	static {

		iso8601DateFormat.setTimeZone(TimeZone.getTimeZone("GMT_ID")); //$NON-NLS-1$
		rfc3339DateFormat.setTimeZone(TimeZone.getTimeZone("GMT_ID")); //$NON-NLS-1$
	}

	public static long parseDateTime(String dateTime) {

		if((dateTime != null) && (!dateTime.trim().isEmpty())){

			//Parse the date/time in the IETF/RFC3339 format (default):
			long parsedDateTime = parseRfc3339DateTime(dateTime);	

			//Parse the date/time in the W3C/ISO8601 format:
			if(parsedDateTime == -1){
				parsedDateTime = parseIso8601DateTime(dateTime);
			}

			return parsedDateTime;
		}

		return -1;
	}

	public static long parseIso8601DateTime(String dateTime) {

		if((dateTime != null) && (!dateTime.trim().isEmpty())){

			try {
				return (iso8601DateFormat.parse(dateTime).getTime());
			} 
			catch (ParseException p) {
				//Ignore and return -1.
			}
		}

		return -1;
	}

	public static String formatIso8601DateTime(long dateTime) {
		return (iso8601DateFormat.format(dateTime));
	}

	public static long parseRfc3339DateTime(String dateTime) {

		if((dateTime != null) && (!dateTime.trim().isEmpty())){

			try {
				return (rfc3339DateFormat.parse(dateTime).getTime());
			} 
			catch (ParseException p) {
				//Ignore and return -1.
			}
		}

		return -1;
	}

	public static String formatRfc3339DateTime(long dateTime) {
		return (rfc3339DateFormat.format(dateTime));
	}
	
	public static String formatTime(long time) {

		long numberOfMillisecondsInASecond = 1000;
		long numberOfMillisecondsInAMinute = (numberOfMillisecondsInASecond * 60);
		long numberOfMillisecondsInAHour = (numberOfMillisecondsInAMinute * 60);

		long hours = (time / numberOfMillisecondsInAHour);
		long minutes = ((time - (hours * numberOfMillisecondsInAHour)) / numberOfMillisecondsInAMinute);
		long seconds = ((time - (hours * numberOfMillisecondsInAHour) - (minutes * numberOfMillisecondsInAMinute)) / numberOfMillisecondsInASecond);
		long milliseconds = (time - (hours * numberOfMillisecondsInAHour) - (minutes * numberOfMillisecondsInAMinute) - (seconds * numberOfMillisecondsInASecond));

		StringBuilder formatTime = new StringBuilder();

		if(hours > 0) {

			formatTime.append(hours);
			formatTime.append(" hour"); //$NON-NLS-1$

			if(hours > 1) {
				formatTime.append("s"); //$NON-NLS-1$
			}
		}

		if(minutes > 0) {

			if(formatTime.length() > 0) {
				formatTime.append(", "); //$NON-NLS-1$
			}

			formatTime.append(minutes);
			formatTime.append(" minute"); //$NON-NLS-1$

			if(minutes > 1) {
				formatTime.append("s"); //$NON-NLS-1$
			}
		}

		if(seconds > 0) {

			if(formatTime.length() > 0) {
				formatTime.append(", "); //$NON-NLS-1$
			}

			formatTime.append(seconds);
			formatTime.append(" second"); //$NON-NLS-1$

			if(seconds > 1) {
				formatTime.append("s"); //$NON-NLS-1$
			}
		}

		if(milliseconds >= 0) {

			if(formatTime.length() > 0) {
				formatTime.append(", "); //$NON-NLS-1$
			}

			formatTime.append(milliseconds);
			formatTime.append(" millisecond"); //$NON-NLS-1$

			if((milliseconds == 0) || (milliseconds > 1)) {
				formatTime.append("s"); //$NON-NLS-1$
			}
		}

		return (formatTime.toString().trim());
	}
}
