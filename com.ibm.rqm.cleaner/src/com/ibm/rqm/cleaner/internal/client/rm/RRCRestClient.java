/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client.rm;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_REQUEST_URI;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_METHOD_DELETE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_METHOD_GET;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_METHOD_HEAD;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_METHOD_POST;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_METHOD_PUT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.JAZZ_URI_FORM_AUTHENTICATION;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.ibm.rqm.cleaner.internal.client.AuthenticationException;
import com.ibm.rqm.cleaner.internal.util.CleanerConstants;
import com.ibm.rqm.cleaner.internal.util.LogUtils;
import com.ibm.rqm.integration.client.clientlib.HttpClientConstants;

/**
 * <p>Requirements Management (RM) OSLC HTTP client.</p>
 * 
 * <p>Note: Copied from <code>com.ibm.rqm.requirement.service</code> plug-in.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public class RRCRestClient {
	
	public boolean usePrivateHeader = true;
	public static boolean useRedirect = false;
	private static final Collection<CloseableHttpClient> authenticatedClients =
		Collections.synchronizedCollection(new HashSet<CloseableHttpClient>());

	public RRCRestClient(boolean usePrivateHeader){
		this.usePrivateHeader = usePrivateHeader;
	}

	public RRCRestClient() {}

	public RequestResponse performHead(IRequirementsRepository repository,
			String url, Map<String, String> requestHeaderAttributes)
			throws IOException {
		HttpRequestBase method = null;
		CloseableHttpResponse response = null;
		try {
			method = createConnection(HTTP_METHOD_HEAD, url, repository);
			applyRequestHeader(method, requestHeaderAttributes);
			response = executeHttpMethod(repository, method, url, 0);
			return new RequestResponse(response, true);
		} finally {
			if (method != null)
				method.releaseConnection();
		}
	}
	
	public RequestResponse performGet(IRequirementsRepository repository,
			String url, Map<String, String> requestHeaderAttributes)
			throws IOException {
		HttpRequestBase method = null;
		CloseableHttpResponse response = null;
		try {
			method = createConnection(HTTP_METHOD_GET, url, repository);
			applyRequestHeader(method, requestHeaderAttributes);
			response = executeHttpMethod(repository, method, url, 0);
			return new RequestResponse(response, false);
		} finally {
			if (method != null)
				method.releaseConnection();
		}
	}
	
	
	private CloseableHttpResponse executeHttpMethod(IRequirementsRepository repository, HttpRequestBase method, String url, int retriedTimes)
			throws IOException {
		
		CloseableHttpResponse httpResponse = null;
		//Some how when the request is sent by the HTTP server the following code is causing the two request URIs in the header
		try {
			Header header = method.getFirstHeader(HTTP_HEADER_REQUEST_URI);
			method.removeHeader(header);
		} catch (Exception e) {
			//May be the header doesn't exist, never mind
		}
		method.addHeader(HTTP_HEADER_REQUEST_URI, url);
		
		CloseableHttpClient httpClient = repository.getHttpClient();
		
		//Add the encoding header to compress the response:
		//Note: Do NOT compress the response to a) eliminate uncompressing the response and b) HTTP client wire logging the uncompressed response.  
		//method.addRequestHeader(HTTP_HEADER_ACCEPT_ENCODING, ENCODING_GZIP );
		
		// we're assuming that all httpClients need to authenticate.  Until a client
		// has authenticated only one thread will be able to use that httpClient.
		if (authenticatedClients.contains(httpClient)) {
			preExecute(repository, method);
			httpResponse = httpClient.execute(method);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP || statusCode == HttpURLConnection.HTTP_SEE_OTHER || statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
				((HttpGet) method).releaseConnection();
				authenticatedClients.remove(httpClient);
				return executeHttpMethod(repository, method, url, retriedTimes);
			}
		} else {
			synchronized (httpClient) {
				preExecute(repository, method);
				try {
					httpResponse = httpClient.execute(method);
				} catch (IOException e) {
					method.releaseConnection();
					authenticatedClients.remove(httpClient);
					if(retriedTimes < 5){//try at most 5 times
						
						if(LogUtils.isTraceEnabled()) {
							LogUtils.logTrace("IOException thrown when attempting to executeMethod with Requirement Application, had retried the invocation " + retriedTimes +" times, will retry the invocation again.");//$NON-NLS-1$//$NON-NLS-2$
						}
						
						return executeHttpMethod(repository, method, url, retriedTimes++);							
					}else {
						throw e;
					}
				}

				int statusCode = httpResponse.getStatusLine().getStatusCode();
				// If we're redirected (302 - JFS) or unauthorized (401 - RRS); try to authenticate
				if ((statusCode == HttpURLConnection.HTTP_MOVED_TEMP  || statusCode == HttpURLConnection.HTTP_SEE_OTHER || statusCode == HttpURLConnection.HTTP_UNAUTHORIZED)
						&& repository.getLoginHandler() != null) {
					method.releaseConnection();
					
					try {
						HttpRequestBase authenticatedMethod = repository.getLoginHandler().authenticate(repository, method, httpResponse);
						httpResponse = repository.getHttpClient().execute(authenticatedMethod);
						
					} catch (AuthenticationException e) {
						// authentication failed
						throw new AuthenticationException(e.getLocalizedMessage());
					}
				}
				if (statusCode != HttpURLConnection.HTTP_MOVED_TEMP && statusCode != HttpURLConnection.HTTP_SEE_OTHER && statusCode != HttpURLConnection.HTTP_UNAUTHORIZED)
					authenticatedClients.add(httpClient);
			}
		}
		
		return httpResponse;
	}

	private static String preExecute(IRequirementsRepository repo, HttpRequestBase method){
		
		String requestBody = ""; //$NON-NLS-1$
		try{
			if(method.getURI().toString().contains("/" + JAZZ_URI_FORM_AUTHENTICATION) || method.getURI().toString().endsWith("resources")){ //$NON-NLS-1$ //$NON-NLS-2$ 
				requestBody = ""; //$NON-NLS-1$
			}else if(method.getURI().toString().contains("resources/rrc")){ //$NON-NLS-1$
				requestBody = ""; //$NON-NLS-1$
			}
			else if(method instanceof HttpEntityEnclosingRequestBase){
				HttpEntityEnclosingRequestBase entityMethod = (HttpEntityEnclosingRequestBase)method;
				entityMethod.getEntity().getContentLength();
				if(entityMethod.getEntity().isRepeatable()){
					HttpEntity entity = entityMethod.getEntity();
					requestBody = EntityUtils.toString(entity, HttpClientConstants.UTF8);
				}
			}
			
			
			String token = captureStart(method.getURI().toString(),method.getMethod(),requestBody) ;
			return token;
		}catch(Exception e){
			LogUtils.logError(e.toString(), e);
		}
		return null;
	}

	private static void applyRequestHeader(HttpRequestBase method,
			Map<String, String> requestHeaderAttributes) {
		if (requestHeaderAttributes != null) {
			Set<String> keys = requestHeaderAttributes.keySet();
			for (String headerName : keys) {
				method.addHeader(headerName, requestHeaderAttributes.get(headerName));
			}
		}
	}

	private static HttpRequestBase createConnection(String method,
			String url, IRequirementsRepository repository) throws IOException {
		HttpRequestBase m = null;
		if (HTTP_METHOD_GET.equals(method)) {
			m = new HttpGet();
		} else if (HTTP_METHOD_PUT.equals(method)) {
			m = new HttpPut();
		} else if (HTTP_METHOD_DELETE.equals(method)) {
			m = new HttpDelete();
		} else if (HTTP_METHOD_HEAD.equals(method)) {
			m = new HttpHead();
		} else if (HTTP_METHOD_POST.equals(method)) {
			m = new HttpPost();
		}
		repository.setFollowRedirect(false);

		// m.setRequestHeader(HTTP.Headers.ACCEPT, HTTP.Headers.WILDCARD);
		// we need to make url relative
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (Exception e) {
			LogUtils.logError(e.toString(), e);
		}
		m.setURI(uri);
		return m;
	}

	public RequestResponse performPut(IRequirementsRepository repository,
			String url, InputStream contents,
			String contentType,
			Map<String, String> requestHeaderAttributes) throws IOException {
		HttpRequestBase method = null;
		CloseableHttpResponse response = null;
		try {
			method = createConnection(HTTP_METHOD_PUT, url, repository);
			HttpPut putMethod = (HttpPut) method;
			applyRequestHeader(method, requestHeaderAttributes);
			putMethod.addHeader(CleanerConstants.HTTP_HEADER_CONTENT_TYPE, contentType);
			InputStreamEntity requestEntity = new InputStreamEntity(
					contents);
			putMethod.setEntity(requestEntity);
			response = executeHttpMethod(repository, method, url, 0);
			return new RequestResponse(response, false);
		} finally {
			if (method != null)
				method.releaseConnection();
		}
	}

	public RequestResponse performPost(
			IRequirementsRepository repository,
			String url, 
			InputStream contents,
			String contentType, 
			Map<String, String> requestHeaderAttributes) throws IOException {
		HttpRequestBase method = null;
		CloseableHttpResponse response = null;
		try {
			method = createConnection(HTTP_METHOD_POST, url, repository);
			HttpPost postMethod = (HttpPost) method;
			applyRequestHeader(method, requestHeaderAttributes);
			postMethod.addHeader(CleanerConstants.HTTP_HEADER_CONTENT_TYPE, contentType);
			InputStreamEntity requestEntity = new InputStreamEntity(
					contents);
			postMethod.setEntity(requestEntity);
			response = executeHttpMethod(repository, method, url, 0);
			return new RequestResponse(response, false);
		}finally {
			if (method != null)
				method.releaseConnection();
		}
	}

	public RequestResponse performDelete(
			IRequirementsRepository repository,
			String url,
			Map<String, String> requestHeaderAttributes) throws IOException {
		HttpRequestBase method = null;
		CloseableHttpResponse response = null;
		try {
			method = createConnection(HTTP_METHOD_DELETE, url, repository);
			applyRequestHeader(method, requestHeaderAttributes);
			
			response = executeHttpMethod(repository, method, url, 0);
			return new RequestResponse(response, false);
		}finally {
			if (method != null)
				method.releaseConnection();
		}
	}

	private static int counter;
	static HashMap<String, Object[]> map = new HashMap<String, Object[]>();
	private synchronized static String captureStart(String... strings) {
		counter++;
		String tokenStr = "" + counter; //$NON-NLS-1$
		Object[] objs = new Object[2];
		objs[0] = new Date();
		objs[1] = strings;
		map.put(tokenStr, objs);
		return tokenStr;
	}

}
