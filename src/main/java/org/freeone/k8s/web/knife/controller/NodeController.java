package org.freeone.k8s.web.knife.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1Pod;
import org.apache.commons.lang3.StringUtils;
import org.freeone.k8s.web.knife.entity.vo.NodeVo;
import org.freeone.k8s.web.knife.entity.vo.PodRequestLimitsInfo;
import org.freeone.k8s.web.knife.utils.K8sUtils;
import org.freeone.k8s.web.knife.utils.NodeUtils;
import org.freeone.k8s.web.knife.utils.PodUtils;
import org.freeone.k8s.web.knife.utils.ResultKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/node")
public class NodeController {

    Logger logger = LoggerFactory.getLogger(NodeController.class);

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping("/list")
    public ResultKit nodeList(@RequestParam Long k8sId, String version) throws KubectlException, JsonProcessingException {
        List<NodeVo> nodeVoList = new ArrayList<>();
        if(StringUtils.isNoneBlank(version)) {
            KubernetesClient kubernetesClient = K8sUtils.k8sClient(k8sId);
            NodeList list = kubernetesClient.nodes().list();

            List<Node> items = list.getItems();
            for (Node item : items) {
                // 看json 和V1Node的结果一样，都是接口返回的json，设置都可以互转
                String json = objectMapper.writeValueAsString(item);
                V1Node v1Node = K8sUtils.apiClient(k8sId).getJSON().deserialize(json, V1Node.class);
                NodeVo nodeVo = NodeUtils.v1Node2NodeVo(v1Node);
                nodeVoList.add(nodeVo);
            }
            return ResultKit.okWithData(nodeVoList);
        }else {
            ApiClient apiClient = K8sUtils.apiClient(k8sId);
            List<V1Node> v1NodeList = Kubectl.get(V1Node.class).apiClient(apiClient).execute();


            for (V1Node v1Node : v1NodeList) {
                NodeVo nodeVo = NodeUtils.v1Node2NodeVo(v1Node);
                nodeVoList.add(nodeVo);
            }
            return ResultKit.okWithData(nodeVoList);
        }

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
