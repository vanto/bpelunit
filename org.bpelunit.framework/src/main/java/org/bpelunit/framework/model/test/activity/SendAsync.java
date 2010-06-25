/**
 * This file belongs to the BPELUnit utility and Eclipse plugin set. See enclosed
 * license file for more information.
 * 
 */
package org.bpelunit.framework.model.test.activity;

import java.util.ArrayList;
import java.util.List;

import org.bpelunit.framework.exception.SynchronousSendException;
import org.bpelunit.framework.model.test.PartnerTrack;
import org.bpelunit.framework.model.test.data.SendDataSpecification;
import org.bpelunit.framework.model.test.report.ArtefactStatus;
import org.bpelunit.framework.model.test.report.ITestArtefact;
import org.bpelunit.framework.model.test.report.StateData;
import org.bpelunit.framework.model.test.wire.IncomingMessage;
import org.bpelunit.framework.model.test.wire.OutgoingMessage;

/**
 * A send asynchronous activity is intended to send a single outgoing transmission which contains a
 * SOAP body (a normal message, or a SOAP fault).
 * 
 * An asynchronous send will always receive an "OK" back from the server (unless a severe server
 * configuration error occurred). The specification does NOT allow to send back meaningful SOAP
 * Faults, so no such faults will be received as an answer. However, as a severe server config error
 * may indeed result in a return body (though not necessarily a SOAP body), the activity provides
 * the means to gather and display this body as part of the status data. Note that such a body is
 * ALWAYS unexpected.
 * 
 * Send Async can be used inside an asynchronous send/receive, receive/send, or send only block.
 * There are two constructors to accomodate these usages; one for directly initializing the activiy
 * as a child of a parent track, and one for initializing the activity as a child of another
 * (asynchronous two way) activity.
 * 
 * @version $Id$
 * @author Philip Mayer
 * 
 */
public class SendAsync extends Activity {

	/**
	 * The parent activity, if there is one.
	 */
	private TwoWayAsyncActivity fParentActivity;

	/**
	 * The send specification, which handles the actual send
	 */
	private SendDataSpecification fSendSpec;

	/**
	 * The send async activity may receive a body, which is illegal, but should be presented to the
	 * user.
	 */
	private String fWrongBody;


	// ************************** Initialization ************************

	public SendAsync(PartnerTrack partnerTrack) {
		super(partnerTrack);
		fParentActivity= null;
	}

	public SendAsync(TwoWayAsyncActivity activity) {
		super(activity.getPartnerTrack());
		fParentActivity= activity;
	}

	public void initialize(SendDataSpecification spec) {
		fSendSpec= spec;
		fStatus= ArtefactStatus.createInitialStatus();
	}

	// ***************************** Activity **************************

	@Override
	public void run(ActivityContext context) {

		fSendSpec.handle(context);

		if (fSendSpec.hasProblems()) {
			fStatus= fSendSpec.getStatus();
			return;
		}

		OutgoingMessage msg= new OutgoingMessage();
		msg.setSOAPAction(fSendSpec.getSOAPHTTPAction());
		msg.setTargetURL(fSendSpec.getTargetURL());
		msg.setBody(fSendSpec.getInWireFormat());

		IncomingMessage incoming;
		try {
			fSendSpec.delay();
			incoming= context.sendMessage(msg);
		} catch (SynchronousSendException e) {
			fStatus= ArtefactStatus.createErrorStatus("HTTP Error while sending out synchronous message!", e);
			return;
		} catch (InterruptedException e) {
			fStatus= ArtefactStatus.createAbortedStatus("Aborting due to error in another partner track.", e);
			return;
		}

		if (incoming.getReturnCode() > 200 && incoming.getReturnCode() < 300) {
			fStatus= ArtefactStatus.createPassedStatus();
		} else {
			// This is not possible unless there was a really grave error at the
			// server side.
			// Asynchronous receives may not throw a SOAP error.
			fStatus= ArtefactStatus.createErrorStatus("Asynchronous send got a non-2XX error code: " + incoming.getReturnCode(), null);
			fWrongBody= incoming.getBody();
		}
	}

	@Override
	public String getActivityCode() {
		return "SendAsync";
	}

	@Override
	public int getActivityCount() {
		return 1;
	}

	// ************************** ITestArtefact ************************

	@Override
	public String getName() {
		return "Send Asynchronous";
	}

	@Override
	public ITestArtefact getParent() {
		if (fParentActivity != null)
			return fParentActivity;
		else
			return getPartnerTrack();
	}

	@Override
	public List<ITestArtefact> getChildren() {
		List<ITestArtefact> children= new ArrayList<ITestArtefact>();
		children.add(fSendSpec);
		return children;
	}

	@Override
	public List<StateData> getStateData() {
		List<StateData> stateData= new ArrayList<StateData>();
		stateData.addAll(fStatus.getAsStateData());
		if (fWrongBody != null)
			stateData.add(new StateData("Return Body", fWrongBody));
		return stateData;
	}
}
