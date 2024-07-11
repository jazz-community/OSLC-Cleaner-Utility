/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_ACCEPT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_OSLC_CORE_VERSION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.MEDIA_TYPE_RDF_XML;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_DC_TERMS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_OSLC;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.OSLC_VERSION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_CREATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_CREATION_FACTORY;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_QUERY_BASE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_QUERY_CAPABILITY;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_RESOURCE_TYPE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_SERVICE_PROVIDER;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_TITLE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_SERVICE_PROVIDER;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.ibm.rqm.cleaner.internal.client.dwa.DwaOslcHttpClient;
import com.ibm.rqm.cleaner.internal.util.LogUtils;
import com.ibm.rqm.cleaner.internal.util.RdfUtils;
import com.ibm.rqm.cleaner.internal.util.StringUtils;
import com.ibm.rqm.cleaner.internal.util.UrlUtils;

/**
 * <p>OSLC HTTP client.</p>
 * 
 * <p><b>Note:</b> Any new methods added to this abstract class <i>may</i> 
 * need to be overridden in one or more of the subclasses.</p> 
 *  
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public abstract class OslcHttpClient extends JazzHttpClient {

	private List<ServiceProvider> serviceProviders = null;
	private String firstServiceProviderCatalogUri = null;

	protected OslcHttpClient(URL server) throws MalformedURLException, GeneralSecurityException {
		super(server);
		
		this.serviceProviders = new ArrayList<ServiceProvider>();
	}

	public abstract String getServiceProvidersPropertyUri();

	@Override
	public int ping() throws IOException{
		
		if(LogUtils.isTraceEnabled()) {
			LogUtils.logTrace("Pinging first service provider catalog '" + firstServiceProviderCatalogUri + "' for '" + getServerUrl() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		int returnCode = super.ping();

		if (returnCode == HttpStatus.SC_OK || returnCode == HttpStatus.SC_MOVED_TEMPORARILY || returnCode == HttpStatus.SC_SEE_OTHER) {

			//Verify the connection by performing a HEAD/GET request on the first service provider catalog (requires authentication):
			if(UrlUtils.isValidUrl(firstServiceProviderCatalogUri)){

				//Note: Ping requests (first service provider catalog) do not require a configuration context.
				final String currentConfigurationContextUrl = getConfigurationContextUrl();

				setConfigurationContextUrl(null);

				try {

					returnCode = (head(firstServiceProviderCatalogUri));

					if((returnCode == HttpStatus.SC_METHOD_NOT_ALLOWED) || (returnCode == HttpStatus.SC_NOT_IMPLEMENTED)) {
						returnCode = (getStatusCode(firstServiceProviderCatalogUri));
					}
				}
				finally {
					setConfigurationContextUrl(currentConfigurationContextUrl);
				}
			}
		}

		if(LogUtils.isDebugEnabled()) {
			LogUtils.logDebug("Pinged first service provider catalog '" + firstServiceProviderCatalogUri + "' for '" + getServerUrl() + "': " + returnCode); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		return returnCode;
	}

	@Override
	protected int login(Credential credential) throws IOException{	    

		if(LogUtils.isTraceEnabled()) {
			LogUtils.logTrace("Logging in to '" + getServerUrl() + "' from the OslcHttpClient."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		int returnCode = super.login(credential);
		if (returnCode == HttpStatus.SC_OK || returnCode == HttpStatus.SC_MOVED_TEMPORARILY || returnCode == HttpStatus.SC_SEE_OTHER) {

			if(serviceProviders.isEmpty()){

				//Resolve the root services document:
				String rootServicesDocumentUri = getRootServicesUri();

				if(StringUtils.isSet(rootServicesDocumentUri)) {

					String serviceProvidersPropertyUri = getServiceProvidersPropertyUri();

					if(StringUtils.isSet(serviceProvidersPropertyUri)) {

						Model rootServicesDocumentModel = RdfUtils.read(this, rootServicesDocumentUri);

						//Iterate the service provider catalogs in the root services document:
						StmtIterator serviceProviderCatalogStatementsIterator = rootServicesDocumentModel.listStatements(null, rootServicesDocumentModel.createProperty(serviceProvidersPropertyUri), ((RDFNode)(null)));

						if(serviceProviderCatalogStatementsIterator.hasNext()){

							while(serviceProviderCatalogStatementsIterator.hasNext()){

								//Resolve the service provider catalog:
								Resource serviceProviderCatalogResource = serviceProviderCatalogStatementsIterator.next().getResource();

								String serviceProviderCatalogUri = serviceProviderCatalogResource.getURI();

								Model serviceProviderCatalogModel = RdfUtils.read(this, serviceProviderCatalogUri);

								//Iterate the service providers in the service provider catalog:
								StmtIterator serviceProviderPropertyStatementsIterator = serviceProviderCatalogModel.listStatements(null, serviceProviderCatalogModel.createProperty(NAMESPACE_URI_OSLC, PROPERTY_SERVICE_PROVIDER), ((RDFNode)(null)));

								while(serviceProviderPropertyStatementsIterator.hasNext()){
									
									Statement serviceProviderPropertyStatement = serviceProviderPropertyStatementsIterator.next();

									//Resolve the service provider document: 
									Resource serviceProviderPropertyResource = serviceProviderPropertyStatement.getResource();

									String serviceProviderPropertyUri = serviceProviderPropertyResource.getURI();
									serviceProviderPropertyStatement.getProperty(serviceProviderCatalogModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE)).getString();

									Model serviceProviderModel = RdfUtils.read(this, serviceProviderPropertyUri);
									
									String serviceProviderTitle = null;
									if(this instanceof DwaOslcHttpClient) {
										serviceProviderTitle = serviceProviderPropertyResource.getRequiredProperty(serviceProviderCatalogModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE)).getString();
									}

									//Iterate the service providers in the service provider document:
									StmtIterator serviceProviderStatementsIterator = serviceProviderModel.listStatements(null, RDF.type, serviceProviderModel.createProperty(NAMESPACE_URI_OSLC, RESOURCE_SERVICE_PROVIDER));

									while(serviceProviderStatementsIterator.hasNext()){

										//Resolve the service provider: 
										Resource serviceProviderResource = serviceProviderStatementsIterator.next().getSubject();

										//Resolve the service provider title:
										if(!(this instanceof DwaOslcHttpClient)) {
											serviceProviderTitle = serviceProviderResource.getRequiredProperty(serviceProviderModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE)).getString();
										}

										ServiceProvider serviceProvider = new ServiceProvider(serviceProviderPropertyUri, serviceProviderTitle);

										//Resolve the service provider creation factories:
										Map<String, String> creationFactories = serviceProvider.getCreationFactories();

										//Iterate the creation factories in the service provider:
										StmtIterator creationFactoriesStatementsIterator = serviceProviderModel.listStatements(null, serviceProviderModel.createProperty(NAMESPACE_URI_OSLC, PROPERTY_CREATION_FACTORY), ((RDFNode)(null)));

										while(creationFactoriesStatementsIterator.hasNext()){

											//Resolve the creation factory: 
											Resource creationFactoryResource = creationFactoriesStatementsIterator.next().getResource();

											//Resolve the creation URI from the creation factory: 
											String creationFactoryCreationUri = creationFactoryResource.getRequiredProperty(serviceProviderModel.createProperty(NAMESPACE_URI_OSLC, PROPERTY_CREATION)).getResource().getURI();

											//Iterate the resource types (if any since the oslc:resourceType property is optional) in the creation factory: 
											StmtIterator creationFactoryResourceTypesStatementsIterator = serviceProviderModel.listStatements(creationFactoryResource, serviceProviderModel.createProperty(NAMESPACE_URI_OSLC, PROPERTY_RESOURCE_TYPE), ((RDFNode)(null)));

											while(creationFactoryResourceTypesStatementsIterator.hasNext()){

												String creationFactoryResourceType = creationFactoryResourceTypesStatementsIterator.next().getResource().getURI();

												creationFactories.put(creationFactoryResourceType, creationFactoryCreationUri);
											}
										}

										//Resolve the service provider query capabilities:
										Map<String, String> queryCapabilities = serviceProvider.getQueryCapabilities();

										//Iterate the query factories in the service provider:
										StmtIterator queryCapabilitiesStatementsIterator = serviceProviderModel.listStatements(null, serviceProviderModel.createProperty(NAMESPACE_URI_OSLC, PROPERTY_QUERY_CAPABILITY), ((RDFNode)(null)));

										while(queryCapabilitiesStatementsIterator.hasNext()){

											//Resolve the query capability: 
											Resource queryCapabilityResource = queryCapabilitiesStatementsIterator.next().getResource();

											//Resolve the query base URI from the query capability: 
											String queryCapabilityQueryBaseUri = queryCapabilityResource.getRequiredProperty(serviceProviderModel.createProperty(NAMESPACE_URI_OSLC, PROPERTY_QUERY_BASE)).getResource().getURI();

											//Iterate the resource types (if any since the oslc:resourceType property is optional) in the query capability:
											StmtIterator queryCapabilityResourceTypesStatementsIterator = serviceProviderModel.listStatements(queryCapabilityResource, serviceProviderModel.createProperty(NAMESPACE_URI_OSLC, PROPERTY_RESOURCE_TYPE), ((RDFNode)(null)));

											while(queryCapabilityResourceTypesStatementsIterator.hasNext()){

												String queryCapabilityResourceType = queryCapabilityResourceTypesStatementsIterator.next().getResource().getURI();

												queryCapabilities.put(queryCapabilityResourceType, queryCapabilityQueryBaseUri);
											}
										}

										serviceProviders.add(serviceProvider);
									}

									if(firstServiceProviderCatalogUri == null){
										firstServiceProviderCatalogUri = serviceProviderCatalogUri;
									}
								}
							}
						}
						else{
							LogUtils.logError("Error resolving the service provider(s) in the root services document for server '" + getServerUrl() + "'."); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
					else{
						LogUtils.logWarning("No service provider property URI set for server '" + getServerUrl() + "'.  The service provider(s) will not be resolved for this server."); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				else{
					LogUtils.logWarning("No root services URI set for server '" + getServerUrl() + "'.  The service provider(s) will not be resolved for this server."); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}

		if(LogUtils.isDebugEnabled()) {
			LogUtils.logDebug("Logged in to '" + getServerUrl() + "' from the OslcHttpClient: " + returnCode); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return returnCode;
	}
	
	@Override
	public Map<String, String> getRequestHeaders() {

		Map<String, String> requestHeaders = super.getRequestHeaders();
		
		requestHeaders.put(HTTP_HEADER_OSLC_CORE_VERSION, OSLC_VERSION);
		
		//Note: The application/rdf+xml accept header is required for the Design Management (DM) application (406 Not Acceptable returned when using application/xml).
		requestHeaders.put(HTTP_HEADER_ACCEPT, MEDIA_TYPE_RDF_XML);
				
		return requestHeaders;
	}

	public ServiceProvider getServiceProviderByTitle(String title){

		for(ServiceProvider serviceProvider : serviceProviders){

			if(serviceProvider.getTitle().equals(title)){
				return serviceProvider;
			}
		}

		return null;
	}

	public ServiceProvider getServiceProviderByUrl(String url){

		for(ServiceProvider serviceProvider : serviceProviders){

			if(serviceProvider.getUrl().equals(url)){
				return serviceProvider;
			}
		}

		return null;
	}

	public List<ServiceProvider> getServiceProviders(){
		return serviceProviders;
	}

	public List<Trs2Provider> getTrs2Providers(){
		return (Collections.emptyList());
	}
}
