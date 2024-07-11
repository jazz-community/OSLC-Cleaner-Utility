/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client.dwa;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.ENCODING_UTF8;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_CONTENT_LENGTH;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_CONTENT_LOCATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_CONTENT_TYPE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_ETAG;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_LOCATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_OAUTH_AUTHORIZE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_OAUTH_TOKEN;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_OAUTH_VERIFIER;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_METHOD_DELETE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_METHOD_GET;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_METHOD_POST;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_METHOD_PUT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.JAZZ_FIELD_PASSWORD;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.JAZZ_FIELD_USERNAME;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.JAZZ_URI_FORM_AUTHENTICATION_ACEGI;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.MEDIA_TYPE_APPLICATION_X_WWW_FORM_URL_ENCODED_UTF_8;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.MEDIA_TYPE_RDF_XML;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_OSLC_RM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.ROOT_SERVICES;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.SERVICE_PROVIDERS_URI_RM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.URI_TEMPLATE_PUBLIC_ROOT_SERVICES;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.wink.client.ClientResponse;
import org.eclipse.lyo.client.oslc.OAuthRedirectException;
import org.eclipse.lyo.client.oslc.OslcOAuthClient;
import org.eclipse.lyo.client.oslc.jazz.JazzRootServicesHelper;

import com.hp.hpl.jena.rdf.model.Model;
import com.ibm.icu.text.MessageFormat;
import com.ibm.rqm.cleaner.internal.client.Credential;
import com.ibm.rqm.cleaner.internal.client.HttpClientException;
import com.ibm.rqm.cleaner.internal.client.OslcHttpClient;
import com.ibm.rqm.cleaner.internal.util.LogUtils;
import com.ibm.rqm.cleaner.internal.util.RdfUtils;
import com.ibm.rqm.cleaner.internal.util.StringUtils;
import com.ibm.rqm.cleaner.internal.util.UrlUtils;
import com.ibm.rqm.integration.client.clientlib.HttpClientConstants;

/**
 * <p>IBM Rational DOORS Web Access (DWA) Requirements Management (RM) OSLC HTTP client.</p>
 * 
 * <p>Note: Requires IBM Rational DOORS Web Access (DWA) 9.5.0 or later.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   1.0
 */
public final class DwaOslcHttpClient extends OslcHttpClient {

	private OslcOAuthClient oslcOAuthClient = null;
	private Map<String, String> eTags = null;
	private final static String JAZZ_LOGOUT_URL = "/service/com.ibm.team.repository.service.internal.ILogoutRestService"; //$NON-NLS-1$

	public DwaOslcHttpClient(URL serverUrl) throws MalformedURLException, GeneralSecurityException {                
		super(serverUrl);
	} 

	@Override
	public String getServiceProvidersPropertyUri(){
		return SERVICE_PROVIDERS_URI_RM;
	}

	@Override
	public String getRootServicesUri() {

		//Note: DOORS Web Access uses a different root services document URL (https://<host>:<port>/<context root>/public/rootservices).
		return (MessageFormat.format(URI_TEMPLATE_PUBLIC_ROOT_SERVICES, new Object[]{getServerUrl()}));
	}    

	@Override
	protected int login() throws IOException {

		final PrintStream SYSTEM_OUT = System.out;

		try{

			//Work-around: Ignore System.out since OslcOAuthClient.getResourceInternal(String, String, boolean) writes unnecessary output. 
			System.setOut(new PrintStream(new ByteArrayOutputStream()));

			//Step 1: Initialize a JazzRootServicesHelper and resolve the OSLC Requirements Management V2.0 catalog:
			//Note: The Jazz server URL MUST not include the /rootservices segment.  
			JazzRootServicesHelper helper = new JazzRootServicesHelper(getRootServicesUri().replace(("/" + ROOT_SERVICES), ""), NAMESPACE_URI_OSLC_RM); //$NON-NLS-1$ //$NON-NLS-2$

			//Step 2: Initialize an OslcOAuthClient:
			Credential credential = getCredential();

			oslcOAuthClient = helper.initOAuthClient(credential.getConsumerKey(), credential.getOAuthSecret());

			try {

				//Step 3: Attempt to GET the context URL to authenticate (OAuth):
				oslcOAuthClient.getResource(getServerUrl(), MEDIA_TYPE_RDF_XML);
			} 
			catch (OAuthRedirectException o) {

				//Step 4: Authenticate (OAuth):
				HttpClient httpClient = oslcOAuthClient.getHttpClient();

				HttpGet redirectGetRequest = new HttpGet(o.getRedirectURL() + "?" + HTTP_HEADER_OAUTH_TOKEN + "=" + o.getAccessor().requestToken); //$NON-NLS-1$ //$NON-NLS-2$
				HttpClientParams.setRedirecting(redirectGetRequest.getParams(), false);
				HttpResponse redirectGetResponse = httpClient.execute(redirectGetRequest);
				EntityUtils.consume(redirectGetResponse.getEntity());

				HttpGet locationGetRequest = new HttpGet(redirectGetResponse.getFirstHeader(HTTP_HEADER_LOCATION).getValue()); 
				HttpClientParams.setRedirecting(locationGetRequest.getParams(), false);
				HttpResponse locationGetResponse = httpClient.execute(locationGetRequest);
				EntityUtils.consume(locationGetResponse.getEntity());

				List<NameValuePair> credentialParameters = new ArrayList<NameValuePair>();
				credentialParameters.add(new BasicNameValuePair(JAZZ_FIELD_USERNAME, credential.getUsername()));
				credentialParameters.add(new BasicNameValuePair(JAZZ_FIELD_PASSWORD, credential.getPassword()));

				HttpPost authenticatePostRequest = new HttpPost(getServerUrl() + JAZZ_URI_FORM_AUTHENTICATION_ACEGI);
				authenticatePostRequest.setEntity(new UrlEncodedFormEntity(credentialParameters, ENCODING_UTF8));
				HttpResponse authenticatePostResponse = httpClient.execute(authenticatePostRequest);
				EntityUtils.consume(authenticatePostResponse.getEntity());

				HttpGet authenticateGetRequest = new HttpGet(authenticatePostResponse.getFirstHeader(HTTP_HEADER_LOCATION).getValue());
				HttpClientParams.setRedirecting(authenticateGetRequest.getParams(), false);
				HttpResponse authenticateGetResponse = httpClient.execute(authenticateGetRequest);
				EntityUtils.consume(authenticateGetResponse.getEntity());

				Map<String, String> queryMap = UrlUtils.getQueryMap(authenticateGetRequest.getURI().toString());

				authenticatePostRequest = new HttpPost(getServerUrl() + JAZZ_URI_FORM_AUTHENTICATION_ACEGI);
				HttpParams authenticatePostRequestParameters = authenticatePostRequest.getParams();
				authenticatePostRequestParameters.setParameter(HTTP_HEADER_OAUTH_TOKEN, queryMap.get(HTTP_HEADER_OAUTH_TOKEN));
				authenticatePostRequestParameters.setParameter(HTTP_HEADER_OAUTH_VERIFIER, queryMap.get(HTTP_HEADER_OAUTH_VERIFIER));
				authenticatePostRequestParameters.setParameter(HTTP_HEADER_OAUTH_AUTHORIZE, Boolean.TRUE.toString());
				authenticatePostRequest.addHeader(HTTP_HEADER_CONTENT_TYPE, MEDIA_TYPE_APPLICATION_X_WWW_FORM_URL_ENCODED_UTF_8); 
				authenticatePostResponse = httpClient.execute(authenticatePostRequest);
				EntityUtils.consume(authenticatePostResponse.getEntity());

				Header contentLengthHeader = authenticatePostResponse.getFirstHeader(HTTP_HEADER_CONTENT_LENGTH); 

				if ((contentLengthHeader != null) && (Integer.parseInt(contentLengthHeader.getValue()) > 0)) {
					throw new InvalidCredentialsException("Authentication to " + getServerUrl() + "failed"); //$NON-NLS-1$ //$NON-NLS-2$
				} 

				//Step 5: Attempt to GET the context URL:
				oslcOAuthClient.getResource(getServerUrl(), MEDIA_TYPE_RDF_XML).getEntity(InputStream.class).close();
			}			
		}
		catch(IOException i){
			throw i;
		}
		catch(Exception e){
			throw (new IOException(e));
		}
		finally{
			System.setOut(SYSTEM_OUT);
		}

		eTags = new HashMap<String, String>();

		//Verify the connection by performing a HEAD request on the root services document:
		return (ping());
	}

	@Override
	public int head(String uri, String queryString) throws IOException{

		//Note: DOORS Web Access does NOT support the HTTP HEAD method (405 Method Not Allowed).
		try{

			ClientResponse clientResponse = oslcOAuthClient.getResource(UrlUtils.resolveUrl(uri, queryString), MEDIA_TYPE_RDF_XML);

			clientResponse.getEntity(InputStream.class).close();

			return (clientResponse.getStatusCode());
		}
		catch(IOException i){
			throw i;
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}   

	@Override
	public int delete(String uri, String queryString) throws IOException {

		//Note: DOORS Web Access does NOT support the HTTP DELETE method (405 Method Not Allowed).
		throw new HttpClientException("HTTP request method DELETE is not allowed.  Permitted request methods are GET, PUT.", HTTP_METHOD_DELETE, uri, HttpStatus.SC_METHOD_NOT_ALLOWED); //$NON-NLS-1$
	}   

	@Override
	public InputStream get(String uri, String queryString, String mediaType) throws IOException {

		try {

			ClientResponse clientResponse = oslcOAuthClient.getResource(UrlUtils.resolveUrl(uri, queryString), (mediaType != null ? mediaType : MEDIA_TYPE_RDF_XML));

			int statusCode = clientResponse.getStatusCode(); 

			if ((statusCode != HttpStatus.SC_OK) && (statusCode != HttpStatus.SC_MOVED_TEMPORARILY) && (statusCode != HttpStatus.SC_SEE_OTHER)) {
				throw new HttpClientException(StringUtils.toString(clientResponse.getEntity(InputStream.class)), HTTP_METHOD_GET, uri, statusCode);
			}   

			eTags.put(uri, clientResponse.getHeaders().getFirst(HTTP_HEADER_ETAG));

			return (clientResponse.getEntity(InputStream.class));
		} 
		catch(IOException i){
			throw i;
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public String getResponseHeader(String uri, String queryString, String mediaType, String responseHeaderName) throws IOException{

		try {

			ClientResponse clientResponse = oslcOAuthClient.getResource(UrlUtils.resolveUrl(uri, queryString), (mediaType != null ? mediaType : MEDIA_TYPE_RDF_XML));

			int statusCode = clientResponse.getStatusCode(); 

			if ((statusCode != HttpStatus.SC_OK) && (statusCode != HttpStatus.SC_MOVED_TEMPORARILY) && (statusCode != HttpStatus.SC_SEE_OTHER)) {
				throw new HttpClientException(StringUtils.toString(clientResponse.getEntity(InputStream.class)), HTTP_METHOD_GET, uri, statusCode);
			}   

			eTags.put(uri, clientResponse.getHeaders().getFirst(HTTP_HEADER_ETAG));

			return (clientResponse.getHeaders().getFirst(responseHeaderName));
		} 
		catch(IOException i){
			throw i;
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public String post(String uri, Model model, String queryString) throws IOException {

		//Note: DOORS Web Access does NOT support the HTTP POST method (405 Method Not Allowed).
		throw new HttpClientException("HTTP request method POST is not allowed.  Permitted request methods are GET, PUT.", HTTP_METHOD_POST, uri, HttpStatus.SC_METHOD_NOT_ALLOWED); //$NON-NLS-1$
	}

	@Override
	public String put(String uri, Model model, String queryString) throws IOException {

		String url = UrlUtils.resolveUrl(uri, queryString);

		try {

			//Assumption: OslcClient.updateResource(String, Object, String) uses the HTTP PUT method.
			//Note: DOORS Web Access requires an If-Match header containing the ETag for all PUT requests.
			ClientResponse clientResponse = oslcOAuthClient.updateResource(url, new ByteArrayInputStream(RdfUtils.write(model).getBytes(ENCODING_UTF8)), MEDIA_TYPE_RDF_XML, MEDIA_TYPE_RDF_XML, eTags.get(uri));

			int statusCode = clientResponse.getStatusCode(); 

			if ((statusCode != HttpStatus.SC_OK) && (statusCode != HttpStatus.SC_MOVED_TEMPORARILY) && (statusCode != HttpStatus.SC_SEE_OTHER)) {

				if (statusCode == HttpStatus.SC_SEE_OTHER) {

					clientResponse.getEntity(InputStream.class).close();

					throw new HttpClientException(clientResponse.getHeaders().getFirst(HTTP_HEADER_CONTENT_LOCATION), HTTP_METHOD_PUT, uri, statusCode);
				} 
				else {
					throw new HttpClientException(StringUtils.toString(clientResponse.getEntity(InputStream.class)), HTTP_METHOD_PUT, uri, statusCode);
				}   
			}  

			eTags.put(uri, clientResponse.getHeaders().getFirst(HTTP_HEADER_ETAG));

			clientResponse.getEntity(InputStream.class).close();
		} 
		catch(IOException i){
			throw i;
		}
		catch (Exception e) {
			throw new IOException(e);
		}

		return url;
	}
	
	@Override
	public int logout() {
		
		if(LogUtils.isTraceEnabled()) {
			LogUtils.logTrace("Logging out of '" + getServerUrl() + "'."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		try {
			String server = this.serverUrl.toString();
			if(server.endsWith(HttpClientConstants.FORWARD_SLASH)) {
				server = server.substring(0, server.length() - 1);
			}
			String url = server + JAZZ_LOGOUT_URL;
			HttpGet get = new HttpGet(url);
			HttpResponse response = oslcOAuthClient.getHttpClient().execute(get);
			
			return response.getStatusLine().getStatusCode();
		}catch(Exception e) {
			LogUtils.logTrace("Error during logging off. ", e); //$NON-NLS-1$
		}
		return 0;
	}
}
