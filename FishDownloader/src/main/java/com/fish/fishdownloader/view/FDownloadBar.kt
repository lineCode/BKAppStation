package com.fish.fishdownloader.view

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.fish.downloader.extensions.bid
import com.fish.fishdownloader.IFDownloadAction
import com.fish.fishdownloader.IFDownloadCallbacks
import com.fish.fishdownloader.R
import com.fish.fishdownloader.service.*
import com.google.gson.Gson
import java.io.File

/**
 * Created by Administrator on 2018/1/27.
 */
class FDownloadBar(val ctx: Context, val attrs: AttributeSet?) : FrameLayout(ctx, attrs) {
    companion object {
        val GSON = Gson()
        val H = Handler(Looper.getMainLooper())
    }

    init {
        View.inflate(ctx, R.layout.v_download_bar, this)
    }

    lateinit var mConnection: ServiceConnection
    lateinit var mServiceStub: IFDownloadAction
    val mActionTv by bid<TextView>(R.id.tv_dlbar_pg)
    val mProgress by bid<FrameLayout>(R.id.fl_dlbar_progress)
    private var mStatus: DownloadStatus = DownloadStatus.IDLE
        set(value) {
            if (field != value) {
                field = value
                flushUI()
            }
        }
    val mCK = object : IFDownloadCallbacks.Stub() {
        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?) {
        }

        override fun onProgress(pg: Double) {
            post {
                mActionTv.text = "下载中"
                progressUI(pg)
                mOnProgress(pg)
            }
        }

        override fun onComplete(filePath: String) {
            post {
                progressUI(0.0)
                mOnComplete(filePath)
                installApp(ctx, filePath)
                mStatus = DownloadStatus.COMPLETE
            }
        }

        override fun onFailed(msg: String) {
            post {
                mOnFailed(msg)
                mStatus = DownloadStatus.FAILED
                ZToast(msg)
            }
        }

        override fun onCanceled(msg: String) {
            post {
                mOnCanceled(msg)
                mStatus = DownloadStatus.IDLE
            }
        }

        override fun onPause(msg: String) {
            post {
                mOnPause(msg)
                mStatus = DownloadStatus.PAUSE
            }
        }
    }
    var mOnProgress: (Double) -> Unit = {}
    var mOnComplete: (String) -> Unit = {}
    var mOnFailed: (String) -> Unit = {}
    var mOnCanceled: (String) -> Unit = {}
    var mOnPause: (String) -> Unit = {}
    var mTag = ""

    /****PUBLIC FUNCS****/
    fun bindTag(tag: String) {
        mTag = tag
        initStatusBySP(mTag)
        initConnect()
    }

    fun putInfo(name: String, url: String, size: Int) {
        postDelayed({
            mServiceStub.initInfo(mTag, name, url, size)
        }, 100)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.e("FDB", "ONDETACHED")
        release()
    }

    fun release() {
        try {
            if (this::mConnection.isInitialized)
                ctx.applicationContext.unbindService(mConnection)
        } catch (ex: Exception) {
        }
    }

    /****INITIAL****/
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.e("FDB", "ONATTACHED")
        cleanView()
    }

    private fun cleanView() {
        mActionTv.text = "下载"
        progressUI(0.0)
        flushUI()
    }

    private fun initStatusBySP(tag: String) {
        if (hasInfo(ctx, tag)) {
            takeInfo(ctx, tag)?.run {
                if (ptr != size)
                    mStatus = DownloadStatus.PAUSE
                else if (File(filePath).exists())
                    mStatus = DownloadStatus.INSTALL_CHK
                else {
                    deleteInfo(ctx, tag)
                    mStatus = DownloadStatus.IDLE
                }
            }
        } else {
            mStatus = DownloadStatus.IDLE
        }
    }

    private fun initConnect() {
        mConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                Log.e("remote service", "disconnected")
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.e("remote service", "connected")
                mServiceStub = IFDownloadAction.Stub.asInterface(service)
                mServiceStub.registerCK(mTag, mCK)
            }
        }
        ctx.applicationContext.bindService(Intent(ctx.applicationContext, FishDownloaderSVC::class.java), mConnection, Service.BIND_AUTO_CREATE)
    }


    /****Inner funcs****/
    private fun progressUI(pg: Double) {
        mProgress.layoutParams = mProgress.layoutParams.apply { this@apply.width = (this@FDownloadBar.width * pg).toInt() }
    }

    private fun download() {
        mStatus = DownloadStatus.DOWNLOADING
        mServiceStub.startDownload(mTag)
    }

    private fun pause() {
        mServiceStub.pauseByTag(mTag)
    }

    private fun open() {
        try {
            ctx.startActivity(ctx.packageManager.getLaunchIntentForPackage(mTag))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun flushUI() {
        mActionTv.run {
            when (mStatus) {
                DownloadStatus.IDLE -> {
                    text = "下载"
                    onceClick(this@FDownloadBar::download)
                }
                DownloadStatus.DOWNLOADING -> {
                    text = "下载中"
                    onceClick(this@FDownloadBar::pause)
                }
                DownloadStatus.COMPLETE -> {
                    text = "安装中"
                    postDelayed({ mStatus = DownloadStatus.INSTALL_CHK }, 1000)
                }
                DownloadStatus.PAUSE -> {
                    text = "继续"
                    onceClick(this@FDownloadBar::download)
                    progressUI(takeInfo(ctx, mTag)?.run { 1.0 * ptr / size } ?: return@run)
                }
                DownloadStatus.FAILED -> {
                    text = "失败"
                }
                DownloadStatus.INSTALL_CHK -> {
                    if (isInstalled(ctx, mTag)) {
                        text = "打开"
                        setOnClickListener { open() }
                    } else {
                        text = "安装"
                        onceClick {
                            mStatus = DownloadStatus.COMPLETE
                            installApp(ctx, takeInfo(ctx, mTag)?.filePath ?: return@onceClick)
                        }
                    }
                }
            }
        }
    }


    /****others****/
    private enum class DownloadStatus {
        IDLE,
        DOWNLOADING,
        COMPLETE,
        PAUSE,
        FAILED,
        INSTALL_CHK,
    }

    var onceClkFlag = false
    private fun onceClick(ck: () -> Unit) {
        onceClkFlag = true
        setOnClickListener {
            onceClkFlag = false
            ck()
            if (!onceClkFlag)
                setOnClickListener {}
        }
    }

    fun ZToast(msg: String) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
    }
}

fun installApp(ctx: Context, filePath: String) = try {
    ctx.startActivity(Intent(Intent.ACTION_VIEW).run {
        setDataAndType(Uri.fromFile(File(filePath)), "application/vnd.android.package-archive")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
} catch (ex: Exception) {
    ex.printStackTrace()
}

fun isInstalled(ctx: Context, pkg: String) = pkg in ctx.packageManager
        .getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
        .filter { it != null }.map { it.packageName } ?: listOf<String>()