package org.bpelunit.framework.coverage;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bpelunit.framework.control.deploy.activebpel.ActiveBPELDeployer;
import org.bpelunit.framework.control.ext.IBPELDeployer;
import org.bpelunit.framework.control.ext.IDeployment;
import org.bpelunit.framework.control.ext.ISOAPEncoder;
import org.bpelunit.framework.control.util.ParseUtil;
import org.bpelunit.framework.coverage.annotation.Instrumenter;
import org.bpelunit.framework.coverage.annotation.MetricsManager;
import org.bpelunit.framework.coverage.annotation.metrics.IMetric;
import org.bpelunit.framework.coverage.annotation.metrics.activitycoverage.ActivityMetricHandler;
import org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools;
import org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.deploy.archivetools.IDeploymentArchiveHandler;
import org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.deploy.archivetools.impl.ActiveBPELDeploymentArchiveHandler;
import org.bpelunit.framework.coverage.exceptions.ArchiveFileException;
import org.bpelunit.framework.coverage.exceptions.BpelException;
import org.bpelunit.framework.coverage.exceptions.CoverageMeasurementException;
import org.bpelunit.framework.coverage.receiver.CoverageMessageReceiver;
import org.bpelunit.framework.coverage.receiver.MarkersRegisterForArchive;
import org.bpelunit.framework.coverage.result.statistic.IFileStatistic;
import org.bpelunit.framework.exception.ConfigurationException;
import org.bpelunit.framework.exception.DeploymentException;
import org.bpelunit.framework.model.ProcessUnderTest;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

/**
 * Class for integration of test coverage measurement
 * 
 * @author Alex Salnikow, Ronald Becher
 * 
 */
public class CoverageMeasurementTool implements ICoverageMeasurementTool {

	private Logger logger = Logger.getLogger(this.getClass());

	// TODO what is failure?
	private boolean failure;

	private boolean error;

	private CoverageMessageReceiver messageReceiver = null;

	private MarkersRegisterForArchive markersRegistry = null;

	private MetricsManager metricManager;

	private String pathToWSDL = null;

	private String errorStatus = null;

	public CoverageMeasurementTool() {
		metricManager = new MetricsManager();
		markersRegistry = new MarkersRegisterForArchive(metricManager);
		messageReceiver = new CoverageMessageReceiver(markersRegistry);
	}

	// **********************Konfiguration*********************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bpelunit.framework.coverage.ICoverageMeasurementTool#configureMetrics
	 * (java.util.Map)
	 */
	public void configureMetrics(Map<String, List<String>> configMap)
			throws ConfigurationException {
		if (configMap == null) {
			setErrorStatus("Configuration error.");
			throw new ConfigurationException(
					"Coverage metrics are not configured.");
		}
		Iterator<String> iter = configMap.keySet().iterator();
		String key;
		IMetric metric;
		while (iter.hasNext()) {
			key = iter.next();
			metric = MetricsManager.createMetric(key, configMap.get(key),
					markersRegistry);
			if (metric != null)
				metricManager.addMetric(metric);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bpelunit.framework.coverage.ICoverageMeasurementTool#setSOAPEncoder
	 * (org.bpelunit.framework.control.ext.ISOAPEncoder)
	 */
	public void setSOAPEncoder(ISOAPEncoder encoder) {
		messageReceiver.setSOAPEncoder(encoder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bpelunit.framework.coverage.ICoverageMeasurementTool#setPathToWSDL
	 * (java.lang.String)
	 */
	public void setPathToWSDL(String wsdl) {
		logger.info("PATHTOWSDL=" + wsdl);
		pathToWSDL = wsdl;
		messageReceiver.setPathToWSDL(wsdl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bpelunit.framework.coverage.ICoverageMeasurementTool#getEncodingStyle
	 * ()
	 */
	public String getEncodingStyle() {
		return messageReceiver.getEncodingStyle();
	}

	// **********************Instrumentierung********************************

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.bpelunit.framework.coverage.ICoverageMeasurementTool#
	 * prepareArchiveForCoverageMeasurement(java.lang.String, java.lang.String,
	 * org.bpelunit.framework.control.ext.IBPELDeployer)
	 */
	/*
	 * public String prepareArchiveForCoverageMeasurement(String pathToArchive,
	 * String archiveFile, IBPELDeployer deployer) throws
	 * CoverageMeasurementException {
	 * logger.info("Instrumentation is started."); if (pathToWSDL == null) {
	 * setErrorStatus("Path to WSDL file for coverage measurment failure");
	 * throw new CoverageMeasurementException(
	 * "Path to WSDL file for coverage measurment failure"); }
	 * IDeploymentArchiveHandler archiveHandler = null; if (deployer instanceof
	 * ActiveBPELDeployer) { archiveHandler = new
	 * ActiveBPELDeploymentArchiveHandler(); } // else if //point for extention
	 * for other BPEL Engines
	 * 
	 * if (archiveHandler == null) { setErrorStatus(deployer.toString() +
	 * " is by coverage tool not supported"); throw new
	 * CoverageMeasurementException(deployer.toString() +
	 * " is by coverage tool not supported"); } String newArchiveFile =
	 * archiveHandler.createArchivecopy(FilenameUtils .concat(pathToArchive,
	 * archiveFile)); prepareLoggingService(archiveHandler);
	 * executeInstrumentationOfBPEL(archiveHandler);
	 * archiveHandler.closeArchive();
	 * logger.info("Instrumentation is complete."); return newArchiveFile; }
	 */

	public String prepareArchiveForCoverageMeasurement(String archive,
			ProcessUnderTest put, IBPELDeployer deployer)
			throws CoverageMeasurementException {
		logger.info("Instrumentation is started.");
		if (pathToWSDL == null) {
			setErrorStatus("Path to WSDL file for coverage measurment failure");
			throw new CoverageMeasurementException(
					"Path to WSDL file for coverage measurment failure");
		}

		String newArchiveFile = ArchiveUtil.createArchivecopy(archive);
		deployer.setArchiveLocation(newArchiveFile);

		IDeployment deployment = null;
		try {
			deployment = deployer.getDeployment(put);
		} catch (DeploymentException e) {
			throw new CoverageMeasurementException(
					"Failed to get the IDeployment required to handle archive",
					e);
		}

		prepareLoggingService(deployment);
		executeInstrumentationOfBPEL(deployment);
		ArchiveUtil.closeArchives();
		logger.info("Instrumentation is complete.");
		return newArchiveFile;
	}

	/*
	 * Startet die Instrumentierung aller BPEL-Dateien, die im Archive sind.
	 * 
	 * @param archiveHandler
	 * 
	 * @throws BpelException
	 * 
	 * @throws ArchiveFileException
	 */
	/**
	 * Starts instrumenting all BPEL files in archive
	 * 
	 * @param archiveHandler
	 * @throws BpelException
	 * @throws ArchiveFileException
	 */
	/*
	 * private void executeInstrumentationOfBPEL( IDeploymentArchiveHandler
	 * archiveHandler) throws BpelException, ArchiveFileException {
	 * 
	 * Instrumenter instrumenter = new Instrumenter(); Document doc;
	 * BpelXMLTools.count = 0; int count = 0; String bpelFile;
	 * ActivityMetricHandler.targetscount = 0; for (Iterator<String> iter =
	 * archiveHandler.getAllBPELFileNames() .iterator(); iter.hasNext();) {
	 * bpelFile = iter.next(); markersRegistry.addRegistryForFile(bpelFile); doc
	 * = archiveHandler.getDocument(bpelFile); Iterator iter2 =
	 * doc.getDescendants(new ElementFilter("link", doc
	 * .getRootElement().getNamespace())); while (iter2.hasNext()) {
	 * iter2.next(); count++;
	 * 
	 * } doc = instrumenter.insertAnnotations(doc, metricManager);
	 * archiveHandler.writeDocument(doc, bpelFile);
	 * 
	 * logger.info("Instrumentation of BPEL-File " + bpelFile +
	 * " is copmplete.");
	 * 
	 * }
	 * 
	 * }
	 */

	private void executeInstrumentationOfBPEL(IDeployment deployment)
			throws BpelException, ArchiveFileException {
		Instrumenter instrumenter = new Instrumenter();
		Document doc;
		BpelXMLTools.count = 0;
		int count = 0;
		String bpelFile;
		ActivityMetricHandler.targetscount = 0;
		try {
			for (Iterator<String> iter = ArchiveUtil.getBPELFileList(
					deployment.getArchive()).iterator(); iter.hasNext();) {
				bpelFile = iter.next();
				markersRegistry.addRegistryForFile(bpelFile);
				doc = ParseUtil.getJDOMDocument(bpelFile);
				addImports(doc);
				Iterator iter2 = doc.getDescendants(new ElementFilter("link",
						doc.getRootElement().getNamespace()));
				while (iter2.hasNext()) {
					iter2.next();
					count++;

				}
				doc = instrumenter.insertAnnotations(doc, metricManager);
				ParseUtil.writeDocument(doc, bpelFile);

				logger.info("Instrumentation of BPEL-File " + bpelFile
						+ " is copmplete.");

			}
		} catch (IOException e) {
			throw new ArchiveFileException(
					"Instrumentation of bpel archive failed", e);
		}
	}

	private void addImports(Document doc) {
		Element process = doc.getRootElement();
		Element importElem = new Element("import", CoverageConstants.BPEL_NS);
		importElem.setAttribute("namespace",
				CoverageConstants.COVERAGETOOL_NAMESPACE.getURI());
		importElem.setAttribute("location",
				CoverageConstants.COVERAGE_SERVICE_WSDL);
		importElem.setAttribute("importType",
				CoverageConstants.WSDL_IMPORT_TYPE);
		process.addContent(importElem);
	}

	/*
	 * F�gt die WSDL-Datei f�r Service, der die Log-Eintr�ge empf�ngt. Diese
	 * Log-Eintr�ge dokumentieren die Ausf�hrung bestimmter Codeteile.
	 * 
	 * @param archiveHandler
	 * 
	 * @param simulatedUrl
	 * 
	 * @throws ArchiveFileException
	 */
	/**
	 * Registers WSDL file with the service receiving the log entries.
	 * 
	 * <br />Those log entries document execution of certain code parts
	 * 
	 * @param archiveHandler
	 * @param simulatedUrl
	 * @throws ArchiveFileException
	 * @throws ArchiveFileException
	 */
	/*
	 * private void prepareLoggingService(IDeploymentArchiveHandler
	 * archiveHandler) throws ArchiveFileException {
	 * archiveHandler.addWSDLFile(new File(pathToWSDL)); }
	 */

	private void prepareLoggingService(IDeployment deployment)
			throws ArchiveFileException {
		deployment.addLoggingService(pathToWSDL);
	}

	// **********************Testlauf*********************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bpelunit.framework.coverage.ICoverageMeasurementTool#setErrorStatus
	 * (java.lang.String)
	 */
	public void setErrorStatus(String message) {
		logger.info(message);
		errorStatus = message;
		error = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bpelunit.framework.coverage.ICoverageMeasurementTool#setCurrentTestCase
	 * (java.lang.String)
	 */
	public void setCurrentTestCase(String testCase) {
		messageReceiver.setCurrentTestcase(testCase);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bpelunit.framework.coverage.ICoverageMeasurementTool#putMessage(java
	 * .lang.String)
	 */
	public synchronized void putMessage(String body) {
		messageReceiver.putMessage(body);
		notifyAll();
	}

	// **********************Ergebnisse*********************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bpelunit.framework.coverage.ICoverageMeasurementTool#getStatistics()
	 */
	public List<IFileStatistic> getStatistics() {
		if (error) {
			return null;
		}
		return markersRegistry.getStatistics();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bpelunit.framework.coverage.ICoverageMeasurementTool#getStatus()
	 */
	public String getErrorStatus() {
		return errorStatus;
	}
}
