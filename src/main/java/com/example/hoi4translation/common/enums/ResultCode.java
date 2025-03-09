package com.example.hoi4translation.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息响应码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    /**
     * 系统异常
     */
    SYSTEM_ERROR(500, "系统异常");

    private final Integer code;
    private final String msg;
}
