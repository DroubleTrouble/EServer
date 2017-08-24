package com.ly.eserver.service;

/**
 * 数据位
 *
 * @author Qing
 */
public enum DataBitType {
    DATA_5(5),
    DATA_6(6),
    DATA_7(7),
    DATA_8(8);

    private int value;

    public int getValue() {
        return this.value;
    }

    /**
     * 数据位
     *
     * @param value
     */
    DataBitType(int value) {
        this.value = value;
    }

    public static DataBitType fromValue(int value) {
        switch (value) {
            case 5:
                return DATA_5;
            case 6:
                return DATA_6;
            case 7:
                return DATA_7;
            default:
                return DATA_8;
        }
    }
}
