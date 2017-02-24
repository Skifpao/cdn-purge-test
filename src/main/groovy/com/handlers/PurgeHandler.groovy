package com.handlers

import com.service.PurgeService
import ratpack.handling.Context
import ratpack.handling.Handler

/**
 * Created by skifpao on 24/02/2017.
 */
class PurgeHandler implements Handler {

    @Override
    void handle(Context ctx) throws Exception {
        ctx.render ctx.request.path
    }
}
