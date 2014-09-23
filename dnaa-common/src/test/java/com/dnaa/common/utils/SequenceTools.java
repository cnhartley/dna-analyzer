/**
 * 
 */
package com.dnaa.common.utils;

import java.util.Random;

/**
 * @author Chris
 *
 */
public class SequenceTools {

	private static final char[] dnaChars = { 'a','A','c','C','g','G','t','T' };
	private static final char[] rnaChars = { 'a','A','c','C','g','G','u','U' };
	
	public static final String generateDNA(int size) {
		return generateSequence(size, dnaChars);
	}
	
	public static final String generateRNA(int size) {
		return generateSequence(size, rnaChars);
	}
	
	private static final String generateSequence(int size, char[] chars) {
		StringBuilder sb = new StringBuilder();
		Random rand = new Random(System.currentTimeMillis());
		
		for (int i = 0; i < size; i++) {
			sb.append(chars[rand.nextInt(chars.length)]);
		}
		
		return sb.toString();
	}
	
	
	public static final double average(Number[] values) {
		double total = 0d;
		int length = values.length;
		for (Number value : values)
			if (value != null)
				total += value.doubleValue();
			else
				length--;
		return total / length;
	}
	
	
	public static final double average(double[] values) {
		double total = 0d;
		for (double value : values)
			total += value;
		return total / values.length;
	}
	
	
	public static final double average(long[] values) {
		double total = 0d;
		for (long value : values)
			total += value;
		return total / values.length;
	}
	
	
	public static final double average(int[] values) {
		double total = 0d;
		for (int value : values)
			total += value;
		return total / values.length;
	}


}
