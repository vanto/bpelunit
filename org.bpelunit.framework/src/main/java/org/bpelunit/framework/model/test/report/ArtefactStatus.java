/**
 * This file belongs to the BPELUnit utility and Eclipse plugin set. See enclosed
 * license file for more information.
 * 
 */
package org.bpelunit.framework.model.test.report;

import java.util.ArrayList;
import java.util.List;

/**
 * The ArtefactStatus is a representation of the current status of any BPELUnit artefact. It
 * contains a status code, message, and possible exception information.
 * 
 * @version $Id$
 * @author Philip Mayer
 * 
 */
public class ArtefactStatus {

	public enum StatusCode {
		NOTYETSPECIFIED, INPROGRESS, ABORTED, PASSED, FAILED, ERROR
	}

	/**
	 * The actual code
	 */
	private StatusCode fCode;

	/**
	 * Message of this status
	 */
	private String fMessage;

	/**
	 * Exception (if any).
	 */
	private Exception fException;


	// ******************** Factory methods ***********************

	public static ArtefactStatus createInitialStatus() {
		return new ArtefactStatus(StatusCode.NOTYETSPECIFIED, "Not yet specified.", null);
	}

	public static ArtefactStatus createPassedStatus() {
		return new ArtefactStatus(StatusCode.PASSED, "Passed", null);
	}

	public static ArtefactStatus createFailedStatus(String message) {
		return new ArtefactStatus(StatusCode.FAILED, message, null);
	}

	public static ArtefactStatus createErrorStatus(String message) {
		return createErrorStatus(message, null);
	}

	public static ArtefactStatus createErrorStatus(String message, Exception e) {
		return new ArtefactStatus(StatusCode.ERROR, message, e);
	}

	public static ArtefactStatus createAbortedStatus(String message, Exception e) {
		return new ArtefactStatus(StatusCode.ABORTED, message, e);
	}

	public static ArtefactStatus createAbortedStatus(String message) {
		return new ArtefactStatus(StatusCode.ABORTED, message, null);
	}

	public static ArtefactStatus createInProgressStatus() {
		return new ArtefactStatus(StatusCode.INPROGRESS, "In progress.", null);
	}


	private ArtefactStatus(StatusCode error, String string, Exception e) {
		fCode= error;
		fMessage= string;
		fException= e;
	}

	// ******************** Convenience getters ***********************

	public boolean hasProblems() {
		return fCode == StatusCode.ABORTED || fCode == StatusCode.FAILED || fCode == StatusCode.ERROR;
	}

	public boolean isError() {
		return fCode == StatusCode.ERROR;
	}

	public boolean isFailure() {
		return fCode == StatusCode.FAILED;
	}

	public boolean isInitial() {
		return fCode == StatusCode.NOTYETSPECIFIED;
	}

	public boolean isAborted() {
		return fCode == StatusCode.ABORTED;
	}

	public boolean isPassed() {
		return fCode == StatusCode.PASSED;
	}

	public boolean isInProgress() {
		return fCode == StatusCode.INPROGRESS;
	}

	// ******************** Other tools ***********************

	public StatusCode getCode() {
		return fCode;
	}

	public String getMessage() {
		return fMessage;
	}

	public String getExceptionMessage() {
		if (fException != null)
			return fException.getMessage();
		else
			return null;
	}

	public List<StateData> getAsStateData() {
		List<StateData> stateData= new ArrayList<StateData>();
		stateData.add(new StateData("Status Code", fCode.name()));
		stateData.add(new StateData("Status Message", fMessage));
		if (fException != null)
			stateData.add(new StateData("Exception", fException.getMessage()));
		return stateData;
	}

	@Override
	public String toString() {
		StringBuffer toString= new StringBuffer();
		toString.append("Status { Code => \"" + fCode.name() + "\", ");
		toString.append("Message => \"" + fMessage + "\"");
		if (fException != null)
			toString.append(", Exception => \"" + fException.getMessage() + "\"");
		toString.append(" }");
		return toString.toString();
	}

}
