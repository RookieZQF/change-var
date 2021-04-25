

### change-var Plugin For XPocket
#### 简介
动态修改变量的值,不局限于java进程;
支持Linux操作系统X86_64与amd64

#### 操作指南
使用
``` shell
use change-var@CHANGE-VAR
```
进入change-var插件域
然后使用
``` shell
attach pid
read PrintGCDetai 1
write PrintGCDetails 12 1
read PrintGCDetai 1
detach
```
命令详情可以使用help
``` shell
help
```
或者
``` shell
system.help change-var@CHANGE-VAR
```
