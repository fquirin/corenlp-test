package net.b07z.sepia.nlu.analyzers;

import java.util.List;

/**
 * NER classifier interface to analyze text and extract entities.
 * 
 * @author Florian Quirin
 *
 */
public interface NerClassifier {
	
	/**
	 * Return list with NER result entries for sentence.
	 */
	public List<NerEntry> analyzeSentence(String sentence);
	
	/**
	 * Get list of named entities. Skips begin/end-of-sentence labels and optionally the default label "O".
	 * Optionally combines subsequent entities with same name to one entity (e.g. "New" + "York" -> "New York").  	
	 */
	public List<NerEntry> getEntities(String sentence, boolean fuseSame, boolean removeDefaultLabel);

}
