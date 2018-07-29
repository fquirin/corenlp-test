package net.b07z.sepia.nlu.analyzers;

public class NerEntry {
	
	private String token;
	private String originalToken;
	private String label;
	
	public NerEntry(String originalToken, String token, String label){
		this.originalToken = originalToken;
		this.token = token;
		this.label = label;
	}
	
	public String getToken(){
		return token;
	}
	
	public String getOriginalToken(){
		return originalToken;
	}
	
	public String getLabel(){
		return label;
	}
	
	public void addToken(String tokenToAdd){
		token += (" " + tokenToAdd);
	}
	public void addOriginalToken(String tokenToAdd){
		originalToken += (" " + tokenToAdd);
	}
	
	@Override
	public String toString(){
		return (originalToken + "=" + label);
	}

}
