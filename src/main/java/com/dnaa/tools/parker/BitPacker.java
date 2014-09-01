/**
 * 
 */
package com.dnaa.tools.parker;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chartley
 *
 */
public final class BitPacker {

	private static final char[] int2charMapDNA = { 'A','C','G','T' };
	private static final char[] int2charMapRNA = { 'A','C','G','U' };
	
	private static final Map<Character,Integer> nucleotidePackingBitMap =
			new HashMap<Character,Integer>();
	
	static {
		nucleotidePackingBitMap.put('a', 0);
		nucleotidePackingBitMap.put('A', 0);
		nucleotidePackingBitMap.put('c', 1);
		nucleotidePackingBitMap.put('C', 1);
		nucleotidePackingBitMap.put('g', 2);
		nucleotidePackingBitMap.put('G', 2);
		nucleotidePackingBitMap.put('t', 3);
		nucleotidePackingBitMap.put('T', 3);
		nucleotidePackingBitMap.put('u', 3);
		nucleotidePackingBitMap.put('U', 3);
	}
	
	
	public static final byte[] pack(String str) {
		return pack(str, nucleotidePackingBitMap);
	}
	
	
	public static final byte[] pack(String sequence, Map<Character,Integer> char2bitMap) {
		ByteBuffer bb = ByteBuffer.allocate((int)Math.ceil((Integer.SIZE + sequence.length() * 2d) / Byte.SIZE));
		bb.putInt(sequence.length());
		
		byte b = 0;
		int bit = Byte.SIZE - 2;
		for (char c : sequence.toCharArray()) {
			b |= (char2bitMap.get(c) << bit);
			if (bit == 0) {
				bb.put(b);
				bit = Byte.SIZE;
				b = 0;
			}
			bit -= 2;
		}
		
		if (bb.hasRemaining())
			bb.put(b);
		
		return bb.array();
	}

	
	public static final String unpackDNA(byte[] bytes) {
		return unpack(bytes, int2charMapDNA);
	}
	
	public static final String unpackRNA(byte[] bytes) {
		return unpack(bytes, int2charMapRNA);
	}
	
	public static final String unpack(byte[] bytes, char[] mapping) {
		int bitShift = Integer.SIZE - Integer.numberOfLeadingZeros(mapping.length - 1);
		StringBuilder sb = new StringBuilder();
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		
		int len = bb.getInt();
		while (bb.hasRemaining()) {
			int bit = Byte.SIZE - bitShift;
			for (byte b = bb.get(); len > 0 && bit >= 0; len--, bit -= bitShift) {
				sb.append(mapping[(b >> bit) & 3]);
			}
		}
		
		bb.clear();
		return sb.toString();
	}
	
}
