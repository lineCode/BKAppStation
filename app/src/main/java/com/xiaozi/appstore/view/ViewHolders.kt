package com.xiaozi.appstore.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.fish.downloader.view.DownloadBar
import com.xiaozi.appstore.*
import com.xiaozi.appstore.activity.AppActivity
import com.xiaozi.appstore.activity.AppListActivity
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.component.GlobalData
import com.xiaozi.appstore.manager.*
import com.xiaozi.appstore.plugin.ImageLoaderHelper
import java.io.File

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
        v.setOnClickListener { AppActivity.open(v.context, app.appId) }
        DownloadBarImplement.initDownloadBar(mDL, app)
    }

    fun release(tag: String) {
        DownloadTagsManager.mTagsMap[tag]?.text = mDL.text()

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
        mTvCon.text = app.tip
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

class CommentVH(val v: View) : RecyclerView.ViewHolder(v) {
    constructor(parent: ViewGroup?) : this(LayoutInflater.from(parent?.context).inflate(R.layout.i_comment, parent, false))

    val imgIcon = v.findViewById<ImageView>(R.id.img_icomment_userhead)
    val tvName = v.findViewById<TextView>(R.id.tv_icomment_username)
    val tvAgree = v.findViewById<TextView>(R.id.tv_icomment_agree)
    val tvDate = v.findViewById<TextView>(R.id.tv_icomment_date)
    val tvContent = v.findViewById<TextView>(R.id.tv_icomment_content)
    val mDrawableAgree = v.context.resources.getDrawable(R.drawable.icon_agreed).apply { setBounds(0, 0, minimumWidth, minimumHeight) }
    val mDrawableUnAgree = v.context.resources.getDrawable(R.drawable.icon_unagreed).apply { setBounds(0, 0, minimumWidth, minimumHeight) }
    fun load(data: DataManager.Comment, tvAction: TextView.() -> Unit) {
        ImageLoaderHelper.loadImageWithCache(data.headIcon, imgIcon)
        tvName.text = data.name
        tvDate.text = data.time
        tvContent.text = data.content
        tvAgree.safety {
            text = "${data.count}"
            setCompoundDrawables(null, null, if (data.isAgreed == 1) mDrawableAgree else mDrawableUnAgree, null)
            if (data.isAgreed == 0) setOnClickListener { this.tvAction() }
        }
    }
}

class ImageVH(v: ImageView) : RecyclerView.ViewHolder(v) {
    val img = v
    fun load(imgUrl: String) {
        ImageLoaderHelper.loadImageWithCache(imgUrl, img)
    }
}

class DownloadingVH(private val v: View) : RecyclerView.ViewHolder(v) {
    constructor(parent: ViewGroup?) : this(LayoutInflater.from(parent?.context).inflate(R.layout.i_downloading, parent, false))

    private val mTvName = v.findViewById<TextView>(R.id.tv_idl_name)
    private val mTvContent = v.findViewById<TextView>(R.id.tv_idl_content)
    val mDownloader = v.findViewById<DownloadBar>(R.id.download_idl)
    fun load(data: DownloadInfoManager.DownloadInfo) {
        mTvName.text = data.name
        if (data.size == data.ptr) {
            if (!File(data.path).exists()) {
                mTvContent.text = "文件已删除"
                mDownloader.text("已删除")
                return
            }
            mTvContent.text = "已完成"
            mDownloader.apply {
                if (Framework.Package.isInstalled(data.tag)) {
                    text("打开")
                    setOnClickListener { Framework.App.openOtherApp(data.tag) }
                } else {
                    text("安装")
                    setOnClickListener { File(data.path).safety(Framework.App::installApp) }
                }
            }
        } else {
            mTvContent.text = "${Framework.Trans.Size(data.ptr)}/${Framework.Trans.Size(data.size)} 0B/s"
            mDownloader.text("暂停")
            mDownloader.setOnClickListener { }
        }
    }

    fun content(str: String) {
        mTvContent.text = str
    }
}

class SearchVH(private val v: TextView) : RecyclerView.ViewHolder(v) {
    fun load(searchWord: String) {
        AppListActivity.open(v.context, "搜索结果", AppListType.ALL.str, AppCondition.SEARCH.str, searchWord)
    }
}


class RecyclerDividerDecor(private val ctx: Context, private val dividerSize: Int) : RecyclerView.ItemDecoration() {
    val mDividerSize = ctx.dp2px(dividerSize)
    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        if (parent?.layoutManager is LinearLayoutManager) {

            if ((parent.layoutManager as LinearLayoutManager).orientation == LinearLayoutManager.HORIZONTAL) {
                outRect?.set(mDividerSize, mDividerSize, mDividerSize, 0)
            } else {
                outRect?.set(0, 0, 0, mDividerSize)
            }
        }
    }
}

object DownloadBarImplement {
    fun initDownloadBar(dlBar: DownloadBar, app: DataManager.AppInfo) {
        dlBar.run {
            restore()
            if (Framework.Package.installed().contains(app.pkg)) {
                text("打开")
                setOnClickListener { Framework.App.openOtherApp(app.pkg) }
            } else if (DownloadInfoManager.downloadInfos.containsBy(app.pkg, DownloadInfoManager.DownloadInfo::tag)) {
                DownloadInfoManager.getInfoByTag(app.pkg)!!.run {
                    if (size == ptr) {
                        if (!File(path).exists()) {
                            text("已删除")
                            DownloadInfoManager.removeInfo(app.pkg)
                            return
                        }
                        text("已完成")
                        if (Framework.Package.isInstalled(tag)) {
                            text("打开")
                            setOnClickListener { Framework.App.openOtherApp(tag) }
                        } else {
                            text("安装")
                            setOnClickListener { File(path).safety(Framework.App::installApp) }
                        }
                    } else {
                        text("继续")
                        setOnClickListener {
                            initDownload(app.pkg, app.name, app.dlUrl, app.sizeInt.toLong())
                            onceReDownload()
                        }
                    }
                }
            } else {
                initDownload(app.pkg, app.name, app.dlUrl, app.sizeInt.toLong())
            }
        }
    }
}



