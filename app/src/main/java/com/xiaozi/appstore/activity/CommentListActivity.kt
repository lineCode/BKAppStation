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
import com.xiaozi.appstore.manager.AccountManager
import com.xiaozi.appstore.manager.CommentListPresenterImpl
import com.xiaozi.appstore.manager.DataManager
import com.xiaozi.appstore.manager.NetManager
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
        fun open(ctx: Context, appID: Int) {
            ctx.startActivity(Intent(ctx, CommentListActivity::class.java).apply { putExtra(KEY_APPID, appID) })
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
                holder.load(mData[position]) {
                    if (!AccountManager.isLoggedIn()) {
                        startActivity(Intent(this@CommentListActivity, LoginActivity::class.java))
                    } else {
                        NetManager.applyThumbsup(mAppId, it.id, AccountManager.uid(), it.isAgreed == 0, {
                            mLoader.load(true)
                        }, this@CommentListActivity::ZToast)
                    }
                }
            }

            override fun getItemCount() = mData.size

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = CommentVH(parent)

        }
        rv_comment_list.layoutManager = LinearLayoutManager(this)
        rv_comment_list.adapter = mAdapter
        swipe_comment_list.onSwipe({ mLoader.load() }) { mLoader.load(false, mData.size) }
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
    }

    private fun initEffects() {
        tv_comment_write.setOnClickListener {
            fl_comment_page.visibility = View.VISIBLE
            et_comment.requestFocus()

        }
        tv_comment_apply.setOnClickListener {
            if (et_comment.text.isEmpty()) {
                ZToast("请输入评论内容")
            } else {
                fl_comment_page.visibility = View.GONE
                NetManager.applyComment(mAppId, et_comment.text.toString(), 0, AccountManager.uid(), AccountManager.userName, {
                    ZToast("评论提交成功")
                }) { this@CommentListActivity::ZToast }
            }
        }
        tv_comment_cancel.setOnClickListener {
            fl_comment_page.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        mLoader.load(true)
    }
}