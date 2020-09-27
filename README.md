## 专用于aliyun的服务对接

#### application.yml 配置文件
```yaml
fs:
   fileUploadPath: /images
   fileHost: images

aliyun:
  sso:
    endpoint: "http://oss-cn-shenzhen.aliyuncs.com"
    accessKeyId: ""
    accessKeySecret: ""
    bucketName: ""
    username: ""
    thumbParam: "x-oss-process=image/resize,m_fill,h_750,w_750"
    accessUrl: "http://xxxxxx.oss-cn-shenzhen.aliyuncs.com/"
  sms:
    accessKeyId: ""
    accessKeySecret: ""
```

#### docker-compose.yml 配置
```yaml
version: '3'
services:
  aliyun-hub:
    image: zelejs/allin-web:jdk11
    container_name: aliyun-hub
    privileged: true
    volumes:
      - ./images:/images   # where application.yml fileUploadPath path: /images
  aliyun-hub-dl:
    container_name: aliyun-hub-dl
    privileged: true
    image: 'daocloud.io/library/nginx:latest'
    volumes:
      - ./nginx/conf.d:/etc/nginx/conf.d
      - ./images:/usr/share/nginx/html/images
      - ./attachments:/usr/share/nginx/html/attachments
networks:
  default:
    external:
      name: ${network}   ## host docker network       
```
