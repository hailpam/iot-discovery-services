# iot-discovery-services 
Welcome to the iot-discovery-services project.
The main purpose of the discovery library is to allow its clients (device, gateway, etc.) to securely discover services (eg. a message broker) and configuration (eg. topic names) for a specific service type (eg. MQTT) in a given domain name.
It implements the [DNS-SD IETF RFC 7673 ](https://tools.ietf.org/html/rfc6763) which specifies how DNS resource records are named and structured to facilitate service discovery.

## Build
This is a Gradle project.

```
$ cd $PROJECT_HOME
$ gradle clean fatJar
```


##Service Discovery workflow

In order to perform service discovery, the first thing to do is to add the relevant records to DNS. As per the RFC, you need to provision a <service.domain> PTR record, which will point to the corresponding <instance.service.domain> SRV record(s). 
After the provisioning is done, you can lookup service instance names by service types for the domain name.

On the image below, a user is provisioning a mqtt service with the URL "iot.eclipse.org:1883" under domain name "example.com".

Using the iot-discovery-services library, the device then looks up services of type "mqtt" under domain name "example.com" and finds "iot.eclipse.org:1883".


![Provisioning and resolution workflow](https://github.com/rpiccand/iot-discovery-services/blob/master/img/dns-sd%20workflow.png)

## Using the Library
Here is a simple example which retrieves the "mqtt" service instances from "7pqg77uhvroq.1.iotverisign.com". Any DNS resolver can be used - however, to be considered secure, the Discovery process must rely on a DNSSEC validating resolver, which is the case for Verisign's 198.41.1.1. Also, the domain name must be dnssec-enabled.

```
package com.verisign.iot.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;

import com.verisign.iot.discovery.domain.Fqdn;
import com.verisign.iot.discovery.domain.ServiceInstance;
import com.verisign.iot.discovery.exceptions.ConfigurationException;
import com.verisign.iot.discovery.exceptions.LookupException;
import com.verisign.iot.discovery.services.DnsServicesDiscovery;

public class Discoverer {

	public static void main(String[] args) throws IOException, LookupException,
			ConfigurationException {

		Fqdn fullyQualifiedDomainName = new Fqdn("7pqg77uhvroq.1.iotverisign.com");
		String serviceType = "mqtt";
		String dnsResolver = "198.41.1.1";
		boolean checkDnssec = true;

		DnsServicesDiscovery discoverer = new DnsServicesDiscovery();
		discoverer.dnsServer(InetAddress.getByName(dnsResolver));

		Set<ServiceInstance> discoveryResult = discoverer.listServiceInstances(
				fullyQualifiedDomainName, serviceType, checkDnssec);

		for (ServiceInstance serviceInstance : discoveryResult) {
			System.out.println(serviceInstance);
		}

	}

}


```

## Using DNSSEC to secure the discovery process
The Domain Name System Security Extensions (DNSSec) is a technology designed to ensure the authenticity of DNS records  by applying PKI principles. The iot-discovery-services library validates the DNSSec records, which ensures that they are trustworthy. 

## Going further into DNS-SD DNS Records
The iot-discovery-services library abstracts the complexity of using raw DNS records. For the sake of completeness, this section describes a concrete example of provisioned DNS Records for service discovery. We use the command line [bind utility dig](https://www.isc.org/downloads/bind/) to retrieve the DNS Records. The domain name used to host this example is 7pqg77uhvroq.1.iotverisign.com.

### Listing service types
After new service instances have been provisioned, this command lists the DNS labels for which a service type was provisioned. It is a query for PTR records for the "_services._dns-sd._udp" label.

```
$ dig +noall +answer _services._dns-sd._udp.7pqg77uhvroq.1.iotverisign.com PTR

_services._dns-sd._udp.7pqg77uhvroq.1.iotverisign.com. 5 IN PTR	_mqtt._tcp.tkatlvzsjaoq.1.iotverisign.com.

```
The response indicates that there is one service type which label is "_mqtt._tcp" at "tkatlvzsjaoq.1.iotverisign.com". 
 
### Listing service names
Using the service type label, this command will list the existing names for "mqtt" services. It is a query for PTR records for the "_mqtt._tcp" label.

```
$ dig +noall +answer _mqtt._tcp.tkatlvzsjaoq.1.iotverisign.com. PTR
_mqtt._tcp.tkatlvzsjaoq.1.iotverisign.com. 5 IN	PTR eclipse._mqtt._tcp.tkatlvzsjaoq.1.iotverisign.com.
```

The response indicates that there is one service of type "mqtt" which label is "eclipse" at "_mqtt._tcp.tkatlvzsjaoq.1.iotverisign.com".

### Listing service instances
Using the service name label, this command will list the existing services instances. It is a query for ANY record associated with the service named "eclipse".

```
$ dig +noall +answer eclipse._mqtt._tcp.tkatlvzsjaoq.1.iotverisign.com. ANY
eclipse._mqtt._tcp.tkatlvzsjaoq.1.iotverisign.com. 5 IN	TXT "server=Mosquitto" "version=1.3.1"
eclipse._mqtt._tcp.tkatlvzsjaoq.1.iotverisign.com. 5 IN	SRV 0 0 1883 iot.eclipse.org.
```

The response indicates that there are two records:
- one SRV that specifies that the service URL is "iot.eclipse.org:1883"
- one TXT which gives some configuration information

Instead of using ANY, we could restrict the query to either SRV or TXT records.

# License
Eclipse Public License - v 1.0
https://www.eclipse.org/legal/epl-v10.html
