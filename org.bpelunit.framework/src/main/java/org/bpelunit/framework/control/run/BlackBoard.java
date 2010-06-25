/**
 * This file belongs to the BPELUnit utility and Eclipse plugin set. See enclosed
 * license file for more information.
 * 
 */
package org.bpelunit.framework.control.run;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.bpelunit.framework.BPELUnitRunner;
import org.bpelunit.framework.control.util.BPELUnitConstants;

/**
 * Class used for thread communication in the test runner.
 * 
 * @version $Id$
 * @author Philip Mayer
 * 
 * @param <KEY>
 * @param <OBJECT>
 */
public class BlackBoard<KEY, OBJECT> {

	private Map<KEY, OBJECT> map;

	public BlackBoard() {
		map= new HashMap<KEY, OBJECT>();
	}

	public synchronized void putObject(KEY key, OBJECT object) throws InterruptedException {

		while (map.containsKey(key)) {
			wait(BPELUnitConstants.TIMEOUT_SLEEP_TIME);
		}
		map.put(key, object);
		notifyAll();
	}

	public synchronized OBJECT getObject(KEY key) throws TimeoutException /*, InterruptedException*/ {

		int timeout= 0;

		while ( (!map.containsKey(key) && (timeout < BPELUnitRunner.getTimeout()))) {
			timeout+= BPELUnitConstants.TIMEOUT_SLEEP_TIME;
			try {
			wait(BPELUnitConstants.TIMEOUT_SLEEP_TIME);
			} catch(InterruptedException e) {
				// If we are interrupted we still need to wait whether a message arrives
			}
		}

		if (timeout >= BPELUnitRunner.getTimeout()) {
			throw new TimeoutException("Waiting for object for key " + key + " took too long.");
		}

		OBJECT object= map.get(key);
		map.remove(key);

		notifyAll();
		return object;
	}

}
