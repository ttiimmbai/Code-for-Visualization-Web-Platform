package com.example.demo.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PatQuery {
    @ApiModelProperty(value = "患者名称,模糊查询")
    private String patientname;

    @ApiModelProperty(value = "检查类别,模糊查询")
    private String devtype;

    @ApiModelProperty(value = "患者id")
    private String patientid;

    @ApiModelProperty(value = "医院id")
    private String hosid;

    @ApiModelProperty(value = "检查部位")
    private String position;

    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "查询开始时间", example = "2019-01-01 10:10:10")
    private String begin;//注意，这里使用的是String类型，前端传过来的数据无需进行类型转换


    @ApiModelProperty(value = "查询结束时间", example = "2019-12-01 10:10:10")
    private String end;
}
