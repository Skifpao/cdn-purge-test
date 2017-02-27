package com.wtf.domains.fastly

import com.wtf.domains.Environment
import wslite.http.auth.HTTPAuthorization
import wslite.rest.RESTClient

@SuppressWarnings("Println")
class FastlyClient {

    RESTClient fastlyClient

    FastlyClient(String key) {
        fastlyClient = new RESTClient("https://api.fastly.com/")
        fastlyClient.authorization = new FastlyAuthorization(key)
        fastlyClient.defaultAcceptHeader = "application/json"
    }

    String createService(String name, Environment environment) {
        def prefix = environment.suffix.toUpperCase()
        if (prefix) {
            prefix = "${prefix} "
        }
        def serviceResponse = fastlyClient.post(path: "service") {
            urlenc name: "${prefix}Core-$name"
        }
        println "Added a Fastly service for $name with id $serviceResponse.json.id"
        serviceResponse.json.id
    }

    void addDomain(String serviceId, int version, String domain) {
        fastlyClient.post(path: "service/$serviceId/version/$version/domain") {
            urlenc name: domain
        }
        println "Added $domain as a domain to service $serviceId"
    }

    void addBackend(String serviceId, int version, Environment environment) {
        def backend = "cdn-purge-test.herokuapp.com"
        fastlyClient.post(path: "/service/$serviceId/version/${version}/backend") {
            urlenc([
                name              : backend,
                address           : backend,
//                port              : 443,
                connect_timeout   : 1000,
                first_byte_timeout: 15000,
//                error_threshold   : 3,
//                shield            : 'london_city-uk',
//                use_ssl           : false,
//                ssl_check_cert    : true,
//                ssl_cert_hostname : backend,
//                healthcheck       : 'status_page_check'
            ])
        }
        println "Added backend to service $serviceId for ${environment}"
    }

    int addNextVersion(String serviceId, int currentVersion) {
        def version = fastlyClient.put(path: "service/$serviceId/version/$currentVersion/clone").json.number
        println "Added cloned new version $version for service $serviceId"
        version
    }

    int addNewVersion(String serviceId) {
        def version = fastlyClient.post(path: "service/$serviceId/version").json.number
        println "Added clean new version $version for service $serviceId"
        version
    }

    void activate(String serviceId, int version) {
        fastlyClient.put(path: "service/$serviceId/version/$version/activate")
        println "Activated version $version for service $serviceId"
    }

    void addStatusCheck(String serviceId, int version, Environment environment, HealthCheckLevel level) {
        def backend = "${environment.herokuAppName}.herokuapp.com"
        fastlyClient.post(path: "/service/$serviceId/version/${version}/healthcheck") {
            urlenc(
                name: "status_page_check",
                method: "HEAD",
                path: "/version",
                timeout: 5000,
                check_interval: level.interval,
                host: backend,
                window: level.window,
                threshold: level.threshold,
                initial: level.initial
            )
        }
        println "Added status check to service $serviceId for environment $environment"
    }

    void updateDefaultTtl(String serviceId, int version) {
        fastlyClient.put(path: "/service/$serviceId/version/${version}/settings") {
            urlenc "general.default_ttl": 3701
        }
        println "Updated default TTL for service $serviceId"
    }

    void addSslForcingSetting(String serviceId, int version) {
        fastlyClient.post(path: "service/$serviceId/version/${version}/request_settings") {
            urlenc name: "default_request_settings", xff: "append", max_stale_age: 259200, force_ssl: 1
        }
        println "Added SSL forcing setting to service $serviceId"
    }

    void addHeaderSetting(String serviceId, int version, String name, String header, String value, boolean ignoreIfSet, String type = 'cache', String responseCondition = null) {
        def args = [
            name         : name,
            type         : type,
            action       : "set",
            dst          : header,
            src          : "$value",
            ignore_if_set: ignoreIfSet ? 1 : 0,
            priority     : 10
        ]

        if (responseCondition) {
            args.response_condition = responseCondition
        }

        fastlyClient.post(path: "service/$serviceId/version/${version}/header") {
            urlenc(args)
        }
    }

    void addCacheSetting(String serviceId, int version, String name, int ttl_in_seconds, int stale_ttl_in_seconds, String condition, String action = 'cache') {
        fastlyClient.post(path: "service/$serviceId/version/$version/cache_settings") {
            urlenc(
                name: name,
                cache_condition: condition,
                action: action,
                stale_ttl: stale_ttl_in_seconds,
                ttl: ttl_in_seconds
            )
        }
    }

    void addCondition(String serviceId, int version, String name, String statement, String type, int priority = 10) {
        fastlyClient.post(path: "service/$serviceId/version/$version/condition") {
            urlenc(
                name: name,
                statement: statement,
                type: type,
                priority: priority
            )
        }
    }

    void addResponseObject(Map additionalValues, String serviceId, int version = 1, String name, int status, String response, String contentType, String content) {
        def requestData = [
            name        : name,
            status      : status,
            response    : response,
            content_type: contentType,
            content     : content.stripIndent()
        ] + additionalValues
        fastlyClient.post(path: "service/$serviceId/version/$version/response_object") {
            urlenc requestData
        }
    }

    void addCacheResponseObject(String serviceId, int version, String name, int status, String response, String conditionName, String contentType, String content) {
        addResponseObject(serviceId, version, name, status, response, contentType, content, cache_condition: conditionName)
    }

    void addRequestResponseObject(String serviceId, int version, String name, int status, String response, String conditionName, String contentType, String content) {
        addResponseObject(serviceId, version, name, status, response, contentType, content, request_condition: conditionName)
    }

    int getEditableVersion(String serviceId) {
        def response = fastlyClient.get(path: "service/$serviceId/version")
        def highestVersion = response.json.max { it.number }
        highestVersion.active ? addNextVersion(serviceId, highestVersion.number) : highestVersion.number
    }

    List fetchServices() {
        fastlyClient.get(path: "/service").json
    }

    void addMainCustomVcl(String serviceId, int version, String content) {
        def vclName = 'main_vcl'
        def requestData = [
            name   : vclName,
            content: content
        ]
        fastlyClient.post(path: "service/$serviceId/version/$version/vcl") {
            urlenc requestData
        }
        fastlyClient.put(path: "service/$serviceId/version/$version/vcl/${vclName}/main")
        println "Added Main Custom VCL to service $serviceId"
    }

    private static class FastlyAuthorization implements HTTPAuthorization {

        private final String key

        FastlyAuthorization(String key) {
            this.key = key
        }

        @Override
        void authorize(Object conn) {
            conn.addRequestProperty("Fastly-Key", key)
        }

    }

}
