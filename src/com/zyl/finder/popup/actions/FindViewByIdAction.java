package com.zyl.finder.popup.actions;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.xml.sax.SAXException;

import com.zyl.finder.findviewbyid.Builder;
import com.zyl.finder.handlers.FindViewByIdHandler;

public class FindViewByIdAction implements IObjectActionDelegate {

	private Shell shell;
	private IWorkbenchPart targetPart;
	/**
	 * Constructor for Action1.
	 */
	public FindViewByIdAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		IStructuredSelection selection = ((IStructuredSelection)targetPart.getSite().getSelectionProvider().getSelection());
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof IJavaElement)
		{
			IJavaElement je = (IJavaElement) firstElement;
			if(je.getResource().getType() != IResource.FILE)
			{
				MessageDialog.openInformation(shell, "Caster", "请选择java文件!");
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
						MessageDialog.openInformation(shell, "Caster", e.getMessage());
					}
				}else
				{
					MessageDialog.openInformation(shell, "Caster", "请选择java文件!");
				}
			}
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
