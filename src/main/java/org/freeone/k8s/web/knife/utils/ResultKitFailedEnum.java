package org.freeone.k8s.web.knife.utils;

public enum ResultKitFailedEnum {
    /**
     * 登录失败
     */
    LOGIN_FAILED(10_005, "login failed"),

    /**
     * 文件类型错误
     */
    FILE_TYPE_ERROR(10_009, "file type error");


    private int code;

    private String msg;



    ResultKitFailedEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
