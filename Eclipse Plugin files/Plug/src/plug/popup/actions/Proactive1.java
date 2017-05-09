package plug.popup.actions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.jdt.internal.debug.ui.console.JavaConsoleTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.*;

public class Proactive1 extends JavaConsoleTracker implements IPatternMatchListenerDelegate {

	String searchQuery="";
	
	
	@Override
	public void connect(TextConsole arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void matchFound(PatternMatchEvent event) {
		// TODO Auto-generated method stub
		
		System.out.println("match found");
		System.out.println(event.getSource().toString());
		
		ProcessConsole myConsole=(ProcessConsole)event.getSource();
		
		try {
			System.out.println(myConsole.getDocument().get(event.getOffset(),event.getLength()));
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
		
		String directoryName=ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		ArrayList<String> sourceCode=new ArrayList<String>();
		try {
			listFilesAndFilesSubDirectories(directoryName,"Test.java",sourceCode,10);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void listFilesAndFilesSubDirectories(String directoryName,String filename,ArrayList<String> sourceCode,int linenumber) throws IOException{
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
        	//System.out.println("hre");
            if (file.isFile() && file.getName().equals(filename)){
            	//System.out.println("hre");
                //files.add(file);
                FileInputStream fs= new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fs));
                for(int i = 0; i < linenumber; ++i)
                  br.readLine();
                String lineIWant = br.readLine();
                System.out.println(lineIWant.trim());
                sourceCode.add(lineIWant);
                
            } else if (file.isDirectory()){
            	//System.out.println("hre");
                listFilesAndFilesSubDirectories(file.getAbsolutePath(),filename,sourceCode,linenumber);
            }
        }
    }
	
	
	
}
