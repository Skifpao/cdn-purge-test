package com.handlers

import com.service.PurgeService
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.handling.InjectionHandler

/**
 * Created by skifpao on 24/02/2017.
 */
class PurgeHandler extends InjectionHandler {

    void handle(Context ctx, PurgeService service) {

        ctx.render service.provision()
//        println service.html
//        ctx.render groovyTemplate("result.html", result: service.html)
    }
}
