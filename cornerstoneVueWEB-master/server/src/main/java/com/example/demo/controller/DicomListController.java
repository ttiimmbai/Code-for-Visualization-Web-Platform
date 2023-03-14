package com.example.demo.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.R;
import com.example.demo.entity.DicomList;
import com.example.demo.entity.vo.PatQuery;
import com.example.demo.service.DicomListService;
import com.example.demo.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Tim
 * @since 2021-10-20
 */

@RestController
@RequestMapping("/server/dicom-list")
@CrossOrigin
public class DicomListController {

    @Autowired
    private DicomListService dicomListService;

    @Value("${baseurl}")
    private String baseUrl;

    @Autowired
    private FileUtils fileUtils;
    //条件查询带分页的方法
    @PostMapping("pagePatCondition/{current}/{limit}")
    @ApiOperation(value = "分页条件查询患者")
    public R pageTeacherCondition(@PathVariable long current, @PathVariable long limit, @RequestBody(required = false) PatQuery patQuery){

        Page<DicomList> pagePat =new Page<>(current,limit);

        QueryWrapper<DicomList> queryWrapper=new QueryWrapper<>();

        String name=patQuery.getPatientname();
        String devtype=patQuery.getDevtype();
        String begin=patQuery.getBegin();
        String end =patQuery.getEnd();
        String patientid=patQuery.getPatientid();
        String hosid=patQuery.getHosid();
        String status=patQuery.getStatus();
        String position=patQuery.getPosition();
        System.out.println("position:======="+position);


        if (!StringUtils.isEmpty(name)){
            queryWrapper.like("patientname",name);
        }

        if (!StringUtils.isEmpty(devtype)){
            queryWrapper.like("devtype",devtype);
        }
        if (!StringUtils.isEmpty(patientid)){
            queryWrapper.like("patientid",patientid);
        }

        if (!StringUtils.isEmpty(hosid)){
            queryWrapper.like("hosid",hosid);
        }
        if (!StringUtils.isEmpty(status)){
            queryWrapper.eq("status",status);
        }

        if (!StringUtils.isEmpty(begin)){
            queryWrapper.ge("gmt_date",begin);
        }

        if (!StringUtils.isEmpty(end)){
            queryWrapper.le("gmt_date",end);
        }

        if (!StringUtils.isEmpty(position)){
            queryWrapper.eq("position",position);
        }
        queryWrapper.orderByDesc("gmt_date");

        dicomListService.page(pagePat,queryWrapper);
        long total=pagePat.getTotal();
        List<DicomList> records=pagePat.getRecords();
        Map map=new HashMap();
        map.put("total",total);
        map.put("rows",records);
        return R.ok().data(map);
    }

//    @GetMapping("findAll")
//    public R findAll(){
//
//        List<DicomList> list= dicomListService.list(null);
//        for (DicomList d:list){
//           String date= d.getGmtDate().toString();
//           String ym=date.substring(0,7);
//           String day=date.substring(8,10);
//           String filepath="D:\\img\\H0002\\"+ym+"\\"+day+"\\"+d.getSeriesnum();
//           mkdir(filepath);
//            System.out.println(filepath);
//        }
//        return R.ok().data("items",list);
//    }
//    public static void mkdir(String path) {
//        File fd = null;
//        try {
//            fd = new File(path);
//            if (!fd.exists()) {
//                fd.mkdirs();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            fd = null;
//        }
//    }

    @PostMapping("getSeriesnumAndDateById/{id}")
    @ApiOperation(value = "根据id获取序列号和日期并返回图像路径列表")
    public R getSeriesnumAndDateById(@PathVariable String id){

     DicomList dicomList=dicomListService.getById(id);
     String seriesnum=dicomList.getSeriesnum();
     LocalDateTime ldt=dicomList.getGmtDate();
     DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM");
     DateTimeFormatter df2 = DateTimeFormatter.ofPattern("dd");
     String date1=df1.format(ldt);
     String date2=df2.format(ldt);
     //拼接路径
     String url=baseUrl+"/jpg";

     List<String> fileNames=new ArrayList<>();
     File dir=new File(url);
     fileUtils.findFileList(dir,fileNames,seriesnum,date1,date2);
     for (String value:fileNames){
         System.out.println(value);
     }
      Map map= new HashMap();
      map.put("list",fileNames);
      return R.ok().data(map);
    }

    @PostMapping("getStatusById/{id}")
    @ApiOperation(value = "根据id获取状态")
    public R getStatusById(@PathVariable String id){

        DicomList dicomList=dicomListService.getById(id);
        int status=dicomList.getStatus();
        Map map= new HashMap();
        map.put("status",status);
        return R.ok().data(map);
    }


}
