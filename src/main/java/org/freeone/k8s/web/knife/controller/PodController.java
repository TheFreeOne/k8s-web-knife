package org.freeone.k8s.web.knife.controller;

import com.google.common.io.ByteStreams;
import io.kubernetes.client.Exec;
import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.EventsV1Event;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Namespaces;
import org.apache.commons.lang3.StringUtils;
import org.freeone.k8s.web.knife.entity.vo.ContainerVo;
import org.freeone.k8s.web.knife.entity.vo.EventVo;
import org.freeone.k8s.web.knife.entity.vo.PodVo;
import org.freeone.k8s.web.knife.utils.K8sUtils;
import org.freeone.k8s.web.knife.utils.PodUtils;
import org.freeone.k8s.web.knife.utils.ResultKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
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
    public ResultKit logs(@PathVariable("namespace") String namespace, @PathVariable("name") String name, @RequestParam Long k8sId, String containerName, HttpServletResponse response) throws Exception {
        ApiClient apiClient = K8sUtils.fasterApiClient(k8sId);
        CoreV1Api coreV1Api = K8sUtils.coreV1Api(apiClient);
        V1Pod pod = Kubectl.get(V1Pod.class).name(name).namespace(namespace).apiClient(apiClient).execute();
        if (StringUtils.isBlank(containerName)) {
            containerName = pod.getSpec().getContainers().get(0).getName();
        }
        String logs = coreV1Api.readNamespacedPodLog(name, namespace, containerName, false, false, null, null, null, null, 1000, null);
        return ResultKit.okWithData(logs);
    }


    @RequestMapping("/exec/{namespace}/{name}")
//    @ResponseBody
    public void exec(@PathVariable("namespace") String namespace, @PathVariable("name") String name, @RequestParam Long k8sId
//        ,@RequestParam String[] command
            , HttpServletResponse response) throws Exception {
        ApiClient apiClient1 = K8sUtils.apiClient(k8sId);
//        String name = "mytomcat-deployment-6695dbdd78-899xj";
        String container = "mytomcat";
//        String[] command = new String[]{"ls","-lh"};
        String[] command = new String[]{"tail", "-200", "/usr/local/tomcat/logs/localhost_access_log.2023-04-20.txt"};
        boolean stdin = false;
        boolean tty = false;

        V1Pod pod = new V1Pod().metadata(new V1ObjectMeta().name(name).namespace(Namespaces.NAMESPACE_DEFAULT));

        Exec exec = new Exec(apiClient1);
        ServletOutputStream outputStream = response.getOutputStream();
        try {
            Process proc = exec.exec(pod, command, container, stdin, tty);
            copyAsync(proc.getInputStream(), outputStream);
            copyAsync(proc.getErrorStream(), outputStream);
            if (stdin) {
                copyAsync(System.in, proc.getOutputStream());
            }
            proc.waitFor();
        } catch (InterruptedException | ApiException | IOException ex) {
            throw new KubectlException(ex);
        }
//        return ResultKit.okWithData("");
    }


    protected static Thread copyAsync(InputStream in, OutputStream out) {
        Thread t =
                new Thread(
                        new Runnable() {
                            public void run() {
                                try {
                                    ByteStreams.copy(in, out);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
        t.start();
        return t;
    }


}
