package org.freeone.k8s.web.knife.utils;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.EventsV1Api;
import io.kubernetes.client.openapi.apis.NodeV1Api;
import io.kubernetes.client.util.ClientBuilder;
import org.freeone.k8s.web.knife.entity.K8sApiServerConfig;
import org.freeone.k8s.web.knife.repository.K8sApiServerConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class K8sUtils {

    @Autowired
    private K8sApiServerConfigRepository k8sConfigRecordRepository;
    private static K8sApiServerConfigRepository staticK8sConfigRecordRepository;

    @PostConstruct
    public void init() {
        staticK8sConfigRecordRepository = k8sConfigRecordRepository;
    }


    public static final ApiClient fasterApiClient(Long k8sId) {

        K8sApiServerConfig k8sRecord = staticK8sConfigRecordRepository.findById(k8sId).orElse(null);
        if (k8sRecord == null) {
            throw new RuntimeException("invalid k8sId");
        }

        ApiClient apiClient = new ClientBuilder().setBasePath(k8sRecord.getApiServerUrl())
                .setVerifyingSsl(false)
                .build();
        apiClient.addDefaultHeader("Authorization", k8sRecord.getK8sSecret());
        apiClient.setConnectTimeout(200);
        apiClient.setReadTimeout(200);
        apiClient.setWriteTimeout(200);
        return apiClient;
    }
    public static final ApiClient longTimeOutApiClient(Long k8sId) {

        K8sApiServerConfig k8sRecord = staticK8sConfigRecordRepository.findById(k8sId).orElse(null);
        if (k8sRecord == null) {
            throw new RuntimeException("invalid k8sId");
        }

        ApiClient apiClient = new ClientBuilder().setBasePath(k8sRecord.getApiServerUrl())
                .setVerifyingSsl(false)
                .build();
        apiClient.addDefaultHeader("Authorization", k8sRecord.getK8sSecret());
        apiClient.setConnectTimeout(1800000);
        apiClient.setReadTimeout(1800000);
        apiClient.setWriteTimeout(1800000);
        return apiClient;
    }

    public static final ApiClient apiClient(Long k8sId) {

        K8sApiServerConfig k8sRecord = staticK8sConfigRecordRepository.findById(k8sId).orElse(null);
        if (k8sRecord == null) {
            throw new RuntimeException("invalid k8sId");
        }

        ApiClient apiClient = new ClientBuilder().setBasePath(k8sRecord.getApiServerUrl())
                .setVerifyingSsl(false)
                .build();
        apiClient.addDefaultHeader("Authorization", k8sRecord.getK8sSecret());
        apiClient.setConnectTimeout(5_000);
        apiClient.setReadTimeout(5_000);
        apiClient.setWriteTimeout(5_000);
        return apiClient;
    }

    public static final ApiClient apiClient(String k8sApiServerUrl, String k8sSecret) {
        ApiClient apiClient = new ClientBuilder().setBasePath(k8sApiServerUrl)
                .setVerifyingSsl(false)
                .build();
        apiClient.addDefaultHeader("Authorization", k8sSecret);
        apiClient.setConnectTimeout(5_000);
        apiClient.setReadTimeout(5_000);
        apiClient.setWriteTimeout(5_000);
        return apiClient;
    }


    public static final CoreV1Api coreV1Api(ApiClient apiClient) {
        return new CoreV1Api(apiClient);
    }


    public static final AppsV1Api appsV1Api(ApiClient apiClient) {
        return new AppsV1Api(apiClient);
    }


    public static final NodeV1Api nodeV1Api(ApiClient apiClient) {
        return new NodeV1Api(apiClient);
    }

    public static final EventsV1Api eventsV1Api(ApiClient apiClient) {
        return new EventsV1Api(apiClient);
    }


}
