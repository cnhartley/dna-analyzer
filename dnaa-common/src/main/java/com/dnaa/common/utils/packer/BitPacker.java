/**
 * 
 */
package com.dnaa.common.utils.packer;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

/**
 * @author chartley
 *
 */
public final class BitPacker {
	
	
	/**
	 * 
	 * @param nums
	 * @return
	 */
	private static final Integer maxValueIn(Collection<Integer> nums) {
		Integer max = 0;
		for (Integer num : nums)
			max = Math.max(num, max);
		return max;
	}
	
	
	/**
	 * 
	 * @param sequence
	 * @param char2bitMap
	 * @return
	 */
	public static final byte[] pack(String sequence, Map<Character,Integer> char2bitMap) {
		final int bitShift = Integer.SIZE - Integer.numberOfLeadingZeros(maxValueIn(char2bitMap.values()));
		ByteBuffer bb = ByteBuffer.allocate((int)Math.ceil((Integer.SIZE + sequence.length() * 2d) / Byte.SIZE));
		bb.putInt(sequence.length());
		
		byte b = 0;
		int bit = Byte.SIZE - bitShift;
		for (char c : sequence.toCharArray()) {
			b |= (char2bitMap.get(c) << bit);
			if (bit == 0) {
				bb.put(b);
				bit = Byte.SIZE;
				b = 0;
			}
			bit -= bitShift;
		}
		
		if (bb.hasRemaining())
			bb.put(b);
		
		return bb.array();
	}
	
	
	/**
	 * 
	 * @param bytes
	 * @param mapping
	 * @return
	 */
	public static final String unpack(byte[] bytes, char[] mapping) {
		final int bitShift = Integer.SIZE - Integer.numberOfLeadingZeros(mapping.length - 1);
		final int mask = ~(-1 << bitShift);
		StringBuilder sb = new StringBuilder();
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		
		int len = bb.getInt();
		while (bb.hasRemaining()) {
			int bit = Byte.SIZE - bitShift;
			for (byte b = bb.get(); len > 0 && bit >= 0; len--, bit -= bitShift) {
				sb.append(mapping[(b >> bit) & mask]);
			}
		}
		
		bb.clear();
		return sb.toString();
	}
	
}
