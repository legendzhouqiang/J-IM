package org.tio.utils.thread.pool;

import java.util.concurrent.locks.ReadWriteLock;

public interface ISynRunnable extends Runnable {
	public ReadWriteLock runningLock();

	public boolean isNeededExecute();

	public boolean isCanceled();

	public void setCanceled(boolean isCanceled);

	public void runTask();
}
