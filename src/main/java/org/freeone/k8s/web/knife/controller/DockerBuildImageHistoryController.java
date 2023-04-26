package org.freeone.k8s.web.knife.controller;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import org.apache.commons.lang3.StringUtils;
import org.freeone.k8s.web.knife.entity.DockerImageBuildHistory;
import org.freeone.k8s.web.knife.entity.DockerImageDeployHistory;
import org.freeone.k8s.web.knife.entity.TemporaryFile;
import org.freeone.k8s.web.knife.repository.DockerImageBuildHistoryRepository;
import org.freeone.k8s.web.knife.repository.DockerImageDeployHistoryRepository;
import org.freeone.k8s.web.knife.repository.TemporaryFileRepository;
import org.freeone.k8s.web.knife.repository.UploadFileHistoryRepository;
import org.freeone.k8s.web.knife.service.DockerBuildService;
import org.freeone.k8s.web.knife.utils.CommonUtils;
import org.freeone.k8s.web.knife.utils.DockerUtils;
import org.freeone.k8s.web.knife.utils.ResultKit;
import org.freeone.k8s.web.knife.utils.ResultKitFailedEnum;
import org.hibernate.annotations.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.QueryHint;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/docker/image/build/history")
public class DockerBuildImageHistoryController {

    @Autowired
    private DockerImageBuildHistoryRepository dockerImageBuildHisotyRepository;
    @Autowired
    private DockerImageDeployHistoryRepository dockerImageDeployHistoryRepository;

    @Autowired
    private UploadFileHistoryRepository uploadFileHistoryRepository;


    @Autowired
    private DockerBuildService dockerBuildService;

    @Autowired
    private TemporaryFileRepository temporaryFileRepository;

    @RequestMapping("/page")
    public ResultKit pagelist(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {


        Specification<DockerImageBuildHistory> specification = new Specification<DockerImageBuildHistory>() {
            @Override
            public Predicate toPredicate(Root<DockerImageBuildHistory> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
//                predicates.add(criteriaBuilder.equal(root.get("")))
//                criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
                Order idDesc = criteriaBuilder.desc(root.get("id"));
//                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).orderBy(idDesc).getRestriction();
                // 这样就没有where 1 = 1了
                criteriaQuery.orderBy(idDesc);
                return criteriaQuery.getRestriction();
//                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        Sort sort = Sort.by(Sort.Order.desc("id"));
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        Page<DockerImageBuildHistory> page = dockerImageBuildHisotyRepository.findAll(specification, pageRequest);
        return ResultKit.okWithData(page);
    }

    @PostMapping("/uploadWarOrJar")
    public ResultKit uploadWarOrJar(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            return ResultKit.failed("没有文件");
        }
        String originalFilename = file.getOriginalFilename();

        if (!originalFilename.toLowerCase().endsWith(".jar") && !originalFilename.toLowerCase().endsWith(".war")) {
            return ResultKit.failed(ResultKitFailedEnum.FILE_TYPE_ERROR);
        }
        String path = CommonUtils.getUploadPath();


        try {
            String uuid = CommonUtils.getUUID();
            String uploadPath = path + uuid + "/" + originalFilename;
//            String uploadPath = path + originalFilename;
            File destFile = new File(uploadPath);
            if (!destFile.getParentFile().exists()) {
                destFile.mkdirs();
            }
            file.transferTo(destFile);
            TemporaryFile temporaryFile = new TemporaryFile();
            temporaryFile.setFilePath(uploadPath);
            temporaryFile.setCreateTime(new Date());
            temporaryFile.setTicket(uuid);
            temporaryFileRepository.save(temporaryFile);
            return ResultKit.okWithData(temporaryFile.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultKit.failed(e.getMessage() != null ? e.getMessage() : "系统异常");

        }
    }

    /**
     * 创建构建任务
     *
     * @param version
     * @param description
     * @param targetDeployment
     * @param dockerId
     * @param dockerfileId
     * @return
     * @throws Exception
     */
    @PostMapping("/createBuildTask")
    public final ResultKit createBuildTask(@RequestParam String version, @RequestParam String description, @RequestParam String targetDeployment, @RequestParam Long dockerId, @RequestParam Long dockerfileId, @RequestParam Long tempFileId, @RequestParam Long k8sId) throws Exception {
        if (StringUtils.isEmpty(version)) {
            version = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        }
        version = version.trim();

        String finalVersion = version;
        String tagString = targetDeployment + ":" + version;
        DockerImageBuildHistory buildHistory = new DockerImageBuildHistory();
        buildHistory.setVersion(version);
        buildHistory.setDescription(description);
        buildHistory.setTargetDeployment(targetDeployment);
        buildHistory.setCreateTime(new Date());
        buildHistory.setBuildStatus((byte) 0);
        buildHistory.setErrInfo("");
        buildHistory.setImageId("");
        buildHistory.setImageTag(tagString);
        buildHistory.setTargetDockerId(dockerId);
        buildHistory.setTempFileId(tempFileId);
        buildHistory.setTargetDockerfileId(dockerfileId);
        buildHistory.setActualDockerfileContent("");
        buildHistory.setK8sId(k8sId);
        dockerImageBuildHisotyRepository.save(buildHistory);
        Long buildHistoryId = buildHistory.getId();
        dockerBuildService.buildImage(buildHistoryId);
        return ResultKit.ok();
    }

    @RequestMapping("/getBuildTask")
    public ResultKit getBuildTask(@RequestParam Long id) throws Exception {
        Optional<DockerImageBuildHistory> byId = dockerImageBuildHisotyRepository.findById(id);
        DockerImageBuildHistory dockerImageBuildHistory = byId.orElse(null);
        return ResultKit.okWithData(dockerImageBuildHistory);
    }

    /**
     * 发布
     * @param version
     * @param description
     * @param targetDeployment
     * @param imageTag
     * @param imageBuildId
     * @param k8sId
     * @return
     * @throws Exception
     */
    @PostMapping("/createDeployTask")
    public ResultKit createDeployTask(@RequestParam String version, @RequestParam String description, @RequestParam String targetDeployment, @RequestParam String imageTag, @RequestParam Long imageBuildId, @RequestParam Long k8sId) throws Exception {
        if (StringUtils.isEmpty(version)) {
            version = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        }
        version = version.trim();

        // TODO: 2023-03-22 校验镜像是否存在


        DockerImageBuildHistory dockerImageBuildHistory = dockerImageBuildHisotyRepository.findById(imageBuildId).orElse(null);
        if (dockerImageBuildHistory ==null) {
            return ResultKit.failed("未找到镜像的构建历史");
        }
        Long targetDockerId = dockerImageBuildHistory.getTargetDockerId();
        String imageName = dockerImageBuildHistory.getImageTag();

        DockerClient connect = DockerUtils.connect(targetDockerId);
        List<Image> exec = connect.listImagesCmd().withImageNameFilter(imageName).exec();
        if (exec==null ||exec.isEmpty()) {
            return ResultKit.failed("不存在的docker镜像");
        }


        DockerImageDeployHistory deployHistory = new DockerImageDeployHistory();
        deployHistory.setVersion(version);
        deployHistory.setDescription(description);
        deployHistory.setK8sId(k8sId);
        deployHistory.setTargetDeployment(targetDeployment);
        deployHistory.setCreateTime(new Date());
        deployHistory.setDeployStatus((byte) 0);
        deployHistory.setErrInfo("");
        deployHistory.setImageBuildId(imageBuildId);
        deployHistory.setImageTag(imageTag);
        dockerImageDeployHistoryRepository.save(deployHistory);
        Long deployHistoryId = deployHistory.getId();
        dockerBuildService.deployImage(deployHistoryId);
        return ResultKit.ok();
    }

    @PostMapping("/delete")
    public ResultKit delete(@RequestParam Long id) {
        Optional<DockerImageBuildHistory> byId = dockerImageBuildHisotyRepository.findById(id);
        if (byId.isPresent()) {
            dockerImageBuildHisotyRepository.deleteById(id);
            return ResultKit.ok();
        } else {
            return ResultKit.failed("不存在");
        }

    }
}
