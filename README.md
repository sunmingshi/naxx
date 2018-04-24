# naxx
nio网络模型

## 编译运行
> cd 到naxx目录下，执行 mvn package，执行 java -jar NAXX.jar

## 使用
> 需要在启动操作中，注册Controller到Context中
> controller包内为示例代码,支持注解指定uri或者默认小写类名/方法名为uri

## Issue
耦合度过高了，准备拆分