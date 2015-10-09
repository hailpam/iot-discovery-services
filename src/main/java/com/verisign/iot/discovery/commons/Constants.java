
package com.verisign.iot.discovery.commons;

/**
 * Constants to be used in.
 *
 * @author nchigurupati
 * @version 1.0
 * @since 2015/04/28
 */
public final class Constants
{

	/**
	 * Default DNS domain.
	 */
	public static final String DEFAULT_DNSSEC_DOMAIN = "verisigninc.com";

	/**
	 * Default Trust Anchor, a cryptographic public key to be used for validation purposes.
	 */
	public static final String DEFAULT_TRUST_ANCHOR = ". IN DS 19036 8 2 49AAC11D7B6F6446702E54A"
			+ "1607371607A1A41855200FD2CE1CDDE32F24E8FB5";
	/**
	 * A lookup path for locally stored Trust Anchor(s).
	 */
	public static final String DEFAULT_TRUST_ANCHOR_LOCATION = "/var/lib/unbound/root.key";

	/**
	 * The Domain label separator.
	 */
	public static final String DNS_LABEL_DELIMITER = ".";

	/**
	 * TXT Record prefix for device placeholder records
	 */
	public static final String TXT_RECORD_PREFIX = "_x";

	/**
	 * The Label associated with PTR records for a service.
	 */
	public static final String LABEL = "._label";
	/**
	 * The Name associated with PTR records for a service.
	 */
	public static final String NAME = "._name";

	/**
	 * TCP DNS label found in service type PTR records
	 */
	public static final String TCP = "_tcp";

	/**
	 * UDP DNS label found in service type PTR records
	 */
	public static final String UDP = "_udp";

    /**
     * Subtype DNS-SD label to specify any subtype-based service lookup.
     */
    public static final String SUBTYPE = "_sub";

	/**
	 * Default protocol for TLSA record prefix
	 */
	public static final String TLSA_DEFAULT_PROTOCOL = "tcp";

	/**
	 * Default port for TLSA record prefix
	 */
	public static final int TLSA_DEFAULT_PORT = 0;


	public static final char COLON_UNICODE_CHAR = ':';

	/**
	 * Service Discovery specific prefix.
	 */
	public static final String SERVICES_DNS_SD_UDP = "_services._dns-sd._udp";

	/**
	 * Cache default size for the resolver.
	 */
	public static final int CACHE_SIZE = 1000;
	/**
	 * 15 minutes in seconds, Cache default TTL.
	 */
	public static final int CACHE_TIME_LIMIT = 15 * 60;

	/**
	 * Resource Record default TTL.
	 */
	public static final long RECORD_DEFAULT_TTL = 3600L;


	private Constants ()
    {
		throw new AssertionError( String.format( "No instances of %s for you!", this.getClass().getName() ) );
	}

}
