package com.xiaozi.appstore.manager

import android.app.Activity
import cc.fish.cld_ctrl.common.util.AppUtils
import cc.fish.fishhttp.net.RequestHelper
import cc.fish.fishhttp.util.MD5Utils
import com.google.gson.reflect.TypeToken
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.component.Device
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.component.GlobalData
import com.xiaozi.appstore.manager.NetManager.UrlPassNullParam
import com.xiaozi.appstore.plugin._CLIENT_ID
import com.xiaozi.appstore.plugin._DEBUG
import com.xiaozi.appstore.plugin._KEY
import java.io.Serializable

/**
 * Created by fish on 18-1-2.
 */
object NetManager {

    val SUCCESS_CODE = 0
    private val _TEST_URL = "http://192.168.1.100:18079/v1"
    private val _PRODUCT_URL = ""
    private val MAIN_URL = if (_DEBUG) _TEST_URL else _PRODUCT_URL

    inline fun <reified T> fastCall(url: String, crossinline success: T.() -> Unit = {}, crossinline failed: String.() -> Unit = {}) = createOri<T>(url, success, failed).get(Framework._C, Framework._H)
    inline fun <reified T> fastCallBaseResp(url: String, crossinline success: T.() -> Unit = {}, crossinline failed: String.() -> Unit = {}) = createBase(url, success, failed).get(Framework._C, Framework._H)

    inline fun <reified T> createBase(url: String, crossinline success: T.() -> Unit, crossinline failed: String.() -> Unit) = RequestHelper<BaseResp<T>?>().apply {
        Url(url)
        Method(RequestHelper.Method.GET)
        System.currentTimeMillis().apply {
            UrlParam("timestamp", "${this}", true)
            UrlParam("apiToken", MD5Utils.md5Encrypt("$_CLIENT_ID$_KEY$this"))
        }
        UrlParam("androidId", Device.getAndroidID())
        UrlParam("clientVersion", "${AppUtils.getVersionCode(Framework._C)}")
        UrlParam("deviceModel", Device.DEVICE_NAME)
        UrlParam("imei", Device.getIMEI())
        UrlParam("mac", Device.getMacAddr())
        UrlParam("osVersion", Device.getMacAddr())
        HeadPassNullParam("userToken", AccountManager.token())
        ResultType(object : TypeToken<BaseResp<T>>() {})
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


    /**APIS **************/

    fun loadAppConfDry() = loadAppConfig(null) {}

    fun loadAppConfig(activity: Activity?, success: () -> Unit) {
        success()
        createBase<RespAppConf>("$MAIN_URL/cconfig", {
            GlobalData.storeAppConfig(this)
            success()
        }) {
            activity?.ZToast(this)
        }.ResultType(object : TypeToken<BaseResp<RespAppConf>>(){}).get(Framework._C, Framework._H)
    }

    fun loadAppList(type: String = AppListType.ALL.str, condition: String, keyword: String = "", index: Int = 0, success: RespAppList.() -> Unit, failed: String.() -> Unit) {
        createBase<RespAppList>("$MAIN_URL/app/list", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlPassNullParam("adstype", type)
                .UrlParam("condition", condition)
                .UrlPassNullParam("keyword", keyword)
                .UrlParam("number", "20")
                .UrlParam("start", "${1 + index * 20}")
                .ResultType(object : TypeToken<BaseResp<RespAppList>>(){}).get(Framework._C, Framework._H)
    }

    fun loadAssociateApps(pkg: String, success: RespAppList.() -> Unit, failed: String.() -> Unit) {
        createBase<RespAppList>("$MAIN_URL/app/list", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("associatedApp", pkg)
                .UrlParam("condition", "associate")
                .ResultType(object : TypeToken<BaseResp<RespAppList>>(){}).get(Framework._C, Framework._H)
    }

    fun loadCommentList(appId: Int, index: Int = 0, success: RespCommentList.() -> Unit, failed: String.() -> Unit) {
        createBase<RespCommentList>("$MAIN_URL/comment/list", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("appId", "$appId")
                .UrlParam("number", "20")
                .UrlParam("start", "${1 + index * 20}")
                .ResultType(object : TypeToken<BaseResp<RespCommentList>>(){}).get(Framework._C, Framework._H)
    }

    fun loadAppDetail(appId: Int, success: RespAppInfo.() -> Unit, failed: String.() -> Unit) {
        createBase<RespAppInfo>("$MAIN_URL/app/info", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("appId", "$appId")
                .ResultType(object : TypeToken<BaseResp<RespAppInfo>>(){}).get(Framework._C, Framework._H)
    }

    fun loadUserInfo(activity: Activity, userId: Int) {
        createBase<RespUserInfo>("$MAIN_URL/user/getinfo", {
        }, activity::ZToast)
                .Method(RequestHelper.Method.GET)
                .UrlParam("userId", "$userId")
                .ResultType(object : TypeToken<BaseResp<RespUserInfo>>(){}).get(Framework._C, Framework._H)
    }

    fun loadBanners(success: RespBanners.() -> Unit, failed: String.() -> Unit) {
        createBase<RespBanners>("$MAIN_URL/ad", success, failed)
                .Method(RequestHelper.Method.GET)
                .ResultType(object : TypeToken<BaseResp<RespBanners>>(){}).get(Framework._C, Framework._H)
    }

    fun loadHotWords(success: RespHots.() -> Unit) {
        createBase<RespHots>("$MAIN_URL/topsearchkw", success, {})
                .Method(RequestHelper.Method.GET)
                .ResultType(object : TypeToken<BaseResp<RespHots>>(){}).get(Framework._C, Framework._H)
    }

    fun applyComment(appId: Int, commentTxt: String, point: Int, userId: Int, userName: String, success: Any?.() -> Unit, failed: String.() -> Unit) {
        createBase<Any?>("$MAIN_URL/comment/add", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("appId", "$appId")
                .UrlParam("commentTxt", commentTxt)
                .UrlParam("point", "$point")
                .UrlParam("userId", "$userId")
                .UrlParam("userName", userName)
                .ResultType(object : TypeToken<BaseResp<Any?>>(){}).get(Framework._C, Framework._H)
    }

    fun applyFeedback(content: String, success: Any?.() -> Unit, failed: String.() -> Unit) {
        createBase<Any?>("$MAIN_URL/feedback", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("advise", content)
                .UrlParam("email", "")
                .UrlParam("qq", "")
                .UrlParam("userId", "")
                .ResultType(object : TypeToken<BaseResp<Any?>>(){}).get(Framework._C, Framework._H)
    }

    fun applyThumbsup(appId: Int, commentId: Int, userId: Int, success: Any?.() -> Unit, failed: String.() -> Unit) {
        createBase<Any?>("$MAIN_URL/comment/thumbup", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("appId", "$appId")
                .UrlParam("beThumbsup", "1")
                .UrlParam("commentId", "$commentId")
                .UrlParam("userId", "$userId")
                .ResultType(object : TypeToken<BaseResp<Any?>>(){}).get(Framework._C, Framework._H)
    }

    fun login(openId: String, unionId: String, userName: String, imgUrl: String, success: RespLoginInfo.() -> Unit, failed: String.() -> Unit) {
        createBase<RespLoginInfo>("$MAIN_URL/user/login", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("openId", openId)
                .UrlParam("unionId", unionId)
                .UrlParam("userName", userName)
                .UrlParam("userImageUrl", imgUrl)
                .ResultType(object : TypeToken<BaseResp<RespLoginInfo>>(){}).get(Framework._C, Framework._H)
    }

    fun RequestHelper<*>.UrlPassNullParam(key: String, value: String) = this.apply { if (value.isNotBlank()) UrlParam(key, value) }
    fun RequestHelper<*>.HeadPassNullParam(key: String, value: String) = this.apply { if (value.isNotBlank()) HeaderParam(key, value) }
}

enum class AppListType(val str: String) {
    ALL(""),
    APP("app"),
    GAME("game")
}

enum class AppCondition(val str: String) {
    HOT("hot"),
    TOP("top"),
    SEARCH("search"),
    ASSOCIATE("associate")
}

open class BaseResp<T>(var code: Int = 0, var msg: String = "", var data: T) : Serializable
data class RespAppConf(val configs: RespAppConfEntity) : Serializable
data class RespAppList(val appNodes: RespAppListEntity) : Serializable
data class RespAppInfo(val appInfo: RespAppInfoEntity) : Serializable
data class RespUserInfo(val userInfo: RespUserInfoEntity) : Serializable
data class RespCommentList(val comments: RespCommentListEntity) : Serializable
data class RespBanners(val banners: RespBannersEntity) : Serializable
data class RespLoginInfo(val userId: String, val userToken: String) : Serializable
data class RespHots(val names: Array<String>) : Serializable

data class RespAppConfEntity(val appClass: RespConfClz, val gameClass: RespConfClz, val timeStamp: Long)
data class RespConfClz(val `class`: Array<RespAppClass>)
data class RespAppClass(val id: Int, val name: String, val subclass: Array<RespClassSec>)
data class RespClassSec(val id: Int, val name: String)

data class RespAppListEntity(val node: Array<RespAppListInfo>, val number: Int, val start: Int)
data class RespAppListInfo(val appDesc: String, val appName: String, val downloadCount: Int, val iconUrl: String,
                           val packageName: String, val size: Int, val sn: Int, val appId: Int, val downloadUrl: String)

data class RespAppInfoEntity(val adType: String, val appName: String, val commentCount: Int, val desContent: String,
                             val appId: Int, val downloadUrl: String, val iconUrl: String, val imgUrls: Array<String>,
                             val packageName: String, val point: Int, val size: Int, val tips: String, val updateLog: String)

data class RespUserInfoEntity(val userId: Int, val userImageUrl: String, val userName: String)

data class RespCommentListEntity(val node: Array<RespComment>, val number: Int, val start: Int, val total: Int)
data class RespComment(val authorName: String, val thumbsupSign: Int, val content: String, val date: Long, val thumbsupCount: Int, val commentId: Int)

data class RespBannersEntity(val banner: Array<RespBanner>)
data class RespBanner(val image: String, val link: String, val sn: Int)
