/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client.rm;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.ENCODING_UTF8;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_ACCEPT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_CONTENT_LOCATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_DOORS_RP_REQUEST_TYPE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_IF_MATCH;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_NET_JAZZ_JFS_OWNING_CONTEXT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_OSLC_CONFIGURATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_VALUE_PRIVATE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_X_RM_QUERY_TIMEOUT_ENABLE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_METHOD_GET;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_METHOD_POST;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_METHOD_PUT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.MEDIA_TYPE_RDF_XML;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_COMPONENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_PROJECT_AREA;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_PROJECT_AREAS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_WILDCARD;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.QUERY_PARAMETER_DNG_ACCEPT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.QUERY_PARAMETER_DNG_PRIVATE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.SERVICE_PROVIDERS_URI_RM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.URI_TEMPLATE_DNG_PROJECTS;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hp.hpl.jena.rdf.model.Model;
import com.ibm.icu.text.MessageFormat;
import com.ibm.rqm.cleaner.internal.client.Credential;
import com.ibm.rqm.cleaner.internal.client.HttpClientException;
import com.ibm.rqm.cleaner.internal.client.OslcHttpClient;
import com.ibm.rqm.cleaner.internal.util.CleanerUtils;
import com.ibm.rqm.cleaner.internal.util.LogUtils;
import com.ibm.rqm.cleaner.internal.util.RdfUtils;
import com.ibm.rqm.cleaner.internal.util.StringUtils;
import com.ibm.rqm.cleaner.internal.util.UrlUtils;
import com.ibm.rqm.cleaner.internal.util.XmlUtils;
import com.ibm.rqm.integration.client.clientlib.HttpClientConstants;

/**
 * <p>Requirements Management (RM) OSLC HTTP client.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public final class RmOslcHttpClient extends OslcHttpClient {
    
	private Repository repository = null;
	private Map<String, String> eTags = null;
	private boolean disableDngQueryTimeout = false;
	private final RRCRestClient rrcRestClient;
	private final Map<String, String> componentUrlOwningContextUrlMap;
	
	private final static String JAZZ_LOGOUT_URL = "/service/com.ibm.team.repository.service.internal.ILogoutRestService"; //$NON-NLS-1$

	/**
	 * <p>The component URL, required to resolve the owning context URL used in all HTTP requests 
	 * to the project area associated with this OSLC HTTP client.</p>
	 * 
	 * <p>The owning context URL will be included in the <code>net.jazz.jfs.owning-context</code> request 
	 * header for all HTTP requests to the project area associated with this OSLC HTTP client, if supported.</p>
	 */
	private String componentUrl = null;

	public RmOslcHttpClient(URL serverUrl) throws MalformedURLException, GeneralSecurityException {                

    	super(serverUrl);
    	
    	this.rrcRestClient = new RRCRestClient();
    	this.componentUrlOwningContextUrlMap = new HashMap<String, String>();
    }
   
    @Override
	public Map<String, String> getRequestHeaders() {

    	Map<String, String> requestHeaders = super.getRequestHeaders();

    	requestHeaders.put(HTTP_HEADER_DOORS_RP_REQUEST_TYPE, HTTP_HEADER_VALUE_PRIVATE);

    	String configurationContextUrl = getConfigurationContextUrl();

    	//Note: IBM Rational DOORS Next Generation consumes the oslc.configuration request header.
    	if(StringUtils.isSet(configurationContextUrl)) {
    		requestHeaders.put(HTTP_HEADER_OSLC_CONFIGURATION, configurationContextUrl);
    	}

    	//Note: IBM Rational DOORS Next Generation consumes the net.jazz.jfs.owning-context request header.
    	if(StringUtils.isSet(componentUrl)) {

    		String owningContextUrl = componentUrlOwningContextUrlMap.get(componentUrl);

    		if(StringUtils.isSet(owningContextUrl)) {
    			requestHeaders.put(HTTP_HEADER_NET_JAZZ_JFS_OWNING_CONTEXT, owningContextUrl);
    		}
    	}

    	if(disableDngQueryTimeout) {
    		requestHeaders.put(HTTP_HEADER_X_RM_QUERY_TIMEOUT_ENABLE, Boolean.FALSE.toString());
    	}

		return requestHeaders;
	}
    
    /**
	 * Logs out of the RM server.
	 * @return The response code of the logout
	 */
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
			RequestResponse response = rrcRestClient.performGet(repository, url, null);
			if(response.getResponseCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
				Map<String, String> headers = response.getResponseHeaders();
				if(headers != null && headers.containsKey(HttpClientConstants.LOCATION)) {
					url = headers.get(HttpClientConstants.LOCATION);
					response = rrcRestClient.performGet(repository, url, null);
				}
			}
			
			return response.getResponseCode();
		}catch(Exception e) {
			LogUtils.logTrace("Error during logging off. ", e); //$NON-NLS-1$
		}
		return 0;
	}
    
    @Override
	public String getServiceProvidersPropertyUri(){
    	return SERVICE_PROVIDERS_URI_RM;
    }

    public String getComponentUrl() {
  		return componentUrl;
  	}

  	public void setComponentUrl(String componentUrl) {
  		this.componentUrl = componentUrl;
  	}
	
  	public boolean isDisableDngQueryTimeout() {
		return disableDngQueryTimeout;
	}

	public void setDisableDngQueryTimeout(boolean disableDngQueryTimeout) {
		this.disableDngQueryTimeout = disableDngQueryTimeout;
	}
	
    @Override
	protected int login(Credential credential) throws IOException{

    	int returnCode = super.login(credential);

    	if ((returnCode == HttpStatus.SC_OK) || (returnCode == HttpStatus.SC_MOVED_TEMPORARILY) || (returnCode == HttpStatus.SC_SEE_OTHER)) {

    		//Resolve all the component URLs and owning context URLs from the (private) DNG projects API:
    		//Note: IBM Rational DOORS Next Generation requires the net.jazz.jfs.owning-context request header set to the owning context URL.
    		if(componentUrlOwningContextUrlMap.isEmpty()){

    			try {

    				String projectAreasUrl = MessageFormat.format(URI_TEMPLATE_DNG_PROJECTS, new Object[]{getServerUrl()});

    				Document projectAreasDocument = XmlUtils.read(this, (projectAreasUrl + "?" + QUERY_PARAMETER_DNG_ACCEPT + "=" + UrlUtils.urlEncode(PROPERTY_WILDCARD) + "&" + QUERY_PARAMETER_DNG_PRIVATE + "=" + UrlUtils.urlEncode(Boolean.TRUE.toString().toLowerCase()))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    				XPath xPath = XPathFactory.newInstance().newXPath();

    				NodeList projectAreaNodes = ((NodeList)(xPath.compile("/" + PROPERTY_PROJECT_AREAS + "/" + PROPERTY_PROJECT_AREA).evaluate(projectAreasDocument, XPathConstants.NODESET))); //$NON-NLS-1$ //$NON-NLS-2$

    				for (int childrenIndex = 0; childrenIndex < projectAreaNodes.getLength(); childrenIndex++) {

    					Node projectAreaNode = projectAreaNodes.item(childrenIndex);

    					String componentUrl = xPath.compile(PROPERTY_COMPONENT).evaluate(projectAreaNode);

    					String owningContextUrl = xPath.compile(PROPERTY_URL).evaluate(projectAreaNode); 

    					if((StringUtils.isSet(componentUrl)) && (StringUtils.isSet(owningContextUrl))) {
    						componentUrlOwningContextUrlMap.put(componentUrl, owningContextUrl);
    					}
    				}

    				if(componentUrlOwningContextUrlMap.isEmpty()) {
    					LogUtils.logError("Error resolving the component URL to owning context URL mappings for server '" + getServerUrl() + "'."); //$NON-NLS-1$ //$NON-NLS-2$
    				}
    				else if(LogUtils.isTraceEnabled()) {
    					LogUtils.logTrace("Caching component URL to owning context URL mappings for server '" + getServerUrl() + "': " + CleanerUtils.toString(componentUrlOwningContextUrlMap)); //$NON-NLS-1$ //$NON-NLS-2$
    				}
    			}
    			catch (Exception e) {
    				LogUtils.logError("Error resolving the component URL to owning context URL mappings for server '" + getServerUrl() + "'.", e); //$NON-NLS-1$ //$NON-NLS-2$	
    			}
    		}		
    	}

    	return returnCode;
	}
    
	@Override
	protected int login() throws IOException {
		
		repository = new Repository(getServerUrl(), getCredential());

		eTags = new HashMap<String, String>();
		
		//Verify the connection by performing a HEAD request on the root services document:
		return (ping());
	}

	@Override
	public int head(String uri, String queryString) throws IOException {

		int responseCode = rrcRestClient.performHead(repository, UrlUtils.resolveUrl(uri, UrlUtils.appendQueryString(queryString, getRequestParameters())), getRequestHeaders()).getResponseCode();
		
		//Problem: Rational DOORS Next Generation (RDNG) may NOT support the HTTP HEAD method (405 Method Not Allowed or 501 Not Implemented).
		//Work-around: Use the HTTP GET method.
		//Defect: 80536
		if ((responseCode == HttpStatus.SC_METHOD_NOT_ALLOWED) || (responseCode == HttpStatus.SC_NOT_IMPLEMENTED)) {
			responseCode = rrcRestClient.performGet(repository, UrlUtils.resolveUrl(uri, UrlUtils.appendQueryString(queryString, getRequestParameters())), getRequestHeaders()).getResponseCode();
		}
		
		return responseCode;
	}   

	@Override
	public int delete(String uri, String queryString) throws IOException {
		return (rrcRestClient.performDelete(repository, UrlUtils.resolveUrl(uri, UrlUtils.appendQueryString(queryString, getRequestParameters())), getRequestHeaders()).getResponseCode());
	}   

	@Override
	public InputStream get(String uri, String queryString, String mediaType) throws IOException {
		
		Map<String, String> getRequestHeaders = new HashMap<String, String>(getRequestHeaders());
		
		if(mediaType != null){
			getRequestHeaders.put(HTTP_HEADER_ACCEPT, mediaType);
		}
		
		RequestResponse requestResponse = rrcRestClient.performGet(repository, UrlUtils.resolveUrl(uri, UrlUtils.appendQueryString(queryString, getRequestParameters())), getRequestHeaders);
		
		int responseCode = requestResponse.getResponseCode(); 
		
		if ((responseCode != HttpStatus.SC_OK) && (responseCode != HttpStatus.SC_MOVED_TEMPORARILY) && (responseCode != HttpStatus.SC_SEE_OTHER)) {
			throw new HttpClientException(StringUtils.toString(requestResponse.getStream()), HTTP_METHOD_GET, uri, responseCode);
		}   
		
		eTags.put(uri, requestResponse.getETag());
		
		return (requestResponse.getStream());
	}      
	
	@Override
	public String getResponseHeader(String uri, String queryString, String mediaType, String responseHeaderName) throws IOException{

		Map<String, String> getRequestHeaders = new HashMap<String, String>(getRequestHeaders());
		
		if(mediaType != null){
			getRequestHeaders.put(HTTP_HEADER_ACCEPT, mediaType);
		}
		
		RequestResponse requestResponse = rrcRestClient.performGet(repository, UrlUtils.resolveUrl(uri, UrlUtils.appendQueryString(queryString, getRequestParameters())), getRequestHeaders);
		
		int responseCode = requestResponse.getResponseCode(); 
		
		if ((responseCode != HttpStatus.SC_OK) && (responseCode != HttpStatus.SC_MOVED_TEMPORARILY) && (responseCode != HttpStatus.SC_SEE_OTHER)) {
			throw new HttpClientException(StringUtils.toString(requestResponse.getStream()), HTTP_METHOD_GET, uri, responseCode);
		}   
		
		eTags.put(uri, requestResponse.getETag());

		return (requestResponse.getResponseHeaderValue(responseHeaderName));
	}
	
	@Override
	public String post(String uri, Model model, String queryString) throws IOException {
		
		RequestResponse requestResponse = rrcRestClient.performPost(repository, UrlUtils.resolveUrl(uri, UrlUtils.appendQueryString(queryString, getRequestParameters())), new ByteArrayInputStream(RdfUtils.write(model).getBytes(ENCODING_UTF8)), MEDIA_TYPE_RDF_XML, getRequestHeaders());

		int responseCode = requestResponse.getResponseCode(); 

		if ((responseCode != HttpStatus.SC_OK) && (responseCode != HttpStatus.SC_CREATED)) {
			
			if (responseCode == HttpStatus.SC_SEE_OTHER) {
				throw new HttpClientException(requestResponse.getResponseHeaders().get(HTTP_HEADER_CONTENT_LOCATION), HTTP_METHOD_POST, uri, responseCode);
			} 
			else {
				throw new HttpClientException(StringUtils.toString(requestResponse.getStream()), HTTP_METHOD_POST, uri, responseCode);
			}   
		}   
		
		return uri;
	}

	@Override
	public String put(String uri, Model model, String queryString) throws IOException {

		//Note: DOORS Next Generation requires an If-Match header containing the ETag for all PUT requests.
		Map<String, String> putRequestHeaders = new HashMap<String, String>(getRequestHeaders());
		putRequestHeaders.put(HTTP_HEADER_IF_MATCH, eTags.get(uri));
		
		RequestResponse requestResponse = rrcRestClient.performPut(repository, UrlUtils.resolveUrl(uri, UrlUtils.appendQueryString(queryString, getRequestParameters())), new ByteArrayInputStream(RdfUtils.write(model).getBytes(ENCODING_UTF8)), MEDIA_TYPE_RDF_XML, putRequestHeaders);
		
		int responseCode = requestResponse.getResponseCode(); 

		if ((responseCode != HttpStatus.SC_OK) && (responseCode != HttpStatus.SC_CREATED)) {
			
			if (responseCode == HttpStatus.SC_SEE_OTHER) {
				throw new HttpClientException(requestResponse.getResponseHeaders().get(HTTP_HEADER_CONTENT_LOCATION), HTTP_METHOD_PUT, uri, responseCode);
			} 
			else {
				throw new HttpClientException(StringUtils.toString(requestResponse.getStream()), HTTP_METHOD_PUT, uri, responseCode);
			}   
		}   
		
		eTags.put(uri, requestResponse.getETag());
		
		return uri;
	}   
}
