package org.eclipse.iot.tiaki.domain;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by tjmurphy on 6/5/15.
 */
public class TLSAPrefixTest {

	private final String DEFAULT_VALUES_PREFIX = "_0._tcp";

	/////////////////////////////////////////////
	/////////////    Default values   ///////////
	/////////////////////////////////////////////


	@Test
	public void defaultValuesDefaultConstructor () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix();
		Assert.assertTrue( tlsaPrefix.toString().equals( DEFAULT_VALUES_PREFIX ) );
	}


	@Test
	public void defaultValuesNullInit () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( null );
		Assert.assertTrue( tlsaPrefix.toString().equals( DEFAULT_VALUES_PREFIX ) );
	}


	@Test
	public void defaultValuesEmptyInit () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "" );
		Assert.assertTrue( tlsaPrefix.toString().equals( DEFAULT_VALUES_PREFIX ) );
	}


	@Test
	public void defaultValuesWhitespaceInit () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "         " );
		Assert.assertTrue( tlsaPrefix.toString().equals( DEFAULT_VALUES_PREFIX ) );
	}

	/////////////////////////////////////////////
	/////////////    Default port    ////////////
	/////////////////////////////////////////////


	@Test
	public void defaultPort () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( ":udp" );
		Assert.assertTrue( tlsaPrefix.toString().equals( "_0._udp" ) );
	}


	@Test
	public void defaultPortWithWhitespace () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "       :udp" );
		Assert.assertTrue( tlsaPrefix.toString().equals( "_0._udp" ) );
	}

	/////////////////////////////////////////////
	///////////    Default protocol    //////////
	/////////////////////////////////////////////


	@Test
	public void defaultProtocol () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "123" );
		Assert.assertTrue( tlsaPrefix.toString().equals( "_123._tcp" ) );
	}


	@Test
	public void defaultProtocolWithDelimiter () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "123:" );
		Assert.assertTrue( tlsaPrefix.toString().equals( "_123._tcp" ) );
	}


	@Test
	public void defaultProtocolZeroPort () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "0" );
		Assert.assertTrue( tlsaPrefix.toString().equals( "_0._tcp" ) );
	}


	@Test
	public void defaultProtocol65534Port () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "65534" );
		Assert.assertTrue( tlsaPrefix.toString().equals( "_65534._tcp" ) );
	}


	@Test
	public void defaultProtocol65535Port () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "65535" );
		Assert.assertTrue( tlsaPrefix.toString().equals( "_65535._tcp" ) );
	}


	@Test(expected = IllegalArgumentException.class)
	public void defaultProtocolInvalidPortNegativeValue () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "-1" );
	}


	@Test(expected = IllegalArgumentException.class)
	public void defaultProtocolInvalidPortValueExceedsMax () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "65536" );
	}

	/////////////////////////////////////////////
	///////////    No Default Values    /////////
	/////////////////////////////////////////////


	@Test
	public void giveStringOfDefaultValues () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "0:tcp" );
		Assert.assertTrue( tlsaPrefix.toString().equals( "_0._tcp" ) );
	}


	@Test
	public void port1ProtocolTCP () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "1:tcp" );
		Assert.assertTrue( tlsaPrefix.toString().equals( "_1._tcp" ) );
	}


	@Test
	public void port1ProtocolUDP () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "1:udp" );
		Assert.assertTrue( tlsaPrefix.toString().equals( "_1._udp" ) );
	}


	@Test(expected = IllegalArgumentException.class)
	public void portNegative1ProtocolUDP () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "-1:udp" );
	}


	@Test(expected = IllegalArgumentException.class)
	public void port65536ProtocolUDP () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "65536:udp" );
	}


	@Test
	public void port65535ProtocolUDP () {
		TLSAPrefix tlsaPrefix = new TLSAPrefix( "65535:udp" );
	}

}
