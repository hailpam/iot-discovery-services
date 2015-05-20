package com.verisign.iot.discovery.commons;

/**
 * Define a minimal set of enumerative Status Codes in the IANA available range for assignment. In
 * particular, the range [9000, 9999] defines DNS related errors, instead the range [10000, 19999]
 * defines a range of generic protocol errors.
 *
 * @see <a href="https://tools.ietf.org/html/rfc2929">IANA Considerations</a>
 * @author pmaresca <pmaresca@verisign.com>
 * @version 1.0
 * @since 2015/05/02
 */
public enum StatusCode {

    SUCCESSFUL_OPERATION((short) 10000, "Successful DNS Operation"),
    CONFIGURATION_ERROR((short) 10001, "Inconsistent secured DNS settings"),
    NETWORK_ERROR((short) 10002, "Unreachable DNS or timeout expired"),
    RESOURCE_LOOKUP_ERROR((short) 9003, "DNS Resolution error"),
    RESOURCE_INSECURE_ERROR((short) 9505, "Unsecured DNS packet"),
    RESOURCE_UNEXPECTED((short) 9506, "Unexpected Resource Record Type"),
    SIGNATURE_ERROR((short) 9016, "DNS failed to verify"),
    SERVER_ERROR((short) 9002, "DNS server failure"),
    RESOLUTION_NAME_ERROR((short) 9001, "DNS name does not exist"),
    RESOLUTION_RR_TYPE_ERROR((short) 9007, "RR Type does not exist");

    private final Short code;
    private final String label;

    private StatusCode(final Short code, final String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * Return the Error Code for this instance.
     *
     * @return A <code>short</code> defining the Error Code
     */
    public Short statusCode() {
        return this.code;
    }

    /**
     * Return the label for the Error Code of this instance.
     *
     * @return A <code>String</code> containing a brief explanation of the Error Code
     */
    public String statusLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return String.format("%d: %s: ", this.code, this.label);
    }

}
