/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2021. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client.rm;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_CONSTANTS_BASIC;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_CONSTANTS_FORM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_AUTHENTICATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_CONTENT_TYPE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_LOCATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_OAUTH_AUTHORIZE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_OAUTH_CALLBACK;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_OAUTH_TOKEN;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_VALUE_AUTHENTICATION_FAILED;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_VALUE_AUTHENTICATION_REQUIRED;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.JAZZ_FIELD_PASSWORD;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.JAZZ_FIELD_USERNAME;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.JAZZ_REPOSITORY_ROLE_ADMINS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.JAZZ_REPOSITORY_ROLE_DWADMINS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.JAZZ_REPOSITORY_ROLE_GUESTS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.JAZZ_REPOSITORY_ROLE_PROJECTADMINS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.JAZZ_REPOSITORY_ROLE_USERS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.JAZZ_URI_FORM_AUTHENTICATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.JAZZ_URI_FORM_AUTHENTICATION_FAILED;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.JAZZ_URI_FORM_OAUTHENTICATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.MEDIA_TYPE_APPLICATION_X_WWW_FORM_URL_ENCODED_UTF_8;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.ibm.rqm.cleaner.internal.client.AuthenticationException;
import com.ibm.rqm.cleaner.internal.util.UrlUtils;

/**
 * <p>Requirements Management (RM) OSLC HTTP client utilities in support of form-based authentication.</p>
 * 
 * <p>Note: Copied from <code>com.ibm.rqm.requirement.service</code> plug-in.</p>
 * <p>Note: Updated to adopt http 4.x client. </p>
 *  
 * @author  Paul Slauenwhite
 */
public class AuthenticationUtil{
    
    public static synchronized String formBasedAuthenticate(String repoPath, CloseableHttpClient httpClient,
            UsernamePasswordCredentials storedCredentials) throws AuthenticationException, UnsupportedEncodingException {
        final int expectedResponse = 302;
        final int expectedResponse2 = 403;
        String redirectURI = null;

        repoPath = UrlUtils.appendTrailingForwardSlash(repoPath);
        
        repoPath += JAZZ_URI_FORM_AUTHENTICATION;
        HttpPost postMethod = new HttpPost(repoPath);
        postMethod.setHeader(HTTP_HEADER_CONTENT_TYPE, MEDIA_TYPE_APPLICATION_X_WWW_FORM_URL_ENCODED_UTF_8);
        
        try {
        	if ( storedCredentials != null ) {
        		ArrayList<BasicNameValuePair> postParameters = new ArrayList<BasicNameValuePair>();
        		postParameters.add(new BasicNameValuePair(JAZZ_FIELD_USERNAME, storedCredentials.getUserName()));
	            if ( storedCredentials.getPassword() != null ) {
	            	postParameters.add(new BasicNameValuePair(JAZZ_FIELD_PASSWORD, storedCredentials.getPassword()));
	            }
	            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters);
	            postMethod.setEntity(entity);
        	}
            try {
                HttpResponse response = httpClient.execute(postMethod);
                int rc = response.getStatusLine().getStatusCode();
                Header authHeader = response.getFirstHeader(HTTP_HEADER_AUTHENTICATION);
                if (authHeader != null) {
                    if (authHeader.getValue().equals(HTTP_HEADER_VALUE_AUTHENTICATION_REQUIRED)) {
                        throw new AuthenticationException("There is a server configuration error. The " + JAZZ_URI_FORM_AUTHENTICATION + " cannot be a protected resource."); //$NON-NLS-1$ //$NON-NLS-2$
                    } else if (authHeader.getValue().equals(HTTP_HEADER_VALUE_AUTHENTICATION_FAILED)) {
                        throw new AuthenticationException("Either the username or password is not valid."); //$NON-NLS-1$
                    } else {
                        throw new AuthenticationException("There was an unexpected response from the authentication servlet.  " + HTTP_HEADER_VALUE_AUTHENTICATION_REQUIRED + " or " + HTTP_HEADER_VALUE_AUTHENTICATION_FAILED + " was expected, but " + authHeader.getValue() + " was received. Check your server configuration."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    }
                } else if (rc == expectedResponse) {
                    redirectURI = response.getFirstHeader(HTTP_HEADER_LOCATION).getValue();
                    if (redirectURI.matches("^.*/" + JAZZ_URI_FORM_AUTHENTICATION_FAILED + ".*$")) {  //$NON-NLS-1$ //$NON-NLS-2$
                        // Login failed
                        throw new AuthenticationException("Either the username or password is not valid."); //$NON-NLS-1$
                    }
                } else if (rc == expectedResponse2) {
                    throw new AuthenticationException("The request for the URL " + repoPath + " was denied with a Forbidden status. Contact your administrator to verify the assigned roles for the top-level project area."); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    // The form auth request should always respond with a 302 redirect code
                    // Unexpected response: fail the login
                    throw new AuthenticationException("There was an unexpected response from " + repoPath + ". A " + expectedResponse + " response code was expected, but a " + rc + " was received. Check your server configuration."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                }
            } catch (Exception e) {
                if (e.getClass().equals(AuthenticationException.class)) {
                    throw (AuthenticationException)e;
                } else {
                	String eMsg = e.getMessage();
                	if ( eMsg == null ) {
                		eMsg = ""; //$NON-NLS-1$
                	}
                    throw new AuthenticationException("An unexpected error was caught while attempting to login: " + e.getMessage()); //$NON-NLS-1$
                }
            }
        } finally {
            postMethod.releaseConnection();           
        }
        
        return redirectURI;
    }
    
    public static synchronized void formBasedOauthAuthenticate(IRequirementsRepository repository, String repoPath,
			CloseableHttpClient httpClient, String oauth_token, String oauth_callback)
			throws AuthenticationException, UnsupportedEncodingException {
    	formBasedOauthAuthenticate(repository, repoPath, httpClient, oauth_token, oauth_callback, true);
    }
    
    public static synchronized void formBasedOauthAuthenticate(IRequirementsRepository repository, String repoPath,
			CloseableHttpClient httpClient, String oauth_token, String oauth_callback, boolean addOAuthUri)
			throws AuthenticationException, UnsupportedEncodingException {
		final int expectedResponse = 302;

		if (addOAuthUri) {
			 
			repoPath = UrlUtils.appendTrailingForwardSlash(repoPath);
	
			repoPath += JAZZ_URI_FORM_OAUTHENTICATION;
		}
		HttpPost postMethod = new HttpPost(repoPath);
		postMethod.setHeader(HTTP_HEADER_CONTENT_TYPE,
				MEDIA_TYPE_APPLICATION_X_WWW_FORM_URL_ENCODED_UTF_8);

		try {
			ArrayList<BasicNameValuePair> postParameters = new ArrayList<BasicNameValuePair>();
    		postParameters.add(new BasicNameValuePair(HTTP_HEADER_OAUTH_TOKEN, oauth_token));
    		postParameters.add(new BasicNameValuePair(HTTP_HEADER_OAUTH_CALLBACK, oauth_callback));
    		postParameters.add(new BasicNameValuePair(HTTP_HEADER_OAUTH_AUTHORIZE, Boolean.TRUE.toString()));
           
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters);
            postMethod.setEntity(entity);
			
			try {
				HttpResponse response = repository.executeMethod(postMethod);
				int rc = response.getStatusLine().getStatusCode();
				if (rc != expectedResponse) {

					// Oauth authorize failed
					throw new AuthenticationException(
							"Either the username or password is not valid."); //$NON-NLS-1$
				}

			} catch (Exception e) {
				if (e.getClass().equals(AuthenticationException.class)) {
					throw (AuthenticationException) e;
				} else {
					throw new AuthenticationException("An unexpected error was caught while attempting to login: " + e.getMessage()); //$NON-NLS-1$
				}
			}
		} finally {
			postMethod.releaseConnection();
		}
	}
    
    public static String determineAuthMethod(CloseableHttpClient httpClient, String repoPath, IRequirementsRepository repository) throws AuthenticationException {
        
        if (httpClient == null)
            throw new IllegalArgumentException("httpClient must not be null"); //$NON-NLS-1$
        if (repoPath == null)
            throw new IllegalArgumentException("repoPath must not be null"); //$NON-NLS-1$
        
        repoPath = UrlUtils.appendTrailingForwardSlash(repoPath);
        
        repoPath += JAZZ_URI_FORM_AUTHENTICATION;

        HttpGet formAuth = new HttpGet(repoPath);
        String serverAuthMethod = null;
        repository.setFollowRedirect(false);
        try {
            // if the JAZZ_URI_FORM_AUTHENTICATION path exists, then we know we are using FORM auth
            try {
                HttpResponse response = repository.executeMethod(formAuth);
                int rc = response.getStatusLine().getStatusCode();
                if (rc == 200) {
                    // Tomcat and Jetty return a status code 200 if the server is using FORM auth
                    serverAuthMethod = HTTP_CONSTANTS_FORM;
                } else if (rc == 302 && response.getFirstHeader(HTTP_HEADER_LOCATION).getValue().matches("^.*/" + JAZZ_URI_FORM_AUTHENTICATION_FAILED + ".*$")) {  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    // WebSphere returns a status code 302 and redirects to the auth servlet if the server is using FORM authentication
                    serverAuthMethod = HTTP_CONSTANTS_FORM;
                } else if ((rc == 500) || (rc == 400) || (rc == 401)) {
                	
                	//Note: As of DNG 5.0.1, GET /j_security_check requests return 500 (Internal Server Error) when DNG is configured for form authentication.
                	//Note: As of DNG 5.0.2, GET /j_security_check requests return 400 (Bad Request) when DNG is configured for form authentication.
                	//Note: As of DNG 6.0.1, GET /j_security_check requests return 401 (Unauthorized) when DNG is configured for form authentication.
                	serverAuthMethod = HTTP_CONSTANTS_FORM;                	
                } else {
                    serverAuthMethod = HTTP_CONSTANTS_BASIC;
                }
            } catch (Exception e) {
                throw new AuthenticationException("An unexpected error was caught while attempting to login: " + e.getMessage()); //$NON-NLS-1$
            }
            return serverAuthMethod;
        } finally {
            formAuth.releaseConnection();
        }
    }

    public static boolean credDefined(UsernamePasswordCredentials storedCredentials) {
        String storedUsername = null;
        String storedPassword = null;
        if (storedCredentials != null) {
            storedUsername = storedCredentials.getUserName();
            storedPassword = storedCredentials.getPassword();
        }
        
        return (storedUsername != null && storedUsername.length() != 0
        		&& storedPassword != null && storedPassword.length() != 0);
    }
    
    //Parse JSON for Permission List
    public static List<String> getRolePermissionList(String responseData) {
    	List<String> roleList = new ArrayList<String>();
		if (responseData.length() == 0) {
			return roleList;
		}
		
		//Grab everything in between brackets, which are roles
		String tmpList = responseData.substring(responseData.indexOf("[") + 1,  //$NON-NLS-1$
				responseData.indexOf("]") - 1); 								//$NON-NLS-1$
		//Check to see if list has more than 1 item if so split
		if(tmpList.contains(",")) { //$NON-NLS-1$
			roleList = Arrays.asList(tmpList.split("\\,")); //$NON-NLS-1$
		} else {
			roleList.add(tmpList);
		}
		return roleList;
    }
    
 
    public static boolean isJazzAdmins(List<String> rolePermissions) {
    	boolean result = false;
		if (rolePermissions == null || rolePermissions.size() == 0) {
			return false;
		}
		
		Iterator<String> itr = rolePermissions.iterator();
		while(itr.hasNext()) {
			if(itr.next().toString().contains(JAZZ_REPOSITORY_ROLE_ADMINS)) {
				result = true;
				break;
			}
				
		}
		return result;
    }
    
    public static boolean isJazzProjectAdmins(List<String> rolePermissions) {
    	boolean result = false;
		if (rolePermissions == null || rolePermissions.size() == 0) {
			return false;
		}
		
		Iterator<String> itr = rolePermissions.iterator();
		while(itr.hasNext()) {
			if(itr.next().toString().contains(JAZZ_REPOSITORY_ROLE_PROJECTADMINS)) {
				result = true;
				break;
			}
				
		}
		return result;
    }
    
    public static boolean isJazzUsers(List<String> rolePermissions) {
    	boolean result = false;
		if (rolePermissions == null || rolePermissions.size() == 0) {
			return false;
		}
		
		Iterator<String> itr = rolePermissions.iterator();
		while(itr.hasNext()) {
			if(itr.next().toString().contains(JAZZ_REPOSITORY_ROLE_USERS)) {
				result = true;
				break;
			}
				
		}
		return result;
    }
    
    public static boolean isJazzDWAdmins(List<String> rolePermissions) {
    	boolean result = false;
		if (rolePermissions == null || rolePermissions.size() == 0) {
			return false;
		}
		
		Iterator<String> itr = rolePermissions.iterator();
		while(itr.hasNext()) {
			if(itr.next().toString().contains(JAZZ_REPOSITORY_ROLE_DWADMINS)) {
				result = true;
				break;
			}
				
		}
		return result;
    }
    
    public static boolean isJazzGuests(List<String> rolePermissions) {
    	boolean result = false;
		if (rolePermissions == null || rolePermissions.size() == 0) {
			return false;
		}
		
		Iterator<String> itr = rolePermissions.iterator();
		while(itr.hasNext()) {
			if(itr.next().toString().contains(JAZZ_REPOSITORY_ROLE_GUESTS)) {
				result = true;
				break;
			}
				
		}
		return result;
    }
}
