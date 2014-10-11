// This class extends Abstract Parser. 
// Function: It opens TSV files and converts it to xhtml using  xhtmlcontenthandler in parse method. And that result is passed to JsonHandler.
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import javax.swing.text.html.parser.Parser;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.jsoup.Jsoup;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

public class TSVparser extends AbstractParser {
	public Set getSupportedTypes(ParseContext arg0) {
		return null;
	}
	//This method is invoked by parseContent method.

	public void parse(InputStream inputStream, ContentHandler contentHandler,
			Metadata metadata, ParseContext arg3) throws IOException,
			SAXException, TikaException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream));
		String tmp, tmp1;
		String[] names = null;
		int i;
		// This variable takes colheader.txt file which contains names of
		// columns.
//		Please change path file accordingly.
		File path = new File("/users/karishma/downloads/colheaders.txt");
		FileInputStream f1 = new FileInputStream(path);
		BufferedReader br1 = new BufferedReader(new InputStreamReader(f1));
		XHTMLContentHandler xtml = new XHTMLContentHandler(contentHandler,
				metadata);
		xtml.startDocument();
		xtml.characters("<html><head><title></title></head><body><table>");
		while ((tmp1 = br1.readLine()) != null) {
			names = tmp1.split("\t");
			xtml.characters("<tr>");
			for (i = 0; i < names.length; i++) {
				if (!names[i].equals("gap")) {
					xtml.characters("<th>");
					xtml.characters(names[i]);
					xtml.characters("</th>");
				}
			}
		}
		while ((tmp = br.readLine()) != null) {
			String[] words = tmp.split("\t");
			xtml.characters("<tr>");
			for (i = 0; i < words.length; i++) {
				if (!names[i].equals("gap")) {
					if (words[i].isEmpty()) {
						words[i] = "NA";
					}
					xtml.characters("<td>");
					xtml.characters(words[i]);
					xtml.characters("</td>");
				}
			}
			xtml.characters("</tr>");
		}
		xtml.characters("</table></body></html>");
		xtml.endDocument();
		System.out.println("Calling json handler!");
		JsonHandler hand = new JsonHandler(xtml, metadata);

		br1.close();
		hand.convert(xtml);
	}

	// This parser converts XHTML file and finds data between <table></table>
	// tags. It finds headers from first row and other data by splitting it at
	// <tr> tag and it stores data in List.

	public static List<List<String>> dummyParser(String inputHtml) {
		List<List<String>> finalResult = new ArrayList<List<String>>();
		int startindex = inputHtml.indexOf("<table>");
		int endindex = inputHtml.indexOf("</table>");
		String tableString = inputHtml.substring(startindex, endindex);
		String[] rows = tableString.split("<tr>");
		int numRows = rows.length;
		for (int i = 1; i < numRows; ++i) {
			String headers = rows[i].split("</tr>")[0];
			String splitString = (i == 1) ? "<th>" : "<td>";
			String endSplitString = (i == 1) ? "</th>" : "</td>";
			List<String> currentList = new ArrayList<String>();
			for (String header : headers.split(splitString)) {
				currentList.add(header.split(endSplitString)[0]);
			}
			finalResult.add(currentList);
		}

		return finalResult;
	}
// It calls for parse method, and parameters passed for that are: file stream, content handler,metadata and parsercontext.
	public static void parseContent(String fileName) {
		File path = new File(fileName);
		TSVparser parser = new TSVparser();
		Metadata metadata = new Metadata();
		ParseContext context = new ParseContext();
		ContentHandler contenthandler = new BodyContentHandler(
				1024 * 1024 * 1024);
		try {
			parser.parse(new FileInputStream(path), contenthandler, metadata,
					context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
