package org.freeone.k8s.web.knife;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * [Protect the Docker daemon socket](https://docs.docker.com/engine/security/protect-access/)
 * [Docker Remote API未授权访问漏洞](https://help.aliyun.com/noticelist/articleid/13116380.html)
 * https://docs.docker.com/engine/api/#authentication
 * https://blog.csdn.net/RenshenLi/article/details/121986071
 */
public class DockerJavaExample {

    private static void dockerPing() throws URISyntaxException {
        DockerClient client = connect();
        client.pingCmd().exec();
        System.out.println("ping...");
    }

    private static void dockerInfo() throws URISyntaxException {
        DockerClient client = connect();
        Info info = client.infoCmd().exec();
        System.out.println("docker info : " + info.toString());
    }

    /**
     * 创建容器
     * @param client
     * @return
     * @throws URISyntaxException
     */
    public static CreateContainerResponse createContainer(DockerClient client) throws URISyntaxException {

        CreateContainerCmd containerCmd = client.createContainerCmd("nginx:latest")
                //名字
                .withName("nginx-01")
                //端口映射 内部80端口与外部81端口映射
                .withHostConfig(new HostConfig().withPortBindings(new Ports(new ExposedPort(80), Ports.Binding.bindPort(81))))
                //环境变量
                .withEnv("key=value")
                //挂载
                .withVolumes(new Volume("/var/log"));

        //创建
        CreateContainerResponse response = containerCmd.exec();
        System.out.println(response.getId());
        return response;
    }

    /**
     * 创建并启动容器
     * @throws URISyntaxException
     */
    public static void startContainer() throws URISyntaxException {
        DockerClient client = connect();
        //创建
        CreateContainerResponse response = createContainer(client);
        String containerId = response.getId();
        //启动
        client.startContainerCmd(containerId).exec();
    }

    /**
     * 停止容器
     * @throws URISyntaxException
     */
    public static void stopContainer() throws URISyntaxException {
        String containerId = "f97cca9cc21f";
        DockerClient client = connect();
        client.stopContainerCmd(containerId).exec();
    }

    /**
     * 、删除容器
     * @throws URISyntaxException
     */
    public static void removeContainer() throws URISyntaxException {
        String containerId = "f97cca9cc21f";
        DockerClient client = connect();
        client.removeContainerCmd(containerId).exec();
    }

    /**
     * 构建镜像
     * @return
     * @throws URISyntaxException
     */
    public static String buildImage() throws URISyntaxException {
        String imageName = "app";
        String imageTag = "v1";
        DockerClient client = connect();
        ImmutableSet<String> tag = ImmutableSet.of(imageName + ":" + imageTag);
        String imageId = client.buildImageCmd(new File("/opt/tmp/Dockerfile"))

                .withTags(tag)
                .start()
                .awaitImageId();
        return imageId;
    }


    /**
     * 打镜像tag
     * @throws URISyntaxException
     */
    public static void tagImage() throws URISyntaxException {
        DockerClient client = connect();
        client.tagImageCmd("nginx:latest", "172.16.10.151:80/library/nginx", "v2").exec();
    }

    public static void listImages() throws URISyntaxException {
        DockerClient client = connect();
        List<Image> exec = client.listImagesCmd().exec();
        for (Image image : exec) {
            Map<String, String> labels = image.getLabels();
            String[] repoTags = image.getRepoTags();
            System.out.println(image.toString());
        }
    }


    public static DockerClient connect() throws URISyntaxException {
        String host = "tcp://192.168.110.130:12375";
        String apiVersion = "1.41";
        //创建DefaultDockerClientConfig

        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withApiVersion(apiVersion)
                .withDockerHost(host)
//                .withDockerTlsVerify(Boolean.TRUE)

                .build();
//        .withDockerTlsVerify(true)
//                .withDockerTlsVerify(dock.get(connection).getWithTls().getDockerTLSVerify().toString())
//                .withDockerCertPath(dock.get(connection).getWithTls().getDockerCertPath())
//
        //创建DockerHttpClient
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        //创建DockerClient
        DockerClient client = DockerClientImpl.getInstance(config, httpClient);
        return client;
    }

    public static List<String> getIpAddress() throws SocketException {
        List<String> list = new LinkedList<>();
        Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
        while (enumeration.hasMoreElements()) {
            NetworkInterface network = (NetworkInterface) enumeration.nextElement();
            Enumeration addresses = network.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = (InetAddress) addresses.nextElement();
                if (address != null && (address instanceof Inet4Address || address instanceof Inet6Address)) {
                    list.add(address.getHostAddress());
                }
            }
        }
        return list;
    }



    public static List<String> getIpAddress2() throws SocketException {
        List<String> list = new LinkedList<>();
        Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
        while (enumeration.hasMoreElements()) {
            NetworkInterface network = (NetworkInterface) enumeration.nextElement();
            if (network.isVirtual() || !network.isUp()) {
                continue;
            } else {
                Enumeration addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = (InetAddress) addresses.nextElement();
                    if (address != null && (address instanceof Inet4Address || address instanceof Inet6Address)) {
                        list.add(address.getHostAddress());
                    }
                }
            }
        }


        return list;
    }


    public static void main(String[] args)
            throws Exception {
        System.out.println("\\'");
    }

}
