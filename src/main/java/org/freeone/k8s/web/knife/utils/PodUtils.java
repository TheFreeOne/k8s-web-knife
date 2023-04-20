package org.freeone.k8s.web.knife.utils;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.CoreV1Event;
import io.kubernetes.client.openapi.models.EventsV1Event;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerState;
import io.kubernetes.client.openapi.models.V1ContainerStateRunning;
import io.kubernetes.client.openapi.models.V1ContainerStateTerminated;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import io.kubernetes.client.openapi.models.V1HTTPGetAction;
import io.kubernetes.client.openapi.models.V1HTTPHeader;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1ObjectReference;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodCondition;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodStatus;
import io.kubernetes.client.openapi.models.V1Probe;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import org.freeone.k8s.web.knife.entity.vo.ContainerVo;
import org.freeone.k8s.web.knife.entity.vo.EventVo;
import org.freeone.k8s.web.knife.entity.vo.HTTPGetActionVo;
import org.freeone.k8s.web.knife.entity.vo.PodConditionVo;
import org.freeone.k8s.web.knife.entity.vo.PodRequestLimitsInfo;
import org.freeone.k8s.web.knife.entity.vo.PodVo;
import org.freeone.k8s.web.knife.entity.vo.ProbeVo;
import org.freeone.k8s.web.knife.entity.vo.QuantityVo;
import org.freeone.k8s.web.knife.entity.vo.VolumeMountVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PodUtils extends DeploymentUtils {

    static Logger logger = LoggerFactory.getLogger(PodUtils.class);

    public static final PodVo v1Pod2PodVo(V1Pod pod) {
//        logger.info("pod = {}", pod);
        V1ObjectMeta metadata = pod.getMetadata();
        V1PodStatus status = pod.getStatus();

        String podName = metadata.getName();
        String uid = metadata.getUid();
        Map<String, String> labels = metadata.getLabels();
        String namespace = metadata.getNamespace();

        String resourceVersion = metadata.getResourceVersion();

        List<V1ContainerStatus> containerStatuses = status.getContainerStatuses();
        Integer restartCount = 0;
        Boolean ready = null;
        V1ContainerStatus v1ContainerStatus = null;
        OffsetDateTime startedAt = null;
        OffsetDateTime offsetDateTime = null;

        if (containerStatuses != null) {
            v1ContainerStatus = containerStatuses.get(0);
            restartCount = v1ContainerStatus.getRestartCount();
            ready = v1ContainerStatus.getReady();

            V1ContainerState state = v1ContainerStatus.getState();
            V1ContainerStateRunning running = state.getRunning();

            if (running != null) {
                startedAt = running.getStartedAt();
                ZonedDateTime zonedDateTime = startedAt.atZoneSameInstant(ZoneId.systemDefault());
                offsetDateTime = zonedDateTime.toOffsetDateTime();

            }
        }

        // ready or pending
        String podPhase = status.getPhase();
        String podHostIP = status.getHostIP();
        String podIP = status.getPodIP();
        String qosClass = status.getQosClass();

        // conditions

        List<PodConditionVo> podConditionList = findPodConditionList(pod);


        PodVo podVo = new PodVo(podName, podPhase, podHostIP, podIP, restartCount, offsetDateTime, ready);
        if (ready != null && ready == false) {
            V1ContainerState lastState = v1ContainerStatus.getLastState();
            V1ContainerStateTerminated terminated = lastState.getTerminated();
            String reason = "";
            String message = "";
            if (terminated != null) {
                reason = terminated.getReason();
                message = terminated.getMessage();
            }

            podVo.setReason(reason);
            podVo.setMessage(message);
        }

        Date createTime = offsetDateTime2Date(offsetDateTime);
        podVo.setCreateAt(createTime);
        podVo.setResourceVersion(resourceVersion);
        podVo.setUid(uid);
        podVo.setQosClass(qosClass);
        podVo.setConditions(podConditionList);
        podVo.setLabels(labels);
        podVo.setNamespace(namespace);

        return podVo;
    }


    public static final List<PodConditionVo> findPodConditionList(V1Pod v1Pod) {
        List<PodConditionVo> list = new ArrayList<>();
        V1PodStatus status = v1Pod.getStatus();
        if (status != null) {

            List<V1PodCondition> conditions = status.getConditions();
            if (conditions != null) {
                for (V1PodCondition condition : conditions) {
                    PodConditionVo deploymentConditionVo = new PodConditionVo();
                    deploymentConditionVo.setType(condition.getType());
                    deploymentConditionVo.setStatus(condition.getStatus());
                    deploymentConditionVo.setReason(condition.getReason());
                    deploymentConditionVo.setMessage(condition.getMessage());
                    deploymentConditionVo.setLastUpdateTime(condition.getLastProbeTime());
                    deploymentConditionVo.setLastTransitionTime(condition.getLastTransitionTime());

                    list.add(deploymentConditionVo);
                }
            }
        }

        return list;
    }

    public static final List<EventVo> findEventVos(V1Pod v1Pod, List<EventsV1Event> events) {
        String uid = v1Pod.getMetadata().getUid();

        List<EventVo> eventVoList = new ArrayList<>();

        for (EventsV1Event event : events) {
            logger.info("event = {}", event);
            V1ObjectReference regarding = event.getRegarding();
            String kind = regarding.getKind();
            if ("Pod".equals(kind)) {
                String regardingUid = regarding.getUid();
                if (regardingUid.equals(uid)) {
                    V1ObjectMeta eventMetadata = event.getMetadata();
                    String type = event.getType();
                    String reason = event.getReason();

                    OffsetDateTime creationTimestamp = eventMetadata.getCreationTimestamp();
                    Date createAt = offsetDateTime2Date(creationTimestamp);
                    String namespace = eventMetadata.getNamespace();
                    String name = eventMetadata.getName();


                    // 类似面板中的from
//                    String component = event.get().getComponent();


                    String message = event.getNote();
                    EventVo eventVo = new EventVo();
//                    eventVo.setFrom(component);
                    eventVo.setMessage(message);
                    eventVo.setType(type);
                    eventVo.setReason(reason);
                    eventVo.setName(name);
                    eventVo.setNamespace(namespace);
                    eventVo.setCreateAt(createAt);

                    eventVoList.add(eventVo);
                }
            }
        }
        return eventVoList;
    }

    public static final List<EventVo> findEvents(V1Pod v1Pod, List<CoreV1Event> events) {
        String uid = v1Pod.getMetadata().getUid();

        List<EventVo> eventVoList = new ArrayList<>();

        for (CoreV1Event event : events) {
            logger.info("event = {}", event);
            V1ObjectReference regarding = event.getInvolvedObject();
            String kind = regarding.getKind();
            if ("Pod".equals(kind)) {
                String regardingUid = regarding.getUid();
                if (regardingUid.equals(uid)) {
                    V1ObjectMeta eventMetadata = event.getMetadata();
                    String type = event.getType();
                    String reason = event.getReason();
                    Integer count = event.getCount();
                    OffsetDateTime firstTimestamp = event.getFirstTimestamp();
                    Date firstDate = offsetDateTime2Date(firstTimestamp);
                    OffsetDateTime lastTimestamp = event.getLastTimestamp();
                    Date lastDate = offsetDateTime2Date(lastTimestamp);

                    String namespace = eventMetadata.getNamespace();
                    String name = eventMetadata.getName();


                    // 类似面板中的from
//                    String component = event.get().getComponent();


                    String message = event.getMessage();
                    EventVo eventVo = new EventVo();
//                    eventVo.setFrom(component);
                    eventVo.setMessage(message);
                    eventVo.setType(type);
                    eventVo.setReason(reason);
                    eventVo.setName(name);
                    eventVo.setNamespace(namespace);
                    eventVo.setFirstDate(firstDate);
                    eventVo.setLastDate(lastDate);
                    eventVoList.add(eventVo);
                }
            }
        }
        return eventVoList;
    }

    public static final List<ContainerVo> findContainers(V1Pod pod) {
        List<ContainerVo> list = new ArrayList<>();
        List<V1Container> containers = pod.getSpec().getContainers();
        List<V1ContainerStatus> containerStatuses = pod.getStatus().getContainerStatuses();
        for (int i = 0; i < containers.size(); i++) {
            ContainerVo containerVo = new ContainerVo();
            V1Container container = containers.get(i);
            if (containerStatuses != null) {
                V1ContainerStatus v1ContainerStatus = containerStatuses.get(i);
                if (v1ContainerStatus != null) {
                    Boolean ready = v1ContainerStatus.getReady();
                    Integer restartCount = v1ContainerStatus.getRestartCount();
                    Boolean started = v1ContainerStatus.getStarted();
                    containerVo.setReady(ready);
                    containerVo.setRestartCount(restartCount);
                    containerVo.setStarted(started);
                }
            }

            String image = container.getImage();
            V1Probe readinessProbe = container.getReadinessProbe();
            if (readinessProbe != null) {

                Integer initialDelaySeconds = readinessProbe.getInitialDelaySeconds();
                Integer failureThreshold = readinessProbe.getFailureThreshold();
                Integer periodSeconds = readinessProbe.getPeriodSeconds();
                Integer successThreshold = readinessProbe.getSuccessThreshold();
                Integer timeoutSeconds = readinessProbe.getTimeoutSeconds();
                Long terminationGracePeriodSeconds = readinessProbe.getTerminationGracePeriodSeconds();
                V1HTTPGetAction httpGet = readinessProbe.getHttpGet();

                HTTPGetActionVo httpGetActionVo = new HTTPGetActionVo();
                if (httpGet != null) {
                    String host = httpGet.getHost();
                    List<V1HTTPHeader> httpHeaders = httpGet.getHttpHeaders();
                    String path = httpGet.getPath();
                    IntOrString port = httpGet.getPort();
                    String scheme = httpGet.getScheme();


                    httpGetActionVo.setPath(path);
                    if (port != null) {
                        boolean isInteger = port.isInteger();
                        if (isInteger) {
                            httpGetActionVo.setPortInt(port.getIntValue());
                        } else {
                            httpGetActionVo.setPortString(port.getStrValue());
                        }

                    }

                    httpGetActionVo.setScheme(scheme);
                    httpGetActionVo.setHost(host);

                    List<Map<String, String>> headerList = new ArrayList<>();
                    if (httpHeaders != null) {
                        for (V1HTTPHeader httpHeader : httpHeaders) {
                            String name = httpHeader.getName();
                            String value = httpHeader.getValue();

                            Map<String, String> map = new HashMap<>();
                            map.put(name, value);
                        }

                    }

                    httpGetActionVo.setHttpHeaders(headerList);
                }

                ProbeVo probeVo = new ProbeVo();
                probeVo.setInitialDelaySeconds(initialDelaySeconds);
                probeVo.setFailureThreshold(failureThreshold);
                probeVo.setPeriodSeconds(periodSeconds);
                probeVo.setSuccessThreshold(successThreshold);
                probeVo.setTimeoutSeconds(timeoutSeconds);
                probeVo.setTerminationGracePeriodSeconds(terminationGracePeriodSeconds);
                probeVo.setHttpGet(httpGetActionVo);
                containerVo.setReadinessProbe(probeVo);
            }

            V1Probe livenessProbe = container.getLivenessProbe();
            if (livenessProbe != null) {

                Integer initialDelaySeconds = livenessProbe.getInitialDelaySeconds();
                Integer failureThreshold = livenessProbe.getFailureThreshold();
                Integer periodSeconds = livenessProbe.getPeriodSeconds();
                Integer successThreshold = livenessProbe.getSuccessThreshold();
                Integer timeoutSeconds = livenessProbe.getTimeoutSeconds();
                Long terminationGracePeriodSeconds = livenessProbe.getTerminationGracePeriodSeconds();
                V1HTTPGetAction httpGet = livenessProbe.getHttpGet();

                HTTPGetActionVo httpGetActionVo = new HTTPGetActionVo();
                if (httpGet != null) {
                    String host = httpGet.getHost();
                    List<V1HTTPHeader> httpHeaders = httpGet.getHttpHeaders();
                    String path = httpGet.getPath();
                    IntOrString port = httpGet.getPort();
                    String scheme = httpGet.getScheme();


                    httpGetActionVo.setPath(path);
                    if (port != null) {
                        boolean isInteger = port.isInteger();
                        if (isInteger) {
                            httpGetActionVo.setPortInt(port.getIntValue());
                        } else {
                            httpGetActionVo.setPortString(port.getStrValue());
                        }

                    }

                    httpGetActionVo.setScheme(scheme);
                    httpGetActionVo.setHost(host);

                    List<Map<String, String>> headerList = new ArrayList<>();
                    for (V1HTTPHeader httpHeader : httpHeaders) {
                        String name = httpHeader.getName();
                        String value = httpHeader.getValue();

                        Map<String, String> map = new HashMap<>();
                        map.put(name, value);
                    }

                    httpGetActionVo.setHttpHeaders(headerList);
                }

                ProbeVo probeVo = new ProbeVo();
                probeVo.setInitialDelaySeconds(initialDelaySeconds);
                probeVo.setFailureThreshold(failureThreshold);
                probeVo.setPeriodSeconds(periodSeconds);
                probeVo.setSuccessThreshold(successThreshold);
                probeVo.setTimeoutSeconds(timeoutSeconds);
                probeVo.setTerminationGracePeriodSeconds(terminationGracePeriodSeconds);
                probeVo.setHttpGet(httpGetActionVo);
                containerVo.setLivenessProbe(probeVo);
            }
            List<V1VolumeMount> volumeMounts = container.getVolumeMounts();
            List<VolumeMountVo> volumeMountVos = new ArrayList<>();

            for (V1VolumeMount volumeMount : volumeMounts) {


                VolumeMountVo volumeMountVo = new VolumeMountVo();
                volumeMountVo.setName(volumeMount.getName());
                volumeMountVo.setMountPath(volumeMount.getMountPath());
                volumeMountVo.setMountPropagation(volumeMount.getMountPropagation());
                volumeMountVo.setReadOnly(volumeMount.getReadOnly());
                volumeMountVo.setSubPath(volumeMount.getSubPath());
                volumeMountVo.setSubPathExpr(volumeMount.getSubPathExpr());
                volumeMountVos.add(volumeMountVo);
            }

            containerVo.setImage(image);
            containerVo.setVolumeMountVos(volumeMountVos);
            list.add(containerVo);
        }
        return list;
    }

    public static final ContainerVo v1Container2ContainerVo(V1Container container) {

        ContainerVo containerVo = new ContainerVo();
        containerVo.setName(container.getName());
        containerVo.setImagePullPolicy(container.getImagePullPolicy());
        String image = container.getImage();
        V1Probe readinessProbe = container.getReadinessProbe();
        if (readinessProbe != null) {

            Integer initialDelaySeconds = readinessProbe.getInitialDelaySeconds();
            Integer failureThreshold = readinessProbe.getFailureThreshold();
            Integer periodSeconds = readinessProbe.getPeriodSeconds();
            Integer successThreshold = readinessProbe.getSuccessThreshold();
            Integer timeoutSeconds = readinessProbe.getTimeoutSeconds();
            Long terminationGracePeriodSeconds = readinessProbe.getTerminationGracePeriodSeconds();
            V1HTTPGetAction httpGet = readinessProbe.getHttpGet();

            HTTPGetActionVo httpGetActionVo = new HTTPGetActionVo();
            if (httpGet != null) {
                String host = httpGet.getHost();
                List<V1HTTPHeader> httpHeaders = httpGet.getHttpHeaders();
                String path = httpGet.getPath();
                IntOrString port = httpGet.getPort();
                String scheme = httpGet.getScheme();


                httpGetActionVo.setPath(path);
                if (port != null) {
                    boolean isInteger = port.isInteger();
                    if (isInteger) {
                        httpGetActionVo.setPortInt(port.getIntValue());
                    } else {
                        httpGetActionVo.setPortString(port.getStrValue());
                    }

                }

                httpGetActionVo.setScheme(scheme);
                httpGetActionVo.setHost(host);

                List<Map<String, String>> headerList = new ArrayList<>();
                if (httpHeaders != null) {
                    for (V1HTTPHeader httpHeader : httpHeaders) {
                        String name = httpHeader.getName();
                        String value = httpHeader.getValue();

                        Map<String, String> map = new HashMap<>();
                        map.put(name, value);
                    }

                }

                httpGetActionVo.setHttpHeaders(headerList);
            }

            ProbeVo probeVo = new ProbeVo();
            probeVo.setInitialDelaySeconds(initialDelaySeconds);
            probeVo.setFailureThreshold(failureThreshold);
            probeVo.setPeriodSeconds(periodSeconds);
            probeVo.setSuccessThreshold(successThreshold);
            probeVo.setTimeoutSeconds(timeoutSeconds);
            probeVo.setTerminationGracePeriodSeconds(terminationGracePeriodSeconds);
            probeVo.setHttpGet(httpGetActionVo);
            containerVo.setReadinessProbe(probeVo);
        }

        V1Probe livenessProbe = container.getLivenessProbe();
        if (livenessProbe != null) {

            Integer initialDelaySeconds = livenessProbe.getInitialDelaySeconds();
            Integer failureThreshold = livenessProbe.getFailureThreshold();
            Integer periodSeconds = livenessProbe.getPeriodSeconds();
            Integer successThreshold = livenessProbe.getSuccessThreshold();
            Integer timeoutSeconds = livenessProbe.getTimeoutSeconds();
            Long terminationGracePeriodSeconds = livenessProbe.getTerminationGracePeriodSeconds();
            V1HTTPGetAction httpGet = livenessProbe.getHttpGet();

            HTTPGetActionVo httpGetActionVo = new HTTPGetActionVo();
            if (httpGet != null) {
                String host = httpGet.getHost();
                List<V1HTTPHeader> httpHeaders = httpGet.getHttpHeaders();
                String path = httpGet.getPath();
                IntOrString port = httpGet.getPort();
                String scheme = httpGet.getScheme();


                httpGetActionVo.setPath(path);
                if (port != null) {
                    boolean isInteger = port.isInteger();
                    if (isInteger) {
                        httpGetActionVo.setPortInt(port.getIntValue());
                    } else {
                        httpGetActionVo.setPortString(port.getStrValue());
                    }

                }

                httpGetActionVo.setScheme(scheme);
                httpGetActionVo.setHost(host);

                List<Map<String, String>> headerList = new ArrayList<>();
                for (V1HTTPHeader httpHeader : httpHeaders) {
                    String name = httpHeader.getName();
                    String value = httpHeader.getValue();

                    Map<String, String> map = new HashMap<>();
                    map.put(name, value);
                }

                httpGetActionVo.setHttpHeaders(headerList);
            }

            ProbeVo probeVo = new ProbeVo();
            probeVo.setInitialDelaySeconds(initialDelaySeconds);
            probeVo.setFailureThreshold(failureThreshold);
            probeVo.setPeriodSeconds(periodSeconds);
            probeVo.setSuccessThreshold(successThreshold);
            probeVo.setTimeoutSeconds(timeoutSeconds);
            probeVo.setTerminationGracePeriodSeconds(terminationGracePeriodSeconds);
            probeVo.setHttpGet(httpGetActionVo);
            containerVo.setLivenessProbe(probeVo);
        }
        List<V1VolumeMount> volumeMounts = container.getVolumeMounts();
        List<VolumeMountVo> volumeMountVos = new ArrayList<>();
        if (volumeMounts != null) {
            for (V1VolumeMount volumeMount : volumeMounts) {


                VolumeMountVo volumeMountVo = new VolumeMountVo();
                volumeMountVo.setName(volumeMount.getName());
                volumeMountVo.setMountPath(volumeMount.getMountPath());
                volumeMountVo.setMountPropagation(volumeMount.getMountPropagation());
                volumeMountVo.setReadOnly(volumeMount.getReadOnly());
                volumeMountVo.setSubPath(volumeMount.getSubPath());
                volumeMountVo.setSubPathExpr(volumeMount.getSubPathExpr());
                volumeMountVos.add(volumeMountVo);
            }
        }


        containerVo.setImage(image);
        containerVo.setVolumeMountVos(volumeMountVos);
        return containerVo;
    }

    public static PodRequestLimitsInfo v1Pod2PodRequestLimitsInfo(V1Pod v1Pod) {
        V1ObjectMeta metadata = v1Pod.getMetadata();
        String namespace = metadata.getNamespace();
        String name = metadata.getName();
        OffsetDateTime creationTimestamp = metadata.getCreationTimestamp();

        List<QuantityVo> requestList = new ArrayList<>();
        List<QuantityVo> limitList = new ArrayList<>();

        V1PodSpec spec = v1Pod.getSpec();
        if (spec != null) {
            List<V1Container> containers = spec.getContainers();
            if (containers != null && !containers.isEmpty()) {
                V1Container v1Container = containers.get(0);
                V1ResourceRequirements resources = v1Container.getResources();
                if (resources != null) {
                    Map<String, Quantity> requests = resources.getRequests();
                    Map<String, Quantity> limits = resources.getLimits();


                    if (requests != null) {
                        for (Map.Entry<String, Quantity> quantityEntry : requests.entrySet()) {
                            String key = quantityEntry.getKey();
                            Quantity value = quantityEntry.getValue();
                            QuantityVo quantityVo = new QuantityVo();

                            quantityVo.setName(key).setNumber(value.getNumber()).setNumberString(value.toSuffixedString()).setFormat(value.getFormat().toString());
                            requestList.add(quantityVo);
                        }
                    }


                    if (limits != null) {
                        for (Map.Entry<String, Quantity> quantityEntry : limits.entrySet()) {
                            String key = quantityEntry.getKey();
                            Quantity value = quantityEntry.getValue();
                            QuantityVo quantityVo = new QuantityVo();
                            quantityVo.setName(key).setNumber(value.getNumber()).setNumberString(value.toSuffixedString()).setFormat(value.getFormat().toString());
                            limitList.add(quantityVo);
                        }
                    }


                }
            }

        }


        PodRequestLimitsInfo podRequestLimitsInfo = new PodRequestLimitsInfo();

        return podRequestLimitsInfo.setName(name)
                .setNamespace(namespace)
                .setLimitList(limitList)
                .setRequestList(requestList)
                .setCreateTime(CommonUtils.offsetDateTime2Date(creationTimestamp));
    }

}
