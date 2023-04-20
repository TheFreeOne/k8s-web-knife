package org.freeone.k8s.web.knife.entity.vo;

public class VolumeMountVo {
    private String name;

    private Boolean readOnly;

    /**
     * 挂载路径
     */
    private String mountPath;

    /**
     * 组路径
     */
    private String subPath;

    private String mountPropagation;

    private String subPathExpr;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getMountPath() {
        return this.mountPath;
    }

    public void setMountPath(String mountPath) {
        this.mountPath = mountPath;
    }

    public String getSubPath() {
        return this.subPath;
    }

    public void setSubPath(String subPath) {
        this.subPath = subPath;
    }

    public String getMountPropagation() {
        return this.mountPropagation;
    }

    public void setMountPropagation(String mountPropagation) {
        this.mountPropagation = mountPropagation;
    }

    public String getSubPathExpr() {
        return this.subPathExpr;
    }

    public void setSubPathExpr(String subPathExpr) {
        this.subPathExpr = subPathExpr;
    }
}
