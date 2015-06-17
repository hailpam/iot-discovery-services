package com.verisign.iot.discovery.domain;

import org.xbill.DNS.TLSARecord;
import org.xbill.DNS.utils.base16;

/**
 * Created by tjmurphy on 6/1/15.
 */
public class CertRecord extends DiscoveryRecord
{

	private TLSARecord tlsaRecord;

	public CertRecord ( TLSARecord tlsaRecord )
    {
		super( base16.toString( tlsaRecord.getCertificateAssociationData() ), tlsaRecord.getTTL());
		this.tlsaRecord = tlsaRecord;
	}


	@Override
	public String getServiceType ()
    {
		throw new UnsupportedOperationException( "Not supported by this record type" );
	}


	@Override
	public String getServiceZone ( String dnsLabel )
    {
		throw new UnsupportedOperationException( "Not supported by this record type" );
	}


	@Override
	public String getServiceName ( String dnsLabel )
    {
		throw new UnsupportedOperationException( "Not supported by this record type" );
	}


	@Override
	public String toDisplay ()
    {
		return String.format("%d %s", ttl, rData);
	}

}
