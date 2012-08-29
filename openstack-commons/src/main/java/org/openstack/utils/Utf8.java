package org.openstack.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Helper class that does String <-> UTF8 encoding
 * 
 */
public class Utf8 {
	public static final Charset CHARSET = Charset.forName("UTF-8");

	private Utf8() {
	}

	public static String toString(ByteArrayOutputStream baos) {
		return toString(baos.toByteArray());
	}

	public static String toString(byte[] bytes) {
		return new String(bytes, CHARSET);
	}

	public static byte[] getBytes(String s) {
		return s.getBytes(CHARSET);
	}

	public static InputStreamReader openFile(File file) throws FileNotFoundException {
		return new InputStreamReader(new FileInputStream(file), CHARSET);
	}

	public static Reader openReader(InputStream is) {
		return new InputStreamReader(is, CHARSET);
	}

	public static OutputStreamWriter openWriter(OutputStream outputStream) {
		return new OutputStreamWriter(outputStream, CHARSET);
	}

	public static String toString(ByteBuffer buffer) {
		CharsetDecoder decoder = CHARSET.newDecoder();
		CharBuffer chars;
		try {
			chars = decoder.decode(buffer);
		} catch (CharacterCodingException e) {
			throw new IllegalArgumentException("Error convert from UTF8", e);
		}
		return chars.toString();
	}
}
