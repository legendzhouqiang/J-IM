package org.tio.utils.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoleilu.hutool.setting.dialect.Props;

/**
 * @author tanyaowu 
 * 2017年10月8日 下午3:20:23
 */
public class QuartzUtils {
	private static Logger log = LoggerFactory.getLogger(QuartzUtils.class);

	private static String file = "config/tio-quartz.properties";

	/**
	 * 
	 * @author: tanyaowu
	 */
	public QuartzUtils() {
	}

	private static final List<QuartzTimeVo> jobClasses = new ArrayList<>(10);

	public static void start() {
		initJobClasses();
		if (jobClasses.size() <= 0) {
			log.error("文件[{}]中没有配置定时任务类", file);
			return;
		}
		try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			int index = 1;
			for (QuartzTimeVo quartzTimeVo : jobClasses) {
				try {
					@SuppressWarnings("unchecked")
					Class<? extends Job> clazzz = (Class<? extends Job>) Class.forName(quartzTimeVo.getClazz());
//					@SuppressWarnings("unchecked")
					JobDetail job = JobBuilder.newJob(clazzz).withIdentity("job-" + index, "group-" + index).build();
					CronTrigger trigger = newTrigger().withIdentity("trigger-" + index, "group-" + index).withSchedule(cronSchedule(quartzTimeVo.getCron())).build();

					@SuppressWarnings("unused")
					Date d = scheduler.scheduleJob(job, trigger);			
					log.info("定时任务[{}]已经启动, cron:{}", clazzz.getName(), trigger.getCronExpression());

				} catch (ClassNotFoundException e) {
					log.error(e.toString(), e);
				} finally {
					index++;
				}

			}

			scheduler.start();
		} catch (SchedulerException e) {
			log.error(e.toString(), e);
			throw new RuntimeException(e);
		}
	}

	public static void initJobClasses() {
		Props props = new Props(file);
		Set<Entry<Object, Object>> set = props.entrySet();//.keySet();
		if (set != null && set.size() > 0) {
			for (Entry<Object, Object> entry : set) {
				String clazz = StringUtils.trim((String) entry.getKey());
				String cron = StringUtils.trim((String) entry.getValue());

				QuartzTimeVo quartzTimeVo = new QuartzTimeVo(clazz, cron);
				jobClasses.add(quartzTimeVo);
			}
		}
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}

	private static class QuartzTimeVo {
		private String clazz = null;
		private String cron = null;

		public QuartzTimeVo(String clazz, String cron) {
			super();
			this.clazz = clazz;
			this.cron = cron;
		}

		public String getClazz() {
			return clazz;
		}

		//		public void setClazz(String clazz) {
		//			this.clazz = clazz;
		//		}

		public String getCron() {
			return cron;
		}

		//		public void setCron(String cron) {
		//			this.cron = cron;
		//		}
	}
}