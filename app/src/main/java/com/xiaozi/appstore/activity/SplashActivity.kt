package com.xiaozi.appstore.activity

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
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
        if (checkPermissions()) {
            checkUpdate()
        }
    }

    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 200)
                return false
            }
        } else {
            return true
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (requestCode == 200 && grantResults!![0] == PackageManager.PERMISSION_GRANTED) {
                checkUpdate()
            } else {
                finish()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            finish()
        }
    }
}
