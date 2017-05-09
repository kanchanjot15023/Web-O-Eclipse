package plug.popup.actions;

import org.eclipse.jdt.internal.debug.ui.console.JavaConsoleTracker;
import org.eclipse.ui.console.TextConsole;

public class StackTrace1 extends JavaConsoleTracker{

	public void running()
	{
		TextConsole console = getConsole();
		String exceptionName;
        exceptionName = console.getDocument().get();
        System.out.println(exceptionName);
	}
}
