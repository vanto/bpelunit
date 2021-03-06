/**
 * This file belongs to the BPELUnit utility and Eclipse plugin set. See enclosed
 * license file for more information.
 * 
 */
package org.bpelunit.toolsupport.util;

import org.bpelunit.toolsupport.ToolSupportActivator;
import org.bpelunit.toolsupport.editors.BPELUnitEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/**
 * A validator for dialogs allowing the user to select a WSDL file.
 * 
 * @version $Id$
 * @author Philip Mayer
 * 
 */
public class WSDLFileValidator implements ISelectionStatusValidator {

	private BPELUnitEditor fEditor;

	public WSDLFileValidator(BPELUnitEditor editor) {
		fEditor= editor;
	}

	public IStatus validate(Object[] selection) {
		if ( (selection.length != 1) || (! (selection[0] instanceof IFile))) {
			return new Status(IStatus.ERROR, ToolSupportActivator.getPluginId(), 0, "Please select a WSDL file", null);
		}

		// Validate it...
		try {
			IFile file= (IFile) selection[0];
			fEditor.getWsdlForFile(file.getProjectRelativePath().toString());
		} catch (WSDLReadingException e) {
			String msg= e.getMessage() + ( (e.getCause() != null) ? ": " + e.getCause() : "");
			return new Status(IStatus.ERROR, ToolSupportActivator.getPluginId(), 0, msg, null);
		}

		return new Status(IStatus.OK, ToolSupportActivator.getPluginId(), 0, "", null); //$NON-NLS-1$
	}

}
