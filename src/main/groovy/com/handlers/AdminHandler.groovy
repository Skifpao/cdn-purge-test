package com.handlers

import com.service.UpdatePageService
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.handling.InjectionHandler

import static ratpack.groovy.Groovy.groovyTemplate

/**
 * Created by skifpao on 24/02/2017.
 */
class AdminHandler extends InjectionHandler {
    void handle(Context ctx, UpdatePageService service) {
        service.html = ctx.pathTokens.html
        ctx.render ctx.pathTokens.html
    }

}
