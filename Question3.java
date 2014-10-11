import java.io.File;

public class Question3 {

	public static void main(String args[]) {
		int i = 0;
// Below code reads all tsv files and it passes each file to the TSVparser.
		
		String directoryPath = "/Users/karishma/desktop/572/csci572_data";
		File directory = new File(directoryPath);
		for (String tsvFileName : directory.list()) {
			long startTime = System.currentTimeMillis();
			String currentFileName = directoryPath + File.separator
					+ tsvFileName;
			System.out.println("CurrentFilePath: " + currentFileName);
			TSVparser.parseContent(currentFileName);
			long endTime = System.currentTimeMillis();
			System.out.println(" for one operation "
					+ (endTime - startTime + 1));
			i++;
		}
		// Outputs total number of tsv files,converted to individual JSON files.
		System.out.println("total no of  files processed: " + i);
	}
}
