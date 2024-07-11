/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2017, 2018. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

/**
 * <p>TRS2 change event POJO.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   1.0
 */
public class Trs2ChangeEvent {

	private final String uri;
	private String changed = null;
	private String type = null;
	private boolean isRdfPatch = false;
	private String rdfPatch = null;
	private long beforeETag = -1;
	private long afterETag = -1;
	private long order = -1;
	
	public Trs2ChangeEvent(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return uri;
	}

	public String getChanged() {
		return changed;
	}

	public void setChanged(String changed) {
		this.changed = changed;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isRdfPatch() {
		return isRdfPatch;
	}

	public void setIsRdfPatch(boolean isRdfPatch) {
		this.isRdfPatch = isRdfPatch;
	}

	public String getRdfPatch() {
		return rdfPatch;
	}

	public void setRdfPatch(String rdfPatch) {
		this.rdfPatch = rdfPatch;
	}

	public long getBeforeETag() {
		return beforeETag;
	}

	public void setBeforeETag(long beforeETag) {
		this.beforeETag = beforeETag;
	}

	public long getAfterETag() {
		return afterETag;
	}

	public void setAfterETag(long afterETag) {
		this.afterETag = afterETag;
	}

	public long getOrder() {
		return order;
	}

	public void setOrder(long order) {
		this.order = order;
	}

	@Override
	public boolean equals(Object object) {
		
		if (!(object instanceof Trs2ChangeEvent)) {
			return false;
		}

		if (this == object) {
			return true;
		}
				
		Trs2ChangeEvent trs2ChangeEvent = ((Trs2ChangeEvent)(object));

		if (uri == null) {
			return (trs2ChangeEvent.uri == null);
		} 
		
		return (uri.equals(trs2ChangeEvent.uri));
	}

	@Override
	public String toString() {
		return uri;
	}

	public String toXml() {
		return (toXml(true));
	}
	
	public String toXml(boolean formatXml) {

		StringBuilder xml = new StringBuilder();

		if(StringUtils.isSet(uri)) {

			xml.append("<rdf:Description rdf:about=\""); //$NON-NLS-1$
			xml.append(uri);
			xml.append("\">"); //$NON-NLS-1$

			if(formatXml){
				xml.append("\n"); //$NON-NLS-1$
			}
			
			if(StringUtils.isSet(rdfPatch)) {

				if(formatXml){
					xml.append(" "); //$NON-NLS-1$
				}

				xml.append("<trspatch:rdfPatch rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">"); //$NON-NLS-1$
				xml.append(rdfPatch);
				xml.append("</trspatch:rdfPatch>"); //$NON-NLS-1$
				
				if(formatXml){
					xml.append("\n"); //$NON-NLS-1$
				}
			}

			if(StringUtils.isSet(type)) {

				if(formatXml){
					xml.append(" "); //$NON-NLS-1$
				}

				xml.append("<rdf:type rdf:resource=\""); //$NON-NLS-1$
				xml.append(type);
				xml.append("\"/>"); //$NON-NLS-1$
				
				if(formatXml){
					xml.append("\n"); //$NON-NLS-1$
				}
			}

			if(order != -1) {

				if(formatXml){
					xml.append(" "); //$NON-NLS-1$
				}

				xml.append("<trs2:order rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">"); //$NON-NLS-1$
				xml.append(order);
				xml.append("</trs2:order>"); //$NON-NLS-1$
				
				if(formatXml){
					xml.append("\n"); //$NON-NLS-1$
				}
			}

			if(afterETag != -1) {

				if(formatXml){
					xml.append(" "); //$NON-NLS-1$
				}

				xml.append("<trspatch:afterEtag rdf:datatype=\"http://www.w3.org/2001/XMLSchema#long\">"); //$NON-NLS-1$
				xml.append(afterETag);
				xml.append("</trspatch:afterEtag>"); //$NON-NLS-1$
				
				if(formatXml){
					xml.append("\n"); //$NON-NLS-1$
				}
			}

			if(beforeETag != -1) {

				if(formatXml){
					xml.append(" "); //$NON-NLS-1$
				}

				xml.append("<trspatch:beforeEtag rdf:datatype=\"http://www.w3.org/2001/XMLSchema#long\">"); //$NON-NLS-1$
				xml.append(beforeETag);
				xml.append("</trspatch:beforeEtag>"); //$NON-NLS-1$
				
				if(formatXml){
					xml.append("\n"); //$NON-NLS-1$
				}
			}

			if(StringUtils.isSet(changed)) {

				if(formatXml){
					xml.append(" "); //$NON-NLS-1$
				}

				xml.append("<trs2:changed rdf:resource=\""); //$NON-NLS-1$
				xml.append(changed);
				xml.append("\"/>"); //$NON-NLS-1$
				
				if(formatXml){
					xml.append("\n"); //$NON-NLS-1$
				}
			}

			xml.append("</rdf:Description>"); //$NON-NLS-1$
		}
		else {
			xml.append("Invalid TRS2 change event."); //$NON-NLS-1$
		}

		return (xml.toString());
	}

}
