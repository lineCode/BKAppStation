package com.xiaozi.appstore.manager

/**
 * Created by fish on 18-1-2.
 */
class DataManager {

    object AppInfoDM {
        @Synchronized
        fun appendApps(apps: Array<RespAppInfo>) {
            apps.map {
                RamStorage.appInfoMap.putV(transAppInfo(it), AppInfo::pkg)
                RamStorage.appUploadInfoMap.putV(transAppUpload(it), UploadInfo::pkg)
            }
        }

        @Synchronized
        fun importApps(apps: Array<RespAppInfo>) {
            RamStorage.appInfoMap.clear()
            appendApps(apps)
        }

        @Synchronized
        fun getAppInfos() = RamStorage.appInfoMap.valueArray()

        fun getAppInfo(pkg: String) = RamStorage.appInfoMap[pkg]
        fun getAppUploadInfo(pkg: String) = RamStorage.appUploadInfoMap[pkg]

        private fun transAppUpload(resp: RespAppInfo) = UploadInfo(resp.pkg)
        private fun transAppInfo(resp: RespAppInfo): DataManager.AppInfo {
            TODO("NOT IMPLED")
        }

    }

    data class Banner(val img: String)
    data class AppInfo(val name: String, val pkg: String, val icon: String, val size: String,
                       val tip: String, val content: String, val dlUrl: String, val installCnt: String)

    data class UploadInfo(val pkg: String)

}

object RamStorage {
    val appInfoMap: MutableMap<String, DataManager.AppInfo> = HashMap()
    val appUploadInfoMap: MutableMap<String, DataManager.UploadInfo> = HashMap()
}


@Synchronized
fun <K, V> MutableMap<K, V>.putV(value: V, keyExpr: V.() -> K) = put(value.keyExpr(), value)

@Synchronized
fun <K, V> MutableMap<K, V>.putVs(values: Array<V>, keyExpr: V.() -> K) = values.map { putV(it, keyExpr) }

@Synchronized
inline fun <K, reified V> MutableMap<K, V>.valueArray() = values.toTypedArray()