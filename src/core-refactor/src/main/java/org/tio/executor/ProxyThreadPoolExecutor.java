package org.tio.executor;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.tio.runnable.SynRunnable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/20
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
@Slf4j
public class ProxyThreadPoolExecutor implements InvocationHandler {

    private ThreadPoolExecutor executor;

    public ProxyThreadPoolExecutor() throws NoSuchMethodException {
    }

    public ThreadPoolExecutor bind(ThreadPoolExecutor executor) {
        this.executor = executor;
        return (ThreadPoolExecutor) Proxy.newProxyInstance(this.executor.getClass().getClassLoader(),
                this.executor.getClass().getInterfaces(), this);
    }


    private List<Method> proxyMethods = Lists.newArrayList(
            ThreadPoolExecutor.class.getMethod("execute", Runnable.class),
            ThreadPoolExecutor.class.getMethod("submit", Runnable.class),
            ThreadPoolExecutor.class.getMethod("submit", Runnable.class, Object.class));

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!proxyMethods.contains(method)) {
            return method.invoke(this.executor, args);
        }
        Runnable runnable = (Runnable) args[0];
        if (runnable instanceof SynRunnable) {
            SynRunnable synRunnable = (SynRunnable) runnable;
            ReentrantLock lock = synRunnable.runningLock();
            boolean b = lock.tryLock();
            try {
                if (b) {
                    return method.invoke(this.executor, args);
                } else {
                    log.debug(synRunnable.getName());
                    return null;
                }
            } finally {
                if (b) {
                    lock.unlock();
                }
            }
        } else {
            return method.invoke(this.executor, args);
        }
    }

    public static void main(String[] args) throws NoSuchMethodException {

    }
}
