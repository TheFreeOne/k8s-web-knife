package org.freeone.k8s.web.knife.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.google.common.collect.ImmutableSet;
import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.util.Namespaces;
import io.kubernetes.client.util.wait.Wait;
import kotlin.collections.ArrayDeque;
import org.freeone.k8s.web.knife.entity.DockerApiConfig;
import org.freeone.k8s.web.knife.entity.DockerImageBuildHistory;
import org.freeone.k8s.web.knife.entity.DockerImageDeployHistory;
import org.freeone.k8s.web.knife.entity.DockerfileTemplate;
import org.freeone.k8s.web.knife.entity.TemporaryFile;
import org.freeone.k8s.web.knife.repository.DockerApiConfigRepository;
import org.freeone.k8s.web.knife.repository.DockerImageBuildHistoryRepository;
import org.freeone.k8s.web.knife.repository.DockerImageDeployHistoryRepository;
import org.freeone.k8s.web.knife.repository.DockerfileTemplateRepository;
import org.freeone.k8s.web.knife.repository.TemporaryFileRepository;
import org.freeone.k8s.web.knife.service.DockerBuildService;
import org.freeone.k8s.web.knife.utils.CommonUtils;
import org.freeone.k8s.web.knife.utils.DeploymentUtils;
import org.freeone.k8s.web.knife.utils.DockerUtils;
import org.freeone.k8s.web.knife.utils.K8sUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DockerBuildServiceImpl implements DockerBuildService {

    @Value("classpath:DockerfileWithCopy")
    private Resource dockerfileWithCopy;

    @Value("classpath:DockerfileWithAdd")
    private Resource dockerfileWithAdd;


    @Autowired
    private DockerImageBuildHistoryRepository dockerImageBuildHisotyRepository;
    @Autowired
    private DockerImageDeployHistoryRepository dockerImageDeployHistoryRepository;


    @Autowired
    private DockerApiConfigRepository dockerApiRecordRepository;

    @Autowired
    private DockerfileTemplateRepository dockerfileTemplateRepository;

    @Autowired
    private TemporaryFileRepository temporaryFileRepository;

    private Logger logger = LoggerFactory.getLogger(DockerBuildServiceImpl.class);

    /**
     * Error occurred while preparing Docker context folder. 一般可能时dockerfile有问题 或者dockerfile所在的目录有文件被锁住
     * 异步方法构建镜像
     *
     * @param buildHistoryId
     */
    @Async
    @Override
    public void buildImage(Long buildHistoryId) {

        Optional<DockerImageBuildHistory> historyOptional = dockerImageBuildHisotyRepository.findById(buildHistoryId);
        if (historyOptional.isPresent()) {
            DockerImageBuildHistory dockerImageBuildHistory = historyOptional.get();
            Long targetDockerId = dockerImageBuildHistory.getTargetDockerId();
            DockerApiConfig dockerApiRecord = dockerApiRecordRepository.findById(targetDockerId).orElse(null);

            DockerfileTemplate dockerfileTemplate = dockerfileTemplateRepository.findById(dockerImageBuildHistory.getTargetDockerfileId()).orElse(null);

            if (dockerfileTemplate == null) {
                dockerImageBuildHistory.setErrInfo("invalid target dockerfileId");
                dockerImageBuildHistory.setBuildStatus((byte) 2);
                dockerImageBuildHisotyRepository.save(dockerImageBuildHistory);
                return;
            }

            if (dockerApiRecord == null) {
                dockerImageBuildHistory.setErrInfo("invalid target dockerId");
                dockerImageBuildHistory.setBuildStatus((byte) 2);
                dockerImageBuildHisotyRepository.save(dockerImageBuildHistory);
                return;
            }

            TemporaryFile temporaryFile = temporaryFileRepository.findById(dockerImageBuildHistory.getTempFileId()).orElse(null);
            if (temporaryFile == null) {
                dockerImageBuildHistory.setErrInfo("invalid target temp file id");
                dockerImageBuildHistory.setBuildStatus((byte) 2);
                dockerImageBuildHisotyRepository.save(dockerImageBuildHistory);
                return;
            }

            File tempDockerfile = null;
            File dockerfileFolder = null;
            File tempUpdateFile = null;
            String tempUploadFileName = null;
            String temporaryUploadFileFilePath = null;
            try {

                String tagString = dockerImageBuildHistory.getImageTag();
                ImmutableSet<String> tag = ImmutableSet.of(tagString);
                DockerClient dockerClient = DockerUtils.connect(targetDockerId);

                String content = dockerfileTemplate.getContent();


                temporaryUploadFileFilePath = temporaryFile.getFilePath();
                String ticket = temporaryFile.getTicket();
                Long fileId = temporaryFile.getId();
                tempUpdateFile = new File(temporaryUploadFileFilePath);
                tempUploadFileName = tempUpdateFile.getName();


                List<String> dockerfileContentList = new ArrayDeque<>();


                String[] lines = content.split("\\r?\\n");
                for (String line : lines) {
                    line = line.trim();
                    if (!line.startsWith("#")) {
                        line = line.replace("${kwk.filename}", tempUploadFileName);
                        line = line.replace("${kwk.fileName}", tempUploadFileName);

                        line = line.replace("${kwk.ticket}", ticket);

                        line = line.replace("${kwk.fileId}", fileId.toString());
                        line = line.replace("${kwk.fileid}", fileId.toString());
                        String firstIpAddress = CommonUtils.getFirstIpAddress();
                        line = line.replace("${kwk.localIp}", firstIpAddress);
                        line = line.replace("${kwk.localip}", firstIpAddress);
                        line = line.replace("${kwk.ip}", firstIpAddress);

                    }
                    dockerfileContentList.add(line);
                }
                String dockerfileContent = String.join("\n", dockerfileContentList);
                dockerImageBuildHistory.setActualDockerfileContent(dockerfileContent);

                String path = CommonUtils.getUploadPath();
                String uuid = CommonUtils.getUUID();
                dockerfileFolder = new File(path + uuid);
                if (!dockerfileFolder.exists()) {
                    dockerfileFolder.mkdirs();
                }
                tempDockerfile = new File(path + uuid + "/Dockerfile");
                logger.info("tmpfile name is {}", tempDockerfile.getName());

                BufferedWriter writer = new BufferedWriter(new FileWriter(tempDockerfile));


                writer.write(dockerfileContent);
                writer.close();

                System.out.println("Temp file created: " + tempDockerfile.getAbsolutePath());


                String imageId = dockerClient.buildImageCmd()
                        .withDockerfile(tempDockerfile)
                        .withTags(tag)
                        .withLabels(new HashMap<String, String>() {{
                            put("kwk.description", dockerImageBuildHistory.getDescription());
                            put("kwk.version", dockerImageBuildHistory.getVersion());
                            put("kwk.target.deployment", dockerImageBuildHistory.getTargetDeployment());
                        }})
                        .start().awaitImageId(3, TimeUnit.MINUTES);

                dockerImageBuildHistory.setImageId(imageId);
                dockerImageBuildHistory.setBuildStatus((byte) 1);


            } catch (Exception e) {
                e.printStackTrace();
                String errInfo = e.getMessage();
                dockerImageBuildHistory.setErrInfo(Optional.ofNullable(errInfo).orElse(""));
                dockerImageBuildHistory.setBuildStatus((byte) 2);
            } finally {
                if (tempDockerfile != null) {
                    tempDockerfile.delete();
                }
                if (dockerfileFolder != null) {
                    dockerfileFolder.delete();
                }
                if (tempUpdateFile != null) {
                    tempUpdateFile.delete();
                }
                if (tempUploadFileName != null && temporaryUploadFileFilePath != null) {
                    String temporaryUploadFileFolder = temporaryUploadFileFilePath.replace(tempUploadFileName, "");
                    File file = new File(temporaryUploadFileFolder);
                    if (file.exists() && !file.isFile()) {
                        file.delete();
                    }

                }

            }
            dockerImageBuildHisotyRepository.save(dockerImageBuildHistory);

        }

    }

    /**
     * 部署
     * https://github.com/kubernetes-client/java/blob/da520d53a9515b5525103dcb75344a8449ccc708/extended/src/test/java/io/kubernetes/client/extended/kubectl/KubectlApplyTest.java
     * https://github.com/kubernetes-client/java/blob/1269a950e8746478225c98684fb683b6892262c9/examples/examples-release-15/src/main/java/io/kubernetes/client/examples/PatchExample.java
     *
     * @param deployHistoryId
     */
    @Async
    @Override
    public void deployImage(Long deployHistoryId) {

        Optional<DockerImageDeployHistory> byId = dockerImageDeployHistoryRepository.findById(deployHistoryId);
        if (byId.isPresent()) {
            DockerImageDeployHistory dockerImageDeployHistory = byId.get();
            try {
                Long imageBuildId = dockerImageDeployHistory.getImageBuildId();

                Long k8sId = dockerImageDeployHistory.getK8sId();
                ApiClient apiClient = K8sUtils.apiClient(k8sId);
                AppsV1Api appsV1Api = K8sUtils.appsV1Api(apiClient);

                String targetDeployment = dockerImageDeployHistory.getTargetDeployment();
                DockerImageBuildHistory dockerImageBuildHistory = dockerImageBuildHisotyRepository.findById(imageBuildId).orElse(null);


                if (dockerImageBuildHistory != null) {

                    //                dockerImageBuildHistory.get
                    V1Deployment v1Deployment = Kubectl.get(V1Deployment.class).namespace(Namespaces.NAMESPACE_DEFAULT).name(targetDeployment).apiClient(apiClient).execute();
                    V1ObjectMeta metadata = v1Deployment.getMetadata();
                    String name = metadata.getName();
                    metadata.getAnnotations().put("kubernetes.io/change-cause", dockerImageDeployHistory.getDescription());


                    logger.info("name = {}", name);

                    Integer replicas = v1Deployment.getSpec().getReplicas();
                    if (replicas == null || replicas == 0) {
                        v1Deployment.getSpec().setReplicas(1);
                    }

                    Map<String, String> annotations = DeploymentUtils.findAnnotations(v1Deployment);
                    String lastAppliedConfiguration = annotations.get("kubectl.kubernetes.io/last-applied-configuration");


                    v1Deployment.getSpec().getTemplate().getSpec().getContainers().get(0).setImage(dockerImageDeployHistory.getImageTag());
                    v1Deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getResources().setRequests(null);
                    v1Deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getResources().setLimits(null);

                    V1Deployment deployment = appsV1Api.replaceNamespacedDeployment(targetDeployment, Namespaces.NAMESPACE_DEFAULT, v1Deployment, null, null, null, null);

                    dockerImageDeployHistory.setDeployStatus((byte) 1);
                    dockerImageDeployHistoryRepository.save(dockerImageDeployHistory);

                    AtomicReference<String> errInfo = new AtomicReference<>("");
//                    boolean poll = Wait.poll(
//                            Duration.ofSeconds(10),
//                            Duration.ofSeconds(5),
//                            () -> {
//                                try {
//
//                                    System.out.println("Waiting until  deployment deployed successfully...");
//                                    boolean resut = appsV1Api
//                                            .readNamespacedDeployment(v1Deployment.getMetadata().getName(), v1Deployment.getMetadata().getNamespace(), null)
//                                            .getStatus()
//                                            .getReadyReplicas()
//                                            > 0;
//                                    System.out.println("resut = " +resut);
//                                    return  resut;
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    errInfo.set(e.getMessage());
//                                    return false;
//                                }
//                            });
                    boolean poll = false;
                    // 重复10次，间隔5秒
                    for (int i = 0; i < 10; i++) {
                        poll = appsV1Api
                                            .readNamespacedDeployment(v1Deployment.getMetadata().getName(), v1Deployment.getMetadata().getNamespace(), null)
                                            .getStatus()
                                            .getReadyReplicas()
                                            > 0;
                        if (poll) {
                            break;
                        }
                        Thread.sleep(1000L * 5);
                    }


                    if (poll) {
                        dockerImageDeployHistory.setDeployStatus((byte) 1);
                    } else {
                        dockerImageDeployHistory.setDeployStatus((byte) 2);
                    }

                    dockerImageDeployHistory.setErrInfo(errInfo.get());
                    dockerImageDeployHistoryRepository.save(dockerImageDeployHistory);

                }

            } catch (Exception e) {
                e.printStackTrace();
                dockerImageDeployHistory.setDeployStatus((byte) 2);
                dockerImageDeployHistory.setErrInfo(e.getMessage());
                dockerImageDeployHistoryRepository.save(dockerImageDeployHistory);
            }


        }

//
    }
}
