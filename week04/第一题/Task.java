package concurrency.homework;

public class Task implements Runnable {
    Result result;

    //通过构造函数传入result
    Task (Result result) {
        this.result = result;
    }

    public void run() {
        //可以操作result
        int a = sum();
        result.setR(a);
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