package plug.popup.actions;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ViewContentProvider implements IStructuredContentProvider {

	
	 Result[] displayableResults;

	 public ViewContentProvider(Result[] results)
	  {
	    this.displayableResults = results;
	  }

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		return this.displayableResults;
	}
	 
}
