package org.bpelunit.framework.coverage.annotation.metrics;

import java.util.List;

import org.bpelunit.framework.coverage.exceptions.BpelException;
import org.jdom.Element;

/*
 * Die Schnittstelle ist f�r Handler vorgesehen, die die Instrumentierung f�r die Metriken �bernehmen.
 * @author Alex
 *
 */
/**
 * This interface is for handlers instrumenting the metrics
 * 
 * @author Alex, Ronald Becher
 */
public interface IMetricHandler {

	/*
	 * F�gt die Marker an den richtigen Stellen in BPEL-Process-Element ein
	 * (Instrumentierung). Anhand dieser Marker werden danach entsprechende
	 * Invoke aufrufe generiert und dadurch die Ausf�hrung bestimmter
	 * Aktivit�ten geloggt.
	 * 
	 * @param process_elements
	 * 
	 * @throws BpelException
	 */
	/**
	 * Inserts the markers at the correct place in a bpel process element.
	 * 
	 * <br />According to those markers the associated invoke calls will be
	 * generated, logging the processing of certain activities
	 * 
	 * @param processElements
	 * @throws BpelException
	 */
	public void insertMarkersForMetric(List<Element> processElements)
			throws BpelException;

}
