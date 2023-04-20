### open /run/flannel/subnet.env: no such file or directory

```shell

vim /run/flannel/subnet.env

FLANNEL_NETWORK=10.244.0.0/16
FLANNEL_SUBNET=10.244.0.1/24
FLANNEL_MTU=1450
FLANNEL_IPMASQ=true

```

上面的示例中，Flannel配置了以下内容：
- FLANNEL_NETWORK：整个Kubernetes集群中Flannel使用的CIDR范围（子网）。
- FLANNEL_SUBNET：节点访问Flannel子网的IP地址和子网掩码。
- FLANNEL_MTU：节点上的Flannel网卡的MTU值。
- FLANNEL_IPMASQ：用于NAT整个子网的IP aacking。
