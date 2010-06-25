/**
 * This file belongs to the BPELUnit utility and Eclipse plugin set. See enclosed
 * license file for more information.
 * 
 */

package org.bpelunit.framework.control.deploy.ode;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bpelunit.framework.control.ext.GenericDeployment;
import org.bpelunit.framework.control.ext.PartnerLink;
import org.bpelunit.framework.control.util.ParseUtil;
import org.bpelunit.framework.coverage.CoverageConstants;
import org.bpelunit.framework.coverage.exceptions.ArchiveFileException;
import org.bpelunit.framework.exception.DeploymentException;
import org.bpelunit.framework.model.Partner;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.schlichtherle.io.File;
import de.schlichtherle.io.FileInputStream;
import de.schlichtherle.io.FileOutputStream;
import de.schlichtherle.io.FileWriter;

/**
 * @author chamith
 * 
 */
public class ODEDeployment extends GenericDeployment {

	private static final String DESCRIPTOR_NS = "http://www.apache.org/ode/schemas/dd/2007/03";
	private static final String DESCRIPTOR = "deploy.xml";
	private static final String PROCESS_ELEMENT = "process";
	private static final String INVOKE_ATTR = "invoke";
	private static final String NAME_ATTR = "name";
	private static final String PARTNERLINK_ATTR = "partnerLink";
	private static final String SERVICE_ATTR = "service";
	private static final String PORT_ATTR = "port";

	private Document fDescriptorDocument;
	private Logger fLogger;
	private String fDescriptorPath;

	public ODEDeployment(Partner[] partners, String archive)
			throws DeploymentException {
		super(partners, archive);
		fLogger = Logger.getLogger(getClass());
		this.fDescriptorDocument = getDescriptorDocument();
	}

	// ************* IDeployment Implementation method ********************
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bpelunit.framework.control.deploy.ode.IDeployment#addLoggingService()
	 */
	public void addLoggingService(String wsdl) throws ArchiveFileException {
		addWSDL(wsdl);
		prepareDeploymentDescriptor();

	}

	/**
	 * This method gets the details of the partnerlinks defined by the process.
	 * Details include the service to which a particular partnerlink map to and
	 * additionally the port within the service, if enough details are present
	 * to determine it by parsing the ODE deployment descriptor deploy.xml.
	 */
	public PartnerLink[] getPartnerLinks() throws DeploymentException {
		ArrayList<PartnerLink> links = new ArrayList<PartnerLink>();
		Element envelope = fDescriptorDocument.getRootElement();

		Iterator<Element> processes = envelope
				.getDescendants(new ElementFilter(PROCESS_ELEMENT));

		while (processes.hasNext()) {
			Element process = processes.next();
			String processName = process.getAttributeValue(NAME_ATTR);

			if (processName.contains(getProcessUnderTest().getName())) {
				Iterator<Element> invokes = process
						.getDescendants(new ElementFilter(INVOKE_ATTR));

				while (invokes.hasNext()) {
					Element invoke = invokes.next();
					Element serviceElement = (Element) invoke.getChildren()
							.iterator().next();
					String serviceName = serviceElement
							.getAttributeValue(NAME_ATTR);
					QName service = extractQName(serviceName, serviceElement);
					String port = serviceElement.getAttributeValue(PORT_ATTR);
					String partnerLink = invoke
							.getAttributeValue(PARTNERLINK_ATTR);

					PartnerLink pl = new PartnerLink(partnerLink, service, port);
					links.add(pl);
				}

				return links.toArray(new PartnerLink[] {});
			}
		}

		throw new DeploymentException("Could not find the process with the specified name in the deployment: " + getProcessUnderTest().getName());
	}

	// ******************** Private helper methods ****************************

	private Document getDescriptorDocument() throws DeploymentException {
		File archive = new File(getArchive());
		Document document = null;

		for (File file : (File[]) archive.listFiles()) {
			if (file.getName().equals(DESCRIPTOR)) {
				try {
					document = ParseUtil
							.getJDOMDocument(file.getAbsolutePath());
					fDescriptorPath = file.getAbsolutePath();
				} catch (IOException e) {
					throw new DeploymentException(
							"Error while reading deployment descriptor from file \""
									+ file.getAbsolutePath() + "\".", e);
				}

				break;
			}
		}

		return document;
	}

	private void addWSDL(String wsdlPath) throws ArchiveFileException {
		java.io.File wsdl = new java.io.File(wsdlPath);
		fLogger.info("CoverageTool: Adding WSDL-file " + wsdl.getPath()
				+ " for CoverageLogging in ode-archive");
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(getArchive() + File.separator
					+ FilenameUtils.getName(wsdl.getAbsolutePath()));
			out.write(FileUtils.readFileToByteArray(wsdl));
			fLogger.info("CoverageTool: WSDL-file sucessfull added.");

		} catch (IOException e) {
			throw new ArchiveFileException(
					"Could not add WSDL file for coverage measurement tool ("
							+ wsdl.getName() + ") in deployment archive ", e);
		} finally {

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void prepareDeploymentDescriptor() throws ArchiveFileException {
		FileInputStream is = null;
		FileWriter writer = null;

		try {
			Element deploy = fDescriptorDocument.getRootElement();
			Iterator<Element> processes = deploy
					.getDescendants(new ElementFilter(PROCESS_ELEMENT));
			deploy
					.addNamespaceDeclaration(CoverageConstants.COVERAGETOOL_NAMESPACE);

			while (processes.hasNext()) {
				Element process = processes.next();
				String processName = process.getAttributeValue(NAME_ATTR);

				if (processName.contains(getProcessUnderTest().getName())) {

					Element invoke = constructInvokeElement();
					process.addContent(invoke);
					// addPartnerLinkEndpoint(process);
					writer = new FileWriter(fDescriptorPath);
					XMLOutputter xmlOutputter = new XMLOutputter(Format
							.getPrettyFormat());
					xmlOutputter.output(fDescriptorDocument, writer);
					break;
				}
			}
		} catch (IOException e) {

			throw new ArchiveFileException(
					"An I/O error occurred when writing deployment descriptor: "
							+ fDescriptorPath, e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					System.out.println("Hello.");
				}
			}
		}
	}

	private Element constructInvokeElement() {
		Element invoke = new Element("invoke", DESCRIPTOR_NS);
		invoke.setAttribute(PARTNERLINK_ATTR,
				CoverageConstants.PARTNERLINK_NAME);
		Element service = new Element(SERVICE_ATTR, DESCRIPTOR_NS);
		String serviceName = CoverageConstants.COVERAGETOOL_NAMESPACE
				.getPrefix()
				+ ":" + CoverageConstants.SERVICE_NAME;
		service.setAttribute(NAME_ATTR, serviceName);
		service.setAttribute(PORT_ATTR, CoverageConstants.PORT_OF_SERVICE);
		invoke.addContent(service);
		return invoke;
	}

	private QName extractQName(String serviceName, Element service) {
		final int NS_URI = 0;
		final int LOCALNAME = 1;

		String tokens[];

		if (serviceName.contains(":")) {
			tokens = serviceName.split(":");

			if (isUri(tokens[NS_URI]) && !isPrefix(tokens[NS_URI], service)) {
				return new QName(tokens[NS_URI], tokens[LOCALNAME]);
			} else {
				String namespace = getPrefixValue(tokens[NS_URI], service);
				return new QName(namespace, tokens[LOCALNAME]);
			}
		}

		return new QName(null, serviceName);
	}

	private boolean isUri(String uriStr) {
		try {
			new URI(uriStr);
		} catch (URISyntaxException e) {
			return false;
		}

		return true;
	}

	private boolean isPrefix(String uriStr, Element service) {

		if (service != null && service.getNamespace(uriStr) != null) {
			return true;
		}
		return false;
	}

	private String getPrefixValue(String prefix, Element service) {

		if (service != null && service.getNamespace(prefix) != null) {
			return service.getNamespace(prefix).getURI();
		}
		return null;
	}

}
