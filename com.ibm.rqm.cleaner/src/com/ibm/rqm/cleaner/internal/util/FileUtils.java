/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2017. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

import java.io.File;

/**
 * <p>File and directory utilities.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   1.0
 */
public class FileUtils {

	/**
	 * <p>Deletes the directory, including any files/directories in the directory.</p>
	 * 
	 * @param directory The directory to be deleted.
	 * @return <code>true</code> if the directory is deleted, otherwise <code>false</code>.
	 */
	public static boolean deleteDirectory(File directory) {

		final File[] files = directory.listFiles();

		for (File file : files){

			if(file.isDirectory() ) {
				deleteDirectory(file);
			} 
			else {
				file.delete();
			}
		}

		return (directory.delete());
	}
}
