/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2018, 2021. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client.unknown;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;

import org.apache.http.HttpStatus;

import com.ibm.rqm.cleaner.internal.client.OslcHttpClient;

/**
 * <p>OSLC HTTP client for unknown OSLC service providers.</p>
 * 
 * <p>The following are unknown about these OSLC service providers:</p>
 * 
 * <ul>
 * <li>Root services URI</li>
 * <li>Service provider property URI</li>
 * <li>Ping URI</li>
 * <li>Logout URI</li>
 * </ul>
 * 
 * <p><b>Note:</b> This HTTP client only supports basic authentication.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   1.0
 */
public final class UnknownOslcHttpClient extends OslcHttpClient {
    
	public UnknownOslcHttpClient(URL serverUrl) throws MalformedURLException, GeneralSecurityException {                
		super(serverUrl);        
	}

	@Override
	public String getServiceProvidersPropertyUri(){
		return null;
	}   
	
	@Override
	public String getRootServicesUri() {
		return null;
	}  
	
	@Override
	protected int login() throws IOException {

		//Verify the connection by performing a ping:
		return (ping());
	}
	
	@Override
	public int ping() throws IOException{

		//Return OK (200) since there is no known URL to ping since server URL may not be a valid HTTP end point:
		return HttpStatus.SC_OK;
	}

	@Override
	public int logout() {
		
		//Return OK (200) since there is no known URL to logout:
		return HttpStatus.SC_OK;
	}

	@Override
	public int head(String uri, String queryString) throws IOException{

		int responseCode = super.head(uri, queryString);

		//Problem: Some OSLC service providers may NOT support the HTTP HEAD.
		//Work-around: Use the HTTP GET method.
		if ((responseCode == HttpStatus.SC_METHOD_NOT_ALLOWED) || (responseCode == HttpStatus.SC_NOT_IMPLEMENTED) || (responseCode == HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE)) {
			responseCode = getStatusCode(uri, queryString);
		}

		return responseCode;
	}   
}
