package org.freeone.k8s.web.knife.entity.vo;

import com.google.gson.annotations.SerializedName;
import io.kubernetes.client.custom.IntOrString;

public class TCPSocketActionVo {


    private String host;


    private Integer portInt;

    private String portString;

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPortInt() {
        return this.portInt;
    }

    public void setPortInt(Integer portInt) {
        this.portInt = portInt;
    }

    public String getPortString() {
        return this.portString;
    }

    public void setPortString(String portString) {
        this.portString = portString;
    }
}
