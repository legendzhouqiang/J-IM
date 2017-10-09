package org.tio.utils.prop;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.lock.MapWithLock;

/**
 * @author tanyaowu
 * 2017年8月18日 下午5:36:02
 */
public class MapWithLockPropSupport implements IPropSupport {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(MapWithLockPropSupport.class);

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	private MapWithLock<String, Object> props = null;//

	/**
	 *
	 * @author tanyaowu
	 */
	public MapWithLockPropSupport() {
	}

	/**
	 *
	 * @author tanyaowu
	 */
	@Override
	public void clearAttribute() {
		initProps();
		Lock lock = props.getLock().writeLock();
		Map<String, Object> m = props.getObj();
		try {
			lock.lock();
			m.clear();
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 *
	 * @param key
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public Object getAttribute(String key) {
		initProps();
		Map<String, Object> m = props.getObj();
		Object ret = m.get(key);
		return ret;

		//		Lock lock = props.getLock().readLock();
		//		Map<String, Object> m = props.getObj();
		//		try {
		//			lock.lock();
		//			Object ret = m.get(key);
		//			return ret;
		//		} catch (Exception e) {
		//			throw e;
		//		} finally {
		//			lock.unlock();
		//		}
	}

	private void initProps() {
		if (props == null) {
			synchronized (this) {
				if (props == null) {
					props = new MapWithLock<>(new HashMap<String, Object>());
				}
			}
		}
	}

	/**
	 * @param key
	 * @author tanyaowu
	 */
	@Override
	public void removeAttribute(String key) {
		initProps();
		Lock lock = props.getLock().writeLock();
		Map<String, Object> m = props.getObj();
		try {
			lock.lock();
			m.remove(key);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 *
	 * @param key
	 * @param value
	 * @author tanyaowu
	 */
	@Override
	public void setAttribute(String key, Object value) {
		initProps();
		Lock lock = props.getLock().writeLock();
		Map<String, Object> m = props.getObj();
		try {
			lock.lock();
			m.put(key, value);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}
}
