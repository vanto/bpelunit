package org.bpelunit.framework.coverage.annotation.tools.bpelxmltools;

import static org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools.NAMESPACE_BPEL_1_1;
import static org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.BpelXMLTools.NAMESPACE_BPEL_2_0;

import java.util.Hashtable;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Die Klasse repr�sentiert die Basisaktivit�ten.
 * 
 * @author Alex Salnikow
 *
 */
public class BasicActivities {

	public static final String INVOKE_ACTIVITY = "invoke";

	public static final String RECEIVE_ACTIVITY = "receive";

	public static final String REPLY_ACTIVITY = "reply";

	public static final String THROW_ACTIVITY = "throw";

	public static final String RETHROW_ACTIVITY = "rethrow";

	public static final String WAIT_ACTIVITY = "wait";

	public static final String ASSIGN_ACTIVITY = "assign";

	public static final String EMPTY_ACTIVITY = "empty";

	public static final String COMPENSATE_ACTIVITY = "compensate";

	public static final String COMPENSATESCOPE_ACTIVITY = "compensateScope";

	public static final String EXIT_ACTIVITY = "exit";

	public static final String TERMINATE_ACTIVITY = "terminate";

	public static final String VALIDATE_ACTIVITY = "validate";

	private static Hashtable<String, String> basis_activities;


	/**
	 * �berpr�ft, ob das Element eine BPEL-BasicActivit�t repr�sentiert.
	 * 
	 * @param element
	 * @return ist BasicActivit�t
	 */
	public static boolean isBasisActivity(Element element) {
		return isBasisActivity(element.getName());
	}

	public static boolean isBasisActivity(String name) {
		return basis_activities.containsKey(name) ? true : false;
	}

	/**
	 * Legt die Aktivit�ten fest, abh�ngig von der BPEL-Version (1.1 oder 2.0)
	 *
	 */
	public static void initialize() {
		Namespace bpelNamespace = BpelXMLTools.getProcessNamespace();
		if (bpelNamespace.equals(NAMESPACE_BPEL_2_0)) {
			basis_activities = new Hashtable<String, String>();
			basis_activities.put(INVOKE_ACTIVITY, INVOKE_ACTIVITY);
			basis_activities.put(EXIT_ACTIVITY, EXIT_ACTIVITY);
			basis_activities.put(RECEIVE_ACTIVITY, RECEIVE_ACTIVITY);
			basis_activities.put(REPLY_ACTIVITY, REPLY_ACTIVITY);
			basis_activities.put(THROW_ACTIVITY, THROW_ACTIVITY);
			basis_activities.put(RETHROW_ACTIVITY, RETHROW_ACTIVITY);
			basis_activities.put(WAIT_ACTIVITY, WAIT_ACTIVITY);
			basis_activities.put(ASSIGN_ACTIVITY, ASSIGN_ACTIVITY);
			basis_activities.put(EMPTY_ACTIVITY, EMPTY_ACTIVITY);
			basis_activities.put(COMPENSATE_ACTIVITY, COMPENSATE_ACTIVITY);
			basis_activities.put(VALIDATE_ACTIVITY, VALIDATE_ACTIVITY);
			basis_activities.put(COMPENSATESCOPE_ACTIVITY,
					COMPENSATESCOPE_ACTIVITY);
		} else if (bpelNamespace.equals(NAMESPACE_BPEL_1_1)) {
			basis_activities = new Hashtable<String, String>();
			basis_activities.put(INVOKE_ACTIVITY, INVOKE_ACTIVITY);
			basis_activities.put(RECEIVE_ACTIVITY, RECEIVE_ACTIVITY);
			basis_activities.put(REPLY_ACTIVITY, REPLY_ACTIVITY);
			basis_activities.put(THROW_ACTIVITY, THROW_ACTIVITY);
			basis_activities.put(WAIT_ACTIVITY, WAIT_ACTIVITY);
			basis_activities.put(ASSIGN_ACTIVITY, ASSIGN_ACTIVITY);
			basis_activities.put(EMPTY_ACTIVITY, EMPTY_ACTIVITY);
			basis_activities.put(COMPENSATE_ACTIVITY, COMPENSATE_ACTIVITY);
			basis_activities.put(TERMINATE_ACTIVITY, TERMINATE_ACTIVITY);
		}

	}

}
