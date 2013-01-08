package org.openstack.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.io.Closeables;

/**
 * Utility functions to do with IO
 * 
 * @author justinsb
 * 
 */
@Deprecated
// Use fathomdb-commons IoUtils instead
public class Io {
	static final Logger log = Logger.getLogger(Io.class.getName());

	static final void logError(String message, Throwable e) {
		log.log(Level.SEVERE, message, e);
	}

	public static String readAll(Reader in) throws IOException {
		StringBuilder contents = new StringBuilder();

		char[] buffer = new char[8192];
		while (true) {
			int readCount = in.read(buffer);
			if (readCount == -1) {
				break;
			}
			contents.append(buffer, 0, readCount);
		}

		return contents.toString();
	}

	public static String readAll(InputStream inputStream) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, Utf8.CHARSET));
		return readAll(in);
	}

	public static String readAll(File file) throws IOException {
		BufferedReader in = new BufferedReader(Utf8.openFile(file));
		try {
			return readAll(in);
		} finally {
			Closeables.closeQuietly(in);
		}
	}

	public static String readAll(URL url) throws IOException {
		InputStream is = url.openStream();
		try {
			return readAll(is);
		} finally {
			Closeables.closeQuietly(is);
		}
	}

	public static byte[] readBytesFully(DataInputStream input, int length) throws IOException {
		byte[] buffer = new byte[length];
		int offset = 0;
		while (offset < length) {
			int read = input.read(buffer, offset, length - offset);
			if (read == -1) {
				throw new IOException("Encounted EOF while reading buffer of size: " + length);
			}
			offset += read;
		}
		return buffer;
	}

	public static void delete(File file) throws IOException {
		if (file != null) {
			if (!file.delete()) {
				throw new IOException("Unable to delete file");
			}
		}
	}

}
