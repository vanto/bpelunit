package org.bpelunit.framework.coverage.annotation.metrics.chcoverage;

import static org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools.*;

import java.util.Iterator;
import java.util.List;

import org.bpelunit.framework.coverage.annotation.Instrumenter;
import org.bpelunit.framework.coverage.annotation.metrics.IMetricHandler;
import org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.StructuredActivities;
import org.bpelunit.framework.coverage.exceptions.BpelException;
import org.bpelunit.framework.coverage.receiver.MarkersRegisterForArchive;
import org.jdom.Comment;
import org.jdom.Element;

public class CompensationMetricHandler implements IMetricHandler {

	public static final String METRIC_NAME = "CompensationHandlerMetric";

	public static final String COMPENS_HANDLER_LABEL = "compHandler";

	/**
	 * Generiert eine eindeutige Markierung.
	 * 
	 * @return eindeutige Markierung
	 */
	public static String getNexMarker() {
		return COMPENS_HANDLER_LABEL
				+ Instrumenter.COVERAGE_LABEL_INNER_SEPARATOR + (count++);
	}

	private MarkersRegisterForArchive markersRegistry;

	public CompensationMetricHandler(MarkersRegisterForArchive markersRegistry) {
		this.markersRegistry = markersRegistry;
	}


	/**
	 * F�gt die Marker an den richtigen Stellen in
	 * BPEL-Process-Element ein (Instrumentierung). Anhand dieser Marker werden
	 * danach entsprechende Invoke aufrufe generiert und dadurch die Ausf�hrung
	 * bestimmter Aktivit�ten geloggt.
	 * 
	 * @param process_elements
	 * @throws BpelException 
	 */
	public void insertMarkersForMetric(List<Element> process_elements)
			throws BpelException {
		Element handler;
		for (Iterator<Element> iter = process_elements.iterator(); iter.hasNext();) {
			handler = iter.next();
			Element activity = getFirstEnclosedActivity(handler);
			if (!activity.getName().equals(
					StructuredActivities.SEQUENCE_ACTIVITY)) {
				activity = encloseInSequence(activity);
			}
			String marker = getNexMarker();
			markersRegistry.registerMarker(marker);
			Comment comment = new Comment(
					Instrumenter.COVERAGE_LABEL_IDENTIFIER + marker);
			activity.addContent(0, comment);
		}
	}

}
