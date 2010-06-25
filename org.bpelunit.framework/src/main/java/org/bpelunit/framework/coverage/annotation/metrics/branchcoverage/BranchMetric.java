package org.bpelunit.framework.coverage.annotation.metrics.branchcoverage;

import static org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools.isStructuredActivity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.bpelunit.framework.coverage.annotation.MetricsManager;
import org.bpelunit.framework.coverage.annotation.metrics.IMetric;
import org.bpelunit.framework.coverage.annotation.metrics.IMetricHandler;
import org.bpelunit.framework.coverage.exceptions.BpelException;
import org.bpelunit.framework.coverage.receiver.MarkerState;
import org.bpelunit.framework.coverage.receiver.MarkersRegisterForArchive;
import org.bpelunit.framework.coverage.result.statistic.IStatistic;
import org.bpelunit.framework.coverage.result.statistic.impl.Statistic;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

public class BranchMetric implements IMetric {

	public static final String METRIC_NAME = "BranchCoverage";

	private IMetricHandler metricHandler;

	private List<Element> elementsOfBPEL = null;

	public BranchMetric(MarkersRegisterForArchive markersRegistry) {
		metricHandler = new BranchMetricHandler(markersRegistry);
	}

	public String getName() {
		return METRIC_NAME;
	}
	
	
	/*
	 * Liefert Pr�fixe von allen Marken dieser Metrik. Sie erm�glichen die
	 * Zuordnung der empfangenen Marken einer Metrik
	 * 
	 * @return Pr�fixe von allen Marken dieser Metrik
	 */
	
	/* (non-Javadoc)
	 * @see org.bpelunit.framework.coverage.annotation.metrics.IMetric#getMarkersId()
	 */
	public List<String> getMarkersId() {
		List<String> list = new ArrayList<String>();
		list.add(BranchMetricHandler.BRANCH_LABEL);
		return list;
	}

	/*
	 * Erzeugt Statistiken
	 * 
	 * @param allMarkers
	 *            alle einegf�gten Marken (von allen Metriken), nach dem Testen
	 * @return Statistik
	 */
	/* (non-Javadoc)
	 * @see org.bpelunit.framework.coverage.annotation.metrics.IMetric#createStatistic(java.util.Hashtable)
	 */
	public IStatistic createStatistic(
			Hashtable<String, Hashtable<String, MarkerState>> allMarkers) {
		IStatistic statistic = new Statistic(METRIC_NAME);
		statistic.setStateList(MetricsManager.getStatus(
				BranchMetricHandler.BRANCH_LABEL, allMarkers));
		return statistic;
	}

	/*
	 * Erh�lt die noch nicht modifizierte Beschreibung des BPELProzesses als
	 * XML-Element. Alle f�r die Instrumentierung ben�tigten Elemente der
	 * Prozessbeschreibung werden gespeichert
	 * 
	 * @param process
	 *            noch nicht modifiziertes BPEL-Prozess
	 */
	/* (non-Javadoc)
	 * @see org.bpelunit.framework.coverage.annotation.metrics.IMetric#setOriginalBPELProcess(org.jdom.Element)
	 */
	public void setOriginalBPELProcess(Element process) {
		Element next_element;
		Iterator<Element> iter = process
				.getDescendants(new ElementFilter(process
						.getNamespace()));
		elementsOfBPEL = new ArrayList<Element>();
		while (iter.hasNext()) {
			next_element = iter.next();
			if (isStructuredActivity(next_element)) {
				elementsOfBPEL.add(next_element);
			}
		}

	}

	/*
	 * delegiert die Instrumentierungsaufgabe an eigenen Handler
	 * 
	 * @throws BpelException
	 */
	/* (non-Javadoc)
	 * @see org.bpelunit.framework.coverage.annotation.metrics.IMetric#insertMarkers()
	 */
	public void insertMarkers() throws BpelException {
		if (elementsOfBPEL != null) {
			metricHandler.insertMarkersForMetric(elementsOfBPEL);
			elementsOfBPEL = null;
		}
	}

}
