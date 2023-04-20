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
