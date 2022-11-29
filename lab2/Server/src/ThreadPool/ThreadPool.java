package ThreadPool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    /// thread Pool executor
    private ThreadPoolExecutor threadPool;
    // max size of Task Queue (configured on Server)
    private int maxQueueSize;
    private final int minThreadCount = 1;
    private final int maxThreadCount = Integer.MAX_VALUE;
// constructor
    public ThreadPool(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
        // create fixed size blocking queue
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(maxQueueSize);
        // create thread pool
        threadPool = new ThreadPoolExecutor(minThreadCount, maxThreadCount, 10, TimeUnit.MILLISECONDS, taskQueue);
    }
// add new Task method
    public synchronized void addTask(Task task) {
        threadPool.execute(task);
    }
    public int getMaxQueueSize() {
        return maxQueueSize;
    }
}
