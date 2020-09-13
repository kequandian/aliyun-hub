package com.jfeat.upload.services;

/**
 * Created by jackyhuang on 2018/1/3.
 */
public interface LoadFileCodeService {
    public String genAndGetCode(String name);
    public boolean checkCode(String code);
}
