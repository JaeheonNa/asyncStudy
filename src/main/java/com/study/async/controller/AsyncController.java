package com.study.async.controller;

import com.study.async.service.AsyncService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class AsyncController {

    private final AsyncService asyncService;

    @GetMapping("synchronized")
    public Map asyncWithSynchronized() throws InterruptedException {
        return asyncService.asyncWithSynchronized();
    }


    @GetMapping("future")
    public Map asyncWithFuture() throws ExecutionException, InterruptedException {
        return asyncService.asyncWithFuture();
    }

    @GetMapping("completableFuture")
    public Map asyncWithCompletableFuture() throws ExecutionException, InterruptedException {
        return asyncService.asyncWithFuture();
    }
}
