package com.ly.eserver.listener;

import android.os.Message;

/**
 * 红外监听器
 * @author Xuqn
 *
 */
public interface IrListener {
	/**
	 * 接收监听
	 * @param rev
	 */
    void onIrReceived(Message rev);
}
