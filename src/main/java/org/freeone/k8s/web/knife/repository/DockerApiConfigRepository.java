package org.freeone.k8s.web.knife.repository;

import org.freeone.k8s.web.knife.entity.DockerApiConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DockerApiConfigRepository extends JpaRepository<DockerApiConfig, Long>, JpaSpecificationExecutor<DockerApiConfig> {


}
