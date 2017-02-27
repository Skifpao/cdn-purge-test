package com.handlers

import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponseStatus
import ratpack.func.Predicate
import ratpack.groovy.handling.GroovyChainAction
import ratpack.handling.Context

import static com.handlers.CacheControlValues.*

class CacheControlHandlers extends GroovyChainAction {

    void execute() {
        onlyIf prodMode(), {
            response.beforeSend { response ->
                if (!response.headers.contains(HttpHeaderNames.CACHE_CONTROL)) {
                    def cacheControlValue = DO_NOT_CACHE

                    def method = request.method
                    if (method.get || method.head) {
                        switch (response.status.code) {
                            case HttpResponseStatus.OK.code():
                            case HttpResponseStatus.NOT_FOUND.code():
                                cacheControlValue = CACHE_FOR_A_MINUTE
                                break

                            case HttpResponseStatus.MOVED_PERMANENTLY.code():
                                cacheControlValue = CACHE_FOR_AN_HOUR
                                break
                        }
                    }

                    response.headers.set(HttpHeaderNames.CACHE_CONTROL, cacheControlValue)
                }
            }
            next()
        }
    }

    private Predicate<Context> prodMode() {
        { ctx -> true }
    }
}
