package org.freeone.k8s.web.knife.entity.vo;

import java.util.List;

public class DeploymentStatusVo {

    private Integer updatedReplicas;

    private Integer replicas;

    private Integer availableReplicas;

    private Integer unavailableReplicas;

    private Integer readyReplicas;

    private List<DeploymentConditionVo> conditions;

    public Integer getUpdatedReplicas() {
        return this.updatedReplicas;
    }

    public void setUpdatedReplicas(Integer updatedReplicas) {
        this.updatedReplicas = updatedReplicas;
    }

    public Integer getReplicas() {
        return this.replicas;
    }

    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }

    public Integer getAvailableReplicas() {
        return this.availableReplicas;
    }

    public void setAvailableReplicas(Integer availableReplicas) {
        this.availableReplicas = availableReplicas;
    }

    public Integer getUnavailableReplicas() {
        return this.unavailableReplicas;
    }

    public void setUnavailableReplicas(Integer unavailableReplicas) {
        this.unavailableReplicas = unavailableReplicas;
    }

    public Integer getReadyReplicas() {
        return this.readyReplicas;
    }

    public void setReadyReplicas(Integer readyReplicas) {
        this.readyReplicas = readyReplicas;
    }

    public List<DeploymentConditionVo> getConditions() {
        return this.conditions;
    }

    public void setConditions(List<DeploymentConditionVo> conditions) {
        this.conditions = conditions;
    }
}
