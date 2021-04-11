package concurrency.homework;

import java.util.concurrent.CountDownLatch;

public class Task1 implements Runnable {
    Result result;

    private CountDownLatch latch;

    //通过构造函数传入result
    Task1(Result result,CountDownLatch latch) {
        this.result = result;
        this.latch = latch;
    }

    public void run() {
        //可以操作result
        int a = sum();
        result.setR(a);
        latch.countDown();
    }

    private int sum() {
        return fibo(36);
    }

    private int fibo(int a) {
        if ( a < 2)
            return 1;
        return fibo(a-1) + fibo(a-2);
    }

}