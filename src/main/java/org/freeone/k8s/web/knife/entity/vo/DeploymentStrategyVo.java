package org.freeone.k8s.web.knife.entity.vo;

public class DeploymentStrategyVo {
    /**
     * Type of deployment. Can be "Recreate" or "RollingUpdate". Default is RollingUpdate.
     * 滚动更新 或 重新创建
     */
    private String type;

    /**
     * 最大超量
     * 控制发布速度的快慢，实例值越大，发布越快
     */
    private String maxSurgeString;
    private Integer maxSurgeInt;

    /**
     * 最大不可用
     * 控制发布的稳定性，实例值越小，发布越平滑
     */
    private String maxUnavailableString;
    private Integer maxUnavailableInt;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMaxSurgeString() {
        return this.maxSurgeString;
    }

    public void setMaxSurgeString(String maxSurgeString) {
        this.maxSurgeString = maxSurgeString;
    }

    public Integer getMaxSurgeInt() {
        return this.maxSurgeInt;
    }

    public void setMaxSurgeInt(Integer maxSurgeInt) {
        this.maxSurgeInt = maxSurgeInt;
    }

    public String getMaxUnavailableString() {
        return this.maxUnavailableString;
    }

    public void setMaxUnavailableString(String maxUnavailableString) {
        this.maxUnavailableString = maxUnavailableString;
    }

    public Integer getMaxUnavailableInt() {
        return this.maxUnavailableInt;
    }

    public void setMaxUnavailableInt(Integer maxUnavailableInt) {
        this.maxUnavailableInt = maxUnavailableInt;
    }
}
