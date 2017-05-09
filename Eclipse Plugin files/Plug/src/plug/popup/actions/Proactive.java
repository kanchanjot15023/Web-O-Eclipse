package plug.popup.actions;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.ParameterType;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.SerializationException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementReference;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

import plug.Activator;

public class Proactive extends AbstractHandler implements IViewActionDelegate,IElementUpdater{

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		
		if(InvocationProactive.status==-1 || InvocationProactive.status==0)
		{
			InvocationProactive.status=1;
			MessageDialog.openInformation(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Information",
					"Proactive Mode Enabled");
		action.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"images/prowithtick.png"));
		}
		else
		{
			InvocationProactive.status=0;
			MessageDialog.openInformation(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Information",
					"Proactive Mode Disabled");
			action.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"images/pro.png"));
		}
		
		ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		service.refreshElements("ca.usask.ca.srlab.surfclipse.client.EnableProactive", null);
		System.out.println(action.getId());
		
		service.refreshElements("Plug.Proactive", null);
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		System.out.println("selection changed");
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub
		
		
System.out.println("In Proactive Mode");
		
		if(InvocationProactive.status==-1 || InvocationProactive.status==0)
		{
			InvocationProactive.status=1;
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			MessageDialog.openInformation(
					window.getShell(),
					"Information",
					"Proactive Mode Enabled");
		}
		else
		{
			InvocationProactive.status=0;
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			MessageDialog.openInformation(
					window.getShell(),
					"Information",
					"Proactive Mode Disabled");
		}
		
		System.out.println(event.getCommand().getId());
		ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
				service.refreshElements(event.getCommand().getId(), null);
				
				
		return null;
	}

	@Override
	public void init(IViewPart view) {
		// TODO Auto-generated method stub
		
		System.out.println("inittt");
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		// TODO Auto-generated method stub
		System.out.println("checked");
		if(InvocationProactive.status==-1 || InvocationProactive.status==0)
		{
		element.setIcon(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"images/pro.png"));
		}
		else
			element.setIcon(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,"images/prowithtick.png"));
	}


}
