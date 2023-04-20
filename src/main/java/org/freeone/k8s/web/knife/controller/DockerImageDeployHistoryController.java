package org.freeone.k8s.web.knife.controller;

import org.freeone.k8s.web.knife.entity.DockerImageDeployHistory;
import org.freeone.k8s.web.knife.repository.DockerImageDeployHistoryRepository;
import org.freeone.k8s.web.knife.utils.ResultKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/docker/image/deploy/history")
public class DockerImageDeployHistoryController {

    @Autowired
    private DockerImageDeployHistoryRepository dockerImageDeployHistoryRepository;

    @RequestMapping("/page")
    public ResultKit page(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        Specification<DockerImageDeployHistory> specification = new Specification<DockerImageDeployHistory>() {
            @Override
            public Predicate toPredicate(Root<DockerImageDeployHistory> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        Sort sort = Sort.by(Sort.Order.desc("id"));
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<DockerImageDeployHistory> page = dockerImageDeployHistoryRepository.findAll(specification, pageRequest);
        return ResultKit.okWithData(page);
    }

    @RequestMapping("/delete")
    public ResultKit delete(Long id) {
        dockerImageDeployHistoryRepository.deleteById(id);
        return ResultKit.ok();
    }
}
