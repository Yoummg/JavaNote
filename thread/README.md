##  thread文件描述

### 1.任务描述：给定一个目录，计算目录的大小（结果的单位是字节）。

* [TotalFileSizeSequential](TotalFileSizeSequential.java)：正常的单线程完成任务。

* [NaivelyConcurrenTotalFileSize](NaivelyConcurrenTotalFileSize.java)： 简单的多线程完成任务。如果子目录超过线程数，造成死锁。

* [ConcurrentTotalFileSize](ConcurrentTotalFileSize.java) ：先不计算大小，把目录和大小都存储起来。

* [ConcurrentTotalFileSizeWLatch](ConcurrentTotalFileSizeWLatch.java)：引入CountDownLatch。

* [ConcurrentTotalFileSizeWQueue](ConcurrentTotalFileSizeWQueue.java)： 引入阻塞队列。

* [ForkJoin](ForkJoin.java)：Java7 的新特性。



