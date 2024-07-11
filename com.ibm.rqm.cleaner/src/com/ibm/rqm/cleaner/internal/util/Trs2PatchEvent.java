/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2017, 2019. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rqm.cleaner.internal.util;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.NAMESPACE_URI_OSLC_CONFIG_SELECTS;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PATCH_EVENT_PATTERN;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.PATCH_PATTERN;

import java.util.regex.Matcher;

/**
 * <p>TRS2 patch event POJO and parser.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   1.0
 */
public class Trs2PatchEvent {

	private String selectionsUri = null;
	private String addedUri = null;
	private String deletedUri = null;
		
	private Trs2PatchEvent() {
		//No-operation.
	}
	
	public String getSelectionsUri() {
		return selectionsUri;
	}

	public void setSelectionsUri(String selectionsUri) {
		this.selectionsUri = selectionsUri;
	}

	public String getAddedUri() {
		return addedUri;
	}

	public void setAddedUri(String addedUri) {
		this.addedUri = addedUri;
	}

	public String getDeletedUri() {
		return deletedUri;
	}

	public void setDeletedUri(String deletedUri) {
		this.deletedUri = deletedUri;
	}

	@Override
	public boolean equals(Object object) {
		
		if (!(object instanceof Trs2PatchEvent)) {
			return false;
		}

		if (this == object) {
			return true;
		}
				
		Trs2PatchEvent trs2ChangeEvent = ((Trs2PatchEvent)(object));

		if (selectionsUri == null) {
			return (trs2ChangeEvent.selectionsUri == null);
		} 
		
		return (selectionsUri.equals(trs2ChangeEvent.selectionsUri));
	}

	@Override
	public String toString() {
		
		StringBuilder trs2PatchEvent = new StringBuilder();

		if(StringUtils.isSet(deletedUri)) {
			
			trs2PatchEvent.append("D <"); //$NON-NLS-1$
			trs2PatchEvent.append(selectionsUri);
			trs2PatchEvent.append("> <"); //$NON-NLS-1$
			trs2PatchEvent.append(NAMESPACE_URI_OSLC_CONFIG_SELECTS);
			trs2PatchEvent.append("> <"); //$NON-NLS-1$
			trs2PatchEvent.append(deletedUri);			
			trs2PatchEvent.append("> ."); //$NON-NLS-1$
		}

		if(StringUtils.isSet(addedUri)) {
			
			trs2PatchEvent.append("A <"); //$NON-NLS-1$
			trs2PatchEvent.append(selectionsUri);
			trs2PatchEvent.append("> <"); //$NON-NLS-1$
			trs2PatchEvent.append(NAMESPACE_URI_OSLC_CONFIG_SELECTS);
			trs2PatchEvent.append("> <"); //$NON-NLS-1$
			trs2PatchEvent.append(addedUri);			
			trs2PatchEvent.append("> ."); //$NON-NLS-1$
		}
		
		return (trs2PatchEvent.toString());
	}

	public static Trs2PatchEvent parse(String trs2PatchEventString) throws Exception{
		
		Matcher patchEventMatcher = PATCH_EVENT_PATTERN.matcher(trs2PatchEventString);

		if(patchEventMatcher.matches()) {

			Trs2PatchEvent trs2PatchEvent = new Trs2PatchEvent();
			
			Matcher patchMatcher = PATCH_PATTERN.matcher(trs2PatchEventString);

			while(patchMatcher.find()) {

				String operation = patchMatcher.group(1);
				String selectionUri = patchMatcher.group(2);
				String resourceUri = patchMatcher.group(4);

				if((StringUtils.isSet(trs2PatchEvent.getSelectionsUri())) && (!trs2PatchEvent.getSelectionsUri().equalsIgnoreCase(selectionUri))) {
					throw new Exception("Different selections"); //$NON-NLS-1$
				}

				trs2PatchEvent.setSelectionsUri(selectionUri);

				if(operation.equals("A")) { //$NON-NLS-1$

					if(StringUtils.isSet(trs2PatchEvent.getAddedUri())) {
						throw new Exception("Multiple adds"); //$NON-NLS-1$
					}
					else {
						trs2PatchEvent.setAddedUri(resourceUri);
					}
				}
				else if(operation.equals("D")) { //$NON-NLS-1$

					if(StringUtils.isSet(trs2PatchEvent.getDeletedUri())) {
						throw new Exception("Multiple deletes"); //$NON-NLS-1$
					}
					else {
						trs2PatchEvent.setDeletedUri(resourceUri);
					}
				}
			}
			
			return trs2PatchEvent;
		}

		throw new Exception("Invalid patch syntax"); //$NON-NLS-1$
	}
}
