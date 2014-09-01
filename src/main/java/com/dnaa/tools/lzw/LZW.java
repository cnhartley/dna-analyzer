/**
 * 
 */
package com.dnaa.tools.lzw;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

/**
 * @author Chris
 *
 */
public final class LZW {

	private static final class Marker {
		public static final int CLR = 1 << Byte.SIZE;
		public static final int EOD = CLR + 1;
		public static final int NEW = EOD + 1;
	}
	
	private static final class DictionaryItem {
		private byte[] bytes = new byte[1024];
		private int size = 0;
		
		public DictionaryItem() { }
		
		public DictionaryItem(byte b) {
			put(b);
		}
		
		public DictionaryItem(byte[] bytes) {
			put(bytes);
		}
		
		public DictionaryItem(DictionaryItem di) {
			if (di != null) put(di.get());
		}
		
		public DictionaryItem put(byte b) {
			bytes[size++] = b;
			return this;
		}
		
		public DictionaryItem put(byte[] bytes) {
			for (byte b : bytes)
				put(b);
			return this;
		}
		
		public byte[] get() {
			byte[] used = new byte[size];
			for (int b = 0; b < used.length; b++)
				used[b] = bytes[b];
			return used;
		}
		
		public int getSize() {
			return size;
		}
		
		public boolean isEmpty() {
			return size == 0;
		}
		
		public void clear() {
			size = 0;
		}
		
		@Override
		public boolean equals(Object obj) {
			System.out.println("DictionaryItem.equals(" + this + "," + obj + ")");
			if (obj != null && obj instanceof DictionaryItem) {
				DictionaryItem di = (DictionaryItem)obj;
				if (getSize() == di.getSize()) {
					for (int i = 0; i < size; i++)
						if (bytes[i] != di.bytes[i])
							return false;
					return true;
				}
			}
			return false;
		}
		
		@Override
		public String toString() {
			try {
				return new String(get(), charset);
			} catch (UnsupportedEncodingException e) {
				System.err.println("Unsupported encoding character set: " + charset);
				return new String(get());
			}
		}
	}
	
	
	private static final String charset = "UTF-8";

	public static final String compress(String uncompressed) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(uncompressed.getBytes());

		if (compress(in, out)) {
			return out.toString();
		}
		else
			return "";
	}
	public static final boolean compress(InputStream input, OutputStream output) throws IOException {
		boolean success = false;
		int bufferSize = 1024;
		BufferedInputStream in = null;
		DeflaterOutputStream out = null;
		byte[] buffer = new byte[bufferSize];
		try {
			in = new BufferedInputStream(input, bufferSize);
			out = new DeflaterOutputStream(output,true);
			
			int numBytes = 0;
			while ((numBytes = in.read(buffer)) > 0) {
				out.write(buffer, 0, numBytes);
			}
			
			out.flush();
			success = true;
		}
		catch (UnsupportedEncodingException ignore) { }
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignore) { }
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignore) { }
			}
		}
		
		return success;
	}
	
	/*public static final boolean compress(InputStream in, OutputStream out) throws IOException {
		Map<DictionaryItem,Integer> dictionary = initializeCompressionDictionary();
		BufferedInputStream bufferIn = new BufferedInputStream(in);
		int dictionarySize = Marker.NEW;
		
		int numBytes = 0;
		byte[] bytes = new byte[1024];
		DictionaryItem w = new DictionaryItem();
		DictionaryItem wc = new DictionaryItem();
		while ((numBytes = bufferIn.read(bytes)) > 0) {
			for (int b = 0; b < numBytes; b++) {
				wc.clear();
				wc.put(w.get()).put(bytes[b]);
				System.out.println("next byte wc=\"" + wc + "\", character code(s): " + Arrays.toString(wc.get()) + ", dictionarySize=" + dictionarySize + "");
				
				if (dictionary.containsKey(wc)) {
					w.clear();
					w.put(wc.get());
				}
				else {
					//if (!dictionary.containsKey(w))
					//	throw new IOException("Unknown dictionary symbol: [" + w + "], character code(s): " + Arrays.toString(w.get()));
					
					if (!w.isEmpty())
						out.write(dictionary.get(w));
					
					System.out.println("dictionary.put(\"" + wc + "\", " + dictionarySize + ")");
					dictionary.put(wc, dictionarySize++);
					w.clear();
					w.put(bytes[b]);
				}
			}
			
			
		}
		
		if (!w.isEmpty()) {
			System.out.println("dictionary.put(\"" + wc + "\", " + dictionarySize + ")");
			out.write(dictionary.get(w));
		}

		return true;
	}
	
	
	private static final Map<DictionaryItem,Integer> initializeCompressionDictionary() throws UnsupportedEncodingException {
		Map<DictionaryItem,Integer> dictionary = new HashMap<DictionaryItem,Integer>();
		DictionaryItem di;
		
		for (int i = 0; i < Marker.CLR; i++) {
			di = new DictionaryItem((byte) i);
			System.out.println("dictionary.put(\"" + di + "\", " + i + ")");
			dictionary.put(di, i);
		}
		
		return dictionary;
	}*/

	
	public static final String decompress(byte[] compressed) {
		Map<Integer,String> dictionary = initializeDecompressionDictionary();
		int dictionarySize = Marker.NEW;
		
		for (byte b : compressed) {
			if (b == Marker.CLR) {
				// Reset the dictionary...
				dictionary = initializeDecompressionDictionary();
				dictionarySize = Marker.NEW;
			}
			else if (b == Marker.EOD) {
				// End Of Document...
				break;
			}
			else {
				dictionary.put(dictionarySize++, "");
			}
		}
		
		dictionary.clear();
		return null;
	}
	
	
	private static final Map<Integer,String> initializeDecompressionDictionary() {
		Map<Integer,String> dictionary = new HashMap<Integer,String>();
		
		for (int i = 0; i < Marker.CLR; i++)
			dictionary.put(i, "" + (char)i);
		
		return dictionary;
	}
	
}
