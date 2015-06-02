# iot-discovery-services 
Hi, and welcome to the iot-discovery-services project.
The main purpose of the discovery library is to allow its clients (device, gateway, etc.) to securely discover services (eg. a message broker) and configuration (eg. topic names) for a specific service type (eg. MQTT) in a given domain name.
It implements the [DNS-SD IETF RFC 7673 ](https://tools.ietf.org/html/rfc6763) which specifies how DNS resource records are named and structured to facilitate service discovery.
##Service Discovery workflow

The first thing to do is to add the relevant records to DNS. As per the RFC, you need to provision a <service.domain> PTR record, which will point to the corresponding <instance.service.domain> SRV record(s). 
After the provisioning is done, you can lookup service instances by types for the domain name.
