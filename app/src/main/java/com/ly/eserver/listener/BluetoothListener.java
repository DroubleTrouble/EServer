package com.ly.eserver.listener;

import android.os.Message;

/**
 * 蓝牙监听
 */

public interface BluetoothListener {
    /**
     * 接收监听
     * @param rev
     */
    void onBluetoothReceived(Message rev);
}