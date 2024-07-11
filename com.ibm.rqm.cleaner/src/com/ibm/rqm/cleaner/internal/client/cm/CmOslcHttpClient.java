/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client.cm;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.SERVICE_PROVIDERS_URI_CM;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;

import org.apache.http.HttpStatus;

import com.ibm.rqm.cleaner.internal.client.OslcHttpClient;

/**
 * <p>Change Management (CM) OSLC HTTP client.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public final class CmOslcHttpClient extends OslcHttpClient {
    
	public CmOslcHttpClient(URL serverUrl) throws MalformedURLException, GeneralSecurityException {                
		super(serverUrl);        
	}

	@Override
	public String getServiceProvidersPropertyUri(){
		return SERVICE_PROVIDERS_URI_CM;
	}   
	
	@Override
	public int head(String uri, String queryString) throws IOException{

		int responseCode = super.head(uri, queryString);

		//Problem: Rational Team Concert (RTC) may NOT support the HTTP HEAD method (415 Unsupported Media Type or 501 Not Implemented or 415 Unsupported Media Type).
		//Work-around: Use the HTTP GET method.
		if ((responseCode == HttpStatus.SC_METHOD_NOT_ALLOWED) || (responseCode == HttpStatus.SC_NOT_IMPLEMENTED) || (responseCode == HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE)) {
			responseCode = getStatusCode(uri, queryString);
		}

		return responseCode;
	}   
}
