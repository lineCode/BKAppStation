package com.xiaozi.appstore.plugin

import java.util.*

/**
 * Created by fish on 17-7-3.
 */
class ForceObb : Observable() {
    private var mCache: Any? = null

    override fun hasChanged() = true

    fun addObserver(o: Observer?, useCache: Boolean) {
        super.addObserver(o)
        if (useCache && mCache != null) {
            o?.update(this@ForceObb, mCache)
        }
    }

    override fun notifyObservers(data: Any?) {
        mCache = data
        super.notifyObservers(data)
    }

    fun cleanCache() {
        mCache = null
    }

    fun getCache() = mCache
}