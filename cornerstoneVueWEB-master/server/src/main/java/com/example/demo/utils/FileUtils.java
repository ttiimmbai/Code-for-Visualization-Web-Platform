package com.example.demo.utils;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileUtils {
    public static void findFileList(File dir ,List<String> fileNames,String seriesnum,String date1,String date2){
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            System.out.println("不存在");
            return ;
        }
        String[] files = dir.list();// 读取目录下的所有目录文件信息
        for (int i = 0; i < files.length; i++) {// 循环，添加文件名或回调自身
            File file = new File(dir, files[i]);
            if (file.isFile()) {// 如果文件
                fileNames.add("\\jpg\\"+file.getName());// 添加文件全路径名
            } else {// 如果是目录
                findFileList(file, fileNames,seriesnum,date1,date2);// 回调自身继续查询
            }
        }
    }


}
