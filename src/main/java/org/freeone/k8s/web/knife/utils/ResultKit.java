package org.freeone.k8s.web.knife.utils;


public class ResultKit {

    private int code = 10_000;

    private String msg = "success";

    private String subCode;

    private String subMsg;

    private Object data;

    public static ResultKit ok(){
        return new ResultKit();
    }
    public static ResultKit okWithData(Object data){
        ResultKit resultKit = new ResultKit();
        resultKit.setData(data);
        return resultKit;
    }

    public static ResultKit failed(String msg) {
        ResultKit resultKit = new ResultKit();
        resultKit.setCode(10_001);
        resultKit.setMsg(msg);
        return resultKit;
    }
     public static ResultKit error(String msg) {
        ResultKit resultKit = new ResultKit();
        resultKit.setCode(11_001);
        resultKit.setMsg(msg);
        return resultKit;
    }


    public static ResultKit failed(ResultKitFailedEnum resultKitFailedEnum) {
        ResultKit resultKit = new ResultKit();
        resultKit.setCode(resultKitFailedEnum.getCode());
        resultKit.setMsg(resultKitFailedEnum.getMsg());
        return resultKit;
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

    public String getSubCode() {
        return this.subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public String getSubMsg() {
        return this.subMsg;
    }

    public void setSubMsg(String subMsg) {
        this.subMsg = subMsg;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
