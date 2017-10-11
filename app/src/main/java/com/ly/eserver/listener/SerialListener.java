package com.ly.eserver.listener;

import android.os.Message;

/**
 * 串口监听
 * @author Xuqn
 *
 */
public interface SerialListener {
	/**
	 * 接收监听
	 * @param rev
	 */
    void onSerialReceived(Message rev);
}
