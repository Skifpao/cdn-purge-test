package com.service

import com.wtf.domains.Environment
import com.wtf.domains.fastly.FastlyClient

import static com.wtf.domains.FastlyCustomConfig.buildConfig

/**
 * Created by skifpao on 24/02/2017.
 */
class PurgeService {

     void provision(){
        client = new FastlyClient("7a400cb9df4954bfc46c246e5fcbd91c")

//       def provisioner =  new FastlyServiceProvisioner(client)
//        provisionPublished(Environment.PROD)
    }

    public static final ArrayList<String> RETAINED_QUERY_PARAMETERS = ['tracker_id', 'topics', 'sptoken', 'vip_code']
//    public static final String ERROR_PAGE_CONTENT = Resources.toString(getResource("503.html"), UTF_8)


    private final PUBLISHED_SERVICES = [
            (Environment.PROD): "aCN0Sr9VArXGM8mln1BKw"
    ]

    FastlyClient client

    void addDefaultCacheControl(String serviceId, int version) {
        client.addHeaderSetting(serviceId, version, "default_cache_control", "http.Cache-Control", '"max-age=60"', true)
        println "Added default cache control header to service $serviceId"
    }

    void addDefault404CacheSettings(String serviceId, int version) {
        def conditionName = "404_from_origin"
        client.addCondition(serviceId, version, conditionName, "beresp.status == 404", "CACHE")
        println "Added 404 response condition to service $serviceId"
        client.addCacheSetting(serviceId, version, "short_404_ttl", 5, 5, conditionName)
        println "Added default cache control header to service $serviceId"
    }

    void addAutoDiscoverBlock(String serviceId, int version) {
        def conditionName = "autodiscover_request"
        client.addCondition(serviceId, version, conditionName, 'req.url ~ "autodiscover.xml"', "REQUEST")
        println "Added AutoDiscover Block to service $serviceId"
        client.addRequestResponseObject(serviceId, version, "block_autodiscover", 404, "Not Found", conditionName, "text/plain", "This is not the autodiscover service you were looking for...")
        println "Added AutoDiscover response to service $serviceId"
    }

//    void addWwwRedirect(String serviceId, int version) {
//        def conditionName = "host_is_www"
//        client.addCondition(serviceId, version, conditionName, 'req.http.host ~ "^www"', "REQUEST")
//        println "Added Condition WWW to service $serviceId"
//
//        client.addRequestResponseObject(serviceId, version, "redirect_to_knect365", 301, "Moved Permanently", conditionName, "", "")
//        println "Added Response redirect_to_knect365 to service $serviceId"
//
//        client.addCondition(serviceId, version, 'redirect_to_knect365', 'req.http.host ~ "^www" && resp.status == 301', "RESPONSE")
//        println "Added Response condition redirect_to_knect365 service $serviceId"
//
//        client.addHeaderSetting(serviceId, version, "knect365_redirect", "http.location", '"https://knect365.com" req.url', false, "RESPONSE", 'redirect_to_knect365')
//        println "Added Header knect365_redirect to service $serviceId"
//    }

    void addServerHeaderOverwriting(String serviceId, int version) {
        client.addHeaderSetting(serviceId, version, "overwrite_server_header", "http.Server", '"SERVER"', false)
        println "Added Server header overwriting to service $serviceId"
    }

    void addQueryParameterStripping(String serviceId, int version) {
        String regex = "^(${RETAINED_QUERY_PARAMETERS.join('|')})\$"
        client.addHeaderSetting(serviceId, version, "strip_unwanted_query_parameters", "url", 'querystring.regfilter_except(req.url, "' + regex + '")', false, 'Request')
        println "Added Query stripping to service $serviceId"
    }

//    void addDefault503Response(String serviceId, int version) {
//        def conditionName = "is_503_response"
//        client.addCondition(serviceId, version, conditionName, "beresp.status == 503", "CACHE")
//        println "Added 503 response condition to service $serviceId"
//        client.addCacheResponseObject(serviceId, version, "default_503_page", 503, "Service Unavailable", conditionName, "text/html", "ERROR_PAGE_CONTENT")
//        println "Added 503 response to service $serviceId"
//    }

    void addDisallowPurgeResponse(String serviceId, int version) {
        def conditionName = "http_method_is_purge"
        client.addCondition(serviceId, version, conditionName, "req.request ~ \"PURGE\"", "REQUEST", 9)
        println "Added disallow purge request condition to service $serviceId"
        client.addRequestResponseObject(serviceId, version, "disallow_purge", 403, "Forbidden", conditionName, "text/plain", "Nope!")
        println "Added disallow purge request response to service $serviceId"
    }

    void addNonGetHeadOrPostResponse(String serviceId, int version) {
        def conditionName = "http_method_is_not_head_or_get_or_post"
        client.addCondition(serviceId, version, conditionName, "!(req.request ~ \"HEAD|GET|POST\")", "REQUEST")
        println "Added non head, get or post request condition to service $serviceId"
        client.addRequestResponseObject(serviceId, version, "prevent_non_get_or_head_or_post", 405, "Method Not Allowed", conditionName, "text/plain", "The method you are using is not allowed!")
        println "Added non head, get or post response to service $serviceId"
    }

    void addWordpressExploitTargetingRequestResponse(String serviceId, int version) {
        def conditionName = "wordpress_exploit_post"
        def statement = 'req.request == "POST" && req.http.User-Agent == "Mozilla/5.0 (Windows NT 6.0; rv:16.0) Gecko/20130722 Firefox/16.0"'
        client.addCondition(serviceId, version, conditionName, statement, "REQUEST")
        println "Added disallow wordpress exploit post request condition to service $serviceId"
        client.addRequestResponseObject(serviceId, version, "forbidden", 403, "Forbidden", conditionName, "text/plain", "Blocked!")
        println "Added bad request response to service $serviceId"
    }

    void addBlockedRequestResponse(String serviceId, int version) {
        def conditionName = "blocked_requests"
        def statement = 'req.url ~ "/passwd" || req.url ~ "windows/win.ini" || req.url ~ "/boot.ini"|| req.url ~ "/proc/version"'
        client.addCondition(serviceId, version, conditionName, statement, "REQUEST")
        println "Added blocked service $serviceId"
        client.addRequestResponseObject(serviceId, version, "blocked", 403, "Blocked", conditionName, "text/plain", "Blocked!")
        println "Added blocked request response to service $serviceId"
    }



    void provisionPublished(Environment environment) {
        def serviceId = lookupPublishedService(environment)
        def version = client.addNewVersion(serviceId)
        client.addBackend(serviceId, version, environment)
        client.addDomain(serviceId, version, 'https://cdn-purge-test.herokuapp.com')
        addAutoDiscoverBlock(serviceId, version)
//        addWwwRedirect(serviceId, version)
        addDefault404CacheSettings(serviceId, version)
//        if (environment == Environment.PROD) {
//        }
//        addDefault503Response(serviceId, version)
        addDefaultCacheControl(serviceId, version)
        addServerHeaderOverwriting(serviceId, version)
        addQueryParameterStripping(serviceId, version)
        addDisallowPurgeResponse(serviceId, version)
        addNonGetHeadOrPostResponse(serviceId, version)
        addWordpressExploitTargetingRequestResponse(serviceId, version)
        addBlockedRequestResponse(serviceId, version)

        client.updateDefaultTtl(serviceId, version)
        client.addSslForcingSetting(serviceId, version)
        client.activate(serviceId, version)
        client.addMainCustomVcl(serviceId, version, buildConfig("<html></html>"))
    }

//    private String lookupPreviewService(Environment environment) {
//        check(PREVIEW_SERVICES, environment)
//    }

    private String lookupPublishedService(Environment environment) {
        "aCN0Sr9VArXGM8mln1BKw"
    }

    private String check(Map<Environment, String> services, Environment environment) {
        if (services.containsKey(environment)) {
            services[environment]
        } else {
            throw new IllegalStateException("No service configured for $environment")
        }
    }
}
