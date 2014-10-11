//This handles deduplication part. We tried with MD5 hash and was able to find only 27 records.
//Than we used simhashlibrary but it was overflowing the HASH value so we used BigInteger in murmurhash and Djb2
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Question4 {

	public static String file = "";
	public static int duplicate = 0, i = 0;
	public static int same = 0;
	public static int diff = 0;

	public static List<String> values = new ArrayList<String>();
	public static List<String> diffLines = new ArrayList<String>();

	public static List<String> duplicateLines = new ArrayList<String>();

	// Below code opens the directory which contains all json files and finds
	// duplicates.
	public static void main(String args[]) throws IOException {
		String path = "/users/karishma/desktop/572/572_Json_file";
		File directory = new File(path);
		for (String jsonFileName : directory.list()) {
			file = path + File.separator + jsonFileName;
			System.out.println("File name:" + file);
			System.out.println("Total no of duplicates = " + duplicate);
			System.out.println("Total no of records :" + i);
			getlines(file);
		}

		System.out.println("Done");
		System.out.println("Total no of records :" + i);
		System.out.println("Total no of duplicates = " + duplicate);
		System.out.println("Total no of records left  = " + (i - duplicate));

	}

	// This function preprocesses the string it removes all extra whitespaces
	// and other symbols. But allowed + and - as we are using longitude and
	// latitude.
	private static String preprocess(String n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n.length(); ++i) {
			if (Character.isDigit(n.charAt(i)))
				sb.append(n.charAt(i));
			if (Character.isLowerCase(n.charAt(i))
					|| Character.isUpperCase(n.charAt(i)) || n.charAt(i) == '+'
					|| n.charAt(i) == '-')
				sb.append(Character.toLowerCase(n.charAt(i)));
		}
		return sb.toString();
	}

	// It takes Json files and finds particular key that is needed.
	public static String parseJson(String str) {
		String dedupstring = "";
		String[] data = str.split(",");

		for (int i = 0; i < data.length; i++) {
			if (data[i].contains("title")) {
				dedupstring += preprocess(data[i]) + " ";
			}
			if (data[i].contains("location2 ")) {
				data[i] = data[i].substring(1);
				dedupstring += preprocess(data[i]) + " ";
			}

			if (data[i].contains("company")) {
				data[i] = data[i].substring(1);
				dedupstring += preprocess(data[i]) + " ";
			}
			if (data[i].contains("latitude")) {
				dedupstring += preprocess(data[i]) + " ";
			}
			if (data[i].contains("longitude")) {
//				data[i] = data[i].substring(0, data[i].length() - 1);
				dedupstring += preprocess(data[i]) + " ";
			}
			if (data[i].contains("department")) {
				dedupstring += preprocess(data[i]) + " ";
			}

		}

		return dedupstring;
	}

	public static void getlines(String filename) {
		try {
			// It compares for hamming distance for exact same duplicates and
			// near duplicates
			Comparator<BigInteger> comp1 = new Comparator<BigInteger>() {
				@Override
				public int compare(BigInteger t1, BigInteger t2) {
					Simhash simhash = new Simhash(new BinaryWordSeg());
					return simhash.hammingDistance(t1, t2) == 0 ? 0 : 1;
				}
			};

			FileInputStream f1 = new FileInputStream(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(f1));
			String tmp;
			// It adds record which is duplicate into the treemap.
			ArrayList<String> list = new ArrayList<String>();
			TreeMap<BigInteger, String> map = new TreeMap<BigInteger, String>(
					comp1);
			while ((tmp = br.readLine()) != null) {
				list.add(tmp);
				i++;
			}

			Simhash simhash = new Simhash(new BinaryWordSeg());
			for (String line : list) {
				String convertedLine = parseJson(line);

				BigInteger simkey = simhash.simhash64(convertedLine);
				// checks whether map contains that key value or not.If it
				// contains it is duplicate.
				if (map.containsKey(simkey) == false) {
					map.put(simkey, convertedLine);
				} else {
					// Checks for the value that is present in the map is
					// exactly same or not.(For near duplicates.)
					String oldString = map.get(simkey);
					if (oldString.equals(convertedLine))
						++same;
					else {
						++diff;
					}
					duplicate++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println();
		}

	}

}
