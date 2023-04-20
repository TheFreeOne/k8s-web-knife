package org.freeone.k8s.web.knife.entity.vo;

import java.util.Date;

public class NodeConditionVo {


    private Date lastHeartbeatTime;

    private Date lastTransitionTime;

    private String message;

    private String reason;


    private String status;

    private String type;

    public Date getLastHeartbeatTime() {
        return this.lastHeartbeatTime;
    }

    public NodeConditionVo setLastHeartbeatTime(Date lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
        return this;
    }

    public Date getLastTransitionTime() {
        return this.lastTransitionTime;
    }

    public NodeConditionVo setLastTransitionTime(Date lastTransitionTime) {
        this.lastTransitionTime = lastTransitionTime;
        return this;
    }

    public String getMessage() {
        return this.message;
    }

    public NodeConditionVo setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getReason() {
        return this.reason;
    }

    public NodeConditionVo setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public String getStatus() {
        return this.status;
    }

    public NodeConditionVo setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getType() {
        return this.type;
    }

    public NodeConditionVo setType(String type) {
        this.type = type;
        return this;
    }
}
