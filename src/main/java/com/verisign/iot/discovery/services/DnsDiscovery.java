package com.verisign.iot.discovery.services;

import com.verisign.iot.discovery.domain.*;
import com.verisign.iot.discovery.exceptions.ConfigurationException;
import com.verisign.iot.discovery.exceptions.LookupException;

import java.util.Set;

/**
 * Define a set of facilities to discover Services registered on DNS. This interface can be
 * considered as a DAO for DNS Service Discovery, using DNS as Data Source.
 *
 * @author pmaresca <pmaresca@verisign.com>
 * @version 1.0
 * @see <a href="https://tools.ietf.org/html/rfc6763">DNS-SD</a>
 * @since 2015/05/02
 */
public interface DnsDiscovery extends DnsSecChecker {

	/**
	 * List the Service Types under the provided domain.
	 *
	 * @param browsingDomain A <code>Fqdn</code> referring the browsing domain
	 * @param secValidation  <code>true</code> iff DNSSEC trust chain has to be validated
	 * @return A set of <code>String</code> referring the registered Service Types
	 * @throws LookupException        In case of any unrecoverable error during the lookup process.
	 * @throws ConfigurationException In case of wrong/faulty static and/or runtime configuration.
	 */
	Set<String> listServiceTypes ( Fqdn browsingDomain, boolean secValidation ) throws LookupException, ConfigurationException;

	/**
	 * List the Service Instances under the provided domain, of the defined <i>type</i>.
	 *
	 * @param browsingDomain A <code>Fqdn</code> referring the browsing domain
	 * @param type           A <code>String</code> identifying the Service Type
	 * @return A set of <code>ServiceInstance</code> objects
	 * @throws LookupException        In case of any unrecoverable error during the lookup process.
	 * @throws ConfigurationException In case of wrong/faulty static and/or runtime configuration.
	 */
	Set<ServiceInstance> listServiceInstances ( Fqdn browsingDomain, String type, boolean secValidation ) throws LookupException,
			ConfigurationException;

	/**
	 * List the Service Resource Records under the provided domain, of the defined <i>type</i>.
	 *
	 * @param browsingDomain A <code>Fqdn</code> referring the browsing domain
	 * @param type           A <code>String</code> identifying the Service Type
	 * @return A set of <code>ServiceInstance</code> objects
	 * @throws LookupException        In case of any unrecoverable error during the lookup process.
	 * @throws ConfigurationException In case of wrong/faulty static and/or runtime configuration.
	 */
	Set<ServiceRecord> listServiceRecords ( Fqdn browsingDomain, String type, boolean secValidation ) throws LookupException,
			ConfigurationException;

	/**
	 * List the Text Resource Records under the provided domain for the specified <i>label</i>.
	 *
	 * @param browsingDomain A <code>Fqdn</code> referring the browsing domain
	 * @param label          A <code>String</code> identifying the label to be looked up
	 * @return A set of <code>ServiceInstance</code> objects
	 * @throws LookupException        In case of any unrecoverable error during the lookup process.
	 * @throws ConfigurationException In case of wrong/faulty static and/or runtime configuration.
	 */
	Set<TextRecord> listTextRecords ( Fqdn browsingDomain, String label, boolean secValidation ) throws LookupException,
			ConfigurationException;

	/**
	 * List the TLSA Resource Records under the provided domain for the specified <i>label</i>.
	 *
	 * @param browsingDomain A <code>Fqdn</code> referring the browsing domain
	 * @param label          A <code>String</code> identifying the label to be looked up
	 * @return A set of <code>TLSADiscoveryRecord</code> objects
	 * @throws LookupException        In case of any unrecoverable error during the lookup process.
	 * @throws ConfigurationException In case of wrong/faulty static and/or runtime configuration.
	 */
	Set<TLSADiscoveryRecord> listTLSARecords ( Fqdn browsingDomain, String label, boolean secValidation ) throws LookupException,
			ConfigurationException;
}
