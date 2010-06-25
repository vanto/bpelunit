/**
 * This file belongs to the BPELUnit utility and Eclipse plugin set. See enclosed
 * license file for more information.
 * 
 */
package org.bpelunit.framework.model.test.activity;

import java.util.List;

import org.bpelunit.framework.model.Partner;
import org.bpelunit.framework.model.test.PartnerTrack;
import org.bpelunit.framework.model.test.report.ArtefactStatus;
import org.bpelunit.framework.model.test.report.ITestArtefact;
import org.bpelunit.framework.model.test.report.StateData;

/**
 * An activity is one logical event in the chain of events of a partner track. It is executed as
 * part of the partner track, "acting on behalf of the partner".
 * 
 * An activity uses other activities and, in the end, the data send/receive packages to achieve its
 * goal. When one of these child activities fail, error, or abort, the parent takes on its status.
 * 
 * If none of the children fail, error, or abort, the activity reports a passed status.
 * 
 * Each activity has very unique requirements. For example, a send/receive synchronous activity will
 * send and receive in a single, indivisable HTTP connection. A receive/send asynchronous activity
 * has in fact two consecutive single HTTP connections, each with send and receive data.
 * 
 * @version $Id$
 * @author Philip Mayer
 * 
 */
public abstract class Activity implements ITestArtefact {

	/**
	 * The partner track this activity belongs to.
	 */
	private PartnerTrack fPartnerTrack;

	/**
	 * The status of this object
	 */
	protected ArtefactStatus fStatus;


	// ******************* Initialization *********************

	public Activity(PartnerTrack partnerTrack) {
		fPartnerTrack= partnerTrack;
		fStatus= ArtefactStatus.createInitialStatus();
	}


	// ************************** Run *************************

	public abstract void run(ActivityContext context);


	// ******************* Getters and Setters ***************

	public PartnerTrack getPartnerTrack() {
		return fPartnerTrack;
	}


	public Partner getPartner() {
		return fPartnerTrack.getPartner();
	}

	public abstract int getActivityCount();

	/**
	 * Returns the code (not human-readable) of the activity, for example, "sendReceiveSync"
	 * 
	 * @return
	 */
	public abstract String getActivityCode();

	public boolean hasProblems() {
		return getStatus().hasProblems();
	}

	// ********************** ITestArtefact ******************

	public abstract String getName();

	public ArtefactStatus getStatus() {
		return fStatus;
	}

	public abstract ITestArtefact getParent();

	public abstract List<ITestArtefact> getChildren();

	public List<StateData> getStateData() {
		return fStatus.getAsStateData();
	}

	public void reportProgress(ITestArtefact artefact) {
		fPartnerTrack.reportProgress(artefact);
	}

	// *********************** Other ***************************

	@Override
	public String toString() {
		return getName();
	}

}
