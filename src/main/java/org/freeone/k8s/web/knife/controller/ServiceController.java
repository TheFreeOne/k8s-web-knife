package org.freeone.k8s.web.knife.controller;

import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.util.Namespaces;
import org.freeone.k8s.web.knife.entity.vo.ServiceVo;
import org.freeone.k8s.web.knife.utils.K8sUtils;
import org.freeone.k8s.web.knife.utils.ResultKit;
import org.freeone.k8s.web.knife.utils.ServiceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * 管理k8s的service
 */
@RestController
@RequestMapping("/service")
public class ServiceController {

    @RequestMapping("/list")
    public ResultKit list(@RequestParam Long k8sId, @RequestParam(defaultValue = Namespaces.NAMESPACE_DEFAULT) String namespace) throws KubectlException {
        ApiClient apiClient = K8sUtils.apiClient(k8sId);
        List<V1Service> execute = Kubectl.get(V1Service.class).namespace(namespace).apiClient(apiClient).execute();

        List<ServiceVo> list = new ArrayList<>();
        for (V1Service v1Service : execute) {
            ServiceVo serviceVo = ServiceUtils.v1Service2ServiceVo(v1Service);
            list.add(serviceVo);
        }
        return ResultKit.okWithData(list);
    }


    @RequestMapping("/add")
    public ResultKit add(@RequestParam Long k8sId, @RequestParam(defaultValue = Namespaces.NAMESPACE_DEFAULT) String namespace, @RequestParam String json) throws KubectlException {
        ApiClient apiClient = K8sUtils.apiClient(k8sId);
        V1Service v1Service = apiClient.getJSON().deserialize(json, V1Service.class);

        V1Service out =
                Kubectl.apply(V1Service.class).namespace(namespace).forceConflict(true).apiClient(apiClient).resource(v1Service).execute();
        return ResultKit.okWithData(json);

    }


    @RequestMapping("/delete")
    public ResultKit delete(@RequestParam Long k8sId, @RequestParam(defaultValue = Namespaces.NAMESPACE_DEFAULT) String namespace, @RequestParam String name) throws KubectlException {
        ApiClient apiClient = K8sUtils.apiClient(k8sId);
        V1Service v1Service = Kubectl.get(V1Service.class).namespace(namespace).name(name).apiClient(apiClient).execute();
        ServiceVo serviceVo = ServiceUtils.v1Service2ServiceVo(v1Service);
        if (serviceVo.getSelector() == null || serviceVo.getSelector().keySet().size() == 0) {
            return ResultKit.failed("不可删除");
        }
        Kubectl.delete(V1Service.class).namespace(namespace).name(name).apiClient(apiClient).execute();
        return ResultKit.ok();
    }


}
