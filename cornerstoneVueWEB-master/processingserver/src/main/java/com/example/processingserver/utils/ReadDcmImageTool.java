package com.example.processingserver.utils;

import org.ini4j.Ini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.processingserver.service.ReadDicomFileTagInfoService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ReadDcmImageTool
 * @Author Bob
 * @Date 2019-04-01 16:28
 */
@Component
public class ReadDcmImageTool {
    /**
     *
     * @param filePath 文件路径
     * @return 返回几个序列的list集合
     */
    @Autowired
    ReadDicomFileTagInfoService readDicomFileTagInfoService;
    public List<List<String>> getImageSeriesList(String filePath){
        List<List<String>> seriesList = new ArrayList<List<String>>();

        File file = new File(filePath);
        if(!file.exists()){
            return seriesList;
        }
        File[] f = file.listFiles();
        if (file.isFile()){
            System.out.println("给的不是一个图片目录，而是一个文件");
            return null;
        }else if(f.length>0) {
            try {
/*                for (File subFile:f) {
                    if(this.isExistDcm(subFile,"dcm")){
                        Ini ini = new Ini(new File(filePath+"/seriesinfo/"+"study"+subFile.getName()+".ini"));
                        Ini.Section sneezy = ini.get("series");
                        List<String> series = sneezy.getAll("series");
                        for (int i=0;i<series.size();i++){
                            Ini iniSon = new Ini(new File(filePath+"/seriesinfo/"+series.get(i)+".ini"));
                            Ini.Section sneezySon = iniSon.get("filenames");
                            List<String> seriesImage = sneezySon.getAll("filename");
                            for (int j=0;j<seriesImage.size();j++){
                                seriesImage.set(j,filePath+"\\"+f[0].getName()+"\\"+series.get(i)+"\\"+seriesImage.get(j));
                            }
                            seriesList.add(seriesImage);
                        }
                    }
                }*/
                for (int z=0;z<f.length;z++) {
                    if(this.isExistDcm(f[z],"dcm")){
                        File studyUid = new File(filePath+"/seriesinfo/"+"study"+f[z].getName()+".ini");
                        if(!studyUid.exists()){
                            if(f[z].isDirectory()){
                                File[] seriesFlies = f[z].listFiles();
                                for(int x=0;x<seriesFlies.length;x++){

                                    File[] images = seriesFlies[x].listFiles();
                                    for(int y=0;y<images.length;y++){
                                        readDicomFileTagInfoService.getTagInfo(images[y].getAbsolutePath());

                                    }
                                }
                            }
                            writeStudyInfo(filePath,readDicomFileTagInfoService,f[z]);

                            studyUid = new File(filePath+"/seriesinfo/"+"study"+f[z].getName()+".ini");
                        }
                        Ini ini = new Ini(studyUid);
                        Ini.Section sneezy = ini.get("series");
                        List<String> series = sneezy.getAll("series");
                        long start = System.currentTimeMillis();
                        for (int i=0;i<series.size();i++){
                            File oneSerie = new File(filePath+"/seriesinfo/"+series.get(i)+".ini");
                            if(!oneSerie.exists()){
                                if(f[z].isDirectory()&&readDicomFileTagInfoService.getPatientId()==null){
                                    File[] seriesFlies = f[z].listFiles();
                                    for(int x=0;x<seriesFlies.length;x++){
                                        File[] images = seriesFlies[x].listFiles();
                                        for(int y=0;y<images.length;y++){
                                            readDicomFileTagInfoService.getTagInfo(images[y].getAbsolutePath());
                                        }
                                    }
                                }
                                writeSeriesInfo(filePath,readDicomFileTagInfoService);
                                oneSerie = new File(filePath+"/seriesinfo/"+series.get(i)+".ini");
                            }
                            Ini iniSon = new Ini(oneSerie);
                            Ini.Section sneezySon = iniSon.get("filenames");
                            List<String> seriesImage = sneezySon.getAll("filename");
                            for (int j=0;j<seriesImage.size();j++){
                                seriesImage.set(j,filePath+"\\"+f[z].getName()+"\\"+series.get(i)+"\\"+seriesImage.get(j));
                            }
                            seriesList.add(seriesImage);
                        }
                        long end = System.currentTimeMillis();
                        System.out.println(end-start);
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                init(readDicomFileTagInfoService);
            }

        }
        return seriesList;

    }

    /**
     *
     * @param filePath 图像路径
     * @return 返回给微信小程序路径
     */
    public List<List<String>> getWCImageSeriesList(String filePath){
        List<List<String>> seriesList = new ArrayList<List<String>>();

        File file = new File(filePath);
        if(!file.exists()){
            return seriesList;
        }
        File[] f = file.listFiles();
        if (file.isFile()){
            System.out.println("给的不是一个图片目录，而是一个文件");
            return null;
        }else if(f.length>0) {
            try {
                for (int z=0;z<f.length;z++) {
                    if(this.isExistDcm(f[z],"dcm")){
                        File studyUid = new File(filePath+"/seriesinfo/"+"study"+f[z].getName()+".ini");
                        Ini ini = new Ini(studyUid);
                        Ini.Section sneezy = ini.get("series");
                        List<String> series = sneezy.getAll("series");
                        for (int i=0;i<series.size();i++){
                            File oneSerie = new File(filePath+"/seriesinfo/"+series.get(i)+".ini");
                            Ini iniSon = new Ini(oneSerie);
                            Ini.Section sneezySon = iniSon.get("filenames");
                            List<String> seriesImage = sneezySon.getAll("filename");
                            for (int j=0;j<seriesImage.size();j++){
                                seriesImage.set(j,filePath+"\\"+"jpg"+"\\"+series.get(i)+"\\"+seriesImage.get(j).replace("dcm","jpg"));
                            }
                            seriesList.add(seriesImage);
                        }
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                init(readDicomFileTagInfoService);
            }

        }
        return seriesList;

    }
    private static void init(ReadDicomFileTagInfoService rdf)
    {
        rdf.getSeriesMap().clear();
        rdf.getStudy_countMap().clear();
        rdf.getFilenameMap().clear();
        rdf.getSeriesDiMap().clear();
        rdf.setAccessionN(null);
        rdf.setAge(null);
        rdf.setBirth(null);
        rdf.setSex(null);
        rdf.setModality(null);
        rdf.setPatientId(null);
        rdf.setPatName(null);
        rdf.setStudyDate(null);
        rdf.getFileInstanceNumMap().clear();
    }
    //写入series.ini
    private static void writeSeriesInfo(String filePath,ReadDicomFileTagInfoService rdf) throws IOException {
        //series 和 filename 关联
        Map<String, List<String>> ser = rdf.getFilenameMap();
        Map<String , String > instancenum = rdf .getFileInstanceNumMap();

        for (Map.Entry<String, List<String>> entry : ser.entrySet()) {
            //写seriesInfo.ini 文件名排序
            List<String> filenamelist = entry.getValue();
            Collections.sort(filenamelist);

            String series = entry.getKey();
            String series_path = filePath + "/seriesinfo" + "/" + series + ".ini";
            File file = new File(series_path);
            if(file.exists())
            {
                file.delete();
            }
            //创建父级目录
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            //创建文件
            file.createNewFile();
            FileWriter fw = new FileWriter(series_path,true);
            BufferedWriter bw = new BufferedWriter(fw);

            try
            {

                bw.write("[imagecount]\r\n");
                bw.write("count="+filenamelist.size()+"\r\n");
                bw.write("[filenames]\r\n");
                for(String filename:filenamelist)
                {
                    String  instanceNumber = instancenum.get(filename);
                    bw.write("filename=" + filename + ";" + instanceNumber  +"\r\n");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                //logger.error("写配置文件series***.ini 错误");
                return;
            }finally {
                bw.flush();
                fw.close();
                bw.close();
            }
        }
    }

    //写入studyxxx.ini
    private static void writeStudyInfo(String filePath,ReadDicomFileTagInfoService rdf,File syudyFile) throws IOException {
        //seriesMap 是 study 和 series的关联
        Map<String, List<String>> seriesMap = rdf.getSeriesMap();
        //seriesDiMap 是 series 和 discription 的关联
        Map<String , String > seriesDiMap = rdf.getSeriesDiMap();
        //每个study有多少张图像
        Map<String,Integer> study_str = rdf.getStudy_countMap();

        for(Map.Entry<String , List<String>> entry : seriesMap.entrySet())
        {
            List<String> serlist = entry.getValue();
            Collections.sort(serlist);

            String str = entry.getKey();
            if(str == null||str == ""){
                str = syudyFile.getName();
            }
            //写配置文件study***.ini
            String study_path_2 = filePath + "/" + "seriesinfo"+ "/" + "study" + syudyFile.getName() + ".ini";
            File fileini = new File(study_path_2);
            if(fileini.exists())
            {
                fileini.delete();
            }
            if(!fileini.getParentFile().exists()) {
                fileini.getParentFile().mkdirs();
            }

            fileini.createNewFile();
            FileWriter fwstu = new FileWriter(fileini,true);
            BufferedWriter bw = new BufferedWriter(fwstu);
            try
            {
                bw.write("[imagecount]\r\n");
                bw.write("count=" + study_str.get(str).intValue() + "\r\n");
                bw.write("[SeriesCount]\r\n");
                bw.write("SeriesNum="+serlist.size()+"\r\n");
                bw.write("[series]\r\n");
                String serNum =  String.valueOf(serlist.size());
                for(int j = 0;j<serlist.size();j++)
                {
                    String seriesUID = serlist.get(j);
                    String SeriesDiscript = seriesDiMap.get(seriesUID);
                    bw.write("series="+seriesUID+"\r\n");

                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                //logger.error("写配置文件study***.ini错误！");
                return;
            }
            finally
            {
                bw.flush();
                bw.close();
                fwstu.close();

            }
        }
    }

    public Boolean isExistDcm(File file,String suffix){
        File[] f = file.listFiles();
        if(f == null){
            return false;
        }
        for (File subFile:f) {
            if(subFile == null){
                return false;
            }else {
                if(subFile.isDirectory()){
                    return isExistDcm(subFile,suffix);
                }else if(subFile.isFile()&&subFile.getName().endsWith(suffix)){
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args){
        List<String> arr = new ArrayList<String>();
        for(String s:arr){
            System.out.println(s);
        }
        System.out.println(arr.size());
    }
}
