package com.fish.fishdownloader.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import com.fish.fishdownloader.IFDownloadAction
import com.fish.fishdownloader.IFDownloadCallbacks
import com.fish.fishdownloader.service.FishDownloaderData.mCKS
import com.fish.fishdownloader.service.FishDownloaderData.mInfos
import com.fish.fishdownloader.service.FishDownloaderSVC.Companion.DOWNLOAD_DIR
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by Administrator on 2018/1/27.
 */
class FishDownloaderSVC : Service() {
    companion object {
        val DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory("ad/fishdownload/")
        const val TAG = "FISH DOWNLOAD SVC"
        val GSON = Gson()
    }

    private val mActionBinder = object : IFDownloadAction.Stub() {
        override fun initInfo(tag: String, name: String, downloadUrl: String, size: Int) {
            if (hasInfo(this@FishDownloaderSVC, tag)) {
                FishDownloaderData.mInfos[tag] = takeInfo(this@FishDownloaderSVC, tag)!!.apply { cancelSignal = false;pauseSignal = false }
            } else {
                FishDownloaderData.mInfos[tag] = DownloadRecInfo(tag, name, downloadUrl, "", 0, size, false, false)
            }
        }

        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?, aCK: IBinder?) {
        }

        override fun getAbsFilePath(tag: String): String {
            return mInfos[tag]?.filePath ?: ""
        }

        override fun startDownload(tag: String) {
            takeInfo(this@FishDownloaderSVC, tag)?.run {
                if (ptr == size ) {
                    if (File(filePath).exists()) {
                        FishDownloaderData.mCKS[tag]?.onComplete(filePath)
                        return
                    }
                    else
                        deleteInfo(this@FishDownloaderSVC, tag)
                }
            }
            if (tag !in FishDownloaderData.mDownloadings) {
                FishDownloader().get(this@FishDownloaderSVC, FishDownloaderData.mInfos[tag] ?: return)
                FishDownloaderData.mDownloadings.add(tag)
            }
        }

        override fun cancelDownloadByTag(tag: String) {
            mInfos[tag]?.cancelSignal = true
        }

        override fun cancelAll() {
            mInfos.map { it.value.cancelSignal = true }
        }

        override fun registerCK(tag: String, ck: IBinder) {
            mCKS[tag] = IFDownloadCallbacks.Stub.asInterface(ck)
        }

        override fun unregisterCK(tag: String) {
            mCKS.remove(tag)
        }

        override fun unregisterAllCKs() {
            mCKS.clear()
        }

        override fun hasTag(tag: String) = mInfos.containsKey(tag)

        override fun pauseByTag(tag: String) {
            mInfos[tag]?.pauseSignal = true
        }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.e(TAG, "ON BIND")
        return mActionBinder
    }
}

object FishDownloaderData {
    val mCKS = mutableMapOf<String, IFDownloadCallbacks>()
    val mInfos = mutableMapOf<String, DownloadRecInfo>()
    val mDownloadings = mutableSetOf<String>()
}

class FishDownloader {
    companion object {
        private const val BUF_SIZE = 2 * 1024
        const val TAG = "FISH Downloader"
    }

    private fun createFile(info: DownloadRecInfo): File {
        if (info.filePath.isNotBlank())
            File(info.filePath).run {
                if (exists())
                    return this
                else
                    FishDownloaderData.mInfos[info.tag]?.ptr = 0
            }
        return File(DOWNLOAD_DIR, "${info.name}-${System.currentTimeMillis()}.apk").apply {
            Log.e(TAG, "CREATE FILE")
            if (!parentFile.exists()) parentFile.mkdirs()
            if (exists()) delete()
            createNewFile()
            mInfos[info.tag]?.filePath = absolutePath
        }
    }

    fun get(ctx: Context, info: DownloadRecInfo) = Runnable {
        try {
            Log.e(TAG, "START ${info.downloadUrl}")
            val connection = URL(info.downloadUrl).openConnection() as HttpURLConnection
            if (info.ptr > 0) {
                connection.addRequestProperty("Range", "bytes=${limit(info.ptr.toInt(), 1024)}-")
            }
            Log.e(TAG, "url connected!")
            if (connection.responseCode == 200 || connection.responseCode == 206 || connection.responseCode == 302) {
                Log.e(TAG, "code:${connection.responseCode}")
                if (connection.contentLength != 0) info.size = connection.contentLength
                Log.e(TAG, "lenth:${info.size}")
                val f = createFile(info)
                val fos = FileOutputStream(f, true)
                val netIS = connection.inputStream
                var downloadPtr = limit(info.ptr.toInt(), 1024)
                var readCnt = 0
                val buf = ByteArray(BUF_SIZE)
                do {
                    readCnt = netIS.read(buf, 0, BUF_SIZE)
                    if (readCnt == -1)
                        break
                    fos.write(buf, 0, readCnt)
                    fos.flush()
                    Log.e(TAG, "dptr:$downloadPtr, readCnt:$readCnt, BUF SIZE: $BUF_SIZE")
                    downloadPtr += readCnt
                    mCKS[info.tag]?.onProgress(downloadPtr * 1.0 / info.size)
                    info.ptr = downloadPtr
                    Log.e(TAG, "cancelSig: ${info.cancelSignal}, pauseSignal: ${info.pauseSignal}")
                } while (readCnt > 0 && !info.cancelSignal && !info.pauseSignal)
                Log.e(TAG, "exit looper")
                try {
                    fos.close()
                    netIS.close()
                    connection.disconnect()
                } catch (ioex: IOException) {
                    ioex.printStackTrace()
                }
                if (info.cancelSignal) {
                    mCKS[info.tag]?.onCanceled(info.tag)
                    deleteInfo(ctx, info.tag)
                    f.delete()
                } else if (info.pauseSignal) {
                    saveInfo(ctx, info)
                    mCKS[info.tag]?.onPause(info.filePath)
                } else {
                    mCKS[info.tag]?.onComplete(info.filePath)
                }
            } else {
                mCKS[info.tag]?.onFailed("REQUEST ERROR:${connection.responseCode}")
                deleteInfo(ctx, info.tag)
            }
        } catch (ioEX: IOException) {
            ioEX.printStackTrace()
            mCKS[info.tag]?.onFailed("CONNECTION FAILED")
            deleteInfo(ctx, info.tag)
        } finally {
            FishDownloaderData.mDownloadings.remove(info.tag)
        }
    }

    private fun limit(origin: Int, limits: Int) = if (origin <= limits) 0 else origin - limits
}

@Synchronized private fun deleteInfo(ctx: Context, tag: String) = ctx.getSharedPreferences("download_rec", Context.MODE_PRIVATE).edit().remove(tag).apply()
@Synchronized private fun saveInfo(ctx: Context, info: DownloadRecInfo) = ctx.getSharedPreferences("download_rec", Context.MODE_PRIVATE).edit().putString(info.tag, FishDownloaderSVC.GSON.toJson(info)).apply()
@Synchronized private fun takeInfo(ctx: Context, tag: String): DownloadRecInfo? {
    ctx.getSharedPreferences("download_rec", Context.MODE_PRIVATE).all.run {
        if (containsKey(tag))
            return FishDownloaderSVC.GSON.fromJson(this[tag] as? String
                    ?: return null, DownloadRecInfo::class.java)
        return null
    }
}
@Synchronized private fun hasInfo(ctx: Context, tag: String) = ctx.getSharedPreferences("download_rec", Context.MODE_PRIVATE).all.containsKey(tag)

data class DownloadRecInfo(var tag: String, var name: String, var downloadUrl: String, var filePath: String, var ptr: Int, var size: Int, var cancelSignal: Boolean, var pauseSignal: Boolean)