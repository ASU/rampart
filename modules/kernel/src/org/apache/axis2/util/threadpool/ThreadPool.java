/*
* Copyright 2004,2005 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package org.apache.axis2.util.threadpool;

import edu.emory.mathcs.backport.java.util.concurrent.Executor;
import edu.emory.mathcs.backport.java.util.concurrent.SynchronousQueue;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import org.apache.axis2.AxisFault;
import org.apache.axis2.i18n.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This the thread pool for axis2. This class will be used a singleton
 * across axis2 engine. <code>ThreadPool</code> is accepts <code>AxisWorkers</code> which has
 * run method on them and execute this method, using one of the threads
 * in the thread pool.
 */
public class ThreadPool implements ThreadFactory {
	private static final Log log = LogFactory.getLog(ThreadPool.class);
    protected static long SLEEP_INTERVAL = 1000;
    private static boolean shutDown;
    protected ThreadPoolExecutor executor;
    
    //integers that define the pool size, with the default values set.
    private int corePoolSize = 5;
    private int maxPoolSize = Integer.MAX_VALUE;
    
    public ThreadPool() {
        setExecutor(createDefaultExecutor("Axis2 Task", Thread.NORM_PRIORITY, true));
    }
    
    public ThreadPool(int corePoolSize,int maxPoolSize) {
    	this.corePoolSize = corePoolSize;
    	this.maxPoolSize = maxPoolSize;
        setExecutor(createDefaultExecutor("Axis2 Task", Thread.NORM_PRIORITY, true));
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public void execute(Runnable worker) {
        if (shutDown) {
            throw new RuntimeException(Messages.getMessage("threadpoolshutdown"));
        }
        executor.execute(worker);
    }

    /**
     * A forceful shutdown mechanism for thread pool.
     */
    public void forceShutDown() {
        if (log.isDebugEnabled()) {
            log.debug("forceShutDown called. Thread workers will be stopped");
        }
        executor.shutdownNow();
    }

    /**
     * This is the recommended shutdown method for the thread pool
     * This will wait till all the workers that are already handed over to the
     * thread pool get executed.
     *
     * @throws org.apache.axis2.AxisFault
     */
    public void safeShutDown() throws AxisFault {
        synchronized (this) {
            shutDown = true;
        }

        executor.shutdown();
    }

    protected ThreadPoolExecutor createDefaultExecutor(final String name,
                                                       final int priority,
                                                       final boolean daemon) {
        ThreadPoolExecutor rc = new ThreadPoolExecutor(corePoolSize , maxPoolSize , 10,
                TimeUnit.SECONDS, new SynchronousQueue(),
                new edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, name);
                thread.setDaemon(daemon);
                thread.setPriority(priority);
                return thread;
            }
        });
        rc.allowCoreThreadTimeOut(true);
        return rc;
    }
}
