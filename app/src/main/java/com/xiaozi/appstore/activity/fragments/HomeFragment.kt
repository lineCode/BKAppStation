package com.xiaozi.appstore.activity.fragments

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cc.fish.coreui.BaseFragment
import com.jude.rollviewpager.RollPagerView
import com.jude.rollviewpager.adapter.StaticPagerAdapter
import com.xiaozi.appstore.R
import com.xiaozi.appstore.manager.AppListType
import com.xiaozi.appstore.manager.DataManager
import com.xiaozi.appstore.manager.HomeListDataPresenterImpl
import com.xiaozi.appstore.manager.INetAppListPresenter
import com.xiaozi.appstore.plugin.ImageLoaderHelper
import com.xiaozi.appstore.safety
import com.xiaozi.appstore.safetySelf
import com.xiaozi.appstore.view.AsyncWaiter
import com.xiaozi.appstore.view.HomeVH

/**
 * Created by fish on 18-1-4.
 */
class HomeFragment : BaseFragment() {
    val mDrawableTab by lazy { resources.getDrawable(R.drawable.icon_linebar).apply { setBounds(0, 0, minimumWidth, minimumHeight) } }
    val mDrawableTabWhite by lazy { resources.getDrawable(R.drawable.icon_linebar_white).apply { setBounds(0, 0, minimumWidth, minimumHeight) } }
    val mDefaultItemLP = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    var mTvTabApp: TextView? = null
    var mTvTabGame: TextView? = null
    var mRPV: RollPagerView? = null
    var mRV: RecyclerView? = null
    var mDataloader: INetAppListPresenter? = null
    val mData: MutableList<DataManager.AppInfo> = mutableListOf()

    override fun initView(inflater: LayoutInflater) = inflater.inflate(R.layout.f_home, null).safetySelf {
        mTvTabApp = findViewById(R.id.tv_fhome_tab_app)
        mTvTabGame = findViewById(R.id.tv_fhome_tab_game)
        mRPV = findViewById(R.id.rp_fhome_top)
        mRV = findViewById(R.id.rv_fhome)
        mRV?.addItemDecoration(DividerItemDecoration(activity, VERTICAL))
        initDataLoader()
        checkTab(0)
        freshRPV(arrayOf<DataManager.Banner>())
        initEffects()
    }

    private fun initDataLoader() {
        mDataloader = HomeListDataPresenterImpl(AsyncWaiter(activity)) {
            mData.safety {
                clear()
                addAll(this@HomeListDataPresenterImpl)
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun freshRPV(banner: Array<DataManager.Banner>) {
        mRPV?.apply {
            setPlayDelay(3000)
            setAnimationDurtion(300)
            setAdapter(object : StaticPagerAdapter() {
                override fun getView(container: ViewGroup?, position: Int) = ImageView(container?.context).apply {
                    ImageLoaderHelper.loadImageWithCache(banner[position].img, this)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    layoutParams = mDefaultItemLP
                }

                override fun getCount() = banner.size
            })
        }
    }

    private fun initEffects() {
        mTvTabApp?.setOnClickListener { checkTab(0) }
        mTvTabGame?.setOnClickListener { checkTab(1) }
    }

    override fun onSelected() {}

    private fun checkTab(index: Int) {
        mTvTabApp?.setCompoundDrawables(null, null, null, if (index == 0) mDrawableTab else mDrawableTabWhite)
        mTvTabGame?.setCompoundDrawables(null, null, null, if (index == 1) mDrawableTab else mDrawableTabWhite)
        if (index == 0) {
            mDataloader?.load(AppListType.HOT_APP)
        } else if (index == 1) {
            mDataloader?.load(AppListType.HOT_GAME)
        }
    }

    val mAdapter = object : RecyclerView.Adapter<HomeVH>() {
        override fun getItemCount() = mData.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = HomeVH(parent)

        override fun onBindViewHolder(holder: HomeVH?, position: Int) {
            holder?.load(mData[position])
        }

    }
}