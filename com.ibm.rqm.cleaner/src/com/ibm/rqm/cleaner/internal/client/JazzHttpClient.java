/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.ENCODING_UTF8;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_ACCEPT_LANGUAGE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_CONFIGURATION_CONTEXT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.MEDIA_TYPE_RDF_XML;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.REQUEST_PARAMETER_OSLC_CONFIG_CONTEXT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.URI_TEMPLATE_ROOT_SERVICES;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;

import com.hp.hpl.jena.rdf.model.Model;
import com.ibm.icu.text.MessageFormat;
import com.ibm.rqm.cleaner.internal.util.LogUtils;
import com.ibm.rqm.cleaner.internal.util.RdfUtils;
import com.ibm.rqm.cleaner.internal.util.StringUtils;
import com.ibm.rqm.cleaner.internal.util.UrlUtils;
import com.ibm.rqm.integration.client.clientlib.HttpClientConstants;
import com.ibm.rqm.integration.client.clientlib.JFSHttpsClient;

/**
 * <p>Jazz HTTP client.</p>
 * 
 * <p><b>Note:</b> Any new methods added to this abstract class <i>may</i> 
 * need to be overridden in one or more of the subclasses.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public abstract class JazzHttpClient extends JFSHttpsClient {

	protected URL serverUrl;
	protected Credential credential;

	/**
	 * <p>The configuration context URL used in all HTTP requests to the project area associated with this 
	 * OSLC HTTP client.</p>
	 * 
	 * <p>The configuration context URL will be included in the <code>Configuration-Context</code> and 
	 * <code>oslc.configuration</code> request headers and <code>oslc_config.context</code> query parameter 
	 * for all HTTP requests to the project area associated with this OSLC HTTP client, if supported.</p>
	 */
	private String configurationContextUrl = null;

	/**
	 * <p>Language value of the <code>Accept-Language</code> request header for all requests.</p>
	 */
	private String acceptLanguage = null;
	
	public String getConfigurationContextUrl() {
		return configurationContextUrl;
	}

	public void setConfigurationContextUrl(String configurationContextUrl) {
		this.configurationContextUrl = configurationContextUrl;
	}

	public String getAcceptLanguage() {
		return acceptLanguage;
	}

	public void setAcceptLanguage(String acceptLanguage) {
		this.acceptLanguage = acceptLanguage;
	}
	
	protected JazzHttpClient(URL server) throws MalformedURLException, GeneralSecurityException {
		super(server.getProtocol(), server.getHost(), server.getPort(), server.getPath());
		this.serverUrl = server;
	}
	
	public Map<String, String> getRequestHeaders() {

		Map<String, String> requestHeaders = new HashMap<String, String>();

		if(StringUtils.isSet(configurationContextUrl)) {
			requestHeaders.put(HTTP_HEADER_CONFIGURATION_CONTEXT, configurationContextUrl);
		}

		if(StringUtils.isSet(acceptLanguage)) {
			requestHeaders.put(HTTP_HEADER_ACCEPT_LANGUAGE, acceptLanguage);
		}

		return requestHeaders;
	}
	
	public Map<String, String> getRequestParameters() {

		Map<String, String> requestParameters = new HashMap<String, String>();

		if(StringUtils.isSet(configurationContextUrl)) {
			requestParameters.put(REQUEST_PARAMETER_OSLC_CONFIG_CONTEXT, UrlUtils.urlEncode(configurationContextUrl));
		}

		return requestParameters;
	}

	public String getRootServicesUri() {
		return (MessageFormat.format(URI_TEMPLATE_ROOT_SERVICES, new Object[]{getServerUrl()}));
	}  
	

	public int ping() throws IOException{

		if(LogUtils.isTraceEnabled()) {
			LogUtils.logTrace("Pinging root services document '" + getRootServicesUri() + "' for '" + getServerUrl() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		int returnCode = HttpStatus.SC_BAD_REQUEST; 

		//Note: Ping requests (root services document) do not require a configuration context.
		final String currentConfigurationContextUrl = getConfigurationContextUrl();

		setConfigurationContextUrl(null);

		try {

			//Verify the connection by performing a HEAD/GET request on the root services document (does not require authentication):
			returnCode = (head(getRootServicesUri()));

			if((returnCode == HttpStatus.SC_METHOD_NOT_ALLOWED) || (returnCode == HttpStatus.SC_NOT_IMPLEMENTED)) {
				returnCode = (getStatusCode(getRootServicesUri()));
			}
		}
		finally {
			setConfigurationContextUrl(currentConfigurationContextUrl);
		}

		if(LogUtils.isTraceEnabled()) {
			LogUtils.logTrace("Pinged root services document '" + getRootServicesUri() + "' for '" + getServerUrl() + "': " + returnCode); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		return returnCode;
	}
	
	protected int login(Credential credential) throws IOException {	    

		if(LogUtils.isTraceEnabled()) {
			LogUtils.logTrace("Logging in to '" + getServerUrl() + "' from the JazzHttpClint."); //$NON-NLS-1$ //$NON-NLS-2$
		}

		this.credential = credential;

		int returnCode = login();
		
		if(LogUtils.isTraceEnabled()) {
			LogUtils.logTrace("Logged in to '" + getServerUrl() + "' from the JazzHttpClint: " + returnCode); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return returnCode;
	}

	protected int login() throws IOException {
		return super.login(credential.getUsername(), credential.getPassword());
	}

	
	/**
	 * Remove unicode characters not supported by XML.  The XML spec only
	 * allows the following characters:
	 * 
	 * 	Char ::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
	 * 
	 * Also remove invalid entities such as &#11;
	 * 
	 * See workitem 42573
	 * 
	 * @see http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char
	 */
	//Note: Copied from com.ibm.rqm.integration.service.Util.scrubXmlCharacters(String).
	private static String scrubXmlCharacters (String s) {
		if (s == null || "".equals(s)) { //$NON-NLS-1$
			return s;
		}

		// Escaped control characters such as &#11; are not
		// valid in xml 1.0.  Including them in the xml would
		// cause any well written xml parser to fail.  These
		// characters need to be unescaped in the string so
		// that they can be checked by the character range
		// check below
		Pattern entityEscapePattern = Pattern.compile("&#(\\d+);"); //$NON-NLS-1$
		Matcher matcher = entityEscapePattern.matcher(s);
		while (matcher.find()) {
			int unescaped = Integer.valueOf(matcher.group(1));
			char character = ((char)(unescaped));
			if (!isValidXmlCharacter(character)) {
				s = s.replaceAll(matcher.group(), Character.toString(character));
			}
		}

		// ensure that all characters in the string are
		// in the range:
		// #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
		// characters not in this range will be omitted
		// from the string
		final int len = s.length();
		final StringBuilder builder = new StringBuilder();
		for (int i=0; i<len; i++) {
			char c = s.charAt(i);

			//Escape special characters (including Microsoft smart punctuation) with a decimal numeric character reference:
			//  0x2013: en dash
			//  0x2014: em dash 
			//  0x2032: prime 
			//  0x201C: left double quotation mark
			//  0x201D: right double quotation mark
			//  0x2018: left single quotation mark
			//  0x2019: right single quotation mark
			//  0x00A0: no-break space
			//  0x00AD: soft hyphen
			//  0x00B5: micro sign
			//  0x0247: latin small letter e with stroke
			//  0x2011: non-breaking hyphen
			//  0x2026: horizontal ellipsis
			if((c == 0x2013) ||
					(c == 0x2014) ||
					(c == 0x2032) ||
					(c == 0x201C) ||
					(c == 0x201D) ||
					(c == 0x2018) ||
					(c == 0x2019) ||
					(c == 0x00A0) ||
					(c == 0x00AD) ||
					(c == 0x00B5) ||
					(c == 0x0247) ||
					(c == 0x2011) ||
					(c == 0x2026)){
				builder.append("&#"); //$NON-NLS-1$
				builder.append(((int)(c)));
				builder.append(";"); //$NON-NLS-1$
			}
			else if (isValidXmlCharacter(c)){
				builder.append(c);
			}
			else if (Character.isHighSurrogate(c) && (i+1<len)) {
				char c2 = s.charAt(i+1);
				if (Character.isLowSurrogate(c2)) {
					builder.append("&#"); //$NON-NLS-1$
					builder.append(Character.toCodePoint(c, c2));
					builder.append(";"); //$NON-NLS-1$
				}
			}
		}

		return builder.toString();
	}

	/**
	 * <p>Determines if a character is a valid XML character.</p>
	 * 
	 * <p>The <a href="http://www.w3.org/TR/2006/REC-xml11-20060816/#charsets">XML 1.1</a>  
	 * specification defines the following characters as valid XML characters:</p>
	 * 
	 * <ul>
	 * <li>#x9</li>
	 * <li>#xA</li>
	 * <li>#xD</li>
	 * <li>#x20-#xD7FF</li>
	 * <li>#xE000-#xFFFD</li>
	 * <li>#x10000-#x10FFFF</li>
	 * </ul>
	 * 
	 */
	private static boolean isValidXmlCharacter(char character) {
		return ((character == 0x9) ||
				(character == 0xA) ||
				(character == 0xD) ||
				((character >= 0x20) && (character <= 0xD7FF)) ||
				((character >= 0xE000) && (character <= 0xFFFD)) ||
				((character >= 0x10000) && (character <= 0x10FFFF)));		
	}
	
	public int lock(String uri) throws IOException{
		//Fix headers:
		Map<String, String> requestHeaders = getRequestHeaders();
		//Fix request parameters:
		String query = UrlUtils.appendQueryString("", getRequestParameters()); //$NON-NLS-1$
		
		Map responseMap = lock(new URL(uri), query, requestHeaders);

		return (Integer) responseMap.get(HttpClientConstants.RQM_RESPONSE_CODE);
	}
	
	public int unlock(String uri) throws IOException{

		//Fix headers:
		Map<String, String> requestHeaders = getRequestHeaders();
		//Fix request parameters:
		String query = UrlUtils.appendQueryString("", getRequestParameters()); //$NON-NLS-1$
		
		Map responseMap = unlock(new URL(uri), query, requestHeaders);

		return (Integer) responseMap.get(HttpClientConstants.RQM_RESPONSE_CODE);
	}

	public int delete(String uri) throws IOException, URISyntaxException{
		return (delete(uri, null));
	}

	public int delete(String uri, String queryString) throws IOException, URISyntaxException{
		
		//Fix headers:
		Map<String, String> requestHeaders = getRequestHeaders();
		//Fix request parameters:
		String query = UrlUtils.appendQueryString(queryString, getRequestParameters());
		
		URIBuilder builder = new URIBuilder(uri);
		
		if(query != null) {
			builder.setQuery(query);
		}

		Map responseMap =  deleteFromServer(builder.build().toURL(), requestHeaders);
		
		return (Integer) responseMap.get(HttpClientConstants.RQM_RESPONSE_CODE);
	}
	
	public int head(String uri) throws IOException {
		return (head(uri, null));
	}
	
	public int head(String uri, String queryString) throws IOException{
		
		//Fix headers:
		Map<String, String> requestHeaders = getRequestHeaders();
		//Fix request parameters:
		String query = UrlUtils.appendQueryString(queryString, getRequestParameters());
		
		Map responseMap = head(new URL(uri), query, requestHeaders);

		return (Integer) responseMap.get(HttpClientConstants.RQM_RESPONSE_CODE);
	}

	public InputStream get(String uri) throws IOException{
		return (get(uri, null));
	}
	
	public InputStream get(String uri, String queryString) throws IOException{
		return (get(uri, queryString, null));
	}		

	public InputStream get(String uri, String queryString, String mediaType) throws IOException{

		Map responseMap = handleGet(uri, queryString, mediaType);

		String responseMsg = (String) responseMap.get(HttpClientConstants.RQM_RESPONSE_MESSAGE);
		
		return new ByteArrayInputStream(responseMsg.getBytes(ENCODING_UTF8));
	}

	public int getStatusCode(String uri) throws IOException {
		return (getStatusCode(uri, null));
	}

	public int getStatusCode(String uri, String queryString) throws IOException {
		return (getStatusCode(uri, queryString, null));
	}
	
	public int getStatusCode(String uri, String queryString, String mediaType) throws IOException {
		
		Map responseMap = handleGet(uri, queryString, mediaType);
		
		return (Integer) responseMap.get(HttpClientConstants.RQM_RESPONSE_CODE);
	}

	public String getResponseHeader(String uri, String responseHeaderName) throws IOException{
		return (getResponseHeader(uri, null, responseHeaderName));
	}
	
	public String getResponseHeader(String uri, String queryString, String responseHeaderName) throws IOException{
		return (getResponseHeader(uri, queryString, null, responseHeaderName));
	}		

	public String getResponseHeader(String uri, String queryString, String mediaType, String responseHeaderName) throws IOException{
		Map responseMap = handleGet(uri, queryString, mediaType);
		
		if(responseMap.containsKey(responseHeaderName)) {
			return (String) responseMap.get(responseHeaderName);
		}
		
		return null;
	}

	private Map handleGet(String uri, String queryString, String mediaType) throws IOException{
		//Fix headers:
		Map<String, String> requestHeaders = getRequestHeaders();
		//Fix request parameters:
		String query = UrlUtils.appendQueryString(queryString, getRequestParameters());
		
		return getFromServer(new URL(uri), query, requestHeaders);
	}
	
	protected String interSendToServer(boolean isPut, String uri, Model model, String queryString) throws IOException {
		//Fix headers:
		Map<String, String> requestHeaders = getRequestHeaders();
		//Fix request parameters:
		String query = UrlUtils.appendQueryString(queryString, getRequestParameters());
		StringEntity xml = new StringEntity(scrubXmlCharacters(RdfUtils.write(model)), MEDIA_TYPE_RDF_XML, ENCODING_UTF8);
		
		internalSendToServer(
				isPut, 
				new URL(uri), 
				xml,
				false,
				query,
				requestHeaders);
		
		return uri;
	}
	
	public String put(String uri, Model model) throws IOException {
		return (put(uri, model, null));
	}

	public String put(String uri, Model model, String queryString) throws IOException{
		return interSendToServer(true, uri, model, queryString);
	}

	public String post(String uri, Model model) throws IOException {
		return (post(uri, model, null));
	}

	public String post(String uri, Model model, String queryString) throws IOException{
		return interSendToServer(false, uri, model, queryString);
	}

	public Credential getCredential() {
		return credential;
	}

	/**
	 * <p>Resolves the server URL with the trailing separator character.</p>
	 * 
	 * @return The server URL with the trailing separator character.
	 */
	public String getServerUrl(){
		return (UrlUtils.appendTrailingForwardSlash(serverUrl.toString()));
	}
}