/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2019. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use.equals(resourceType)) {
			return ;
		}
		else if(duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_LABEL_OSLC_RM_VALIDATED_BY;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT_COLLECTION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PROPERTY_OSLC_RM_VALIDATED_BY;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_OSLC_QM_TEST_CASE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_OSLC_QM_TEST_EXECUTION_RECORD;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_OSLC_QM_TEST_PLAN;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_OSLC_QM_TEST_RESULT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_OSLC_QM_TEST_SCRIPT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_OSLC_RM_REQUIREMENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_OSLC_RM_REQUIREMENT_COLLECTION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_RQM_QM_TEST_SCRIPT_STEP;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_RQM_QM_TEST_SCRIPT_STEP_RESULT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_REQUIREMENT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_REQUIREMENT_COLLECTION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_CASE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_EXECUTION_RECORD;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_PLAN;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_RESULT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_SCRIPT;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_SCRIPT_STEP;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.RESOURCE_TYPE_URI_TEST_SCRIPT_STEP_RESULT;

import java.util.ArrayList;
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
public class OslcUtils {

	public static boolean isLinkOwned(String resourceTypeUri, String propertyPrefixedName){

		if(RESOURCE_TYPE_URI_TEST_PLAN.equals(resourceTypeUri)) {

			if(PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT_COLLECTION.equals(propertyPrefixedName)) {
				return true;
			}
		}
		else if(RESOURCE_TYPE_URI_TEST_CASE.equals(resourceTypeUri)) {

			if(PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT.equals(propertyPrefixedName)) {
				return true;
			}
		}
		else if(RESOURCE_TYPE_URI_TEST_SCRIPT_STEP.equals(resourceTypeUri)) {

			if(PROPERTY_OSLC_QM_VALIDATES_REQUIREMENT.equals(propertyPrefixedName)) {
				return true;
			}
		}

		return false;
	}

	public static String getResourceTypeUri(String resourceType){
		
		if(RESOURCE_OSLC_QM_TEST_PLAN.equals(resourceType)) {
			return RESOURCE_TYPE_URI_TEST_PLAN;
		}
		else if(RESOURCE_OSLC_QM_TEST_CASE.equals(resourceType)) {
			return RESOURCE_TYPE_URI_TEST_CASE;
		}
		else if(RESOURCE_OSLC_QM_TEST_SCRIPT.equals(resourceType)) {
			return RESOURCE_TYPE_URI_TEST_SCRIPT;
		}
		else if(RESOURCE_RQM_QM_TEST_SCRIPT_STEP.equals(resourceType)) {
			return RESOURCE_TYPE_URI_TEST_SCRIPT_STEP;
		}
		else if(RESOURCE_OSLC_QM_TEST_EXECUTION_RECORD.equals(resourceType)) {
			return RESOURCE_TYPE_URI_TEST_EXECUTION_RECORD;
		}
		else if(RESOURCE_OSLC_QM_TEST_RESULT.equals(resourceType)) {
			return RESOURCE_TYPE_URI_TEST_RESULT;
		}
		else if(RESOURCE_RQM_QM_TEST_SCRIPT_STEP_RESULT.equals(resourceType)) {
			return RESOURCE_TYPE_URI_TEST_SCRIPT_STEP_RESULT;
		}
		else if(RESOURCE_OSLC_RM_REQUIREMENT.equals(resourceType)) {
			return RESOURCE_TYPE_URI_REQUIREMENT;
		}
		else if(RESOURCE_OSLC_RM_REQUIREMENT_COLLECTION.equals(resourceType)) {
			return RESOURCE_TYPE_URI_REQUIREMENT_COLLECTION;
		}

		return null;
	}
	
	public static List<String> getPropertyLabels(String propertyPrefixedName, Map<String, List<String>> propertyPrefixedNamePropertyLabelsMap){

		List<String> propertyLabels = new ArrayList<String>();

		if(StringUtils.isSet(propertyPrefixedName)) {

			if(propertyPrefixedNamePropertyLabelsMap != null) {

				List<String> propertyLabelList = propertyPrefixedNamePropertyLabelsMap.get(propertyPrefixedName);

				if((propertyLabelList != null) && (!propertyLabelList.isEmpty())) {
					propertyLabels.addAll(propertyLabelList);
				}
			}
			else if(PROPERTY_OSLC_RM_VALIDATED_BY.equals(propertyPrefixedName)) {
				propertyLabels.add(PROPERTY_LABEL_OSLC_RM_VALIDATED_BY);
			}
		}

		return propertyLabels;
	}
}
