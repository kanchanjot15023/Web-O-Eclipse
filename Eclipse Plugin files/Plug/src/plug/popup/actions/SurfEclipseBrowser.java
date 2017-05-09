package plug.popup.actions;

import java.util.HashMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import plug.Activator;

public class SurfEclipseBrowser extends ViewPart {

	public static final String ID = "ca.usask.ca.srlab.surfclipse.client.views.SurfClipseBrowser";
	public Browser webbrowser;
	String title;
	  String url;
	  String runningDocumentTitle;
	  Button prevButton;
	  Button nextButton;
	  Label pageLabel;
	  
	  protected void create_navigator_buttons(Composite parent)
	  {
	    Composite composite = new Composite(parent, 0);
	    GridLayout gridLayout = new GridLayout(5, false);
	    gridLayout.marginWidth = 0;
	    gridLayout.marginHeight = 0;
	    gridLayout.verticalSpacing = 10;
	    gridLayout.horizontalSpacing = 0;
	    composite.setLayout(gridLayout);
	    
	    GridData gridData = new GridData(16777216, 4, true, false);
	    composite.setLayoutData(gridData);
	    
	    gridData = new GridData(-1, 4, false, false);
	    
	    this.prevButton = new Button(composite, 8);
	    this.prevButton.setImage(PlatformUI.getWorkbench().getSharedImages()
	      .getImage("IMG_TOOL_BACK"));
	    this.prevButton.setToolTipText("Previous Page");
	    this.prevButton.setLayoutData(gridData);
	    
	    this.prevButton.addSelectionListener(new SelectionListener() {
			
	      public void widgetSelected(SelectionEvent e)
	      {
	        SurfEclipseBrowser.this.webbrowser.back();
	      }
	      
	      public void widgetDefaultSelected(SelectionEvent e) {}
	    });
	    this.nextButton = new Button(composite, 8);
	    this.nextButton.setImage(PlatformUI.getWorkbench().getSharedImages()
	      .getImage("IMG_TOOL_FORWARD"));
	    this.nextButton.setToolTipText("Next Page");
	    this.nextButton.setLayoutData(gridData);
	    this.nextButton.addSelectionListener(new SelectionListener()
	    {
	      public void widgetSelected(SelectionEvent e)
	      {
	        SurfEclipseBrowser.this.webbrowser.forward();
	      }
	      
	      public void widgetDefaultSelected(SelectionEvent e) {}
	    });
	    Button backtoresult = new Button(composite, 8);
	    backtoresult.setToolTipText("Back to Search Results");
	    backtoresult.setLayoutData(gridData);
	    backtoresult.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"images/back.png").createImage());
	    backtoresult.addSelectionListener(new SelectionListener()
	    {
	      public void widgetSelected(SelectionEvent e)
	      {
	        try
	        {
	          String viewID = "ca.usask.ca.srlab.surfclipse.client.views.SurfClipseClientView";
	          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewID);
	        }
	        catch (Exception localException) {}
	      }
	      
	      public void widgetDefaultSelected(SelectionEvent e) {}
	    });
	    Button bookmarkButton = new Button(composite, 8);
	    bookmarkButton.setToolTipText("Bookmark this Page");
	    
	    bookmarkButton.setLayoutData(gridData);
	    bookmarkButton.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"images/bookmark.png").createImage());
	    bookmarkButton.addSelectionListener(new SelectionListener()
	    {
	      public void widgetSelected(SelectionEvent e)
	      {
	        String title = SurfEclipseBrowser.this.runningDocumentTitle;
	        String url = SurfEclipseBrowser.this.webbrowser.getUrl();
	        BookmarkManager.addBookMark(title, url);
	        SurfEclipseBrowser.this.showMessageBox("Bookmarked successfully!");
	      }
	      
	      public void widgetDefaultSelected(SelectionEvent e) {}
	    });
	    final Button showBookmarkButton = new Button(composite, 8);
	    showBookmarkButton.setToolTipText("Browse from Bookmarks");
	    showBookmarkButton.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"images/open_book1.png").createImage());
	    showBookmarkButton.setLayoutData(gridData);
	    showBookmarkButton.addSelectionListener(new SelectionListener()
	    {
	      public void widgetSelected(SelectionEvent e)
	      {
	        Button myButton = (Button)e.getSource();
	        if (myButton == showBookmarkButton) {
	          SurfEclipseBrowser.this.showAllBookMarks(myButton);
	        }
	      }
	      
	      public void widgetDefaultSelected(SelectionEvent e) {}
	    });
	  }
	  
	  public void showAllBookMarks(Button bm)
	  {
	    try
	    {
	      final HashMap<String, String> bookmarks = BookmarkManager.loadBookMarks();
	      System.out.println("Bookmarks loaded:" + bookmarks.keySet().size());
	      Menu menu = new Menu(bm);
	      for (String title : bookmarks.keySet())
	      {
	        MenuItem item = new MenuItem(menu, 0);
	        item.setText(title);
	        
	        item.addListener(13, new Listener() 
	        {
	          public void handleEvent(Event event)
	          {
	            MenuItem s_item = (MenuItem)event.widget;
	            System.out.println(s_item.getText());
	            String pageURL = (String)bookmarks.get(s_item.getText());
	            SurfEclipseBrowser.this.webbrowser.setUrl(pageURL);
	          }
	        });
	      }
	      bm.setMenu(menu);
	      menu.setVisible(true);
	    }
	    catch (Exception localException) {}
	  }
	  
	  protected Image get_search_image()
	  {
	    return ImageDescriptor.createFromFile(SurfEclipseBrowser.class, "gof.png").createImage();
	  }
	  
	  protected void add_page_search_panel(Composite parent)
	  {
	    Composite composite = new Composite(parent, 0);
	    GridLayout gridLayout = new GridLayout(3, false);
	    gridLayout.marginWidth = 0;
	    gridLayout.marginHeight = 0;
	    gridLayout.verticalSpacing = 10;
	    gridLayout.horizontalSpacing = 5;
	    composite.setLayout(gridLayout);
	    
	    GridData gridData = new GridData(16777216, 4, true, false);
	    composite.setLayoutData(gridData);
	    
	    GridData gdata2 = new GridData();
	    gdata2.heightHint = 25;
	    gdata2.widthHint = 600;
	    gdata2.horizontalAlignment = 1;
	    gdata2.grabExcessHorizontalSpace = false;
	    
	    Label address = new Label(composite, 0);
	    address.setText("Address:");
	    address.setFont(new Font(composite.getDisplay(), "Arial", 11, 1));
	    
	    final Text input = new Text(composite, 2052);
	    input.setToolTipText("Enter a page URL");
	    input.setLayoutData(gdata2);
	    input.setFont(new Font(composite.getDisplay(), "Arial", 11, 0));
	    
	    GridData gdata3 = new GridData();
	    gdata3.heightHint = 30;
	    gdata3.widthHint = 90;
	    gdata3.horizontalAlignment = 1;
	    
	    Button searchButton = new Button(composite, 8);
	    searchButton.setText("Go");
	    searchButton.setFont(new Font(composite.getDisplay(), "Arial", 10, 1));
	    searchButton.setLayoutData(gdata3);
	    searchButton.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"images/go1.png").createImage());
	    
	    searchButton.addSelectionListener(new SelectionListener()
	    {
	      public void widgetSelected(SelectionEvent e)
	      {
	        String targetUrl = input.getText();
	        if (!targetUrl.isEmpty()) {
	          SurfEclipseBrowser.this.webbrowser.setUrl(targetUrl);
	        } else {
	          SurfEclipseBrowser.this.showMessageBox("Please enter your search query or URL");
	        }
	      }
	      
	      public void widgetDefaultSelected(SelectionEvent e) {}
	    });
	    input.addKeyListener(new KeyAdapter() 
	    {
	      public void keyPressed(KeyEvent event)
	      {
	        switch (event.keyCode)
	        {
	        case 13: 
	          try
	          {
	            String targetUrl = input.getText();
	            if (!targetUrl.isEmpty()) {
	              SurfEclipseBrowser.this.webbrowser.setUrl(targetUrl);
	            } else {
	              SurfEclipseBrowser.this.showMessageBox("Please enter your search query or URL");
	            }
	          }
	          catch (Exception localException) {}
	        case 127: 
	          input.setText("");
	          break;
	        case 27: 
	          input.setText("");
	        }
	      }
	    });
	  }
	  
	  protected void add_swt_browser(Composite parent)
	  {
	    try
	    {
	      GridData gridData = new GridData(4, 4, true, true);
	      
	      this.webbrowser = new Browser(parent, 0);
	      this.webbrowser.setLayoutData(gridData);
	      this.webbrowser.addTitleListener(new TitleListener() 
	      {
	        public void changed(TitleEvent event)
	        {
	          System.out.println("TitleEvent: " + event.title);
	          
	          SurfEclipseBrowser.this.runningDocumentTitle = event.title;
	          System.out.println("Current running page title:" + SurfEclipseBrowser.this.runningDocumentTitle);
	        }
	      });
	      this.webbrowser.addProgressListener(new ProgressListener() 
	      {
	        public void completed(ProgressEvent event) {}
	        
	        public void changed(ProgressEvent event) {}
	      });
	      this.webbrowser.setUrl("http://www.google.com");
	    }
	    catch (Exception exc)
	    {
	      exc.printStackTrace();
	    }
	  }
	  
	  protected void add_browsed_page_info(Composite parent)
	  {
	    Composite composite = new Composite(parent, 0);
	    GridLayout gridLayout = new GridLayout(1, false);
	    gridLayout.marginWidth = 0;
	    gridLayout.marginHeight = 0;
	    gridLayout.verticalSpacing = 10;
	    gridLayout.horizontalSpacing = 5;
	    composite.setLayout(gridLayout);
	    
	    GridData gridData = new GridData(16777216, 4, true, false);
	    composite.setLayoutData(gridData);
	    
	    GridData gdata2 = new GridData();
	    gdata2.heightHint = 25;
	    gdata2.widthHint = 800;
	    gdata2.horizontalAlignment = 16777216;
	    gdata2.grabExcessHorizontalSpace = true;
	    
	    this.pageLabel = new Label(parent, 0);
	    this.pageLabel.setFont(new Font(parent.getDisplay(), "Arial", 14, 1));
	    this.pageLabel.setLayoutData(gdata2);
	    final Color myColor = new Color(parent.getDisplay(), 0, 102, 255);
	    this.pageLabel.setForeground(myColor);
	    this.pageLabel.addDisposeListener(new DisposeListener() 
	    {
	      public void widgetDisposed(DisposeEvent e)
	      {
	        myColor.dispose();
	      }
	    });
	  }
	  
	  public void createPartControl(Composite parent)
	  {
	    GridLayout glayout = new GridLayout();
	    glayout.marginWidth = 5;
	    glayout.marginHeight = 5;
	    parent.setLayout(glayout);
	    
	    GridData gdata = new GridData(4, 4, true, true);
	    parent.setLayoutData(gdata);
	    
	    create_navigator_buttons(parent);
	    add_page_search_panel(parent);
	    
	    add_swt_browser(parent);
	  }
	  
	  protected void showMessageBox(String message)
	  {
	    try
	    {
	      Shell shell = org.eclipse.swt.widgets.Display.getDefault().getShells()[0];
	      MessageDialog.openInformation(shell, "Information", message);
	    }
	    catch (Exception localException) {}
	  }
	  
	  public void show_the_result_link(String title, String url)
	  {
	    this.title = title;
	    this.url = url;
	    this.webbrowser.setUrl(this.url);
	  }
	  
	  public void setFocus()
	  {
	    this.webbrowser.setFocus();
	  }

	

}
