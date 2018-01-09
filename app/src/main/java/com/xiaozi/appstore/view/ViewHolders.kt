package com.xiaozi.appstore.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.fish.downloader.view.DownloadBar
import com.xiaozi.appstore.R
import com.xiaozi.appstore.manager.DataManager
import com.xiaozi.appstore.onClick
import com.xiaozi.appstore.plugin.ImageLoaderHelper
import com.xiaozi.appstore.safety

/**
 * Created by fish on 18-1-5.
 */
class HomeVH(val v: View) : RecyclerView.ViewHolder(v) {
    constructor(parent: ViewGroup?) : this(LayoutInflater.from(parent?.context).inflate(R.layout.i_fhome, parent, false))

    val mTvName = v.findViewById<TextView>(R.id.tv_ifhome_name)
    val mTvInfo = v.findViewById<TextView>(R.id.tv_ifhome_info)
    val mTvTip = v.findViewById<TextView>(R.id.tv_ifhome_tip)
    val mImgIcon = v.findViewById<ImageView>(R.id.img_ifhome_icon)
    val mDL = v.findViewById<DownloadBar>(R.id.dlbar_ifhome)

    fun load(app: DataManager.AppInfo) {
        mTvName.text = app.name
        mTvTip.text = app.tip
        mTvInfo.text = "${app.installCnt}次安装/${app.size}"
        ImageLoaderHelper.loadImageWithCache(app.icon, mImgIcon)
    }

    fun itemClk(ck: () -> Unit) {
        v.setOnClickListener { ck() }
    }
}

class TypedAppListVH(val v: View) : RecyclerView.ViewHolder(v) {
    constructor(parent: ViewGroup?) : this(LayoutInflater.from(parent?.context).inflate(R.layout.i_applist, parent, false))

    val mTvName = v.findViewById<TextView>(R.id.tv_iapp_name)
    val mTvPos = v.findViewById<TextView>(R.id.tv_iapp_pos)
    val mTvCon = v.findViewById<TextView>(R.id.tv_iapp_content)
    val mImgIcon = v.findViewById<ImageView>(R.id.img_iapp_icon)
    val mDL = v.findViewById<DownloadBar>(R.id.dlbar_iapp)

    fun load(app: DataManager.AppInfo, poi: Int) {
        mTvName.text = app.name
        if (poi > 0) {
            mTvPos.text = "$poi"
        } else
            mTvPos.visibility = View.GONE
        mTvCon.text = app.content
        ImageLoaderHelper.loadImageWithCache(app.icon, mImgIcon)
    }

    fun itemClk(ck: () -> Unit) {
        v.onClick(ck)
    }
}

class CategoryVH(val v: View) : RecyclerView.ViewHolder(v) {
    constructor(parent: ViewGroup?) : this(LayoutInflater.from(parent?.context).inflate(R.layout.i_category, parent, false))

    val mTvName = v.findViewById<TextView>(R.id.tv_icate_name)
    val mImgIcon = v.findViewById<ImageView>(R.id.img_icate_icon)
    val mCateSecIdArray = arrayOf(R.id.tv_icate1, R.id.tv_icate2, R.id.tv_icate3, R.id.tv_icate4, R.id.tv_icate5, R.id.tv_icate6)

    fun load(category: DataManager.Category, clk: DataManager.SecCategory.() -> Unit) {
        mTvName.text = category.name
        ImageLoaderHelper.loadImageWithCache(category.icon, mImgIcon)
        for (i in category.tabs.indices)
            category.tabs[i].safety {
                v.findViewById<TextView>(mCateSecIdArray[i]).run {
                    text = name
                    onClick { this@safety.clk() }
                }
            }
    }
}

