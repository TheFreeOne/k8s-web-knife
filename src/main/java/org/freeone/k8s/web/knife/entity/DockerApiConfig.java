package org.freeone.k8s.web.knife.entity;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * k8s 配置记录
 * docker --tlsverify --tlscacert=/root/tls/pem/ca.pem --tlscert=/root/tls/pem/client-cert.pem --tlskey=/root/tls/pem/client-key.pem -H tcp://192.168.0.150:2376 version
 */
@Table(name = "tb_docker_api_config")
@Entity
public class DockerApiConfig {

    private Long id;

    /**
     * tcp://192.168.110.130:12375
     */
    private String hostPath;

    /**
     * 1.41 docker info
     */
    private String apiVersion;

    private String dockerName;

    private Date createTime;
    /**
     * 客户端证书
     */
    private String clientCertPem;
    /**
     * 客户端私钥
     */
    private String clientKeyPem;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return this.id;
    }

    @Column(name = "host_path", nullable = false, columnDefinition = "varchar(2048)  comment 'dockerapi服务的路径'")
    public String getHostPath() {
        return this.hostPath;
    }

    public void setHostPath(String hostPath) {
        this.hostPath = hostPath;
    }

    @Column(name = "api_version", nullable = false, columnDefinition = " varchar(2048) default ''  comment 'api服务的版本 docker info 查看'")

    public String getApiVersion() {
        return this.apiVersion;
    }


    @CreatedDate
    @Column(name = "create_time", nullable = true, columnDefinition = "datetime(0)  comment '创建时间'")
    public Date getCreateTime() {
        return this.createTime;
    }

    @Column(name = "client_cert_pem", nullable = true, columnDefinition = "longtext  comment '客户端证书'")
    public String getClientCertPem() {
        return this.clientCertPem;
    }

    @Column(name = "client_key_pem", nullable = true, columnDefinition = "longtext  comment '客户端私钥'")
    public String getClientKeyPem() {
        return this.clientKeyPem;
    }

    public void setClientKeyPem(String clientKeyPem) {
        this.clientKeyPem = clientKeyPem;
    }

    public void setClientCertPem(String clientCertPem) {
        this.clientCertPem = clientCertPem;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getDockerName() {
        return this.dockerName;
    }

    public void setDockerName(String dockerName) {
        this.dockerName = dockerName;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
