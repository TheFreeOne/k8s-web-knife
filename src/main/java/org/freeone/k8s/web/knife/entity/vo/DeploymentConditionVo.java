package org.freeone.k8s.web.knife.entity.vo;

import java.time.OffsetDateTime;

public class DeploymentConditionVo {

    /**
     * 最后的迁移时间
     */
    private OffsetDateTime lastTransitionTime;
    /**
     * 最后的检测时间
     */
    private OffsetDateTime lastUpdateTime;

    /**
     * 信息
     */
    private String message;
    /**
     * 原因
     */
    private String reason;

    /**
     * 状态
     */
    private String status;

    /**
     * 类别
     */
    private String type;

    public OffsetDateTime getLastTransitionTime() {
        return this.lastTransitionTime;
    }

    public void setLastTransitionTime(OffsetDateTime lastTransitionTime) {
        this.lastTransitionTime = lastTransitionTime;
    }

    public OffsetDateTime getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(OffsetDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
