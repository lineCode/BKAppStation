package com.xiaozi.appstore

import android.app.Application
import cc.fish.cld_ctrl.ad.CldAd
import cc.fish.cld_ctrl.common.net.NetManager
import com.xiaozi.appstore.component.Framework

/**
 * Created by fish on 18-1-2.
 */
class App : Application(){
    override fun onCreate() {
        super.onCreate()
        Framework.mContext = applicationContext
        CldAd.init(applicationContext)
    }
}