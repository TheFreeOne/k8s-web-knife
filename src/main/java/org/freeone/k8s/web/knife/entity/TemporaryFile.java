package org.freeone.k8s.web.knife.entity;


import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_temp_file")
@Entity
public class TemporaryFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "testSeq")
    @SequenceGenerator(name = "testSeq", initialValue = 10_000, allocationSize = 1, sequenceName = "TEST_SEQUENCE")
    @Column(name = "id")
    private Long id;

    @Column(name = "file_path", nullable = false, columnDefinition = "varchar(2048) comment '绝对路径' ")
    private String filePath;

    @CreatedDate
    @Column(name = "create_time", nullable = false, columnDefinition = "datetime(0)  comment '创建时间'")
    private Date createTime;

    @Column(name = "ticket", nullable = false, columnDefinition = "varchar(256) comment '下载文件的票据'")
    private String ticket;


    public Long getId() {
        return id;
    }


    public String getFilePath() {
        return this.filePath;
    }


    public Date getCreateTime() {
        return this.createTime;
    }


    public String getTicket() {
        return this.ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
