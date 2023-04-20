package org.freeone.k8s.web.knife.entity.vo;

public class ServicePortVo {


    private String appProtocol;


    private String name;
    /**
     * 映射到该node的端口
     */
    private Integer nodePort;
    /**
     * Service 暴露的端口号
     */
    private Integer port;
    /**
     * 该端口的协议类型，可选值为 TCP 或 UDP，默认为 TCP。
     */
    private String protocol;
    /**
     * 定义此 Service 要转发的目标 pod 的端口号，当一个请求到达服务所在的节点由iptables转发到Pod时，请求会被转发到pod上运行的该端口的容器内；
     */
    private Integer targetPortInt;

    private String targetPortString;

    public String getAppProtocol() {
        return this.appProtocol;
    }

    public ServicePortVo setAppProtocol(String appProtocol) {
        this.appProtocol = appProtocol;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public ServicePortVo setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getNodePort() {
        return this.nodePort;
    }

    public ServicePortVo setNodePort(Integer nodePort) {
        this.nodePort = nodePort;
        return this;
    }

    public Integer getPort() {
        return this.port;
    }

    public ServicePortVo setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public ServicePortVo setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public Integer getTargetPortInt() {
        return this.targetPortInt;
    }

    public ServicePortVo setTargetPortInt(Integer targetPortInt) {
        this.targetPortInt = targetPortInt;
        return this;
    }

    public String getTargetPortString() {
        return this.targetPortString;
    }

    public ServicePortVo setTargetPortString(String targetPortString) {
        this.targetPortString = targetPortString;
        return this;
    }
}
