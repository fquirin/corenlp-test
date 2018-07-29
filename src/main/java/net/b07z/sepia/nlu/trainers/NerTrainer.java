package net.b07z.sepia.nlu.trainers;

/**
 * NER interface for training models.
 * 
 * @author Florian Quirin
 *
 */
public interface NerTrainer {
	
	/**
	 * Train model with info given in constructor and store it.
	 */
	public void train();
}
