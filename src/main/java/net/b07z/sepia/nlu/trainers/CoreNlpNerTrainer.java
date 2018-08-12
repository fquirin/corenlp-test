package net.b07z.sepia.nlu.trainers;

import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.StringUtils;

/**
 * Stanford CoreNLP CRF classifier for training.
 * 
 * @author Florian Quirin
 *
 */
public class CoreNlpNerTrainer implements NerTrainer {
	
	Properties props;
	private String modelFile;
	CRFClassifier<CoreLabel> model;
	String languageCode;

	/**
	 * Setup CoreNLP NER classifier with properties file and training data.
	 * @param propertiesFile
	 * @param trainDataFileBase
	 * @param modelOutputFileBase
	 * @param languageCode
	 */
	public CoreNlpNerTrainer(String propertiesFile, String trainDataFileBase, String modelOutputFileBase, String languageCode){
		this.languageCode = languageCode;
		this.modelFile = modelOutputFileBase + "_" + this.languageCode;
		String trainDataFile = trainDataFileBase + "_" + this.languageCode;
		props = StringUtils.propFileToProperties(propertiesFile);
		props.setProperty("serializeTo", this.modelFile);
		props.setProperty("trainFile", trainDataFile);
		
		//Create a StanfordCoreNLP object 
		//Properties props = new Properties();
		//props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		//StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	}
	
	@Override
	public void train() {
		//train
		SeqClassifierFlags flags = new SeqClassifierFlags(props);
		model = new CRFClassifier<>(flags);
		model.train();
		
		//store
		model.serializeClassifier(modelFile);
	}

}
