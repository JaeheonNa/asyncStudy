package com.study.async.repository;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Repository
public class AsyncRepository {

    public Map<String, String> getRunnableMap(Map map){
        map.put("getAsyncResult", "subThread result");
        return map;
    }


    public Map<String, String> getCallableMap_1(Map map){
        map.put("Callable_1", "Callable_1");
        return map;
    }

    public Map<String, String> getCallableMap_2(Map map){
        map.put("Callable_2", "Callable_2");
        return map;
    }

    public List<String> getFirstApi(List<String> msg){
        msg.add("first");
        return msg;
    }
    public List<String> getSecondApi(List<String> msg){
        msg.add("second");
        return msg;
    }

    // @Async는 리턴타입을 void, Future, ListenableFuture, CompletableFuture만 지원
    @Async
    public CompletableFuture<List<String>> getWithCompletableFuture(List<String> stringList) throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> cf = new CompletableFuture<>();
        CompletableFuture<List<String>> listCompletableFuture = cf.completedFuture(stringList) // complete 되면
                                                                    .thenApply(msg -> getFirstApi(msg)) // callback을 받아서 메소드 실행
                                                                    .thenApply(msg -> getSecondApi(msg));

        return CompletableFuture.completedFuture(listCompletableFuture.get());
    }

    @Async("taskExecutor_spring")
    public Future<List<String>> getWithFuture(List<String> stringList) throws ExecutionException, InterruptedException {
        Future<List<String>> futureList = Executors.newSingleThreadExecutor().submit(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return getFirstApi(stringList);
            }
        });

        // new AsyncResult로 감싸서 리턴하면
        // spring aop가 객체를 Future로 감싸서 리턴해줌.
        return new AsyncResult<>(futureList.get());
    }
}
