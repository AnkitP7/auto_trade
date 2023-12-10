package com.kite.automation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class ThreadExecutor {

    private ExecutorService executorService = Executors.newFixedThreadPool(15);

    public void submitTask(Runnable runnable)
    {
        executorService.submit(runnable);
    }

}
