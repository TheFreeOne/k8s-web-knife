package org.freeone.k8s.web.knife.sdk;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class TestBean {

    private Date requestTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date requestDate;

    public Date getRequestTime() {
        return this.requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getRequestDate() {
        return this.requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }
}
