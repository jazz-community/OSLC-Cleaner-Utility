/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2019, 2022. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.LINE_SEPARATOR;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.MEDIA_TYPE_APPLICATION_XHTML;

import java.io.IOException;
import java.io.InputStream;

import com.ibm.rqm.cleaner.internal.client.OslcHttpClient;

/**
 * <p>Cleaner XHTML utilities.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   1.0
 */
public final class XhtmlUtils {
	
	/**
	 * <p>Reads the XHTML document from the HTTP GET response XML.</p>
	 *  
	 * @param OslcHttpClient The {@link OslcHttpClient}.
	 * @param uri The HTTP GET request absolute URI.
	 * @return The XHTML document from the HTTP GET response XML.
	 * @throws IOException If an I/O error occurs during reading.
	 */
	public static String read(OslcHttpClient oslcHttpClient, String uri) throws IOException {	

		long startTime = System.currentTimeMillis();
		
		InputStream inputStream = oslcHttpClient.get(uri, null, MEDIA_TYPE_APPLICATION_XHTML);

    	long endTime = System.currentTimeMillis();
    	
    	if(PerformanceUtils.isTraceEnabled()) {
			PerformanceUtils.logTrace("Read '" + uri + "' as '" + MEDIA_TYPE_APPLICATION_XHTML + "'", startTime, endTime); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    	}
    	
		try{
			return (StringUtils.toString(inputStream));
		}
		catch(Exception e){

			String responseContent = StringUtils.toString(oslcHttpClient.get(uri, null, MEDIA_TYPE_APPLICATION_XHTML));
			
			LogUtils.logError("Error parsing HTTP GET " + uri + " response XHTML (status code: " + oslcHttpClient.head(uri) + ").  Response:" + (StringUtils.isSet(responseContent) ? (LINE_SEPARATOR + responseContent) : " <no response content>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

			throw new IOException(e);
		}
	}
}
