package org.freeone.k8s.web.knife.repository;

import org.freeone.k8s.web.knife.entity.K8sApiServerConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface K8sApiServerConfigRepository extends JpaRepository<K8sApiServerConfig, Long>, JpaSpecificationExecutor<K8sApiServerConfig> {


}
