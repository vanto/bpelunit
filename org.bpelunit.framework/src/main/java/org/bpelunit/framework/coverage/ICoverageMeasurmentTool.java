package org.bpelunit.framework.coverage;

import java.util.List;
import java.util.Map;

import org.bpelunit.framework.control.ext.IBPELDeployer;
import org.bpelunit.framework.control.ext.ISOAPEncoder;
import org.bpelunit.framework.coverage.exceptions.CoverageMeasurmentException;
import org.bpelunit.framework.coverage.result.statistic.IFileStatistic;
import org.bpelunit.framework.exception.ConfigurationException;

/**
 * Die Schnittstelle zum Einbinden der Testabdeckungsmessung.
 * 
 * @author Alex Salnikow
 *
 */
public interface ICoverageMeasurmentTool {

	/**
	 * Methode zum Konfigurieren der Metrike, die erhoben werdfen sollen.
	 * 
	 * @param configMap
	 *            Map (Metrikname,Liste), wobei Liste entweder
	 *            Zusatzinformationen enth�lt oder gleich null. Bei
	 *            Aktivit�tsabdeckung sind die Basisaktivit�ten, die
	 *            ber�cksichtigt werden sollen, in der Liste eingetragen.
	 * @throws ConfigurationException
	 */
	public abstract void configureMetrics(Map<String, List<String>> configMap)
			throws ConfigurationException;

	/**
	 * 
	 * @param encoder
	 *            sSOAPEncoder f�r die Dekodierung der Nachrichten mit
	 *            Coverage-Marken
	 */
	public abstract void setSOAPEncoder(ISOAPEncoder encoder);

	/**
	 * 
	 * @param wsdl
	 *            WSDL-Beschreibung des Coverage Logging Services
	 */
	public abstract void setPathToWSDL(String wsdl);

	/**
	 * 
	 * @return Encoding Style der Coverage-Nachrichten
	 */
	public abstract String getEncodingStyle();

	/**
	 * Prepariert das Deploymentarchive f�r die Messung der Abdeckung beim
	 * Testen des BPEL-Prozesses. Die Instrumentierung wird auf einer Kopie
	 * durchgef�hrt.
	 * 
	 * @param pathToArchive
	 * @param archiveFile
	 * @param deployer
	 * @return Name der preparierten Archivedatei
	 * @throws CoverageMeasurmentException
	 */
	public abstract String prepareArchiveForCoverageMeasurement(
			String pathToArchive, String archiveFile, IBPELDeployer deployer)
			throws CoverageMeasurmentException;

	public abstract void setErrorStatus(String message);

	/**
	 * Setzt den Testfall, der gerade ausgef�hrt wird. Dadurch ist es m�glich,
	 * die Testabdeckung von jedem Testfalls zu bestimmen.
	 * 
	 * @param testCase
	 *            Testfall, der gerade ausgef�hrt wird.
	 */
	public abstract void setCurrentTestCase(String testCase);

	/**
	 * Empf�ngt SOAP-Nachrichten mit Coverage Marken w�hrend der Testausf�hrung
	 * 
	 * @param body
	 *            Nachricht mit Coverage-Marken
	 */
	public abstract void putMessage(String body);

	/**
	 * Generiert Statistiken (nach dem Testlauf) f�r alle BPEL-Dateien, die im
	 * Archive sind.
	 * 
	 * @return Statistiken (nach dem Testlauf) f�r alle BPEL-Dateien, die im
	 *         Archive sind.
	 */
	public abstract List<IFileStatistic> getStatistics();

	/**
	 * 
	 * @return gibt Informationen in einem Fehlerfall
	 */
	public abstract String getErrorStatus();

}