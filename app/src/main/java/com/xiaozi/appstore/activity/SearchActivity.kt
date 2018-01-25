package com.xiaozi.appstore.activity

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import com.xiaozi.appstore.R
import com.xiaozi.appstore.dp2px
import com.xiaozi.appstore.manager.SearchPresenterImpl
import com.xiaozi.appstore.view.SearchVH
import kotlinx.android.synthetic.main.a_search.*

/**
 * Created by fish on 18-1-16.
 */
class SearchActivity : Activity() {

    lateinit var mAdapter: RecyclerView.Adapter<SearchVH>
    val mData = mutableListOf<String>()
    val mLoader = SearchPresenterImpl {
        mData.clear()
        mData.addAll(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_search)
        initRV()
        mLoader.load()
    }

    fun initRV() {
        rv_search.layoutManager = GridLayoutManager(this@SearchActivity, 2)
        mAdapter = object : RecyclerView.Adapter<SearchVH>() {
            val mTextView = TextView(this@SearchActivity).apply {
                layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT).apply {
                    dp2px(4).let { setMargins(it, it, it, it) }
                }

            }

            override fun onBindViewHolder(holder: SearchVH, position: Int) {
                holder.load(mData[position])
            }

            override fun getItemCount() = mData.size

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = SearchVH(mTextView)

        }
    }
}