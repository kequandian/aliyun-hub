package com.jfeat.upload.api;

import com.alibaba.fastjson.JSONObject;
import com.jfeat.crud.base.tips.SuccessTip;
import com.jfeat.crud.base.tips.Tip;
import com.jfeat.orc.OrcInfo;
import com.jfeat.orc.alibaba.ocr.demo.APPCodeUtil;
import com.jfeat.orc.alibaba.ocr.demo.APPKeyUtil;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "ORC")
@RestController
public class ORCEndpoint {

    @Resource
    APPKeyUtil appKeyUtil;
    @Resource
    APPCodeUtil appCodeUtil;

    @PostMapping("/api/orc")
    public Tip orc(@RequestBody OrcInfo orcInfo){
        JSONObject jsonObject = appKeyUtil.OrcOne(orcInfo.getImageUrl());
        return SuccessTip.create(jsonObject);
    }


    @PostMapping("/api/orc/code")
    public Tip orcCode(@RequestBody OrcInfo orcInfo){
        JSONObject jsonObject = appCodeUtil.OrcOne(orcInfo.getImageUrl());
        return SuccessTip.create(jsonObject);
    }

}
