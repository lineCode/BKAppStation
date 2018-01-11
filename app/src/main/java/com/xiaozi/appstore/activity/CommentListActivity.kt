package com.xiaozi.appstore.activity

import android.os.Bundle
import android.view.View
import com.xiaozi.appstore.R
import kotlinx.android.synthetic.main.a_comment_list.*

/**
 * Created by fish on 18-1-11.
 */
class CommentListActivity : BaseBarActivity() {
    override fun title() = "评论详情"
    override fun layoutID() = R.layout.a_comment_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tv_comment_write.setOnClickListener { fl_comment_page.visibility = View.VISIBLE }
        tv_comment_apply.setOnClickListener { fl_comment_page.visibility = View.GONE }
        tv_comment_cancel.setOnClickListener { fl_comment_page.visibility = View.GONE }
    }
}