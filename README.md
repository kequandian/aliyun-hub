## 专用于aliyun的服务对接

#### application.yml 配置文件
```yaml
am:  ## 基于spring boot本地文件上传配置
  fileUploadPath: "/images"
  fileHost: "http://localhost:8080"
  
aliyun:
  sso:
    endpoint: "http://oss-cn-shenzhen.aliyuncs.com"
    accessKeyId: ""
    accessKeySecret: ""
    bucketName: ""
    username: ""
    thumbParam: "x-oss-process=image/resize,m_fill,h_750,w_750"
    accessUrl: "http://muaskin.oss-cn-shenzhen.aliyuncs.com/"
  sms:
    accessKeyId: ""
    accessKeySecret: ""
```

#### docker-compose.yml 配置
```yaml
version: '3'
services:
  api:
    image: zelejs/app-openjre11
    container_name: ${app}-api
    volumes:
      - ./data-vol/images:/images   # where application.yml fileUploadPath path: /images
  gateway:
    image: daocloud.io/library/nginx:latest
    container_name: ${app}-gateway
    privileged: true
    volumes:
      - ./gateway/nginx.conf:/etc/nginx/nginx.conf
      - ./gateway/conf.d:/etc/nginx/conf.d
      - ./gateway/html:/usr/share/nginx/html
      - ./data-vol/images:/usr/share/nginx/html/images   # use for visiting http://localhost:8080/images in browser
    ports:
      - "8080:80"   # export 8080 port
```

#### gateway/80.conf 配置
```
$ cat ./gateway/conf.d/80.conf
server{
    listen       80;
    server_name  localhost;
   
    client_max_body_size 200m;  # max file upload size

    location /images {
        root /usr/share/nginx/html;
    } 
}
```
