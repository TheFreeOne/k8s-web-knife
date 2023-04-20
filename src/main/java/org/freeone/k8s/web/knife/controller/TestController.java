package org.freeone.k8s.web.knife.controller;


import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import org.freeone.k8s.web.knife.entity.DeploymentDTO;
import org.freeone.k8s.web.knife.sdk.TestRequest;
import org.freeone.k8s.web.knife.utils.ResultKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private ApiClient defaultApiClient;

    @RequestMapping("/foo.htm")
    private ModelAndView foo(ModelAndView mv) {
        mv.setViewName("/foo");
        mv.addObject("uuid", "asdfasdf");
        return mv;
    }

    @RequestMapping("/testReuqest")
    public ResultKit testRequest(@RequestBody TestRequest request) {
        return ResultKit.okWithData(request);
    }

    @RequestMapping("/create/namespace")
    public Map<String, String> testNamespace(@RequestBody V1Namespace namespace) {
        Map<String, String> message = new HashMap<>();
        //初始化k8s
        CoreV1Api coreV1Api = new CoreV1Api(defaultApiClient);

        try {
            V1Namespace result = coreV1Api.createNamespace(namespace, null, null, null, null);
            message.put("success", "应用命名空间创建成功！");
        } catch (ApiException e) {
            if (e.getCode() == 409) {
                message.put("error", "命名空间已重复！");
            }
            if (e.getCode() == 200) {
                message.put("success", "应用命名空间创建成功！");
            }
            if (e.getCode() == 201) {
                message.put("error", "命名空间已重复！");
            }
            if (e.getCode() == 401) {
                message.put("error", "无权限操作！");
            }
            message.put("error", "应用命名空间创建失败！");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @PostMapping("/create/deployments")
    public Map<String, String> createDeployments(@RequestBody DeploymentDTO deploymentDTO) {
        Map<String, String> messages = new HashMap<>();

        // 赋值操作
        V1Deployment deployment = new V1Deployment();
        deployment.setApiVersion("apps/v1");
        deployment.kind("Deployment");

        // 赋值metadata
        V1ObjectMeta objectMeta = new V1ObjectMeta();
        objectMeta.name(deploymentDTO.getMetadata_name());
        objectMeta.namespace(deploymentDTO.getMetadata_namespace());
        Map<String, String> labels = new HashMap<>();
        labels.put("app", "nginx");
        objectMeta.labels(labels);

        // 赋值spec
        V1DeploymentSpec deploymentSpec = new V1DeploymentSpec();

        //赋值spec-selector
        V1LabelSelector selector = new V1LabelSelector();
        Map<String, String> matchLabels = new HashMap<>();
        matchLabels.put("app", "nginx");
        selector.matchLabels(matchLabels);

        //赋值template
        V1PodTemplateSpec templateSpec = new V1PodTemplateSpec();
        V1ObjectMeta metadata = new V1ObjectMeta();
        metadata.labels(labels);
        templateSpec.metadata(metadata);

        // spec-Template下的Spec
        V1PodSpec podSpec = new V1PodSpec();
        // spec-Template-spec-container
        List<V1Container> listContainer = new ArrayList<>();
        V1Container container = new V1Container();
        container.setName(deploymentDTO.getContainers_name());
        container.setImage(deploymentDTO.getContainers_image());
        container.setImagePullPolicy(deploymentDTO.getContainers_imagePullPolicy());

        // spec-Template-spec-container-ports
        List<V1ContainerPort> ports = new ArrayList<>();
        V1ContainerPort v1ContainerPort = new V1ContainerPort();
        v1ContainerPort.setContainerPort(88);
        container.setPorts(ports);

        listContainer.add(container);
        podSpec.setContainers(listContainer);
        templateSpec.setSpec(podSpec);


        deploymentSpec.setReplicas(deploymentDTO.getSpec_replicas());
        deploymentSpec.setTemplate(templateSpec);
        deploymentSpec.setSelector(selector);
        deployment.setMetadata(objectMeta);
        deployment.setSpec(deploymentSpec);


        AppsV1Api apiInstance = new AppsV1Api(defaultApiClient);
        try {

            V1Deployment result = apiInstance.createNamespacedDeployment(objectMeta.getNamespace(), deployment, null, null, null, null);
            messages.put("success", "工作负载创建成功！");
        } catch (ApiException e) {
            if (e.getCode() == 409) {
                messages.put("error", "工作负载创建已重复！");
            } else if (e.getCode() == 200) {
                messages.put("success", "工作负载创建成功！");
            } else if (e.getCode() == 201) {
                messages.put("error", "工作负载创建已重复！");
            } else if (e.getCode() == 401) {
                messages.put("error", "无权限操作！");
            } else {
                messages.put("error", "工作负载创建失败！");
            }
        }
        return messages;
    }

}
