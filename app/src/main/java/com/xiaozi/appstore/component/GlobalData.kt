package com.xiaozi.appstore.component

import com.xiaozi.appstore.manager.NetManager
import com.xiaozi.appstore.manager.RespAppConf


/**
 * Created by fish on 18-1-2.
 */
object GlobalData {
    private var mAppConf: RespAppConf? = null
    fun storeAppConfig(config: RespAppConf) {
        mAppConf = config
    }

    fun getAppConfig() = mAppConf
}