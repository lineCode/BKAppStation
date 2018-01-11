package com.xiaozi.appstore.activity

import android.app.Activity
import android.os.Bundle
import cc.fish.cld_ctrl.appstate.CldApp
import cc.fish.cld_ctrl.appstate.entity.RespUpdate
import com.xiaozi.appstore.manager.NetManager
import com.xiaozi.appstore.Call
import com.xiaozi.appstore.plugin._GSON
import com.xiaozi.appstore.safetyNullable

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUpdate()
    }

    private fun checkUpdate() {
        CldApp.checkUpdateForString {
            _GSON.fromJson(it, RespUpdate::class.java).safetyNullable {
                if (it == null || this!!.is_force == 0)
                    NetManager.loadAppConfig(this@SplashActivity) { Call(2000) { HomeActivity.open(this@SplashActivity, this) } }
                else
                    Call(2000) { HomeActivity.open(this@SplashActivity, this@safetyNullable) }
            }
        }
    }
}
