package net.b07z.sepia.nlu.analyzers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import net.b07z.sepia.nlu.classifiers.NerClassifier;
import net.b07z.sepia.nlu.classifiers.NerEntry;
import net.b07z.sepia.nlu.classifiers.TokenLabel;
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
	private String languageCode;
	
	/**
	 * Create NER classifier with model and tokenizer.
	 * @param modelFileBase
	 * @param tokenizer
	 * @param languageCode
	 */
	public CoreNlpNerClassifier(String modelFileBase, Tokenizer tokenizer, String languageCode){
		this.languageCode = languageCode;
		String modelFile = modelFileBase + "_" + this.languageCode;
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
			TokenLabel tl = new TokenLabel(token, label, -1.0);
			NerEntry ne = new NerEntry(getOriginalToken(token, sentence), token, label, Arrays.asList(tl));
			nerEntries.add(ne);
		}
		return nerEntries;
	}
	
	@Override
	public List<NerEntry> getEntities(String sentence, boolean fuseSame, boolean removeDefaultLabel) {
		String normSentence = tokenizer.normalizeSentence(sentence);
		
		//filter special labels
		StringBuffer sb = new StringBuffer();
		if (removeDefaultLabel){
			sb.append("O");
		}
		String BOS = tokenizer.getBeginningOfSentenceLabel().trim();
		String EOS = tokenizer.getEndOfSentenceLabel().trim();
		String SEP = tokenizer.getSeparatorLabel().trim();
		if (!BOS.isEmpty()){
			sb.append("|");
			sb.append(Pattern.quote(BOS));
		}
		if (!EOS.isEmpty()){
			sb.append("|");
			sb.append(Pattern.quote(EOS));
		}
		if (!SEP.isEmpty()){
			sb.append("|");
			sb.append(Pattern.quote(SEP));
		}
		String commonlabelRegex = sb.toString().replaceFirst("^\\|", "").toString();
		//System.out.println("commonlabelRegex: " + commonlabelRegex); 			//DEBUG
		//String commonlabelRegex = "O" + "|" + BOS + "|" + EOS + "|" + SEP;
		
		List<CoreLabel> result = model.classify(normSentence).get(0); 	//since this is one sentence it should return size=1
		List<NerEntry> nerEntries = new ArrayList<>();
		NerEntry ne = null;
		String lastLabel = "";
		for (CoreLabel cl : result) {
			String label = cl.getString(AnswerAnnotation.class);
			//add to previous entry?
			if (fuseSame && ne != null && label.equals(lastLabel)){
				String token = cl.originalText();
				ne.addToken(token);
				ne.addOriginalToken(getOriginalToken(token, sentence));
			
			//make new entry
			}else{
				//skip all labels that are common
				if (!label.matches(commonlabelRegex)){
					lastLabel = label;
					String token = cl.originalText();
					TokenLabel tl = new TokenLabel(token, label, -1.0);
					ne = new NerEntry(getOriginalToken(token, sentence), token, label, Arrays.asList(tl));
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
