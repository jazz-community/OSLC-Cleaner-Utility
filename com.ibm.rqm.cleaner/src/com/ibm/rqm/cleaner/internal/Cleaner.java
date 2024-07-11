/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2024. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.BASE_PAGE_FILE_NAME_PREFIX;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CHANGE_EVENT_TYPE_CREATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CHANGE_EVENT_TYPE_DELETION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CHANGE_EVENT_TYPE_MODIFICATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CHANGE_LOG_PAGE_FILE_NAME_PREFIX;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_ADD_MISSING_BACK_LINKS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_READ_ALL_RESOURCES;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_READ_TRS2;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_READ_TRS2_WITH_SELECTIONS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_REMOVE_ALL_FORWARD_LINKS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_REMOVE_BROKEN_FORWARD_LINKS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_REMOVE_BROKEN_FORWARD_LINKS_WITH_MISSING_BACK_LINKS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_REMOVE_LINK_CONFIG_CONTEXT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_RENAME_LINKS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_REPORT_BROKEN_LINKS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_UPDATE_BACK_LINK_LABELS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_UPDATE_FORWARD_LINK_LABELS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_UPDATE_LINK_LABELS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_UPDATE_VERSIONED_LINKS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_VALIDATE_TRS2;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.CLEANING_COMMAND_VALIDATE_TRS2_NO_SKIPPED_RESOURCES;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.FORWARD_SLASH;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_ETAG;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_LINK;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_METHOD_GET;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_METHOD_HEAD;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.LINE_SEPARATOR;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.LQE_BASE_PAGE_FILE_NAME_SEGMENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.LQE_CHANGE_LOG_PAGE_FILE_NAME_SEGMENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.LQE_TRS2_BASE_PAGES_DIRECTORY_NAME;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.LQE_TRS2_CHANGE_LOG_PAGES_DIRECTORY_NAME;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.LQE_TRS2_DIRECTORY_NAME;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.MEDIA_TYPE_OSLC_COMPACT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_DCTERMS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_DC_TERMS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_JAZZ_QM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_JAZZ_RM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_JAZZ_TRS2;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_JLDP;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_OSLC;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_OSLC_CONFIG;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_OWL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_RDF;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_RM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_TRS2_PATCH;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.OSLC_DIRECTORY_NAME;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_AFTER_ETAG;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_BASE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_BEFORE_ETAG;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_CHANGE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_CHANGED;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_COMPONENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_CUTOFF_EVENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_DCTERMS_IDENTIFIER;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_DCTERMS_MODIFIED;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_DOCUMENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_IDENTIFIER;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_IS_LOCKED;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_LABEL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_MEMBER;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_MODIFIED;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_NEXT_PAGE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OBJECT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_ORDER;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_CONFIG_COMPONENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_PREDICATE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_PREVIOUS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_RDF_PATCH;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_RDF_TYPE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_RELATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_REMOTE_LINK;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_SAME_AS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_SELECTS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_SERVICE_PROVIDER;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_SHORT_ID;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_SOFT_DELETED;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_SUBJECT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_TITLE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_TYPE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_WILDCARD;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROTOCOL_HTTP;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.QUERY_PARAMETER_DNG_SOURCE_OR_TARGET;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.QUERY_PARAMETER_OSLC_PAGE_SIZE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.QUERY_PARAMETER_OSLC_PREFIX;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.QUERY_PARAMETER_OSLC_PROPERTIES;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.QUERY_PARAMETER_OSLC_WHERE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_PAGE_FILE_NAME_SEGMENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_PREVIEW;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_RESPONSE_INFO;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TEMPLATE_QUERY;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_COMPACT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_LINK_TYPE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_REQUIREMENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_REQUIREMENT_COLLECTION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_CASE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_PLAN;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_SCRIPT_STEP;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.SELECTIONS_NEXT_PAGE_PATTERN;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.SELECTIONS_PAGE_FILE_NAME_PREFIX;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.SELECTIONS_URI_LAST_SEGMENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.TRS2_BASE_PAGES_DIRECTORY_NAME;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.TRS2_CHANGE_LOG_PAGES_DIRECTORY_NAME;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.TRS2_DIRECTORY_NAME;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.TRS2_SELECTIONS_DIRECTORY_NAME;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.TRS2_SELECTIONS_PAGES_DIRECTORY_NAME;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.TXT_FILE_EXTENSION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.URI_TEMPLATE_DNG_LINKS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.XML_FILE_EXTENSION;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.w3c.dom.Document;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.PropertyNotFoundException;
import com.hp.hpl.jena.vocabulary.RDF;
import com.ibm.icu.text.MessageFormat;
import com.ibm.rqm.cleaner.internal.client.Credential;
import com.ibm.rqm.cleaner.internal.client.HttpClientException;
import com.ibm.rqm.cleaner.internal.client.OslcHttpClient;
import com.ibm.rqm.cleaner.internal.client.OslcHttpClientFactory;
import com.ibm.rqm.cleaner.internal.client.ServiceProvider;
import com.ibm.rqm.cleaner.internal.client.Trs2Provider;
import com.ibm.rqm.cleaner.internal.client.dwa.DwaOslcHttpClient;
import com.ibm.rqm.cleaner.internal.client.qm.QmOslcHttpClient;
import com.ibm.rqm.cleaner.internal.client.rm.RmOslcHttpClient;
import com.ibm.rqm.cleaner.internal.util.CleanerUtils;
import com.ibm.rqm.cleaner.internal.util.DateTimeUtils;
import com.ibm.rqm.cleaner.internal.util.FileUtils;
import com.ibm.rqm.cleaner.internal.util.LogUtils;
import com.ibm.rqm.cleaner.internal.util.OslcUtils;
import com.ibm.rqm.cleaner.internal.util.PerformanceUtils;
import com.ibm.rqm.cleaner.internal.util.QName;
import com.ibm.rqm.cleaner.internal.util.RdfUtils;
import com.ibm.rqm.cleaner.internal.util.StringUtils;
import com.ibm.rqm.cleaner.internal.util.Trs2ChangeEvent;
import com.ibm.rqm.cleaner.internal.util.Trs2PatchEvent;
import com.ibm.rqm.cleaner.internal.util.UriUtils;
import com.ibm.rqm.cleaner.internal.util.UrlUtils;
import com.ibm.rqm.cleaner.internal.util.XhtmlUtils;
import com.ibm.rqm.cleaner.internal.util.XmlUtils;
import com.ibm.rqm.integration.client.clientlib.RQMProtocolException;

/**
 * <p>Cleaner to clean OSLC Quality Management (QM) resources.</p>
 * 
 * <p>For more information, see <code>readme.txt</code>.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public final class Cleaner {

	private List<String> commands = null;
	private final OslcHttpClient httpClient;
	private final List<ServiceProvider> serviceProviders;
	private final long lastModifiedDateTime;
	private final String oldPublicUrl;
	private final String newPublicUrl;
	private final boolean ignoreReadErrors;
	private final boolean output;
	private final boolean test;
	private final File inputDirectory;
	private final File outputDirectory;
	private final List<String> trs2ProviderTypes;
	private final List<String> ignoreResourceUrls;
	private final List<String> processResourceUrls;
	private final List<String> ignoreTargetResourceUrls;
	private final List<String> processTargetResourceUrls;
	private final Map<String, List<String>> propertyPrefixedNamePropertyLabelsMap;
	private final boolean usePrivateDngApi;
	private final boolean attemptRqmUnlock;
	private final int pageSize;
	private final List<String> resourceIdentifiers;

	private String projectAreaNames = null;
	private Map<String, List<String>> linkTypeUriSameAsUrisMap = null;

	public Cleaner(List<String> commands, OslcHttpClient httpClient, Map<URL, Credential> credentials, List<ServiceProvider> serviceProviders, long lastModifiedDateTime, String oldPublicUrl, String newPublicUrl, boolean ignoreReadErrors, boolean output, boolean test, File inputDirectory, File outputDirectory, List<String> trs2ProviderTypes, List<String> ignoreResourceUrls, List<String> processResourceUrls, List<String> ignoreTargetResourceUrls, List<String> processTargetResourceUrls, Map<String, List<String>> propertyPrefixedNamePropertyLabelsMap, boolean usePrivateDngApi, boolean attemptRqmUnlock, int pageSize, List<String> resourceIdentifiers){
		
		this.commands = commands;
		this.httpClient = httpClient;
		this.serviceProviders = serviceProviders;
		this.lastModifiedDateTime = lastModifiedDateTime;
		this.oldPublicUrl = oldPublicUrl;
		this.newPublicUrl = newPublicUrl;
		this.ignoreReadErrors = ignoreReadErrors;
		this.output = output;
		this.test = test;
		this.inputDirectory = inputDirectory;
		this.outputDirectory = outputDirectory;
		this.trs2ProviderTypes = trs2ProviderTypes;
		this.ignoreResourceUrls = ignoreResourceUrls;
		this.processResourceUrls = processResourceUrls;
		this.ignoreTargetResourceUrls = ignoreTargetResourceUrls;
		this.processTargetResourceUrls = processTargetResourceUrls;
		this.propertyPrefixedNamePropertyLabelsMap = propertyPrefixedNamePropertyLabelsMap;
		this.usePrivateDngApi = usePrivateDngApi;	
		this.attemptRqmUnlock = attemptRqmUnlock;
		this.pageSize = pageSize;
		this.resourceIdentifiers = resourceIdentifiers;
	}

	public void clean() throws Exception{
		
		if((commands.contains(CLEANING_COMMAND_VALIDATE_TRS2)) || (commands.contains(CLEANING_COMMAND_VALIDATE_TRS2_NO_SKIPPED_RESOURCES))){
			
			boolean validateSkippedResources = (!commands.contains(CLEANING_COMMAND_VALIDATE_TRS2_NO_SKIPPED_RESOURCES));

			List<Trs2Provider> trs2Providers = new ArrayList<Trs2Provider>();

			
			if(inputDirectory != null) {
			
				File trs2InputDirectory = new File(inputDirectory, TRS2_DIRECTORY_NAME);

				File[] childDirectories = trs2InputDirectory.listFiles(new FileFilter() {
					
					@Override
					public boolean accept(File file) {
						return file.isDirectory();
					}
				});
				
				if((childDirectories != null) && (childDirectories.length > 0)) {

					for(File childDirectory : childDirectories) {
						trs2Providers.add(new Trs2Provider(childDirectory.getAbsolutePath()));
					}
				}

				//Support the directory structure and RDF/XML file(s) generated by LQE/LDX V6.0.5+:
				else {

					trs2InputDirectory = new File(inputDirectory, LQE_TRS2_DIRECTORY_NAME);

					if(trs2InputDirectory.isDirectory()) {
						trs2Providers.add(new Trs2Provider(trs2InputDirectory.getAbsolutePath()));
					}
				}
			}
			else {
				trs2Providers = httpClient.getTrs2Providers();				
			}

			if(trs2Providers.isEmpty()) {

				if(inputDirectory != null) {
					LogUtils.logInfo("No TRS2 feeds found for input directory '" + inputDirectory+ "'."); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					LogUtils.logInfo("No TRS2 feeds found for server '" + httpClient.getServerUrl() + "'.");					 //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else {

				for(Trs2Provider trs2Provider : trs2Providers) {

					if((inputDirectory != null) || (trs2ProviderTypes.contains(trs2Provider.getType()))) {

						//Resolve the TRS2 feed model:
						Model trs2Model = null;

						if(inputDirectory != null) {

							File trs2FeedDirectory = new File(trs2Provider.getUrl());

							File trs2File = new File(trs2FeedDirectory, trs2FeedDirectory.getName() + XML_FILE_EXTENSION);

							//Support the directory structure and RDF/XML file(s) generated by LQE/LDX V6.0.5+:
							if(!trs2File.exists()) {

								//Example: https%3A%2F%2Fqvml523.ratl.swg.usma.ibm.com%3A9443%2Fqm%2Ftrs2.txt
								final FileFilter fileFilter = new FileFilter() {

									@Override
									public boolean accept(File file) {

										String fileName = file.getName();

										return ((file.isFile()) && (fileName.startsWith(PROTOCOL_HTTP)) && (fileName.endsWith(TXT_FILE_EXTENSION)));
									}
								};

								File[] trs2Files = trs2FeedDirectory.listFiles(fileFilter);

								if((trs2Files != null) && (trs2Files.length == 1)) {
									trs2File = trs2Files[0];
								}
							}

							if(output) {
								LogUtils.logInfo("Reading TRS2 feed " + trs2File.getAbsolutePath() + "."); //$NON-NLS-1$ //$NON-NLS-2$
							}

							trs2Model = RdfUtils.read(trs2File);
						}
						else {

							//Check for the 'TRS Consumer-Internal' license:
							int statusCode = httpClient.head(trs2Provider.getUrl());

							if((statusCode ==  HttpStatus.SC_FORBIDDEN) || (statusCode ==  HttpStatus.SC_UNAUTHORIZED)) {
								throw new HttpClientException("The user cannot access the TRS2 feed (" + trs2Provider.getUrl() + ").  Note, the user must have the (singleton) 'TRS Consumer-Internal' license assigned.  Before executing this command, unassign the 'TRS Consumer-Internal' license from the lqe_user or jts_user user and assign it to the user.  After executing this command, unassign the 'TRS Consumer-Internal' license from the user and reassign it to the lqe_user or jts_user user.", HTTP_METHOD_HEAD, trs2Provider.getUrl(), statusCode);  //$NON-NLS-1$ //$NON-NLS-2$
							}

							if(output) {
								LogUtils.logInfo("Reading TRS2 feed " + trs2Provider.getUrl() + "."); //$NON-NLS-1$ //$NON-NLS-2$
							}

							trs2Model = RdfUtils.read(httpClient, trs2Provider.getUrl());
						}

						//Resolve the TRS2 change event(s) from the TRS2 feed model and change log model(s):
						if(output) {
							LogUtils.logInfo("Reading TRS2 change log(s)."); //$NON-NLS-1$
						}

						List<Model> trs2ChangeLogModels = new ArrayList<Model>();
						trs2ChangeLogModels.add(trs2Model);

						if(inputDirectory != null) {

							File trs2ChangeLogsDirectory = new File(trs2Provider.getUrl(), TRS2_CHANGE_LOG_PAGES_DIRECTORY_NAME);

							File[] trs2ChangeLogFiles = trs2ChangeLogsDirectory.listFiles(new FileFilter() {

								@Override
								public boolean accept(File file) {
									return ((file.isFile()) && (file.getName().endsWith(XML_FILE_EXTENSION)));
								}
							});

							//Support the directory structure and RDF/XML file(s) generated by LQE/LDX V6.0.5+:
							if((trs2ChangeLogFiles == null) || (trs2ChangeLogFiles.length == 0)) {

								trs2ChangeLogsDirectory = new File(trs2Provider.getUrl(), LQE_TRS2_CHANGE_LOG_PAGES_DIRECTORY_NAME);

								trs2ChangeLogFiles = trs2ChangeLogsDirectory.listFiles(new FileFilter() {

									@Override
									public boolean accept(File file) {
										
										String fileName = file.getName();
										
										return ((file.isFile()) && (fileName.contains(LQE_CHANGE_LOG_PAGE_FILE_NAME_SEGMENT)) && (fileName.endsWith(TXT_FILE_EXTENSION)));
									}
								});
							}
							
							for(File trs2ChangeLogFile : trs2ChangeLogFiles) {

								if(output) {
									LogUtils.logInfo("Reading TRS2 change log " + trs2ChangeLogFile.getAbsolutePath() + "."); //$NON-NLS-1$ //$NON-NLS-2$
								}

								trs2ChangeLogModels.add(RdfUtils.read(trs2ChangeLogFile));
							}
						}
						else {

							Model trs2ChangeLogModel = trs2Model;

							while(trs2ChangeLogModel != null) {

								String trs2ChangeLogUri = RdfUtils.getPropertyResourceValue(trs2ChangeLogModel, NAMESPACE_URI_JAZZ_TRS2, PROPERTY_PREVIOUS);

								if((StringUtils.isSet(trs2ChangeLogUri)) && (!RDF.nil.getURI().equals(trs2ChangeLogUri))){

									if(output) {
										LogUtils.logInfo("Reading TRS2 change log '" + trs2ChangeLogUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$
									}

									//Resolve the TRS2 change log model:
									trs2ChangeLogModel = RdfUtils.read(httpClient, trs2ChangeLogUri);

									trs2ChangeLogModels.add(trs2ChangeLogModel);
								}
								else {
									break;
								}
							}
						}				

						if(output) {
							LogUtils.logInfo("Resolving the TRS2 change event(s) from the TRS2 change log(s)."); //$NON-NLS-1$
						}

						List<Trs2ChangeEvent> trs2ChangeEvents = new ArrayList<Trs2ChangeEvent>();

						for(Model trs2ChangeLogModel : trs2ChangeLogModels) {

							//Resolve the TRS2 change event(s):
							StmtIterator trs2ChangeEventStatementsIterator = trs2ChangeLogModel.listStatements(null, trs2ChangeLogModel.createProperty(NAMESPACE_URI_JAZZ_TRS2, PROPERTY_CHANGE), ((RDFNode)(null)));

							while(trs2ChangeEventStatementsIterator.hasNext()) {

								Resource trs2ChangeEventResource = trs2ChangeEventStatementsIterator.next().getResource();

								try {

									Trs2ChangeEvent trs2ChangeEvent = new Trs2ChangeEvent(trs2ChangeEventResource.getURI());
									trs2ChangeEvent.setChanged(trs2ChangeEventResource.getRequiredProperty(trs2ChangeLogModel.createProperty(NAMESPACE_URI_JAZZ_TRS2, PROPERTY_CHANGED)).getResource().getURI());
									trs2ChangeEvent.setType(trs2ChangeEventResource.getRequiredProperty(trs2ChangeLogModel.createProperty(NAMESPACE_URI_RDF, PROPERTY_TYPE)).getResource().getURI());
									trs2ChangeEvent.setOrder(trs2ChangeEventResource.getRequiredProperty(trs2ChangeLogModel.createProperty(NAMESPACE_URI_JAZZ_TRS2, PROPERTY_ORDER)).getLong());

									Property rdfPatchProperty = trs2ChangeLogModel.createProperty(NAMESPACE_URI_TRS2_PATCH, PROPERTY_RDF_PATCH);

									trs2ChangeEvent.setIsRdfPatch(trs2ChangeEventResource.hasProperty(rdfPatchProperty));

									if(trs2ChangeEvent.isRdfPatch()) {

										trs2ChangeEvent.setBeforeETag(trs2ChangeEventResource.getRequiredProperty(trs2ChangeLogModel.createProperty(NAMESPACE_URI_TRS2_PATCH, PROPERTY_BEFORE_ETAG)).getLiteral().getLong());
										trs2ChangeEvent.setAfterETag(trs2ChangeEventResource.getRequiredProperty(trs2ChangeLogModel.createProperty(NAMESPACE_URI_TRS2_PATCH, PROPERTY_AFTER_ETAG)).getLiteral().getLong());
										trs2ChangeEvent.setRdfPatch(trs2ChangeEventResource.getRequiredProperty(rdfPatchProperty).getLiteral().getString());
									}

									trs2ChangeEvents.add(trs2ChangeEvent);
								} 
								catch (PropertyNotFoundException p) {
									LogUtils.logInfo("Change event " + trs2ChangeEventResource.getURI() + " is missing a property (expected: " + p.getMessage() + ").  Note: This change event will be ignored."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								}
							}
						}

						//Sort (ascending) the TRS2 change event(s) by the trs2:order property:
						Collections.sort(trs2ChangeEvents, new Comparator<Trs2ChangeEvent>() {

							@Override
							public int compare(Trs2ChangeEvent leftTrs2ChangeEvent, Trs2ChangeEvent rightTrs2ChangeEvent) {
								return (Long.compare(leftTrs2ChangeEvent.getOrder(), rightTrs2ChangeEvent.getOrder()));
							}
						});

						//Resolve the TRS2 base member(s) and TRS2 cutoff change event from the TRS2 base(s):
						if(output) {
							LogUtils.logInfo("Reading TRS2 base(s)."); //$NON-NLS-1$
						}

						List<Model> trs2BaseModels = new ArrayList<Model>();

						if(inputDirectory != null) {

							File trs2BasesDirectory = new File(trs2Provider.getUrl(), TRS2_BASE_PAGES_DIRECTORY_NAME);

							File[] trs2BaseFiles = trs2BasesDirectory.listFiles(new FileFilter() {

								@Override
								public boolean accept(File file) {
									return ((file.isFile()) && (file.getName().endsWith(XML_FILE_EXTENSION)));
								}
							});

							//Support the directory structure and RDF/XML file(s) generated by LQE/LDX V6.0.5+:
							if((trs2BaseFiles == null) || (trs2BaseFiles.length == 0)) {

								trs2BasesDirectory = new File(trs2Provider.getUrl(), LQE_TRS2_BASE_PAGES_DIRECTORY_NAME);

								trs2BaseFiles = trs2BasesDirectory.listFiles(new FileFilter() {

									@Override
									public boolean accept(File file) {
										
										String fileName = file.getName();
										
										return ((file.isFile()) && (fileName.contains(LQE_BASE_PAGE_FILE_NAME_SEGMENT)) && (fileName.endsWith(TXT_FILE_EXTENSION)));
									}
								});
							}
							
							for(File trs2BaseFile : trs2BaseFiles) {

								if(output) {
									LogUtils.logInfo("Reading TRS2 base " + trs2BaseFile.getAbsolutePath() + "."); //$NON-NLS-1$ //$NON-NLS-2$
								}

								trs2BaseModels.add(RdfUtils.read(trs2BaseFile));
							}
						}
						else {

							Model trs2BaseModel = trs2Model;

							while(trs2BaseModel != null) {

								//Case 1: TRS2 feed model:
								String trs2BaseUri = RdfUtils.getPropertyResourceValue(trs2BaseModel, NAMESPACE_URI_JAZZ_TRS2, PROPERTY_BASE);

								//Case 2: TRS2 base model:
								if(StringUtils.isNotSet(trs2BaseUri)) {			
									trs2BaseUri = RdfUtils.getPropertyResourceValue(trs2BaseModel, NAMESPACE_URI_JLDP, PROPERTY_NEXT_PAGE);
								}

								if((StringUtils.isSet(trs2BaseUri)) && (!RDF.nil.getURI().equals(trs2BaseUri))){

									if(output) {
										LogUtils.logInfo("Reading TRS2 base '" + trs2BaseUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$
									}

									trs2BaseModel = RdfUtils.read(httpClient, trs2BaseUri);

									trs2BaseModels.add(trs2BaseModel);
								}
								else {
									break;
								}
							}
						}

						if(output) {
							LogUtils.logInfo("Resolving the TRS2 base member(s) and TRS2 cutoff change event from the TRS2 base."); //$NON-NLS-1$
						}

						List<String> trs2BaseMemberUris = new ArrayList<String>();

						String trs2CutoffChangeEventUri = null;

						for(Model trs2BaseModel : trs2BaseModels) {

							//Resolve the TRS2 base member(s)
							StmtIterator trs2BaseMemberStatementsIterator = trs2BaseModel.listStatements(null, trs2BaseModel.createProperty(NAMESPACE_URI_JLDP, PROPERTY_MEMBER), ((RDFNode)(null)));

							while(trs2BaseMemberStatementsIterator.hasNext()) {
								trs2BaseMemberUris.add(trs2BaseMemberStatementsIterator.next().getResource().getURI());
							}

							//Resolve the TRS2 cutoff change event URI:
							String newTrs2CutoffChangeEventUri = RdfUtils.getPropertyResourceValue(trs2BaseModel, NAMESPACE_URI_JAZZ_TRS2, PROPERTY_CUTOFF_EVENT);

							if(StringUtils.isSet(newTrs2CutoffChangeEventUri)) {

								//Assumption: There is exactly-only trs2:cutoffEvent property in the TRS2 base.
								if(StringUtils.isSet(trs2CutoffChangeEventUri)) {
									LogUtils.logInfo("Another trs2:cutoffEvent property (" + newTrs2CutoffChangeEventUri + ") was found in the TRS2 base.  There should be exactly-only trs2:cutoffEvent property in the TRS2 base."); //$NON-NLS-1$ //$NON-NLS-2$
								}
								else {
									trs2CutoffChangeEventUri = newTrs2CutoffChangeEventUri;
								}
							}
						}

						//Validate the properties of the TRS2 change event(s):
						if(output) {
							LogUtils.logInfo("Validating properties of " + trs2ChangeEvents.size() + " TRS2 change event" + (trs2ChangeEvents.size() == 1 ? "":"s") + "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						}

						Map<String, Long> selectionsCurrentETag = new HashMap<String, Long>();

						for(Trs2ChangeEvent trs2ChangeEvent : trs2ChangeEvents) {

							//Required properties:
							long order = trs2ChangeEvent.getOrder();
							String type = trs2ChangeEvent.getType();
							String changed = trs2ChangeEvent.getChanged();

							//Optional properties:
							long beforeETag = trs2ChangeEvent.getBeforeETag();
							long afterETag = trs2ChangeEvent.getAfterETag();
							String rdfPatch = trs2ChangeEvent.getRdfPatch();
							boolean isRdfPatch = trs2ChangeEvent.isRdfPatch();

							if(order <= 0) {
								
								LogUtils.logInfo("Change event #" + order + " contains an invalid order (expected: positive number, actual: " + order + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								
								if(output) {
									LogUtils.logInfo(trs2ChangeEvent.toXml());
								}
							}

							if((StringUtils.isNotSet(type)) || ((!CHANGE_EVENT_TYPE_CREATION.equalsIgnoreCase(type)) && (!CHANGE_EVENT_TYPE_MODIFICATION.equalsIgnoreCase(type)) && (!CHANGE_EVENT_TYPE_DELETION.equalsIgnoreCase(type)))) {

								LogUtils.logInfo("Change event #" + order + " contains an invalid type (expected: " + CHANGE_EVENT_TYPE_CREATION + ", " + CHANGE_EVENT_TYPE_MODIFICATION + ", or " + CHANGE_EVENT_TYPE_DELETION + ", actual: " + type + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

								if(output) {
									LogUtils.logInfo(trs2ChangeEvent.toXml());
								}
							}

							if(StringUtils.isNotSet(changed)) {
								
								LogUtils.logInfo("Change event #" + order + " contains an invalid changed (expected: non-empty test artifact, shape, or selections URI, actual: " + changed + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								
								if(output) {
									LogUtils.logInfo(trs2ChangeEvent.toXml());
								}
							}

							if(isRdfPatch) {

								if(beforeETag < 0) {
									
									LogUtils.logInfo("Change event #" + order + " contains an invalid before eTag (expected: zero or positive number, actual: " + beforeETag + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									
									if(output) {
										LogUtils.logInfo(trs2ChangeEvent.toXml());
									}
								}

								if(afterETag <= 0) {

									LogUtils.logInfo("Change event #" + order + " contains an invalid after eTag (expected: positive number, actual: " + afterETag + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									
									if(output) {
										LogUtils.logInfo(trs2ChangeEvent.toXml());
									}
								}

								if(afterETag != order) {

									LogUtils.logInfo("Change event #" + order + " contains an invalid after eTag (expected: equal to the order (" + order + "), actual: " + afterETag + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
									
									if(output) {
										LogUtils.logInfo(trs2ChangeEvent.toXml());
									}
								}

								if(afterETag <= beforeETag) {
									
									LogUtils.logInfo("Change event #" + order + " contains an invalid after eTag (expected: greater than the before eTag (" + beforeETag + "), actual: " + afterETag + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
									
									if(output) {
										LogUtils.logInfo(trs2ChangeEvent.toXml());
									}
								}

								if(selectionsCurrentETag.containsKey(changed)) {

									long currentETag = selectionsCurrentETag.get(changed);

									if(currentETag != beforeETag) {
										
										LogUtils.logInfo("Change event #" + order + " contains a mismatched before eTag (expected: previous selections order (" + currentETag + "), actual: " + beforeETag + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
										
										if(output) {
											LogUtils.logInfo(trs2ChangeEvent.toXml());
										}
									}
								}

								selectionsCurrentETag.put(changed, afterETag);

								if(!changed.endsWith(SELECTIONS_URI_LAST_SEGMENT)) { 
									
									LogUtils.logInfo("Change event #" + order + " contains an invalid changed (expected: non-empty selections URI, actual: " + changed + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									
									if(output) {
										LogUtils.logInfo(trs2ChangeEvent.toXml());
									}
								}

								try {

									if(StringUtils.isNotSet(rdfPatch)) {
										
										LogUtils.logInfo("Change event #" + order + " contains an invalid patch (expected: non-empty patch, actual: empty patch)."); //$NON-NLS-1$ //$NON-NLS-2$
										
										if(output) {
											LogUtils.logInfo(trs2ChangeEvent.toXml());
										}
									}
									else {

										Trs2PatchEvent trs2PatchEvent = Trs2PatchEvent.parse(rdfPatch);

										if(!trs2PatchEvent.toString().equals(rdfPatch)) {
											
											LogUtils.logInfo("Change event #" + order + " contains an invalid patch (expected: valid patch syntax, actual: " + rdfPatch + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$	
											
											if(output) {
												LogUtils.logInfo(trs2ChangeEvent.toXml());
											}
										}
										else if(!trs2PatchEvent.getSelectionsUri().equals(changed)) {
											
											LogUtils.logInfo("Change event #" + order + " contains an invalid patch (expected: selections URI matching the changed (" + changed + "), actual: " + rdfPatch + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$	
											
											if(output) {
												LogUtils.logInfo(trs2ChangeEvent.toXml());
											}
										}
									}
								}
								catch (Exception e) {

									LogUtils.logInfo("Change event #" + order + " contains an invalid patch (expected: valid patch syntax, actual: " + rdfPatch + "): " + e.toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$	
									
									if(output) {
										LogUtils.logInfo(trs2ChangeEvent.toXml());
									}
								}
							}
							else{

								if(beforeETag != -1) {
									
									LogUtils.logInfo("Change event #" + order + " contains a before eTag (expected: not set, actual: " + beforeETag + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									
									if(output) {
										LogUtils.logInfo(trs2ChangeEvent.toXml());
									}
								}

								if(afterETag != -1) {
									
									LogUtils.logInfo("Change event #" + order + " contains an after eTag (expected: not set, actual: " + afterETag + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									
									if(output) {
										LogUtils.logInfo(trs2ChangeEvent.toXml());
									}
								}

								//Note: TRS2 change events (creation/modification/deletion) for selections do NOT use a patch.
								if(changed.endsWith(SELECTIONS_URI_LAST_SEGMENT)) {

									if((CHANGE_EVENT_TYPE_CREATION.equalsIgnoreCase(type) || (CHANGE_EVENT_TYPE_MODIFICATION.equalsIgnoreCase(type)))) { 
										selectionsCurrentETag.put(changed, order);
									}	
									else if(CHANGE_EVENT_TYPE_DELETION.equalsIgnoreCase(type)) { 
										selectionsCurrentETag.remove(changed);
									}	
								}
							}
						}

						//Validate the response ETag for selections:
						if(validateSkippedResources) { 

							if(output) {
								LogUtils.logInfo("Validating the response ETag for " + selectionsCurrentETag.size() + " selection" + (selectionsCurrentETag.size() == 1 ? "":"s") + "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
							}

							for(String selectsUri : selectionsCurrentETag.keySet()) {

								long order = selectionsCurrentETag.get(selectsUri);
								String eTagResponseHeader = httpClient.getResponseHeader(selectsUri, HTTP_HEADER_ETAG);

								if(StringUtils.isLong(eTagResponseHeader)) {

									long eTag = Long.parseLong(eTagResponseHeader);

									if(order != eTag) {
										LogUtils.logInfo("Selections URI '" + selectsUri + "' contains an invalid ETag (expected: " + order + ", actual: " + eTag + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$					
									}
								}
								else {
									LogUtils.logInfo("Selections URI '" + selectsUri + "' contains an invalid ETag (expected: " + order + ", actual: " + eTagResponseHeader + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$					
								}
							}
						}
						else if(output) {
							LogUtils.logInfo("Note: Validation will NOT validate the response ETag for " + selectionsCurrentETag.size() + " selection" + (selectionsCurrentETag.size() == 1 ? "":"s") + ".  To validate the response ETag for selections, run the '" + CLEANING_COMMAND_VALIDATE_TRS2 + "' command."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
						}

						//Simulate LQE indexing TRS2 base member(s):
						if(output) {
							LogUtils.logInfo("Simulating LQE indexing of " + trs2BaseMemberUris.size() + " TRS2 base member" + (trs2ChangeEvents.size() == 1 ? "":"s") + "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						}

						if((!validateSkippedResources) && (output)) { 
							LogUtils.logInfo("Note: Simulated LQE indexing will NOT validate skipped resources.  To validate skipped resources, run the '" + CLEANING_COMMAND_VALIDATE_TRS2 + "' command."); //$NON-NLS-1$ //$NON-NLS-2$
						}

						Map<String, IOException> skippedResources = new HashMap<String, IOException>();

						Map<String, Map<String, IOException>> skippedSelectionsResources = new HashMap<String, Map<String, IOException>>();

						//Index the TRS2 base member(s):
						for(String trs2BaseMemberUri : trs2BaseMemberUris) {

							//Index the selections:
							if(trs2BaseMemberUri.endsWith(SELECTIONS_URI_LAST_SEGMENT)) {

								if(validateSkippedResources) {

									List<String> selectsUris = new ArrayList<String>();

									resolveSelectsUris(trs2BaseMemberUri, selectsUris);

									for(String selectsUri : selectsUris) {

										try {
											httpClient.get(selectsUri);
										}
										catch (IOException i) {

											if(!skippedSelectionsResources.containsKey(trs2BaseMemberUri)) {
												skippedSelectionsResources.put(trs2BaseMemberUri, new HashMap<String, IOException>());
											}

											skippedSelectionsResources.get(trs2BaseMemberUri).put(selectsUri, i);
										}
									}
								}
							}

							//Index the test artifact or shape:
							else if(validateSkippedResources){

								try {
									httpClient.get(trs2BaseMemberUri);
								}
								catch (IOException i) {
									skippedResources.put(trs2BaseMemberUri, i);
								}
							}
						}

						//Simulate LQE indexing TRS2 change event(s):
						if(StringUtils.isNotSet(trs2CutoffChangeEventUri)) {
							LogUtils.logInfo("Cannot simulating LQE indexing of the TRS2 change events since the TRS2 base does NOT contain the TRS2 cutoff change event."); //$NON-NLS-1$
						}
						else {

							//Resolve the TRS2 change event(s) starting with the TRS2 cutoff event:
							List<Trs2ChangeEvent> indexedTrs2ChangeEvents = trs2ChangeEvents;

							if(RDF.nil.getURI().equals(trs2CutoffChangeEventUri)) {

								if(output) {
									LogUtils.logInfo("Simulating LQE indexing of all " + trs2ChangeEvents.size() + " TRS2 change event" + (trs2ChangeEvents.size() == 1 ? "":"s") + " since the TRS2 cutoff change event is rdf:nil."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
								}

								if((!validateSkippedResources) && (output)) { 
									LogUtils.logInfo("Note: Simulated LQE indexing will NOT validate skipped resources.  To validate skipped resources, run the '" + CLEANING_COMMAND_VALIDATE_TRS2 + "' command."); //$NON-NLS-1$ //$NON-NLS-2$
								}
							}
							else {

								int trs2CutoffChangeEventIndex = trs2ChangeEvents.indexOf(new Trs2ChangeEvent(trs2CutoffChangeEventUri));

								if(trs2CutoffChangeEventIndex == -1) {

									if(output) {
										LogUtils.logInfo("Simulating LQE indexing of all " + trs2ChangeEvents.size() + " TRS2 change event" + (trs2ChangeEvents.size() == 1 ? "":"s") + " since the TRS2 change log does NOT contain the TRS2 cutoff change event '" + trs2CutoffChangeEventUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
									}

									if((!validateSkippedResources) && (output)) { 
										LogUtils.logInfo("Note: Simulated LQE indexing will NOT validate skipped resources.  To validate skipped resources, run the '" + CLEANING_COMMAND_VALIDATE_TRS2 + "' command."); //$NON-NLS-1$ //$NON-NLS-2$
									}
								}
								else {

									indexedTrs2ChangeEvents = trs2ChangeEvents.subList(trs2CutoffChangeEventIndex, trs2ChangeEvents.size());

									if(output) {
										LogUtils.logInfo("Simulating LQE indexing of " + indexedTrs2ChangeEvents.size() + "/" + trs2ChangeEvents.size() + " TRS2 change event" + (indexedTrs2ChangeEvents.size() == 1 ? "":"s") + " after TRS2 cutoff change event '" + trs2CutoffChangeEventUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
									}

									if((!validateSkippedResources) && (output)) { 
										LogUtils.logInfo("Note: Simulated LQE indexing will NOT validate skipped resources.  To validate skipped resources, run the '" + CLEANING_COMMAND_VALIDATE_TRS2 + "' command."); //$NON-NLS-1$ //$NON-NLS-2$
									}
								}
							}

							//Index the TRS2 change event(s):
							for(Trs2ChangeEvent indexedTrs2ChangeEvent : indexedTrs2ChangeEvents) {

								String type = indexedTrs2ChangeEvent.getType();
								String changed = indexedTrs2ChangeEvent.getChanged();

								//Index the selections:
								if(changed.endsWith(SELECTIONS_URI_LAST_SEGMENT)) {

									//Note: Modification change events without a RDF patch are used to reset the ETags.
									if((CHANGE_EVENT_TYPE_CREATION.equalsIgnoreCase(type)) || ((CHANGE_EVENT_TYPE_MODIFICATION.equalsIgnoreCase(type)) && (!indexedTrs2ChangeEvent.isRdfPatch()))) {

										if(validateSkippedResources) {

											List<String> selectsUris = new ArrayList<String>();

											resolveSelectsUris(changed, selectsUris);

											for(String selectsUri : selectsUris) {

												try {
													httpClient.get(selectsUri);
												}
												catch (IOException i) {

													if(!skippedSelectionsResources.containsKey(changed)) {
														skippedSelectionsResources.put(changed, new HashMap<String, IOException>());
													}

													skippedSelectionsResources.get(changed).put(selectsUri, i);
												}
											}
										}
									}
									else if(CHANGE_EVENT_TYPE_DELETION.equalsIgnoreCase(type)) {
										skippedSelectionsResources.remove(changed);
									}
									else if(CHANGE_EVENT_TYPE_MODIFICATION.equalsIgnoreCase(type)) {

										Trs2PatchEvent trs2PatchEvent = Trs2PatchEvent.parse(indexedTrs2ChangeEvent.getRdfPatch());

										String deletedUri = trs2PatchEvent.getDeletedUri();

										if((StringUtils.isSet(deletedUri)) && (skippedSelectionsResources.containsKey(changed))) {
											skippedSelectionsResources.get(changed).remove(deletedUri);
										}

										String addedUri = trs2PatchEvent.getAddedUri();

										if((StringUtils.isSet(addedUri)) && (validateSkippedResources)) {

											try {
												httpClient.get(addedUri);
											}
											catch (IOException i) {

												if(!skippedSelectionsResources.containsKey(changed)) {
													skippedSelectionsResources.put(changed, new HashMap<String, IOException>());
												}

												skippedSelectionsResources.get(changed).put(addedUri, i);
											}
										}
									}
								}

								//Index the test artifact or shape (deletion event):
								else if(CHANGE_EVENT_TYPE_DELETION.equalsIgnoreCase(type)) {
									skippedResources.remove(changed);
								}

								//Index the test artifact or shape (creation/modification event):
								else if(validateSkippedResources){

									try {
										httpClient.get(changed);
									}
									catch (IOException i) {
										skippedResources.put(changed, i);
									}
								}
							}
						}

						//Print out the skipped resources:
						if(validateSkippedResources) {

							if(!skippedResources.isEmpty()) {

								LogUtils.logInfo("Skipped Resources (bases/change logs):"); //$NON-NLS-1$

								for(String skippedResourceUri : skippedResources.keySet()) {
									LogUtils.logInfo("Skipped Resource:\n" + skippedResourceUri + "\n" + resolveSkippedResourceMessage(skippedResourceUri, skippedResources.get(skippedResourceUri))); //$NON-NLS-1$ //$NON-NLS-2$
								}
							}

							if(!skippedSelectionsResources.isEmpty()) {

								LogUtils.logInfo("Skipped Resources (selections):"); //$NON-NLS-1$

								for(String selectionsUri : skippedSelectionsResources.keySet()) {

									for(String skippedSelectionsResourceUri : skippedSelectionsResources.get(selectionsUri).keySet()) {
										LogUtils.logInfo("Skipped Resource (" + selectionsUri + "):\n" + skippedSelectionsResourceUri + "\n" + resolveSkippedResourceMessage(skippedSelectionsResourceUri, skippedSelectionsResources.get(selectionsUri).get(skippedSelectionsResourceUri))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									}
								}
							}
						}
					}
				}

				LogUtils.logInfo("Validated " + trs2Providers.size() + " TRS2 feed" + (trs2Providers.size() == 1 ? "" : "s") + " for " + (inputDirectory != null ? "input directory '" + inputDirectory+ "'" : "server '" + httpClient.getServerUrl() + "'") + (output ? ":" : ".")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$

				if(output){

					for(Trs2Provider trs2Provider : trs2Providers){
						LogUtils.logInfo(trs2Provider.getUrl());
					}
				}
			}
		}
		else if((commands.contains(CLEANING_COMMAND_READ_TRS2)) || (commands.contains(CLEANING_COMMAND_READ_TRS2_WITH_SELECTIONS))){

			final List<Trs2Provider> trs2Providers = httpClient.getTrs2Providers();

			if(trs2Providers.isEmpty()) {
				LogUtils.logInfo("No TRS2 feeds found for server '" + httpClient.getServerUrl() + "'."); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {

				boolean readSelections = (commands.contains(CLEANING_COMMAND_READ_TRS2_WITH_SELECTIONS));

				File trs2OutputDirectory = null;
				
				if(outputDirectory != null) {

					//Create the output directories:
					//Assumption: One or more valid TRS2 provider types are configured.
					trs2OutputDirectory = new File(outputDirectory, TRS2_DIRECTORY_NAME);

					if(trs2OutputDirectory.exists()) {
						FileUtils.deleteDirectory(trs2OutputDirectory);
					}

					trs2OutputDirectory.mkdir();
				}
				
				for(Trs2Provider trs2Provider : trs2Providers) {

					if(trs2ProviderTypes.contains(trs2Provider.getType())) {

						//Read/write the TRS2 feed:
						if(output) {
							LogUtils.logInfo("Reading TRS2 feed " + trs2Provider.getUrl() + "."); //$NON-NLS-1$ //$NON-NLS-2$
						}

						List<String> trs2SelectionsUris = new ArrayList<String>();

						File trs2FeedOutputDirectory = null;
						
						if(trs2OutputDirectory != null) {

							trs2FeedOutputDirectory = new File(trs2OutputDirectory, UrlUtils.getLastSegment(trs2Provider.getUrl()));
							
							trs2FeedOutputDirectory.mkdir();
						}

						//Check for the 'TRS Consumer-Internal' license:
						int statusCode = httpClient.head(trs2Provider.getUrl());

						if((statusCode ==  HttpStatus.SC_FORBIDDEN) || (statusCode ==  HttpStatus.SC_UNAUTHORIZED)) {
							throw new HttpClientException("The user cannot access the TRS2 feed (" + trs2Provider.getUrl() + ").  Note, the user must have the (singleton) 'TRS Consumer-Internal' license assigned.  Before executing this command, unassign the 'TRS Consumer-Internal' license from the lqe_user or jts_user user and assign it to the user.  After executing this command, unassign the 'TRS Consumer-Internal' license from the user and reassign it to the lqe_user or jts_user user.", HTTP_METHOD_HEAD, trs2Provider.getUrl(), statusCode); //$NON-NLS-1$ //$NON-NLS-2$
						}

						Model trs2Model = RdfUtils.read(httpClient, trs2Provider.getUrl());

						//Resolve the selections URIs(s):
						if(readSelections) {

							StmtIterator trs2ChangeEventChangedStatementsIterator = trs2Model.listStatements(null, trs2Model.createProperty(NAMESPACE_URI_JAZZ_TRS2, PROPERTY_CHANGED), ((RDFNode)(null)));

							while(trs2ChangeEventChangedStatementsIterator.hasNext()) {

								String trs2ChangeEventChangedUri = trs2ChangeEventChangedStatementsIterator.next().getResource().getURI();

								if((trs2ChangeEventChangedUri.endsWith(SELECTIONS_URI_LAST_SEGMENT)) && (!trs2SelectionsUris.contains(trs2ChangeEventChangedUri))) {
									trs2SelectionsUris.add(trs2ChangeEventChangedUri);
								}
							}
						}

						if(trs2FeedOutputDirectory != null) {

							File trs2File = new File(trs2FeedOutputDirectory, (UrlUtils.getLastSegment(trs2Provider.getUrl()) + XML_FILE_EXTENSION));
							trs2File.createNewFile();

							if(output) {
								LogUtils.logInfo("Writing TRS2 feed to " + trs2File.getAbsolutePath() + "."); //$NON-NLS-1$ //$NON-NLS-2$
							}

							PrintStream trs2PrintStream = new PrintStream(trs2File);
							trs2PrintStream.print(RdfUtils.write(trs2Model));
							trs2PrintStream.close();
						}
						else {
							
							if(output) {
								LogUtils.logInfo("Writing TRS2 feed '" + UrlUtils.getLastSegment(trs2Provider.getUrl()) + "' to the console."); //$NON-NLS-1$ //$NON-NLS-2$
							}
							
							LogUtils.logInfo(RdfUtils.write(trs2Model).trim());
						}

						//Read/write the TRS2 change log(s):
						if(output) {
							LogUtils.logInfo("Reading TRS2 change log(s)."); //$NON-NLS-1$
						}

						File trs2ChangeLogOutputDirectory = null;
						
						if(trs2FeedOutputDirectory != null) {
							
							trs2ChangeLogOutputDirectory = new File(trs2FeedOutputDirectory, TRS2_CHANGE_LOG_PAGES_DIRECTORY_NAME);			
							trs2ChangeLogOutputDirectory.mkdir();
						}
						
						int trs2ChangeLogPageCount = 0;

						Model trs2ChangeLogModel = trs2Model;

						while(trs2ChangeLogModel != null) {

							String trs2ChangeLogUri = RdfUtils.getPropertyResourceValue(trs2ChangeLogModel, NAMESPACE_URI_JAZZ_TRS2, PROPERTY_PREVIOUS);

							if((StringUtils.isSet(trs2ChangeLogUri)) && (!RDF.nil.getURI().equals(trs2ChangeLogUri))){

								if(output) {
									LogUtils.logInfo("Reading TRS2 change log '" + trs2ChangeLogUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$
								}

								trs2ChangeLogModel = RdfUtils.read(httpClient, trs2ChangeLogUri);

								//Resolve the selections URIs(s):
								if(readSelections) {

									StmtIterator trs2ChangeEventChangedStatementsIterator = trs2ChangeLogModel.listStatements(null, trs2ChangeLogModel.createProperty(NAMESPACE_URI_JAZZ_TRS2, PROPERTY_CHANGED), ((RDFNode)(null)));

									while(trs2ChangeEventChangedStatementsIterator.hasNext()) {

										String trs2ChangeEventChangedUri = trs2ChangeEventChangedStatementsIterator.next().getResource().getURI();

										if((trs2ChangeEventChangedUri.endsWith(SELECTIONS_URI_LAST_SEGMENT)) && (!trs2SelectionsUris.contains(trs2ChangeEventChangedUri))) {
											trs2SelectionsUris.add(trs2ChangeEventChangedUri);
										}
									}
								}

								trs2ChangeLogPageCount++;

								if(trs2ChangeLogOutputDirectory != null) {
									
									File trs2ChangeLogFile = new File(trs2ChangeLogOutputDirectory, (CHANGE_LOG_PAGE_FILE_NAME_PREFIX + trs2ChangeLogPageCount + XML_FILE_EXTENSION));
									trs2ChangeLogFile.createNewFile();

									if(output) {
										LogUtils.logInfo("Writing TRS2 change log to " + trs2ChangeLogFile.getAbsolutePath() + "."); //$NON-NLS-1$ //$NON-NLS-2$
									}

									PrintStream trs2ChangeLogPrintStream = new PrintStream(trs2ChangeLogFile);
									trs2ChangeLogPrintStream.print(RdfUtils.write(trs2ChangeLogModel));
									trs2ChangeLogPrintStream.close();
								}
								else {
									
									if(output) {
										LogUtils.logInfo("Writing TRS2 change log '" + CHANGE_LOG_PAGE_FILE_NAME_PREFIX + trs2ChangeLogPageCount + "' to the console."); //$NON-NLS-1$ //$NON-NLS-2$
									}
									
									LogUtils.logInfo(RdfUtils.write(trs2ChangeLogModel).trim());
								}
							}
							else {
								break;
							}
						}

						//Read/write the TRS2 base(s):
						if(output) {
							LogUtils.logInfo("Reading TRS2 base(s)."); //$NON-NLS-1$
						}

						File trs2BaseOutputDirectory = null;

						if(trs2FeedOutputDirectory != null) {

							trs2BaseOutputDirectory = new File(trs2FeedOutputDirectory, TRS2_BASE_PAGES_DIRECTORY_NAME);			
							trs2BaseOutputDirectory.mkdir();
						}

						int trs2BasePageCount = 0;

						Model trs2BaseModel = trs2Model;

						while(trs2BaseModel != null) {

							//Case 1: TRS2 feed model:
							String trs2BaseUri = RdfUtils.getPropertyResourceValue(trs2BaseModel, NAMESPACE_URI_JAZZ_TRS2, PROPERTY_BASE);

							//Case 2: TRS2 base model:
							if(StringUtils.isNotSet(trs2BaseUri)) {			
								trs2BaseUri = RdfUtils.getPropertyResourceValue(trs2BaseModel, NAMESPACE_URI_JLDP, PROPERTY_NEXT_PAGE);
							}

							if((StringUtils.isSet(trs2BaseUri)) && (!RDF.nil.getURI().equals(trs2BaseUri))){

								if(output) {
									LogUtils.logInfo("Reading TRS2 base '" + trs2BaseUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$
								}

								trs2BaseModel = RdfUtils.read(httpClient, trs2BaseUri);

								//Resolve the selections URIs(s):
								if(readSelections) {

									StmtIterator trs2BaseMemberStatementsIterator = trs2BaseModel.listStatements(null, trs2BaseModel.createProperty(NAMESPACE_URI_JLDP, PROPERTY_MEMBER), ((RDFNode)(null)));

									while(trs2BaseMemberStatementsIterator.hasNext()) {

										String trs2BaseMemberUri = trs2BaseMemberStatementsIterator.next().getResource().getURI();

										if((trs2BaseMemberUri.endsWith(SELECTIONS_URI_LAST_SEGMENT)) && (!trs2SelectionsUris.contains(trs2BaseMemberUri))) {
											trs2SelectionsUris.add(trs2BaseMemberUri);
										}
									}
								}

								trs2BasePageCount++;

								if(trs2BaseOutputDirectory != null) {

									File trs2BaseFile = new File(trs2BaseOutputDirectory, (BASE_PAGE_FILE_NAME_PREFIX + trs2BasePageCount + XML_FILE_EXTENSION));
									trs2BaseFile.createNewFile();

									if(output) {
										LogUtils.logInfo("Writing TRS2 base to " + trs2BaseFile.getAbsolutePath() + "."); //$NON-NLS-1$ //$NON-NLS-2$
									}

									PrintStream trs2BasePrintStream = new PrintStream(trs2BaseFile);
									trs2BasePrintStream.print(RdfUtils.write(trs2BaseModel));
									trs2BasePrintStream.close();
								}
								else {

									if(output) {
										LogUtils.logInfo("Writing TRS2 base '" + BASE_PAGE_FILE_NAME_PREFIX + trs2BasePageCount + "' to the console."); //$NON-NLS-1$ //$NON-NLS-2$
									}

									LogUtils.logInfo(RdfUtils.write(trs2BaseModel).trim());
								}
							}
							else {
								break;
							}
						}

						//Read/write the TRS2 selections:
						if((readSelections) && (!trs2SelectionsUris.isEmpty())) {

							if(output) {
								LogUtils.logInfo("Reading TRS2 selections."); //$NON-NLS-1$
							}

							File trs2SelectionsParentOutputDirectory = null;

							if(trs2FeedOutputDirectory != null) {

								trs2SelectionsParentOutputDirectory = new File(trs2FeedOutputDirectory, TRS2_SELECTIONS_DIRECTORY_NAME);			
								trs2SelectionsParentOutputDirectory.mkdir();
							}

							for(String selectionsUri : trs2SelectionsUris) {

								//Check if the configuration associated with the TRS2 selections is archived:
								String configurationUri = selectionsUri.substring(0, selectionsUri.lastIndexOf(SELECTIONS_URI_LAST_SEGMENT));

								int returnCode = httpClient.head(configurationUri);

								if (returnCode != HttpStatus.SC_NOT_FOUND){	

									File trs2SelectionsOutputDirectory = null;

									if(trs2SelectionsParentOutputDirectory != null) {

										trs2SelectionsOutputDirectory = new File(trs2SelectionsParentOutputDirectory, MessageFormat.format(TRS2_SELECTIONS_PAGES_DIRECTORY_NAME, new Object[]{UrlUtils.getLastSegment(configurationUri)}));			
										trs2SelectionsOutputDirectory.mkdir();
									}

									int trs2SelectionsPageCount = 0;

									long totalReadSelectionsTime = 0;
									String originalSelectionsUri = new String(selectionsUri);

									while(StringUtils.isSet(selectionsUri)) {

										if(output) {
											LogUtils.logInfo("Reading TRS2 selections '" + selectionsUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$
										}

										long readSelectionsPageStartTime = System.currentTimeMillis();
										
										Model trs2SelectionsModel = RdfUtils.read(httpClient, selectionsUri);

										long readSelectionsPageEndTime = System.currentTimeMillis();
									    
										totalReadSelectionsTime += (readSelectionsPageEndTime - readSelectionsPageStartTime);
										
										if(PerformanceUtils.isTraceEnabled()) {
											PerformanceUtils.logTrace("Read TRS2 selections page '" + selectionsUri + "'", readSelectionsPageStartTime, readSelectionsPageEndTime); //$NON-NLS-1$ //$NON-NLS-2$
										}
										
										trs2SelectionsPageCount++;

										if(trs2SelectionsOutputDirectory != null) {

											File trs2SelectionsFile = new File(trs2SelectionsOutputDirectory, (SELECTIONS_PAGE_FILE_NAME_PREFIX + trs2SelectionsPageCount + XML_FILE_EXTENSION));
											trs2SelectionsFile.createNewFile();

											if(output) {
												LogUtils.logInfo("Writing TRS2 selections to " + trs2SelectionsFile.getAbsolutePath() + "."); //$NON-NLS-1$ //$NON-NLS-2$
											}

											PrintStream trs2SelectionsPrintStream = new PrintStream(trs2SelectionsFile);
											trs2SelectionsPrintStream.print(RdfUtils.write(trs2SelectionsModel));
											trs2SelectionsPrintStream.close();
										}
										else {

											if(output) {
												LogUtils.logInfo("Writing TRS2 selections '" + SELECTIONS_PAGE_FILE_NAME_PREFIX + trs2SelectionsPageCount + "' to the console."); //$NON-NLS-1$ //$NON-NLS-2$
											}

											LogUtils.logInfo(RdfUtils.write(trs2SelectionsModel).trim());
										}

										//Resolve the next TRS2 selections page:
										selectionsUri = resolveSelectionsNextPageUri(httpClient, selectionsUri);
									}
									
									if(PerformanceUtils.isTraceEnabled()) {
										PerformanceUtils.logTrace("Read TRS2 selections '" + originalSelectionsUri + "'", totalReadSelectionsTime); //$NON-NLS-1$ //$NON-NLS-2$
									}
								}
							}
						}
					}
				}

				LogUtils.logInfo("Read " + trs2ProviderTypes.size() + " TRS2 feed" + (trs2ProviderTypes.size() == 1 ? "" : "s") + " for server '" + httpClient.getServerUrl() + "'" + (output ? ":" : ".")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

				if(output){

					for(String trs2ProviderType : trs2ProviderTypes){
						LogUtils.logInfo(trs2ProviderType);
					}
				}
			}
		}
	}

	public void report(List<String> resourceTypes) throws Exception{

		List<QName> supportedResourceProperties = new ArrayList<QName>();
		supportedResourceProperties.add(new QName(NAMESPACE_URI_DC_TERMS, PROPERTY_IDENTIFIER));
		supportedResourceProperties.add(new QName(NAMESPACE_URI_RDF, PROPERTY_TYPE));

		if(commands.contains(CLEANING_COMMAND_READ_ALL_RESOURCES)){

			LogUtils.logInfo("Reporting " + resourceTypes.size() + " resource feed" + (resourceTypes.size() == 1 ? "" : "s") + " in project area" + (serviceProviders.size() == 1 ? "" : "s") + " '" + getProjectAreaNames() + "' for server '" + httpClient.getServerUrl() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$

			File oslcOutputDirectory = null;
			
			if(outputDirectory != null) {

				//Create the output directories:
				//Assumption: One or more valid OSLC service providers exist.
				oslcOutputDirectory = new File(outputDirectory, OSLC_DIRECTORY_NAME);

				if(oslcOutputDirectory.exists()) {
					FileUtils.deleteDirectory(oslcOutputDirectory);
				}

				oslcOutputDirectory.mkdir();
			}
			
			//Iterate the service providers:
			for(ServiceProvider serviceProvider : serviceProviders){			

				httpClient.setConfigurationContextUrl(serviceProvider.getConfigurationContextUrl());

				if((output) && (StringUtils.isSet(serviceProvider.getConfigurationContextUrl()))) {
					LogUtils.logInfo("Note: Using configuration context URL '" + serviceProvider.getConfigurationContextUrl() + "' for resource requests."); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				//Note: Do NOT repeat virtually the same message (see above) when only one project is configured.
				if((output) && (serviceProviders.size() > 1)){
					LogUtils.logInfo("Reporting " + resourceTypes.size() + " resource feed" + (resourceTypes.size() == 1 ? "" : "s") + " in project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				}

				File resourceFeedOutputDirectory = null;
				
				if(oslcOutputDirectory != null) {

					resourceFeedOutputDirectory = new File(oslcOutputDirectory, serviceProvider.getTitle().replace(' ', '_'));
					
					resourceFeedOutputDirectory.mkdir();
				}
				
				for(String resourceType : resourceTypes) {

					String resourceTypeUri = OslcUtils.getResourceTypeUri(resourceType);
					String resourceName = UriUtils.getTypeName(resourceTypeUri);
					String resourceDisplayName = UriUtils.getTypeDisplayName(resourceTypeUri);

					if(output){
						LogUtils.logInfo("Reporting '" + resourceDisplayName + "' resource feed in project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}

					//Resolve the resource feed page URL (query base):
					//Note: There is no query (oslc.where) for pruning the resource feed.
					//Note: Some service providers (e.g. RQM OSLC QM) use the resource type URI appended with 'Query' in oslc:QueryCapability/oslc:resourceType/rdf:resource. 
					String resourceFeedPageUrl = serviceProvider.getQueryCapabilities().get(MessageFormat.format(RESOURCE_TEMPLATE_QUERY, new Object[]{resourceTypeUri}));

					//Note: Some service providers (e.g. DNG OSLC RM) use the resource type URI in oslc:QueryCapability/oslc:resourceType/rdf:resource. 
					if (StringUtils.isNotSet(resourceFeedPageUrl)) {	
						resourceFeedPageUrl = serviceProvider.getQueryCapabilities().get(resourceTypeUri);					
					}

					int resourceFeedPageCount = 0;
					
					//Iterate the resource feed pages: 
					while(StringUtils.isSet(resourceFeedPageUrl)){

						if(output) {
							LogUtils.logInfo("Reading '" + resourceDisplayName + "' resource feed " + resourceFeedPageUrl + "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}

						//Resolve the resource feed page model:
						//Note: Some service providers (e.g. DNG OSLC RM) only return resource URIs in the feed unless the oslc.properties and/or oslc.select parameters are included in the request.  As such, include the oslc.properties=* parameter in the request to include all properties.
						//Note: Some service providers (e.g. DNG OSLC RM) return multiple resource types in a query response unless the oslc.where parameter is included in the request.  As such, include the oslc.where=rdf:type=<resource type URI> and (required) oslc.prefix=rdf=<http://www.w3.org/1999/02/22-rdf-syntax-ns#> parameters in the request to include only the required resource types for performance reasons.
						//Note: Include the oslc.pageSize=100 parameter in the request to limit the number of resources per page for performance reasons.
						Model resourceFeedPageModel = RdfUtils.read(httpClient, resourceFeedPageUrl, (resolveOslcPrefixQueryParameter(supportedResourceProperties) + "&" + resolveOslcPropertiesQueryParameter() + "&" + resolveOslcWhereQueryParameter(resourceTypeUri, httpClient) + "&" + resolveOslcPageSizeQueryParameter())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

						resourceFeedPageCount++;
						
						if(resourceFeedOutputDirectory != null) {

							File oslcFile = new File(resourceFeedOutputDirectory, (resourceName + RESOURCE_PAGE_FILE_NAME_SEGMENT + resourceFeedPageCount + XML_FILE_EXTENSION));
							oslcFile.createNewFile();

							if(output) {
								LogUtils.logInfo("Writing '" + resourceDisplayName + "' resource feed to " + oslcFile.getAbsolutePath() + "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							}

							PrintStream oslcPrintStream = new PrintStream(oslcFile);
							oslcPrintStream.print(RdfUtils.write(resourceFeedPageModel));
							oslcPrintStream.close();
						}
						else {
							
							if(output) {
								LogUtils.logInfo("Writing '" + resourceDisplayName + "' resource feed '" + resourceName + RESOURCE_PAGE_FILE_NAME_SEGMENT + resourceFeedPageCount + "' to the console."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							}
							
							LogUtils.logInfo(RdfUtils.write(resourceFeedPageModel).trim());
						}
						
						//Important: Reset the resource feed page URL.
						resourceFeedPageUrl = null;

						//Resolve the next page of the resource feed URL:
						StmtIterator responseInfoStatementsIterator = resourceFeedPageModel.listStatements(null, RDF.type, resourceFeedPageModel.createProperty(NAMESPACE_URI_OSLC, RESOURCE_RESPONSE_INFO));

						if(responseInfoStatementsIterator.hasNext()){

							Resource responseInfoResource = responseInfoStatementsIterator.next().getSubject();

							StmtIterator nextPageStatementsIterator = resourceFeedPageModel.listStatements(responseInfoResource, resourceFeedPageModel.createProperty(NAMESPACE_URI_OSLC, PROPERTY_NEXT_PAGE), ((RDFNode)(null)));

							if(nextPageStatementsIterator.hasNext()){
								resourceFeedPageUrl = nextPageStatementsIterator.next().getResource().getURI();
							}
						}
					}
				}
			}

			LogUtils.logInfo("Reported " + resourceTypes.size() + " resource feed" + (resourceTypes.size() == 1 ? "" : "s") + " in project area" + (serviceProviders.size() == 1 ? "" : "s") + " '" + getProjectAreaNames() + "' for server '" + httpClient.getServerUrl() + "'" + (output ? ":" : ".")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
			
			if(output){

				for(String resourceType : resourceTypes){
					LogUtils.logInfo(resourceType);
				}
			}
		}
	}

	public void clean(String resourceTypeUri, String targetResourceTypeUri, String propertyPrefixedName, String targetPropertyPrefixedName) throws Exception{

		boolean removeAllForwardLinks = (commands.contains(CLEANING_COMMAND_REMOVE_ALL_FORWARD_LINKS));
		
		int serviceProviderCount = 0;
		int resourceCount = 0;
		int ignoredResourceCount = 0;
		int oldResourceCount = 0;
		int propertyCount = 0;
		int reportBrokenLinksPropertyCount = 0;
		int ignoredReportBrokenLinksPropertyCount = 0;
		int removedBrokenForwardLinks = 0;
		int addedMissingBackLinks = 0;
		int updatedLinkLabels = 0;
		int updatedBackLinkLabels = 0;
		int updatedVersionedLinks = 0;
		int renamedLinks = 0;
		int removeLinkConfigContext = 0;

		String resourceName = UriUtils.getTypeName(resourceTypeUri);
		String resourceDisplayName = UriUtils.getTypeDisplayName(resourceTypeUri);

		String targetResourceName = UriUtils.getTypeName(targetResourceTypeUri);

		String propertyNamespacePrefix = UriUtils.getNamespacePrefix(propertyPrefixedName);
		String propertyName = UriUtils.getName(propertyPrefixedName);
		String propertyNamespaceUri = UriUtils.resolveNamespaceUri(propertyNamespacePrefix);

		List<QName> supportedResourceProperties = new ArrayList<QName>();
		supportedResourceProperties.add(new QName(NAMESPACE_URI_DC_TERMS, PROPERTY_MODIFIED));
		supportedResourceProperties.add(new QName(NAMESPACE_URI_DC_TERMS, PROPERTY_IDENTIFIER));
		supportedResourceProperties.add(new QName(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));
		supportedResourceProperties.add(new QName(NAMESPACE_URI_RDF, PROPERTY_TYPE));
		supportedResourceProperties.add(new QName(NAMESPACE_URI_OSLC_CONFIG, PROPERTY_COMPONENT));
		supportedResourceProperties.add(new QName(propertyNamespaceUri, propertyName));
		
		String targetPropertyNamespacePrefix = UriUtils.getNamespacePrefix(targetPropertyPrefixedName);
		String targetPropertyName = UriUtils.getName(targetPropertyPrefixedName);
		String targetPropertyNamespaceUri = UriUtils.resolveNamespaceUri(targetPropertyNamespacePrefix);

		List<QName> supportedTargetResourceProperties = new ArrayList<QName>();
		supportedTargetResourceProperties.add(new QName(NAMESPACE_URI_DC_TERMS, PROPERTY_MODIFIED));
		supportedTargetResourceProperties.add(new QName(NAMESPACE_URI_DC_TERMS, PROPERTY_IDENTIFIER));
		supportedTargetResourceProperties.add(new QName(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));
		supportedTargetResourceProperties.add(new QName(NAMESPACE_URI_RDF, PROPERTY_TYPE));
		supportedTargetResourceProperties.add(new QName(NAMESPACE_URI_OSLC_CONFIG, PROPERTY_COMPONENT));
		supportedTargetResourceProperties.add(new QName(targetPropertyNamespaceUri, targetPropertyName));

		LogUtils.logInfo("Starting " + (test ? "test " : "") + "processing '" + propertyPrefixedName + "' properties in '" + resourceDisplayName + "' resources in project area" + (serviceProviders.size() == 1 ? "" : "s") + " '" + getProjectAreaNames() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$

		List<String> removedBrokenForwardLinksMessages = new ArrayList<String>();
		List<String> reportBrokenLinksMessages = new ArrayList<String>();
		List<String> addedMissingBackLinksMessages = new ArrayList<String>();
		List<String> updatedLinkLabelsMessages = new ArrayList<String>();
		List<String> updatedBackLinkLabelsMessages = new ArrayList<String>();
		List<String> updatedVersionedLinksMessages = new ArrayList<String>();
		List<String> renamedLinksMessages = new ArrayList<String>();
		List<String> removeLinkConfigContextMessages = new ArrayList<String>();

		List<String> nonReportBrokenLinkCommands = new ArrayList<String>(commands);
		nonReportBrokenLinkCommands.remove(CLEANING_COMMAND_REPORT_BROKEN_LINKS);
		
		//Iterate the service providers:
		for(ServiceProvider serviceProvider : serviceProviders){			

			httpClient.setConfigurationContextUrl(serviceProvider.getConfigurationContextUrl());

			if((output) && (StringUtils.isSet(serviceProvider.getConfigurationContextUrl()))) {
				LogUtils.logInfo("Note: Using configuration context URL '" + serviceProvider.getConfigurationContextUrl() + "' for resource requests."); //$NON-NLS-1$ //$NON-NLS-2$
			}

			serviceProviderCount++;

			if(output){
				LogUtils.logInfo((test ? "Test p" : "P") + "rocessing project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}

			//Resolve the resource feed page URL (query base):
			//Note: There is no query (oslc.where) for pruning the resource feed to include only resources with the property.
			//Note: Some service providers (e.g. RQM OSLC QM) use the resource type URI appended with 'Query' in oslc:QueryCapability/oslc:resourceType/rdf:resource. 
			String resourceFeedPageUrl = serviceProvider.getQueryCapabilities().get(MessageFormat.format(RESOURCE_TEMPLATE_QUERY, new Object[]{resourceTypeUri}));

			//Note: Some service providers (e.g. DNG OSLC RM) use the resource type URI in oslc:QueryCapability/oslc:resourceType/rdf:resource. 
			if (StringUtils.isNotSet(resourceFeedPageUrl)) {	
				resourceFeedPageUrl = serviceProvider.getQueryCapabilities().get(resourceTypeUri);					
			}

			//Iterate the resource feed pages: 
			while(StringUtils.isSet(resourceFeedPageUrl)){

				//Resolve the resource feed page model:
				//Note: Some service providers (e.g. DNG OSLC RM) only return resource URIs in a query response unless the oslc.properties and/or oslc.select parameters are included in the request.  As such, include the oslc.properties=<required properties> and (required) oslc.prefix=<required prefixes> parameters in the request to include only the required properties for performance reasons.
				//Note: Some service providers (e.g. DNG OSLC RM) return multiple resource types in a query response unless the oslc.where parameter is included in the request.  As such, include the oslc.where=rdf:type=<resource type URI> and (required) oslc.prefix=rdf=<http://www.w3.org/1999/02/22-rdf-syntax-ns#> parameters in the request to include only the required resource types for performance reasons.
				//Note: Include the oslc.pageSize=100 parameter in the request to limit the number of resources per page for performance reasons.
				//Note: Required properties MUST be added to the supportedResourceProperties list.
				Model resourceFeedPageModel = RdfUtils.read(httpClient, resourceFeedPageUrl, (resolveOslcPrefixQueryParameter(supportedResourceProperties) + "&" + resolveOslcPropertiesQueryParameter(supportedResourceProperties) + "&" + resolveOslcWhereQueryParameter(resourceTypeUri, httpClient) + "&" + resolveOslcPageSizeQueryParameter())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				
				StmtIterator resourceFeedPageStatementsIterator = resourceFeedPageModel.listStatements(null, RDF.type, resourceFeedPageModel.createResource(resourceTypeUri));

				//Iterate the resources in the resource feed page:
				while(resourceFeedPageStatementsIterator.hasNext()){

					resourceCount++;

					//Resolve the resource:
					Resource resource = resourceFeedPageStatementsIterator.next().getSubject();
					
					Model resourceModel = resource.getModel();
					
					String resourceUri = resource.getURI();

					String configurationContextUrl = serviceProvider.getConfigurationContextUrl();
					
					if(ignoreResource(resourceUri)) {
						
						ignoredResourceCount++;
						
						if(output){
							LogUtils.logInfo((test ? "Test s" : "S") + "kipped processing '" + resourceDisplayName + "' resource '" + resourceUri + "' since ignored or not processed."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						}
					} else if(processResource(resource)){

						try {

							if(output){
								LogUtils.logInfo((test ? "Test p" : "P") + "rocessing '" + resourceDisplayName + "' resource '" + resourceUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
							}

							if(commands.contains(CLEANING_COMMAND_REPORT_BROKEN_LINKS)){

								long startTime = System.currentTimeMillis();
								
								List<String> propertyUris = new ArrayList<String>();

								//When using a configuration context (global configuration) and the link type is not owned by the resource type, 
								//resolve the links from LDX using non-standard APIs since the resource model will not contain link types that
								//the resource type does not own:
								if((StringUtils.isSet(configurationContextUrl)) && (!OslcUtils.isLinkOwned(resourceTypeUri, propertyPrefixedName))) {
									if(LogUtils.isTraceEnabled()) {
										LogUtils.logTrace("Using global configuration context '" + configurationContextUrl + "' and the link type '" + propertyPrefixedName + "' is not owned by the resource type '" + resourceTypeUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
									}
									List<String> linksFromLdx = resolveLinksFromLdx(propertyPrefixedName, resource, httpClient);

									if((linksFromLdx != null) && (linksFromLdx.size() > 0)) {
										propertyUris.addAll(linksFromLdx);
									}
								}
								else {
									if(LogUtils.isTraceEnabled()) {
										LogUtils.logTrace("Not using global configuration context '" + configurationContextUrl + "' or the link type '" + propertyPrefixedName + "' is owned by the resource type '" + resourceTypeUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
									}
									//Iterate the properties in the resource:
									StmtIterator propertyStatementsIterator = resourceModel.listStatements(resource, resourceModel.createProperty(propertyNamespaceUri, propertyName), ((RDFNode)(null)));

									while(propertyStatementsIterator.hasNext()){

										//Resolve the property:
										Statement propertyStatement = propertyStatementsIterator.next();
										Resource propertyResource = propertyStatement.getResource();

										String propertyUri = propertyResource.getURI();

										if(!propertyUris.contains(propertyUri)) {
											propertyUris.add(propertyUri);
										}
									}
								}

								for(String propertyUri : propertyUris) {

									reportBrokenLinksPropertyCount++;
									
									if(ignoreTargetResource(propertyUri)) {

										ignoredReportBrokenLinksPropertyCount++;
										
										if(output){
											LogUtils.logInfo("Skipped reporting '" + propertyPrefixedName + "' property '" + propertyUri + "' since ignored or not processed."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
										}
									}
									else {

										if(output){
											LogUtils.logInfo("Reporting '" + propertyPrefixedName + "' property '" + propertyUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
										}

										URL propertyUrl = new URL(propertyUri);

										String propertyUrlPath = propertyUrl.getPath();

										//Resolve the server URL (https://<server>:<port>/<context root>/):
										URL targetServerUrl = new URL(propertyUrl.getProtocol(), propertyUrl.getHost(), propertyUrl.getPort(), propertyUrlPath.substring(0, (propertyUrlPath.indexOf(FORWARD_SLASH, 1) + 1)));

										OslcHttpClient targetHttpClient = OslcHttpClientFactory.getInstance().getClient(targetServerUrl);    
										targetHttpClient.setConfigurationContextUrl(configurationContextUrl);

										if((output) && (StringUtils.isSet(configurationContextUrl))) {
											LogUtils.logInfo("Note: Using configuration context URL '" + configurationContextUrl + "' for target resource requests."); //$NON-NLS-1$ //$NON-NLS-2$
										}

										try {

											//Resolve the target resource model:
											//Note: Required properties MUST be added to the supportedTargetResourceProperties list.
											Model targetResourceModel = RdfUtils.read(targetHttpClient, propertyUri, (resolveOslcPrefixQueryParameter(supportedTargetResourceProperties) + "&" + resolveOslcPropertiesQueryParameter(supportedTargetResourceProperties))); //$NON-NLS-1$

											Resource targetResource = RdfUtils.getRootResource(targetResourceModel, targetResourceTypeUri);

											if(targetResource == null) {
												
												if(output){
													LogUtils.logInfo("Skipped reporting broken link '" + propertyPrefixedName + "' property from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'" + (StringUtils.isSet(configurationContextUrl) ? " and configuration context '" + configurationContextUrl + "'" : "") + " due to missing link '" + targetPropertyPrefixedName + "' property in target '" + targetResourceName + "' resource '" + propertyUri + "' since cannot resolve the root RDF resource."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
												}
											}
											else if(processResource(targetResource)){
											
												String reportBrokenLinksMessage = "Found broken link '" + propertyPrefixedName + "' property from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'" + (StringUtils.isSet(configurationContextUrl) ? " and configuration context '" + configurationContextUrl + "'" : "") + " due to missing link '" + targetPropertyPrefixedName + "' property in target '" + targetResourceName + "' resource '" + propertyUri + "'."; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$

												//When using a configuration context (global configuration) and the link type is not owned by the resource type, 
												//resolve the links from LDX using non-standard APIs since the resource model will not contain link types that
												//the resource type does not own:
												if((StringUtils.isSet(configurationContextUrl)) && (!OslcUtils.isLinkOwned(targetResourceTypeUri, targetPropertyPrefixedName))) {
													if(LogUtils.isTraceEnabled()) {
														LogUtils.logTrace("Using global configuration context '" + configurationContextUrl + "' and the target link type '" + targetPropertyPrefixedName + "' is not owned by the target resource type '" + targetResourceTypeUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
													}
													List<String> backLinksFromLdx = resolveLinksFromLdx(targetPropertyPrefixedName, targetResource, targetHttpClient);

													if((backLinksFromLdx == null) || (backLinksFromLdx.size() == 0) || (!backLinksFromLdx.contains(resourceUri))) {

														reportBrokenLinksMessages.add("        " + reportBrokenLinksMessage); //$NON-NLS-1$

														if(output) {
															LogUtils.logInfo(reportBrokenLinksMessage);
														}
													}
												}
												else {
													if(LogUtils.isTraceEnabled()) {
														LogUtils.logTrace("Not using global configuration context '" + configurationContextUrl + "' or the target link type '" + targetPropertyPrefixedName + "' is owned by the target resource type '" + targetResourceTypeUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
													}
													Statement targetPropertyStatement = targetResourceModel.createStatement(targetResource, targetResourceModel.createProperty(targetPropertyNamespaceUri, targetPropertyName), resource);

													if(!targetResourceModel.contains(targetPropertyStatement)){

														reportBrokenLinksMessages.add("        " + reportBrokenLinksMessage); //$NON-NLS-1$

														if(output) {
															LogUtils.logInfo(reportBrokenLinksMessage);
														}
													}	
												}
											}
											else if(output){
												LogUtils.logInfo("Skipped reporting broken link '" + propertyPrefixedName + "' property from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'" + (StringUtils.isSet(configurationContextUrl) ? " and configuration context '" + configurationContextUrl + "'" : "") + " due to missing link '" + targetPropertyPrefixedName + "' property in target '" + targetResourceName + "' resource '" + propertyUri + "' since the last modified date is before '" + DateTimeUtils.formatIso8601DateTime(lastModifiedDateTime) + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$
											}
										}
										catch(HttpClientException h) {	

											String reportBrokenLinksMessage = "Found broken link '" + propertyPrefixedName + "' property from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'" + (StringUtils.isSet(configurationContextUrl) ? " and configuration context '" + configurationContextUrl + "'" : "") + " due to missing (return code: " + h.getReturnCode() + ") target '" + targetResourceName + "' resource '" + propertyUri + "'."; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$

											reportBrokenLinksMessages.add("        " + reportBrokenLinksMessage); //$NON-NLS-1$

											if(output) {
												LogUtils.logInfo(reportBrokenLinksMessage);
											}
										}
									}
								}		
								
								long endTime = System.currentTimeMillis();
						    
								if(PerformanceUtils.isTraceEnabled()) {
									PerformanceUtils.logTrace("Report broken links in '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'" + (StringUtils.isSet(configurationContextUrl) ? " and configuration context '" + configurationContextUrl + "'" : ""), startTime, endTime); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$=
								}
							}
							
							if(!nonReportBrokenLinkCommands.isEmpty()) {
								
								//Copy the resource by re-reading the resource:
								Model resourceCopyModel = RdfUtils.read(httpClient, resourceUri);
								
								Resource resourceCopy = RdfUtils.getRootResource(resourceCopyModel, resourceTypeUri);

								//Cache the list of removed broken forward link statements:
								List<Statement> removedBrokenForwardLinkStatements = new ArrayList<Statement>();

								//Cache the list of updated link label links:
								List<Link> updatedLinkLabelLinks = new ArrayList<Link>();

								//Cache the list of updated OSLC versioned forward link URLs:
								List<String> updatedVersionedLinkUrls = new ArrayList<String>();

								//Cache the list of renamed link URIs:
								List<String> renamedLinkUris = new ArrayList<String>();
								
								//Cache the list of links with unwanted oslc config query parameter URIs:
								List<String> removeLinkConfigContextUris = new ArrayList<String>();

								//Iterate the properties in the resource:
								StmtIterator propertyStatementsIterator = resourceCopyModel.listStatements(resourceCopy, resourceCopyModel.createProperty(propertyNamespaceUri, propertyName), ((RDFNode)(null)));

								while(propertyStatementsIterator.hasNext()){

									propertyCount++;

									//Resolve the property:
									Statement propertyStatement = propertyStatementsIterator.next();
									Resource propertyResource = propertyStatement.getResource();

									String propertyUri = propertyResource.getURI();

									if(ignoreTargetResource(propertyUri)) {

										if(output){
											LogUtils.logInfo((test ? "Test s" : "S") + "kipped processing '" + propertyPrefixedName + "' property '" + propertyUri + "' since ignored or not processed."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
										}
									}
									else {

										if(output){
											LogUtils.logInfo((test ? "Test p" : "P") + "rocessing '" + propertyPrefixedName + "' property '" + propertyUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
										}

										URL propertyUrl = new URL(propertyUri);

										String propertyUrlPath = propertyUrl.getPath();

										//Resolve the server URL (https://<server>:<port>/<context root>/):
										URL targetServerUrl = new URL(propertyUrl.getProtocol(), propertyUrl.getHost(), propertyUrl.getPort(), propertyUrlPath.substring(0, (propertyUrlPath.indexOf(FORWARD_SLASH, 1) + 1)));

										if(commands.contains(CLEANING_COMMAND_REMOVE_ALL_FORWARD_LINKS)){
											removedBrokenForwardLinkStatements.add(propertyStatement);
										}
										
										if(!removedBrokenForwardLinkStatements.contains(propertyStatement)){
									
											if(commands.contains(CLEANING_COMMAND_REMOVE_BROKEN_FORWARD_LINKS)){

												OslcHttpClient targetHttpClient = OslcHttpClientFactory.getInstance().getClient(targetServerUrl);    
												targetHttpClient.setConfigurationContextUrl(serviceProvider.getConfigurationContextUrl());

												if((output) && (StringUtils.isSet(serviceProvider.getConfigurationContextUrl()))) {
													LogUtils.logInfo("Note: Using configuration context URL '" + serviceProvider.getConfigurationContextUrl() + "' for target resource requests."); //$NON-NLS-1$ //$NON-NLS-2$
												}

												int returnCode = targetHttpClient.head(propertyUri);

												if ((returnCode == HttpStatus.SC_NOT_FOUND) || (returnCode == HttpStatus.SC_GONE)){	

													//Test if the property server is still accessible:
													returnCode = targetHttpClient.ping();

													if ((returnCode == HttpStatus.SC_OK) || (returnCode == HttpStatus.SC_MOVED_TEMPORARILY) || (returnCode == HttpStatus.SC_SEE_OTHER)) {
														removedBrokenForwardLinkStatements.add(propertyStatement);
													}
													else {
														LogUtils.logError("Skipping property since pinging server '" + targetServerUrl.toString() + "' failed (response code: " + returnCode + ") when processing '" + propertyPrefixedName + "' property '" + propertyUri + "' in '" + resourceDisplayName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
													}
												}
												else if (returnCode == HttpStatus.SC_OK){	

													//Resolve the target resource model:
													//Note: Deleted DOORS requirements are soft-deleted (<rm:softDeleted>true</rm:softDeleted>).  As such, OSLC RM V2 API HEAD/GET requests for soft-deleted requirements return 200 (OK).
													Model targetResourceModel = RdfUtils.read(targetHttpClient, propertyUri);

													Resource targetResource = RdfUtils.getRootResource(targetResourceModel, targetResourceTypeUri);

													Statement softDeletedPropertyStatement = targetResourceModel.getProperty(targetResource, targetResourceModel.createProperty(NAMESPACE_URI_JAZZ_RM, PROPERTY_SOFT_DELETED));

													//Note: The rm:softDeleted property is optional.
													if(softDeletedPropertyStatement != null){

														if(Boolean.parseBoolean(softDeletedPropertyStatement.getString())){
															removedBrokenForwardLinkStatements.add(propertyStatement);
														}
													}
												}
												else if(returnCode == HttpStatus.SC_METHOD_NOT_ALLOWED){
													LogUtils.logError("Skipping property since the HTTP HEAD method is not supported by server '" + targetServerUrl.toString() + "' when processing '" + propertyPrefixedName + "' property '" + propertyUri + "' in '" + resourceDisplayName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
												}
											}
										}

										if(!removedBrokenForwardLinkStatements.contains(propertyStatement)){

											if(commands.contains(CLEANING_COMMAND_REMOVE_BROKEN_FORWARD_LINKS_WITH_MISSING_BACK_LINKS)){

												OslcHttpClient targetHttpClient = OslcHttpClientFactory.getInstance().getClient(targetServerUrl);    
												targetHttpClient.setConfigurationContextUrl(serviceProvider.getConfigurationContextUrl());

												if((output) && (StringUtils.isSet(serviceProvider.getConfigurationContextUrl()))) {
													LogUtils.logInfo("Note: Using configuration context URL '" + serviceProvider.getConfigurationContextUrl() + "' for target resource requests."); //$NON-NLS-1$ //$NON-NLS-2$
												}

												int returnCode = targetHttpClient.head(propertyUri);

												if (returnCode == HttpStatus.SC_OK){	

													//Resolve the target resource model:
													Model targetResourceModel = RdfUtils.read(targetHttpClient, propertyUri);

													Resource targetResource = RdfUtils.getRootResource(targetResourceModel, targetResourceTypeUri);

													if(targetResource == null) {
														
														if(output){
															removedBrokenForwardLinksMessages.add("        " + (test ? "Test s" : "S") + "kipped removing" + (removeAllForwardLinks ? " " : " broken ") + "forward link '" + propertyPrefixedName + "' property from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "' due to missing back link '" + targetPropertyPrefixedName + "' property in target '" + targetResourceName + "' resource '" + propertyUri + "' since cannot resolve the root RDF resource."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$
														}
													}
													else if(processResource(targetResource)){

														//When using a configuration context (global configuration) and the link type is not owned by the resource type, 
														//resolve the links from LDX using non-standard APIs since the resource model will not contain link types that
														//the resource type does not own:
														if(StringUtils.isSet(configurationContextUrl) && (!OslcUtils.isLinkOwned(targetResourceTypeUri, targetPropertyPrefixedName))) {

															List<String> backLinksFromLdx = resolveLinksFromLdx(targetPropertyPrefixedName, targetResource, targetHttpClient);

															if((backLinksFromLdx == null) || (backLinksFromLdx.size() == 0) || (!backLinksFromLdx.contains(resourceUri))) {
																removedBrokenForwardLinkStatements.add(propertyStatement);
															}
														}
														else {

															Statement targetPropertyStatement = targetResourceModel.createStatement(targetResource, targetResourceModel.createProperty(targetPropertyNamespaceUri, targetPropertyName), resource);

															if(!targetResourceModel.contains(targetPropertyStatement)){
																removedBrokenForwardLinkStatements.add(propertyStatement);
															}	
														}
													}
													else{
														removedBrokenForwardLinksMessages.add((output ? "        " : "") + (test ? "Test s" : "S") + "kipped removing" + (removeAllForwardLinks ? " " : " broken ") + "forward link '" + propertyPrefixedName + "' property from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "' due to missing back link '" + targetPropertyPrefixedName + "' property in target '" + targetResourceName + "' resource '" + propertyUri + "' since the last modified date is before '" + DateTimeUtils.formatIso8601DateTime(lastModifiedDateTime) + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$
													}
												}
												else if(returnCode == HttpStatus.SC_METHOD_NOT_ALLOWED){
													LogUtils.logError("Skipping property since the HTTP HEAD method is not supported by server '" + targetServerUrl.toString() + "' when processing '" + propertyPrefixedName + "' property '" + propertyUri + "' in '" + resourceDisplayName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
												}
											}
										}

										if(!removedBrokenForwardLinkStatements.contains(propertyStatement)){

											if(commands.contains(CLEANING_COMMAND_ADD_MISSING_BACK_LINKS)){

												OslcHttpClient targetHttpClient = OslcHttpClientFactory.getInstance().getClient(targetServerUrl);    
												targetHttpClient.setConfigurationContextUrl(serviceProvider.getConfigurationContextUrl());

												if((output) && (StringUtils.isSet(serviceProvider.getConfigurationContextUrl()))) {
													LogUtils.logInfo("Note: Using configuration context URL '" + serviceProvider.getConfigurationContextUrl() + "' for target resource requests."); //$NON-NLS-1$ //$NON-NLS-2$
												}

												int returnCode = targetHttpClient.head(propertyUri);

												if (returnCode == HttpStatus.SC_OK){	

													//Resolve the target resource model:
													Model targetResourceModel = RdfUtils.read(targetHttpClient, propertyUri);
													
													Resource targetResource = RdfUtils.getRootResource(targetResourceModel, targetResourceTypeUri);

													if(targetResource == null) {
														
														if(output){
															addedMissingBackLinksMessages.add("        " + (test ? "Test s" : "S") + "kipped adding missing back link '" + targetPropertyPrefixedName + "' property '" + resourceUri + "' to resource '" + propertyUri + "' since cannot resolve the root RDF resource."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
														}
													}
													else if(processResource(targetResource)){

														Statement targetPropertyStatement = targetResourceModel.createStatement(targetResource, targetResourceModel.createProperty(targetPropertyNamespaceUri, targetPropertyName), resource);

														if(!targetResourceModel.contains(targetPropertyStatement)){

															//Resolve the target service provider:
															ServiceProvider targetServiceProvider = null;

															StmtIterator targetServiceProviderStatementsIterator = targetResourceModel.listStatements(targetResource, targetResourceModel.createProperty(NAMESPACE_URI_OSLC, PROPERTY_SERVICE_PROVIDER), ((RDFNode)(null)));

															if(targetServiceProviderStatementsIterator.hasNext()){

																String targetServiceProviderUrl = targetServiceProviderStatementsIterator.next().getResource().getURI();

																targetServiceProvider = targetHttpClient.getServiceProviderByUrl(targetServiceProviderUrl);
															}

															//Back-up the target resource:
															LogUtils.logDebug("Before adding missing back link '" + targetPropertyPrefixedName + "' property referencing '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "' to '" + targetResourceName + "' resource '" + propertyUri + "'" + (targetServiceProvider != null ? " in project area '" + targetServiceProvider.getTitle() + "'" : "") + ":" + LINE_SEPARATOR + RdfUtils.write(targetResourceModel, null, false));  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
															
															//Add the missing back link:
															targetResourceModel.add(targetPropertyStatement);

															//Add the reified statement for the missing back link label:
															String missingBackLinkLabel = resolveBackLinkLabel(resourceTypeUri, resourceCopy);

															if((missingBackLinkLabel == null) || (missingBackLinkLabel.trim().isEmpty())){
																missingBackLinkLabel = resourceUri;
															}

															ReifiedStatement reifiedStatement = targetPropertyStatement.createReifiedStatement();													
															reifiedStatement.addProperty(targetResourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE), missingBackLinkLabel.toString());

															//Back-up the target resource:
															LogUtils.logDebug("After adding missing back link '" + targetPropertyPrefixedName + "' property referencing '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "' to '" + targetResourceName + "' resource '" + propertyUri + "'" + (targetServiceProvider != null ? " in project area '" + targetServiceProvider.getTitle() + "'" : "") + ":" + LINE_SEPARATOR + RdfUtils.write(targetResourceModel, null, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
															
															if(!test){

																//Update the target resource:
																updateResource(targetHttpClient, propertyUri, targetResourceModel); //$NON-NLS-1$							
															}

															//Capture the output message:
															addedMissingBackLinksMessages.add((output ? "        " : "") + (test ? "Test a" : "A") + "dded missing back link '" + targetPropertyPrefixedName + "' property '" + resourceUri + "' to resource '" + propertyUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

															addedMissingBackLinks++;
														}	
													}
													else{
														addedMissingBackLinksMessages.add((output ? "        " : "") + (test ? "Test s" : "S") + "kipped adding missing back link '" + targetPropertyPrefixedName + "' property '" + resourceUri + "' to resource '" + propertyUri + "' since the last modified date is before '" + DateTimeUtils.formatIso8601DateTime(lastModifiedDateTime) + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
													}
												}
												else if(returnCode == HttpStatus.SC_METHOD_NOT_ALLOWED){
													LogUtils.logError("Skipping property since the HTTP HEAD method is not supported by server '" + targetServerUrl.toString() + "' when processing '" + propertyPrefixedName + "' property '" + propertyUri + "' in '" + resourceDisplayName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
												}
											}
											
											if(commands.contains(CLEANING_COMMAND_UPDATE_LINK_LABELS)){

												//Resolve the actual forward link label:
												String actualForwardLinkLabel = null;

												Resource propertyReifiedStatement = resourceCopyModel.getAnyReifiedStatement(propertyStatement);

												if (propertyReifiedStatement != null) {

													Statement propertyTitleProperty = propertyReifiedStatement.getProperty(resourceCopyModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

													if (propertyTitleProperty != null) {
														actualForwardLinkLabel = propertyTitleProperty.getString();
													}
												}

												if ((actualForwardLinkLabel != null) && (actualForwardLinkLabel.trim().equalsIgnoreCase(propertyUri.trim()))){

													//Resolve the expected forward link label:
													OslcHttpClient targetHttpClient = OslcHttpClientFactory.getInstance().getClient(targetServerUrl);    
													targetHttpClient.setConfigurationContextUrl(serviceProvider.getConfigurationContextUrl());

													if((output) && (StringUtils.isSet(serviceProvider.getConfigurationContextUrl()))) {
														LogUtils.logInfo("Note: Using configuration context URL '" + serviceProvider.getConfigurationContextUrl() + "' for target resource requests."); //$NON-NLS-1$ //$NON-NLS-2$
													}

													int returnCode = targetHttpClient.head(propertyUri);

													if (returnCode == HttpStatus.SC_OK){	

														//Resolve the target resource model:
														Model targetResourceModel = RdfUtils.read(targetHttpClient, propertyUri);

														Resource targetResource = RdfUtils.getRootResource(targetResourceModel, targetResourceTypeUri);

														if(targetResource == null) {
															
															if(output){
																updatedLinkLabelsMessages.add("        " + (test ? "Test s" : "S") + "kipped updating link label link '" + targetPropertyPrefixedName + "' property '" + resourceUri + "' to resource '" + propertyUri + "' since cannot resolve the root RDF resource."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
															}
														}
														else if(processResource(targetResource)){

															String expectedForwardLinkLabel = resolveForwardLinkLabel(propertyUri, targetResource, targetHttpClient);

															if((expectedForwardLinkLabel != null) && (!expectedForwardLinkLabel.trim().isEmpty())){
																updatedLinkLabelLinks.add(new Link(propertyUri, expectedForwardLinkLabel));
															}
														}
														else{
															updatedLinkLabelsMessages.add((output ? "        " : "") + (test ? "Test s" : "S") + "kipped updating link label link '" + targetPropertyPrefixedName + "' property '" + resourceUri + "' to resource '" + propertyUri + "' since the last modified date is before '" + DateTimeUtils.formatIso8601DateTime(lastModifiedDateTime) + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
														}
													}
													else if(returnCode == HttpStatus.SC_METHOD_NOT_ALLOWED){
														LogUtils.logError("Skipping property since the HTTP HEAD method is not supported by server '" + targetServerUrl.toString() + "' when processing '" + propertyPrefixedName + "' property '" + propertyUri + "' in '" + resourceDisplayName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
													}
												}
											}

											if(commands.contains(CLEANING_COMMAND_UPDATE_FORWARD_LINK_LABELS)){

												//Resolve the actual forward link label:
												String actualForwardLinkLabel = null;

												Resource propertyReifiedStatement = resourceCopyModel.getAnyReifiedStatement(propertyStatement);

												if (propertyReifiedStatement != null) {

													Statement propertyTitleProperty = propertyReifiedStatement.getProperty(resourceCopyModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

													if (propertyTitleProperty != null) {
														actualForwardLinkLabel = propertyTitleProperty.getString();
													}
												}

												//Resolve the expected forward link label:
												OslcHttpClient targetHttpClient = OslcHttpClientFactory.getInstance().getClient(targetServerUrl);    
												targetHttpClient.setConfigurationContextUrl(serviceProvider.getConfigurationContextUrl());

												if((output) && (StringUtils.isSet(serviceProvider.getConfigurationContextUrl()))) {
													LogUtils.logInfo("Note: Using configuration context URL '" + serviceProvider.getConfigurationContextUrl() + "' for target resource requests."); //$NON-NLS-1$ //$NON-NLS-2$
												}

												int returnCode = targetHttpClient.head(propertyUri);

												if (returnCode == HttpStatus.SC_OK){	

													//Resolve the target resource model:
													Model targetResourceModel = RdfUtils.read(targetHttpClient, propertyUri);

													Resource targetResource = RdfUtils.getRootResource(targetResourceModel, targetResourceTypeUri);

													if(targetResource == null) {
														
														if(output){
															updatedLinkLabelsMessages.add("        " + (test ? "Test s" : "S") + "kipped updating forward link label link '" + targetPropertyPrefixedName + "' property '" + resourceUri + "' to resource '" + propertyUri + "' since cannot resolve the root RDF resource."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
														}
													}
													else if(processResource(targetResource)){

														String expectedForwardLinkLabel = resolveForwardLinkLabel(propertyUri, targetResource, targetHttpClient);

														if((expectedForwardLinkLabel != null) && (!expectedForwardLinkLabel.trim().isEmpty()) && (!expectedForwardLinkLabel.trim().equalsIgnoreCase(actualForwardLinkLabel))){
															updatedLinkLabelLinks.add(new Link(propertyUri, expectedForwardLinkLabel));
														}
													}
													else{
														updatedLinkLabelsMessages.add((output ? "        " : "") + (test ? "Test s" : "S") + "kipped updating forward link label link '" + targetPropertyPrefixedName + "' property '" + resourceUri + "' to resource '" + propertyUri + "' since the last modified date is before '" + DateTimeUtils.formatIso8601DateTime(lastModifiedDateTime) + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
													}
												}
												else if(returnCode == HttpStatus.SC_METHOD_NOT_ALLOWED){
													LogUtils.logError("Skipping property since the HTTP HEAD method is not supported by server '" + targetServerUrl.toString() + "' when processing '" + propertyPrefixedName + "' property '" + propertyUri + "' in '" + resourceDisplayName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
												}
											}

											if(commands.contains(CLEANING_COMMAND_UPDATE_BACK_LINK_LABELS)){

												OslcHttpClient targetHttpClient = OslcHttpClientFactory.getInstance().getClient(targetServerUrl);    
												targetHttpClient.setConfigurationContextUrl(serviceProvider.getConfigurationContextUrl());

												if((output) && (StringUtils.isSet(serviceProvider.getConfigurationContextUrl()))) {
													LogUtils.logInfo("Note: Using configuration context URL '" + serviceProvider.getConfigurationContextUrl() + "' for target resource requests."); //$NON-NLS-1$ //$NON-NLS-2$
												}

												int returnCode = targetHttpClient.head(propertyUri);

												if (returnCode == HttpStatus.SC_OK){	

													//Resolve the target resource model:
													Model targetResourceModel = RdfUtils.read(targetHttpClient, propertyUri);

													Resource targetResource = RdfUtils.getRootResource(targetResourceModel, targetResourceTypeUri);

													if(targetResource == null) {
														
														if(output){
															updatedBackLinkLabelsMessages.add("        " + (test ? "Test s" : "S") + "kipped updating back link label for '" + targetPropertyPrefixedName + "' property '" + resourceUri + "' to resource '" + propertyUri + "' since cannot resolve the root RDF resource."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
														}
													}
													else if(processResource(targetResource)){

														Statement targetPropertyStatement = targetResourceModel.createStatement(targetResource, targetResourceModel.createProperty(targetPropertyNamespaceUri, targetPropertyName), resource);

														if(targetResourceModel.contains(targetPropertyStatement)){

															//Resolve the actual back link label:
															String actualBackLinkLabel = null;

															Resource targetPropertyReifiedResource = targetResourceModel.getAnyReifiedStatement(targetPropertyStatement);

															if (targetPropertyReifiedResource != null) {

																Statement targetPropertyTitleStatement = targetPropertyReifiedResource.getProperty(targetResourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

																if (targetPropertyTitleStatement != null) {
																	actualBackLinkLabel = targetPropertyTitleStatement.getString();
																}
															}

															//Resolve the expected back link label:
															String expectedBackLinkLabel = resolveBackLinkLabel(resourceTypeUri, resourceCopy);

															//Problem:  The IBM Rational DOORS Web Access (DWA) OSLC Requirements Management (RM) V2 API does not expose the back link label in the requirement RDF/XML.   As such, there is no way to determine the current back link label.
															//Solution: Always update the back link label.
															//Concerns: 1. The OSLC Cleaner Utility will take longer to run since it is updating all back link labels.
															//          2. Correct back link labels will be updated resulting in modified requirements (included in requirement reconciliation) and history events with no changes. 
															//If the actual back link label is different than the expected back link label (including unresolved), updated the back link label:
															if((expectedBackLinkLabel != null) && (!expectedBackLinkLabel.trim().isEmpty()) && (!expectedBackLinkLabel.equalsIgnoreCase(actualBackLinkLabel))){

																//Resolve the target service provider:
																ServiceProvider targetServiceProvider = null;

																StmtIterator targetServiceProviderStatementsIterator = targetResourceModel.listStatements(targetResource, targetResourceModel.createProperty(NAMESPACE_URI_OSLC, PROPERTY_SERVICE_PROVIDER), ((RDFNode)(null)));

																if(targetServiceProviderStatementsIterator.hasNext()){

																	String targetServiceProviderUrl = targetServiceProviderStatementsIterator.next().getResource().getURI();

																	targetServiceProvider = targetHttpClient.getServiceProviderByUrl(targetServiceProviderUrl);
																}

																//Back-up the target resource:
																LogUtils.logDebug("Before removing updated back link label for '" + targetPropertyPrefixedName + "' property referencing '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "' to '" + targetResourceName + "' resource '" + propertyUri + "'" + (targetServiceProvider != null ? " in project area '" + targetServiceProvider.getTitle() + "'" : "") + ":" + LINE_SEPARATOR + RdfUtils.write(targetResourceModel, null, false));  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
																
																//Remove the updated back link label link statement:
																targetResourceModel.remove(targetPropertyStatement);

																//Note: Iterate the list of reified statements (instead of using the iterator) to avoid the ConcurrentModificationException after removing the reification.
																for(ReifiedStatement reifiedStatement : targetResourceModel.listReifiedStatements(targetPropertyStatement).toList()){

																	//Remove the reified statement for the updated back link label:
																	reifiedStatement.removeAll(targetResourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

																	targetResourceModel.removeReification(reifiedStatement);
																}

																//Back-up the target resource:
																LogUtils.logDebug("After removing updated back link label for '" + targetPropertyPrefixedName + "' property referencing '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "' to '" + targetResourceName + "' resource '" + propertyUri + "'" + (targetServiceProvider != null ? " in project area '" + targetServiceProvider.getTitle() + "'" : "") + ":" + LINE_SEPARATOR + RdfUtils.write(targetResourceModel, null, false));  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
																
																if(!test){

																	//Update the target resource:
																	updateResource(targetHttpClient, propertyUri, targetResourceModel);
																}

																//Resolve the updated target resource model:
																targetResourceModel = RdfUtils.read(targetHttpClient, propertyUri);

																targetResource = RdfUtils.getRootResource(targetResourceModel, targetResourceTypeUri);

																//Back-up the target resource:
																LogUtils.logDebug("Before re-adding updated back link label for '" + targetPropertyPrefixedName + "' property referencing '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "' to '" + targetResourceName + "' resource '" + propertyUri + "'" + (targetServiceProvider != null ? " in project area '" + targetServiceProvider.getTitle() + "'" : "") + ":" + LINE_SEPARATOR + RdfUtils.write(targetResourceModel, null, false));  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
																
																targetPropertyStatement = targetResourceModel.createStatement(targetResource, targetResourceModel.createProperty(targetPropertyNamespaceUri, targetPropertyName), resource);

																//Add the updated back link label link statement:
																targetResourceModel.add(targetPropertyStatement);

																//Add the reified statement for the updated back link label:
																ReifiedStatement reifiedStatement = targetPropertyStatement.createReifiedStatement();								

																reifiedStatement.addProperty(targetResourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE), expectedBackLinkLabel);	

																//Back-up the target resource:
																LogUtils.logDebug("After re-adding updated back link label for '" + targetPropertyPrefixedName + "' property referencing '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "' to '" + targetResourceName + "' resource '" + propertyUri + "'" + (targetServiceProvider != null ? " in project area '" + targetServiceProvider.getTitle() + "'" : "") + ":" + LINE_SEPARATOR + RdfUtils.write(targetResourceModel, null, false));  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
																
																if(!test){

																	//Update the target resource:
																	updateResource(targetHttpClient, propertyUri, targetResourceModel);
																}

																//Capture the output message:
																updatedBackLinkLabelsMessages.add((output ? "        " : "") + (test ? "Test u" : "U") + "pdating back link label for '" + targetPropertyPrefixedName + "' property '" + resourceUri + "' to resource '" + propertyUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

																updatedBackLinkLabels++;													}											
														}
														else{
															updatedBackLinkLabelsMessages.add((output ? "        " : "") + (test ? "Test s" : "S") + "kipped updating back link label for '" + targetPropertyPrefixedName + "' property '" + resourceUri + "' to resource '" + propertyUri + "' since existing back link label could not be resolved."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$												
														}
													}
													else{
														updatedBackLinkLabelsMessages.add((output ? "        " : "") + (test ? "Test s" : "S") + "kipped updating back link label for '" + targetPropertyPrefixedName + "' property '" + resourceUri + "' to resource '" + propertyUri + "' since the last modified date is before '" + DateTimeUtils.formatIso8601DateTime(lastModifiedDateTime) + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
													}
												}
												else if(returnCode == HttpStatus.SC_METHOD_NOT_ALLOWED){
													LogUtils.logError("Skipping property since the HTTP HEAD method is not supported by server '" + targetServerUrl.toString() + "' when processing '" + propertyPrefixedName + "' property '" + propertyUri + "' in '" + resourceDisplayName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
												}
											}

											if(commands.contains(CLEANING_COMMAND_UPDATE_VERSIONED_LINKS)){
												
												if(UrlUtils.isVersionedUrl(propertyUri.trim())){
													updatedVersionedLinkUrls.add(propertyUri);
												}												
											}

											if(commands.contains(CLEANING_COMMAND_RENAME_LINKS)){

												if(propertyUri.trim().startsWith(oldPublicUrl)){
													renamedLinkUris.add(propertyUri);
												}
											}
											
											if(commands.contains(CLEANING_COMMAND_REMOVE_LINK_CONFIG_CONTEXT)){
												
												String queryUri = UrlUtils.getQueryString(propertyUri);
												
												if (StringUtils.isSet(queryUri))
												{
													if(output){
														LogUtils.logInfo((test ? "Test f" : "F") + "ound link: " + propertyUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
													}
													removeLinkConfigContextUris.add(propertyUri);
												}
												
											}
										}
									}
								}

								//Remove the broken forward links in the resource:
								if(removedBrokenForwardLinkStatements.size() > 0){

									//Back-up the resource:
									LogUtils.logDebug("Before removing" + (removeAllForwardLinks ? " " : " broken ") + "forward link '" + propertyPrefixedName + "' propert" + (removedBrokenForwardLinkStatements.size() == 1 ? "y" : "ies") + " referencing '" + targetResourceName + "' resource" + (removedBrokenForwardLinkStatements.size() == 1 ? "" : "s") + " '" + RdfUtils.write(removedBrokenForwardLinkStatements) + "' from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "':" + LINE_SEPARATOR + RdfUtils.write(resourceCopyModel, null, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$
									
									//Remove the list of removed broken forward links and their reified statements from the resource:
									for(Statement statement : removedBrokenForwardLinkStatements){

										resourceCopyModel.remove(statement);

										//Note: Iterate the list of reified statements (instead of using the iterator) to avoid the ConcurrentModificationException after removing the reification.
										for(ReifiedStatement reifiedStatement : resourceCopyModel.listReifiedStatements(statement).toList()){

											reifiedStatement.removeAll(resourceCopyModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

											resourceCopyModel.removeReification(reifiedStatement);
										}
									}

									//Back-up the resource:
									LogUtils.logDebug("After removing" + (removeAllForwardLinks ? " " : " broken ") + "forward link '" + propertyPrefixedName + "' propert" + (removedBrokenForwardLinkStatements.size() == 1 ? "y" : "ies") + " referencing '" + targetResourceName + "' resource" + (removedBrokenForwardLinkStatements.size() == 1 ? "" : "s") + " '" + RdfUtils.write(removedBrokenForwardLinkStatements) + "' from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "':" + LINE_SEPARATOR + RdfUtils.write(resourceCopyModel, null, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$
									
									if(!test){

										//Update the resource:	
										updateResource(httpClient, resourceUri, resourceCopyModel);
									}

									//Capture the output message:
									removedBrokenForwardLinksMessages.add((output ? "        " : "") + (test ? "Test r" : "R") + "emoved" + (removeAllForwardLinks ? " " : " broken ") + "forward link '" + propertyPrefixedName + "' propert" + (removedBrokenForwardLinkStatements.size() == 1 ? "y" : "ies") + " '" + RdfUtils.write(removedBrokenForwardLinkStatements) + "' from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$

									removedBrokenForwardLinks += removedBrokenForwardLinkStatements.size();
								}
								
								//Update the link labels in the resource:
								if(updatedLinkLabelLinks.size() > 0){

									//Resolve the updated resource model:
									resourceModel = RdfUtils.read(httpClient, resourceUri);

									resource = RdfUtils.getRootResource(resourceModel, resourceTypeUri);

									//Back-up the resource:
									LogUtils.logDebug("Before removing updated link label link '" + propertyPrefixedName + "' propert" + (updatedLinkLabelLinks.size() == 1 ? "y" : "ies") + " referencing '" + targetResourceName + "' resource" + (updatedLinkLabelLinks.size() == 1 ? "" : "s") + " from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "':" + LINE_SEPARATOR + RdfUtils.write(resourceModel, null, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
									
									//Remove the list of updated link label link statements and their reified statements from the resource:
									for(Link link : updatedLinkLabelLinks){

										Statement propertyStatement = resourceModel.createStatement(resource, resourceModel.createProperty(propertyNamespaceUri, propertyName), resourceModel.createResource(link.getUrl()));

										//Remove the updated link label link statement:
										resourceModel.remove(propertyStatement);

										//Note: Iterate the list of reified statements (instead of using the iterator) to avoid the ConcurrentModificationException after removing the reification.
										for(ReifiedStatement reifiedStatement : resourceModel.listReifiedStatements(propertyStatement).toList()){

											//Remove the reified statement for the updated link label:
											reifiedStatement.removeAll(resourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

											resourceModel.removeReification(reifiedStatement);
										}
									}

									//Back-up the resource:
									LogUtils.logDebug("After removing updated link label link '" + propertyPrefixedName + "' propert" + (updatedLinkLabelLinks.size() == 1 ? "y" : "ies") + " referencing '" + targetResourceName + "' resource" + (updatedLinkLabelLinks.size() == 1 ? "" : "s") + " from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "':" + LINE_SEPARATOR + RdfUtils.write(resourceModel, null, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$
									
									if(!test){

										//Update the resource:
										updateResource(httpClient, resourceUri, resourceModel);
									}

									//Resolve the updated resource model:
									resourceModel = RdfUtils.read(httpClient, resourceUri);

									resource = RdfUtils.getRootResource(resourceModel, resourceTypeUri);

									//Back-up the resource:
									LogUtils.logDebug("Before re-adding updated link label link '" + propertyPrefixedName + "' propert" + (updatedLinkLabelLinks.size() == 1 ? "y" : "ies") + " referencing '" + targetResourceName + "' resource" + (updatedLinkLabelLinks.size() == 1 ? "" : "s") + " from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "':" + LINE_SEPARATOR + RdfUtils.write(resourceModel, null, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
									
									//Add the list of updated link label link statements and their reified statements to the resource:
									for(Link link : updatedLinkLabelLinks){

										Statement propertyStatement = resourceModel.createStatement(resource, resourceModel.createProperty(propertyNamespaceUri, propertyName), resourceModel.createResource(link.getUrl()));

										//Add the updated link label link statement:
										resourceModel.add(propertyStatement);

										//Add the reified statement for the updated link label:
										ReifiedStatement reifiedStatement = propertyStatement.createReifiedStatement();								

										reifiedStatement.addProperty(resourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE), link.getLabel());	
									}

									//Back-up the resource:
									LogUtils.logDebug("After re-adding updated link label link '" + propertyPrefixedName + "' propert" + (updatedLinkLabelLinks.size() == 1 ? "y" : "ies") + " referencing '" + targetResourceName + "' resource" + (updatedLinkLabelLinks.size() == 1 ? "" : "s") + " from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "':" + LINE_SEPARATOR + RdfUtils.write(resourceModel, null, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$
									
									if(!test){

										//Update the resource:
										updateResource(httpClient, resourceUri, resourceModel);
									}

									//Capture the output message:
									updatedLinkLabelsMessages.add((output ? "        " : "") + (test ? "Test u" : "U") + "pdated link label link '" + propertyPrefixedName + "' propert" + (updatedLinkLabelLinks.size() == 1 ? "y" : "ies") + " from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$

									updatedLinkLabels += updatedLinkLabelLinks.size();
								}

								//Updating the OSLC versioned URL forward links to OSLC concept URL forward links in the resource:
								if(updatedVersionedLinkUrls.size() > 0){

									//Resolve the updated resource model:
									resourceModel = RdfUtils.read(httpClient, resourceUri);

									resource = RdfUtils.getRootResource(resourceModel, resourceTypeUri);

									//Back-up the resource:
									LogUtils.logDebug("Before updating the OSLC versioned forward link '" + propertyPrefixedName + "' propert" + (updatedVersionedLinkUrls.size() == 1 ? "y" : "ies") + " referencing '" + targetResourceName + "' resource" + (updatedVersionedLinkUrls.size() == 1 ? "" : "s") + " from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "':" + LINE_SEPARATOR + RdfUtils.write(resourceModel, null, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$

									//Update the OSLC versioned URL forward links to OSLC concept URL forward links in the resource:
									for(String versionedUrl : updatedVersionedLinkUrls){

										final String conceptUrl = UrlUtils.getConceptUrl(versionedUrl);

										//Resolve and remove the OSLC versioned URL forward link:
										Statement propertyStatement = resourceModel.createStatement(resource, resourceModel.createProperty(propertyNamespaceUri, propertyName), resourceModel.createResource(versionedUrl));

										resourceModel.remove(propertyStatement);

										//Resolve and remove the reified statement for the OSLC versioned URL forward link:
										String propertyLinkLabel = null;

										final Resource propertyReifiedStatement = resourceModel.getAnyReifiedStatement(propertyStatement);

										if (propertyReifiedStatement != null) {

											final Statement propertyTitleProperty = propertyReifiedStatement.getProperty(resourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

											if (propertyTitleProperty != null) {

												propertyLinkLabel = propertyTitleProperty.getString();

												//Note: Iterate the list of reified statements (instead of using the iterator) to avoid the ConcurrentModificationException after removing the reification.
												for(ReifiedStatement reifiedStatement : resourceModel.listReifiedStatements(propertyStatement).toList()){

													//Remove the reified statement for the OSLC versioned URL forward link:
													reifiedStatement.removeAll(resourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

													resourceModel.removeReification(reifiedStatement);
												}
											}	
										}

										//If the OSLC concept URL forward link does not exist, re-add the OSLC concept URL forward link:
										propertyStatement = resourceModel.createStatement(resource, resourceModel.createProperty(propertyNamespaceUri, propertyName), resourceModel.createResource(conceptUrl));
										
										if(!resourceModel.contains(propertyStatement)) {

											//Re-add the OSLC concept URL forward link:
											resourceModel.add(propertyStatement);

											//Re-add the reified statement for the OSLC concept URL forward link:
											if(propertyLinkLabel != null){

												ReifiedStatement reifiedStatement = propertyStatement.createReifiedStatement();								

												reifiedStatement.addProperty(resourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE), propertyLinkLabel);	
											}
										}
									}

									//Back-up the resource:
									LogUtils.logDebug("After updating the OSLC versioned forward link '" + propertyPrefixedName + "' propert" + (updatedVersionedLinkUrls.size() == 1 ? "y" : "ies") + " referencing '" + targetResourceName + "' resource" + (updatedVersionedLinkUrls.size() == 1 ? "" : "s") + " from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "':" + LINE_SEPARATOR + RdfUtils.write(resourceModel, null, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$
									
									if(!test){

										//Update the resource:
										updateResource(httpClient, resourceUri, resourceModel);
									}
									
									//Capture the output message:
									updatedVersionedLinksMessages.add((output ? "        " : "") + (test ? "Test u" : "U") + "pdated OSLC versioned forward link '" + propertyPrefixedName + "' propert" + (updatedVersionedLinkUrls.size() == 1 ? "y" : "ies") + " from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$

									updatedVersionedLinks += updatedVersionedLinkUrls.size();
								}

								//Rename the links in the resource:
								if(renamedLinkUris.size() > 0){

									//Resolve the updated resource model:
									resourceModel = RdfUtils.read(httpClient, resourceUri);

									resource = RdfUtils.getRootResource(resourceModel, resourceTypeUri);

									//Back-up the resource:
									LogUtils.logDebug("Before renaming link '" + propertyPrefixedName + "' propert" + (renamedLinkUris.size() == 1 ? "y" : "ies") + " referencing '" + targetResourceName + "' resource" + (renamedLinkUris.size() == 1 ? "" : "s") + " from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "':" + LINE_SEPARATOR + RdfUtils.write(resourceModel, null, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
									
									//Rename the links in the resource:
									for(String oldLinkUri : renamedLinkUris){

										//Rename the link:
										String newLinkUri = oldLinkUri.replaceFirst(Pattern.quote(oldPublicUrl), newPublicUrl);

										//Resolve and remove the old link:
										Statement propertyStatement = resourceModel.createStatement(resource, resourceModel.createProperty(propertyNamespaceUri, propertyName), resourceModel.createResource(oldLinkUri));

										resourceModel.remove(propertyStatement);

										//Resolve and remove the reified statement for the old link:
										String propertyLinkLabel = null;

										Resource propertyReifiedStatement = resourceModel.getAnyReifiedStatement(propertyStatement);

										if (propertyReifiedStatement != null) {

											Statement propertyTitleProperty = propertyReifiedStatement.getProperty(resourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

											if (propertyTitleProperty != null) {

												propertyLinkLabel = propertyTitleProperty.getString();

												//Note: Iterate the list of reified statements (instead of using the iterator) to avoid the ConcurrentModificationException after removing the reification.
												for(ReifiedStatement reifiedStatement : resourceModel.listReifiedStatements(propertyStatement).toList()){

													//Remove the reified statement for the renamed link:
													reifiedStatement.removeAll(resourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

													resourceModel.removeReification(reifiedStatement);
												}
											}	
										}

										//Re-add the new link:
										propertyStatement = resourceModel.createStatement(resource, resourceModel.createProperty(propertyNamespaceUri, propertyName), resourceModel.createResource(newLinkUri));

										resourceModel.add(propertyStatement);

										//Re-add the reified statement for the new link:
										if(propertyLinkLabel != null){

											ReifiedStatement reifiedStatement = propertyStatement.createReifiedStatement();								

											reifiedStatement.addProperty(resourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE), propertyLinkLabel);	
										}
									}

									//Back-up the resource:
									LogUtils.logInfo("After renaming link '" + propertyPrefixedName + "' propert" + (renamedLinkUris.size() == 1 ? "y" : "ies") + " referencing '" + targetResourceName + "' resource" + (renamedLinkUris.size() == 1 ? "" : "s") + " from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "':" + LINE_SEPARATOR + RdfUtils.write(resourceModel, null, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$
									
									if(!test){

										//Update the resource:
										updateResource(httpClient, resourceUri, resourceModel);
									}

									//Capture the output message:
									renamedLinksMessages.add((output ? "        " : "") + (test ? "Test r" : "R") + "enamed link '" + propertyPrefixedName + "' propert" + (renamedLinkUris.size() == 1 ? "y" : "ies") + " from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$

									renamedLinks += renamedLinkUris.size();
								}
								
								//Fix ConfigLinks in the resource:
								if(removeLinkConfigContextUris.size() > 0){
									
									//Resolve the updated resource model:
									resourceModel = RdfUtils.read(httpClient, resourceUri);

									resource = RdfUtils.getRootResource(resourceModel, resourceTypeUri);

									//Back-up the resource:
									LogUtils.logDebug("Before fixing link '" + propertyPrefixedName + "' propert" + (removeLinkConfigContextUris.size() == 1 ? "y" : "ies") + " referencing '" + targetResourceName + "' resource" + (removeLinkConfigContextUris.size() == 1 ? "" : "s") + " from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "':" + LINE_SEPARATOR + RdfUtils.write(resourceModel, null, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
				
									//Remove configuration context from the links in the resource:
									for(String oldpropertyUri : removeLinkConfigContextUris){
										if(output){
											LogUtils.logInfo((test ? "Test f" : "F") + "ixing link with config context/query param'" + oldpropertyUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
										}
										//create new link without any query parameter:
							            String newLinkUri = UrlUtils.getQueryBaseUri(oldpropertyUri);

										//Resolve and remove the old link:
										Statement propertyStatement = resourceModel.createStatement(resource, resourceModel.createProperty(propertyNamespaceUri, propertyName), resourceModel.createResource(oldpropertyUri));

										resourceModel.remove(propertyStatement);

										//Resolve and remove the reified statement for the old link:
										String propertyLinkLabel = null;

										Resource propertyReifiedStatement = resourceModel.getAnyReifiedStatement(propertyStatement);

										if (propertyReifiedStatement != null) {

											Statement propertyTitleProperty = propertyReifiedStatement.getProperty(resourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

											if (propertyTitleProperty != null) {

												propertyLinkLabel = propertyTitleProperty.getString();

												//Note: Iterate the list of reified statements (instead of using the iterator) to avoid the ConcurrentModificationException after removing the reification.
												for(ReifiedStatement reifiedStatement : resourceModel.listReifiedStatements(propertyStatement).toList()){

													//Remove the reified statement for the renamed link:
													reifiedStatement.removeAll(resourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

													resourceModel.removeReification(reifiedStatement);
												}
											}	
										}

										//Re-add the new link:
										propertyStatement = resourceModel.createStatement(resource, resourceModel.createProperty(propertyNamespaceUri, propertyName), resourceModel.createResource(newLinkUri));

										resourceModel.add(propertyStatement);

										//Re-add the reified statement for the new link:
										if(propertyLinkLabel != null){

											ReifiedStatement reifiedStatement = propertyStatement.createReifiedStatement();								

											reifiedStatement.addProperty(resourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE), propertyLinkLabel);	
										}
									}

									//Back-up the resource:
									LogUtils.logDebug("After fixing config link '" + propertyPrefixedName + "' propert" + (removeLinkConfigContextUris.size() == 1 ? "y" : "ies") + " referencing '" + targetResourceName + "' resource" + (removeLinkConfigContextUris.size() == 1 ? "" : "s") + " from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "':" + LINE_SEPARATOR + RdfUtils.write(resourceModel, null, false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$
									
									if(!test){

										//Update the resource:
										updateResource(httpClient, resourceUri, resourceModel);
									}

									//Capture the output message:
									removeLinkConfigContextMessages.add((output ? "        " : "") + (test ? "Test f" : "F") + "ixed link '" + propertyPrefixedName + "' propert" + (removeLinkConfigContextUris.size() == 1 ? "y" : "ies") + " from '" + resourceName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$

									removeLinkConfigContext += removeLinkConfigContextUris.size();
								}
							}
						}
						catch (HttpClientException h) {

							LogUtils.logError("Error communicating with the server " + (h.hasUri() ? "using URI '" + h.getUri() + "' " : "") + "when processing '" + resourceDisplayName + "' resource '" + resourceUri + "' in project area '" + serviceProvider.getTitle() + "'.", h); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
							
							if((ignoreReadErrors) && (HTTP_METHOD_GET.equalsIgnoreCase(h.getMethod()))) {

								if(output) {
									LogUtils.logInfo((test ? "Test s" : "S") + "kipped processing '" + resourceDisplayName + "' resource '" + resourceUri + "' since error communicating with the server" + (h.hasUri() ? " using URI '" + h.getUri() + "'." : "."));  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
								}
							}
							else {
								throw h;
							}
						}
					}
					else{

						oldResourceCount++;

						if(output){
							LogUtils.logInfo((test ? "Test s" : "S") + "kipped processing '" + resourceDisplayName + "' resource '" + resourceUri + "' since the last modified date is before '" + DateTimeUtils.formatIso8601DateTime(lastModifiedDateTime) + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
						}
					}
				}

				//Important: Reset the resource feed page URL.
				resourceFeedPageUrl = null;

				//Resolve the next page of the resource feed URL:
				StmtIterator responseInfoStatementsIterator = resourceFeedPageModel.listStatements(null, RDF.type, resourceFeedPageModel.createProperty(NAMESPACE_URI_OSLC, RESOURCE_RESPONSE_INFO));

				if(responseInfoStatementsIterator.hasNext()){

					Resource responseInfoResource = responseInfoStatementsIterator.next().getSubject();

					StmtIterator nextPageStatementsIterator = resourceFeedPageModel.listStatements(responseInfoResource, resourceFeedPageModel.createProperty(NAMESPACE_URI_OSLC, PROPERTY_NEXT_PAGE), ((RDFNode)(null)));

					if(nextPageStatementsIterator.hasNext()){
						resourceFeedPageUrl = nextPageStatementsIterator.next().getResource().getURI();
					}
				}
			}
		}
		
		//Print the summary information for the reportBrokenLinks command separately: 
		if(commands.contains(CLEANING_COMMAND_REPORT_BROKEN_LINKS)){

			LogUtils.logInfo("Summary for command '" + CLEANING_COMMAND_REPORT_BROKEN_LINKS + "':"); //$NON-NLS-1$ //$NON-NLS-2$
			LogUtils.logInfo("    Processed " + serviceProviderCount + " project area" + (serviceProviderCount == 1 ? "." : "s.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			LogUtils.logInfo("    Processed " + resourceCount + " '" + resourceDisplayName + "' resource" + (resourceCount == 1 ? "." : "s.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			LogUtils.logInfo("        Skipped " + ignoredResourceCount + " '" + resourceDisplayName + "' resource" + (ignoredResourceCount == 1 ? "." : "s since ignored or not processed.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			LogUtils.logInfo("        Skipped " + oldResourceCount + " '" + resourceDisplayName + "' resource" + (oldResourceCount == 1 ? "." : "s since old.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			LogUtils.logInfo("    Processed " + reportBrokenLinksPropertyCount + " '" + propertyPrefixedName + "' propert" + (reportBrokenLinksPropertyCount == 1 ? "y." : "ies.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			LogUtils.logInfo("        Skipped " + ignoredReportBrokenLinksPropertyCount + " '" + propertyPrefixedName + "' propert" + (ignoredReportBrokenLinksPropertyCount == 1 ? "y" : "ies") + " since ignored or not processed."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			LogUtils.logInfo("    Found " + reportBrokenLinksMessages.size() + " broken link '" + propertyPrefixedName + "' propert" + (reportBrokenLinksMessages.size() == 1 ? "y" : "ies" + (reportBrokenLinksMessages.size() > 0 ? ":" : "."))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

			if(reportBrokenLinksMessages.size() > 0) {

				Collections.sort(reportBrokenLinksMessages);

				for(String brokenLinksOutputMessage : reportBrokenLinksMessages){
					LogUtils.logInfo(brokenLinksOutputMessage);
				}
			}
		}
		
		if(!nonReportBrokenLinkCommands.isEmpty()) {

			LogUtils.logInfo("Summary for command" + (nonReportBrokenLinkCommands.size() > 1 ? "s '" : " '") + CleanerUtils.toString(nonReportBrokenLinkCommands) + "':"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			LogUtils.logInfo("    Processed " + serviceProviderCount + " project area" + (serviceProviderCount == 1 ? "." : "s.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			LogUtils.logInfo("    Processed " + resourceCount + " '" + resourceDisplayName + "' resource" + (resourceCount == 1 ? "." : "s.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			LogUtils.logInfo("        Skipped " + ignoredResourceCount + " '" + resourceDisplayName + "' resource" + (ignoredResourceCount == 1 ? "." : "s since ignored or not processed.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			LogUtils.logInfo("        Skipped " + oldResourceCount + " '" + resourceDisplayName + "' resource" + (oldResourceCount == 1 ? "." : "s since old.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			LogUtils.logInfo("    Processed " + propertyCount + " '" + propertyPrefixedName + "' propert" + (propertyCount == 1 ? "y." : "ies.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$			

			if((commands.contains(CLEANING_COMMAND_REMOVE_BROKEN_FORWARD_LINKS)) || (commands.contains(CLEANING_COMMAND_REMOVE_ALL_FORWARD_LINKS)) || (commands.contains(CLEANING_COMMAND_REMOVE_BROKEN_FORWARD_LINKS_WITH_MISSING_BACK_LINKS))){

				LogUtils.logInfo("    " + (test ? "Test r" : "R") + "emoved " + removedBrokenForwardLinks + (removeAllForwardLinks ? " " : " broken ") + "forward link '" + propertyPrefixedName + "' propert" + (removedBrokenForwardLinks == 1 ? "y." : "ies.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$

				if(output){

					for(String removedBrokenForwardLinksOutputMessage : removedBrokenForwardLinksMessages){
						LogUtils.logInfo(removedBrokenForwardLinksOutputMessage);
					}
				}
			}

			if(commands.contains(CLEANING_COMMAND_ADD_MISSING_BACK_LINKS)){

				LogUtils.logInfo("    " + (test ? "Test a" : "A") + "dded " + addedMissingBackLinks + " missing back link '" + targetPropertyPrefixedName + "' propert" + (addedMissingBackLinks == 1 ? "y." : "ies.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

				if(output){

					for(String addedMissingBackLinksOutputMessage : addedMissingBackLinksMessages){
						LogUtils.logInfo(addedMissingBackLinksOutputMessage);
					}
				}
			}

			if((commands.contains(CLEANING_COMMAND_UPDATE_LINK_LABELS)) || (commands.contains(CLEANING_COMMAND_UPDATE_FORWARD_LINK_LABELS))){

				LogUtils.logInfo("    " + (test ? "Test u" : "U") + "pdated link labels for " + updatedLinkLabels + " link '" + targetPropertyPrefixedName + "' propert" + (updatedLinkLabels == 1 ? "y." : "ies.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

				if(output){

					for(String updatedLinkLabelsOutputMessage : updatedLinkLabelsMessages){
						LogUtils.logInfo(updatedLinkLabelsOutputMessage);
					}
				}
			}

			if(commands.contains(CLEANING_COMMAND_UPDATE_BACK_LINK_LABELS)){

				LogUtils.logInfo("    " + (test ? "Test u" : "U") + "pdated back link labels for " + updatedBackLinkLabels + " link '" + targetPropertyPrefixedName + "' propert" + (updatedBackLinkLabels == 1 ? "y." : "ies.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

				if(output){

					for(String updatedBackLinkLabelsOutputMessage : updatedBackLinkLabelsMessages){
						LogUtils.logInfo(updatedBackLinkLabelsOutputMessage);
					}
				}
			}

			if(commands.contains(CLEANING_COMMAND_UPDATE_VERSIONED_LINKS)){

				LogUtils.logInfo("    " + (test ? "Test u" : "U") + "pdated OSLC versioned forward links for " + updatedVersionedLinks + " link '" + targetPropertyPrefixedName + "' propert" + (updatedVersionedLinks == 1 ? "y." : "ies.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
				
				if(output){

					for(String updatedVersionedForwardLinksOutputMessage : updatedVersionedLinksMessages){
						LogUtils.logInfo(updatedVersionedForwardLinksOutputMessage);
					}
				}
			}
			
			if(commands.contains(CLEANING_COMMAND_RENAME_LINKS)){

				LogUtils.logInfo("    " + (test ? "Test r" : "R") + "enamed " + renamedLinks + " link '" + targetPropertyPrefixedName + "' propert" + (renamedLinks == 1 ? "y." : "ies.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

				if(output){

					for(String renamedLinksMessage : renamedLinksMessages){
						LogUtils.logInfo(renamedLinksMessage);
					}
				}
			}
			
			if(commands.contains(CLEANING_COMMAND_REMOVE_LINK_CONFIG_CONTEXT)){

				LogUtils.logInfo("    " + (test ? "Test f" : "F") + "ixed " + removeLinkConfigContext + " link '" + targetPropertyPrefixedName + "' propert" + (removeLinkConfigContext == 1 ? "y." : "ies.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

				if(output){

					for(String fixConfigLinksMessage : removeLinkConfigContextMessages){
						LogUtils.logInfo(fixConfigLinksMessage);
					}
				}
			}
		}

		LogUtils.logInfo("Completed " + (test ? "test " : "") + "processing '" + propertyPrefixedName + "' properties in '" + resourceDisplayName + "' resources in project area" + (serviceProviders.size() == 1 ? "" : "s") + " '" + getProjectAreaNames() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
	}		

	private String updateResource(OslcHttpClient oslcHttpClient, String resourceUri, Model resourceModel) throws IOException, HttpException {

		try {
			return (oslcHttpClient.put(resourceUri, resourceModel));
		} 
		catch (RQMProtocolException h) {

			if((h.getResponseCode() == HttpStatus. SC_LOCKED || h.getResponseCode() == HttpStatus.SC_BAD_REQUEST) && (oslcHttpClient instanceof QmOslcHttpClient)) {

				//Note: The rqm_qm:isLocked property is only supported by the OSLC QM API.
				final Boolean isLocked = RdfUtils.getPropertyBooleanValue(resourceModel, NAMESPACE_URI_JAZZ_QM, PROPERTY_IS_LOCKED);

				if((isLocked != null) && (isLocked)) {

					if(attemptRqmUnlock) {

						final String relationUri = RdfUtils.getPropertyResourceValue(resourceModel, NAMESPACE_URI_DCTERMS, PROPERTY_RELATION);

						if(UrlUtils.isValidUrl(relationUri)) {

							int statusCode = oslcHttpClient.unlock(relationUri);

							if(statusCode == HttpStatus.SC_OK) {

								if(output) {
									LogUtils.logInfo("Locked resource '" + resourceUri + "' was unlocked."); //$NON-NLS-1$ //$NON-NLS-2$
								}

								try {
									return (oslcHttpClient.put(resourceUri, resourceModel));
								}
								finally {

									statusCode = oslcHttpClient.lock(relationUri);

									if(statusCode == HttpStatus.SC_OK) {

										if(output) {
											LogUtils.logInfo("Unlocked resource '" + resourceUri + "' was locked."); //$NON-NLS-1$ //$NON-NLS-2$
										}
									}
									else if(statusCode == HttpStatus.SC_FORBIDDEN) {

										if(output) { 
											LogUtils.logInfo("Unlocked resource '" + resourceUri + "' cannot be locked.  Check if the 'E-Signature Required for Lock/Unlock' precondition for this resource type is enabled in the project area."); //$NON-NLS-1$ //$NON-NLS-2$
										}	
										if(LogUtils.isTraceEnabled()) {
											LogUtils.logTrace("Unlocked resource '" + resourceUri + "' cannot be locked since the LOCK HTTP request returned " + statusCode + " (" + statusCode + ").  Check if the 'E-Signature Required for Lock/Unlock' precondition for this resource type is enabled in the project area."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
										}
									}
									else {

										if(output) {
											LogUtils.logInfo("Unlocked resource '" + resourceUri + "' cannot be locked (" + statusCode + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
										}
										if(LogUtils.isTraceEnabled()) {
											LogUtils.logTrace("Unlocked resource '" + resourceUri + "' cannot be locked since the LOCK HTTP request returned " + statusCode + " (" + statusCode + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
										}
									}
								}
							}
							else if(statusCode == HttpStatus.SC_FORBIDDEN) {

								if(output) {
									LogUtils.logInfo("Locked resource '" + resourceUri + "' cannot be unlocked.  Check if the 'E-Signature Required for Lock/Unlock' precondition for this resource type is enabled in the project area."); //$NON-NLS-1$ //$NON-NLS-2$
								}
								if(LogUtils.isTraceEnabled()) {
									LogUtils.logTrace("Locked resource '" + resourceUri + "' cannot be unlocked since the UNLOCK HTTP request returned " + statusCode + " (" + statusCode + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
								}
							}
							else{

								if(output) {
									LogUtils.logInfo("Locked resource '" + resourceUri + "' cannot be unlocked."); //$NON-NLS-1$ //$NON-NLS-2$
								}
								if(LogUtils.isTraceEnabled()) {
									LogUtils.logTrace("Locked resource '" + resourceUri + "' cannot be unlocked since the UNLOCK HTTP request returned " + statusCode + " (" + statusCode + ")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
								}
							}
						}
						else{

							if(output) {
								LogUtils.logInfo("Locked resource '" + resourceUri + "' cannot be unlocked."); //$NON-NLS-1$ //$NON-NLS-2$
							}

							if(LogUtils.isTraceEnabled()) {
								LogUtils.logTrace("Locked resource '" + resourceUri + "' cannot be unlocked since it does not contain a valid URI in the dcterms:relation property."); //$NON-NLS-1$ //$NON-NLS-2$
							}
						}
					}
					else if(output) {
						LogUtils.logInfo("Resource '" + resourceUri + "' is locked.  See the -aru/-attemptRqmUnlock argument."); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}			

			throw h;
		}
	}

	public String getProjectAreaNames(){

		if(projectAreaNames == null){

			StringBuilder projectAreaNamesBuilder = new StringBuilder();

			for(ServiceProvider serviceProvider : serviceProviders){			

				if(projectAreaNamesBuilder.length() > 0){
					projectAreaNamesBuilder.append(", "); //$NON-NLS-1$
				}

				projectAreaNamesBuilder.append(serviceProvider.getTitle());
			}

			projectAreaNames = (projectAreaNamesBuilder.toString());
		}

		return projectAreaNames;
	}

	public List<String> getLinkTypeSameAsUris(String linkTypeUri, OslcHttpClient oslcHttpClient) throws Exception {

		if(linkTypeUriSameAsUrisMap == null){
			linkTypeUriSameAsUrisMap = new HashMap<String, List<String>>();
		}

		if(!linkTypeUriSameAsUrisMap.containsKey(linkTypeUri)) {

			List<String> sameAsUris = new ArrayList<String>();

			linkTypeUriSameAsUrisMap.put(linkTypeUri, sameAsUris);

			//Note: The link type URI could be a valid link type URI (e.g. http://open-services.net/ns/rm#validatedBy) or an URI to a link type resource (type: http://www.ibm.com/xmlns/rdm/rdf/LinkType). 
			if(UriUtils.containsValidTypeUri(linkTypeUri)) {
								
				if(!sameAsUris.contains(linkTypeUri)) {
					sameAsUris.add(linkTypeUri);
				}
			}
			else {
			
				Model linkTypeModel = RdfUtils.read(oslcHttpClient, linkTypeUri);

				Resource linkTypeResource = RdfUtils.getRootResource(linkTypeModel, RESOURCE_TYPE_URI_LINK_TYPE);

				StmtIterator sameAsStatementsIterator = linkTypeModel.listStatements(linkTypeResource, linkTypeModel.createProperty(NAMESPACE_URI_OWL, PROPERTY_SAME_AS), ((RDFNode)(null)));

				while(sameAsStatementsIterator.hasNext()){

					String sameAsUri = sameAsStatementsIterator.next().getResource().getURI();

					if(!sameAsUris.contains(sameAsUri)) {
						sameAsUris.add(sameAsUri);
					}	
				}
			}
			
			if(LogUtils.isTraceEnabled()) {
				LogUtils.logTrace("Caching link type '" + linkTypeUri + "': " + CleanerUtils.toString(sameAsUris)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}			

		return (linkTypeUriSameAsUrisMap.get(linkTypeUri));
	}

	private String resolveSelectionsNextPageUri(OslcHttpClient oslcHttpClient, String selectionsUri) throws IOException {

		if((oslcHttpClient != null) && (UrlUtils.isValidUrl(selectionsUri))){

			String linkResponseHeader = oslcHttpClient.getResponseHeader(selectionsUri, HTTP_HEADER_LINK);

			if(StringUtils.isSet(linkResponseHeader)) {

				Matcher nextPageMatcher = SELECTIONS_NEXT_PAGE_PATTERN.matcher(linkResponseHeader);

				if(nextPageMatcher.find()) {

					String selectionsNextPageUri = nextPageMatcher.group(1);

					if((StringUtils.isSet(selectionsNextPageUri)) && (!RDF.nil.getURI().equals(selectionsNextPageUri))){
						return selectionsNextPageUri;
					}
				}
			}
		}
		
		return null;
	}

	private boolean ignoreResource(String resourceUrl) {

		if(StringUtils.isSet(resourceUrl)) {

			if(ignoreResourceUrls != null) {

				for(String ignoreResourceUrl : ignoreResourceUrls) {

					if(resourceUrl.startsWith(ignoreResourceUrl)) {
						return true;
					}
				}
			}

			if(processResourceUrls != null) {

				for(String processResourceUrl : processResourceUrls) {

					if(resourceUrl.startsWith(processResourceUrl)) {
						return false;
					}
				}
				
				return true;
			}
		}

		return false;
	}

	private boolean ignoreTargetResource(String targetResourceUrl) {

		if(StringUtils.isSet(targetResourceUrl)) {

			if(ignoreTargetResourceUrls != null) {

				for(String ignoreTargetResourceUrl : ignoreTargetResourceUrls) {

					if(targetResourceUrl.startsWith(ignoreTargetResourceUrl)) {
						return true;
					}
				}
			}

			if(processTargetResourceUrls != null) {

				for(String processTargetResourceUrl : processTargetResourceUrls) {

					if(targetResourceUrl.startsWith(processTargetResourceUrl)) {
						return false;
					}
				}
				
				return true;
			}
		}

		return false;
	}
	
	private boolean processResource(Resource resource){
		
		if(lastModifiedDateTime != -1){

			Model model = resource.getModel();

			Statement modifiedPropertyStatement = model.getProperty(resource, model.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_MODIFIED));

			//Note: The dcterms:modified property is optional.
			if(modifiedPropertyStatement != null){

				long modifiedPropertyDateTime = DateTimeUtils.parseDateTime(modifiedPropertyStatement.getString());

				if(modifiedPropertyDateTime != -1){
					return (modifiedPropertyDateTime > lastModifiedDateTime);
				}
				else{
					LogUtils.logError("Error parsing property '" + PROPERTY_DCTERMS_MODIFIED + "' value '" + modifiedPropertyStatement.getString() + "' for resource '" + resource.getURI() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				}
			}
			else if(LogUtils.isTraceEnabled()) {
				LogUtils.logTrace("Cannot resolve property '" + PROPERTY_DCTERMS_MODIFIED + "' for resource '" + resource.getURI() + "'.");				 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}

		return true;
	}

	private List<String> resolveLinksFromLdx(String propertyPrefixedName, Resource resource, OslcHttpClient oslcHttpClient) throws Exception {

		//When using a configuration context (global configuration) and the link type is not owned by the resource type, 
		//resolve the links from LDX using non-standard APIs since the resource model will not contain link types that
		//the resource type does not own:
		//1. IBM Rational DOORS Next Generation: Resolve the links using a private API since the OSLC RM compact rendering/rich hover XHTML contains at most 15 links.
		//2. Other applications: Resolve the links from the compact rendering/rich hover XHTML.
		if((oslcHttpClient instanceof RmOslcHttpClient) && (usePrivateDngApi)){
			return (resolveLinksFromDngLinksApi(propertyPrefixedName, resource, oslcHttpClient));
		}
		else {
			return (resolveLinksFromCompactRendering(propertyPrefixedName, resource, oslcHttpClient));
		}
	}
	
	private List<String> resolveLinksFromDngLinksApi(String propertyPrefixedName, Resource resource, OslcHttpClient oslcHttpClient) throws Exception {

		String resourceUri = resource.getURI();
		
		if((oslcHttpClient instanceof RmOslcHttpClient) && (usePrivateDngApi)){

			RmOslcHttpClient rmOslcHttpClient = ((RmOslcHttpClient)(oslcHttpClient));

			try {
				
				long startTime = System.currentTimeMillis();
				
				List<String> links = new ArrayList<String>();

				String propertyNamespacePrefix = UriUtils.getNamespacePrefix(propertyPrefixedName);
				String propertyName = UriUtils.getName(propertyPrefixedName);
				String propertyNamespaceUri = UriUtils.resolveNamespaceUri(propertyNamespacePrefix);
				String propertyUri = (propertyNamespaceUri + propertyName);

				//Resolve the links from the (private) DNG links API:
				String dngLinksUrl = MessageFormat.format(URI_TEMPLATE_DNG_LINKS, new Object[]{rmOslcHttpClient.getServerUrl()});

				//Set the component URL (required to resolve the owning context):
				//Note: IBM Rational DOORS Next Generation requires the net.jazz.jfs.owning-context request header set to the owning context URL.
				rmOslcHttpClient.setComponentUrl(resolveComponentUrl(resource));

				Model remoteLinksModel = RdfUtils.read(rmOslcHttpClient, dngLinksUrl, (QUERY_PARAMETER_DNG_SOURCE_OR_TARGET + "=" + UrlUtils.urlEncode(resourceUri))); //$NON-NLS-1$

				if(LogUtils.isTraceEnabled()) {
					LogUtils.logTrace("Resolving remote links for resource '" + resourceUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$
				}

				//Resolve the links from all the remote links:
				StmtIterator remoteLinkStatementsIterator = remoteLinksModel.listStatements(null, RDF.type, remoteLinksModel.createProperty(NAMESPACE_URI_RM, PROPERTY_REMOTE_LINK));

				while(remoteLinkStatementsIterator.hasNext()){

					Resource remoteLinkResource = remoteLinkStatementsIterator.next().getSubject();

					String sourceResourceUri = remoteLinksModel.getRequiredProperty(remoteLinkResource, remoteLinksModel.createProperty(NAMESPACE_URI_RDF, PROPERTY_SUBJECT)).getResource().getURI();

					if(resourceUri.equals(sourceResourceUri)) {

						String linkTypeUri = remoteLinksModel.getRequiredProperty(remoteLinkResource, remoteLinksModel.createProperty(NAMESPACE_URI_RDF, PROPERTY_PREDICATE)).getResource().getURI();

						List<String> sameAsUris = getLinkTypeSameAsUris(linkTypeUri, rmOslcHttpClient);

						if((sameAsUris != null) && (!sameAsUris.isEmpty())){

							for(String sameAsUri : sameAsUris) {

								if(propertyUri.equals(sameAsUri)) {

									String remoteLinkUri = remoteLinksModel.getRequiredProperty(remoteLinkResource, remoteLinksModel.createProperty(NAMESPACE_URI_RDF, PROPERTY_OBJECT)).getResource().getURI();

									if(!links.contains(remoteLinkUri)) {
										links.add(remoteLinkUri);
									}

									break;
								}
							}
						}
					}
				}

				if(LogUtils.isTraceEnabled()) {

					String linksToString = CleanerUtils.toString(links);
					if(LogUtils.isTraceEnabled()) {
						LogUtils.logTrace("Found the following links for resource '" + resourceUri + "' and property '" + propertyPrefixedName + "':" + (StringUtils.isSet(linksToString) ? (LINE_SEPARATOR + linksToString) : " <no links>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					}
				}

				long endTime = System.currentTimeMillis();

				if(PerformanceUtils.isTraceEnabled()) {
					PerformanceUtils.logTrace("Resolve links for resource '" + resourceUri + "' and property '" + propertyPrefixedName + "'", startTime, endTime); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}

				return links;
			}
			finally {
				rmOslcHttpClient.setComponentUrl(null);
			}
		}
		else {
			throw new Exception("Attempting to resolve remote links for resource '" + resourceUri + "' and property '" + propertyPrefixedName + "' using the incorrect API."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	private String resolveComponentUrl(Resource resource) {
		
		String componentUrl = null;

		if(resource != null) {

			Model model = resource.getModel();

			Statement componentPropertyStatement = model.getProperty(resource, model.createProperty(NAMESPACE_URI_OSLC_CONFIG, PROPERTY_COMPONENT));

			//Note: The oslc_config:component property is optional.
			if(componentPropertyStatement != null){				
				componentUrl = componentPropertyStatement.getResource().getURI();
			}
		}

		if(LogUtils.isTraceEnabled()) {
			LogUtils.logTrace("Resolve property '" + PROPERTY_OSLC_CONFIG_COMPONENT + "' for resource '" + resource.getURI() + "': " + componentUrl); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		return componentUrl;
	}

	private List<String> resolveLinksFromCompactRendering(String propertyPrefixedName, Resource resource, OslcHttpClient oslcHttpClient) throws Exception {
		
		long startTime = System.currentTimeMillis();
		
		String resourceUri = resource.getURI();

		List<String> links = new ArrayList<String>();

		List<String> propertyLabels = OslcUtils.getPropertyLabels(propertyPrefixedName, propertyPrefixedNamePropertyLabelsMap);

		if((propertyLabels != null) && (!propertyLabels.isEmpty())) {

			//Resolve the links from the compact rendering:
			Model compactResourceModel = RdfUtils.read(oslcHttpClient, resourceUri, null, MEDIA_TYPE_OSLC_COMPACT);

			if(LogUtils.isTraceEnabled()) {
				LogUtils.logTrace("Resolving compact rendering for resource '" + resourceUri + "' and property '" + propertyPrefixedName + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			
			//Resolve the links from all the previews (small and large):
			StmtIterator previewStatementsIterator = compactResourceModel.listStatements(null, RDF.type, compactResourceModel.createProperty(NAMESPACE_URI_OSLC, RESOURCE_PREVIEW));

			while(previewStatementsIterator.hasNext()){

				Resource previewResource = previewStatementsIterator.next().getSubject();

				StmtIterator documentStatementsIterator = compactResourceModel.listStatements(previewResource, compactResourceModel.createProperty(NAMESPACE_URI_OSLC, PROPERTY_DOCUMENT), ((RDFNode)(null)));

				if(documentStatementsIterator.hasNext()){

					String previewUrl = documentStatementsIterator.next().getResource().getURI();

					String previewXhtml = XhtmlUtils.read(oslcHttpClient, previewUrl);

					if(LogUtils.isTraceEnabled()) {
						LogUtils.logTrace("Preview '" + previewUrl + "' for resource '" + resourceUri + "' and property '" + propertyPrefixedName + "':" + LINE_SEPARATOR + previewXhtml); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					}
					
					for(String propertyLabel : propertyLabels) {

						//Example:
						//<td valign="top" class="label" style="padding-left:8px"> Validated By
						//	<img style='vertical-align:text-bottom;padding-left:3px;height:16px;width:16px;vertical-align:middle;margin-top:-1px' src='https://qm70.rtp.raleigh.ibm.com:9443/rm/web/com.ibm.rdm.web/common/images/incoming-link.svg' alt='Incoming link'/> (2): 
						//	<a target="_top" title="https://qm70.rtp.raleigh.ibm.com:9443/qm/oslc_qm/contexts/_xPQ_0VqREemq54FmsqIOEw/resources/com.ibm.rqm.planning.VersionedTestCase/_GEojIXaUEembSo8BwSYzCw" class="externalLink btd" href="https://qm70.rtp.raleigh.ibm.com:9443/qm/oslc_qm/contexts/_xPQ_0VqREemq54FmsqIOEw/resources/com.ibm.rqm.planning.VersionedTestCase/_GEojIXaUEembSo8BwSYzCw?oslc_config.context=https%3A%2F%2Fqm70.rtp.raleigh.ibm.com%3A9443%2Fgc%2Fconfiguration%2F2">https://qm70.rtp.raleigh.ibm.com:9443/qm/oslc_qm/contexts/_xPQ_0VqREemq54FmsqIOEw/resources/com.ibm.rqm.planning.VersionedTestCase/_GEojIXaUEembSo8BwSYzCw</a>
						//	,
						//	<a target="_top" title="https://qm70.rtp.raleigh.ibm.com:9443/qm/oslc_qm/contexts/_xPQ_0VqREemq54FmsqIOEw/resources/com.ibm.rqm.planning.VersionedTestCase/_sxwg0XNKEemRSJy37bfQOQ" class="externalLink btd" href="https://qm70.rtp.raleigh.ibm.com:9443/qm/oslc_qm/contexts/_xPQ_0VqREemq54FmsqIOEw/resources/com.ibm.rqm.planning.VersionedTestCase/_sxwg0XNKEemRSJy37bfQOQ?oslc_config.context=https%3A%2F%2Fqm70.rtp.raleigh.ibm.com%3A9443%2Fgc%2Fconfiguration%2F2">https://qm70.rtp.raleigh.ibm.com:9443/qm/oslc_qm/contexts/_xPQ_0VqREemq54FmsqIOEw/resources/com.ibm.rqm.planning.VersionedTestCase/_sxwg0XNKEemRSJy37bfQOQ</a>
						//</td>
						Pattern pattern = Pattern.compile("<td(?=(?:(?!</td>)[\\s\\S])*?" + propertyLabel + ")[\\s\\S]*?</td>"); //$NON-NLS-1$ //$NON-NLS-2$
						Matcher matcher = pattern.matcher(previewXhtml);

						if(matcher.find()) {

							String td = matcher.group();

							pattern = Pattern.compile("<a[^<]*href=\"([^\"]+)+\"[^<]*</a>"); //$NON-NLS-1$
							matcher = pattern.matcher(td);

							while(matcher.find()) {

								//Example: https://qm70.rtp.raleigh.ibm.com:9443/qm/oslc_qm/contexts/_xPQ_0VqREemq54FmsqIOEw/resources/com.ibm.rqm.planning.VersionedTestCase/_GEojIXaUEembSo8BwSYzCw?oslc_config.context=https%3A%2F%2Fqm70.rtp.raleigh.ibm.com%3A9443%2Fgc%2Fconfiguration%2F2
								//Note: Remove any request parameters (including oslc_config.context) from the resource URL. 
								String url = UrlUtils.getQueryBaseUri(matcher.group(1));

								if(!links.contains(url)) {
									links.add(url);
								}
							}
						}
					}
				}
			}
		}
		else {
			throw new Exception("Command could not resolve the property label for resource '" + resourceUri + "' and property '" + propertyPrefixedName + "' property."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		if(LogUtils.isTraceEnabled()) {
			
    		String linksToString = CleanerUtils.toString(links);
    		if(LogUtils.isTraceEnabled()) {
    			LogUtils.logTrace("Found the following links using property label(s) '" + CleanerUtils.toString(propertyLabels) + "' in preview(s) for resource '" + resourceUri + "' and property '" + propertyPrefixedName + "':" + (StringUtils.isSet(linksToString) ? (LINE_SEPARATOR + linksToString) : " <no links>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    		}
		}
		
    	long endTime = System.currentTimeMillis();
    	
    	if(PerformanceUtils.isTraceEnabled()) {
			PerformanceUtils.logTrace("Resolve links using property label(s) '" + CleanerUtils.toString(propertyLabels) + "' in preview(s) for resource '" + resourceUri + "' and property '" + propertyPrefixedName + "'", startTime, endTime); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    	}
    	
		return links;
	}

	private String resolveForwardLinkLabel(String targetResourceUri, Resource targetResource, OslcHttpClient targetHttpClient) throws Exception {

		StringBuilder forwardLinkLabel = new StringBuilder();

		Model targetResourceModel = targetResource.getModel();

		//Special handling for IBM Rational DOORS Web Access (DWA) forward link labels:
		//Format: <target RM resource's module title> (<target RM resource numerical ID>)
		if(targetHttpClient instanceof DwaOslcHttpClient){

			//Resolve the requirement label from the compact rendering:
			String requirementLabel = null;

			Model targetCompactResourceModel = RdfUtils.read(targetHttpClient, targetResourceUri, null, MEDIA_TYPE_OSLC_COMPACT);

			Resource targetCompactResource = RdfUtils.getRootResource(targetCompactResourceModel, RESOURCE_TYPE_URI_COMPACT);
			
			Statement targetCompactResourceLabelPropertyStatement = targetCompactResourceModel.getProperty(targetCompactResource, targetCompactResourceModel.createProperty(NAMESPACE_URI_OSLC, PROPERTY_LABEL));

			if(targetCompactResourceLabelPropertyStatement != null){

				String targetCompactResourceLabelProperty = targetCompactResourceLabelPropertyStatement.getString();

				if ((targetCompactResourceLabelProperty != null) && (!targetCompactResourceLabelProperty.trim().isEmpty())){
					requirementLabel = targetCompactResourceLabelProperty;
				}
			}
			
			if((requirementLabel != null) && (requirementLabel.length() > 0)){
				forwardLinkLabel.append(requirementLabel);
			}
		}

		//Format: <target RM resource numerical ID>: <target RM resource title>
		else{

			//Resolve the requirement identifier (numerical ID):
			String requirementIdentifier = null;

			Statement targetResourceIdentifierPropertyStatement = targetResourceModel.getProperty(targetResource, targetResourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_IDENTIFIER));

			if(targetResourceIdentifierPropertyStatement != null){

				String targetResourceIdentifierProperty = targetResourceIdentifierPropertyStatement.getString();

				if ((targetResourceIdentifierProperty != null) && (!targetResourceIdentifierProperty.trim().isEmpty())){
					requirementIdentifier = targetResourceIdentifierProperty;					
				}
			}

			//Resolve the requirement title:
			String requirementTitle = null;

			Statement targetResourceTitlePropertyStatement = targetResourceModel.getProperty(targetResource, targetResourceModel.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

			if(targetResourceTitlePropertyStatement != null){

				String targetResourceTitleProperty = targetResourceTitlePropertyStatement.getString();

				if ((targetResourceTitleProperty != null) && (!targetResourceTitleProperty.trim().isEmpty())){
					requirementTitle = targetResourceTitleProperty;
				}
			}

			if((requirementIdentifier != null) && (requirementIdentifier.length() > 0)){

				forwardLinkLabel.append(requirementIdentifier);
				forwardLinkLabel.append(":"); //$NON-NLS-1$
				
				//Note: The requirement title may be empty.
				if((requirementTitle != null) && (requirementTitle.length() > 0)) {
					
					forwardLinkLabel.append(" "); //$NON-NLS-1$
					forwardLinkLabel.append(requirementTitle);
				}
			}
		}

		return (forwardLinkLabel.toString());
	}

	private String resolveOslcPrefixQueryParameter(List<QName> supportedProperties) throws Exception {

		StringBuilder oslcPrefixQueryParameter = new StringBuilder();

		for(QName supportedProperty : supportedProperties) {

			//Example: dcterms=<http://purl.org/dc/terms/>			
			StringBuilder oslcPrefixQueryParameterValue = new StringBuilder();
			oslcPrefixQueryParameterValue.append(supportedProperty.getNamespacePrefix());
			oslcPrefixQueryParameterValue.append("=<"); //$NON-NLS-1$
			oslcPrefixQueryParameterValue.append(supportedProperty.getNamespaceUri());
			oslcPrefixQueryParameterValue.append(">"); //$NON-NLS-1$

			String encodedOslcPrefixQueryParameterValue = UrlUtils.urlEncode(oslcPrefixQueryParameterValue.toString());

			//Note: Do not permit duplicates.
			if(oslcPrefixQueryParameter.indexOf(encodedOslcPrefixQueryParameterValue) == -1) {

				if(oslcPrefixQueryParameter.length() > 0) {
					oslcPrefixQueryParameter.append("&"); //$NON-NLS-1$
				}

				oslcPrefixQueryParameter.append(QUERY_PARAMETER_OSLC_PREFIX);
				oslcPrefixQueryParameter.append("="); //$NON-NLS-1$
				oslcPrefixQueryParameter.append(encodedOslcPrefixQueryParameterValue);
			}
		}

		return (oslcPrefixQueryParameter.toString());
	}

	private String resolveOslcPropertiesQueryParameter() throws Exception {
		return (QUERY_PARAMETER_OSLC_PROPERTIES + "=" + PROPERTY_WILDCARD); //$NON-NLS-1$
	}

	private String resolveOslcPropertiesQueryParameter(List<QName> supportedProperties) throws Exception {

		StringBuilder oslcPropertiesQueryParameter = new StringBuilder();

		for(QName supportedProperty : supportedProperties) {

			//Example: dcterms:title
			StringBuilder oslcPropertiesQueryParameterValue= new StringBuilder();
			oslcPropertiesQueryParameterValue.append(supportedProperty.getNamespacePrefix());
			oslcPropertiesQueryParameterValue.append(":"); //$NON-NLS-1$
			oslcPropertiesQueryParameterValue.append(supportedProperty.getName());

			//Note: Do not permit duplicates.
			if(oslcPropertiesQueryParameter.indexOf(oslcPropertiesQueryParameterValue.toString()) == -1) {

				if(oslcPropertiesQueryParameter.length() > 0) {
					oslcPropertiesQueryParameter.append(","); //$NON-NLS-1$
				}

				oslcPropertiesQueryParameter.append(oslcPropertiesQueryParameterValue);
			}
		}

		return (QUERY_PARAMETER_OSLC_PROPERTIES + "=" + UrlUtils.urlEncode(oslcPropertiesQueryParameter.toString())); //$NON-NLS-1$
	}

	private String resolveOslcWhereQueryParameter(String resourceTypeUri, OslcHttpClient oslcHttpClient) {

		StringBuilder oslcWhereQueryParameter = new StringBuilder();
		oslcWhereQueryParameter.append(PROPERTY_RDF_TYPE);
		oslcWhereQueryParameter.append("=<"); //$NON-NLS-1$
		oslcWhereQueryParameter.append(resourceTypeUri);
		oslcWhereQueryParameter.append(">"); //$NON-NLS-1$

		if((resourceIdentifiers != null) && (!resourceIdentifiers.isEmpty())) {

			boolean includeQuotes = true;
			
			//Problem: In IBM Rational DOORS Next Generation 6.0.6 and earlier, an OSLC RM V2 API query with an oslc.where predicate containing a string or plain literal property value enclosed in double quote characters returns no results.
			//Work-around: In the oslc.where predicate, do NOT enclose the string or plain literal property value in double quote characters. 
			//Note: IBM Rational DOORS Next Generation 7.0 and later is backward compatible.
			//Defect: 130931: OSLC RM V2 API query returns no results when using oslc.where.
			if(oslcHttpClient instanceof RmOslcHttpClient){
				includeQuotes = false;
			}
			
			oslcWhereQueryParameter.append(" and "); //$NON-NLS-1$
			oslcWhereQueryParameter.append(PROPERTY_DCTERMS_IDENTIFIER);
			oslcWhereQueryParameter.append(" in ["); //$NON-NLS-1$

			for (int index = 0; index < resourceIdentifiers.size(); index++) {

				if(index > 0) {
					oslcWhereQueryParameter.append(","); //$NON-NLS-1$
				}

				if(includeQuotes) {
					oslcWhereQueryParameter.append("\""); //$NON-NLS-1$
				}
				
				oslcWhereQueryParameter.append(resourceIdentifiers.get(index));
				
				if(includeQuotes) {
					oslcWhereQueryParameter.append("\""); //$NON-NLS-1$
				}
			}

			oslcWhereQueryParameter.append("]"); //$NON-NLS-1$
		}

		return (QUERY_PARAMETER_OSLC_WHERE + "=" + UrlUtils.urlEncode(oslcWhereQueryParameter.toString())); //$NON-NLS-1$
	}

	private String resolveOslcPageSizeQueryParameter() {
		return (QUERY_PARAMETER_OSLC_PAGE_SIZE + "=" + pageSize); //$NON-NLS-1$
	}

	private String resolveBackLinkLabel(String resourceTypeUri, Resource resource) throws Exception {
		
		String backLinkLabel = null;

		Model model = resource.getModel();

		//Format: <QM resource numerical ID>: <QM resource title>
		if((RESOURCE_TYPE_URI_TEST_PLAN.equals(resourceTypeUri)) || (RESOURCE_TYPE_URI_TEST_CASE.equals(resourceTypeUri))){

			String testWebId = null;
			String testTitle = null;

			Statement shortIdPropertyStatement = model.getProperty(resource, model.createProperty(NAMESPACE_URI_OSLC, PROPERTY_SHORT_ID));

			if(shortIdPropertyStatement != null){
				testWebId = shortIdPropertyStatement.getString();
			}

			Statement titlePropertyStatement = model.getProperty(resource, model.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

			if(titlePropertyStatement != null){
				testTitle = titlePropertyStatement.getString();
			}

			if((testWebId != null) && (testWebId.length() > 0) && (testTitle != null) && (testTitle.length() > 0)){
				backLinkLabel = (testWebId + ": " + testTitle); //$NON-NLS-1$
			}
		}
		
		//Format: <QM resource numerical ID>: <QM resource title> [<QM test script step number>]
		else if(RESOURCE_TYPE_URI_TEST_SCRIPT_STEP.equals(resourceTypeUri)){

			String testScriptStepIndex = null;
			String testScriptWebId = null;
			String testScriptTitle = null;

			//Problem:  The test script step properties required to create the link label are only supported in RQM 6.0+ (see task-development 116971).
			//Solution: Use the RQM Reportable REST API to resolve the test script step properties required to create the link label.
			Statement testScriptStepRelationPropertyStatement = model.getProperty(resource, model.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_RELATION));

			if(testScriptStepRelationPropertyStatement != null){

				String testScriptStepRelationPropertyUri = testScriptStepRelationPropertyStatement.getResource().getURI();

				Document testScriptStepDocument = XmlUtils.read(httpClient, testScriptStepRelationPropertyUri);

				XPath xPath = XPathFactory.newInstance().newXPath();

				testScriptStepIndex = xPath.compile("/step/position").evaluate(testScriptStepDocument); //$NON-NLS-1$

				String testScriptUri = xPath.compile("/step/testscript/@href").evaluate(testScriptStepDocument); //$NON-NLS-1$

				Document testScriptDocument = XmlUtils.read(httpClient, testScriptUri);

				testScriptWebId = xPath.compile("/testscript/webId").evaluate(testScriptDocument); //$NON-NLS-1$

				testScriptTitle = xPath.compile("/testscript/title").evaluate(testScriptDocument); //$NON-NLS-1$
			}

			if((testScriptStepIndex != null) && (testScriptStepIndex.length() > 0) && (testScriptWebId != null) && (testScriptWebId.length() > 0) && (testScriptTitle != null) && (testScriptTitle.length() > 0)){
				backLinkLabel = (testScriptWebId + ": " + testScriptTitle + " [" + testScriptStepIndex + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		} 
		
		//Format: <RM resource numerical ID>: <RM resource title>
		else if((RESOURCE_TYPE_URI_REQUIREMENT.equals(resourceTypeUri)) || (RESOURCE_TYPE_URI_REQUIREMENT_COLLECTION.equals(resourceTypeUri))) {

			String requirementWebId = null;
			String requirementTitle = null;

			Statement idPropertyStatement = model.getProperty(resource, model.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_IDENTIFIER));

			if(idPropertyStatement != null){
				requirementWebId = idPropertyStatement.getString();
			}

			Statement titlePropertyStatement = model.getProperty(resource, model.createProperty(NAMESPACE_URI_DC_TERMS, PROPERTY_TITLE));

			if(titlePropertyStatement != null){
				requirementTitle = titlePropertyStatement.getString();
			}

			if((requirementWebId != null) && (requirementWebId.length() > 0) && (requirementTitle != null) && (requirementTitle.length() > 0)){
				backLinkLabel = (requirementWebId + ": " + requirementTitle); //$NON-NLS-1$
			}
		}

		return backLinkLabel;
	}

	private void resolveSelectsUris(String selectionsUri, List<String> selectsUris) throws IOException {
		
		LogUtils.logInfo("Reading TRS2 selections '" + selectionsUri + "'."); //$NON-NLS-1$ //$NON-NLS-2$

		Model selectionModel = RdfUtils.read(httpClient, selectionsUri);

		StmtIterator selectsStatementsIterator = selectionModel.listStatements(null, selectionModel.createProperty(NAMESPACE_URI_OSLC_CONFIG, PROPERTY_SELECTS), ((RDFNode)(null)));

		while(selectsStatementsIterator.hasNext()){

			Statement selectsProperty = selectsStatementsIterator.next();

			if(selectsProperty != null) {
				selectsUris.add(selectsProperty.getResource().getURI());
			}
		}

		//Resolve the selects URIs for the next selections page:
		String selectionsNextPageUri = resolveSelectionsNextPageUri(httpClient, selectionsUri);
		
		if(StringUtils.isSet(selectionsNextPageUri)) {
			resolveSelectsUris(selectionsNextPageUri, selectsUris);
		}
	}

	private String resolveSkippedResourceMessage(String skippedResourceUri, Exception exception) {

		StringBuilder message = new StringBuilder();

		if(exception instanceof HttpClientException) {

			HttpClientException httpClientException = ((HttpClientException)(exception));

			int returnCode = httpClientException.getReturnCode();

			message.append(returnCode);
			message.append(" ("); //$NON-NLS-1$
			message.append(returnCode);
			message.append(")\n"); //$NON-NLS-1$
			message.append(httpClientException.getMessage().trim());
		}
		else {
			message.append(exception.toString().trim());
		}

		return message.toString();
	}
	
	private class Link {

		private final String url;
		private final String label;

		public Link(String url, String label){

			this.url = url;
			this.label = label;
		}

		public String getUrl() {
			return url;
		}

		public String getLabel() {
			return label;
		}
	}
} 