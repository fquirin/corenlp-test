package net.b07z.sepia.nlu.examples;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.slf4j.impl.SimpleLogger;

import net.b07z.sepia.nlu.analyzers.CoreNlpNerClassifier;
import net.b07z.sepia.nlu.analyzers.NerClassifier;
import net.b07z.sepia.nlu.tokenizers.RealLifeChatTokenizer;
import net.b07z.sepia.nlu.tokenizers.Tokenizer;
import net.b07z.sepia.nlu.tools.CoreNlpDataHandler;
import net.b07z.sepia.nlu.tools.CustomDataHandler;
import net.b07z.sepia.nlu.trainers.CoreNlpNerTrainer;
import net.b07z.sepia.nlu.trainers.NerTrainer;

/**
 * Test CoreNLP NER with custom trained data.
 * 
 * @author Florian Quirin
 *
 */
public class CoreNlpDemo {
	
	public static void main(String[] args) throws IOException {
		
		System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "WARN");
		//Logger log = LoggerFactory.getLogger(CoreNlpDemo.class);
		
		String nerCompactTrainDataFile = "data/nerCompactTrain.txt";
		String coreNlpPropertiesFile = "data/corenlp.properties";
		String coreNlpModelFile = "data/corenlp.model";		//add .gz for auto-compression
		String coreNlpTrainFile = "data/corenlp_train.txt";
		
		//Create training data from compact custom format
		Map<String, String> trainData = CustomDataHandler.importCompactFormatToMap(nerCompactTrainDataFile);
		
		//Convert compact format to CoreNLP format and write file
		Tokenizer tokenizer = new RealLifeChatTokenizer();
		List<String> trainDataLines = CoreNlpDataHandler.importCompactTrainData(trainData, tokenizer, false);
		CoreNlpDataHandler.writeTrainData(coreNlpTrainFile, trainDataLines);
		
		//Train
		NerTrainer trainer = new CoreNlpNerTrainer(coreNlpPropertiesFile, coreNlpTrainFile, coreNlpModelFile);
		trainer.train();
		
		//Test
		NerClassifier ner = new CoreNlpNerClassifier(coreNlpModelFile, tokenizer);
		System.out.println(ner.analyzeSentence("Show me the way from LA to SF"));
		System.out.println(ner.analyzeSentence("Show me the way from Essen to Bochum"));
		System.out.println(ner.analyzeSentence("I want to go to the Statue of Liberty"));
		System.out.println(ner.getEntities("I need to go to Westminster Abbey"));
		System.out.println(ner.getEntities("I'm looking for flights from SFO to ORD"));
		System.out.println(ner.getEntities("I want to visit Chicago"));
	}

}