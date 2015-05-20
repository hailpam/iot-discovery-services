package com.verisign.iot.discovery.utils;

import com.verisign.iot.discovery.domain.Fqdn;

/**
 * A collection of static utility methods to deal with parameter validations.
 *
 * @author pmaresca <pmaresca@verisign.com>
 * @version 1.0
 * @since 2015/05/02
 */
public final class ValidatorUtil {

    public static final String FQDN_PATTERN = "^([(\\\\ )|(\\\\0-9)0-9a-zA-Z_-]+\\.){2,}$";
    public static final String DNS_SD_FQDN_PATTERN = "\\";
    public static final int FQDN_LENGTH = 255;

    /**
     * Check the input expression.
     *
     * @param what A <code>boolean</code> expression to be checked
     * @throws IllegalArgumentException In case the argument cannot be validated
     */
    public static void check(boolean what) throws IllegalArgumentException {
        if (!what) {
            throw new IllegalArgumentException("failed to assert");
        }
    }

    /**
     * Validate the Label in Input.
     *
     * @param label A <code>String</code> containing a Label
     * @throws IllegalArgumentException In case the argument cannot be validated
     */
    public static void isValidLabel(String label) throws IllegalArgumentException {
        if (label == null || label.isEmpty()) {
            throw new IllegalArgumentException("null/blank label");
        }
    }

    /**
     * Validate the Domain Name in Input.
     *
     * @see <a href="http://en.wikipedia.org/wiki/Domain_name">Domain Name</a>
     * @see <a href="http://en.wikipedia.org/wiki/CNAME_record">CNAME</a>
     * @param name A <code>Fqdn</code> representing a Domain Name
     * @throws IllegalArgumentException In case the argument cannot be validated
     */
    public static void isValidDomainName(Fqdn name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("null/blank Fully Qualified Domain Name");
        } else if (name.fqdn() == null || name.fqdn().isEmpty()) {
            throw new IllegalArgumentException("null/blank name");
        } else if (!name.fqdn().matches(FQDN_PATTERN) || name.fqdn().length() > FQDN_LENGTH) {
            throw new IllegalArgumentException(String.format("invalid FQDN [%s]", name));
        }
    }

    /**
     * Validate the Domain Name in Input.
     *
     * @see <a href="http://en.wikipedia.org/wiki/Domain_name">Domain Name</a>
     * @see <a href="http://en.wikipedia.org/wiki/CNAME_record">CNAME</a>
     * @param name A <code>String</code> containing a Domain Name
     * @throws IllegalArgumentException In case the argument cannot be validated
     */
    public static void isValidDomainName(String name) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("null/blank name");
        } else if (!name.matches(FQDN_PATTERN) || name.length() > FQDN_LENGTH) {
            throw new IllegalArgumentException(String.format("invalid FQDN [%s]", name));
        }
    }

    /**
     * Validate whether the Domain Name is DNS-SD specific or not.
     *
     * @param name name A <code>String</code> containing a Domain Name
     * @return <code>true</code> iff the FQDN is DNS-SD specific
     */
    public static boolean isDnsSdDomainName(String name) {
        return name.contains(DNS_SD_FQDN_PATTERN);
    }

    private ValidatorUtil() {
        throw new AssertionError(String.format("No instances of %s for you!", this.getClass().getName()));
    }
}
