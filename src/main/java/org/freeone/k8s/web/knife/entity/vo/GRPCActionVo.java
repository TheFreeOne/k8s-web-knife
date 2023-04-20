package org.freeone.k8s.web.knife.entity.vo;

import com.google.gson.annotations.SerializedName;

public class GRPCActionVo {


    private Integer port;

    private String service;

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getService() {
        return this.service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
