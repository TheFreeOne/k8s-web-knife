package org.freeone.k8s.web.knife;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.Image;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.kubernetes.client.Exec;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.KubectlExec;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.EventsV1Api;
import io.kubernetes.client.openapi.apis.NodeV1Api;
import io.kubernetes.client.openapi.models.EventsV1Event;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1HTTPGetAction;
import io.kubernetes.client.openapi.models.V1HTTPHeader;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeAddress;
import io.kubernetes.client.openapi.models.V1NodeSpec;
import io.kubernetes.client.openapi.models.V1NodeStatus;
import io.kubernetes.client.openapi.models.V1NodeSystemInfo;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1Probe;
import io.kubernetes.client.util.Namespaces;
import org.freeone.k8s.web.knife.service.DockerBuildService;
import org.freeone.k8s.web.knife.utils.DockerUtils;
import org.freeone.k8s.web.knife.utils.K8sUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class K8sWebKnifeApplicationTests {

    Logger logger = LoggerFactory.getLogger(K8sWebKnifeApplicationTests.class);

    @Autowired(required = false)
    @Qualifier("defaultApiClient")
    private ApiClient apiClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    private CoreV1Api coreV1Api;

    @Autowired(required = false)
    private AppsV1Api appsV1Api;

    @Autowired(required = false)
    private NodeV1Api nodeV1Api;


    @Autowired(required = false)
    private EventsV1Api eventsV1Api;

    @Autowired
    private DockerBuildService dockerBuildService;

    @Test
    public void testBuildkit() throws Exception {

        DockerClient connect = DockerUtils.connect(68L);
        List<Image> exec = connect.listImagesCmd().exec();
        System.out.println(exec);

    }

    @Test
    public void testExec() throws KubectlException {
        ApiClient apiClient1 = K8sUtils.apiClient(2L);
        String name = "mytomcat-deployment-8df844768-cdgqs";
        String container = "mytomcat";
        String[] command = new String[]{"ls","-lh"};
        boolean stdin = false;
        boolean tty = false;
//        KubectlExec
        Integer execute = Kubectl.exec().namespace(Namespaces.NAMESPACE_DEFAULT)
                .name(name)
                .container(container)
                .stdin(false).tty(false)
                .command(command).apiClient(apiClient1).execute();

//        System.out.println(execute);

        V1Pod pod = new V1Pod().metadata(new V1ObjectMeta().name(name).namespace(Namespaces.NAMESPACE_DEFAULT));

        Exec exec = new Exec(apiClient1);
        try {
            Process proc = exec.exec(pod, command, container, stdin, tty);
            copyAsync(proc.getInputStream(), System.out);
            copyAsync(proc.getErrorStream(), System.err);
            if (stdin) {
                copyAsync(System.in, proc.getOutputStream());
            }
             proc.waitFor();
        } catch (InterruptedException | ApiException | IOException ex) {
            throw new KubectlException(ex);
        }
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

    @Test
    public   void testDeploy() {
//        JsonObject json = new JsonParser().parse("{\"age\": 1}").getAsJsonObject();
//        System.out.println(json.get("age"));
//        System.out.println(json.getAsJsonObject("name"));
        dockerBuildService.deployImage(2L);
    }


    @Test
    public void testNode() throws KubectlException {
        List<V1Node> execute = Kubectl.get(V1Node.class).apiClient(apiClient).execute();
        for (V1Node v1Node : execute) {
            V1ObjectMeta metadata = v1Node.getMetadata();
            V1NodeStatus status = v1Node.getStatus();
            V1NodeSpec spec = v1Node.getSpec();
            List<V1NodeAddress> addresses = status.getAddresses();
            String internalIP = "";
            String hostname = "";
            // 10.244.0.0/24
            String podCIDR = spec.getPodCIDR();
            // [10.244.0.0/24]
            List<String> podCIDRs = spec.getPodCIDRs();
            Map<String, String> labels = metadata.getLabels();


            for (V1NodeAddress address : addresses) {
                if ("InternalIP".equalsIgnoreCase(address.getType())) {
                    internalIP = address.getAddress();
                } else if ("Hostname".equalsIgnoreCase(address.getType())) {
                    hostname = address.getAddress();
                }
            }
            V1NodeSystemInfo nodeInfo = status.getNodeInfo();
            // v1.25.5
            String kubeletVersion = nodeInfo.getKubeletVersion();
            // v1.25.5
            String kubeProxyVersion = nodeInfo.getKubeProxyVersion();
            // docker://20.10.17
            String containerRuntimeVersion = nodeInfo.getContainerRuntimeVersion();
            // Kali GNU/Linux Rolling
            String osImage = nodeInfo.getOsImage();
            // 6.0.0-kali3-amd64
            String kernelVersion = nodeInfo.getKernelVersion();
            String name = metadata.getName();
            System.out.println(v1Node);
        }
    }

    @Test
    public void testDeployment() throws KubectlException {

        /*
        {
        args: null
        command: null
        env: null
        envFrom: null
        image: mytomcat:0.0.3
        imagePullPolicy: IfNotPresent
        lifecycle: null
        livenessProbe: class V1Probe {
            exec: null
            failureThreshold: 3
            grpc: null
            httpGet: class V1HTTPGetAction {
                host: null
                httpHeaders: [class V1HTTPHeader {
                    name: Custom-Header
                    value: Awesome
                }]
                path: /
                port: 8080
                scheme: HTTP
            }
            initialDelaySeconds: 60
            periodSeconds: 10
            successThreshold: 1
            tcpSocket: null
            terminationGracePeriodSeconds: null
            timeoutSeconds: 3
        }
        name: mytomcat
        ports: [class V1ContainerPort {
            containerPort: 8080
            hostIP: null
            hostPort: null
            name: null
            protocol: TCP
        }]
        readinessProbe: class V1Probe {
            exec: null
            failureThreshold: 10
            grpc: null
            httpGet: class V1HTTPGetAction {
                host: null
                httpHeaders: null
                path: /
                port: 8080
                scheme: HTTP
            }
            initialDelaySeconds: 5
            periodSeconds: 5
            successThreshold: 1
            tcpSocket: null
            terminationGracePeriodSeconds: null
            timeoutSeconds: 3
        }
        resources: class V1ResourceRequirements {
            limits: null
            requests: null
        }
        securityContext: null
        startupProbe: null
        stdin: null
        stdinOnce: null
        terminationMessagePath: /dev/termination-log
        terminationMessagePolicy: File
        tty: null
        volumeDevices: null
        volumeMounts: null
        workingDir: null
    }
         */
        V1Deployment execute = Kubectl.get(V1Deployment.class).namespace(Namespaces.NAMESPACE_DEFAULT).name("mytomcat-deployment").apiClient(apiClient).execute();
        System.out.println(execute);
        V1PodSpec spec = execute.getSpec().getTemplate().getSpec();
        if (spec != null) {
            List<V1Container> containers = spec.getContainers();
            // 容器配置
            if (containers != null && !containers.isEmpty()) {
                V1Container v1Container = containers.get(0);
                // mytomcat:0.0.3
                String image = v1Container.getImage();
                // IfNotPresent
                String imagePullPolicy = v1Container.getImagePullPolicy();
                // 存活策略
                V1Probe livenessProbe = v1Container.getLivenessProbe();

                V1HTTPGetAction httpGet = livenessProbe.getHttpGet();
                // /
                String path = httpGet.getPath();
                // 8080
                IntOrString port = httpGet.getPort();
                // HTTP
                String scheme = httpGet.getScheme();
                // 自定义请求头
                List<V1HTTPHeader> httpHeaders = httpGet.getHttpHeaders();

                // 初始化秒
                Integer initialDelaySeconds = livenessProbe.getInitialDelaySeconds();
                // 检测周期
                Integer periodSeconds = livenessProbe.getPeriodSeconds();
                // 成功阈值
                Integer successThreshold = livenessProbe.getSuccessThreshold();
                // 超时时长 秒
                Integer timeoutSeconds = livenessProbe.getTimeoutSeconds();

                V1Probe readinessProbe = v1Container.getReadinessProbe();
//                readinessProbe.ge


                Map<String, Quantity> requests = v1Container.getResources().getRequests();
                if (requests != null) {
                    System.out.println(requests);
                }
                Map<String, Quantity> limits = v1Container.getResources().getLimits();
                if (limits != null) {
                    System.out.println(limits);
                }
            }
        }

    }

    @Test
    public void testEvnet() throws KubectlException {
        List<EventsV1Event> execute = Kubectl.get(EventsV1Event.class).namespace(Namespaces.NAMESPACE_DEFAULT).apiClient(apiClient).execute();
//        EventsV1Event execute = Kubectl.get(EventsV1Event.class).namespace(Namespaces.NAMESPACE_DEFAULT).name("mytomcat-deployment-5b6dc9b76c-xw25r").apiClient(apiClient).execute();
        System.out.println(execute);

    }

//    public void testDocker() {
//        DockerClientConfig custom = DefaultDockerClientConfig.createDefaultConfigBuilder()
//                .withDockerHost("tcp://docker.somewhere.tld:2376")
//                .withDockerTlsVerify(true)
//                .withDockerCertPath("/home/user/.docker")
//                .withRegistryUsername("registryUser")
//                .withRegistryPassword("registryPass")
//                .withRegistryEmail("registryMail")
//                .withRegistryUrl("registryUrl")
//                .build();
//    }


//    public static void main(String[] args) throws IOException {
//        String address = "192.168.0.142";
//
//        Process process = Runtime.getRuntime().exec("ping " + address);
////        InputStreamReader r = new InputStreamReader(process.getInputStream(), "UTF-8");
//
////        InputStreamReader r = new InputStreamReader(process.getInputStream(), "GB2312");
//        InputStreamReader r = new InputStreamReader(process.getInputStream(), "GBK");
//        LineNumberReader returnData = new LineNumberReader(r);
//
//        String returnMsg = "";
//        String line = "";
//        while ((line = returnData.readLine()) != null) {
//            System.out.println(line);
//            returnMsg += line;
//        }
//
//        if (returnMsg.indexOf("100% loss") != -1) {
//            System.out.println("与 " + address + " 连接不畅通.");
//        } else {
//            System.out.println("与 " + address + " 连接畅通.");
//        }
//    }

}
