package org.freeone.k8s.web.knife.entity.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PodRequestLimitsInfo {

    private String namespace;
    private String name;

    private Date createTime;

    private List<QuantityVo> requestList = new ArrayList<>();
    private List<QuantityVo> limitList = new ArrayList<>();

    public List<QuantityVo> getRequestList() {
        return this.requestList;
    }

    public PodRequestLimitsInfo setRequestList(List<QuantityVo> requestList) {
        this.requestList = requestList;
        return this;
    }

    public List<QuantityVo> getLimitList() {
        return this.limitList;
    }

    public PodRequestLimitsInfo setLimitList(List<QuantityVo> limitList) {
        this.limitList = limitList;
        return this;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public PodRequestLimitsInfo setNamespace(String namespace) {
        this.namespace = namespace;

        return this;
    }

    public String getName() {
        return this.name;
    }

    public PodRequestLimitsInfo setName(String name) {
        this.name = name;
        return this;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public PodRequestLimitsInfo setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }
}
