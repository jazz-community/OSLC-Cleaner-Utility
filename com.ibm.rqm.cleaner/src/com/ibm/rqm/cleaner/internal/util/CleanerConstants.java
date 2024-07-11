/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

import java.text.MessageFormat;
import java.util.regex.Pattern;

/**
 * <p>Cleaner constants.</p>
 * 
 * <p>Note: Copied from <code>com.ibm.rqm.oslc.service</code> and <code>com.ibm.rqm.oslc.common</code> plug-ins.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public final class CleanerConstants {

	private CleanerConstants() {
		//No-operation.
	}

	//Delimiters:
	public static final String DELIMITER_COMPOUND_ARGUMENT_VALUE = "::";  //$NON-NLS-1$	
	
	//OSLC query parameters:
	public static final String QUERY_PARAMETER_OSLC_PROPERTIES = "oslc.properties";  //$NON-NLS-1$	
	public static final String QUERY_PARAMETER_OSLC_PREFIX = "oslc.prefix";  //$NON-NLS-1$	
	public static final String QUERY_PARAMETER_OSLC_WHERE = "oslc.where";  //$NON-NLS-1$	
	public static final String QUERY_PARAMETER_OSLC_PAGE_SIZE = "oslc.pageSize";  //$NON-NLS-1$	

	//OSLC default values:
	public static final int OSLC_DEFAULT_VALUE_PAGE_SIZE = 512;	

	//OSLC request parameters:
	public static final String REQUEST_PARAMETER_OSLC_CONFIG_CONTEXT = "oslc_config.context"; //$NON-NLS-1$

	//OSLC (config):
	public static final String SELECTIONS_URI_LAST_SEGMENT = "/selections";  //$NON-NLS-1$

	//Directories:
	//Note: Update the description of readTrs2/validateTrs2/validateTrs2NoSkippedResources commands if these directory names changed.
	public static final String TRS2_DIRECTORY_NAME = "TRS2"; //$NON-NLS-1$
	public static final String TRS2_CHANGE_LOG_PAGES_DIRECTORY_NAME = "Change_Log_Pages"; //$NON-NLS-1$
	public static final String TRS2_BASE_PAGES_DIRECTORY_NAME = "Base_Pages"; //$NON-NLS-1$
	public static final String TRS2_SELECTIONS_DIRECTORY_NAME = "Selections"; //$NON-NLS-1$
	public static final String TRS2_SELECTIONS_PAGES_DIRECTORY_NAME = "Selections_Pages_{0}"; //$NON-NLS-1$
	public static final String LQE_TRS2_DIRECTORY_NAME = "trs"; //$NON-NLS-1$
	public static final String LQE_TRS2_CHANGE_LOG_PAGES_DIRECTORY_NAME = "Change Log Pages"; //$NON-NLS-1$
	public static final String LQE_TRS2_BASE_PAGES_DIRECTORY_NAME = "Base Pages"; //$NON-NLS-1$
	public static final String OSLC_DIRECTORY_NAME = "OSLC"; //$NON-NLS-1$

	//Files:
	public static final String CHANGE_LOG_PAGE_FILE_NAME_PREFIX = "change_log_page_"; //$NON-NLS-1$
	public static final String BASE_PAGE_FILE_NAME_PREFIX = "base_page_"; //$NON-NLS-1$
	public static final String SELECTIONS_PAGE_FILE_NAME_PREFIX = "selections_page_"; //$NON-NLS-1$
	public static final String LQE_CHANGE_LOG_PAGE_FILE_NAME_SEGMENT = "_changeLog"; //$NON-NLS-1$
	public static final String LQE_BASE_PAGE_FILE_NAME_SEGMENT = "_base"; //$NON-NLS-1$
	public static final String RESOURCE_PAGE_FILE_NAME_SEGMENT = "_page_"; //$NON-NLS-1$
	
	//File extensions:
	public static final String XML_FILE_EXTENSION = ".xml"; //$NON-NLS-1$
	public static final String TXT_FILE_EXTENSION = ".txt"; //$NON-NLS-1$

	//Service URI aliases:
	public static final String SERVICE_URI_ALIAS_TRS2 = "trs2"; //$NON-NLS-1$
	
	//Cleaning commands:
	public static final String CLEANING_COMMAND_REMOVE_LINK_CONFIG_CONTEXT = "removeLinkConfigContext"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_READ_ALL_RESOURCES = "readAllResources"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_REMOVE_BROKEN_FORWARD_LINKS = "removeBrokenForwardLinks"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_REMOVE_ALL_FORWARD_LINKS = "removeAllForwardLinks"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_REMOVE_BROKEN_FORWARD_LINKS_WITH_MISSING_BACK_LINKS = "removeBrokenForwardLinksWithMissingBackLinks"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_REPORT_BROKEN_LINKS = "reportBrokenLinks"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_ADD_MISSING_BACK_LINKS = "addMissingBackLinks"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_UPDATE_LINK_LABELS = "updateLinkLabels"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_UPDATE_FORWARD_LINK_LABELS = "updateForwardLinkLabels"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_UPDATE_BACK_LINK_LABELS = "updateBackLinkLabels"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_UPDATE_VERSIONED_LINKS = "updateVersionedLinks"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_RENAME_LINKS = "renameLinks"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_READ_TRS2 = "readTrs2"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_READ_TRS2_WITH_SELECTIONS = "readTrs2WithSelections"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_VALIDATE_TRS2 = "validateTrs2"; //$NON-NLS-1$
	public static final String CLEANING_COMMAND_VALIDATE_TRS2_NO_SKIPPED_RESOURCES = "validateTrs2NoSkippedResources"; //$NON-NLS-1$
	
	//TRS2 change events types:
	public static final String CHANGE_EVENT_TYPE_CREATION = "http://open-services.net/ns/core/trs#Creation"; //$NON-NLS-1$
	public static final String CHANGE_EVENT_TYPE_MODIFICATION = "http://open-services.net/ns/core/trs#Modification"; //$NON-NLS-1$
	public static final String CHANGE_EVENT_TYPE_DELETION = "http://open-services.net/ns/core/trs#Deletion"; //$NON-NLS-1$

	//TRS2 provider types:
	public static final String TRS2_PROVIDER_TYPE_QM = "http://open-services.net/ns/qm#"; //$NON-NLS-1$
	public static final String TRS2_PROVIDER_TYPE_QM_PROCESS = "http://jazz.net/ns/process#"; //$NON-NLS-1$

	//RRC:
	public static final String RRC_ARTIFACTCONVERTER_URL = "artifactConverter"; //$NON-NLS-1$
	public static final String RRC_BASE_URL = "resources"; //$NON-NLS-1$
	public static final String RRC_QUERY_URL = "query"; //$NON-NLS-1$
	public static final String RRC_MULTI_FETCH_URL = "multi-fetch"; //$NON-NLS-1$
	public static final String RRC_STOREDQUERY_URL = "storedquery"; //$NON-NLS-1$
	public static final String RRC_CALM_QUERY_URL = "calmquery"; //$NON-NLS-1$
	public static final String RRC_SPARQL_QUERY_URL = "sparqlquery"; //$NON-NLS-1$
	public static final String RRC_FOLDERS_URL = "folders"; //$NON-NLS-1$
	public static final String RRC_TAGS_URL = "tags"; //$NON-NLS-1$
	public static final String RRC_PROJECTS_URL = "projects"; //$NON-NLS-1$
	public static final String RRC_FRIENDS_URL = "friends"; //$NON-NLS-1$
	public static final String RRC_PROJECTRESOURCES_URL = "project-resources"; //$NON-NLS-1$
	public static final String RRC_PROJECTTEMPLATES_URL = "projectTemplates"; //$NON-NLS-1$
	public static final String RRC_COMMENTS_URL = "comments"; //$NON-NLS-1$
	public static final String RRC_WRAPPER_RESOURCE_URL = "wrapper-resources"; //$NON-NLS-1$
	public static final String RRC_WRAPPED_RESOURCE_URL = "wrappedResources"; //$NON-NLS-1$
	public static final String RRC_BINARY_RESOURCE_URL = "binary"; //$NON-NLS-1$
	public static final String RRC_REVISIONS_URL = "revisions"; //$NON-NLS-1$
	public static final String RRC_LINK_TYPES_URL = "linkTypes"; //$NON-NLS-1$
	public static final String RRC_LINKS_URL = "links"; //$NON-NLS-1$
	public static final String RRC_LINKS_20_URL = "links"; //$NON-NLS-1$
	public static final String RRC_TEMPLATES_URL = "templates"; //$NON-NLS-1$
	public static final String RRC_RECENTFEEDS_URL = "recentfeeds"; //$NON-NLS-1$
	public static final String RRC_DISCOVERY_URL = "discovery"; //$NON-NLS-1$
	public static final String RRC_REVIEWS_URL = "reviews"; //$NON-NLS-1$
	public static final String RRC_REVIEW_RESULTS_URL = "reviews/results"; //$NON-NLS-1$
	public static final String RRC_OPERATIONS_URL = "operations"; //$NON-NLS-1$
	public static final String RRC_PROCESS_SECURITY_URL = "process-security"; //$NON-NLS-1$
	public static final String RRC_BASELINES_URL = "baselines"; //$NON-NLS-1$
	public static final String RRC_MULTI_REQUEST_URL = "multi-request"; //$NON-NLS-1$
	public static final String RRC_MAIL_URL = "mail"; //$NON-NLS-1$
	public static final String RRC_LOGS_URL = "logs"; //$NON-NLS-1$
	public static final String RRC_IMPORT_URL = "import"; //$NON-NLS-1$
	public static final String RRC_EXPORT_URL = "export"; //$NON-NLS-1$
	public static final String RRC_TYPES_URL = "types"; //$NON-NLS-1$
	public static final String RRC_VIEWS_URL = "views"; //$NON-NLS-1$
	public static final String RRC_PROXY_URL = "proxy?uri="; //$NON-NLS-1$
	public static final String RRC_REQUEST_VALIDATION_URL = "validation/request"; //$NON-NLS-1$
	public static final String RRC_RESPONSE_VALIDATION_URL = "validation/response"; //$NON-NLS-1$
	public static final String RRC_MODULES_URL = "modules"; //$NON-NLS-1$
	public static final String RRC_GLOSSARY_URL = "glossary"; //$NON-NLS-1$
	
	//Media types:
	public static final String MEDIA_TYPE_APPLICATION_XML = "application/xml"; //$NON-NLS-1$
	public static final String MEDIA_TYPE_APPLICATION_XHTML = "application/xhtml+xml"; //$NON-NLS-1$
	public static final String MEDIA_TYPE_RDF_XML = "application/rdf+xml"; //$NON-NLS-1$
	public static final String MEDIA_TYPE_RDF_XML_APPLICATION_XML = MEDIA_TYPE_RDF_XML + "," + MEDIA_TYPE_APPLICATION_XML + ";q=0.5"; //$NON-NLS-1$ //$NON-NLS-2$
	public static final String MEDIA_TYPE_APPLICATION_X_WWW_FORM_URL_ENCODED_UTF_8 = "application/x-www-form-urlencoded;charset=UTF-8"; //$NON-NLS-1$
	
	/**
	 * @deprecated As of Rational Quality Manager 5.0, the OSLC Quality Management V1.0 service provider is not supported or tested.  Please use the OSLC Quality Management V2.0 service provider.
	 */
	public static final String MEDIA_TYPE_OSLC_TEST_CASE_V1 = "application/x-oslc-qm-testcase-1.0+xml"; //$NON-NLS-1$
	
	/**
	 * @deprecated As of Rational Quality Manager 5.0, the OSLC Quality Management V1.0 service provider is not supported or tested.  Please use the OSLC Quality Management V2.0 service provider.
	 */
	public static final String MEDIA_TYPE_OSLC_TEST_PLAN_V1 = "application/x-oslc-qm-testplan-1.0+xml"; //$NON-NLS-1$
	
	/**
	 * @deprecated As of Rational Quality Manager 5.0, the OSLC Quality Management V1.0 service provider is not supported or tested.  Please use the OSLC Quality Management V2.0 service provider.
	 */
	public static final String MEDIA_TYPE_OSLC_QM_SERVICE_PROVIDER_CATALOG_V1 = "application/x-oslc-disc-service-provider-catalog+xml"; //$NON-NLS-1$
	
	/**
	 * @deprecated As of Rational Quality Manager 5.0, the OSLC Quality Management V1.0 service provider is not supported or tested.  Please use the OSLC Quality Management V2.0 service provider.
	 */
	public static final String MEDIA_TYPE_OSLC_QM_SERVICE_DESCRIPTION_V1 = "application/x-oslc-qm-service-description+xml"; //$NON-NLS-1$
	public static final String MEDIA_TYPE_OSLC_QM_RESOURCE_LEGACY = "application/x-oslc-qm-resource+xml"; //$NON-NLS-1$
	public static final String MEDIA_TYPE_UNKNOWN_PROXY = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"; //$NON-NLS-1$	
	public static final String MEDIA_TYPE_WILDCARD = "*/*"; //$NON-NLS-1$
	public static final String MEDIA_TYPE_APPLICATION_WILDCARD = "application/*"; //$NON-NLS-1$
	public static final String MEDIA_TYPE_TEXT_WILDCARD = "text/*"; //$NON-NLS-1$
	public static final String MEDIA_TYPE_TEXT_JSON = "text/json"; //$NON-NLS-1$
	public static final String MEDIA_TYPE_TEXT_JSON_UTF_8 = "text/json;charset=UTF-8"; //$NON-NLS-1$
	public static final String MEDIA_TYPE_TEXT_XML = "text/xml"; //$NON-NLS-1$
	public static final String MEDIA_TYPE_TEXT_HTML = "text/html"; //$NON-NLS-1$
	public static final String MEDIA_TYPE_ATOM = "application/atom+xml"; //$NON-NLS-1$
	public static final String MEDIA_TYPE_JAZZ_COMPACT_RENDERING = "application/x-jazz-compact-rendering"; //$NON-NLS-1$
	public static final String MEDIA_TYPE_OSLC_COMPACT = "application/x-oslc-compact+xml"; //$NON-NLS-1$
	
	//Formatting:
	public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\\n"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final String FORWARD_SLASH = "/"; //$NON-NLS-1$
	public static final String ISO_8601_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //$NON-NLS-1$
	public static final String RFC_3339_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; //$NON-NLS-1$

	//OSLC:
	public static final String OSLC_VERSION = "2.0"; //$NON-NLS-1$

	//Jazz:
	public static final String ROOT_SERVICES = "rootservices"; //$NON-NLS-1$
	
	//DWA:
	public static final String DWA_OAUTH_REALM_NAME = "DWA"; //$NON-NLS-1$
	
	//HTTP (methods):	
	public static final String HTTP_METHOD_HEAD = "HEAD"; //$NON-NLS-1$
	public static final String HTTP_METHOD_GET = "GET"; //$NON-NLS-1$
	public static final String HTTP_METHOD_PUT = "PUT"; //$NON-NLS-1$
	public static final String HTTP_METHOD_POST = "POST"; //$NON-NLS-1$
	public static final String HTTP_METHOD_DELETE = "DELETE"; //$NON-NLS-1$

	//HTTP (header values):
	public static final String HTTP_HEADER_VALUE_PRIVATE = "private"; //$NON-NLS-1$

	//HTTP (headers):
	public static final String HTTP_HEADER_ETAG = "ETag"; //$NON-NLS-1$
	public static final String HTTP_HEADER_IF_MATCH = "If-Match"; //$NON-NLS-1$
	public static final String HTTP_HEADER_ACCEPT = "Accept"; //$NON-NLS-1$
	public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type"; //$NON-NLS-1$
	public static final String HTTP_HEADER_LOCATION = "Location"; //$NON-NLS-1$
	public static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length"; //$NON-NLS-1$
	public static final String HTTP_HEADER_OSLC_CORE_VERSION = "OSLC-Core-Version"; //$NON-NLS-1$
	public static final String HTTP_HEADER_SINGLE_COOKIE_HEADER = "http.protocol.single-cookie-header"; //$NON-NLS-1$
	public static final String HTTP_HEADER_CONTENT_ENCODING = "Content-Encoding"; //$NON-NLS-1$
	public static final String HTTP_HEADER_ACCEPT_ENCODING = "Accept-Encoding"; //$NON-NLS-1$
	public static final String HTTP_HEADER_CONTENT_LOCATION = "content-location"; //$NON-NLS-1$
	public static final String HTTP_HEADER_AUTHENTICATION = "X-com-ibm-team-repository-web-auth-msg"; //$NON-NLS-1$
	public static final String HTTP_HEADER_NO_RETRY = "X-com-ibm-team-repository.common.remoteaccess.noRetry"; //$NON-NLS-1$
	public static final String HTTP_HEADER_OAUTH_TOKEN = "oauth_token"; //$NON-NLS-1$
	public static final String HTTP_HEADER_OAUTH_VERIFIER = "oauth_verifier"; //$NON-NLS-1$  
	public static final String HTTP_HEADER_OAUTH_CALLBACK = "oauth_callback"; //$NON-NLS-1$
	public static final String HTTP_HEADER_OAUTH_AUTHORIZE = "authorize"; //$NON-NLS-1$
	public static final String HTTP_HEADER_COOKIE = "Cookie"; //$NON-NLS-1$
	public static final String HTTP_HEADER_SET_COOKIE = "Set-Cookie"; //$NON-NLS-1$
	public static final String HTTP_HEADER_JSESSIONID = "JSESSIONID"; //$NON-NLS-1$
	public static final String HTTP_HEADER_JAZZ_WEB_OAUTH = "X-jazz-web-oauth-url"; //$NON-NLS-1$
	public static final String HTTP_HEADER_JAZZ_FORM_OAUTH = "JazzFormAuth"; //$NON-NLS-1$
	public static final String HTTP_HEADER_REQUEST_URI = "Request-URI"; //$NON-NLS-1$
	public static final String HTTP_HEADER_WWW_AUTHENTICATE = "WWW-Authenticate"; //$NON-NLS-1$
	public static final String HTTP_HEADER_LINK = "Link"; //$NON-NLS-1$
	public static final String HTTP_HEADER_CONFIGURATION_CONTEXT = "Configuration-Context"; //$NON-NLS-1$
	public static final String HTTP_HEADER_ACCEPT_LANGUAGE = "Accept-Language"; //$NON-NLS-1$
	public static final String HTTP_HEADER_DOORS_RP_REQUEST_TYPE = "DoorsRP-Request-Type"; //$NON-NLS-1$
	public static final String HTTP_HEADER_OSLC_CONFIGURATION = "oslc.configuration"; //$NON-NLS-1$
	public static final String HTTP_HEADER_NET_JAZZ_JFS_OWNING_CONTEXT = "net.jazz.jfs.owning-context"; //$NON-NLS-1$
	public static final String HTTP_HEADER_X_RM_QUERY_TIMEOUT_ENABLE = "X-RM-Query-Timeout-Enable"; //$NON-NLS-1$

    //HTTP (constants):
	public static final String HTTP_CONSTANTS_FORM = "FORM"; //$NON-NLS-1$
	public static final String HTTP_CONSTANTS_BASIC = "BASIC"; //$NON-NLS-1$
	public static final String HTTP_CONSTANTS_CHARSET = "charset"; //$NON-NLS-1$

    //HTTP (header values):
	public static final String HTTP_HEADER_VALUE_AUTHENTICATION_REQUIRED = "authrequired"; //$NON-NLS-1$
	public static final String HTTP_HEADER_VALUE_AUTHENTICATION_FAILED = "authfailed"; //$NON-NLS-1$
    
	//Protocols:
	public static final String PROTOCOL_HTTPS = "https"; //$NON-NLS-1$
	public static final String PROTOCOL_HTTP = "http"; //$NON-NLS-1$
	
    //Jazz (repository roles):
	public static final String JAZZ_REPOSITORY_ROLE_GUESTS = "JazzGuests"; //$NON-NLS-1$
	public static final String JAZZ_REPOSITORY_ROLE_DWADMINS = "JazzDWAdmins"; //$NON-NLS-1$
	public static final String JAZZ_REPOSITORY_ROLE_USERS = "JazzUsers"; //$NON-NLS-1$
	public static final String JAZZ_REPOSITORY_ROLE_PROJECTADMINS = "JazzProjectAdmins"; //$NON-NLS-1$
	public static final String JAZZ_REPOSITORY_ROLE_ADMINS = "JazzAdmins"; //$NON-NLS-1$

    //Jazz (uri):
	public static final String JAZZ_URI_FORM_AUTHENTICATION = "j_security_check"; //$NON-NLS-1$
	public static final String JAZZ_URI_FORM_AUTHENTICATION_ACEGI = "j_acegi_security_check"; //$NON-NLS-1$
	public static final String JAZZ_URI_FORM_OAUTHENTICATION = "oauth-authorize"; //$NON-NLS-1$
	public static final String JAZZ_URI_FORM_LOGOUT = "auth/logout"; //$NON-NLS-1$
	public static final String JAZZ_URI_FORM_AUTHENTICATION_FAILED = "auth/authfailed"; //$NON-NLS-1$
	public static final String JAZZ_URI_FORM_AUTHENTICATION_REQUIRED = "auth/authrequired"; //$NON-NLS-1$
	public static final String JAZZ_URI_FORM_IDENITY = "authenticated/identity"; //$NON-NLS-1$
    
    //Jazz (fields):
	public static final String JAZZ_FIELD_USERNAME = "j_username"; //$NON-NLS-1$
	public static final String JAZZ_FIELD_PASSWORD = "j_password"; //$NON-NLS-1$

	//Encodings:
	public static final String ENCODING_UTF8 = "UTF-8"; //$NON-NLS-1$
	public static final String ENCODING_UTF16 = "UTF-16"; //$NON-NLS-1$
	public static final String ENCODING_GZIP = "gzip";   //$NON-NLS-1$
	public static final String ENCODING_ISO_8859_1 = "ISO-8859-1";   //$NON-NLS-1$

	//XML:
	/**
	 * <p>The template for the XML declaration.</p>
	 * 
	 * <p>Substitution tokens (<code>{&lt;zero-based token index&gt;}</code>) mappings:</p>
	 * 
	 * <ul>
	 * <li>{0}: The encoding.</li>
	 * </ul>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;</code></p>
	 * 
	 * @see java.text.MessageFormat#format(String, Object[])
	 * @see #ENCODING_UTF8
	 */
	public static final String XML_DECLARATION_ENCODING = "<?xml version=\"1.0\" encoding=\"{0}\"?>"; //$NON-NLS-1$
	
	//RDF (languages):
	public static final String RDF_XML_ABBREVIATED = "RDF/XML-ABBREV"; //$NON-NLS-1$

	//RDF (properties):
	/**
	 * <p>If <code>false</code>, an XML declaration is not included with the serialized RDF model.  
	 * If <code>true</code>, the XML declaration without the encoding is included with the serialized 
	 * RDF model.  If not set (default) or <code>true</code> and the {@link java.io.OutputStreamWriter} uses an 
	 * encoding other than UTF-8/UTF-16, the XML declaration with the encoding is included with the 
	 * serialized RDF model.<p> 
	 */
	public static final String RDF_PROPERTY_SHOW_XML_DECLARATION = "showXmlDeclaration"; //$NON-NLS-1$
	
	/**
	 * <p>Number of indent spaces of the serialized RDF model (default: 0).</p>
	 * 
	 * <p>Note: This property in only supported when using the abbreviated XML 
	 * (see {@link #RDF_XML_ABBREVIATED}) syntax.</p>
	 */
	public static final String RDF_PROPERTY_INDENT = "indent"; //$NON-NLS-1$

	/**
	 * <p>Number of tab spaces of the serialized RDF model (default: 2).</p>
	 */
	public static final String RDF_PROPERTY_TAB = "tab"; //$NON-NLS-1$
	
	/**
	 * <p>Number of characters before inserting a newline character (default: 60).</p>
	 */
	public static final String RDF_PROPERTY_WIDTH = "width"; //$NON-NLS-1$	
	
	//Prefixes:
	public static final String PREFIX_DC = "dc"; //$NON-NLS-1$
	public static final String PREFIX_DCTERMS = "dcterms"; //$NON-NLS-1$
	public static final String PREFIX_FOAF = "foaf"; //$NON-NLS-1$
	public static final String PREFIX_RQM_QM = "rqm_qm"; //$NON-NLS-1$
	public static final String PREFIX_RTC_CM = "rtc_cm"; //$NON-NLS-1$
	public static final String PREFIX_OSLC = "oslc"; //$NON-NLS-1$
	public static final String PREFIX_OSLC_CM = "oslc_cm"; //$NON-NLS-1$
	public static final String PREFIX_OSLC_QM = "oslc_qm"; //$NON-NLS-1$
	public static final String PREFIX_OSLC_RM = "oslc_rm"; //$NON-NLS-1$
	public static final String PREFIX_OSLC_DISC = "oslc_disc"; //$NON-NLS-1$
	public static final String PREFIX_OSLC_CONFIG = "oslc_config"; //$NON-NLS-1$
	public static final String PREFIX_JAZZ_TRS = "trs"; //$NON-NLS-1$
	public static final String PREFIX_JAZZ_TRS2 = "trs2"; //$NON-NLS-1$
	public static final String PREFIX_JAZZ_ACP = "acp"; //$NON-NLS-1$
	public static final String PREFIX_CALM = "calm"; //$NON-NLS-1$
	public static final String PREFIX_RDF = "rdf"; //$NON-NLS-1$
	public static final String PREFIX_RDFS = "rdfs"; //$NON-NLS-1$
	public static final String PREFIX_JFS = "jfs"; //$NON-NLS-1$
	public static final String PREFIX_JPRES = "jpres"; //$NON-NLS-1$
	public static final String PREFIX_JPROC = "jproc"; //$NON-NLS-1$
	public static final String PREFIX_XML = "xml"; //$NON-NLS-1$
	public static final String PREFIX_XMLNS = "xmlns"; //$NON-NLS-1$
	public static final String PREFIX_RDM_RM = "rm"; //$NON-NLS-1$
	public static final String PREFIX_RDM_RM_WORKFLOW = "rmWorkflow"; //$NON-NLS-1$
	public static final String PREFIX_H = "h"; //$NON-NLS-1$
	public static final String PREFIX_XS = "xs"; //$NON-NLS-1$
	public static final String PREFIX_RDM_RM_TYPES = "rmTypes"; //$NON-NLS-1$
	public static final String PREFIX_OWL = "owl"; //$NON-NLS-1$
	public static final String PREFIX_JRS = "jrs"; //$NON-NLS-1$
	
	//Properties:
	public static final String PROPERTY_WILDCARD = "*";  //$NON-NLS-1$
	public static final String PROPERTY_CONTRIBUTOR = "contributor"; //$NON-NLS-1$
	public static final String PROPERTY_NAME = "name"; //$NON-NLS-1$
	public static final String PROPERTY_MBOX = "mbox"; //$NON-NLS-1$
	public static final String PROPERTY_NICK = "nick"; //$NON-NLS-1$
	public static final String PROPERTY_CREATED = "created"; //$NON-NLS-1$
	public static final String PROPERTY_CREATOR = "creator"; //$NON-NLS-1$
	public static final String PROPERTY_DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String PROPERTY_IDENTIFIER = "identifier"; //$NON-NLS-1$
	public static final String PROPERTY_MODIFIED = "modified"; //$NON-NLS-1$
	public static final String PROPERTY_CATEGORY = "category"; //$NON-NLS-1$
	public static final String PROPERTY_TITLE = "title"; //$NON-NLS-1$
	public static final String PROPERTY_SHORT_ID = "shortId"; //$NON-NLS-1$
	public static final String PROPERTY_RELATION = "relation"; //$NON-NLS-1$
	public static final String PROPERTY_INCLUDED_IN_TEST_SCRIPT = "includedInTestScript"; //$NON-NLS-1$
	public static final String PROPERTY_INDEX = "index"; //$NON-NLS-1$
	public static final String PROPERTY_INSTANCE_SHAPE = "instanceShape"; //$NON-NLS-1$	
	public static final String PROPERTY_SERVICE_PROVIDER = "serviceProvider"; //$NON-NLS-1$
	public static final String PROPERTY_RELATED_CHANGE_REQUEST = "relatedChangeRequest"; //$NON-NLS-1$
	public static final String PROPERTY_TESTS_DEVELOPMENT_PLAN = "testsDevelopmentPlan"; //$NON-NLS-1$
	public static final String PROPERTY_TESTED_BY_TEST_PLAN = "testedByTestPlan"; //$NON-NLS-1$
	public static final String PROPERTY_USES_TEST_CASE = "usesTestCase"; //$NON-NLS-1$
	public static final String PROPERTY_VALIDATES_REQUIREMENT_COLLECTION = "validatesRequirementCollection"; //$NON-NLS-1$
	public static final String PROPERTY_TESTS_CHANGE_REQUEST = "testsChangeRequest"; //$NON-NLS-1$
	public static final String PROPERTY_USES_TEST_SCRIPT = "usesTestScript"; //$NON-NLS-1$
	public static final String PROPERTY_EXECUTION_INSTRUCTIONS = "executionInstructions"; //$NON-NLS-1$
	public static final String PROPERTY_VALIDATES_REQUIREMENT = "validatesRequirement"; //$NON-NLS-1$
	public static final String PROPERTY_BLOCKED_BY_CHANGE_REQUEST = "blockedByChangeRequest"; //$NON-NLS-1$
	public static final String PROPERTY_RUNS_ON_TEST_ENVIRONMENT = "runsOnTestEnvironment"; //$NON-NLS-1$
	public static final String PROPERTY_REPORTS_ON_TEST_PLAN = "reportsOnTestPlan"; //$NON-NLS-1$
	public static final String PROPERTY_RUNS_TEST_CASE = "runsTestCase"; //$NON-NLS-1$
	public static final String PROPERTY_STATUS = "status"; //$NON-NLS-1$
	public static final String PROPERTY_AFFECTED_BY_CHANGE_REQUEST = "affectedByChangeRequest"; //$NON-NLS-1$
	public static final String PROPERTY_EXECUTES_TEST_SCRIPT = "executesTestScript"; //$NON-NLS-1$
	public static final String PROPERTY_PRODUCED_BY_TEST_EXECUTION_RECORD = "producedByTestExecutionRecord"; //$NON-NLS-1$
	public static final String PROPERTY_REPORTS_ON_TEST_CASE = "reportsOnTestCase"; //$NON-NLS-1$
	public static final String PROPERTY_NEXT_PAGE = "nextPage"; //$NON-NLS-1$
	public static final String PROPERTY_TOTAL_COUNT = "totalCount"; //$NON-NLS-1$
	public static final String PROPERTY_SCORE = "score"; //$NON-NLS-1$
	public static final String PROPERTY_DESCRIBES = "describes"; //$NON-NLS-1$
	public static final String PROPERTY_PROPERTY = "property"; //$NON-NLS-1$
	public static final String PROPERTY_ALLOWED_VALUES = "allowedValues"; //$NON-NLS-1$
	public static final String PROPERTY_ALLOWED_VALUE = "allowedValue"; //$NON-NLS-1$
	public static final String PROPERTY_DEFAULT_VALUE = "defaultValue"; //$NON-NLS-1$
	public static final String PROPERTY_HIDDEN = "hidden"; //$NON-NLS-1$
	public static final String PROPERTY_IS_MEMBER_PROPERTY = "isMemberProperty"; //$NON-NLS-1$
	public static final String PROPERTY_MAX_SIZE = "maxSize"; //$NON-NLS-1$
	public static final String PROPERTY_OCCURS = "occurs"; //$NON-NLS-1$	
	public static final String PROPERTY_PROPERTY_DEFINITION = "propertyDefinition"; //$NON-NLS-1$
	public static final String PROPERTY_RANGE = "range"; //$NON-NLS-1$
	public static final String PROPERTY_READ_ONLY = "readOnly"; //$NON-NLS-1$
	public static final String PROPERTY_REPRESENTATION = "representation"; //$NON-NLS-1$
	public static final String PROPERTY_VALUE_TYPE = "valueType"; //$NON-NLS-1$
	public static final String PROPERTY_VALUE_SHAPE = "valueShape"; //$NON-NLS-1$
	public static final String PROPERTY_TYPE = "type"; //$NON-NLS-1$
	public static final String PROPERTY_CUTOFF_EVENT = "cutoffEvent"; //$NON-NLS-1$
	public static final String PROPERTY_MEMBER = "member"; //$NON-NLS-1$
	public static final String PROPERTY_BASE = "base"; //$NON-NLS-1$
	public static final String PROPERTY_PAGE_NUM = "pageNum"; //$NON-NLS-1$
	public static final String PROPERTY_DATE = "date"; //$NON-NLS-1$
	public static final String PROPERTY_CHANGELOG = "changeLog"; //$NON-NLS-1$
	public static final String PROPERTY_CHANGES = "changes"; //$NON-NLS-1$
	public static final String PROPERTY_ORDER = "order"; //$NON-NLS-1$
	public static final String PROPERTY_CHANGED = "changed"; //$NON-NLS-1$
	public static final String PROPERTY_PREVIOUS = "previous"; //$NON-NLS-1$
	public static final String PROPERTY_ACCESS_CONTROL = "accessControl"; //$NON-NLS-1$
	public static final String PROPERTY_VALIDATED_BY = "validatedBy"; //$NON-NLS-1$
	public static final String PROPERTY_CREATION_FACTORY = "creationFactory"; //$NON-NLS-1$
	public static final String PROPERTY_CREATION = "creation"; //$NON-NLS-1$
	public static final String PROPERTY_QUERY_CAPABILITY = "queryCapability"; //$NON-NLS-1$
	public static final String PROPERTY_QUERY_BASE = "queryBase"; //$NON-NLS-1$
	public static final String PROPERTY_RESOURCE_TYPE = "resourceType"; //$NON-NLS-1$
	public static final String PROPERTY_TESTED_BY_TEST_CASE = "testedByTestCase"; //$NON-NLS-1$
	public static final String PROPERTY_RELATED_TEST_PLAN = "relatedTestPlan"; //$NON-NLS-1$
	public static final String PROPERTY_RELATED_TEST_CASE = "relatedTestCase"; //$NON-NLS-1$
	public static final String PROPERTY_RELATED_TEST_SCRIPT = "relatedTestScript"; //$NON-NLS-1$
	public static final String PROPERTY_RELATED_TEST_RESULT = "relatedTestResult"; //$NON-NLS-1$
	public static final String PROPERTY_AFFECTS_TEST_RESULT = "affectsTestResult"; //$NON-NLS-1$
	public static final String PROPERTY_BLOCKS_TEST_EXECUTION_RECORD = "blocksTestExecutionRecord"; //$NON-NLS-1$
	public static final String PROPERTY_RELATED_TEST_EXECUTION_RECORD = "relatedTestExecutionRecord"; //$NON-NLS-1$
	public static final String PROPERTY_URL = "url"; //$NON-NLS-1$
	public static final String PROPERTY_REQUIREMENT_FACTORY = "requirementFactory"; //$NON-NLS-1$
	public static final String PROPERTY_SOFT_DELETED = "softDeleted"; //$NON-NLS-1$
	public static final String PROPERTY_LABEL = "label"; //$NON-NLS-1$
	public static final String PROPERTY_TRACKED_RESOURCE_SET_PROVIDER = "trackedResourceSetProvider"; //$NON-NLS-1$
	public static final String PROPERTY_TRACKED_RESOURCE_SET = "trackedResourceSet"; //$NON-NLS-1$
	public static final String PROPERTY_CHANGE = "change"; //$NON-NLS-1$
	public static final String PROPERTY_BEFORE_ETAG = "beforeEtag"; //$NON-NLS-1$
	public static final String PROPERTY_AFTER_ETAG = "afterEtag"; //$NON-NLS-1$
	public static final String PROPERTY_RDF_PATCH = "rdfPatch"; //$NON-NLS-1$
	public static final String PROPERTY_SELECTS = "selects"; //$NON-NLS-1$
	public static final String PROPERTY_DOCUMENT = "document"; //$NON-NLS-1$
	public static final String PROPERTY_REMOTE_LINK = "remoteLink"; //$NON-NLS-1$
	public static final String PROPERTY_SUBJECT = "subject"; //$NON-NLS-1$
	public static final String PROPERTY_PREDICATE = "predicate"; //$NON-NLS-1$
	public static final String PROPERTY_OBJECT = "object"; //$NON-NLS-1$
	public static final String PROPERTY_SAME_AS = "sameAs"; //$NON-NLS-1$
	public static final String PROPERTY_COMPONENT = "component"; //$NON-NLS-1$
	public static final String PROPERTY_PROJECT_AREAS = "project-areas"; //$NON-NLS-1$
	public static final String PROPERTY_PROJECT_AREA = "project-area"; //$NON-NLS-1$
	public static final String PROPERTY_PUBLISHER = "publisher"; //$NON-NLS-1$
	public static final String PROPERTY_OAUTH_REALM_NAME = "oauthRealmName"; //$NON-NLS-1$
	public static final String PROPERTY_IS_LOCKED = "isLocked"; //$NON-NLS-1$
	
	//Resources:
	public static final String RESOURCE_RESOURCE_SHAPE = "ResourceShape"; //$NON-NLS-1$
	public static final String RESOURCE_ALLOWED_VALUES = "AllowedValues"; //$NON-NLS-1$
	public static final String RESOURCE_PROPERTY = "Property"; //$NON-NLS-1$
	public static final String RESOURCE_RESPONSE_INFO = "ResponseInfo"; //$NON-NLS-1$
	public static final String RESOURCE_PERSON = "Person"; //$NON-NLS-1$
	public static final String RESOURCE_SERVICE_PROVIDER = "ServiceProvider"; //$NON-NLS-1$
	public static final String RESOURCE_CHANGE_REQUEST = "ChangeRequest"; //$NON-NLS-1$
	public static final String RESOURCE_TEST_PLAN = "TestPlan"; //$NON-NLS-1$
	public static final String RESOURCE_TEST_CASE = "TestCase"; //$NON-NLS-1$
	public static final String RESOURCE_TEST_SCRIPT = "TestScript"; //$NON-NLS-1$
	public static final String RESOURCE_TEST_SCRIPT_STEP = "TestScriptStep"; //$NON-NLS-1$
	public static final String RESOURCE_TEST_SCRIPT_STEP_RESULT = "TestScriptStepResult"; //$NON-NLS-1$
	public static final String RESOURCE_TEST_RESULT = "TestResult"; //$NON-NLS-1$
	public static final String RESOURCE_TEST_EXECUTION_RECORD = "TestExecutionRecord"; //$NON-NLS-1$
	public static final String RESOURCE_REQUIREMENT = "Requirement"; //$NON-NLS-1$
	public static final String RESOURCE_REQUIREMENT_COLLECTION = "RequirementCollection"; //$NON-NLS-1$
	public static final String RESOURCE_CATEGORY = "Category"; //$NON-NLS-1$
	public static final String RESOURCE_STORAGE = "storage"; //$NON-NLS-1$
	public static final String RESOURCE_SERVICE = "service"; //$NON-NLS-1$
	public static final String RESOURCE_TRACKED_RESOURCE_SET = "TrackedResourceSet"; //$NON-NLS-1$
	public static final String RESOURCE_PREVIEW = "Preview"; //$NON-NLS-1$
	public static final String RESOURCE_COMPACT = "Compact"; //$NON-NLS-1$
	public static final String RESOURCE_LINK_TYPE = "LinkType"; //$NON-NLS-1$
	
	//Prefixed (dc:) properties:
	public static final String PROPERTY_DC_CREATOR = (PREFIX_DC + ':' + PROPERTY_CREATOR); 
	public static final String PROPERTY_DC_CONTRIBUTOR = (PREFIX_DC + ':' + PROPERTY_CONTRIBUTOR); 
	public static final String PROPERTY_DC_DESCRIPTION = (PREFIX_DC + ':' + PROPERTY_DESCRIPTION); 
	public static final String PROPERTY_DC_IDENTIFIER = (PREFIX_DC + ':' + PROPERTY_IDENTIFIER); 
	public static final String PROPERTY_DC_MODIFIED = (PREFIX_DC + ':' + PROPERTY_MODIFIED); 
	public static final String PROPERTY_DC_TITLE = (PREFIX_DC + ':' + PROPERTY_TITLE); 
	public static final String PROPERTY_DC_RELATION = (PREFIX_DC + ':' + PROPERTY_RELATION); 
	
	//Prefixed (dcterms:) properties:
	public static final String PROPERTY_DCTERMS_CREATOR = (PREFIX_DCTERMS + ':' + PROPERTY_CREATOR); 
	public static final String PROPERTY_DCTERMS_CONTRIBUTOR = (PREFIX_DCTERMS + ':' + PROPERTY_CONTRIBUTOR); 
	public static final String PROPERTY_DCTERMS_DESCRIPTION = (PREFIX_DCTERMS + ':' + PROPERTY_DESCRIPTION); 
	public static final String PROPERTY_DCTERMS_IDENTIFIER = (PREFIX_DCTERMS + ':' + PROPERTY_IDENTIFIER); 
	public static final String PROPERTY_DCTERMS_SUBJECT = (PREFIX_DCTERMS + ':' + PROPERTY_SUBJECT); 
	public static final String PROPERTY_DCTERMS_MODIFIED = (PREFIX_DCTERMS + ':' + PROPERTY_MODIFIED); 
	public static final String PROPERTY_DCTERMS_RELATION = (PREFIX_DCTERMS + ':' + PROPERTY_RELATION); 
	public static final String PROPERTY_DCTERMS_TITLE = (PREFIX_DCTERMS + ':' + PROPERTY_TITLE); 
	public static final String PROPERTY_DCTERMS_CREATED = (PREFIX_DCTERMS + ':' + PROPERTY_CREATED); 
	
	//Prefixed (foaf:) properties:
	public static final String PROPERTY_FOAF_NAME = (PREFIX_FOAF + ':' + PROPERTY_NAME); 
	public static final String PROPERTY_FOAF_MBOX = (PREFIX_FOAF + ':' + PROPERTY_MBOX); 
	public static final String PROPERTY_FOAF_NICK = (PREFIX_FOAF + ':' + PROPERTY_NICK); 

	//Prefixed (rqm_qm:) properties:
	public static final String PROPERTY_RQM_QM_CATEGORY = (PREFIX_RQM_QM + ':' + PROPERTY_CATEGORY); 
	public static final String PROPERTY_RQM_QM_TOTAL_COUNT = (PREFIX_RQM_QM + ':' + PROPERTY_TOTAL_COUNT); 

	//Prefixed (rtc_cm:) properties:
	public static final String PROPERTY_RTC_CM_RELATED_TEST_RESULT = (PREFIX_RTC_CM + ':' + PROPERTY_RELATED_TEST_RESULT); 

	//Prefixed (oslc:) properties:		
	public static final String PROPERTY_OSLC_SERVICE_PROVIDER = (PREFIX_OSLC + ':' + PROPERTY_SERVICE_PROVIDER); 
	public static final String PROPERTY_OSLC_INSTANCE_SHAPE = (PREFIX_OSLC + ':' + PROPERTY_INSTANCE_SHAPE); 
	
	//Prefixed (oslc_config:) properties:
	public static final String PROPERTY_OSLC_CONFIG_COMPONENT = (PREFIX_OSLC_CONFIG + ':' + PROPERTY_COMPONENT); 

	//Prefixed (jazz_acc:) properties:		
	public static final String PROPERTY_JAZZ_ACCESS_CONTROL = (PREFIX_JAZZ_ACP + ':' + PROPERTY_ACCESS_CONTROL); 
	
	//Prefixed (oslc_qm:) properties:		
	public static final String PROPERTY_OSLC_QM_STATUS = (PREFIX_OSLC_QM + ':' + PROPERTY_STATUS); 
	public static final String PROPERTY_OSLC_QM_AFFECTED_BY_CHANGE_REQUEST = (PREFIX_OSLC_QM + ':' + PROPERTY_AFFECTED_BY_CHANGE_REQUEST); 
	public static final String PROPERTY_OSLC_QM_EXECUTES_TEST_SCRIPT = (PREFIX_OSLC_QM + ':' + PROPERTY_EXECUTES_TEST_SCRIPT); 
	public static final String PROPERTY_OSLC_QM_PRODUCED_BY_TEST_EXECUTION_RECORD = (PREFIX_OSLC_QM + ':' + PROPERTY_PRODUCED_BY_TEST_EXECUTION_RECORD); 
	public static final String PROPERTY_OSLC_QM_REPORTS_ON_TEST_CASE = (PREFIX_OSLC_QM + ':' + PROPERTY_REPORTS_ON_TEST_CASE); 
	public static final String PROPERTY_OSLC_QM_REPORTS_ON_TEST_PLAN = (PREFIX_OSLC_QM + ':' + PROPERTY_REPORTS_ON_TEST_PLAN); 	
	public static final String PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT_COLLECTION = (PREFIX_OSLC_QM + ':' + PROPERTY_VALIDATES_REQUIREMENT_COLLECTION); 
	public static final String PROPERTY_OSLC_QM_USES_TEST_CASE = (PREFIX_OSLC_QM + ':' + PROPERTY_USES_TEST_CASE); 
	public static final String PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST = (PREFIX_OSLC_QM + ':' + PROPERTY_RELATED_CHANGE_REQUEST); 
	public static final String PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT = (PREFIX_OSLC_QM + ':' + PROPERTY_VALIDATES_REQUIREMENT); 
	public static final String PROPERTY_OSLC_QM_TESTS_CHANGE_REQUEST = (PREFIX_OSLC_QM + ':' + PROPERTY_TESTS_CHANGE_REQUEST); 
	public static final String PROPERTY_OSLC_QM_USES_TEST_SCRIPT = (PREFIX_OSLC_QM + ':' + PROPERTY_USES_TEST_SCRIPT); 
	public static final String PROPERTY_OSLC_QM_EXECUTION_INSTRUCTIONS = (PREFIX_OSLC_QM + ':' + PROPERTY_EXECUTION_INSTRUCTIONS); 
	public static final String PROPERTY_OSLC_QM_BLOCKED_BY_CHANGE_REQUEST = (PREFIX_OSLC_QM + ':' + PROPERTY_BLOCKED_BY_CHANGE_REQUEST); 
	public static final String PROPERTY_OSLC_QM_RUNS_ON_TEST_ENVIRONMENT = (PREFIX_OSLC_QM + ':' + PROPERTY_RUNS_ON_TEST_ENVIRONMENT); 
	public static final String PROPERTY_OSLC_QM_RUNS_TEST_CASE = (PREFIX_OSLC_QM + ':' + PROPERTY_RUNS_TEST_CASE); 

	//Prefixed (oslc_cm:) properties:		
	public static final String PROPERTY_OSLC_CM_TESTED_BY_TEST_CASE = (PREFIX_OSLC_CM + ':' + PROPERTY_TESTED_BY_TEST_CASE); 
	public static final String PROPERTY_OSLC_CM_RELATED_TEST_PLAN = (PREFIX_OSLC_CM + ':' + PROPERTY_RELATED_TEST_PLAN); 
	public static final String PROPERTY_OSLC_CM_RELATED_TEST_CASE = (PREFIX_OSLC_CM + ':' + PROPERTY_RELATED_TEST_CASE); 
	public static final String PROPERTY_OSLC_CM_RELATED_TEST_SCRIPT = (PREFIX_OSLC_CM + ':' + PROPERTY_RELATED_TEST_SCRIPT); 
	public static final String PROPERTY_OSLC_CM_AFFECTS_TEST_RESULT = (PREFIX_OSLC_CM + ':' + PROPERTY_AFFECTS_TEST_RESULT); 
	public static final String PROPERTY_OSLC_CM_BLOCKS_TEST_EXECUTION_RECORD = (PREFIX_OSLC_CM + ':' + PROPERTY_BLOCKS_TEST_EXECUTION_RECORD); 
	public static final String PROPERTY_OSLC_CM_RELATED_TEST_EXECUTION_RECORD = (PREFIX_OSLC_CM + ':' + PROPERTY_RELATED_TEST_EXECUTION_RECORD); 
	
	//Prefixed (oslc_rm:) properties:		
	public static final String RESOURCE_OSLC_RM_REQUIREMENT = (PREFIX_OSLC_RM + ':' + RESOURCE_REQUIREMENT); 
	public static final String RESOURCE_OSLC_RM_REQUIREMENT_COLLECTION = (PREFIX_OSLC_RM + ':' + RESOURCE_REQUIREMENT_COLLECTION); 
	public static final String PROPERTY_OSLC_RM_VALIDATED_BY = (PREFIX_OSLC_RM + ':' + PROPERTY_VALIDATED_BY); 

	//Prefixed (calm:) properties:		
	public static final String PROPERTY_CALM_TESTS_DEV_PLAN = (PREFIX_CALM + ':' + PROPERTY_TESTS_DEVELOPMENT_PLAN); 
	public static final String PROPERTY_CALM_TESTED_BY_TEST_PLAN = (PREFIX_CALM + ':' + PROPERTY_TESTED_BY_TEST_PLAN); 
	
	//Prefixed (rdf:) properties:		
	public static final String PROPERTY_RDF_TYPE = (PREFIX_RDF + ':' + PROPERTY_TYPE); 
	
	//Prefixed (oslc_qm:) resources:		
	public static final String RESOURCE_OSLC_QM_TEST_PLAN = (PREFIX_OSLC_QM + ':' + RESOURCE_TEST_PLAN); 
	public static final String RESOURCE_OSLC_QM_TEST_CASE = (PREFIX_OSLC_QM + ':' + RESOURCE_TEST_CASE); 
	public static final String RESOURCE_OSLC_QM_TEST_SCRIPT = (PREFIX_OSLC_QM + ':' + RESOURCE_TEST_SCRIPT); 
	public static final String RESOURCE_OSLC_QM_TEST_RESULT = (PREFIX_OSLC_QM + ':' + RESOURCE_TEST_RESULT); 
	public static final String RESOURCE_OSLC_QM_TEST_EXECUTION_RECORD = (PREFIX_OSLC_QM + ':' + RESOURCE_TEST_EXECUTION_RECORD); 

	//Prefixed (rqm_qm:) resources:		
	public static final String RESOURCE_RQM_QM_TEST_SCRIPT_STEP = (PREFIX_RQM_QM + ':' + RESOURCE_TEST_SCRIPT_STEP); 
	public static final String RESOURCE_RQM_QM_TEST_SCRIPT_STEP_RESULT = (PREFIX_RQM_QM + ':' + RESOURCE_TEST_SCRIPT_STEP_RESULT); 

	//Prefixed (jfs:) resources:		
	public static final String RESOURCE_JFS_STORAGE = (PREFIX_JFS + ':' + RESOURCE_STORAGE); 
	public static final String RESOURCE_JFS_SERVICE = (PREFIX_JFS + ':' + RESOURCE_SERVICE); 

	//Namespaces:
	public static final String NAMESPACE_URI_DCTERMS = "http://purl.org/dc/terms/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_RDFS = "http://www.w3.org/2000/01/rdf-schema#"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_DC_TERMS = "http://purl.org/dc/terms/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_DC = "http://purl.org/dc/elements/1.1/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_OSLC = "http://open-services.net/ns/core#"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_JAZZ_TRS = "http://jazz.net/ns/trs#"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_JAZZ_TRS2 = "http://open-services.net/ns/core/trs#"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_JLDP = "http://www.w3.org/ns/ldp#"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_TRS2_PATCH = "http://open-services.net/ns/core/trspatch#"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_OSLC_CONFIG = "http://open-services.net/ns/config#"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_OSLC_CONFIG_SELECTS = (NAMESPACE_URI_OSLC_CONFIG + "selects"); //$NON-NLS-1$
	public static final String NAMESPACE_URI_RM = "http://www.ibm.com/xmlns/rdm/rdf/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_RM_WORKFLOW = "http://www.ibm.com/xmlns/rdm/workflow/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_JFS = "http://jazz.net/xmlns/foundation/1.0/"; //$NON-NLS-1$	
	public static final String NAMESPACE_URI_H = "http://www.w3.org/TR/REC-html40"; //$NON-NLS-1$	
	public static final String NAMESPACE_URI_XS = "http://schema.w3.org/xs/"; //$NON-NLS-1$	
	public static final String NAMESPACE_URI_RM_TYPES = "http://www.ibm.com/xmlns/rdm/types/"; //$NON-NLS-1$	
	public static final String NAMESPACE_URI_OWL = "http://www.w3.org/2002/07/owl#"; //$NON-NLS-1$	
	
	/**
	 * @deprecated As of Rational Quality Manager 5.0, the OSLC Change Management V1.0 consumer is not supported or tested.  Please use the OSLC Change Management V1.0 consumer.
	 */
	public static final String NAMESPACE_URI_OSLC_CM_V1 = "http://open-services.net/xmlns/cm/1.0/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_OSLC_CM = "http://open-services.net/ns/cm#"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_OSLC_QM_PREFIX = "http://open-services.net/xmlns/qm/"; //$NON-NLS-1$
	
	/**
	 * @deprecated As of Rational Quality Manager 5.0, the OSLC Quality Management V1.0 service provider is not supported or tested.  Please use the OSLC Quality Management V2.0 service provider.
	 */
	public static final String NAMESPACE_URI_OSLC_QM_V1 = NAMESPACE_URI_OSLC_QM_PREFIX + "1.0/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_OSLC_QM = "http://open-services.net/ns/qm#"; //$NON-NLS-1$
	
	/**
	 * @deprecated As of Rational Quality Manager 5.0, the OSLC Requirements Management V1.0 consumer is not supported or tested.  Please use the OSLC Requirements Management V1.0 consumer.
	 */
	public static final String NAMESPACE_URI_OSLC_RM_V1 = "http://open-services.net/xmlns/rm/1.0/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_OSLC_RM = "http://open-services.net/ns/rm#"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_OSLC_DISC = "http://open-services.net/xmlns/discovery/1.0/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_OSLC_DISC_JAZZ = "http://jazz.net/xmlns/prod/jazz/discovery/1.0/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_JAZZ_JFS = "http://jazz.net/xmlns/prod/jazz/jfs/1.0/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_JAZZ_CALM = "http://jazz.net/xmlns/prod/jazz/calm/1.0/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_JAZZ_PRESENTATION = "http://jazz.net/xmlns/prod/jazz/presentation/1.0/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_JAZZ_PROCESS = "http://jazz.net/xmlns/prod/jazz/process/1.0/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_JAZZ_QM = "http://jazz.net/ns/qm/rqm#"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_JAZZ_RM = "http://jazz.net/ns/rm#"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_XML = "http://www.w3.org/XML/1998/namespace"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_DC_ELEMENTS = "http://purl.org/dc/elements/1.1/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_FOAF = "http://xmlns.com/foaf/0.1/"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema#"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_ACCESS_CONTROL = "http://jazz.net/ns/acp#"; //$NON-NLS-1$
	public static final String NAMESPACE_URI_JRS = "http://jazz.net/ns/jrs#"; //$NON-NLS-1$

	//Resource Types (rm:):
	public static final String RESOURCE_TYPE_URI_LINK_TYPE= (NAMESPACE_URI_RM + RESOURCE_LINK_TYPE); 

	//Resource Types (oslc:):
	public static final String RESOURCE_TYPE_URI_COMPACT = (NAMESPACE_URI_OSLC + RESOURCE_COMPACT); 

	//Resource Types (oslc_qm:):
	public static final String RESOURCE_TYPE_URI_TEST_PLAN = (NAMESPACE_URI_OSLC_QM + RESOURCE_TEST_PLAN);
	public static final String RESOURCE_TYPE_URI_TEST_CASE = (NAMESPACE_URI_OSLC_QM + RESOURCE_TEST_CASE);
	public static final String RESOURCE_TYPE_URI_TEST_SCRIPT = (NAMESPACE_URI_OSLC_QM + RESOURCE_TEST_SCRIPT);
	public static final String RESOURCE_TYPE_URI_TEST_RESULT = (NAMESPACE_URI_OSLC_QM + RESOURCE_TEST_RESULT);
	public static final String RESOURCE_TYPE_URI_TEST_EXECUTION_RECORD = (NAMESPACE_URI_OSLC_QM + RESOURCE_TEST_EXECUTION_RECORD);

	//Resource Types (rqm_qm):
	public static final String RESOURCE_TYPE_URI_TEST_SCRIPT_STEP = (NAMESPACE_URI_JAZZ_QM + RESOURCE_TEST_SCRIPT_STEP);
	public static final String RESOURCE_TYPE_URI_TEST_SCRIPT_STEP_RESULT = (NAMESPACE_URI_JAZZ_QM + RESOURCE_TEST_SCRIPT_STEP_RESULT);

	//Resource Types (oslc_cm:):
	public static final String RESOURCE_TYPE_URI_CHANGE_REQUEST = (NAMESPACE_URI_OSLC_CM + RESOURCE_CHANGE_REQUEST);

	//Resource Types (oslc_rm:):
	public static final String RESOURCE_TYPE_URI_REQUIREMENT = (NAMESPACE_URI_OSLC_RM + RESOURCE_REQUIREMENT); 
	public static final String RESOURCE_TYPE_URI_REQUIREMENT_COLLECTION = (NAMESPACE_URI_OSLC_RM + RESOURCE_REQUIREMENT_COLLECTION); 

	//Property labels (oslc_rm:):
	public static final String PROPERTY_LABEL_OSLC_RM_VALIDATED_BY = "Validated By"; //$NON-NLS-1$ 

	//Service providers:
	/**
	 * @deprecated As of Rational Quality Manager 5.0, the OSLC Quality Management V1.0 service provider is not supported or tested.  Please use the OSLC Quality Management V2.0 service provider.
	 */
	public static final String SERVICE_PROVIDERS_URI_QM = (NAMESPACE_URI_OSLC_QM_V1 + "qmServiceProviders"); //$NON-NLS-1$
	
	/**
	 * @deprecated As of Rational Quality Manager 5.0, the OSLC Change Management V1.0 consumer is not supported or tested.  Please use the OSLC Change Management V1.0 consumer.
	 */
	public static final String SERVICE_PROVIDERS_URI_CM = (NAMESPACE_URI_OSLC_CM_V1 + "cmServiceProviders"); //$NON-NLS-1$
	
	/**
	 * @deprecated As of Rational Quality Manager 5.0, the OSLC Requirements Management V1.0 consumer is not supported or tested.  Please use the OSLC Requirements Management V1.0 consumer.
	 */
	public static final String SERVICE_PROVIDERS_URI_RM = (NAMESPACE_URI_OSLC_RM_V1 + "rmServiceProviders"); //$NON-NLS-1$

	//Publisher Identifier URIs:
	public static final String PUBLISHER_IDENTIFIER_URI_QM = "http://jazz.net/application/qm"; //$NON-NLS-1$
	public static final String PUBLISHER_IDENTIFIER_URI_CCM = "http://jazz.net/application/ccm"; //$NON-NLS-1$
	public static final String PUBLISHER_IDENTIFIER_URI_RM = "http://jazz.net/application/rm"; //$NON-NLS-1$
	public static final String PUBLISHER_IDENTIFIER_URI_DM = "http://jazz.net/application/dm"; //$NON-NLS-1$
	
	//Note: IBM Rational DOORS and IBM Rational DOORS Web Access does not support a publisher identifier URI.  As such, use a placeholder publisher identifier URI.
	public static final String PUBLISHER_IDENTIFIER_URI_DWA = "http://jazz.net/application/dwa"; //$NON-NLS-1$

	//Templates (URIs):
	/**
	 * <p>The template for root services URIs.</p>
	 * 
	 * <p>Substitution tokens (<code>{&lt;zero-based token index&gt;}</code>) mappings:</p>
	 * 
	 * <ul>
	 * <li>{0}: The server URL (including context root) with the trailing separator character.</li>
	 * </ul>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>https://localhost:9443/jazz/rootservices</code></p>
	 * 
	 * @see java.text.MessageFormat#format(String, Object[])
	 */
	public static final String URI_TEMPLATE_ROOT_SERVICES = "{0}" + ROOT_SERVICES;  //$NON-NLS-1$

	/**
	 * <p>The template for service document URIs.</p>
	 * 
	 * <p>Substitution tokens (<code>{&lt;zero-based token index&gt;}</code>) mappings:</p>
	 * 
	 * <ul>
	 * <li>{0}: The server URL (including context root) with the trailing separator character.</li>
	 * </ul>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>https://localhost:9443/rm/service-document</code></p>
	 * 
	 * @see java.text.MessageFormat#format(String, Object[])
	 */
	public static final String URI_TEMPLATE_SERVICE_DOCUMENT = "{0}service-document";  //$NON-NLS-1$

	/**
	 * <p>The template for public root services URIs.</p>
	 * 
	 * <p>Substitution tokens (<code>{&lt;zero-based token index&gt;}</code>) mappings:</p>
	 * 
	 * <ul>
	 * <li>{0}: The server URL (including context root) with the trailing separator character.</li>
	 * </ul>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>https://localhost:8443/dwa/public/rootservices</code></p>
	 * 
	 * @see java.text.MessageFormat#format(String, Object[])
	 */
	public static final String URI_TEMPLATE_PUBLIC_ROOT_SERVICES = "{0}public/" + ROOT_SERVICES; //$NON-NLS-1$
	
	/**
	 * <p>The template for (private) IBM Rational DOORS Next Generation links URIs.</p>
	 * 
	 * <p>Substitution tokens (<code>{&lt;zero-based token index&gt;}</code>) mappings:</p>
	 * 
	 * <ul>
	 * <li>{0}: The server URL (including context root) with the trailing separator character.</li>
	 * </ul>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>https://localhost:8443/rm/links</code></p>
	 * 
	 * @see java.text.MessageFormat#format(String, Object[])
	 */
	public static final String URI_TEMPLATE_DNG_LINKS = "{0}links"; //$NON-NLS-1$
	
	/**
	 * <p>The template for (private) IBM Rational DOORS Next Generation projects URIs.</p>
	 * 
	 * <p>Substitution tokens (<code>{&lt;zero-based token index&gt;}</code>) mappings:</p>
	 * 
	 * <ul>
	 * <li>{0}: The server URL (including context root) with the trailing separator character.</li>
	 * </ul>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>https://localhost:8443/rm/rm-projects</code></p>
	 * 
	 * @see java.text.MessageFormat#format(String, Object[])
	 */
	public static final String URI_TEMPLATE_DNG_PROJECTS = "{0}rm-projects"; //$NON-NLS-1$
	
	/**
	 * <p>The template for resource creation URIs.</p>
	 * 
	 * <p>Substitution tokens (<code>{&lt;zero-based token index&gt;}</code>) mappings:</p>
	 * 
	 * <ul>
	 * <li>{0}: The service URL.</li>
	 * <li>{1}: The project area UUID.</li>
	 * <li>{2}: The resource type namespace URI.</li>
	 * <li>{3}: The resource type name.</li>
	 * </ul>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>https://localhost:9443/jazz/oslc_qm/contexts/_stJVgGfqEd24KMoDcFLfcQ/resources/com.ibm.rqm.planning.VersionedTestPlan</code></p>
	 * 
	 * @see org.eclipse.osgi.util.NLS#bind(String, Object[])
	 */
	public static final String URI_TEMPLATE_RESOURCE_CREATION = "{0}/contexts/{1}/resources/{2}.{3}";  //$NON-NLS-1$
	
	/**
	 * <p>The template for resource URIs.</p>
	 * 
	 * <p>Substitution tokens (<code>{&lt;zero-based token index&gt;}</code>) mappings:</p>
	 * 
	 * <ul>
	 * <li>{0}: The service URL.</li>
	 * <li>{1}: The project area UUID.</li>
	 * <li>{2}: The resource type namespace URI.</li>
	 * <li>{3}: The resource type name.</li>
	 * <li>{4}: The resource UUID.</li>
	 * </ul>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>https://localhost:9443/jazz/oslc_qm/contexts/_stJVgGfqEd24KMoDcFLfcQ/resources/com.ibm.rqm.planning.VersionedTestPlan/_aOH1MM_FEd-P1MkhHoHuKg</code></p>
	 * 
	 * @see org.eclipse.osgi.util.NLS#bind(String, Object[])
	 * @see #URI_TEMPLATE_RESOURCE_CREATION
	 */
	public static final String URI_TEMPLATE_RESOURCE = URI_TEMPLATE_RESOURCE_CREATION + "/{4}";  //$NON-NLS-1$
	
	/**
	 * <p>The template for version resource URIs.</p>
	 * 
	 * <p>Substitution tokens (<code>{&lt;zero-based token index&gt;}</code>) mappings:</p>
	 * 
	 * <ul>
	 * <li>{0}: The service URL.</li>
	 * <li>{1}: The project area UUID.</li>
	 * <li>{2}: The resource type namespace URI.</li>
	 * <li>{3}: The resource type name.</li>
	 * <li>{4}: The resource UUID.</li>
	 * <li>{5}: The resource state UUID.</li>
	 * </ul>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>https://localhost:9443/jazz/oslc_qm/contexts/_stJVgGfqEd24KMoDcFLfcQ/resources/com.ibm.rqm.planning.VersionedTestPlan/_aOH1MM_FEd-P1MkhHoHuKg/_5PilaWl4EeSU04Ga3i8q8g</code></p>
	 * 
	 * @see org.eclipse.osgi.util.NLS#bind(String, Object[])
	 * @see #URI_TEMPLATE_VERSION_RESOURCE_CREATION
	 */
	public static final String URI_TEMPLATE_VERSION_RESOURCE = URI_TEMPLATE_RESOURCE_CREATION + "/{4}/{5}";  //$NON-NLS-1$
	
	//DNG query parameters:
	public static final String QUERY_PARAMETER_DNG_SOURCE_OR_TARGET = "sourceOrTarget";  //$NON-NLS-1$	
	public static final String QUERY_PARAMETER_DNG_ACCEPT = "accept";  //$NON-NLS-1$	
	public static final String QUERY_PARAMETER_DNG_PRIVATE = "private";  //$NON-NLS-1$	

	//Templates (resources):
	/**
	 * <p>The template for a query resource name or URI.</p>
	 * 
	 * <p>Substitution tokens (<code>{&lt;zero-based token index&gt;}</code>) mappings:</p>
	 * 
	 * <ul>
	 * <li>{0}: The (optional) resource type namespace URI and resource name.</li>
	 * </ul>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>TestPlanQuery</code></p>
	 * <p><code>http://open-services.net/ns/qm#TestPlanQuery</code></p>
	 * 
	 * @see java.text.MessageFormat#format(String, Object[])
	 */
	public static final String RESOURCE_TEMPLATE_QUERY = "{0}Query"; //$NON-NLS-1$
	
	//Expressions:
	public static final String PATCH_EXPRESSION = "([AD]) \\<([^\\>]+)\\> \\<(" + Pattern.quote(NAMESPACE_URI_OSLC_CONFIG_SELECTS) + ")\\> \\<([^\\>]+)\\> \\."; //$NON-NLS-1$ //$NON-NLS-2$
	
	//Patterns:
	public static final Pattern SELECTIONS_NEXT_PAGE_PATTERN = Pattern.compile("\\<([^\\>]+)" + Pattern.quote(">; rel=\"next\"")); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Pattern PATCH_EVENT_PATTERN = Pattern.compile("^(" + PATCH_EXPRESSION + ")+$"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final Pattern PATCH_PATTERN = Pattern.compile(PATCH_EXPRESSION);

	/**
	 * <p>The pattern to match OSLC versioned URLs.</p>
	 */
	public static final Pattern VERSIONED_URL_PATTERN = Pattern.compile(MessageFormat.format(URI_TEMPLATE_VERSION_RESOURCE, new Object[]{"", "[^/]+", "([^/]+)\\", /*.*/ "([^/\\.]+)", /*/*/ "([^/]+)", /*/*/ "([^/]+)"})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
}