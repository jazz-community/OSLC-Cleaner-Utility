/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2019. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>Cleaner utilities.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   1.0
 */
public class CleanerUtils {

	public static String toString(String[] array){
		return (toString(Arrays.asList(array)));
	}

	public static String toString(List<?> list){

		StringBuilder string = new StringBuilder();

		if((list != null) && (!list.isEmpty())){

			for(Object element : list){

				if(string.length() > 0){
					string.append(", "); //$NON-NLS-1$
				}

				string.append(element);
			}
		}

		return (string.toString().trim());
	}
	
	public static String toString(Map<?,?> map){

		StringBuilder string = new StringBuilder();

		if((map != null) && (!map.isEmpty())){

			for(Object key : map.keySet()){

				if(string.length() > 0){
					string.append(", "); //$NON-NLS-1$
				}

				string.append(key);
				string.append("="); //$NON-NLS-1$
				string.append(map.get(key));
			}
		}

		return (string.toString().trim());
	}
	
	public static boolean contains(Object[] array, Object element){

		if((array != null) && (array.length > 0) && (element != null)){

			for(Object arrayElement : array){

				if(element.equals(arrayElement)){
					return true;
				}
			}
		}

		return false;
	}

	public static String getAsterisks(int length) {
		
		StringBuilder asterisks = new StringBuilder();

		for (int counter = 0; counter < length; counter++) {
			asterisks.append("*"); //$NON-NLS-1$
		}
		
		return asterisks.toString();
	}
}
