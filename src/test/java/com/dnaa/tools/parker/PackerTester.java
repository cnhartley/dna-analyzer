/**
 * 
 */
package com.dnaa.tools.parker;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dnaa.tools.SequenceTools;

/**
 * @author Chris
 *
 */
public class PackerTester {

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
	 * Test method for {@link com.dnaa.tools.parker.BitPacker#pack(java.lang.String)}.
	 */
	@Test
	public void testPack() {
		System.out.println("== testPack() ==");
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
				packed = BitPacker.pack(sequence);
				timeToPack[i] = System.currentTimeMillis() - t;
				
				t = System.currentTimeMillis();
				unpacked = BitPacker.unpackDNA(packed);
				timeToUnpack[i] = System.currentTimeMillis() - t;
				
				if (!sequence.equalsIgnoreCase(unpacked)) {
					System.out.println("ERROR - unpacked solution is not equals to the original.");
					System.out.println("  sequence [" + sequence + "] size=" + sequence.getBytes().length + " bytes");
					System.out.println("    packed [" + new String(packed) + "] size=" + packed.length + " bytes");
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
	
}
