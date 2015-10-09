
package com.verisign.iot.discovery.domain;

import java.util.Set;
import org.xbill.DNS.TXTRecord;

/**
 * Class to capture TXT record information from DNS.
 *
 * @see <a href="http://en.wikipedia.org/wiki/TXT_Record">TXT Resource Record</a>
 * @author nchigurupati
 * @version 1.0
 * @since Mar 30, 2015
 */
public final class TextRecord extends DiscoveryRecord
{

    /**
     * Takes in input a <code>Set</code> of strings and produces a value corresponding to the max TTL
     *
     * @param txtsRec  A <code>Set</code> of strings
     *
     * @return  A properly built compound <code>TextRecord</code>
     */
    public final static TextRecord build( Set<TextRecord> txtsRec )
    {
        StringBuilder flattened = new StringBuilder();
        long max = Long.MIN_VALUE;
        for(TextRecord txt: txtsRec) {
            flattened.append(txt.getRData());
            if(txt.getTtl() > max)
                max = txt.getTtl();
        }

        return new TextRecord(flattened.toString(), max);
    }

	/**
	 * Static builder. It wraps out a {@link TXTRecord} by extracting relevant data.
	 *
	 * @param txtRec
	 *        A {@link TXTRecord} instance to be worked out
	 * @return An instance of <code>TextRecord</code>
	 */
	public final static TextRecord build ( TXTRecord txtRec )
    {
		return new TextRecord( txtRec.rdataToString(), txtRec.getTTL() );
	}


	private TextRecord ( String txtData, long ttl )
    {
		super( txtData, ttl );
	}


	@Override
	public String getServiceType ()
    {
		throw new UnsupportedOperationException( "Not supported by TextRecord" );
	}


	@Override
	public String getServiceZone ( String dnsLabel )
    {
		throw new UnsupportedOperationException( "Not supported by TextRecord" );
	}


	@Override
	public String getServiceName ( String dnsLabel )
    {
		throw new UnsupportedOperationException( "Not supported by TextRecord" );
	}

	@Override
	public String toString()
	{
		return super.toString();
	}

    @Override
    public String toDisplay()
    {
        return String.format("%d TXT %s", ttl, rData);
    }

}
