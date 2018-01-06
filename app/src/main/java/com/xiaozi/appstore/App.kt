package com.xiaozi.appstore

import android.app.Application
import android.content.pm.PackageManager
import cc.fish.cld_ctrl.ad.CldAd
import cc.fish.cld_ctrl.common.net.NetManager
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.component.GlobalData

/**
 * Created by fish on 18-1-2.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Framework.mContext = applicationContext
        CldAd.init(applicationContext)
        initEnv()
    }

    private fun initEnv() {
        packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES).filter { it != null }.map { it.packageName }.run(GlobalData::initInstalledApp)
    }
}