package com.jfeat.upload;

import com.jfeat.upload.services.LoadFileCodeService;
import com.jfeat.upload.services.impl.LoadFileCodeServiceImpl;

import java.util.concurrent.TimeUnit;


/**
 * Created by jackyhuang on 2018/1/3.
 */
public class LoadFileCodeServiceTest {

    LoadFileCodeService loadFileCodeService = new LoadFileCodeServiceImpl();

    @org.junit.Test
    public void testCache() throws InterruptedException {
        String code = loadFileCodeService.genAndGetCode("abc");
        System.out.println(code);
        System.out.println(loadFileCodeService.checkCode(code));
        TimeUnit.SECONDS.sleep(6);
        System.out.println(loadFileCodeService.checkCode(code));
    }
}
