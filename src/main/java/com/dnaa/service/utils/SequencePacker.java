/**
 * 
 */
package com.dnaa.service.utils;

import java.util.HashMap;
import java.util.Map;

import com.dnaa.tools.parker.BitPacker;

/**
 * @author Chris
 *
 */
public class SequencePacker {

	private static final char[] dnaMap = { 'A', 'C', 'G', 'T' };
	private static final char[] rnaMap = { 'A', 'C', 'G', 'U' };
	
	private static final Map<Character,Integer> map =
			new HashMap<Character,Integer>();
	
	static {
		map.put('a', 0);
		map.put('A', 0);
		map.put('c', 1);
		map.put('C', 1);
		map.put('g', 2);
		map.put('G', 2);
		map.put('t', 3);
		map.put('T', 3);
		map.put('u', 3);
		map.put('U', 3);
	};
	
	
	public static final byte[] pack(String unpacked) {
		return unpacked != null ? BitPacker.pack(unpacked) : new byte[0];
	}
	
	
	public static final String unpackDNA(String packed) {
		return packed != null ? BitPacker.unpack(packed.getBytes(), dnaMap) : "";
	}
	
	
	public static final String unpackRNA(String packed) {
		return packed != null ? BitPacker.unpack(packed.getBytes(), rnaMap) : "";
	}

}
