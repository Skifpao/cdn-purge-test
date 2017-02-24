package com.handlers

import com.service.UpdatePageService
import ratpack.form.Form
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.handling.InjectionHandler

import static ratpack.groovy.Groovy.groovyTemplate

/**
 * Created by skifpao on 24/02/2017.
 */
class AdminHandler extends InjectionHandler {
    void handle(Context ctx, UpdatePageService service) {
        ctx.byMethod {
            it.get {
                ctx.render groovyTemplate("viewPage.html")
            }.post {
                ctx.parse(Form).then({ data ->
                    service.html = data.get("textarea")
                    println service.html
                    ctx.redirect("/date")

                })

            }
        }
    }

}
