package org.tio.utils.lock;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 自带读写锁的对象.
 *
 * @author tanyaowu
 */
public class ObjWithLock<T> implements Serializable {
	/**
	 * 
	 */
	private T obj = null;

	/**
	 * 
	 */
	private ReentrantReadWriteLock lock = null;

	/**
	 * 
	 * @param obj
	 * @author tanyaowu
	 */
	public ObjWithLock(T obj) {
		this(obj, new ReentrantReadWriteLock());
	}

	/**
	 * 
	 * @param obj
	 * @param lock
	 * @author tanyaowu
	 */
	public ObjWithLock(T obj, ReentrantReadWriteLock lock) {
		super();
		this.obj = obj;
		this.lock = lock;
	}

	/**
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public ReentrantReadWriteLock getLock() {
		return lock;
	}

	/**
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public T getObj() {
		return obj;
	}

	/**
	 * 
	 * @param obj
	 * @author tanyaowu
	 */
	public void setObj(T obj) {
		this.obj = obj;
	}
	
	private static final long serialVersionUID = -3048283373239453901L;
}
