/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2019, 2022. All Rights Reserved.
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
 * @since   1.0
 */
public class PerformanceUtils {

	private static Log performanceLogger = null;
	
	private static Log getPerformanceLogger() {

		if (performanceLogger == null) {
			performanceLogger = LogFactory.getLog(PerformanceUtils.class);	
    	}
		return performanceLogger;		
	}
	
	public static boolean isTraceEnabled() {
		return (getPerformanceLogger().isTraceEnabled());
	}
	
	public static void logTrace(String message, long startTime, long endTime) {
		if(isTraceEnabled()) {			
			logTrace(message, (endTime - startTime));
		}
	}	

	public static void logTrace(String message, long time) {
		getPerformanceLogger().trace(DateTimeUtils.formatTime(time) + ": " + message); //$NON-NLS-1$
	}	

}
