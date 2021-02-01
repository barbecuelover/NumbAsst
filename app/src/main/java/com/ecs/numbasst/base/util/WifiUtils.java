package com.ecs.numbasst.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.PatternMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zw
 * @time 2021/1/28
 * @description
 */
public class WifiUtils {


    private WifiManager wifiManager;

    public WifiUtils(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }

    /**
     * wifi是否打开
     *
     * @return
     */
    public boolean isWifiEnable() {
        boolean isEnable = false;
        if (wifiManager != null) {
            if (wifiManager.isWifiEnabled()) {
                isEnable = true;
            }
        }
        return isEnable;
    }

    /**
     * 打开WiFi
     */
    public void openWifi() {
        if (wifiManager != null && !isWifiEnable()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭WiFi
     */
    public void closeWifi() {
        if (wifiManager != null && isWifiEnable()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 获取WiFi列表
     *
     * @return
     */
    public List<ScanResult> getWifiList() {
        List<ScanResult> resultList = new ArrayList<>();
        if (wifiManager != null && isWifiEnable()) {
            resultList.addAll(wifiManager.getScanResults());
        }
        return resultList;
    }

    /**
     * 有密码连接
     *
     * @param ssid
     * @param pws
     */
    public void connectWifiPws(String ssid, String pws) {
        wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
        int netId = wifiManager.addNetwork(getWifiConfig(ssid, pws, true));
        wifiManager.enableNetwork(netId, true);
    }

    /**
     * 无密码连接
     *
     * @param ssid
     */
    public void connectWifiNoPws(String ssid) {
        wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
        int netId = wifiManager.addNetwork(getWifiConfig(ssid, "", false));
        wifiManager.enableNetwork(netId, true);
    }

    /**
     * wifi设置
     *
     * @param ssid
     * @param pws
     * @param isHasPws
     */
    private WifiConfiguration getWifiConfig(String ssid, String pws, boolean isHasPws) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        WifiConfiguration tempConfig = isExist(ssid);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }
        if (isHasPws) {
            config.preSharedKey = "\"" + pws + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return config;
    }

    /**
     * 得到配置好的网络连接
     *
     * @param ssid
     * @return
     */
    private WifiConfiguration isExist(String ssid) {
        @SuppressLint("MissingPermission") List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                return config;
            }
        }
        return null;
    }


    public static void wifiConnect(Context context, WifiManager wifiManager, ConnectivityManager.NetworkCallback networkCallback, String name, String password) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            NetworkSpecifier specifier =
                    new WifiNetworkSpecifier.Builder()
                            .setSsidPattern(new PatternMatcher(name, PatternMatcher.PATTERN_PREFIX))
                            .setWpa2Passphrase(password)
                            .build();

            NetworkRequest request =
                    new NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .setNetworkSpecifier(specifier)
                            .build();

            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            connectivityManager.requestNetwork(request, networkCallback);
            // Release the request when done.
            // connectivityManager.unregisterNetworkCallback(networkCallback);
        } else {
            connectWifi(wifiManager, name, password, "WPA");
        }
    }


    public static void connectWifi(WifiManager wifiManager, String wifiName, String password, String type) {
        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        String ssid = "\"" + wifiName + "\"";
        String psd = "\"" + password + "\"";

        //2、配置wifi信息
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        switch (type) {
            case "WEP":
                // 加密类型为WEP
                conf.wepKeys[0] = psd;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA"://WEPA2 AES
                // 加密类型为WPA
                conf.preSharedKey = psd;
                break;
            default:
                //无密码
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        //3、链接wifi
        // wifiManager.addNetwork(conf);
        Log.d("zwcc", "添加WIFI");

        @SuppressLint("MissingPermission") List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        Log.d("zwcc", " List WifiConfiguration =" + list.size());
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals(ssid)) {
                wifiManager.disconnect();
                wifiManager.addNetwork(conf);
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                Log.d("zwcc", "重连 netWorkID ");
                return;
            }
        }

        wifiManager.disableNetwork(wifiManager.getConnectionInfo().getNetworkId());
        int netId = wifiManager.addNetwork(conf);
        wifiManager.enableNetwork(netId, true);

        Log.d("zwcc", "新连接  netWorkID =" + netId);
    }
}
