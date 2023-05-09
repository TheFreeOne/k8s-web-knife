package org.freeone.k8s.web.knife.entity;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_docker_image_deploy_history")
@Entity
public class DockerImageDeployHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description", nullable = false, columnDefinition = "varchar(100) default '' comment '备注' ")
    private String description;

    @Column(name = "k8s_id", nullable = false, columnDefinition = "bigint  comment 'deploy所属的k8sid' ")
    private Long k8sId;

    @Column(name = "target_deployment", nullable = false, columnDefinition = "varchar(32) default '' comment '目标部署服务' ")
    private String targetDeployment;

    @CreatedDate
    @Column(name = "create_time", nullable = false, columnDefinition = "datetime(0)  comment '创建时间'")
    private Date createTime;

    @Column(name = "version", nullable = false, columnDefinition = "varchar(32) default '' comment '版本' ")
    private String version;

    @Column(name = "deploy_status", nullable = false)
    private Byte deployStatus;

    @Column(name = "image_build_id", nullable = false, columnDefinition = "bigint  comment '本地docker镜像构建历史的id' ")
    private Long imageBuildId;

    @Column(name = "image_tag", nullable = false, columnDefinition = "varchar(1024) default '' comment '镜像tag,由目标deploy和version拼接而成' ")
    private String imageTag;

    @Column(name = "err_info", nullable = false, columnDefinition = "longtext    comment '构建失败时的错误信息' ")
    private String errInfo;


    public Long getId() {
        return this.id;
    }


    public Date getCreateTime() {
        return this.createTime;
    }


    public String getDescription() {
        return this.description;
    }


    public String getVersion() {
        return this.version;
    }

    public String getTargetDeployment() {
        return this.targetDeployment;
    }


    public String getImageTag() {
        return this.imageTag;
    }


    public String getErrInfo() {
        return this.errInfo;
    }


    public Byte getDeployStatus() {
        return this.deployStatus;
    }


    public Long getImageBuildId() {
        return this.imageBuildId;
    }


    public void setImageBuildId(Long imageBuildId) {
        this.imageBuildId = imageBuildId;
    }


    public Long getK8sId() {
        return this.k8sId;
    }

    public void setK8sId(Long k8sId) {
        this.k8sId = k8sId;
    }

    public void setDeployStatus(Byte deployStatus) {
        this.deployStatus = deployStatus;
    }

    public void setErrInfo(String errInfo) {
        this.errInfo = errInfo;
    }

    public void setImageTag(String imageTag) {
        this.imageTag = imageTag;
    }


    public void setTargetDeployment(String targetDeployment) {
        this.targetDeployment = targetDeployment;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
