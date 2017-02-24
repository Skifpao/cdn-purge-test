import com.AdminModule
import com.handlers.AdminHandler
import com.handlers.PurgeHandler
import com.handlers.ViewHandler
import ratpack.groovy.template.TextTemplateModule

import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack

ratpack {
    bindings {
        module TextTemplateModule
        module AdminModule
    }

    handlers {
        get {
            redirect(301, "/admin")
        }
        get("admin") { render groovyTemplate("admin.html") }
        post "addHtml", new AdminHandler()
        post "purge", new PurgeHandler()

        get("date", new ViewHandler())
    }

}