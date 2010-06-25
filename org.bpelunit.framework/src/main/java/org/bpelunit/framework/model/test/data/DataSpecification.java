/**
 * This file belongs to the BPELUnit utility and Eclipse plugin set. See enclosed
 * license file for more information.
 * 
 */
package org.bpelunit.framework.model.test.data;

import org.bpelunit.framework.exception.SpecificationException;
import org.bpelunit.framework.model.test.activity.Activity;
import org.bpelunit.framework.model.test.report.ArtefactStatus;
import org.bpelunit.framework.model.test.report.ITestArtefact;

/**
 * Abstract superclass of the two data specification packages Send and Receive.
 * 
 * @version $Id$
 * @author Philip Mayer
 * 
 */
public abstract class DataSpecification implements ITestArtefact {

	/**
	 * Next parent activity this data specification belongs to.
	 */
	private Activity fActivity;

	/**
	 * Status of this object.
	 */
	protected ArtefactStatus fStatus;


	// ********************** Initialization ***************************


	public DataSpecification(Activity parent) throws SpecificationException {
		fActivity= parent;
		fStatus= ArtefactStatus.createInitialStatus();
	}

	// ************************** ITestArtefact ************************


	public ITestArtefact getParent() {
		return fActivity;
	}

	public ArtefactStatus getStatus() {
		return fStatus;
	}

	public void reportProgress(ITestArtefact artefact) {
		fActivity.reportProgress(artefact);
	}

	// ***************************** Other ******************************

	public boolean hasProblems() {
		return fStatus.hasProblems();
	}
}
