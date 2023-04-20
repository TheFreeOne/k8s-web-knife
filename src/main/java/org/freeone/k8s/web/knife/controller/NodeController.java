package org.freeone.k8s.web.knife.controller;

import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1Pod;
import org.freeone.k8s.web.knife.entity.vo.NodeVo;
import org.freeone.k8s.web.knife.entity.vo.PodRequestLimitsInfo;
import org.freeone.k8s.web.knife.utils.K8sUtils;
import org.freeone.k8s.web.knife.utils.NodeUtils;
import org.freeone.k8s.web.knife.utils.PodUtils;
import org.freeone.k8s.web.knife.utils.ResultKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/node")
public class NodeController {

    Logger logger = LoggerFactory.getLogger(NodeController.class);

    @RequestMapping("/list")
    public ResultKit nodeList(@RequestParam Long k8sId) throws KubectlException {
        ApiClient apiClient = K8sUtils.apiClient(k8sId);
        List<V1Node> v1NodeList = Kubectl.get(V1Node.class).apiClient(apiClient).execute();

        List<NodeVo> nodeVoList = new ArrayList<>();
        for (V1Node v1Node : v1NodeList) {

            NodeVo nodeVo = NodeUtils.v1Node2NodeVo(v1Node);
            nodeVoList.add(nodeVo);
        }
        return ResultKit.okWithData(nodeVoList);
    }

    @RequestMapping("/get")
    public ResultKit get(@RequestParam Long k8sId, @RequestParam String name) throws KubectlException {
        ApiClient apiClient = K8sUtils.apiClient(k8sId);

        V1Node v1Node = Kubectl.get(V1Node.class).name(name).apiClient(apiClient).execute();

        NodeVo nodeVo = NodeUtils.v1Node2NodeVo(v1Node);

        List<V1Pod> v1PodList = Kubectl.get(V1Pod.class).apiClient(apiClient).execute();

        List<PodRequestLimitsInfo> podRequestLimitsInfoList = new ArrayList<>();
        if (v1PodList != null) {
            for (V1Pod pod : v1PodList) {
                PodRequestLimitsInfo podRequestLimitsInfo = PodUtils.v1Pod2PodRequestLimitsInfo(pod);
                podRequestLimitsInfoList.add(podRequestLimitsInfo);
            }
        }

        nodeVo.setPodRequestLimitsInfoList(podRequestLimitsInfoList);


        return ResultKit.okWithData(nodeVo);
    }


}
