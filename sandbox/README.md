# aliyun-hub-sandbox
> aliyun-hub文件上传模块自动部署

#####  docker-compose.yml
```yml
version: '3'
services:
  zero-upload:
    image: zelejs/app-openjre11
    container_name: zero-upload
    volumes:
      - ./zero-upload:/webapps
      - ./upload-files:/upload-files
    ports:
      - '8080:8080'
```