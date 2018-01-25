package com.xiaozi.appstore.component

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by fish on 18-1-2.
 */
class Framework {
    companion object {
        var mContext: Context? = null
        val _C: Context by lazy { mContext!! }
        val _H = Handler(Looper.getMainLooper())
    }

    object App {
        fun openOtherApp(pkg: String) = try {
            mContext?.startActivity(mContext?.packageManager?.getLaunchIntentForPackage(pkg))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        fun installApp(f: File) =
                mContext?.startActivity(Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
    }

    object Package {
        private val installedPkgs = mutableSetOf<String>()
        @Synchronized fun installed(force: Boolean = false): List<String> {
            if (force || installedPkgs.isEmpty()) {
                installedPkgs.clear()
                installedPkgs.addAll(mContext!!.packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
                        .filter { it != null }.map { it.packageName })
            }
            return installedPkgs.toList()
        }
        fun addInstalled(pkg: String) {
            if (installedPkgs.isEmpty()) {
                installed()
            }
            installedPkgs.add(pkg)
        }
        fun removeInstalled(pkg: String) {
            if (installedPkgs.isEmpty()) {
                installed()
            }
            installedPkgs.remove(pkg)
        }
        fun isInstalled(pkg: String) = pkg in installedPkgs

    }

    object Date {
        fun toYMD(ts: Long): String {
            if (ts == 0L) {
                return "--"
            }
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return df.format(Date(ts * 1000L))
        }

        fun toYMDHMS(ts: Long): String {
            if (ts == 0L) {
                return "--"
            }
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return df.format(Date(ts * 1000L))
        }
    }

    object Trans {
        val LEVEL_M = 1000 shl 10
        val LEVEL_G = 1000 shl 20
        val LEVEL_T = 1000 shl 30
        fun Size(size: Int): String {
            var s: String
            var n: Float
            when (size) {
                in 0..1000 -> {
                    s = "B"
                    n = size / 1f
                }
                in (1000 + 1)..LEVEL_M -> {
                    s = "K"
                    n = size * 1f / (1 shl 10)
                }
                in (LEVEL_M + 1)..LEVEL_G -> {
                    s = "M"
                    n = size * 1f / (1 shl 20)
                }
                else -> {
                    s = "G"
                    n = size * 1f / (1 shl 30)
                }
            }
            return String.format("%.2f%s", n, s)
        }
    }

    object Math {
        fun limitL(a: Long, b: Long) = if (a > b) a - b else 0
    }
}