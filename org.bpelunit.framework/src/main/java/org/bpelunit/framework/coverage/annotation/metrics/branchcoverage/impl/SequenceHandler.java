package org.bpelunit.framework.coverage.annotation.metrics.branchcoverage.impl;

import static org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools.getProcessNamespace;
import static org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools.isActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bpelunit.framework.coverage.annotation.metrics.branchcoverage.BranchMetricHandler;
import org.bpelunit.framework.coverage.annotation.metrics.branchcoverage.IStructuredActivityHandler;
import org.bpelunit.framework.coverage.exceptions.BpelException;
import org.bpelunit.framework.coverage.receiver.MarkersRegisterForArchive;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

/**
 * Handler, der die Instrumentierung der
 * sequence-Aktivit�ten f�r die Zweigabdeckung �bernehmen.
 * 
 * @author Alex Salnikow
 */
public class SequenceHandler implements IStructuredActivityHandler {

	private MarkersRegisterForArchive markersRegistry;

	public SequenceHandler(MarkersRegisterForArchive markersRegistry) {
		this.markersRegistry = markersRegistry;
	}

	/**
	 * F�gt Markierungen, die sp�ter durch Invoke-Aufrufe protokolliert werden,
	 * um die Ausf�hrung der Zweige zu erfassen.
	 * 
	 * @param structured_activity
	 * @throws BpelException
	 */
	public void insertBranchMarkers(Element structured_activity) {
		List<Element> children = structured_activity.getContent(new ElementFilter(
				getProcessNamespace()));
		Element child;
		List<Element> activities = new ArrayList<Element>();
		for (Iterator<Element> iter = children.iterator(); iter.hasNext();) {
			activities.add(iter.next());
		}

		Element previousActivity = null;
		for (int i = 0; i < activities.size(); i++) {
			child = activities.get(i);
			if (isActivity(child)) {
				if (previousActivity != null)
					markersRegistry.registerMarker(BranchMetricHandler
							.insertLabelBevorActivity(child));

				previousActivity = child;
			}
		}
	}
}
