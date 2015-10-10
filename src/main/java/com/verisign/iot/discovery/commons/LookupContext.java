
package com.verisign.iot.discovery.commons;

import com.verisign.iot.discovery.domain.Fqdn;
import java.io.Serializable;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Resolver;

/**
 * Context Object for lookup operations.
 *
 * @author pmaresca <pmaresca@verisign.com>
 * @version 1.0
 * @since 2015/05/02
 */
public final class LookupContext implements Serializable
{

	private static final long serialVersionUID = 448226124178324159L;
	/** Resolver to be used during the lookup. */
	private Resolver resolver;
    /** Validating Resolver to be used during the DNSSEC check. */
    private Resolver valResolver;
	/** Instantiated @ Lookup} to be used. */
	private Lookup lookup;
	/** Fully Qualified Domain Name to lookup to (according to the other parameters). */
	private Fqdn domainName;
	/** A label to be looked up (decorating the domain information). */
	private String label;
	/** A type to be looked up (decorating the domain information). */
	private String type;
	/** A type to be added during the lookup. */
	private String prefix;
	/** Indicate whether DNSSEC validation has to be carried out. */
	private boolean secure;
	/** Indicate the Resource Record type. */
	private int rrType;


	public Resolver getResolver ()
    {
		return this.resolver;
	}


	public void setResolver ( Resolver resolver )
    {
		this.resolver = resolver;
	}

    public Resolver getValResolver ()
    {
		return this.valResolver;
	}


	public void setValResolver ( Resolver resolver )
    {
		this.valResolver = resolver;
	}

	public Lookup getLookup ()
    {
		return this.lookup;
	}

	public void setLookup ( Lookup lookup )
    {
		this.lookup = lookup;
	}

	public Fqdn getDomainName ()
    {
		return this.domainName;
	}

	public void setDomainName ( Fqdn domainName )
    {
		this.domainName = domainName;
	}

	public String getLabel ()
    {
		return this.label;
	}

	public void setLabel ( String label )
    {
		this.label = label;
	}

	public String getType ()
    {
		return this.type;
	}

	public void setType ( String type )
    {
		this.type = type;
	}

	public boolean isSecure ()
    {
		return this.secure;
	}

	public void setSecure ( boolean secure )
    {
		this.secure = secure;
	}

	public String getPrefix ()
    {
		return this.prefix;
	}

	public void setPrefix ( String prefix )
    {
		this.prefix = prefix;
	}

	public int getRrType ()
    {
		return this.rrType;
	}

	public void setRrType ( int rrType )
    {
		this.rrType = rrType;
	}

}
