/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2013, 2022. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.ENCODING_UTF8;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.FORWARD_SLASH;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.VERSIONED_URL_PATTERN;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * <p>URL utilities.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public final class UrlUtils {

	/**
	 * <p>Resolves the context root from the absolute URL string.</code>
	 * 
	 * <p>By default, <code>null<code> is returned if the URL string is 
	 * <code>null</code>, empty, invalid URL, relative URL, or does not 
	 * contain a context root.</p>
	 * 
	 * @param urlString The URL string.
	 * @return The context root from the URL string.
	 */
	public static String getContextRoot(final String urlString) { 
		
		if(isValidUrl(urlString)){

			try{

				String path = new URL(urlString).getPath();

				if (StringUtils.isSet(path)) {

					path = path.trim();

					if(!FORWARD_SLASH.equals(path)) {

						//Remove the leading forward slash from the path, if exists:
						if(path.startsWith("/")) { //$NON-NLS-1$
							path = path.substring(1);
						}

						//Return the context root (first segment):
						if(path.contains(FORWARD_SLASH)){
							return (path.substring(0, path.indexOf(FORWARD_SLASH)));
						}		

						//Return the context root (full path):
						return path;
					}
				}
			} 
			catch (MalformedURLException m) {
				//Ignore.
			}
		}

		return null;
	}

	public static String appendTrailingForwardSlash(String urlString){
		
		if(StringUtils.isSet(urlString)){
			
			if(!urlString.trim().endsWith(FORWARD_SLASH)){
				return (urlString + FORWARD_SLASH);
			}
		}
		
		return urlString;
	}
	
	public static boolean isValidUrl(String urlString){

		if(StringUtils.isSet(urlString)){

			try {

				new URL(urlString);

				return true;
			} 
			catch (MalformedURLException e) {
				//Ignore and return false.
			}
		}

		return false;
	}
	
	public static String resolveUrl(String urlString, String queryString){

		if((isValidUrl(urlString)) && (StringUtils.isSet(queryString))){
			return (getQueryBaseUri(urlString) + "?" + getQueryString(urlString, queryString)); //$NON-NLS-1$
		}

		return urlString;
	}

	/**
	 * <p>Resolves the query base URI from the query URI.</p>
	 * 
	 * <p>The format of the resource type URI is:</p>
	 * 
	 * <p><code>&lt;query base&gt;[/]</code></p>
	 * <p><code>&lt;query base&gt;[?&lt;query parameters&gt;]</code></p>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>&lt;see {@link CleanerConstants#URI_TEMPLATE_RESOURCE_CREATION}&gt;/</code></p>
	 * <p><code>&lt;see {@link CleanerConstants#URI_TEMPLATE_RESOURCE_CREATION}&gt;?oslc.where=dcterms:identifier="123456789"</code></p>
	 *  
	 * @param resourceTypeUri The query URI.
	 * @return The query base URI from the query URI.
	 */
	public static String getQueryBaseUri(String queryUri){

		if(queryUri != null){

			int questionMarkIndex = queryUri.indexOf('?');

			//Trim the query string:
			if(questionMarkIndex != -1){
				return (queryUri.substring(0, questionMarkIndex));	    	
			}		

			//Trim trailing forward slash:
			if(queryUri.trim().endsWith("/")){ //$NON-NLS-1$
				return (queryUri.substring(0, (queryUri.lastIndexOf('/'))));
			}
		}

		return queryUri;
	}

	public static String getQueryString(String urlString, String queryString) {

		String existingQueryString = getQueryString(urlString);

		return (appendQueryString(existingQueryString, queryString));
	}
	
	public static String appendQueryString(String existingQueryString, Map<String, String> requestParameters) {

		String queryString = existingQueryString;
		
		if(requestParameters != null) {
		
			for(String name : requestParameters.keySet()){
				queryString = appendQueryString(queryString, (name + "=" + requestParameters.get(name))); //$NON-NLS-1$
			}	
		}
		
		return queryString;
	}
	
	public static String appendQueryString(String existingQueryString, String queryString) {

		if(StringUtils.isSet(existingQueryString)) {

			if((StringUtils.isSet(queryString)) && (!existingQueryString.contains(queryString))){
				return (existingQueryString + "&" + queryString); //$NON-NLS-1$
			}

			return existingQueryString;
		}

		return queryString;
	}
	
	public static String getQueryString(String urlString) {

		if(isValidUrl(urlString)){

			try{
				return (new URL(urlString).getQuery());
			} 
			catch (MalformedURLException m) {
				//Ignore.
			}
		}

		return null;
	}

	public static Map<String, String> getQueryMap(String urlString) {

		Map<String, String> queryMap = new HashMap<String, String>();

		String queryString = getQueryString(urlString);

		if(StringUtils.isSet(queryString)) {

			for (String parameter : queryString.split("\\&")) { //$NON-NLS-1$

				//Split on the first occurrence of the equals sign:
				String[] nameValue = parameter.split("\\=", 2); //$NON-NLS-1$

				queryMap.put(nameValue[0], nameValue[1]);
			}
		}

		return queryMap;
	}
	
	/**
	 * <p>Resolves the last segment of the path of the URL string.  If the
	 * URL string is not a valid URL, the URL string is returned.</p> 
	 * 
	 * @param urlString The URL string.
	 * @return The the last segment of the path of the URL string, otherwise the URL string.
	 * @see #isValidUrl(String)
	 */
	public static String getLastSegment(String urlString) {

		if(isValidUrl(urlString)){

			try{

				URL url = new URL(urlString);

				String[] segements = url.getPath().split("/"); //$NON-NLS-1$

				return (segements[segements.length - 1]);
			} 
			catch (MalformedURLException m) {
				//Ignore.
			}
		}

		return urlString;
	}
	
	/**
	 * <p>Resolves the URL-encoded string using the UTF-8 encoding.  If the UTF-8 encoding is supported, 
	 * the string is returned.</p> 
	 * 
	 * @param string The string to URL-encode.
	 * @return The URL-encoded string.
	 * @see URLEncoder#encode(String, String)
	 * @see CleanerConstants#ENCODING_UTF8
	 */
	public static String urlEncode(String string) {
		
		try {
			return (URLEncoder.encode(string, ENCODING_UTF8));
		} 
		catch (UnsupportedEncodingException u) {
			//Ignore since UTF-8 encoding is supported.
		}

		return string;
	}
	
	/**
	 * <p>Resolves the URL-decoded string using the UTF-8 encoding.  If the UTF-8 encoding is supported, 
	 * the string is returned.</p> 
	 * 
	 * @param string The string to URL-decode.
	 * @return The URL-decoded string.
	 * @see URLDecoder#decode(String, String)
	 * @see CleanerConstants#ENCODING_UTF8
	 */
	public static String urlDecode(String string) {
		
		try {
			return (URLDecoder.decode(string, ENCODING_UTF8));
		} 
		catch (UnsupportedEncodingException u) {
			//Ignore since UTF-8 encoding is supported.
		}

		return string;
	}
	
	/**
	 * <p>Determines if the URL is an OSLC versioned URL.</p>
	 * 
	 * <p>The format of the OSLC versioned URL is:</p>
	 * 
	 * <p><code>&lt;service URL&gt;/&lt;project area UUID&gt;/&lt;resource type namespace URI&gt;/&lt;resource type name&gt;/&lt;resource UUID&gt;/&lt;resource state UUID&gt;[/]</code></p>
	 * <p><code>&lt;service URL&gt;/&lt;project area UUID&gt;/&lt;resource type namespace URI&gt;/&lt;resource type name&gt;/&lt;resource UUID&gt;/&lt;resource state UUID&gt;[?&lt;query parameters&gt;]</code></p>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>&lt;see {@link CleanerConstants#URI_TEMPLATE_VERSION_RESOURCE}&gt;/</code></p>
	 * <p><code>&lt;see {@link CleanerConstants#URI_TEMPLATE_VERSION_RESOURCE}&gt;?&lt;query parameters&gt;</code></p>
	 *  
	 * @param url The URL.
	 * @return <code>true</code> if the URL is an OSLC versioned URL, otherwise <code>false</code>.
	 * @see #getConceptUrl(String)
	 */
	public static boolean isVersionedUrl(String url){

		if(isValidUrl(url)){			
			return (VERSIONED_URL_PATTERN.matcher(url).find());
		}

		return false;
	}
	
	/**
	 * <p>Resolves the OSLC concept URL from the OSLC versioned URL (see {@link #isVersionedUrl(String)}).</p>
	 * 
	 * <p>The format of the OSLC concept URL is:</p>
	 * 
	 * <p><code>&lt;service URL&gt;/&lt;project area UUID&gt;/&lt;resource type namespace URI&gt;/&lt;resource type name&gt;/&lt;resource UUID&gt;[/]</code></p>
	 * <p><code>&lt;service URL&gt;/&lt;project area UUID&gt;/&lt;resource type namespace URI&gt;/&lt;resource type name&gt;/&lt;resource UUID&gt;[?&lt;query parameters&gt;]</code></p>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>&lt;see {@link CleanerConstants#URI_TEMPLATE_RESOURCE}&gt;/</code></p>
	 * <p><code>&lt;see {@link CleanerConstants#URI_TEMPLATE_RESOURCE}&gt;?&lt;query parameters&gt;</code></p>
	 *  
	 * @param versionedUrl The OSLC versioned URL.
	 * @return The OSLC concept URL from the OSLC versioned URL, otherwise the OSLC versioned URL.
	 * @see #isVersionedUrl(String)
	 */
	public static String getConceptUrl(String versionedUrl){

		if(isVersionedUrl(versionedUrl)){

			final Matcher versionedUrlMatcher = VERSIONED_URL_PATTERN.matcher(versionedUrl);

			if(versionedUrlMatcher.find()) {

				final String stateId = versionedUrlMatcher.group(4);

				return (versionedUrl.replace(FORWARD_SLASH + stateId, "")); //$NON-NLS-1$
			}
		}

		return versionedUrl;
	}
}
