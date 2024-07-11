/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.LINE_SEPARATOR;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.MEDIA_TYPE_APPLICATION_XML;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.ibm.rqm.cleaner.internal.client.OslcHttpClient;

/**
 * <p>Cleaner XML utilities.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   1.0
 */
public final class XmlUtils {
	
	/**
	 * <p>Reads the XML document from the HTTP GET response XML.</p>
	 *  
	 * @param OslcHttpClient The {@link OslcHttpClient}.
	 * @param uri The HTTP GET request absolute URI.
	 * @return The XML document from the HTTP GET response XML.
	 * @throws IOException If an I/O error occurs during reading.
	 */
	public static Document read(OslcHttpClient oslcHttpClient, String uri) throws IOException {	

		long startTime = System.currentTimeMillis();
		
		InputStream inputStream = oslcHttpClient.get(uri, null, MEDIA_TYPE_APPLICATION_XML);

    	long endTime = System.currentTimeMillis();
    	
    	if(PerformanceUtils.isTraceEnabled()) {
			PerformanceUtils.logTrace("Read '" + uri + "' as '" + MEDIA_TYPE_APPLICATION_XML + "'", startTime, endTime); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    	}
    	
		try{
			return (DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream));
		}
		catch(Exception e){

			String responseContent = StringUtils.toString(oslcHttpClient.get(uri));
			
			LogUtils.logError("Error parsing HTTP GET " + uri + " response XML (status code: " + oslcHttpClient.head(uri) + ").  Response:" + (StringUtils.isSet(responseContent) ? (LINE_SEPARATOR + responseContent) : " <no response content>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

			throw new IOException(e);
		}
	}
}
