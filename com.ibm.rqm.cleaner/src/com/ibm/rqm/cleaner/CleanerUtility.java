/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner;

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
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.DELIMITER_COMPOUND_ARGUMENT_VALUE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.FORWARD_SLASH;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.OSLC_DEFAULT_VALUE_PAGE_SIZE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_CALM_TESTED_BY_TEST_PLAN;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_CALM_TESTS_DEV_PLAN;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_CM_AFFECTS_TEST_RESULT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_CM_BLOCKS_TEST_EXECUTION_RECORD;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_CM_RELATED_TEST_CASE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_CM_RELATED_TEST_EXECUTION_RECORD;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_CM_RELATED_TEST_PLAN;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_CM_RELATED_TEST_SCRIPT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_CM_TESTED_BY_TEST_CASE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_QM_AFFECTED_BY_CHANGE_REQUEST;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_QM_BLOCKED_BY_CHANGE_REQUEST;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_QM_TESTS_CHANGE_REQUEST;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT_COLLECTION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_RM_VALIDATED_BY;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_RTC_CM_RELATED_TEST_RESULT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_OSLC_QM_TEST_CASE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_OSLC_QM_TEST_EXECUTION_RECORD;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_OSLC_QM_TEST_PLAN;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_OSLC_QM_TEST_RESULT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_OSLC_QM_TEST_SCRIPT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_OSLC_RM_REQUIREMENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_OSLC_RM_REQUIREMENT_COLLECTION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_RQM_QM_TEST_SCRIPT_STEP;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_RQM_QM_TEST_SCRIPT_STEP_RESULT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_CHANGE_REQUEST;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_REQUIREMENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_REQUIREMENT_COLLECTION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_CASE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_EXECUTION_RECORD;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_PLAN;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_RESULT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_SCRIPT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_SCRIPT_STEP;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_SCRIPT_STEP_RESULT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.TRS2_PROVIDER_TYPE_QM;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.TRS2_PROVIDER_TYPE_QM_PROCESS;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ibm.rqm.cleaner.internal.Cleaner;
import com.ibm.rqm.cleaner.internal.client.Credential;
import com.ibm.rqm.cleaner.internal.client.OslcHttpClient;
import com.ibm.rqm.cleaner.internal.client.OslcHttpClientFactory;
import com.ibm.rqm.cleaner.internal.client.ServiceProvider;
import com.ibm.rqm.cleaner.internal.util.CleanerUtils;
import com.ibm.rqm.cleaner.internal.util.DateTimeUtils;
import com.ibm.rqm.cleaner.internal.util.LogUtils;
import com.ibm.rqm.cleaner.internal.util.StringUtils;
import com.ibm.rqm.cleaner.internal.util.UrlUtils;

/**
 * <p>Cleaner utility to clean OSLC Quality Management (QM) resources.</p>
 * 
 * <p>For more information, see <code>readme.txt</code>.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public class CleanerUtility {

	private static final String VERSION = "1.0"; //$NON-NLS-1$

	//Note: When updating supported resource types and properties, update this class and OslcUtils. 
	private static final String[] SUPPORTED_QM_PROPERTY_PREFIXED_NAMES = new String[]{PROPERTY_CALM_TESTS_DEV_PLAN, PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST, PROPERTY_OSLC_QM_TESTS_CHANGE_REQUEST, PROPERTY_OSLC_QM_BLOCKED_BY_CHANGE_REQUEST, PROPERTY_OSLC_QM_AFFECTED_BY_CHANGE_REQUEST, PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT_COLLECTION, PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT};

	private static final String[] SUPPORTED_RM_PROPERTY_PREFIXED_NAMES = new String[]{PROPERTY_OSLC_RM_VALIDATED_BY};
	
	private static final String[] SUPPORTED_QM_RESOURCE_TYPES = new String[]{RESOURCE_OSLC_QM_TEST_PLAN, RESOURCE_OSLC_QM_TEST_CASE, RESOURCE_OSLC_QM_TEST_SCRIPT, RESOURCE_RQM_QM_TEST_SCRIPT_STEP, RESOURCE_OSLC_QM_TEST_EXECUTION_RECORD, RESOURCE_OSLC_QM_TEST_RESULT, RESOURCE_RQM_QM_TEST_SCRIPT_STEP_RESULT};

	private static final String[] SUPPORTED_RM_RESOURCE_TYPES = new String[]{RESOURCE_OSLC_RM_REQUIREMENT, RESOURCE_OSLC_RM_REQUIREMENT_COLLECTION};

	private static final String[] SUPPORTED_TRS2_PROVIDER_TYPES = new String[]{TRS2_PROVIDER_TYPE_QM, TRS2_PROVIDER_TYPE_QM_PROCESS};

	private static final String LOG4J_FILE_PATH = "log4j2.xml"; //$NON-NLS-1$
	
	private static final String LOG4J2_CONFIGURATION = "log4j2.configurationFile"; //$NON-NLS-1$
	
	private static enum CmdLineArg {
		COMMANDS("-c", "-commands"), //$NON-NLS-1$ //$NON-NLS-2$
		QUALITY_MANAGER_URL("-qm", "-qualityManagerURL"), //$NON-NLS-1$ //$NON-NLS-2$		
		REQUIREMENTS_MANAGER_URL("-rm", "-requirementsManagerURL"), //$NON-NLS-1$ //$NON-NLS-2$		
		CREDENTIALS("-cr", "-credentials"), //$NON-NLS-1$ //$NON-NLS-2$
		PROJECT_AREA("-pa", "-projectAreas"), //$NON-NLS-1$ //$NON-NLS-2$
		CONFIGURATION_CONTEXTS("-cc", "-configurationContexts"), //$NON-NLS-1$ //$NON-NLS-2$		
		RESOURCE_TYPES("-rt", "-resourceTypes"), //$NON-NLS-1$ //$NON-NLS-2$
		PROPERTY_PREFIXED_NAMES("-ppn", "-propertyPrefixedNames"), //$NON-NLS-1$ //$NON-NLS-2$
		PROPERTY_LABELS("-pl", "-propertyLabels"), //$NON-NLS-1$ //$NON-NLS-2$
		IGNORE_RESOURCE_URLS("-iru", "-ignoreResourceUrls"), //$NON-NLS-1$ //$NON-NLS-2$
		PROCESS_RESOURCE_URLS("-pru", "-processResourceUrls"), //$NON-NLS-1$ //$NON-NLS-2$
		IGNORE_TARGET_RESOURCE_URLS("-itru", "-ignoreTargetResourceUrls"), //$NON-NLS-1$ //$NON-NLS-2$
		PROCESS_TARGET_RESOURCE_URLS("-ptru", "-processTargetResourceUrls"), //$NON-NLS-1$ //$NON-NLS-2$
		LAST_MODIFIED("-lm", "-lastModified"), //$NON-NLS-1$ //$NON-NLS-2$
		RESOURCE_IDENTIFIER("-ri", "-resourceIdentifier"), //$NON-NLS-1$ //$NON-NLS-2$
		OLD_PUBLIC_URL("-opu", "-oldPublicUrl"), //$NON-NLS-1$ //$NON-NLS-2$
		NEW_PUBLIC_URL("-npu", "-newPublicUrl"), //$NON-NLS-1$ //$NON-NLS-2$
		IGNORE_READ_ERRORS("-ire", "-ignoreReadErrors", false), //$NON-NLS-1$ //$NON-NLS-2$
		OUTPUT("-o", "-output", false), //$NON-NLS-1$ //$NON-NLS-2$		
		PAGE_SIZE("-ps", "-pageSize"), //$NON-NLS-1$ //$NON-NLS-2$
		USE_PRIVATE_DNG_API("-updi", "-usePrivateDngApi", false), //$NON-NLS-1$ //$NON-NLS-2$
		DISABLE_DNG_QUERY_TIMEOUT("-ddqt", "-disableDngQueryTimeout", false), //$NON-NLS-1$ //$NON-NLS-2$
		ATTEMPT_RQM_UNLOCK("-aru", "-attemptRqmUnlock", false), //$NON-NLS-1$ //$NON-NLS-2$
		TEST("-t", "-test", false), //$NON-NLS-1$ //$NON-NLS-2$
		HELP("-h", "-help", false), //$NON-NLS-1$ //$NON-NLS-2$
		VERSION("-v", "-version", false), //$NON-NLS-1$ //$NON-NLS-2$
		LIST_RESOURCES("-lr", "-listResources", false), //$NON-NLS-1$ //$NON-NLS-2$
		LIST_PROPERTIES("-lp", "-listProperties", false), //$NON-NLS-1$ //$NON-NLS-2$
		INPUT_DIRECTORY("-id", "-inputDirectory"), //$NON-NLS-1$ //$NON-NLS-2$
		OUTPUT_DIRECTORY("-od", "-outputDirectory"), //$NON-NLS-1$ //$NON-NLS-2$
		TRS2_PROVIDER_TYPES("-t2pt", "-trs2ProviderTypes"), //$NON-NLS-1$ //$NON-NLS-2$
		LIST_TRS2_PROVIDER_TYPES("-lt2pt", "-listTrs2ProviderTypes", false), //$NON-NLS-1$ //$NON-NLS-2$
		ACCEPT_LANGUAGE("-al", "-acceptLanguage"), //$NON-NLS-1$ //$NON-NLS-2$

		//Not supported:
		USER_NAME("-u", "-username", CmdLineArg.CREDENTIALS), //$NON-NLS-1$ //$NON-NLS-2$
		PASSWORD("-pw", "-password", CmdLineArg.CREDENTIALS), //$NON-NLS-1$ //$NON-NLS-2$
		PROPERTY_PREFIXED_NAME("-propertyPrefixedName", CmdLineArg.PROPERTY_PREFIXED_NAMES), //$NON-NLS-1$
		
		//Not supported starting 7.0.3: 
		PERFORMANCE("-p", "-performance"), //$NON-NLS-1$ //$NON-NLS-2$
		DEBUG("-d", "-debug", false), //$NON-NLS-1$ //$NON-NLS-2$
		LOG("-l", "-log"), //$NON-NLS-1$ //$NON-NLS-2$
		HTTP_DEBUG("-hd", "-httpDebug", false); //$NON-NLS-1$ //$NON-NLS-2$
		
		
		private final String shortName;
		private final String longName;
		private final boolean isValueRequired;
		private final CmdLineArg replacement;
		private String value = null;				
		
		private CmdLineArg(String shortName, String longName) { 
			this(shortName, longName, true); 
		}

		private CmdLineArg(String shortName, String longName, boolean isValueRequired) { 
			this(shortName, longName, isValueRequired, null);
		}

		private CmdLineArg(String shortName, String longName, CmdLineArg replacement) { 
			this(shortName, longName, true, replacement);
		}

		private CmdLineArg(String longName, CmdLineArg replacement) { 
			this(null, longName, true, replacement);
		}

		private CmdLineArg(String shortName, String longName, boolean isValueRequired, CmdLineArg replacement) { 

			this.shortName=shortName; 
			this.longName=longName; 
			this.isValueRequired = isValueRequired; 
			this.replacement = replacement;
		};

		public boolean isEqual(String name) {
			
			//Note: The short and/or long name(s) may not be set.
			if(StringUtils.isSet(name)){
				return ((name.equalsIgnoreCase(shortName)) || (name.equalsIgnoreCase(longName)));
			}
			
			return false;
		}

		public boolean isValueRequired() { 
			return isValueRequired; 
		}

		public CmdLineArg getReplacement() { 
			return replacement; 
		}

		public void setValue(String value) { 

			if(value != null){
				this.value = value.trim();
			}
			else{
				this.value = value;
			}
		}

		public String getValue() { 
			return value; 
		}

		public String getName() { 
			return longName; 
		}
		
		@Override
		public String toString() {
			
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(this.longName);

			if(this.isValueRequired) {

				stringBuilder.append("="); //$NON-NLS-1$
				stringBuilder.append(this.value);
			}

			return (stringBuilder.toString());
		}
	}

	public static void main(String[] args) {

		OslcHttpClient qmOslcHttpClient = null;
		OslcHttpClient rmOslcHttpClient = null;
		int systemExitCode = 0;
		
		// Read log4j2.xml configuration into System Properties.		
		System.setProperty(LOG4J2_CONFIGURATION, LOG4J_FILE_PATH); //$NON-NLS-1$
		
		try {                  

			List<CmdLineArg> cmdArgs = processArgs(args);

			String cmdArgsString = CleanerUtils.toString(cmdArgs);

			//Remove the passwords from the command arguments string:
			if (cmdArgs.contains(CmdLineArg.CREDENTIALS)) {

				String[] credentialValues = CmdLineArg.CREDENTIALS.getValue().split("\\,"); //$NON-NLS-1$

				for(String credentialValue : credentialValues){

					String[] credentialProperties = credentialValue.split(Pattern.quote(DELIMITER_COMPOUND_ARGUMENT_VALUE));

					if(credentialProperties.length == 3){
						cmdArgsString = cmdArgsString.replaceAll(Pattern.quote(credentialProperties[0] + "::" + credentialProperties[1] + "::" + credentialProperties[2]), (credentialProperties[0] + "::" + credentialProperties[1] + "::" + CleanerUtils.getAsterisks(credentialProperties[2].length()))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					}
					else if(credentialProperties.length == 5){
						cmdArgsString = cmdArgsString.replaceAll(Pattern.quote(credentialProperties[0] + "::" + credentialProperties[1] + "::" + credentialProperties[2] + "::" + credentialProperties[3] + "::" + credentialProperties[4]), (credentialProperties[0]  + "::" + credentialProperties[1] + "::" + CleanerUtils.getAsterisks(credentialProperties[2].length()) + "::" + credentialProperties[3] + "::" + CleanerUtils.getAsterisks(credentialProperties[4].length()))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
					}
				}
			}
			
			if (cmdArgs.contains(CmdLineArg.LOG) || cmdArgs.contains(CmdLineArg.DEBUG) || cmdArgs.contains(CmdLineArg.HTTP_DEBUG) || cmdArgs.contains(CmdLineArg.PERFORMANCE)) {				
				System.err.println("The \"-l/-log\",\"-d/-debug\", \"-hd/-httpDebug\" and ,\"-p/-performance\"  arguments are not supported. Please configure logging in the log4j2.xml file."); //$NON-NLS-1$
				System.exit(0);
			}									

			LogUtils.logInfo("Running the OSLC Cleaner Utility with the following arguments: " + cmdArgsString); //$NON-NLS-1$

			if ((cmdArgs.contains(CmdLineArg.HELP)) || (cmdArgs.size() == 0)) {

				BufferedReader readmeReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("readme.txt"))); //$NON-NLS-1$
				String line = null;
				
				while((line = readmeReader.readLine()) != null){
					LogUtils.logInfo(line);
				}
				
				System.exit(0);
			}

			if (cmdArgs.contains(CmdLineArg.VERSION)) {

				LogUtils.logInfo(getVersion());
				System.exit(0);
			}

			if (cmdArgs.contains(CmdLineArg.LIST_RESOURCES)) {

				LogUtils.logInfo("Supported resources:"); //$NON-NLS-1$

				for(String supportQmResourceType : SUPPORTED_QM_RESOURCE_TYPES){
					LogUtils.logInfo(supportQmResourceType);
				}

				for(String supportRmResourceType : SUPPORTED_RM_RESOURCE_TYPES){
					LogUtils.logInfo(supportRmResourceType);
				}

				System.exit(0);
			}

			if (cmdArgs.contains(CmdLineArg.LIST_PROPERTIES)) {

				LogUtils.logInfo("Supported properties:"); //$NON-NLS-1$

				for(String supportQmPropertyPrefixedName : SUPPORTED_QM_PROPERTY_PREFIXED_NAMES){
					LogUtils.logInfo(supportQmPropertyPrefixedName);
				}
				
				for(String supportRmPropertyPrefixedName : SUPPORTED_RM_PROPERTY_PREFIXED_NAMES){
					LogUtils.logInfo(supportRmPropertyPrefixedName);
				}

				System.exit(0);
			}

			if (cmdArgs.contains(CmdLineArg.LIST_TRS2_PROVIDER_TYPES)) {

				LogUtils.logInfo("Supported TRS2 provider types:"); //$NON-NLS-1$
				
				for(String supportTrs2ProviderType : SUPPORTED_TRS2_PROVIDER_TYPES){
					LogUtils.logInfo(supportTrs2ProviderType);
				}

				System.exit(0);
			}

			LogUtils.logInfo("Configuring the OSLC Cleaner Utility."); //$NON-NLS-1$

			//Note: HTTP client arguments MUST be handled BEFORE resolving the first HTTP client, to configured all HTTP clients. 
			OslcHttpClientFactory.getInstance().setDisableDngQueryTimeout(cmdArgs.contains(CmdLineArg.DISABLE_DNG_QUERY_TIMEOUT));

			if (cmdArgs.contains(CmdLineArg.ACCEPT_LANGUAGE)) {
				
				String acceptLanguage = CmdLineArg.ACCEPT_LANGUAGE.getValue();
				
				if(StringUtils.isNotSet(acceptLanguage)) {
					throw new IllegalArgumentException(CmdLineArg.ACCEPT_LANGUAGE.getName() + " requires a valid language"); //$NON-NLS-1$					
				}
				
				OslcHttpClientFactory.getInstance().setAcceptLanguage(acceptLanguage);
			}

			if (cmdArgs.contains(CmdLineArg.CREDENTIALS)) {

				Map<URL, Credential> credentials = new HashMap<URL, Credential>();

				String[] credentialValues = CmdLineArg.CREDENTIALS.getValue().split("\\,"); //$NON-NLS-1$

				if(credentialValues.length == 0){
					throw new IllegalArgumentException(CmdLineArg.CREDENTIALS.getName() + " requires one (or more) valid credentials"); //$NON-NLS-1$
				}
				else{

					for(String credentialValue : credentialValues){

						String[] credentialProperties = credentialValue.split(Pattern.quote(DELIMITER_COMPOUND_ARGUMENT_VALUE));

						String credentialUrl = credentialProperties[0];
						credentialUrl = UrlUtils.appendTrailingForwardSlash(credentialUrl);

						if(credentialProperties.length == 3){
							credentials.put(new URL(credentialUrl), new Credential(credentialProperties[1], credentialProperties[2]));							
						}
						else if(credentialProperties.length == 5){
							credentials.put(new URL(credentialUrl), new Credential(credentialProperties[1], credentialProperties[2], credentialProperties[3], credentialProperties[4]));
						}
						else{
							throw new IllegalArgumentException(CmdLineArg.CREDENTIALS.getName() + " contains invalid credential '" + credentialValue + "'"); //$NON-NLS-1$ //$NON-NLS-2$						
						}
					}
				}	
				
				if(credentials.isEmpty()){
					throw new IllegalArgumentException(CmdLineArg.CREDENTIALS.getName() + " requires one (or more) valid credentials"); //$NON-NLS-1$
				}
				else{
					OslcHttpClientFactory.getInstance().setCredentials(credentials);
				}
			} 
									
			if(!cmdArgs.contains(CmdLineArg.COMMANDS)){
				throw new IllegalArgumentException(CmdLineArg.COMMANDS.getName() + " requires one (or more) valid commands"); //$NON-NLS-1$
			}

			List<String> rmCommands = new ArrayList<String>();
		
			List<String> cmCommands = new ArrayList<String>();

			List<String> rmOnlyCommands = new ArrayList<String>();

			List<String> reportingCommands = new ArrayList<String>();

			List<String> qmCommandsTypeIndependent = new ArrayList<String>();
			
			String[] commands = CmdLineArg.COMMANDS.getValue().split("\\,"); //$NON-NLS-1$

			if(commands.length == 0){
				throw new IllegalArgumentException(CmdLineArg.COMMANDS.getName() + " requires one (or more) valid commands"); //$NON-NLS-1$
			}
			else{

				for(String command : commands){
										
					if(CLEANING_COMMAND_RENAME_LINKS.equals(command)){
					
						if(!cmdArgs.contains(CmdLineArg.OLD_PUBLIC_URL)){
							throw new IllegalArgumentException(CmdLineArg.COMMANDS.getName() + " " + CLEANING_COMMAND_RENAME_LINKS + " requires " + CmdLineArg.OLD_PUBLIC_URL.getName()); //$NON-NLS-1$ //$NON-NLS-2$
						}
						else if(!UrlUtils.isValidUrl(CmdLineArg.OLD_PUBLIC_URL.getValue())){
							throw new IllegalArgumentException(CmdLineArg.OLD_PUBLIC_URL.getName() + " requires a valid URL"); //$NON-NLS-1$					
						}

						if(!cmdArgs.contains(CmdLineArg.NEW_PUBLIC_URL)){
							throw new IllegalArgumentException(CmdLineArg.COMMANDS.getName() + " " + CLEANING_COMMAND_RENAME_LINKS + " requires " + CmdLineArg.NEW_PUBLIC_URL.getName()); //$NON-NLS-1$ //$NON-NLS-2$
						}
						else if(!UrlUtils.isValidUrl(CmdLineArg.NEW_PUBLIC_URL.getValue())){
							throw new IllegalArgumentException(CmdLineArg.NEW_PUBLIC_URL.getName() + " requires a valid URL"); //$NON-NLS-1$					
						}
						
						cmCommands.add(command);
					}
					else if(CLEANING_COMMAND_UPDATE_VERSIONED_LINKS.equals(command)){
						rmOnlyCommands.add(command);
					}
					else if((CLEANING_COMMAND_REMOVE_BROKEN_FORWARD_LINKS.equals(command)) || (CLEANING_COMMAND_REMOVE_ALL_FORWARD_LINKS.equals(command)) || (CLEANING_COMMAND_REMOVE_BROKEN_FORWARD_LINKS_WITH_MISSING_BACK_LINKS.equals(command))) {
						
						cmCommands.add(command);
						
						rmCommands.add(command);
					}
					else if((CLEANING_COMMAND_REPORT_BROKEN_LINKS.equals(command)) || (CLEANING_COMMAND_ADD_MISSING_BACK_LINKS.equals(command)) || (CLEANING_COMMAND_UPDATE_LINK_LABELS.equals(command)) || (CLEANING_COMMAND_UPDATE_FORWARD_LINK_LABELS.equals(command)) || (CLEANING_COMMAND_UPDATE_BACK_LINK_LABELS.equals(command)) || (CLEANING_COMMAND_REMOVE_LINK_CONFIG_CONTEXT.equals(command))){
						rmCommands.add(command);
					}
					else if((CLEANING_COMMAND_READ_TRS2.equals(command)) || (CLEANING_COMMAND_READ_TRS2_WITH_SELECTIONS.equals(command)) ||  (CLEANING_COMMAND_VALIDATE_TRS2.equals(command)) || (CLEANING_COMMAND_VALIDATE_TRS2_NO_SKIPPED_RESOURCES.equals(command))){
						qmCommandsTypeIndependent.add(command);
					}
					else if(CLEANING_COMMAND_READ_ALL_RESOURCES.equals(command)){
						reportingCommands.add(command);
					}
					else{
						throw new IllegalArgumentException(CmdLineArg.COMMANDS.getName() + " contains invalid command '" + command + "'"); //$NON-NLS-1$ //$NON-NLS-2$						
					}
				}
			}

			List<String> resourceTypes = null;
			boolean containsRmResourceType = false;
			boolean containsQmResourceType = false;

			if (cmdArgs.contains(CmdLineArg.RESOURCE_TYPES)) {

				String[] resourceTypesValues = CmdLineArg.RESOURCE_TYPES.getValue().split(","); //$NON-NLS-1$

				if(resourceTypesValues.length == 0){
					throw new IllegalArgumentException(CmdLineArg.RESOURCE_TYPES.getName() + " requires one (or more) valid resource types"); //$NON-NLS-1$
				}

				for(String resourceType : resourceTypesValues){

					if((!CleanerUtils.contains(SUPPORTED_QM_RESOURCE_TYPES, resourceType)) && (!CleanerUtils.contains(SUPPORTED_RM_RESOURCE_TYPES, resourceType))){
						throw new IllegalArgumentException(CmdLineArg.RESOURCE_TYPES.getName() + " contains an invalid resource type '" + resourceType + "'"); //$NON-NLS-1$ //$NON-NLS-2$						
					}

					if(CleanerUtils.contains(SUPPORTED_QM_RESOURCE_TYPES, resourceType)) {
						containsQmResourceType = true;
					}

					if(CleanerUtils.contains(SUPPORTED_RM_RESOURCE_TYPES, resourceType)) {
						containsRmResourceType = true;
					}
				}

				resourceTypes = new ArrayList<String>(Arrays.asList(resourceTypesValues));
				
				if(!qmCommandsTypeIndependent.isEmpty()){
					LogUtils.logWarning("Note: The " + CmdLineArg.RESOURCE_TYPES.getName() + " argument will be ignored for the following commands: " + CleanerUtils.toString(qmCommandsTypeIndependent)); //$NON-NLS-1$ //$NON-NLS-2$											
				}
			}
			else if(!rmOnlyCommands.isEmpty()){

				resourceTypes = new ArrayList<String>(Arrays.asList(SUPPORTED_RM_RESOURCE_TYPES));
				
				containsRmResourceType = true;
				
				if((!reportingCommands.isEmpty()) || (!cmCommands.isEmpty()) || (!rmCommands.isEmpty()) || (!rmOnlyCommands.isEmpty())){
					LogUtils.logWarning("Note: The " + CmdLineArg.RESOURCE_TYPES.getName() + " argument is not provided.  The following RM (oslc_rm) resource types resource types will be used: " + CleanerUtils.toString(SUPPORTED_RM_RESOURCE_TYPES)); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else{

				//Note: Do not include the RM resource types as the default resource types for backward compatibility.
				resourceTypes = new ArrayList<String>(Arrays.asList(SUPPORTED_QM_RESOURCE_TYPES));
				
				containsQmResourceType = true;
				
				if((!reportingCommands.isEmpty()) || (!cmCommands.isEmpty()) || (!rmCommands.isEmpty())){
					LogUtils.logWarning("Note: The " + CmdLineArg.RESOURCE_TYPES.getName() + " argument is not provided.  The following QM (oslc_qm) resource types resource types will be used: " + CleanerUtils.toString(SUPPORTED_QM_RESOURCE_TYPES)); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			List<String> propertyPrefixedNames = null;

			if (cmdArgs.contains(CmdLineArg.PROPERTY_PREFIXED_NAMES)) {

				String[] propertyPrefixedNameValues = CmdLineArg.PROPERTY_PREFIXED_NAMES.getValue().split(","); //$NON-NLS-1$

				if(propertyPrefixedNameValues.length == 0){
					throw new IllegalArgumentException(CmdLineArg.PROPERTY_PREFIXED_NAMES.getName() + " requires one (or more) valid property prefixed names"); //$NON-NLS-1$
				}

				for(String propertyPrefixedNameValue : propertyPrefixedNameValues){

					if((!CleanerUtils.contains(SUPPORTED_QM_PROPERTY_PREFIXED_NAMES, propertyPrefixedNameValue)) && (!CleanerUtils.contains(SUPPORTED_RM_PROPERTY_PREFIXED_NAMES, propertyPrefixedNameValue))){
						throw new IllegalArgumentException(CmdLineArg.PROPERTY_PREFIXED_NAMES.getName() + " contains an invalid property prefixed name '" + propertyPrefixedNameValue + "'"); //$NON-NLS-1$ //$NON-NLS-2$						
					}
				}

				propertyPrefixedNames = new ArrayList<String>(Arrays.asList(propertyPrefixedNameValues));
				
				if((!qmCommandsTypeIndependent.isEmpty()) || (!reportingCommands.isEmpty())){
					
					List<String> unsupportedCommands = new ArrayList<String>();
					unsupportedCommands.addAll(qmCommandsTypeIndependent);
					unsupportedCommands.addAll(reportingCommands);
				
					LogUtils.logWarning("Note: The " + CmdLineArg.PROPERTY_PREFIXED_NAMES.getName() + " argument will be ignored for the following commands: " + CleanerUtils.toString(unsupportedCommands)); //$NON-NLS-1$ //$NON-NLS-2$																
				}
			}
			else{
				
				propertyPrefixedNames = new ArrayList<String>();
				propertyPrefixedNames.addAll(Arrays.asList(SUPPORTED_QM_PROPERTY_PREFIXED_NAMES));
				propertyPrefixedNames.addAll(Arrays.asList(SUPPORTED_RM_PROPERTY_PREFIXED_NAMES));
			}
			
			Map<String, List<String>> propertyPrefixedNamePropertyLabelsMap = null;

			if (cmdArgs.contains(CmdLineArg.PROPERTY_LABELS)) {

				String[] propertyLabelValues = CmdLineArg.PROPERTY_LABELS.getValue().split(","); //$NON-NLS-1$

				if(propertyLabelValues.length == 0){
					throw new IllegalArgumentException(CmdLineArg.PROPERTY_LABELS.getName() + " requires one (or more) valid property labels"); //$NON-NLS-1$
				}

				propertyPrefixedNamePropertyLabelsMap = new HashMap<String, List<String>>();

				for(String propertyLabelValue : propertyLabelValues){

					String[] propertyLabelProperties = propertyLabelValue.split(Pattern.quote(DELIMITER_COMPOUND_ARGUMENT_VALUE));

					if(propertyLabelProperties.length != 2) {
						throw new IllegalArgumentException(CmdLineArg.PROPERTY_LABELS.getName() + " requires a valid property prefixed name and property label");		 //$NON-NLS-1$
					}

					String propertyPrefixedName = propertyLabelProperties[0];

					if((!CleanerUtils.contains(SUPPORTED_QM_PROPERTY_PREFIXED_NAMES, propertyPrefixedName)) && (!CleanerUtils.contains(SUPPORTED_RM_PROPERTY_PREFIXED_NAMES, propertyPrefixedName))){
						throw new IllegalArgumentException(CmdLineArg.PROPERTY_LABELS.getName() + " contains an invalid property prefixed name '" + propertyPrefixedName + "'"); //$NON-NLS-1$ //$NON-NLS-2$						
					}

					String propertyLabel = propertyLabelProperties[1];

					if(StringUtils.isNotSet(propertyLabel)){
						throw new IllegalArgumentException(CmdLineArg.PROPERTY_LABELS.getName() + " requires a valid property label");						 //$NON-NLS-1$
					}

					List<String> propertyLabels = propertyPrefixedNamePropertyLabelsMap.get(propertyPrefixedName);

					if(propertyLabels == null){

						propertyLabels = new ArrayList<String>();

						propertyPrefixedNamePropertyLabelsMap.put(propertyPrefixedName, propertyLabels);
					}

					if(!propertyLabels.contains(propertyLabel)) {
						propertyLabels.add(propertyLabel);
					}
				}
			}

			List<String> ignoreResourceUrls = null;

			if (cmdArgs.contains(CmdLineArg.IGNORE_RESOURCE_URLS)) {

				String[] ignoreResourceUrlValues = CmdLineArg.IGNORE_RESOURCE_URLS.getValue().split(","); //$NON-NLS-1$

				if(ignoreResourceUrlValues.length == 0){
					throw new IllegalArgumentException(CmdLineArg.IGNORE_RESOURCE_URLS.getName() + " requires one (or more) valid URLs"); //$NON-NLS-1$
				}

				for(String ignoreResourceUrlValue : ignoreResourceUrlValues){

					if(!UrlUtils.isValidUrl(ignoreResourceUrlValue)){
						throw new IllegalArgumentException(CmdLineArg.IGNORE_RESOURCE_URLS.getName() + " contains an invalid URL '" + ignoreResourceUrlValue + "'"); //$NON-NLS-1$ //$NON-NLS-2$						
					}
				}

				ignoreResourceUrls = new ArrayList<String>(Arrays.asList(ignoreResourceUrlValues));				
			}
			
			List<String> processResourceUrls = null;

			if (cmdArgs.contains(CmdLineArg.PROCESS_RESOURCE_URLS)) {

				String[] processResourceUrlValues = CmdLineArg.PROCESS_RESOURCE_URLS.getValue().split(","); //$NON-NLS-1$

				if(processResourceUrlValues.length == 0){
					throw new IllegalArgumentException(CmdLineArg.PROCESS_RESOURCE_URLS.getName() + " requires one (or more) valid URLs"); //$NON-NLS-1$
				}

				for(String processResourceUrlValue : processResourceUrlValues){

					if(!UrlUtils.isValidUrl(processResourceUrlValue)){
						throw new IllegalArgumentException(CmdLineArg.PROCESS_RESOURCE_URLS.getName() + " contains an invalid URL '" + processResourceUrlValue + "'"); //$NON-NLS-1$ //$NON-NLS-2$						
					}
				}

				processResourceUrls = new ArrayList<String>(Arrays.asList(processResourceUrlValues));				
			}

			List<String> ignoreTargetResourceUrls = null;

			if (cmdArgs.contains(CmdLineArg.IGNORE_TARGET_RESOURCE_URLS)) {

				String[] ignoreTargetResourceUrlValues = CmdLineArg.IGNORE_TARGET_RESOURCE_URLS.getValue().split(","); //$NON-NLS-1$

				if(ignoreTargetResourceUrlValues.length == 0){
					throw new IllegalArgumentException(CmdLineArg.IGNORE_TARGET_RESOURCE_URLS.getName() + " requires one (or more) valid URLs"); //$NON-NLS-1$
				}

				for(String ignoreTargetResourceUrlValue : ignoreTargetResourceUrlValues){

					if(!UrlUtils.isValidUrl(ignoreTargetResourceUrlValue)){
						throw new IllegalArgumentException(CmdLineArg.IGNORE_TARGET_RESOURCE_URLS.getName() + " contains an invalid URL '" + ignoreTargetResourceUrlValue + "'"); //$NON-NLS-1$ //$NON-NLS-2$						
					}
				}

				ignoreTargetResourceUrls = new ArrayList<String>(Arrays.asList(ignoreTargetResourceUrlValues));				
			}

			List<String> processTargetResourceUrls = null;

			if (cmdArgs.contains(CmdLineArg.PROCESS_TARGET_RESOURCE_URLS)) {

				String[] processTargetResourceUrlValues = CmdLineArg.PROCESS_TARGET_RESOURCE_URLS.getValue().split(","); //$NON-NLS-1$

				if(processTargetResourceUrlValues.length == 0){
					throw new IllegalArgumentException(CmdLineArg.PROCESS_TARGET_RESOURCE_URLS.getName() + " requires one (or more) valid URLs"); //$NON-NLS-1$
				}

				for(String processTargetResourceUrlValue : processTargetResourceUrlValues){

					if(!UrlUtils.isValidUrl(processTargetResourceUrlValue)){
						throw new IllegalArgumentException(CmdLineArg.PROCESS_TARGET_RESOURCE_URLS.getName() + " contains an invalid URL '" + processTargetResourceUrlValue + "'"); //$NON-NLS-1$ //$NON-NLS-2$						
					}
				}

				processTargetResourceUrls = new ArrayList<String>(Arrays.asList(processTargetResourceUrlValues));				
			}

			List<String> trs2ProviderTypes = null;

			if (cmdArgs.contains(CmdLineArg.TRS2_PROVIDER_TYPES)) {

				if (cmdArgs.contains(CmdLineArg.INPUT_DIRECTORY)) {
					throw new IllegalArgumentException(CmdLineArg.TRS2_PROVIDER_TYPES.getName() + " cannot be used with " + CmdLineArg.INPUT_DIRECTORY.getName()); //$NON-NLS-1$
				}
				
				String[] trs2ProviderTypesValues = CmdLineArg.TRS2_PROVIDER_TYPES.getValue().split(","); //$NON-NLS-1$

				if(trs2ProviderTypesValues.length == 0){
					throw new IllegalArgumentException(CmdLineArg.TRS2_PROVIDER_TYPES.getName() + " requires one (or more) valid TRS2 provider types"); //$NON-NLS-1$
				}

				for(String trs2ProviderType : trs2ProviderTypesValues){

					if(!CleanerUtils.contains(SUPPORTED_TRS2_PROVIDER_TYPES, trs2ProviderType)){
						throw new IllegalArgumentException(CmdLineArg.TRS2_PROVIDER_TYPES.getName() + " contains an invalid TRS2 provider type '" + trs2ProviderType + "'"); //$NON-NLS-1$ //$NON-NLS-2$						
					}
				}

				trs2ProviderTypes = new ArrayList<String>(Arrays.asList(trs2ProviderTypesValues));

				if((!qmCommandsTypeIndependent.isEmpty()) && ((!rmCommands.isEmpty()) || (!cmCommands.isEmpty()) || (!rmOnlyCommands.isEmpty()))){
					LogUtils.logWarning("Note: The " + CmdLineArg.TRS2_PROVIDER_TYPES.getName() + " argument will only be used for the following commands: " + CleanerUtils.toString(qmCommandsTypeIndependent)); //$NON-NLS-1$ //$NON-NLS-2$											
				}
			}
			else{
				trs2ProviderTypes = new ArrayList<String>(Arrays.asList(SUPPORTED_TRS2_PROVIDER_TYPES));
			}

			File inputDirectory = null;
			
			if (cmdArgs.contains(CmdLineArg.INPUT_DIRECTORY)) {
				
				inputDirectory = new File(CmdLineArg.INPUT_DIRECTORY.getValue());
				
				if(!inputDirectory.isDirectory()){
					throw new IllegalArgumentException(CmdLineArg.INPUT_DIRECTORY.getName() + " requires a valid relative directory name or absolute directory path to a directory that exists"); //$NON-NLS-1$
				}
			}

			File outputDirectory = null;
			
			if (cmdArgs.contains(CmdLineArg.OUTPUT_DIRECTORY)) {
				
				outputDirectory = new File(CmdLineArg.OUTPUT_DIRECTORY.getValue());
				
				if(!outputDirectory.isDirectory()){
					throw new IllegalArgumentException(CmdLineArg.OUTPUT_DIRECTORY.getName() + " requires a valid relative directory name or absolute directory path to a directory that exists"); //$NON-NLS-1$
				}
			}

			boolean isOffineMode = ((inputDirectory != null) && (commands.length == 1) && (CLEANING_COMMAND_VALIDATE_TRS2_NO_SKIPPED_RESOURCES.equals(commands[0])));
			URL qmServerUrl = null;
			URL rmServerUrl = null;
			Map<URL, Credential> credentials = null;
			List<ServiceProvider> qmServiceProviders = null;
			List<ServiceProvider> rmServiceProviders = null;

			if (cmdArgs.contains(CmdLineArg.QUALITY_MANAGER_URL)) {

				String url = CmdLineArg.QUALITY_MANAGER_URL.getValue();

				if(!UrlUtils.isValidUrl(url)){
					throw new IllegalArgumentException(CmdLineArg.QUALITY_MANAGER_URL.getName() + " requires a valid URL"); //$NON-NLS-1$					
				}

				url = UrlUtils.appendTrailingForwardSlash(url);

				qmServerUrl = new URL(url);

				//Validate the server URL by resolving the context root without the leading/trailing slash characters and checking for slash characters:
				String path = qmServerUrl.getPath();
				
				if((StringUtils.isNotSet(path)) || (path.trim().equals(FORWARD_SLASH))){
					throw new IllegalArgumentException(CmdLineArg.QUALITY_MANAGER_URL.getName() + " requires a valid server URL"); //$NON-NLS-1$
				}

				path = path.substring(1);
				path = path.substring(0, (path.length() - 1));

				if(path.contains(FORWARD_SLASH)){
					throw new IllegalArgumentException(CmdLineArg.QUALITY_MANAGER_URL.getName() + " requires a valid server URL"); //$NON-NLS-1$
				}
			} 
			else if((!isOffineMode) && (containsQmResourceType)) {
				throw new IllegalArgumentException(CmdLineArg.QUALITY_MANAGER_URL.getName() + " is required"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			if (cmdArgs.contains(CmdLineArg.REQUIREMENTS_MANAGER_URL)) {

				String url = CmdLineArg.REQUIREMENTS_MANAGER_URL.getValue();

				if(!UrlUtils.isValidUrl(url)){
					throw new IllegalArgumentException(CmdLineArg.REQUIREMENTS_MANAGER_URL.getName() + " requires a valid URL"); //$NON-NLS-1$					
				}

				url = UrlUtils.appendTrailingForwardSlash(url);

				rmServerUrl = new URL(url);

				//Validate the server URL by resolving the context root without the leading/trailing slash characters and checking for slash characters:
				String path = rmServerUrl.getPath();
				path = path.substring(1);
				path = path.substring(0, (path.length() - 1));

				if(path.contains(FORWARD_SLASH)){
					throw new IllegalArgumentException(CmdLineArg.REQUIREMENTS_MANAGER_URL.getName() + " requires a valid server URL"); //$NON-NLS-1$
				}
			}
			else if(containsRmResourceType){				
				throw new IllegalArgumentException(CmdLineArg.REQUIREMENTS_MANAGER_URL.getName() + " is required"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			//Connect to the server:
			if(!isOffineMode) {

				if(!cmdArgs.contains(CmdLineArg.CREDENTIALS)) {
					throw new IllegalArgumentException(CmdLineArg.CREDENTIALS.getName() + " is required"); //$NON-NLS-1$
				}

				if((qmServerUrl != null) && (containsQmResourceType)) {

					LogUtils.logInfo("Connecting to server '" + qmServerUrl + "'."); //$NON-NLS-1$ //$NON-NLS-2$

					qmOslcHttpClient = OslcHttpClientFactory.getInstance().getClient(qmServerUrl);    

					if (cmdArgs.contains(CmdLineArg.PROJECT_AREA)) {

						String[] projectAreaValues = CmdLineArg.PROJECT_AREA.getValue().split("\\,"); //$NON-NLS-1$

						if(projectAreaValues.length == 0){
							throw new IllegalArgumentException(CmdLineArg.PROJECT_AREA.getName() + " requires one (or more) valid project area names"); //$NON-NLS-1$
						}

						qmServiceProviders = new ArrayList<ServiceProvider>();

						for(String projectAreaValue : projectAreaValues){

							String projectAreaName = projectAreaValue;
							
							if(projectAreaValue.contains(DELIMITER_COMPOUND_ARGUMENT_VALUE)) {

								String[] projectAreaProperties = projectAreaValue.split(Pattern.quote(DELIMITER_COMPOUND_ARGUMENT_VALUE));

								if(projectAreaProperties.length == 2) {

									String productServerUrlString = UrlUtils.appendTrailingForwardSlash(projectAreaProperties[0]);

									if(UrlUtils.isValidUrl(productServerUrlString)){

										URL productServerUrl = new URL(productServerUrlString);

										if(productServerUrl.equals(qmServerUrl)) {
											projectAreaName = projectAreaProperties[1];
										}
										else {
											continue;
										}
									}
									else {
										throw new IllegalArgumentException(CmdLineArg.PROJECT_AREA.getName() + " requires a valid product URL with each project area name"); //$NON-NLS-1$
									}
								}
								else {
									throw new IllegalArgumentException(CmdLineArg.PROJECT_AREA.getName() + " requires one product URL with each project area name"); //$NON-NLS-1$
								}
							}

							if(StringUtils.isNotSet(projectAreaName)){
								throw new IllegalArgumentException(CmdLineArg.PROJECT_AREA.getName() + " requires a valid project area name"); //$NON-NLS-1$
							}

							ServiceProvider serviceProvider = qmOslcHttpClient.getServiceProviderByTitle(projectAreaName);

							if(serviceProvider != null){
								qmServiceProviders.add(serviceProvider);
							}
							else{
								LogUtils.logError("Error resolving the service provider for project area '" + projectAreaName + "'."); //$NON-NLS-1$ //$NON-NLS-2$						
							}
						}
					}
					else{
						qmServiceProviders = qmOslcHttpClient.getServiceProviders();
					}

					if(qmServiceProviders.size() == 0){
						throw new IllegalArgumentException(CmdLineArg.QUALITY_MANAGER_URL.getName() + " requires a valid server with one (or more) valid service providers"); //$NON-NLS-1$
					}
					
					if (cmdArgs.contains(CmdLineArg.CONFIGURATION_CONTEXTS)) {

						String[] configurationContextValues = CmdLineArg.CONFIGURATION_CONTEXTS.getValue().split("\\,"); //$NON-NLS-1$

						if(configurationContextValues.length == 0){
							throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " requires one (or more) valid configuration context URLs"); //$NON-NLS-1$
						}

						for(String configurationContextValue : configurationContextValues){

							if(configurationContextValue.contains(DELIMITER_COMPOUND_ARGUMENT_VALUE)) {

								String[] configurationContextProperties = configurationContextValue.split(Pattern.quote(DELIMITER_COMPOUND_ARGUMENT_VALUE));
								String productServerUrlString = null;
								String projectAreaName = null;
								String configurationContextUrl = null;
								
								if(configurationContextProperties.length == 2) {

									projectAreaName = configurationContextProperties[0];
									configurationContextUrl = configurationContextProperties[1];									
								}
								else if(configurationContextProperties.length == 3) {

									productServerUrlString = configurationContextProperties[0];
									projectAreaName = configurationContextProperties[1];
									configurationContextUrl = configurationContextProperties[2];									
								}
								else {
									throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " requires one product URL and/or project area name with each configuration context URL"); //$NON-NLS-1$
								}

								if(StringUtils.isNotSet(configurationContextUrl)) {
									throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " requires a valid configuration context URL"); //$NON-NLS-1$
								}
								
								if(StringUtils.isSet(productServerUrlString)){
									
									productServerUrlString = UrlUtils.appendTrailingForwardSlash(productServerUrlString);
									
									if(UrlUtils.isValidUrl(productServerUrlString)){

										URL productServerUrl = new URL(productServerUrlString);

										if(!productServerUrl.equals(qmServerUrl)) {
											continue;
										}
									}
									else {
										throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " requires a valid product URL with each project area name and configuration context URL"); //$NON-NLS-1$
									}
								}
								
								if(StringUtils.isSet(projectAreaName)){
							
									ServiceProvider serviceProvider = qmOslcHttpClient.getServiceProviderByTitle(projectAreaName);

									if(serviceProvider != null){
										serviceProvider.setConfigurationContextUrl(configurationContextUrl);
									}
									else{
										LogUtils.logError("Error resolving the service provider for project area '" + projectAreaName + "' configuration configuration context '" + configurationContextUrl + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$						
									}

								}
								else {
									throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " requires a valid project area name with each configuration context URL"); //$NON-NLS-1$									
								}
							}
							else {
								throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " requires a supported product URL and/or project area name with configuration context URL"); //$NON-NLS-1$									
							}
						}
					}
				}

				if((rmServerUrl != null) && (containsRmResourceType)) {

					LogUtils.logInfo("Connecting to server '" + rmServerUrl + "'."); //$NON-NLS-1$ //$NON-NLS-2$

					rmOslcHttpClient = OslcHttpClientFactory.getInstance().getClient(rmServerUrl);    

					if (cmdArgs.contains(CmdLineArg.PROJECT_AREA)) {

						String[] projectAreaValues = CmdLineArg.PROJECT_AREA.getValue().split("\\,"); //$NON-NLS-1$

						if(projectAreaValues.length == 0){
							throw new IllegalArgumentException(CmdLineArg.PROJECT_AREA.getName() + " requires one (or more) valid project area names"); //$NON-NLS-1$
						}

						rmServiceProviders = new ArrayList<ServiceProvider>();

						for(String projectAreaValue : projectAreaValues){

							String projectAreaName = projectAreaValue;
							
							if(projectAreaValue.contains(DELIMITER_COMPOUND_ARGUMENT_VALUE)) {

								String[] projectAreaProperties = projectAreaValue.split(Pattern.quote(DELIMITER_COMPOUND_ARGUMENT_VALUE));

								if(projectAreaProperties.length == 2) {

									String productServerUrlString = UrlUtils.appendTrailingForwardSlash(projectAreaProperties[0]);

									if(UrlUtils.isValidUrl(productServerUrlString)){

										URL productServerUrl = new URL(productServerUrlString);

										if(productServerUrl.equals(rmServerUrl)) {
											projectAreaName = projectAreaProperties[1];
										}
										else {
											continue;
										}
									}
									else {
										throw new IllegalArgumentException(CmdLineArg.PROJECT_AREA.getName() + " requires a valid product URL with each project area name"); //$NON-NLS-1$
									}
								}
								else {
									throw new IllegalArgumentException(CmdLineArg.PROJECT_AREA.getName() + " requires one product URL with each project area name"); //$NON-NLS-1$
								}
							}

							if(StringUtils.isNotSet(projectAreaName)){
								throw new IllegalArgumentException(CmdLineArg.PROJECT_AREA.getName() + " requires a valid project area name"); //$NON-NLS-1$
							}

							ServiceProvider serviceProvider = rmOslcHttpClient.getServiceProviderByTitle(projectAreaName);

							if(serviceProvider != null){
								rmServiceProviders.add(serviceProvider);
							}
							else{
								LogUtils.logError("Error resolving the service provider for project area '" + projectAreaName + "'."); //$NON-NLS-1$ //$NON-NLS-2$						
							}
						}
					}
					else{
						rmServiceProviders = rmOslcHttpClient.getServiceProviders();
					}

					if(rmServiceProviders.size() == 0){
						throw new IllegalArgumentException(CmdLineArg.REQUIREMENTS_MANAGER_URL.getName() + " requires a valid server with one (or more) valid service providers"); //$NON-NLS-1$
					}
					
					if (cmdArgs.contains(CmdLineArg.CONFIGURATION_CONTEXTS)) {

						String[] configurationContextValues = CmdLineArg.CONFIGURATION_CONTEXTS.getValue().split("\\,"); //$NON-NLS-1$

						if(configurationContextValues.length == 0){
							throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " requires one (or more) valid configuration context URLs"); //$NON-NLS-1$
						}

						for(String configurationContextValue : configurationContextValues){

							if(configurationContextValue.contains(DELIMITER_COMPOUND_ARGUMENT_VALUE)) {

								String[] configurationContextProperties = configurationContextValue.split(Pattern.quote(DELIMITER_COMPOUND_ARGUMENT_VALUE));
								String productServerUrlString = null;
								String projectAreaName = null;
								String configurationContextUrl = null;
								
								if(configurationContextProperties.length == 2) {

									projectAreaName = configurationContextProperties[0];
									configurationContextUrl = configurationContextProperties[1];									
								}
								else if(configurationContextProperties.length == 3) {

									productServerUrlString = configurationContextProperties[0];
									projectAreaName = configurationContextProperties[1];
									configurationContextUrl = configurationContextProperties[2];									
								}
								else {
									throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " requires one product URL and/or project area name with each configuration context URL"); //$NON-NLS-1$
								}

								if(StringUtils.isNotSet(configurationContextUrl)) {
									throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " requires a valid configuration context URL"); //$NON-NLS-1$
								}

								if(StringUtils.isSet(productServerUrlString)){
									
									productServerUrlString = UrlUtils.appendTrailingForwardSlash(productServerUrlString);
									
									if(UrlUtils.isValidUrl(productServerUrlString)){

										URL productServerUrl = new URL(productServerUrlString);

										if(!productServerUrl.equals(rmServerUrl)) {
											continue;
										}
									}
									else {
										throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " requires a valid product URL with each project area name and configuration context URL"); //$NON-NLS-1$
									}
								}
								
								if(StringUtils.isSet(projectAreaName)){
							
									ServiceProvider serviceProvider = rmOslcHttpClient.getServiceProviderByTitle(projectAreaName);

									if(serviceProvider != null){
										serviceProvider.setConfigurationContextUrl(configurationContextUrl);
									}
									else{
										LogUtils.logError("Error resolving the service provider for project area '" + projectAreaName + "' configuration configuration context '" + configurationContextUrl + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$						
									}

								}
								else {
									throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " requires a valid project area name with each configuration context URL"); //$NON-NLS-1$									
								}
							}
							else {
								throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " requires a supported product URL and/or project area name with configuration context URL"); //$NON-NLS-1$									
							}
						}
					}
				}
			}

			long lastModifiedDateTime = -1;

			if (cmdArgs.contains(CmdLineArg.LAST_MODIFIED)) {

				//Parse the date/time:
				lastModifiedDateTime = DateTimeUtils.parseDateTime(CmdLineArg.LAST_MODIFIED.getValue());

				if(lastModifiedDateTime == -1){

					try {

						//Parse the number of seconds:
						long longDateTime = Long.parseLong(CmdLineArg.LAST_MODIFIED.getValue());

						//Note: A positive numerical value (greater than 0) is required.
						//Note: 1 second = 1000 milliseconds
						if(longDateTime > 0){
							lastModifiedDateTime = (System.currentTimeMillis() - (longDateTime * 1000));
						}
					} 
					catch (NumberFormatException n) {
						//Ignore since an invalid value.
					}
				}

				if(lastModifiedDateTime == -1){
					throw new IllegalArgumentException(CmdLineArg.LAST_MODIFIED.getName() + " requires a valid date/time or number of seconds"); //$NON-NLS-1$					
				}
			}

			List<String> resourceIdentifiers = null;

			if (cmdArgs.contains(CmdLineArg.RESOURCE_IDENTIFIER)) {

				String[] resourceIdentifierValues = CmdLineArg.RESOURCE_IDENTIFIER.getValue().split(","); //$NON-NLS-1$

				if(resourceIdentifierValues.length == 0){
					throw new IllegalArgumentException(CmdLineArg.RESOURCE_IDENTIFIER.getName() + " requires one (or more) valid resource identifiers"); //$NON-NLS-1$
				}

				for(String resourceIdentifierValue : resourceIdentifierValues){

					if(StringUtils.isNotSet(resourceIdentifierValue)){
						throw new IllegalArgumentException(CmdLineArg.RESOURCE_IDENTIFIER.getName() + " contains an invalid resource identifier '" + resourceIdentifierValue + "'"); //$NON-NLS-1$ //$NON-NLS-2$						
					}
				}

				resourceIdentifiers = new ArrayList<String>(Arrays.asList(resourceIdentifierValues));				
			}

			//Enforce support for configuration management (commands/resources/properties):			
			if (cmdArgs.contains(CmdLineArg.CONFIGURATION_CONTEXTS)) {

				List<String> supportedQmCommands = new ArrayList<String>();
				supportedQmCommands.add(CLEANING_COMMAND_REMOVE_LINK_CONFIG_CONTEXT);
				supportedQmCommands.add(CLEANING_COMMAND_REMOVE_BROKEN_FORWARD_LINKS);
				supportedQmCommands.add(CLEANING_COMMAND_REMOVE_ALL_FORWARD_LINKS);
				supportedQmCommands.add(CLEANING_COMMAND_REMOVE_BROKEN_FORWARD_LINKS_WITH_MISSING_BACK_LINKS);
				supportedQmCommands.add(CLEANING_COMMAND_UPDATE_LINK_LABELS);
				supportedQmCommands.add(CLEANING_COMMAND_UPDATE_FORWARD_LINK_LABELS);

				List<String> supportedQmRmCommands = new ArrayList<String>();
				supportedQmRmCommands.add(CLEANING_COMMAND_REPORT_BROKEN_LINKS);
				
				List<String> supportedQmResourceTypes = new ArrayList<String>();
				supportedQmResourceTypes.add(RESOURCE_OSLC_QM_TEST_PLAN);
				supportedQmResourceTypes.add(RESOURCE_OSLC_QM_TEST_CASE);
				supportedQmResourceTypes.add(RESOURCE_RQM_QM_TEST_SCRIPT_STEP);

				List<String> supportedQmPropertyPrefixedNames = new ArrayList<String>();
				supportedQmPropertyPrefixedNames.add(PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT_COLLECTION);
				supportedQmPropertyPrefixedNames.add(PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT);							

				List<String> unsupportedQmCommands = new ArrayList<String>(rmCommands);
				//Note: Remove duplicates before adding.
				unsupportedQmCommands.removeAll(cmCommands);
				unsupportedQmCommands.addAll(cmCommands);
				unsupportedQmCommands.removeAll(supportedQmRmCommands);

				validate(resourceTypes, propertyPrefixedNames, unsupportedQmCommands, supportedQmCommands, supportedQmResourceTypes, supportedQmPropertyPrefixedNames);				

				List<String> supportedQmRmResourceTypes = new ArrayList<String>(supportedQmResourceTypes);
				supportedQmRmResourceTypes.add(RESOURCE_OSLC_RM_REQUIREMENT);
				supportedQmRmResourceTypes.add(RESOURCE_OSLC_RM_REQUIREMENT_COLLECTION);

				List<String> supportedQmRmPropertyPrefixedNames = new ArrayList<String>(supportedQmPropertyPrefixedNames);
				supportedQmRmPropertyPrefixedNames.add(PROPERTY_OSLC_RM_VALIDATED_BY);							

				List<String> unsupportedQmRmCommands = new ArrayList<String>(rmCommands);
				//Note: Remove duplicates before adding.
				unsupportedQmRmCommands.removeAll(cmCommands);
				unsupportedQmRmCommands.addAll(cmCommands);
				unsupportedQmRmCommands.removeAll(supportedQmCommands);

				validate(resourceTypes, propertyPrefixedNames, unsupportedQmRmCommands, supportedQmRmCommands, supportedQmRmResourceTypes, supportedQmRmPropertyPrefixedNames);
			}

			String oldPublicUrl = CmdLineArg.OLD_PUBLIC_URL.getValue();

			String newPublicUrl = CmdLineArg.NEW_PUBLIC_URL.getValue();

			boolean ignoreReadErrors = cmdArgs.contains(CmdLineArg.IGNORE_READ_ERRORS);

			boolean output = cmdArgs.contains(CmdLineArg.OUTPUT);

			boolean test = cmdArgs.contains(CmdLineArg.TEST);

			boolean usePrivateDngApi = cmdArgs.contains(CmdLineArg.USE_PRIVATE_DNG_API);

			boolean attemptRqmUnlock = cmdArgs.contains(CmdLineArg.ATTEMPT_RQM_UNLOCK);

			int pageSize = -1;

			if (cmdArgs.contains(CmdLineArg.PAGE_SIZE)) {

				try {

					int pageSizeValue = Integer.parseInt(CmdLineArg.PAGE_SIZE.getValue());

					//Note: A positive numerical value (greater than 0) is required.
					if(pageSizeValue > 0){
						pageSize = pageSizeValue;
					}
				} 
				catch (NumberFormatException n) {
					//Ignore since an invalid value.
				}

				if(pageSize == -1){
					throw new IllegalArgumentException(CmdLineArg.PAGE_SIZE.getName() + " requires a valid page size"); //$NON-NLS-1$					
				}
			}
			else {
				pageSize = OSLC_DEFAULT_VALUE_PAGE_SIZE;
			}
			
			LogUtils.logInfo("Starting the OSLC Cleaner Utility."); //$NON-NLS-1$

			if(test){
				LogUtils.logInfo("Note: Test run (no resources will be updated)."); //$NON-NLS-1$		
			}
			
			//Clean the test resources in each project area:
			if(!rmOnlyCommands.isEmpty()){

				//Note: RM command (requires RM HTTP client and service provider(s)).
				if(resourceTypes.contains(RESOURCE_OSLC_RM_REQUIREMENT_COLLECTION)){

					if(propertyPrefixedNames.contains(PROPERTY_OSLC_RM_VALIDATED_BY)) {
					
						Cleaner cleaner = new Cleaner(rmOnlyCommands, rmOslcHttpClient, credentials, rmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_REQUIREMENT_COLLECTION, RESOURCE_TYPE_URI_TEST_PLAN, PROPERTY_OSLC_RM_VALIDATED_BY, PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT_COLLECTION);
					}
				}

				//Note: RM command (requires RM HTTP client and service provider(s)).
				if(resourceTypes.contains(RESOURCE_OSLC_RM_REQUIREMENT)){

					if(propertyPrefixedNames.contains(PROPERTY_OSLC_RM_VALIDATED_BY)) {

						Cleaner cleaner = new Cleaner(rmOnlyCommands, rmOslcHttpClient, credentials, rmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_REQUIREMENT, RESOURCE_TYPE_URI_TEST_CASE, PROPERTY_OSLC_RM_VALIDATED_BY, PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT);
					}
				}
			}
			
			if(!rmCommands.isEmpty()){

				//Note: RM command (requires RM HTTP client and service provider(s)).
				if(resourceTypes.contains(RESOURCE_OSLC_RM_REQUIREMENT_COLLECTION)){

					if(propertyPrefixedNames.contains(PROPERTY_OSLC_RM_VALIDATED_BY)) {
					
						Cleaner cleaner = new Cleaner(rmCommands, rmOslcHttpClient, credentials, rmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_REQUIREMENT_COLLECTION, RESOURCE_TYPE_URI_TEST_PLAN, PROPERTY_OSLC_RM_VALIDATED_BY, PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT_COLLECTION);
					}
				}

				//Note: RM command (requires RM HTTP client and service provider(s)).
				if(resourceTypes.contains(RESOURCE_OSLC_RM_REQUIREMENT)){

					if(propertyPrefixedNames.contains(PROPERTY_OSLC_RM_VALIDATED_BY)) {

						Cleaner cleaner = new Cleaner(rmCommands, rmOslcHttpClient, credentials, rmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_REQUIREMENT, RESOURCE_TYPE_URI_TEST_CASE, PROPERTY_OSLC_RM_VALIDATED_BY, PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT);
					}
				}

				if(resourceTypes.contains(RESOURCE_OSLC_QM_TEST_PLAN)){

					if(propertyPrefixedNames.contains(PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT_COLLECTION)) {
					
						Cleaner cleaner = new Cleaner(rmCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_TEST_PLAN, RESOURCE_TYPE_URI_REQUIREMENT_COLLECTION, PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT_COLLECTION, PROPERTY_OSLC_RM_VALIDATED_BY);
					}
				}
				
				if(resourceTypes.contains(RESOURCE_OSLC_QM_TEST_CASE)){

					if(propertyPrefixedNames.contains(PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT)) {

						Cleaner cleaner = new Cleaner(rmCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_TEST_CASE, RESOURCE_TYPE_URI_REQUIREMENT, PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT, PROPERTY_OSLC_RM_VALIDATED_BY);
					}
				}
				
				if(resourceTypes.contains(RESOURCE_RQM_QM_TEST_SCRIPT_STEP)){

					if(propertyPrefixedNames.contains(PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT)) {

						Cleaner cleaner = new Cleaner(rmCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_TEST_SCRIPT_STEP, RESOURCE_TYPE_URI_REQUIREMENT, PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT, PROPERTY_OSLC_RM_VALIDATED_BY);
					}
				}
			}

			if(!cmCommands.isEmpty()){

				if(resourceTypes.contains(RESOURCE_OSLC_QM_TEST_PLAN)){

					if(propertyPrefixedNames.contains(PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST)) {

						Cleaner cleaner = new Cleaner(cmCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_TEST_PLAN, RESOURCE_TYPE_URI_CHANGE_REQUEST, PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST, PROPERTY_OSLC_CM_RELATED_TEST_PLAN);
					}

					//Note: The forward link (calm:testsDevelopmentPlan) in the Test Plan (oslc_qm:TestPlan) and the back link (calm:testedByTestPlan) in the Change Request (oslc_cm:ChangeRequest) are in the CALM (calm) name space.
					if(propertyPrefixedNames.contains(PROPERTY_CALM_TESTS_DEV_PLAN)) {

						Cleaner cleaner = new Cleaner(cmCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_TEST_PLAN, RESOURCE_TYPE_URI_CHANGE_REQUEST, PROPERTY_CALM_TESTS_DEV_PLAN, PROPERTY_CALM_TESTED_BY_TEST_PLAN);
					}
				}

				if(resourceTypes.contains(RESOURCE_OSLC_QM_TEST_CASE)){

					if(propertyPrefixedNames.contains(PROPERTY_OSLC_QM_TESTS_CHANGE_REQUEST)) {

						Cleaner cleaner = new Cleaner(cmCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_TEST_CASE, RESOURCE_TYPE_URI_CHANGE_REQUEST, PROPERTY_OSLC_QM_TESTS_CHANGE_REQUEST, PROPERTY_OSLC_CM_TESTED_BY_TEST_CASE);
					}

					if(propertyPrefixedNames.contains(PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST)) {

						Cleaner cleaner = new Cleaner(cmCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_TEST_CASE, RESOURCE_TYPE_URI_CHANGE_REQUEST, PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST, PROPERTY_OSLC_CM_RELATED_TEST_CASE);
					}
				}

				if(resourceTypes.contains(RESOURCE_OSLC_QM_TEST_SCRIPT)){

					if(propertyPrefixedNames.contains(PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST)) {

						Cleaner cleaner = new Cleaner(cmCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_TEST_SCRIPT, RESOURCE_TYPE_URI_CHANGE_REQUEST, PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST, PROPERTY_OSLC_CM_RELATED_TEST_SCRIPT);
					}
				}

				if(resourceTypes.contains(RESOURCE_OSLC_QM_TEST_EXECUTION_RECORD)){

					if(propertyPrefixedNames.contains(PROPERTY_OSLC_QM_BLOCKED_BY_CHANGE_REQUEST)) {

						Cleaner cleaner = new Cleaner(cmCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_TEST_EXECUTION_RECORD, RESOURCE_TYPE_URI_CHANGE_REQUEST, PROPERTY_OSLC_QM_BLOCKED_BY_CHANGE_REQUEST, PROPERTY_OSLC_CM_BLOCKS_TEST_EXECUTION_RECORD);
					}

					if(propertyPrefixedNames.contains(PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST)) {

						Cleaner cleaner = new Cleaner(cmCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_TEST_EXECUTION_RECORD, RESOURCE_TYPE_URI_CHANGE_REQUEST, PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST, PROPERTY_OSLC_CM_RELATED_TEST_EXECUTION_RECORD);
					}
				}

				if(resourceTypes.contains(RESOURCE_OSLC_QM_TEST_RESULT)){

					if(propertyPrefixedNames.contains(PROPERTY_OSLC_QM_AFFECTED_BY_CHANGE_REQUEST)) {

						Cleaner cleaner = new Cleaner(cmCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_TEST_RESULT, RESOURCE_TYPE_URI_CHANGE_REQUEST, PROPERTY_OSLC_QM_AFFECTED_BY_CHANGE_REQUEST, PROPERTY_OSLC_CM_AFFECTS_TEST_RESULT);
					}
					
					//Note: The back link (rtc_cm:relatedTestResult) in the Change Request (oslc_cm:ChangeRequest) referencing Test Result (oslc_qm:TestResult) is in the RTC (rtc_cm) name space.
					if(propertyPrefixedNames.contains(PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST)) {

						Cleaner cleaner = new Cleaner(cmCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_TEST_RESULT, RESOURCE_TYPE_URI_CHANGE_REQUEST, PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST, PROPERTY_RTC_CM_RELATED_TEST_RESULT);
					}
				}

				if(resourceTypes.contains(RESOURCE_RQM_QM_TEST_SCRIPT_STEP_RESULT)){

					//Note: The Test Script Step Result (rqm_qm:TestScriptStepResult) contains a forward link (oslc_qm:affectedByChangeRequest) referencing a Change Request (oslc_cm:ChangeRequest), which contains a back link (oslc_cm:affectsTestResult) referencing Test Result (oslc_qm:TestResult) instead of the Test Script Step Result (rqm_qm:TestScriptStepResult).
					//Note: Some RQM versions do not include the Test Script Step Result query capability in the OSLC QM service provider document.  As such, the OSLC Cleaner Utility cannot clean Test Script Step Result resources.
					if(propertyPrefixedNames.contains(PROPERTY_OSLC_QM_AFFECTED_BY_CHANGE_REQUEST)) {

						Cleaner cleaner = new Cleaner(cmCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_TEST_SCRIPT_STEP_RESULT, RESOURCE_TYPE_URI_CHANGE_REQUEST, PROPERTY_OSLC_QM_AFFECTED_BY_CHANGE_REQUEST, PROPERTY_OSLC_CM_AFFECTS_TEST_RESULT);
					}

					//Note: The Test Script Step Result (rqm_qm:TestScriptStepResult) contains a forward link (oslc_qm:relatedChangeRequest) referencing a Change Request (oslc_cm:ChangeRequest), which contains a back link (rtc_cm:relatedTestResult) referencing Test Result (oslc_qm:TestResult) instead of the Test Script Step Result (rqm_qm:TestScriptStepResult).
					//Note: The back link (rtc_cm:relatedTestResult) in the Change Request (oslc_cm:ChangeRequest) referencing Test Result (oslc_qm:TestResult) is in the RTC (rtc_cm) name space.
					if(propertyPrefixedNames.contains(PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST)) {

						Cleaner cleaner = new Cleaner(cmCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
						cleaner.clean(RESOURCE_TYPE_URI_TEST_SCRIPT_STEP_RESULT, RESOURCE_TYPE_URI_CHANGE_REQUEST, PROPERTY_OSLC_QM_RELATED_CHANGE_REQUEST, PROPERTY_RTC_CM_RELATED_TEST_RESULT);
					}
				}
			}
			
			if(!qmCommandsTypeIndependent.isEmpty()){

				Cleaner cleaner = new Cleaner(qmCommandsTypeIndependent, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
				cleaner.clean();
			}
			
			if(!reportingCommands.isEmpty()){

				if(containsQmResourceType) {

					List<String> qmResourceTypes = new ArrayList<String>();
					
					for(String resourceType : resourceTypes){

						if(CleanerUtils.contains(SUPPORTED_QM_RESOURCE_TYPES, resourceType)) {
							qmResourceTypes.add(resourceType);
						}
					}

					Cleaner cleaner = new Cleaner(reportingCommands, qmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
					cleaner.report(qmResourceTypes);
				}

				if(containsRmResourceType) {

					List<String> rmResourceTypes = new ArrayList<String>();
					
					for(String resourceType : resourceTypes){

						if(CleanerUtils.contains(SUPPORTED_RM_RESOURCE_TYPES, resourceType)) {
							rmResourceTypes.add(resourceType);
						}
					}

					Cleaner cleaner = new Cleaner(reportingCommands, rmOslcHttpClient, credentials, qmServiceProviders, lastModifiedDateTime, oldPublicUrl, newPublicUrl, ignoreReadErrors, output, test, inputDirectory, outputDirectory, trs2ProviderTypes, ignoreResourceUrls, processResourceUrls, ignoreTargetResourceUrls, processTargetResourceUrls, propertyPrefixedNamePropertyLabelsMap, usePrivateDngApi, attemptRqmUnlock, pageSize, resourceIdentifiers);
					cleaner.report(rmResourceTypes);
				}
			}

			LogUtils.logInfo("OSLC Cleaner Utility has completed.");  //$NON-NLS-1$
		} 
		catch (Throwable t) {

			LogUtils.logError(t.toString(), t);

			LogUtils.logInfo("OSLC Cleaner Utility has terminated due to an error."); //$NON-NLS-1$
			
			systemExitCode = 1;
		}
		finally {

			OslcHttpClientFactory.getInstance().logoutClients();

			System.exit(systemExitCode);
		}
	}
	
	private static void validate(List<String> resourceTypes, List<String> propertyPrefixedNames, List<String> unsupportedCommands, List<String> supportedCommands, List<String> supportedResourceTypes, List<String> supportedPropertyPrefixedNames) throws IllegalArgumentException {

		if(!Collections.disjoint(unsupportedCommands, supportedCommands)) {

			unsupportedCommands.removeAll(supportedCommands);

			List<String> unsupportedResourceTypes = new ArrayList<String>(resourceTypes);

			if(!Collections.disjoint(unsupportedResourceTypes, supportedResourceTypes)) {

				unsupportedResourceTypes.removeAll(supportedResourceTypes);

				List<String> unsupportedPropertyPrefixedNames = new ArrayList<String>(propertyPrefixedNames);
				unsupportedPropertyPrefixedNames.removeAll(supportedPropertyPrefixedNames);

				if(!unsupportedPropertyPrefixedNames.isEmpty()) {
					throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " is not supported for the following property prefixed name" + ((unsupportedPropertyPrefixedNames.size() > 1) ? "s" : "") + ": " + CleanerUtils.toString(unsupportedPropertyPrefixedNames)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$						
				}
			}

			if(!unsupportedResourceTypes.isEmpty()) {
				throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " is not supported for the following resource type" + ((unsupportedResourceTypes.size() > 1) ? "s" : "") + ": " + CleanerUtils.toString(unsupportedResourceTypes)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$						
			}
		}

		if(!unsupportedCommands.isEmpty()) {				
			throw new IllegalArgumentException(CmdLineArg.CONFIGURATION_CONTEXTS.getName() + " is not supported for the following command" + ((unsupportedCommands.size() > 1) ? "s" : "") + ": " + CleanerUtils.toString(unsupportedCommands)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}
	
	private static List<CmdLineArg> processArgs(String[] args) throws IllegalArgumentException {
		ArrayList<CmdLineArg> argList = new ArrayList<CmdLineArg>();
		for (String arg : args) {
			boolean found = false;
			for (CmdLineArg cmd : CmdLineArg.values()) {
				String[] nameVal = arg.split("\\="); //$NON-NLS-1$
				if (cmd.isEqual(nameVal[0])) {
					
					CmdLineArg replacement = cmd.getReplacement();
					
					if(replacement != null){
						throw new IllegalArgumentException(cmd.getName() + " is an unsupported argument and replaced by the " + replacement.getName() + " argument"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					
					found = true;
					if (nameVal.length > 1 && !cmd.isValueRequired()) {
						throw new IllegalArgumentException(cmd.getName() + " requires no value"); //$NON-NLS-1$
					} else if (nameVal.length > 1) {
						cmd.setValue(nameVal[1]);
					} else if (cmd.isValueRequired()) {
						throw new IllegalArgumentException(cmd.getName() + " requires a value"); //$NON-NLS-1$
					}
					argList.add(cmd);
					break;
				}
			}
			if (!found) {
				throw new IllegalArgumentException(arg + " is an invalid argument"); //$NON-NLS-1$
			}
		}
		return argList;
	}

	private static String getVersion() {
		return ("OSLC Cleaner Utility, version " + VERSION); //$NON-NLS-1$
	}
}
