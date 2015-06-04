package com.verisign.iot.discovery.domain;

import org.xbill.DNS.TLSARecord;
import org.xbill.DNS.utils.base16;

/**
 * Created by tjmurphy on 6/1/15.
 */
public class TLSADiscoveryRecord extends DiscoveryRecord {

	private TLSARecord tlsaRecord;

	public TLSADiscoveryRecord ( TLSARecord tlsaRecord ) {
		super( base16.toString( tlsaRecord.getCertificateAssociationData() ), tlsaRecord.getTTL());
		this.tlsaRecord = tlsaRecord;
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
		return String.format("%s %d", rData, ttl);
	}
}
