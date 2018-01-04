package com.xiaozi.appstore

import android.app.Application
import com.xiaozi.appstore.component.Framework

/**
 * Created by fish on 18-1-2.
 */
class App : Application(){
    override fun onCreate() {
        super.onCreate()
        Framework.mContext = this.applicationContext
    }
}