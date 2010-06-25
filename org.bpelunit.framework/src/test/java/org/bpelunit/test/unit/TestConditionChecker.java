/**
 * This file belongs to the BPELUnit utility and Eclipse plugin set. See enclosed
 * license file for more information.
 * 
 */
package org.bpelunit.test.unit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.bpelunit.framework.control.soap.NamespaceContextImpl;
import org.bpelunit.framework.model.test.data.ReceiveCondition;
import org.bpelunit.test.util.TestUtil;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * 
 * Tests the XPath condition checker.
 * 
 * @version $Id: TestConditionChecker.java,v 1.5 2006/07/11 14:27:43 phil Exp $
 * @author Philip Mayer
 * 
 */
public class TestConditionChecker extends SimpleTest {

	private ReceiveCondition eval(String eval, String result) throws Exception {

		Element parent= TestUtil.readLiteralData("/condition/simple.xmlfrag");

		ReceiveCondition c= new ReceiveCondition(null, eval, result);

		NamespaceContextImpl ns= new NamespaceContextImpl();
		ns.setNamespace("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
		ns.setNamespace("b", "http://xmlns.oracle.com/AirlineReservationSync");

		c.evaluate(parent, ns);
		return c;
	}

	@Test
	public void testBooleanOK() throws Exception {
		ReceiveCondition c= eval("b:AirlineReservationSyncProcessResponse/b:result/text()", "'false'");
		assertFalse(c.isError() || c.isFailure());
	}

	@Test
	public void testTextNode() throws Exception {
		ReceiveCondition c= eval("b:AirlineReservationSyncProcessResponse/b:bookingNumber/text()", "'589588'");
		assertFalse(c.isError() || c.isFailure());
	}

	@Test
	public void testAttribute() throws Exception {
		ReceiveCondition c= eval("b:AirlineReservationSyncProcessResponse/b:result/@some", "'Hallo'");
		assertFalse(c.isError() || c.isFailure());
	}

	@Test
	public void testWrongXPath() throws Exception {
		ReceiveCondition c= eval("b:AirlineReservationSyncProcessResponse/b:result@some", "'Hallo'");
		assertTrue(c.isError());
	}

	@Test
	public void testFail() throws Exception {
		ReceiveCondition c= eval("b:AirlineReservationSyncProcessResponse/b:result/@some", "'Lulu'");
		assertTrue(c.isFailure());
	}
}
