package com.xiaozi.appstore.manager

import com.xiaozi.appstore.component.Framework

/**
 * Created by fish on 18-1-2.
 */
class DataManager {
    object AppInfoDM {
        @Synchronized
        fun appendApps(apps: Array<RespAppListInfo>) {
            apps.map {
                RamStorage.appInfoMap.putV(trans(it), AppInfo::pkg)
            }
        }

        @Synchronized
        fun importApps(apps: Array<RespAppListInfo>) {
            RamStorage.appInfoMap.clear()
            appendApps(apps)
        }

        @Synchronized
        fun getAppInfos() = RamStorage.appInfoMap.valueArray()

        fun getAppInfo(pkg: String) = RamStorage.appInfoMap[pkg]

        fun trans(data: RespAppListInfo) = data.run {
            AppInfo(appId, packageName, appName, iconUrl, downloadCount, Framework.Trans.Size(size), appDesc, downloadUrl)
        }
    }


    object CategoryDM {
        lateinit var mAppCategory: MutableList<Category>
        lateinit var mGameCategory: MutableList<Category>
        private fun trans(data: RespConfClz) = data.`class`.map {
            Category(it.name, "type icon", it.id, it.subclass.map { SecCategory(it.name, it.id) })
        }
    }

    object Transor {
        fun CommentTransor(data: RespCommentList)
                = data.comments.node.map { Comment("need head icon", 0, it.authorName, Framework.Date.toYMD(it.date), it.content, it.thumbsupCount, it.thumbsupSign, it.commentId) }.toTypedArray()

        fun BannerTransor(data: RespBanners)
                = data.banners.map { Banner(it.banner.image, it.banner.link) }

        fun AppDetailTransor(resp: RespAppInfo) = resp.appInfo.run {
            AppDetail(appId, appName, packageName, iconUrl, Framework.Trans.Size(size), updateLog, tips, desContent, downloadUrl, commentCount, imgUrls)
        }
    }

    data class Banner(val img: String, val link: String)
    data class AppDetail(val appId: Int, val name: String, val pkg: String, val icon: String, val size: String, val updateLog: String,
                         val tip: String, val content: String, val dlUrl: String, val commentCnt: Int, val imgs: Array<String>)

    data class Category(val name: String, val icon: String, val classId: Int, val tabs: List<SecCategory>)
    data class SecCategory(val name: String, val classId: Int)
    data class AppInfo(val appId: Int, val pkg: String, val name: String, val icon: String, val installCnt: Int, val size: String, val tip: String, val dlUrl: String)
    data class Comment(val headIcon: String, val score: Int, val name: String, val time: String, val content: String, val count: Int, val isAgreed: Int, val id: Int)

}

object RamStorage {
    val appInfoMap: MutableMap<String, DataManager.AppInfo> = HashMap()
}


@Synchronized
fun <K, V> MutableMap<K, V>.putV(value: V, keyExpr: V.() -> K) = put(value.keyExpr(), value)

@Synchronized
fun <K, V> MutableMap<K, V>.putVs(values: Array<V>, keyExpr: V.() -> K) = values.map { putV(it, keyExpr) }

@Synchronized
inline fun <K, reified V> MutableMap<K, V>.valueArray() = values.toTypedArray()