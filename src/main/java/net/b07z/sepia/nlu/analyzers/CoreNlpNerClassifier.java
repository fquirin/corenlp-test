package net.b07z.sepia.nlu.analyzers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import net.b07z.sepia.nlu.tokenizers.Tokenizer;

/**
 * Stanford CoreNLP implementation of NER classifier.
 * 
 * @author Florian Quirin
 *
 */
public class CoreNlpNerClassifier implements NerClassifier{
	
	private CRFClassifier<CoreLabel> model;
	private Tokenizer tokenizer;
	
	/**
	 * Create NER classifier with model and tokenizer.
	 * @param model
	 * @param tokenizer
	 */
	public CoreNlpNerClassifier(String modelFile, Tokenizer tokenizer){
		this.model = CRFClassifier.getClassifierNoExceptions(modelFile);
		this.tokenizer = tokenizer;
	}

	@Override
	public List<NerEntry> analyzeSentence(String sentence) {
		String normSentence = tokenizer.normalizeSentence(sentence);
		List<CoreLabel> result = model.classify(normSentence).get(0); 	//since this is one sentence it should return size=1
		List<NerEntry> nerEntries = new ArrayList<>();
		for (CoreLabel cl : result) {
			String label = cl.getString(AnswerAnnotation.class);
			String token = cl.originalText();
			NerEntry ne = new NerEntry(getOriginalToken(token, sentence), token, label);
			nerEntries.add(ne);
		}
		return nerEntries;
	}
	
	@Override
	public List<NerEntry> getEntities(String sentence) {
		String normSentence = tokenizer.normalizeSentence(sentence);
		String BOS = Pattern.quote(tokenizer.getBeginningOfSentenceLabel().trim());
		String EOS = Pattern.quote(tokenizer.getEndOfSentenceLabel().trim());
		String SEP = Pattern.quote(tokenizer.getSeparatorLabel().trim());
		String commonlabelRegex = "O" + "|" + BOS + "|" + EOS + "|" + SEP;
		List<CoreLabel> result = model.classify(normSentence).get(0); 	//since this is one sentence it should return size=1
		List<NerEntry> nerEntries = new ArrayList<>();
		NerEntry ne = null;
		String lastLabel = "";
		for (CoreLabel cl : result) {
			String label = cl.getString(AnswerAnnotation.class);
			//add to previous entry?
			if (ne != null && label.equals(lastLabel)){
				String token = cl.originalText();
				ne.addToken(token);
				ne.addOriginalToken(getOriginalToken(token, sentence));
			
			//make new entry
			}else{
				//skip all labels that are common
				if (!label.matches(commonlabelRegex)){
					lastLabel = label;
					String token = cl.originalText();
					ne = new NerEntry(getOriginalToken(token, sentence), token, label);
					nerEntries.add(ne);
				}else{
					ne = null;
				}
			}
		}
		return nerEntries;
	}
	
	//This is a kind of "heavy" way to get back the original token before it was converted to lower-case ... :see_no_evil:
	private String getOriginalToken(String normToken, String orgSentence){
		Matcher matcher = Pattern.compile("(?i)" + normToken).matcher(orgSentence);
		if (matcher.find()){
			return matcher.group();
		}else{
			return normToken;
		}
	}

}
