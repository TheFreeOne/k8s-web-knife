# buildkit离线安装

### 下载
[地址](https://github.com/moby/buildkit)

- 示例版本 buildkit-v0.10.6.linux-amd64.tar.gz

```shell
tar -zxvf buildkit-v0.10.6.linux-amd64.tar.gz  -C /usr/local/
```
### buildkit.service
```shell
vim /usr/lib/systemd/system/buildkit.service 
cat > /etc/systemd/system/buildkitd.service << EOF
[Unit]
Description=BuildKit
Documentation=https://github.com/moby/buildkit

[Service]
ExecStart=/usr/local/bin/buildkitd --oci-worker=false --containerd-worker=true  --addr fd://

[Install]
WantedBy=multi-user.target

EOF
```

```shell
[Unit]
Description=BuildKit
Requires=buildkit.socket
After=buildkit.socket
Documentation=https://github.com/moby/buildkit
[Service]
# Replace runc builds with containerd builds  
ExecStart=/usr/local/bin/buildkitd --addr fd://  
#unix:///run/buildkit/buildkitd.sock
[Install]
WantedBy=multi-user.target
```
```shell
└─# buildctl --help  
NAME:
   buildctl - build utility

USAGE:
   buildctl [global options] command [command options] [arguments...]

VERSION:
   v0.10.6

COMMANDS:
   du        disk usage
   prune     clean up build cache
   build, b  build
   debug     debug utilities
   help, h   Shows a list of commands or help for one command

GLOBAL OPTIONS:
   --debug                enable debug output in logs
   --addr value           buildkitd address (default: "unix:///run/buildkit/buildkitd.sock")
   --tlsservername value  buildkitd server name for certificate validation
   --tlscacert value      CA certificate for validation
   --tlscert value        client certificate
   --tlskey value         client key
   --tlsdir value         directory containing CA certificate, client certificate, and client key
   --timeout value        timeout backend connection after value seconds (default: 5)
   --help, -h             show help
   --version, -v          print the version


```
### socket配置文件

```shell
vim /usr/lib/systemd/system/buildkit.socket
```

```shell
[Unit]
Description=BuildKit
Documentation=https://github.com/moby/buildkit
[Socket]
ListenStream=%t/buildkit/buildkitd.sock
SocketMode=0660
[Install]
WantedBy=sockets.target
```

### 启动api
https://github.com/moby/buildkit#expose-buildkit-as-a-tcp-service
```shell
buildkitd \
  --addr tcp://0.0.0.0:1234 \
  --tlscacert /path/to/ca.pem \
  --tlscert /path/to/cert.pem \
  --tlskey /path/to/key.pem
```
