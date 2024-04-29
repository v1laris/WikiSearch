package com.wks.wikisearch.aspect;

import com.wks.wikisearch.service.CounterService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CounterAspect {
    private final CounterService counterService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CounterAspect.class);


    public CounterAspect(CounterService counterService) {
        this.counterService = counterService;
    }


    @Before("execution(* com.wks.wikisearch.controller.*.*(..)) ")
    public synchronized void incrementCounter() {
        counterService.increment();
        LOGGER.info("Counter incremented to {} ", counterService.get());
    }
}
