/**
 * 
 */
package com.dnaa.tools.lzw;

import static org.junit.Assert.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dnaa.tools.SequenceTools;
import com.dnaa.tools.parker.BitPacker;

/**
 * @author Chris
 *
 */
public class LZWTester {

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
	 * Test method for {@link com.dnaa.tools.lzw.LZW#compress(java.lang.String)}.
	 * @throws IOException 
	 */
	@Test
	public void testCompress() throws IOException {
		System.out.println("== test_lzw() ==");
		int repeatTestForeachSize = 10;
		int[] sequenceSizes  = { 24,32,36,48,64,88,112,128,245,389,512,968,1024,4095,10056,94163,231645,515848,1324957 };
		long[] timeToPack = new long[repeatTestForeachSize];
		long[] timeToUnpack = new long[repeatTestForeachSize];
		long[] timeToCompress = new long[repeatTestForeachSize];
		double[] reduction = new double[repeatTestForeachSize];
		double[] compression = new double[repeatTestForeachSize];
		String sequence, unpacked, compressed = "";
		byte[] packed;
		long t;
		
		FileWriter writer = new FileWriter("myTestStats.csv");
		
		writer.append("Sequence Length");
		writer.append(',');
		writer.append("Time to Pack");
		writer.append(',');
		writer.append("Pack Compression Ratio");
		writer.append(',');
		writer.append("Time to Unpack");
		writer.append(',');
		writer.append("Time to Compress");
		writer.append(',');
		writer.append("Compression Ratio of Packed");
		writer.append('\n');

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
				
				t = System.currentTimeMillis();
				try { compressed = LZW.compress( new String(packed,"UTF-8") ); }
				catch (IOException e) { e.printStackTrace(); }
				timeToCompress[i] = System.currentTimeMillis() - t;
				
				if (!sequence.equalsIgnoreCase(unpacked)) {
					System.out.println("ERROR - unpacked solution is not equals to the original.");
					System.out.println("  sequence [" + sequence + "] size=" + sequence.getBytes().length + " bytes");
					System.out.println("    packed [" + new String(packed) + "] size=" + packed.length + " bytes");
					System.out.println("  unpacked [" + unpacked + "] size=" + unpacked.getBytes().length + " bytes");
					System.out.println("compressed [" + compressed + "] size=" + compressed.getBytes().length + " bytes");
					reduction[i] = -1d;
				}
				else {
					reduction[i] = 100d * (double)packed.length / (double)sequence.getBytes().length;
					compression[i] = 100d * (double)compressed.getBytes().length / (double)packed.length;
					
					writer.append("" + sequence.getBytes().length);
					writer.append(',');
					writer.append("" + timeToPack[i]);
					writer.append(',');
					writer.append("" + (100*reduction[i]));
					writer.append(',');
					writer.append("" + timeToUnpack[i]);
					writer.append(',');
					writer.append("" + timeToCompress[i]);
					writer.append(',');
					writer.append("" + (100*compression[i]));
					writer.append('\n');
				}
				assertTrue("unpacked solution is not equals to the original.", sequence.equalsIgnoreCase(unpacked));
			}
			System.out.printf("average reduction %.2f%% %s\n", SequenceTools.average(reduction), Arrays.toString(reduction));
			System.out.printf("average time to pack %.2fms %s\n", SequenceTools.average(timeToPack), Arrays.toString(timeToPack));
			System.out.printf("average time to unpack %.2fms %s\n", SequenceTools.average(timeToUnpack), Arrays.toString(timeToUnpack));
			System.out.printf("average time to compress %.2fms %s\n", SequenceTools.average(timeToCompress), Arrays.toString(timeToCompress));
			System.out.printf("average compression %.2f%% %s\n", SequenceTools.average(compression), Arrays.toString(compression));
			
		}
		
		writer.flush();
	    writer.close();
	}

	/**
	 * Test method for {@link com.dnaa.tools.lzw.LZW#decompress(byte[])}.
	 */
	@Test
	public void testDecompress() {
		fail("Not yet implemented"); // TODO
	}

}
