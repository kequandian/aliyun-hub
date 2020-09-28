##  aliyun-hub-sandbox
> aliyun-hub文件上传模块自动部署

#### 准备 API jar包 

在[core](./core)项目目录执行`mvn package`将aliyun-hub上传模块打包，之后将生成的`aliyun-hub-1.0.0-standalone.jar`包放入`api`目录。

```
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
> 配置 `api/config`下的`application.yml`配置文件
> 本地文件上传无需配置

> application.yml 示例
```yml
fs:
   fileUploadPath: /attachments
   fileHost: attachments   
   
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

##### Nginx 配置文件
> TODO: 通过工具可省略这一步

将 sandbox.d/*.conf 配置文件拷贝到 **连接的网络的nginx容器** 的 `conf.d/sandbox.d`目录下。


#### 执行配置脚本文件

> `network`参数为接入的docker网络名称，执行后容器将接入指定的docker网络。

```
$ sh config.sh 
Usage: config.sh <network>
Option:
      network   - docker network name you want to connect
```

> 可用`doker network`命令查询容器的网络名称:
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

#### 访问 swagger-ui.html 查看 api文档
- ***http://\<sandbox-endpoint\>/aliyun-hub/swagger-ui.html***
>  e.g.  http://192.168.3.100:8080/aliyun-hub/swagger-ui.html


