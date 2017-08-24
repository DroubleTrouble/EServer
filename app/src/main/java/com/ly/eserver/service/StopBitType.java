package com.ly.eserver.service;

/**
 * @author Qing
 */
public enum StopBitType {
    STOP_1(1),
    STOP_1_5(1.5F),
    STOP_2(2);

    private float value;

    public float getValue() {
        return this.value;
    }

    /**
     * @param value
     */
    StopBitType(float value) {
        this.value = value;
    }

    public static StopBitType fromValue(float value) {
        if (value == 2) {
            return STOP_2;
        } else if (value == 1.5F) {
            return STOP_1_5;
        } else {
            return STOP_1;
        }
    }
}
