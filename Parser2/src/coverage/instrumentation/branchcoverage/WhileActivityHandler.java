package coverage.instrumentation.branchcoverage;

import org.jdom.Element;

import coverage.instrumentation.bpelxmltools.BpelXMLTools;
import coverage.instrumentation.exception.BpelException;

public class WhileActivityHandler implements IStructuredActivity {

	public void insertMarkerForBranchCoverage(Element element) throws BpelException {
		Element activity=BpelXMLTools.getFirstActivityChild(element);
		if (activity == null) {
			throw new BpelException(BpelException.MISSING_REQUIRED_ACTIVITY);
		}
		BranchMetric.insertMarkerForBranch(activity, "");
	}

}
