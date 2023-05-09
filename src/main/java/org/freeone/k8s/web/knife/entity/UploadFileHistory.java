package org.freeone.k8s.web.knife.entity;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_upload_file_history")
@Entity
public class UploadFileHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "upload_path", nullable = false, columnDefinition = "varchar(1024) comment '文件保存的路径'")
    private String uploadPath;

    @CreatedDate
    @Column(name = "create_time", nullable = false, columnDefinition = "datetime(0)  comment '创建时间'")
    private Date createTime;


    public Long getId() {
        return this.id;
    }


    public String getUploadPath() {
        return this.uploadPath;
    }





    public Date getCreateTime() {
        return this.createTime;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
