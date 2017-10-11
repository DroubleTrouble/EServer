package com.ly.eserver.protocol.dlt645.dlt64597;


import com.ly.eserver.protocol.MeterQueryInfo;
import com.ly.eserver.protocol.dlt645.DLT645_Parse;
import com.ly.eserver.protocol.exception.PacketLengthException;
import com.ly.eserver.protocol.exception.ParameterFormatException;
import com.ly.eserver.protocol.exception.ProtocolDataTypeException;
import com.ly.eserver.protocol.exception.ProtocolIdException;
import com.ly.eserver.util.ParseUtil;
import com.ly.eserver.util.StringUtil;

import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * DLT645_1997模块
 *
 * @author Xuqn
 */
public class DLT645_1997 extends DLT645_Parse {

    /**
     * *重要*数据标识长度
     */
    public static final int DATA_FLAG_LEN = 2;

    /**
     * 电能量标识
     */
    public static final int ENERGY_FLAG = 0;
    /**
     * 最大需量标识
     */
    public static final int DEMAND_FLAG = 1;
    /**
     * 最大需量时间标识
     */
    public static final int DEMAND_TIME_FLAG = 2;
    /**
     * 变量标识
     */
    public static final int VARIABLE_FLAG = 3;
    /**
     * 事件标识
     */
    public static final int EVENT_FLAG = 4;
    /**
     * 参变量标识
     */
    public static final int PARAMETER_FLAG = 5;
    /**
     * 高级请求标识
     */
    public static final int ADV_REQUEST_FLAG = 9;

    /**
     * 读数据返回
     */
    public static final byte READ_RETURN = 0x01;
    /**
     * 写数据返回
     */
    public static final byte WRITE_RETURN = 0x04;


    //请求数据模版
    public static final byte[] READ_REQUEST = new byte[]{(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE,
            (byte) 0x68, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x68, (byte) 0x01, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x16};

    //写数据模版
    public static final byte[] WRITE_REQUEST_HEAD = new byte[]{(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE,
            (byte) 0x68, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x68};

    private static final int REQUEST_ADDR_START = 5;
    private static final int REQUEST_CON_START = 14;
    private static final int REQUEST_LEN = 18;

    /**
     * 获取数据信息
     *
     * @param data
     * @return
     * @throws PacketLengthException
     */
    public static DLT645_1997_Info getDataInfo(byte[] data) throws PacketLengthException {
        DLT645_1997_Info info = new DLT645_1997_Info();
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
            case 0x01:
                //读数据
                //解析数据域
                byte[] dataType = getDataFlag(info.resultData, DATA_FLAG_LEN);
                info.id = parseId(dataType[0]);
                switch (info.id) {
                    case ENERGY_FLAG:
                        //电能量
                        info.subId = parseElecEnergySubId(dataType);
                        break;
                    case DEMAND_FLAG:
                        //最大需量
                        info.subId = parseDemandSubId(dataType);
                        break;
                    case DEMAND_TIME_FLAG:
                        //最大需量发生时间
                        info.subId = parseDemandTimeSubId(dataType);
                        break;
                    case VARIABLE_FLAG:
                        //变量
                        info.subId = ((dataType[1] & 0xF0) >> 4) - 1;
                        break;
                    case EVENT_FLAG:
                        //事件
                        info.subId = parseEventSubId(dataType);
                        break;
                    case PARAMETER_FLAG:
                        //参变量
                        info.subId = (dataType[1] & 0xF0) >> 4;
                        break;
                }
                break;
        }
        return info;
    }

    /**
     * 获取南网二型(芯珑)采集器数据信息
     *
     * @param data
     * @return
     * @throws PacketLengthException
     */
    public static DLT645_1997_Info getXLDataInfo(byte[] data) throws PacketLengthException {
        DLT645_1997_Info info = new DLT645_1997_Info();
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
            case 0x04:
                //写数据
            case 0x01:
                //读数据
                //解析数据域
                byte[] dataType = getDataFlag(info.resultData, DATA_FLAG_LEN);
                switch (dataType[0] & 0xF0) {
                    case 0xF0:
                        //南网二型(芯珑)采集器扩展设置
                        info.id = 10;
                        switch (dataType[1] & 0xFF) {
                            case 0x33:
                                //采集器工作模式
                                info.subId = 0;
                                break;
                            case 0x05:
                                //采集器版本
                                info.subId = 2;
                                break;
                        }
                        break;
                }
                break;
        }
        return info;
    }

    /**
     * 获取高级抄读项
     *
     * @param queryInfo 抄读信息
     * @param addr      地址
     * @param data      数据
     * @param pl        权限等级
     * @param pw        密码
     * @return
     * @throws ProtocolIdException
     */
    public static byte[] getAdvRequest(MeterQueryInfo queryInfo, String addr, String data, int pl, String pw) throws ProtocolIdException {
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
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA);
                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(sdf.parse(data));
                } catch (ParseException e) {
                    return new byte[0];
                }
                value[0] = ParseUtil.INTToBCDByte(c.get(Calendar.YEAR) % 100);//年
                value[1] = ParseUtil.INTToBCDByte((c.get(Calendar.MONTH) + 1));//月
                value[2] = ParseUtil.INTToBCDByte(c.get(Calendar.DAY_OF_MONTH));//日
                value[3] = ParseUtil.INTToBCDByte(c.get(Calendar.HOUR_OF_DAY));//时
                value[4] = ParseUtil.INTToBCDByte(c.get(Calendar.MINUTE));//分
                value[5] = ParseUtil.INTToBCDByte(c.get(Calendar.SECOND));//秒
                break;
            case 2:
                //写通信地址
                conCode = 0x0A;
                value = ParseUtil.STRToBCD(data);
                break;
            case 3:
                //更改通信速率
                conCode = 0x0C;
                if (data.matches("^[0-9]*$")) {
                    value = new byte[1];
                    switch (Integer.valueOf(data)) {
                        case 600:
                            value[0] = 0x04;
                            break;
                        case 1200:
                            value[0] = 0x08;
                            break;
                        case 2400:
                            value[0] = 0x10;
                            break;
                        case 4800:
                            value[0] = 0x20;
                            break;
                        case 9600:
                            value[0] = 0x40;
                            break;
                        default:
                            value[0] = 0x08;
                            break;
                    }
                } else {
                    return new byte[0];
                }
                break;
            case 4:
                //修改认证密码
                conCode = 0x0F;
                //解析密码设置
                if (data.matches("^[0-9]*$")) {
                    value = ParseUtil.STRToBCD(data);
                } else {
                    return new byte[0];
                }
                break;
            case 5:
                //最大需量清零
                conCode = 0x10;
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        return getAdvRequest(conCode, addr, pl, pw, new byte[0], value);
    }

    /**
     * 生成高级请求信息
     *
     * @param conCode 控制码
     * @param addr    地址
     * @param pLevel  权限等级
     * @param pw      密码
     * @param flag    数据标识
     * @param data    数据
     * @return
     */
    public static byte[] getAdvRequest(byte conCode, String addr, int pLevel, String pw, byte[] flag, byte[] data) {
        int Len;
        int dataLen;//数据区长度
        ByteBuffer bf;
        byte[] value;
        byte[] ret;
        if (flag == null || data == null) return new byte[0];
        if (pLevel == -1) {
            //无需认证
            dataLen = flag.length + data.length;
            Len = WRITE_REQUEST_HEAD.length + dataLen + 4;//控制码+长度+效验+帧尾
        } else {
            //需要认证
            dataLen = flag.length + data.length + 4;
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
            //插入密码
            if (pw != null && pw.matches("^[0-9]{6}$")) {
                value = ParseUtil.Swap(ParseUtil.STRToBCD(pw));
                for (int i = 0; i < 3; i++) {
                    value[i] += 0x33;
                }
                bf.put(value);
            } else {
                bf.put(new byte[]{0x33, 0x33, 0x33});
            }
        }
        bf.put(data);
        bf.put(new byte[]{0x00, 0x16});//校验位+帧尾
        //插入地址
        if (addr != null && addr.matches("[0-9]{12}")) {
            value = ParseUtil.Swap(ParseUtil.STRToBCD(addr));
            System.arraycopy(value, 0, ret, REQUEST_ADDR_START, 6);
        }
        //重新计算校验位
        byte checkSum = checkSum(ret, 4, Len - 2);
        ret[Len - 2] = checkSum;
        return ret;
    }

    /**
     * 生成数据读取请求报文
     *
     * @param queryInfo
     * @param addr
     * @param phase
     * @param rate
     * @param time
     * @return
     * @throws ProtocolIdException
     */
    public static byte[] getReadRequest(MeterQueryInfo queryInfo, String addr, int phase, int rate, int time) throws ProtocolIdException {
        switch (queryInfo.getQueryId()) {
            case ENERGY_FLAG:
                //电能量
                return getElecEnergyRequest(queryInfo, addr, rate, time);
            case DEMAND_FLAG:
                //最大需量
                return getDemandRequest(queryInfo, addr, rate, time);
            case DEMAND_TIME_FLAG:
                //最大需量发生时间
                return getDemandTimeRequest(queryInfo, addr, rate, time);
            case VARIABLE_FLAG:
                //变量
                return getElecVarRequest(queryInfo, addr, phase);
            case EVENT_FLAG:
                //事件
                return getEventRequest(queryInfo, addr, phase);
            case PARAMETER_FLAG:
                //参变量
                return getParaRequest(queryInfo, addr);
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_ID(), queryInfo.getQueryId());
        }
    }

    /**
     * 根据抄读类型解析报文(不包含参变量数据)
     *
     * @param queryInfo
     * @param data
     * @return
     * @throws PacketLengthException
     * @throws ProtocolIdException
     * @throws ProtocolDataTypeException
     */
    public static List<String> parse(MeterQueryInfo queryInfo, byte[] data)
            throws PacketLengthException, ProtocolIdException, ProtocolDataTypeException {
        List<String> ret = new ArrayList<String>();
        if (data.length <= DATA_FLAG_LEN) throw new PacketLengthException(DATA_FLAG_LEN);
        //根据数据类型解析报文
        switch (queryInfo.getQueryId()) {
            case ENERGY_FLAG:
                ret = parseElecEnergy(queryInfo, data);
                break;
            case DEMAND_FLAG:
                ret = parseDemand(queryInfo, data);
                break;
            case DEMAND_TIME_FLAG:
                ret = parseDemandTime(queryInfo, data);
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
     * 解析电能量数据报文
     *
     * @param queryInfo
     * @param data
     * @return
     * @throws PacketLengthException
     * @throws ProtocolIdException
     * @throws ProtocolDataTypeException
     */
    public static List<String> parseElecEnergy(MeterQueryInfo queryInfo, byte[] data)
            throws PacketLengthException, ProtocolIdException, ProtocolDataTypeException {
        String[] typeName;
        List<String> ret = new ArrayList<String>();
        String unit = "";
        int dataType = data[0] & 0x0F;
        if (queryInfo.getQuerySubId() < 2) {
            unit = "kWh";
        } else if (queryInfo.getQuerySubId() < 8) {
            unit = "kvarh";
        } else {
            throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        byte[] parseData = getDataField(data, DATA_FLAG_LEN);
        String[] values = parseNumData(parseData, 4, 6);
        if (values.length < 4) {
            typeName = new String[]{"总:", "费率1:", "费率2:", "费率3:", "费率4:"};
        } else {
            typeName = new String[]{"总:", "尖:", "峰:", "平:", "谷:"};
        }
        switch (dataType) {
            case 0x0F:
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
     * @throws ProtocolIdException
     * @throws ProtocolDataTypeException
     */
    public static List<String> parseDemand(MeterQueryInfo queryInfo, byte[] data)
            throws PacketLengthException, ProtocolIdException, ProtocolDataTypeException {
        String[] typeName;
        List<String> ret = new ArrayList<String>();
        String unit = "";
        int dataType = data[0] & 0x0F;
        if (queryInfo.getQuerySubId() < 2) {
            unit = "kW";
        } else if (queryInfo.getQuerySubId() < 8) {
            unit = "kvar";
        } else {
            throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        byte[] praseData = getDataField(data, DATA_FLAG_LEN);
        String[] values = parseNumData(praseData, 3, 2);
        if (values.length < 4) {
            typeName = new String[]{"总:", "费率1:", "费率2:", "费率3:", "费率4:"};
        } else {
            typeName = new String[]{"总:", "尖:", "峰:", "平:", "谷:"};
        }
        switch (dataType) {
            case 0x0F:
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
     * 解析变量数据报文
     *
     * @param queryInfo
     * @param data
     * @return
     * @throws PacketLengthException
     * @throws ProtocolIdException
     * @throws ProtocolDataTypeException
     */
    public static List<String> parseElecVar(MeterQueryInfo queryInfo, byte[] data)
            throws PacketLengthException, ProtocolIdException, ProtocolDataTypeException {
        String[] typeName = new String[]{"A相:", "B相:", "C相:"};
        List<String> ret = new ArrayList<String>();
        String unit = "";
        int dataType = data[0] & 0x0F;
        int len = 0;//数据长度
        int pointAt = 0;//小数点位置
        boolean haveSum = false;//是否有总数据
        switch (queryInfo.getQuerySubId()) {
            case 1:
                //电压
                len = 2;
                pointAt = 0;
                unit = "V";
                break;
            case 2:
                //电流
                len = 2;
                pointAt = 2;
                unit = "A";
                break;
            case 3:
                //瞬时有功功率
                len = 3;
                pointAt = 2;
                unit = "kW";
                haveSum = true;
                break;
            case 4:
                //瞬时无功功率
                len = 2;
                pointAt = 2;
                unit = "kvarh";
                haveSum = true;
                break;
            case 5:
                //功率因数
                len = 2;
                pointAt = 1;
                unit = "";
                haveSum = true;
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        byte[] parseData = getDataField(data, DATA_FLAG_LEN);
        String[] values = parseNumData(parseData, len, pointAt);
        switch (dataType) {
            case 0x0F:
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
     * 解析最大需量发生时间
     *
     * @param queryInfo
     * @param data
     * @return
     * @throws PacketLengthException
     * @throws ProtocolDataTypeException
     */
    public static List<String> parseDemandTime(MeterQueryInfo queryInfo, byte[] data) throws PacketLengthException, ProtocolDataTypeException {
        String[] typeName = new String[]{"总:", "尖:", "峰:", "平:", "谷:"};
        List<String> ret = new ArrayList<String>();
        int dataType = data[0] & 0x0F;
        String[] values = parseDatesData(data, 4, 1);
        switch (dataType) {
            case 0x0F:
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
     * 解析数据大类
     *
     * @param code
     * @return
     */
    public static int parseId(byte code) {
        switch (code & 0xF0) {
            case 0x90:
                //电能量
                return ENERGY_FLAG;
            case 0xA0:
                //最大需量
                return DEMAND_FLAG;
            case 0xB0:
                //最大需量发生时间/变量
                switch (code & 0x0F) {
                    case 0x02:
                    case 0x03:
                        //事件
                        return EVENT_FLAG;
                    case 0x06:
                        //变量
                        return VARIABLE_FLAG;
                    default:
                        //最大需量发生时间
                        return DEMAND_TIME_FLAG;
                }
            case 0xC0:
                //参变量
                return PARAMETER_FLAG;
        }
        return -1;
    }

    /**
     * 解析事件数据
     *
     * @param queryInfo
     * @param data
     * @return
     * @throws PacketLengthException
     * @throws ProtocolIdException
     * @throws ProtocolDataTypeException
     */
    public static List<String> parseEvent(MeterQueryInfo queryInfo, byte[] data) throws PacketLengthException, ProtocolIdException, ProtocolDataTypeException {
        List<String> ret = new ArrayList<String>();
        byte[] parseData = getDataField(data, DATA_FLAG_LEN);
        switch (queryInfo.getQuerySubId()) {
            case 0:
            case 1:
                ret.add(parseCustomDateData(parseData, 1));
                break;
            case 2:
            case 3:
                ret.add(parseNumData(parseData));
                break;
            case 4:
                ret.add(MessageFormat.format("{0} 分", parseNumData(parseData)));
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                ret.addAll(parseEventType(queryInfo.getQuerySubId(), parseData, data[0] & 0x0F));
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        return ret;
    }

    /**
     * 解析断相次数,断相时间,起始时间,结束时间
     *
     * @param subId
     * @param data
     * @param dataType
     * @return
     * @throws ProtocolIdException
     * @throws ProtocolDataTypeException
     */
    public static List<String> parseEventType(int subId, byte[] data, int dataType) throws ProtocolIdException, ProtocolDataTypeException {
        String[] typeName = new String[]{"总:", "A相:", "B相:", "C相:"};
        List<String> ret = new ArrayList<String>();
        String unit;
        if (subId == 6) {
            unit = "分";
        } else {
            unit = "";
        }
        String value;
        switch (subId) {
            case 5:
            case 6:
                value = parseNumData(data);
                break;
            case 7:
            case 8:
                value = parseCustomDateData(data, 1);
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), subId);
        }
        switch (dataType) {
            case 0x0F:
                ret.add(MessageFormat.format("{0}{1}{2}", typeName[0], value, unit));
                break;
            case 0:
            case 1:
            case 2:
            case 3:
                ret.add(MessageFormat.format("{0}{1}{2}", typeName[dataType], value, unit));
                break;
            default:
                throw new ProtocolDataTypeException(dataType);
        }

        return ret;
    }

    /**
     * 解析参变量数据
     *
     * @param queryInfo
     * @param data
     * @return
     * @throws PacketLengthException
     * @throws ProtocolIdException
     */
    public static String parseParameter(MeterQueryInfo queryInfo, byte[] data) throws PacketLengthException, ProtocolIdException {
        String ret;
        int dataLen = data.length - DATA_FLAG_LEN;
        if (dataLen <= 0) throw new PacketLengthException(DATA_FLAG_LEN);
        byte[] parseData = getDataField(data, DATA_FLAG_LEN);
        int index = queryInfo.getParaIndex();
        switch (queryInfo.getQuerySubId()) {
            case 1:
                //时间设置1
                ret = parseParaType1(index, parseData);
                break;
            case 6:
                //时间设置2
                ret = parseParaType2(index, parseData);
                break;
            case 2:
                //状态设置
                ret = parseParaType3(index, parseData);
                break;
            case 3:
                //设备信息设置
                ret = parseParaType4(index, parseData);
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        return ret;
    }

    /**
     * 解析时间设置1
     *
     * @param index
     * @param data
     * @return
     * @throws PacketLengthException
     * @throws ProtocolIdException
     */
    public static String parseParaType1(int index, byte[] data) throws PacketLengthException, ProtocolIdException {
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
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ret;
    }

    /**
     * 解析时间设置2
     *
     * @param index
     * @param data
     * @return
     * @throws ProtocolIdException
     */
    public static String parseParaType2(int index, byte[] data) throws ProtocolIdException {
        String ret;
        if (data.length >= 1) {
            ret = ParseUtil.BCDToSTR(data);
        } else {
            throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ret;
    }

    /**
     * 解析状态设置
     *
     * @param index
     * @param data
     * @return
     * @throws ProtocolIdException
     */
    public static String parseParaType3(int index, byte[] data) throws ProtocolIdException {
        StringBuilder sb = new StringBuilder();
        byte status = data[0];
        switch (index) {
            case 0:
                //电表运行状态字
                sb.append("抄表:" + ((status & 0x01) == 1 ? "手动" : "自动")).append("\r");
                sb.append("最大需量计算方式:" + ((status & 0x02) == 1 ? "区间" : "滑差")).append("\r");
                sb.append("电池电压:" + ((status & 0x04) == 1 ? "欠压" : "正常")).append("\r");
                sb.append("有功电能方向:" + ((status & 0x10) == 1 ? "反向" : "正向")).append("\r");
                sb.append("无功电能方向" + ((status & 0x20) == 1 ? "反向" : "正向"));
                break;
            case 1:
                //电网运行状态字
                sb.append("A相:" + ((status & 0x01) == 1 ? "断电" : "正常")).append("\r");
                sb.append("B相:" + ((status & 0x02) == 1 ? "断电" : "正常")).append("\r");
                sb.append("C相:" + ((status & 0x04) == 1 ? "断电" : "正常")).append("\r");
                sb.append("A相:" + ((status & 0x10) == 1 ? "过压" : "正常")).append("\r");
                sb.append("B相:" + ((status & 0x20) == 1 ? "过压" : "正常")).append("\r");
                sb.append("C相:" + ((status & 0x40) == 1 ? "过压" : "正常"));
                break;
            case 2:
                //周休日状态字
                sb.append("周日:" + ((status & 0x01) == 1 ? "休息" : "工作")).append("\r");
                sb.append("周一:" + ((status & 0x02) == 1 ? "休息" : "工作")).append("\r");
                sb.append("周二:" + ((status & 0x04) == 1 ? "休息" : "工作")).append("\r");
                sb.append("周三:" + ((status & 0x08) == 1 ? "休息" : "工作")).append("\r");
                sb.append("周四:" + ((status & 0x10) == 1 ? "休息" : "工作")).append("\r");
                sb.append("周五:" + ((status & 0x20) == 1 ? "休息" : "工作")).append("\r");
                sb.append("周六:" + ((status & 0x40) == 1 ? "休息" : "工作"));
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return sb.toString();
    }

    /**
     * 解析设备信息设置
     *
     * @param index
     * @param data
     * @return
     * @throws ProtocolIdException
     * @throws PacketLengthException
     */
    public static String parseParaType4(int index, byte[] data) throws ProtocolIdException, PacketLengthException {
        String ret;
        switch (index) {
            case 0:
                //电表常数(有功)
            case 1:
                //电表常数(无功)
                if (data.length < 3) throw new PacketLengthException(3);
                ret = ParseUtil.BCDToSTR(data);
                break;
            case 2:
            case 3:
            case 4:
                //解析表号,用户号,设备码
                if (data.length < 6) throw new PacketLengthException(6);
                ret = ParseUtil.BCDToSTR(data);
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ret;
    }


    /**
     * 解析电能量小类
     *
     * @param dataType
     * @return
     */
    public static int parseElecEnergySubId(byte[] dataType) {
        switch (dataType[0] & 0x01) {
            case 0x00:
                return ((dataType[1] & 0xF0) >> 4) - 1;
            case 0x01:
                return ((dataType[1] & 0xF0) >> 4) + 1;
            default:
                return -1;
        }
    }

    /**
     * 解析最大需量小类
     *
     * @param dataType
     * @return
     */
    public static int parseDemandSubId(byte[] dataType) {
        return parseElecEnergySubId(dataType);
    }

    /**
     * 解析最大需量发生时间小类
     *
     * @param dataType
     * @return
     */
    public static int parseDemandTimeSubId(byte[] dataType) {
        return parseElecEnergySubId(dataType);
    }

    /**
     * 解析事件小类
     *
     * @param dataType
     * @return
     */
    public static int parseEventSubId(byte[] dataType) {
        switch (dataType[0] & 0x0F) {
            case 0x02:
                return dataType[1] & 0x0F;
            default:
                return ((dataType[1] & 0xF0) >> 4) + 4;
        }
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
        System.arraycopy(dataFlag, 0, request, REQUEST_CON_START, DATA_FLAG_LEN);
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
     * 获取电能量请求报文
     *
     * @param queryInfo
     * @param addr
     * @param rate
     * @param time
     * @return
     * @throws ProtocolIdException
     */
    public static byte[] getElecEnergyRequest(MeterQueryInfo queryInfo, String addr, int rate, int time) throws ProtocolIdException {
        byte[] dataFlag = new byte[]{0x33, 0x33};
        //生成控制命令
        //类型
        if (queryInfo.getQuerySubId() < 2) {
            dataFlag[0] += 0x90;
            dataFlag[1] += (queryInfo.getQuerySubId() + 1) << 4;
        } else if (queryInfo.getQuerySubId() < 8) {
            dataFlag[0] += 0x91;
            dataFlag[1] += (queryInfo.getQuerySubId() - 1) << 4;
        } else {
            throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        //时间
        switch (time) {
            case 1:
                //上月
                dataFlag[0] += 0x04;
                break;
            case 2:
                //上上月
                dataFlag[0] += 0x08;
                break;
        }
        //费率
        dataFlag[1] += rate;
        dataFlag = ParseUtil.Swap(dataFlag);
        return getReadRequest(dataFlag, addr);
    }

    /**
     * 获取变量请求报文
     *
     * @param queryInfo
     * @param addr
     * @param phase     相位: 0: 总 1:A相 2:B相 3:C相
     * @return
     * @throws ProtocolIdException
     */
    public static byte[] getElecVarRequest(MeterQueryInfo queryInfo, String addr, int phase) throws ProtocolIdException {
        byte[] dataFlag = new byte[]{0x33, 0x33};
        //生成控制命令
        //类型
        dataFlag[1] += queryInfo.getQuerySubId() << 4;
        //相位
        if (queryInfo.getQuerySubId() < 2 && phase == 0) {
            //电流,电压无总数据
            throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        dataFlag[1] += phase;
        dataFlag[0] += 0xB6;//只限于电流,电压,有功功率,无功功率,功率因数
        dataFlag = ParseUtil.Swap(dataFlag);
        return getReadRequest(dataFlag, addr);
    }

    /**
     * 获取最大需量请求报文
     *
     * @param queryInfo
     * @param addr
     * @param rate
     * @param time
     * @return
     * @throws ProtocolIdException
     */
    public static byte[] getDemandRequest(MeterQueryInfo queryInfo, String addr, int rate, int time) throws ProtocolIdException {
        byte[] dataFlag = new byte[]{0x33, 0x33};
        //生成控制命令
        //类型
        if (queryInfo.getQuerySubId() < 2) {
            dataFlag[0] += 0xA0;
            dataFlag[1] += (queryInfo.getQuerySubId() + 1) << 4;
        } else if (queryInfo.getQuerySubId() < 8) {
            dataFlag[0] += 0xA1;
            dataFlag[1] += (queryInfo.getQuerySubId() - 1) << 4;
        } else {
            throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        //时间
        switch (time) {
            case 1:
                //上月
                dataFlag[0] += 0x04;
                break;
            case 2:
                //上上月
                dataFlag[0] += 0x08;
                break;
        }
        //费率
        dataFlag[1] += rate;
        dataFlag = ParseUtil.Swap(dataFlag);
        return getReadRequest(dataFlag, addr);
    }

    /**
     * 获取最大需量发生时间请求报文
     *
     * @param queryInfo
     * @param addr
     * @param rate
     * @param time
     * @return
     * @throws ProtocolIdException
     */
    public static byte[] getDemandTimeRequest(MeterQueryInfo queryInfo, String addr, int rate, int time) throws ProtocolIdException {
        byte[] dataFlag = new byte[]{0x33, 0x33};
        //生成控制命令
        //类型
        if (queryInfo.getQuerySubId() < 2) {
            dataFlag[0] += 0xB0;
            dataFlag[1] += (queryInfo.getQuerySubId() + 1) << 4;
        } else if (queryInfo.getQuerySubId() < 8) {
            dataFlag[0] += 0xB1;
            dataFlag[1] += (queryInfo.getQuerySubId() - 1) << 4;
        } else {
            throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        //时间
        switch (time) {
            case 1:
                //上月
                dataFlag[0] += 0x04;
                break;
            case 2:
                //上上月
                dataFlag[0] += 0x08;
                break;
        }
        //费率
        dataFlag[1] += rate;
        dataFlag = ParseUtil.Swap(dataFlag);
        return getReadRequest(dataFlag, addr);
    }

    /**
     * 获取事件请求报文
     *
     * @param queryInfo
     * @param addr
     * @param phase
     * @return
     */
    public static byte[] getEventRequest(MeterQueryInfo queryInfo, String addr, int phase) {
        byte[] dataFlag = new byte[]{0x33, 0x33};
        if (queryInfo.getQuerySubId() < 5) {
            dataFlag[0] += 0xB2;
            dataFlag[1] += (0x10 + queryInfo.getQuerySubId());
        } else {
            dataFlag[0] += 0xB3;
            dataFlag[1] += (queryInfo.getQuerySubId() - 4) << 4;
            //相位
            dataFlag[1] += phase;
        }
        dataFlag = ParseUtil.Swap(dataFlag);
        return getReadRequest(dataFlag, addr);
    }

    /**
     * 获取参变量请求
     *
     * @param queryInfo
     * @param addr
     * @return
     * @throws ProtocolIdException
     */
    public static byte[] getParaRequest(MeterQueryInfo queryInfo, String addr) throws ProtocolIdException {
        byte[] dataFlag = new byte[]{0x33, 0x33};
        int index = queryInfo.getParaIndex();
        switch (queryInfo.getQuerySubId()) {
            case 1:
                //时间设置1
            case 2:
                //状态设置
            case 3:
                //设备信息设置
                dataFlag[0] += 0xC0;
                dataFlag[1] += queryInfo.getQuerySubId() << 4;
                //类别
                dataFlag[1] += index;
                break;
            case 6:
                //时间设置2
                dataFlag[0] += 0xC3;
                dataFlag[1] += 0x10;
                dataFlag[1] += index;
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        dataFlag = ParseUtil.Swap(dataFlag);
        return getReadRequest(dataFlag, addr);
    }

    /**
     * 获取参变量写请求数据
     *
     * @param queryInfo
     * @param addr
     * @param data
     * @param pl
     * @param pw
     * @return
     * @throws ProtocolIdException
     * @throws ParameterFormatException
     */
    public static byte[] getParaWriteRequest(MeterQueryInfo queryInfo, String addr, String data, int pl, String pw)
            throws ProtocolIdException, ParameterFormatException {
        byte[] ret = new byte[0];
        byte[] paraFlag;
        byte[] paraData;
        int index = queryInfo.getParaIndex();
        switch (queryInfo.getQuerySubId()) {
            case 1:
                //时间设置1
                paraData = formatParaType1(index, data);
                paraFlag = new byte[]{0x10, (byte) 0xC0};
                paraFlag[0] += index;
                break;
            case 6:
                //时间设置2
                paraData = formatParaType6(index, data);
                paraFlag = new byte[]{0x10, (byte) 0xC3};
                paraFlag[0] += index;
                break;
            case 2:
                //状态设置
                paraData = formatParaType2(index, data);
                paraFlag = new byte[]{0x20, (byte) 0xC0};
                paraFlag[0] += index;
                break;
            case 3:
                //设备信息设置
                paraData = formatParaType3(index, data);
                paraFlag = new byte[]{0x30, (byte) 0xC0};
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
        ret = getAdvRequest((byte) 0x04, addr, pl, pw, paraFlag, paraData);
        return ret;
    }

    /**
     * 获取南网二型(芯珑)采集器请求报文
     *
     * @param queryInfo
     * @param addr
     * @return
     */
    public static byte[] getXLReadRequest(MeterQueryInfo queryInfo, String addr)
            throws ProtocolIdException {
        byte[] dataFlag;
        if (queryInfo.getQueryId() != 10)
            throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_ID(), queryInfo.getQueryId());
        switch (queryInfo.getQuerySubId()) {
            case 0:
                //采集器工作模式
                byte[] request = new byte[]{(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE,
                        (byte) 0x68, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x68,
                        (byte) 0x01, (byte) 0x08, (byte) 0x66, (byte) 0x32,
                        (byte) 0x7D, (byte) 0x86, (byte) 0x7F, (byte) 0x8C, //密码
                        (byte) 0x32, (byte) 0x32, (byte) 0x00, (byte) 0x16};
                //插入地址
                if (addr.matches("[0-9]{12}")) {
                    //数字地址才进行解析
                    byte[] addrB = ParseUtil.Swap(ParseUtil.STRToBCD(addr));
                    System.arraycopy(addrB, 0, request, REQUEST_ADDR_START, 6);
                }
                //重新计算校验位
                byte checkSum = checkSum(request, 4, request.length - 2);
                request[request.length - 2] = checkSum;
                return request;
            case 1:
                //采集器数据清零
                dataFlag = new byte[]{(byte) 0x65, 0x32};
                break;
            case 2:
                //采集器版本信息
                dataFlag = new byte[]{(byte) 0x38, 0x32};
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        return getReadRequest(dataFlag, addr);
    }

    /**
     * 获取南网二型(芯珑)采集器设置报文
     *
     * @param queryInfo
     * @param addr      非广播地址外任何地址
     * @param data
     * @return
     */
    public static byte[] getXLWriteRequest(MeterQueryInfo queryInfo, String addr, byte[] data)
            throws ProtocolIdException {
        byte[] dataFlag;
        boolean haveKey = true;
        switch (queryInfo.getQuerySubId()) {
            case 0:
                //采集器工作模式
                dataFlag = new byte[]{(byte) 0xFF, 0x33};
                break;
            case 1:
                //采集器数据擦除
                dataFlag = new byte[]{(byte) 0xFF, 0x32};
                break;
            case 2:
                //采集器版本
                dataFlag = new byte[]{(byte) 0xFF, 0x05};
                haveKey = false;
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_SUB_ID(), queryInfo.getQuerySubId());
        }
        ByteBuffer bf = ByteBuffer.allocate(WRITE_REQUEST_HEAD.length + data.length + (haveKey ? 10 : 6));
        bf.put(WRITE_REQUEST_HEAD.clone());
        bf.put((byte) 0x04);//写数据
        bf.put((byte) ((haveKey ? 4 : 0) + data.length + dataFlag.length));//数据长度
        if (queryInfo.getQueryId() != 10)
            throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_ID(), queryInfo.getQueryId());
        for (int i = 0; i < dataFlag.length; i++) {
            dataFlag[i] += 0x33;
        }
        bf.put(ParseUtil.Swap(dataFlag));//数据标识
        if (haveKey) bf.put(new byte[]{0x7D, (byte) 0x86, 0x7F, (byte) 0x8C});//密码JSLY
        for (int i = 0; i < data.length; i++) {
            data[i] += 0x33;
        }
        bf.put(ParseUtil.Swap(data));
        bf.put((byte) 0);//校验位
        bf.put((byte) 0x16);
        //生成写请求报文
        byte[] request = bf.array();
        //插入地址
        if (addr.matches("[0-9]{12}")) {
            //数字地址才进行解析
            byte[] addrB = ParseUtil.Swap(ParseUtil.STRToBCD(addr));
            System.arraycopy(addrB, 0, request, REQUEST_ADDR_START, 6);
        }
        byte checkSum = checkSum(request, 4, request.length - 2);
        request[request.length - 2] = checkSum;
        return request;
    }


    /**
     * 生成时间设置1设置信息
     *
     * @param index
     * @param data
     * @return
     * @throws ParameterFormatException
     * @throws ProtocolIdException
     */
    public static byte[] formatParaType1(int index, String data) throws ParameterFormatException, ProtocolIdException {
        byte[] ret;
        Calendar c;
        switch (index) {
            case 0:
                //日期设置
                ret = new byte[4];
                if (data.matches("^[0-9]*$")) {
                    long time = Long.valueOf(data);
                    c = Calendar.getInstance();
                    c.setTimeInMillis(time);
                    ret[3] = ParseUtil.INTToBCDByte(c.get(Calendar.YEAR) % 100);//年
                    ret[2] = ParseUtil.INTToBCDByte(c.get(Calendar.MONTH) + 1);//月
                    ret[1] = ParseUtil.INTToBCDByte(c.get(Calendar.DAY_OF_MONTH));//日
                    ret[0] = ParseUtil.INTToBCDByte(c.get(Calendar.DAY_OF_WEEK) - 1);//周
                } else {
                    //必须为数字字符串
                    throw new ParameterFormatException(data, "string(long)");
                }
                break;
            case 1:
                //时间设置
                ret = new byte[3];
                if (data.matches("^[0-9]*$")) {
                    c = Calendar.getInstance();
                    long time = Long.valueOf(data);
                    c.setTimeInMillis(time);
                    ret[2] = ParseUtil.INTToBCDByte(c.get(Calendar.HOUR_OF_DAY));//时
                    ret[1] = ParseUtil.INTToBCDByte(c.get(Calendar.MINUTE));//分
                    ret[0] = ParseUtil.INTToBCDByte(c.get(Calendar.SECOND));//秒
                } else {
                    //必须为数字字符串
                    throw new ParameterFormatException(data, "string(long)");
                }
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ret;
    }

    /**
     * 生成状态设置设置信息
     *
     * @param index
     * @param data  格式:00000000
     * @return
     * @throws ProtocolIdException
     * @throws ParameterFormatException
     */
    public static byte[] formatParaType2(int index, String data) throws ProtocolIdException, ParameterFormatException {
        byte[] ret;
        switch (index) {
            case 0:
            case 1:
            case 2:
                //验证格式是否正确
                if (data.matches("^[0-9]*$")) {
                    ret = new byte[1];
                    data = StringUtil.fillWith(data, '0', 8, false);
                    //二进制String->byte
                    for (int i = 0; i < 8; i++) {
                        ret[0] |= ((data.charAt(7 - i) == '0' ? 1 : 0) << i);
                    }
                } else {
                    //必须为数字字符串
                    throw new ParameterFormatException(data, "string(number)");
                }
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ret;
    }

    /**
     * 生成设备信息设置设置信息
     *
     * @param index
     * @param data
     * @return
     * @throws ProtocolIdException
     * @throws ParameterFormatException
     */
    public static byte[] formatParaType3(int index, String data) throws ProtocolIdException, ParameterFormatException {
        byte[] ret;
        int len;
        //验证格式是否正确
        if (!data.matches("^[0-9]*$")) throw new ParameterFormatException(data, "string(number)");
        switch (index) {
            case 0:
            case 1:
                len = 3;
                break;
            case 2:
            case 3:
            case 4:
                len = 2;
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        data = StringUtil.fillWith(data, '0', len, false);
        ret = ParseUtil.STRToBCD(data);
        return ParseUtil.Swap(ret);
    }

    /**
     * 生成时间设置2设置信息
     *
     * @param index
     * @param data
     * @return
     * @throws ProtocolIdException
     * @throws ParameterFormatException
     */
    public static byte[] formatParaType6(int index, String data) throws ProtocolIdException, ParameterFormatException {
        byte[] ret;
        //验证格式是否正确
        if (!data.matches("^[0-9]*$")) throw new ParameterFormatException(data, "string(number)");
        switch (index) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                data = StringUtil.fillWith(data, '0', 2, false);
                ret = ParseUtil.STRToBCD(data);
                break;
            default:
                throw new ProtocolIdException(ProtocolIdException.Companion.getTYPE_PARAMETER_INDEX(), index);
        }
        return ParseUtil.Swap(ret);
    }

    /**
     * 解析错误信息
     *
     * @param err
     * @return
     */
    public static String parsetErrorStr(byte err) {
        StringBuilder sb = new StringBuilder("错误:");
        if ((err & 0x01) > 0) {
            sb.append("非法数据");
        } else if ((err & 0x02) > 0) {
            sb.append("数据标识错误");
        } else if ((err & 0x04) > 0) {
            sb.append("密码错");
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

    /**
     * 解析日期数据组,用于解析带有时间数据的报文
     *
     * @param data 数据域
     * @param len
     * @param skip
     * @return
     * @throws PacketLengthException
     */
    public static String[] parseDatesData(byte[] data, int len, int skip) throws PacketLengthException {
        List<String> ret;
        int dataLen = data.length - DATA_FLAG_LEN;
        if (dataLen <= 0) return new String[]{"数据项错误"};
        byte[] parseData = new byte[dataLen];
        ret = new ArrayList<String>(dataLen / len + 1);
        //截取有效数据段
        parseData = getDataField(data, DATA_FLAG_LEN);
        byte[] dataData = new byte[len];
        for (int a = 0; a < dataLen; a += len) {
            if (parseData[a] == (byte) 0xAA) {
                a += 1;
                if (a + len > dataLen) {
                    break;
                }
            }
            System.arraycopy(parseData, a, dataData, 0, len);
            ret.add(parseCustomDateData(dataData, 1));
        }
        String[] values = new String[ret.size()];
        ret.toArray(values);
        values = ParseUtil.Swap(values);
        return values;
    }
}
