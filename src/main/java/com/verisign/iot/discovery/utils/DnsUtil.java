package com.verisign.iot.discovery.utils;

import com.verisign.iot.discovery.commons.StatusCode;
import com.verisign.iot.discovery.domain.Fqdn;
import com.verisign.iot.discovery.domain.TextRecord;
import com.verisign.iot.discovery.exceptions.ConfigurationException;
import com.verisign.iot.discovery.exceptions.LookupException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jitsi.dnssec.validator.ValidatingResolver;
import org.xbill.DNS.Cache;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ResolverConfig;
import org.xbill.DNS.Section;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

/**
 * A collection of static utility methods to deal with DNS.
 *
 * @author pmaresca <pmaresca@verisign.com>
 * @version 1.0
 * @since 2015/05/02
 */
public final class DnsUtil {

    private static final String INSECURE = "insecure";
    private static final String CHAIN_OF_TRUST = "chain of trust";
    private static final String NO_DATA = "nodata";
    private static final String NO_SIGNATURE = "missing signature";


    /**
     * Instantiate a DNS <code>Resolver</code> by the provided Server. In case of DNSSEC validation
     * is needed, a <code>ValidatingResolver</code> is instantiated.
     *
     * @param dnsSec <code>true</code> iff DNSSEC is enabled
     * @param trustAnchor Public cryptographic to validate against
     * @param server Server to use as DNS resolver
     * @return An instance of <code>Resolver</code>
     * @throws ConfigurationException Exceptional circumstances in which <code>Resolver</code>
     * cannot be created.
     */
    public static Resolver getResolver(boolean dnsSec, String trustAnchor, String server) 
                            throws ConfigurationException 
    {
        Resolver resolver = instantiateResolver(dnsSec, trustAnchor, server);
        if (resolver == null) {
            throw new ConfigurationException(String.format("Unable to retrieve a Resolver from [%s]", server));
        }

        return resolver;
    }

    /**
     * Instantiate a set of default DNS <code>Resolver</code> by the provided Server. In case of
     * DNSSEC validation is needed, <code>ValidatingResolver</code> will be instantiated.
     *
     * @param dnsSec <code>true</code> iff DNSSEC is enabled
     * @param trustAnchor Public cryptographic to validate against
     * @return A list of default <code>Resolver</code>
     * @throws ConfigurationException Exceptional circumstances in which no default
     * <code>Resolver</code> can be created.
     */
    public static Map<String, Resolver> getResolvers(boolean dnsSec, String trustAnchor) 
                                            throws ConfigurationException 
    {
        String[] servers = ResolverConfig.getCurrentConfig().servers();
        Map<String, Resolver> resolvers = new LinkedHashMap<>(servers.length);
        for (String server : servers) {
            Resolver resolver = instantiateResolver(dnsSec, trustAnchor, server);
            if (resolver != null) {
                resolvers.put(server, resolver);
            }
        }

        if (resolvers.isEmpty()) {
            throw new ConfigurationException("Unable to retrieve Default Resolvers");
        }

        return resolvers;
    }

    /**
     * Validate the DNSSEC trust chain against the provided domain name (i.e. <code>Fqdn</code>).
     *
     * @param name A <code>Fqdn</code> representing the validating domain
     * @param resolver A DNS <code>Resovler</code> to be used in this validation
     * @return <code>true</code> iff the DNSSEC is valid
     * @throws LookupException Containing the specific <code>StatusCode</code> defining the error
     * that has been raised.
     */
    public static boolean checkDnsSec(Fqdn name, Resolver resolver) throws LookupException 
    {
        try {
            ValidatingResolver validating = (ValidatingResolver) resolver;
            Record toValidate = Record.newRecord(Name.fromConstantString(name.fqdn()), Type.A, DClass.IN);
            Message dnsResponse = validating.send(Message.newQuery(toValidate));
            RRset[] rrSets = dnsResponse.getSectionRRsets(Section.ADDITIONAL);
            StringBuilder reason = new StringBuilder("");
            for (RRset rrset : rrSets) {
                if (rrset.getName().equals(Name.root) && rrset.getType() == Type.TXT
                        && rrset.getDClass() == ValidatingResolver.VALIDATION_REASON_QCLASS) {
                    reason.append(TextRecord.build((TXTRecord) rrset.first()).getRData());
                }
            }
            StatusCode outcome = StatusCode.SUCCESSFUL_OPERATION;
            if(dnsResponse.getRcode() == Rcode.SERVFAIL) {
                if(reason.toString().toLowerCase().contains(INSECURE) || 
                    reason.toString().toLowerCase().contains(CHAIN_OF_TRUST))
                    outcome = StatusCode.RESOURCE_INSECURE_ERROR;
                else if(reason.toString().toLowerCase().contains(NO_SIGNATURE))
                    outcome = StatusCode.RESOLUTION_NAME_ERROR;
                else if(reason.toString().toLowerCase().contains(NO_DATA))
                    outcome = StatusCode.NETWORK_ERROR;
            }else if(dnsResponse.getRcode() == Rcode.NXDOMAIN) {
                outcome = StatusCode.RESOLUTION_NAME_ERROR;
            }else if(dnsResponse.getRcode() == Rcode.NOERROR &&
                        !dnsResponse.getHeader().getFlag(Flags.AD)) {
                outcome = StatusCode.RESOURCE_INSECURE_ERROR;
            }

            if(outcome != StatusCode.SUCCESSFUL_OPERATION)
                throw ExceptionsUtil.build(outcome,
                                           "DNSSEC Validation Failed",
                                           new LinkedHashMap<String, StatusCode>());
        } catch (IOException e) {
            // it might be a transient error network: retry with next Resolver
            return false;
        }

        return true;
    }

    /**
     * Validate the DNS <code>Lookup</code>, catching any transient or blocking issue.
     *
     * @param lookup A <code>Lookup</code> used to pull Resource Records
     * @return A <code>StatusCode</code> with the check outcome
     * @throws LookupException Containing the specific <code>StatusCode</code> defining the error
     * that has been raised.
     */
    public static StatusCode checkLookupStatus(Lookup lookup)
                                throws LookupException 
    {
        StatusCode outcome = null;
        if (lookup.getResult() == Lookup.TRY_AGAIN) {
            outcome = StatusCode.NETWORK_ERROR;
        } else if (lookup.getResult() == Lookup.UNRECOVERABLE) {
            outcome = StatusCode.SERVER_ERROR;
        } else if (lookup.getResult() == Lookup.HOST_NOT_FOUND) {
            // Domain Name not found
            outcome = StatusCode.RESOLUTION_NAME_ERROR;
        } else if (lookup.getResult() == Lookup.TYPE_NOT_FOUND) {
            // RR set not found
            outcome = StatusCode.RESOLUTION_RR_TYPE_ERROR;
        } else {
            outcome = StatusCode.SUCCESSFUL_OPERATION;
        }

        return outcome;
    }

    /**
     * Instantiate a DNS <code>Lookup</code> object.
     *
     * @param domainName A domain name to lookup
     * @param resolver A <code>Resolver</code> to be used for lookup
     * @param rrType The Resource Record <code>Type</code>
     * @param cache The Resource Record <code>Cache</code>
     * @return An instance of <code>Lookup</code>
     * @throws LookupException Containing the specific <code>StatusCode</code> defining the error
     * that has been raised.
     */
    public static Lookup instantiateLookup(String domainName, Resolver resolver, int rrType, Cache cache)
                            throws LookupException 
    {
        Lookup lookup = null;
        try {
            lookup = new Lookup(domainName, rrType);
            lookup.setResolver(resolver);
            lookup.setCache(cache);
        } catch (TextParseException ex) {
            throw new LookupException(StatusCode.RESOURCE_LOOKUP_ERROR, String.format("Unable to crea a Lookup for [%s]",
                    domainName));
        }

        return lookup;
    }

    /**
     * Private helper to instantiate a DNS <code>Resolver</code> by the provided Server.
     *
     * @param dnsSec <code>true</code> iff DNSSEC is enabled
     * @param trustAnchor Public cryptographic to validate against
     * @param server Server to use as DNS resolver
     * @return <code>null</code> in case the <code>Resolver</code> cannot be instantiated
     */
    private static Resolver instantiateResolver(boolean dnsSec, String trustAnchor, String server) 
    {
        try {
            Resolver resolver = new SimpleResolver(server);
            if (!dnsSec) {
                return resolver;
            }

            ValidatingResolver validating = new ValidatingResolver(resolver);
            validating.loadTrustAnchors(new ByteArrayInputStream(trustAnchor.getBytes(StandardCharsets.UTF_8)));

            return validating;
        } catch (IOException e) {
            return null;
        }
    }

    private DnsUtil() 
    {
        throw new AssertionError(String.format("No instances of %s for you!", this.getClass().getName()));
    }

}
