package plug.popup.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;

public class Test {

	public static void main(String[] args) throws InterruptedException
	{
		Test t=new Test();
		t.t.start();
		t.t2.start();
		t.t2.join();
		
		t.t1.start();
		
	}
	public static void main1(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		String title = "Null pointer exception";
      	 String[] command = new String[3];
      	 command[0]="/usr/local/bin/python";
      	 command[1]="stack_search.py"; //path to the script
      	 command[2]=title; //argument/option
      	 
      	 
      	 String cmd = "python/";
           Runtime r = Runtime.getRuntime();
           System.out.println("1");
           
           
           ProcessBuilder pb = new ProcessBuilder(command);
           pb.redirectOutput(Redirect.INHERIT);
           pb.redirectError(Redirect.INHERIT);
           
           Process p =pb.start();
           //Process p = r.exec(command);
           System.out.println("2");
           p.waitFor();
           BufferedReader br1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
           BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
           /*
           BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
           BufferedReader br1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
           p.waitFor();
           String line = "";
           
           while (br.ready())
           {
           line = br.readLine();
      	 if(line!=null)
           System.out.println(line);
           }

           
           while (br1.ready())
           {
           line = br1.readLine();
      	 if(line!=null)
           System.out.println(line);
           }
           */

 String line = "";
           
           while (br.ready())
           {
           line = br.readLine();
      	 if(line!=null)
           System.out.println(line);
           }
           
           
          // String line=null;
           while (br1.ready())
           {
           line = br1.readLine();
      	 if(line!=null)
           System.out.println(line);
           }
           System.out.println("here");
          // p.waitFor();
           System.out.println("done");
       }

	Thread t = new Thread()
	{
	     public void run()
	     {
	        // put whatever code you want to run inside the thread here.
	    	 String title = "String index out of bound";
	      	 String[] command = new String[3];
	      	 command[0]="/usr/local/bin/python";
	      	 command[1]="stack_search.py"; //path to the script
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
	
	Thread t1 = new Thread()
	{
	     public void run()
	     {
	        // put whatever code you want to run inside the thread here.
	    
	    	 String title = "String index out of bound";
	      	 String[] command = new String[3];
	      	 command[0]="/usr/local/bin/python3";
	      	 command[1]="bing_search.py"; //path to the script
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
	
	Thread t2 = new Thread()
	{
	     public void run()
	     {
	        // put whatever code you want to run inside the thread here.
	     
	    	 String title = "String index out of bound";
	      	 String[] command = new String[3];
	      	 command[0]="/usr/local/bin/python3";
	      	 command[1]="google_search.py"; //path to the script
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
	

}
