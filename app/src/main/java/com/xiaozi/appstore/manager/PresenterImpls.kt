package com.xiaozi.appstore.manager

import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.view.AsyncWaiter

/**
 * Created by fish on 18-1-7.
 */
class HomeListDataPresenterImpl(private val waiter: AsyncWaiter, private val onLoaded: Array<DataManager.AppInfo>.() -> Unit) : INetAppListPresenter {
    var isLoading = false
    override fun load(type: AppListType) {
        if (isLoading) return
        waiter.show(false)
        NetManager.loadAppList(type, {
            DataManager.importApps(this)
            DataManager.mAppInfoCache.values.toTypedArray().onLoaded()
            isLoading = false
            waiter.hide(200)
        }) {
            waiter.activity.ZToast(this)
            isLoading = false
            waiter.hide(200)
        }
    }

}