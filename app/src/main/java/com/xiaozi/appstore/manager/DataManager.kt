package com.xiaozi.appstore.manager

import com.xiaozi.appstore.App

/**
 * Created by fish on 18-1-2.
 */
object DataManager {
    val mAppInfoCache: MutableMap<String, AppInfo> = HashMap()
    val mUploadInfoCache: MutableMap<String, UploadInfo> = HashMap()

    @Synchronized fun appendApps(apps: Array<RespAppInfo>) {
        apps.map {
            mAppInfoCache.put(it.pkg, transAppInfo(it))
            mUploadInfoCache.put(it.pkg, transAppUpload(it))
        }
    }

    fun importApps(apps: Array<RespAppInfo>) {
        mAppInfoCache.clear()
        mUploadInfoCache.clear()
        appendApps(apps)
    }
    private fun transAppUpload(resp: RespAppInfo) = UploadInfo(resp.pkg)
    private fun transAppInfo(resp: RespAppInfo): DataManager.AppInfo {
        TODO("NOT IMPLED")
    }

    data class Banner(val img: String)
    data class AppInfo(val name: String, val pkg: String, val icon: String, val size: String,
                       val tip: String, val content: String, val dlUrl: String, val installCnt: String)
    data class UploadInfo(val pkg: String)
}

