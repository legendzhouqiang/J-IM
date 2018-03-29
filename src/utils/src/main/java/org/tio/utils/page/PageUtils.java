package org.tio.utils.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.lock.ObjWithLock;

/**
 * @author tanyaowu
 * 2017年5月10日 下午1:14:15
 */
public class PageUtils {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(PageUtils.class);

	public static <T> Page<T> fromList(List<T> list, int pageNumber, int pageSize) {
		if (list == null) {
			return null;
		}

		Page<T> page = pre(list, pageNumber, pageSize);

		List<T> pageData = page.getList();
		if (pageData == null) {
			return page;
		}

		int startIndex = Math.min((page.getPageNumber() - 1) * page.getPageSize(), list.size());
		int endIndex = Math.min(page.getPageNumber() * page.getPageSize(), list.size());

		for (int i = startIndex; i < endIndex; i++) {
			pageData.add(list.get(i));
		}
		page.setList(pageData);
		return page;
	}

	public static <T> Page<T> fromSet(Set<T> set, int pageNumber, int pageSize) {
		if (set == null) {
			return null;
		}

		Page<T> page = pre(set, pageNumber, pageSize);

		List<T> pageData = page.getList();
		if (pageData == null) {
			return page;
		}

		int startIndex = Math.min((page.getPageNumber() - 1) * page.getPageSize(), set.size());
		int endIndex = Math.min(page.getPageNumber() * page.getPageSize(), set.size());

		int i = 0;
		for (T t : set) {
			if (i >= endIndex) {
				break;
			}
			if (i < startIndex) {
				i++;
				continue;
			}

			pageData.add(t);
			i++;
			continue;
		}
		page.setList(pageData);
		return page;
	}

	public static <T> Page<T> fromSetWithLock(ObjWithLock<Set<T>> setWithLock, int pageNumber, int pageSize) {
		if (setWithLock == null) {
			return null;
		}
		Lock lock = setWithLock.getLock().readLock();
		lock.lock();
		try {
			Set<T> set = setWithLock.getObj();
			return fromSet(set, pageNumber, pageSize);
		} finally {
			lock.unlock();
		}

	}

	private static <T> Page<T> pre(java.util.Collection<T> list, int pageNumber, int pageSize) {
		if (list == null) {
			return new Page<>(null, pageNumber, pageSize, 0);
		}

		pageSize = processPageSize(pageSize);
		pageNumber = processpageNumber(pageNumber);

		int recordCount = list.size();
		if (pageSize > recordCount) {
			pageSize = recordCount;
		}

		List<T> pageData = new ArrayList<>(pageSize);
		Page<T> ret = new Page<>(pageData, pageNumber, pageSize, recordCount);
		return ret;
	}

	private static int processpageNumber(int pageNumber) {
		return pageNumber <= 0 ? 1 : pageNumber;
	}

	private static int processPageSize(int pageSize) {
		return pageSize <= 0 ? Integer.MAX_VALUE : pageSize;
	}
}
