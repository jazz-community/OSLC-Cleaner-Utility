/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2019. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>OSLC HTTP client service provider.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public class ServiceProvider {

	/**
	 * <p>URL (<code>rdf:about</code>) of the service provider.</p>
	 */
	private final String url;

	/**
	 * <p>Title (<code>dcterms:title</code>) of the service provider.</p>
	 */
	private final String title;

	/**
	 * <p>Creation factories (<code>oslc:CreationFactory</code>) of the service provider,
	 * mapping a <code>oslc:resourceType</code> to a <code>oslc:creation</code>.</p>
	 */
	private final Map<String, String> creationFactories;

	/**
	 * <p>Query capabilities (<code>oslc:QueryCapability</code>) of the service provider,
	 * mapping a <code>oslc:resourceType</code> (see {@link com.ibm.rqm.cleaner.internal.util.CleanerConstants#RESOURCE_TEMPLATE_QUERY}) 
	 * to a <code>oslc:queryBase</code>.</p>
	 */
	private final Map<String, String> queryCapabilities;
	
	/**
	 * <p>URL of the configuration context of the project area associated with this service provider.</p>
	 * 
	 * <p>The configuration context URL will be included in the <code>Configuration-Context</code> request 
	 * header for all requests to the project area associated with this service provider.</p>
	 */
	private String configurationContextUrl = null;
	
	public ServiceProvider(String url, String title){
		
		this.url = url;
		this.title = title;
		this.creationFactories = new HashMap<String, String>();
		this.queryCapabilities = new HashMap<String, String>();
	}

	public Map<String, String> getCreationFactories() {
		return creationFactories;
	}

	public Map<String, String> getQueryCapabilities() {
		return queryCapabilities;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public void setConfigurationContextUrl(String configurationContextUrl) {
		this.configurationContextUrl = configurationContextUrl;
	}

	public String getConfigurationContextUrl() {
		return configurationContextUrl;
	}
}
