package net.b07z.sepia.nlu.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.b07z.sepia.nlu.tokenizers.Tokenizer;

/**
 * Writes data in different formats to a file that can be used to train Stanford's CoreNLP NER.
 * 
 * @author Florian Quirin
 *
 */
public class CoreNlpDataHandler {
	
	/**
	 * Import data in compact map form, key=sentence, value=labels, e.g.:<br> 
	 * key="Way from A to B"<br>
	 * value="O O START O END" (do NOT include BOS and EOS labels!!)<br>
	 * Uses the tokenizer to get tokens from sentence and adds beginning/end-of-sentence tokens automatically.
	 * @param compactMap
	 * @param tokenizer
	 * @param failOnMismatch - fail or skip on token-label mismatch?
	 * @return
	 */
	public static List<String> importCompactTrainData(Map<String, String> compactMap, Tokenizer tokenizer, boolean failOnMismatch){
		List<String> trainDataLines = new ArrayList<>();
		for (Map.Entry<String, String> es : compactMap.entrySet()){
			String sentence = es.getKey();
			String labelString = tokenizer.getBeginningOfSentenceLabel() + es.getValue() + tokenizer.getEndOfSentenceLabel();
			List<String> tokens = tokenizer.getTokens(sentence);
			String[] labels = labelString.trim().split("\\s+");
			boolean gotError = false;
			if (tokens.size() != labels.length){
				if (failOnMismatch){
					throw new RuntimeException("Tokens and labels size does not match for: " + sentence);
				}else{
					System.err.println("Skipped sentence - Tokens and labels size does not match for: " + sentence);
				}
				gotError = true;
			}
			if (!gotError){
				for (int i=0; i<tokens.size(); i++){
					trainDataLines.add(tokens.get(i) + "\t" + labels[i]);
				}
			}
		}
		return trainDataLines;
	}
	
	/**
	 * Write data imported e.g. via {@link #importCompactTrainData} to file.
	 * @param filePath
	 * @param data
	 * @throws IOException
	 */
	public static void writeTrainData(String filePath, List<String> data) throws IOException{
		Files.write(Paths.get(filePath), data, StandardCharsets.UTF_8);
	}
}
