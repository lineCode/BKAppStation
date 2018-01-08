package com.xiaozi.appstore.manager

import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.view.AsyncWaiter

/**
 * Created by fish on 18-1-7.
 */
class AppListDataPresenterImpl(private val waiter: AsyncWaiter, private val onLoaded: Array<DataManager.AppInfo>.() -> Unit) : INetAppsPresenter {
    var isLoading = false
    override fun load(type: AppListType, filter: String) {
        if (isLoading) return
        waiter.show(false)
        NetManager.loadAppList(type, {
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