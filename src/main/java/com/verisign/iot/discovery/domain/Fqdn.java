package com.verisign.iot.discovery.domain;

import com.verisign.iot.discovery.commons.Constants;
import com.verisign.iot.discovery.utils.ValidatorUtil;
import java.util.Objects;

/**
 * A class abstracting a Fully Qualified Domain Name (FQDN).
 *
 * @see <a href="http://en.wikipedia.org/wiki/Fully_qualified_domain_name">FQDN</a>
 * @see <a href="http://en.wikipedia.org/wiki/CNAME_record">CNAME</a>
 * @author pmaresca <pmaresca@verisign.com>
 * @version 1.0
 * @since 2015/05/02
 */
public final class Fqdn
{

    /**
     * Quoting template for escaped FQDNs.
     */
    private static final String DNS_SD_QUOTED = "\"%s\"";

    /**
     * A label to be concatenated with the root domain name.
     */
    private final String entityLabel;
    /**
     * A root domain name.
     */
    private final String domain;

    /**
     * Constructor taking in input the domain name to be used onwards.
     *
     * @param domain A <code>String</code> containing a root domain name
     */
    public Fqdn(String domain)
    {
        this("", domain);
    }

    /**
     * Two arguments constructor passing the entity label and the root domain name to be
     * concatenated.
     *
     * @param entityLabel A <code>String</code> containing an entity's label
     * @param domain A <code>String</code> containing a root domain name
     */
    public Fqdn(String entityLabel, String domain)
    {
        ValidatorUtil.isValidDomainName(domain.endsWith(".") ? domain : domain + ".");
        ValidatorUtil.check(entityLabel != null);
        this.entityLabel = entityLabel.trim();
        this.domain = domain.trim();
    }

    /**
     * Return a FQDN.
     *
     * @return A <code>String</code> containing the FQDN
     */
    // TODO Remove in favour of toString()
    public String fqdn()
    {
        return buildFQDN("");
    }

    /**
     * Return a FQDN comprehensive of the specified <i>prefix</i>.
     *
     * @param prefix A <code>String</code> containing prefix to be concatenated
     * @return A <code>String</code> containing the FQDN concatenated with the prefix
     */
    public String fqdnWithPrefix(String prefix)
    {
        String builtFqdn = buildFQDN("");
        if(builtFqdn.contains(prefix))
            return builtFqdn;
        else
            return buildFQDN(prefix);
    }

	/**
	 * Return the entity's label.
	 *
	 * @return A <code>String</code> containing the entity's label
	 */
	public String label()
    {
        return this.entityLabel;
    }

    /**
     * Return the root domain name.
     *
     * @return A <code>String</code> containing the root domain name
     */
    public String domain()
    {
        return this.domain;
    }

    /**
     * Build a FQDN up concatenating the specified <i>prefix</i>.
     *
     * @param prefix A <code>String</code> containing a prefix to be concatenated
     * @return A <code>String</code> containing a FQDN comprehensive of <i>prefix</i>
     */
    private String buildFQDN(String prefix)
    {
        StringBuilder builder = new StringBuilder();
        if (prefix != null && !prefix.isEmpty()) {
            builder.append(prefix).append(Constants.DNS_LABEL_DELIMITER);
        }
        if (!this.entityLabel.isEmpty()) {
            builder.append(this.entityLabel).append(Constants.DNS_LABEL_DELIMITER);
        }
        builder.append(this.domain);
        if (!this.domain.endsWith(Constants.DNS_LABEL_DELIMITER)) {
            builder.append(Constants.DNS_LABEL_DELIMITER);
        }
        ValidatorUtil.isValidDomainName(builder.toString());

        if (ValidatorUtil.isDnsSdDomainName(builder.toString())) {
            return String.format(DNS_SD_QUOTED, builder.toString());
        } else {
            return builder.toString();
        }
    }

    @Override
    public String toString()
    {
        return buildFQDN("");
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Fqdn other = (Fqdn) obj;
        if (!Objects.equals(this.entityLabel, other.entityLabel)) {
            return false;
        }
        if (!Objects.equals(this.domain, other.domain)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.entityLabel);
        hash = 47 * hash + Objects.hashCode(this.domain);

        return hash;
    }

}
