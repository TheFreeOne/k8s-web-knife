package org.freeone.k8s.web.knife.controller;

import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.EventsV1Api;
import io.kubernetes.client.openapi.models.CoreV1Event;
import io.kubernetes.client.openapi.models.CoreV1EventList;
import io.kubernetes.client.openapi.models.EventsV1Event;
import io.kubernetes.client.openapi.models.EventsV1EventList;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Namespaces;
import org.freeone.k8s.web.knife.entity.vo.EventVo;
import org.freeone.k8s.web.knife.utils.K8sUtils;
import org.freeone.k8s.web.knife.utils.ResultKit;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/event")
public class EventController {


    @RequestMapping("/list")
    public ResultKit list(@RequestParam(defaultValue = "default") String namespace, @RequestParam Long k8sId) throws Exception {
        ApiClient apiClient1 = K8sUtils.apiClient(k8sId);
        EventsV1Api eventsV1Api = K8sUtils.eventsV1Api(apiClient1);
        EventsV1EventList eventsV1EventList = eventsV1Api.listNamespacedEvent(namespace, null, null, null, null, null, null, null, null, null, null);
        List<EventsV1Event> items = eventsV1EventList.getItems();
        System.out.println(eventsV1EventList);
        return ResultKit.ok();
    }

    @RequestMapping("/list/{namespace}/pod/{podName}")
    public ResultKit listByPod(@PathVariable String namespace, @PathVariable String podName, @RequestParam Long k8sId) throws Exception {
        List<EventVo> eventVoList = new ArrayList<>();
        ApiClient apiClient1 = K8sUtils.apiClient(k8sId);
        CoreV1Api coreV1Api = K8sUtils.coreV1Api(apiClient1);

        // 一样的效果
        CoreV1EventList aTrue = coreV1Api.listNamespacedEvent(Namespaces.NAMESPACE_DEFAULT, "true", null, null, null, null, null, null, null, null, null);

        List<CoreV1Event> eventList = aTrue.getItems();
        List<CoreV1Event> targetEventList = eventList.stream().filter(event -> event.getInvolvedObject().getName().equals(podName)).collect(Collectors.toList());

        for (CoreV1Event coreV1Event : targetEventList) {
            String type = coreV1Event.getType();
            String reason = coreV1Event.getReason();
            OffsetDateTime firstTimestamp = coreV1Event.getFirstTimestamp();
            OffsetDateTime lastTimestamp = coreV1Event.getLastTimestamp();


            // 类似面板中的from
            String component = coreV1Event.getSource().getComponent();


            String message = coreV1Event.getMessage();
            EventVo eventVo = new EventVo();
            eventVo.setFrom(component);
            eventVo.setMessage(message);
            eventVo.setType(type);
            eventVo.setReason(reason);
            if (firstTimestamp != null) {
                Date from = Date.from(firstTimestamp.atZoneSameInstant(ZoneId.systemDefault()).toInstant());
                eventVo.setFirstDate(from);
            }
            if (lastTimestamp != null) {
                Date from = Date.from(lastTimestamp.atZoneSameInstant(ZoneId.systemDefault()).toInstant());
                eventVo.setFirstDate(from);
            }


            eventVoList.add(eventVo);
        }

        return ResultKit.okWithData(eventVoList);

    }

    @RequestMapping("/list/{namespace}/depoly/{deployName}")
    public ResultKit listByDeploy(@PathVariable String namespace, @PathVariable String deployName, @RequestParam Long k8sId) throws Exception {

        ApiClient apiClient = K8sUtils.apiClient(k8sId);
        CoreV1Api coreV1Api = K8sUtils.coreV1Api(apiClient);

        List<EventVo> eventVoList = new ArrayList<>();

        V1Deployment deployment = Kubectl.get(V1Deployment.class).namespace(namespace).name(deployName).apiClient(apiClient).execute();


        // 一样的效果
        CoreV1EventList aTrue = coreV1Api.listNamespacedEvent(Namespaces.NAMESPACE_DEFAULT, "true", null, null, null, null, null, null, null, null, null);

        List<CoreV1Event> eventList = aTrue.getItems();
        List<CoreV1Event> targetEventList = eventList.stream().filter(event -> event.getInvolvedObject().getName().equals(deployName)).collect(Collectors.toList());

        for (CoreV1Event coreV1Event : targetEventList) {
            String type = coreV1Event.getType();
            String reason = coreV1Event.getReason();
            OffsetDateTime firstTimestamp = coreV1Event.getFirstTimestamp();
            OffsetDateTime lastTimestamp = coreV1Event.getLastTimestamp();


            // 类似面板中的from
            String component = coreV1Event.getSource().getComponent();


            String message = coreV1Event.getMessage();
            EventVo eventVo = new EventVo();
            eventVo.setFrom(component);
            eventVo.setMessage(message);
            eventVo.setType(type);
            eventVo.setReason(reason);
            if (firstTimestamp != null) {
                Date from = Date.from(firstTimestamp.atZoneSameInstant(ZoneId.systemDefault()).toInstant());
                eventVo.setFirstDate(from);
            }
            if (lastTimestamp != null) {
                Date from = Date.from(lastTimestamp.atZoneSameInstant(ZoneId.systemDefault()).toInstant());
                eventVo.setFirstDate(from);
            }


            eventVoList.add(eventVo);
        }

        return ResultKit.okWithData(eventVoList);

    }

}
