package com.xiaozi.appstore.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.xiaozi.appstore.R
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.manager.DataManager
import com.xiaozi.appstore.manager.PresenterImpls
import com.xiaozi.appstore.safety
import com.xiaozi.appstore.safetyNullable

/**
 * Created by fish on 18-1-9.
 */
class AppActivity : BaseBarActivity() {
    override fun title() = "应用市场"
    override fun layoutID() = R.layout.a_app
    lateinit var mData: DataManager.AppInfo
    lateinit var mUpload: DataManager.UploadInfo

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
    }

    private fun initData() {
        intent.getStringExtra(KEY_PACKAGE).safetyNullable {
            if (this == null){
                exit()
                return
            }
            mData = PresenterImpls.AppInfoCachedPresenterImpl.get(this)!!
            mUpload = PresenterImpls.AppUpdateCachedPresenterImpl.get(this)!!
        }
        if (!::mData.isInitialized || !::mUpload.isInitialized) {
            exit()
            return
        }
    }

    private fun exit() {
        ZToast("应用信息错误")
        finish()
    }
}