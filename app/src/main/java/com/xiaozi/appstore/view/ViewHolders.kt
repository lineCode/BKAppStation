package com.xiaozi.appstore.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.fish.downloader.view.DownloadBar
import com.xiaozi.appstore.R
import com.xiaozi.appstore.bind
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.manager.DataManager
import com.xiaozi.appstore.plugin.ImageLoaderHelper

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
}
