package org.freeone.k8s.web.knife.utils;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import org.freeone.k8s.web.knife.entity.vo.ServicePortVo;
import org.freeone.k8s.web.knife.entity.vo.ServiceVo;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceUtils {

    public static final ServiceVo v1Service2ServiceVo(V1Service v1Service) {
        V1ObjectMeta metadata = v1Service.getMetadata();
        V1ServiceSpec spec = v1Service.getSpec();

        String uid = metadata.getUid();
        String name = metadata.getName();
        String namespace = metadata.getNamespace();
        OffsetDateTime creationTimestamp = metadata.getCreationTimestamp();
        String clusterIP = spec.getClusterIP();
        List<String> clusterIPs = spec.getClusterIPs();
        Map<String, String> selector = spec.getSelector();
        String type = spec.getType();
        List<V1ServicePort> ports = spec.getPorts();
        List<ServicePortVo> portVoList = new ArrayList<>();
        if (ports != null) {
            for (V1ServicePort port : ports) {
                ServicePortVo servicePortVo = v1ServicePort2ServicePortVo(port);
                portVoList.add(servicePortVo);
            }
        }


        ServiceVo serviceVo = new ServiceVo()
                .setUid(uid)
                .setClusterIp(clusterIP)
                .setClusterIPs(clusterIPs)
                .setType(type)
                .setPorts(portVoList)
                .setNamespace(namespace)
                .setName(name)
                .setSelector(selector)
                .setCreateTime(CommonUtils.offsetDateTime2Date(creationTimestamp));
        return serviceVo;
    }

    public static final ServicePortVo v1ServicePort2ServicePortVo(V1ServicePort v1ServicePort) {
        String appProtocol = v1ServicePort.getAppProtocol();
        String name = v1ServicePort.getName();
        Integer nodePort = v1ServicePort.getNodePort();
        String protocol = v1ServicePort.getProtocol();
        Integer port = v1ServicePort.getPort();
        IntOrString targetPort = v1ServicePort.getTargetPort();

        ServicePortVo servicePortVo = new ServicePortVo()
                .setName(name)
                .setNodePort(nodePort)
                .setPort(port)
                .setProtocol(protocol)
                .setAppProtocol(appProtocol);
        if (targetPort != null) {
            if (targetPort.isInteger()) {
                servicePortVo.setTargetPortInt(targetPort.getIntValue());
            } else {
                servicePortVo.setTargetPortString(targetPort.getStrValue());
            }
        }


        return servicePortVo;
    }
}
