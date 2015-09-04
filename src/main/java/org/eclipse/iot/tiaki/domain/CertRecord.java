/*
 * Copyright (c) 2015, Verisign, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */

package org.eclipse.iot.tiaki.domain;

import org.xbill.DNS.TLSARecord;
import org.xbill.DNS.utils.base16;


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
