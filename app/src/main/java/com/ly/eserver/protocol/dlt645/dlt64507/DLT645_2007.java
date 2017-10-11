package com.ly.eserver.protocol.dlt645.dlt64507;


import android.util.Log;

import com.ly.eserver.protocol.MeterQueryInfo;
import com.ly.eserver.protocol.dlt645.DLT645_Parse;
import com.ly.eserver.protocol.exception.PacketLengthException;
import com.ly.eserver.protocol.exception.ParameterFormatException;
import com.ly.eserver.protocol.exception.ProtocolDataTypeException;
import com.ly.eserver.protocol.exception.ProtocolIdException;
import com.ly.eserver.util.ParseUtil;

import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * DLT645-2007模块
 *
 * @author Xuqn
 */
public class DLT645_2007 extends DLT645_Parse {

    /**
     * *重要*数据标识长度
     */
    public static final int DATA_FLAG_LEN = 4;

    /**
     * 电能量标识
     */
    public static final int ENERGY_FLAG = 0;
    /**
     * 最大需量标识
     */
    public static final int DEMAND_FLAG = 1;
    /**
     * 变量标识
     */
    public static final int VARIABLE_FLAG = 2;
    /**
     * 事件记录标识
     */
    public static final int EVENT_FLAG = 3;
    /**
     * 参变量标识
     */
    public static final int PARAMETER_FLAG = 4;
    /**
     * 高级请求标识
     */
    public static final int ADV_REQUEST_FLAG = 9;

    /**
     * 读数据返回
     */
    public static final byte READ_RETURN = 0x11;
    /**
     * 写数据返回
     */
    public static final byte WRITE_RETURN = 0x14;

    //请求数据模版
    public static final byte[] READ_REQUEST = new byte[]{(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE,
            (byte) 0x68, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x68, (byte) 0x11, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x16};

    //写数据模版
    public static final byte[] WRITE_REQUEST_HEAD = new byte[]{(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE,
            (byte) 0x68, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x68};

    private static final int REQUEST_ADDR_START = 5;
    private static final int REQUEST_CON_START = 14;
    private static final int REQUEST_LEN = 20;

    /**
     * 获取数据信息
     *
     * @param data
     * @return
     * @throws PacketLengthException
     */
    public static DLT645_2007_Info getDataInfo(byte[] data) throws PacketLengthException {
        DLT645_2007_Info info = new DLT645_2007_Info();
        byte[] parseData = removeHeadFE(data);//获取用于解析数据
        if (!checkData(parseData)) return info;
        //获取表号
        byte[] addr = new byte[6];
        for (int i = 0; i < 6; i++) {
            addr[5 - i] = parseData[i + 1];
        }
        info.setAmmAddrByte(addr);
        //解析控制码
        byte c = parseData[8];
        info.isReceived = (c & 0x80) > 0;
        info.isReceiveCur = (c & 0x40) <= 0;
        info.haveFollow = (c & 0x20) > 0;
        info.conCode = (byte) (c & 0x1F);
        //获取数据
        info.dataLen = parseData[9] & 0xFF;
        info.resultData = new byte[info.dataLen];
        for (int i = 10, a = 0; a < info.dataLen; i++, a++) {
            int num = parseData[i] & 0xFF;
            num -= 0x33;
            info.resultData[a] = (byte) (num);
        }
        if (!info.isReceiveCur) {
            return info;
        }
        switch (info.conCode) {
            case 0x11:
                //读数据
                //解析数据域
                byte[] dataType = getDataFlag(info.resultData, DATA_FLAG_LEN);
                info.id = dataType[0];
                switch (info.id) {
                    case ENERGY_FLAG:
                        //电能量
                        info.subId = parseElecEnergySubId(dataType[1]);
                        break;
                    case DEMAND_FLAG:
                        //最大需量
                        info.subId = parseDemandSubId(dataType[1]);
                        break;
                    case VARIABLE_FLAG:
                        //变量
                        info.subId = dataType[1];
                        break;
                    case EVENT_FLAG:
                        //事件
                        if (dataType[1] == 0x30) {
                            info.subId = dataType[2] + 19;
                        } else {
                            info.subId = dataType[1];
                        }
                        break;
                    case PARAMETER_FLAG:
                        //参变量
                        if (dataType[1] == 0) {
                            info.subId = dataType[2];
                        } else {
                            info.subId = dataType[1] + 14;
                        }
                        break;
                }
                break;
            case 0x13:
                //读通信地址
                info.id = 9;
                info.subId = 0;
                break;
        }
        return info;
    }

    /**
     * 解析电能量小类
     *
     * @param value
     * @return
     */
    public static int parseElecEnergySubId(int value) {
        if (value < 0x0A) {
            return value;
        } else if (value >= 0x80 && value <= 0x86) {
            return value - 0x75;
        } else {
            int tempValue = parseElecEnergySubId(value - 0x14);
            if (tempValue != -1) return tempValue;
            tempValue = parseElecEnergySubId(value - 0x28);
            if (tempValue != -1) return tempValue;
            tempValue = parseElecEnergySubId(value - 0x3C);
            if (tempValue != -1) return tempValue;
        }
        return -1;
    }

    /**
     * 解析最大需量小类
     *
     * @param value
     * @return
     */
    public static int parseDemandSubId(int value) {
        return parseElecEnergySubId(value);
    }


    /**
     * 生成数据读取请求报文
     *
     * @param queryInfo
     * @param phase     相位
     * @param rate      费率
     * @param time      时间
     * @return
     * @throws ProtocolIdException
     */
    public static byte[] getReadRequest(MeterQueryInfo queryInfo, String addr, int phase, int rate, int time) throws ProtocolIdException {
        switch (queryInfo.getQueryId()) {
            case ENERGY_FLAG:
                //电能量
                return getElecEnergyRequest(queryInfo, addr, phase, rate, time);
            case DEMAND_FLAG:
                //最大需量
                return getDemandRequest(queryInfo, addr, phase, rate, time);
            case VARIABLE_FLAG:
                //变量
                return getElecVarRequest(queryInfo, addr, phase);
            case EVENT_FLAG:
                //事件
                return getEventRequest(queryInfo, addr, phase, time);
            case PARAMETER_FLAG:
                //参变量
                return getParaRequest(queryInfo, addr);
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_ID(), queryInfo.getQueryId());
        }
    }

    /**
     * 生成高级请求信息
     *
     * @param conCode 控制码
     * @param addr
     * @param pLevel  权限等级
     * @param pw      操作密码
     * @param id      操作员代码
     * @param flag    数据标识
     * @param data    请求数据
     * @return
     */
    public static byte[] getAdvRequest(byte conCode, String addr, int pLevel, String pw, String id, byte[] flag, byte[] data) {
        int Len;
        int dataLen;//数据区长度
        ByteBuffer bf;
        int pwStart, idStrat;
        byte[] value;
        byte[] ret;
        if (flag == null || data == null) return new byte[0];
        if (pLevel == -1) {
            //无需认证
            dataLen = flag.length + data.length;
            Len = WRITE_REQUEST_HEAD.length + dataLen + 4;//控制码+长度+效验+帧尾
        } else {
            //需要认证
            dataLen = flag.length + data.length + 8;
            Len = WRITE_REQUEST_HEAD.length + dataLen + 4;//控制码+长度+密码+权限+认证码+效验+帧尾
        }
        bf = ByteBuffer.allocate(Len);
        ret = bf.array();
        bf.put(WRITE_REQUEST_HEAD);
        bf.put(conCode);//控制码
        bf.put((byte) dataLen);//长度
        bf.put(flag);//标识
        if (pLevel != -1) {
            bf.put((byte) (0x33 + pLevel));//权限等级
            pwStart = bf.position();
            bf.put(new byte[]{0x33, 0x33, 0x33});//密码
            idStrat = bf.position();
            bf.put(new byte[]{0x33, 0x33, 0x33, 0x33});//操作员代码
            //插入密码
            if (pw != null && pw.matches("^[0-9]{6}$")) {
                value = ParseUtil.Swap(ParseUtil.STRToBCD(pw));
                for (int i = 0; i < 3; i++) {
                    value[i] += 0x33;
                }
                System.arraycopy(value, 0, ret, pwStart, 3);
            }
            //插入操作员代码
            if (id != null && id.matches("^[0-9]{8}$")) {
                value = ParseUtil.Swap(ParseUtil.STRToBCD(id));
                for (int i = 0; i < 4; i++) {
                    value[i] += 0x33;
                }
                System.arraycopy(value, 0, ret, idStrat, 4);
            }
        }
        bf.put(data);//数据
        bf.put(new byte[]{0x00, 0x16});//校验位+帧尾
        //插入地址
        if (addr != null && addr.matches("[0-9]{12}")) {
            //数字地址才进行解析
            value = ParseUtil.Swap(ParseUtil.STRToBCD(addr));
            System.arraycopy(value, 0, ret, REQUEST_ADDR_START, 6);
        }
        //重新计算校验位
        byte checkSum = checkSum(ret, 4, Len - 2);
        ret[Len - 2] = checkSum;
        return ret;
    }

    /**
     * 获取高级应用请求报文
     *
     * @param queryInfo
     * @param addr
     * @param data      数据
     * @param pl        密码权限
     * @param pw        密码
     * @param id        操作者代码
     * @return
     * @throws ParameterFormatException
     * @throws ProtocolIdException
     */
    public static byte[] getAdvRequest(MeterQueryInfo queryInfo, String addr, String data, int pl, String pw, String id)
            throws ParameterFormatException, ProtocolIdException {
        if (queryInfo.getQueryId() != 9) return new byte[0];
        byte[] value = new byte[0];
        byte conCode = 0;
        switch (queryInfo.getQuerySubId()) {
            case 1:
                //广播校时
                addr = "999999999999";
                conCode = 0x08;
                //解析时间设置
                value = new byte[6];
                Calendar timeSet = Calendar.getInstance();
                if (data.matches("^[0-9]*$")) {
                    timeSet.setTimeInMillis(Long.valueOf(data));
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA);
                    try {
                        timeSet.setTime(sdf.parse(data));
                    } catch (ParseException e) {
                        throw new ParameterFormatException(data, "yyyy年MM月dd日 HH:mm:ss");
                    }
                }
                value[0] = ParseUtil.INTToBCDByte(timeSet.get(Calendar.YEAR) % 100);//年
                value[1] = ParseUtil.INTToBCDByte((timeSet.get(Calendar.MONTH) + 1));//月
                value[2] = ParseUtil.INTToBCDByte(timeSet.get(Calendar.DAY_OF_MONTH));//日
                value[3] = ParseUtil.INTToBCDByte(timeSet.get(Calendar.HOUR_OF_DAY));//时
                value[4] = ParseUtil.INTToBCDByte(timeSet.get(Calendar.MINUTE));//分
                value[5] = ParseUtil.INTToBCDByte(timeSet.get(Calendar.SECOND));//秒
                break;
            case 2:
                //读/写通信地址
                if (data == null || data.equals("")) {
                    //读通信地址
                    conCode = 0x13;
                } else {
                    //写通信地址
                    conCode = 0x15;
                    //解析通信地址设置
                    value = ParseUtil.STRToBCD(data);
                }
                break;
            case 3:
                //更改通信速率
                conCode = 0x17;
                value = new byte[]{0x04};//默认波特率1200
                if (data.matches("^[0-9]*$")) {
                    switch (Integer.valueOf(data)) {
                        case 600:
                            value[0] = 0x02;
                            break;
                        case 2400:
                            value[0] = 0x08;
                            break;
                        case 4800:
                            value[0] = 0x10;
                            break;
                        case 9600:
                            value[0] = 0x20;
                            break;
                        case 19200:
                            value[0] = 0x40;
                            break;
                    }
                }
                break;
            case 4:
                //修改密码
                conCode = 0x18;
                //解析密码设置
                if (data.matches("^[0-9]*$")) {
                    value = ParseUtil.STRToBCD(data);
                } else {
                    //必须为数字字符串
                    throw new ParameterFormatException(data, "string(number)");
                }
                break;
            case 5:
                //最大需量清零
                conCode = 0x19;
                break;
            case 6:
                //电表清零
                conCode = 0x1A;
                break;
            case 7:
                //事件清零
                conCode = 0x1B;
                //事件清零需要添加
                value = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
                break;
            case 8:
            case 9:
                //拉合闸
                conCode = 0x1C;
                value = new byte[8];
                Calendar timeValue = Calendar.getInstance();
                if (data.matches("^[0-9]*$")) {
                    timeValue.setTimeInMillis(Long.valueOf(data));
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA);
                    try {
                        timeValue.setTime(sdf.parse(data));
                    } catch (ParseException e) {
                        throw new ParameterFormatException(data, "yyyy年MM月dd日 HH:mm:ss");
                    }
                }
                value[0] = ParseUtil.INTToBCDByte(timeValue.get(Calendar.YEAR) % 100);//年
                value[1] = ParseUtil.INTToBCDByte((timeValue.get(Calendar.MONTH) + 1));//月
                value[2] = ParseUtil.INTToBCDByte(timeValue.get(Calendar.DAY_OF_MONTH));//日
                value[3] = ParseUtil.INTToBCDByte(timeValue.get(Calendar.HOUR_OF_DAY));//时
                value[4] = ParseUtil.INTToBCDByte(timeValue.get(Calendar.MINUTE));//分
                value[5] = ParseUtil.INTToBCDByte(timeValue.get(Calendar.SECOND));//秒
                value[6] = 0x00;
                switch (queryInfo.getQuerySubId()) {
                    case 8:
                        //拉闸
                        value[7] = 0x1A;
                        break;
                    case 9:
                        //合闸
                        value[7] = 0x1B;
                        break;
                }
//				pl = 2;//默认采用02级认证
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        for (int i = 0; i < value.length; i++) {
            value[i] += 0x33;
        }
        return getAdvRequest(conCode, addr, pl, pw, id, new byte[0], ParseUtil.Swap(value));
    }

    /**
     * 获取电能量请求报文
     *
     * @param queryInfo
     * @param addr
     * @param phase
     * @param rate
     * @param time
     * @return
     */
    public static byte[] getElecEnergyRequest(MeterQueryInfo queryInfo, String addr, int phase, int rate, int time) {
        byte[] dataFlag = new byte[]{0x33, 0x33, 0x33, 0x33};
        //生成控制命令
        //类型
        if (queryInfo.getQuerySubId() <= 10) {
            dataFlag[1] += queryInfo.getQuerySubId();
        } else {
            dataFlag[1] += (byte) (0x80 + (queryInfo.getQuerySubId() - 10));
        }
        //相位
        byte p = 0;
        switch (phase) {
            case 1:
                p = 0x14;
                //A相
                break;
            case 2:
                p = 0x28;
                //B相
                break;
            case 3:
                p = 0x3C;
                //C相
                break;
            default:
                //费率
                dataFlag[2] += rate;
                break;
        }
        dataFlag[1] += p;
        //时间
        dataFlag[3] += time;
        dataFlag = ParseUtil.Swap(dataFlag);
        return getReadRequest(dataFlag, addr);
    }

    /**
     * 获取变量请求报文
     *
     * @param queryInfo
     * @param addr
     * @param phase
     * @return
     */
    public static byte[] getElecVarRequest(MeterQueryInfo queryInfo, String addr, int phase) {
        byte[] dataFlag = new byte[]{0x35, 0x33, 0x33, 0x33};
        //生成控制命令
        //类型
        dataFlag[1] += queryInfo.getQuerySubId();
        //相位
        byte p;
        switch (queryInfo.getQuerySubId()) {
            case 1:
            case 2:
                if (phase == 0) {
                    p = (byte) 0xFF;
                } else {
                    p = (byte) phase;
                }
                break;
            default:
                p = (byte) phase;
        }
        dataFlag[2] += p;
        dataFlag = ParseUtil.Swap(dataFlag);
        return getReadRequest(dataFlag, addr);
    }

    /**
     * 获取最大需量请求报文
     *
     * @param queryInfo
     * @param addr
     * @param phase
     * @param rate
     * @param time
     * @return
     */
    public static byte[] getDemandRequest(MeterQueryInfo queryInfo, String addr, int phase, int rate, int time) {
        byte[] dataFlag = new byte[]{0x34, 0x33, 0x33, 0x33};
        //生成控制命令
        //类型
        if (queryInfo.getQuerySubId() <= 10) {
            dataFlag[1] += queryInfo.getQuerySubId();
        } else {
            dataFlag[1] += (byte) (0x80 + (queryInfo.getQuerySubId() - 10));
        }
        //相位
        byte p = 0;
        switch (phase) {
            case 1:
                p = 0x14;
                //A相
                break;
            case 2:
                p = 0x28;
                //B相
                break;
            case 3:
                p = 0x3C;
                //C相
                break;
            default:
                //费率
                dataFlag[2] += rate;
                break;
        }
        dataFlag[2] += p;
        //时间
        dataFlag[3] += time;
        dataFlag = ParseUtil.Swap(dataFlag);
        return getReadRequest(dataFlag, addr);
    }

    /**
     * 获取事件请求报文
     *
     * @param queryInfo
     * @param addr
     * @param phase
     * @param time
     * @return
     */
    public static byte[] getEventRequest(MeterQueryInfo queryInfo, String addr, int phase, int time) {
        byte[] dataFlag = new byte[]{0x36, 0x33, 0x33, 0x33};
        if (queryInfo.getQuerySubId() <= 18) {
            dataFlag[1] += queryInfo.getQuerySubId();
            dataFlag[2] += phase;
        } else {
            dataFlag[1] += 0x30;
            dataFlag[2] += (queryInfo.getQuerySubId() - 19);

        }
        dataFlag[3] += time;
        dataFlag = ParseUtil.Swap(dataFlag);
        return getReadRequest(dataFlag, addr);
    }

    /**
     * 生成参变量请求信息
     *
     * @param queryInfo
     * @param addr
     * @return
     */
    public static byte[] getParaRequest(MeterQueryInfo queryInfo, String addr) {
        byte[] dataFlag = new byte[]{0x37, 0x33, 0x33, 0x33};
        if (queryInfo.getQuerySubId() >= 15) {
            dataFlag[1] += (queryInfo.getQuerySubId() - 14);
        } else {
            dataFlag[2] += queryInfo.getQuerySubId();
        }
        dataFlag[3] += queryInfo.getParaIndex() + 1;//序号由1开始
        dataFlag = ParseUtil.Swap(dataFlag);
        return getReadRequest(dataFlag, addr);
    }

    /**
     * 生成读取请求信息
     *
     * @param dataFlag 数据标识
     * @param addr
     * @return
     */
    public static byte[] getReadRequest(byte[] dataFlag, String addr) {
        byte[] request = READ_REQUEST.clone();
        //插入请求
        System.arraycopy(dataFlag, 0, request, REQUEST_CON_START, 4);
        //插入地址
        if (addr.matches("[0-9]{12}")) {
            //数字地址才进行解析
            byte[] addrB = ParseUtil.Swap(ParseUtil.STRToBCD(addr));
            System.arraycopy(addrB, 0, request, REQUEST_ADDR_START, 6);
        }
        //重新计算校验位
        byte checkSum = checkSum(request, 4, REQUEST_LEN - 2);
        request[REQUEST_LEN - 2] = checkSum;
        return request;
    }

    /**
     * 根据抄读信息解析报文(不用来解析参变量数据)
     *
     * @param queryInfo
     * @param data      数据域中数据
     * @return
     * @throws PacketLengthException
     * @throws ProtocolIdException
     * @throws ProtocolDataTypeException
     */
    public static List<String> parse(MeterQueryInfo queryInfo, byte[] data)
            throws PacketLengthException, ProtocolIdException, ProtocolDataTypeException {
        List<String> ret = new ArrayList<String>();
        if (data.length <= DATA_FLAG_LEN) {
            ret.add("数据报文异常");
            return ret;
        }
        //根据抄读类型解析报文
        switch (queryInfo.getQueryId()) {
            case ENERGY_FLAG:
                ret = parseElecEnergy(queryInfo, data);
                break;
            case DEMAND_FLAG:
                ret = parseDemand(queryInfo, data);
                break;
            case VARIABLE_FLAG:
                ret = parseElecVar(queryInfo, data);
                break;
            case EVENT_FLAG:
                ret = parseEvent(queryInfo, data);
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_ID(), queryInfo.getQueryId());
        }
        return ret;
    }

    /**
     * 解析事件数据
     *
     * @param queryInfo
     * @param data
     * @return
     * @throws PacketLengthException
     * @throws ProtocolIdException
     */
    public static List<String> parseEvent(MeterQueryInfo queryInfo, byte[] data) throws PacketLengthException, ProtocolIdException {
        List<String> ret = new ArrayList<String>();
        int times = data[0];
        byte[] parseData = getDataField(data, DATA_FLAG_LEN);
        switch (queryInfo.getQuerySubId()) {
            case 1:
            case 2:
            case 3:
            case 4:
                //失压,欠压,过压,断相
                ret = parseEventType1(parseData, times);
                break;
            case 13:
                //断流
                ret = parseEventType2(parseData, times);
                break;
            case 16:
                //电压合格率
                ret = parseEventType3(parseData);
                break;
            case 32:
            case 33:
                //开表盖,开端钮盒
                ret = parseEventType4(parseData, times);
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        return ret;
    }

    /**
     * 解析失压,欠压,过压,断相数据
     *
     * @param data
     * @param times 次数
     * @return
     * @throws PacketLengthException
     */
    public static List<String> parseEventType1(byte[] data, int times) throws PacketLengthException {
        List<String> resultList = new ArrayList<String>();
        data = ParseUtil.Swap(data);
        ByteBuffer bf = ByteBuffer.wrap(data);
        byte[] value2;
        byte[] value3;
        byte[] value4;
        byte[] value6;
        if (times == 0) {
            //总量数据
            if (data.length < 18) throw new PacketLengthException(18);
            value3 = new byte[3];
            bf.get(value3);
            resultList.add("A相次数:" + parseNumData(ParseUtil.Swap(value3), 0) + "次");
            bf.get(value3);
            resultList.add("A相累计时间:" + parseNumData(ParseUtil.Swap(value3), 0) + "分");
            bf.get(value3);
            resultList.add("B相次数:" + parseNumData(ParseUtil.Swap(value3), 0) + "次");
            bf.get(value3);
            resultList.add("B相累计时间:" + parseNumData(ParseUtil.Swap(value3), 0) + "分");
            bf.get(value3);
            resultList.add("C相次数:" + parseNumData(ParseUtil.Swap(value3), 0) + "次");
            bf.get(value3);
            resultList.add("C相总累计时间:" + parseNumData(ParseUtil.Swap(value3), 0) + "分");
        } else {
            //相量数据
            if (data.length < 131) throw new PacketLengthException(131);
            value2 = new byte[2];
            value3 = new byte[3];
            value4 = new byte[4];
            value6 = new byte[6];
            bf.get(value6);
            resultList.add("发生时间:" + parseDateData(ParseUtil.Swap(value6)));
            bf.get(value6);
            resultList.add("结束时间:" + parseDateData(ParseUtil.Swap(value6)));
            bf.get(value4);
            resultList.add("正向有功总电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kWh");
            bf.get(value4);
            resultList.add("反向有功总电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kWh");
            bf.get(value4);
            resultList.add("组合无功1总电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kvarh");
            bf.get(value4);
            resultList.add("组合无功2总电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kvarh");
            String[] phase = new String[]{"A相", "B相", "C相"};
            for (int i = 0; i < 3; i++) {
                bf.get(value4);
                resultList.add(phase[i] + "正向有功电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kWh");
                bf.get(value4);
                resultList.add(phase[i] + "反向有功电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kWh");
                bf.get(value4);
                resultList.add(phase[i] + "组合无功1电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kvarh");
                bf.get(value4);
                resultList.add(phase[i] + "组合无功2电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kvarh");
                bf.get(value2);
                resultList.add(phase[i] + "电压:" + parseNumData(ParseUtil.Swap(value2), 3) + "V");
                bf.get(value3);
                resultList.add(phase[i] + "电流:" + parseNumData(ParseUtil.Swap(value3), 3) + "A");
                bf.get(value3);
                resultList.add(phase[i] + "有功功率:" + parseNumData(ParseUtil.Swap(value3), 3) + "kW");
                bf.get(value3);
                resultList.add(phase[i] + "无功功率:" + parseNumData(ParseUtil.Swap(value3), 3) + "kvar");
                bf.get(value2);
                resultList.add(phase[i] + "功率因数:" + parseNumData(ParseUtil.Swap(value2), 1));
            }
            bf.get(value4);
            resultList.add("总安时数:" + parseNumData(ParseUtil.Swap(value4), 6) + "AH");
            bf.get(value4);
            resultList.add("A相安时数:" + parseNumData(ParseUtil.Swap(value4), 6) + "AH");
            bf.get(value4);
            resultList.add("B相安时数:" + parseNumData(ParseUtil.Swap(value4), 6) + "AH");
            bf.get(value4);
            resultList.add("C相安时数:" + parseNumData(ParseUtil.Swap(value4), 6) + "AH");
        }
        return resultList;
    }

    /**
     * 解析断流数据
     *
     * @param data
     * @param times 次数
     * @return
     * @throws PacketLengthException
     */
    public static List<String> parseEventType2(byte[] data, int times) throws PacketLengthException {
        List<String> resultList = new ArrayList<String>();
        data = ParseUtil.Swap(data);
        ByteBuffer bf = ByteBuffer.wrap(data);
        byte[] value2;
        byte[] value3;
        byte[] value4;
        byte[] value6;
        if (times == 0) {
            //总量数据
            if (data.length < 18) throw new PacketLengthException(18);
            value3 = new byte[3];
            bf.get(value3);
            resultList.add("A相次数:" + parseNumData(ParseUtil.Swap(value3), 0) + "次");
            bf.get(value3);
            resultList.add("A相累计时间:" + parseNumData(ParseUtil.Swap(value3), 0) + "分");
            bf.get(value3);
            resultList.add("B相次数:" + parseNumData(ParseUtil.Swap(value3), 0) + "次");
            bf.get(value3);
            resultList.add("B相累计时间:" + parseNumData(ParseUtil.Swap(value3), 0) + "分");
            bf.get(value3);
            resultList.add("C相次数:" + parseNumData(ParseUtil.Swap(value3), 0) + "次");
            bf.get(value3);
            resultList.add("C相总累计时间:" + parseNumData(ParseUtil.Swap(value3), 0) + "分");
        } else {
            //相量数据
            if (data.length < 115) throw new PacketLengthException(115);
            value2 = new byte[2];
            value3 = new byte[3];
            value4 = new byte[4];
            value6 = new byte[6];
            bf.get(value6);
            resultList.add("发生时间:" + parseDateData(ParseUtil.Swap(value6)));
            bf.get(value6);
            resultList.add("结束时间:" + parseDateData(ParseUtil.Swap(value6)));
            bf.get(value4);
            resultList.add("正向有功总电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kWh");
            bf.get(value4);
            resultList.add("反向有功总电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kWh");
            bf.get(value4);
            resultList.add("组合无功1总电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kvarh");
            bf.get(value4);
            resultList.add("组合无功2总电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kvarh");
            String[] phase = new String[]{"A相", "B相", "C相"};
            for (int i = 0; i < 3; i++) {
                bf.get(value4);
                resultList.add(phase[i] + "正向有功电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kWh");
                bf.get(value4);
                resultList.add(phase[i] + "反向有功电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kWh");
                bf.get(value4);
                resultList.add(phase[i] + "组合无功1电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kvarh");
                bf.get(value4);
                resultList.add(phase[i] + "组合无功2电能增量:" + parseNumData(ParseUtil.Swap(value4), 6) + "kvarh");
                bf.get(value2);
                resultList.add(phase[i] + "电压:" + parseNumData(ParseUtil.Swap(value2), 3) + "V");
                bf.get(value3);
                resultList.add(phase[i] + "电流:" + parseNumData(ParseUtil.Swap(value3), 3) + "A");
                bf.get(value3);
                resultList.add(phase[i] + "有功功率:" + parseNumData(ParseUtil.Swap(value3), 3) + "kW");
                bf.get(value3);
                resultList.add(phase[i] + "无功功率:" + parseNumData(ParseUtil.Swap(value3), 3) + "kvar");
                bf.get(value2);
                resultList.add(phase[i] + "功率因数:" + parseNumData(ParseUtil.Swap(value2), 1));
            }
        }
        return resultList;
    }

    /**
     * 解析电压合格率数据
     *
     * @param data
     * @return
     * @throws PacketLengthException
     */
    public static List<String> parseEventType3(byte[] data) throws PacketLengthException {
        List<String> resultList = new ArrayList<String>();
        data = ParseUtil.Swap(data);
        ByteBuffer bf = ByteBuffer.wrap(data);
        if (data.length < 27) throw new PacketLengthException(27);
        byte[] value2 = new byte[2];
        byte[] value3 = new byte[3];
        byte[] value4 = new byte[4];
        bf.get(value3);
        resultList.add("电压监测时间:" + parseNumData(ParseUtil.Swap(value3), 0) + "分");
        bf.get(value3);
        resultList.add("电压合格率:" + parseNumData(ParseUtil.Swap(value3), 4) + "%");
        bf.get(value3);
        resultList.add("电压超限率:" + parseNumData(ParseUtil.Swap(value3), 4) + "%");
        bf.get(value3);
        resultList.add("电压超上限时间:" + parseNumData(ParseUtil.Swap(value3), 0) + "分");
        bf.get(value3);
        resultList.add("电压超下限时间:" + parseNumData(ParseUtil.Swap(value3), 0) + "分");
        bf.get(value2);
        resultList.add("最高电压:" + parseNumData(ParseUtil.Swap(value2), 3) + "V");
        bf.get(value4);
        resultList.add("出现时间:" + parseCustomDateData(ParseUtil.Swap(value4), 1));
        bf.get(value2);
        resultList.add("最低电压:" + parseNumData(ParseUtil.Swap(value2), 3) + "V");
        bf.get(value4);
        resultList.add("出现时间:" + parseCustomDateData(ParseUtil.Swap(value4), 1));
        return resultList;
    }

    /**
     * 解析开表盖,开端钮盒数据
     *
     * @param data
     * @param times 次数
     * @return
     * @throws PacketLengthException
     */
    public static List<String> parseEventType4(byte[] data, int times) throws PacketLengthException {
        List<String> resultList = new ArrayList<String>();
        ByteBuffer bf;
        byte[] value3;
        byte[] value4;
        byte[] value6;
        if (times == 0) {
            //总量
            bf = ByteBuffer.wrap(data);
            if (data.length < 3) throw new PacketLengthException(3);
            value3 = new byte[3];
            bf.get(value3);
            resultList.add("总数:" + parseNumData(value3, 0) + "次");
        } else {
            //分量
            bf = ByteBuffer.wrap(ParseUtil.Swap(data));
            if (data.length < 60) throw new PacketLengthException(60);
            value4 = new byte[4];
            value6 = new byte[6];
            bf.get(value6);
            resultList.add("发生时间:" + parseDateData(ParseUtil.Swap(value6)));
            bf.get(value6);
            resultList.add("结束时间:" + parseDateData(ParseUtil.Swap(value6)));
            String[] time = new String[]{"前-", "后-"};
            for (int i = 0; i < 2; i++) {
                bf.get(value4);
                resultList.add(time[i] + "正向有功总电能:" + parseNumData(ParseUtil.Swap(value4), 6) + "kWh");
                bf.get(value4);
                resultList.add(time[i] + "反向有功总电能:" + parseNumData(ParseUtil.Swap(value4), 6) + "kWh");
                bf.get(value4);
                resultList.add(time[i] + "第一象限无功总电能:" + parseNumData(ParseUtil.Swap(value4), 6) + "kvarh");
                bf.get(value4);
                resultList.add(time[i] + "第二象限无功总电能:" + parseNumData(ParseUtil.Swap(value4), 6) + "kvarh");
                bf.get(value4);
                resultList.add(time[i] + "第三象限无功总电能:" + parseNumData(ParseUtil.Swap(value4), 6) + "kvarh");
                bf.get(value4);
                resultList.add(time[i] + "第四象限无功总电能:" + parseNumData(ParseUtil.Swap(value4), 6) + "kvarh");
            }
        }
        return resultList;
    }

    /**
     * 解析参数返回数据
     *
     * @param queryInfo
     * @param data      数据域
     * @return
     * @throws PacketLengthException
     * @throws ProtocolIdException
     */
    public static String parseParameter(MeterQueryInfo queryInfo, byte[] data) throws PacketLengthException, ProtocolIdException {
        String ret;
        if (data.length <= DATA_FLAG_LEN) throw new PacketLengthException(DATA_FLAG_LEN);
        byte[] parseData = getDataField(data, DATA_FLAG_LEN);
        int index = queryInfo.getParaIndex();
        switch (queryInfo.getQuerySubId()) {
            case 1:
                //解析时间项1设置
                ret = parseParaType1(index, parseData);
                break;
            case 2:
                //解析时间项2设置
                ret = parseParaType2(index, parseData);
                break;
            case 3:
                //解析屏显设置
                ret = parseParaType3(index, parseData);
                break;
            case 4:
                //解析设备信息设置
                ret = parseParaType4(index, parseData);
                break;
            case 14:
                //解析限值设置
                ret = parseParaType14(index, parseData);
                break;
            case 18:
                //解析版本信息
                ret = parseParaType18(index, parseData);
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        return ret;
    }

    /**
     * 解析时间项1设置
     *
     * @param index
     * @param data
     * @return
     * @throws ProtocolIdException
     * @throws PacketLengthException
     */
    public static String parseParaType1(int index, byte[] data)
            throws ProtocolIdException, PacketLengthException {
        String ret;
        StringBuilder sb = new StringBuilder();
        switch (index) {
            case 0:
                //解析日期及周次
                if (data.length < 4) throw new PacketLengthException(4);
                sb.append(parseNumData(data[0])).append("年");
                sb.append(parseNumData(data[1])).append("月");
                sb.append(parseNumData(data[2])).append("日 ");
//	    		switch(ParseUtil.BCDToINT(data[3])){
//		    		case 0:
//		    			sb.append("星期日");
//		    			break;
//		    		case 1:
//		    			sb.append("星期一");
//		    			break;
//		    		case 2:
//		    			sb.append("星期二");
//		    			break;
//		    		case 3:
//		    			sb.append("星期三");
//		    			break;
//		    		case 4:
//		    			sb.append("星期四");
//		    			break;
//		    		case 5:
//		    			sb.append("星期五");
//		    			break;
//		    		case 6:
//		    			sb.append("星期六");
//		    			break;
//	    		}
                ret = sb.toString();
                break;
            case 1:
                //解析时间
                if (data.length < 2) throw new PacketLengthException(2);
                sb.append(parseNumData(data[0])).append(":");
                sb.append(parseNumData(data[1])).append(":");
                sb.append(parseNumData(data[2]));
                ret = sb.toString();
                break;
            case 2:
            case 3:
            case 4:
                ret = parseNumData(data);
                break;
            case 5:
            case 6:
                sb.append(parseDateData(data, 1));
                ret = sb.toString();
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ret;
    }

    /**
     * 解析时间项2设置
     *
     * @param index
     * @param data
     * @return
     * @throws ProtocolIdException
     */
    public static String parseParaType2(int index, byte[] data) throws ProtocolIdException {
        String ret;
        switch (index) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                ret = parseNumData(data);
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ret;
    }

    /**
     * 解析屏显设置
     *
     * @param index
     * @param data
     * @return
     * @throws ProtocolIdException
     */
    public static String parseParaType3(int index, byte[] data) throws ProtocolIdException {
        String ret;
        switch (index) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                ret = parseNumData(data);
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ret;
    }

    /**
     * 解析设备信息设置
     *
     * @param index
     * @param data
     * @return
     * @throws ProtocolIdException
     */
    public static String parseParaType4(int index, byte[] data) throws ProtocolIdException {
        String ret;
        switch (index) {
            case 0:
            case 1:
            case 8:
            case 9:
                ret = ParseUtil.BCDToSTR(data);
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 10:
            case 11:
            case 12:
                ret = new String(data);
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ret;
    }

    /**
     * 解析限值设置
     *
     * @param index
     * @param data
     * @return
     * @throws ProtocolIdException
     */
    public static String parseParaType14(int index, byte[] data) throws ProtocolIdException {
        String ret;
        switch (index) {
            case 0:
            case 1:
            case 2:
            case 3:
                ret = parseNumData(data);
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ret;
    }

    /**
     * 解析版本信息
     *
     * @param index
     * @param data
     * @return
     * @throws ProtocolIdException
     */
    public static String parseParaType18(int index, byte[] data) throws ProtocolIdException {
        String ret;
        switch (index) {
            case 0:
            case 1:
            case 2:
                ret = new String(data);
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ret;
    }

    /**
     * 生成配置下发报文
     *
     * @param queryInfo
     * @param addr
     * @param pl     开始为1
     * @param data      数据
     * @param pl        密码权限
     * @param pw        操作密码
     * @param id        操作员代码
     * @return
     * @throws ProtocolIdException
     * @throws ParameterFormatException
     */
    public static byte[] getParaWriteRequest(MeterQueryInfo queryInfo, String addr, String data, int pl, String pw, String id)
            throws ProtocolIdException, ParameterFormatException {
        byte[] ret = new byte[0];
        byte[] paraFlag;
        byte[] paraData;
        int index = queryInfo.getParaIndex() + 1;
        switch (queryInfo.getQuerySubId()) {
            case 1:
                //时间项1设置
                paraData = formatParaType1(index, data);
                paraFlag = new byte[]{0, 1, 0, 4};
                paraFlag[0] += index;
                break;
            case 2:
                //时间项2设置
                paraData = formatParaType2(index, data);
                paraFlag = new byte[]{0, 2, 0, 4};
                paraFlag[0] += index;
                break;
            case 3:
                //屏显设置
                paraData = formatParaType3(index, data);
                paraFlag = new byte[]{0, 3, 0, 4};
                paraFlag[0] += index;
                break;
            case 4:
                //设备信息设置
                paraData = formatParaType4(index, data);
                paraFlag = new byte[]{0, 4, 0, 4};
                paraFlag[0] += index;
                break;
            case 14:
                //限值设置
                paraData = formatParaType14(index, data);
                paraFlag = new byte[]{0, 14, 0, 4};
                paraFlag[0] += index;
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        for (int i = 0; i < paraFlag.length; i++) {
            paraFlag[i] += 0x33;
        }
        for (int i = 0; i < paraData.length; i++) {
            paraData[i] += 0x33;
        }
        //生成请求报文
        ret = getAdvRequest((byte) 0x14, addr, pl, pw, id, paraFlag, paraData);
        return ret;
    }

    /**
     * 生成时间项1设置信息
     *
     * @param index 开始为1
     * @param data
     * @return
     * @throws ProtocolIdException
     * @throws ParameterFormatException
     */
    public static byte[] formatParaType1(int index, String data) throws ProtocolIdException, ParameterFormatException {
        byte[] ret;
        SimpleDateFormat sdf;
        Calendar c;
        switch (index) {
            case 1:
                //日期设置
                ret = new byte[4];
                if (data.matches("^[0-9]*$")) {
                    long time = Long.valueOf(data);
                    c = Calendar.getInstance();
                    c.setTimeInMillis(time);
                    ret[0] = ParseUtil.INTToBCDByte(c.get(Calendar.YEAR) % 100);//年
                    ret[1] = ParseUtil.INTToBCDByte(c.get(Calendar.MONTH) + 1);//月
                    ret[2] = ParseUtil.INTToBCDByte(c.get(Calendar.DAY_OF_MONTH));//日
                    ret[3] = ParseUtil.INTToBCDByte(c.get(Calendar.DAY_OF_WEEK) - 1);//周
                } else {
                    //必须为数字字符串
                    throw new ParameterFormatException(data, "string(long)");
                }
                break;
            case 2:
                //时间设置
                ret = new byte[3];
                if (data.matches("^[0-9]*$")) {
                    c = Calendar.getInstance();
                    long time = Long.valueOf(data);
                    c.setTimeInMillis(time);
                    ret[0] = ParseUtil.INTToBCDByte(c.get(Calendar.HOUR_OF_DAY));//时
                    ret[1] = ParseUtil.INTToBCDByte(c.get(Calendar.MINUTE));//分
                    ret[2] = ParseUtil.INTToBCDByte(c.get(Calendar.SECOND));//秒
                } else {
                    //必须为数字字符串
                    throw new ParameterFormatException(data, "string(long)");
                }
                break;
            case 3:
            case 4:
                ret = ParseUtil.NUMToBCD(Integer.valueOf(data));
                break;
            case 6:
            case 7:
                sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
                ret = new byte[5];
                try {
                    c = Calendar.getInstance();
                    c.setTime(sdf.parse(data));
                    ret[0] += (c.get(Calendar.DAY_OF_MONTH) & 100);//年
                    ret[1] += (c.get(Calendar.MONTH + 1));//月
                    ret[2] += c.get(Calendar.DAY_OF_MONTH);//日
                    ret[3] += c.get(Calendar.HOUR_OF_DAY);//时
                    ret[4] += c.get(Calendar.MINUTE);//分
                } catch (ParseException e) {
                    Log.e("DLT645_2007","DLT645_2007 时间设置项日期格式错误");
                }
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ParseUtil.Swap(ret);
    }

    /**
     * 生成时间项2设置信息
     * @param index 开始为1
     * @param data
     * @return
     * @throws ProtocolIdException
     */
    public static byte[] formatParaType2(int index, String data) throws ProtocolIdException {
        byte[] ret;
        switch (index) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 6:
                ret = ParseUtil.NUMToBCD(Integer.valueOf(data));
                break;
            case 5:
                byte[] value = ParseUtil.NUMToBCD(Integer.valueOf(data));
                ret = new byte[2];
                System.arraycopy(value, 0, ret, 0, value.length);
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ParseUtil.Swap(ret);
    }

    /**
     * 生成屏显设置信息
     *
     * @param index 开始为1
     * @param data
     * @return
     * @throws ProtocolIdException
     */
    public static byte[] formatParaType3(int index, String data) throws ProtocolIdException {
        byte[] ret;
        switch (index) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                ret = ParseUtil.NUMToBCD(Integer.valueOf(data));
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ParseUtil.Swap(ret);
    }

    /**
     * 生成设备信息设置信息
     *
     * @param index 开始为1
     * @param data
     * @return
     * @throws ProtocolIdException
     */
    public static byte[] formatParaType4(int index, String data) throws ProtocolIdException {
        byte[] ret;
        switch (index) {
            case 1:
            case 2:
                ret = new byte[6];
                byte[] value = ParseUtil.STRToBCD(data);
                System.arraycopy(value, 0, ret, 0, value.length);
                break;
            case 3:
                ret = data.getBytes();
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ParseUtil.Swap(ret);
    }

    /**
     * 生成限值设置信息
     *
     * @param index
     * @param data
     * @return
     * @throws ProtocolIdException
     */
    public static byte[] formatParaType14(int index, String data) throws ProtocolIdException {
        byte[] ret;
        data = data.replace(".", "");
        byte[] value = ParseUtil.NUMToBCD(Integer.valueOf(data));
        switch (index) {
            case 0:
            case 1:
                ret = new byte[3];
                System.arraycopy(value, 0, ret, 0, value.length);
                break;
            case 2:
            case 3:
                ret = new byte[2];
                System.arraycopy(value, 0, ret, 0, value.length);
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ParseUtil.Swap(ret);
    }


    /**
     * 解析电能量数据报文
     *
     * @param queryInfo
     * @param data
     * @return
     * @throws PacketLengthException
     * @throws ProtocolIdException
     * @throws ProtocolDataTypeException
     */
    public static List<String> parseElecEnergy(MeterQueryInfo queryInfo, byte[] data) throws PacketLengthException, ProtocolIdException, ProtocolDataTypeException {
        String[] typeName = new String[]{"总:", "尖:", "峰:", "平:", "谷:"};
        List<String> ret = new ArrayList<String>();
        String unit = "";
        int dataType = data[1];
        int id = queryInfo.getQuerySubId();
        //根据抄读项添加单位
        if ((id >= 0 && id <= 2) || (id >= 12 && id <= 17)) {
            unit = "kWh";
        } else if (id >= 3 && id <= 8) {
            unit = "kvarh";
        } else if (id >= 9 && id <= 10) {
            unit = "kVAh";
        } else {
            throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), id);
        }
        byte[] parseData = getDataField(data, DATA_FLAG_LEN);
        String[] values = parseNumData(parseData, 4, 6);
        switch (dataType) {
            case -1:
                //数据集合
                for (int i = 0; i < values.length && i < typeName.length; i++) {
                    ret.add(MessageFormat.format("{0}{1}{2}", typeName[i], values[i], unit));
                }
                break;
            case 0:
                //总
            case 1:
                //尖
            case 2:
                //峰
            case 3:
                //平
            case 4:
                //谷
                ret.add(MessageFormat.format("{0}{1}{2}", typeName[dataType], values[0], unit));
                break;
            default:
                throw new ProtocolDataTypeException(dataType);
        }
        return ret;
    }

    /**
     * 解析最大需量
     *
     * @param queryInfo
     * @param data
     * @return
     * @throws PacketLengthException
     * @throws ProtocolDataTypeException
     * @throws ProtocolIdException
     */
    public static List<String> parseDemand(MeterQueryInfo queryInfo, byte[] data)
            throws PacketLengthException, ProtocolDataTypeException, ProtocolIdException {
        String[] typeName = new String[]{"总:", "尖:", "峰:", "平:", "谷:"};
        List<String> ret = new ArrayList<String>();
        String unit = "";
        int dataType = data[1];
        int id = queryInfo.getQuerySubId();
        if (id == 0x01 || id == 0x02) {
            unit = "kW";
        } else if (id == 0x03 || id == 0x04) {
            unit = "kvar";
        } else {
            throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), id);
        }
        //截取数据域
        byte[] parseData = getDataField(data, DATA_FLAG_LEN);
        String[] values = parseDemandValue(parseData, unit);
        switch (dataType) {
            case -1:
                //数据集合
                for (int i = 0; i < values.length && i < typeName.length; i++) {
                    ret.add(MessageFormat.format("{0}{1}", typeName[i], values[i]));
                }
                break;
            case 0:
                //总
            case 1:
                //尖
            case 2:
                //峰
            case 3:
                //平
            case 4:
                //谷
                ret.add(MessageFormat.format("{0}{1}", typeName[dataType], values[0]));
                break;
            default:
                throw new ProtocolDataTypeException(dataType);
        }
        return ret;
    }

    /**
     * 解析变量数据报文
     *
     * @param queryInfo
     * @param data
     * @return
     * @throws PacketLengthException
     * @throws ProtocolDataTypeException
     * @throws ProtocolIdException
     */
    public static List<String> parseElecVar(MeterQueryInfo queryInfo, byte[] data)
            throws PacketLengthException, ProtocolDataTypeException, ProtocolIdException {
        String[] typeName = new String[]{"A相:", "B相:", "C相:"};
        List<String> ret = new ArrayList<String>();
        String unit = "";
        int dataType = data[1];
        int len = 0;//数据长度
        int pointAt = 0;//小数点位置
        boolean haveSum = false;//是否有总数据
        boolean haveNegative = false;//包含负值
        switch (queryInfo.getQuerySubId()) {
            case 1:
                //电压
                len = 2;
                pointAt = 3;
                unit = "V";
                break;
            case 2:
                //电流
                len = 3;
                pointAt = 3;
                unit = "A";
                haveNegative = true;
                break;
            case 3:
                //瞬时有功功率
                len = 3;
                pointAt = 2;
                unit = "kW";
                haveSum = true;
                haveNegative = true;
                break;
            case 4:
                //瞬时无功功率
                len = 3;
                pointAt = 2;
                unit = "kvar";
                haveSum = true;
                haveNegative = true;
                break;
            case 5:
                //瞬时视在功率
                len = 3;
                pointAt = 2;
                unit = "kVA";
                haveSum = true;
                haveNegative = true;
                break;
            case 6:
                //功率因数
                len = 2;
                pointAt = 1;
                unit = "";
                haveSum = true;
                haveNegative = true;
                break;
            case 7:
                //相角
                len = 2;
                pointAt = 3;
                unit = "°";
                break;
            case 8:
                //电压波形失真度
                len = 2;
                pointAt = 2;
                unit = "&";
                break;
            case 9:
                //电流波形失真度
                len = 2;
                pointAt = 2;
                unit = "&";
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        //解析数据
        byte[] parseData = getDataField(data, DATA_FLAG_LEN);
        String[] values = parseNumData(parseData, len, pointAt, haveNegative);
        switch (dataType) {
            case -1:
                //数据集合
                int i = 0;
                if (haveSum) {
                    i = 1;
                    ret.add(MessageFormat.format("总:{0}{1}", values[0], unit));
                }
                for (int a = 0, b = i; a < typeName.length && b < values.length; a++, b++) {
                    ret.add(MessageFormat.format("{0}{1}{2}", typeName[a], values[b], unit));
                }
                break;
            case 0:
                //总
                ret.add(MessageFormat.format("总:{0}{1}", values[0], unit));
                break;
            case 1:
                //A相
            case 2:
                //B相
            case 3:
                //C相
                ret.add(MessageFormat.format("{0}{1}{2}", typeName[dataType - 1], values[0], unit));
                break;
            default:
                throw new ProtocolDataTypeException(dataType);
        }
        return ret;
    }

    /**
     * 解析最大需量
     *
     * @param data
     * @param unit
     * @return
     * @throws PacketLengthException
     */
    public static String[] parseDemandValue(byte[] data, String unit) throws PacketLengthException {
        List<String> ret;
        StringBuilder valueStr;
        StringBuilder timeStr;
        int dataLen = data.length - DATA_FLAG_LEN;
        if (dataLen <= 0) throw new PacketLengthException(DATA_FLAG_LEN);
        //解析数据
        ret = new ArrayList<String>(dataLen / 8);
        for (int a = 0; a < dataLen; a += 8) {
            valueStr = new StringBuilder();
            timeStr = new StringBuilder();
            //解析时间
            for (int b = a; b < a + 5; b++) {
                timeStr.append(BCDToINT(data[b]));
                switch (b % 8) {
                    case 0:
                        timeStr.append("年");
                        break;
                    case 1:
                        timeStr.append("月");
                        break;
                    case 2:
                        timeStr.append("日");
                        break;
                    case 3:
                        timeStr.append(":");
                        break;
                }
            }
            //解析最大需量
            for (int b = a + 5; b < a + 8; b++) {
                valueStr.append(BCDToINT(data[b]));
                switch (b % 8) {
                    case 5:
                        valueStr.append(".");
                        break;
                }
            }
            ret.add(MessageFormat.format("{0}{1} {2}", getNumberFormat(valueStr.toString(), 2), unit, timeStr));
        }
        String[] values = new String[ret.size()];
        ret.toArray(values);
        values = ParseUtil.Swap(values);
        return values;
    }

    public static String BCDToINT(byte data) {
        int value = ParseUtil.BCDToINT(data);
        if (value < 0) {
            return "FF";
        } else {
            if (value < 10) {
                return "0" + String.valueOf(value);
            } else {
                return String.valueOf(value);
            }
        }
    }

    /**
     * 解析错误信息
     *
     * @param err
     * @return
     */
    public static String parsetErrorStr(byte err) {
        StringBuilder sb = new StringBuilder("错误:");
        if ((err & 0x02) > 0) {
            sb.append("无请求数据");
        } else if ((err & 0x04) > 0) {
            sb.append("密码错/未授权");
        } else if ((err & 0x08) > 0) {
            sb.append("通信速率不能更改");
        } else if ((err & 0x10) > 0) {
            sb.append("年时区数超");
        } else if ((err & 0x20) > 0) {
            sb.append("日时区数超");
        } else if ((err & 0x40) > 0) {
            sb.append("费率数超");
        } else {
            sb.append("其他错误");
        }
        return sb.toString();
    }
}
