package com.crawl.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {
    public static ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(50,100,10, TimeUnit.MINUTES,new LinkedBlockingDeque<>());

}
