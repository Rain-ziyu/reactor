package com.ahj.platform.reactor;

/**
 * @Description:
 * @Author: ziyu
 * @Created: 2024/12/16-11:47
 * @Since:
 */
public class ThreadDemo {


}
 class InterruptExample {

    private static class MyThread2 extends Thread {
        @Override
        public void run() {
            while (!interrupted()) {
                // ..
            }
            System.out.println("Thread end");
        }
    }
     public static void main(String[] args) throws InterruptedException {
         Thread thread1 = new MyThread1();
         thread1.start();
         thread1.interrupt();
         System.out.println("Main run");
     }
     private static class MyThread1 extends Thread {
         @Override
         public void run() {
             try {
                 Thread.sleep(2000);
                 System.out.println("Thread run");
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
     }

}