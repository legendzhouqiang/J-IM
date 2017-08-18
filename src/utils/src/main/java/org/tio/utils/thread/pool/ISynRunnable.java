package org.tio.utils.thread.pool;

import java.util.concurrent.locks.ReadWriteLock;

public interface ISynRunnable extends Runnable {
	public boolean isCanceled();

	public boolean isNeededExecute();

	public ReadWriteLock runningLock();

	public void runTask();

	public void setCanceled(boolean isCanceled);
}
