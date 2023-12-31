package com.ecnu.center.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ExecutorConfig {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorConfig.class);
//    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();// 获取cpu个数
//    private static final int COUR_SIZE = CPU_COUNT * 2;
//    private static final int MAX_COUR_SIZE = CPU_COUNT * 4;
    private static final int COUR_SIZE = 5;
    private static final int MAX_COUR_SIZE = 20;
    @Bean
    public Executor asyncMainExecutor() {
        logger.info("start asyncMainExecutor CPU:" + COUR_SIZE);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(COUR_SIZE);
        //配置最大线程数
        executor.setMaxPoolSize(MAX_COUR_SIZE);
        //配置队列大小
        executor.setQueueCapacity(MAX_COUR_SIZE * 4);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("thread-file");
        /**
         * rejection-policy：当pool已经达到max size的时候，如何处理新任务
         * CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
         */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //初始化执行器
        executor.initialize();
        return executor;
    }

}
