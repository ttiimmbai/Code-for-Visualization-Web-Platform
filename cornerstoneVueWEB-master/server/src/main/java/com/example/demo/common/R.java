package com.example.demo.common;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class R {

    @ApiModelProperty(value = "是否成功")

    private Boolean success;


    @ApiModelProperty(value = "返回码")

    private Integer code;


    @ApiModelProperty(value = "返回消息")

    private String message;


    @ApiModelProperty(value = "返回数据")

    private Map<String, Object> data = new HashMap<String, Object>();


    //构造方法私有
    private R(){}

    //成功静态方法
    public static com.example.demo.common.R ok(){
        com.example.demo.common.R r=new com.example.demo.common.R();
        r.setSuccess(true);
        r.setCode(com.example.demo.common.ResultCode.SUCCESS);
        r.setMessage("成功");
        return r;
    }

    //失败静态方法
    public static com.example.demo.common.R error(){
        com.example.demo.common.R r=new com.example.demo.common.R();
        r.setSuccess(false);
        r.setCode(com.example.demo.common.ResultCode.ERROR);
        r.setMessage("失败");
        return r;
    }


    public com.example.demo.common.R success(Boolean success){

        this.setSuccess(success);

        return this;

    }


    public com.example.demo.common.R message(String message){

        this.setMessage(message);

        return this;

    }


    public com.example.demo.common.R code(Integer code){

        this.setCode(code);

        return this;

    }


    public com.example.demo.common.R data(String key, Object value){

        this.data.put(key, value);

        return this;

    }


    public com.example.demo.common.R data(Map<String, Object> map){

        this.setData(map);

        return this;

    }
}
