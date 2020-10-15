## aliyun-hub-deploy
> aliyun-hub 文件上传模块自动部署

#### 准备 API jar包 
> 在[core](./core)项目目录执行`mvn package`将aliyun-hub上传模块打包，之后将生成的 `aliyun-hub-1.0.0-standalone.jar` 包放入`api`目录。

```shell
$ mvn package
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  10.942 s
[INFO] Finished at: 2020-09-14T16:38:08+08:00
[INFO] ------------------------------------------------------------------------
```


#### 设置上传的配置文件

配置 `api/config`下的`application.yml`配置文件

> application.yml 示例
```yml
## 本地文件上传配置
fs:
   fileUploadPath: /attachments
   fileHost: /attachments   
   
## aliyun服务配置
aliyun:
  sso:
    endpoint: "http://oss-cn-shenzhen.aliyuncs.com"
    accessKeyId: ""
    accessKeySecret: ""
    bucketName: ""
    username: ""
    thumbParam: "x-oss-process=image/resize,m_fill,h_750,w_750"
    accessUrl: "http://xxxxx.oss-cn-shenzhen.aliyuncs.com/"
  sms:
    accessKeyId: ""
    accessKeySecret: ""
```

#### 独立部署aliyun-hub服务
```
$ docker-compose up -d
```


#### 在混合云 gateway 端增加以下配置并重新启动 
> docker stop nginx-gateway && docker-compose restart nginx-gateway

```
$ cat aliyun-hub-hybrid-gateway.conf
location /images {
        proxy_pass http://aliyun-hub-nginx:80;
    }

    location /attachments {
        proxy_pass http://aliyun-hub-nginx:80;
    }

    ## set route within hybrid nginx gateway
    location /api/fs {
        proxy_pass http://aliyun-hub:8080;
        proxy_set_header Host $http_host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto  $scheme;
        proxy_buffering off;
        proxy_max_temp_file_size 0;
        proxy_connect_timeout 30;
        proxy_cache_valid 200 302 10m;
        proxy_cache_valid 301 1h;
        proxy_cache_valid any 1m;
    }

    location /api/cloud/aliyun {
        proxy_pass http://aliyun-hub:8080;
        proxy_set_header Host $http_host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto  $scheme;
        proxy_buffering off;
        proxy_max_temp_file_size 0;
        proxy_connect_timeout 30;
        proxy_cache_valid 200 302 10m;
        proxy_cache_valid 301 1h;
        proxy_cache_valid any 1m;
    }
```

