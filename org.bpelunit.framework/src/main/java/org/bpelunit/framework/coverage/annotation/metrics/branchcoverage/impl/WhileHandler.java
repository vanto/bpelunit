package org.bpelunit.framework.coverage.annotation.metrics.branchcoverage.impl;

import static org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools.getFirstEnclosedActivity;

import org.bpelunit.framework.coverage.annotation.metrics.branchcoverage.BranchMetricHandler;
import org.bpelunit.framework.coverage.annotation.metrics.branchcoverage.IStructuredActivityHandler;
import org.bpelunit.framework.coverage.exceptions.BpelException;
import org.bpelunit.framework.coverage.receiver.MarkersRegisterForArchive;
import org.jdom.Element;


/**
 * Handler, der die Instrumentierung der
 * while-Aktivit�ten f�r die Zweigabdeckung �bernehmen.
 * 
 * @author Alex Salnikow
 */

public class WhileHandler implements IStructuredActivityHandler {

	private MarkersRegisterForArchive markersRegistry;

	public WhileHandler(MarkersRegisterForArchive markersRegistry) {
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
		Element activity = getFirstEnclosedActivity(structured_activity);
		if (activity == null)
			throw new BpelException(BpelException.MISSING_REQUIRED_ACTIVITY);
		
		markersRegistry.registerMarker(BranchMetricHandler.insertLabelBevorAllActivities(activity));
		markersRegistry.registerMarker(BranchMetricHandler.insertLabelAfterAllActivities(activity));
	}

}
