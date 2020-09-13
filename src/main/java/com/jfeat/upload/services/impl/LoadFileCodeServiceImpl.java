package com.jfeat.upload.services.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jfeat.upload.services.LoadFileCodeService;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by jackyhuang on 2018/1/3.
 */
@Service
public class LoadFileCodeServiceImpl implements LoadFileCodeService {

    private static Cache<String, String> cache = CacheBuilder
            .newBuilder()
            .maximumSize(100)
            .expireAfterAccess(60, TimeUnit.SECONDS) //当缓存项在指定的时间段内没有被读或写就会被回收。
            .build();


    @Override
    public String genAndGetCode(String name) {
        String key = UUID.randomUUID().toString();
        cache.put(key, name);
        return key;
    }

    @Override
    public boolean checkCode(String code) {
        try {

            String name = cache.get(code, () -> "");

            if (name == null || name.equals("")) {
                return false;
            }

            return true;
        }
        catch (ExecutionException e) {
            return false;
        }
    }
}
