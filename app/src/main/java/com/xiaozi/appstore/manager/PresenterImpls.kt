package com.xiaozi.appstore.manager

import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.view.AsyncWaiter

/**
 * Created by fish on 18-1-7.
 */
open class AppListDataPresenterImpl(private val waiter: AsyncWaiter, private val type: String, private val condition: String, private val keyword: String = "", private val onLoaded: Array<DataManager.AppInfo>.() -> Unit) : INetAppsPresenter {
    var isFirstLoad = true
    override fun load(showWaiter: Boolean, index: Int) {
        if (waiter.showing()) return
        if (showWaiter || isFirstLoad)
            waiter.show(false)
        else
            waiter.showHidden()
        isFirstLoad = false
        NetManager.loadAppList(type, condition, keyword, index, {
            if (index == 0)
                DataManager.AppInfoDM.importApps(this.appNodes.node)
            else
                DataManager.AppInfoDM.appendApps(this.appNodes.node)
            DataManager.AppInfoDM.getAppInfos().onLoaded()
            waiter.hide(200)
        }) {
            waiter.activity.ZToast(this)
            waiter.hide(200)
        }
    }
}

class CommentListPresenterImpl(private val waiter: AsyncWaiter, private val pkg: String, private val onLoaded: Array<DataManager.AppInfo>.() -> Unit) : INetAppsPresenter {
    override fun load(showWaiter: Boolean, index: Int) {

    }

}



object PresenterImpls {
    val AppInfoCachedPresenterImpl = object : ICachedDataPresenter<DataManager.AppInfo> {
        override fun get(pkg: String) = DataManager.AppInfoDM.getAppInfo(pkg)
    }
}


