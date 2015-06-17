package com.verisign.iot.discovery.services;

import com.verisign.iot.discovery.commons.Configurable;
import com.verisign.iot.discovery.commons.Constants;
import com.verisign.iot.discovery.commons.LookupContext;
import com.verisign.iot.discovery.commons.StatusChangeEvent;
import com.verisign.iot.discovery.commons.StatusCode;
import com.verisign.iot.discovery.domain.Fqdn;
import com.verisign.iot.discovery.domain.PointerRecord;
import com.verisign.iot.discovery.domain.RecordsContainer;
import com.verisign.iot.discovery.domain.ServiceInstance;
import com.verisign.iot.discovery.domain.ServiceRecord;
import com.verisign.iot.discovery.domain.TLSADiscoveryRecord;
import com.verisign.iot.discovery.domain.TLSAPrefix;
import com.verisign.iot.discovery.domain.TextRecord;
import com.verisign.iot.discovery.exceptions.ConfigurationException;
import com.verisign.iot.discovery.exceptions.LookupException;
import com.verisign.iot.discovery.utils.DnsUtil;
import com.verisign.iot.discovery.utils.ExceptionsUtil;
import com.verisign.iot.discovery.utils.FormattingUtil;
import com.verisign.iot.discovery.utils.RDataUtil;
import com.verisign.iot.discovery.utils.ValidatorUtil;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.xbill.DNS.Cache;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TLSARecord;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.Type;

/**
 * Class encapsulating the DNS-SD Service Lookup facilities.
 *
 * @author pmaresca <pmaresca@verisign.com>
 * @version 1.0
 * @since 2015/05/02
 */
public class DnsServicesDiscovery extends Configurable implements DnsDiscovery 
{

    /**
     * Lookup Cache.
     */
    private Cache smimeACache;
    /**
     * Thread-owned Errors trace.
     */
    private ThreadLocal<Map<String, StatusCode>> errorsTrace;
    /**
     * DNS Lookup helper.
     */
    private ServicesLookupHelper helper;

    public DnsServicesDiscovery() {
        this(Constants.CACHE_SIZE, Constants.CACHE_TIME_LIMIT);
    }

    /**
     * Overloaded constructor taking as argument Cache size and TTL.
     *
     * @param cacheSize Unsigned <code>int</code> defining the Cache size
     * @param cacheTTL Unsigned <code>int</code> defining the Cache TTL
     */
    public DnsServicesDiscovery(int cacheSize, int cacheTTL) 
    {
        this.smimeACache = new Cache(DClass.ANY);
        this.smimeACache.setMaxEntries(cacheSize);
        this.smimeACache.setMaxNCache(cacheTTL);
        this.helper = this.new ServicesLookupHelper();
        this.errorsTrace = new ThreadLocal<Map<String, StatusCode>>() {
            @Override
            protected Map<String, StatusCode> initialValue() {
                return new LinkedHashMap<>();
            }
        };
    }

    @Override
    public Set<String> listServiceTypes(Fqdn browsingDomain, boolean secValidation) 
                        throws LookupException, ConfigurationException 
    {
        ValidatorUtil.isValidDomainName(browsingDomain);
        validatedConf();
        Set<String> result = null;
        try {
            result = new TreeSet<>();
            result.addAll(this.helper.serviceTypes(browsingDomain, secValidation));
            if (result.isEmpty() && !ExceptionsUtil.onlyNameResolutionTrace(this.errorsTrace.get())) {
                throw ExceptionsUtil.build(StatusCode.RESOURCE_LOOKUP_ERROR,
                                           String.format("Unable to resolve [%s]", browsingDomain),
                                           errorsTrace.get());
            }
        } catch (LookupException | ConfigurationException exception) {
            throw exception;
        } finally {
            errorsTrace.remove();
        }

        return result;
    }

    @Override
    public Set<ServiceInstance> listServiceInstances(Fqdn browsingDomain, String type, boolean secValidation)
            throws LookupException, ConfigurationException 
    {
        ValidatorUtil.isValidDomainName(browsingDomain);
        ValidatorUtil.isValidLabel(type);
        validatedConf();
        Set<ServiceInstance> result = null;
        try {
            result = new TreeSet<>();
            result.addAll(this.helper.serviceInstances(browsingDomain, type, secValidation));
            if (result.isEmpty() && !ExceptionsUtil.onlyNameResolutionTrace(this.errorsTrace.get())) {
                throw ExceptionsUtil.build(StatusCode.RESOURCE_LOOKUP_ERROR,
                        String.format("Unable to resolve [%s]", browsingDomain.fqdnWithPrefix(type)),
                        errorsTrace.get());
            }
        } catch (LookupException | ConfigurationException exception) {
            throw exception;
        } finally {
            errorsTrace.remove();
        }

        return result;
    }

    @Override
    public Set<ServiceRecord> listServiceRecords(Fqdn browsingDomain, String type, boolean secValidation)
                                throws LookupException, ConfigurationException 
    {
        ValidatorUtil.isValidDomainName(browsingDomain);
        ValidatorUtil.isValidLabel(type);
        validatedConf();
        Set<ServiceRecord> result = null;
        try {
            result = new TreeSet<>();
            result.addAll(this.helper.serviceRecords(browsingDomain, type, secValidation));
            if (result.isEmpty() && !ExceptionsUtil.onlyNameResolutionTrace(this.errorsTrace.get())) {
                throw ExceptionsUtil.build(StatusCode.RESOURCE_LOOKUP_ERROR,
                        String.format("Unable to resolve [%s]", browsingDomain.fqdnWithPrefix(type)),
                        errorsTrace.get());
            }
        } catch (LookupException | ConfigurationException exception) {
            throw exception;
        } finally {
            errorsTrace.remove();
        }

        return result;
    }

    @Override
    public Set<TextRecord> listTextRecords(Fqdn browsingDomain, String label, boolean secValidation)
                                throws LookupException, ConfigurationException 
    {
        ValidatorUtil.isValidDomainName(browsingDomain);
        ValidatorUtil.isValidLabel(label);
        validatedConf();
        Set<TextRecord> result = null;
        try {
            result = new LinkedHashSet<>();
            Fqdn txtFqdn = new Fqdn(label, browsingDomain.domain());
            result.addAll(this.helper.serviceTexts(txtFqdn, label, secValidation));
            if (result.isEmpty() && !ExceptionsUtil.onlyNameResolutionTrace(this.errorsTrace.get())) {
                throw ExceptionsUtil.build(StatusCode.RESOURCE_LOOKUP_ERROR,
                        String.format("Unable to resolve [%s]", browsingDomain.fqdnWithPrefix(label)),
                        errorsTrace.get());
            }
        } catch (LookupException | ConfigurationException exception) {
            throw exception;
        } finally {
            errorsTrace.remove();
        }

        return result;
    }

    @Override
    public Set<TLSADiscoveryRecord> listTLSARecords(Fqdn browsingDomain, TLSAPrefix tlsaPrefix, 
                                                    boolean secValidation)
                                        throws LookupException, ConfigurationException 
    {
        ValidatorUtil.isValidDomainName(browsingDomain);
        validatedConf();
        Set<TLSADiscoveryRecord> result = null;
        try {
            result = new TreeSet<>();
            result.addAll(this.helper.tlsaRecords(browsingDomain, tlsaPrefix, secValidation));
            if (result.isEmpty() && !ExceptionsUtil.onlyNameResolutionTrace(this.errorsTrace.get())) {
                throw ExceptionsUtil.build(StatusCode.RESOURCE_LOOKUP_ERROR,
                        String.format("Unable to resolve [%s]", browsingDomain),
                        errorsTrace.get());
            }
        } catch (LookupException exception) {
            throw exception;
        } finally {
            errorsTrace.remove();
        }

        return result;
    }

    @Override
    public boolean isDnsSecValid(Fqdn name) throws LookupException, ConfigurationException 
    {
        ValidatorUtil.isValidDomainName(name);
        validatedConf();
        if (name == null || name.fqdn().isEmpty()) {
            name = new Fqdn(this.dnsSecDomain);
        }

        Map<String, Resolver> resolvers = retrieveResolvers(true);
        Iterator<String> itrResolvers = resolvers.keySet().iterator();
        boolean validated = false;
        String server = null;
        do {
            server = itrResolvers.next();
            statusChange(FormattingUtil.server(server));
            statusChange(FormattingUtil.query(name, "", "A"));
            try {
                validated = DnsUtil.checkDnsSec(name, resolvers.get(server));
                if (validated) {
                    statusChange(FormattingUtil.response(String.format("Received Authentic Data for [%s]",
                            name.fqdn())));
                } else {
                    statusChange(FormattingUtil.response(String.format("Network error validating [%s]",
                            name.fqdn())));
                }
            } catch (LookupException le) {
                if (le.dnsError() == StatusCode.RESOURCE_LOOKUP_ERROR) {
                    statusChange(FormattingUtil.response(String.format("Unable to Resolve [%s]: Network/Server Error",
                            name.fqdn())));
                } else {
                    statusChange(FormattingUtil.response(String.format("Unable to Authenticate [%s]: Network/Server Error",
                            name.fqdn())));
                }
                throw le;
            }
        } while (itrResolvers.hasNext() && !validated);

        return validated;
    }

    /**
     * Private helper to retrieve a set of one or more instances of <code>Resolver</code> to carry
     * out the lookup.
     *
     * @param secValidation <code>true</code> iff DNSSEC validation id needed
     * @return Instance(s) of <code>Resolver</code>
     * @throws ConfigurationException In case instance(s) of <code>Resolver</code> cannot he
     * instantiated.
     */
    private Map<String, Resolver> retrieveResolvers(boolean secValidation) 
                                    throws ConfigurationException 
    {
        Map<String, Resolver> resolvers = new LinkedHashMap<>();
        if (this.dnsServer != null
                && (!this.dnsServer.getHostAddress().isEmpty()
                || !this.dnsServer.getCanonicalHostName().isEmpty())) {
            String server = ((this.dnsServer.getHostAddress().isEmpty())
                    ? this.dnsServer.getCanonicalHostName() : this.dnsServer.getHostAddress());
            resolvers.put(server, DnsUtil.getResolver(secValidation, this.trustAnchorDefault, server));
        } else {
            resolvers.putAll(DnsUtil.getResolvers(secValidation, this.trustAnchorDefault));
        }

        return resolvers;
    }

    /**
     * Resource Record holder type enumeration. It enumerates the types hold by DNS RRs.
     */
    private enum RrHolderType {

        NAMES, ZONES, TYPES, OTHER;
    }

    /**
     * Private inner helper class to implement DNS-specific lookup operations.
     *
     * @author pmaresca <pmaresca@verisign.com>
     * @version 1.0
     * @since 2015/05/02
     */
    private class ServicesLookupHelper 
    {

        public ServicesLookupHelper() 
        {
            super();
        }

        /**
         * Retrieve a set of Service Types from the browsing domain.
         *
         * @param browsingDomain <code>Fqdn</code> representing the browsing domain
         * @param secValidation  <code>true</code> in case secure browsing is needed
         * @return A set of <code>String</code> identifying the retrieved Service Types.
         * @throws LookupException In case of any unrecoverable error during the lookup process.
         * @throws ConfigurationException In case of wrong/faulty static and/or runtime
         * configuration.
         */
        public Set<String> serviceTypes(Fqdn browsingDomain, boolean secValidation)
                                throws LookupException, ConfigurationException 
        {
            Map<String, Resolver> resolvers = retrieveResolvers(secValidation);
            RecordsContainer set = new RecordsContainer();
            errorsTrace.get().clear();
            Iterator<String> itrResolvers = resolvers.keySet().iterator();
            LookupContext ctx = context(browsingDomain, Constants.SERVICES_DNS_SD_UDP, "", "",
                    Type.PTR, secValidation
            );
            String server = null;
            do {
                server = itrResolvers.next();
                Resolver resolver = resolvers.get(server);
                ctx.setResolver(resolver);
                statusChange(FormattingUtil.server(server));
                try {
                    Record[] records = lookup(ctx);
                    set.getLabels().addAll(helper.getServiceTypeNamesFromRecords(records, ctx));
                    statusChange(StatusChangeEvent.build(browsingDomain.fqdn(), Type.string(Type.PTR),
                            StatusChangeEvent.castedList(set.getLabels())));
                } catch (LookupException le) {
                    if (le.dnsError().equals(StatusCode.NETWORK_ERROR) && !itrResolvers.hasNext()) {
                        throw  le;
                    } else if (le.dnsError().equals(StatusCode.SERVER_ERROR)
                            || le.dnsError().equals(StatusCode.RESOURCE_INSECURE_ERROR)) {
                        throw le;
                    } else {
                        errorsTrace.get().put(
                                ExceptionsUtil.traceKey(resolver, browsingDomain.fqdn(),
                                        "Retrieving-Types"), le.dnsError());
                    }
                }
            } while (itrResolvers.hasNext() && set.getLabels().isEmpty());

            return set.getLabels();
        }

        /**
         * Retrieve a set of Text Resource Records from the browsing domain for the specified
         * <i>label</i>.
         *
         * @param browsingDomain <code>Fqdn</code> representing the browsing domain
         * @param label A label to be looked up
         * @param secValidation  <code>true</code> in case secure browsing is needed
         * @return A set of <code>String</code> identifying the retrieved Text records
         * @throws LookupException In case of any unrecoverable error during the lookup process.
         * @throws ConfigurationException In case of wrong/faulty static and/or runtime
         * configuration.
         */
        public Set<TextRecord> serviceTexts(Fqdn browsingDomain, String label, boolean secValidation)
                                    throws LookupException, ConfigurationException 
        {
            Map<String, Resolver> resolvers = retrieveResolvers(secValidation);
            RecordsContainer set = new RecordsContainer();
            errorsTrace.get().clear();
            Iterator<String> itrResolvers = resolvers.keySet().iterator();
            LookupContext ctx = context(browsingDomain, label, label, "", Type.TXT, secValidation);
            String server = null;
            do {
                server = itrResolvers.next();
                Resolver resolver = resolvers.get(server);
                ctx.setResolver(resolver);
                statusChange(FormattingUtil.server(server));
                try {
                    Record[] records = lookup(ctx);
                    parseRecords(records, set, "", RrHolderType.OTHER);
                    statusChange(StatusChangeEvent.build(browsingDomain.fqdnWithPrefix(label),
                            Type.string(Type.TXT), StatusChangeEvent.castedList(set.getTexts())));
                } catch (LookupException le) {
                    if (le.dnsError().equals(StatusCode.NETWORK_ERROR) && !itrResolvers.hasNext()) {
                        throw  le;
                    } else if (le.dnsError().equals(StatusCode.SERVER_ERROR)
                            || le.dnsError().equals(StatusCode.RESOURCE_INSECURE_ERROR)) {
                        throw le;
                    } else {
                        errorsTrace.get().put(
                                ExceptionsUtil.traceKey(resolver, browsingDomain.fqdnWithPrefix(label),
                                        "Retrieving-Texts"),
                                le.dnsError());
                    }
                }
            } while (itrResolvers.hasNext() && set.getTexts().isEmpty());

            return set.getTexts();
        }

        /**
         * Retrieve a set of Service Resource Records from the browsing domain, according to the
         * specified <i>type</i>.
         *
         * @param browsingDomain <code>Fqdn</code> representing the browsing domain
         * @param type A <code>String</code> defining the Service Type to be looked up
         * @param secValidation  <code>true</code> in case secure browsing is needed
         * 
         * @return A set of <code>String</code> identifying the retrieve Service records.
         * 
         * @throws LookupException In case of any unrecoverable error during the lookup process.
         * @throws ConfigurationException In case of wrong/faulty static and/or runtime
         * configuration.
         */
        public Set<ServiceRecord> serviceRecords(Fqdn browsingDomain, String type, boolean secValidation)
                                    throws LookupException, ConfigurationException 
        {
            Map<String, Resolver> resolvers = retrieveResolvers(secValidation);
            Set<ServiceRecord> records = new TreeSet<>();
            errorsTrace.get().clear();
            Iterator<String> itrResolvers = resolvers.keySet().iterator();
            LookupContext ctx = context(browsingDomain, "", "", type, Type.PTR, secValidation);
            String server = null;
            do {
                server = itrResolvers.next();
                Resolver resolver = resolvers.get(server);
                ctx.setResolver(resolver);
                statusChange(FormattingUtil.server(server));
                try {
                    String dnsLabel = retrieveDnsLabel(ctx);
                    ctx.setDnsLabel(dnsLabel);
                    statusChange(StatusChangeEvent.build(ctx.getDomainName().fqdnWithPrefix(ctx.getPrefix()),
                            Type.string(ctx.getRrType()),
                            StatusChangeEvent.castedValue(ctx.getDomainName().fqdnWithPrefix(dnsLabel))));
                    Set<String> zones = retrieveDnsZones(ctx);
                    ctx.setDomainName(browsingDomain);
                    statusChange(StatusChangeEvent.build(ctx.getDomainName().fqdnWithPrefix(ctx.getPrefix()),
                            Type.string(ctx.getRrType()),
                            StatusChangeEvent.castedList(zones)));
                    Set<String> names = retrieveDnsNames(ctx, zones);
                    ctx.setDomainName(browsingDomain);
                    statusChange(StatusChangeEvent.build(ctx.getDomainName().fqdnWithPrefix(ctx.getPrefix()),
                            Type.string(ctx.getRrType()),
                            StatusChangeEvent.castedList(names)));
                    records.addAll(retrieveDnsRecords(ctx, names));
                    ctx.setDomainName(browsingDomain);
                    statusChange(StatusChangeEvent.build(ctx.getDomainName().fqdnWithPrefix(ctx.getPrefix()),
                            Type.string(ctx.getRrType()),
                            StatusChangeEvent.castedList(records)));
                } catch (LookupException le) {
                    if (le.dnsError().equals(StatusCode.NETWORK_ERROR) && !itrResolvers.hasNext()) {
                        throw  le;
                    } else if (le.dnsError().equals(StatusCode.SERVER_ERROR)
                            || le.dnsError().equals(StatusCode.RESOURCE_INSECURE_ERROR)) {
                        throw le;
                    } else {
                        errorsTrace.get().put(
                                ExceptionsUtil.traceKey(resolver, browsingDomain.fqdnWithPrefix(type),
                                        "Retrieving-Records"),
                                le.dnsError());
                    }
                }
            } while (itrResolvers.hasNext() && records.isEmpty());

            return records;
        }

        /**
         * Retrieve a set of Service Resource Records from the browsing domain, according to the
         * specified <i>type</i>.
         *
         * @param browsingDomain <code>Fqdn</code> representing the browsing domain
         * @param type A <code>String</code> defining the Service Type to be looked up
         * @param secValidation  <code>true</code> in case secure browsing is needed
         * 
         * @return A set of <code>String</code> identifying the retrieve Service records.
         * 
         * @throws LookupException In case of any unrecoverable error during the lookup process.
         * @throws ConfigurationException In case of wrong/faulty static and/or runtime
         * configuration.
         */
        public Set<ServiceInstance> serviceInstances(Fqdn browsingDomain, String type, boolean secValidation)
                                        throws LookupException, ConfigurationException 
        {
            Map<String, Resolver> resolvers = retrieveResolvers(secValidation);
            Set<ServiceInstance> instances = new TreeSet<>();
            errorsTrace.get().clear();
            Iterator<String> itrResolvers = resolvers.keySet().iterator();
            LookupContext ctx = context(browsingDomain, "", "", type, Type.PTR, secValidation);
            String server = null;
            do {
                server = itrResolvers.next();
                Resolver resolver = resolvers.get(server);
                ctx.setResolver(resolver);
                statusChange(FormattingUtil.server(server));
                try {
                    String dnsLabel = retrieveDnsLabel(ctx);
                    ctx.setDnsLabel(dnsLabel);
                    statusChange(StatusChangeEvent.build(ctx.getDomainName().fqdnWithPrefix(ctx.getPrefix()),
                            Type.string(ctx.getRrType()),
                            StatusChangeEvent.castedValue(ctx.getDomainName().fqdnWithPrefix(dnsLabel))));
                    Set<String> zones = retrieveDnsZones(ctx);
                    ctx.setDomainName(browsingDomain);
                    statusChange(StatusChangeEvent.build(ctx.getDomainName().fqdnWithPrefix(ctx.getPrefix()),
                            Type.string(ctx.getRrType()),
                            StatusChangeEvent.castedList(zones)));
                    Set<String> names = retrieveDnsNames(ctx, zones);
                    ctx.setDomainName(browsingDomain);
                    statusChange(StatusChangeEvent.build(ctx.getDomainName().fqdnWithPrefix(ctx.getPrefix()),
                            Type.string(ctx.getRrType()),
                            StatusChangeEvent.castedList(names)));
                    instances.addAll(retrieveDnsInstances(ctx, names));
                } catch (LookupException le) {
                    if (le.dnsError().equals(StatusCode.NETWORK_ERROR) && !itrResolvers.hasNext()) {
                        throw  le;
                    } else if (le.dnsError().equals(StatusCode.SERVER_ERROR)
                            || le.dnsError().equals(StatusCode.RESOURCE_INSECURE_ERROR)) {
                        throw le;
                    } else {
                        errorsTrace.get().put(
                                ExceptionsUtil.traceKey(resolver, browsingDomain.fqdnWithPrefix(type),
                                        "Retrieving-Instances"),
                                le.dnsError());
                    }
                }
            } while (itrResolvers.hasNext() && instances.isEmpty());

            return instances;
        }

        /**
         * 
         * Retrieve a set of TLSA Records from the browsing domain, according to the
         * specified <i>options</i>.
         *
         * @param browsingDomain <code>Fqdn</code> representing the browsing domain
         * @param tlsaPrefix A <code>String</code> defining the TLSA prefix as couple 
         *                   <code>port:protocol</code>
         * @param secValidation  <code>true</code> in case secure browsing is needed
         * 
         * @return A set of <code>String</code> identifying the retrieve Service records.
         * 
         * @throws LookupException        In case of any unrecoverable error during the lookup process.
         * @throws ConfigurationException In case of wrong/faulty static and/or runtime configuration.
         */
        public Set<TLSADiscoveryRecord> tlsaRecords(Fqdn browsingDomain, TLSAPrefix tlsaPrefix, 
                                                    boolean secValidation)
                                            throws LookupException, ConfigurationException 
        {
            Map<String, Resolver> resolvers = retrieveResolvers(secValidation);
            Set<TLSADiscoveryRecord> tlsaDiscoveryRecords = new TreeSet<>();
            errorsTrace.get().clear();
            Iterator<String> itrResolvers = resolvers.keySet().iterator();
            String tlsaFqdn = tlsaPrefix.toString() + Constants.DNS_LABEL_DELIMITER + browsingDomain.fqdn();
            Fqdn browsingDomainWithTLSAPrefix = new Fqdn(tlsaFqdn);
            LookupContext ctx = context(browsingDomainWithTLSAPrefix, "", "", "", Type.TLSA, secValidation);
            String server;
            do {
                server = itrResolvers.next();
                Resolver resolver = resolvers.get(server);
                ctx.setResolver(resolver);
                statusChange(FormattingUtil.server(server));
                try {
                    Record[] records = lookup(ctx);
                    for (Record record : records) {
                        if (record instanceof TLSARecord) {
                            tlsaDiscoveryRecords.add(new TLSADiscoveryRecord((TLSARecord) record));
                        }
                    }
                } catch (LookupException le) {
                    if (le.dnsError().equals(StatusCode.NETWORK_ERROR) && !itrResolvers.hasNext()) {
                        throw  le;
                    } else if (le.dnsError().equals(StatusCode.SERVER_ERROR)
                            || le.dnsError().equals(StatusCode.RESOURCE_INSECURE_ERROR)) {
                        throw le;
                    } else {
                        errorsTrace.get().put(
                                ExceptionsUtil.traceKey(resolver, browsingDomain.domain(),
                                        "Retrieving-Instances"),
                                le.dnsError());
                    }
                }
            } while (itrResolvers.hasNext() && tlsaDiscoveryRecords.isEmpty());

            return tlsaDiscoveryRecords;
        }

        /**
         * Instantiate and trigger a DNS lookup according to the defined input parameters.
         *
         * @param ctx A <code>LookupContext</code> defining this lookup parameters
         * @return A set of one or more Resource <code>Record</code>
         * @throws LookupException In case of unsuccessful DNS lookup; the <code>StatusCode</code>
         * is returned as part of this error.
         */
        private Record[] lookup(LookupContext ctx) throws LookupException 
        {
            Lookup lookup = DnsUtil.instantiateLookup(ctx.getDomainName().fqdnWithPrefix(ctx.getPrefix()),
                                                      ctx.getResolver(),
                                                      ctx.getRrType(),
                                                      smimeACache);
            ctx.setLookup(lookup);
            if(ctx.isSecure())
                DnsUtil.checkDnsSec(ctx.getDomainName(), ctx.getResolver());
            Record[] records = lookup.run();
            statusChange(FormattingUtil.query(ctx.getDomainName(), ctx.getPrefix(),
                         Type.string(ctx.getRrType())));
            StatusCode outcome = DnsUtil.checkLookupStatus(lookup);
            if (outcome.equals(StatusCode.SERVER_ERROR) || outcome.equals(StatusCode.NETWORK_ERROR)) {
                throw ExceptionsUtil.build(outcome, String.format("Unable to resolve [%s]",
                        ctx.getDomainName()),
                        errorsTrace.get());
            } else {
                errorsTrace.get().put(
                        ExceptionsUtil.traceKey(ctx.getResolver(), ctx.getResolver().toString() + ctx.getDomainName(),
                                "Checking-Lookup-Status"), outcome);
            }

            return (records == null?new Record[0]:records);
        }

        /**
         * Retrieve the DNS domain label for the browsing domain and specified Service Type.
         *
         * @param ctx A <code>LookupContext</code> defining this lookup parameters
         * @return A <code>String</code> containing the DNS domain label
         * @throws LookupException In case of unsuccessful DNS lookup; the <code>StatusCode</code>
         * is returned as part of this error.
         */
        private String retrieveDnsLabel(LookupContext ctx) throws LookupException 
        {
            ctx.setPrefix(ctx.getType() + Constants.NAME);
            ctx.setRrType(Type.PTR);
            Record[] records = lookup(ctx);
            String dnsLabel = null;
            if (records != null) {
                for (Record record : records) {
                    if (record instanceof PTRRecord) {
                        dnsLabel = PointerRecord.build((PTRRecord) record).getDnsLabel();
                    }
                }
            }

            if (dnsLabel == null) {
                throw ExceptionsUtil.build(StatusCode.SERVER_ERROR,
                        String.format("Unable to retrieve DNS Label for [%s]",
                                ctx.getDomainName().fqdnWithPrefix(ctx.getPrefix())),
                        errorsTrace.get());
            } else {
                return dnsLabel;
            }
        }

        /**
         * Retrieve the DNS Service's Zones.
         *
         * @param ctx A <code>LookupContext</code> defining this lookup parameters
         * @return A set of <code>String</code> containing the DNS zones
         * @throws LookupException In case of unsuccessful DNS lookup; the <code>StatusCode</code>
         * is returned as part of this error.
         */
        private Set<String> retrieveDnsZones(LookupContext ctx) throws LookupException 
        {
            ctx.setPrefix(Constants.SERVICES_DNS_SD_UDP);
            ctx.setRrType(Type.PTR);
            Record[] records = lookup(ctx);
            RecordsContainer set = new RecordsContainer();
            parseRecords(records, set, ctx.getDnsLabel(), RrHolderType.ZONES);

            return set.getLabels();
        }

        /**
         * Retrieve the DNS Service's Names.
         *
         * @param ctx A <code>LookupContext</code> defining this lookup parameters
         * @return A set of <code>String</code> containing the DNS service names
         * @throws LookupException In case of unsuccessful DNS lookup; the <code>StatusCode</code>
         * is returned as part of this error.
         */
        private Set<String> retrieveDnsNames(LookupContext ctx, Set<String> zones)
                                throws LookupException 
        {
            ctx.setPrefix(ctx.getDnsLabel());
            ctx.setRrType(Type.PTR);
            RecordsContainer set = new RecordsContainer();
            for (String zone : zones) {
                ctx.setDomainName(new Fqdn(zone));
                Record[] records = lookup(ctx);
                parseRecords(records, set, ctx.getDnsLabel(), RrHolderType.NAMES);
            }

            return set.getLabels();
        }

        /**
         * Retrieve the Service's records.
         *
         * @param ctx A <code>LookupContext</code> defining this lookup parameters
         * @return A set of <code>String</code> containing the service's records
         * @throws LookupException In case of unsuccessful DNS lookup; the <code>StatusCode</code>
         * is returned as part of this error.
         */
        private Set<ServiceRecord> retrieveDnsRecords(LookupContext ctx, Set<String> svcNames)
                                    throws LookupException 
        {
            RecordsContainer set = new RecordsContainer();
            ctx.setPrefix("");
            ctx.setRrType(Type.SRV);
            for (String svcName : svcNames) {
                ctx.setDomainName(new Fqdn(svcName));
                Record[] records = lookup(ctx);
                parseRecords(records, set, ctx.getDnsLabel(), RrHolderType.OTHER);
            }

            return set.getRecords();
        }

        /**
         * Retrieve the Service's instances.
         *
         * @param ctx A <code>LookupContext</code> defining this lookup parameters
         * @return A set of <code>String</code> containing the service's records
         * @throws LookupException In case of unsuccessful DNS lookup; the <code>StatusCode</code>
         * is returned as part of this error.
         */
        private Set<ServiceInstance> retrieveDnsInstances(LookupContext ctx, Set<String> svcNames)
                                        throws LookupException 
        {
            Set<ServiceInstance> svcInstances = new TreeSet<>();
            Set<String> aName = new LinkedHashSet<>();
            RecordsContainer set = new RecordsContainer();
            for (String svcName : svcNames) {
                aName.clear();
                set.getTexts().clear();
                aName.add(svcName);

                ctx.setPrefix(ctx.getDnsLabel());
                Set<ServiceRecord> svcRecords = retrieveDnsRecords(ctx, aName);
                statusChange(StatusChangeEvent.build(svcName, "", StatusChangeEvent.castedList(svcRecords)));
                if (svcRecords.isEmpty()) {
                    continue;
                }

                ctx.setPrefix("");
                ctx.setRrType(Type.TXT);
                ctx.setDomainName(new Fqdn(svcName));
                Record[] records = lookup(ctx);
                parseRecords(records, set, ctx.getLabel(), RrHolderType.OTHER);
                statusChange(StatusChangeEvent.build(svcName, "", StatusChangeEvent.castedList(set.getTexts())));
                if (set.getTexts().isEmpty()) {
                    continue;
                }

                svcInstances.add(new ServiceInstance(ctx.getType(), svcRecords.iterator().next(),
                        set.getTexts().iterator().next()));
            }

            return svcInstances;
        }

        /**
         * Scrapes the Discovery Service Records according to their nature.
         *
         * @param records An array of <code>Record</code> retrieve upon a lookup
         * @param set A <code>ResourcesContainer</code>
         * @param dnsLabel A <code>String</code> containing the extracted DNS Label
         * @param pht A Resource Record Type holder
         */
        private void parseRecords(Record[] records, final RecordsContainer set,
                                  String dnsLabel, RrHolderType pht) 
        {
            if (records != null) {
                for (Record record : records) {
                    if (record instanceof PTRRecord && pht == RrHolderType.ZONES) {
                        String zone = PointerRecord.build((PTRRecord) record).getServiceZone(dnsLabel);
                        if (zone != null) {
                            set.getLabels().add(zone);
                        }
                    } else if (record instanceof PTRRecord && pht == RrHolderType.NAMES) {
                        String name = PointerRecord.build((PTRRecord) record).getServiceName(dnsLabel);
                        if (name != null) {
                            set.getLabels().add(name);
                        }
                    } else if (record instanceof PTRRecord && pht == RrHolderType.TYPES) {
                        set.getLabels().add(PointerRecord.build((PTRRecord) record).getServiceType());
                    } else if (record instanceof SRVRecord) {
                        ServiceRecord svcRecord = ServiceRecord.build((SRVRecord) record);
                        if (svcRecord != null) {
                            set.getRecords().add(svcRecord);
                        }
                    } else if (record instanceof TXTRecord) {
                        set.getTexts().add(TextRecord.build((TXTRecord) record));
                    } else {
                        errorsTrace.get().put(
                                ExceptionsUtil.traceKey(record.toString(), dnsLabel,
                                        "Parsing-Service-Records"),
                                StatusCode.RESOURCE_UNEXPECTED);
                    }
                }
            }
        }

        public Set<String> getServiceTypeNamesFromRecords(Record[] records, LookupContext lookupContext) 
                                throws LookupException 
        {
            Set<String> serviceTypeNames = new HashSet<>();
            if (records.length > 0) {
                for (Record record : records) {
                    String dnsLabel = RDataUtil.getDnsLabelFromRData(record.rdataToString());
                    if (dnsLabel == null) {
                        continue;
                    }
                    String labelRecordName = dnsLabel + "._label";

                    LookupContext labelRecordContext
                            = context(lookupContext.getDomainName(), labelRecordName, "", "", Type.PTR, lookupContext.isSecure());
                    labelRecordContext.setResolver(lookupContext.getResolver());
                    Record[] nameRecordArray = lookup(labelRecordContext);

                    if (nameRecordArray.length == 0 || nameRecordArray[0] == null) {
                        continue;
                    }
                    Record nameRecord = nameRecordArray[0];
                    String serviceTypeName = RDataUtil.getServiceTypeNameFromRData(nameRecord.rdataToString());
                    if (serviceTypeName == null) {
                        continue;
                    }
                    serviceTypeNames.add(serviceTypeName);
                }
            }

            return serviceTypeNames;
        }

        /**
         * Create a Lookup Context to be passed over the nested calls.
         *
         * @param name A browsing domain
         * @param prefix The prefix label to be used
         * @param type An <code>int</code> specifying the Resource Record Type
         * @param sec    <code>true</code> iff DNSSEC validation is needed
         * @return A <code>LookupContext</code> created accordingly
         */
        // TODO RecordsContainer might be handled by the Context
        private LookupContext context(Fqdn name, String prefix, String label, String type,
                                      int rrType, boolean sec) 
        {
            LookupContext ctx = new LookupContext();
            ctx.setDomainName(name);
            ctx.setPrefix(prefix);
            ctx.setLabel(label);
            ctx.setType(type);
            ctx.setRrType(rrType);
            ctx.setSecure(sec);

            return ctx;
        }

    }

}
