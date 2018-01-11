package com.xiaozi.appstore.activity.fragments

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cc.fish.coreui.BaseFragment
import com.xiaozi.appstore.App
import com.xiaozi.appstore.R
import com.xiaozi.appstore.manager.*
import com.xiaozi.appstore.safety
import com.xiaozi.appstore.safetySelf
import com.xiaozi.appstore.view.AsyncWaiter
import com.xiaozi.appstore.view.LoadableSwipeLayout
import com.xiaozi.appstore.view.TypedAppListVH

/**
 * Created by fish on 18-1-8.
 */
sealed class BaseAppListFragment : BaseFragment() {
    lateinit var mTvChart: TextView
    lateinit var mTvCategory: TextView
    lateinit var mRvList: RecyclerView
    lateinit var mLLCategory: LinearLayout
    lateinit var mType: AppListType
    lateinit var mListLoader: INetAppsPresenter
    lateinit var mWaiter: AsyncWaiter
    lateinit var mSwiper: LoadableSwipeLayout
    lateinit var mAdapter: RecyclerView.Adapter<TypedAppListVH>
    val mData: MutableList<DataManager.AppInfo> = mutableListOf()
    val mDrawableTab by lazy { resources.getDrawable(R.drawable.icon_linebar).apply { setBounds(0, 0, minimumWidth, minimumHeight) } }
    val mDrawableTabWhite by lazy { resources.getDrawable(R.drawable.icon_linebar_white).apply { setBounds(0, 0, minimumWidth, minimumHeight) } }
    val mDefaultItemLP = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)


    override fun initView(inflater: LayoutInflater) = inflater.inflate(R.layout.f_app, null).safetySelf {
        mType = when (this@BaseAppListFragment) {
            is AppFragment -> AppListType.APP
            is GameFragment -> AppListType.GAME
        }
        mWaiter = AsyncWaiter(activity)
        mTvChart = findViewById(R.id.tv_fapp_tab_chart)
        mTvCategory = findViewById(R.id.tv_fapp_tab_category)
        mRvList = findViewById(R.id.rv_fapp_chart)
        mLLCategory = findViewById(R.id.ll_fapp_category)
        mSwiper = findViewById(R.id.sp_fapp)
        initRv()
        initLoader()
        initEffects()
    }

    fun initLoader() {
        mListLoader = AppListDataPresenterImpl(mWaiter, mType.str, AppCondition.TOP.str) {
            mData.safety {
                clear()
                addAll(this@AppListDataPresenterImpl)
            }
        }
    }

    private fun initRv() {
        mAdapter = object : RecyclerView.Adapter<TypedAppListVH>() {
            override fun getItemCount() = mData.size

            override fun onBindViewHolder(holder: TypedAppListVH, position: Int) {
                holder.load(mData[position], position + 1)
            }

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = TypedAppListVH(parent)
        }
        mRvList.run {
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
        }
        mSwiper.onSwipe(mData, { mListLoader.load() }) { mListLoader.load(index = this) }
    }

    private fun initEffects() {
        mTvChart.setOnClickListener { checkTab(0) }
        mTvCategory.setOnClickListener { checkTab(1) }
    }

    private fun checkTab(index: Int) {
        mTvChart.setCompoundDrawables(null, null, null, if (index == 0) mDrawableTab else mDrawableTabWhite)
        mTvCategory.setCompoundDrawables(null, null, null, if (index == 1) mDrawableTab else mDrawableTabWhite)
        if (index == 0) {
            if (mData.isEmpty())
                mListLoader.load()
        } else if (index == 1) {
        }
    }
}

class AppFragment : BaseAppListFragment()
class GameFragment : BaseAppListFragment()