package com.xiaozi.appstore.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.xiaozi.appstore.R
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.manager.CommentListPresenterImpl
import com.xiaozi.appstore.manager.DataManager
import com.xiaozi.appstore.view.AsyncWaiter
import com.xiaozi.appstore.view.CommentVH
import kotlinx.android.synthetic.main.a_comment_list.*

/**
 * Created by fish on 18-1-11.
 */
class CommentListActivity : BaseBarActivity() {
    override fun title() = "评论详情"
    override fun layoutID() = R.layout.a_comment_list

    companion object {
        val KEY_APPID = "APPID"
        fun open(ctx: Context, pkg: String) {
            ctx.startActivity(Intent(ctx, CommentListActivity::class.java).apply { putExtra(KEY_APPID, pkg) })
        }
    }

    var mAppId: Int = -1
    lateinit var mWaiter: AsyncWaiter
    lateinit var mLoader: CommentListPresenterImpl
    lateinit var mAdapter: RecyclerView.Adapter<CommentVH>

    val mData = mutableListOf<DataManager.Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initRV()
        initLoader()
        initEffects()
    }

    private fun initRV() {
        mAdapter = object : RecyclerView.Adapter<CommentVH>() {
            override fun onBindViewHolder(holder: CommentVH, position: Int) {
                holder.load(mData[position]){}
            }

            override fun getItemCount() = mData.size

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = CommentVH(parent)

        }
        rv_comment_list.layoutManager = LinearLayoutManager(this)
        rv_comment_list.adapter = mAdapter
        swipe_comment_list.onSwipe(mData, { mLoader.load() }) { mLoader.load(false, this) }
    }

    private fun initData() {
        mAppId = intent.getIntExtra(KEY_APPID, -1)
        if (mAppId == -1)
            exit()
    }

    private fun exit() {
        ZToast("评论获取失败")
        finish()
    }

    private fun initLoader() {
        mWaiter = AsyncWaiter(this)
        mLoader = CommentListPresenterImpl(mWaiter, mAppId) {
            mData.run {
                if (!it)
                    clear()
                addAll(this@CommentListPresenterImpl)
                mAdapter.notifyDataSetChanged()
            }
        }
        mLoader.load(true)
    }

    private fun initEffects() {
        tv_comment_write.setOnClickListener { fl_comment_page.visibility = View.VISIBLE }
        tv_comment_apply.setOnClickListener { fl_comment_page.visibility = View.GONE }
        tv_comment_cancel.setOnClickListener { fl_comment_page.visibility = View.GONE }
    }
}