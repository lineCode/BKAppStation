package com.xiaozi.appstore.manager

import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.view.AsyncWaiter

/**
 * Created by fish on 18-1-7.
 */
class AppListDataPresenterImpl(private val waiter: AsyncWaiter, private val onLoaded: Array<DataManager.AppInfo>.() -> Unit) : INetAppsPresenter {
    var isLoading = false
    override fun load(type: String, filter: String) {
        if (isLoading) return
        waiter.show(false)
        NetManager.loadAppList(type, filter, {
            DataManager.AppInfoDM.importApps(this)
            DataManager.AppInfoDM.getAppInfos().onLoaded()
            isLoading = false
            waiter.hide(200)
        }) {
            waiter.activity.ZToast(this)
            isLoading = false
            waiter.hide(200)
        }
    }
}

class CategoryPresenterImpl(private val waiter: AsyncWaiter, private val onLoaded: Array<DataManager>.() -> Unit)

object PresenterImpls {
    val AppInfoCachedPresenterImpl = object : ICachedDataPresenter<DataManager.AppInfo> {
        override fun get(pkg: String) = DataManager.AppInfoDM.getAppInfo(pkg)
    }
    val AppUpdateCachedPresenterImpl = object : ICachedDataPresenter<DataManager.UploadInfo> {
        override fun get(pkg: String) = DataManager.AppInfoDM.getAppUploadInfo(pkg)
    }
}


