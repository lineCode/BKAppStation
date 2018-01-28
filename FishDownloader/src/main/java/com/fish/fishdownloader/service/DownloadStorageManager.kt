package com.fish.fishdownloader.service

import android.content.Context
import com.google.gson.Gson

/**
 * Created by Administrator on 2018/1/28.
 */
@Synchronized
fun deleteInfo(ctx: Context, tag: String) = ctx.getSharedPreferences("download_rec", Context.MODE_PRIVATE).edit().remove(tag).apply()

@Synchronized
fun saveInfo(ctx: Context, info: DownloadRecInfo) = ctx.getSharedPreferences("download_rec", Context.MODE_PRIVATE).edit().putString(info.tag, FishDownloaderSVC.GSON.toJson(info)).apply()

@Synchronized
fun takeInfo(ctx: Context, tag: String): DownloadRecInfo? {
    ctx.getSharedPreferences("download_rec", Context.MODE_PRIVATE).all.run {
        if (containsKey(tag))
            return FishDownloaderSVC.GSON.fromJson(this[tag] as? String
                    ?: return null, DownloadRecInfo::class.java)
        return null
    }
}

@Synchronized
fun takeAllInfo(ctx: Context): List<DownloadRecInfo> {
    return ctx.getSharedPreferences("download_rec", Context.MODE_PRIVATE).all.map { FishDownloaderSVC.GSON.fromJson(it.value.toString(), DownloadRecInfo::class.java) }
            .filter { it != null } ?: listOf()
}

@Synchronized
fun hasInfo(ctx: Context, tag: String) = ctx.getSharedPreferences("download_rec", Context.MODE_PRIVATE).all.containsKey(tag)

data class DownloadRecInfo(var tag: String, var name: String, var downloadUrl: String, var filePath: String, var ptr: Int, var size: Int, var cancelSignal: Boolean, var pauseSignal: Boolean)
