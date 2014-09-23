/**
 * 
 */
package com.dnaa.common.utils;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author Chris
 *
 */
public class SequencePackerUnitTests {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	/**
	 * Test method for the specified sequence to be packed and verified for
	 * {@link com.dnaa.tools.parker.BitPacker#pack(java.lang.String)}.
	 * 
	 * Sequence: (length=44)
	 * A c T G  T c A T  G G G A  c t g C  a T G c  a a a T  g G G C  C c t a  A a G A  a C A C  A C T T 
	 * 00011110 11010011 10101000 01111001 00111001 00000011 10101001 01011100 00001000 00010001 00011111
	 * x1E      xD3      xA8      x79      x39      x03      xA9      x5C      x08      x11      x1F
	 * 30       -45      -88      121      57       3        -87      92       8        17       31
	 * (byte.length=11)
	 * 
	 * length=44 => x00 x00 x00 x2C
	 *              0   0   0   44
	 * fully packed:
	 * 00 00 00 2C 1E D3 A8 79 39 03 A9 5C 08 11 1F 
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testSpecificPack() throws UnsupportedEncodingException {
		System.out.println("== testSpecificPack() ==");
		long t, timeToPack, timeToUnpack;
		double reduction = -1d;
		String sequence = "AcTGTcATGGGActgCaTGcaaaTgGGCCctaAaGAaCACACTT";
		String validPackedStrHex = "00 00 00 2C 1E D3 A8 79 39 03 A9 5C 08 11 1F ";
		
		t = System.currentTimeMillis();
		byte[] packed = SequencePacker.pack(sequence);
		timeToPack = System.currentTimeMillis() - t;
		
		assertTrue("packed sequence is not valid!", validPackedStrHex.equalsIgnoreCase(toHexString(packed)));
		
		t = System.currentTimeMillis();
		String unpacked = SequencePacker.unpackDNA(packed);
		timeToUnpack = System.currentTimeMillis() - t;
		
		if (!sequence.equalsIgnoreCase(unpacked)) {
			System.out.println("ERROR - unpacked solution is not equals to the original.");
			System.out.println("  sequence [" + sequence + "] size=" + sequence.getBytes().length + " bytes");
			System.out.println("    packed [" + new String(packed) + "]<=>hex[" + toHexString(packed) + "] size=" + packed.length + " bytes");
			System.out.println("  unpacked [" + unpacked + "] size=" + unpacked.getBytes().length + " bytes");
		}
		else {
			reduction = 100d * (double)packed.length / (double)sequence.getBytes().length;
		}
		System.out.printf("reduction %.2f%% \n", reduction);
		System.out.printf("time to pack %dms \n", timeToPack);
		System.out.printf("time to unpack %dms \n", timeToUnpack);
		System.out.flush();
		
		assertTrue("unpacked solution is not equals to the original.", sequence.equalsIgnoreCase(unpacked));
	}
	
	/**
	 * Test method to calculate statistics for
	 * {@link com.dnaa.tools.parker.BitPacker#pack(java.lang.String)}.
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void testPackStatistics() {
		System.out.println("== testPackStatistics() ==");
		int repeatTestForeachSize = 10;
		int[] sequenceSizes  = { 24,32,36,48,64,88,112,128,245,389,512,968,1024,4095,10056,94163,231645,515848,1324957 };
		long[] timeToPack = new long[repeatTestForeachSize];
		long[] timeToUnpack = new long[repeatTestForeachSize];
		double[] reduction = new double[repeatTestForeachSize];
		String sequence, unpacked;
		byte[] packed;
		long t;
		
		for (int size : sequenceSizes) {
			System.out.println("-- testing seqeunce size=" + size + " --> ");
			for (int i = 0; i < repeatTestForeachSize; i++) {
				sequence = SequenceTools.generateDNA(size);
				
				t = System.currentTimeMillis();
				packed = SequencePacker.pack(sequence);
				timeToPack[i] = System.currentTimeMillis() - t;
				
				t = System.currentTimeMillis();
				unpacked = SequencePacker.unpackDNA(packed);
				timeToUnpack[i] = System.currentTimeMillis() - t;
				
				if (!sequence.equalsIgnoreCase(unpacked)) {
					System.out.println("ERROR - unpacked solution is not equals to the original.");
					System.out.println("  sequence [" + sequence + "] size=" + sequence.getBytes().length + " bytes");
					System.out.println("    packed [" + new String(packed) + "]<=>hex[" + toHexString(packed) + "] size=" + packed.length + " bytes");
					System.out.println("  unpacked [" + unpacked + "] size=" + unpacked.getBytes().length + " bytes");
					reduction[i] = -1d;
				}
				else {
					reduction[i] = 100d * (double)packed.length / (double)sequence.getBytes().length;
				}
				assertTrue("unpacked solution is not equals to the original.", sequence.equalsIgnoreCase(unpacked));
			}
			System.out.printf("average reduction %.2f%% %s\n", SequenceTools.average(reduction), Arrays.toString(reduction));
			System.out.printf("average time to pack %.2fms %s\n", SequenceTools.average(timeToPack), Arrays.toString(timeToPack));
			System.out.printf("average time to unpack %.2fms %s\n", SequenceTools.average(timeToUnpack), Arrays.toString(timeToUnpack));
		}
	}

	/**
	 * Test method for {@link com.dnaa.tools.parker.BitPacker#unpack(byte[], char[])}.
	 */
	@Test
	public void testUnpack() {
		fail("Not yet implemented"); // TODO
	}
	
	


	private static final String toHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (byte b : bytes)
			sb.append(String.format("%02X ", b));
		
		return sb.toString();
	}
	
}
