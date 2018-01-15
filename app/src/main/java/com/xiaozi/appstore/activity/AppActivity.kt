package com.xiaozi.appstore.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.xiaozi.appstore.R
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.manager.AppDetailPresenterImpl
import com.xiaozi.appstore.manager.DataManager
import com.xiaozi.appstore.manager.IDataPresenter
import com.xiaozi.appstore.manager.PresenterImpls
import com.xiaozi.appstore.plugin.ImageLoaderHelper
import com.xiaozi.appstore.safetyNullable
import com.xiaozi.appstore.view.AsyncWaiter
import com.xiaozi.appstore.view.ImageVH
import com.xiaozi.appstore.view.RecyclerDividerDecor
import kotlinx.android.synthetic.main.a_app.*
import kotlinx.android.synthetic.main.i_applist.*

/**
 * Created by fish on 18-1-9.
 */
class AppActivity : BaseBarActivity() {
    override fun title() = "应用市场"
    override fun layoutID() = R.layout.a_app
    lateinit var mData: DataManager.AppDetail
    lateinit var mLoader: IDataPresenter
    lateinit var mWaiter: AsyncWaiter
    lateinit var mAdapter: RecyclerView.Adapter<ImageVH>

    companion object {
        val KEY_APPID = "appID"
        fun open(ctx: Context, appId: Int) {
            ctx.startActivity(Intent(ctx, AppActivity::class.java).apply {
                putExtra(KEY_APPID, appId)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLoader()
    }

    private fun initLoader() {
        mWaiter = AsyncWaiter(this)
        mLoader = AppDetailPresenterImpl(mWaiter, getAppId()) { mData = it; initView() }
    }

    private fun getAppId() = intent.getIntExtra(KEY_APPID, -1).apply {
        if (this == -1)
            exit()
    }

    private fun initView() {
        if (!this::mData.isInitialized) exit()
        tv_iapp_name.text = mData.name
        tv_iapp_content.text = mData.tip
        tv_iapp_pos.visibility = View.GONE
        tv_app_info.text = mData.content
        tv_app_update_info.text = mData.updateLog
        ImageLoaderHelper.loadImageWithCache(mData.icon, img_iapp_icon)
        tv_app_chat.run {
            text = "${mData.commentCnt}"
        }
        initRV()
    }

    private fun initRV() {
        mAdapter = object : RecyclerView.Adapter<ImageVH>() {
            val imgView = ImageView(this@AppActivity)
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = ImageVH(imgView)

            override fun getItemCount() = mData.imgs.size

            override fun onBindViewHolder(holder: ImageVH, position: Int) {
                holder.load(mData.imgs[position])
            }
        }
        rv_app.run {
            layoutManager = LinearLayoutManager(this@AppActivity)
            adapter = mAdapter
//            addItemDecoration(RecyclerDividerDecor(this@AppActivity, 4))
            mAdapter.notifyDataSetChanged()
        }
    }


    private fun exit() {
        ZToast("应用信息错误")
        finish()
    }
}