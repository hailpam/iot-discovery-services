package com.verisign.iot.discovery.commons;

import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author pmaresca <pmaresca@verisign.com>
 */
public class StatusChangeEventTest {

	public StatusChangeEventTest() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void rowColumnFormatting() {
		List<String> results = new LinkedList<>();
		results.add("Test result");
		results.add("Test result1");
		StatusChangeEvent event = StatusChangeEvent.build("_0._tcp.test.com", "PTR", results);
		System.out.println(event.rowFormatted());

		Assert.assertTrue(!event.columnFormatted().isEmpty());
		Assert.assertTrue(!event.rowFormatted().isEmpty());
	}
}
