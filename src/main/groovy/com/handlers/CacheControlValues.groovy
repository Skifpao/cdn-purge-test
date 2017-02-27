package com.handlers

import io.netty.handler.codec.http.HttpHeaderValues

class CacheControlValues {

    public static final int STALE_CACHE_DURATION = 86400
    public static final String DO_NOT_CACHE = "$HttpHeaderValues.PRIVATE, $HttpHeaderValues.NO_STORE, $HttpHeaderValues.NO_CACHE, $HttpHeaderValues.MAX_AGE=0, $HttpHeaderValues.MUST_REVALIDATE"
    public static final String CACHE_FOR_A_MINUTE = "$HttpHeaderValues.PUBLIC, $HttpHeaderValues.MAX_AGE=60, stale-if-error=$STALE_CACHE_DURATION"
    public static final String CACHE_FOR_AN_HOUR = "$HttpHeaderValues.PUBLIC, $HttpHeaderValues.MAX_AGE=3600, stale-if-error=$STALE_CACHE_DURATION"
    public static final String CACHE_INDEFINITELY = "$HttpHeaderValues.PUBLIC, $HttpHeaderValues.MAX_AGE=31536000, stale-if-error=$STALE_CACHE_DURATION"

    private CacheControlValues() {
    }
}
