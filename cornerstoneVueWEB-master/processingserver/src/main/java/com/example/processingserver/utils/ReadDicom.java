package com.example.processingserver.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReadDicom {

    @Value("${web.virtualImagePath}")
    private String virtualImagePath;

    private String path;

    public ReadDicom(String path) {

        this.path = path;
    }
    public ReadDicom(){

    }

    public void setPath(String path) {

        this.path = path;
    }
    //获取dicom图像路径
    public List<List<String>> getDicom(){
        List<List<String>> dicomList=new ArrayList<>();
        File file=new File(this.path);
        File listout[]=file.listFiles();
        for (int i = 0;i < listout.length;i++){
            if (listout[i].isDirectory()){
                File listin[]=listout[i].listFiles();
                List<String> oneSeries=new ArrayList<>();
                for (int j=0;j<listin.length;j++){
                    if (listin[j].getName().endsWith(".dcm")&&listin[j].isFile()){
                        String tempPath=listin[j].getAbsolutePath();
                        String newPath=tempPath.replaceAll("D:\\\\img","http://localhost:9091/test");
                        int k=j+1;
                        String finalPath=newPath+";"+k;
                        oneSeries.add(finalPath);
                    }
                    if (listin[j].isDirectory()){
                        File listin2[]=listin[j].listFiles();
                        for (int k=0;k<listin2.length;k++){
                        if (listin2[k].getName().endsWith(".dcm")&&listin2[k].isFile()){
                            String tempPath=listin2[k].getAbsolutePath();
//                            System.out.println("lallalal"+virtualImagePath);
                            String newPath=tempPath.replaceAll("D:\\\\img","http://localhost:9091/test");
                            int l=k+1;
                            String finalPath=newPath+";"+l;
                            oneSeries.add(finalPath);
                        }
                        }
                    }
                }
                if(!oneSeries.isEmpty()) {
                    dicomList.add(oneSeries);
                }
            }
        }
        return dicomList;
    }

    //获取png图像路径
    public List<List<String>> getPNG() {
        List<List<String>> dicomList = new ArrayList<>();
        File file = new File(this.path);
        File listout[] = file.listFiles();
        List<String> oneSeries = new ArrayList<>();
        for (int j = 0; j < listout.length; j++) {
            if (listout[j].getName().endsWith(".png") && listout[j].isFile()) {
                String tempPath = listout[j].getAbsolutePath();
                String newPath = tempPath.replaceAll("D:\\\\img", "http://localhost:9091/test");
                int k = j + 1;
                String finalPath = newPath + ";" + k;
                oneSeries.add(finalPath);
            }

        }
        if (!oneSeries.isEmpty()) {
            dicomList.add(oneSeries);
        }

        return dicomList;
    }
    //获取dicom图像路径
    public List<List<String>> getNii(){
        List<List<String>> dicomList=new ArrayList<>();
        File file=new File(this.path);
        File listout[]=file.listFiles();
        for (int i = 0;i < listout.length;i++){
            if (listout[i].isDirectory()){
                File listin[]=listout[i].listFiles();
                List<String> oneSeries=new ArrayList<>();
                for (int j=0;j<listin.length;j++){
                    if (listin[j].getName().endsWith(".nii")&&listin[j].isFile()){
                        String tempPath=listin[j].getAbsolutePath();
                        String newPath=tempPath.replaceAll("D:\\\\img","http://localhost:9091/test");
                        int k=j+1;
                        String finalPath=newPath+";"+k;
                        oneSeries.add(finalPath);
                    }
                    if (listin[j].isDirectory()){
                        File listin2[]=listin[j].listFiles();
                        for (int k=0;k<listin2.length;k++){
                            if (listin2[k].getName().endsWith(".nii")&&listin2[k].isFile()){
                                String tempPath=listin2[k].getAbsolutePath();
//                            System.out.println("lallalal"+virtualImagePath);
                                String newPath=tempPath.replaceAll("D:\\\\img","http://localhost:9091/test");
                                int l=k+1;
                                String finalPath=newPath+";"+l;
                                oneSeries.add(finalPath);
                            }
                        }
                    }
                }
                if(!oneSeries.isEmpty()) {
                    dicomList.add(oneSeries);
                }
            }
        }
        return dicomList;
    }

    //查找三维重建文件夹
    public String findBaseFile(){
        File file=new File(this.path);
        File listout[]=file.listFiles();
        int Flag=0;
        int num=0;
        //找到文件数最多的文件夹
        for (int i = 0;i < listout.length;i++){
            if (listout[i].isDirectory()) {
                if (getFileNum(listout[i]) > num) {
                    num = getFileNum(listout[i]);
                    Flag = i;
                }
            }
        }

        File target=listout[Flag];
        List<File> listFiles = new ArrayList<File>();
        findDcm(target,listFiles);
        String path=listFiles.get(0).getAbsolutePath();
        String[] pathArr=path.split("\\\\");
        String basefile="";
        for (int i=0;i<pathArr.length-1;i++){
            basefile=basefile+pathArr[i]+"//";
        }
        System.out.println(basefile);


        return basefile;
    }

    //查找文件夹下dcm文件
    public void findDcm(File dir,List<File> listFiles)
    {
        File[] files = dir.listFiles();
        for(File file : files)
        {
            if(file.isDirectory())
            {
                findDcm(file,listFiles);
            }
            else
            {
                if(file.getName().endsWith(".dcm"))
                    listFiles.add(file);
            }
        }
    }

//    public String getDcmPath(File f){
//        if(f.isFile()&&f.getName().endsWith("dcm")) {
//                return f.getAbsolutePath();
//
//        }else {
//            //遍历文件夹
//            File[] files = f.listFiles();
//            if(files!=null && files.length>0) {
//                //继续递归得到的文件或文件夹
//                for (File file : files) {
//                    getDcmPath(file);
//                }
//            }
//            return null;
//        }
//
//
//    }
    //获得文件夹下文件数量
    public int getFileNum(File dir){

        int fileCount = 0;
        File[] files=dir.listFiles();
        for (int i=0;i<files.length;i++){
            if(!files[i].isDirectory()){
                fileCount++;
            }else{
                fileCount += getFileNum(files[i]);
            }
        }

        return fileCount;
    }

    public static void main(String[] args) {
        File file=new File("D:\\img\\H0002\\2008-10\\22\\RDR000008");
        ReadDicom readDicom=new ReadDicom("D:\\img\\H0002\\2019-11\\12\\H0002CT000545");
       readDicom.findBaseFile();
    }
}
