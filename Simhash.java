// We took this library from : https://github.com/sing1ee/simhash-java.git
// It had two problems: 1.) It was overflowing hash value for individual features 
//						2.) Overall Hashvalue generated for whole data was also overflowed.
// So we modified and rewrote it.
 
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class Simhash {

	private IWordSeg wordSeg;

	public Simhash(IWordSeg wordSeg) {
		this.wordSeg = wordSeg;
	}

	public int hammingDistance(int hash1, int hash2) {
		int i = hash1 ^ hash2;
		i = i - ((i >>> 1) & 0x55555555);
		i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
		i = (i + (i >>> 4)) & 0x0f0f0f0f;
		i = i + (i >>> 8);
		i = i + (i >>> 16);
		return i & 0x3f;
	}

	public int hammingDistance(long hash1, long hash2) {
		long i = hash1 ^ hash2;
		i = i - ((i >>> 1) & 0x5555555555555555L);
		i = (i & 0x3333333333333333L) + ((i >>> 2) & 0x3333333333333333L);
		i = (i + (i >>> 4)) & 0x0f0f0f0f0f0f0f0fL;
		i = i + (i >>> 8);
		i = i + (i >>> 16);
		i = i + (i >>> 32);
		return (int) i & 0x7f;
	}

	public int hammingDistance(BigInteger hash1, BigInteger hash2) {
		BigInteger I = hash1.xor(hash2);
		BigInteger exp1_p1 = I.shiftRight(1); 
		exp1_p1.and(BigInteger.valueOf(0x5555555555555555L));
		I = I.subtract(exp1_p1);
		exp1_p1 = I.and(BigInteger.valueOf(0x3333333333333333L));
		BigInteger exp1_p2 = I.shiftRight(2).and(BigInteger.valueOf(0x3333333333333333L));
		I = exp1_p1.add(exp1_p2);
		exp1_p1 = I.add(I.shiftRight(4));
		I = exp1_p1.and(BigInteger.valueOf(0x0f0f0f0f0f0f0f0fL));
		I = I.add(I.shiftRight(8));
		I = I.add(I.shiftRight(16));
		I = I.add(I.shiftRight(32));
		return I.and(BigInteger.valueOf(0x7f)).intValue();
	}

	
    public String makeSHA1Hash(String input)
            throws NoSuchAlgorithmException
        {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.reset();
            byte[] buffer = input.getBytes();
            md.update(buffer);
            byte[] digest = md.digest();

            String hexStr = "";
            for (int i = 0; i < digest.length; i++) {
                hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
            }
            return hexStr;
        }

	
	public BigInteger simhash64(String doc) {
		int bitLen = 64;
		int[] bits = new int[bitLen];
		List<String> tokens = wordSeg.tokens(doc);
		for (String t : tokens) {
			BigInteger v = MurmurHash.hash64_ours(t);

			for (int i = bitLen; i >= 1; --i) {
				if (v.testBit(bitLen-i) )
					++bits[i - 1];
				else
					--bits[i - 1];
			}
		}
//		long hash = 0x0000000000000000;
//		long one = 0x0000000000000001;
		BigInteger ONE = BigInteger.ONE;
		BigInteger HASH = BigInteger.ZERO;
		for (int i = bitLen; i >= 1; --i) {
			if (bits[i - 1] > 1) {
				HASH = HASH.or(ONE);
//				hash |= one;
			}
			ONE = ONE.multiply(BigInteger.valueOf(2));
//			one = one << 1;
		}
		return HASH;
	}

	private BigInteger generateHashValue(String t) 
	{
		BigInteger hash = BigInteger.valueOf(5381);
		for(int i=0;i<t.length();++i) {
			hash = hash.multiply(BigInteger.valueOf(33));
			hash = hash.add(BigInteger.valueOf((int)t.charAt(i)));
		}
		return hash;
	}

	public long simhash32(String doc) {
		int bitLen = 32;
		int[] bits = new int[bitLen];
		List<String> tokens = wordSeg.tokens(doc);
		for (String t : tokens) {
			int v = MurmurHash.hash32(t);
			for (int i = bitLen; i >= 1; --i) {
				if (((v >> (bitLen - i)) & 1) == 1)
					++bits[i - 1];
				else
					--bits[i - 1];
			}
		}
		int hash = 0x00000000;
		int one = 0x00000001;
		for (int i = bitLen; i >= 1; --i) {
			if (bits[i - 1] > 1) {
				hash |= one;
			}
			one = one << 1;
		}
		return hash;
	}
}
