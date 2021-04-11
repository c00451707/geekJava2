package concurrency.homework;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {
    
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    
    private final String namePrefix;
    private final boolean daemon;
    
    public CustomThreadFactory(String namePrefix, boolean daemon) {
        this.daemon = daemon;
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
    }
    
    public CustomThreadFactory(String namePrefix) {
        // 设置为守护线程，主线程就可以结束
        this(namePrefix, true);
    }
    
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + "_thread_" + threadNumber.getAndIncrement(), 0);
        t.setDaemon(daemon);
        return t;
    }
}