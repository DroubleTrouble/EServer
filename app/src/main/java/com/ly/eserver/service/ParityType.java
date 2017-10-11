package com.ly.eserver.service;

/**
 * 校验
 *
 * @author Qing
 */
public enum ParityType {
    NONE('N'),
    EVEN('E'),//偶校验
    ODD('O');//奇校验

    private char value;

    public char getValue() {
        return this.value;
    }

    public static ParityType fromValue(int value) {
        switch (value) {
            case 'E':
                return EVEN;
            case 'O':
                return ODD;
            default:
                return NONE;
        }
    }

    /**
     * 奇偶校验方式
     *
     * @param value
     */
    ParityType(char value) {
        this.value = value;
    }
}
