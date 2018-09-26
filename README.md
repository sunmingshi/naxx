# naxx
> nio网络模型，支持长连接

## 使用
> 实现Decoder和Encoder，创建main方法启动Acceptor

## Issue
> 没有主动关闭超时或异常的连接，导致会产生CLOSE_WAIT
