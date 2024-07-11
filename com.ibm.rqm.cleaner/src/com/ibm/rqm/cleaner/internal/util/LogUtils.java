/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Apache Log4j logger utilities.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public class LogUtils {

	private static Log logger = null;
	private static String LOGGER_NAME = "com.ibm.rqm.cleaner"; //$NON-NLS-1$
	
	private static Log getLogger() {

		if (logger == null) {
    		logger = LogFactory.getLog(LOGGER_NAME);	
    	}
		return logger;
	}
	
	public static boolean isTraceEnabled() {
		return (getLogger().isTraceEnabled());
	}

	public static void logTrace(String message) {
		if(isTraceEnabled()) {
			getLogger().trace(message);
		}		
	}

	public static void logTrace(String message, Throwable throwable) {
		if(isTraceEnabled()) {
			getLogger().trace(message, throwable);
		}
	}

	public static boolean isDebugEnabled() {
		return (getLogger().isDebugEnabled());
	}

	public static void logDebug(String message) {
		if(isDebugEnabled()) {
			getLogger().debug(message);
		}		
	}

	public static void logDebug(String message, Throwable throwable) {
		if(isDebugEnabled()) {
			getLogger().debug(message, throwable);
		}
	}

	public static boolean isInfoEnabled() {
		return (getLogger().isInfoEnabled());
	}

	public static void logInfo(String message) {
		if(isInfoEnabled()) {
			getLogger().info(message);
		}
	}

	public static void logInfo(String message, Throwable throwable) {
		if(isInfoEnabled()) {
			getLogger().info(message, throwable);
		}
	}

	public static void logWarning(String message) {
		getLogger().warn(message);
	}

	public static void logWarning(String message, Throwable throwable) {
		getLogger().warn(message, throwable);
	}

	public static void logError(String message) {
		getLogger().error(message);        
	}

	public static void logError(String message, Throwable throwable) {
		getLogger().error(message, throwable);        
	}			
		
}
