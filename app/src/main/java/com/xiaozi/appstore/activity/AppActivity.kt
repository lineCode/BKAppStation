package com.xiaozi.appstore.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.xiaozi.appstore.R
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.manager.DataManager
import com.xiaozi.appstore.manager.PresenterImpls
import com.xiaozi.appstore.plugin.ImageLoaderHelper
import com.xiaozi.appstore.safetyNullable
import kotlinx.android.synthetic.main.a_app.*
import kotlinx.android.synthetic.main.i_applist.*

/**
 * Created by fish on 18-1-9.
 */
class AppActivity : BaseBarActivity() {
    override fun title() = "应用市场"
    override fun layoutID() = R.layout.a_app
    lateinit var mData: DataManager.AppInfo

    companion object {
        val KEY_PACKAGE = "package"
        fun open(ctx: Context, pkg: String) {
            ctx.startActivity(Intent(ctx, AppActivity::class.java).apply {
                putExtra(KEY_PACKAGE, pkg)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        intent.getStringExtra(KEY_PACKAGE).safetyNullable {
            if (this == null){
                exit()
                return
            }
            mData = PresenterImpls.AppInfoCachedPresenterImpl.get(this)!!
        }
        if (!::mData.isInitialized) {
            exit()
            return
        }
    }

    private fun initView() {
        tv_iapp_name.text = mData.name
        tv_iapp_content.text = mData.tip
        tv_iapp_pos.visibility = View.GONE
        tv_app_chat.text = ""
        tv_app_info.text = mData.content
        tv_app_info.text = ""
        ImageLoaderHelper.loadImageWithCache(mData.icon, img_iapp_icon)
    }


    private fun exit() {
        ZToast("应用信息错误")
        finish()
    }
}