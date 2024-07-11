/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2011, 2022. All Rights Reserved.
 *  
 * U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 ******************************************************************************/
package com.ibm.rqm.cleaner.internal.client.rm;

import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.ENCODING_GZIP;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.ENCODING_ISO_8859_1;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_CONSTANTS_CHARSET;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_CONTENT_ENCODING;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_CONTENT_TYPE;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_ETAG;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_LOCATION;
import static com.ibm.rqm.cleaner.internal.util.CleanerConstants.HTTP_HEADER_SET_COOKIE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.wink.common.http.HttpStatus;

import com.ibm.rqm.cleaner.internal.util.LogUtils;
import com.ibm.rqm.cleaner.internal.util.StringUtils;
import com.ibm.rqm.integration.client.clientlib.HttpClientConstants;

/**
 * <p>Requirements Management (RM) OSLC HTTP client request response.</p>
 * 
 * <p>Note: Copied from <code>com.ibm.rqm.requirement.service</code> plug-in.</p>
 * 
 *  
 * @author  Paul Slauenwhite
 * @version 1.0
 * @since   0.9
 */
public class RequestResponse{
	private int responseCode;
	private Map<String, String> responseHeaders;
	private InputStream stream;
	private String statusText;
	private Charset charset;
	private int contentLength = -1;

	private static int bufferSize = Integer.parseInt(System.getProperty(
			"rrc.buffer.size", "32768")); //$NON-NLS-1$ //$NON-NLS-2$
	
	private RequestResponse() {}
	
	public RequestResponse(CloseableHttpResponse response, boolean isHeadMethod) throws IOException {
		int responseCode = response.getStatusLine().getStatusCode();
		setResponseCode(responseCode);
		setStatusText(HttpStatus.valueOf(responseCode).toString());
		Map<String, String> responseHeaders = new HashMap<String, String>(
				response.getAllHeaders().length);
		for (Header header : response.getAllHeaders()) {
			if (HTTP_HEADER_SET_COOKIE.equalsIgnoreCase(header.getName()) && (null != responseHeaders.get(header.getName()))) {
				responseHeaders.put(header.getName(), responseHeaders.get(header.getName()) + "; " + header.getValue()); //$NON-NLS-1$
			} else {
				responseHeaders.put(header.getName(), header.getValue());
			}
		}
		setResponseHeaders(responseHeaders);
		
		// capture the character set of the response content
		Header header = response.getFirstHeader(HTTP_HEADER_CONTENT_TYPE);
		if (header != null && header.getElements().length == 1) {
			NameValuePair property
					= header.getElements()[0].getParameterByName(HTTP_CONSTANTS_CHARSET);
			if (property != null) {
				charset = Charset.forName(property.getValue()); 
			}
		}
		if (charset == null) {
			// Use default charset, which is ISO-8859-1 according to the following spec:
			// http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7
			charset = Charset.forName(ENCODING_ISO_8859_1);
		}
		
		if (!isHeadMethod) {
			setStream(getStreamFromResponse(response));
		}
	}
	
	protected ByteArrayInputStream getStreamFromResponse(HttpResponse response) throws IOException {
		HttpEntity entity = response.getEntity();
		String message = EntityUtils.toString(entity, HttpClientConstants.UTF8);
		if(message == null || message.length() == 0) {
			StatusLine status = response.getStatusLine();
			if(status != null && status.getStatusCode() >= HttpStatus.BAD_REQUEST.getCode()) {
				message =response.getStatusLine().getReasonPhrase();
			}
		}
		if(message != null && message.length() > 0) {
			return new ByteArrayInputStream(message.getBytes(HttpClientConstants.UTF8));
		}
		return null;
	}
	
	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseHeaderValue(String responseHeaderName) {

		if(responseHeaders != null) {
			return responseHeaders.get(responseHeaderName);
		}

		return null;
	}

	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(Map<String, String> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public InputStream getStream() {
		return stream;
	}

	protected void setStream(InputStream stream) {	
		try {
			if (stream != null) {
				this.stream = copy(stream);
			}
		} catch (IOException e) {
			LogUtils.logError(e.toString(), e);
		}
	}
	
	public void setStream(ByteArrayInputStream stream, int contentLength) {
		this.stream = stream;
		setContentLength(contentLength);
	}
	
	private InputStream copy(InputStream incomingStream) throws IOException {
		InputStream inputStream = uncompressStream(incomingStream);
		ByteArrayOutputStream fileBytesOutputStream = new ByteArrayOutputStream();
		try {
			int read;
			int totalRead = 0;
			byte[] next = new byte[bufferSize];
			while ((read = inputStream.read(next)) != -1) {
				fileBytesOutputStream.write(next, 0, read);
				totalRead += read;
			}
			setContentLength(totalRead);
			
		} finally {
			inputStream.close();
			fileBytesOutputStream.close();
		}
		return new ByteArrayInputStream(fileBytesOutputStream.toByteArray());
	}

	/**
	 * check the Content-Encoding header of the response.  If it is set to gzip, then we will set it to be uncompressed
	 *   via wrapping it in a GZIPInputStream
	 * 
	 * @return return the GZIPInputStream-wrapped inputStream if compressed; otherwise return the original unaltered inputStream 
	 */
	private InputStream uncompressStream(InputStream incomingStream) throws IOException {
		InputStream inputStream = incomingStream;
		if ( responseHeaders != null ) {
			String contentEncodingHeader = responseHeaders.get(HTTP_HEADER_CONTENT_ENCODING);
			if ( contentEncodingHeader != null && contentEncodingHeader.length() > 0 &&  incomingStream != null) {
				// only uncompress if gzip is listed alone or the first entry in the encoding header
				String[] encodings = contentEncodingHeader.split("\\,"); //$NON-NLS-1$
				if ( encodings.length > 0 ) {
					if ( ENCODING_GZIP.equals( encodings[0].trim()) ) {
						inputStream = new GZIPInputStream(incomingStream);
					}
				}
			}
		}
		return inputStream;
	}

	public String getStatusText() {
		return statusText;
	}
	
	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public URL getLocation() throws MalformedURLException {
		if (responseHeaders != null) {
			String location = responseHeaders.get(HTTP_HEADER_LOCATION);
			return new URL(location);
		}
		return null;
	}
	
	public String getETag() throws MalformedURLException {
		
		if (responseHeaders != null) {
			
			String eTag = responseHeaders.get(HTTP_HEADER_ETAG);
			
			//Note: Some newer DOORS Next versions use a lower case etag response header. 
			if(StringUtils.isNotSet(eTag)) {
				eTag = responseHeaders.get(HTTP_HEADER_ETAG.toLowerCase());
			}
			
			return eTag;
		}
		
		return null;
	}
	
	public Charset getCharset() {
		return charset;
	}
	
	public int getContentLength() {
		return contentLength;
	}
	
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public static RequestResponse newEmptyResponse() {
		return new RequestResponse();
	}
}
