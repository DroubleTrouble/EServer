package com.ly.eserver.listener;

import android.os.Message;

/**
 * 设备监听
 *
 * @author Xuqn
 */
public interface DeviceListener {
    /**
     * 设备消息回复
     *
     * @param rev
     */
    boolean onDeviceReceiver(Message rev);
}
