package com.zyl.finder.handlers;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.packageview.*;
import org.xml.sax.SAXException;
import com.zyl.finder.findviewbyid.Builder;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class FindViewByIdHandler extends AbstractHandler
{
	/**
	 * The constructor.
	 */
	public FindViewByIdHandler()
	{
	}
	/**
	 * the command has been executed, so extract extract the needed
	 * information from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveSite(event).getSelectionProvider().getSelection();
//		MessageDialog.openInformation(window.getShell(), "Caster", jp.getResource().getLocation().toFile().toString());
		if (selection == null)
		{
			return null;
		}
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof IJavaElement)
		{
			IJavaElement je = (IJavaElement) firstElement;
			if(je.getResource().getType() != IResource.FILE)
			{
				MessageDialog.openInformation(window.getShell(), "Caster", "请选择java文件!");
			}else
			{
				File file = je.getResource().getLocation().toFile();
				IJavaProject jp = je.getJavaProject();
				File file_project = jp.getResource().getLocation().toFile();
				if(file.getName().endsWith(".java"))
				{
					Builder b = new Builder();
					try
					{
						b.build(file_project,file);
					} catch (IllegalArgumentException | IOException | ParserConfigurationException | SAXException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						MessageDialog.openInformation(window.getShell(), "Caster", e.getMessage());
					}
				}else
				{
					MessageDialog.openInformation(window.getShell(), "Caster", "请选择java文件!");
				}
			}
		}
		return null;
	}
}
