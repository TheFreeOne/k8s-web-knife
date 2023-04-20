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
 */
@Table(name = "tb_k8s_api_server_config")
@Entity
public class K8sApiServerConfig {

    private Long id;

    private String apiServerUrl;

    private String k8sSecret;

    private String clusterName;


    private Date createTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return this.id;
    }

    @Column(name = "api_server_url", nullable = false, columnDefinition = "varchar(2048)  comment 'k8s api_server路径'")
    public String getApiServerUrl() {
        return this.apiServerUrl;
    }
    @Column(name = "k8s_secret", nullable = false, columnDefinition = "varchar(4096)  comment 'k8s api_server路径'")
    public String getK8sSecret() {
        return this.k8sSecret;
    }

    @Column(name = "cluster_name", nullable = false, columnDefinition = "varchar(4096)  comment 'k8s api_server路径'")
    public String getClusterName() {
        return this.clusterName;
    }

    @CreatedDate
    @Column(name = "create_time", nullable = true, columnDefinition = "datetime(0)  comment '创建时间'")
    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setApiServerUrl(String apiServerUrl) {
        this.apiServerUrl = apiServerUrl;
    }

    public void setK8sSecret(String k8sSecret) {
        this.k8sSecret = k8sSecret;
    }
}
