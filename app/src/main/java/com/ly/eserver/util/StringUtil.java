package com.ly.eserver.util;

import android.widget.EditText;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 字符串工具
 *
 * @author Xuqn
 */
public class StringUtil {

    private static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    public static String getNowDateTimeString() {
        return simpleDateFormat(new Date());
    }

    /**
     * 用指定字符填充字符串至指定长度
     *
     * @param src   原字符串
     * @param f     填补
     * @param len   填补长度
     * @param right 填补方向
     * @return
     */
    public static String fillWith(String src, char f, int len, boolean right) {
        int srcLen = src.length();
        int fillLen = len - srcLen;
        if (len <= srcLen) return src;
        StringBuilder sb = new StringBuilder(src);
        for (int i = 0; i < fillLen; i++) {
            if (right) {
                sb.append(f);
            } else {
                sb.insert(0, f);
            }
        }
        return sb.toString();
    }

    /**
     * 将byte[]数组转换为16进制字符串
     *
     * @param bytes
     * @return
     */
    public static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    /**
     * 将byte[]数组转换为16进制字符串
     *
     * @param bytes
     * @param m
     * @param n
     * @return
     */
    public static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString().toUpperCase(Locale.CHINA);
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
        stringbuffer.append(" ");
    }

    /**
     * 获取TextView值
     *
     * @param v
     * @param defaultValue 默认值
     * @return
     */
    public static String getTextViewValue(TextView v, String defaultValue) {
        if (v == null)
            return defaultValue;
        String value = v.getText().toString();
        if (value.equals("")) {
            v.setText(String.valueOf(defaultValue));
            value = defaultValue;
        }
        return value;
    }

    /**
     * 检测输入框输入值是否为空
     *
     * @param edits
     * @return
     */
    public static boolean checkInputNotNull(EditText... edits) {
        for (EditText edit : edits) {
            if (edit.getText().toString().equals("")) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测文本框值是否为空
     *
     * @param edits
     * @return
     */
    public static boolean checkInputNotNull(TextView... edits) {
        for (TextView edit : edits) {
            if (edit.getText().toString().equals("")) {
                return false;
            }
        }
        return true;
    }

    /**
     * 清空输入内容
     *
     * @param edits
     * @return
     */
    public static void emptyInput(EditText... edits) {
        for (EditText edit : edits) {
            edit.setText("");
        }
    }

    /**
     * 清空输入内容
     *
     * @param edits
     * @return
     */
    public static void emptyInput(TextView... edits) {
        for (TextView edit : edits) {
            edit.setText("");
        }
    }


    /**
     * 检测输入项是否为IPv4
     *
     * @param value
     * @return
     */
    public static boolean checkInputIsIp(String value) {
        if (value == null) return false;
        String regx = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
        return value.matches(regx);
    }

    /**
     * 检测输入项是否为IPv4, 带IP段最大限制
     *
     * @param value
     * @param limit 各IP段限制 null:无限制
     * @return
     */
    public static boolean checkInputIsIp(String value, Byte[] limit) {
        if (checkInputIsIp(value)) {
            if (limit == null) return true;
            Integer[] ips = new Integer[4];
            String[] ip = value.split("\\.");
            for (int i = 0; i < ip.length && i < 4; i++) {
                ips[i] = Integer.valueOf(ip[i]);
            }
            for (int i = 0; i < limit.length && i < ips.length; i++) {
                if (ips[i] > (limit[i] & 0xFF)) return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 检测输入项是否为ASCII
     *
     * @param value
     * @return
     */
    public static boolean checkStringIsASCII(String value) {
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if (c >= 127 || c < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 过滤非指定字符
     *
     * @param str
     * @param regex 指定字符
     * @return
     */
    public static String stringFilter(String str, String regex) {
        if (str == null || str.equals("")) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            String c = String.valueOf(str.charAt(i));
            //判断是否为指定字符
            if (regex.indexOf(c) >= 0) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 标准日期转换
     *
     * @param date
     * @return
     */
    public static String simpleDateFormat(Date date) {
        return simpleDateFormat(date.getTime());
    }

    /**
     * 解析标准日期字符串
     *
     * @param date
     * @return
     */
    public static long simpleDateParse(String date) {
        if (date == null) return -1;
        try {
            return dateFormat.parse(date).getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA);

    /**
     * 标准日期转换
     *
     * @param date
     * @return
     */
    public static String simpleDateFormat(long date) {
        if (date <= 0) return "无";
        return dateFormat.format(date);
    }

    private static SimpleDateFormat hourFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时", Locale.CHINA);

    /**
     * 日期转换(至小时)
     *
     * @param date
     * @return
     */
    public static String simpleHourFormat(long date) {
        if (date <= 0) return "无";
        return hourFormat.format(date);
    }

    private static SimpleDateFormat minuteFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);

    /**
     * 日期转换(至分)
     *
     * @param date
     * @return
     */
    public static String simpleMinuteFormat(long date) {
        if (date <= 0) return "无";
        return minuteFormat.format(date);
    }

    private static SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);

    /**
     * 日期转换(至日)
     *
     * @param date
     * @return
     */
    public static String simpleDayFormat(long date) {
        if (date <= 0) return "无";
        return dayFormat.format(date);
    }

    private static SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);

    /**
     * 日期转换(至月)
     *
     * @param date
     * @return
     */
    public static String simpleMonthFormat(long date) {
        if (date <= 0) return "无";
        return monthFormat.format(date);
    }

    private static SimpleDateFormat dateWeekFormat = new SimpleDateFormat("yyyy年MM月dd日 E", Locale.CHINA);

    /**
     * 日期转换带周选项
     *
     * @param date
     * @return
     */
    public static String simpleDateWithWeekFormat(long date) {
        if (date <= 0) return "无";
        return dateWeekFormat.format(date);
    }

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);

    /**
     * 时间转换
     *
     * @param date
     * @return
     */
    public static String simpleTimeFormat(long date) {
        if (date <= 0) return "无";
        return timeFormat.format(date);
    }

    private static SimpleDateFormat dataTimeFormat = new SimpleDateFormat("dd日 HH:mm", Locale.CHINA);

    /**
     * 日期时间转换
     *
     * @param date
     * @return
     */
    public static String simpleDayTimeFormat(long date) {
        if (date <= 0) return "无";
        return dataTimeFormat.format(date);
    }

    /**
     * 当为无效数据(-1)时返回空
     *
     * @param value
     * @return
     */
    public static String valueOf(long value) {
        if (value == -1) {
            return "无";
        }
        return String.valueOf(value);
    }

    /**
     * 当为无效数据(-1)时返回空
     *
     * @param value
     * @return
     */
    public static String valueOf(double value) {
        if (value == -1) {
            return "无";
        }
        DecimalFormat df = new DecimalFormat("#0.0###");
        return df.format(value);
    }

    /**
     * HexString To byte[]
     *
     * @param str
     * @return
     */
    public static byte[] getBytes(String str) {
        if (!str.matches("^[0-9a-fA-F]*$")) {
            throw new NumberFormatException(str);
        }
        if (str.length() % 2 != 0) str += "0";
        byte[] hex = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i += 2) {
            String num = str.substring(i, i + 2);
            hex[i / 2] = (byte) (Integer.valueOf(num, 16) & 0xFF);
        }
        return hex;
    }

    /**
     * HexString To byte[]
     *
     * @param str
     * @param len
     * @return
     */
    public static byte[] getBytes(String str, int len) {
        byte[] bs = getBytes(str);
        byte[] ret = new byte[len];
        System.arraycopy(bs, 0, ret, 0, len < bs.length ? len : bs.length);
        return ret;
    }

    /**
     * byte[] To HexString
     *
     * @param byteArray
     * @return
     */
    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException(
                    "this byteArray must not be null or empty");
        return toHexString(byteArray, byteArray.length);
    }

    /**
     * byte[] To HexString
     *
     * @param byteArray
     * @param size
     * @return
     */
    public static String toHexString(byte[] byteArray, int size) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException(
                    "this byteArray must not be null or empty");
        final StringBuilder hexString = new StringBuilder(2 * size);
        for (int i = 0; i < size; i++) {
            if ((byteArray[i] & 0xff) < 0x10)//
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString().toUpperCase(Locale.CHINA);
    }

    /**
     * To Hex String
     *
     * @param value
     * @return
     */
    public static String toHexString(int value) {
        value = value & 0xFF;
        StringBuilder hexString = new StringBuilder();
        if (value < 0x10) {
            hexString.append("0");
        }
        hexString.append(Integer.toHexString(value));
        return hexString.toString().toUpperCase(Locale.CHINA);
    }

    /**
     * 转换为全角字符
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }


    /**
     * ByteBuffer 2 String
     *
     * @param buffer
     * @return
     */
    public static String byteBufferToString(ByteBuffer buffer) {
        CharBuffer charBuffer = null;
        try {
            Charset charset = Charset.forName("UTF-8");
            CharsetDecoder decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer);
            buffer.flip();
            return charBuffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

