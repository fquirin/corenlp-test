package net.b07z.sepia.nlu.examples;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.slf4j.impl.SimpleLogger;

import net.b07z.sepia.nlu.analyzers.CoreNlpNerClassifier;
import net.b07z.sepia.nlu.classifiers.NerClassifier;
import net.b07z.sepia.nlu.classifiers.NerEntry;
import net.b07z.sepia.nlu.tokenizers.RealLifeChatTokenizer;
import net.b07z.sepia.nlu.tokenizers.Tokenizer;
import net.b07z.sepia.nlu.tools.CompactDataEntry;
import net.b07z.sepia.nlu.tools.CompactDataHandler;
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
public class CoreNlpNerDemo {
	
	public static void main(String[] args) throws IOException {
		System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "WARN");
		//Logger log = LoggerFactory.getLogger(CoreNlpDemo.class);
		long tic = System.currentTimeMillis();
		
		String nerCompactTrainDataFile = "data/nerCompactTrain.txt";
		String nerCompactTestDataFile = "data/nerCompactTest.txt";
		String coreNlpPropertiesFile = "data/corenlp.properties";
		String modelFileBase = "data/corenlp_model_ner";		//add .gz for auto-compression
		String trainFileBase = "data/corenlp_train_ner";
		
		String languageCode = "en";
		
		//Create training data from compact custom format
		Collection<CompactDataEntry> trainData = CustomDataHandler.importCompactData(nerCompactTrainDataFile);
		Collection<CompactDataEntry> testData = CustomDataHandler.importCompactData(nerCompactTestDataFile);
		
		//Convert compact format to CoreNLP format and write file
		Tokenizer tokenizer = new RealLifeChatTokenizer();
		CompactDataHandler cdh = new CoreNlpDataHandler();
		List<String> trainDataLines = cdh.importTrainDataNer(trainData, tokenizer, false, null);
		String trainFile = trainFileBase + "_" + languageCode;
		CustomDataHandler.writeTrainData(trainFile, trainDataLines);
		
		//Train
		NerTrainer trainer = new CoreNlpNerTrainer(coreNlpPropertiesFile, trainFileBase, modelFileBase, languageCode);
		trainer.train();
		
		//Test
		NerClassifier ner = new CoreNlpNerClassifier(modelFileBase, tokenizer, languageCode);
		int good = 0;
		int bad = 0;
		for (CompactDataEntry cde : testData){
			String sentence = cde.getSentence();
			List<NerEntry> nerRes = ner.getEntities(sentence, false, false);
			String labelsAsString = NerEntry.getLabelStringFromEntryList(nerRes);
			String labelsExpected = cde.getLabels();
			System.out.println(sentence);
			System.out.println(NerEntry.getTokenStringFromEntryList(nerRes));
			if (labelsExpected.equals(labelsAsString)){
				System.out.println(labelsExpected);
				System.out.println(labelsAsString);
				good++;
			}else{
				System.out.println(labelsExpected);
				System.out.println(labelsAsString + " --- ERROR");
				bad++;
			}
			System.out.println();
		}
		System.out.println("Good: " + good + ", bad: " + bad + ", prec.: " + ((double)good/(good+bad)));
		System.out.println("Took: " + (System.currentTimeMillis() - tic) + "ms");
	}

}
