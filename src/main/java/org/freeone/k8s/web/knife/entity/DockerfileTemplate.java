package org.freeone.k8s.web.knife.entity;


import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_dockerfile_template")
@Entity
public class DockerfileTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "template_name", nullable = false, columnDefinition = "varchar(64)  comment '模板名称'")
    private String templateName;

    /**
     * 构建文件所属的docker api
     */
    @Column(name = "target_docker_id", nullable = false, columnDefinition = "bigint comment '所属的dockerId' ")
    private Long targetDockerId;

    @Column(name = "content",   columnDefinition = "longtext comment '模板文件的内容' ")
    private String content;

    @CreatedDate
    @Column(name = "create_time", nullable = false, columnDefinition = "datetime(0)  comment '创建时间'")
    private Date createTime;

    @Column(name = "is_readonly",   columnDefinition = "bit comment '是否只读,系统加的只读' ")
    private Boolean isReadonly;





    public Long getId() {
        return this.id;
    }


    public Long getTargetDockerId() {
        return this.targetDockerId;
    }

    public void setTargetDockerId(Long targetDockerId) {
        this.targetDockerId = targetDockerId;
    }


    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return this.createTime;
    }


    public String getTemplateName() {
        return this.templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getIsReadonly() {
        return this.isReadonly;
    }

    public void setIsReadonly(Boolean readonly) {
        this.isReadonly = readonly;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
