package com.xiaozi.appstore.manager

/**
 * Created by fish on 18-1-7.
 */
interface INetAppsPresenter {
    fun load(type: AppListType = AppListType.ALL, filter: String = "")
}
