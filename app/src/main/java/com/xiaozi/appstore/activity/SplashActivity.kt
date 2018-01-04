package com.xiaozi.appstore.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.xiaozi.appstore.R
import com.xiaozi.appstore.manager.ConfSPMgr
import com.xiaozi.appstore.manager.NetManager
import com.xiaozi.appstore.plugin.ZToast
import com.xiaozi.appstore.plugin.safetyRun
import com.xiaozi.appstore.view.AsyncWaiter
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splash)
//        ConfSPMgr.putValue("123", "456")
//        tv.setOnClickListener { NetManager.fastCall<String>("http://192.168.1.201:60001/yqz/debug/conf", { ZToast(this) }) { ZToast(this) } }

        val s = AsyncWaiter()
        s.show(this@SplashActivity)
        s.hide(3000L)

    }
}
