# 文件上传失败定位及原因分析
在系统开发中，经常有文件上传的需求。本文暂不讨论如何优雅的处理文件上传的接口请求，只讨论文件上传中可能遇到的一些问题及原因。

## 问题
### 问题1: 被前端上传组件限制了文件格式和大小
- 成因：前端的上传组件，如elementui的el-upload可以在before-upload 钩子中限制用户上传文件的格式和大小，而判断过程可能没将错误显性的暴露出来，将异常吞没，导致用户上传操作被阻断。
- 问题表现：选中文件后，没有文件上传接口的调用
- 解决思路：在对应的hook中做有效的判断，如果确实有文件格式和大小限制的需求，当不符合限制要求的时候一定要将错误提示告知用户，而不是直接阻断操作不做任何提示。

### 问题2：被前端网络请求控件限制了文件上传过程
- 成因：前端网络请求控件如axois中，很多项目为了避免长耗时请求的出现，会定义全局的timeout时间，业务请求耗时一般能控制在timeout的时间要求范围内，而文件上传请求很可能因为文件大小的原因，导致请求超时。
- 问题表现：选中文件后，调用上传接口开始上传，然后进度走到一部分以后(往往是在请求在固定的timeout时间断掉)，就开始提示network canceled，而接口会因为请求没完成被取消而没有任何返回。
- 解决思路：针对请求时间会超过全局timeout的请求，单独定义timeout的时间。

### 问题3：被代理应用限制了文件大小
- 成因：很多应用接口都会在请求之前加一个nginx代理服务器，而nginx对于文件请求，是有默认1M的限制。
- 问题表现：网络请求响应结果最终是nginx的错误提示，往往是413
- 解决思路：根据实际情况修改上传请求的大小限制
```
# 默认值1M
client_max_body_size 200M;
```

### 问题4：被接口应用本身限制了文件大小
- 成因：SpringBoot的Servlet对文件，是有默认大小限制的。
- 问题表现：网络请求是java自己的异常，至于错误提示得看是否在全局异常中有响应的处理,可见BaseExceptionHandler.handleMaxUploadSizeExceededException
- 解决思路：根据实际情况修改上传请求的大小限制
```
  servlet:
    multipart:
      # 默认值1M
      max-file-size: 200MB
      # 默认值10M
      max-request-size: 200MB
```

### 问题5：被Servlet容器或者网关等拦截
- 成因：有部分应用场景，是将应用部署在tomcat等Servlet容器中，或者是有全局的网关处理，这两个也可能会拦截大文件请求。
- 解决思路：根据实际情况，寻找解决办法

### 问题6：文件存储程异常
- 成因：比如服务器磁盘损坏、磁盘满了，可能导出新的请求进不来。
- 问题表现：文件看似上传成功，但实际访问不到

### 问题7：文件转存或者处理过长异常
- 成因：有很多业务场景中，会将上传上来的文件做阿里云oss的转存、裁剪等操作，而这些操作可能带来长耗时或者异常。
- 问题表现：文件上传进度条走完，但网络请求一致没返回结果。这是以为上传进度条表达的是文件上传的过长，而等待是因为接口的处理过程长耗时或异常
- 问题表现：这个值得继续讨论，此次暂不展开；简单说就是将长耗时操作异步处理，或者是阿里云oss只传，不经过服务器中转。

## 总结
首先对于文件上传，业务(产品)需要首先确定文件格式和大小的限制，不合法的文件格式会带来安全问题，超限的文件大小会带来服务器和带宽的压力，同时给用户体验带来影响。        
对于文件的大小，应该在前端、接口端(SpringBoot)、代理服务器端(nginx)，以及网关等其他可能的地方同时做相同的约束。       
若出现问题，通过问题表现，报错信息等一步一步定位问题点可能是出现在哪个点，请求的过长肯定是前端->应用服务器->接口，从前往后排查。
