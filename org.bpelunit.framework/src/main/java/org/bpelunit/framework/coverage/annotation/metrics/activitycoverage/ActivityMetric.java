package org.bpelunit.framework.coverage.annotation.metrics.activitycoverage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
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
 * Activity Metric class
 * 
 * @author Alex Salnikow, Ronald Becher
 */
public class ActivityMetric implements IMetric {

	private Logger logger=Logger.getLogger(getClass());
	
	public static final String METRIC_NAME = "ActivityCoverage";

	private List<String> activities_to_respekt;

	private IMetricHandler metricHandler;

	private List<Element> elementsOfBPEL = null;

	public ActivityMetric(List<String> activitesToRespect, MarkersRegisterForArchive markersRegistry) {
		activities_to_respekt = new ArrayList<String>();
		if (activitesToRespect != null) {
			for (Iterator<String> iter = activitesToRespect.iterator(); iter
					.hasNext();) {
				String basicActivity = iter.next();
				activities_to_respekt.add(basicActivity);
			}
		}
		metricHandler = new ActivityMetricHandler(markersRegistry);
	}

	/**
	 * 
	 */
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
	 * Delivers prefixes of all metric's markers.
	 * 
	 * <br />They allow the association to received metrics 
	 * 
	 * @return prefixes
	 */
	public List<String> getMarkersId() {
		return activities_to_respekt;
	}


	/*
	 * Erzeugt Statistiken
	 * 
	 * @param allMarkers
	 *            alle einegf�gten Marken (von allen Metriken), nach dem Testen
	 * @return Statistik
	 */
	/**
	 * Generates statistics
	 * 
	 * @param allMarkers
	 *            all inserted markers (after testing)
	 * @return statistic
	 */
	public IStatistic createStatistic(
			Hashtable<String, Hashtable<String, MarkerState>> allMarkers) {
		IStatistic statistic = new Statistic(METRIC_NAME);
		IStatistic subStatistic;
		String label;
		for (Iterator<String> iter = activities_to_respekt.iterator(); iter
				.hasNext();) {
			label = iter.next();
			subStatistic = new Statistic(METRIC_NAME + ": " + label);
			List<MarkerState> statusListe = MetricsManager.getStatus(label,
					allMarkers);
			subStatistic.setStateList(statusListe);
			statistic.addSubStatistic(subStatistic);
		}
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
	/**
	 * Gets the unmodified bpel process as xml element.
	 * 
	 * <br />All elements needed for instrumentating will be saved
	 * 
	 * @param unmodified bpel process
	 */
	public void setOriginalBPELProcess(Element process) {
		ElementFilter filter = new ElementFilter(process.getNamespace());
		elementsOfBPEL = new ArrayList<Element>();
		for (Iterator<Element> iter = process.getDescendants(filter); iter
				.hasNext();) {
			Element basicActivity = iter.next();
			if (activities_to_respekt.contains(basicActivity.getName()))
				elementsOfBPEL.add(basicActivity);
		}	
	}

	/*
	 * delegiert die Instrumentierungsaufgabe an eigenen Handler
	 * 
	 * @throws BpelException
	 */
	/**
	 * Delegates instrumentating to own handler
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
