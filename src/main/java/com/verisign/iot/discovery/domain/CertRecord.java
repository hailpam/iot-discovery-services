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

    /**
     * Retrieve the Certificate Usage.
     *
     * @return  An <code>int</code> representing this TLSA usage
     */
    public int certUsage() { return tlsaRecord.getCertificateUsage(); }

    /**
     * Retrieve the Certificate Matching Type.
     *
     * @return  An <code>int</code> representing this TLSA matching type
     */
    public int certMatchingType() { return tlsaRecord.getMatchingType(); }

    /**
     * Retrieve the Certificate Selector.
     *
     * @return  An <code>int</code> representing this TLSA selector
     */
    public int certSelector() { return tlsaRecord.getSelector(); }

    
	@Override
	public String toDisplay ()
    {
		return String.format("%d %s", ttl, rData);
	}

}
