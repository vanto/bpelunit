/**
 * This file belongs to the BPELUnit utility and Eclipse plugin set. See enclosed
 * license file for more information.
 * 
 */
package org.bpelunit.toolsupport.editors.wizards.pages;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.bpelunit.framework.xml.suite.XMLActivity;
import org.bpelunit.framework.xml.suite.XMLSoapActivity;
import org.bpelunit.toolsupport.editors.wizards.ActivityEditMode;
import org.bpelunit.toolsupport.editors.wizards.ReceiveSendAsyncActivityWizard;
import org.bpelunit.toolsupport.editors.wizards.ReceiveSendSyncActivityWizard;
import org.bpelunit.toolsupport.editors.wizards.WizardPageCode;
import org.bpelunit.toolsupport.editors.wizards.components.IComponentListener;
import org.bpelunit.toolsupport.editors.wizards.components.OperationDataComponent;
import org.bpelunit.toolsupport.editors.wizards.fields.DialogField;
import org.bpelunit.toolsupport.util.schema.InvalidInputException;
import org.bpelunit.toolsupport.util.schema.NoSuchOperationException;
import org.bpelunit.toolsupport.util.schema.WSDLParser;
import org.bpelunit.toolsupport.util.schema.nodes.Element;
import org.eclipse.swt.widgets.Composite;

/**
 * A page for editing SOAP operations.
 * 
 * @version $Id$
 * @author Philip Mayer
 * 
 */
public class OperationWizardPage extends ActivityWizardPage implements IComponentListener {

	private XMLSoapActivity fOperationActivity;
	private OperationDataComponent fOperationsComponent;

	public OperationWizardPage(XMLSoapActivity operationActivity, ActivityEditMode mode, String pageName) {
		super(pageName, mode);
		this.fOperationActivity = operationActivity;
		this.setDescription("Enter an operation and the data to send.");
	}

	@Override
	protected void createFieldControls(Composite composite, int nColumns) {

		this.fOperationsComponent = new OperationDataComponent(this, this.getFontMetrics());
		this.fOperationsComponent.init(this.fOperationActivity);
		this.fOperationsComponent.createControls(composite, nColumns);
		this.fOperationsComponent.addComponentListener(this);

		this.valueChanged(null);
	}

	public void valueChanged(DialogField field) {
		this.setErrorMessage(this.fOperationsComponent.getErrorMessage());
		this.setPageComplete(this.fOperationsComponent.isPageComplete());
	}

	public Definition getDefinition() {
		return this.fOperationsComponent.getDefinition();
	}

	public QName getService() {
		return this.fOperationsComponent.getService();
	}

	public String getPort() {
		return this.fOperationsComponent.getPort();
	}

	public String getOperation() {
		return this.fOperationsComponent.getOperation();
	}

	public boolean getSendFault() {
		return this.fOperationsComponent.getSendFault();
	}

	public String getSendFaultName() {
		return this.fOperationsComponent.getSendFaultName();
	}

	public boolean getReceiveFault() {
		return this.fOperationsComponent.getReceiveFault();
	}

	public WSDLParser getWSDLParser() {
		return this.fOperationsComponent.getWSDLParser();
	}

	protected XMLActivity getActivity() {
		return this.fOperationActivity;
	}

	public OperationDataComponent getOperationDataComponent() {
		return this.fOperationsComponent;
	}

	@Override
	public WizardPageCode getCode() {
		return WizardPageCode.OPERATION;
	}

	public Element getElementForOperation() throws InvalidInputException, NoSuchOperationException {
		Element element = null;
		WSDLParser parser = this.getWSDLParser();
		if (parser == null) {
			return null;
		}
		
		/* Normally I'd expect faults only to be sent instead of an <output>,
		 * but it turns out they can be sent instead of an <input>, for
		 * solicit-response operations (see WSDL 1.1, section 2.4.3). */
		if (this.getSendFault()) {
			element = parser.getFaultElementForOperation(
					getService(), getPort(), getOperation(), getSendFaultName());
		}
		else if (this.getWizard() instanceof ReceiveSendAsyncActivityWizard
				|| this.getWizard() instanceof ReceiveSendSyncActivityWizard) {
			element = parser.getOutputElementForOperation(this.getService(), this.getPort(), this
					.getOperation());
		} else {
			element = parser.getInputElementForOperation(this.getService(), this.getPort(), this
					.getOperation());
		}
		return element;
	}
}
