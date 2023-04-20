package org.freeone.k8s.web.knife.entity.vo;

import com.google.gson.annotations.SerializedName;
import io.kubernetes.client.openapi.models.V1ExecAction;
import io.kubernetes.client.openapi.models.V1GRPCAction;
import io.kubernetes.client.openapi.models.V1HTTPGetAction;
import io.kubernetes.client.openapi.models.V1TCPSocketAction;

public class ProbeVo {


    private ExecActionVo exec;

    private GRPCActionVo grpc;

    private TCPSocketActionVo tcpSocket;

    private HTTPGetActionVo httpGet;


    /**
     * 失败次数
     */
    private Integer failureThreshold;


    /**
     * 初始化等待秒
     */
    private Integer initialDelaySeconds;
    /**
     * 检测次数
     */
    private Integer periodSeconds;

    /**
     * 成功次数
     */
    private Integer successThreshold;

    /**
     * null
     */
    private Long terminationGracePeriodSeconds;
    /**
     * 超时秒
     */
    private Integer timeoutSeconds;

    public ExecActionVo getExec() {
        return this.exec;
    }

    public void setExec(ExecActionVo exec) {
        this.exec = exec;
    }

    public GRPCActionVo getGrpc() {
        return this.grpc;
    }

    public void setGrpc(GRPCActionVo grpc) {
        this.grpc = grpc;
    }

    public TCPSocketActionVo getTcpSocket() {
        return this.tcpSocket;
    }

    public void setTcpSocket(TCPSocketActionVo tcpSocket) {
        this.tcpSocket = tcpSocket;
    }

    public HTTPGetActionVo getHttpGet() {
        return this.httpGet;
    }

    public void setHttpGet(HTTPGetActionVo httpGet) {
        this.httpGet = httpGet;
    }

    public Integer getFailureThreshold() {
        return this.failureThreshold;
    }

    public void setFailureThreshold(Integer failureThreshold) {
        this.failureThreshold = failureThreshold;
    }

    public Integer getInitialDelaySeconds() {
        return this.initialDelaySeconds;
    }

    public void setInitialDelaySeconds(Integer initialDelaySeconds) {
        this.initialDelaySeconds = initialDelaySeconds;
    }

    public Integer getPeriodSeconds() {
        return this.periodSeconds;
    }

    public void setPeriodSeconds(Integer periodSeconds) {
        this.periodSeconds = periodSeconds;
    }

    public Integer getSuccessThreshold() {
        return this.successThreshold;
    }

    public void setSuccessThreshold(Integer successThreshold) {
        this.successThreshold = successThreshold;
    }

    public Long getTerminationGracePeriodSeconds() {
        return this.terminationGracePeriodSeconds;
    }

    public void setTerminationGracePeriodSeconds(Long terminationGracePeriodSeconds) {
        this.terminationGracePeriodSeconds = terminationGracePeriodSeconds;
    }

    public Integer getTimeoutSeconds() {
        return this.timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}
