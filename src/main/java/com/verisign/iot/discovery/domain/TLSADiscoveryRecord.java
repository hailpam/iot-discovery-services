package com.verisign.iot.discovery.domain;

import com.verisign.iot.discovery.utils.EncodingUtil;
import org.xbill.DNS.TLSARecord;

/**
 * Created by tjmurphy on 6/1/15.
 */
public class TLSADiscoveryRecord extends DiscoveryRecord {


	public TLSADiscoveryRecord ( TLSARecord tlsaRecord ) {
		super(EncodingUtil.bytesToHex( tlsaRecord.getCertificateAssociationData() ), tlsaRecord.getTTL());
	}


	@Override
	public String getServiceType () {
		throw new UnsupportedOperationException( "Not supported by this record type" );
	}


	@Override
	public String getServiceZone ( String dnsLabel ) {
		throw new UnsupportedOperationException( "Not supported by this record type" );
	}


	@Override
	public String getServiceName ( String dnsLabel ) {
		throw new UnsupportedOperationException( "Not supported by this record type" );
	}


	@Override
	public String toDisplay () {
		return this.rData;
	}
}
