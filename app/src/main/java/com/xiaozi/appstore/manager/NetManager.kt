package com.xiaozi.appstore.manager

import cc.fish.fishhttp.net.RequestHelper
import com.google.gson.reflect.TypeToken
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.plugin._DEBUG
import java.io.Serializable

/**
 * Created by fish on 18-1-2.
 */
object NetManager {

    val SUCCESS_CODE = 0
    private val _TEST_URL = ""
    private val _PRODUCT_URL = ""
    private val MAIN_URL = if (_DEBUG) _TEST_URL else _PRODUCT_URL

    inline fun <reified T> fastCall(url: String, crossinline success: T.() -> Unit = {}, crossinline failed: String.() -> Unit = {}) = createOri<T>(url, success, failed).get(Framework._C, Framework._H)

    inline fun <reified T> fastCallBaseResp(url: String, crossinline success: T.() -> Unit = {}, crossinline failed: String.() -> Unit = {}) = createBase(url, success, failed).get(Framework._C, Framework._H)


    inline fun <reified T> createBase(url: String, crossinline success: T.() -> Unit, crossinline failed: String.() -> Unit) = RequestHelper<BaseResp<T>?>().apply {
        Url(url)
        Method(RequestHelper.Method.GET)
        UrlParam("ts", "${System.currentTimeMillis()}", true)
        ResultType(object : TypeToken<BaseResp<T?>>() {})
        Success {
            if (it == null)
                "null response".failed()
            else if (it.code == SUCCESS_CODE)
                it.data.success()
            else
                it.msg.failed()
        }
        Failed { it.failed() }
    }

    inline fun <reified T> createOri(url: String, crossinline success: T.() -> Unit, crossinline failed: String.() -> Unit) = RequestHelper<T?>().apply {
        Url(url)
        UrlParam("ts", "${System.currentTimeMillis()}", true)
        ResultType(object : TypeToken<T?>() {})
        Method(RequestHelper.Method.GET)
        Success {
            if (it == null)
                "".failed()
            else
                it.success()
        }
        Failed { it.failed() }
    }

    fun loadAppConfig(success: () -> Unit) {
        success()
//        createBase<AppConf>("$MAIN_URL/app/conf", {
//            success()
//        }){}
    }

    fun loadAppList(type: AppListType = AppListType.ALL, filter: String = "", success: Array<RespAppInfo>.() -> Unit, failed: String.() -> Unit) {
        createBase<Array<RespAppInfo>>("$MAIN_URL/app/list", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("type", type.str)
                .apply { if(filter.isEmpty()) UrlParam("filter", filter) }
                .get(Framework._C, Framework._H)

    }
}

enum class AppListType(val str: String) {
    ALL("all"),
    HOT_APP("hot_app"),
    HOT_GAME("hot_game"),
    CHART_APP("chart_app"),
    CHART_GAME("chart_game")
}

data class BaseResp<Data>(var code: Int, var msg: String, var data: Data) : Serializable
data class RespAppConf(var conf: String)
data class RespAppInfo(var pkg: String)