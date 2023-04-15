package com.study.async.service;


import com.study.async.repository.AsyncRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@AllArgsConstructor
public class AsyncService {

    private final AsyncRepository asyncRepository;

    public Map asyncWithSynchronized() throws InterruptedException {
        Map<String, String> result = new HashMap();

        // subThread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // subThread 동작 시작
                asyncRepository.getRunnableMap(result);
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
        return result;
    }

    public Map asyncWithFuture() throws ExecutionException, InterruptedException {

        // Runnable과 달리 리턴값을 가짐.
        // Synchronize 하지 않아도 알아서 wait-notify 됨.
        // 단, 단일 작업 처리. 즉 의존성 있는 작업 해결 못 함. 하려면 callBack 지옥에 빠짐.
        // (java8 completableFuture로 단일 작업 해결 가능)
        Callable<Map> callable = new Callable<Map>() {
            @Override
            public Map call() throws Exception {
                Map<String, String> map = new HashMap<>();

                Future<Map> futureMap = Executors.newSingleThreadExecutor().submit(new Callable<Map>() {
                    @Override
                    public Map call() throws Exception {
                        return asyncRepository.getCallableMap_2(map);
                    }
                });
                // 결과값을 get()으로 꺼낼 때 까지 wait.
                return asyncRepository.getCallableMap_1(futureMap.get());
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

        return result;
    }

    public List asyncWithCompletableFuture() throws ExecutionException, InterruptedException {
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
        // return 값을 받아올 객체 생성.
        List<String> list = new ArrayList<>();
        // 각각의 스레드로 동작. 단, callback을 받아야 다음 단계로 넘어가기 때문에 아래 코드가 3개의 서브스레드를 사용한다고 하더라도
        // 한 개의 서브스레드를 사용하는 것과 크게 다르지 않음.
        List<String> result1 = cf.completedFuture(list) // complete 되면
                .thenApply(msg -> asyncRepository.getFirstApi(msg)) // callback을 받아서 메소드 실행
                .thenApply(msg -> asyncRepository.getSecondApi(msg)) // callback을 받아서 메소드 실행
                .get();

        for (String s : result1) {
            System.out.println(s);
        }

        return result1;
    }

    public List asyncWithSpringAnnotation() throws ExecutionException, InterruptedException {
        List<String> stringList = new ArrayList<>();
        // @Async는 리턴타입을 void, Future, ListenableFuture, CompletableFuture만 지원.
        // 리턴 시 특별한 객체로 감싸야 정상 동작.
        // 만약 다른 리턴타입을 정의할 경우 받아오는 서브 스레드가 결과값을 주기 전에 메인스레드가 확인하고 지나가버림.
        // 즉, wait-notify 과정이 없어지는 것.
        // @Async는 호출할 때마다 새로운 스레드를 생성함. 스레드 풀을 사용하는 게 아님. ******
        // 스레드풀을 사용하려면 @Async("빈네임")으로.
        CompletableFuture<List<String>> returnStringList = asyncRepository.getWithCompletableFuture(stringList);
        Future<List<String>> returnStringList1 = asyncRepository.getWithFuture(returnStringList.get());
        return returnStringList1.get();
    }


}
