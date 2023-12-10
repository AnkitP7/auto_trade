package com.kite.automation.schedules;

import com.kite.automation.strategies.AbstractStrategy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Data
@Slf4j
public class KiteAutomationTaskScheduler {

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private Map<String, ScheduledFuture> scheduledFutureMap = new HashMap<>();

    public void scheduleTask(final AbstractStrategy abstractStrategy)
    {
        threadPoolTaskScheduler.setRemoveOnCancelPolicy(true);
        if(threadPoolTaskScheduler.getPoolSize() == 1)
        {
            threadPoolTaskScheduler.setPoolSize(10);
        }

        if ( abstractStrategy.getGenericStrategyConfig() != null
                && abstractStrategy.getGenericStrategyConfig().getStartTime() != null
                && abstractStrategy.getGenericStrategyConfig().getStartTime().getTime() > Calendar.getInstance().getTime().getTime())
        {
            final ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(abstractStrategy, abstractStrategy.getGenericStrategyConfig().getStartTime());
            scheduledFutureMap.put(abstractStrategy.getKeyId(), scheduledFuture);
        }

        final Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY, 15);
        instance.set(Calendar.MINUTE, 24);
        instance.set(Calendar.SECOND, 0);

        if(instance.getTime().getTime() > Calendar.getInstance().getTime().getTime())
        {
            final ScheduledFuture<?> scheduledFuture1 = threadPoolTaskScheduler.schedule(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    abstractStrategy.close();
                }
            }, instance.getTime());
            scheduledFutureMap.put(abstractStrategy.getKeyId()+"_CLOSE", scheduledFuture1);
        }

    }

    public void removeScheduledTasks(String userId) {
        log.info("Current scheduled tasks {}", getScheduledTasks() );
        for (String key :
        scheduledFutureMap.keySet()) {
            log.info("Checking for {} in scheduled future {}", userId, key);
            if(key.toLowerCase().contains(userId.toLowerCase())) {
                log.info("Cancelling scheduled future {}", key);
                final ScheduledFuture scheduledFuture = scheduledFutureMap.get(key);
                if (scheduledFuture != null) {
                    final boolean cancelled = scheduledFuture.cancel(false);
                    if(cancelled)
                    {
                        log.info("Cancelled the schedule successfully {}" ,key);
                    }
                }
            }
        }

    }

    public int getScheduledTasks()
    {
        int output = 0;
        final BlockingQueue<Runnable> queue = threadPoolTaskScheduler.getScheduledThreadPoolExecutor().getQueue();
        if(queue != null)
        {
            for (Runnable runnable: queue)
            {
                ScheduledFuture scheduledFuture = (ScheduledFuture) runnable;
                output++;
            }
        }
        return output;
    }

}
