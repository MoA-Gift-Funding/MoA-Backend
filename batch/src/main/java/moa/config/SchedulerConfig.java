package moa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class SchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        SimpleAsyncTaskScheduler simpleAsyncTaskScheduler = new SimpleAsyncTaskScheduler();
        simpleAsyncTaskScheduler.setVirtualThreads(true);
        taskRegistrar.setTaskScheduler(simpleAsyncTaskScheduler);
    }
}
