# biubiubiu-barrage

------

> 欢迎关注我的博客：[MashiroC的奇思妙想](https://blog.mashiroc.cn/)
>
> 写点杂七杂八、奇思妙想。
>
> 偶尔分享一下最近学到的新技术。

一个用微信端发送弹幕，大屏幕播放的弹幕系统。

前端基本是网上找的和自己随便写的

管理界面现在只有接口

微信验证部分原本是自己写，现在使用工作站的轮子magicloop

***2018.10.25***

修改了一部分问题，验证部分使用jwt

***2018.8.24***

增加了防刷的机制和敏感词过滤

#### 示例:

![弹幕示例](http://p92wwofg0.bkt.clouddn.com/pic.png)

### 技术栈

- spring boot
- mysql
- mybatis
- netty
- redis
- websocket
- jwt
- 微信接入

### 注意事项

- netty和spring必须运行在不同端口
- 本地测试时模拟数据吧

### TODO

- 增加更多炫酷的弹幕
- 后台管理界面的前端
- 增加验证方式(magicloop还没有重构完毕)