package com.verisign.iot.discovery.domain;

import java.util.Objects;

/**
 * Base Class abstracting the commonalities of Discovery Data. A <code>DiscoveryRecord</code>
 * defines a @{link Comparable} instance.
 *
 * @author pmaresca <pmaresca@verisign.com>
 * @since 1.0
 * @version May 2, 2015
 */
public abstract class DiscoveryRecord implements Comparable<DiscoveryRecord> {

    /**
     * A <code>String</code> containing this resource record raw textual data.
     */
    protected final String rData;
    /**
     * Resource record specific TTL.
     */
    protected final long ttl;

    public DiscoveryRecord() {
        this("", 0L);
    }

    public DiscoveryRecord(String rData, long ttl) {
        this.rData = rData;
        this.ttl = ttl;
    }

    public String getRData() {
        return this.rData;
    }

    public long getTtl() {
        return this.ttl;
    }

    /**
     * According to the specific implementation, it extract a <b>Service Type</b> and return it.
     *
     * @return A <code>String</code> containing a Service Type
     */
    public abstract String getServiceType();

    /**
     * According to the specific implementation, it extract a <b>Service Zone</b> and return it.
     *
     * @param dnsLabel A <code>String</code> defining the DNS Label
     * @return A <code>String</code> containing a Service Zone
     */
    public abstract String getServiceZone(String dnsLabel);

    /**
     * According to the specific implementation, it extract a <b>Service Name</b> and return it.
     *
     * @param dnsLabel A <code>String</code> defining the DNS Label
     * @return A <code>String</code> containing a Service Name
     */
    public abstract String getServiceName(String dnsLabel);

    /**
     * Serialize this Discovery Record into a display format.
     *
     * @return A <code>String</code> with a representable version of this
     * <code>DiscoveryRecord</code>
     */
    public abstract String toDisplay();

    @Override
    public int compareTo(DiscoveryRecord t) {
        return this.rData.compareTo(t.getRData());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.rData);
        hash = 97 * hash + (int) (this.ttl ^ (this.ttl >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DiscoveryRecord other = (DiscoveryRecord) obj;
        if (!Objects.equals(this.rData, other.rData)) {
            return false;
        }
        if (this.ttl != other.ttl) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s %d", this.ttl, this.rData);
    }

}
