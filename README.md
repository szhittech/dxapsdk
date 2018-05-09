# 和而泰（H&T）设备配网Android端使用说明
----
## 扫描

    HeTApApi.getInstance().scan(context,ssid,pass,mode,onApScanListener,timeout);


- context 上下文；
- ssid：路由器的WiFi名称；
- pass：路由器的密码；
- mode：设备类型；
- onApScanListener:扫描回调
- timeout:扫描的超时时间，不填写默认100秒

## 绑定
    HeTApApi.getInstance().bind(selectTarget,apPassword,onApBindListener,timeout);

- selectTarget：扫描到的设备对象（一般是用户在列表里面选择的那个目标设备）；
- apPassword：设备的ap密码，一般默认是空，填写null即可；
- onApBindListener绑定回调
- timeout:绑定的超时时间，不填写默认100秒


## 停止
    HeTApApi.getInstance().stop();
在退出页面的时候调用此方法

## 日志开关
    HeTApApi.getInstance().setLogEnable(true);

- true开启日志
- false关闭日志
- 默认日志是关闭的

