package me.hollow.trollgod.api.util.thread;

import java.util.concurrent.*;

public class ThreadUtil
{
    public static void submitToExecutor(final Runnable runnable) {
        final ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.submit(runnable);
        executor.shutdown();
    }
}
