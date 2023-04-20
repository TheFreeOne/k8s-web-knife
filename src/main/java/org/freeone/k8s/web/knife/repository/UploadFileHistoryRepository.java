package org.freeone.k8s.web.knife.repository;

import org.freeone.k8s.web.knife.entity.DockerImageBuildHistory;
import org.freeone.k8s.web.knife.entity.UploadFileHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadFileHistoryRepository extends JpaRepository<UploadFileHistory, Long>, JpaSpecificationExecutor<UploadFileHistory> {


}
