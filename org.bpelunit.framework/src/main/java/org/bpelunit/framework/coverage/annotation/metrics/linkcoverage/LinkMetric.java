package org.bpelunit.framework.coverage.annotation.metrics.linkcoverage;

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

/**
 * Link Metric
 * @author Alex, Ronald Becher
 *
 */
public class LinkMetric implements IMetric {

	public static final String METRIC_NAME = "LinkCoverage";

	private IMetricHandler metricHandler;

	private List<Element> elementsOfBPEL = null;

	public LinkMetric(MarkersRegisterForArchive markersRegistry) {
		metricHandler = new LinkMetricHandler(markersRegistry);
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
	/**
	 * Gets metric's markers' prefix
	 * 
	 * <br />The prefix allows identification of all metrics' markers
	 * 
	 * @return prefix
	 */
	public List<String> getMarkersId() {
		List<String> list = new ArrayList<String>(2);
		list.add(LinkMetricHandler.POSITIV_LINK_LABEL);
		list.add(LinkMetricHandler.NEGATIV_LINK_LABEL);
		return list;
	}

	public IMetricHandler getHandler() {
		return metricHandler;
	}

	/* (non-Javadoc)
	 * @see org.bpelunit.framework.coverage.annotation.metrics.IMetric#createStatistic(java.util.Hashtable)
	 */
	public IStatistic createStatistic(
			Hashtable<String, Hashtable<String, MarkerState>> allMarkers) {
		IStatistic statistic = new Statistic(METRIC_NAME);
		statistic.addSubStatistic(createSubstatistic(
				LinkMetricHandler.POSITIV_LINK_LABEL, allMarkers));
		statistic.addSubStatistic(createSubstatistic(
				LinkMetricHandler.NEGATIV_LINK_LABEL, allMarkers));
		return statistic;
	}

	private IStatistic createSubstatistic(String name,
			Hashtable<String, Hashtable<String, MarkerState>> allLabels) {
		IStatistic subStatistic;
		subStatistic = new Statistic(METRIC_NAME + ": " + name);
		List<MarkerState> statusListe = MetricsManager.getStatus(name,
				allLabels);
		subStatistic.setStateList(statusListe);
		return subStatistic;
	}

	/*
	 * Erh�lt die noch nicht modifizierte Beschreibung des BPELProzesses als
	 * XML-Element. Alle f�r die Instrumentierung ben�tigten Elemente der
	 * Prozessbeschreibung werden gespeichert
	 * 
	 * @param process
	 *            noch nicht modifiziertes BPEL-Prozess
	 */
	/**
	 * Erh�lt die noch nicht modifizierte Beschreibung des BPELProzesses als
	 * XML-Element. Alle f�r die Instrumentierung ben�tigten Elemente der
	 * Prozessbeschreibung werden gespeichert
	 * 
	 * @param process
	 *            noch nicht modifiziertes BPEL-Prozess
	 */
	public void setOriginalBPELProcess(Element process) {
		elementsOfBPEL = new ArrayList<Element>();
		Iterator<Element> iter = process
				.getDescendants(new ElementFilter(LinkMetricHandler.LINK_TAG,
						process.getNamespace()));
		while (iter.hasNext()) {
			elementsOfBPEL.add(iter.next());
		}

	}

	/*
	 * delegiert die Instrumentierungsaufgabe an eigenen Handler
	 * 
	 * @throws BpelException
	 */
	/**
	 * Delegates the instrumenting to its own handler
	 * 
	 * @throws BpelException
	 */
	public void insertMarkers() throws BpelException {
		if (elementsOfBPEL != null) {
			metricHandler.insertMarkersForMetric(elementsOfBPEL);
			elementsOfBPEL = null;
		}

	}

}
