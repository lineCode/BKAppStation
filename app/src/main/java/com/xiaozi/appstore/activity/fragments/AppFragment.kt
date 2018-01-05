package com.xiaozi.appstore.activity.fragments

import android.view.LayoutInflater
import android.view.View
import cc.fish.coreui.BaseFragment
import com.xiaozi.appstore.R
import com.xiaozi.appstore.plugin.safetySelf

/**
 * Created by fish on 18-1-4.
 */
class AppFragment : BaseFragment() {
    override fun initView(inflater: LayoutInflater) = inflater.inflate(R.layout.f_app, null).safetySelf {

    }


    override fun onSelected() {
    }

}