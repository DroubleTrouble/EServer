package com.ly.eserver.protocol.dlt645;


import com.ly.eserver.protocol.exception.PacketLengthException;
import com.ly.eserver.util.ParseUtil;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * DLT645解析方法
 * @author Xuqn
 */
public class DLT645_Parse {

    /**
     * 检测数据是否正确
     * @param data
     * @return
     */
    public static boolean checkData(byte[] data) {
        int Len;
        int dataHeadIndex = 0;
        int checkLen;
        int cs;
        //校验数据长度
        if (data == null) {
            return false;
        }
        Len = data.length;
        //校验报尾
        if (data[Len - 1] != (byte) 0x16)
            return false;
        //校验报头
        if (data[0] == (byte) 0xFE) {
            if (Len < 16)
                return false;
            dataHeadIndex = 4;
        } else if (data[0] == (byte) 0x68) {
            if (Len < 12)
                return false;
        } else {
            return false;
        }
        //核对校验
        checkLen = Len - 2;
        cs = data[checkLen];
        byte sum = checkSum(data, dataHeadIndex, checkLen);
        return cs == sum;
    }

    /**
     * 获取数据标识
     *
     * @param dataResult
     * @param dataFlagLen
     * @return
     * @throws PacketLengthException
     */
    public static byte[] getDataFlag(byte[] dataResult, int dataFlagLen) throws PacketLengthException {
        if (dataResult.length < dataFlagLen) {
            throw new PacketLengthException(dataFlagLen);
        }
        byte[] dataFlag = new byte[dataFlagLen];
        System.arraycopy(dataResult, 0, dataFlag, 0, dataFlagLen);
        return ParseUtil.Swap(dataFlag);
    }

    /**
     * 获得数据域
     *
     * @param dataResult
     * @param dataFlagLen
     * @return
     * @throws PacketLengthException
     */
    public static byte[] getDataField(byte[] dataResult, int dataFlagLen) throws PacketLengthException {
        if (dataResult.length <= dataFlagLen) {
            throw new PacketLengthException(dataFlagLen);
        }
        byte[] dataField = new byte[dataResult.length - dataFlagLen];
        System.arraycopy(dataResult, dataFlagLen, dataField, 0, dataField.length);
        return ParseUtil.Swap(dataField);
    }

    /**
     * 移除报文头的FE数据
     *
     * @param data
     * @return
     */
    public static byte[] removeHeadFE(byte[] data) {
        int headIndex = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0x68) {
                headIndex = i;
                break;
            }
        }
        if (headIndex != 0) {
            byte[] newData = new byte[data.length - headIndex];
            System.arraycopy(data, headIndex, newData, 0, newData.length);
            return newData;
        } else {
            return data;
        }
    }

    /**
     * 计算校验位
     *
     * @param bytes
     * @param start
     * @param end
     * @return
     */
    public static byte checkSum(byte[] bytes, int start, int end) {
        int sum = 0;
        for (int i = start; i < end; i++) {
            sum += bytes[i] & 0xFF;
        }
        sum &= 0xFF;
        return (byte) sum;
    }

    /**
     * 解析BCD格式日期
     *
     * @param data
     * @param skip 1:秒 2:分...5:年 依次类推
     * @return
     */
    public static String parseDateData(byte[] data, int skip) {
        StringBuilder sb = new StringBuilder();
        if (skip >= 6)
            return "";
        int len = data.length;
        if (len < 6) {
            if (skip <= 6 - len) {
                //若跳过条目小于长度差则不跳过
                skip = 0;
            } else {
                //若大于则按长度差减少跳过步长
                skip -= (6 - len);
            }
        } else {
            len = 6;
        }
        for (int i = skip; i < len; i++) {
            sb.append(parseNumData(data[i]));
            switch (i % 6) {
                case 0:
                    sb.append("年");
                    break;
                case 1:
                    sb.append("月");
                    break;
                case 2:
                    sb.append("日 ");
                    break;
                case 3:
                    //时
                    sb.append(":");
                    break;
                case 4:
                    //分
                    sb.append(":");
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * 解析任意长度BCD格式日期
     *
     * @param data
     * @param start 起始位 0:年1:月... 依次类推
     * @return
     */
    public static String parseCustomDateData(byte[] data, int start) {
        if (start > 5)
            return "";
        StringBuilder sb = new StringBuilder();
        String[] timeUnit = new String[]{"年", "月", "日", ":", ":", ""};
        int len = data.length;
        if (len > 6) len = 6;
        for (int i = 0; i < len; i++) {
            sb.append(parseNumData(data[i]))
                    .append(timeUnit[i + start]);
        }
        if (sb.charAt(sb.length() - 1) == ':') sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * 解析BCD格式日期
     *
     * @param data
     * @return
     */
    public static String parseDateData(byte[] data) {
        return parseDateData(data, 0);
    }

    /**
     * 解析数字数据
     *
     * @param data
     * @return
     */
    public static String parseNumData(byte data) {
        int value = ParseUtil.BCDToINT(data);
        if (value < 0) {
            return "FF";
        } else {
            if (value < 10) {
                return "0" + String.valueOf(value);
            }
            return String.valueOf(value);
//        	return String.format("%02d", value);
        }
    }

    /**
     * 解析数字数据
     *
     * @param data
     * @return
     */
    public static String parseNumData(byte[] data,int j) {
        int i = j*4;
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(parseNumData(b));
        }
        String value = sb.toString().substring(i,i+4);
        if (value.matches("^[0-9]*$")) {
            //若为数字结果则进行去冗余操作
            return Long.valueOf(value).toString();
        }
        return value;
    }
    public static String parseNumData(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(parseNumData(b));
        }
        String value = sb.toString();
        if (value.matches("^[0-9]*$")) {
            //若为数字结果则进行去冗余操作
            return Long.valueOf(value).toString();
        }
        return value;
    }
    /**
     * 解析数字数据
     *
     * @param data
     * @param pointAt
     * @return
     */
//    public static String parseNumData(byte[] data, int pointAt) {
//        StringBuilder sb = new StringBuilder();
//        int len = data.length;
//        for (int i = 0; i < len; i++) {
//            sb.append(parseNumData(data[i]));
//        }
//        if (pointAt < sb.length() && pointAt != 0) sb.insert(pointAt, ".");
//        String value = sb.toString();
//        return getNumberFormat(value, pointAt);
//    }

    /**
     * 解析数字数据
     *
     * @param data         不包含数据标识
     * @param len          数据项长度
     * @param pointAt
     * @param haveNegative 包含负数标志
     * @return
     */
    public static String[] parseNumData(byte[] data, int len, int pointAt, boolean haveNegative) {
        List<String> ret;
        StringBuilder valueStr;
        int dataLen = data.length;
        ret = new ArrayList<>(dataLen / len + 1);
        boolean negativeFlag;
        //解析数据
        for (int a = 0; a <= dataLen - len; a += len) {
            if (data[a] == (byte) 0xAA) {
                a += 1;
                if (a + len > dataLen) {
                    break;
                }
            }
            valueStr = new StringBuilder();
            negativeFlag = false;
            if (haveNegative && ParseUtil.BCDToINT(data[a]) >= 0) {
                //判断负值标志,并且为正确的BCD码
                if ((data[a] & 0x80) > 0) {
                    negativeFlag = true;
                }
                data[a] = (byte) (data[a] & 0x7F);

            }
            for (int b = a; b < a + len; b++) {
                int value = ParseUtil.BCDToINT(data[b]);
                if (value < 0) {
                    valueStr.append("FF");
                } else {
                    if (value < 10) {
                        valueStr.append("0");
                    }
                    valueStr.append(String.valueOf(value));
                }
            }
            if (pointAt != 0) valueStr.insert(pointAt, ".");
            ret.add((negativeFlag ? "-" : "") + getNumberFormat(valueStr.toString(), pointAt));
        }
        String[] values = new String[ret.size()];
        ret.toArray(values);
        values = ParseUtil.Swap(values);
        return values;
    }

    /**
     * 解析数字数据(非负数)
     *
     * @param data    不包含数据标识
     * @param len     数据项长度
     * @param pointAt
     * @return
     */
    public static String[] parseNumData(byte[] data, int len, int pointAt) {
        return parseNumData(data, len, pointAt, false);
    }

    /**
     * 数字字符串去冗余
     *
     * @param value
     * @param pointAt
     * @return
     */
    public static String getNumberFormat(String value, int pointAt) {
        if (value.matches("^(\\d+\\.?)|(\\.?\\d+)|(\\d+\\.?\\d+)$")) {
            //若为数字结果则进行去冗余操作
            if (pointAt == 0 && value.charAt(0) != '.') {
                return Long.valueOf(value).toString();
            } else {
                DecimalFormat df = new DecimalFormat("#0.0###");
                BigDecimal bg = new BigDecimal(value);
                return df.format(bg);
            }
        } else {
            return value;
        }
    }

    /**
     * 获取密码权限级别
     *
     * @param pw
     * @return
     */
    public static int getPasswordLevel(String pw) {
        if (pw.matches("^[0-9]{8}$")) {
            long pl = Long.valueOf(pw, 16) & 0xFF000000;
            pl = (pl >> 24) & 0xFF;
            return (int) pl;
        } else {
            return 0;
        }
    }

    /**
     * 解析带单位数值
     *
     * @param data       数值
     * @param pointIndex 小数点位置
     * @param meterType  表计类型(水表/气表/热表)
     * @return
     */
    public static String parseValueWithUnit(byte[] data, int pointIndex, int meterType) {
        ByteBuffer bf = ByteBuffer.wrap(ParseUtil.Swap(data));
        byte[] value = new byte[data.length - 1];
        byte unit;
        //根据单位位置解析数据
        if ((data[0] & 0xFF) == 0x2C) {
            bf.get(value);
            unit = bf.get();
        } else {
            unit = bf.get();
            bf.get(value);
        }
        return MessageFormat.format("{0}{1}",
                ParseUtil.BCDToLONG(value, true) / (Math.pow(10, pointIndex)),
                parseUnit(unit, meterType));
    }

    /**
     * 解析单位
     *
     * @param unit      单位
     * @param meterType 表计类型(水表/气表/热表)
     * @return
     */
    public static String parseUnit(int unit, int meterType) {
        switch (unit) {
            case 0x02:
                return "Wh";
            case 0x05:
                return "kWh";
            case 0x08:
                return "MWh";
            case 0x0A:
                return "MWhx100";
            case 0x01:
                return "J";
            case 0x0B:
                return "kJ";
            case 0x0E:
                return "MJ";
            case 0x11:
                return "GJ";
            case 0x13:
                return "GJx100";
            case 0x14:
                return "W";
            case 0x17:
                return "kW";
            case 0x1A:
                return "MW";
            case 0x29:
                return "L";
            case 0x2C:
                return "m³";
            case 0x32:
                return "L/h";
            case 0x35:
                return "m³/h";
            default:
                return "";
        }
    }
}
