/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.ENCODING_UTF8;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.LINE_SEPARATOR;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_ACCESS_CONTROL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_DC;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_DC_TERMS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_FOAF;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_H;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_JAZZ_CALM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_JAZZ_QM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_JAZZ_TRS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_JAZZ_TRS2;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_JFS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_JRS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_OSLC;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_OSLC_CM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_OSLC_CONFIG;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_OSLC_QM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_OSLC_RM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_OWL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_RDF;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_RDFS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_RM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_RM_TYPES;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_RM_WORKFLOW;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_XS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_CALM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_DC;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_DCTERMS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_FOAF;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_H;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_JAZZ_ACP;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_JAZZ_TRS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_JAZZ_TRS2;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_JFS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_JRS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_OSLC;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_OSLC_CM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_OSLC_CONFIG;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_OSLC_QM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_OSLC_RM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_OWL;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_RDF;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_RDFS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_RDM_RM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_RDM_RM_TYPES;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_RDM_RM_WORKFLOW;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_RQM_QM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PREFIX_XS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RDF_PROPERTY_SHOW_XML_DECLARATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RDF_PROPERTY_TAB;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RDF_PROPERTY_WIDTH;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RDF_XML_ABBREVIATED;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.XML_DECLARATION_ENCODING;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.RSIterator;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.ibm.icu.text.MessageFormat;
import com.ibm.rqm.cleaner.internal.client.OslcHttpClient;

/**
 * <p>Cleaner RDF utilities.</p>
 * 
 * <p>Note: Copied from <code>com.ibm.rqm.oslc.service</code> plug-in.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public final class RdfUtils {
	
	public static Model getDefaultModel(){

		Model defaultModel = ModelFactory.createDefaultModel();

		defaultModel.setNsPrefix(PREFIX_DCTERMS, NAMESPACE_URI_DC_TERMS); 
		defaultModel.setNsPrefix(PREFIX_DC, NAMESPACE_URI_DC); 
		defaultModel.setNsPrefix(PREFIX_OSLC_QM, NAMESPACE_URI_OSLC_QM); 
		defaultModel.setNsPrefix(PREFIX_OSLC_RM, NAMESPACE_URI_OSLC_RM); 
		defaultModel.setNsPrefix(PREFIX_OSLC_CM, NAMESPACE_URI_OSLC_CM); 
		defaultModel.setNsPrefix(PREFIX_OSLC, NAMESPACE_URI_OSLC); 
		defaultModel.setNsPrefix(PREFIX_OSLC_CONFIG, NAMESPACE_URI_OSLC_CONFIG); 
		defaultModel.setNsPrefix(PREFIX_RDF, NAMESPACE_URI_RDF); 
		defaultModel.setNsPrefix(PREFIX_RDFS, NAMESPACE_URI_RDFS); 
		defaultModel.setNsPrefix(PREFIX_CALM, NAMESPACE_URI_JAZZ_CALM); 
		defaultModel.setNsPrefix(PREFIX_FOAF, NAMESPACE_URI_FOAF); 
		defaultModel.setNsPrefix(PREFIX_RQM_QM, NAMESPACE_URI_JAZZ_QM); 
		defaultModel.setNsPrefix(PREFIX_JAZZ_ACP, NAMESPACE_URI_ACCESS_CONTROL); 
		defaultModel.setNsPrefix(PREFIX_JAZZ_TRS, NAMESPACE_URI_JAZZ_TRS); 
		defaultModel.setNsPrefix(PREFIX_JAZZ_TRS2, NAMESPACE_URI_JAZZ_TRS2); 
		defaultModel.setNsPrefix(PREFIX_RDM_RM, NAMESPACE_URI_RM); 
		defaultModel.setNsPrefix(PREFIX_RDM_RM_WORKFLOW, NAMESPACE_URI_RM_WORKFLOW); 
		defaultModel.setNsPrefix(PREFIX_JFS, NAMESPACE_URI_JFS); 
		defaultModel.setNsPrefix(PREFIX_H, NAMESPACE_URI_H); 
		defaultModel.setNsPrefix(PREFIX_XS, NAMESPACE_URI_XS); 
		defaultModel.setNsPrefix(PREFIX_RDM_RM_TYPES, NAMESPACE_URI_RM_TYPES); 
		defaultModel.setNsPrefix(PREFIX_OWL, NAMESPACE_URI_OWL); 
		defaultModel.setNsPrefix(PREFIX_JRS, NAMESPACE_URI_JRS); 
		
		return defaultModel;
	}

	/**
	 * <p>Resolves a copy of the source {@link Resource}.</p>
	 * 
	 * <p>The copy operation is a deep copy, including:</p>
	 * 
	 * <ul>
	 * <li>The resource URI.</li>
	 * <li>The resource type URI (see {@link RDF#type}).</li>
	 * <li>All statements.</li>
	 * <li>All reified statements.</li>
	 * <li>All properties of each reified statement.</li>
	 * </ul>
	 * 
	 * @param sourceResource The source {@link Resource}.
	 * @return A copy of the source {@link Resource}.
	 */
	public static Resource copy(Resource sourceResource){
		return (copy(getDefaultModel(), sourceResource));
	}
	
	/**
	 * <p>Resolves a copy of the source {@link Resource} in the {@link Model}.</p>
	 * 
	 * <p>The copy operation is a deep copy, including:</p>
	 * 
	 * <ul>
	 * <li>The resource URI.</li>
	 * <li>The resource type URI (see {@link RDF#type}).</li>
	 * <li>All statements.</li>
	 * <li>All reified statements.</li>
	 * <li>All properties of each reified statement.</li>
	 * </ul>
	 * 
	 * @param sourceResource The source {@link Resource}.
	 * @param destinationResourceModel The destination {@link Model}.
	 * @return A copy of the source {@link Resource}.
	 */
	public static Resource copy(Model destinationResourceModel, Resource sourceResource){
		
		Resource destinationResource = destinationResourceModel.createResource(sourceResource.getURI());
		destinationResource.addProperty(RDF.type, destinationResourceModel.createProperty(sourceResource.getRequiredProperty(RDF.type).getResource().getURI()));
		
		StmtIterator stmtIterator = sourceResource.getModel().listStatements(sourceResource, null, ((RDFNode)(null)));

		while(stmtIterator.hasNext()){

			Statement statement = stmtIterator.next();
			Property property = statement.getPredicate();

			if ((!RDF.type.equals(property)) && (!RDF.subject.equals(property)) && (!RDF.predicate.equals(property)) && (!RDF.object.equals(property))){

				//Add the statement:
				destinationResource.addProperty(property, statement.getObject());

				//Add the reified statement(s):
				if(statement.isReified()){

					RSIterator reifiedStatementsIterator = statement.getModel().listReifiedStatements(statement);

					while(reifiedStatementsIterator.hasNext()) {

						//Create a new reified statement:
						ReifiedStatement reifiedStatement = destinationResource.getModel().createReifiedStatement(statement);

						//Add all the properties to the new reified statement:
						StmtIterator reifiedStatementPropertiesIterator = reifiedStatementsIterator.next().listProperties();

						while (reifiedStatementPropertiesIterator.hasNext()) {

							Statement reifiedStatementProperty = reifiedStatementPropertiesIterator.next();

							reifiedStatement.addProperty(reifiedStatementProperty.getPredicate(), reifiedStatementProperty.getObject());
						}
					}
				}
			}
		}    		
		
		return destinationResource;
	}
	
	public static String write(List<Statement> statements){
		
		StringBuffer buffer = new StringBuffer();
		
		for(Statement statement : statements){
			
			if(buffer.length() > 0){
				buffer.append(", "); //$NON-NLS-1$
			}
			
			buffer.append(statement.getObject().toString().trim());
		}
		
		return (buffer.toString().trim());
	}
	
	
	/**
	 * <p>Serializes the RDF model to an output stream.</p>
	 * 
	 * <p>The resulting RDF model is serialized with:</p>
	 * 
	 * <ul>
	 * <li>the default <code>RDF/XML</code> serialization language,</li>
	 * <li>an XML declaration with <code>UTF-8</code> encoding, and </li>
	 * <li>one tab space of the serialized RDF model.</li>
	 * </ul>
	 * 
	 * @param model The RDF model to be serialized.
	 * @throws IOException If an I/O error occurs during serialization.
	 */
	public static String write(Model model) throws IOException{
		return (write(model, null));
	}
	
	/**
	 * <p>Serializes the RDF model to an output stream.</p>
	 * 
	 * <p>The resulting RDF model is serialized with:</p>
	 * 
	 * <ul>
	 * <li>an XML declaration with <code>UTF-8</code> encoding, and </li>
	 * <li>one tab space of the serialized RDF model.</li>
	 * </ul>
	 * 
	 * @param model The RDF model to be serialized.
	 * @param serializationLanguage The serialization language used to serialize the RDF model, or <code>null</code> for the default <code>RDF/XML</code> serialization language.
	 * @throws IOException If an I/O error occurs during serialization.
	 */
	public static String write(Model model, String serializationLanguage) throws IOException{
		return (write(model, serializationLanguage, true));
	}

	/**
	 * <p>Serializes the RDF model to a writer.</p>
	 * 
	 * <p>The resulting RDF model is serialized with:</p>
	 * 
	 * <ul>
	 * <li>one tab space of the serialized RDF model.</li>
	 * </ul>
	 * 
	 * @param model The RDF model to be serialized.
	 * @param serializationLanguage The serialization language used to serialize the RDF model, or <code>null</code> for the default <code>RDF/XML</code> serialization language.
	 * @param showXmlDeclaration <code>true</code> if an XML declaration with <code>UTF-8</code> encoding is serialized, otherwise <code>false</code>.
	 * @throws IOException If an I/O error occurs during serialization.
	 */
	public static String write(Model model, String serializationLanguage, boolean showXmlDeclaration) throws IOException{	
		
		//Resolve a print writer:
		StringWriter modelBuffer = new StringWriter();		
		
		//Write the XML declaration with an UTF-8 encoding:
		if(showXmlDeclaration){
			modelBuffer.append(MessageFormat.format(XML_DECLARATION_ENCODING, new Object[]{ENCODING_UTF8}));
			modelBuffer.append(LINE_SEPARATOR);
		}
		
		//Resolve the RDF writer:
		RDFWriter rdfWriter = model.getWriter(serializationLanguage);
		
		//Configure the RDF writer:	
		//Note: The error handler is configured first to handle errors in the remaining configuration.
		RdfUtilsErrorHandler rdfUtilsErrorHandler = new RdfUtilsErrorHandler();

		rdfWriter.setErrorHandler(rdfUtilsErrorHandler);

		//Note: Jena does not display the encoding in the XML declaration unless using an OutputStreamWriter with an encoding other than UTF-8/UTF-16.
		rdfWriter.setProperty(RDF_PROPERTY_SHOW_XML_DECLARATION, Boolean.FALSE.toString());

		//Note: Jena uses space characters instead of tab characters for indentation. 
		rdfWriter.setProperty(RDF_PROPERTY_TAB, String.valueOf(1));
				
		//Note: Jena inserts a newline at the specified column width. 
		rdfWriter.setProperty(RDF_PROPERTY_WIDTH, String.valueOf(Integer.MAX_VALUE));
		
		//Serialize the RDF model:
		rdfWriter.write(model, modelBuffer, null);
		
		//Handle configuration/serialization errors:
		Exception exception = rdfUtilsErrorHandler.getException();
		
		if(exception != null){
			throw new IOException(exception.getMessage());
		}
		
		return (modelBuffer.toString().trim());
	}
	
	public static void printModel(Model model, boolean isAbbreviated) throws IOException {
		
		if(isAbbreviated) {
			LogUtils.logInfo(RdfUtils.write(model, RDF_XML_ABBREVIATED));
		}
		else {
			LogUtils.logInfo(RdfUtils.write(model));			
		}
	}
	
	/**
	 * <p>Reads the RDF model from the HTTP GET response RDF/XML.</p>
	 * 
	 * <p>Note: The HTTP GET request absolute URI is used to convert relative URIs in the RDF model to absolute URIs to eliminate reading/writing errors.</p>
	 * 
	 * @param OslcHttpClient The {@link OslcHttpClient}.
	 * @param uri The HTTP GET request absolute URI.
	 * @throws IOException If an I/O error occurs during reading.
	 */
	public static Model read(OslcHttpClient oslcHttpClient, String uri) throws IOException {	
		return (read(oslcHttpClient, uri, null));
	}
	
	/**
	 * <p>Reads the RDF model from the HTTP GET response RDF/XML.</p>
	 * 
	 * <p>Note: The HTTP GET request absolute URI is used to convert relative URIs in the RDF model to absolute URIs to eliminate reading/writing errors.</p>
	 * 
	 * @param OslcHttpClient The {@link OslcHttpClient}.
	 * @param uri The HTTP GET request absolute URI.
	 * @param queryString The request query string, otherwise <code>null</code>.
	 * @throws IOException If an I/O error occurs during reading.
	 */
	public static Model read(OslcHttpClient oslcHttpClient, String uri, String queryString) throws IOException {	
		return (read(oslcHttpClient, uri, queryString, null));
	}
	
	/**
	 * <p>Reads the RDF model from the HTTP GET response RDF/XML.</p>
	 * 
	 * <p>Note: The HTTP GET request absolute URI is used to convert relative URIs in the RDF model to absolute URIs to eliminate reading/writing errors.</p>
	 * 
	 * @param OslcHttpClient The {@link OslcHttpClient}.
	 * @param uri The HTTP GET request absolute URI.
	 * @param queryString The request query string, otherwise <code>null</code>.
	 * @param mediaType The media type prepended to the <code>Accept</code> request header, otherwise <code>null</code>.
	 * @throws IOException If an I/O error occurs during reading.
	 */
	public static Model read(OslcHttpClient oslcHttpClient, String uri, String queryString, String mediaType) throws IOException {	

		long startTime = System.currentTimeMillis();
		
		InputStream inputStream = oslcHttpClient.get(uri, queryString, mediaType);

    	long endTime = System.currentTimeMillis();
    	
    	if(PerformanceUtils.isTraceEnabled()) {
			PerformanceUtils.logTrace("Read '" + UrlUtils.resolveUrl(uri, queryString) + "'" + (StringUtils.isSet(mediaType) ? " as '" + mediaType + "'" : ""), startTime, endTime); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    	}
    	
    	if(LogUtils.isTraceEnabled()) {
    		LogUtils.logTrace("Reading model from '" + UrlUtils.resolveUrl(uri, queryString) + "'" + (StringUtils.isSet(mediaType) ? " as '" + mediaType + "'." : ".")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    	}

		try{
			return (getDefaultModel().read(inputStream, uri));
		}
		catch(Exception e){

			String responseContent = StringUtils.toString(oslcHttpClient.get(uri, queryString, mediaType));
			
			LogUtils.logError("Error parsing HTTP GET " + uri + (StringUtils.isSet(queryString) ? ("?" + queryString) : "") + " response RDF/XML (status code: " + oslcHttpClient.head(uri, queryString) + ").  Response:" + (StringUtils.isSet(responseContent) ? (LINE_SEPARATOR + responseContent) : " <no response content>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

			throw new IOException(e);
		}
	}
	
	/**
	 * <p>Reads the RDF model from a local file containing RDF/XML content.</p>
	 * 
	 * @param file The {@link File}.
	 * @return RDF model read from a local file containing RDF/XML content.
	 * @throws IOException If an I/O error occurs during reading.
	 */
	public static Model read(File file) throws IOException {	

		try{
			return (getDefaultModel().read(new FileInputStream(file), null));
		}
		catch(Exception e){

			LogUtils.logError("Error reading and parsing RDF/XML content from file '" + file.getAbsolutePath() + "'.");  //$NON-NLS-1$ //$NON-NLS-2$

			throw new IOException(e);
		}
	}
	
	/**
	 * <p>RDF error handler to resolve the last handled error.</p>
	 * 
	 * <p>Note: Warnings are ignored.</p>
	 * 
	 *  
	 * @author  Paul Slauenwhite
	 * @version 0.9
	 * @since   0.9
	 * @see     RDFErrorHandler
	 */
	private static class RdfUtilsErrorHandler implements RDFErrorHandler{

		private Exception exception = null;
		
		public Exception getException() {
			return exception;
		}

		/* (non-Javadoc)
		 * @see com.hp.hpl.jena.rdf.model.RDFErrorHandler#warning(java.lang.Exception)
		 */
		@Override
		public void warning(Exception exception) {
			//Ignore warnings.
		}

		/* (non-Javadoc)
		 * @see com.hp.hpl.jena.rdf.model.RDFErrorHandler#error(java.lang.Exception)
		 */
		@Override
		public void error(Exception exception) {
			this.exception = exception;
		}
		
		/* (non-Javadoc)
		 * @see com.hp.hpl.jena.rdf.model.RDFErrorHandler#fatalError(java.lang.Exception)
		 */
		@Override
		public void fatalError(Exception exception) {
			this.exception = exception;
		}
	}
	
	public static String getPropertyResourceValue(Model model, String nameSpace, String localName) {

		StmtIterator statementIterator = model.listStatements(null, model.createProperty(nameSpace, localName), ((RDFNode)(null)));

		if(statementIterator.hasNext()){

			//Assumption: The RDF model contains only one resource property with the same name space and local name.
			Statement propertyStatement = statementIterator.next();

			if(propertyStatement != null) {
				return (propertyStatement.getResource().getURI());
			}
		}

		return null;		
	}
	
	public static String getPropertyStringValue(Model model, String nameSpace, String localName) {

		StmtIterator statementIterator = model.listStatements(null, model.createProperty(nameSpace, localName), ((RDFNode)(null)));

		if(statementIterator.hasNext()){

			//Assumption: The RDF model contains only one string property with the same name space and local name.
			Statement propertyStatement = statementIterator.next();

			if(propertyStatement != null) {
				return (propertyStatement.getString());
			}
		}

		return null;		
	}

	public static Boolean getPropertyBooleanValue(Model model, String nameSpace, String localName) {

		StmtIterator statementIterator = model.listStatements(null, model.createProperty(nameSpace, localName), ((RDFNode)(null)));

		if(statementIterator.hasNext()){

			//Assumption: The RDF model contains only one boolean property with the same name space and local name.
			Statement propertyStatement = statementIterator.next();

			if(propertyStatement != null) {
				return (propertyStatement.getBoolean());
			}
		}

		return null;		
	}

	//Note: Copied from com.ibm.rqm.oslc.tests.AbstractOslcTest.
	public static Resource getRootResource(Model model, String resourceTypeUri) throws Exception{

		if(resourceTypeUri != null){

			//Performance improvement for models with only one typed resource: 
			List<Statement> statements = model.listStatements(null, com.hp.hpl.jena.vocabulary.RDF.type, model.createResource(resourceTypeUri)).toList();

			//Assumption: The model has only one root resource.
			if(statements.size() == 1){

				Statement statement = statements.get(0);

				if(statement != null){

					Resource rootResource = statement.getSubject();

					if(rootResource != null){

						if(LogUtils.isTraceEnabled()) {
				    		LogUtils.logTrace("Resolved root resource with URI '" + resourceTypeUri + " from model."); //$NON-NLS-1$ //$NON-NLS-2$
				    	}

						return rootResource;
					}
				}
			}
		}

		List<Resource> rootResources = getRootResources(model);
		
		//Assumption: The model has only one root resource.
		if(rootResources.size() == 1) {

			if(LogUtils.isTraceEnabled()) {
	    		LogUtils.logTrace("Resolved root resource with no URI from model."); //$NON-NLS-1$
	    	}

			return (rootResources.get(0));
		}

		if(LogUtils.isTraceEnabled()) {
    		LogUtils.logTrace("Unresolved root resource with URI '" + resourceTypeUri + " from model."); //$NON-NLS-1$ //$NON-NLS-2$
    	}

		return null;
	}

	//Note: Copied from com.ibm.rqm.oslc.tests.AbstractOslcTest.
	public static List<Resource> getRootResources(Model model) throws Exception{

		List<Resource> rootResources = new ArrayList<Resource>();

		//Filter the following statements:
		//1) A subject of any other statement(s) in the model (reified statement).
		//2) A literal resource as the object of the statement.
		//3) A subject, predicate, or object as the predicate of the statement.
		com.hp.hpl.jena.util.iterator.Filter<Statement> filter = new com.hp.hpl.jena.util.iterator.Filter<Statement>() {

			@Override 
			public boolean accept(Statement statement) { 

				Property predicate = statement.getPredicate();

				return ((!statement.isReified()) && (statement.getObject().isResource()) && (!com.hp.hpl.jena.vocabulary.RDF.subject.equals(predicate)) && (!com.hp.hpl.jena.vocabulary.RDF.predicate.equals(predicate)) && (!com.hp.hpl.jena.vocabulary.RDF.object.equals(predicate)));
			} 
		};

		//Resolve the objects of the model:
		List<Resource> objects = new ArrayList<Resource>();
		ExtendedIterator<Statement> statementIterator = model.listStatements().filterKeep(filter);

		while (statementIterator.hasNext()) {

			//Assumption: All statements have an URI or anonymous resource as the object. 
			Resource resource =  statementIterator.next().getObject().asResource();

			//Maintain uniqueness:
			if(!objects.contains(resource)) {
				objects.add(resource);
			}
		}

		//Resolve the root resources of the model (resources that are not an object of any other statement(s) in the model):
		statementIterator = model.listStatements().filterKeep(filter);

		while (statementIterator.hasNext()) {

			Resource subject = statementIterator.next().getSubject();

			if(!objects.contains(subject)){

				//Maintain uniqueness:
				if(!rootResources.contains(subject)) {
					rootResources.add(subject);
				}
			}
		}

		return rootResources;
	}
}
