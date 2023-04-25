package org.freeone.k8s.web.knife.controller;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.extended.kubectl.util.deployment.DeploymentHelper;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1DeploymentStrategy;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1HTTPGetAction;
import io.kubernetes.client.openapi.models.V1HTTPGetActionBuilder;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1Probe;
import io.kubernetes.client.openapi.models.V1ReplicaSet;
import io.kubernetes.client.openapi.models.V1RollingUpdateDeployment;
import io.kubernetes.client.util.Namespaces;
import io.kubernetes.client.util.PatchUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.freeone.k8s.web.knife.entity.vo.ContainerVo;
import org.freeone.k8s.web.knife.entity.vo.DeploymentStatusVo;
import org.freeone.k8s.web.knife.entity.vo.DeploymentStrategyVo;
import org.freeone.k8s.web.knife.entity.vo.DeploymentVo;
import org.freeone.k8s.web.knife.entity.vo.EnvVarVo;
import org.freeone.k8s.web.knife.entity.vo.HTTPGetActionVo;
import org.freeone.k8s.web.knife.entity.vo.PodVo;
import org.freeone.k8s.web.knife.entity.vo.ProbeVo;
import org.freeone.k8s.web.knife.entity.vo.ReplicaSetVo;
import org.freeone.k8s.web.knife.entity.vo.RolloutHistoryVo;
import org.freeone.k8s.web.knife.repository.K8sApiServerConfigRepository;
import org.freeone.k8s.web.knife.utils.DeploymentUtils;
import org.freeone.k8s.web.knife.utils.K8sUtils;
import org.freeone.k8s.web.knife.utils.PodUtils;
import org.freeone.k8s.web.knife.utils.ResultKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 重启  kubectl rollout restart -n default deployment mytomcat-deployment
 * scale kubectl scale -n default deployment mytomcat-deployment --replicas=1
 * delete kubectl delete -n default deployment mytomcat-deployment
 */
@RestController
@RequestMapping("/deploy")
public class DeploymentController {

    public static final Logger LOGGER = LoggerFactory.getLogger(DeploymentController.class);


    @Autowired
    private K8sApiServerConfigRepository k8sConfigRecordRepository;


    @RequestMapping("/list")
    public ResultKit list(@RequestParam Long k8sId) throws ApiException, KubectlException {

        List<V1Deployment> execute = Kubectl.get(V1Deployment.class).namespace(Namespaces.NAMESPACE_DEFAULT).apiClient(K8sUtils.apiClient(k8sId)).execute();
        List<V1Deployment> items = execute;
        List<DeploymentVo> list = new ArrayList<>();
        for (V1Deployment v1Deployment : items) {
            V1DeploymentSpec spec = v1Deployment.getSpec();
            String image = DeploymentUtils.findImage(v1Deployment);
            V1ObjectMeta v1ObjectMeta = v1Deployment.getMetadata();
            // mytomcat-deployment
            String name = v1ObjectMeta.getName();
            String namespace = v1ObjectMeta.getNamespace();
            String uid = v1ObjectMeta.getUid();
            OffsetDateTime creationTimestamp = v1ObjectMeta.getCreationTimestamp();
            Date createTime = Date.from(creationTimestamp.atZoneSameInstant(ZoneId.systemDefault()).toInstant());
            int replicas = DeploymentUtils.findReplicas(v1Deployment);
            int readyReplicas = DeploymentUtils.findReadyReplicas(v1Deployment);

            DeploymentVo deploymentVo = new DeploymentVo(uid, namespace, name, replicas, creationTimestamp);
            deploymentVo.setImage(image);
            deploymentVo.setCreateTime(createTime);
            deploymentVo.setReadyReplicas(readyReplicas);
            deploymentVo.setSelectorMatchLabels(DeploymentUtils.findSelectorMatcherLabels(v1Deployment));


            list.add(deploymentVo);
        }
        return ResultKit.okWithData(list);
    }

    @RequestMapping("/get/{namespace}/{name}")
    public ResultKit get(@PathVariable("namespace") String namespace, @PathVariable("name") String name, @RequestParam Long k8sId) throws Exception {

        ApiClient apiClient = K8sUtils.apiClient(k8sId);
        V1Deployment v1Deployment = Kubectl.get(V1Deployment.class).namespace(namespace).name(name).apiClient(apiClient).execute();

        V1ObjectMeta deploymentMetadata = v1Deployment.getMetadata();
        String deployUid = deploymentMetadata.getUid();

        OffsetDateTime creationTimestamp = deploymentMetadata.getCreationTimestamp();

        List<V1ReplicaSet> replicaSetListItems = Kubectl.get(V1ReplicaSet.class).namespace(namespace).apiClient(K8sUtils.apiClient(k8sId)).execute();

        List<V1ReplicaSet> replicasetListForDeployment = DeploymentUtils.filterReplicaSetList(v1Deployment, replicaSetListItems);

        List<String> replicasetUidList = replicasetListForDeployment.stream().map(replicaSet -> replicaSet.getMetadata().getUid()).collect(Collectors.toList());

        List<V1Pod> podList = Kubectl.get(V1Pod.class).namespace(namespace).apiClient(K8sUtils.apiClient(k8sId)).execute();
        podList = podList.stream().filter(pod -> {
            String podOwnerUid = pod.getMetadata().getOwnerReferences().get(0).getUid();
            return replicasetUidList.contains(podOwnerUid);
        }).collect(Collectors.toList());


        List<PodVo> podVoList = DeploymentUtils.v1PodList2PodVoList(podList);

        Map<String, String> labels = DeploymentUtils.findLabels(v1Deployment);
        Map<String, String> selectorMatcherLabels = DeploymentUtils.findSelectorMatcherLabels(v1Deployment);


        Map<String, String> annotations = DeploymentUtils.findAnnotations(v1Deployment);

        Date createTime = Date.from(creationTimestamp.atZoneSameInstant(ZoneId.systemDefault()).toInstant());
        int replicas = DeploymentUtils.findReplicas(v1Deployment);
        int readyReplicas = DeploymentUtils.findReadyReplicas(v1Deployment);
        DeploymentStrategyVo deploymentStrategyVo = DeploymentUtils.findDeploymentStrategyVo(v1Deployment);
        DeploymentStatusVo deploymentStatusVo = DeploymentUtils.findDeploymentStatusVo(v1Deployment);
        List<ReplicaSetVo> replicaSetVos = DeploymentUtils.replicaSetList2ReplicaSetVoListAndFilterAvaliable(replicasetListForDeployment);

        List<V1ReplicaSet> oldRSes = new ArrayList<>();
        List<V1ReplicaSet> allOldRSes = new ArrayList<>();
        V1ReplicaSet allReplicaSets = DeploymentHelper.getAllReplicaSets(v1Deployment, K8sUtils.appsV1Api(apiClient), oldRSes, allOldRSes);

        List<ReplicaSetVo> oldReplicaSetList = DeploymentUtils.replicaSetList2ReplicaSetVoList(oldRSes);
        List<ReplicaSetVo> allReplicaSetList = DeploymentUtils.replicaSetList2ReplicaSetVoList(allOldRSes);
        ReplicaSetVo newReplicaSet = DeploymentUtils.replicaSet2ReplicaSetVo(allReplicaSets);

        List<RolloutHistoryVo> rollHistoryList = DeploymentUtils.findRollHistoryList(replicasetListForDeployment);

        List<V1Container> containers = v1Deployment.getSpec().getTemplate().getSpec().getContainers();
        List<ContainerVo> containerVos = new ArrayList<>();
        if (containers != null) {
            for (V1Container container : containers) {
                ContainerVo containerVo = PodUtils.v1Container2ContainerVo(container);
                containerVos.add(containerVo);
            }
        }

        rollHistoryList.sort(new Comparator<RolloutHistoryVo>() {
            @Override
            public int compare(RolloutHistoryVo o1, RolloutHistoryVo o2) {
                if (o1.getRevision().equals(o2.getRevision())) {
                    return 0;
                }
                return o1.getRevision() > o2.getRevision() ? 1 : -1;
            }
        });
        Collections.reverse(rollHistoryList);

        DeploymentVo deploymentVo = new DeploymentVo(deployUid, namespace, name, replicas, creationTimestamp);
        deploymentVo.setLabels(labels);
        deploymentVo.setAnnotations(annotations);
        deploymentVo.setPodVoList(podVoList);
        deploymentVo.setCreateTime(createTime);
        deploymentVo.setStrategy(deploymentStrategyVo);
        deploymentVo.setStatus(deploymentStatusVo);
        deploymentVo.setReplicaSetList(replicaSetVos);
        deploymentVo.setReplicas(replicas);
        deploymentVo.setReadyReplicas(readyReplicas);
        deploymentVo.setNewReplicaSet(newReplicaSet);
        deploymentVo.setOldReplicaSetList(oldReplicaSetList);
        deploymentVo.setAllReplicaSetList(allReplicaSetList);
        deploymentVo.setRolloutHistoryVos(rollHistoryList);
        deploymentVo.setContainers(containerVos);
        deploymentVo.setSelectorMatchLabels(selectorMatcherLabels);

//        deploymentVo.setConditionList(deploymentConditionList);
//        deploymentVo.setStrategy(deploymentSpec.getStrategy());
        return ResultKit.okWithData(deploymentVo);
    }


    @RequestMapping("/getAsJson/{namespace}/{name}")
    public ResultKit getAsJson(@PathVariable("namespace") String namespace, @PathVariable("name") String name, @RequestParam Long k8sId) throws Exception {
        V1Deployment v1Deployment = Kubectl.get(V1Deployment.class).namespace(namespace).name(name).apiClient(K8sUtils.apiClient(k8sId)).execute();
        String serializeJson = K8sUtils.apiClient(k8sId).getJSON().serialize(v1Deployment);
        return ResultKit.okWithData(serializeJson);
    }


    @RequestMapping("/restart/{namespace}/{name}")
    public ResultKit restart(@PathVariable("namespace") String namespace, @PathVariable("name") String deploymentName, @RequestParam Long k8sId) throws Exception {


        ApiClient apiClient = K8sUtils.apiClient(k8sId);
        V1Deployment runningDeployment = Kubectl.get(V1Deployment.class).namespace(namespace).name(deploymentName).apiClient(apiClient).execute();


        // Explicitly set "restartedAt" annotation with current date/time to trigger rollout when patch
        // is applied
        runningDeployment
                .getSpec()
                .getTemplate()
                .getMetadata()
                .putAnnotationsItem("kubectl.kubernetes.io/restartedAt", LocalDateTime.now().toString());


        runningDeployment.getMetadata().getAnnotations().put("kubernetes.io/change-cause", "restart");
        try {
            String deploymentJson = K8sUtils.apiClient(k8sId).getJSON().serialize(runningDeployment);

            PatchUtils.patch(
                    V1Deployment.class,
                    () ->
                            K8sUtils.appsV1Api(apiClient).patchNamespacedDeploymentCall(
                                    deploymentName,
                                    namespace,
                                    new V1Patch(deploymentJson),
                                    null,
                                    null,
                                    "kubectl-rollout",
                                    null,
                                    null,
                                    null),
                    V1Patch.PATCH_FORMAT_STRATEGIC_MERGE_PATCH,
                    K8sUtils.apiClient(k8sId));


            // Wait until deployment has stabilized after rollout restart
//            Wait.poll(
//                    Duration.ofSeconds(3),
//                    Duration.ofSeconds(60),
//                    () -> {
//                        try {
//                            System.out.println("Waiting until example deployment restarted successfully...");
//                            return appsV1Api
//                                    .readNamespacedDeployment(deploymentName, namespace, null)
//                                    .getStatus()
//                                    .getReadyReplicas()
//                                    > 0;
//                        } catch (ApiException e) {
//                            e.printStackTrace();
//                            return false;
//                        }
//                    });
            System.out.println("Example deployment restarted successfully!");
            return ResultKit.ok();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/updateByJson/{namespace}/{name}")
    public ResultKit updateByJson(@PathVariable("namespace") String namespace, @PathVariable("name") String deploymentName, @RequestParam String deploymentJson, @RequestParam Long k8sId) throws Exception {

        ApiClient apiClient = K8sUtils.apiClient(k8sId);
        V1Deployment v1Deployment = apiClient.getJSON().deserialize(deploymentJson, V1Deployment.class);

        PatchUtils.patch(
                V1Deployment.class,
                () ->
                        K8sUtils.appsV1Api(apiClient).patchNamespacedDeploymentCall(
                                deploymentName,
                                namespace,
                                new V1Patch(deploymentJson),
                                null,
                                null,
                                "kubectl-rollout",
                                null,
                                null,
                                null),
                V1Patch.PATCH_FORMAT_STRATEGIC_MERGE_PATCH,
                K8sUtils.apiClient(k8sId));

        return ResultKit.ok();
    }

    @RequestMapping("/updateEnv/{namespace}/{name}")
    public ResultKit updateEnv(@PathVariable("namespace") String namespace, @PathVariable("name") String deploymentName, @RequestBody EnvVarVo[] env, @RequestParam Long k8sId) throws Exception {

        ApiClient apiClient = K8sUtils.apiClient(k8sId);
        V1Deployment runningDeployment = Kubectl.get(V1Deployment.class).namespace(namespace).name(deploymentName).apiClient(apiClient).execute();
        V1Container v1Container = runningDeployment.getSpec().getTemplate().getSpec().getContainers().get(0);
        List<V1EnvVar> envVarList = new ArrayList<>();
        for (EnvVarVo envVarVo : env) {
            V1EnvVar v1EnvVar = new V1EnvVar();
            v1EnvVar.setName(envVarVo.getName());
            v1EnvVar.setValue(envVarVo.getValue());
            envVarList.add(v1EnvVar);
        }

//        V1EnvVar v1EnvVar = new V1EnvVar();
//        v1EnvVar.setName("kwk.env.update");
//        v1EnvVar.setValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//        envVarList.add(v1EnvVar);
        v1Container.setEnv(envVarList);
//        String serialize = apiClient.getJSON().serialize(runningDeployment);
//        PatchUtils.patch(
//                V1Deployment.class,
//                () ->
//                        K8sUtils.appsV1Api(apiClient).patchNamespacedDeploymentCall(
//                                deploymentName,
//                                namespace,
//                                new V1Patch(serialize),
//                                null,
//                                null,
//                                "kubectl-rollout",
//                                null,
//                                null,
//                                null),
//                V1Patch.PATCH_FORMAT_STRATEGIC_MERGE_PATCH,
//                apiClient);
        K8sUtils.appsV1Api(apiClient).replaceNamespacedDeployment(deploymentName, namespace, runningDeployment, null, null, null, null);
        return ResultKit.ok();
    }

    @RequestMapping("/addByJson/{namespace}/{name}")
    public ResultKit addByJson(@PathVariable("namespace") String namespace, @PathVariable("name") String deploymentName, @RequestParam String deploymentJson, @RequestParam Long k8sId) throws Exception {

        ApiClient apiClient = K8sUtils.apiClient(k8sId);
        V1Deployment v1Deployment = apiClient.getJSON().deserialize(deploymentJson, V1Deployment.class);
        Kubectl.apply(V1Deployment.class).namespace(namespace).name(deploymentName).resource(v1Deployment).apiClient(apiClient).execute();
        return ResultKit.ok();

    }

    /**
     * 将Replicas设置成0，将deployment下线
     *
     * @param namespace
     * @param name
     * @return
     * @throws Exception
     */
    @RequestMapping("/offline/{namespace}/{name}")
    public ResultKit offlineDeployment(@PathVariable("namespace") String namespace, @PathVariable("name") String name, @RequestParam Long k8sId) throws Exception {
        ApiClient apiClient = K8sUtils.apiClient(k8sId);
        Kubectl.scale(V1Deployment.class).name(name).namespace(namespace).replicas(0).apiClient(apiClient).execute();
        return ResultKit.ok();
    }

    @RequestMapping("/online/{namespace}/{name}")
    public ResultKit onlineDeployment(@PathVariable("namespace") String namespace, @PathVariable("name") String name, @RequestParam Long k8sId) throws Exception {

        ApiClient apiClient = K8sUtils.apiClient(k8sId);
//        V1Deployment deployment = Kubectl.get(V1Deployment.class).apiClient(apiClient).name(name).namespace(namespace).execute();
//        deployment.getSpec().setReplicas(1);
//        deployment.getMetadata().getAnnotations().put("kubernetes.io/change-cause", "online");
//        String serializeJson = K8sUtils.apiClient(k8sId).getJSON().serialize(deployment);
//        V1Deployment execute = Kubectl.patch(V1Deployment.class).name(name).namespace(namespace).patchContent(new V1Patch(serializeJson)).apiClient(apiClient).execute();
//        V1Deployment v1Deployment = Kubectl.scale(V1Deployment.class).apiClient(apiClient).name(name).namespace(namespace).replicas(0).execute();
        Kubectl.scale(V1Deployment.class).name(name).namespace(namespace).replicas(1).apiClient(apiClient).execute();
        return ResultKit.ok();
    }

    @RequestMapping("/updateStrategy/{namespace}/{name}")
    public ResultKit updateStrategy(DeploymentStrategyVo strategy, @PathVariable("namespace") String namespace, @PathVariable("name") String name, @RequestParam Long k8sId) throws KubectlException, ApiException {
        String type = strategy.getType();
        if (!"RollingUpdate".equals(type) && !"Recreate".equals(type)) {
            return ResultKit.failed("error type");
        }
        ApiClient apiClient = K8sUtils.apiClient(k8sId);
        V1Deployment deployment = Kubectl.get(V1Deployment.class).apiClient(apiClient).name(name).namespace(namespace).execute();

        V1DeploymentStrategy v1DeploymentStrategy = deployment.getSpec().getStrategy();

        if ("RollingUpdate".equals(type)) {
            V1RollingUpdateDeployment v1RollingUpdateDeployment = new V1RollingUpdateDeployment();
            if (strategy.getMaxSurgeInt() == null) {
                throw new RuntimeException("strategy.getMaxSurgeInt() is null");
            }
            if (strategy.getMaxUnavailableInt() == null) {
                throw new RuntimeException("strategy.getMaxUnavailableInt() is null");
            }
            IntOrString surgeInt = new IntOrString(strategy.getMaxSurgeInt());
            IntOrString maxUnavailableInt = new IntOrString(strategy.getMaxUnavailableInt());


            v1RollingUpdateDeployment.setMaxSurge(surgeInt);
            v1RollingUpdateDeployment.setMaxUnavailable(maxUnavailableInt);

            v1DeploymentStrategy.setRollingUpdate(v1RollingUpdateDeployment);
            v1DeploymentStrategy.setType("RollingUpdate");
        } else {
            v1DeploymentStrategy.setType("Recreate");
            v1DeploymentStrategy.setRollingUpdate(null);
        }
        K8sUtils.appsV1Api(apiClient).replaceNamespacedDeployment(name, namespace, deployment, null, null, null, null);
        return ResultKit.ok();
    }

    @RequestMapping("/updateProbe/{namespace}/{name}")
    public ResultKit updateProbe(@RequestBody ContainerVo request, @PathVariable("namespace") String namespace, @PathVariable("name") String name, @RequestParam Long k8sId, @RequestParam String probeType) throws KubectlException, ApiException {
        if (!"HTTP Probe".equals(probeType)) {
            return ResultKit.failed("不支持此种检测方式");
        }
        ApiClient apiClient = K8sUtils.apiClient(k8sId);
        V1Deployment runningDeployment = Kubectl.get(V1Deployment.class).namespace(namespace).name(name).apiClient(apiClient).execute();
        V1Container v1Container = runningDeployment.getSpec().getTemplate().getSpec().getContainers().get(0);
        ProbeVo readinessProbe = request.getReadinessProbe();
        ProbeVo livenessProbe = request.getLivenessProbe();
        if (readinessProbe != null) {
            V1Probe readinessV1Probe = new V1Probe();
            readinessV1Probe.setFailureThreshold(readinessProbe.getFailureThreshold());
            readinessV1Probe.setSuccessThreshold(readinessProbe.getSuccessThreshold());
            readinessV1Probe.setPeriodSeconds(readinessProbe.getPeriodSeconds());
            readinessV1Probe.setInitialDelaySeconds(readinessProbe.getInitialDelaySeconds());
            readinessV1Probe.setTimeoutSeconds(readinessProbe.getTimeoutSeconds());
            readinessV1Probe.setTerminationGracePeriodSeconds(readinessProbe.getTerminationGracePeriodSeconds());


            if (readinessProbe.getHttpGet() != null) {
                HTTPGetActionVo httpGet = readinessProbe.getHttpGet();
                V1HTTPGetActionBuilder v1HTTPGetActionBuilder = new V1HTTPGetActionBuilder();
                if (httpGet.getScheme() != null) {
                    v1HTTPGetActionBuilder.withScheme(httpGet.getScheme());
                }
                if (httpGet.getPortInt() != null) {
                    v1HTTPGetActionBuilder.withNewPort(httpGet.getPortInt());
                }
                if (httpGet.getPath() != null) {
                    v1HTTPGetActionBuilder.withPath(httpGet.getPath());
                }
                V1HTTPGetAction v1httpGet = v1HTTPGetActionBuilder.build();
                readinessV1Probe.setHttpGet(v1httpGet);
            }
            v1Container.setReadinessProbe(readinessV1Probe);
        }
        if (livenessProbe != null) {
            V1Probe livenessV1Probe = new V1Probe();
            livenessV1Probe.setFailureThreshold(livenessProbe.getFailureThreshold());
            livenessV1Probe.setSuccessThreshold(livenessProbe.getSuccessThreshold());
            livenessV1Probe.setPeriodSeconds(livenessProbe.getPeriodSeconds());
            livenessV1Probe.setInitialDelaySeconds(livenessProbe.getInitialDelaySeconds());
            livenessV1Probe.setTimeoutSeconds(livenessProbe.getTimeoutSeconds());
            livenessV1Probe.setTerminationGracePeriodSeconds(livenessProbe.getTerminationGracePeriodSeconds());


            if (livenessProbe.getHttpGet() != null) {
                HTTPGetActionVo httpGet = livenessProbe.getHttpGet();
                V1HTTPGetActionBuilder v1HTTPGetActionBuilder = new V1HTTPGetActionBuilder();
                if (httpGet.getScheme() != null) {
                    v1HTTPGetActionBuilder.withScheme(httpGet.getScheme());
                }
                if (httpGet.getPortInt() != null) {
                    v1HTTPGetActionBuilder.withNewPort(httpGet.getPortInt());
                }
                if (httpGet.getPath() != null) {
                    v1HTTPGetActionBuilder.withPath(httpGet.getPath());
                }
                V1HTTPGetAction v1httpGet = v1HTTPGetActionBuilder.build();
                livenessV1Probe.setHttpGet(v1httpGet);
            }

            v1Container.setLivenessProbe(livenessV1Probe);
        }
        String serialize = apiClient.getJSON().serialize(runningDeployment);
        PatchUtils.patch(
                V1Deployment.class,
                () ->
                        K8sUtils.appsV1Api(apiClient).patchNamespacedDeploymentCall(
                                name,
                                namespace,
                                new V1Patch(serialize),
                                null,
                                null,
                                "kubectl-rollout",
                                null,
                                null,
                                null),
                V1Patch.PATCH_FORMAT_STRATEGIC_MERGE_PATCH,
                apiClient);

        return ResultKit.ok();
    }


}
