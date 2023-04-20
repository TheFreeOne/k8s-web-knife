package org.freeone.k8s.web.knife.repository;

import org.freeone.k8s.web.knife.entity.DockerfileTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DockerfileTemplateRepository extends JpaRepository<DockerfileTemplate, Long>, JpaSpecificationExecutor<DockerfileTemplate> {


    List<DockerfileTemplate> findAllByTargetDockerIdOrderByIdDesc(Long dockerId);
}
