package org.bpelunit.framework.coverage.annotation.metrics.branchcoverage.impl;

import static org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools.ELSE_ELEMENT;
import static org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools.ELSE_IF_ELEMENT;
import static org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools.createSequence;
import static org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools.getFirstEnclosedActivity;
import static org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools.getProcessNamespace;
import static org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools.insertElseBranch;

import java.util.List;

import org.bpelunit.framework.coverage.annotation.metrics.branchcoverage.BranchMetricHandler;
import org.bpelunit.framework.coverage.annotation.metrics.branchcoverage.IStructuredActivityHandler;
import org.bpelunit.framework.coverage.exceptions.BpelException;
import org.bpelunit.framework.coverage.receiver.MarkersRegisterForArchive;
import org.jdom.Element;


/**
 * Handler, der die Instrumentierung der
 * if-Aktivit�ten f�r die Zweigabdeckung �bernehmen.
 * 
 * @author Alex Salnikow
 */
public class IfHandler implements IStructuredActivityHandler {

	private MarkersRegisterForArchive markersRegistry;

	public IfHandler(MarkersRegisterForArchive markersRegistry) {
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
		insertMarkerForIfBranch(getFirstEnclosedActivity(structured_activity));
		List elseif_branches = structured_activity.getChildren(ELSE_IF_ELEMENT,
				getProcessNamespace());
		for (int i = 0; i < elseif_branches.size(); i++) {
			insertMarkerForElseIfBranches(getFirstEnclosedActivity((Element) elseif_branches
					.get(i)));
		}
		Element elseElement = structured_activity.getChild(ELSE_ELEMENT, getProcessNamespace());
		if (elseElement == null) {
			elseElement = insertElseBranch(structured_activity);
			elseElement.addContent(createSequence());
		}
		insertMarkerForElseBranch(getFirstEnclosedActivity(elseElement));
	}

	/**
	 * 
	 * @param branch_activity
	 *            Aktivit�t aus dem Else-Zweig.
	 * @throws BpelException
	 *             Wenn keine Aktivit�t in dem Zweig vorhanden ist.
	 */
	private void insertMarkerForElseBranch(Element branch_activity)
			throws BpelException {
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
	private void insertMarkerForElseIfBranches(Element branch_activity)
			throws BpelException {
		if (branch_activity == null)
			throw new BpelException(BpelException.MISSING_REQUIRED_ACTIVITY);
			
		markersRegistry.registerMarker(BranchMetricHandler.insertLabelBevorAllActivities(branch_activity));
	}

	/**
	 * 
	 * @param branch_activity
	 *            Aktivit�t aus dem If-Zweig.
	 * @throws BpelException
	 *             Wenn keine Aktivit�t in dem Zweig vorhanden ist.
	 */
	private void insertMarkerForIfBranch(Element branch_activity)
			throws BpelException {
		if (branch_activity == null)
			throw new BpelException(BpelException.MISSING_REQUIRED_ACTIVITY);

		markersRegistry.registerMarker(BranchMetricHandler.insertLabelBevorAllActivities(branch_activity));

	}

}
