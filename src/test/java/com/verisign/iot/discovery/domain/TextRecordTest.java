
package com.verisign.iot.discovery.domain;

import org.junit.Assert;
import org.junit.Test;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.TextParseException;

public class TextRecordTest {

	@Test
	public void testEqualsObject () throws TextParseException {
		TextRecord txt1 = TextRecord.build( DiscoveryRecordUtil.createTxtRecord( "txt1", 3600 ) );
		TextRecord txt2 = TextRecord.build( DiscoveryRecordUtil.createTxtRecord( "txt1", 3600 ) );

		Assert.assertTrue( "Both text records should be equal", txt1.equals( txt2 ) );
	}


	@Test
	public void testNotEqualsObject () throws TextParseException {
		TextRecord txt1 = TextRecord.build( DiscoveryRecordUtil.createTxtRecord( "txt1", 3600 ) );
		TextRecord txt2 = TextRecord.build( DiscoveryRecordUtil.createTxtRecord( "txt2", 3600 ) );

		Assert.assertTrue( "Both text records should not be equal", !txt1.equals( txt2 ) );
	}



}
