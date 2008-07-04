/*
 * 
 * Functions to read XML files considering byte order marks
 * 
 */
package org.xmldap.util;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class XmlFileUtil {
	/**
	 * Removes the byte order mark from the stream, if
     * it exists and returns the encoding name.
     * 
     * Adapted code from org/apache/xerces/xinclude/XIncludeTextReader.java
     * 
     * If null is returned then some bytes were read but they were no BOM bytes.
     * You have to reset the stream in this case. I don't do that here 
     * because mark/reset is not implemented on all plattforms (windows)
     * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static String getEncoding(InputStream stream) throws IOException {

        stream.mark(4);

        byte[] b = new byte[3];
		int count = 0;
		int b0 = 0;
		int b1 = 0;

		count = stream.read(b, 0, 2);
		if (count == 2) {
			b0 = b[0] & 0xFF;
			b1 = b[1] & 0xFF;

            if (b0 == 0xFE && b1 == 0xFF) {
                return "UTF-16BE";
			} else if (b0 == 0xFF && b1 == 0xFE) {
				return "UTF-16LE";
			}
		} else {
			return null;
		}

        byte[] B = new byte[1];
		count = stream.read(B, 0, 1);
		if (count == 1) {
			final int b2 = B[0] & 0xFF;
			if (b0 != 0xEF || b1 != 0xBB || b2 != 0xBF) {
				// First three bytes are not BOM, so reset.
                stream.reset();
            } else {
				return "UTF-8";
			}
		}
		return null;
	}
	
	/**
	 * Read an XML file into a Document considering Byte Order Marks
	 * 
	 * @param filename
	 * 
	 * @return XML Document
	 * 
	 * @throws IOException
	 * @throws ValidityException
	 * @throws ParsingException
	 */
	public static Document readXmlFile(String filename) 
	 throws IOException, ValidityException, ParsingException 
	{
		FileInputStream fis = new FileInputStream(filename);
		String encoding = getEncoding(fis);
		if (encoding == null) {
			// read some bytes but where not able to determine BO
			// have to reset stream
			fis.close();
			fis = new FileInputStream(filename);
			encoding = "utf-8";
		}

		System.out.println("encoding: " + encoding);
		
		int avail = fis.available();
		byte[] encryptedStoreBytes = new byte[avail];
		fis.read(encryptedStoreBytes);
		fis.close();

		System.out.println(new String(encryptedStoreBytes, encoding));
		
		Builder parser = new Builder();
		Document encryptedStoreDoc = parser.build(new String(
				encryptedStoreBytes, encoding), "");
		return encryptedStoreDoc;
	}



	    public static Document readXml(InputStream stream) throws IOException, ValidityException, ParsingException
	    {
	        String encoding = getEncoding(stream);
		    return readXml(encoding, stream);
		}



	    /**
	     * Read an XML file into a Document considering Byte Order Marks
	     *
	     * @param stream
	     *
	     * @return XML Document
	     *
	     * @throws IOException
	     * @throws ValidityException
	     * @throws ParsingException
	     */
	    public static Document readXml(String encoding, InputStream stream) throws IOException, ValidityException, ParsingException
	    {
        if (encoding == null) {
            // read some bytes but where not able to determine BO
            // have to reset stream
            encoding = "utf-8";
        }


        int avail = stream.available();
        byte[] encryptedStoreBytes = new byte[avail];
        stream.read(encryptedStoreBytes);
        stream.close();
        Builder parser = new Builder();
        Document encryptedStoreDoc = parser.build(new String(encryptedStoreBytes, encoding), "");
        return encryptedStoreDoc;
    }

	public String doRead(InputStream in) throws IOException {
		BufferedReader ins = new BufferedReader(new InputStreamReader(in));
		
		StringBuilder sb = new StringBuilder();
        try {
    		int c = -1;
    		char[] charBuf = null;
    		while (true) {
	    		int len = in.available();
	    		if (len > 0) {
	    			if (charBuf == null) {
	    				charBuf = new char[len];
	    			} else {
	    				if (len > charBuf.length) {
	    					charBuf = new char[len];
	    				}
	    			}
	    		} else {
	    			// available is not always relyable
	    			if (charBuf == null) {
	    				charBuf = new char[2048];
	    			} else {
	    				if (2048 > charBuf.length) {
	    					charBuf = new char[2048];
	    				}
	    			}
	    		}
	    		c = ins.read(charBuf, 0, charBuf.length);
	    		if (c == -1) {
	    			break;
	    		} else {
	    			sb.append(charBuf, 0, c);
	    		}
    		}
        } finally {
	    	try {
				in.close();
			} catch (IOException e) {}
    		try {
				ins.close();
			} catch (IOException e) {}
        }
        return sb.toString();
	}

}
