package com.xiaozi.appstore.manager

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.fish.fishdownloader.IFDownloadAction
import com.fish.fishdownloader.IFDownloadCallbacks
import com.fish.fishdownloader.service.DownloadRecInfo
import com.fish.fishdownloader.service.FishDownloaderSVC
import com.google.gson.reflect.TypeToken
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.plugin._GSON

/**
 * Created by fish on 18-1-25.
 */
object DownloadTagsManager {

    lateinit var mConnection: ServiceConnection
    lateinit var mServiceStub: IFDownloadAction
    fun initControlConnection(context: Context) {
        mConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                Log.e("remote service", "disconnected")
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.e("remote service", "connected")
                mServiceStub = IFDownloadAction.Stub.asInterface(service)
                mServiceStub.registerCK("control", object : IFDownloadCallbacks.Stub(){
                    override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?) {
                    }

                    override fun onProgress(pg: Double) {
                    }

                    override fun onComplete(filePath: String?) {
                    }

                    override fun onFailed(msg: String?) {
                    }

                    override fun onCanceled(msg: String?) {
                    }

                    override fun onPause(msg: String?) {
                    }
                })
            }
        }
        context.applicationContext.bindService(Intent(context.applicationContext, FishDownloaderSVC::class.java), mConnection, Service.BIND_AUTO_CREATE)
    }
    fun getAllInfo(ctx: Context): List<DownloadRecInfo> {
        if (!this::mServiceStub.isInitialized) return listOf()
        return _GSON.fromJson(mServiceStub.getAbsFilePath("control"), object : TypeToken<List<DownloadRecInfo>>(){}.type)
    }
//    val mTagsMap = mutableMapOf<String, DownloadTagRec>()
//
//    fun store(pkg: String, url: String, name: String, size: Long, text: String = "下载") {
//        mTagsMap.put(pkg, DownloadTagRec(pkg, url, name, size, text))
//    }
//
//    data class DownloadTagRec(val pkg: String, val url: String, val name: String, val size: Long, var text: String)

}