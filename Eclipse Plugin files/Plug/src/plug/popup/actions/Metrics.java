package plug.popup.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Metrics {
	static Lcs l;
	
	static String title="";
	static String path="";
	
	static double titleToTitleSimilarity(DBObject object, String queryFromPlugin)
	{
		double score = 0.0;
		String title = ((BasicBSONObject) object).getString("title");
	    title = title.replace('\n',' ');
		
	    
	    score=Ranking.calculateCosineSimilarity(createMap(queryFromPlugin), createMap(title));
	    
		return score;
	}
	
	
	
	
	//didn't get it
	static double titleToContextSimilarity(DBObject object, String queryFromPlugin)
	{
		double score1 = 0.0;
		
		
		String context = ((BasicBSONObject) object).getString("code_in_question");
	    if(context==null)
	    {
	    	context = ((BasicBSONObject) object).getString("code");
	    }
	    
	    score1=Ranking.calculateCosineSimilarity(createMap(queryFromPlugin), createMap(context));
		//call cosine similarity function
		return score1;
	}
	
	
	
	static double titleToDescriptionSimilarity(DBObject object, String queryFromPlugin)
	{
		double score = 0.0;
		String searchEngine = ((BasicBSONObject) object).getString("search_engine");
		StringBuilder description = new StringBuilder("");
		String d = "";
		if(searchEngine.equals("stackoverflow"))
		{
			d = ((BasicBSONObject) object).getString("question");
			d = d.replace('\n',' ');	
			description.append(d);
			description.append(" ");
			BasicDBList answersList = (BasicDBList) object.get("answers");
			if(answersList!=null)
			{
				for (int i = 0; i < answersList.size(); i++) 
				{
					BasicDBObject answerObj = (BasicDBObject) answersList.get(i);
					if(answerObj != null)
					{
						String answer = answerObj.getString("answer");
						description.append(answer);
						description.append(" ");
					}
				}
			}
	   }
	   else
	   {
	   		d = ((BasicBSONObject) object).getString("content");
	   		d = d.replace('\n',' ');
	   		description.append(d);
	   }
	  
	   score=Ranking.calculateCosineSimilarity(createMap(queryFromPlugin), createMap(description.toString()));
	  
	   System.out.println("title to description"+score);
	   
	return score;
	}
	
	
	static double stackoverflowVoteScore(DBObject object,String queryFromPlugin)
	{
		
		double scoreFinal=0.0;
		
		String searchEngine = ((BasicBSONObject) object).getString("search_engine");
		
		BasicDBList answersList = (BasicDBList) object.get("answers");
	    
		
		
		int questionScore = 0;
	    if(searchEngine.equals("stackoverflow"))
    	 {
	    	double totalScore = 0;
    		double normalizedStackoverflowVoteScore = 0.0;
    		ArrayList<Integer> answersScore = new ArrayList<Integer>();
    		questionScore = (int) object.get("score");
    		
		    if(answersList!=null)
		    {
		    //System.out.println(answersList.size());
		    	for (int i = 0; i < answersList.size(); i++) 
		    	{
		    		BasicDBObject answerObj = (BasicDBObject) answersList.get(i);
		    		if(answerObj != null)
		    		{
		    			answersScore.add(answerObj.getInt("score"));
		    		}
		    	}
		    }
		   // System.out.println(answersScore.size()+"......."+answersScore);
		    
		    int sumOfAnsScores = 0;
		    for(Integer d : answersScore)
		    	sumOfAnsScores += d;
		    
		   //  System.out.println("Ques score:"+questionScore);
		    
		    totalScore = sumOfAnsScores + questionScore;
		    
		   //  System.out.println("Vote count of posts in page:"+totalScore);
		    
		    totalScore = (double)totalScore/(double)(answersScore.size()+1);
		    
		     int maxAnsVoteCount = Collections.max(answersScore);
		     int minAnsVoteCount = Collections.min(answersScore);
		     double maxVoteCount = 0;
		     double minVoteCount = 0;
		     
		     if(maxAnsVoteCount > questionScore)
		    	 maxVoteCount = maxAnsVoteCount;
		     else
		    	 maxVoteCount = questionScore;
		     
		     if(minAnsVoteCount > questionScore)
		    	 minVoteCount = questionScore;
		     else
		    	 minVoteCount = minAnsVoteCount;
		     if((maxVoteCount - minVoteCount) != 0)
		     {
		    	 normalizedStackoverflowVoteScore = (totalScore - minVoteCount) / (maxVoteCount - minVoteCount);
		     }
		     else
		    	 normalizedStackoverflowVoteScore = 0.0;
		     
		     System.out.println("Stackoverflow Vote Score:"+normalizedStackoverflowVoteScore);
		     System.out.println();
		     scoreFinal = normalizedStackoverflowVoteScore;
		    
    	 }
	    System.out.println("Stackoverflow Vote Score:"+scoreFinal);
		
		return scoreFinal;
	}
	
	
	static double sourceCodeContextMatchingScore(DBObject object, String queryFromPlugin, String codeFromPlugin)
	{
		double scoreFinal = 0;
		
		if(codeFromPlugin.length()==0)
			return 0.0;
		
		String searchEngine = ((BasicBSONObject) object).getString("search_engine");
		
		if(searchEngine.equals("google") || searchEngine.equals("bing"))
	    {
	    	
	    	int j=0;
	    	BasicDBList codesList = (BasicDBList) object.get("code");
	    	//System.out.println(codesList);
	    	Iterator<Object> iterator = codesList.iterator();
	    	ArrayList<Double> lcsCodes = new ArrayList<Double>();
   			while (iterator.hasNext()) 
   			{
   				Object o = iterator.next();
   				//System.out.println(++j+". "+o);
   				double s = l.lcs(codeFromPlugin,(String) o);
   				lcsCodes.add(s/codeFromPlugin.length());
   				
   			}
   			//System.out.println(codes);
   			double maxLcsCodes;
   			//System.out.println("lcs: "+lcsCodes);
   			if(lcsCodes.size()>1)
   			{
   				maxLcsCodes = Collections.max(lcsCodes);
   			}
   			
   			else
   				maxLcsCodes = 0;
   			//System.out.println(maxLcsCodes);
   			//System.out.println();
   			scoreFinal = maxLcsCodes;
			}
	    
		
	    if(searchEngine.equals("stackoverflow"))
	    {
	    	
	    	BasicDBList answersList = (BasicDBList) object.get("answers");
		    if(answersList!=null)
		    {
		    	for (int i = 0; i < answersList.size(); i++) 
		    	{
		    		BasicDBObject answerObj = (BasicDBObject) answersList.get(i);
		    		if(answerObj != null)
		    		{
		    			String answer = answerObj.getString("answer");
		    			String score = answerObj.getString("score");
		    			String code = answerObj.getString("code");
		    		}
		    	}
		    }
	    	
	    	
	    	double maxLcs = 0;
	    	BasicDBList codeInQuestion = (BasicDBList) object.get("code_in_question");
		    BasicDBList codesInAnswers = null;
		    if(answersList!=null)
		    {
		    	//System.out.println(answersList.size());
		    	for (int i = 0; i < answersList.size(); i++) 
		    	{
		    		BasicDBObject answerObj = (BasicDBObject) answersList.get(i);
		    		if(answerObj != null)
		    		{
		    			codesInAnswers = (BasicDBList) answerObj.get("code");
		        
		    		}
		    	}
		    	
		    	int j = 0;
		    	// System.out.println("stack");
		    	// System.out.println(codesInAnswers);
		    	Iterator<Object> iterator = codesInAnswers.iterator();
		    	ArrayList<Double> lcsCodes = new ArrayList<Double>();
		    	double s;
		    	
		    	while (iterator.hasNext()) 
		    	{
		    		Object o = iterator.next();
		    		//System.out.println(++j+". "+o);
		    		s = Lcs.lcs(codeFromPlugin,(String) o);
		    		lcsCodes.add(s/codeFromPlugin.length());
   				
		    	}
		    	
		    	double maxLcsCodes;
		    	//System.out.println("lcs: "+lcsCodes);
		    	if(lcsCodes.size()>1)
		    	{
		    		maxLcsCodes = Collections.max(lcsCodes);
		    	}
		    	
		    	else
		    		maxLcsCodes = 0;
   			
		    	//System.out.println("MAx lcs in ans codes: "+maxLcsCodes);
   			
		    	j = 0;
		    	iterator = codeInQuestion.iterator();
		    	ArrayList<Double> lcsCodesQues = new ArrayList<Double>();
		    	while (iterator.hasNext()) 
		    	{
		    		Object o = iterator.next();
		    		//System.out.println(++j+". "+o);
		    		s = Lcs.lcs(codeFromPlugin,(String) o);
		    		lcsCodesQues.add(s/codeFromPlugin.length());
   				
		    	}
		    	
		    	double maxLcsCodesQues;
		    	//System.out.println("lcs: "+lcsCodesQues);
		    	if(lcsCodesQues.size()>1)
		    	{
		    		maxLcsCodesQues = Collections.max(lcsCodesQues);
		    	}
		    	
		    	else
		    		maxLcsCodesQues = 0;
   			
		    	//System.out.println("MAx lcs in ques codes: "+maxLcsCodesQues);
   	
		    	if(maxLcsCodes < maxLcsCodesQues)
		    	{
		    		maxLcs = maxLcsCodesQues;
		    	}
		    	else
		    		maxLcs = maxLcsCodes;
		    	//System.out.println("MAx lcs in ques/ans codes: "+maxLcs);
		    	//System.out.println();
		    	System.out.println("maxx"+maxLcs+" "+maxLcsCodes+" "+maxLcsCodesQues);
		    }
		    
		    scoreFinal = maxLcs;  
		    
		    
	    }
	    
	    System.out.println("sourceCodeContextMatchingScore: "+scoreFinal);
	    
		return scoreFinal;
	}
	
	
	
	
	static double overallScoreForLink(DBObject object, String queryFromPlugin, String codeFromPlugin,Result result)
	{
		double overallScoreForLink = 0;
		double titleToTitleSimilarityScore = 0;
		double titleToContextSimilarityScore = 0;
		double titleToDescriptionSimilarityScore = 0;
		double stackoverflowVoteScore = 0;
		double sourceCodeContextMatchingScore = 0;
		
		if(object!=null)
		{
    	    String link = ((BasicBSONObject) object).getString("link");
    	    String title = ((BasicBSONObject) object).getString("title");
    	    String searchEngine = ((BasicBSONObject) object).getString("search_engine");
    	    
    	    //ttt
    	    titleToContextSimilarityScore=titleToTitleSimilarity(object, queryFromPlugin); 
    	    //cosine similarity between queryFromPlugin and title
    	    //titleToTitleSimilarityScore = cosine(queryFromPlugin, title);

    	    
    	    //ttc
    	    titleToContextSimilarityScore=titleToContextSimilarity(object, queryFromPlugin);
    	    //cosine similarity between queryFromPlugin and context
    	    //titleToContextSimilarityScore = cosine(queryFromPlugin, context);
		    
    	   
    	   
		    //ttd
		    titleToDescriptionSimilarityScore=titleToDescriptionSimilarity(object, queryFromPlugin);
		    //cosine similarity between queryFromPlugin and description
		    //titleToDescriptionSimilarityScore = cosine(queryFromPlugin, description);
    	    
		    
		    
    	    //stackoverflow vote score
    	    stackoverflowVoteScore=stackoverflowVoteScore(object, queryFromPlugin);
    	    
    	    
    	    //sourceCodeContextMatchingScore
    	    sourceCodeContextMatchingScore=sourceCodeContextMatchingScore(object, queryFromPlugin, codeFromPlugin);
    	    
    	    
    	    double contentScore=0.5*titleToTitleSimilarityScore + 0.15*titleToContextSimilarityScore + 0.35*titleToDescriptionSimilarityScore;
    	    
    	    
    	    
    	    //check here
    	    overallScoreForLink = (0.35*contentScore) + (0.10*stackoverflowVoteScore) + (0.55*sourceCodeContextMatchingScore);
		
    	    result.content_score=contentScore;
    	    result.context_score=sourceCodeContextMatchingScore;
    	    result.stackOverflowVoteScore=stackoverflowVoteScore;
    	    result.title=title;
    	    result.resultURL=link;
    	    result.totalScore=overallScoreForLink;
    	    
    	    result.titleToTitleScore=titleToTitleSimilarityScore;
    	    result.titleToDescriptionScore=titleToDescriptionSimilarityScore;
    	    result.titleToContextScore=titleToContextSimilarityScore;
    	    result.searchEngine=searchEngine;
    	    
    	    
    	    
    	    
    	    System.out.println("Final Score: "+overallScoreForLink);
		}
		
		return overallScoreForLink;
	}
	
	
	static void call_search_engine(String searchengine,String query)
	{
		try
        {
       	 String title = query;
       	 System.out.println(title);
       	 
       	 String[] command = new String[3];
       	 if(searchengine == "google")
       	 {
       		 command[0]="/usr/local/bin/python3";
       		 command[1]="google_search.py"; //path to the script
       		 command[2]=title; //argument/option
       	 }
       	 else if(searchengine == "stackoverflow")
       	 {
       		 command[0]="/usr/local/bin/python";
       		 command[1]="stack_search.py"; //path to the script
       		 command[2]=title; //argument/option
       	 }
       	 else if(searchengine == "bing")
      	 {
      		 command[0]="/usr/local/bin/python3";
      		 command[1]="bing_search.py"; //path to the script
      		 command[2]=title; //argument/option
      	 }
       	 for(int k = 0;k<command.length ; k++)
       	 System.out.print(command[k]+" ");
       	 
       	 System.out.println();
       	ProcessBuilder pb = new ProcessBuilder(command);
       	pb.redirectOutput(Redirect.INHERIT);
       	pb.redirectError(Redirect.INHERIT);
       	Process p = pb.start();
            Runtime r = Runtime.getRuntime();
            System.out.println("1");
            
            //Process p = r.exec(command);
            System.out.println("2");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            BufferedReader br1 = new BufferedReader(new InputStreamReader(p.getInputStream()));
            p.waitFor();
            String line = "";
            while (br.ready())
            {
            line = br.readLine();
       	 if(line!=null)
            System.out.println(line);
            }
            String line1 = "";
            while (br1.ready())
            {
            line1 = br1.readLine();
       	 if(line1!=null)
            System.out.println(line1);
            }

            

        }
        catch (Exception e)
        {
		String cause = e.getMessage();
		if (cause.equals("python: not found"))
			System.out.println("No python interpreter found.");
        }
	}
	
	
	
	
	
	
	public static Result[] getResults(String searchQuery,String sourceCode)
	{
		ArrayList<Result> resultList=new ArrayList<Result>();
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        DB db = mongoClient.getDB( "test" );
        System.out.println("Connect to database successfully");
        DBCollection collection = db.getCollection("data");
        
        DBObject dbQuery = new BasicDBObject("query",searchQuery);
        DBCursor cursor = collection.find(dbQuery);
   	 	int queryAlreadyInDb = 0;
   	 	while(cursor.hasNext()) 
   	 	{
   	 		BasicDBObject object = (BasicDBObject) cursor.next();
   	 		String t = object.getString("query");
   	 		if(t.equals(searchQuery))
   	 		{
   	 			queryAlreadyInDb = 1;
   	 			break;
   	 		}
   	 	}
   	 	
   	 	if(queryAlreadyInDb == 0)
   	 	{
   	 		Metrics.title=searchQuery;
   	 		
   	 		Metrics t=new Metrics();
   	 		t.title=searchQuery;
   	 		
   	 		
   	 	URL url = Metrics.class.getProtectionDomain().getCodeSource().getLocation();
  	 	 System.out.println("URL"+url.getPath());
  	 	
  	 	 Metrics.path=url.getPath();
  	 	 t.path=url.getPath();
   	 		
  	 	 
   	 		t.stack.start();
   	 		t.google.start();
   	 		
   	 		
   	 	 
   	 	 
   	 		try {
				t.google.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
   	 		t.bing.start();
   	 		
   	 		try {
				t.bing.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   	 		
		  
   	 	}
	    cursor = collection.find(dbQuery);
	    
	    
	    
	    int s = 0;
	     while(cursor.hasNext()) {
	    	 Result temp=new Result();
	    	s++;
	    	System.out.println(s);
	    	DBObject theObj = cursor.next();
	    	System.out.println(overallScoreForLink(theObj, searchQuery, sourceCode,temp));
	    	//break;
	    	System.out.println("result"+temp.resultURL);
        resultList.add(temp);
	    }
        
	     
	     Collections.sort(resultList,new Comparator<Result>() 
			{ 
				public int compare(Result s1, Result s2)
					{ 
					  return ((Double)s2.totalScore).compareTo((Double)s1.totalScore);
					}	

			});	
	     
	     
	     System.out.println("sizeeee------------"+resultList.size());
	     ArrayList<Result> resultSub=null;
	     if(resultList.size() < 30)
	     resultSub = new ArrayList<Result>(resultList.subList(0, resultList.size()));
	     else
	    	 resultSub = new ArrayList<Result>(resultList.subList(0, 30));
	     
	     Result[] results = resultSub.toArray(new Result[0]);
	     
	     
	     System.out.println("000000000000000011111"+resultList.get(0).resultURL);
	     
	     System.out.println(" 00000"+results[0].resultURL);
	     
	     return results;
	     
	}
	
	
	
	public static HashMap<String,Double> createMap(String line)
	{
		
		HashMap<String,Double> tempMap=new HashMap<String,Double>();
		String[] tokenizedTerms = line.replaceAll("\\."," ").replaceAll(System.getProperty("line.separator")," ").replaceAll("-"," ").replaceAll("[\\W&&[^\\s]]", "").replaceAll("[0-9]", "").replaceAll("\\s{2,}", " ").trim().toLowerCase().split("\\W+");
		
		for (String term : tokenizedTerms) 
		{
			//SnowballStemmer snowballStemmer = new englishStemmer();
            //snowballStemmer.setCurrent(term);
            //snowballStemmer.stem();
            //term =  snowballStemmer.getCurrent();
    
			 if(tempMap.get(term)==null)
				 tempMap.put(term, 1.0);
			 else
				 tempMap.put(term, tempMap.get(term)+1);
		}
		
		
		return tempMap;
	}
	
	
	
	
	Thread stack = new Thread()
	{
	     public void run()
	     {
	    	 String title = Metrics.title;
	      	 String[] command = new String[3];
	      	 command[0]="/usr/local/bin/python";
	      	 command[1]=Metrics.path+"stack_search.py"; //path to the script
	      	 command[2]=title; 
	      	 
	      	Runtime r = Runtime.getRuntime();
	           System.out.println("1");
	           ProcessBuilder pb = new ProcessBuilder(command);
	           pb.redirectOutput(Redirect.INHERIT);
	           pb.redirectError(Redirect.INHERIT);
	           
	           
	           Process p = null;
			try {
				p = pb.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	           System.out.println("2");
	           try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     
	     }
	};
	
	
	
	
	Thread bing = new Thread()
	{
	     public void run()
	     {
	        // put whatever code you want to run inside the thread here.
	    
	    	 String title = Metrics.title;
	      	 String[] command = new String[3];
	      	 command[0]="/usr/local/bin/python3";
	      	
	      	 command[1]=Metrics.path+"bing_search.py"; //path to the script
	      	 command[2]=title; 
	      	 
	      	Runtime r = Runtime.getRuntime();
	           System.out.println("1");
	           
	           ProcessBuilder pb = new ProcessBuilder(command);
	           pb.redirectOutput(Redirect.INHERIT);
	           pb.redirectError(Redirect.INHERIT);
	           
	           Process p = null;
			try {
				p = pb.start();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	           
	           
	           System.out.println("2");
	           try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     
	     }
	};
	
	
	
	Thread google = new Thread()
	{
	     public void run()
	     {
	        // put whatever code you want to run inside the thread here.
	     
	    	 String title = Metrics.title;
	      	 String[] command = new String[3];
	      	 command[0]="/usr/local/bin/python3";
	      	 System.out.println(Metrics.path+"google_search.py");
	      	 command[1]=Metrics.path+"google_search.py"; //path to the script
	      	 command[2]=title; 
	      	 
	      	Runtime r = Runtime.getRuntime();
	           System.out.println("1");
	           
	           ProcessBuilder pb = new ProcessBuilder(command);
	           pb.redirectOutput(Redirect.INHERIT);
	           pb.redirectError(Redirect.INHERIT);
	           
	           Process p = null;
				try {
					p = pb.start();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		           
		           
		           System.out.println("2");
		           try {
					p.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	     
	     }
	};
	
	
	
	
	
	public static void main( String args[] )throws ScriptException, IOException {
		
	         String queryFromPlugin = "null pointer exception";
	         String codeFromQuery = "if(null pointer exception)";
	    	 System.out.println(queryFromPlugin);
   
	}
	
}
