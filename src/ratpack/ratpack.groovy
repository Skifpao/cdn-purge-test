import com.AdminModule
import com.handlers.AdminHandler
import com.handlers.CacheControlHandlers
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
        insert new CacheControlHandlers()
        get {
            redirect(301, "/admin")
        }
        path "admin", new AdminHandler()

        post "purgeLayout", new PurgeHandler()

        get("date", new ViewHandler())
    }

}