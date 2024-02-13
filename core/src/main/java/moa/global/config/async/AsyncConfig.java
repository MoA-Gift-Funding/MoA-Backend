package moa.global.config.async;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@EnableAsync
@Configuration
@Component
public class AsyncConfig implements AsyncConfigurer {

    public static final String VIRTUAL_THREAD_EXECUTOR = "virtualThreadExecutor";

    @Bean
    public Executor virtualThreadExecutor() {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        return new TaskExecutorAdapter(executor);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }
}
