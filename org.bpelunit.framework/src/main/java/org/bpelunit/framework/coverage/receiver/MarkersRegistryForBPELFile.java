package org.bpelunit.framework.coverage.receiver;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.bpelunit.framework.coverage.annotation.Instrumenter;
import org.bpelunit.framework.coverage.annotation.MetricsManager;
import org.bpelunit.framework.coverage.annotation.metrics.IMetric;
import org.bpelunit.framework.coverage.result.statistic.IFileStatistic;
import org.bpelunit.framework.coverage.result.statistic.impl.FileStatistic;

/*
 * 
 * Die Klasse verwaltet alle eingef�gten Coverage-Marken f�r eine BPEL-Datei.
 * 
 * @author Alex Salnikow
 * 
 */
/**
 * This class manages all included coverage markings for bpel file
 * 
 * @author Alex Salnikow, Ronald Becher
 * 
 */
public class MarkersRegistryForBPELFile {

	private String fileName;

	private Hashtable<String, Hashtable<String, MarkerState>> allMetricsTable;

	private MetricsManager metricManager;

	public MarkersRegistryForBPELFile(String filename,
			MetricsManager metricManager) {
		this.fileName = filename;
		this.metricManager = metricManager;
		prepareStructur(metricManager);
	}

	public String getBPELFileName() {
		return fileName;
	}

	/*
	 * registriert eingef�gte Marken
	 * 
	 * @param marke Coverage Marke
	 * @param status
	 *            Coverage-Marke
	 */
	/**
	 * Registers marker
	 * 
	 * @param Coverage marker
	 * @param marker status
	 */
	public void registerMarker(String marker, MarkerState status) {
		String prefix = marker.substring(0, marker
				.indexOf(Instrumenter.COVERAGE_LABEL_INNER_SEPARATOR));
		allMetricsTable.get(prefix).put(marker, status);
	}

	/*
	 * Initialisiert Datenstruktur f�r die Speicherung der Marken f�r alle
	 * Metriken.
	 * 
	 * @param metricManager
	 */
	/**
	 * Initializes data structure for saving markers for all metrics
	 * 
	 * @param metricsManager
	 */
	private void prepareStructur(MetricsManager metricsManager) {
		List<IMetric> metrics = metricsManager.getMetrics();
		allMetricsTable = new Hashtable<String, Hashtable<String, MarkerState>>();
		for (Iterator<IMetric> iter = metrics.iterator(); iter.hasNext();) {
			IMetric metric = iter.next();
			for (Iterator<String> iterator = metric.getMarkersId().iterator(); iterator
					.hasNext();) {
				allMetricsTable.put(iterator.next(),
						new Hashtable<String, MarkerState>());
			}
		}
	}

	/*
	 * 
	 * @return Statistik der BPEL-Datei
	 */
	/**
	 * Get file statistic
	 * @return bpel file statistics
	 */
	public IFileStatistic getFileStatistic() {
		IFileStatistic fileStatistic = new FileStatistic(fileName);
		fileStatistic.setStatistics(metricManager
				.createStatistics(allMetricsTable));
		return fileStatistic;
	}

}
