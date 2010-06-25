package org.bpelunit.framework.coverage.annotation.tools.bpelxmltools.exprlang;





public class XpathLanguage extends ExpressionLanguage {

	public static final String LANGUAGE_SPEZIFIKATION = "urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0";

	public String negateExpression(String expression) {

		return "not(" + expression + ")";
	}

	public  String valueOf(String string) {
		return "$" + string;
	}

	public  String concat(String[] strings) {
		StringBuffer concat = new StringBuffer("concat(");
		for (int i = 0; i < strings.length; i++) {
			if (i > 0)
				concat.append(',');
			concat.append(strings[i]);
		}
		concat.append(")");
		return concat.toString();
	}

	@Override
	public String getLanguageSpecification() {
		return LANGUAGE_SPEZIFIKATION;
	}

}
