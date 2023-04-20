package org.freeone.k8s.web.knife.entity.vo;

import java.util.List;

public class ContainerVo {

    private String name;

    private String image;

    private String imagePullPolicy;

    private ProbeVo readinessProbe;

    private ProbeVo livenessProbe;

    private List<VolumeMountVo> volumeMountVos;

    private Boolean ready;

    private Integer restartCount;

    private Boolean started;

    public Boolean getReady() {
        return this.ready;
    }

    public void setReady(Boolean ready) {
        this.ready = ready;
    }

    public Integer getRestartCount() {
        return this.restartCount;
    }

    public void setRestartCount(Integer restartCount) {
        this.restartCount = restartCount;
    }

    public Boolean getStarted() {
        return this.started;
    }

    public void setStarted(Boolean started) {
        this.started = started;
    }

    public List<VolumeMountVo> getVolumeMountVos() {
        return this.volumeMountVos;
    }

    public void setVolumeMountVos(List<VolumeMountVo> volumeMountVos) {
        this.volumeMountVos = volumeMountVos;
    }

    public ProbeVo getLivenessProbe() {
        return this.livenessProbe;
    }

    public void setLivenessProbe(ProbeVo livenessProbe) {
        this.livenessProbe = livenessProbe;
    }

    public ProbeVo getReadinessProbe() {
        return this.readinessProbe;
    }

    public void setReadinessProbe(ProbeVo readinessProbe) {
        this.readinessProbe = readinessProbe;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImagePullPolicy() {
        return this.imagePullPolicy;
    }

    public void setImagePullPolicy(String imagePullPolicy) {
        this.imagePullPolicy = imagePullPolicy;
    }
}
