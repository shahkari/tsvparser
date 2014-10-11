// This class in JsonContentHandler which converts XHTML to individual JSON records.
//Initially we used jsoup library to for JSON data, but it was really slow,so we created our own dummyparser in TSVparser file 
//which finds records between specified tags. And than it is converted to JSON files.

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.XHTMLContentHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.xml.sax.ContentHandler;



public class JsonHandler extends XHTMLContentHandler {

	public static int index = 800,total=0;
	int dataLength_c, i;
	public static List<String> jsonList = new ArrayList<String>();
	public final int BATCH_SIZE = 1;
// Name for JSON files that would be generated for each record.
	String filePrefix = "/Users/karishma/Desktop/572/572_all_data_files_2/Json_Example";

	public JsonHandler(ContentHandler handler, Metadata metadata) {
		super(handler, metadata);
	}
	public static void temp() {
		// Document doc = Jsoup.parse(handler.toString());

		// Element tbody=doc.select("tbody").first();
		//
		// for(Node node : tbody.childNodes()) {
		// List<String> currentData = new ArrayList<String>();
		// for(Node trNode : node.childNodes()) {
		// for(Node tdNode : trNode.childNodes()) {
		// currentData.add(tdNode.toString());
		// }
		// }
		// htmlData.add(currentData);
		// }

	}
/// Convert method takes handler as parameter, here it takes XHTMLhandler/
	public void convert(ContentHandler handler) {
//Calls for dummy parser
		List<List<String>> htmlData = TSVparser.dummyParser(handler.toString());
		int dataLength = htmlData.size();
		dataLength_c = dataLength - 1;
		for (i = 1; i < dataLength; ++i) {
			Map<String, String> currentJsonMap = new HashMap<String, String>();
			int len = htmlData.get(i).size();
			for (int j = 0; j < len; ++j) {
			
				currentJsonMap.put(htmlData.get(0).get(j),
						htmlData.get(i).get(j));
			}
		//Calls method to convert given table data to JSON
			String jsonString = converToJson(currentJsonMap);
			writeJsonData(jsonString);
			
		}
		System.out.println("total no of files produced: " + total);
	}
// Writes each JSON string to a file by calling writetoafile method.
	private void writeJsonData(String jsonString) {
		
		total++;
		writeToFile(filePrefix + "-" + index + ".txt", jsonString);
//		jsonList.clear();
		index += 1;


	}
// Converts each row from table to JSON string
	public static String converToJson(Map<String, String> jsonMap) {
		String jsonString = "";
		jsonString += "{";
		for (Entry<String, String> key : jsonMap.entrySet()) {
			if (!key.getKey().isEmpty()) {
				jsonString += "\"";
				jsonString += key.getKey();
				jsonString += "\"";
				jsonString += ":";
				jsonString += "\"";
				jsonString += key.getValue();
				jsonString += "\"";
				jsonString += ",";
			}

		}
		jsonString = jsonString.substring(0, jsonString.length() - 1);
		jsonString += "}";
		return jsonString;
	}

	private void writeToFile(String fileName, String jsonString) {
		FileWriter fileWriter = null;
		try {

			fileWriter = new FileWriter(fileName);
			fileWriter.write(jsonString);
			System.out.println("file created");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileWriter != null)
				try {
					fileWriter.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

}