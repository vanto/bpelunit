package org.bpelunit.framework.coverage.annotation.metrics.branchcoverage.impl;

import static org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools.*;

import java.util.List;

import org.bpelunit.framework.coverage.annotation.metrics.branchcoverage.BranchMetricHandler;
import org.bpelunit.framework.coverage.annotation.metrics.branchcoverage.IStructuredActivityHandler;
import org.bpelunit.framework.coverage.exceptions.BpelException;
import org.bpelunit.framework.coverage.receiver.MarkersRegisterForArchive;
import org.jdom.Element;


/**
 * Handler, der die Instrumentierung der
 * switch-Aktivit�ten f�r die Zweigabdeckung �bernehmen.
 * 
 * @author Alex Salnikow
 */

public class SwitchHandler implements IStructuredActivityHandler {
	private MarkersRegisterForArchive markersRegistry;

	public SwitchHandler(MarkersRegisterForArchive markersRegistry) {
		this.markersRegistry = markersRegistry;
	}

	/**
	 * F�gt Markierungen, die sp�ter durch Invoke-Aufrufe protokolliert werden,
	 * um die Ausf�hrung der Zweige zu erfassen.
	 * 
	 * @param structured_activity
	 * @throws BpelException
	 */
	public void insertBranchMarkers(Element structured_activity)
			throws BpelException {
		List case_branches = structured_activity.getChildren(SWITCH_CASE_ELEMENT,
				getProcessNamespace());
		for (int i = 0; i < case_branches.size(); i++) {
			insertMarkerForCaseBranches(getFirstEnclosedActivity((Element) case_branches
					.get(i)));
		}
		Element otherwiseElement = structured_activity
				.getChild(SWITCH_OTHERWISE_ELEMENT, getProcessNamespace());
		if (otherwiseElement == null) {
			otherwiseElement = insertOtherwiseBranch(structured_activity);
			otherwiseElement.addContent(createSequence());
		}
		insertMarkerForOtherwiseBranch(otherwiseElement);
	}

	/**
	 * 
	 * @param branch_activity
	 *            Aktivit�t aus dem Else-Zweig.
	 * @throws BpelException
	 *             Wenn keine Aktivit�t in dem Zweig vorhanden ist.
	 */
	private void insertMarkerForOtherwiseBranch(Element otherwiseElement)
			throws BpelException {
		Element branch_activity = getFirstEnclosedActivity(otherwiseElement);
		if (branch_activity == null)
			throw new BpelException(BpelException.MISSING_REQUIRED_ACTIVITY);
		
		markersRegistry.registerMarker(BranchMetricHandler.insertLabelBevorAllActivities(branch_activity));

	}

	/**
	 * 
	 * @param branch_activity
	 *            Aktivit�t aus dem ElseIf-Zweig.
	 * @throws BpelException
	 *             Wenn keine Aktivit�t in dem Zweig vorhanden ist.
	 */
	private void insertMarkerForCaseBranches(Element branch_activity)
			throws BpelException {
		if (branch_activity == null)
			throw new BpelException(BpelException.MISSING_REQUIRED_ACTIVITY);
		
		markersRegistry.registerMarker(BranchMetricHandler.insertLabelBevorAllActivities(branch_activity));
	}

}
