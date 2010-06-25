package org.bpelunit.framework.coverage.annotation.metrics.branchcoverage;

import org.bpelunit.framework.coverage.exceptions.BpelException;
import org.jdom.Element;

/**
 * Die Schnittstelle f�r die Handler, die die Instrumentierung der
 * Strukturierten Aktivit�ten f�r die Zweigabdeckung �bernehmen.
 * 
 * @author Alex Salnikow
 */
public interface IStructuredActivityHandler {
	/**
	 * F�gt Markierungen, die sp�ter durch Invoke-Aufrufe protokolliert werden,
	 * um die Ausf�hrung der Zweige zu erfassen.
	 * 
	 * @param structured_activity
	 * @throws BpelException
	 */
	public void insertBranchMarkers(Element structured_activity)
			throws BpelException;
}
