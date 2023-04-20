package org.freeone.k8s.web.knife.controller;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.freeone.k8s.web.knife.entity.DockerApiConfig;
import org.freeone.k8s.web.knife.entity.DockerfileTemplate;
import org.freeone.k8s.web.knife.repository.DockerApiConfigRepository;
import org.freeone.k8s.web.knife.repository.DockerfileTemplateRepository;
import org.freeone.k8s.web.knife.utils.CommonUtils;
import org.freeone.k8s.web.knife.utils.DockerUtils;
import org.freeone.k8s.web.knife.utils.ResultKit;
import org.freeone.k8s.web.knife.utils.ResultKitFailedEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/docker/image")
public class DockerImageController {

    @Value("classpath:DockerfileWithCopy")
    private Resource dockerfileWithCopy;

    @Value("classpath:DockerfileWithAdd")
    private Resource dockerfileWithAdd;

    @Autowired
    private DockerApiConfigRepository dockerApiRecordRepository;

    @Autowired
    private DockerfileTemplateRepository dockerfileTemplateRepository;

    /**
     * 增加默认模板
     *
     * @param hostPath
     * @param dockerName
     * @param apiVersion
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    @RequestMapping("/addDockerApiRecord")
    public ResultKit addDockerApiRecord(@RequestParam String hostPath, @RequestParam String dockerName, String apiVersion, String clientCertPem, String clientKeyPem) throws Exception {
        Date now = new Date();
        if (StringUtils.isNoneBlank(clientCertPem, clientKeyPem)) {
            DockerClient connect = DockerUtils.connect(hostPath, apiVersion, clientCertPem, clientKeyPem);
        } else {
            DockerClient connect = DockerUtils.connect(hostPath, apiVersion);
        }


        DockerApiConfig dockerApiRecord = new DockerApiConfig();
        dockerApiRecord.setDockerName(dockerName);
        dockerApiRecord.setHostPath(hostPath);
        dockerApiRecord.setApiVersion(apiVersion);
        dockerApiRecord.setCreateTime(now);
        dockerApiRecord.setClientCertPem(clientCertPem);
        dockerApiRecord.setClientKeyPem(clientKeyPem);
        dockerApiRecordRepository.save(dockerApiRecord);

        Long targetDockerId = dockerApiRecord.getId();


        DockerfileTemplate dockerfileTemplate = new DockerfileTemplate();
        dockerfileTemplate.setTargetDockerId(targetDockerId);
        dockerfileTemplate.setIsReadonly(true);
        String content = FileUtils.readFileToString(dockerfileWithAdd.getFile(), Charset.defaultCharset());
        dockerfileTemplate.setContent(content);
        dockerfileTemplate.setCreateTime(now);
        dockerfileTemplate.setTemplateName(dockerName + " " + "示例模板 add");
        dockerfileTemplateRepository.save(dockerfileTemplate);


        DockerfileTemplate dockerfileTemplateAdd = new DockerfileTemplate();
        dockerfileTemplateAdd.setTargetDockerId(targetDockerId);
        dockerfileTemplateAdd.setIsReadonly(true);
        String contentCopy = FileUtils.readFileToString(dockerfileWithCopy.getFile(), Charset.defaultCharset());
        dockerfileTemplateAdd.setContent(contentCopy);
        dockerfileTemplateAdd.setCreateTime(now);
        dockerfileTemplateAdd.setTemplateName(dockerName + " " + "示例模板 Copy");
        dockerfileTemplateRepository.save(dockerfileTemplateAdd);

        return ResultKit.okWithData(targetDockerId);

    }

    @RequestMapping("/editDockerApiRecord")
    public ResultKit editDockerApiRecord(@RequestParam Long id, @RequestParam String hostPath, @RequestParam String dockerName, String apiVersion, String clientCertPem, String clientKeyPem) throws Exception {
        Date now = new Date();
        if (StringUtils.isNoneBlank(clientCertPem, clientKeyPem)) {
            DockerClient connect = DockerUtils.connect(hostPath, apiVersion, clientCertPem, clientKeyPem);
        } else {
            DockerClient connect = DockerUtils.connect(hostPath, apiVersion);
        }

        Optional<DockerApiConfig> byId = dockerApiRecordRepository.findById(id);
        if (byId.isPresent()) {
            DockerApiConfig dockerApiRecord = byId.get();
            dockerApiRecord.setDockerName(dockerName);
            dockerApiRecord.setHostPath(hostPath);
            dockerApiRecord.setApiVersion(apiVersion);
            dockerApiRecord.setCreateTime(now);
            dockerApiRecord.setClientCertPem(clientCertPem);
            dockerApiRecord.setClientKeyPem(clientKeyPem);
            dockerApiRecordRepository.save(dockerApiRecord);
        }
        return ResultKit.ok();

    }

    @RequestMapping("/getDockerApiRecord")
    public ResultKit getDockerApiRecord(@RequestParam Long dockerId) throws Exception {
        DockerApiConfig dockerApiRecord = dockerApiRecordRepository.findById(dockerId).orElse(null);
        return ResultKit.okWithData(dockerApiRecord);
    }
    @RequestMapping("/deleteDockerApiRecord")
    public ResultKit deleteDockerApiRecord(@RequestParam Long dockerId) throws Exception {
        if (dockerApiRecordRepository.findById(dockerId).isPresent()) {
            dockerApiRecordRepository.deleteById(dockerId);
        }
        return ResultKit.ok();
    }

    @RequestMapping("/deleteDockerImage")
    public ResultKit deleteDockerImage(@RequestParam String imageId, @RequestParam Long dockerId) throws Exception {
        DockerClient connect = DockerUtils.connect(dockerId);
        connect.removeImageCmd(imageId).exec();
        return ResultKit.ok();
    }
    @RequestMapping("/forceDeleteDockerImage")
    public ResultKit forceDeleteDockerImage(@RequestParam String imageId, @RequestParam Long dockerId) throws Exception {
        DockerClient connect = DockerUtils.connect(dockerId);
        connect.removeImageCmd(imageId).withForce(true).exec();
        return ResultKit.ok();
    }

    @RequestMapping("/listDockerApiRecord")
    public ResultKit listDockerApiRecord() {
        List<DockerApiConfig> all = dockerApiRecordRepository.findAll();
        return ResultKit.okWithData(all);
    }

    /**
     * 上传文件到docker所在的服务器并构建
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadToDockerServerAndBuild")
    public synchronized ResultKit uploadFile(@RequestParam MultipartFile file) {

        if (file.isEmpty()) {
            return ResultKit.failed("没有文件");
        }

        String originalFilename = file.getOriginalFilename();

        if (!originalFilename.toLowerCase().endsWith(".jar") && !originalFilename.toLowerCase().endsWith(".war")) {
            return ResultKit.failed(ResultKitFailedEnum.FILE_TYPE_ERROR);
        }
        String path = CommonUtils.getUploadPath();
        File destFile = new File(path + originalFilename);
        if (!destFile.getParentFile().exists()) {
            destFile.mkdirs();
        }
        try {
            file.transferTo(destFile);
            InputStream ins = dockerfileWithCopy.getInputStream();
            BufferedOutputStream bos = null;
            BufferedInputStream bis = new BufferedInputStream(ins);
            File fileAtRootPath = new File(CommonUtils.getUploadPath() + dockerfileWithCopy.getFilename());
            try {

                bos = new BufferedOutputStream(new FileOutputStream(fileAtRootPath));
                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                while ((bytesRead = bis.read(buffer, 0, 1024)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                throw new RuntimeException("上传文件压缩出错", e);
            } finally {
                if (ins != null) {
                    try {
                        ins.close();
                    } catch (IOException e) {
                    }
                    ins = null;
                }
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                    }
                    bos = null;
                }
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                    }
                    bis = null;
                }
            }


            ImmutableSet<String> tag = ImmutableSet.of("mytomcat:20230308001");
            DockerClient dockerClient = DockerUtils.connect();
            String imageId = dockerClient.buildImageCmd().withDockerfile(fileAtRootPath)
                    .withTags(tag)
                    .start().awaitImageId(3, TimeUnit.MINUTES);
//            String imageId = dockerClient.buildImageCmd().
//                    .withTags(tag)
//                    .start().awaitImageId(3, TimeUnit.MINUTES);


            return ResultKit.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultKit.failed(e.getMessage() != null ? e.getMessage() : "系统异常");

        }

    }

    /**
     * 上传文件，并构建，不会上传文件到服务器，但需要dockker所在的服务器等访问到 本服务器
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadAndBuild")
    public synchronized ResultKit uploadAndBuild(@RequestParam MultipartFile file) {

        if (file.isEmpty()) {
            return ResultKit.failed("没有文件");
        }

        String originalFilename = file.getOriginalFilename();

        if (!originalFilename.toLowerCase().endsWith(".jar") && !originalFilename.toLowerCase().endsWith(".war")) {
            return ResultKit.failed(ResultKitFailedEnum.FILE_TYPE_ERROR);
        }
        String path = CommonUtils.getUploadPath();
        File destFile = new File(path + originalFilename);
        if (!destFile.getParentFile().exists()) {
            destFile.mkdirs();
        }
        try {
            file.transferTo(destFile);
            ImmutableSet<String> tag = ImmutableSet.of("mytomcat:20230308003");
            DockerClient dockerClient = DockerUtils.connect();
            String imageId = dockerClient.buildImageCmd().withDockerfile(dockerfileWithAdd.getFile())
                    .withTags(tag)
                    .withLabels(new HashMap<String, String>() {{
                        put("remark", "测试");
                    }})
                    .start().awaitImageId(3, TimeUnit.MINUTES);

            return ResultKit.okWithData(imageId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultKit.failed(e.getMessage() != null ? e.getMessage() : "系统异常");

        }

    }

    @RequestMapping("/list")
    public ResultKit list() throws Exception {
        DockerClient connect = DockerUtils.connect();
        List<Image> images = connect.listImagesCmd().exec();
        images = images.stream().filter(image -> image.getParentId() != null && image.getParentId().length() > 0).collect(Collectors.toList());
        return ResultKit.okWithData(images);
    }

    @RequestMapping("/listAll")
    public ResultKit listAll(@RequestParam(defaultValue = "all") String imageType, @RequestParam Long dockerId) throws Exception {
        DockerClient connect = DockerUtils.connect(dockerId);
        List<Image> images = connect.listImagesCmd().exec();
        if ("parentIdNotEmpty".equals(imageType)) {
            images = images.stream().filter(image -> image.getParentId() != null && image.getParentId().length() > 0).collect(Collectors.toList());
        }
        return ResultKit.okWithData(images);
    }

}
