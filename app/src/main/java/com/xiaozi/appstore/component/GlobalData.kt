package com.xiaozi.appstore.component

import com.xiaozi.appstore.manager.AppConf

/**
 * Created by fish on 18-1-2.
 */
object GlobalData {
    private var mAppConf: AppConf? = null
    private val mInstalledApps = mutableSetOf<String>()


    fun storeAppConfig(config: AppConf) {
        mAppConf = config
    }

    fun getAppConfig() = mAppConf

    fun isAppInstalled(pkg: String) = pkg in mInstalledApps
    fun addInstalledApp(pkg: String) = mInstalledApps.add(pkg)
    fun initInstalledApp(pkgs: List<String>) = mInstalledApps.addAll(pkgs)
    fun removeInstalledApp(pkg: String) = mInstalledApps.remove(pkg)
}