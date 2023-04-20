package org.freeone.k8s.web.knife.entity;

public class DeploymentDTO {
    private String metadata_name;
    private String metadata_namespace;
    private String labels_workLayer;
    private Integer spec_replicas;
    private String containers_name;
    private String containers_image;
    private String containers_imagePullPolicy;
    private String remark;

    public String getMetadata_name() {
        return this.metadata_name;
    }

    public void setMetadata_name(String metadata_name) {
        this.metadata_name = metadata_name;
    }

    public String getMetadata_namespace() {
        return this.metadata_namespace;
    }

    public void setMetadata_namespace(String metadata_namespace) {
        this.metadata_namespace = metadata_namespace;
    }

    public String getLabels_workLayer() {
        return this.labels_workLayer;
    }

    public void setLabels_workLayer(String labels_workLayer) {
        this.labels_workLayer = labels_workLayer;
    }

    public Integer getSpec_replicas() {
        return this.spec_replicas;
    }

    public void setSpec_replicas(Integer spec_replicas) {
        this.spec_replicas = spec_replicas;
    }

    public String getContainers_name() {
        return this.containers_name;
    }

    public void setContainers_name(String containers_name) {
        this.containers_name = containers_name;
    }

    public String getContainers_image() {
        return this.containers_image;
    }

    public void setContainers_image(String containers_image) {
        this.containers_image = containers_image;
    }

    public String getContainers_imagePullPolicy() {
        return this.containers_imagePullPolicy;
    }

    public void setContainers_imagePullPolicy(String containers_imagePullPolicy) {
        this.containers_imagePullPolicy = containers_imagePullPolicy;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
