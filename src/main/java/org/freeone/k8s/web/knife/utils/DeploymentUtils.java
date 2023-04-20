package org.freeone.k8s.web.knife.utils;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.extended.kubectl.util.deployment.DeploymentHelper;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerState;
import io.kubernetes.client.openapi.models.V1ContainerStateRunning;
import io.kubernetes.client.openapi.models.V1ContainerStateTerminated;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentCondition;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1DeploymentStatus;
import io.kubernetes.client.openapi.models.V1DeploymentStrategy;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1OwnerReference;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodStatus;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1ReplicaSet;
import io.kubernetes.client.openapi.models.V1ReplicaSetSpec;
import io.kubernetes.client.openapi.models.V1ReplicaSetStatus;
import io.kubernetes.client.openapi.models.V1RollingUpdateDeployment;
import org.apache.commons.lang3.StringUtils;
import org.freeone.k8s.web.knife.entity.vo.DeploymentConditionVo;
import org.freeone.k8s.web.knife.entity.vo.DeploymentStatusVo;
import org.freeone.k8s.web.knife.entity.vo.DeploymentStrategyVo;
import org.freeone.k8s.web.knife.entity.vo.PodVo;
import org.freeone.k8s.web.knife.entity.vo.ReplicaSetVo;
import org.freeone.k8s.web.knife.entity.vo.RolloutHistoryVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeploymentUtils {

    static Logger logger = LoggerFactory.getLogger(DeploymentUtils.class);

    /**
     * 查找镜像
     *
     * @param v1Deployment
     * @return
     */
    public static final String findImage(V1Deployment v1Deployment) {
        String image = null;
        V1DeploymentSpec spec = v1Deployment.getSpec();
        // 获取模板
        V1PodTemplateSpec template = spec.getTemplate();
        List<V1Container> containers = spec.getTemplate().getSpec().getContainers();
        if (containers != null && containers.isEmpty() == false) {
            V1Container v1Container = containers.get(0);
            image = v1Container.getImage();
        }
        return image;
    }

    public static final int findReplicas(V1Deployment v1Deployment) {
        int replicas = 0;
        V1DeploymentStatus status = v1Deployment.getStatus();
        if (status != null) {
            replicas = Optional.ofNullable(status.getReplicas()).orElse(0);
        }
        return replicas;
    }

    public static final int findReadyReplicas(V1Deployment v1Deployment) {
        int readyReplicas = 0;
        V1DeploymentStatus status = v1Deployment.getStatus();
        if (status != null) {
            readyReplicas = Optional.ofNullable(status.getReadyReplicas()).orElse(0);
        }
        return readyReplicas;
    }

    public static final Map<String, String> findLabels(V1Deployment v1Deployment) {
        Map<String, String> labels = v1Deployment.getMetadata().getLabels();
        return labels;
    }
    public static final Map<String, String> findSelectorMatcherLabels(V1Deployment v1Deployment) {
        Map<String, String> labels = v1Deployment.getSpec().getSelector().getMatchLabels();
        return labels;
    }

    public static final Map<String, String> findAnnotations(V1Deployment v1Deployment) {
        Map<String, String> annotations = v1Deployment.getMetadata().getAnnotations();
        return annotations;
    }

    /**
     * 查找更新策略
     * @param v1Deployment
     * @return
     */
    public static final DeploymentStrategyVo findDeploymentStrategyVo(V1Deployment v1Deployment) {
        DeploymentStrategyVo deploymentStrategyVo = new DeploymentStrategyVo();
        deploymentStrategyVo.setType("unknown");
        V1DeploymentSpec spec = v1Deployment.getSpec();
        if (spec != null) {
            V1DeploymentStrategy strategy = spec.getStrategy();
            if (strategy != null) {
                String type = strategy.getType();
                V1RollingUpdateDeployment rollingUpdate = strategy.getRollingUpdate();
                if (StringUtils.isNoneEmpty(type)) {
                    deploymentStrategyVo.setType(type);
                    if ("RollingUpdate".equalsIgnoreCase(type) && rollingUpdate != null) {
                        IntOrString maxSurge = rollingUpdate.getMaxSurge();
                        if (maxSurge.isInteger()) {
                            Integer maxSurgeInt = rollingUpdate.getMaxSurge().getIntValue();
                            deploymentStrategyVo.setMaxSurgeInt(maxSurgeInt);
                        } else {
                            String maxSurgeString = maxSurge.getStrValue();
                            deploymentStrategyVo.setMaxSurgeString(maxSurgeString);
                        }

                        IntOrString maxUnavailable = rollingUpdate.getMaxUnavailable();
                        if (maxUnavailable.isInteger()) {
                            Integer maxUnavailableInt = maxUnavailable.getIntValue();
                            deploymentStrategyVo.setMaxUnavailableInt(maxUnavailableInt);
                        } else {
                            String maxUnavailableString = maxUnavailable.getStrValue();
                            deploymentStrategyVo.setMaxUnavailableString(maxUnavailableString);
                        }


                    }
                }

            }
        }

        return deploymentStrategyVo;
    }

    public static final List<DeploymentConditionVo> findDeploymentConditionList(V1Deployment v1Deployment) {
        List<DeploymentConditionVo> list = new ArrayList<>();

        V1DeploymentSpec spec = v1Deployment.getSpec();
        V1DeploymentStatus status = v1Deployment.getStatus();
        if (status != null) {

            List<V1DeploymentCondition> conditions = status.getConditions();
            if (conditions != null) {
                for (V1DeploymentCondition condition : conditions) {
                    DeploymentConditionVo deploymentConditionVo = new DeploymentConditionVo();
                    deploymentConditionVo.setType(condition.getType());
                    deploymentConditionVo.setStatus(condition.getStatus());
                    deploymentConditionVo.setReason(condition.getReason());
                    deploymentConditionVo.setMessage(condition.getMessage());
                    deploymentConditionVo.setLastUpdateTime(condition.getLastUpdateTime());
                    deploymentConditionVo.setLastTransitionTime(condition.getLastTransitionTime());

                    list.add(deploymentConditionVo);
                }
            }
        }

        return list;
    }

    public static final List<V1ReplicaSet> filterReplicaSetList(V1Deployment v1Deployment, List<V1ReplicaSet> replicaSetList) {
        String deployUid = v1Deployment.getMetadata().getUid();
        List<V1ReplicaSet> collect = replicaSetList.stream().filter(replicaset -> {
            List<V1OwnerReference> ownerReferences = replicaset.getMetadata().getOwnerReferences();
            if (ownerReferences != null) {
                for (V1OwnerReference ownerReference : ownerReferences) {
                    return ownerReference.getUid().equals(deployUid);
                }
            }
            return false;
        }).collect(Collectors.toList());
//        for (V1ReplicaSet replicaSet : collect) {
//            logger.info("replicaSet = {}", replicaSet);
//        }
        return collect;
    }

    public static final List<V1ReplicaSet> filterAvaliableReplicaSetList(V1Deployment v1Deployment, List<V1ReplicaSet> replicaSetList) {
        String deployUid = v1Deployment.getMetadata().getUid();
        List<V1ReplicaSet> collect = replicaSetList.stream().filter(replicaset -> {
            List<V1OwnerReference> ownerReferences = replicaset.getMetadata().getOwnerReferences();
            if (ownerReferences != null) {
                for (V1OwnerReference ownerReference : ownerReferences) {
                    return ownerReference.getUid().equals(deployUid) && replicaset.getStatus().getReplicas() != null && replicaset.getStatus().getReplicas() != 0;
                }
            }
            return false;
        }).collect(Collectors.toList());
//        for (V1ReplicaSet replicaSet : collect) {
//            logger.info("replicaSet = {}", replicaSet);
//        }
        return collect;
    }

    public static final List<ReplicaSetVo> replicaSetList2ReplicaSetVoList(List<V1ReplicaSet> replicasetList) {
        List<ReplicaSetVo> list = new ArrayList<>();

        for (V1ReplicaSet replicaSet : replicasetList) {
            ReplicaSetVo replicaSetVo = replicaSet2ReplicaSetVo(replicaSet);
            list.add(replicaSetVo);
        }
        // 降序
        list.sort(Comparator.comparing(ReplicaSetVo::getCreateTime).reversed());
        return list;
    }

    public static final List<ReplicaSetVo> replicaSetList2ReplicaSetVoListAndFilterAvaliable(List<V1ReplicaSet> replicasetList) {
        List<ReplicaSetVo> list = new ArrayList<>();
        for (V1ReplicaSet replicaSet : replicasetList) {
            ReplicaSetVo replicaSetVo = replicaSet2ReplicaSetVo(replicaSet);
            Integer replicas = replicaSetVo.getReplicas();
            if (replicas == null || replicas == 0) {
                continue;
            }
            list.add(replicaSetVo);
        }
        // 降序
        list.sort(Comparator.comparing(ReplicaSetVo::getCreateTime).reversed());
        return list;
    }

    public static final ReplicaSetVo replicaSet2ReplicaSetVo(V1ReplicaSet replicaSet) {
        if (replicaSet == null) {
            return null;
        }
        V1ObjectMeta metadata = replicaSet.getMetadata();
        String name = metadata.getName();
        String uid = metadata.getUid();
        String namespace = metadata.getNamespace();
        OffsetDateTime creationTimestamp = metadata.getCreationTimestamp();
        Map<String, String> labels = metadata.getLabels();
        String resourceVersion = metadata.getResourceVersion();
        String image = null;

        V1ReplicaSetSpec spec = replicaSet.getSpec();
        if (spec != null) {
            V1PodTemplateSpec template = spec.getTemplate();
            if (template != null) {
                V1PodSpec templateSpec = template.getSpec();
                if (templateSpec != null) {
                    List<V1Container> containers = templateSpec.getContainers();
                    if (containers != null && containers.isEmpty() == false) {
                        image = containers.get(0).getImage();
                    }
                }
            }
        }

        V1ReplicaSetStatus status = replicaSet.getStatus();
        Integer replicas = status.getReplicas();
        if (replicas != null && replicas < 1) {

        }
        Integer availableReplicas = status.getAvailableReplicas();
        Integer readyReplicas = status.getReadyReplicas();

        Long revision = DeploymentHelper.revision(replicaSet.getMetadata());
        String changeCause = StringUtils.EMPTY;
        if (replicaSet.getMetadata().getAnnotations() != null) {
            changeCause = Optional.ofNullable(replicaSet.getMetadata().getAnnotations().get("kubernetes.io/change-cause")).orElse(StringUtils.EMPTY);
        }



        ReplicaSetVo replicaSetVo = new ReplicaSetVo();
        replicaSetVo.setUid(uid);
        replicaSetVo.setName(name);
        replicaSetVo.setNamespace(namespace);
        replicaSetVo.setLabels(labels);
        replicaSetVo.setImage(image);
        replicaSetVo.setCreateTime(offsetDateTime2Date(creationTimestamp));
        replicaSetVo.setResourceVersion(resourceVersion);

        replicaSetVo.setReplicas(replicas);
        replicaSetVo.setAvailableReplicas(availableReplicas);
        replicaSetVo.setReadyReplicas(readyReplicas);
        replicaSetVo.setRevision(revision);
        replicaSetVo.setChangeCause(changeCause);

        return replicaSetVo;

    }


    public static final List<PodVo> v1PodList2PodVoList(List<V1Pod> podList) {
        List<PodVo> podVoList = new ArrayList<>();
        for (V1Pod pod : podList) {
//            logger.info("pod = {}", pod);
            String resourceVersion = pod.getMetadata().getResourceVersion();
            V1PodStatus status = pod.getStatus();
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
//                Date.from( localDateTime.atZone( ZoneId.systemDefault()).toInstant());

                }
            }


            String podPhase = status.getPhase();
            String podHostIP = status.getHostIP();
            String podIP = status.getPodIP();

            V1ObjectMeta poDMetadata = pod.getMetadata();
            String podName = poDMetadata.getName();
            String namespace = poDMetadata.getNamespace();


            PodVo podVo = new PodVo(podName, podPhase, podHostIP, podIP, restartCount, offsetDateTime, ready);
            if (ready != null && ready == false) {
                V1ContainerState lastState = v1ContainerStatus.getLastState();
                V1ContainerStateTerminated terminated = lastState.getTerminated();

                String reason = "";
                String message = "";
                if (terminated != null) {
                    terminated.getReason();
                    terminated.getMessage();
                }
                podVo.setReason(reason);
                podVo.setMessage(message);

                System.out.println(pod);
            }
            if (offsetDateTime != null) {
                Date from = Date.from(offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toInstant());
                podVo.setCreateAt(from);
            }
            podVo.setNamespace(namespace);
            podVo.setResourceVersion(resourceVersion);
            podVoList.add(podVo);
        }


        Collections.sort(podVoList, (item1, item2) -> Long.parseLong(item1.getResourceVersion()) - Long.parseLong(item2.getResourceVersion()) > 0 ? 1 : 0);
        return podVoList;
    }

    public static final DeploymentStatusVo findDeploymentStatusVo(V1Deployment v1Deployment) {
        V1DeploymentStatus status = v1Deployment.getStatus();
        DeploymentStatusVo deploymentStatusVo = new DeploymentStatusVo();
        List<DeploymentConditionVo> deploymentConditionList = findDeploymentConditionList(v1Deployment);
        deploymentStatusVo.setConditions(deploymentConditionList);
        deploymentStatusVo.setReplicas(status.getReplicas());
        deploymentStatusVo.setAvailableReplicas(status.getAvailableReplicas());
        deploymentStatusVo.setUnavailableReplicas(status.getUnavailableReplicas());
        deploymentStatusVo.setUpdatedReplicas(status.getUpdatedReplicas());
        deploymentStatusVo.setReadyReplicas(status.getReadyReplicas());
        return deploymentStatusVo;
    }

    public static List<RolloutHistoryVo> findRollHistoryList(List<V1ReplicaSet> replicaSetList) {
        List<RolloutHistoryVo> list = new ArrayList<>();
        for (V1ReplicaSet rs : replicaSetList) {
            Long revision = DeploymentHelper.revision(rs.getMetadata());

            String changeCause = StringUtils.EMPTY;
            if (rs.getMetadata().getAnnotations() != null) {
                changeCause = Optional.ofNullable(rs.getMetadata().getAnnotations().get("kubernetes.io/change-cause")).orElse(StringUtils.EMPTY);
            }

            RolloutHistoryVo rolloutHistoryVo = new RolloutHistoryVo();
            rolloutHistoryVo.setRevision(revision);
            rolloutHistoryVo.setChangeCause(changeCause);
            list.add(rolloutHistoryVo);
        }
        return list;

    }

    @Deprecated
    public static final Date offsetDateTime2Date(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return Date.from(offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toInstant());
    }
}
