package org.freeone.k8s.web.knife.entity.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NodeVo {


    private String uid;

    /**
     * 节点的名字
     */
    private String name;

    private String status;

    private String roles;


    private String kernelVersion;
    private String kubeletVersion;

    private String kubeProxyVersion;

    private String osImage;

    private String containerRuntimeVersion;

    private Date creationTime;

    private List<NodeAddressVo> addresses;

    private List<NodeConditionVo> conditionVos;

    private String podCIDR;

    private List<String> podCIDRs;

    private  List<QuantityVo> capacityList = new ArrayList<>();
    private List<QuantityVo> allocatableList = new ArrayList<>();


    private List<PodRequestLimitsInfo> podRequestLimitsInfoList;

    public List<PodRequestLimitsInfo> getPodRequestLimitsInfoList() {
        return this.podRequestLimitsInfoList;
    }

    public NodeVo setPodRequestLimitsInfoList(List<PodRequestLimitsInfo> podRequestLimitsInfoList) {
        this.podRequestLimitsInfoList = podRequestLimitsInfoList;
        return this;
    }

    public List<QuantityVo> getCapacityList() {
        return this.capacityList;
    }

    public NodeVo setCapacityList(List<QuantityVo> capacityList) {
        this.capacityList = capacityList;
        return this;
    }

    public List<QuantityVo> getAllocatableList() {
        return this.allocatableList;
    }

    public NodeVo setAllocatableList(List<QuantityVo> allocatableList) {
        this.allocatableList = allocatableList;
        return this;
    }

    public String getPodCIDR() {
        return this.podCIDR;
    }

    public NodeVo setPodCIDR(String podCIDR) {
        this.podCIDR = podCIDR;
        return this;
    }

    public List<String> getPodCIDRs() {
        return this.podCIDRs;
    }

    public NodeVo setPodCIDRs(List<String> podCIDRs) {
        this.podCIDRs = podCIDRs;
        return this;
    }

    public List<NodeConditionVo> getConditionVos() {
        return this.conditionVos;
    }

    public NodeVo setConditionVos(List<NodeConditionVo> conditionVos) {
        this.conditionVos = conditionVos;
        return this;
    }

    public String getStatus() {
        return this.status;
    }

    public NodeVo setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getRoles() {
        return this.roles;
    }

    public NodeVo setRoles(String roles) {
        this.roles = roles;
        return this;
    }

    public List<NodeAddressVo> getAddresses() {
        return this.addresses;
    }

    public NodeVo setAddresses(List<NodeAddressVo> addresses) {
        this.addresses = addresses;
        return this;
    }

    public Date getCreationTime() {
        return this.creationTime;
    }

    public NodeVo setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    public String getKernelVersion() {
        return this.kernelVersion;
    }

    public NodeVo setKernelVersion(String kernelVersion) {
        this.kernelVersion = kernelVersion;
        return this;
    }

    public String getKubeletVersion() {
        return this.kubeletVersion;
    }

    public NodeVo setKubeletVersion(String kubeletVersion) {
        this.kubeletVersion = kubeletVersion;
        return this;
    }

    public String getKubeProxyVersion() {
        return this.kubeProxyVersion;
    }

    public NodeVo setKubeProxyVersion(String kubeProxyVersion) {
        this.kubeProxyVersion = kubeProxyVersion;
        return this;
    }

    public String getOsImage() {
        return this.osImage;
    }

    public NodeVo setOsImage(String osImage) {
        this.osImage = osImage;
        return this;
    }

    public String getContainerRuntimeVersion() {
        return this.containerRuntimeVersion;
    }

    public NodeVo setContainerRuntimeVersion(String containerRuntimeVersion) {
        this.containerRuntimeVersion = containerRuntimeVersion;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public NodeVo setName(String name) {
        this.name = name;
        return this;
    }

    public String getUid() {
        return this.uid;
    }

    public NodeVo setUid(String uid) {
        this.uid = uid;
        return this;
    }
}
