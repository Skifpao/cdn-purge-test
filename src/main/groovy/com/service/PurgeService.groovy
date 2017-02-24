package com.service

import wslite.http.auth.HTTPAuthorization
import wslite.rest.RESTClient
/**
 * Created by skifpao on 24/02/2017.
 */
class PurgeService {

    RESTClient fastlyClient

    PurgeService(String key) {
        fastlyClient = new RESTClient("https://api.fastly.com/")
        fastlyClient.authorization = new FastlyAuthorization(key)
        fastlyClient.defaultAcceptHeader = "application/json"
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
