/**
 * This file belongs to the BPELUnit utility and Eclipse plugin set. See enclosed
 * license file for more information.
 * 
 */
package org.bpelunit.framework.model.test.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.bpelunit.framework.control.util.BPELUnitUtil;
import org.bpelunit.framework.model.test.PartnerTrack;
import org.bpelunit.framework.model.test.data.DataCopyOperation;
import org.bpelunit.framework.model.test.report.ArtefactStatus;
import org.bpelunit.framework.model.test.report.ITestArtefact;
import org.bpelunit.framework.model.test.wire.IncomingMessage;
import org.bpelunit.framework.model.test.wire.OutgoingMessage;

/**
 * A receive/send synchronous activity is intended to receive a incoming synchronous SOAP message,
 * process it, and return an answer within the same HTTP connection.
 * 
 * A receive/send synchronous activity must return an answer to the caller, even if it is only a
 * SOAP fault indicating something went wrong.
 * 
 * The combined receive/send activity has the optional ability of header processing and copying
 * values from input to output.
 * 
 * @version $Id$
 * @author Philip Mayer
 * 
 */
public class ReceiveSendSync extends TwoWaySyncActivity {


	// ********************************* Initialization ****************************

	public ReceiveSendSync(PartnerTrack partnerTrack) {
		super(partnerTrack);
		fStatus= ArtefactStatus.createInitialStatus();
	}


	// ***************************** Activity **************************

	@Override
	public void run(ActivityContext context) {

		context.setHeaderProcessor(fHeaderProcessor);
		context.setMapping(fMapping);

		IncomingMessage incoming;
		try {
			incoming= context.receiveMessage(this.getPartnerTrack());
		} catch (TimeoutException e) {
			fStatus= ArtefactStatus.createErrorStatus("Timeout while waiting for incoming synchronous message", e);
			return;
		} catch (InterruptedException e) {
			fStatus= ArtefactStatus.createAbortedStatus("Aborted while waiting for incoming synchronous message", e);
			return;
		}

		fReceiveSpec.handle(context, incoming.getBody());

		/*
		 * This is the only place in the testing framework where we can (and should actually return
		 * a result even if the test failed.
		 */

		OutgoingMessage msg= new OutgoingMessage();

		if (!fReceiveSpec.hasProblems()) {
			// Receive was successful

			fSendSpec.handle(context);

			if (!fSendSpec.hasProblems()) {
				if (fSendSpec.isFault())
					msg.setCode(500);
				else
					msg.setCode(200);
				msg.setBody(fSendSpec.getInWireFormat());
			} else {
				// Could not successfully generate a return value for
				// whatever reason.
				msg.setCode(500);
				msg.setBody(BPELUnitUtil.generateGenericSOAPFault());
			}
		} else {
			// Receive was not successful
			msg.setCode(500);
			msg.setBody(BPELUnitUtil.generateGenericSOAPFault());
		}

		try {
			fSendSpec.delay();
			context.postAnswer(this.getPartnerTrack(), msg);

			if (fReceiveSpec.hasProblems())
				fStatus= fReceiveSpec.getStatus();
			else if (fSendSpec.hasProblems())
				fStatus= fSendSpec.getStatus();
			else
				fStatus= ArtefactStatus.createPassedStatus();

		} catch (TimeoutException e) {
			fStatus= ArtefactStatus.createErrorStatus("Timeout occurred while waiting for synchronous answer to be sent.", e);
			return;
		} catch (InterruptedException e) {
			fStatus= ArtefactStatus.createAbortedStatus("Aborted while waiting for synchronous answer to be sent.", e);
			return;
		}
	}

	@Override
	public String getActivityCode() {
		return "ReceiveSendSync";
	}


	// ************************** ITestArtefact ************************

	@Override
	public String getName() {
		return "Receive/Send Synchronous";
	}

	@Override
	public List<ITestArtefact> getChildren() {
		List<ITestArtefact> children= new ArrayList<ITestArtefact>();
		if (fMapping != null)
			for (DataCopyOperation copy : fMapping)
				children.add(copy);
		children.add(fReceiveSpec);
		children.add(fSendSpec);
		return children;
	}

}
