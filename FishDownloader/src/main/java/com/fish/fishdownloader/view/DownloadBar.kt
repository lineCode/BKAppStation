package com.fish.downloader.view

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.fish.downloader.extensions.bid
import com.fish.downloader.service.DownloadService
import com.fish.fishdownloader.IDownloadCK
import com.fish.fishdownloader.IDownloader
import com.fish.fishdownloader.R
import com.fish.fishdownloader.view.ColorChangedTextView

/**
 * Created by fish on 17-9-6.
 */
class DownloadBar(ctx: Context, attrs: AttributeSet?) : FrameLayout(ctx, attrs) {

    companion object {
        val DOWNLOADING_COLOR: Int = 0xfffde179.toInt()
        val COMPLETE_COLOR: Int = 0xfffff1ba.toInt()
        val TEXT_COLOR: Int = 0xff1a96fc.toInt()
    }

    init {
        View.inflate(context, R.layout.v_download_bar, this)
    }

    val mTvPG by bid<ColorChangedTextView>(R.id.tv_dlbar_pg)
    val mFlPG by bid<FrameLayout>(R.id.fl_dlbar_progress)
    val mBG by bid<FrameLayout>(R.id.fl_dlbar_bg)
    val mMask by bid<ImageView>(R.id.img_dlbar_mask)

    lateinit var mDlck: (type: CK_TYPE, data: String?) -> Unit
    lateinit var mTag: String
    lateinit var mUrl: String
    lateinit var mFileName: String
    var mSize: Long = 0

    var mConf = DownloadBarConfigure { initView() }
        set(conf) {
            Log.e("SET CONF", "DO")
            field = conf
            initView()
        }
    val mCK = object : IDownloadCK.Stub() {
        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?) {}

        override fun onPause(tag2: String?, filePath: String?, ptr: Int, size: Int) {
            if (tag.equals(mTag)) {
                onceReDownload()
            }
        }

        override fun onProgress(tag2: String?, pg: Double) {
            if (tag2.equals(mTag))
                progress(pg)
        }

        override fun onComplete(tag2: String?, filePath: String?) {
            if (tag2.equals(mTag)) {
                mTvPG.stopTextProg(mConf.compileTextColor)
                complete(filePath)
                if (this@DownloadBar::mDlck.isInitialized)
                    mDlck(CK_TYPE.COMPLETE, filePath)
            }
        }

        override fun onFailed(tag2: String?, msg: String?) {
            if (tag2.equals(mTag)) {
                mTvPG.stopTextProg(mConf.compileTextColor)
                if (this@DownloadBar::mDlck.isInitialized)
                    mDlck(CK_TYPE.FAILED, msg)
            }
        }

        override fun onCanceled(tag2: String?) {
            if (tag2.equals(mTag)) {
                mTvPG.setTextColor(mConf.compileTextColor)
                onceReDownload()
                if (this@DownloadBar::mDlck.isInitialized)
                    mDlck(CK_TYPE.CANCELED, "")
            }
        }
    }

    var mServiceBinder: IDownloader? = null

    val mHandler = Handler(Looper.getMainLooper())

    var mConnection: ServiceConnection? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initView()
    }

    private fun initConnect() {
        mConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                Log.e("remote service", "disconnected")
                mServiceBinder?.unregisterCB(mCK)
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.e("remote service", "connected")
                mServiceBinder = IDownloader.Stub.asInterface(service)
                mServiceBinder?.registerCB(mCK)
            }
        }
        context.bindService(Intent(context, DownloadService::class.java), mConnection, Service.BIND_AUTO_CREATE)
    }

    fun init(ck: (type: CK_TYPE, data: String?) -> Unit) {
        if (!this::mDlck.isInitialized)
            mDlck = ck
        initConnect()
    }

    fun initInfo(tag: String, fileName: String, fileSize: Long, url: String) {
        mTag = tag
        mFileName = fileName
        mSize = fileSize
        mUrl = url
    }

    private fun initView() {
        mTvPG.text = mConf.initText
        mTvPG.setTextColor(mConf.textColor)
        mMask.setBackgroundResource(mConf.maskRes)
        if (mConf.baseBGRes == null) mBG.setBackgroundColor(mConf.baseBGColor) else mBG.setBackgroundResource(mConf.baseBGRes ?: return)
        if (mConf.initBGRes == null) mFlPG.setBackgroundColor(mConf.initBGColor) else mFlPG.setBackgroundResource(mConf.initBGRes ?: return)
        Log.e("attach to win", "att")
    }

    fun text(str: String) {
        mTvPG.text = str
    }

    private fun onceReDownload() = setOnClickListener {
        download()
        setOnClickListener {}
    }

    private fun download() = try {
        mServiceBinder?.startDownload(mUrl, mTag, mFileName, mSize)
        chgDownloadUI()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    private fun chgDownloadUI() {
        mFlPG.layoutParams = mFlPG.layoutParams.apply { width = 0 }
        mTvPG.setTextColor(mConf.downloadingTextColor)
        if (mConf.downloadingBGRes == null) mFlPG.setBackgroundColor(mConf.downloadingBGColor) else mFlPG.setBackgroundResource(mConf.downloadingBGRes ?: return)
    }

    private fun complete(filePath: String?) {
        mHandler.postDelayed({
            if (mConf.completeBGRes == null) mFlPG.setBackgroundColor(mConf.completeBGColor) else mFlPG.setBackgroundResource(mConf.completeBGRes ?: return@postDelayed)
            mTvPG.text = mConf.completeText
        }, 100)
    }

    private fun progress(pg: Double) {
        mHandler.post {
            mConf.pogressCK(this@DownloadBar, mFlPG, pg, mTvPG)
        }
    }

    fun disconnectService() {
        context.unbindService(mConnection ?: return)
    }

    fun cancelByTag(tag: String) {
        mServiceBinder?.cancelDownloadByTag(tag)
    }

    fun cancelAll() {
        mServiceBinder?.cancelAll()
    }

    enum class CK_TYPE { COMPLETE, CANCELED, FAILED }

    data class DownloadBarConfigure(var initText: String = "下载",
                                    var downloadingText: String = "下载",
                                    var completeText: String = "完成",
                                    var textColor: Int = TEXT_COLOR,
                                    var downloadingTextColor: Int = TEXT_COLOR,
                                    var compileTextColor: Int = TEXT_COLOR,
                                    var initBGColor: Int = DOWNLOADING_COLOR,
                                    var initBGRes: Int? = null,
                                    var downloadingBGColor: Int = DownloadBar.DOWNLOADING_COLOR,
                                    var completeBGColor: Int = DownloadBar.COMPLETE_COLOR,
                                    var downloadingBGRes: Int? = null,
                                    var completeBGRes: Int? = null,
                                    var baseBGColor: Int = 0xfffff1ba.toInt(),
                                    var baseBGRes: Int? = null,
                                    var maskRes: Int = R.drawable.i_download_top,
                                    var pogressCK: (parentView: View, progressBar: FrameLayout, pg: Double, colorChangableTV: ColorChangedTextView) -> Unit = { view, img, pg, ctv ->
                                        {
                                            img.layoutParams = img.layoutParams.apply { this@apply.width = (view.width * pg).toInt() }
                                            ctv.setTextProg(String.format(downloadingText), (view.width * pg).toInt())

                                        }()
                                    },
                                    val notifyConfigureChanged: () -> Unit)
}