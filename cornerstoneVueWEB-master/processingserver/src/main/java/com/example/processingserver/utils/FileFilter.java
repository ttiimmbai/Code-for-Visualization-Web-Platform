package com.example.processingserver.utils;

import java.io.File;
import java.io.FilenameFilter;

public class FileFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {

        boolean flag = true;
        // 定义筛选条件
        //endWith(String str);判断是否是以指定格式结尾的
        if (name.toLowerCase().endsWith(".dcm")) {

        }  else {
            flag = false;
        }
        // 返回定义的返回值

        //当返回true时,表示传入的文件满足条件
        return flag;
    }
}
