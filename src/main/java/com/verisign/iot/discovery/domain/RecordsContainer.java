
package com.verisign.iot.discovery.domain;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Container of records.
 *
 * @author pmaresca <pmaresca@verisign.com>
 * @since 1.0
 * @version May 2, 2015
 */
public class RecordsContainer {

	/** A set of <code>String</code> containing generic labels. */
	private final Set<String> labels;
	/** A set of <code>TextRecord</code> containing Service Discovery records. */
	private final Set<TextRecord> texts;
	/** A set of <code>ServiceRecord</code> containing Service Discovery records. */
	private final Set<ServiceRecord> records;


	public RecordsContainer () {
		this.labels = new LinkedHashSet<>();
		this.texts = new TreeSet<>();
		this.records = new TreeSet<>();
	}


	public Set<String> getLabels () {
		return this.labels;
	}


	public Set<TextRecord> getTexts () {
		return this.texts;
	}


	public Set<ServiceRecord> getRecords () {
		return this.records;
	}

}
