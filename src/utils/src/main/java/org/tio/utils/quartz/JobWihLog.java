package org.tio.utils.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.SystemTimer;

import com.xiaoleilu.hutool.date.DateUtil;

/**
 * @author tanyaowu 
 * 2017年10月8日 下午4:58:34
 */
public abstract class JobWihLog implements Job {
	private static Logger log = LoggerFactory.getLogger(JobWihLog.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public JobWihLog() {
	}

	/** 
	 * @param context
	 * @throws JobExecutionException
	 * @author: tanyaowu
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("定时任务[{}]运行开始， 本次id:{}, 本次执行时间:{}, 上次执行时间:{}, 下次执行时间:{}", this.getClass().getName(), context.getFireInstanceId(), DateUtil.formatDateTime(context.getFireTime()),
				DateUtil.formatDateTime(context.getPreviousFireTime()), DateUtil.formatDateTime(context.getNextFireTime()));
		long start = SystemTimer.currentTimeMillis();
		try {
			run(context);
		} catch (JobExecutionException e) {
			throw e;
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		long end = SystemTimer.currentTimeMillis();
		long iv = end - start;
		log.info("定时任务[{}]运行完毕， 本次id:{}, 本次执行时间:{}, 耗时:{}ms", this.getClass().getName(), context.getFireInstanceId(), DateUtil.formatDateTime(context.getFireTime()), iv);

	}

	public abstract void run(JobExecutionContext context) throws Exception;

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}
}
