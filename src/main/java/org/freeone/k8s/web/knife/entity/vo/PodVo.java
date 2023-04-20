package org.freeone.k8s.web.knife.entity.vo;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 实例列表
 */
public class PodVo {
    private String namespace;


    /**
     * 实例名称
     */
    private String name;
    /**
     * 状态
     */
    private String phase;

    /**
     * 节点ip
     */
    private String hostIP;


    /**
     *
     */
    private String podIP;

    /**
     * 重启次数
     */
    private Integer restartCount;

    private OffsetDateTime startedAt;

    private Date createAt;

    private Boolean ready;

    private String reason;

    private String message;

    private String resourceVersion;

    // ------------ more detail -----------------

    private String uid;

    private Map<String, String> labels;

    private String qosClass;

    private List<PodConditionVo> conditions;

    private  List<EventVo> events;

    private List<ContainerVo> containers;



    public PodVo() {
    }

    public PodVo(String name, String phase, String hostIP, String podIP, Integer restartCount, OffsetDateTime startedAt, Boolean ready) {
        this.name = name;
        this.phase = phase;
        this.hostIP = hostIP;
        this.podIP = podIP;
        this.restartCount = restartCount;
        this.startedAt = startedAt;
        this.ready = ready;
    }

    public List<ContainerVo> getContainers() {
        return this.containers;
    }

    public void setContainers(List<ContainerVo> containers) {
        this.containers = containers;
    }

    public List<EventVo> getEvents() {
        return this.events;
    }

    public void setEvents(List<EventVo> events) {
        this.events = events;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public List<PodConditionVo> getConditions() {
        return this.conditions;
    }

    public void setConditions(List<PodConditionVo> conditions) {
        this.conditions = conditions;
    }

    public String getQosClass() {
        return this.qosClass;
    }

    public void setQosClass(String qosClass) {
        this.qosClass = qosClass;
    }

    public Map<String, String> getLabels() {
        return this.labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getResourceVersion() {
        return this.resourceVersion;
    }

    public void setResourceVersion(String resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getReady() {
        return this.ready;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
    }

    public Date getCreateAt() {
        return this.createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhase() {
        return this.phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getHostIP() {
        return this.hostIP;
    }

    public void setHostIP(String hostIP) {
        this.hostIP = hostIP;
    }

    public String getPodIP() {
        return this.podIP;
    }

    public void setPodIP(String podIP) {
        this.podIP = podIP;
    }

    public Integer getRestartCount() {
        return this.restartCount;
    }

    public void setRestartCount(Integer restartCount) {
        this.restartCount = restartCount;
    }

    public OffsetDateTime getStartedAt() {
        return this.startedAt;
    }

    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }
}
