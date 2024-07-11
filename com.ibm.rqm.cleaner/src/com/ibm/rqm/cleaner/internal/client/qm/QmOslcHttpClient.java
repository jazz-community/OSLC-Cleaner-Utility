/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client.qm;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_DC_TERMS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_JAZZ_QM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_JAZZ_TRS2;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_DESCRIPTION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_TITLE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_TRACKED_RESOURCE_SET;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_TRACKED_RESOURCE_SET_PROVIDER;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_TYPE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TRACKED_RESOURCE_SET;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.SERVICE_PROVIDERS_URI_QM;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.ibm.rqm.cleaner.internal.client.Credential;
import com.ibm.rqm.cleaner.internal.client.OslcHttpClient;
import com.ibm.rqm.cleaner.internal.client.Trs2Provider;
import com.ibm.rqm.cleaner.internal.util.LogUtils;
import com.ibm.rqm.cleaner.internal.util.RdfUtils;

/**
 * <p>Quality Management (QM) OSLC HTTP client.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public final class QmOslcHttpClient extends OslcHttpClient {
    
	private List<Trs2Provider> trs2Providers = null;

	public QmOslcHttpClient(URL serverUrl) throws MalformedURLException, GeneralSecurityException {                
		
		super(serverUrl);
		
		this.trs2Providers = new ArrayList<Trs2Provider>();
	} 

	@Override
	public String getServiceProvidersPropertyUri(){
		return SERVICE_PROVIDERS_URI_QM;
	}   

	@Override
	public int head(String uri, String queryString) throws IOException{

		int responseCode = super.head(uri, queryString);

		//Problem: Older versions of Rational Quality Manager (RQM) may NOT support the HTTP HEAD method (415 Unsupported Media Type or 501 Not Implemented or 415 Unsupported Media Type)).
		//Work-around: Use the HTTP GET method.
		//Defect: 192681
		if ((responseCode == HttpStatus.SC_METHOD_NOT_ALLOWED) || (responseCode == HttpStatus.SC_NOT_IMPLEMENTED) || (responseCode == HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE)) {
			responseCode = getStatusCode(uri, queryString);
		}

		return responseCode;
	}   
	
	@Override
	protected int login(Credential credential) throws IOException{	    

		int returnCode = super.login(credential);

		if ((returnCode == HttpStatus.SC_OK) || (returnCode == HttpStatus.SC_MOVED_TEMPORARILY) || (returnCode == HttpStatus.SC_SEE_OTHER)) {

			//Resolve the Tracked Resource Set version 2 (TRS2) URIs from the root services document:
			if(trs2Providers.isEmpty()) {

				//Resolve the root services document:
				String rootServicesDocumentUri = getRootServicesUri();

				Model rootServicesDocumentModel = RdfUtils.read(this, rootServicesDocumentUri);

				//Iterate the TRS2 provider(s) in the root services document:
				StmtIterator trackedResourceSetProviderStatementsIterator = rootServicesDocumentModel.listStatements(null, rootServicesDocumentModel.createProperty(NAMESPACE_URI_JAZZ_QM, PROPERTY_TRACKED_RESOURCE_SET_PROVIDER), ((RDFNode)(null)));

				while(trackedResourceSetProviderStatementsIterator.hasNext()){

					//Resolve the TRS2 provider:
					Resource trackedResourceSetProviderResource = trackedResourceSetProviderStatementsIterator.next().getResource();

					StmtIterator trackedResourceSetStatementsIterator = rootServicesDocumentModel.listStatements(trackedResourceSetProviderResource, RDF.type, rootServicesDocumentModel.createProperty(NAMESPACE_URI_JAZZ_TRS2, RESOURCE_TRACKED_RESOURCE_SET));

					while(trackedResourceSetStatementsIterator.hasNext()){

						Resource trackedResourceSetResource = trackedResourceSetStatementsIterator.next().getSubject();

						//Resolve the TRS2 provider URI:
						String trsProviderUri = trackedResourceSetResource.getRequiredProperty(rootServicesDocumentModel.createProperty(NAMESPACE_URI_JAZZ_TRS2, PROPERTY_TRACKED_RESOURCE_SET)).getResource().getURI();

						//Resolve the TRS2 provider type:
						String trsProviderType = trackedResourceSetResource.getRequiredProperty(rootServicesDocumentModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TYPE)).getResource().getURI();

						//Resolve the TRS2 provider title:
						String trsProviderTitle = trackedResourceSetProviderResource.getRequiredProperty(rootServicesDocumentModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE)).getString();

						//Resolve the TRS2 provider description:
						String trsProviderDescription = trackedResourceSetProviderResource.getRequiredProperty(rootServicesDocumentModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_DESCRIPTION)).getString();

						trs2Providers.add(new Trs2Provider(trsProviderUri, trsProviderType, trsProviderTitle, trsProviderDescription));
					}
				}

				if(trs2Providers.isEmpty()) {
					LogUtils.logError("Error resolving the Tracked Resource Set version 2 (TRS2) URIs from the root services document for server '" + getServerUrl() + "'."); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}

		return returnCode;
	}

	@Override
	public List<Trs2Provider> getTrs2Providers(){
		return trs2Providers;
	}
}
