package org.freeone.k8s.web.knife.controller;


import org.freeone.k8s.web.knife.entity.DockerfileTemplate;
import org.freeone.k8s.web.knife.repository.DockerfileTemplateRepository;
import org.freeone.k8s.web.knife.repository.TemporaryFileRepository;
import org.freeone.k8s.web.knife.utils.ResultKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dockerfile")
public class DockerfileManagerController {

    @Autowired
    private DockerfileTemplateRepository dockerfileTemplateRepository;


    @RequestMapping("/list")
    public ResultKit list(@RequestParam Long dockerId) {
        List<DockerfileTemplate> all = dockerfileTemplateRepository.findAllByTargetDockerIdOrderByIdDesc(dockerId);
        return ResultKit.okWithData(all);
    }

    @RequestMapping("/get")
    public ResultKit get (@RequestParam Long id) {
        DockerfileTemplate dockerfileTemplate = dockerfileTemplateRepository.findById(id).orElse(null);
        return ResultKit.okWithData(dockerfileTemplate);
    }

    @RequestMapping("/add")
    public ResultKit add(@RequestParam Long dockerId, @RequestParam String content, @RequestParam String templateName) {
        DockerfileTemplate dockerfileTemplate = new DockerfileTemplate();
        dockerfileTemplate.setTemplateName(templateName);
        dockerfileTemplate.setContent(content);
        dockerfileTemplate.setIsReadonly(false);
        dockerfileTemplate.setCreateTime(new Date());
        dockerfileTemplate.setTargetDockerId(dockerId);
        dockerfileTemplateRepository.save(dockerfileTemplate);
        return ResultKit.okWithData(dockerfileTemplate.getId());
    }

    @RequestMapping("/edit")
    public ResultKit edit(@RequestParam Long id, @RequestParam String content, @RequestParam String templateName) {
        Optional<DockerfileTemplate> byId = dockerfileTemplateRepository.findById(id);
        if (byId.isPresent()) {
            DockerfileTemplate dockerfileTemplate = byId.get();
            if (dockerfileTemplate.getIsReadonly()) {
                return ResultKit.failed("示例模板,不可编辑");
            }
            dockerfileTemplate.setTemplateName(templateName);
            dockerfileTemplate.setContent(content);
            dockerfileTemplateRepository.save(dockerfileTemplate);
        }
        return ResultKit.ok();
    }

    @RequestMapping("/delete")
    public ResultKit delete(@RequestParam Long id) {
        dockerfileTemplateRepository.deleteById(id);
        return ResultKit.ok();
    }


}
