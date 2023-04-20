package org.freeone.k8s.web.knife.utils;

import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeAddress;
import io.kubernetes.client.openapi.models.V1NodeCondition;
import io.kubernetes.client.openapi.models.V1NodeSpec;
import io.kubernetes.client.openapi.models.V1NodeStatus;
import io.kubernetes.client.openapi.models.V1NodeSystemInfo;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.freeone.k8s.web.knife.entity.vo.NodeAddressVo;
import org.freeone.k8s.web.knife.entity.vo.NodeConditionVo;
import org.freeone.k8s.web.knife.entity.vo.NodeVo;
import org.freeone.k8s.web.knife.entity.vo.QuantityVo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NodeUtils {

    public static final NodeVo v1Node2NodeVo(V1Node v1Node) {
        NodeVo nodeVo = new NodeVo();


        V1ObjectMeta metadata = v1Node.getMetadata();
        OffsetDateTime creationTimestamp = metadata.getCreationTimestamp();
        V1NodeStatus status = v1Node.getStatus();
        V1NodeSpec spec = v1Node.getSpec();

        String podCIDR = spec.getPodCIDR();
        List<String> podCIDRs = spec.getPodCIDRs();


        List<V1NodeAddress> addresses = status.getAddresses();
        List<NodeAddressVo> addressVos = new ArrayList<>();

        if (addresses != null) {
            for (V1NodeAddress address : addresses) {
                NodeAddressVo nodeAddressVo = new NodeAddressVo();
                nodeAddressVo.setType(address.getType());
                nodeAddressVo.setAddress(address.getAddress());
                addressVos.add(nodeAddressVo);
            }
        }
        String roles = null;
        Map<String, String> labels = metadata.getLabels();
        if (labels != null) {

            for (Map.Entry<String, String> stringStringEntry : labels.entrySet()) {

                String key = stringStringEntry.getKey();
                if (key.startsWith("node-role.kubernetes.io/")) {
                    roles = key.replace("node-role.kubernetes.io/", "");
                }

            }

        }

        List<V1NodeCondition> conditions = status.getConditions();
        List<NodeConditionVo> conditionVos = new ArrayList<>();
        if (conditions != null) {
            for (V1NodeCondition condition : conditions) {

                NodeConditionVo nodeConditionVo = new NodeConditionVo();
                nodeConditionVo.setMessage(condition.getMessage())
                        .setReason(condition.getReason())
                        .setStatus(condition.getStatus())
                        .setType(condition.getType())
                        .setLastHeartbeatTime(CommonUtils.offsetDateTime2Date(condition.getLastHeartbeatTime()))
                        .setLastTransitionTime(CommonUtils.offsetDateTime2Date(condition.getLastTransitionTime()));


                conditionVos.add(nodeConditionVo);
            }
        }
        List<QuantityVo> capacityList = new ArrayList<>();
        List<QuantityVo> allocatableList = new ArrayList<>();
        Map<String, Quantity> capacity = status.getCapacity();
        Map<String, Quantity> allocatable = status.getAllocatable();

        if (capacity != null) {
            for (Map.Entry<String, Quantity> capacityQuantityEntry : capacity.entrySet()) {

                String key = capacityQuantityEntry.getKey();
                Quantity value = capacityQuantityEntry.getValue();

                String suffixedString = value.toSuffixedString();
                BigDecimal number = value.getNumber();


                QuantityVo quantityVo = new QuantityVo();
                quantityVo.setName(key).setNumberString(suffixedString).setNumber(number).setFormat(value.getFormat().toString());
                capacityList.add(quantityVo);
            }
        }

        if (allocatable != null) {
            for (Map.Entry<String, Quantity> allocatableQuantityEntry : allocatable.entrySet()) {

                String key = allocatableQuantityEntry.getKey();
                Quantity value = allocatableQuantityEntry.getValue();

                String suffixedString = value.toSuffixedString();
                BigDecimal number = value.getNumber();


                QuantityVo quantityVo = new QuantityVo();
                quantityVo.setName(key).setNumberString(suffixedString).setNumber(number).setFormat(value.getFormat().toString());
                allocatableList.add(quantityVo);
            }
        }


        V1NodeSystemInfo nodeInfo = status.getNodeInfo();
        String uid = metadata.getUid();
        Date createTime = CommonUtils.offsetDateTime2Date(creationTimestamp);
        nodeVo.setUid(uid).setName(metadata.getName())
                .setKernelVersion(nodeInfo.getKernelVersion())
                .setKubeletVersion(nodeInfo.getKubeletVersion())
                .setKubeProxyVersion(nodeInfo.getKubeProxyVersion())
                .setOsImage(nodeInfo.getOsImage())
                .setContainerRuntimeVersion(nodeInfo.getContainerRuntimeVersion())
                .setCreationTime(createTime)
                .setAddresses(addressVos)
                .setRoles(roles)
                .setConditionVos(conditionVos)
                .setPodCIDR(podCIDR)
                .setPodCIDRs(podCIDRs).setCapacityList(capacityList).setAllocatableList(allocatableList);

        return nodeVo;
    }
}
