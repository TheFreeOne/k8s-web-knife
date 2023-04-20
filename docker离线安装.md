# Docker离线安装

### 一、安装步骤

下载 Docker 二进制文件（离线安装包）

下载地址：https://download.docker.com/linux/static/stable/x86_64/

通过 FTP工具将 docker-20.10.17.tgz   上传到服务器上

解压安装包
```shell
tar -zxvf docker-20.10.17.tgz
```
4. 将docker 相关命令拷贝到 /usr/bin，方便直接运行命令
```shell
sudo cp docker/* /usr/bin/
```
   

5. 启动Docker守护程序
```shell
sudo dockerd &
```
   

6. 验证是否安装成功，执行docker info命令，若正常打印版本信息则安装成功。
```shell
sudo docker info
```
   
### 二、kill docker服务后
```shell
ps -ef|grep docker

kill -9 docker的pid
```


### 三、将docker注册成系统服务

1. 在 `/usr/lib/systemd/system/` 目录下创建`docker.service` 文件
```shell
sudo vi /usr/lib/systemd/system/docker.service
```

```shell
[Unit]
Description=Docker Application Container Engine
Documentation=https://docs.docker.com
After=network-online.target firewalld.service
Wants=network-online.target
 
[Service]
Type=notify
# ExecStart=/usr/bin/dockerd  -H fd:// --containerd=/run/containerd/containerd.sock -H 0.0.0.0:2375
# ExecStart=/usr/bin/dockerd  -H fd:// --containerd=/var/run/docker/containerd/containerd.sock -H 0.0.0.0:2375

# 使用127.0.0.1 可以让主机外无法访问， 使用其他端口
# ExecStart=/usr/bin/dockerd \
#  --tlsverify \
#        --tlscacert=/root/tls/pem/ca.pem \
#        --tlscert=/root/tls/pem/server-cert.pem \
#        --tlskey=/root/tls/pem/server-key.pem \
#         -H tcp://0.0.0.0:2375 \
#        -H unix://var/run/docker.sock 
ExecStart=/usr/bin/dockerd
ExecReload=/bin/kill -s HUP $MAINPID
LimitNOFILE=infinity
LimitNPROC=infinity
TimeoutStartSec=0
Delegate=yes
KillMode=process
Restart=on-failure
StartLimitBurst=3
StartLimitInterval=60s
 
[Install]
WantedBy=multi-user.target
```

2. 启动 / 停止 docker 服务
```shell
  sudo systemctl start docker
  
  sudo systemctl stop docker
```
 


3. 开机自启/取消开机自启 docker 服务
```shell
sudo systemctl enable docker

sudo systemctl disable docker
```

```shell
└─# docker -h  
Flag shorthand -h has been deprecated, please use --help

Usage:  docker [OPTIONS] COMMAND

A self-sufficient runtime for containers

Options:
      --config string      Location of client config files (default "/root/.docker")
  -c, --context string     Name of the context to use to connect to the daemon (overrides DOCKER_HOST env var and default context set with "docker context use")
  -D, --debug              Enable debug mode
  -H, --host list          Daemon socket(s) to connect to
  -l, --log-level string   Set the logging level ("debug"|"info"|"warn"|"error"|"fatal") (default "info")
      --tls                Use TLS; implied by --tlsverify
      --tlscacert string   Trust certs signed only by this CA (default "/root/.docker/ca.pem")
      --tlscert string     Path to TLS certificate file (default "/root/.docker/cert.pem")
      --tlskey string      Path to TLS key file (default "/root/.docker/key.pem")
      --tlsverify          Use TLS and verify the remote
  -v, --version            Print version information and quit

Management Commands:
  builder     Manage builds
  config      Manage Docker configs
  container   Manage containers
  context     Manage contexts
  image       Manage images
  manifest    Manage Docker image manifests and manifest lists
  network     Manage networks
  node        Manage Swarm nodes
  plugin      Manage plugins
  secret      Manage Docker secrets
  service     Manage services
  stack       Manage Docker stacks
  swarm       Manage Swarm
  system      Manage Docker
  trust       Manage trust on Docker images
  volume      Manage volumes

Commands:
  attach      Attach local standard input, output, and error streams to a running container
  build       Build an image from a Dockerfile
  commit      Create a new image from a container's changes
  cp          Copy files/folders between a container and the local filesystem
  create      Create a new container
  diff        Inspect changes to files or directories on a container's filesystem
  events      Get real time events from the server
  exec        Run a command in a running container
  export      Export a container's filesystem as a tar archive
  history     Show the history of an image
  images      List images
  import      Import the contents from a tarball to create a filesystem image
  info        Display system-wide information
  inspect     Return low-level information on Docker objects
  kill        Kill one or more running containers
  load        Load an image from a tar archive or STDIN
  login       Log in to a Docker registry
  logout      Log out from a Docker registry
  logs        Fetch the logs of a container
  pause       Pause all processes within one or more containers
  port        List port mappings or a specific mapping for the container
  ps          List containers
  pull        Pull an image or a repository from a registry
  push        Push an image or a repository to a registry
  rename      Rename a container
  restart     Restart one or more containers
  rm          Remove one or more containers
  rmi         Remove one or more images
  run         Run a command in a new container
  save        Save one or more images to a tar archive (streamed to STDOUT by default)
  search      Search the Docker Hub for images
  start       Start one or more stopped containers
  stats       Display a live stream of container(s) resource usage statistics
  stop        Stop one or more running containers
  tag         Create a tag TARGET_IMAGE that refers to SOURCE_IMAGE
  top         Display the running processes of a container
  unpause     Unpause all processes within one or more containers
  update      Update configuration of one or more containers
  version     Show the Docker version information
  wait        Block until one or more containers stop, then print their exit codes

Run 'docker COMMAND --help' for more information on a command.

To get more help with docker, check out our guides at https://docs.docker.com/go/guides/

```

### 开启 api加密

#### 开启api
```shell
ExecStart=/usr/bin/dockerd  -H tcp://0.0.0.0:2375 -H unix://var/run/docker.sock 
```

#### 加密

```shell

# 使用127.0.0.1 可以让主机外无法访问， 使用其他端口
ExecStart=/usr/bin/dockerd \
             -H tcp://0.0.0.0:2375 \
             --tlsverify \
             --tlscacert=/usr/local/ca.pem \
             --tlscert=/usr/local/server-cert.pem \
             --tlskey=/usr/local/server-key.pem \
               -H unix://var/run/docker.sock  
```

加密 tls.sh
```shell
#!/bin/bash
mkdir -p /home/kali/tls/pem
# 获取ip
DOMAIN_HOST=`ifconfig eth0 | grep "inet" | awk '{ print $2}' | sed -n '1p;1q'`
#DOMAIN_HOST=`hostname` #选择域名方案最好
HOST=$DOMAIN_HOST
# 自定义密码
PASSWORD="123456"
# 国家
COUNTRY=CN
# 省份
PROVINCE=gd
# 城市
CITY=gz
# 组织
ORGANIZATION=orgname
#
GROUP=agroup
# 有效多少年
YEARS=100

DAYS=$((365 * YEARS))
# 名称
NAME=personnalname
SUBJ="/C=$COUNTRY/ST=$PROVINCE/L=$CITY/O=$ORGANIZATION/OU=$GROUP/CN=$HOST"
# 自定义信息
#============================================================================================
#此形式是自己给自己签发证书,自己就是CA机构,也可以交给第三方机构去签发
# 生成根证书RSA私钥,password作为私钥密码（身份证）
echo "A.生成根证书RSA私钥,password作为私钥密码（身份证）"
openssl genrsa -passout pass:$PASSWORD -aes256 -out ./ca-key.pem 4096
# 2.用根证书RSA私钥生成自签名的根证书(营业执照)
echo "B.用根证书RSA私钥生成自签名的根证书(营业执照)"
openssl req -new -x509 -days $DAYS -passin pass:$PASSWORD -key ./ca-key.pem -sha256 -subj "$SUBJ" -out ./ca.pem
#============================================================================================
# 给服务器签发证书
echo "给服务器签发证书"
# 1.服务端生成自己的私钥
echo "1.服务端生成自己的私钥"
openssl genrsa -out ./server-key.pem 4096
# 2.服务端生成证书(里面包含公钥与服务端信息)

echo "2.服务端生成证书(里面包含公钥与服务端信息)"

openssl req -new -sha256 -key ./server-key.pem -out ./server.csr -subj "/CN=$DOMAIN_HOST"
# 3.通过什么形式与我进行连接,可设置多个IP地扯用逗号分隔
echo "3.通过什么形式与我进行连接,可设置多个IP地扯用逗号分隔"
echo subjectAltName=IP:"$DOMAIN_HOST",IP:0.0.0.0 > ./extfile.cnf
# 4.权威机构对证书进行进行盖章生效
echo "4.权威机构对证书进行进行盖章生效"
openssl x509 -passin pass:$PASSWORD -req -days $DAYS -sha256 -in ./server.csr -CA ./ca.pem -CAkey ./ca-key.pem -CAcreateserial -out ./server-cert.pem -extfile ./extfile.cnf
#============================================================================================
#给客户端签发证书
echo "给客户端签发证书"
openssl genrsa -out ./client-key.pem 4096
openssl req -subj '/CN=client' -new -key ./client-key.pem -out ./client.csr
echo extendedKeyUsage = clientAuth > ./extfile.cnf
# 证书有效期 365
openssl x509 -passin pass:$PASSWORD -req -days $DAYS -sha256 -in ./client.csr -CA ./ca.pem -CAkey ./ca-key.pem -CAcreateserial -out ./client-cert.pem -extfile ./extfile.cnf
#============================================================================================
# 清理文件
rm -rf ./ca-key.pem
rm -rf ./{server,client}.csr
rm -rf ./ca.srl
# 最终文件
echo "最终文件"
# ca.pem  ==  CA机构证书
echo "ca.pem  ==  CA机构证书"
# client-cert.pem  ==  客户端证书
echo "client-cert.pem  ==  客户端证书"
# client-key.pem  ==  客户私钥
echo "client-key.pem  ==  客户私钥"
# server-cert.pem  == 服务端证书
echo "server-cert.pem  == 服务端证书"
# server-key.pem  ==  服务端私钥
echo "server-key.pem  ==  服务端私钥"

```


相关问题

[docker警告：Your kernel does not support cgroup swap limit capabilities](https://blog.csdn.net/songyu0120/article/details/89170458)


