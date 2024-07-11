/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2018. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client;

/**
 * <p>Tracked Resource Set version 2 (TRS2) provider (<code>qm_rqm:trackedResourceSetProvider</code>).</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   1.0
 */
public class Trs2Provider {

	/**
	 * <p>URL (<code>trs2:trackedResourceSet/rdf:resource</code>) of the TRS2 provider.</p>
	 */
	private final String url;

	/**
	 * <p>Type (<code>dcterms:type</code>) of the TRS2 provider.</p>
	 */
	private final String type;

	/**
	 * <p>Title (<code>dcterms:title</code>) of the TRS2 provider.</p>
	 */
	private final String title;

	/**
	 * <p>Description (<code>dcterms:description</code>) of the TRS2 provider.</p>
	 */
	private final String description;

	public Trs2Provider(String url){
		this(url, null, null, null);
	}
	
	public Trs2Provider(String url, String type, String title, String description){
		
		this.url = url;
		this.type = type;
		this.title = title;
		this.description = description;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
}
