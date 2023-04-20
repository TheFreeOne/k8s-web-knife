package org.freeone.k8s.web.knife.entity;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_docker_image_build_history")
@Entity
public class DockerImageBuildHistory {

    private Long id;

    private String description;

    private Date createTime;

    private String version;

    private Long k8sId;

    private String targetDeployment;

    private Byte buildStatus;

    private String imageId;

    private String imageTag;

    private String errInfo;

    private Long targetDockerId;

    private Long targetDockerfileId;

    /**
     * 目标文件的id
     */
    private Long tempFileId;

    /**
     * 实际上的dockers文件内容
     */
    private String actualDockerfileContent;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return this.id;
    }
    @CreatedDate
    @Column(name = "create_time", nullable = false, columnDefinition = "datetime(0)  comment '创建时间'")
    public Date getCreateTime() {
        return this.createTime;
    }

    @Column(name = "description", nullable = false, columnDefinition = "varchar(100) default '' comment '备注' ")
    public String getDescription() {
        return this.description;
    }

    @Column(name = "version", nullable = false, columnDefinition = "varchar(32) default '' comment '版本' ")
    public String getVersion() {
        return this.version;
    }
    @Column(name = "target_deployment", nullable = false, columnDefinition = "varchar(32) default '' comment '目标部署服务' ")
    public String getTargetDeployment() {
        return this.targetDeployment;
    }

    @Column(name = "build_status", nullable = false)
    public Byte getBuildStatus() {
        return this.buildStatus;
    }
    @Column(name = "image_id", nullable = false,  columnDefinition = "varchar(1024) default '' comment '构建成功后的镜像id' ")
    public String getImageId() {
        return this.imageId;
    }

    @Column(name = "image_tag", nullable = false,  columnDefinition = "varchar(1024) default '' comment '镜像tag,由目标deploy和version拼接而成' ")
    public String getImageTag() {
        return this.imageTag;
    }

    @Column(name = "err_info", nullable = false,  columnDefinition = "longtext    comment '构建失败时的错误信息' ")
    public String getErrInfo() {
        return this.errInfo;
    }

    @Column(name = "target_docker_id", nullable = false,  columnDefinition = "bigint  comment '构建镜像的时候使用的docker' ")
    public Long getTargetDockerId() {
        return this.targetDockerId;
    }

    @Column(name = "target_dockerfile_id", nullable = false,  columnDefinition = "bigint  comment '构架的时候使用的dockerfile' ")
    public Long getTargetDockerfileId() {
        return this.targetDockerfileId;
    }

    @Column(name = "temp_file_id", nullable = false,  columnDefinition = "bigint  comment '构架的时候使用的临时文件' ")
    public Long getTempFileId() {
        return this.tempFileId;
    }
    @Column(name = "actual_dockerfile_content", nullable = false,  columnDefinition = "longtext  comment '构建时实际使用的dockerfile的内容' ")
    public String getActualDockerfileContent() {
        return this.actualDockerfileContent;
    }
    @Column(name = "k8s_id", nullable = false,  columnDefinition = "bigint  comment 'deploy所属的k8sid' ")
    public Long getK8sId() {
        return this.k8sId;
    }

    public void setK8sId(Long k8sId) {
        this.k8sId = k8sId;
    }

    public void setActualDockerfileContent(String actualDockerfileContent) {
        this.actualDockerfileContent = actualDockerfileContent;
    }

    public void setTempFileId(Long tempFileId) {
        this.tempFileId = tempFileId;
    }

    public void setTargetDockerfileId(Long targetDockerfileId) {
        this.targetDockerfileId = targetDockerfileId;
    }

    public void setTargetDockerId(Long targetDockerId) {
        this.targetDockerId = targetDockerId;
    }

    public void setErrInfo(String errInfo) {
        this.errInfo = errInfo;
    }

    public void setImageTag(String imageTag) {
        this.imageTag = imageTag;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setBuildStatus(Byte buildStatus) {
        this.buildStatus = buildStatus;
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
