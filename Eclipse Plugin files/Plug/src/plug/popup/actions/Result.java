package plug.popup.actions;

public class Result {
	 
	public String resultURL="";
	public String title="";
	public String description="";
	public String searchEngine="";
	  
	public double content_score = 0.0D;	//ttt+ttd+ttc
	public double context_score = 0.0D;	//lcs
	  
	public double stackOverflowVoteScore=0.0D;
	  
	public double titleToTitleScore=0.0D;
	public double titleToDescriptionScore=0.0D;
	public double titleToContextScore=0.0D;
	
	public double totalScore=0.0D;
	
}
