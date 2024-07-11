/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.DWA_OAUTH_REALM_NAME;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_DC_TERMS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_JAZZ_JFS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_OSLC;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_IDENTIFIER;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OAUTH_REALM_NAME;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_PUBLISHER;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PUBLISHER_IDENTIFIER_URI_CCM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PUBLISHER_IDENTIFIER_URI_DM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PUBLISHER_IDENTIFIER_URI_DWA;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PUBLISHER_IDENTIFIER_URI_QM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PUBLISHER_IDENTIFIER_URI_RM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.URI_TEMPLATE_PUBLIC_ROOT_SERVICES;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.URI_TEMPLATE_ROOT_SERVICES;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

import com.hp.hpl.jena.rdf.model.Model;
import com.ibm.icu.text.MessageFormat;
import com.ibm.rqm.cleaner.internal.client.cm.CmOslcHttpClient;
import com.ibm.rqm.cleaner.internal.client.dm.DmOslcHttpClient;
import com.ibm.rqm.cleaner.internal.client.dwa.DwaOslcHttpClient;
import com.ibm.rqm.cleaner.internal.client.qm.QmOslcHttpClient;
import com.ibm.rqm.cleaner.internal.client.rm.RmOslcHttpClient;
import com.ibm.rqm.cleaner.internal.client.unknown.UnknownOslcHttpClient;
import com.ibm.rqm.cleaner.internal.util.LogUtils;
import com.ibm.rqm.cleaner.internal.util.PerformanceUtils;
import com.ibm.rqm.cleaner.internal.util.RdfUtils;
import com.ibm.rqm.cleaner.internal.util.UrlUtils;

/**
 * <p>OSLC HTTP client factory.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public class OslcHttpClientFactory{
    
	private final HashMap<URL, OslcHttpClient> authenticatedClients;
	private final HashMap<URL, OslcHttpClient> unauthenticatedClients;

	private String acceptLanguage = null;
	private Map<URL, Credential> credentials = null;
	private boolean disableDngQueryTimeout = false;

	private static OslcHttpClientFactory instance = null;
	
	private OslcHttpClientFactory() {
		
		authenticatedClients = new HashMap<URL, OslcHttpClient>();
		unauthenticatedClients = new HashMap<URL, OslcHttpClient>();
	}
	
	public static OslcHttpClientFactory getInstance() {
	
		if(instance == null) {
			instance = new OslcHttpClientFactory();
		}
		
		return instance;
	}
	
	public void setAcceptLanguage(String acceptLanguage) {
		this.acceptLanguage = acceptLanguage;
	}

	public void setCredentials(Map<URL, Credential> credentials) {
		this.credentials = credentials;
	}
	
	public void setDisableDngQueryTimeout(boolean disableDngQueryTimeout) {
		this.disableDngQueryTimeout = disableDngQueryTimeout;
	}
	
    public OslcHttpClient getClient(URL serverUrl) throws IOException, GeneralSecurityException{        
        
    	OslcHttpClient client = null;
        
    	if (authenticatedClients.containsKey(serverUrl)) {
            client = authenticatedClients.get(serverUrl);
        } 
    	else if (unauthenticatedClients.containsKey(serverUrl)) {
    		
			//Problem: If the log-in to the target application is unsuccessful (e.g. missing/incorrect credentials), the OSLC Cleaner Utility 
    		//         will attempt to re-authenticate with target application on every subsequent HTTP request.  As a result, the user ID may 
    		//         be locked out and/or extraneous authentication failures notifications (e.g. administrator e-mails) may be generated. 
			//Solution: Cache clients that fail authentication and log an error message. 
    		throw new IOException("Unauthenticated server '" + serverUrl + "'."); //$NON-NLS-1$ //$NON-NLS-2$
        } 
    	else {
        	
    		String publisherIdentifierUri = resolvePublisherIdentifierUri(serverUrl);
    		
        	if(PUBLISHER_IDENTIFIER_URI_CCM.equals(publisherIdentifierUri)){ 
        		client = new CmOslcHttpClient(serverUrl);
        	}
        	else if(PUBLISHER_IDENTIFIER_URI_DM.equals(publisherIdentifierUri)){ 
        		client = new DmOslcHttpClient(serverUrl);
        	}
        	else if(PUBLISHER_IDENTIFIER_URI_RM.equals(publisherIdentifierUri)){ 
        		
        		client = new RmOslcHttpClient(serverUrl);
        	
        		((RmOslcHttpClient)(client)).setDisableDngQueryTimeout(disableDngQueryTimeout);
        	}
        	else if(PUBLISHER_IDENTIFIER_URI_DWA.equals(publisherIdentifierUri)){ 
        		client = new DwaOslcHttpClient(serverUrl);
        	}
        	else if(PUBLISHER_IDENTIFIER_URI_QM.equals(publisherIdentifierUri)){ 
        		client = new QmOslcHttpClient(serverUrl);
        	}
        	else{
        		
        		LogUtils.logWarning("Unknown application '" + UrlUtils.getContextRoot(serverUrl.toString()) + "' on server '" + serverUrl + "'.  Using a default HTTP client that supports only basic authentication."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        		client = new UnknownOslcHttpClient(serverUrl);
        	}
        	
        	client.setAcceptLanguage(acceptLanguage);

        	if((credentials == null) || (!credentials.containsKey(serverUrl))){
				
				unauthenticatedClients.put(serverUrl, client);

        		throw new IOException("Missing credential for server '" + serverUrl + "'."); //$NON-NLS-1$ //$NON-NLS-2$
        	}

        	Credential credential = credentials.get(serverUrl);
        	
        	long startTime = System.currentTimeMillis();
        	
        	int returnCode = client.login(credential);

        	long endTime = System.currentTimeMillis();
        	
        	if(PerformanceUtils.isTraceEnabled()) {
				PerformanceUtils.logTrace("Login to server '" + serverUrl + "'", startTime, endTime); //$NON-NLS-1$ //$NON-NLS-2$
        	}
        	
			if ((returnCode != HttpStatus.SC_OK) && (returnCode != HttpStatus.SC_MOVED_TEMPORARILY) && (returnCode != HttpStatus.SC_SEE_OTHER)) {

				unauthenticatedClients.put(serverUrl, client);

				throw new IOException("Error authenticating with server '" + serverUrl + "'. Return Code: " + returnCode); //$NON-NLS-1$ //$NON-NLS-2$
			}

			//Ping the server to verify the connection by performing a HEAD request on the first service provider catalog:
        	returnCode = client.ping();

			if ((returnCode != HttpStatus.SC_OK) && (returnCode != HttpStatus.SC_MOVED_TEMPORARILY) && (returnCode != HttpStatus.SC_SEE_OTHER)) {

				unauthenticatedClients.put(serverUrl, client);

				throw new IOException("Error pinging server '" + serverUrl + "'. Return Code: " + returnCode); //$NON-NLS-1$ //$NON-NLS-2$
			}

			authenticatedClients.put(serverUrl, client);
        }
    	
        return client;
    }

    public void logoutClients() {

    	for(OslcHttpClient oslcHttpClient : authenticatedClients.values()) {

    		if (oslcHttpClient != null) {

    			LogUtils.logInfo("Disconnecting from server '" + oslcHttpClient.getServerUrl() + "'."); //$NON-NLS-1$ //$NON-NLS-2$

    			int returnCode = oslcHttpClient.logout();

    			if ((returnCode != HttpURLConnection.HTTP_OK) && (returnCode != HttpURLConnection.HTTP_MOVED_TEMP) && (returnCode != HttpURLConnection.HTTP_SEE_OTHER)) {
    				LogUtils.logInfo("Failed to disconnect from server '" + oslcHttpClient.getServerUrl() + "' (return code: " + returnCode + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    			}
    		}
    	}

    	authenticatedClients.clear();
    }
	
	private String resolvePublisherIdentifierUri(URL serverUrl) throws MalformedURLException, GeneralSecurityException {

		String publisherIdentifierUri = null;

		if(serverUrl != null) {

			UnknownOslcHttpClient client = new UnknownOslcHttpClient(serverUrl);

			try {

				String rootServicesDocumentUri = MessageFormat.format(URI_TEMPLATE_ROOT_SERVICES, new Object[]{UrlUtils.appendTrailingForwardSlash(serverUrl.toString())});

				Model rootServicesDocumentModel = RdfUtils.read(client, rootServicesDocumentUri);

				String publisherUri = RdfUtils.getPropertyResourceValue(rootServicesDocumentModel, NAMESPACE_URI_OSLC, PROPERTY_PUBLISHER);

				if(UrlUtils.isValidUrl(publisherUri)){

					Model publisherModel = RdfUtils.read(client, publisherUri);

					publisherIdentifierUri = RdfUtils.getPropertyStringValue(publisherModel, NAMESPACE_URI_DC_TERMS, PROPERTY_IDENTIFIER);
				}	
			} 
			catch (Exception e) {

				if(LogUtils.isDebugEnabled()) {
					LogUtils.logDebug("Could not resolve the publisher identifier URI for server '" + serverUrl.toString() + "'.", e); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			
			if(!UrlUtils.isValidUrl(publisherIdentifierUri)) {

				try {

					//Note: IBM Rational DOORS and IBM Rational DOORS Web Access does not support a publisher identifier URI.  As such, detect if 
					//      IBM Rational DOORS and IBM Rational DOORS Web Access from the root services document (using the jfs:oauthRealmName property) 
					//      and use a placeholder publisher identifier URI.
					//Note: DOORS Web Access uses a different root services document URL (https://<host>:<port>/<context root>/public/rootservices).
					String rootServicesDocumentUri = MessageFormat.format(URI_TEMPLATE_PUBLIC_ROOT_SERVICES, new Object[]{serverUrl});

					Model rootServicesDocumentModel = RdfUtils.read(client, rootServicesDocumentUri);

					String oauthRealmName = RdfUtils.getPropertyStringValue(rootServicesDocumentModel, NAMESPACE_URI_JAZZ_JFS, PROPERTY_OAUTH_REALM_NAME);

					if(DWA_OAUTH_REALM_NAME.equals(oauthRealmName)){
						publisherIdentifierUri = PUBLISHER_IDENTIFIER_URI_DWA;
					}
				} 
				catch (Exception e) {

					if(LogUtils.isDebugEnabled()) {
						LogUtils.logDebug("Could not resolve the publisher identifier URI for DWA server '" + serverUrl + "'.", e); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		}

		if(LogUtils.isTraceEnabled()) {
			LogUtils.logTrace("Resolved publisher identifier URI for server '" + serverUrl + "': " + publisherIdentifierUri); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return publisherIdentifierUri;
	}
}
