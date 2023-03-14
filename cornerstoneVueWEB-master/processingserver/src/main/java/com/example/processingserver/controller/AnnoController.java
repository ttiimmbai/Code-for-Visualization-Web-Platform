package com.example.processingserver.controller;

import com.alibaba.fastjson.JSON;
import com.example.processingserver.common.R;
import com.example.processingserver.entity.DicomList;
import com.example.processingserver.service.DicomListService;
import com.example.processingserver.utils.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/get")
@CrossOrigin
public class AnnoController {

    private File file;

    @Autowired
    private ReadDcmImageTool readDcmImageTool;
    @Value("${web.innerImagePath}")
    private String innerImagePath;

    @Value("${web.virtualImagePath}")
    private String virtualImagePath;

    @Value("${baseImg}")
    private String baseImg;
    @Autowired
    private XMLGenerator xmlGenerator;

    @Autowired
    private DicomListService dicomListService;

    @Autowired
    private ReadDcmTagInfo readDcmTagInfo;
    //发送文件
    @PostMapping(value = "/sendDcm1")
    @ResponseBody
    public String sendDcm1(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "id") String id) throws Exception {
//        long startTime=System.currentTimeMillis();
//       // ResourceBundle resource = ResourceBundle.getBundle("imgWebPath");//test为属性文件名，放在包com.mmq下，如果是放在src下，直接用test即可
//        List<List<String>> listList = new ArrayList<List<String>>();
//        System.out.println("imgurl...."+imageUrl);
//      //List<List<String>> listImageName = readDcmImageTool.getImageSeriesList("D:/dcmtest/DJ20170701B0018");
//      List<List<String>> listImageName = readDcmImageTool.getImageSeriesList(imageUrl);
//      for (int i=0;i<listImageName.size();i++){
//          List<String> list = new ArrayList<String>();
//          for(int j=0;j<listImageName.get(i).size();j++){
//            //list.add("http://10.168.1.137:8080/down/DJ20170701B0018/"+listImageName.get(i).get(j) );
//              //list.add("http://10.168.1.137:8080/test/T1855634/1.2.840.113704.1.111.6200.1498892502.1/1.2.840.113704.1.111.6200.1498892508.3/"+listImageName.get(i).get(j) );
//            listImageName.get(i).set(j,listImageName.get(i).get(j).replace(innerImagePath,virtualImagePath));
//
//          }
//          listList.add(list);
//      }
//        System.out.println(JSON.toJSONString(listImageName));
//        long endTime=System.currentTimeMillis();
//        System.out.println("运行时间："+(endTime-startTime)+"ms");
//        List<List<String>> listListtest = new ArrayList<List<String>>();
//        List<String> stringList1=new ArrayList<>();
//        List<String> stringList2=new ArrayList<>();
//      String one="http://localhost:9091/test\\\\H0002\\\\2019-10\\\\30\\\\RDR000001\\\\asdfasd\\\\0000.dcm;1";
//        String two="http://localhost:9091/test\\\\H0002\\\\2019-10\\\\30\\\\RDR000001\\\\asdfasd\\\\0001.dcm;2";
//      stringList1.add(one);
//      stringList1.add(two);
//        stringList2.add(one);
//        stringList2.add(two);
//      listListtest.add(stringList1);
//        listListtest.add(stringList2);
//        System.out.println(listListtest);
//        return JSON.toJSONString(listListtest);//listImageName
//      //return JSON.toJSONString(listList, SerializerFeature.DisableCircularReferenceDetect);
        DicomList dicomList=dicomListService.getById(id);
        String seriesnum=dicomList.getSeriesnum();
        LocalDateTime ldt=dicomList.getGmtDate();
        DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter df2 = DateTimeFormatter.ofPattern("dd");
        String date1=df1.format(ldt);//年月
        String date2=df2.format(ldt);//日
        String url=baseImg+"\\H0002\\"+date1+"\\"+date2+"\\"+seriesnum;
        FileNum fileNum=new FileNum(url);
        int num=fileNum.countFile();
        System.out.println("url....."+num);
        ReadDicom readDicom=new ReadDicom(url);
        List<List<String>> imgList=readDicom.getDicom();
        return JSON.toJSONString(imgList);
    }
    @PostMapping(value = "/sendPNG")
    @ResponseBody
    public String sendPNG(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "id") String id) throws Exception {
//
        DicomList dicomList=dicomListService.getById(id);
        String seriesnum=dicomList.getSeriesnum();
        LocalDateTime ldt=dicomList.getGmtDate();
        DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter df2 = DateTimeFormatter.ofPattern("dd");
        String date1=df1.format(ldt);//年月
        String date2=df2.format(ldt);//日
        String url=baseImg+"\\H0002\\"+date1+"\\"+date2+"\\"+seriesnum+"\\PNG";
        FileNum fileNum=new FileNum(url);
        int num=fileNum.countFile();
        System.out.println("url....."+num);
        ReadDicom readDicom=new ReadDicom(url);
        List<List<String>> imgList=readDicom.getPNG();
        return JSON.toJSONString(imgList);
    }
    @PostMapping(value = "/return3Dre")
    public String return3Dre(HttpServletRequest request, HttpServletResponse response,@RequestParam(value = "id") String id){
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
       return JSON.toJSONString(list);

    }

    @PostMapping(value = "/sendDcm2")
    @ResponseBody
    public String sendDcm2(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "id") String id) throws Exception {
//        long startTime=System.currentTimeMillis();
//       // ResourceBundle resource = ResourceBundle.getBundle("imgWebPath");//test为属性文件名，放在包com.mmq下，如果是放在src下，直接用test即可
//        List<List<String>> listList = new ArrayList<List<String>>();
//        System.out.println("imgurl...."+imageUrl);
//      //List<List<String>> listImageName = readDcmImageTool.getImageSeriesList("D:/dcmtest/DJ20170701B0018");
//      List<List<String>> listImageName = readDcmImageTool.getImageSeriesList(imageUrl);
//      for (int i=0;i<listImageName.size();i++){
//          List<String> list = new ArrayList<String>();
//          for(int j=0;j<listImageName.get(i).size();j++){
//            //list.add("http://10.168.1.137:8080/down/DJ20170701B0018/"+listImageName.get(i).get(j) );
//              //list.add("http://10.168.1.137:8080/test/T1855634/1.2.840.113704.1.111.6200.1498892502.1/1.2.840.113704.1.111.6200.1498892508.3/"+listImageName.get(i).get(j) );
//            listImageName.get(i).set(j,listImageName.get(i).get(j).replace(innerImagePath,virtualImagePath));
//
//          }
//          listList.add(list);
//      }
//        System.out.println(JSON.toJSONString(listImageName));
//        long endTime=System.currentTimeMillis();
//        System.out.println("运行时间："+(endTime-startTime)+"ms");
//        List<List<String>> listListtest = new ArrayList<List<String>>();
//        List<String> stringList1=new ArrayList<>();
//        List<String> stringList2=new ArrayList<>();
//      String one="http://localhost:9091/test\\\\H0002\\\\2019-10\\\\30\\\\RDR000001\\\\asdfasd\\\\0000.dcm;1";
//        String two="http://localhost:9091/test\\\\H0002\\\\2019-10\\\\30\\\\RDR000001\\\\asdfasd\\\\0001.dcm;2";
//      stringList1.add(one);
//      stringList1.add(two);
//        stringList2.add(one);
//        stringList2.add(two);
//      listListtest.add(stringList1);
//        listListtest.add(stringList2);
//        System.out.println(listListtest);
//        return JSON.toJSONString(listListtest);//listImageName
//      //return JSON.toJSONString(listList, SerializerFeature.DisableCircularReferenceDetect);
        DicomList dicomList=dicomListService.getById(id);
        String seriesnum=dicomList.getSeriesnum();
        LocalDateTime ldt=dicomList.getGmtDate();
        DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter df2 = DateTimeFormatter.ofPattern("dd");
        String date1=df1.format(ldt);//年月
        String date2=df2.format(ldt);//日
        String url=baseImg+"\\H0002\\"+date1+"\\"+date2+"\\"+seriesnum;
        FileNum fileNum=new FileNum(url);
        int num=fileNum.countFile();
        System.out.println("url....."+num);
        ReadDicom readDicom=new ReadDicom(url);
        List<List<String>> imgList=readDicom.getDicom();
        int size=0;
        List<List<String>> finalList=new ArrayList<>();
        List<String> tempList=new ArrayList<>();
        for (List<String> l:imgList){
            if (l.size()>=size){
                tempList=l;
            }
            size=l.size();
        }
        finalList.add(tempList);
        return JSON.toJSONString(finalList);
    }
    @PostMapping(value = "/sendNii")
    @ResponseBody
    public String sendNii(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "id") String id) throws Exception {
//        long startTime=System.currentTimeMillis();
//       // ResourceBundle resource = ResourceBundle.getBundle("imgWebPath");//test为属性文件名，放在包com.mmq下，如果是放在src下，直接用test即可
//        List<List<String>> listList = new ArrayList<List<String>>();
//        System.out.println("imgurl...."+imageUrl);
//      //List<List<String>> listImageName = readDcmImageTool.getImageSeriesList("D:/dcmtest/DJ20170701B0018");
//      List<List<String>> listImageName = readDcmImageTool.getImageSeriesList(imageUrl);
//      for (int i=0;i<listImageName.size();i++){
//          List<String> list = new ArrayList<String>();
//          for(int j=0;j<listImageName.get(i).size();j++){
//            //list.add("http://10.168.1.137:8080/down/DJ20170701B0018/"+listImageName.get(i).get(j) );
//              //list.add("http://10.168.1.137:8080/test/T1855634/1.2.840.113704.1.111.6200.1498892502.1/1.2.840.113704.1.111.6200.1498892508.3/"+listImageName.get(i).get(j) );
//            listImageName.get(i).set(j,listImageName.get(i).get(j).replace(innerImagePath,virtualImagePath));
//
//          }
//          listList.add(list);
//      }
//        System.out.println(JSON.toJSONString(listImageName));
//        long endTime=System.currentTimeMillis();
//        System.out.println("运行时间："+(endTime-startTime)+"ms");
//        List<List<String>> listListtest = new ArrayList<List<String>>();
//        List<String> stringList1=new ArrayList<>();
//        List<String> stringList2=new ArrayList<>();
//      String one="http://localhost:9091/test\\\\H0002\\\\2019-10\\\\30\\\\RDR000001\\\\asdfasd\\\\0000.dcm;1";
//        String two="http://localhost:9091/test\\\\H0002\\\\2019-10\\\\30\\\\RDR000001\\\\asdfasd\\\\0001.dcm;2";
//      stringList1.add(one);
//      stringList1.add(two);
//        stringList2.add(one);
//        stringList2.add(two);
//      listListtest.add(stringList1);
//        listListtest.add(stringList2);
//        System.out.println(listListtest);
//        return JSON.toJSONString(listListtest);//listImageName
//      //return JSON.toJSONString(listList, SerializerFeature.DisableCircularReferenceDetect);
        DicomList dicomList=dicomListService.getById(id);
        String seriesnum=dicomList.getSeriesnum();
        LocalDateTime ldt=dicomList.getGmtDate();
        DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter df2 = DateTimeFormatter.ofPattern("dd");
        String date1=df1.format(ldt);//年月
        String date2=df2.format(ldt);//日
        String url=baseImg+"\\H0002\\"+date1+"\\"+date2+"\\"+seriesnum;
        FileNum fileNum=new FileNum(url);
        int num=fileNum.countFile();
        System.out.println("url....."+num);
        ReadDicom readDicom=new ReadDicom(url);
        List<List<String>> imgList=readDicom.getNii();
        int size=0;
        List<List<String>> finalList=new ArrayList<>();
        List<String> tempList=new ArrayList<>();
        for (List<String> l:imgList){
            if (l.size()>=size){
                tempList=l;
            }
            size=l.size();
        }
        finalList.add(tempList);
        return JSON.toJSONString(finalList);
    }
    //保存标注
    @RequestMapping(value="/receiveList",method = RequestMethod.POST)
    @ResponseBody
    public String receiveList(@RequestParam(value = "xy",required = false/*,defaultValue = ""*/) String[] xy ,
                              @RequestParam(value = "fileSrc") String fileSrc , HttpServletRequest request,
                              @RequestParam(value = "annoInfo") String seriesAnnoInfo) throws IOException {

        System.out.println("fileSrc:"+fileSrc);
        System.out.println("xy"+xy);
        System.out.println("annoInfo"+seriesAnnoInfo);
         if (xy == null){
             return "0";
        }

         String imagePath = fileSrc.substring(0,fileSrc.indexOf(";")).replace("wadouri:http://localhost:9091/test","D:\\img");
        int[][] xOy = xmlGenerator.getCoordinateInfo(xy);
         readDcmTagInfo.getTagInfo(imagePath);
         xmlGenerator.createXml(readDcmTagInfo.getFileName(),fileSrc,readDcmTagInfo.getWidth().toString(),readDcmTagInfo.getHeight().toString(),readDcmTagInfo.getPerPixel().toString(),xOy);
                             return "1";
    }

    //下载dcm格式的Zip
    @RequestMapping(value = "/sendDcmZip",method = RequestMethod.POST)
    @ResponseBody
    public void sendDcmZip(HttpServletRequest request, HttpServletResponse response,@RequestParam(value="filename") String filename) throws IOException {
        /** 下面为下载zip压缩包相关流程 */
        long start = System.currentTimeMillis();
        String filePath="";

        System.out.println("filename........."+filename);
        if(filename.contains("http")){
            //ResourceBundle resource = ResourceBundle.getBundle("imgWebPath");//test为属性文件名，放在包com.mmq下，如果是放在src下，直接用test即可
            filePath = filename.replace(virtualImagePath,baseImg);
        }else {
            filePath=filename;
        }
        /** 1.创建临时文件夹  */
        File temDir = new File(filePath);
        System.out.println(filePath);
        if(!temDir.exists()){
            temDir.mkdirs();
        }
        String firstFile = "";
        for(File subFile:temDir.listFiles()){
            if(readDcmImageTool.isExistDcm(subFile,"dcm")){
                firstFile = subFile.getName();
                System.out.println("firstFile......"+firstFile);
            }
        }

        /** 3.设置response的header */
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename="+temDir.getName()+".zip");
        System.out.println("ceshi......"+temDir.getPath()+"\\"+firstFile);
        /** 4.调用工具类，下载zip压缩包 */
        ZipUtils.toZip(temDir.getPath()+"\\"+firstFile, response.getOutputStream(),true);
        long end = System.currentTimeMillis();
        //System.out.println("总用时："+(end-start));

        /** 5.删除临时文件和文件夹 */
        // 这里我没写递归，直接就这样删除了
      /*  File[] listFiles = temDir.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            listFiles[i].delete();
        }
        temDir.delete();*/

    }

}
