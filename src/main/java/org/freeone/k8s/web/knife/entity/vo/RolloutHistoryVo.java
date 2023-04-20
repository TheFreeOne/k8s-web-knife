package org.freeone.k8s.web.knife.entity.vo;

import io.kubernetes.client.extended.kubectl.KubectlRollout;

public class RolloutHistoryVo  {

    private Long revision;

    private String changeCause;

    public Long getRevision() {
        return this.revision;
    }

    public void setRevision(Long revision) {
        this.revision = revision;
    }

    public String getChangeCause() {
        return this.changeCause;
    }

    public void setChangeCause(String changeCause) {
        this.changeCause = changeCause;
    }
}
