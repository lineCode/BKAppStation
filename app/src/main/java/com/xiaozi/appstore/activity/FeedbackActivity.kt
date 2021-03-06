package com.xiaozi.appstore.activity

import android.os.Bundle
import com.xiaozi.appstore.R
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.manager.NetManager
import com.xiaozi.appstore.safety
import kotlinx.android.synthetic.main.a_feedback.*

/**
 * Created by fish on 18-1-15.
 */
class FeedbackActivity : BaseBarActivity() {
    override fun title() = "意见反馈"

    override fun layoutID() = R.layout.a_feedback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        et_feedback.text.toString().safety {
            tv_feedback_apply.setOnClickListener {
                if (this.isEmpty()) {
                    ZToast("请输入反馈内容")
                    return@setOnClickListener
                }
                NetManager.applyFeedback(this, { ZToast("反馈上报成功") }, this@FeedbackActivity::ZToast)
            }
        }
    }

}