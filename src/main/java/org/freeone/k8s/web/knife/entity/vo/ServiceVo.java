package org.freeone.k8s.web.knife.entity.vo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ServiceVo {

    private String uid;

    private String name;

    private String namespace;

    /**
     *  ClusterIP | NodePort
     */
    private String type;

    private String clusterIp;

    private String externalIp;

    private Date createTime;

    private Map<String, String> selector;

    List<String> clusterIPs;

    private List<ServicePortVo> ports;

    public List<ServicePortVo> getPorts() {
        return this.ports;
    }

    public ServiceVo setPorts(List<ServicePortVo> ports) {
        this.ports = ports;
        return this;
    }

    public List<String> getClusterIPs() {
        return this.clusterIPs;
    }

    public ServiceVo setClusterIPs(List<String> clusterIPs) {
        this.clusterIPs = clusterIPs;
        return this;
    }

    public String getUid() {
        return this.uid;
    }

    public ServiceVo setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public ServiceVo setName(String name) {
        this.name = name;
        return this;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public ServiceVo setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getType() {
        return this.type;
    }

    public ServiceVo setType(String type) {
        this.type = type;
        return this;
    }

    public String getClusterIp() {
        return this.clusterIp;
    }

    public ServiceVo setClusterIp(String clusterIp) {
        this.clusterIp = clusterIp;
        return this;
    }

    public String getExternalIp() {
        return this.externalIp;
    }

    public ServiceVo setExternalIp(String externalIp) {
        this.externalIp = externalIp;
        return this;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public ServiceVo setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Map<String, String> getSelector() {
        return this.selector;
    }

    public ServiceVo setSelector(Map<String, String> selector) {
        this.selector = selector;
        return this;
    }
}
