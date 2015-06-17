
package com.verisign.iot.discovery.services;

import com.verisign.iot.discovery.domain.Fqdn;
import com.verisign.iot.discovery.exceptions.ConfigurationException;
import com.verisign.iot.discovery.exceptions.LookupException;

/**
 * Abstraction of DNSSEC-related facilities to deal with cryptographic secure DNS providers.
 * 
 * @author pmaresca <pmaresca@verisign.com>
 * @version 1.0
 * @since 2015/05/02
 */
public interface DnsSecChecker 
{

	/**
	 * Check whether the addressed DNS is secured by DNSSEC.
	 * 
	 * @see <a href="https://tools.ietf.org/html/rfc4035">DNSSEC</a>
	 * @param name  Fully Qualified Domain Name
	 * 
     * @return <code>true</code> iff the DNS is secured by DNSSEC
	 * 
     * @throws LookupException
	 *          In case DNSSEC is broken
	 * @throws ConfigurationException
	 *          Unproper setup for DNS Lookups
	 */
	boolean isDnsSecValid ( Fqdn name ) throws LookupException, ConfigurationException;

}
