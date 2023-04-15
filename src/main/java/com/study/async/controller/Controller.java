package com.study.async.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RequestMapping
@RestController
public class Controller {

    @GetMapping("synchronized")
    public void asyncWithSynchronized() throws InterruptedException {
        Map<String, String> result = new HashMap();

        // subThread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // subThread 동작 시작
                String getAsyncResult = "subThread result";
                result.put("getAsyncResult", getAsyncResult);
                // mainThread에게 동작 재개 노티.
                synchronized (result){
                    result.notify();
                }
            }
        });

        // mainThread가 subThread 동작 지시.
        thread.start();
        // subTread가 동작 재개 신호를 주기 전까지 대기.
        // 이 작업이 없으면 subThread가 result에 값을 담기 전에 아래 코드를 실행하게 되므로,
        // result.get("getAsyncResult")은 null 상태.
        result.wait();
        System.out.println(result.get("getAsyncResult"));
    }


    @GetMapping("future")
    public void asyncWithFuture() throws ExecutionException, InterruptedException {

        // Runnable과 달리 리턴값을 가짐.
        // Synchronize 하지 않아도 알아서 wait-notify 됨.
        // 단, 단일 작업 처리. 즉 의존성 있는 작업 해결 못 함. 하려면 callBack 지옥에 빠짐.
        // (java8 completableFuture로 단일 작업 해결 가능)
        Callable<Map> callable = new Callable<Map>() {
            @Override
            public Map call() throws Exception {
                Future<Map> futureMap =Executors.newSingleThreadExecutor().submit(new Callable<Map>() {
                    @Override
                    public Map call() throws Exception {
                        Map<String, String> map = new HashMap<>();
                        map.put("Callable_2", "Callable_2");
                        return map;
                    }
                });
                // 결과값을 get()으로 꺼낼 때 까지 wait.
                Map map = futureMap.get();
                map.put("Callable_1", "Callable_1");
                return map;
            }
        };

        // 단 하나의 스레드를 갖고 있는 실행자를 가져옴.
        ExecutorService es = Executors.newSingleThreadExecutor();
        // 서브스레드 실행
        Future<Map> futureMap = es.submit(callable);
        // 결과값을 get()으로 꺼낼 때 까지 wait.
        Map<String, String> result = futureMap.get();

        for (String o : result.keySet()) {
            System.out.println(result.get(o));
        }
    }

    @GetMapping("completableFuture")
    public void asyncWithCompletableFuture() throws ExecutionException, InterruptedException {
        CompletableFuture<Map<String, String>> cf = new CompletableFuture<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> map = new HashMap<>();
                map.put("getHello", "Hello");
                cf.complete(map);
            }
        });
        thread.start();
        // complete 작동 시까지 wait.
        Map<String, String> result = cf.get();
        System.out.println(result.get("getHello"));


        // chaining 방법 //
        List<String> list = new ArrayList<>();
        list.add("start");

        // 각각의 스레드로 동작. 단, callback을 받아야 다음 단계로 넘어가기 때문에 아래 코드가 3개의 서브스레드를 사용한다고 하더라도
        // 한 개의 서브스레드를 사용하는 것과 크게 다르지 않음.
        List<String> result1 = cf.completedFuture(list) // complete 되면
                                    .thenApply(msg -> getFirstApi(msg)) // callback을 받아서 메소드 실행
                                    .thenApply(msg -> getSecondApi(msg)) // callback을 받아서 메소드 실행
                                    .get();

        for (String s : result1) {
            System.out.println(s);
        }
    }

    public List<String> getFirstApi(List<String> msg){
        msg.add("first");
        return msg;
    }
    public List<String> getSecondApi(List<String> msg){
        msg.add("second");
        return msg;
    }
}
