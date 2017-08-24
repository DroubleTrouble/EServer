package com.ly.eserver.util;

import java.nio.ByteBuffer;
import java.util.Calendar;

/**
 * 解析工具类
 *
 * @author Xuqn
 */
public class ParseUtil {

    /**
     * Hex转Integer,低位在前
     *
     * @param value
     * @return
     */
    public static int HEXToINT(byte... value) {
        return (int) HEXToDOUBLE(value);
    }

    /**
     * Hex转Long,低位在前
     *
     * @param value
     * @return
     */
    public static long HEXToLONG(byte... value) {
        return (long) HEXToDOUBLE(value);
    }

    /**
     * Hex转Double,低位在前
     *
     * @param value
     * @return
     */
    public static double HEXToDOUBLE(byte... value) {
        double ret = 0;
        for (int i = 0; i < value.length; i++) {
            ret += (((long) (value[i] & 0xFF)) << (8 * i));
        }
        return ret;
    }

    /**
     * 十六进制转BCD字符串
     *
     * @param hex
     * @return
     */
    public static String BCDToSTR(byte[] hex) {
        int Len = hex.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Len; i++) {
            sb.append(BCDToSTR(hex[i]));
        }
        return sb.toString();
    }

    /**
     * 十六进制转BCD字符串
     *
     * @param hex
     * @return
     */
    public static String BCDToSTR(byte hex) {
        long bcd = BCDToINT(hex);
        String str = bcd < 10 ? "0" : "";
        return str + String.valueOf(bcd);
    }

    /**
     * 转Hex数组
     */
    public static byte[] NUMToHEX(long value, int len) {
        byte[] ret = new byte[len];
        for (int i = 0; i < len; i++) {
            ret[i] = (byte) ((value >> (8 * i)) & 0xFF);
        }
        return ret;
    }

    /**
     * BCD码转Integer
     *
     * @param hex
     * @return
     */
    public static int BCDToINT(byte hex) {
        int bcd, high, low;
        low = hex & 0x0F;
        high = (hex & 0xF0) >> 4;
        if (high > 9 || low > 9) return -1;
        bcd = low + high * 10;
        return bcd;
    }

    /**
     * BCD码转Integer
     *
     * @param hex
     * @return
     */
    public static int BCDToINT(byte[] hex) {
        return (int) BCDToLONG(hex, false);
    }

    /**
     * BCD码转Integer
     *
     * @param hex
     * @param isHighFirst
     * @return
     */
    public static int BCDToINT(byte[] hex, boolean isHighFirst) {
        return (int) BCDToLONG(hex, isHighFirst);
    }

    /**
     * BCD码转Long
     *
     * @param hex
     * @return
     */
    public static long BCDToLONG(byte[] hex) {
        return BCDToLONG(hex, false);
    }

    /**
     * BCD码转Long
     *
     * @param hex
     * @param isHighFirst 是否高位在前
     * @return
     */
    public static long BCDToLONG(byte[] hex, boolean isHighFirst) {
        if (isHighFirst) {
            hex = Swap(hex);
        }
        long ret = 0;
        for (int i = 0; i < hex.length; i++) {
            int a = BCDToINT(hex[i]);
            long b = (long) Math.pow(100, i);
            ret += a * b;
        }
        return ret;
    }

    /**
     * 数字字符串转BCD码
     *
     * @param bcd
     * @return
     */
    public static byte[] STRToBCD(String bcd) {
        StringBuilder sb = new StringBuilder(bcd);
        if (sb.length() % 2 == 1) sb.insert(0, "0");
        int Len = sb.length() / 2;
        byte[] hex = new byte[Len];
        for (int i = 0, a = 0; i < Len; i++, a += 2) {
            hex[i] = INTToBCDByte(Integer.valueOf(sb.substring(a, a + 2)));
        }
        return hex;
    }

    /**
     * 数字转BCD码
     *
     * @param bcd
     * @return
     */
    public static byte[] NUMToBCD(long bcd) {
        String str = String.valueOf(bcd);
        return STRToBCD(str);
    }

    /**
     * 数字转BCD
     *
     * @param bcd
     * @param len 字节长度
     * @return
     */
    public static byte[] NUMToBCD(long bcd, int len) {
        String str = StringUtil.fillWith(String.valueOf(bcd), '0', len * 2, false);
        return STRToBCD(str);
    }

    /**
     * 数字转BCD码
     *
     * @param bcd
     * @return
     */
    public static byte INTToBCDByte(int bcd) {
        byte hex;
        hex = (byte) ((bcd / 10 << 4) + bcd % 10);
        return hex;
    }

    /**
     * 颠倒排序
     *
     * @param bytes
     * @return
     */
    public static byte[] Swap(byte[] bytes) {
        int Len = bytes.length;
        byte[] ret = bytes.clone();
        for (int a = 0, b = Len - 1; a < Len; a++, b--) {
            ret[a] = bytes[b];
        }
        return ret;
    }

    /**
     * 颠倒排序
     *
     * @param <T>
     * @param values
     * @return
     */
    public static <T> T[] Swap(T[] values) {
        int Len = values.length;
        T[] ret = values.clone();
        for (int a = 0, b = Len - 1; a < Len; a++, b--) {
            ret[a] = values[b];
        }
        return ret;
    }

    /**
     * 转换byte数组
     *
     * @param bs
     * @return
     */
    public static byte[] toBytesArray(java.util.List<Byte> bs) {
        int Len = bs.size();
        byte[] ret = new byte[Len];
        for (int i = 0; i < Len; i++) {
            ret[i] = bs.get(i);
        }
        return ret;
    }

    /**
     * 合并数组
     *
     * @param a
     * @return
     */
    public static byte[] mergerByteArray(byte[]... a) {
        //声明新数组长度
        int Len = a.length;
        int arrayLen = 0;
        for (int i = 0; i < Len; i++) {
            arrayLen += a[i].length;
        }
        ByteBuffer bf = ByteBuffer.allocate(arrayLen);
        //合并数组
        for (int i = 0; i < Len; i++) {
            bf.put(a[i]);
        }
        return bf.array();
    }

    /**
     * 用指定字符填补至指定长度
     *
     * @param src     原数组
     * @param f       填补
     * @param len     填补长度
     * @param isRight 填补方向
     * @return
     */
    public static byte[] Fill(byte[] src, byte f, int len, boolean isRight) {
        int srcLen = src.length;
        int fillLen = len - srcLen;
        if (len <= srcLen) return src;
        byte[] ret = new byte[len];
        byte[] fill = new byte[fillLen];
        for (int i = 0; i < fillLen; i++) {
            fill[i] = f;
        }
        if (isRight) {
            //补右
            System.arraycopy(fill, 0, ret, srcLen, fillLen);
            System.arraycopy(src, 0, ret, 0, srcLen);
        } else {
            //补左
            System.arraycopy(src, 0, ret, fillLen, srcLen);
            System.arraycopy(fill, 0, ret, 0, fillLen);
        }
        return ret;
    }

    /**
     * 解析BIN(十六进制)数据
     *
     * @param data
     * @return
     */
    public static int parseDataBIN(byte... data) {
        if (data == null || data.length == 0) return 0;
        if (data[0] == (byte) 0xEE || data[0] == (byte) 0xFF) return -1;
        return HEXToINT(data);
    }

    /**
     * 解析GDW376_2009 数据类型1
     *
     * @param data
     * @return
     */
    public static long parseDataType1(byte[] data) {
        if (data.length < 6) return 0;
        if (data[0] == (byte) 0xEE || data[0] == (byte) 0xFF || data[3] == 0) return -1;
        Calendar c = Calendar.getInstance();
        c.set(BCDToINT(data[5]) % 100 + 2000,
                BCDToINT((byte) (data[4] & 0x1F)) - 1,
                BCDToINT(data[3]),
                BCDToINT(data[2]),
                BCDToINT(data[1]),
                BCDToINT(data[0]));
        return c.getTimeInMillis();
    }


    /**
     * 生成GDW376_2009 数据类型1
     *
     * @param time
     * @return
     */
    public static byte[] formatDataType1(long time) {
        ByteBuffer bf = ByteBuffer.allocate(6);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        byte Temp;
        int Year = c.get(Calendar.YEAR) % 100;
        int Month = c.get(Calendar.MONTH) + 1;
        int Day = c.get(Calendar.DAY_OF_MONTH);
        int Week = c.get(Calendar.DAY_OF_WEEK);
        switch (Week) {
            case Calendar.SUNDAY:
                Week = 7;
                break;
            default:
                Week -= 1;
                break;
        }
        int Hour = c.get(Calendar.HOUR_OF_DAY);
        int Minute = c.get(Calendar.MINUTE);
        int Second = c.get(Calendar.SECOND);
        bf.put(ParseUtil.INTToBCDByte(Second));
        bf.put(ParseUtil.INTToBCDByte(Minute));
        bf.put(ParseUtil.INTToBCDByte(Hour));
        bf.put(ParseUtil.INTToBCDByte(Day));
        Temp = (byte) ((ParseUtil.INTToBCDByte(Week) & 0x07) << 5);
        Temp |= ParseUtil.INTToBCDByte(Month) & 0x01F;
        bf.put(Temp);
        bf.put(ParseUtil.INTToBCDByte(Year));
        return bf.array();
    }

    /**
     * 解析GDW376_2009 数据类型5
     *
     * @param data
     * @return
     */
    public static double parseDataType5(byte... data) {
        double ret = 0;
        if (data.length < 2) return ret;
        if (data[0] == (byte) 0xEE || data[0] == (byte) 0xFF) return -1;
        ret += BCDToINT(data[0]);
        ret += BCDToINT((byte) (data[1] & 0x7F)) * 100;
        if (ret == 0) return 0;
        ret /= 10D;
        if ((data[1] & 0x80) > 0) {
            ret = -ret;
        }
        return ret;
    }

    /**
     * 解析GDW376_2009 数据类型3
     *
     * @param data
     * @return
     */
    public static long parseDataType3(byte[] data) {
        if (data.length < 4) return 0;
        int type = (data[3] & 0x10) == 0 ? 1 : -1;
        int rate = (data[3] & 0x40) == 0 ? 1 : 1000;
        data[3] = (byte) (data[3] & 0x0F);
        long value = ParseUtil.BCDToINT(data);
        return value * rate * type;
    }

    /**
     * 生成GDW376_2009 数据类型3
     *
     * @param data 倍率为1时的数值
     * @param rate 倍率 1=1 other=1000
     * @return
     */
    public static byte[] formatDataType3(long data, int rate) {
        byte[] ret = new byte[4];
        byte[] value = Swap(NUMToBCD(Math.abs(data) / (rate == 1 ? 1 : 1000)));
        System.arraycopy(value, 0, ret, 0, value.length > 4 ? 4 : value.length);
        ret[3] &= 0x0F;
        ret[3] |= data > 0 ? 0x00 : 0x10;
        ret[3] |= rate == 1 ? 0x00 : 0x40;
        return ret;
    }

    /**
     * 解析GDW376_2009 数据类型7
     *
     * @param data
     * @return
     */
    public static double parseDataType7(byte... data) {
        double ret = 0;
        if (data.length < 2) return ret;
        if (data[0] == (byte) 0xEE || data[0] == (byte) 0xFF) return -1;
        ret += BCDToINT(data[0]);
        ret += BCDToINT(data[1]) * 100;
        ret /= 10D;
        return ret;
    }

    /**
     * 解析GDW376_2009 数据类型9
     *
     * @param data
     * @return
     */
    public static double parseDataType9(byte... data) {
        double ret = 0;
        if (data.length < 3) return ret;
        if (data[0] == (byte) 0xEE || data[0] == (byte) 0xFF) return -1;
        ret += BCDToINT(data[0]);
        ret += BCDToINT(data[1]) * 100;
        ret += BCDToINT((byte) (data[2] & 0x7F)) * 10000;
        if (ret == 0) return 0;
        ret /= 10000D;
        if ((data[2] & 0x80) > 0) {
            ret = -ret;
        }
        return ret;
    }

    /**
     * 解析GDW376_2009 数据类型11
     *
     * @param data
     * @return
     */
    public static double parseDataType11(byte... data) {
        double ret = 0;
        if (data.length < 4) return ret;
        if (data[0] == (byte) 0xEE || data[0] == (byte) 0xFF) return -1;
        for (int i = 0; i < 4; i++) {
            ret += BCDToINT(data[i]) * Math.pow(100D, i);
        }
        ret /= 100D;
        return ret;
    }

    /**
     * 解析GDW376_2009 数据类型14
     *
     * @param data
     * @return
     */
    public static double parseDataType14(byte... data) {
        double ret = 0;
        if (data.length < 5) return ret;
        if (data[0] == (byte) 0xEE || data[0] == (byte) 0xFF) return -1;
        for (int i = 0; i < 5; i++) {
            ret += BCDToINT(data[i]) * Math.pow(100D, i);
        }
        ret /= 10000D;
        return ret;
    }

    /**
     * 解析GDW376_2009 数据类型15
     *
     * @param data
     * @return
     */
    public static long parseDataType15(byte... data) {
        long ret = 0;
        if (data.length < 5) return ret;
        if (data[0] == (byte) 0xEE || data[0] == (byte) 0xFF || data[2] == 0) return -1;
        //月份与日期不会为0
        if (data[3] == 0 || data[2] == 0) return -1;
        Calendar c = Calendar.getInstance();
        c.set(BCDToINT(data[4]) + 2000,//基础年为2000年
                BCDToINT(data[3]) - 1, //月
                BCDToINT(data[2]), //日
                BCDToINT(data[1]), //时
                BCDToINT(data[0]));//分
        return c.getTimeInMillis();
    }

    /**
     * 生成GDW376_2009 数据类型15
     *
     * @param time
     * @return
     */
    public static byte[] formatDataType15(long time) {
        byte[] ret = new byte[5];
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        ret[0] = INTToBCDByte(c.get(Calendar.MINUTE));
        ret[1] = INTToBCDByte(c.get(Calendar.HOUR_OF_DAY));
        ret[2] = INTToBCDByte(c.get(Calendar.DAY_OF_MONTH));
        ret[3] = INTToBCDByte(c.get(Calendar.MONTH) + 1);
        ret[4] = INTToBCDByte(c.get(Calendar.YEAR) % 100);
        return ret;
    }

    /**
     * 解析GDW376_2009 数据类型18
     *
     * @param data
     * @return
     */
    public static long parseDataType18(byte... data) {
        long ret = 0;
        if (data.length < 3) return ret;
        if (data[0] == (byte) 0xEE || data[0] == (byte) 0xFF || data[2] == 0) return -1;
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                BCDToINT(data[2]),
                BCDToINT(data[1]),
                BCDToINT(data[0]));
        return c.getTimeInMillis();
    }

    /**
     * 解析GDW376_2009 数据类型20
     *
     * @param data
     * @return
     */
    public static long parseDataType20(byte... data) {
        long ret = 0;
        if (data.length < 3) return ret;
        if (data[0] == (byte) 0xEE || data[0] == (byte) 0xFF || data[0] == 0) return -1;
        Calendar c = Calendar.getInstance();
        c.set(BCDToINT(data[2]) + 2000,//基础年为2000年
                BCDToINT(data[1]) - 1,
                BCDToINT(data[0]));
        return c.getTimeInMillis();
    }

    /**
     * 解析GDW376_2009 数据类型25
     *
     * @param data
     * @return
     */
    public static double parseDataType25(byte... data) {
        double ret = 0;
        if (data.length < 3) return ret;
        if (data[0] == (byte) 0xEE || data[0] == (byte) 0xFF) return -1;
        ret += BCDToINT(data[0]);
        ret += BCDToINT(data[1]) * 100;
        ret += BCDToINT((byte) (data[2] & 0x7F)) * 10000;
        if (ret == 0) return 0;
        ret /= 1000D;
        if ((data[2] & 0x80) > 0) {
            ret = -ret;
        }
        return ret;
    }


    /**
     * 解析日冻结数据时标
     *
     * @param data
     * @return
     */
    public static long parseTd_d(byte... data) {
        if (data.length < 3) return 0;
        if (data[0] == (byte) 0xEE || data[0] == (byte) 0xFF || data[0] == 0) return -1;
        Calendar c = Calendar.getInstance();
        c.set(BCDToINT(data[2]) + 2000,
                BCDToINT(data[1]) - 1,
                BCDToINT(data[0]));
        return c.getTimeInMillis();
    }

    /**
     * 生成日冻结数据时标
     *
     * @param time
     * @return
     */
    public static byte[] formatTd_d(long time) {
        byte[] ret = new byte[3];
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        ret[0] = INTToBCDByte(c.get(Calendar.DAY_OF_MONTH));
        ret[1] = INTToBCDByte(c.get(Calendar.MONTH) + 1);
        ret[2] = INTToBCDByte(c.get(Calendar.YEAR) % 100);
        return ret;
    }

    /**
     * 生成曲线类数据时标
     *
     * @param time  时间
     * @param den   密度
     * @param count 点数
     * @return
     */
    public static byte[] formatTd_c(long time, int den, int count) {
        ByteBuffer bf = ByteBuffer.allocate(6);
        bf.put(formatDataType15(time));
        bf.put((byte) den);
        bf.put((byte) count);
        return bf.array();
    }

    /**
     * 生成日月冻结类数据时标
     *
     * @param time
     * @return
     */
    public static byte[] formatTd_m(long time) {
        byte[] ret = new byte[2];
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        ret[0] = INTToBCDByte(c.get(Calendar.DAY_OF_MONTH));
        ret[1] = INTToBCDByte(c.get(Calendar.MONTH) + 1);
        return ret;
    }

    /**
     * 小时冻结类数据时标
     *
     * @param time 小时时间
     * @param den
     * @return
     */
    public static byte[] formatTd_h(int time, int den) {
        byte[] ret = new byte[2];
        ret[0] = INTToBCDByte(time);
        ret[1] = (byte) den;
        return ret;
    }

    /**
     * DLT645数据检查
     * <p>
     * 68 39 86 50 00 00 00 68  7
     * 91   //con  8
     * 08  //len  9
     * 33 32 34 33  dataFlag 13
     * 9C 56 33 33          17
     * 92             18
     * 16            19
     *
     * @param src
     * @param rev
     * @return
     */
    public static byte[] checkDLT645Receive(ByteBuffer src, byte[] rev) {
        if (src == null || rev == null || rev.length == 0) return null;
        for (int i = 0; i < rev.length; i++) {
            if (src.position() != 0) {
                if (rev[i] == 0x16) {
                    if (src.position() == 9) {
                        //若为长度位
                        src.put(rev[i]);
                    } else if (src.position() >= 11) {
                        src.put(rev[i]);
                        //获取长度
                        int len = (src.array()[9] & 0xFF) + 12;//2(报文头) + 6(地址) + 1(控制字) + 1(长度位) + 1(校验位) + 1(结束位)
                        //检测长度是否正确
                        if (src.position() == len) {
                            //正确结束报文
                            byte[] result = new byte[src.position()];
                            System.arraycopy(src.array(), 0, result, 0, result.length);
                            src.clear();
                            return result;
                        } else if (src.position() > len) {
                            //错误报文
                            return null;
                        }
                    } else if (src.position() < 7) {
                        //包含在地址中的0x16
                        src.put(rev[i]);
                    } else {
                        //报文不正确
                        src.clear();
                    }
                } else if (src.position() == 7) {
                    if (rev[i] == 0x68) {
                        //正确报文头结束
                        src.put(rev[i]);
                    } else {
                        //报文头长度错误
                        src.clear();
                    }
                } else {
                    src.put(rev[i]);
                }
            } else if (rev[i] == 0x68) {
                //接收到报文头开始
                src.put((byte) 0x68);
            }

        }
        return null;
    }

    /**
     * CJ188数据检查
     * <p>
     * 68 10 02 56 29 06 00 33 78     8
     * 81            //c   9
     * 16            //len 10
     * <p>
     * 1F 90         //dataFlag
     * 00            //SER
     * 00 01 00 00 2C     //当前累计流量
     * 00 00 00 00 2C     //结算日累计流量
     * 00 00 00 00 00 00 00     //实时时间
     * 00 00            //状态st
     * <p>
     * 49             //cs
     * 16
     *
     * @param src
     * @param rev
     * @return
     */
    public static byte[] check188Receive(ByteBuffer src, byte[] rev) {
        if (src == null || rev == null || rev.length == 0) return null;
        for (int i = 0; i < rev.length; i++) {
            if (src.position() != 0) {
                if (rev[i] == 0x16) {
                    if (src.position() == 10) {
                        //若为长度位
                        src.put(rev[i]);
                    } else if (src.position() >= 11) {
                        src.put(rev[i]);
                        //获取长度
                        int len = (src.array()[10] & 0xFF) + 13;//1(报文头)+1(表计类型)+7(地址)+1(控制字)+1(长度位)+1(校验位)+1(结束位)
                        //检测长度是否正确
                        if (src.position() == len) {
                            //正确结束报文
                            byte[] result = new byte[src.position()];
                            System.arraycopy(src.array(), 0, result, 0, result.length);
                            src.clear();
                            return result;
                        } else if (src.position() > len) {
                            //错误报文
                            return null;
                        }
                    } else if (src.position() < 8) {
                        //包含在地址中的0x16
                        src.put(rev[i]);
                    } else {
                        //报文不正确
                        src.clear();
                    }
                } else {
                    src.put(rev[i]);
                }
            } else if (rev[i] == 0x68) {
                //接收到报文头开始
                src.put((byte) 0x68);
            }
        }
        return null;
    }

    /**
     * 兴源数据检查
     * <p>
     * 53 53 53 53 42  //数据包头  4
     * 78 56 34 12     //4字节地址  8
     * 52              //读指令0x52, 9
     * 02 02 78 56                 13
     * 34 12 78 56 34 12 9E
     * C2 45
     *
     * @param src
     * @param rev
     * @return
     */
    public static byte[] checkXingyuanReceive(ByteBuffer src, byte[] rev) {
        if (src == null || rev == null || rev.length == 0)
            return null;
        for (int i = 0; i < rev.length; i++) {
            if (src.position() >= 4) {
                if (rev[i] == 0x42) { //53 53 53 53 42 数据包头结束42
                    src.put((byte) 0x42);
                } else if (rev[i] == 0x45) { //正确报文头结束
                    src.put((byte) 0x45);
                    //拼接报文帧 TODO 兴源返回帧数据不带长度，是否固定为23?
                    byte[] result = new byte[23];
                    System.arraycopy(src.array(), 0, result, 0, result.length);
                    src.clear();
                    return result;
                } else {
                    src.put(rev[i]);
                }
            } else if (rev[i] == 0x53) { //53 53 53 53 42 数据包头
                //接收到报文头开始
                src.put((byte) 0x53);
            }
        }
        return null;
    }

    /**
     * GDW376数据检查
     *
     * @param src
     * @param rev
     * @return
     */
    public static byte[] checkGDW376Receive(ByteBuffer src, byte[] rev) {
        if (src == null || rev == null || rev.length == 0) return null;
        int len = -1;
        //获取之前接收报文中的长度信息
        if (src.position() >= 5) {
            byte[] bytes = src.array();
            len = (bytes[1] >> 2) & 0x3F;
            len += (bytes[2] & 0x0F) << 6;
        }
        for (int i = 0; i < rev.length; i++) {
            if (src.position() == 5) {
                if (rev[i] == 0x68) {
                    //正确报文头结束
                    src.put(rev[i]);
                    //获取长度
                    byte[] bytes = src.array();
                    len = (bytes[1] >> 2) & 0x3F;
                    len += (bytes[2] & 0x0F) << 6;
                    int temp = (bytes[3] >> 2) & 0x3F;
                    temp += (bytes[4] & 0x0F) << 6;
                    if (len != temp) {
                        //报文不正确
                        src.clear();
                    }
                } else {
                    //报文头长度错误
                    src.clear();
                }
                //src.position() + 1 = 现读取长度; len + 8 = 报文实际长度
            } else if (len != -1 && (src.position() + 1) == len + 8) {
                //报文结束为止
                if (rev[i] == 0x16) {
                    //正确结束
                    src.put(rev[i]);
                    byte[] result = new byte[src.position()];
                    System.arraycopy(src.array(), 0, result, 0, result.length);
                    src.clear();
                    return result;
                } else {
                    //报文结束位错误
                    src.clear();
                }
            } else if (rev[i] == 0x68 && len == -1) {
                //新报文头
                src.position(0);
                src.put(rev[i]);
            } else {
                if (src.array()[0] != 0x68) src.position(0);//未接收到正确报文头,从头接收
                src.put(rev[i]);
            }
        }
        return null;
    }

    /**
     * 南网新规约检查
     *
     * @param src
     * @param rev
     * @return
     */
    public static byte[] checkCSG2014Receive(ByteBuffer src, byte[] rev) {
        if (src == null || rev == null || rev.length == 0) return null;
        int len = -1;
        //获取之前接收报文中的长度信息
        if (src.position() >= 5) {
            byte[] bytes = src.array();
            len = bytes[1] & 0xFF;
            len += ((bytes[2] & 0xFF) << 8);
        }
        for (int i = 0; i < rev.length; i++) {
            if (src.position() == 5) {
                if (rev[i] == 0x68) {
                    //正确报文头结束
                    src.put(rev[i]);
                    //获取长度
                    byte[] bytes = src.array();
                    len = bytes[1] & 0xFF;
                    len += ((bytes[2] & 0xFF) << 8);
                    int temp = bytes[3] & 0xFF;
                    temp += ((bytes[4] & 0xFF) << 8);
                    if (len != temp) {
                        //报文不正确
                        src.clear();
                    }
                } else {
                    //报文头长度错误
                    src.clear();
                }
                //src.position() + 1 = 现读取长度; len + 8 = 报文实际长度
            } else if (len != -1 && (src.position() + 1) == len + 8) {
                //报文结束为止
                if (rev[i] == 0x16) {
                    //正确结束
                    src.put(rev[i]);
                    byte[] result = new byte[src.position()];
                    System.arraycopy(src.array(), 0, result, 0, result.length);
                    src.clear();
                    return result;
                } else {
                    //报文结束位错误
                    src.clear();
                }
            } else if (rev[i] == 0x68 && len == -1) {
                //新报文头
                src.position(0);
                src.put(rev[i]);
            } else {
                if (src.array()[0] != 0x68) src.position(0);//未接收到正确报文头,从头接收
                src.put(rev[i]);
            }
        }
        return null;
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
     */
    public static byte[] intToBytes2(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

}

