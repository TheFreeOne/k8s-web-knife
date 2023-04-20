package org.freeone.k8s.web.knife.entity.vo;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DeploymentVo {
    private String uid;
    private String namespace;
    private String name;
    private Integer replicas;
    private OffsetDateTime creationTimestamp;

    private Date createTime;

    private String image;


    private List<PodVo> podVoList;

    private Integer readyReplicas;

    private Map<String, String> labels;
    private Map<String, String> selectorMatchLabels;
    private Map<String, String> annotations;

    private DeploymentStrategyVo strategy;

    private DeploymentStatusVo status;

    private List<ReplicaSetVo> replicaSetList;

    /**
     * container of all old ReplicaSet exclude the ones with no pods
     */
    private List<ReplicaSetVo> oldReplicaSetList;

    /**
     * container of all old ReplicaSet
     */
    private List<ReplicaSetVo> allReplicaSetList;

    private ReplicaSetVo newReplicaSet;

    private List<RolloutHistoryVo> rolloutHistoryVos;

    private List<ContainerVo> containers;

    public DeploymentVo() {
    }

    public DeploymentVo(String uid, String namespace, String name, Integer replicas, OffsetDateTime creationTimestamp) {
        this.uid = uid;
        this.namespace = namespace;
        this.name = name;
        this.replicas = replicas;
        this.creationTimestamp = creationTimestamp;
    }

    public Map<String, String> getSelectorMatchLabels() {
        return this.selectorMatchLabels;
    }

    public void setSelectorMatchLabels(Map<String, String> selectorMatchLabels) {
        this.selectorMatchLabels = selectorMatchLabels;
    }

    public List<ContainerVo> getContainers() {
        return this.containers;
    }

    public void setContainers(List<ContainerVo> containers) {
        this.containers = containers;
    }

    public List<RolloutHistoryVo> getRolloutHistoryVos() {
        return this.rolloutHistoryVos;
    }

    public void setRolloutHistoryVos(List<RolloutHistoryVo> rolloutHistoryVos) {
        this.rolloutHistoryVos = rolloutHistoryVos;
    }

    public List<ReplicaSetVo> getOldReplicaSetList() {
        return this.oldReplicaSetList;
    }

    public void setOldReplicaSetList(List<ReplicaSetVo> oldReplicaSetList) {
        this.oldReplicaSetList = oldReplicaSetList;
    }

    public List<ReplicaSetVo> getAllReplicaSetList() {
        return this.allReplicaSetList;
    }

    public void setAllReplicaSetList(List<ReplicaSetVo> allReplicaSetList) {
        this.allReplicaSetList = allReplicaSetList;
    }

    public ReplicaSetVo getNewReplicaSet() {
        return this.newReplicaSet;
    }

    public void setNewReplicaSet(ReplicaSetVo newReplicaSet) {
        this.newReplicaSet = newReplicaSet;
    }

    // --------- getter


    public List<ReplicaSetVo> getReplicaSetList() {
        return this.replicaSetList;
    }

    public void setReplicaSetList(List<ReplicaSetVo> replicaSetList) {
        this.replicaSetList = replicaSetList;
    }

    public DeploymentStatusVo getStatus() {
        return this.status;
    }

    public void setStatus(DeploymentStatusVo status) {
        this.status = status;
    }


    public DeploymentStrategyVo getStrategy() {
        return this.strategy;
    }

    public void setStrategy(DeploymentStrategyVo strategy) {
        this.strategy = strategy;
    }

    public Map<String, String> getAnnotations() {
        return this.annotations;
    }

    public void setAnnotations(Map<String, String> annotations) {
        this.annotations = annotations;
    }

    public Map<String, String> getLabels() {
        return this.labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public Integer getReadyReplicas() {
        return this.readyReplicas;
    }

    public void setReadyReplicas(Integer readyReplicas) {
        this.readyReplicas = readyReplicas;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<PodVo> getPodVoList() {
        return this.podVoList;
    }


    public String getNamespace() {
        return this.namespace;
    }

    public String getName() {
        return this.name;
    }

    public String getUid() {
        return this.uid;
    }

    public OffsetDateTime getCreationTimestamp() {
        return this.creationTimestamp;
    }

    public Integer getReplicas() {
        return this.replicas;
    }

    // --------------- setter


    public void setPodVoList(List<PodVo> podVoList) {
        this.podVoList = podVoList;
    }


    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setCreationTimestamp(OffsetDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }
}
