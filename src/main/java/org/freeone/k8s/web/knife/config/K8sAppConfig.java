package org.freeone.k8s.web.knife.config;

import io.kubernetes.client.extended.controller.Controller;
import io.kubernetes.client.extended.controller.builder.ControllerBuilder;
import io.kubernetes.client.extended.controller.builder.DefaultControllerBuilder;
import io.kubernetes.client.extended.controller.reconciler.Reconciler;
import io.kubernetes.client.extended.controller.reconciler.Request;
import io.kubernetes.client.extended.controller.reconciler.Result;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.informer.cache.Lister;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.EventsV1Event;
import io.kubernetes.client.openapi.models.EventsV1EventList;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Endpoints;
import io.kubernetes.client.openapi.models.V1EndpointsList;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

/**
 * https://github.com/kubernetes-client/java/blob/master/examples/examples-release-15/src/main/java/io/kubernetes/client/examples/SpringControllerExample.java
 */
@Configuration
public class K8sAppConfig {

    @Value("${k8s.api-server-url}")
    private String k8sApiServerUrl;

    @Value("${k8s.secret}")
    private String k8sSecret;

    /**
     * io.kubernetes.client.spring.extended.controller.configio.kubernetes.client.spring.extended.controller.config.KubernetesInformerAutoConfiguration
     * 看{@see io.kubernetes.client.spring.extended.controller.configio.kubernetes.client.spring.extended.controller.config.KubernetesInformerAutoConfiguration}
     *
     * @return
     * @throws IOException
     */
    @Deprecated
    @Bean("defaultApiClient")
//    @ConditionalOnMissingBean
    public ApiClient defaultApiClient() throws IOException {
        ApiClient apiClient = new ClientBuilder().setBasePath(k8sApiServerUrl)
                .setVerifyingSsl(false)
//                .setAuthentication(new AccessTokenAuthentication("Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImxLOS1EZGVQRGxyUTJ4QXBXdm96ejQtQkYyMlB4NGpZZkg5N0VJbmNCREUifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJzZWNyZXQtYWRtaW4iLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiYWRtaW4iLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiJiMTk5Nzg0MC1jY2JmLTRhYjctODQ4Zi04ZWZhNjFjZTk5ZDkiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZS1zeXN0ZW06YWRtaW4ifQ.YW7BJHGJwWqDWq-pmu-xAdWwXNXERiGyBlj2nW_H-WFE6l6UbUWWnT251tyNne5geHVyBZS0zXq2gx6U-RvnPSa7dUMHPiTKDeBZceNkpVp5rngyyBqf9y3xoE5IeMcZIgnku0fTPDx65_wH53Lec6uPcoSPCSewd7dIlj1JIMMDYQKnaeOqSpUFCOKb9TQlQbIjHrWxmxW7_suWPPHLHNeEXNNeEOFRwaf4DrglDW0ShDddsi4gffYavl2ryE0tnjPvyZV65MkuEt-7rLdGa3YdV0qlmG32jE1rgeUrpDOT4WfuGKNhDJ-vrNYtvCgqfOnPM6XFeda8jKpz9Y0CZA"))
                .build();

//        ApiClient apiClient = ClientBuilder.defaultClient();
//        apiClient.setBasePath("https://192.168.110.130:6443");
//        apiClient.setVerifyingSsl(false);
        apiClient.addDefaultHeader("Authorization", k8sSecret);
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(apiClient);
        apiClient.setConnectTimeout(5_000);
        apiClient.setReadTimeout(5_000);
        apiClient.setWriteTimeout(5_000);
        return apiClient;
    }

    @Bean
    public SharedInformerFactory sharedInformerFactory(){
        SharedInformerFactory sharedInformerFactory = new SharedInformerFactory();
        return sharedInformerFactory;
    }

    @Autowired
    private ThreadPoolTaskExecutor asyncServiceExecutor;

//    @Bean
    public CommandLineRunner commandLineRunner(
            SharedInformerFactory sharedInformerFactory, Controller nodePrintingController) {

        return args -> {
            System.out.println("starting informers..");
            sharedInformerFactory.startAllRegisteredInformers();
// 使用一个线程处理，避免阻塞
            System.out.println("running controller..");
            asyncServiceExecutor.submit(() -> {
                nodePrintingController.run();
            });

        };
    }

//    @Bean
//    public Controller nodePrintingController(
//            SharedInformerFactory sharedInformerFactory, NodePrintingReconciler reconciler) {
//        DefaultControllerBuilder builder = ControllerBuilder.defaultBuilder(sharedInformerFactory);
//        builder =
//                builder.watch(
//                        (q) -> {
//                            return ControllerBuilder.controllerWatchBuilder(V1Node.class, q)
//                                    .withResyncPeriod(Duration.ofMinutes(1))
//                                    .build();
//                        });
//        builder.withWorkerCount(2);
//        builder.withReadyFunc(reconciler::informerReady);
//        return builder.withReconciler(reconciler).withName("nodePrintingController").build();
//    }

    @Bean
    public SharedIndexInformer<V1Endpoints> endpointsInformer(
            ApiClient apiClient, SharedInformerFactory sharedInformerFactory) {
        GenericKubernetesApi<V1Endpoints, V1EndpointsList> genericApi =
                new GenericKubernetesApi<>(
                        V1Endpoints.class, V1EndpointsList.class, "", "v1", "endpoints", apiClient);
        return sharedInformerFactory.sharedIndexInformerFor(genericApi, V1Endpoints.class, 0);
    }

    @Bean
    public SharedIndexInformer<V1Node> nodeInformer(
            ApiClient apiClient, SharedInformerFactory sharedInformerFactory) {

        GenericKubernetesApi<V1Node, V1NodeList> genericApi =
                new GenericKubernetesApi<>(V1Node.class, V1NodeList.class, "", "v1", "nodes", apiClient);
        return sharedInformerFactory.sharedIndexInformerFor(genericApi, V1Node.class, 60 * 1000L);
    }

    @Bean
    public SharedIndexInformer<V1Pod> podInformer(
            ApiClient apiClient, SharedInformerFactory sharedInformerFactory) {
        GenericKubernetesApi<V1Pod, V1PodList> genericApi =
                new GenericKubernetesApi<>(V1Pod.class, V1PodList.class, "", "v1", "pods", apiClient);
        return sharedInformerFactory.sharedIndexInformerFor(genericApi, V1Pod.class, 0);
    }

//
//    @Bean
//    public SharedIndexInformer<V1Deployment> deploymentInformer(
//            ApiClient apiClient, SharedInformerFactory sharedInformerFactory) {
//        GenericKubernetesApi<V1Deployment, V1DeploymentList> genericApi =
//                new GenericKubernetesApi<>(V1Deployment.class, V1DeploymentList.class, "", "v1", "deploys", apiClient);
//        return sharedInformerFactory.sharedIndexInformerFor(genericApi, V1Deployment.class, 0);
//    }

    @Bean
    public SharedIndexInformer<EventsV1Event> eventInformer(
            ApiClient apiClient, SharedInformerFactory sharedInformerFactory) {
        GenericKubernetesApi<EventsV1Event, EventsV1EventList> genericApi =
                new GenericKubernetesApi<>(EventsV1Event.class, EventsV1EventList.class, "", "v1", "events", apiClient);
        return sharedInformerFactory.sharedIndexInformerFor(genericApi, EventsV1Event.class, 0);
    }


    @Component
    public static class NodePrintingReconciler implements Reconciler {

        @Value("${namespace}")
        private String namespace;
//        private String namespace = Namespaces.NAMESPACE_DEFAULT;

        private SharedInformer<V1Node> nodeInformer;

        private SharedInformer<V1Pod> podInformer;
        private SharedInformer<V1Deployment> deploymentInformer;
        private SharedInformer<EventsV1Event> eventInformer;

        private Lister<V1Node> nodeLister;

        private Lister<V1Pod> podLister;
        private Lister<V1Deployment> deployLister;
        private Lister<EventsV1Event> eventLister;

        public NodePrintingReconciler(
                SharedIndexInformer<V1Node> nodeInformer
                , SharedIndexInformer<V1Pod> podInformer
//                , SharedIndexInformer<V1Deployment> deploymentInformer
                , SharedIndexInformer<EventsV1Event> eventInformer

        ) {
            this.nodeInformer = nodeInformer;
            this.podInformer = podInformer;
//            this.deploymentInformer = deploymentInformer;
            this.eventInformer = eventInformer;
            this.nodeLister = new Lister<>(nodeInformer.getIndexer(), namespace);
            this.podLister = new Lister<>(podInformer.getIndexer(), namespace);

//            this.deployLister = new Lister<>(deploymentInformer.getIndexer(), namespace);
            this.eventLister = new Lister<>(eventInformer.getIndexer(), namespace);
        }

        // *OPTIONAL*
        // If you want to hold the controller from running util some condition..
        public boolean informerReady() {
            return podInformer.hasSynced() && nodeInformer.hasSynced();
        }

        @Override
        public Result reconcile(Request request) {
            V1Node node = nodeLister.get(request.getName());

            System.out.println("get all pods in namespace " + namespace);

//            podLister.namespace(namespace).list().stream()
//                    .map(pod -> pod.getMetadata().getName())
//                    .forEach(System.out::println);

//            deployLister.namespace(namespace).list().stream().map(deploy -> deploy.getMetadata().getName())
//                    .forEach(System.out::println);


//            System.out.println("--------------------event ---------------------");
            eventLister.namespace(namespace).list().stream().forEach(event -> {
//                System.out.println(event);

            });
            ;

//            System.out.println("triggered reconciling " + node.getMetadata().getName());
            return new Result(false);
        }
    }

}


