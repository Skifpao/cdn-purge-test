package com.wtf.domains

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.ToString
import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient

import static com.google.common.net.InetAddresses.isInetAddress

@SuppressWarnings("Println")
class PointDnsDomainProvisioner implements Provisioner {

    RESTClient pointDnsClient

    PointDnsDomainProvisioner(String username, String key) {
        pointDnsClient = new RESTClient("https://pointhq.com/zones/")
        pointDnsClient.authorization = new HTTPBasicAuthorization(username, key)
        pointDnsClient.defaultAcceptHeader = "application/json"
    }

    void addRecord(Domain zone, String type, String name, String data) {
        def existing = getRecord(zone, name)
        if (existing && existing.data.equals("${data}.".toString()) && existing.record_type.equals(type)) {
            println "DNS record ($name, $zone) already exists"
        } else {
            def updatedRecord = [
                zone_record: [
                    name       : name,
                    record_type: type,
                    data       : data,
                    ttl        : 3600
                ]
            ]
            if (existing) {
                pointDnsClient.put(path: "$zone/records/${existing.id}", headers: ["Content-Type": "application/json"]) {
                    json(updatedRecord)
                }
                println "Updated $type record for $name to $data in $zone zone"
            } else {
                pointDnsClient.post(path: "$zone/records", headers: ["Content-Type": "application/json"]) {
                    json(updatedRecord)
                }
                println "Added $type record for $name to $data in $zone zone"
            }
        }
    }

    ZoneRecord getRecord(Domain zone, String name) {
        def response = pointDnsClient.get(path: "$zone/records/?name=$name")
        def mapper = new ObjectMapper()
        List<ZoneRecordEntry> entries = mapper.readValue(response.contentAsString, mapper.getTypeFactory().constructCollectionType(List.class, ZoneRecordEntry.class))
        if (entries.size() == 1) {
            entries[0].zone_record
        } else if (entries.size() == 0) {
            null
        } else {
            throw new RuntimeException("More than one DNS record exists for query $name.$zone")
        }
    }

    void addRecord(Environment environment, Domain domain, SubDomain subDomain) {
        def hostname = environment.subdomain(subDomain.prefix)
        if (hostname) {
            def target = environment.dnsHost(domain)
            def type = isInetAddress(target) ? 'A' : 'CNAME'
            addRecord(domain, type, hostname, target)
        } else {
            println "Skipping adding DNS for APEX record"
        }
    }

    void provision(String hostname) {
        Domain.values().each { domain ->
            Environment.values().each { environment ->
                addRecord(environment, domain, SubDomain.valueOf(hostname))
            }
        }
    }
}

class ZoneRecordEntry {
    ZoneRecord zone_record
}

@ToString(includeNames = true, includePackage = false)
@JsonIgnoreProperties(ignoreUnknown = true)
class ZoneRecord {
    String name
    String data
    Long id
    String record_type
}

