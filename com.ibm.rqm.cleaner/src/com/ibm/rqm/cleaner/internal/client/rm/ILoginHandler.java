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
import org.apache.http.client.methods.HttpRequestBase;


/**
 * <p>Requirements Management (RM) OSLC HTTP client login handler.</p>
 * 
 * <p>Note: Copied from <code>com.ibm.rqm.requirement.service</code> plug-in.</p>
 *  
 *  
 * @author  Paul Slauenwhite
 */
public interface ILoginHandler {
	public HttpRequestBase authenticate(IRequirementsRepository repository, HttpRequestBase method, HttpResponse response) throws  IOException ;
}
