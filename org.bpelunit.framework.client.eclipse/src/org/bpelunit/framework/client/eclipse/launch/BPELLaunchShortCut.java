/**
 * This file belongs to the BPELUnit utility and Eclipse plugin set. See enclosed
 * license file for more information.
 * 
 */
package org.bpelunit.framework.client.eclipse.launch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bpelunit.framework.client.eclipse.BPELUnitActivator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * Shortcut for starting directly by right-clicking on .bpts files
 * 
 * @version $Id$
 * @author Philip Mayer
 * 
 */
public class BPELLaunchShortCut implements ILaunchShortcut {

	public void launch(ISelection selection, String mode) {

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel= (IStructuredSelection) selection;
			Object o= sel.getFirstElement();
			if (o instanceof IFile)
				launch((IFile) o, mode);
		}
	}

	public void launch(IEditorPart editor, String mode) {

		IEditorInput input= editor.getEditorInput();
		IFile file= (IFile) input.getAdapter(IFile.class);

		if (file != null)
			launch(file, mode);

	}

	private void launch(IFile file, String mode) {
		ILaunchConfiguration config= findLaunchConfiguration(file, getConfigurationType());
		if (config != null) {
			DebugUITools.launch(config, mode);
		}
	}

	protected ILaunchConfiguration findLaunchConfiguration(IFile file, ILaunchConfigurationType configType) {

		List<ILaunchConfiguration> candidateConfigs= Collections.emptyList();
		try {
			ILaunchConfiguration[] configs= DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(configType);
			candidateConfigs= new ArrayList<ILaunchConfiguration>(configs.length);
			for (ILaunchConfiguration config : configs) {
				if (config.getAttribute(LaunchConstants.ATTR_PROJECT_NAME, "").equals(file.getProject().getName())) { //$NON-NLS-1$
					if (config.getAttribute(LaunchConstants.ATTR_SUITE_FILE_NAME, "").equals(file.getProjectRelativePath().toString())) { //$NON-NLS-1$
						candidateConfigs.add(config);
					}
				}
			}
		} catch (CoreException e) {
			BPELUnitActivator.log(e);
		}

		// If there are no existing configs associated with the IFile, create
		// one. If there is exactly one config associated with the IFile, return
		// it. Otherwise, if there is more than one config associated with the
		// IFile, prompt the user to choose one.
		int candidateCount= candidateConfigs.size();
		if (candidateCount < 1) {
			return createConfiguration(file);
		} else if (candidateCount == 1) {
			return candidateConfigs.get(0);
		} else {
			// Prompt the user to choose a config. A null result means the user
			// cancelled the dialog, in which case this method returns null,
			// since cancelling the dialog should also cancel launching
			// anything.
			ILaunchConfiguration config= chooseConfiguration(candidateConfigs);
			if (config != null) {
				return config;
			}
		}

		return null;
	}

	/**
	 * Show a selection dialog that allows the user to choose one of the specified launch
	 * configurations. Return the chosen config, or <code>null</code> if the user cancelled the
	 * dialog.
	 */
	protected ILaunchConfiguration chooseConfiguration(List configList) {
		IDebugModelPresentation labelProvider= DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle("Select Test Suite");
		dialog.setMessage("&Select existing configuration:");
		dialog.setMultipleSelection(false);
		int result= dialog.open();
		labelProvider.dispose();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;
	}

	private Shell getShell() {
		return BPELUnitActivator.getActiveWorkbenchWindow().getShell();
	}

	protected ILaunchConfigurationType getConfigurationType() {
		return getLaunchManager().getLaunchConfigurationType(LaunchConstants.ID_LAUNCH_CONFIG_TYPE);
	}

	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	protected ILaunchConfiguration createConfiguration(IFile file) {
		ILaunchConfiguration config= null;
		ILaunchConfigurationWorkingCopy wc= null;

		String configName= file.getName();
		int index= configName.lastIndexOf('.');
		if (index != -1)
			configName= configName.substring(0, index);

		try {
			ILaunchConfigurationType configType= getConfigurationType();
			wc= configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(configName));
			wc.setAttribute(LaunchConstants.ATTR_PROJECT_NAME, file.getProject().getName());
			wc.setAttribute(LaunchConstants.ATTR_SUITE_FILE_NAME, file.getProjectRelativePath().toString());
			wc.setMappedResources(new IResource[] { file.getProject() });
			config= wc.doSave();
		} catch (CoreException exception) {
			reportError(exception);
		}
		return config;
	}

	protected void reportError(CoreException exception) {
		MessageDialog.openError(getShell(), "Error", exception.getStatus().getMessage());
	}
}
