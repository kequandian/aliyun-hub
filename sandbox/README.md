# aliyun-hub-sandbox
> aliyun-hub文件上传模块自动部署

#### 准备 API jar包 

在[core](./core)项目目录执行`mvn package`将aliyun-hub上传模块打包，之后将生成的`file-upload-1.0.0-standalone.jar`包放入`api`目录。

```
$ mvn package
[INFO]
[INFO] --- maven-jar-plugin:3.1.2:jar (default-jar) @ file-upload ---
[INFO] Building jar: /Users/panda/code/java/kequandian/aliyun-hub/core/target/file-upload-1.0.0.jar
[INFO]
[INFO] --- spring-boot-maven-plugin:1.5.6.RELEASE:repackage (default) @ file-upload ---
[INFO] Attaching archive: /Users/panda/code/java/kequandian/aliyun-hub/core/target/file-upload-1.0.0-standalone.jar, with classifier: standalone
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  10.942 s
[INFO] Finished at: 2020-09-14T16:38:08+08:00
[INFO] ------------------------------------------------------------------------
```



#### 设置上传的配置文件

配置 `api/config`下的`application.yml`配置文件

>application.yml 示例
```yml
## 本地文件上传配置
am:
   fileUploadPath: /attachments
   fileHost: http://localhost:8080/images
   
## aliyun服务配置
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


##### Nginx 配置文件

> 为了能在连接的网络中调用Aliyun-hub的上传API，需要在连接的网络中的nginx容器中配置路由规则。

配置如下：

```
​```
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
    location /api/cms {
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
​```
```





#### 执行配置脚本文件

> `network`参数为接入的docker网络名称，执行后容器将接入指定的docker网络。

```
$ sh config.sh 
Usage: config.sh <network>
Option:
      network   - docker network name you want to connect
```
可用`doker network`命令查询容器的网络名称:
``` 
$ docker network ls 
NETWORK ID          NAME                    DRIVER              SCOPE
49f3b80d34d1        bridge                  bridge              local
91757aadf36c        host                    host                local
c32dac1f4b2b        network_you_connect     bridge              local
```



####  启动docker容器
```
$ docker-compose up -d
```