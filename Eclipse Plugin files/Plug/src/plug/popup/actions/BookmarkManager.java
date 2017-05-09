package plug.popup.actions;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class BookmarkManager {

	static String bookmarkFileName = System.getProperty("user.home") + "/surfeclipse/bookmark.json";
	  
	  public static void createBookMarkFile()
	  {
	    try
	    {
	      File f = new File(bookmarkFileName);
	      if (!f.exists())
	      {
	    	  System.out.println("inside if");
	        f.createNewFile();
	        try
	        {
	        	System.out.println("inside try");
	          JSONArray array = new JSONArray();
	          JSONObject jobject = new JSONObject();
	          jobject.put("title", "Google");
	          jobject.put("url", "http://www.google.ca");
	          jobject.put("date", new Date().toString());
	          array.add(jobject);
	          FileWriter writer = new FileWriter(f);
	          array.writeJSONString(writer);
	          writer.close();
	          System.out.println("Bookmark file created successfully.");
	        }
	        catch (Exception exc)
	        {
	          exc.printStackTrace();
	        }
	      }
	      return;
	    }
	    catch (Exception localException1) {}
	  }
	  
	  static String getFileContent()
	  {
	    String content = new String();
	    try
	    {
	      File f = new File(bookmarkFileName);
	      if (!f.exists())
	      {
	    	  BookmarkManager.createBookMarkFile();
	    	  System.out.println("file not created");
	    	  //System.out.println(getFileContent());
	      }
	      Scanner scanner = new Scanner(f);
	      while (scanner.hasNext())
	      {
	    	  System.out.println("inside scanner");
	        String line = scanner.nextLine();
	        content = content + line;
	        System.out.println("******"+content);
	      }
	    }
	    catch (Exception localException) {}
	    return content;
	  }
	  
	  public static void addBookMark(String title, String resultURL)
	  {
	    try
	    {
	      String jsonStr = getFileContent();
	      System.out.println("jsonStr"+jsonStr);
	      JSONParser parser = new JSONParser();
	      JSONArray array = (JSONArray)parser.parse(jsonStr);
	      JSONObject bookmakrObj = new JSONObject();
	      bookmakrObj.put("title", title);
	      bookmakrObj.put("url", resultURL);
	      bookmakrObj.put("date", new Date().toString());
	      array.add(bookmakrObj);
	      FileWriter writer = new FileWriter(bookmarkFileName);
	      array.writeJSONString(writer);
	      writer.close();
	      System.out.println("*****File"+getFileContent());
	    }
	    catch (Exception exc)
	    {
	      exc.printStackTrace();
	    }
	  }
	  
	  public static HashMap<String, String> loadBookMarks()
	  {
		  createBookMarkFile();
	    HashMap<String, String> bookmarks = new HashMap();
	    try
	    {
	      String filecontent = getFileContent();
	      JSONParser parser = new JSONParser();
	      JSONArray array = (JSONArray)parser.parse(filecontent);
	      for (int i = 0; i < array.size(); i++)
	      {
	        JSONObject jobj = (JSONObject)array.get(i);
	        String title = jobj.get("title").toString();
	        String url = jobj.get("url").toString();
	        if (!bookmarks.containsKey(title)) {
	          bookmarks.put(title, url);
	        }
	      }
	    }
	    catch (Exception localException) {}
	    return bookmarks;
	  }
}
