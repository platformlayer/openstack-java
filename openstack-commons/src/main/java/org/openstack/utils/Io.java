package org.openstack.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility functions to do with IO
 * 
 * @author justinsb
 * 
 */
public class Io {
	static final Logger log = Logger.getLogger(Io.class.getName());

	public static File resolve(String filename) {
		if (filename.startsWith("~/")) {
			filename = filename.replace("~/", System.getProperty("user.home") + File.separator);
		}

		return new File(filename);
	}

	public static void safeClose(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (IOException e) {
			logError("Ignoring unexpected error closing item", e);
		}
	}

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
			safeClose(in);
		}
	}

	public static String readAll(URL url) throws IOException {
		InputStream is = url.openStream();
		try {
			return readAll(is);
		} finally {
			safeClose(is);
		}
	}

	public static void copyStreams(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[32768];
		while (true) {
			int bytesRead = is.read(buffer);
			if (bytesRead == -1) {
				break;
			}

			os.write(buffer, 0, bytesRead);
		}
	}

	public static void copyStreams(InputStream is, File outputFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(outputFile);
		try {
			copyStreams(is, fos);
		} finally {
			Io.safeClose(fos);
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

}
