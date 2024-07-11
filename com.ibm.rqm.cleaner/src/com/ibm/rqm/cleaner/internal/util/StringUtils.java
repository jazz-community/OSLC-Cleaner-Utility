/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015, 2018. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.ENCODING_UTF8;

/**
 * <p>String utilities.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   1.0
 */
public final class StringUtils {

	/**
	 * <p>Converts an {@link InputStream} to a {@link String}.
	 * 
	 * @param inputStream The {@link InputStream}.
	 * @return The {@link String} representations of the {@link InputStream}.
	 * @throws IOException If an error occurs when reading or closing the {@link InputStream}.
	 */
	public static String toString(InputStream inputStream) throws IOException {

		Scanner scanner = new Scanner(inputStream, ENCODING_UTF8);

		try { 

			scanner.useDelimiter("\\A"); //$NON-NLS-1$

			if(scanner.hasNext()){
				return  (scanner.next());
			}
			else{
				return ""; //$NON-NLS-1$
			}
		} 
		finally {
			
			scanner.close();
			inputStream.close();
		}
	}
	
	/**
	 * <p>Answers {@code true} if the given {@link String} is not null and it contains non-whitespace 
	 * characters.  Answers {@code false} otherwise.</p>
	 * 
	 * <p>For example:</p>
	 * <p><pre>
	 * isSet(null)  ==> false
	 * isSet("")    ==> false
	 * isSet(" ")   ==> false
	 * isSet("foo") ==> true
	 * </pre></p>
	 * 
	 * @param s
	 * 		The {@link String} to be evaluated.
	 * @return
	 * 		{@code true} if the given {@link String} is not null and it contains non-whitespace 
	 * 		characters.   Answers {@code false} otherwise.
	 */
	public static final boolean isSet(final String s) {
		return !isNotSet(s);
	}
	
	/**
	 * <p>Answers {@code true} if the given {@link String} is null or if it contains only whitespace 
	 * characters. Answers {@code false} otherwise.</p>
	 * 
	 * <p>For example:</p>
	 * <p><pre>
	 * isNotSet(null)  ==> true
	 * isNotSet("")    ==> true
	 * isNotSet(" ")   ==> true
	 * isNotSet("foo") ==> false
	 * </pre></p>
	 * 
	 * @param s
	 * 		The {@link String} to be evaluated.
	 * @return
	 * 		{@code true} if the given {@link String} is null or if it contains only whitespace 
	 * 		characters. Answers {@code false} otherwise.
	 */
	public static final boolean isNotSet(final String s) {
		return s == null || (s.isEmpty() || s.trim().isEmpty());
	}
	
	/**
	 * <p>Answers {@code true} if the given {@link String} contains a {@link Long}. Answers 
	 * {@code false} otherwise.</p>
	 * 
	 * @param string The {@link String} to be evaluated.
	 * @return {@code true} if the given {@link String} contains a {@link Long}, otherwise {@code false}.
	 */
	public static final boolean isLong(final String string) {
		
		if(isSet(string)) {
			
			try {

				Long.parseLong(string);
				
				return true;
			} 
			catch (NumberFormatException n) {
				//Ignore and return false.
			}
		}
		
		return false;
	}
}