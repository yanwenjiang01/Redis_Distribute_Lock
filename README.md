# Redis_Distribute_Lock

#### 介绍
项目依靠redis中setNX进行实现分布式锁，通过使用注解为要进行加锁的业务方法进行加锁。方便使用。

#### 软件架构
springboot+redis


#### 安装教程
无需安装，将项目下载下来，直接加入maven中即可通过@RedisDistributeLockAnnotion注解使用。

#### 使用说明

1.在业务方法上加入注解@RedisDistributeLockAnnotion即可使用。有3种方式：获取锁失败后忽略，抛异常以及等待3种情况。
