package com.xiaozi.appstore.manager

import android.content.Context
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.plugin._GSON
import com.xiaozi.appstore.safety
import com.xiaozi.appstore.toNotNullMutableList
import java.io.Serializable

/**
 * Created by fish on 17-7-4.
 */
sealed class PreferenceManager(module: Module) {
    val module = module
    fun getSP() = Framework._C.getSharedPreferences(module.name, Context.MODE_PRIVATE)
    fun putValue(key: String, value: Any?) {
        if (value == null) {
            getSP().edit().remove(key).apply()
            return
        }
        getSP().edit().apply {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is String -> putString(key, value)
                else -> putString(key, _GSON.toJson(value))
            }
        }.apply()
    }

    fun addItem(key: String, data: String) {
        getSP().getStringSet(key, setOf()).let {
            with(mutableSetOf<String>()) {
                addAll(it)
                add(data)
                getSP().edit().putStringSet(key, it).apply()
            }
        }
    }

    fun getBooleanValue(key: String) = getSP().getBoolean(key, false)
    fun getIntValue(key: String, defaultValue: Int) = getSP().getInt(key, defaultValue)
    fun getIntValue(key: String) = getIntValue(key, -1)
    fun getStringValue(key: String) = getSP().getString(key, "")

    fun haveKey(key: String) = getSP().getString(key, null) != null

    enum class Module {
        Account,
        App,
        Config,
        Download,
    }
}

object AccountManager {
    val KEY_TOKEN = "TOKEN"

    var userName = ""
    var userHeadIcon = ""
    var userId = 0

    fun storeToken(token: String) = AccountSPMgr.putValue(KEY_TOKEN, token)
    fun isLoggedIn() = AccountSPMgr.haveKey(KEY_TOKEN)
    fun token() = AccountSPMgr.getStringValue(KEY_TOKEN)
}

object DownloadInfoManager {
    val KEY_DOWNLOADS = "downloads"
    val downloadInfos = DownloadSPMgr.getSP().all.map { _GSON.safety { fromJson(it.value as String, DownloadInfo::class.java) } }.toNotNullMutableList()

    fun storeInfo(info: DownloadInfo) {
        DownloadSPMgr.putValue(info.tag, info)
        downloadInfos.add(info)
    }

    data class DownloadInfo(val url: String, val path: String, val name: String, val tag: String, val size: Int, var ptr: Int)
}

object AccountSPMgr : PreferenceManager(Module.Account)
object AppSPMgr : PreferenceManager(Module.App)
object ConfSPMgr : PreferenceManager(Module.Config)
object DownloadSPMgr : PreferenceManager(Module.Download)