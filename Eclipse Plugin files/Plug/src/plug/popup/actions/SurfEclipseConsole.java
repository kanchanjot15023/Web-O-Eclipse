package plug.popup.actions;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.internal.dialogs.ViewContentProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import plug.Activator;

public class SurfEclipseConsole extends ViewPart {

	public static final String Client_View_ID = "ca.usask.ca.srlab.surfclipse.client.views.SurfClipseClientView";
	public TableViewer viewer=null;
	 public Text input =null;
	final Display currDisplay = Display.getCurrent();
	  final TextLayout textLayout = new TextLayout(this.currDisplay);
	  Font font1 = new Font(this.currDisplay, "Arial", 13, 1);
	  Font font2 = new Font(this.currDisplay, "Arial", 12, 0);
	  Font font3 = new Font(this.currDisplay, "Arial", 12, 0);
	  Color blue = this.currDisplay.getSystemColor(9);
	  Color green = this.currDisplay.getSystemColor(6);
	  Color gray = this.currDisplay.getSystemColor(16);
	  TextStyle style1 = new TextStyle(this.font1, this.blue, null);
	  TextStyle style2 = new TextStyle(this.font2, this.green, null);
	  TextStyle style3 = new TextStyle(this.font3, this.gray, null);
	
	
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		GridLayout glayout = new GridLayout();
	    glayout.marginWidth = 15;
	    glayout.marginHeight = 5;
	    parent.setLayout(glayout);
	    
	    GridData gdata = new GridData(4, 4, true, true);
	    parent.setLayoutData(gdata);
	    
	    results(parent);
	    
	    PlatformUI.getWorkbench()
	      .getHelpSystem()
	      .setHelp(this.viewer.getControl(), 
	      "ca.usask.ca.srlab.surfclipse.client.viewer");
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	
	
	public void results(Composite parent)
	{
		 GridData gridData = new GridData(4, 4, true, true);
		 this.viewer = new TableViewer(parent, 99074);
		 
		 Table table = this.viewer.getTable();
		    table.setLayoutData(gridData);
		    table.setHeaderVisible(true);
		    table.setLinesVisible(true);
		    table.setFont(font1);
		    
		    String[] columnNames = { "Search Result", "Score", "Content Relevance", 
		    	      "Context Relevance", "Stack Overflow Vote Score" };
		    
		    int[] colWidth = { 700, 100, 120, 120, 150 };
		    int[] colAlignment = { 16384, 16384, 16384, 16384, 
		      16384, 16384 };
		    
		    
		    for (int i = 0; i < columnNames.length; i++)
		    {
		      final int columnNum = i;
		      
		      TableColumn col = new TableColumn(table, colAlignment[i]);
		      col.setText(columnNames[i]);
		      col.setWidth(colWidth[i]);
		    }
		    
		    
		    
		    this.viewer.setContentProvider(new ViewContentProvider());
		    this.viewer.setLabelProvider(new ViewLabelProvider());
		    //this.viewer.setSorter(new MyTableSorter());
		    
		    this.viewer.setInput(getViewSite());
		    
		    
		    
		    this.viewer.addDoubleClickListener(new IDoubleClickListener() 
		    {
		      public void doubleClick(DoubleClickEvent event)
		      {
		        System.out.println("Double clicked");
		        
		        IStructuredSelection selection = (IStructuredSelection)event
		          .getSelection();
		        if (selection.isEmpty()) {
		          return;
		        }
		        
		        List<Object> list = selection.toList();
		        Object obj1 = list.get(0);
		        
		        Result selected = (Result)obj1;
		        System.out.println("Selected URL:" + selected.resultURL);
		        try
		        {
		          String viewID = "ca.usask.ca.srlab.surfclipse.client.views.SurfClipseBrowser";
		          PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		            .getActivePage().showView(viewID);
		          IWorkbenchPage page = 
		            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		            .getActivePage();
		          IViewPart vpart = page.findView(viewID);
		          SurfEclipseBrowser my_browser_view = (SurfEclipseBrowser)vpart;
		          Browser mybrowser = my_browser_view.webbrowser;
		          mybrowser.setUrl(selected.resultURL);
		          
		         // SurfEclipseConsole.this.add_currently_displayed_url(selected.resultURL);
		        }
		        catch (Exception exc)
		        {
		          exc.printStackTrace();
		        }
		      }
		    });
		    setItemHeight(table);
		    setPaintItem(table);
		    
	}
	
	protected void setItemHeight(Table table)
	  {
	    table.addListener(41, new Listener() 
	    {
	      public void handleEvent(Event event)
	      {
	        TableItem item = (TableItem)event.item;
	        String text = item.getText(event.index);
	        Point size = event.gc.textExtent(text);
	        event.width = (size.x + 6);
	        
	        event.height = 50;
	      }
	    });
	    
	    
	  
	    
	    table.addListener(40, new Listener()
	    {
	      public void handleEvent(Event event)
	      {
	    	  
	    	  //System.out.println("Single clicked");
	    	  
	        event.detail &= 0xFFFFFFEF;
	      }
	    });
	  }
	  
	
	
	
	protected void setPaintItem(Table table)
	  {
	    table.addListener(42, new Listener()
	    {
	      public void handleEvent(Event event)
	      {
	        if (event.index == 0)
	        {
	          TableItem item = (TableItem)event.item;
	          
	          String resultText = item.getText(0).trim();
	          Image img=item.getImage(0);
	          int firstNL = resultText.indexOf('\n');
	          SurfEclipseConsole.this.textLayout.setText(resultText);
	          SurfEclipseConsole.this.textLayout.setStyle(SurfEclipseConsole.this.style1, 0, firstNL - 1);
	          SurfEclipseConsole.this.textLayout.setStyle(SurfEclipseConsole.this.style2, firstNL + 1, resultText.length());
	          //SurfEclipseConsole.this.textLayout.setStyle(SurfEclipseConsole.this.style3, lastNL, resultText.length());
	          SurfEclipseConsole.this.textLayout.draw(event.gc, event.x+25, event.y);
	          event.gc.drawImage(img, event.x, event.y);
	          
	        }
	        else if (event.index > 0)
	        {
	          GC gc = event.gc;
	          int index = event.index;
	          
	          TableItem item = (TableItem)event.item;
	          int percent = (int)Double.parseDouble(item.getText(index));
	          Color foreground = gc.getForeground();
	          Color background = gc.getBackground();
	          
	          gc.setForeground(new Color(null, 11, 59, 23));
	          Color myforeground = null;
	          if (index == 1) {
	            myforeground = new Color(null, 165, 159, 220);
	          }
	          if (index == 2) {
	            myforeground = new Color(null, 245, 173, 118);
	          }
	          if (index == 3) {
	            myforeground = new Color(null, 119, 215, 206);
	          }
	          if (index == 4) {
	            myforeground = new Color(null, 171, 104, 104);
	          }
	          gc.setForeground(myforeground);
	          gc.setBackground(new Color(null, 255, 255, 255));
	          int col2Width = 100;
	          int width = (col2Width - 1) * percent / 100;
	          int height = 25;
	          
	          gc.fillGradientRectangle(event.x, event.y + 15, width, 
	            height, false);
	          Rectangle rect2 = new Rectangle(event.x, event.y + 15, 
	            width - 1, height - 1);
	          gc.drawRectangle(rect2);
	          gc.setForeground(new Color(null, 43, 8, 101));
	          String text = percent + "%";
	          Point size = event.gc.textExtent(text);
	          int offset = Math.max(0, (height - size.y) / 2);
	          gc.drawText(text, event.x + 2, event.y + 15 + offset, true);
	          gc.setForeground(background);
	          gc.setBackground(foreground);
	          
	        }
	      }
	    });
	  }
	
	
	
	
	
	
	class ViewContentProvider
    implements IStructuredContentProvider
  {
    ViewContentProvider() {}
    
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {}
    
    public void dispose() {}
    
    public Object[] getElements(Object parent)
    {
      return new String[0];
    }
  }
	
	
	
	class ViewLabelProvider
    extends LabelProvider
    implements ITableLabelProvider
  {
    ViewLabelProvider() {}
    
    public String getColumnText(Object obj, int index)
    {
    	
    	System.out.println("get column context");
      Result myresult = (Result)obj;
      switch (index)
      {
      case 0: 
        if (myresult.title != null) {
          return myresult.title.trim() + "\n" + myresult.resultURL;
        }
        return "";
      case 1: 
        return String.format("%.2f", new Object[] { Double.valueOf(myresult.totalScore * 100.0D) });
      case 2: 
        return String.format("%.2f", new Object[] { Double.valueOf(myresult.content_score * 100.0D) });
      case 3: 
        return String.format("%.2f", new Object[] { Double.valueOf(myresult.context_score * 100.0D) });
      case 4: 
        return String.format("%.2f", new Object[] { Double.valueOf(myresult.stackOverflowVoteScore * 100.0D) });
      }
      return "";
    }
    
    public Image getColumnImage(Object obj, int index)
    {
      if (index > 0) {
        return null;
      }
      return getImage(obj);
    }
    
    public Image getImage(Object obj)
    {
    	
    	
      Image img = null;
      try
      {
        Result result = (Result)obj;
        System.out.println("in stack");
        if (result.searchEngine.equals("stackoverflow")) {
        	
        	System.out.println("in stack");
          img = 
          
        		  AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"images/stack.png").createImage();
        } else if(result.searchEngine.equals("google")) 
        {
          img = 
          
        		  AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"images/google.png").createImage();
        }
        else if(result.searchEngine.equals("bing")) 
        {
          img = 
          
        		  AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"images/bing.png").createImage();
        }
      }
      catch (Exception exc)
      {
        System.err.println(exc.getMessage());
      }
      return img;
    }
  }
	
}
