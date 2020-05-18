# naxx
> nio网络模型，支持长连接

## 使用
> 实现Decoder和Encoder，创建main方法启动Acceptor

## 问题
> 读取数据时多次copy，这里是目前最大的问题。由此也清楚了netty中zero-copy的价值
