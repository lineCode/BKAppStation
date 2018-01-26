package com.xiaozi.appstore.activity.fragments

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import cc.fish.coreui.BaseFragment
import com.xiaozi.appstore.R
import com.xiaozi.appstore.activity.AboutActivity
import com.xiaozi.appstore.activity.DownloadMgrActivity
import com.xiaozi.appstore.activity.FeedbackActivity
import com.xiaozi.appstore.activity.SearchActivity
import com.xiaozi.appstore.manager.AccountManager
import com.xiaozi.appstore.manager.ConfManager
import com.xiaozi.appstore.manager.IDataPresenter
import com.xiaozi.appstore.manager.UserInfoPresenterImpl
import com.xiaozi.appstore.plugin.ImageLoaderHelper
import com.xiaozi.appstore.safety
import com.xiaozi.appstore.safetySelf
import com.xiaozi.appstore.wxapi.WXHelper

/**
 * Created by fish on 18-1-4.
 */
class MineFragment : BaseFragment() {

    lateinit var mUserImg: ImageView
    lateinit var mWifiImg: ImageView
    lateinit var mUserName: TextView
    lateinit var mUserAction: TextView
    lateinit var mLLLogin: LinearLayout
    lateinit var mLoader: IDataPresenter


    override fun initView(inflater: LayoutInflater) = inflater.inflate(R.layout.f_mine, null).safetySelf {
        mUserImg = findViewById(R.id.img_fmine_head)
        mWifiImg = findViewById(R.id.img_fmine_wifi)
        mUserName = findViewById(R.id.tv_fmine_name)
        mUserAction = findViewById(R.id.tv_fmine_action)
        mLLLogin = findViewById<LinearLayout>(R.id.ll_fmine_login)
        mLoader = UserInfoPresenterImpl(activity) {
            ImageLoaderHelper.loadImageWithCache(AccountManager.userHeadIcon, mUserImg)
            mUserName.text = AccountManager.userName
        }
        initEffects(this)
        mLoader.load()
    }

    private fun initEffects(view: View) {
        view.safety {
            findViewById<RelativeLayout>(R.id.rl_fmine_download).setOnClickListener { startActivity(Intent(activity, DownloadMgrActivity::class.java)) }
            findViewById<RelativeLayout>(R.id.rl_fmine_feedback).setOnClickListener { startActivity(Intent(activity, FeedbackActivity::class.java)) }
            findViewById<RelativeLayout>(R.id.rl_fmine_about).setOnClickListener { startActivity(Intent(activity, AboutActivity::class.java)) }
            findViewById<FrameLayout>(R.id.fl_fmine_search).setOnClickListener { startActivity(Intent(activity, SearchActivity::class.java)) }
            findViewById<RelativeLayout>(R.id.fl_fmine_wifi).setOnClickListener {
                mWifiImg.setImageResource(if (ConfManager.isOnlyWifi()) R.drawable.switch_off else R.drawable.switch_on)
                ConfManager.setOnlyWifi(!ConfManager.isOnlyWifi())
            }
        }
    }


    override fun onSelected() {
        if (!AccountManager.isLoggedIn()) {
            mLLLogin.setOnClickListener { WXHelper.login() }
            mUserAction.apply {
                text = "登录"
                setOnClickListener {}
            }
        } else {
            mLLLogin.setOnClickListener {}
            mUserAction.apply {
                text = "退出"
                setOnClickListener {
                    AccountManager.logout()
                    onSelected()
                }
            }
        }
    }

}