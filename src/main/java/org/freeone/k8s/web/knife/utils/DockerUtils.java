package org.freeone.k8s.web.knife.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.KeystoreSSLConfig;
import com.github.dockerjava.core.util.CertificateUtils;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.freeone.k8s.web.knife.entity.DockerApiConfig;
import org.freeone.k8s.web.knife.repository.DockerApiConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;

@Component
public class DockerUtils {

    @Autowired
    private DockerApiConfigRepository dockerApiRecordRepository;
    private static DockerApiConfigRepository staticDockerApiRecordRepository;


    @PostConstruct
    public void init() {
        staticDockerApiRecordRepository = dockerApiRecordRepository;
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

    public static DockerClient connect(Long dockerId) throws Exception {
        DockerApiConfig dockerApiRecord = staticDockerApiRecordRepository.findById(dockerId).orElse(null);
        if (dockerApiRecord == null) {
            throw new RuntimeException("invalid dockerId");
        }

        String host = dockerApiRecord.getHostPath();
        String apiVersion = dockerApiRecord.getApiVersion();

        String clientCertPem = dockerApiRecord.getClientCertPem();
        String clientKeyPem = dockerApiRecord.getClientKeyPem();
        if (StringUtils.isNoneBlank(clientCertPem, clientKeyPem)) {
            return connect(host, apiVersion, clientCertPem, clientKeyPem);
        }

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

    public static DockerClient connect(String host, String apiVersion) throws URISyntaxException {
        //创建DefaultDockerClientConfig

        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withApiVersion(apiVersion);
        if (StringUtils.isNotBlank(apiVersion)) {
            builder.withDockerHost(host);
        }

//                .withDockerTlsVerify(Boolean.TRUE)

        DefaultDockerClientConfig config = builder.build();
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

    public static DockerClient connect(String host, String apiVersion, String clientCertPem, String clientKeyPem) throws URISyntaxException, CertificateException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, KeyStoreException, UnrecoverableKeyException {
        //创建DefaultDockerClientConfig

        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(host);
        if (StringUtils.isNotBlank(apiVersion)) {
            builder.withApiVersion(apiVersion);
        }
        KeyStore keyStore = null;
        KeystoreSSLConfig docker = null;
        if (StringUtils.isNoneBlank(clientCertPem, clientKeyPem)) {
            // cannot find required provider:no such provider: BC
            Security.addProvider(new BouncyCastleProvider());
            keyStore = CertificateUtils.createKeyStore(clientKeyPem, clientCertPem);
//            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            /**
             * keyManagerFactory.init() 方法的参数主要包含以下三个方面的信息：
             *
             * KeyStore：指定用于管理证书以及私钥的密钥库。密钥库通常是包含一个或多个私钥及其对应的公钥证书链的文件。客户端和服务器之间的SSL/TLS握手需要检查预共享的证书是否指定了正确的密钥库，从而确保SSL/TLS会话的完整性。
             * Password：用于保护 KeyStore 的密码，以确保只有授权用户才能访问该 KeyStore 中的私钥和证书。
             * Algorithm：用于指定应该使用的密钥管理算法类型，常见的有“SunX509”、“PKIX”等。
             * 这些参数共同作用于 keyManagerFactory.init() 方法，确保可以加载和初始化 KeyStore，并使用 KeyStore 中的证书和私钥创建 KeyManagerFactory，并进行密钥管理和身份验证，使客户端与服务器之间的数据通信更加安全、可靠和完整性。
             */
//            keyManagerFactory.init(keyStore, "docker".toCharArray());
            // keystorePassword 好像没有什么用
            docker = new KeystoreSSLConfig(keyStore, "docker");
            builder.withCustomSslConfig(docker).withDockerTlsVerify(true);
        }


        DefaultDockerClientConfig config = builder.build();
//        .withDockerTlsVerify(true)
//                .withDockerTlsVerify(dock.get(connection).getWithTls().getDockerTLSVerify().toString())
//                .withDockerCertPath(dock.get(connection).getWithTls().getDockerCertPath())
//
        //创建DockerHttpClient
        ApacheDockerHttpClient.Builder builder1 = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())

                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45));
        if (StringUtils.isNoneBlank(clientCertPem, clientKeyPem)) {
            builder1.sslConfig(docker);
        }
        DockerHttpClient httpClient = builder1
                .build();
        //创建DockerClient
        DockerClient client = DockerClientImpl.getInstance(config, httpClient);
        return client;
    }
}
