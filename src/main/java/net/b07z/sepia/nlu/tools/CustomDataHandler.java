package net.b07z.sepia.nlu.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Some useful stuff to handle files with e.g. training data in "exotic" custom format ^^.
 * 
 * @author Florian Quirin
 *
 */
public class CustomDataHandler {
	
	private static final String CUSTOM_DATA_SEPERATOR = " --- ";
	
	/**
	 * Import data from a custom "compact" file to a map with key=sentence, value=labels.
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> importCompactFormatToMap(String filePath) throws IOException{
		Map<String, String> trainData = new HashMap<>();
		List<String> compactData = Files.readAllLines(Paths.get(filePath));
		int N = 0;
		for (String line : compactData){
			N++;
			if (line == null || line.isEmpty()){
				continue;
			}
			String[] keyVal = line.split(CUSTOM_DATA_SEPERATOR);
			if (keyVal.length > 2){
				throw new RuntimeException("Invalid format in line " + N + ": " + line);
			}
			trainData.put(keyVal[0], keyVal[1]);
		}
		return trainData;
	}

}
