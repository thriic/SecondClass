# SecondClass
cuit第二课堂安卓客户端  

## 功能
- 校外网(webvpn)登录
- 对非完结状态的活动**报名**(报名中,待开始,进行中)  
- 任意时间**签到**并可选择签到/签退时间  
- 生成活动的签到签退链接

## 注意
- 在app上登录后若再通过其他方式外网登录第二课堂，会导致(几分钟内)app登录失败
- 部分用户或活动有概率id获取不全(疑似二课服务端问题)，重新获取也许能解决
- 请根据二课审核机制合理选择签到签退时间  

## 测试环境
Redmi K50 Android13 校外网

*具体实现详见[SecondClass.kt](https://github.com/thriic/SecondClass/blob/master/app/src/main/java/com/thryan/secondclass/core/SecondClass.kt)*
