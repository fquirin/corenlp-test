package net.b07z.sepia.nlu.analyzers;

import java.util.List;

public class NerEntry {
	
	private String token;
	private String originalToken;
	private String bestLabel;
	private String[] allLabels;
	
	public NerEntry(String originalToken, String token, String bestLabel, String[] allLabels){
		this.originalToken = originalToken;
		this.token = token;
		this.bestLabel = bestLabel;
		this.allLabels = allLabels;
	}
	
	public String getToken(){
		return token;
	}
	
	public String getOriginalToken(){
		return originalToken;
	}
	
	public String getBestLabel(){
		return bestLabel;
	}
	
	public String[] getAllLabels(){
		return allLabels;
	}
	
	public void addToken(String tokenToAdd){
		token += (" " + tokenToAdd);
	}
	public void addOriginalToken(String tokenToAdd){
		originalToken += (" " + tokenToAdd);
	}
	
	@Override
	public String toString(){
		return (originalToken + "=" + bestLabel);
	}
	
	//---------- helpers ----------
	
	/**
	 * Take a list of NER entries and convert the best labels to one string. Handy for comparing to test data.
	 * @param entryList
	 * @return
	 */
	public static String getLabelStringFromEntryList(List<NerEntry> entryList){
		StringBuffer buffer = new StringBuffer();
		for (NerEntry ne : entryList){
			buffer.append(ne.bestLabel);
			buffer.append(" ");
		}
		return buffer.toString().trim();
	}
	/**
	 * Take a list of NER entries and convert the normalized tokens to one string.
	 * @param entryList
	 * @return
	 */
	public static String getTokenStringFromEntryList(List<NerEntry> entryList){
		StringBuffer buffer = new StringBuffer();
		for (NerEntry ne : entryList){
			buffer.append(ne.token);
			buffer.append(" ");
		}
		return buffer.toString().trim();
	}

}
