package com.fish.fishdownloader.service

import android.content.Context
import android.util.Log

/**
 * Created by Administrator on 2018/1/28.
 */
@Synchronized
fun deleteInfo(ctx: Context, tag: String) = ctx.applicationContext.getSharedPreferences("download_rec", Context.MODE_MULTI_PROCESS).edit().remove(tag).apply()

@Synchronized
fun saveInfo(ctx: Context, info: DownloadRecInfo) = ctx.applicationContext.getSharedPreferences("download_rec", Context.MODE_MULTI_PROCESS).edit().putString(info.tag, FishDownloaderSVC.GSON.toJson(info)).apply()

@Synchronized
fun takeInfo(ctx: Context, tag: String): DownloadRecInfo? {
    ctx.applicationContext.getSharedPreferences("download_rec", Context.MODE_MULTI_PROCESS).all.run {
        if (containsKey(tag))
            return FishDownloaderSVC.GSON.fromJson(this[tag] as? String
                    ?: return null, DownloadRecInfo::class.java)
        return null
    }
}

@Synchronized
fun takeAllInfo(ctx: Context): List<DownloadRecInfo> {
    return ctx.applicationContext.getSharedPreferences("download_rec", Context.MODE_MULTI_PROCESS).all
            .map {
                FishDownloaderSVC.GSON.fromJson(it.apply { Log.e("DSM", it.toString()) }.value.toString(), DownloadRecInfo::class.java)
            }.filter { it != null }
}

@Synchronized
fun hasInfo(ctx: Context, tag: String) = ctx.applicationContext.getSharedPreferences("download_rec", Context.MODE_MULTI_PROCESS).all.containsKey(tag)

data class DownloadRecInfo(var tag: String, var name: String, var downloadUrl: String, var filePath: String, var ptr: Int, var size: Int, var cancelSignal: Boolean, var pauseSignal: Boolean)
