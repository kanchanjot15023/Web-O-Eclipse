package plug.popup.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.jdt.internal.debug.ui.console.JavaConsoleTracker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.swt.widgets.Text;

public class SearchExecution extends ActionDelegate
implements IViewActionDelegate,IPatternMatchListenerDelegate{

	private Shell shell;
	Result[] collectedResults;
	String stackTrace="";
	String searchQuery="";
	String sourceCode="";
	String errorMessage="";
	static IWorkbenchPage page1;
	/**
	 * Constructor for Action1.
	 */
	public SearchExecution() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
		page1= targetPart.getSite().getPage();
		System.out.println("gooottttt"+page1);
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		
		String executionPath = System.getProperty("user.dir");
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelectionService service = window.getSelectionService();
		ISelection selection=service.getSelection();
		ITextSelection  text= (ITextSelection) selection;
		
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		 IConsoleManager conMan = plugin.getConsoleManager();
		 IConsole[] existing = conMan.getConsoles();
		 for (int i = 0; i < existing.length; i++)
		 {
			 ProcessConsole myConsole=(ProcessConsole) existing[i];
			 //System.out.println(myConsole.getName());
		 if(myConsole!=null){
			 IDocument doc = myConsole.getDocument();
			 //String regex2 = ".+Exception[^\n]++(\\s+at .+)+";
			 //String regex1=".+Exception[^\\n]+\\n(\\t+\\Qat \\E.+\\s+)+";
			 //String regex = ".+Exception[^\\n]+\\n(\\s+at .+)+";
			 //String regexx=".+Exception[^\\n]+(:.*)?\\n(\\s+at .++)+";
			 String regg="(.+Exception(:)?(.+)*)+\\n(\\s*at .+)+";
			 //String regextry="(\\d+\\) .+)|(.+Exception(:)?(.+)*)|(\\s*at .+)|(\\s+... \\d+ more)|(\\s*Caused by:.+)";
			 Pattern pattern = Pattern.compile(regg);
			 Matcher matcher = pattern.matcher(doc.get());
			
			 
			 System.out.println("here");
			 
			 if (matcher.find())
			 {
				 stackTrace=doc.get().substring(matcher.start(), matcher.end());
				 System.out.println("matcher");
				 System.out.println(doc.get().substring(matcher.start(), matcher.end()));
			     System.out.println("-----");
				 System.out.println(matcher.group(0));
				 System.out.println("-----");
			     System.out.println(matcher.group(1));
			 }
			 
			 else 
			 {
				 System.out.println("didnt match");
			 }
					
		 }

		
		 }
		
		//Map<Thread, StackTraceElement[]> ele=Thread.getAllStackTraces();
		//for(Thread str: ele.keySet())
			//for(StackTraceElement s:ele.get(str))
				//System.out.println(s);
		
		String selectedText= text.getText();
		
		MessageBox box = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		box.setMessage("Executing: " + text.getText());
		//box.open();
		
		searchQuery=selectedText;
		
		sourceCode=extract_source_code_context(this.stackTrace,0);
		
		System.out.println("context"+sourceCode);
		
		//trying dummy results
		Result[] dummy=new Result[6];
		dummy[0]=new Result();
		dummy[0].resultURL="http://stackoverflow.com/questions/218384/what-is-a-nullpointerexception-and-how-do-i-fix-it";
		dummy[0].title="Stack";
		
		dummy[1]=new Result();
		dummy[1].resultURL="https://docs.oracle.com/javase/7/docs/api/java/lang/NullPointerException.html";
		dummy[1].title="Google";
		
		dummy[2]=new Result();
		dummy[2].resultURL="http://stackoverflow.com/questions/218384/what-is-a-nullpointerexception-and-how-do-i-fix-it";
		dummy[2].title="Stack";
		
		dummy[3]=new Result();
		dummy[3].resultURL="https://docs.oracle.com/javase/7/docs/api/java/lang/NullPointerException.html";
		dummy[3].title="Google";
		
		dummy[4]=new Result();
		dummy[4].resultURL="http://stackoverflow.com/questions/218384/what-is-a-nullpointerexception-and-how-do-i-fix-it";
		dummy[4].title="Stack";
		
		dummy[5]=new Result();
		dummy[5].resultURL="https://docs.oracle.com/javase/7/docs/api/java/lang/NullPointerException.html";
		dummy[5].title="Google";
		
		
		
		//start the thread to display
		new Thread()
        {
          public void run()
          {
        	  //SearchExecution.this.collectedResults=dummy;
        	 
        	  SearchExecution.this.collectedResults=Metrics.getResults( SearchExecution.this.searchQuery,  SearchExecution.this.sourceCode);
           
        	  System.out.println(SearchExecution.this.collectedResults.length);
        	  //SearchEventManager.this.collectedResults = SearchEventManager.this.collect_search_results();
            Display.getDefault().asyncExec(new Runnable()
            {
              public void run()
              {
                try
                {
                  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("ca.usask.ca.srlab.surfclipse.client.views.SurfClipseClientView");
                  SearchExecution.this.update_surfclipse_view();
                }
                catch (Exception exc)
                {
                  exc.printStackTrace();
                }
              }
            });
          }
        }.start();
		
        
        
		
		/*
		SurfEclipseConsole.getConsole().activate();
		try {
			SurfEclipseConsole.getConsole().newOutputStream().write("Hello world.".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
		//MessageConsole myConsole = findConsole("SurfClipse");
		  // MessageConsoleStream out = myConsole.newMessageStream();
		   //out.println("Hello from Generic console sample action");
		   
		//SurfEclipseConsole myConsole=openConsole();
		//IOConsoleOutputStream stream = myConsole.newOutputStream();
		//try {
			//stream.write("hello form new consoel");
		//} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}
		
		
		  // myConsole.activate();
		   /*
		   IConsoleView view = null;
		   view = (IConsoleView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IConsoleConstants.ID_CONSOLE_VIEW);
		   view.display(myConsole);
		   IViewPart vp =(IViewPart)view;
	        if (vp instanceof PageBookView) {
	            IPage page = ((PageBookView) vp).getCurrentPage();
	            viewer = ResourceUtils.getViewer(page);
	        }
		   */
		   /*
		   IConsole myConsole1 = myConsole;
		   IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		   String id = IConsoleConstants.ID_CONSOLE_VIEW;
		   IConsoleView view = null;
		try {
			view = (IConsoleView) page.showView(id);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   view.display(myConsole);
		*/
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void init(IViewPart view) {
		// TODO Auto-generated method stub
		
		if(page1==null)
		{
			System.out.println(page1);
		SearchExecution.this.page1=view.getSite().getPage();
		System.out.println("initally"+SearchExecution.this.page1+" "+page1);
		}
		else
		{
			System.out.println("not");
		}
		view.getSite().getPage().addPartListener(new IPartListener() {
			
			@Override
			public void partOpened(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void partDeactivated(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void partClosed(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void partActivated(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				//page1= part.getSite().getPage();
				System.out.println("gooottttt Activated"+page1);
			}
		});
				
	}

	private MessageConsole findConsole(String name) {
	      ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();
	      IConsole[] existing = conMan.getConsoles();
	      for (int i = 0; i < existing.length; i++)
	         if (name.equals(existing[i].getName()))
	            return (MessageConsole) existing[i];
	      //no console found, so create a new one
	      MessageConsole myConsole = new MessageConsole(name, null);
	      conMan.addConsoles(new IConsole[]{myConsole});
	      conMan.showConsoleView(myConsole);
	      return myConsole;
	   }

	
	
	protected void update_surfclipse_view()
	  {
	    try
	    {
	      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
	        .getActivePage();
	      String viewID = "ca.usask.ca.srlab.surfclipse.client.views.SurfClipseClientView";
	      IViewPart vpart = page.findView(viewID);
	      SurfEclipseConsole myview = (SurfEclipseConsole)vpart;
	      
	      
	      ViewContentProvider viewContentProvider = new ViewContentProvider(this.collectedResults);
	      myview.viewer.setContentProvider(viewContentProvider);
	     // myview.input.setText(this.searchQuery);
	      //myview.input.setText("Null Pointer Exception");
	    }
	    catch (Exception exc)
	    {
	      exc.printStackTrace();
	      String message = "Failed to collect search results. Please try again.";
	      //showMessageBox(message);
	    }
	  }
	
	
	public String extract_source_code_context(String extractedStack,int status)
	  {
	    String sourceContext = new String();
	    String[] stackElements = extractedStack.split("\n");
	    
	    String firstTraceLine = new String();
	    for (int i = stackElements.length - 1; i > 0; i--)
	    {
	      String elem = stackElements[i];
	      if (elem.contains(".java"))
	      {
	        firstTraceLine = elem;
	        System.out.println("Trace of interest:" + elem);
	        break;
	      }
	    }
	    int open_brac = firstTraceLine.lastIndexOf('(');
	    int close_brac = firstTraceLine.lastIndexOf(')');
	    String fileNameLine = firstTraceLine.substring(open_brac + 1, close_brac);
	    String[] parts = fileNameLine.split(":");
	    String javaFileName = parts[0].trim();
	    int lineNumber = Integer.parseInt(parts[1].trim());
	    System.out.println("file name"+javaFileName+" line number"+lineNumber);
	    try
	    {
	    
	    	
	    	System.out.println("before sending"+page1);
	      sourceContext = extract_the_context(javaFileName, lineNumber,status);
	    }
	    catch (Exception localException) {}
	    return sourceContext;
	  }
	
	
	
	 protected void open_target_project_file(final String fileName, final int lineNumber)
	  {
		 System.out.println("inside function"+page1);
		 
	    IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    IWorkspaceRoot root = workspace.getRoot();
	    try
	    {
	      root.accept(new IResourceVisitor() 
	    		  {
	        public boolean visit(IResource resource)
	          throws CoreException
	        {
	        	//System.out.println("()()()("+page1);
	          if (resource.getType() == 1) {
	            if (resource.getName().endsWith(fileName))
	            {
	              IFile fileToBeOpened = (IFile)resource;
	              IWorkbenchPage page = null;
	              IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	              System.out.println("window..."+ page);
	              System.out.println("page1...."+ SearchExecution.this.page1);
	            		  //getActivePage();
	              if(window==null)
	              {
	            	  page=page1;
	              }
	              else
	            	  page=window.getActivePage();
	              
	              final IWorkbenchPage pageFinal=page;
	              
	              System.out.println("page"+ page);
	              System.out.println("page Final"+ pageFinal);
	              
	              HashMap<String,Integer> map = new HashMap<String,Integer>();
	              map.put("lineNumber", new Integer(lineNumber));
	              
	              IMarker marker = fileToBeOpened.createMarker("org.eclipse.core.resources.textmarker");
	              marker.setAttributes(map);
	              if(window==null)
	              {
	            
	              }
	              else
	              IDE.openEditor(page, fileToBeOpened);
	              
	              return false;
	            }
	          }
	          return true;
	        }
	      });
	    }
	    catch (Exception exc)
	    {
	      exc.printStackTrace();
	    }
	  }
	 
	 
	  
	  protected String extract_the_context(String fileName, int lineNumber,int status)
	  {
	    open_target_project_file(fileName, lineNumber);
	    
		 
	    
	    String context = new String();
	    
	    System.out.println("extracted the code");
	    int block_depth = 3;
	    try
	    {
	    	IWorkbenchPage page =null;
	    	if(status==1)
	    		page=page1;
	    	else
	     page =  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	      System.out.println("pagee extract............###"+page);
	      ITextEditor editor = (ITextEditor)page.getActiveEditor();
	      System.out.println(editor);
	      IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
	      String fileContent = doc.get();
	      System.out.println(fileContent);
	      String[] lines = fileContent.split("\n");
	      
	      int start_line = lineNumber > block_depth ? lineNumber - 
	        block_depth : 0;
	      int end_line = lineNumber < lines.length - block_depth ? lines.length - block_depth : 
	        lines.length;
	      System.out.println("start"+start_line+" end"+end_line+" "+lines.length);
	      for (int i = start_line; i < end_line; i++) {
	        context = context + lines[i] + "\n";
	      }
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return context;
	  }

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
	
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(InvocationProactive.status==1)
		{
		System.out.println("match found");
		System.out.println("pagee1111"+SearchExecution.this.page1);
		
		ProcessConsole myConsole=(ProcessConsole)event.getSource();
		
		try {
			stackTrace=myConsole.getDocument().get(event.getOffset(),event.getLength());
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		sourceCode=extract_source_code_context(this.stackTrace,1);
		System.out.println("context"+sourceCode);
		
		searchQuery=extract_exception_name();
		System.out.println("searchQuery "+searchQuery);
		
		
		
		//trying dummy results
				Result[] dummy=new Result[6];
				dummy[0]=new Result();
				dummy[0].resultURL="http://stackoverflow.com/questions/218384/what-is-a-nullpointerexception-and-how-do-i-fix-it";
				dummy[0].title="Stack";
				
				dummy[1]=new Result();
				dummy[1].resultURL="https://docs.oracle.com/javase/7/docs/api/java/lang/NullPointerException.html";
				dummy[1].title="Google";
				
				dummy[2]=new Result();
				dummy[2].resultURL="http://stackoverflow.com/questions/218384/what-is-a-nullpointerexception-and-how-do-i-fix-it";
				dummy[2].title="Stack";
				
				dummy[3]=new Result();
				dummy[3].resultURL="https://docs.oracle.com/javase/7/docs/api/java/lang/NullPointerException.html";
				dummy[3].title="Google";
				
				dummy[4]=new Result();
				dummy[4].resultURL="http://stackoverflow.com/questions/218384/what-is-a-nullpointerexception-and-how-do-i-fix-it";
				dummy[4].title="Stack";
				
				dummy[5]=new Result();
				dummy[5].resultURL="https://docs.oracle.com/javase/7/docs/api/java/lang/NullPointerException.html";
				dummy[5].title="Google";
				
				
				//start the thread to display
				new Thread()
		        {
		          public void run()
		          {
		        	  //SearchExecution.this.collectedResults=dummy;
		        	SearchExecution.this.collectedResults=Metrics.getResults( SearchExecution.this.searchQuery,  SearchExecution.this.sourceCode);
		        	  
		            //SearchEventManager.this.collectedResults = SearchEventManager.this.collect_search_results();
		            Display.getDefault().asyncExec(new Runnable()
		            {
		              public void run()
		              {
		                try
		                {
		                  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("ca.usask.ca.srlab.surfclipse.client.views.SurfClipseClientView");
		                  SearchExecution.this.update_surfclipse_view();
		                }
		                catch (Exception exc)
		                {
		                  exc.printStackTrace();
		                }
		              }
		            });
		          }
		        }.start();
				
		
		}
		
		else
		{
			System.out.println("+++++++++++++++ else");
		}
		//System.out.println(extract_exception_name());
	}
	
	
	
	
	public String extract_exception_name()
	  {
	    String errorMessage = get_error_message();
	    String exceptionName = new String();
	    try
	    {
	      String exceptionNameRegex = "^.+Exception(:)?";
	      Pattern pattern = Pattern.compile(exceptionNameRegex);
	      Matcher matcher = pattern.matcher(errorMessage);
	      if (matcher.find())
	      {
	        int start = matcher.start();
	        int end = matcher.end();
	        String tempStr = errorMessage.substring(start, end);
	        String[] parts = tempStr.split("\\s+");
	        if (parts.length > 1) {
	          exceptionName = parts[(parts.length - 1)];
	        } else {
	          exceptionName = tempStr;
	        }
	      }
	    }
	    catch (Exception localException) {}
	    return exceptionName;
	  }
	
	
	
	
	public String get_error_message()
	  {
	    String[] lines = this.stackTrace.split("\n");
	    String temp = new String();
	    String[] arrayOfString1;
	    int j = (arrayOfString1 = lines).length;
	    for (int i = 0; i < j; i++)
	    {
	      String line = arrayOfString1[i];
	      if ((!line.startsWith("!")) && (!line.startsWith("at")))
	      {
	        temp = line;
	        break;
	      }
	    }
	    if (RegexMatcher.matches_exception_name(temp))
	    {
	      String filepathRegex = "([a-zA-Z]:)?(\\\\[a-zA-Z0-9\\s\\._-]+)+\\\\?";
	      try
	      {
	        Pattern p = Pattern.compile(filepathRegex);
	        Matcher matcher = p.matcher(temp);
	        if (matcher.find())
	        {
	          String filePath = temp.substring(matcher.start(), matcher.end());
	          String newtemp = temp.replace(filePath, " ");
	          this.errorMessage = newtemp;
	          System.out.println(newtemp);
	        }
	        else
	        {
	          this.errorMessage = temp;
	        }
	        this.errorMessage = this.errorMessage.replaceAll(":", " ");
	      }
	      catch (Exception localException) {}
	    }
	    return this.errorMessage;
	  }
	
	
	
	
	 protected String extract_error_message_from_stack(String extractedStack)
	  {
	    String[] stackElements = extractedStack.split("\n");
	    String errorMessage = new String();
	    errorMessage = stackElements[0];
	    return errorMessage;
	  }
	
	
	/*
	
	public SurfEclipseConsole openConsole() {

		IConsoleView m_consoleView = null;

        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try {
            m_consoleView = (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
        } catch (PartInitException e1) {
            e1.printStackTrace();
        }
        if (m_consoleView == null) {
            return null;
        }


        final SurfEclipseConsole myConsole = new SurfEclipseConsole("Surf", null);


        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { myConsole });
        m_consoleView.display(myConsole);
        
        return myConsole;
        //writeToConsole(myConsole, CLI_PROMPT);
    }
	
	*/
}

