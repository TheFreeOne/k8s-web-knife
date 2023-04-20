package org.freeone.k8s.web.knife.entity.vo;

import com.google.gson.annotations.SerializedName;

public class NodeAddressVo {

    private String address;

    private String type;

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
