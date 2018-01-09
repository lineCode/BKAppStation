package com.xiaozi.appstore.manager

/**
 * Created by fish on 18-1-7.
 */
interface INetAppsPresenter {
    fun load(type: String = AppListType.ALL.str, filter: String = "")
}

interface ICachedDataPresenter<out T> {
    fun get(tag: String): T?
}