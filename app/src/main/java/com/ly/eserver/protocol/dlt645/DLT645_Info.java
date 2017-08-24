package com.ly.eserver.protocol.dlt645;


import com.ly.eserver.util.ParseUtil;

/**
 * DLT645数据信息
 *
 * @author Xuqn
 */
public class DLT645_Info {
    public String addr = "AAAAAAAAAAAA";//表地址

    public int dataLen = 0;//数据长度

    public int id = -1;
    public int subId = -1;

    public boolean isReceived = false;//是否应答帧
    public boolean isReceiveCur = false;//应答是否正确
    public boolean haveFollow = false;//是否有后续数据
    public byte conCode = 0x00;//功能码

    public byte[] resultData;//数据


    /**
     * 设置表计地址
     *
     * @param addr
     */
    public void setAmmAddrByte(byte[] addr) {
        byte[] newAddr = new byte[6];
        int addrLen = addr.length > 6 ? 6 : addr.length;
        System.arraycopy(addr, 0, newAddr, 6 - addrLen, addrLen);
        //BCD转字符串
        this.addr = ParseUtil.BCDToSTR(newAddr);
    }
}
