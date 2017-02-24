import com.handlers.PurgeHandler
import ratpack.groovy.template.TextTemplateModule

import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack

ratpack {
    bindings {
        module TextTemplateModule
    }

    handlers {
        get {
            redirect(301, "/date")
        }
        get ("date") {
            render groovyTemplate("skin.html", name: "Tester")
        }
        post "purge", new PurgeHandler()
    }

}