package cn.alexchao.dcnn_master;

import android.content.Context;
import android.net.wifi.WifiManager;

public class Util {
    public static String getLocalIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int intIpAddress = wifiManager.getConnectionInfo().getIpAddress();
        return intToIp(intIpAddress);
    }

    private static String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
}
