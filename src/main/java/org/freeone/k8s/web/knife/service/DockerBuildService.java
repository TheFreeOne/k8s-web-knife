package org.freeone.k8s.web.knife.service;

import io.kubernetes.client.extended.kubectl.exception.KubectlException;

public interface DockerBuildService {


    void buildImage(Long buildHistoryId);
    void deployImage(Long deployHistoryId)  ;
}
