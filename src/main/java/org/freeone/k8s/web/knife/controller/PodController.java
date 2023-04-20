package org.freeone.k8s.web.knife.controller;

import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.EventsV1Event;
import io.kubernetes.client.openapi.models.V1Pod;
import org.freeone.k8s.web.knife.config.websocket.SSEServer;
import org.freeone.k8s.web.knife.entity.vo.ContainerVo;
import org.freeone.k8s.web.knife.entity.vo.EventVo;
import org.freeone.k8s.web.knife.entity.vo.PodVo;
import org.freeone.k8s.web.knife.utils.K8sUtils;
import org.freeone.k8s.web.knife.utils.PodUtils;
import org.freeone.k8s.web.knife.utils.ResultKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/pod")
public class PodController {

    Logger logger = LoggerFactory.getLogger(PodController.class);

    @RequestMapping("/get/{namespace}/{name}")
    @ResponseBody
    public ResultKit get(@PathVariable("namespace") String namespace, @PathVariable("name") String name, @RequestParam Long k8sId) throws Exception {
        ApiClient apiClient = K8sUtils.apiClient(k8sId);
        V1Pod pod = Kubectl.get(V1Pod.class).name(name).namespace(namespace).apiClient(apiClient).execute();
        PodVo podVo = PodUtils.v1Pod2PodVo(pod);
        List<EventsV1Event> events = Kubectl.get(EventsV1Event.class).namespace(namespace).apiClient(apiClient).execute();
        List<EventVo> eventV1Events = PodUtils.findEventVos(pod, events);
        podVo.setEvents(eventV1Events);
        List<ContainerVo> containers = PodUtils.findContainers(pod);
        podVo.setContainers(containers);
        return ResultKit.okWithData(podVo);
    }

    private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    @RequestMapping("/logs/{namespace}/{name}")
    @ResponseBody
    public ResultKit logs(@PathVariable("namespace") String namespace, @PathVariable("name") String name, @RequestParam Long k8sId, HttpServletResponse response) throws Exception {
        ApiClient apiClient = K8sUtils.fasterApiClient(k8sId);
        CoreV1Api coreV1Api = K8sUtils.coreV1Api(apiClient);
        V1Pod pod = Kubectl.get(V1Pod.class).name(name).namespace(namespace).apiClient(apiClient).execute();
        String containerName = pod.getSpec().getContainers().get(0).getName();
        String logs = coreV1Api.readNamespacedPodLog(name, namespace, containerName, false, false, null, null, null, null, 100, null);
        return ResultKit.okWithData(logs);
    }


}
