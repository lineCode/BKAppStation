package com.xiaozi.appstore.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import com.xiaozi.appstore.R
import com.xiaozi.appstore.component.Framework.Math.limitL
import com.xiaozi.appstore.plugin.Call
import com.xiaozi.appstore.plugin.safety

/**
 * Created by fish on 18-1-4.
 */
object Dialogs {
    fun create(ctx: Context, layoutID: Int): Dialog {
        return Dialog(ctx, R.style.app_dialog).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setContentView(layoutID)
        }
    }

    fun createWaiter(ctx: Context): Dialog {
        return Dialog(ctx, R.style.staticDialog).apply {
            setContentView(R.layout.d_waiter)
            setCanceledOnTouchOutside(false)
        }
    }

}

class AsyncWaiter {
    var showTime = 0L
    var dialog: Dialog? = null
    var mActivity: Activity? = null
    fun show(ctx: Activity) {
        if (dialog?.isShowing == true) return
        dialog = Dialogs.createWaiter(ctx).apply { safety(Dialog::show) }
        showTime = System.currentTimeMillis()
        mActivity = ctx
    }

    fun hide(minDelay: Long) {
        if (dialog?.isShowing == true && !isActivityDead(mActivity)) {
            Call(limitL(minDelay, System.currentTimeMillis() - showTime))
            { dialog.safety(Dialog::cancel) }
        }
    }

    private fun isActivityDead(activity: Activity?) = activity == null || activity.isFinishing || activity.isDestroyed

    fun hide() {
        if (dialog?.isShowing == true && !isActivityDead(mActivity)) {
            dialog.safety(Dialog::cancel)
        }
    }
}

class CommonDialog(ctx: Context) {
    val mCtx = ctx
}


fun Dialog.fullShow() {
    window.decorView.setPadding(0, 0, 0, 0)
    window.attributes = window.attributes.apply {
        gravity = Gravity.BOTTOM
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
    }
    safety(Dialog::show)
}
