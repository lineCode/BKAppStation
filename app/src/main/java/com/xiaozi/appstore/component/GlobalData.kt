package com.xiaozi.appstore.component

import com.xiaozi.appstore.manager.AppConf

/**
 * Created by fish on 18-1-2.
 */
object GlobalData {
    private var mAppConf: AppConf? = null

    fun storeAppConfig(config: AppConf) {
        mAppConf = config
    }

    fun getAppConfig() = mAppConf
}