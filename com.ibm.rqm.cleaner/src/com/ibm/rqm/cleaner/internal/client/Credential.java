/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client;

/**
 * <p>Credential consisting of the following properties:</p>
 * 
 * <ul>
 * <li>username<sup>1</sup></li>
 * <li>password<sup>1</sup></li>
 * <li>consumer key<sup>2</sup></li>
 * <li>OAuth secret<sup>2</sup></li>
 * </ul>
 * 
 * <p><sup>1</sup>Required.</p>
 * <p><sup>2</sup>Optional.  If specified, both are required.</p>
 *  
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   1.0
 */
public class Credential {

	private final String username;
	private final String password;
	private final String consumerKey;
	private final String oAuthSecret;
	
	public Credential(String username, String password){
		this(username, password, null, null);
	}

	public Credential(String username, String password, String consumerKey, String oAuthSecret){
		
		this.username = username;
		this.password = password;
		this.consumerKey = consumerKey;
		this.oAuthSecret = oAuthSecret;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public String getOAuthSecret() {
		return oAuthSecret;
	}
}
