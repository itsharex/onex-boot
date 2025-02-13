## 管理后台前端Setup

### 技术准备
* [VUE](https://cn.vuejs.org/)
* [ElementUI](https://element.eleme.cn/)

### 开发准备
1. 安装[node 8.11+](https://nodejs.org/en/download/),建议安装最新的LTS版
2. 安装IDE,比如Intellij IDEA,其它IDE也可以安装对应的插件
3. 建议使用[cnpm](http://npm.taobao.org/)作为npm仓库

## 开发步骤
1. 检出代码
```shell
git clone https://github.com/zhangchaoxu/onex-portal.git
```
2. 使用IDEA或者WebStorm等工具打开检出代码文件夹
3. 初始化安装`cnpm install`,该步骤只需要初次执行即可
4. 编译和开发热部署`cnpm run serve`
5. 编译和打包生产环境`cnpm run build`
6. Lints检查`cnpm run lint`

### vue开发规范
详见[Vant风格指南](https://youzan.github.io/vant/#/zh-CN/style-guide)

### Tips
1. JavaScript版本使用ES6
2. 打开ESLint用以修复格式检查

