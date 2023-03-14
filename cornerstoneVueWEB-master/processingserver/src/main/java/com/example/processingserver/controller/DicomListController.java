package com.example.processingserver.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.processingserver.common.R;
import com.example.processingserver.entity.DicomList;
import com.example.processingserver.service.DicomListService;
import com.example.processingserver.utils.ReadDicom;
import com.example.processingserver.utils.RestructThread;
import com.example.processingserver.utils.restruction;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/processing/dicom-list")
@CrossOrigin
public class DicomListController {
    @Autowired
    private DicomListService dicomListService;

    @Value("${baseurl}")
    private String baseUrl;

    @Value("${baseImg}")
    private String baseImg;

    @Value("${web.virtualImagePath}")
    private String virtualImagePath;

    @PostMapping("getListeq0")
    @ApiOperation(value = "获取状态为0的患者")
    public R getListeq0(){
        List<DicomList> dicomLists=new ArrayList<>();
        QueryWrapper<DicomList> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("status",0);
        dicomLists= dicomListService.list(queryWrapper);
        Map map= new HashMap();
        map.put("list0",dicomLists);
        return R.ok().data(map);

    }
    @PostMapping("getListeq50")
    @ApiOperation(value = "获取状态为50的患者")
    public R getListeq50(){
        List<DicomList> dicomLists=new ArrayList<>();
        QueryWrapper<DicomList> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("status",50);
        dicomLists= dicomListService.list(queryWrapper);
        Map map= new HashMap();
        map.put("list50",dicomLists);
        return R.ok().data(map);

    }

    @PostMapping("restructById/{id}")
    @ApiOperation(value = "三维重建")
    public R restruct(@PathVariable String id){

        DicomList dicomList=dicomListService.getById(id);
        dicomList.setStatus(25);
        dicomListService.updateById(dicomList);
        String seriesnum=dicomList.getSeriesnum();
        LocalDateTime ldt=dicomList.getGmtDate();
        DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter df2 = DateTimeFormatter.ofPattern("dd");
        String date1=df1.format(ldt);
        String date2=df2.format(ldt);
        //拼接路径
        String root=baseImg+"/H0002"+"/"+date1+"/"+date2+"/"+seriesnum;
        ReadDicom readDicom=new ReadDicom(root);
        String inputfilename=readDicom.findBaseFile();
        File file=new File(root+"/3d");
        if (!file.isDirectory()){
            file.mkdir();
        }

        String outputfilename=root+"/3d"+"/"+seriesnum;
//        dicomList.setStatus(100);
        RestructThread restructThread=new RestructThread(inputfilename,outputfilename,dicomListService,dicomList,root);
        new Thread(restructThread).start();

        return R.ok();
    }

    @PostMapping("return3D/{id}")
    @ApiOperation(value = "根据id返回3d路径")
    public R return3D(@PathVariable String id){
        DicomList dicomList=dicomListService.getById(id);
        String seriesnum=dicomList.getSeriesnum();
        LocalDateTime ldt=dicomList.getGmtDate();
        DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter df2 = DateTimeFormatter.ofPattern("dd");
        String date1=df1.format(ldt);
        String date2=df2.format(ldt);
        //拼接路径
        String root1=virtualImagePath+"/H0002"+"/"+date1+"/"+date2+"/"+seriesnum+"/3d/"+seriesnum+"Draco.gltf";
        String root2=virtualImagePath+"/H0002"+"/"+date1+"/"+date2+"/"+seriesnum+"/3d/"+seriesnum+"_segDraco.gltf";
        Map map=new HashMap();
        ArrayList list=new ArrayList();
        list.add(root1);
        list.add(root2);
        map.put("root",list);
        return R.ok().data(map);
    }

//    @PostMapping("return3Dre")
//    @ApiOperation(value = "根据id返回3d路径")
//    public R return3Dre(@RequestParam(value = "id") String id){
//        DicomList dicomList=dicomListService.getById(id);
//        String seriesnum=dicomList.getSeriesnum();
//        LocalDateTime ldt=dicomList.getGmtDate();
//        DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM");
//        DateTimeFormatter df2 = DateTimeFormatter.ofPattern("dd");
//        String date1=df1.format(ldt);
//        String date2=df2.format(ldt);
//        //拼接路径
//        String root1=virtualImagePath+"/H0002"+"/"+date1+"/"+date2+"/"+seriesnum+"/3d/"+seriesnum+"Draco.gltf";
//        String root2=virtualImagePath+"/H0002"+"/"+date1+"/"+date2+"/"+seriesnum+"/3d/"+seriesnum+"_segDraco.gltf";
//        Map map=new HashMap();
//        ArrayList list=new ArrayList();
//        list.add(root1);
//        list.add(root2);
//        map.put("root",list);
//        return R.ok().data(map);
//    }
}
