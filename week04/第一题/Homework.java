package concurrency.homework;

import netty.gateway.outbound.httpclient4.NamedThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 本周作业：（必做）思考有多少种方式，在main函数启动一个新线程或线程池，
 * 异步运行一个方法，拿到这个方法的返回值后，退出主线程？
 * 写出你的方法，越多越好，提交到github。
 *
 * 一个简单的代码参考：
 */
public class Homework {
    
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Result result4 = new Result();
        Thread t1 = new Thread(new Task1(result4,countDownLatch));
        t1.start();

        // 在这里创建一个线程或线程池，CustomThreadFactory 创建线程为守护线程，当所有执行完之后主线程可以退出
//        ExecutorService executor = Executors.newFixedThreadPool(10);
        ExecutorService executor = new ThreadPoolExecutor(10, 10,
                1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(5),
                new CustomThreadFactory("huster_service"));

        // 2
        Result result2 = new Result();
        Future future1 = executor.submit(new Task(result2));

        // 3
        Result result = new Result();
        Future<Result> future = executor.submit(new Task(result), result);

        // 4
        FutureTask<Integer> futureTask = new FutureTask<>(Homework::sum);
        executor.submit(futureTask);

        // 5
        Result result3 = new Result();
        FutureTask<Result> futureTask1 = new FutureTask<>(new Task(result3), result3);
        executor.submit(futureTask1);

        // 6
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(Homework::sum,executor);

        // 7
        Result result1 = new Result();
        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(new Task(result1),executor);

        long start=System.currentTimeMillis();
        // 异步执行 下面方法
        countDownLatch.await();
        System.out.println("方法1： Thread 模式：异步计算结果为："+ result4.getR());
        future1.get();
        System.out.println("方法2： future 模式：异步计算结果为："+ result2.getR());
        System.out.println("方法3： future + Runnable 模式：异步计算结果为："+ future.get().getR());
        System.out.println("方法4： futureTask 模式：异步计算结果为："+ futureTask.get());
        System.out.println("方法5： futureTask + Runnable 模式：异步计算结果为："+ futureTask1.get().getR());
        System.out.println("方法6： CompletableFuture 模式：异步计算结果为："+ completableFuture.get());
        completableFuture1.get();
        System.out.println("方法7： CompletableFuture + Runnable 模式：异步计算结果为："+ result1.getR());
         
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    private static int sum() {
        return fibo(36);
    }
    
    private static int fibo(int a) {
        if ( a < 2) 
            return 1;
        return fibo(a-1) + fibo(a-2);
    }
}

class Result {
    int r;
    public void setR(int result) {
        this.r = result;
    }
    public int getR() {
        return r;
    }
}
