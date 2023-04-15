package com.study.async.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadConfig {

    @Bean
    public ExecutorService getNewFixedThreadPool(){
        // 운용하는 Thread 갯수가 고정되어있는 Thread Pool
        return Executors.newFixedThreadPool(1);
    }

    @Bean
    public ExecutorService getNewSingleThreadExecutor(){
        // 운용하는 Thread 갯수가 1개로 고정되어있는 Thread Pool
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public ExecutorService getNewScheduledThreadPool(){
        // 일정시간 주기적으로 실행해야 하는 작업이 있는 경우 사용하는 Thread Pool
        return Executors.newScheduledThreadPool(1);
    }

    @Bean
    public ExecutorService getNewCachedThreadPool(){
        // 운용하는 Thread의 갯수를 정하지 않고 상황에 따라서 생성 및 해제하는 Thread Pool.
        // 추가 요청 발생 시 thread 생성. pool에서 관리하다가, 일정 시간 동안 사용 안 할 시 제거.
        // 큐를 사용 안 하고 즉시 스레드를 만들어서 사용하기 때문에 트래픽이 몰릴 경우 스레드가 폭발적으로 증가할 수 있음.
        return Executors.newCachedThreadPool();
    }


}
