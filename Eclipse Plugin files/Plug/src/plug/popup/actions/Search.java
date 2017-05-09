package plug.popup.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;

public class Search extends AbstractHandler implements IViewActionDelegate{

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		System.out.println("Searchiiinnnngggggg");
		
		MessageBox box = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		box.setText("Information");
		box.setMessage("Choose the Search below to search results");
		box.open();
		
		
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
          mybrowser.setUrl("www.google.com");
          
         // SurfEclipseConsole.this.add_currently_displayed_url(selected.resultURL);
        }
        catch (Exception exc)
        {
          exc.printStackTrace();
        }
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	
	
      
		
	}

	@Override
	public void init(IViewPart view) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException {
		// TODO Auto-generated method stub
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"Information",
				"Choose the Search below to search results");
		
		
		
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
          mybrowser.setUrl("www.google.com");
          
         // SurfEclipseConsole.this.add_currently_displayed_url(selected.resultURL);
        }
        catch (Exception exc)
        {
          exc.printStackTrace();
        }
      
		
		
		
		
		return null;
	}


	

	
}
