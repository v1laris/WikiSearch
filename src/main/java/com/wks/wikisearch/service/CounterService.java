package com.wks.wikisearch.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CounterService {
    private final AtomicInteger counter = new AtomicInteger(0);
    public synchronized void increment() {
        counter.incrementAndGet();
    }

    public synchronized int get() {
        return counter.get();
    }
}