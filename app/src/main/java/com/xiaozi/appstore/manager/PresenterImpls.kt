package com.xiaozi.appstore.manager

import android.app.Activity
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
            waiter.activity::ZToast
            arrayOf<DataManager.AppInfo>().onLoaded()
            waiter.hide(200)
        }
    }
}

open class CommentListPresenterImpl(private val waiter: AsyncWaiter, private val appId: Int, private val onLoaded: Array<DataManager.Comment>.(isAppend: Boolean) -> Unit) : INetAppsPresenter {
    override fun load(showWaiter: Boolean, index: Int) {
        if (waiter.isWaiting) return
        if (showWaiter)
            waiter.show(false)
        else
            waiter.showHidden()
        NetManager.loadCommentList(appId, index, {
            DataManager.Transor.CommentTransor(this).onLoaded(index != 0)
            waiter.hide(200)
        }) {
            waiter.activity::ZToast
            arrayOf<DataManager.Comment>().onLoaded(index != 0)
            waiter.hide(200)
        }

    }

}

open class AppDetailPresenterImpl(private val mWaiter: AsyncWaiter, private val appId: Int, private val onLoaded: (data: DataManager.AppDetail) -> Unit) : IDataPresenter {
    override fun load() {
        mWaiter.show()
        NetManager.loadAppDetail(appId, {
            mWaiter.hide(200)
            onLoaded(DataManager.Transor.AppDetailTransor(this))
        }) {
            mWaiter.hide(200)
            mWaiter.activity::ZToast
        }
    }
}

class CategoryPresenterImpl(private val type: AppListType, private val onLoaded: List<DataManager.Category>.() -> Unit) : IDataPresenter {
    override fun load() = when (type) {
        AppListType.APP -> DataManager.CategoryDM.mAppCategory.onLoaded()
        AppListType.GAME -> DataManager.CategoryDM.mGameCategory.onLoaded()
        else -> {
        }
    }
}

class SearchPresenterImpl(private val onLoaded: Array<String>.() -> Unit) : IDataPresenter {
    override fun load() {
        NetManager.loadHotWords{ names.onLoaded() }
    }
}

object PresenterImpls {
    val AppInfoCachedPresenterImpl = object : ICachedDataPresenter<DataManager.AppInfo> {
        override fun get(pkg: String) = DataManager.AppInfoDM.getAppInfo(pkg)
    }
}


