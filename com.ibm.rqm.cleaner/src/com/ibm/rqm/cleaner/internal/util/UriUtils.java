/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2012, 2019. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

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

/**
 * <p>URI utilities.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public final class UriUtils {

	/**
	 * <p>Resolves the type namespace URI from the type URI.</p>
	 * 
	 * <p>The format of the type URI is:</p>
	 * 
	 * <p><code>&lt;type namespace URI terminated with a / or #&gt;&lt;name&gt;</code></p>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>http://open-services.net/ns/qm#TestPlan</code></p>
	 *  
	 * @param typeUri The type URI.
	 * @return The type namespace URI from the type URI.
	 */
	public static String getTypeNamespaceUri(String typeUri){

		if(typeUri != null){

			int delimiterIndex = typeUri.lastIndexOf('#');

			if(delimiterIndex == -1){
				delimiterIndex = typeUri.lastIndexOf('/');
			}

			if(delimiterIndex != -1){
				return (typeUri.substring(0, (delimiterIndex + 1)));
			}
		}

		return null;
	}

	/**
	 * <p>Resolves the type name from the type URI.</p>
	 * 
	 * <p>The format of the type URI is:</p>
	 * 
	 * <p><code>&lt;namespace URI terminated with a / or #&gt;&lt;type name&gt;</code></p>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>http://open-services.net/ns/qm#TestPlan</code></p>
	 *  
	 * @param typeUri The type URI.
	 * @return The type name from the type URI.
	 */
	public static String getTypeName(String typeUri){

		if(typeUri != null){

			int delimiterIndex = typeUri.lastIndexOf('#');

			if(delimiterIndex == -1){
				delimiterIndex = typeUri.lastIndexOf('/');
			}

			if(delimiterIndex != -1){
				return (typeUri.substring(delimiterIndex + 1));
			}
		}

		return null;
	}

	/**
	 * <p>Resolves the type display name from the type URI.</p>
	 * 
	 * <p>The format of the type URI is:</p>
	 * 
	 * <p><code>&lt;namespace URI terminated with a / or #&gt;&lt;type name&gt;</code></p>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>http://open-services.net/ns/qm#TestPlan</code></p>
	 *  
	 * <p>The <code>&lt;type name&gt;</code> is formatted for display.</p>
	 * 
	 * @param typeUri The type URI.
	 * @return The type display name from the type URI.
	 */
	public static String getTypeDisplayName(String typeUri){

		String name = getTypeName(typeUri);

		if(name != null){
			return (name.replaceAll("([a-z])([A-Z])","$1 $2")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return null;
	}

	/**
	 * <p>Resolves the namespace prefix from the prefixed name.</p>
	 * 
	 * <p>The format of the prefixed name is:</p>
	 * 
	 * <p><code>&lt;namespace prefix&gt;:&lt;name&gt;</code></p>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>oslc_qm:validatesRequirement</code></p>
	 *  
	 * @param prefixedName The prefixed name.
	 * @return The namespace prefix from the prefixed name.
	 */
	public static String getNamespacePrefix(String prefixedName){

		if(prefixedName != null){

			int delimiterIndex = prefixedName.lastIndexOf(':');

			if(delimiterIndex != -1){
				return (prefixedName.substring(0, delimiterIndex));
			}
		}

		return null;
	}

	/**
	 * <p>Resolves the name from the prefixed name.</p>
	 * 
	 * <p>The format of the prefixed name is:</p>
	 * 
	 * <p><code>&lt;namespace prefix&gt;:&lt;name&gt;</code></p>
	 * 
	 * <p>For example:</p>
	 * 
	 * <p><code>oslc_qm:validatesRequirement</code></p>
	 *  
	 * @param prefixedName The prefixed name.
	 * @return The name from the prefixed name.
	 */
	public static String getName(String prefixedName){

		if(prefixedName != null){

			int delimiterIndex = prefixedName.lastIndexOf(':');

			if(delimiterIndex != -1){
				return (prefixedName.substring(delimiterIndex + 1));
			}
		}

		return null;
	}

	public static String resolveNamespaceUri(String namespacePrefix) {

		if (PREFIX_DCTERMS.equals(namespacePrefix)) return NAMESPACE_URI_DC_TERMS; 
		if (PREFIX_DC.equals(namespacePrefix)) return NAMESPACE_URI_DC; 
		if (PREFIX_OSLC_QM.equals(namespacePrefix)) return NAMESPACE_URI_OSLC_QM; 
		if (PREFIX_OSLC_RM.equals(namespacePrefix)) return NAMESPACE_URI_OSLC_RM; 
		if (PREFIX_OSLC_CM.equals(namespacePrefix)) return NAMESPACE_URI_OSLC_CM; 
		if (PREFIX_OSLC.equals(namespacePrefix)) return NAMESPACE_URI_OSLC; 
		if (PREFIX_RDF.equals(namespacePrefix)) return NAMESPACE_URI_RDF; 
		if (PREFIX_RDFS.equals(namespacePrefix)) return NAMESPACE_URI_RDFS; 
		if (PREFIX_CALM.equals(namespacePrefix)) return NAMESPACE_URI_JAZZ_CALM; 
		if (PREFIX_FOAF.equals(namespacePrefix)) return NAMESPACE_URI_FOAF; 
		if (PREFIX_RQM_QM.equals(namespacePrefix)) return NAMESPACE_URI_JAZZ_QM; 
		if (PREFIX_JAZZ_ACP.equals(namespacePrefix)) return NAMESPACE_URI_ACCESS_CONTROL; 
		if (PREFIX_JAZZ_TRS.equals(namespacePrefix)) return NAMESPACE_URI_JAZZ_TRS; 
		if (PREFIX_JAZZ_TRS2.equals(namespacePrefix)) return NAMESPACE_URI_JAZZ_TRS2; 
		if (PREFIX_RDM_RM.equals(namespacePrefix)) return NAMESPACE_URI_RM; 
		if (PREFIX_RDM_RM_WORKFLOW.equals(namespacePrefix)) return NAMESPACE_URI_RM_WORKFLOW; 
		if (PREFIX_JFS.equals(namespacePrefix)) return NAMESPACE_URI_JFS; 
		if (PREFIX_H.equals(namespacePrefix)) return NAMESPACE_URI_H; 
		if (PREFIX_XS.equals(namespacePrefix)) return NAMESPACE_URI_XS; 
		if (PREFIX_RDM_RM_TYPES.equals(namespacePrefix)) return NAMESPACE_URI_RM_TYPES; 
		if (PREFIX_OWL.equals(namespacePrefix)) return NAMESPACE_URI_OWL; 
		if (PREFIX_OSLC_CONFIG.equals(namespacePrefix)) return NAMESPACE_URI_OSLC_CONFIG; 
		if (PREFIX_JRS.equals(namespacePrefix)) return NAMESPACE_URI_JRS; 
		
		return null;
	}

	public static String resolveNamespacePrefix(String namespaceUri) {

		if (NAMESPACE_URI_DC_TERMS.equals(namespaceUri)) return PREFIX_DCTERMS; 
		if (NAMESPACE_URI_DC.equals(namespaceUri)) return PREFIX_DC; 
		if (NAMESPACE_URI_OSLC_QM.equals(namespaceUri)) return PREFIX_OSLC_QM; 
		if (NAMESPACE_URI_OSLC_RM.equals(namespaceUri)) return PREFIX_OSLC_RM; 
		if (NAMESPACE_URI_OSLC_CM.equals(namespaceUri)) return PREFIX_OSLC_CM; 
		if (NAMESPACE_URI_OSLC.equals(namespaceUri)) return PREFIX_OSLC; 
		if (NAMESPACE_URI_RDF.equals(namespaceUri)) return PREFIX_RDF; 
		if (NAMESPACE_URI_RDFS.equals(namespaceUri)) return PREFIX_RDFS; 
		if (NAMESPACE_URI_JAZZ_CALM.equals(namespaceUri)) return PREFIX_CALM; 
		if (NAMESPACE_URI_FOAF.equals(namespaceUri)) return PREFIX_FOAF; 
		if (NAMESPACE_URI_JAZZ_QM.equals(namespaceUri)) return PREFIX_RQM_QM; 
		if (NAMESPACE_URI_ACCESS_CONTROL.equals(namespaceUri)) return PREFIX_JAZZ_ACP; 
		if (NAMESPACE_URI_JAZZ_TRS.equals(namespaceUri)) return PREFIX_JAZZ_TRS; 
		if (NAMESPACE_URI_JAZZ_TRS2.equals(namespaceUri)) return PREFIX_JAZZ_TRS2; 
		if (NAMESPACE_URI_RM.equals(namespaceUri)) return PREFIX_RDM_RM; 
		if (NAMESPACE_URI_RM_WORKFLOW.equals(namespaceUri)) return PREFIX_RDM_RM_WORKFLOW; 
		if (NAMESPACE_URI_JFS.equals(namespaceUri)) return PREFIX_JFS; 
		if (NAMESPACE_URI_H.equals(namespaceUri)) return PREFIX_H; 
		if (NAMESPACE_URI_XS.equals(namespaceUri)) return PREFIX_XS; 
		if (NAMESPACE_URI_RM_TYPES.equals(namespaceUri)) return PREFIX_RDM_RM_TYPES; 
		if (NAMESPACE_URI_OWL.equals(namespaceUri)) return PREFIX_OWL; 
		if (NAMESPACE_URI_OSLC_CONFIG.equals(namespaceUri)) return PREFIX_OSLC_CONFIG; 
		if (NAMESPACE_URI_JRS.equals(namespaceUri)) return PREFIX_JRS; 
		
		return null;
	}

	public static boolean containsValidTypeUri(String typeUri) {

		String namespaceUri = getTypeNamespaceUri(typeUri);
		String name = getTypeName(typeUri);
		
		boolean containsValidNamespaceUri = (resolveNamespacePrefix(namespaceUri) != null);
		boolean containsValidName = StringUtils.isSet(name);
		
		return ((containsValidNamespaceUri) && (containsValidName));
	}
}
