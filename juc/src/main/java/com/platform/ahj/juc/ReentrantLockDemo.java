package com.platform.ahj.juc;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description: 针对 ReentrantLock部分使用情况进行模拟
 * @Author: ziyu
 * @Created: 2025/2/8-11:19
 * @Since:
 */
public class ReentrantLockDemo {
    public static void main(String[] args) {
        int permitsNum = 2;
        final Semaphore semaphore = new Semaphore(permitsNum);
        try {
            // 直接获取信号量会返回false
            System.out.println("availablePermits:"+semaphore.availablePermits()+",semaphore.tryAcquire(3,1, TimeUnit.SECONDS):"+semaphore.tryAcquire(3,1, TimeUnit.SECONDS));
            // 直接release使信号量+1，信号量变成了3个超过了permitsNum
            semaphore.release();
            // 再次尝试获取3个信号量，返回true说明直接release使信号量+1是可行的
            System.out.println("availablePermits:"+semaphore.availablePermits()+",semaphore.tryAcquire(3,1, TimeUnit.SECONDS):"+semaphore.tryAcquire(3, 1, TimeUnit.SECONDS));
        }catch (Exception e) {

        }

        ReentrantLock lock = new ReentrantLock();
        lock.lock();  // 计数器 = 1

        try {
            // 临界区代码...
        } finally {
            lock.unlock();  // 计数器 = 0，锁释放 ✅
            lock.unlock();  // 计数器 = -1，抛出 IllegalMonitorStateException ❌
        }
    }
}
