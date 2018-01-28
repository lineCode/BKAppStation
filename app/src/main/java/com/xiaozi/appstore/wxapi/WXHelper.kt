package com.xiaozi.appstore.wxapi

import android.content.Intent
import android.widget.Toast
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.xiaozi.appstore.component.Framework

/**
 * Created by fish on 18-1-15.
 */
class WXHelper {
    companion object {
        val STATE_LOGIN = "TO LOGIN"
        val EXTRA_SHARE_SUCCESS = "SHARE_SUCCESS"
        val BUNDLE_SHARE = "BUNDLE_SHARE"
        var isLoggingIn = false
        val APP_ID = "wx1f714ed004c09b23"
        val APP_SECRET = "9816401d6f518c6e11758e0478f1dd6d"



        fun login() {
            if (isLoggingIn) {
                return
            }
            isLoggingIn = true
            val req = SendAuth.Req()
            req.scope = "snsapi_userinfo"
            req.state = STATE_LOGIN
            try {
                WXAPIFactory.createWXAPI(Framework._C, APP_ID, true).sendReq(req)
            } catch (err: Exception) {
                err.printStackTrace()
                isLoggingIn = false
                Toast.makeText(Framework._C, "登录异常", Toast.LENGTH_SHORT).show()
            }
        }

    }
}