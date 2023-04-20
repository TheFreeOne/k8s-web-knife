package org.freeone.k8s.web.knife.repository;

import org.freeone.k8s.web.knife.entity.DockerImageBuildHistory;
import org.freeone.k8s.web.knife.entity.DockerImageDeployHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DockerImageDeployHistoryRepository extends JpaRepository<DockerImageDeployHistory, Long>, JpaSpecificationExecutor<DockerImageDeployHistory> {


}
