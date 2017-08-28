package org.tio.common;

import lombok.extern.slf4j.Slf4j;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/14
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 基础异常类
 */
@Slf4j
public class TioException extends Exception {
    private int code;

    public TioException(int code) {
        this.code = code;
    }

    public String getMessage() {
        ExceptionCodeEnum codeEnum = ExceptionCodeEnum.valueOf(code);
        if (codeEnum != null) {
            return codeEnum.shortMsg;
        }
        return "No corresponding error short message.";
    }


    public enum ExceptionCodeEnum {
        Null_Input_Error(1, "输入值为Null"),
        Illegal_Operation_Error(2,"方法调用异常");
        public int code;
        private String shortMsg;

        ExceptionCodeEnum(int code, String shortMsg) {
            this.code = code;
            this.shortMsg = shortMsg;
        }

        static ExceptionCodeEnum valueOf(int code) {
            for (ExceptionCodeEnum error : ExceptionCodeEnum.values()) {
                if (error.code == code) {
                    return error;
                }
            }
            return null;
        }
    }
}
