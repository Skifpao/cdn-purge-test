package com.handlers

import com.service.UpdatePageService
import ratpack.handling.Context
import ratpack.handling.InjectionHandler

import static ratpack.groovy.Groovy.groovyTemplate

/**
 * Created by skifpao on 24/02/2017.
 */
class ViewHandler extends InjectionHandler {

    void handle(Context ctx, UpdatePageService service) {
//        println service.html
//        ctx.response.headers.add("Cache-Control","max-age=60")
        ctx.render groovyTemplate("result.html", result: service.html)
    }
}
