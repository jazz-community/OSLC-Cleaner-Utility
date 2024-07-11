/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client.rm;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_REQUIREMENT_FACTORY;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_ARTIFACTCONVERTER_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_BASE_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_BINARY_RESOURCE_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_COMMENTS_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_EXPORT_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_FOLDERS_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_FRIENDS_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_GLOSSARY_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_IMPORT_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_LINKS_20_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_LINKS_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_LINK_TYPES_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_MAIL_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_MODULES_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_OPERATIONS_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_PROCESS_SECURITY_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_PROJECTRESOURCES_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_PROJECTS_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_PROJECTTEMPLATES_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_PROXY_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_QUERY_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_REQUEST_VALIDATION_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_RESPONSE_VALIDATION_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_REVIEWS_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_REVISIONS_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_SPARQL_QUERY_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_TAGS_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_TEMPLATES_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_TYPES_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_WRAPPED_RESOURCE_URL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RRC_WRAPPER_RESOURCE_URL;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.apache.http.protocol.HttpContext;

import com.ibm.rqm.cleaner.internal.client.Credential;
import com.ibm.rqm.cleaner.internal.util.LogUtils;
import com.ibm.rqm.integration.client.clientlib.HttpClientConstants;
import com.ibm.rqm.integration.client.clientlib.Logger;
import com.ibm.rqm.integration.client.clientlib.TrustingSSLSocketFactory;


/**
 * <p>Requirements Management (RM) OSLC HTTP client requirement repository handling
 * the connection to the fronting server as well as authentication.</p>
 * 
 * <p>Note: Copied from <code>com.ibm.rdm.rest</code> plug-in.</p>
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public class Repository implements IRequirementsRepository {
	
	private final String url ;
	private final Credential credential;
	private CloseableHttpClient httpClient = null;
	private AuthScope authScope;
	private ILoginHandler loginHandler = new ClientLoginHandler();
	private boolean mailConfigured = false;
	private AuthRedirectStrategy _authRedirectStrategy = null;
	
	public Repository(String url, Credential credential){
		this.url = url ;
		this.credential = credential;
	}
	
	@Override
	public CloseableHttpClient getHttpClient() {
		if(httpClient == null){
			init();
		}
		return httpClient;
	} 

	public String getRepoPath() {
		return getUrl().getPath();
	}
	
	public URL getUrl(){
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			LogUtils.logError(e.toString(), e);
		}
		return null;
	}
	

	private PoolingHttpClientConnectionManager getConnectionManager() throws GeneralSecurityException, IOException {

		// Accept SSL certs
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", new PlainConnectionSocketFactory()) //$NON-NLS-1$
				.register("https", TrustingSSLSocketFactory.getInstance()) //$NON-NLS-1$
				.build();

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);

		// Socket config
		cm.setDefaultSocketConfig(
				SocketConfig.custom()
				.setSoLinger(-1)
				.setTcpNoDelay(true)
				.build());

		// Connection config
		cm.setDefaultConnectionConfig(
				ConnectionConfig.custom()
				.setBufferSize(8192)
				.build());
		
		int maxConnections = 10; // Default to 10 max connections.  
		// Provide a way to override externally.
		String MAX_ADAPTER_CONNECTIONS = System.getenv("MAX_ADAPTER_CONNECTIONS"); //$NON-NLS-1$
		if(MAX_ADAPTER_CONNECTIONS != null){
			maxConnections = Integer.parseInt(MAX_ADAPTER_CONNECTIONS);
		}
		
		cm.setDefaultMaxPerRoute(maxConnections);
		cm.setMaxTotal(maxConnections);
		cm.setValidateAfterInactivity(100);
		
		return cm;
	}
	@Override
	public void setFollowRedirect(boolean isRedirect) {
		if(_authRedirectStrategy!= null) {
			_authRedirectStrategy.allowRedirect(isRedirect);
		}
	}
	
	private static class AuthRedirectStrategy extends DefaultRedirectStrategy {
		boolean _allowRedirect = true;
		
		/**
		 * This function controls sub-sequent redirects, it is callers responsibility to set this
		 * switch on/off 
		 * @param allow sets false if you want to stop redirects
		 */
		public void allowRedirect(boolean allow) {
			_allowRedirect = allow;
		}
	    
	    @Override
	    public boolean isRedirected(
	        final HttpRequest request,
	        final HttpResponse response,
	        final HttpContext context) throws ProtocolException {
	    	
	    	//only act when it is configured to avoid redirection, otherwise let super class decides
	    	if(!_allowRedirect) { 
	    		return _allowRedirect;
	    	}
	    	return super.isRedirected(request, response, context);
	    }	    
	}
	
	private void init(){
		if (httpClient == null) {	        
//	        String repoProtocol = getUrl().getProtocol();
//	        String repoHost = getUrl().getHost();
//	        int repoPort = getUrl().getPort();
			
			HttpClientBuilder builder = HttpClients.custom();
			try {
				builder.setConnectionManager(getConnectionManager());
			}
			catch (Exception e) {
				Logger.Log.error("Exception while creating connection manager: " + e); //$NON-NLS-1$
			}
			
			String cookiePolicy = System.getenv("RQM_CLIENT_COOKIE_POLICY"); //NON-NLS-1$
			
			if (cookiePolicy == null) 
				cookiePolicy = CookieSpecs.STANDARD;
			RegistryBuilder<AuthSchemeProvider> basicAuthSchemeRegistryBuilder = RegistryBuilder.<AuthSchemeProvider> create();
	    	basicAuthSchemeRegistryBuilder.register(AuthSchemes.BASIC, new BasicSchemeFactory(Charset.forName(HttpClientConstants.UTF8)));
			RFC6265CookieSpecProvider cookieSpecProvider = new RFC6265CookieSpecProvider();
			Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider> 
			create().register(cookiePolicy, cookieSpecProvider).build();
			RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(cookiePolicy).build();
			builder.setDefaultRequestConfig(requestConfig);
			builder.setDefaultCookieSpecRegistry(r);
			Lookup<AuthSchemeProvider> basicAuthSchemeRegistry = basicAuthSchemeRegistryBuilder.build();
	        builder.setDefaultAuthSchemeRegistry(basicAuthSchemeRegistry);
	        _authRedirectStrategy = new AuthRedirectStrategy();//custom redirect strategy to support conditional redirect
	        builder.setRedirectStrategy(_authRedirectStrategy);
	        if(credential.getUsername() != null && credential.getPassword() != null) {
	        	CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
	        	credentialsProvider.setCredentials(AuthScope.ANY, 
	        	    new UsernamePasswordCredentials(credential.getUsername(), credential.getPassword()));
	        	builder.setDefaultCredentialsProvider(credentialsProvider);
	        }
	        
	
			
	        httpClient = builder.build();
	        
//	        if (PROTOCOL_HTTPS.equalsIgnoreCase(repoProtocol)) {
//	            Protocol httpsProtocol = new Protocol(PROTOCOL_HTTPS, SSLProtocolSocketFactory.INSTANCE, repoPort);
//	            hostConfiguration.setHost(repoHost, repoPort, httpsProtocol);
//	        } else {
//	            Protocol protocol = new Protocol(HTTP.HTTP_PROTOCOL, new DefaultProtocolSocketFactory(), repoPort);
//	            hostConfiguration.setHost(repoHost, repoPort, protocol);
//	        }
//	        
//	        httpClient.setHostConfiguration(hostConfiguration);
	     //   AuthenticationUtil.setCredentials(httpClient, getAuthScope(), credential.getUsername(), credential.getPassword());

	        URL serverurl = null;
	        try {
	        	serverurl = new URL(url);
	        } catch (MalformedURLException e) {
	        	LogUtils.logError(e.toString(), e);
	        }
		}
	}
	
	public HttpResponse executeMethod(HttpRequestBase method) throws IOException{
		HttpResponse result = httpClient.execute(method);
		notifyExecute(method);
		return result;
	}
	
	@Override
	public UsernamePasswordCredentials getUsernamePasswordCredentials() {
		return new UsernamePasswordCredentials(credential.getUsername(), credential.getPassword());
	}
	
	public AuthScope getAuthScope() {
		if (authScope == null) {
	        String repoHost = getUrl().getHost();
	        int repoPort = getUrl().getPort();
			authScope = new AuthScope(repoHost, repoPort, AuthScope.ANY_REALM);
		}
		return authScope;
	}
	
	public String getResourcesUrl() {
		return url + RRC_BASE_URL;
	}

	public String getOSLCUrl() {
		return url + PROPERTY_REQUIREMENT_FACTORY;
	}

	public String getQueryUrl() {
		return url + RRC_QUERY_URL;
	}
	
	public String getSparqlQueryUrl() {
		return url + RRC_SPARQL_QUERY_URL;
	}
	
	public String getFoldersUrl() {
		return url + RRC_FOLDERS_URL;
	}
	
	public String getTagsUrl() {
		return url + RRC_TAGS_URL;
	}
	
	public String getFriendsUrl() {
		return url + RRC_FRIENDS_URL;
	}
	
	public String getProjectResourcesUrl() {
		return url + RRC_PROJECTRESOURCES_URL;
	}

	public String getProjectTemplatesUrl() {
		return url + RRC_PROJECTTEMPLATES_URL;
	}
	
	public String getCommentsUrl() {
		return url + RRC_COMMENTS_URL;
	}
	
	public String getWrapperResourceUrl() {
		return url + RRC_WRAPPER_RESOURCE_URL;
	}
	
	public String getBinaryResourceUrl() {
		return url + RRC_BINARY_RESOURCE_URL;
	}
	
	public String getLinkTypesUrl() {
		return url + RRC_LINK_TYPES_URL;
	}
	
	public String getLinksUrl() {
		return url + RRC_LINKS_URL;
	}
	
	public String getLinks20Url() {
		return url + RRC_LINKS_20_URL;
	}
	
	public String getMailUrl() {
		return url + RRC_MAIL_URL;
	}

	public String getTemplatesUrl() {
		return url + RRC_TEMPLATES_URL;
	}
	
	public String getRevisionsUrl() {
		return url + RRC_REVISIONS_URL;
	}
	
	public String getImportUrl() {
		return url + RRC_IMPORT_URL;
	}
	
	public String getExportUrl() {
		return url + RRC_EXPORT_URL;
	}
	
	public String getTypesUrl() {
		return url + RRC_TYPES_URL;
	}
	
	public String getGlossaryUrl() {
		return url + RRC_GLOSSARY_URL;
	}
	
	@Override
	public String getUrlString() {
		return url;
	}

	@Override
	public void registerLoginHandler( ILoginHandler loginHandler ) {
		this.loginHandler = loginHandler;
	}

	@Override
	public ILoginHandler getLoginHandler() {
		return loginHandler;
	}
	
	public String getOperationsUrl() {
		return url + RRC_OPERATIONS_URL;
	}
	
	public String getProcessSecurityUrl() {
		return url + RRC_PROCESS_SECURITY_URL;
	}
	
	public String getProjectsServiceUrl() {
		return url + RRC_PROJECTS_URL;
	}
	
	public String getReviewsUrl() {
		return url + RRC_REVIEWS_URL;
	}

	public boolean isMailConfigured() {
		return mailConfigured;
	}
	
	public void setHttpClient(CloseableHttpClient client) {
		this.httpClient = client;
	}
	
	public String getJsessionId() {
		return null;
	}

	public String getWrappedResourceUrl() {
		return url+ RRC_WRAPPED_RESOURCE_URL;
	}
	
	public String getProxyUrl() {
		return url + RRC_PROXY_URL;
	}

	public String getRequestValidationUrl() {
		return url + RRC_REQUEST_VALIDATION_URL;
	}
	
	public String getResponseValidationUrl() {
		return url + RRC_RESPONSE_VALIDATION_URL;
	}
	
	public String getArtifactConverterUrl() {
		return url+ RRC_ARTIFACTCONVERTER_URL;
	}
	
	public String getModulesUrl() {
		return url+ RRC_MODULES_URL;
	}
	
	public interface ExecuteListener {
		void handleExecute(HttpRequestBase execute);
	}
	
	List<ExecuteListener> listeners = new ArrayList<ExecuteListener>();
	
	public void addExecuteListener(ExecuteListener listener) {
		listeners.add(listener);
	}
	
	void notifyExecute(HttpRequestBase method) {
		for (ExecuteListener listener : listeners)
			try {
				listener.handleExecute(method);
			} catch (Exception e) {
				LogUtils.logError(e.toString(), e);
			}
	}
	
	public void removeExecuteListener(ExecuteListener listener) {
		listeners.remove(listener);
	}

}
