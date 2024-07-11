/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2021. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client.rm;


import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * <p>Requirements Management (RM) OSLC HTTP client requirement repository.</p>
 * 
 * <p>Note: Copied from <code>com.ibm.rqm.requirement.service</code> plug-in.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 */
public interface IRequirementsRepository {
	
	public abstract String getUrlString();

	public abstract CloseableHttpClient getHttpClient();

	public abstract void registerLoginHandler(ILoginHandler loginHandler);

	public abstract ILoginHandler getLoginHandler();
	
	public void setFollowRedirect(boolean isRedirect);
	
	public UsernamePasswordCredentials getUsernamePasswordCredentials();
	
	public HttpResponse executeMethod(HttpRequestBase method) throws IOException;
}
