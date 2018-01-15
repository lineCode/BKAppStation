package com.xiaozi.appstore.activity.fragments

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import cc.fish.coreui.BaseFragment
import com.xiaozi.appstore.R
import com.xiaozi.appstore.activity.AboutActivity
import com.xiaozi.appstore.activity.FeedbackActivity
import com.xiaozi.appstore.safety
import com.xiaozi.appstore.safetySelf
import com.xiaozi.appstore.wxapi.WXHelper

/**
 * Created by fish on 18-1-4.
 */
class MineFragment : BaseFragment() {
    override fun initView(inflater: LayoutInflater) = inflater.inflate(R.layout.f_mine, null).safetySelf {
        initEffects(this)
    }

    private fun initEffects(view: View) {
        view.safety {
            findViewById<LinearLayout>(R.id.ll_fmine_login).setOnClickListener { WXHelper.login() }
            findViewById<RelativeLayout>(R.id.rl_fmine_download).setOnClickListener {  }
            findViewById<RelativeLayout>(R.id.rl_fmine_feedback).setOnClickListener { startActivity(Intent(activity, FeedbackActivity::class.java)) }
            findViewById<RelativeLayout>(R.id.rl_fmine_about).setOnClickListener { startActivity(Intent(activity, AboutActivity::class.java)) }
        }
    }


    override fun onSelected() {
    }

}