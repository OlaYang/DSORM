
package com.meiqi.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

public class TaskExecutor {
    
    /** 最大并发数 */
    private int      taskExecPoolMaxCount = 500;
    
    public int getTaskExecPoolMaxCount() {
        return taskExecPoolMaxCount;
    }

    /** 用于提交任务的线程池 */
    private final ExecutorService execPool;
    
    /** 阻塞信号量 */
    private final Semaphore       semaphore;
    
    private int   semaphoreCount=500;
    

    /**
     * @param taskExecPoolMaxCount the taskExecPoolMaxCount to set
     */
    public void setTaskExecPoolMaxCount(int taskExecPoolMaxCount) {
        this.taskExecPoolMaxCount = taskExecPoolMaxCount;
    }

    public TaskExecutor() {
        this.execPool = Executors.newFixedThreadPool(taskExecPoolMaxCount);
        this.semaphore = new Semaphore(semaphoreCount);
    }

    /**
     * 获取信号量
     * 
     * @return
     */
    public Semaphore getSemaphore() {
        return semaphore;
    }
    
    /**
     * 提交任务
     * 
     * @param runnable
     * @return
     * @throws InterruptedException
     */
    public Future<?> submit(Runnable runnable) throws InterruptedException {
        semaphore.acquire();
        return execPool.submit(runnable);
    }

    /**
     * 关闭线程池
     */
    public void destory() {
        execPool.shutdownNow();
    }
}
