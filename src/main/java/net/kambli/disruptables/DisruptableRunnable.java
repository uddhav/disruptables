package net.kambli.disruptables;

/**
 * A {@code Runnable} that can be disrupted
 *
 * @since May 2015
 */
@FunctionalInterface
public interface DisruptableRunnable
{
    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     */
    void run() throws Exception;
}

