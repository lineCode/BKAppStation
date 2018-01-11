package com.xiaozi.appstore.manager

import android.content.Context
import com.xiaozi.appstore.component.Framework

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
                else -> {
                }
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

object AccountSPMgr : PreferenceManager(Module.Account)
object AppSPMgr : PreferenceManager(Module.App)
object ConfSPMgr : PreferenceManager(Module.Config)