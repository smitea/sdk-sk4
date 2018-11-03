package com.ridko.sk4.common;

import java.util.concurrent.*;

/**
 * 通常在执行大量异步任务时提供改进的性能，这是由于减少了每个任务的调用开销，并且它们提供了一种绑定和管理资源的方法，包括执行集合时所消耗的线程。
 *
 * @author smitea
 */
public class ThreadExcutorUntils {
  private static ThreadPoolExecutor threadPoolExecutor;

  private static ThreadPoolExecutor createThreadPoolExecutor() {
    return new ThreadPoolExecutor(
            // 线程数量
            4,
            // 最大线程数量
            4,
            // 线程空闲时超过指定空闲时间后销毁
            600L,
            // keepAliveTime单位
            TimeUnit.SECONDS,
            // 有界任务队列(当线程池内等待执行的线程数量超过65536<2^16>时，执行拒绝策略)
            new ArrayBlockingQueue<>(65536),
            // 设置线程守护(主线程退出后,强制销毁线程)
            r -> {
              Thread thread = new Thread(r);
              thread.setDaemon(true);
              return thread;
            });
  }

  public static synchronized void submit(Runnable runnable) {
    if (threadPoolExecutor == null) {
      threadPoolExecutor = createThreadPoolExecutor();
    }
    threadPoolExecutor.submit(runnable);
  }
}
