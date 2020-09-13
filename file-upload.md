
#### application.yml 配置文件例子

---  
```yaml
io:
  fileUploadPath: "/images"
  fileHost: ""
  sso:
    endpoint: "http://oss-cn-shenzhen.aliyuncs.com"
    accessKeyId: ""
    accessKeySecret: ""
    bucketName: ""
    username: ""
    thumbParam: "x-oss-process=image/resize,m_fill,h_750,w_750"
    accessUrl: "http://muaskin.oss-cn-shenzhen.aliyuncs.com/"
```
>/images 为文件上传目录

#### docker-compose.yml 例子

---
```yaml
version: '3'
services:
  zero-upload:
    image: zelejs/app-openjre11
    container_name: zero-upload
    volumes:
      - ./zero-upload:/webapps
      - ./images:/images
    ports:
      - '8080:8080'
```

>/images 为文件上传目录