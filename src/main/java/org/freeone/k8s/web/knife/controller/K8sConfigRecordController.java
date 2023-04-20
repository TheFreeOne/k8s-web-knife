package org.freeone.k8s.web.knife.controller;

import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.models.V1Node;
import org.apache.commons.lang3.StringUtils;
import org.freeone.k8s.web.knife.entity.K8sApiServerConfig;
import org.freeone.k8s.web.knife.repository.K8sApiServerConfigRepository;
import org.freeone.k8s.web.knife.utils.K8sUtils;
import org.freeone.k8s.web.knife.utils.ResultKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/k8s/record")
public class K8sConfigRecordController {

    @Autowired
    private K8sApiServerConfigRepository k8sConfigRecordRepository;


    @RequestMapping("/add")
    public ResultKit add(@RequestParam String k8sApiServerUrl, @RequestParam String k8sSecret, @RequestParam String clusterName) throws KubectlException {
        List<V1Node> nodes = Kubectl.get(V1Node.class).apiClient(K8sUtils.apiClient(k8sApiServerUrl, k8sSecret)).execute();
        if (StringUtils.isBlank(clusterName)) {
        }
        K8sApiServerConfig k8sRecord = new K8sApiServerConfig();
        k8sRecord.setApiServerUrl(k8sApiServerUrl);
        if (!k8sSecret.startsWith("Bearer ")) {
            k8sSecret = "Bearer " + k8sSecret;
        }
        k8sRecord.setK8sSecret(k8sSecret);
        k8sRecord.setClusterName(clusterName);
        k8sConfigRecordRepository.save(k8sRecord);
        return ResultKit.okWithData(k8sRecord.getId());
    }

    @RequestMapping("/get")
    public ResultKit get(@RequestParam Long k8sId) {
        K8sApiServerConfig k8sRecord = k8sConfigRecordRepository.findById(k8sId).orElse(null);
        return ResultKit.okWithData(k8sRecord);
    }

    @RequestMapping("/update")
    public ResultKit update(@RequestParam Long id, @RequestParam String apiServerUrl, @RequestParam String k8sSecret, @RequestParam String clusterName) {
        K8sApiServerConfig k8sRecord = k8sConfigRecordRepository.findById(id).orElse(null);
        if (k8sRecord == null) {
            return ResultKit.failed("target is not existed");
        }
        k8sRecord.setK8sSecret(k8sSecret);
        k8sRecord.setClusterName(clusterName);
        k8sRecord.setApiServerUrl(apiServerUrl);
        k8sConfigRecordRepository.save(k8sRecord);
        return ResultKit.okWithData(k8sRecord);
    }
   @RequestMapping("/delete")
    public ResultKit delete(@RequestParam Long id) {
        K8sApiServerConfig k8sRecord = k8sConfigRecordRepository.findById(id).orElse(null);
        if(k8sRecord != null) {
            k8sConfigRecordRepository.deleteById(id);
        }
        return ResultKit.ok();
    }

    @RequestMapping("/list")
    public ResultKit list() {
        List<K8sApiServerConfig> list = k8sConfigRecordRepository.findAll();
        for (K8sApiServerConfig k8sRecord : list) {
            k8sRecord.setK8sSecret("");
        }
        return ResultKit.okWithData(list);
    }
}
