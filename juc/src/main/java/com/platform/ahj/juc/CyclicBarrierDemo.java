package com.platform.ahj.juc;

import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {
    CyclicBarrier cyclicBarrier = new CyclicBarrier(7,()->{
    });

    public void test(String aaa){
    }
    public void test(Integer bbb){

    }

    public static void main(String[] args) {
        CyclicBarrierDemo cyclicBarrierDemo = new CyclicBarrierDemo();
        cyclicBarrierDemo.test((String) null);
        if ((Object)(String)  null ==  (Integer)null){

        }
    }
}
