package com

import com.google.inject.AbstractModule
import com.service.UpdatePageService

/**
 * Created by skifpao on 24/02/2017.
 */
class AdminModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(UpdatePageService)
    }
}