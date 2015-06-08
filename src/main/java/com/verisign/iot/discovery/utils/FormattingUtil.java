package com.verisign.iot.discovery.utils;

import com.verisign.iot.discovery.commons.Constants;
import com.verisign.iot.discovery.domain.Fqdn;

/**
 * A set of static utils to handle message formatting.
 *
 * @author pmaresca
 * @version 1.0
 * @since May 02, 2015
 */
public final class FormattingUtil {

	/**
	 * Query output template.
	 */
	private static final String QUERY_OUTPUT = ";; QUERY\n%s %s";
	/**
	 * Response output template.
	 */
	// TODO TTL, TYPE and other attributes to be split
	private static final String SIMPLE_RESPONSE_OUTPUT = "%s";
	/**
	 * Response output template (decorated with a header).
	 */
	// TODO TTL, TYPE and other attributes to be split
	private static final String RESPONSE_OUTPUT = ";; RESPONSE\n%s\n";
	/**
	 * Server output template.
	 */
	private static final String SERVER_OUTPUT = ";; SERVER\n%s\n";


	/**
	 * Build up a formatted message for resolver server.
	 *
	 * @param server A host's IP/Hostname
	 * @return A <code>String</code> containing the server display
	 */
	public static String server ( String server ) {
		return String.format( SERVER_OUTPUT, server );
	}


	/**
	 * Build up a formatted message for a DNS query.
	 *
	 * @param name   A <code>Fqdn</code> to be looked up
	 * @param prefix A <code>String</code> representing the domain name prefix
	 * @param type   A <code>String</code> containing the resource record type
	 * @return A <code>String</code> containing the query content
	 */
	public static String query ( Fqdn name, String prefix, String type ) {
		return String.format( QUERY_OUTPUT, name.fqdnWithPrefix( prefix ), type );
	}


	/**
	 * Build up a formatted response message.
	 *
	 * @param content A <code>String</code> wrapping the content
	 * @return A formatted <code>String</code>
	 */
	public static String simpleResponse ( String content ) {
		return String.format( SIMPLE_RESPONSE_OUTPUT, content );
	}


	/**
	 * Build up a formatted response message, decorated with a response header.
	 *
	 * @param content A <code>String</code> wrapping the content
	 * @return A formatted <code>String</code>
	 */
	public static String response ( String content ) {
		return String.format( RESPONSE_OUTPUT, content );
	}


	private FormattingUtil () {
		throw new AssertionError( String.format( "No instances of %s for you!",
				this.getClass().getName() ) );
	}
}
