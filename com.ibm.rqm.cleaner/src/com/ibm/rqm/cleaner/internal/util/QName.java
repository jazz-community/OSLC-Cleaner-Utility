/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2019. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

/**
 * <p>Qualified name (or QName) with a namespace URI and prefix.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   1.0
 * @see     UriUtils#resolveNamespacePrefix(String)
 */
public class QName {

	private final String namespaceUri;
	private final String namespacePrefix;
	private final String name;
	
	public QName(String namespaceUri, String name) {
		
		this.namespaceUri = namespaceUri;
		this.namespacePrefix = UriUtils.resolveNamespacePrefix(namespaceUri);
		this.name = name;
	}

	public String getNamespaceUri() {
		return namespaceUri;
	}

	public String getNamespacePrefix() {
		return namespacePrefix;
	}

	public String getName() {
		return name;
	}

	public String getQualifiedName() {

		if((StringUtils.isSet(namespaceUri)) && (StringUtils.isSet(name))) {
			return (namespaceUri + name);
		}

		return null;
	}

	public String getPrefixedName() {

		if((StringUtils.isSet(namespacePrefix)) && (StringUtils.isSet(name))) {
			return (namespacePrefix + ":" + name); //$NON-NLS-1$
		}

		return null;
	}

	@Override
	public String toString() {

		String toString = getPrefixedName();

		if(StringUtils.isNotSet(toString)) {
			toString = getQualifiedName();
		}

		if(StringUtils.isNotSet(toString)) {
			toString = getName();
		}

		return toString;
	}
}
