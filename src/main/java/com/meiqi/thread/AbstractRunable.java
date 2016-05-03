
package com.meiqi.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractRunable implements Runnable {
    /** logger */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private TaskExecutor   exec;

    public AbstractRunable(TaskExecutor exec) {
        this.exec = exec;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            execute();
        } finally {
            exec.getSemaphore().release();
        }
    }

    protected abstract void execute();
}
