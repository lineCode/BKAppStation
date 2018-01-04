package cc.fish.cld_ctrl.common.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import cc.fish.cld_ctrl.common.service.DownloadService;

/**
 * Created by fish on 16-12-14.
 */

public class DownloadUtils {
    /**
     * 启动下载友商App服务
     */
    public static Map<String, String[]> appSuccCK = new HashMap<>();
    public static Map<String, Boolean> appNeedInstall = new HashMap<>();
    public static String appToken = "";
    public static String appUid = "";

    public static void startDownService(Context context, String url, String appName) {
        startDownService(context, url, appName, false);
    }

    public static boolean startDownService(Context context, String url, String appName, boolean onlyWifi, Object... exData) {
        if (!isHaveWifi(context) && onlyWifi) {
            Toast.makeText(context, "只在无线环境下载已打开，请前往设置关闭按钮，或加入无线网络", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isHaveWifi(context)) {
            Toast.makeText(context, "正在使用数据流量下载", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("downloadUrl", url);
        intent.putExtra("appName", appName);
        context.startService(intent);
        if (exData.length >= 5) {
            appSuccCK.put((String)exData[0], (String[])exData[1]);
            appToken = (String) exData[2];
            appUid = (String) exData[3];
            appNeedInstall.put((String)exData[0], (Boolean) exData[4]);
        }
        return true;
    }

    private static boolean isHaveWifi(Context mContext) {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}
