package org.freeone.k8s.web.knife.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义异步线程和定时任务线程
 * XXXConfigurer的扩展配置类,会提示”is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)“
 * 参考(https://blog.csdn.net/f641385712/article/details/89737791)
 */
@Configuration
@EnableAsync
@EnableScheduling
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ThreadPoolConfiguration implements SchedulingConfigurer, AsyncConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolConfiguration.class);

    /**
     * 异步任务
     *
     * @return
     */
    @Bean(name = "asyncServiceExecutor", destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor asyncServiceExecutor() {
        LOGGER.info("start asyncServiceExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 配置核心线程数
        executor.setCorePoolSize(5);
        // 配置最大线程数
        executor.setMaxPoolSize(5);
        // 配置队列大小
        executor.setQueueCapacity(5);
        executor.setKeepAliveSeconds(60 * 10);
        // 配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("async-task-");
        // rejection-policy：拒绝策略：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 执行初始化
        executor.initialize();
        return executor;
    }

    /**
     * 定时任务
     *
     * @return
     */
    @Bean(destroyMethod = "shutdown", name = "taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        LOGGER.info("start taskScheduler");
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("scheduler-");
        // 应用停止的时候是否等待任务完成
//        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 等待的秒数
//        scheduler.setAwaitTerminationSeconds(10);
        scheduler.setErrorHandler(t -> LOGGER.error("定时任务出错", t));
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = taskScheduler();
        scheduledTaskRegistrar.setTaskScheduler(taskScheduler);
    }

    @Override
    public Executor getAsyncExecutor() {
        return asyncServiceExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            LOGGER.error("异步任务执行出现异常, message {}, method {}, params {}", throwable, method, objects);
        };
    }
}
