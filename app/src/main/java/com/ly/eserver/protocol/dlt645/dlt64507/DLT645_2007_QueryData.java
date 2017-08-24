package com.ly.eserver.protocol.dlt645.dlt64507;


import com.ly.eserver.protocol.MeterQueryInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * DLT645_2007 抄读项
 *
 * @author Xuqn
 */
public class DLT645_2007_QueryData {

    /**
     * 获取高级抄读项列表
     *
     * @return
     */
    public static List<MeterQueryInfo> getAdvancedQueryList() {
        List<MeterQueryInfo> queryList = new ArrayList<MeterQueryInfo>();

        MeterQueryInfo queryInfo = new MeterQueryInfo("广播校时", 9, 1);
        queryList.add(queryInfo);

        queryInfo = new MeterQueryInfo("读/写通信地址", 9, 2);
        queryList.add(queryInfo);

        queryInfo = new MeterQueryInfo("更改通信速率", 9, 3);
        queryList.add(queryInfo);

        queryInfo = new MeterQueryInfo("修改认证密码", 9, 4);
        queryList.add(queryInfo);

        queryInfo = new MeterQueryInfo("最大需量清零", 9, 5);
        queryList.add(queryInfo);

        queryInfo = new MeterQueryInfo("电表清零", 9, 6);
        queryList.add(queryInfo);

        queryInfo = new MeterQueryInfo("事件清零", 9, 7);
        queryList.add(queryInfo);

        queryInfo = new MeterQueryInfo("拉闸", 9, 8);
        queryList.add(queryInfo);

        queryInfo = new MeterQueryInfo("合闸", 9, 9);
        queryList.add(queryInfo);

        return queryList;
    }

    /**
     * 获取电能量抄读列表
     *
     * @return
     */
    public static List<MeterQueryInfo> getElecEnergyQueryList() {
        List<MeterQueryInfo> queryList = new ArrayList<MeterQueryInfo>();

        MeterQueryInfo queryInfo = new MeterQueryInfo("组合有功电能", 0, 0);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("正向有功电能", 0, 1);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("反向有功电能", 0, 2);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("组合无功1电能", 0, 3);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("组合无功2电能", 0, 4);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("第一象限无功电能", 0, 5);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("第二象限无功电能", 0, 6);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("第三象限无功电能", 0, 7);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("第四象限无功电能", 0, 8);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("正向视在电能", 0, 9);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("反向视在电能", 0, 10);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("关联总电能", 0, 11);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("正向有功基波总电能", 0, 12);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("反向有功基波总电能", 0, 13);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("正向有功谐波总电能", 0, 14);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("反向有功谐波总电能", 0, 15);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("铜损有功总电能补偿量", 0, 16);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("铁损有功总电能补偿量", 0, 17);
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

        MeterQueryInfo queryInfo = new MeterQueryInfo("电压", 2, 1);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("电流", 2, 2);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("瞬时有功功率", 2, 3);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("瞬时无功功率", 2, 4);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("瞬时视在功率", 2, 5);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("功率因数", 2, 6);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("相角", 2, 7);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("电压波形失真度", 2, 8);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("电流波形失真度", 2, 9);
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

        MeterQueryInfo queryInfo = new MeterQueryInfo("正向有功最大需量及发生时间", 1, 1);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("反向有功最大需量及发生时间", 1, 2);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("组合无功1最大需量及发生时间", 1, 3);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("组合无功2最大需量及发生时间", 1, 4);
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

        MeterQueryInfo queryInfo = new MeterQueryInfo("失压记录", 3, 1);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("欠压记录", 3, 2);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("过压记录", 3, 3);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("断相记录", 3, 4);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("全失压记录", 3, 5);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("辅助电源失电记录", 3, 6);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("电压逆向序记录", 3, 7);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("电流逆向序记录", 3, 8);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("电压不平衡记录", 3, 9);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("电流不平衡记录", 3, 10);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("失流记录", 3, 11);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("过流记录", 3, 12);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("断流记录", 3, 13);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("潮流记录", 3, 14);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("过载记录", 3, 15);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("电压合格率统计数据", 3, 16);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("掉电记录", 3, 17);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("超限记录", 3, 18);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("编程记录", 3, 19);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("电表清零记录", 3, 20);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("需量清零记录", 3, 21);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("事件清零记录", 3, 22);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("校时记录", 3, 23);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("时段表编程记录", 3, 24);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("时区表编程记录", 3, 25);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("周休日编程记录", 3, 26);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("节假日编程记录", 3, 27);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("有功组合方式编程记录", 3, 28);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("无功组合方式1编程记录", 3, 29);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("无功组合方式2编程记录", 3, 30);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("结算日编程记录", 3, 31);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("开表盖记录", 3, 32);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("开端钮盒记录", 3, 33);
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

        MeterQueryInfo queryInfo = new MeterQueryInfo("时间项1设置", 4, 1);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("时间项2设置", 4, 2);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("屏显设置", 4, 3);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("设备信息设置", 4, 4);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("限值设置", 4, 14);
        queryList.add(queryInfo);
        queryInfo = new MeterQueryInfo("版本信息", 4, 18);
        queryList.add(queryInfo);

        return queryList;
    }
}
