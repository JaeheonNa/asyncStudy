package com.study.async.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
