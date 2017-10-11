package com.ly.eserver.protocol.collector.ahi;

import android.annotation.SuppressLint;

import java.nio.ByteBuffer;
import java.util.Locale;

/**
 * 安徽I型采集器规约工具
 *
 * @author Xuqn
 */
@SuppressLint("DefaultLocale")
public class Collector_AHI_Util {
    public static final String TAG = "Collector_AHI_Util";

    public static final int COLLECTOR_ADDR_INDEX = 1;
    public static final int TYPE_INDEX = 8;
    public static final int PROTOCOL_DATA_LENGTH_INDEX = 9;
    public static final int ADDR_INDEX = 10;
    public static final int DATA_INDEX = 10;
    public static final int DATA_WITH_ADDR_INDEX = 16;
    public static final int PW_INDEX = 20;
    public static final int OPEAR_NO_INDEX = 24;

    /**
     * 协议类型
     *
     * @author Xuqn
     */
    public static class PROTOCOL_TYPE {
        /**
         * 读数据
         */
        public static int READ = 0x11;
        /**
         * 写数据
         */
        public static int WRITE = 0x14;
        /**
         * 修改密码
         */
        public static int CHANGE_PW = 0x18;
        /**
         * 初始化
         */
        public static int INIT = 0x1A;
        /**
         * 成功
         * 协议类型状态字需与上0xF0处理
         */
        public static int SUCCESS = 0x90;
        /**
         * 错误
         * 协议类型状态字需与上0xF0处理
         */
        public static int ERROR = 0xD0;
        /**
         * 有后续帧
         * 协议类型状态字需与上0xF0处理
         */
        public static int SUB_FRAMES = 0xB0;

    }

    /**
     * 初始化类型
     *
     * @author Xuqn
     */
    public static class INIT_TYPE {
        /**
         * 硬件复位,需03级密码
         */
        public static byte HARDWARE_RESET = (byte) 0xA0;
        /**
         * 数据区初始化,需02级密码
         */
        public static byte DATA_INIT = (byte) 0xA1;
        /**
         * 设置与数据区初始化,需01级密码
         */
        public static byte CONFIG_AND_DATA_INIT = (byte) 0xA2;
        /**
         * 清除所有已配表
         */
        public static byte CLEAR_ALL_METER_CONFIG = (byte) 0xA3;
    }

    /**
     * 读数据
     */
    public static final byte[] READ_PROTOCOL = new byte[]{
            (byte) 0x68,
            (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
            (byte) 0x68,
            (byte) 0x11,
            (byte) 0x0A,
            (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD,
            (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, //数据标识
            (byte) 0x00,
            (byte) 0x16};

    /**
     * 读后续帧
     */
    public static final byte[] READ_SUB_FRAME_PROTOCOL = new byte[]{
            (byte) 0x68,
            (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
            (byte) 0x68,
            (byte) 0x12,
            (byte) 0x0B,
            (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD,
            (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, //数据标识
            (byte) 0x33, //帧序号
            (byte) 0x00,
            (byte) 0x16};

    /**
     * 写数据
     */
    public static final byte[] WRITE_PROTOCOL = new byte[]{
            (byte) 0x68,//first
            (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, //采集器地址
            (byte) 0x68, //second起始
            (byte) 0x14,//
            (byte) 0x00,
            (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD,
            (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, //数据标识
            (byte) 0x33, //权限
            (byte) 0x33, (byte) 0x33, (byte) 0x33, //密码
            (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, //操作员编号
            (byte) 0x00,
            (byte) 0x16};

    /**
     * 修改密码
     */
    public static final byte[] CHANGE_PW_PROTOCOL = new byte[]{
            (byte) 0x68,
            (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
            (byte) 0x68,
            (byte) 0x18,
            (byte) 0x12,
            (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD,
            (byte) 0x33, (byte) 0x33, (byte) 0x33, (byte) 0x33, //数据标识
            (byte) 0x33, //旧权限
            (byte) 0x33, (byte) 0x33, (byte) 0x33, //旧密码
            (byte) 0x33, //新权限
            (byte) 0x33, (byte) 0x33, (byte) 0x33, //新密码
            (byte) 0x00,
            (byte) 0x16};

    /**
     * 广播校时
     */
    public static final byte[] BROADCAST_TIME_PROTOCOL = new byte[]{
            (byte) 0x68,
            (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99,
            (byte) 0x68,
            (byte) 0x08,
            (byte) 0x06,
            (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD,
            (byte) 0x33, (byte) 0x33, (byte) 0x33, //时间 秒,分,时
            (byte) 0x33, (byte) 0x33, (byte) 0x33, //日期 日,月,年
            (byte) 0x00,
            (byte) 0x16};


    /**
     * 初始化
     */
    public static final byte[] INIT_PROTOCOL = new byte[]{
            (byte) 0x68,
            (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
            (byte) 0x68,
            (byte) 0x1A,
            (byte) 0x0F,
            (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD, (byte) 0xDD,
            (byte) 0x33, //权限
            (byte) 0x33, (byte) 0x33, (byte) 0x33, //密码
            (byte) 0xAB, (byte) 0x89, (byte) 0x67, (byte) 0x45, //操作员
            (byte) 0x33, //初始化类型
            (byte) 0x00, (byte) 0x16};
    /**
     * 强制密码初始化
     * TLY2310E
     */
    public static final byte[] FORCE_PASSWORD = new byte[]{
            (byte) 0x54, (byte) 0x4C, (byte) 0x59, (byte) 0x32,
            (byte) 0x33, (byte) 0x31, (byte) 0x30, (byte) 0x45,//密码TLY2301E
    };

    /**
     * 数据帧校验
     *
     * @param protocol
     * @return
     */
    public static boolean checkProtocol(byte[] protocol) {
        //校验长度
        if (protocol.length < 12) return false;
        int length = protocol[9] & 0xFF;
        if (protocol.length - 12 != length) return false;
        //校验标志位
        if (protocol[0] != 0x68) return false;
        if (protocol[7] != 0x68) return false;
        if (protocol[protocol.length - 1] != 0x16) return false;
        //校验校验位
        int check = updateCheckSum(protocol)[protocol.length - 2] & 0xFF;
        return check == (protocol[protocol.length - 2] & 0xFF);
    }

    /**
     * 数据校验
     *
     * @param buffer
     * @param rev
     * @return
     */
    public static byte[] checkProtocol(ByteBuffer buffer, byte... rev) {
        if (buffer == null || rev == null || rev.length == 0) return null;
        if (buffer.remaining() < rev.length) {
            buffer.clear();
            return null;
        }
        for (int i = 0; i < rev.length; i++) {
            if (buffer.position() != 0) {
                if (rev[i] == 0x16) {
                    if (buffer.position() == 9) {
                        //若为长度位
                        buffer.put(rev[i]);
                    } else if (buffer.position() >= 11) {
                        buffer.put(rev[i]);
                        //获取长度
                        int len = (buffer.array()[9] & 0xFF) + 12;//2(报文头) + 6(地址) + 1(控制字) + 1(长度位) + 1(校验位) + 1(结束位)
                        //检测长度是否正确
                        if (buffer.position() == len) {
                            //正确结束报文
                            byte[] result = new byte[buffer.position()];
                            System.arraycopy(buffer.array(), 0, result, 0, result.length);
                            buffer.clear();
                            return result;
                        } else if (buffer.position() > len) {
                            //错误报文
                            return null;
                        }
                    } else if (buffer.position() < 7) {
                        //包含在地址中的0x16
                        buffer.put(rev[i]);
                    } else {
                        //报文不正确
                        buffer.clear();
                    }
                } else if (buffer.position() == 7) {
                    if (rev[i] == 0x68) {
                        //正确报文头结束
                        buffer.put(rev[i]);
                    } else {
                        //报文头长度错误
                        buffer.clear();
                    }
                } else {
//					if(rev[i] == 0x68){
//						//判断是否为帧数据
//						if(buffer.position() >= 11){
//							buffer.put(rev[i]);
//							//获取长度
//							int len = (buffer.array()[9]&0xFF) + 12;//2(报文头) + 6(地址) + 1(控制字) + 1(长度位) + 1(校验位) + 1(结束位)
//							if(buffer.position() > len){
//								//超过当前保存帧片段长度,重新定位帧头
//								buffer.clear();
//							}
//						}else{
//							//帧头有误,重新定位帧头
//							buffer.clear();
//						}
//					}
                    buffer.put(rev[i]);
                }
            } else if (rev[i] == 0x68) {
                //接收到报文头开始
                buffer.put((byte) 0x68);
            }

        }
        return null;
    }

    /**
     * 获取采集器地址
     *
     * @param protocol
     * @return
     */
    public static byte[] getCollectorAddr(byte[] protocol) {
        byte[] addr = new byte[6];
        System.arraycopy(protocol, COLLECTOR_ADDR_INDEX, addr, 0, addr.length);
        return swap(addr);
    }

    /**
     * 获取表计地址
     *
     * @param protocol
     * @return
     */
    public static byte[] getMeterAddr(byte[] protocol) {
        //错误返回帧,不包含表计地址
        if (getProtocolType(protocol) == PROTOCOL_TYPE.ERROR) return new byte[0];
        byte[] addr = new byte[6];
        System.arraycopy(protocol, ADDR_INDEX, addr, 0, addr.length);
        return addr;
    }

    /**
     * 移除表计地址
     *
     * @param protocol
     * @return
     */
    public static byte[] removeMeterAddr(byte[] protocol) {
        ByteBuffer bf = ByteBuffer.wrap(protocol);
        byte[] ret = new byte[protocol.length - 6];
        bf.get(ret, 0, ADDR_INDEX);
        bf.position(bf.position() + 6);
        bf.get(ret, ADDR_INDEX, ret.length - ADDR_INDEX);
        return ret;
    }

    /**
     * 获取协议类型
     *
     * @param protocol
     * @return
     */
    public static byte getProtocolType(byte[] protocol) {
        return protocol[TYPE_INDEX];
    }

    /**
     * 获取07数据标识
     *
     * @param protocol
     * @return
     */
    public static byte[] get07DataFlag(byte[] protocol, boolean haveMeterAddr) {
        byte[] dataFlag;
        if ((getProtocolType(protocol) & 0xF0) != PROTOCOL_TYPE.ERROR) {
            dataFlag = new byte[4];
            System.arraycopy(protocol,
                    haveMeterAddr ? DATA_WITH_ADDR_INDEX : DATA_INDEX
                    , dataFlag, 0, dataFlag.length);
        } else {
            dataFlag = new byte[0];
        }
        return swap(sum(-0x33, dataFlag));
    }

    /**
     * 获取97数据标识
     *
     * @param protocol
     * @return
     */
    public static byte[] get97DataFlag(byte[] protocol, boolean haveMeterAddr) {
        byte[] dataFlag;
        if ((getProtocolType(protocol) & 0xF0) != PROTOCOL_TYPE.ERROR) {
            dataFlag = new byte[2];
            System.arraycopy(protocol, haveMeterAddr ? DATA_WITH_ADDR_INDEX : DATA_INDEX, dataFlag, 0, dataFlag.length);
        } else {
            dataFlag = new byte[0];
        }
        return swap(sum(-0x33, dataFlag));
    }

    /**
     * 获取07数据区(不包含表计地址与数据标识)
     *
     * @param protocol
     * @return
     */
    public static byte[] get07Data(byte[] protocol, boolean haveMeterAddr) {
        int length = protocol.length;
        byte[] data;
        if ((getProtocolType(protocol) & 0xF0) != PROTOCOL_TYPE.ERROR) {
            //非错误返回帧时,包含表计地址
            if (haveMeterAddr) {
                data = new byte[length - DATA_WITH_ADDR_INDEX - 2 - 4];//数据域不包含数据标识
                System.arraycopy(protocol, DATA_WITH_ADDR_INDEX + 4, data, 0, data.length);
            } else {
                data = new byte[length - DATA_INDEX - 2 - 4];//数据域不包含数据标识
                System.arraycopy(protocol, DATA_INDEX + 4, data, 0, data.length);
            }
        } else {
            data = new byte[length - DATA_INDEX - 2];
            System.arraycopy(protocol, DATA_INDEX, data, 0, data.length);
        }
        return swap(sum(-0x33, data));
    }

    /**
     * 获取97数据区(不包含表计地址与数据标识)
     *
     * @param protocol
     * @return
     */
    public static byte[] get97Data(byte[] protocol, boolean haveMeterAddr) {
        int length = protocol.length;
        byte[] data;
        if ((getProtocolType(protocol) & 0xF0) != PROTOCOL_TYPE.ERROR) {
            //非错误返回帧时,包含表计地址
            if (haveMeterAddr) {
                data = new byte[length - DATA_WITH_ADDR_INDEX - 2 - 2];//数据域不包含数据标识
                System.arraycopy(protocol, DATA_WITH_ADDR_INDEX + 2, data, 0, data.length);
            } else {
                data = new byte[length - DATA_INDEX - 2 - 2];//数据域不包含数据标识
                System.arraycopy(protocol, DATA_INDEX + 2, data, 0, data.length);
            }
        } else {
            data = new byte[length - DATA_INDEX - 2];
            System.arraycopy(protocol, DATA_INDEX, data, 0, data.length);
        }
        return swap(sum(-0x33, data));
    }

    /**
     * 生成读后续帧请求
     *
     * @param dataFlag   数据标识
     * @param frameIndex 帧序号
     * @return
     */
    public static byte[] getSubFrameRequest(byte[] dataFlag, int frameIndex) {
        byte[] request = READ_SUB_FRAME_PROTOCOL.clone();
        //更新数据标识
        request = updateDataFlag(request, swap(sum(0x33, dataFlag)));
        //更新帧序号
        request[0] = Integer.valueOf(frameIndex + 0x33).byteValue();
        //更新校验位
        request = updateCheckSum(request);
        return request;
    }

    /**
     * 插入数据域
     *
     * @param protocol
     * @param data
     * @return
     */
    public static byte[] insertData(byte[] protocol, byte... data) {
        int offset = protocol.length - 2;
        byte[] ret = new byte[protocol.length + data.length];
        System.arraycopy(protocol, 0, ret, 0, offset);
        System.arraycopy(data, 0, ret, offset, data.length);
        System.arraycopy(
                protocol, offset,
                ret, offset + data.length,
                protocol.length - offset);
        return ret;
    }

    /**
     * 更新数据标识
     *
     * @param protocol
     * @param dataFlag
     * @return
     */
    public static byte[] updateDataFlag(byte[] protocol, byte[] dataFlag) {
        System.arraycopy(dataFlag, 0, protocol, DATA_WITH_ADDR_INDEX, dataFlag.length);
        return protocol;
    }

    /**
     * 进行校验位计算
     *
     * @param protocol
     * @return
     */
    public static byte[] updateCheckSum(byte[] protocol) {
        Integer sum = 0;
        for (int i = 0; i < protocol.length - 2; i++) {
            sum += protocol[i] & 0xFF;
        }
        protocol[protocol.length - 2] = Integer.valueOf(sum & 0xFF).byteValue();
        return protocol;
    }

    /**
     * 进行长度计算
     *
     * @param protocol
     * @return
     */
    public static byte[] updateLength(byte[] protocol, boolean haveMeterAddr) {
        Integer length = 0;
        if (haveMeterAddr) {
            length = getMeterAddr(protocol).length;
        }
        length += get07DataFlag(protocol, haveMeterAddr).length;//数据标识长度
        length += get07Data(protocol, haveMeterAddr).length;//数据长度
        protocol[PROTOCOL_DATA_LENGTH_INDEX] = length.byteValue();
        return protocol;
    }

    /**
     * 更新采集器地址
     *
     * @param protocol
     * @param addr
     * @return
     */
    public static byte[] updateCollecotrAddr(byte[] protocol, byte[] addr) {
        System.arraycopy(addr, 0, protocol, COLLECTOR_ADDR_INDEX, addr.length);
        return protocol;
    }

    /**
     * 更新表计地址
     *
     * @param protocol
     * @param addr
     * @return
     */
    public static byte[] updateMeterAddr(byte[] protocol, byte[] addr) {
        addr = swap(sum(0x33, addr));
        System.arraycopy(addr, 0, protocol, ADDR_INDEX, addr.length);
        return protocol;
    }

    /**
     * 更新密码,权限等级,操作者编号
     *
     * @param protocol
     * @param pw       密码
     * @param level    密码权限
     * @param opearNo  操作者编号,可为空
     * @return
     */
    public static byte[] updatePwAndOpearNo(byte[] protocol, byte[] pw, byte level, byte[] opearNo) {
        //更新权限码
        protocol[PW_INDEX] = level;
        //更新密码
        System.arraycopy(pw, 0, protocol, PW_INDEX + 1, pw.length);
        //更新操作者编号
        if (opearNo != null) {
            System.arraycopy(opearNo, 0, protocol, OPEAR_NO_INDEX, opearNo.length);
        }
        return protocol;
    }

    /**
     * 更新强制密码模式
     *
     * @param protocol
     * @param data     "PA-C3"字段
     * @return protocol
     */
    public static byte[] updatePwAndOpearNoNew(byte[] protocol, byte[] data) {

        if (data != null) {
            System.arraycopy(data, 0, protocol, PW_INDEX, data.length);
        }
        return protocol;
    }

    /**
     * 转换字符串至16进制数组
     *
     * @param str
     * @param splitStr 分隔符
     * @return
     */
    public static byte[] convertStrToBytes(String str, String splitStr) {
        if (str == null) return null;
        if (splitStr == null) splitStr = "";
        //移除分隔符
        str = str.replace(splitStr, "");
        //数据类型检查,是否为16进制字符
        if (!str.matches("^[0-9a-fA-F]*$")) return null;
        StringBuilder sb = new StringBuilder(str);
        //补全长度
        if (sb.length() % 2 != 0) sb.insert(sb.length() - 1, 0);
        //转换为16进制数组
        byte[] ret = new byte[sb.length() / 2];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Integer.valueOf(sb.subSequence(i * 2, i * 2 + 2).toString(), 16).byteValue();

        }
        return ret;
    }

    /**
     * 转换16进制数组至字符串
     *
     * @param bytes
     * @param splitStr 分隔符
     * @return
     */
    public static String converBytesToStr(byte[] bytes, String splitStr) {
        if (bytes == null) return "";
        if (splitStr == null) splitStr = "";
        StringBuilder sb = new StringBuilder();
        String str;
        for (byte b : bytes) {
            str = String.format("%02x", b & 0xFF);
            sb.append(str);
            sb.append(splitStr);
        }
        if (!splitStr.equals("")) {
            return sb.substring(0, sb.length() - 1).toUpperCase(Locale.CHINA);
        } else {
            return sb.toString().toUpperCase(Locale.CHINA);
        }

    }

    /**
     * 移除前导字节
     *
     * @param protocol
     * @return
     */
    public static byte[] removeFE(byte[] protocol) {
        if (protocol == null) return null;
        if (protocol.length != 0) {
            if (protocol[0] == (byte) 0xFE) {
                if (protocol.length == 1) return new byte[0];
                byte[] ret = new byte[protocol.length - 1];
                System.arraycopy(protocol, 1, ret, 0, ret.length);
                return removeFE(ret);
            } else {
                return protocol;
            }
        }
        return new byte[0];
    }

    /**
     * 添加前导字
     *
     * @param protocol
     * @return
     */
    public static byte[] addFE(byte[] protocol) {
        if (protocol == null) return null;
        if (protocol.length != 0) {
            return unit(new byte[]{(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE}, protocol);
        }
        return new byte[0];
    }

    /**
     * 颠倒顺序
     *
     * @param data
     * @return
     */
    public static byte[] swap(byte[] data) {
        byte[] swapData = new byte[data.length];
        int maxIndex = swapData.length - 1;
        for (int i = 0; i < data.length; i++) {
            swapData[maxIndex - i] = data[i];
        }
        return swapData;
    }

    /**
     * 批量加值
     *
     * @param num
     * @param data
     * @return
     */
    public static byte[] sum(int num, byte... data) {
        byte[] ret = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            ret[i] = Integer.valueOf((data[i] & 0xFF) + num).byteValue();
        }
        return ret;
    }

    /**
     * 组合
     *
     * @param data
     * @return
     */
    public static byte[] unit(byte[]... data) {
        int length = 0;
        for (byte[] bs : data) {
            length += bs.length;
        }
        ByteBuffer bf = ByteBuffer.allocate(length);
        for (byte[] bs : data) {
            bf.put(bs);
        }
        return bf.array();
    }

    /**
     * 比对数组
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean equals(byte[] a, byte[] b) {
        if (a.length == b.length) {
            for (int i = 0; i < a.length; i++) {
                if (a[i] != b[i]) return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 获取错误信息
     *
     * @param protocol
     * @return
     */
    public static String getErrorMsg(byte[] protocol) {
        int err = protocol[protocol.length - 3] & 0xFF;
        StringBuilder sb = new StringBuilder();
        String[] msgs = new String[]{"其他错误", "无请求数据", "密码错/未授权", "通信速率不能更改"
                , "年时区数超", "日时段数超", "费率数超", ""};
        int errBit;
        for (int i = 0; i < 7; i++) {
            errBit = ((err >> i) & 0x01);
            if (errBit != 0) {
                sb.append(msgs[i]).append(",");
            }

        }
        if (sb.length() > 2) sb.subSequence(0, sb.length() - 1);
        return sb.toString();
    }

    @SuppressLint("DefaultLocale")
    public static String bytes2HexString(byte[] b) {
        String r = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }

        return r;
    }

    /*
     * 16进制字符串转字符串
     */
    public static String hex2String(String hex) throws Exception {
        String r = bytes2String(hexString2Bytes(hex));
        return r;
    }

    /*
     * 字节数组转字符串
     */
    public static String bytes2String(byte[] b) throws Exception {
        String r = new String(b, "UTF-8");
        return r;
    }

    /*
     * 16进制字符串转字节数组
     */
    @SuppressLint("DefaultLocale")
    public static byte[] hexString2Bytes(String hex) {

        if ((hex == null) || (hex.equals(""))) {
            return null;
        } else if (hex.length() % 2 != 0) {
            return null;
        } else {
            hex = hex.toUpperCase();
            int len = hex.length() / 2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i = 0; i < len; i++) {
                int p = 2 * i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p + 1]));
            }
            return b;
        }

    }

    /*
     * 字符转换为字节
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
