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
        val APP_ID = ""
        val APP_SECRET = ""

        val SHARE_TO_SESSION = SendMessageToWX.Req.WXSceneSession
        val SHARE_TO_TIME_LINE = SendMessageToWX.Req.WXSceneTimeline

        val SHARE_TITLE = "最能挣钱的安卓应用！"
        val SHARE_CONTENT = "试玩应用就给钱，邀请徒弟就给钱，徒弟试玩就给钱，任务天天有，今天特别多，来试试最新最好玩的应用吧，顺便赚个零花钱！"


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