package com.hl.gcd;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.SparseArray;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import bolts.CancellationTokenSource;
import bolts.Continuation;
import bolts.Task;

public class DispatchQueue {

    private static String TAG = DispatchQueue.class.getSimpleName();
    private static DispatchQueue queueMain;
    private static DispatchQueue queueGlobal;
    private static SparseArray<DispatchQueue> queueMap = new SparseArray<DispatchQueue>();
    private final Object lock = new Object();
    private final Executor currentExecutor;
    private CancellationTokenSource cts;

    private DispatchQueue(boolean mainQueue) {
        if (!mainQueue) {
            this.currentExecutor = new BackgroundExecutor();
        } else {
            this.currentExecutor = Task.UI_THREAD_EXECUTOR;
        }
        cts = new CancellationTokenSource();
    }

    public static DispatchQueue main() {
        if (queueMain == null) {
            synchronized (DispatchQueue.class) {
                if (queueMain == null)
                    queueMain = new DispatchQueue(true);
            }
        }
        return queueMain;
    }

    public static DispatchQueue global() {
        if (queueGlobal == null) {
            synchronized (DispatchQueue.class) {
                if (queueGlobal == null)
                    queueGlobal = new DispatchQueue(false);
            }
        }
        return queueGlobal;
    }

    public static DispatchQueue global(int groupId) {
        if (queueMap.get(groupId) == null) {
            synchronized (DispatchQueue.class) {
                if (queueMap.get(groupId) == null)
                    queueMap.put(groupId, new DispatchQueue(false));
            }
        }
        return queueMap.get(groupId);
    }

    public Task<Void> async(final Callable<Task<Void>> execute) {
        return asyncDelayed(execute, 0);
    }

    public Task<Void> asyncDelayed(final Callable<Task<Void>> execute, long delayMillis) {
        return Task.delay(delayMillis, cts.getToken())
                .onSuccessTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(Task<Void> task) throws Exception {
                        return execute.call();
                    }
                }, currentExecutor, cts.getToken());
    }

    public void sync(final Callable<Task<Void>> execute) throws InterruptedException {
        sync(execute, 10000);
    }

    public void sync(final Callable<Task<Void>> execute, long timeoutMillis) throws InterruptedException {
        syncDelayed(execute, 0, timeoutMillis);
    }

    public void syncDelayed(final Callable<Task<Void>> execute, long delayMillis) throws InterruptedException {
        syncDelayed(execute, delayMillis, 10000);
    }

    public void syncDelayed(final Callable<Task<Void>> execute, long delayMillis, long timeoutMillis) throws InterruptedException {
        long threadId;
        if (this == queueMain) {
            threadId = Looper.getMainLooper().getThread().getId();
        } else {
            threadId = ((BackgroundExecutor) currentExecutor).getThreadId();
        }
        long currentThreadId = Thread.currentThread().getId();
        if (threadId == currentThreadId) {
            RuntimeException e = new RuntimeException("You can't run sync task on same thread.");
            e.printStackTrace();
            throw e;
        }
        Task.delay(delayMillis, cts.getToken())
                .onSuccessTask(new Continuation<Void, Task<Void>>() {
                    @Override
                    public Task<Void> then(Task<Void> task) throws Exception {
                        return execute.call();
                    }
                }, currentExecutor, cts.getToken())
                .waitForCompletion(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    public void cancel() {
        cts.cancel();
        cts = new CancellationTokenSource();
    }

    private static class BackgroundExecutor implements Executor {

        private final Handler handler;
        private final long threadId;

        BackgroundExecutor() {
            HandlerThread handlerThread = new HandlerThread("queue-handler");
            handlerThread.start();
            this.threadId = handlerThread.getId();
            this.handler = new Handler(handlerThread.getLooper());
        }

        public long getThreadId() {
            return threadId;
        }

        @Override
        public void execute(Runnable command) {
            this.handler.post(command);
        }
    }
}
