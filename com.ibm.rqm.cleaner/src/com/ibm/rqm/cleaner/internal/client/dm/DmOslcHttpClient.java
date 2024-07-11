/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2019, 2021. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client.dm;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.SERVICE_PROVIDERS_URI_RM;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;

import com.ibm.rqm.cleaner.internal.client.OslcHttpClient;

/**
 * <p>Design Management (DM) OSLC HTTP client.</p>
 * 
 * <p><b>Note:</b> Design Management (DM) projects can be configured to act as an OSLC RM V2 service provider.  
 * The OSLC Cleaner Utility only supports projects configured to act as an OSLC RM V2 service provider including 
 * RM (oslc_rm) resources and properties.</p>
 *
 * 
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   1.0
 */
public final class DmOslcHttpClient extends OslcHttpClient {
    
	public DmOslcHttpClient(URL serverUrl) throws MalformedURLException, GeneralSecurityException {                
		super(serverUrl);
	} 

	@Override
	public String getServiceProvidersPropertyUri(){
		
    	//Note: Design Management (DM) projects can be configured to act as an OSLC RM V2 service provider.
		return SERVICE_PROVIDERS_URI_RM;
	}   
}
