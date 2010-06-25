package org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.deploy.archivetools;


import java.io.File;
import java.util.Set;

import org.bpelunit.framework.coverage.exceptions.ArchiveFileException;
import org.bpelunit.framework.coverage.exceptions.BpelException;
import org.jdom.Document;

/**
 * Interface dient als Schnittstelle f�r den Zugriff auf die (deployment)
 * Archivedateien, die spezifisch f�r die BPELEngine sind.
 * 
 * @author Alex Salnikow
 * 
 */

public interface IDeploymentArchiveHandler {

	/**
	 * Erzeugt eine Kopie des Archivs, auf der die Instrumentierung durchgef�hrt wird.
	 * 
	 * @param archiv Deploymentarchive
	 * @return ArchivCopy
	 * @throws ArchiveFileException
	 */
	public String createArchivecopy(String archiv) throws ArchiveFileException;

	/**
	 * 
	 * @return die Namen aller BPEL-Dateien, die im Archiv enthalten sind.
	 */
	public Set<String> getAllBPELFileNames();

	/**
	 * 
	 * @param fileName Name der BPEL-Datei
	 * @return BPEL-Prozessbeschreibung als XML-Dokumnet
	 * @throws BpelException
	 */
	public Document getDocument(String fileName) throws BpelException;

	/**
	 * Schreibt den BPEL-Prozess in Form eines XML-Dokumentes in den Archiv
	 * @param doc BPEL-Prozess als XML-Dokument
	 * @param fileName Name der BPEL-Datei
	 * @throws ArchiveFileException
	 */
	public void writeDocument(Document doc, String fileName) throws ArchiveFileException;

	/**
	 * F�gt in den Archive WSDL-Datei und registriert sie, falls n�tig
	 * @param wsdlFile
	 * @throws ArchiveFileException
	 */
	public void addWSDLFile(File wsdlFile) throws ArchiveFileException;

	/**
	 * Gibt die reservierten Ressourcen (Streams) frei. 
	 *
	 */
	public void closeArchive();

}
