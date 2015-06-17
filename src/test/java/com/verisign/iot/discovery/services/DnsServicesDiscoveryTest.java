package com.verisign.iot.discovery.services;

import com.verisign.iot.discovery.commons.Constants;
import com.verisign.iot.discovery.domain.Fqdn;
import com.verisign.iot.discovery.domain.ServiceInstance;
import com.verisign.iot.discovery.domain.ServiceRecord;
import com.verisign.iot.discovery.domain.TextRecord;
import com.verisign.iot.discovery.exceptions.ConfigurationException;
import com.verisign.iot.discovery.exceptions.LookupException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author pmaresca <pmaresca@verisign.com>
 */
public class DnsServicesDiscoveryTest implements Observer 
{

    public static final String DNS_RESOVLER = "198.41.1.1";
    public static final String DNS_RESOVLER_1 = "8.8.8.8";
    public static final String BAD_RESOVLER = "1.2.3.4";
    public static final String SERVICE_TYPE = "coapspecial";
    public static final String SERVICE_DOMAIN = "n67423p6tgxq.1.iotverisign.com";
    public static final String SERVICE_DOMAIN_1 = "kfjljohydgsa.1.iotqa.end-points.com";
    public static final String TEST_DOMAIN = "com";
    public static final String SERVICE_LABEL = "coap";
    public static final String SERVICE_TYPE_1 = "mqft";
    public static final String SERVICE_NAME = "_coapspecial._udp.avu7unxcs7ia.1.iotverisign.com";
    public static final String SERVICE_TEXT = "f5j4pf5vaw1osjnj4nggdmy2ycl1axlm64knkrayhfsstcxe56ctwnxho1coap";
    public static final String BAD_SERVICE_DOMAIN = "google.totosdfgsdfgsdfgsdfgsdfgsdfgsdfgsdfg";

    private DnsServicesDiscovery discovery;

    
    public DnsServicesDiscoveryTest() 
    {
    }

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void listServiceInstances() 
    {
        try {
            this.discovery = new DnsServicesDiscovery();
            this.discovery.dnsSecDomain(Constants.DEFAULT_DNSSEC_DOMAIN)
                          .dnsServer(InetAddress.getByName(DNS_RESOVLER))
                          .trustAnchorDefault(Constants.DEFAULT_TRUST_ANCHOR)
                          .introspected(true)
                          .observer(this)
                          .checkConfiguration(true);
        } catch (UnknownHostException ex) {
            Assert.fail("Expected correct initialization, not " + ex.toString());
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }
        Fqdn name = new Fqdn(SERVICE_DOMAIN);
        try {
            Set<ServiceInstance> inst = this.discovery.listServiceInstances(name, SERVICE_TYPE, false);
            Assert.assertTrue(inst.size() > 0);
        } catch (LookupException ex) {
            Assert.fail("Expected successful lookup, not " + ex.toString());
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }

    }

    @Test
    public void listServiceInstancesError() 
    {
        try {
            this.discovery = new DnsServicesDiscovery();
            this.discovery.dnsSecDomain(Constants.DEFAULT_DNSSEC_DOMAIN)
                          .dnsServer(InetAddress.getByName(DNS_RESOVLER))
                          .trustAnchorDefault(Constants.DEFAULT_TRUST_ANCHOR)
                          .introspected(true)
                          .observer(this)
                          .checkConfiguration(true);
        } catch (UnknownHostException ex) {
            Assert.fail("Expected correct initialization, not " + ex.toString());
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }
        Fqdn name = new Fqdn(SERVICE_DOMAIN_1);
        try {
            Set<ServiceInstance> inst = this.discovery.listServiceInstances(name, SERVICE_TYPE_1, false);
            Assert.fail("Expected Lookup Error");
        } catch (LookupException ex) {
            Assert.assertTrue(true);
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }

    }

    @Test
    public void listServiceInstancesErrorDomainNotExistent() 
    {
        try {
            this.discovery = new DnsServicesDiscovery();
            this.discovery.dnsSecDomain(Constants.DEFAULT_DNSSEC_DOMAIN)
                          .dnsServer(InetAddress.getByName(DNS_RESOVLER_1))
                          .trustAnchorDefault(Constants.DEFAULT_TRUST_ANCHOR)
                          .introspected(true)
                          .observer(this)
                          .checkConfiguration(true);
        } catch (UnknownHostException ex) {
            Assert.fail("Expected correct initialization, not " + ex.toString());
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }
        Fqdn name = new Fqdn("habla.1.iotverisign.com");
        try {
            Set<ServiceInstance> inst = this.discovery.listServiceInstances(name, "mqtt", false);
            Assert.fail("Expected a Lookup Error");
        } catch (LookupException ex) {
            Assert.assertTrue(true);
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }

    }

    @Test
    public void checkDnsSecError() 
    {
        try {
            this.discovery = new DnsServicesDiscovery();
            this.discovery.dnsSecDomain(Constants.DEFAULT_DNSSEC_DOMAIN)
                          .trustAnchorDefault(Constants.DEFAULT_TRUST_ANCHOR)
                          .dnsServer(InetAddress.getByName(DNS_RESOVLER))
                          .introspected(true)
                          .observer(this)
                          .checkConfiguration(true);
            this.discovery.isDnsSecValid(new Fqdn(BAD_SERVICE_DOMAIN));
            Assert.fail("Expected DNSSEC validation failure");
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        } catch (UnknownHostException ex) {
            Assert.fail("Expected correct retrieval of localhost, not " + ex.toString());
        } catch (LookupException ex) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void checkDnsSecErrorNonExistentDNS() {
        try {
            this.discovery = new DnsServicesDiscovery();
            this.discovery.dnsSecDomain(Constants.DEFAULT_DNSSEC_DOMAIN)
                          .trustAnchorDefault(Constants.DEFAULT_TRUST_ANCHOR)
                          .dnsServer(InetAddress.getByName("127.1.1.1"))
                          .introspected(true)
                          .observer(this)
                          .checkConfiguration(true);
            this.discovery.isDnsSecValid(new Fqdn("google.com"));
            Assert.fail("Expected DNSSEC validation failure");
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        } catch (UnknownHostException ex) {
            Assert.fail("Expected correct retrieval of localhost, not " + ex.toString());
        } catch (LookupException ex) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void checkDnsSec() 
    {
        try {
            this.discovery = new DnsServicesDiscovery();
            this.discovery.dnsSecDomain(Constants.DEFAULT_DNSSEC_DOMAIN)
                         .trustAnchorDefault(Constants.DEFAULT_TRUST_ANCHOR)
                         .dnsServer(InetAddress.getByName(DNS_RESOVLER))
                         .introspected(true)
                         .observer(this)
                         .checkConfiguration(true);
            this.discovery.isDnsSecValid(new Fqdn(SERVICE_NAME));
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        } catch (UnknownHostException ex) {
            Assert.fail("Expected correct retrieval of localhost, not " + ex.toString());
        } catch (LookupException ex) {
            Assert.fail("Expected correct validation, not " + ex.toString());
        }
    }

    @Test
    public void checkDnsSecInvalid() {
        try {
            this.discovery = new DnsServicesDiscovery();
            this.discovery.dnsSecDomain(Constants.DEFAULT_DNSSEC_DOMAIN)
                          .trustAnchorDefault(Constants.DEFAULT_TRUST_ANCHOR)
                          .dnsServer(InetAddress.getByName("1.2.3.4"))
                          .introspected(true)
                          .observer(this)
                          .checkConfiguration(true);
            this.discovery.isDnsSecValid(new Fqdn("google.coma"));
            Assert.fail("Expected a Lookup Error: non-existing Resolver");
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }
        catch (UnknownHostException ex) {
            Assert.fail("Expected correct retrieval of localhost, not " + ex.toString());
        }
        catch (LookupException ex) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void listServiceRecords() {
        try {
            this.discovery = new DnsServicesDiscovery();
            this.discovery.dnsSecDomain(Constants.DEFAULT_DNSSEC_DOMAIN)
                          .dnsServer(InetAddress.getByName(DNS_RESOVLER))
                          .trustAnchorDefault(Constants.DEFAULT_TRUST_ANCHOR)
                          .introspected(true)
                          .observer(this)
                          .checkConfiguration(true);
        } catch (UnknownHostException ex) {
            Assert.fail("Expected correct initialization, not " + ex.toString());
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }
        Fqdn name = new Fqdn(SERVICE_DOMAIN);
        try {
            Set<ServiceRecord> rec = this.discovery.listServiceRecords(name, SERVICE_TYPE, false);
            Assert.assertTrue(rec.size() > 0);
        } catch (LookupException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }

    }

    @Test
    public void listServiceTypes() 
    {
        try {
            this.discovery = new DnsServicesDiscovery();
            this.discovery.dnsSecDomain(Constants.DEFAULT_DNSSEC_DOMAIN)
                          .dnsServer(InetAddress.getByName(DNS_RESOVLER))
                          .trustAnchorDefault(Constants.DEFAULT_TRUST_ANCHOR)
                        .  introspected(true)
                          .observer(this)
                          .checkConfiguration(true);
        } catch (UnknownHostException ex) {
            Assert.fail("Expected correct initialization, not " + ex.toString());
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }
        Fqdn name = new Fqdn(SERVICE_DOMAIN);
        try {
            Set<String> typ = this.discovery.listServiceTypes(name, false);
            Assert.assertTrue(typ.size() > 0);
        } catch (LookupException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }

    }

    @Test
    public void listServiceTexts() 
    {
        try {
            this.discovery = new DnsServicesDiscovery();
            this.discovery.dnsSecDomain(Constants.DEFAULT_DNSSEC_DOMAIN)
                          .dnsServer(InetAddress.getByName(DNS_RESOVLER))
                          .trustAnchorDefault(Constants.DEFAULT_TRUST_ANCHOR)
                          .introspected(true)
                          .observer(this)
                          .checkConfiguration(true);
        } catch (UnknownHostException ex) {
            Assert.fail("Expected correct initialization, not " + ex.toString());
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }
        Fqdn name = new Fqdn(SERVICE_DOMAIN);
        Set<TextRecord> rec = null;
        try {
            rec = this.discovery.listTextRecords(name, SERVICE_LABEL, false);
            Assert.assertTrue(rec.isEmpty());
        } catch (LookupException ex) {
            Assert.assertTrue(true);
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }

        try {
            name = new Fqdn(SERVICE_NAME);
            rec = this.discovery.listTextRecords(name, SERVICE_TEXT, false);
            Assert.assertTrue(rec.size() == 1);
        } catch (LookupException ex) {
            Assert.fail("Expected correct initialization, not " + ex.toString());
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }
    }

    @Test
    public void listServiceTextsBadResolver() 
    {
        try {
            this.discovery = new DnsServicesDiscovery();
            this.discovery.dnsSecDomain(Constants.DEFAULT_DNSSEC_DOMAIN)
                          .dnsServer(InetAddress.getByName(BAD_RESOVLER))
                          .trustAnchorDefault(Constants.DEFAULT_TRUST_ANCHOR)
                          .introspected(true)
                          .observer(this)
                          .checkConfiguration(true);
        } catch (UnknownHostException ex) {
            Assert.fail("Expected correct initialization, not " + ex.toString());
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }
        Fqdn name = new Fqdn(TEST_DOMAIN);
        Set<TextRecord> rec = null;
        try {
            rec = this.discovery.listTextRecords(name, "example", true);
            Assert.fail("Expected a Lookup Exception");
        } catch (LookupException ex) {
            Assert.assertTrue(true);
        } catch (ConfigurationException ex) {
            Assert.fail("Expected correct configuration, not " + ex.toString());
        }
    }

    @Override
    public void update(Observable o, Object o1) 
    {
        System.out.println(o1.toString());
    }

}
