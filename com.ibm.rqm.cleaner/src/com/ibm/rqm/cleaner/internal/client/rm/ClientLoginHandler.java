/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2021. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client.rm;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.FORWARD_SLASH;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_CONSTANTS_BASIC;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_JAZZ_FORM_OAUTH;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_JAZZ_WEB_OAUTH;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_LOCATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_OAUTH_CALLBACK;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_OAUTH_TOKEN;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_SET_COOKIE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_JAZZ_JFS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_NAME;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_JFS_SERVICE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_JFS_STORAGE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_SERVICE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_STORAGE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.URI_TEMPLATE_ROOT_SERVICES;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.URI_TEMPLATE_SERVICE_DOCUMENT;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.ibm.icu.text.MessageFormat;
import com.ibm.rqm.cleaner.internal.util.RdfUtils;
import com.ibm.rqm.cleaner.internal.util.UrlUtils;
import com.ibm.rqm.integration.client.clientlib.HttpClientConstants;

/**
 * <p>Requirements Management (RM) OSLC HTTP client login handler.</p>
 * 
 * <p>Note: Copied from <code>com.ibm.rqm.requirement.service</code> plug-in.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public class ClientLoginHandler implements ILoginHandler {

	/**
		 	// Step (1) The initial method was executed 
			// Step (2) Basic authentication has been requested; configure the HTTP client for basic
			//          authentication
			// Step (3) Otherwise, form authentication has been requested.
			// Step (3a)Process the login by doing a
			//          POST to /jazz/j_security_check
			// Step (3b)After posting the form, the server will return a URL to
			//          resume to the original call. A GET method to this location will
			//          return a token and call back.
			// Step (3c)Process the OAuth confirmation form. For RM 4.0 M8-, expects oauth_token,
			//          oauth_callback and authorize parameters.  For For RM 4.0 RC0+, expects cookies
			//          with the tokens.
			// Step (4) Login and authorization completed. 
			// 			Return the the original method to be executedn 
	 */
	@Override
	public HttpRequestBase authenticate(IRequirementsRepository repository, HttpRequestBase method, HttpResponse response ) throws IOException {
		try {
		
			CloseableHttpClient client = repository.getHttpClient();
	
			String oauthDomainUrl = getOauthDomainUrl(repository);
			
			Header responseHeader = response.getFirstHeader(HTTP_HEADER_JAZZ_WEB_OAUTH);
	
			//Step (2): Configure the HTTP client for basic authentication:
			if(HTTP_CONSTANTS_BASIC.equals(AuthenticationUtil.determineAuthMethod(client, oauthDomainUrl, repository))){
				basicLogin(responseHeader, repository, method);
			}
	
			//Step (3): Configure the HTTP client for form authentication:
			else{
	
				String oauthAuthorizeUrl = null;
				if ( responseHeader != null ) {
					oauthAuthorizeUrl = responseHeader.getValue();
				}
				else {
	
					//temporary backwards compatibility; look for location header
					responseHeader = response.getFirstHeader(HTTP_HEADER_LOCATION);
					if ( responseHeader != null ) {
						oauthAuthorizeUrl = responseHeader.getValue();
					}
				}
	
				HttpGet oauth_authorize = new HttpGet(oauthAuthorizeUrl);
				repository.setFollowRedirect(false);
				HttpResponse response_oauth = repository.executeMethod(oauth_authorize);
				responseHeader = response_oauth.getFirstHeader(HTTP_HEADER_LOCATION);
				String oauth_authorize_url =null;
				if ( responseHeader != null ) {
					oauth_authorize_url = responseHeader.getValue();
				}
				else {
					return method;
				}
				oauth_authorize.releaseConnection();
	
				HttpGet authrequired = new HttpGet(oauth_authorize_url);
				repository.setFollowRedirect(false);
				HttpResponse response_authRequired = repository.executeMethod(authrequired);
	
				/*
				 * The oauthDomainUrl was typically like https://server:port/path where path is /rm.  But if the responseHeaders 
				 * include a JazzFormAuth with Path=x, we will use x in place of the original path.  In addition, 
				 * since the response to change the path is based of of a request that used the oauth_authorize_url, so I will use that URL for the fixup.
				 * 
				 */
				String authUrl = fixAuthDomainUrl(oauth_authorize_url, response_authRequired.getAllHeaders()); 
	
				authrequired.releaseConnection();
	
				// Step (3a)
				//process the login form
				String redirectUri = AuthenticationUtil.formBasedAuthenticate( authUrl != null ? authUrl : oauthDomainUrl , client, repository.getUsernamePasswordCredentials());
	
				// Step (3b)
				HttpGet authenticated_identity = new HttpGet(redirectUri);
				//authenticated_identity.setFollowRedirects(false);
				HttpResponse response_auth_identity = repository.executeMethod(authenticated_identity);
				if(response_auth_identity.getFirstHeader(HTTP_HEADER_LOCATION) == null) {
					// Try Basic auth -> 7.0.3 support
					basicLogin(responseHeader, repository, method);
				}else {
	
					String oauth_authorizeURL = response_auth_identity.getFirstHeader(HTTP_HEADER_LOCATION).getValue();
					authenticated_identity.releaseConnection();
		
					Map<String, String> queryParams = UrlUtils.getQueryMap(oauth_authorizeURL);
		
					// Step (3c)
					//process the oauth authorization form
					//RM 4.0 M8- support:
					if((queryParams.containsKey(HTTP_HEADER_OAUTH_TOKEN)) && (queryParams.containsKey(HTTP_HEADER_OAUTH_CALLBACK))){ 
		
						AuthenticationUtil.formBasedOauthAuthenticate(repository,
								oauthDomainUrl, client, queryParams.get(HTTP_HEADER_OAUTH_TOKEN),
								queryParams.get(HTTP_HEADER_OAUTH_CALLBACK));
		
						method.releaseConnection();
		
						String new1 = queryParams.get(HTTP_HEADER_OAUTH_CALLBACK); 
						new1 = UrlUtils.urlDecode(new1);
						URI requestURI = new URI(new1);
						String currentQuery = requestURI.getQuery();
		
						String OAuthToken = HTTP_HEADER_OAUTH_TOKEN + "=" + queryParams.get(HTTP_HEADER_OAUTH_TOKEN); //$NON-NLS-1$
		
						String targetQuery = OAuthToken;
						if (currentQuery != null && currentQuery.length() > 0)
							new1 = new1 + "&" + OAuthToken; //$NON-NLS-1$
						else
							new1 = new1 + "?" + OAuthToken; //$NON-NLS-1$
						method.setURI(new URI(new1));
		
						// clear the cookies that we used to get the access tokens; otherwise, if another httpclient 
						// accesses the server, it will get different cookies and these will be invalid if this httpclent
						// is still using the old copies4
						// JSESSIONID=7A862302221530893B63223BA0D51286, 
						// JazzFormAuth=Form, 
						// X-com-ibm-team-repository-servlet-identity=tpRMA8CLj5q9y/qlVjxm53A/kUuDT/eNUa0bEPIjSJs=]
						//client.getState().clearCookies();
					}
		
					//RM 4.0 RC0+ support:
					//Note: Do NOT clear the cookies since they contain the tokens.
					else{
		
						HttpGet oauthAuthorize = new HttpGet(oauth_authorizeURL);
						repository.setFollowRedirect(false);
						HttpResponse response_oauthAuthorize = repository.executeMethod(oauthAuthorize);
						if(response_oauthAuthorize.getFirstHeader(HTTP_HEADER_LOCATION) == null)
							throw new HttpException("Can not pass the authorization, please verify your RM provider had been configure correctly");//$NON-NLS-1$
		
						String oauthCallbackURL = response_oauthAuthorize.getFirstHeader(HTTP_HEADER_LOCATION).getValue();
						oauthAuthorize.releaseConnection();
		
						HttpGet oauthCallback = new HttpGet(oauthCallbackURL);
						repository.setFollowRedirect(false);
						repository.executeMethod(oauthCallback);
						oauthCallback.releaseConnection();					
					}
				}
			}
			repository.setFollowRedirect(true);
			return method;
		}catch(Exception e) {
			throw new IOException(e);
		}
	}
	
	private void basicLogin(Header responseHeader, IRequirementsRepository repository, HttpRequestBase method) throws IOException, URISyntaxException {
		//Forward the authentication credentials to the server via an authorization request using the 'X-jazz-web-oauth-url' response header value.
		//Note: WebSphere Application Server requires the authentication credentials.

		if(responseHeader != null) {

			HttpGet oauth_authorize = new HttpGet(responseHeader.getValue());

			//Note: When the server is configured with basic authentication (only), the authorization request may result in a series of HTTP redirects (302) that MUST be followed.
			repository.setFollowRedirect(true);

			HttpResponse oauth_response = repository.executeMethod(oauth_authorize);		

			oauth_authorize.releaseConnection();

			//Resolve the original HTTP method URI + access tokens (e.g. request_token_secret/oauth_token):
			responseHeader = oauth_response.getFirstHeader(HTTP_HEADER_LOCATION);

			if (responseHeader != null) {

				oauth_authorize.releaseConnection();
				oauth_authorize.setURI(new URI(responseHeader.getValue()));
			}
		}
	}

	private String getOauthDomainUrl(IRequirementsRepository repository) throws IOException {

		String oauthDomainUrl = null;
		HttpClient client = repository.getHttpClient();

		//Resolve the root services document URI:
		String rootServicesDocumentUri = MessageFormat.format(URI_TEMPLATE_ROOT_SERVICES, new Object[]{repository.getUrlString()});

		//Resolve the root services document:
		HttpGet getMethod = new HttpGet(rootServicesDocumentUri);

		HttpResponse response = repository.executeMethod(getMethod);
		HttpEntity entity = response.getEntity();
		String message = EntityUtils.toString(entity, HttpClientConstants.UTF8);

		Model rootServicesDocumentModel = RdfUtils.getDefaultModel();

		//Note: Use the root services document URI for converting relative URIs in the model to absolute URIs to eliminate reading/writing errors.		
		rootServicesDocumentModel.read(new BufferedInputStream(new ByteArrayInputStream(message.getBytes(HttpClientConstants.UTF8))), rootServicesDocumentUri);

		//Resolve the first jfs:storage element in the root services document:
		StmtIterator storageStatementsIterator = rootServicesDocumentModel.listStatements(null, rootServicesDocumentModel.createProperty(NAMESPACE_URI_JAZZ_JFS + RESOURCE_STORAGE), ((RDFNode)(null)));

		if(storageStatementsIterator.hasNext()){

			oauthDomainUrl = storageStatementsIterator.next().getResource().getURI();
			oauthDomainUrl = (oauthDomainUrl.substring(0, oauthDomainUrl.lastIndexOf(FORWARD_SLASH)));
		}

		//Backward compatibility with RDNG 5.0 (or before).
		if(oauthDomainUrl == null){

			try {

				//Resolve the service document URI:
				String serviceDocumentUri = MessageFormat.format(URI_TEMPLATE_SERVICE_DOCUMENT, new Object[]{repository.getUrlString()});

				//Resolve the service document:
				getMethod = new HttpGet(serviceDocumentUri);

				response = repository.executeMethod(getMethod);
				entity = response.getEntity();
				message = EntityUtils.toString(entity, HttpClientConstants.UTF8);

				SAXParserFactory saxParserFactory  = SAXParserFactory.newInstance();
				saxParserFactory.setNamespaceAware(true);

				SAXParser saxParser = saxParserFactory.newSAXParser();

				ServiceDocumentHandler serviceDocumentHandler = new ServiceDocumentHandler();

				saxParser.parse(new BufferedInputStream(new ByteArrayInputStream(message.getBytes(HttpClientConstants.UTF8))), serviceDocumentHandler);

				oauthDomainUrl = serviceDocumentHandler.getStorageServiceUri();
				oauthDomainUrl = (oauthDomainUrl.substring(0, oauthDomainUrl.lastIndexOf(FORWARD_SLASH)));
			} 
			catch (Exception e) {
				throw new IOException(e);
			}
		}

		return oauthDomainUrl;	
	}		

	/**
	 * If the response headers include a JazzFormAuth header return a url that uses the original url with the new path. 
	 * Otherwise, if the response headers include a Location header, return the URL in the response header value. 
	 * Otherwise return null.
	 * @param uristring
	 * @param responseHeaders
	 * @return
	 */
	private String fixAuthDomainUrl(String uristring, Header[] responseHeaders) {
		if (responseHeaders != null) {
			for (Header h : responseHeaders) {
				if (h.getName().equalsIgnoreCase(HTTP_HEADER_SET_COOKIE)) {
					String v = h.getValue();

					if (v.startsWith(HTTP_HEADER_JAZZ_FORM_OAUTH)) {
						int pathIndex = v.indexOf(" Path=");	//$NON-NLS-1$
						if (pathIndex >= 0) {
							int semiIndex = v.indexOf(';',pathIndex);
							if (semiIndex >= 0) {
								try {
									String path = v.substring(pathIndex+6, semiIndex);
									URI uri = new URI(uristring);
									URI uriFixed = new URI(uri.getScheme(),null/*userInfo*/,uri.getHost(), uri.getPort(), path, null/*query*/, null);
									return uriFixed.toString();
								} catch (URISyntaxException e) {
									
								}
							}
						}

					}
				}
			}

			//Background: DNG uses delegated authentication (delegates to the JTS for authentication), compared to RTC/RQM that uses container authentication 
			//            (authenticated with a web container such as Liberty).  
			//Problem:    When DNG is installed with a remote JTS (distributed typology), DNG authentication delegates to the remote JTS (different host) but 
			//            the HTTP client is attempting to authenticate with a JTS on the same host as the DNG server. 
			//Solution:   Resolve the authentication URL for the remote JTS from the Location response header, if exists.
			//See:        https://jazz.net/wiki/bin/view/Main/NativeClientAuthentication#FORM_challenge.
			for (Header header : responseHeaders) {

				if ((header != null) && (HTTP_HEADER_LOCATION.equalsIgnoreCase(header.getName()))) {
					return (header.getValue());
				}
			}
		}
		return null;
	}

	class ServiceDocumentHandler extends DefaultHandler {

		private String storageServiceUri = null;

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

			if ((RESOURCE_SERVICE.equals(name)) || (RESOURCE_JFS_SERVICE.equals(name))) { 

				String serviceName = attributes.getValue(PROPERTY_NAME);

				if(RESOURCE_JFS_STORAGE.equals(serviceName)){
					storageServiceUri = attributes.getValue(PROPERTY_URL);
				}
			}
		}

		public String getStorageServiceUri() {
			return storageServiceUri;
		}
	};

}
