package com.ly.eserver.protocol.dlt645.dlt64597;


import com.ly.eserver.protocol.MeterQueryInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * DLT645_1997抄读项
 *
 * @author Xuqn
 */
public class DLT645_1997_QueryData {


    /**
     * 获取高级抄读项
     *
     * @return
     */
    public static List<MeterQueryInfo> getAdvancedQueryList() {
        List<MeterQueryInfo> queryList = new ArrayList<MeterQueryInfo>();

        MeterQueryInfo queryInfo = new MeterQueryInfo("广播校时", 9, 1);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("写通信地址", 9, 2);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("更改通信速率", 9, 3);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("修改认证密码", 9, 4);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("最大需量清零", 9, 5);
        queryList.add(queryInfo);

        return queryList;
    }


    /**
     * 获取电能量抄读列表
     *
     * @return
     */
    public static List<MeterQueryInfo> getElecEnergyTaskList() {
        List<MeterQueryInfo> queryList = new ArrayList<MeterQueryInfo>();

        MeterQueryInfo queryInfo = new MeterQueryInfo("正向有功电能", 0, 0);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("反向有功电能", 0, 1);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("正向无功电能", 0, 2);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("反向无功电能", 0, 3);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("一象限无功电能", 0, 4);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("四象限无功电能", 0, 5);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("二象限无功电能", 0, 6);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("三象限无功电能", 0, 7);
        queryList.add(queryInfo);

        return queryList;
    }

    /**
     * 获取变量抄读列表
     *
     * @return
     */
    public static List<MeterQueryInfo> getElecVarQueryList() {
        List<MeterQueryInfo> queryList = new ArrayList<MeterQueryInfo>();

        MeterQueryInfo queryInfo = new MeterQueryInfo("电压", 3, 1);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("电流", 3, 2);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("瞬时有功功率", 3, 3);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("瞬时无功功率", 3, 4);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("功率因数", 3, 5);
        queryList.add(queryInfo);

        return queryList;
    }

    /**
     * 获取最大需量抄读列表
     *
     * @return
     */
    public static List<MeterQueryInfo> getDemandQueryList() {
        List<MeterQueryInfo> queryList = new ArrayList<MeterQueryInfo>();

        MeterQueryInfo queryInfo = new MeterQueryInfo("正向有功最大需量", 1, 0);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("反向有功最大需量", 1, 1);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("正向无功最大需量", 1, 2);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("反向无功最大需量", 1, 3);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("一象限无功最大需量", 1, 4);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("四象限无功最大需量", 1, 5);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("二象限无功最大需量", 1, 6);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("三象限无功最大需量", 1, 7);
        queryList.add(queryInfo);

        return queryList;
    }

    /**
     * 获取最大需量时间抄读列表
     *
     * @return
     */
    public static List<MeterQueryInfo> getDemandTimeQueryList() {
        List<MeterQueryInfo> queryList = new ArrayList<MeterQueryInfo>();

        MeterQueryInfo queryInfo = new MeterQueryInfo("正向有功最大需量发生时间", 4, 0);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("反向有功最大需量发生时间", 4, 1);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("正向无功最大需量发生时间", 4, 2);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("反向无功最大需量发生时间", 4, 3);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("一象限无功最大需量发生时间", 4, 4);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("四象限无功最大需量发生时间", 4, 5);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("二象限无功最大需量发生时间", 4, 6);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("三象限无功最大需量发生时间", 4, 7);
        queryList.add(queryInfo);

        return queryList;
    }

    /**
     * 获取事件抄读列表
     *
     * @return
     */
    public static List<MeterQueryInfo> getEventQueryList() {
        List<MeterQueryInfo> queryList = new ArrayList<MeterQueryInfo>();

        MeterQueryInfo queryInfo = new MeterQueryInfo("最近一次编程时间", 2, 0);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("最近一次最大需量清零时间", 2, 1);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("编程次数", 2, 2);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("最大需量清零次数", 2, 3);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("电池工作时间", 2, 4);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("断相次数", 2, 5);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("断相时间累计", 2, 6);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("断相起始时间", 2, 7);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("断相结束时间", 2, 8);
        queryList.add(queryInfo);

        return queryList;
    }

    /**
     * 获取参数抄读列表
     *
     * @return
     */
    public static List<MeterQueryInfo> getParameterQueryList() {
        List<MeterQueryInfo> queryList = new ArrayList<MeterQueryInfo>();

        MeterQueryInfo queryInfo = new MeterQueryInfo("时间设置1", 5, 1);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("时间设置2", 5, 6);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("状态设置", 5, 2);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("设备信息设置", 5, 3);
        queryList.add(queryInfo);

        return queryList;
    }

    /**
     * 南网二型采集器(芯珑)扩展规约
     *
     * @return
     */
    public static List<MeterQueryInfo> getXLQueryList() {
        List<MeterQueryInfo> queryList = new ArrayList<MeterQueryInfo>();

        MeterQueryInfo queryInfo = new MeterQueryInfo("采集器工作模式", 10, 0);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("采集器数据擦除", 10, 1);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("采集器版本", 10, 2);
        queryList.add(queryInfo);
        return queryList;
    }
}
